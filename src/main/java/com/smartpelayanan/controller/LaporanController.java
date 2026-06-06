package com.smartpelayanan.controller;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.smartpelayanan.entity.*;
import com.smartpelayanan.repository.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laporan")
public class LaporanController {

    private final LayananAdministrasiRepository layananAdministrasiRepository;
    private final JawabanFormRepository jawabanFormRepository;
    private final LampiranFileRepository lampiranFileRepository;
    private final RiwayatStatusLayananRepository riwayatStatusLayananRepository;
    private final RiwayatStatusRepository riwayatStatusRepository;
    private final PengaduanRepository pengaduanRepository;

    public LaporanController(LayananAdministrasiRepository layananAdministrasiRepository,
                              JawabanFormRepository jawabanFormRepository,
                              LampiranFileRepository lampiranFileRepository,
                              RiwayatStatusLayananRepository riwayatStatusLayananRepository,
                              RiwayatStatusRepository riwayatStatusRepository,
                              PengaduanRepository pengaduanRepository) {
        this.layananAdministrasiRepository = layananAdministrasiRepository;
        this.jawabanFormRepository = jawabanFormRepository;
        this.lampiranFileRepository = lampiranFileRepository;
        this.riwayatStatusLayananRepository = riwayatStatusLayananRepository;
        this.riwayatStatusRepository = riwayatStatusRepository;
        this.pengaduanRepository = pengaduanRepository;
    }

    // GET /api/v1/laporan/layanan/{id}/pdf — generate dan download PDF permohonan layanan
    @GetMapping("/layanan/{id}/pdf")
    public ResponseEntity<byte[]> exportLayananPdf(@PathVariable UUID id) {
        try {
            LayananAdministrasi layanan = layananAdministrasiRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Layanan not found"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header
            Paragraph header = new Paragraph("SMARTPELAYANAN")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("Laporan Permohonan Layanan Administrasi")
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subHeader);

            document.add(new Paragraph("\n"));

            // Info permohonan
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                    .setWidth(UnitValue.createPercentValue(100));

            addTableRow(infoTable, "Nomor Permohonan", layanan.getNomorPermohonan());
            addTableRow(infoTable, "Tanggal Diajukan", layanan.getTglDiajukan() != null ? layanan.getTglDiajukan().toString() : "-");
            addTableRow(infoTable, "Status", layanan.getStatus() != null ? layanan.getStatus().name() : "-");
            addTableRow(infoTable, "Kategori", layanan.getKategori() != null ? layanan.getKategori().getNamaKategori() : "-");

            if (layanan.getUser() != null) {
                addTableRow(infoTable, "Nama Pemohon", layanan.getUser().getNamaLengkap());
                addTableRow(infoTable, "Email", layanan.getUser().getEmail());
                addTableRow(infoTable, "No. Telepon", layanan.getUser().getNoTelp() != null ? layanan.getUser().getNoTelp() : "-");
            }

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Jawaban form
            List<JawabanForm> jawabanList = jawabanFormRepository.findByLayananId(id);
            if (!jawabanList.isEmpty()) {
                document.add(new Paragraph("Data Permohonan").setBold().setFontSize(12));
                Table jawabanTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                        .setWidth(UnitValue.createPercentValue(100));

                for (JawabanForm jawaban : jawabanList) {
                    String label = jawaban.getField() != null ? jawaban.getField().getLabel() : "Field";
                    String nilai = jawaban.getNilai() != null ? jawaban.getNilai() : "-";
                    addTableRow(jawabanTable, label, nilai);
                }
                document.add(jawabanTable);
                document.add(new Paragraph("\n"));
            }

            // Catatan petugas
            if (layanan.getCatatanPetugas() != null && !layanan.getCatatanPetugas().isBlank()) {
                document.add(new Paragraph("Catatan Petugas").setBold().setFontSize(12));
                document.add(new Paragraph(layanan.getCatatanPetugas()));
                document.add(new Paragraph("\n"));
            }

            // Riwayat status
            List<RiwayatStatusLayanan> riwayatList = riwayatStatusLayananRepository.findByLayananIdOrderByCreatedAtDesc(id);
            if (!riwayatList.isEmpty()) {
                document.add(new Paragraph("Riwayat Status").setBold().setFontSize(12));
                Table riwayatTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                        .setWidth(UnitValue.createPercentValue(100));

                // Header row
                riwayatTable.addHeaderCell(new Cell().add(new Paragraph("Status Lama").setBold()));
                riwayatTable.addHeaderCell(new Cell().add(new Paragraph("Status Baru").setBold()));
                riwayatTable.addHeaderCell(new Cell().add(new Paragraph("Diubah Oleh").setBold()));
                riwayatTable.addHeaderCell(new Cell().add(new Paragraph("Tanggal").setBold()));

                for (RiwayatStatusLayanan r : riwayatList) {
                    riwayatTable.addCell(new Cell().add(new Paragraph(r.getStatusLama() != null ? r.getStatusLama() : "-")));
                    riwayatTable.addCell(new Cell().add(new Paragraph(r.getStatusBaru() != null ? r.getStatusBaru() : "-")));
                    riwayatTable.addCell(new Cell().add(new Paragraph(r.getDiubahOleh() != null ? r.getDiubahOleh() : "-")));
                    riwayatTable.addCell(new Cell().add(new Paragraph(r.getCreatedAt() != null ? r.getCreatedAt().toString() : "-")));
                }
                document.add(riwayatTable);
            }

            document.close();

            byte[] pdfBytes = baos.toByteArray();
            String filename = "laporan-" + (layanan.getNomorPermohonan() != null ? layanan.getNomorPermohonan() : id.toString()) + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/v1/laporan/pengaduan/{id}/pdf — generate dan download PDF pengaduan
    @GetMapping("/pengaduan/{id}/pdf")
    public ResponseEntity<byte[]> exportPengaduanPdf(@PathVariable UUID id) {
        try {
            Pengaduan pengaduan = pengaduanRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pengaduan not found"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header
            document.add(new Paragraph("SMARTPELAYANAN")
                    .setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Laporan Pengaduan Masyarakat")
                    .setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Info pengaduan
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                    .setWidth(UnitValue.createPercentValue(100));

            addTableRow(infoTable, "ID Pengaduan", pengaduan.getId().toString());
            addTableRow(infoTable, "Judul", pengaduan.getJudul() != null ? pengaduan.getJudul() : "-");
            addTableRow(infoTable, "Tanggal Kejadian", pengaduan.getTanggalKejadian() != null ? pengaduan.getTanggalKejadian().toString() : "-");
            addTableRow(infoTable, "Lokasi", pengaduan.getLokasi() != null ? pengaduan.getLokasi() : "-");
            addTableRow(infoTable, "Prioritas", pengaduan.getPrioritas() != null ? pengaduan.getPrioritas().name() : "-");
            addTableRow(infoTable, "Status", pengaduan.getStatus() != null ? pengaduan.getStatus().name() : "-");

            // Pelapor
            if (pengaduan.getUser() != null) {
                addTableRow(infoTable, "Nama Pelapor", pengaduan.getUser().getNamaLengkap());
                addTableRow(infoTable, "Kontak", pengaduan.getUser().getEmail());
            } else {
                addTableRow(infoTable, "Nama Pelapor", pengaduan.getNamaPelapor() != null ? pengaduan.getNamaPelapor() : "-");
                addTableRow(infoTable, "Kontak", pengaduan.getKontakPelapor() != null ? pengaduan.getKontakPelapor() : "-");
            }

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Deskripsi
            document.add(new Paragraph("Deskripsi Pengaduan").setBold().setFontSize(12));
            document.add(new Paragraph(pengaduan.getDeskripsi() != null ? pengaduan.getDeskripsi() : "-"));
            document.add(new Paragraph("\n"));

            // Foto bukti — embed gambar langsung ke PDF
            if (pengaduan.getFotoBukti() != null && !pengaduan.getFotoBukti().isBlank()) {
                document.add(new Paragraph("Foto Bukti").setBold().setFontSize(12));
                try {
                    // fotoBukti berisi path seperti "/uploads/xxxx.jpg"
                    String urlPath = pengaduan.getFotoBukti();
                    String fileName = urlPath.startsWith("/uploads/") ? urlPath.substring("/uploads/".length()) : urlPath;
                    Path imgPath = Paths.get("uploads").toAbsolutePath().resolve(fileName);

                    if (Files.exists(imgPath)) {
                        ImageData imageData = ImageDataFactory.create(imgPath.toUri().toURL());
                        Image img = new Image(imageData);
                        img.setMaxWidth(UnitValue.createPercentValue(80));
                        img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        document.add(img);
                    } else {
                        document.add(new Paragraph("Foto: " + pengaduan.getFotoBukti()).setFontSize(10));
                    }
                } catch (Exception imgEx) {
                    document.add(new Paragraph("Foto: " + pengaduan.getFotoBukti()).setFontSize(10));
                }
                document.add(new Paragraph("\n"));
            }


            // Catatan admin
            if (pengaduan.getCatatanAdmin() != null && !pengaduan.getCatatanAdmin().isBlank()) {
                document.add(new Paragraph("Catatan Admin").setBold().setFontSize(12));
                document.add(new Paragraph(pengaduan.getCatatanAdmin()));
                document.add(new Paragraph("\n"));
            }

            List<RiwayatStatus> riwayatList = riwayatStatusRepository
                    .findByReferensiIdAndTipeReferensiOrderByChangedAtDesc(id, RiwayatStatus.TipeReferensiEnum.PENGADUAN);
            if (!riwayatList.isEmpty()) {
                document.add(new Paragraph("Riwayat Status").setBold().setFontSize(12));
                Table riwayatTable = new Table(UnitValue.createPercentArray(new float[]{22, 22, 24, 32}))
                        .setWidth(UnitValue.createPercentValue(100));

                riwayatTable.addHeaderCell(new Cell().add(new Paragraph("Status Lama").setBold()));
                riwayatTable.addHeaderCell(new Cell().add(new Paragraph("Status Baru").setBold()));
                riwayatTable.addHeaderCell(new Cell().add(new Paragraph("Diubah Oleh").setBold()));
                riwayatTable.addHeaderCell(new Cell().add(new Paragraph("Tanggal").setBold()));

                for (RiwayatStatus r : riwayatList) {
                    riwayatTable.addCell(new Cell().add(new Paragraph(r.getStatusLama() != null ? r.getStatusLama() : "-")));
                    riwayatTable.addCell(new Cell().add(new Paragraph(r.getStatusBaru() != null ? r.getStatusBaru() : "-")));
                    riwayatTable.addCell(new Cell().add(new Paragraph(r.getAdmin() != null ? r.getAdmin().getEmail() : "-")));
                    riwayatTable.addCell(new Cell().add(new Paragraph(r.getChangedAt() != null ? r.getChangedAt().toString() : "-")));
                }
                document.add(riwayatTable);
                document.add(new Paragraph("\n"));
            }

            // Lampiran — embed gambar jika ada
            List<LampiranFile> lampiranList = lampiranFileRepository.findByReferensiId(id.toString());
            if (!lampiranList.isEmpty()) {
                document.add(new Paragraph("Lampiran File").setBold().setFontSize(12));
                for (LampiranFile l : lampiranList) {
                    document.add(new Paragraph("- " + l.getNamaFile()));
                    // Coba embed jika tipe gambar
                    String tipe = l.getTipeFile() != null ? l.getTipeFile().toLowerCase() : "";
                    boolean isImage = tipe.contains("image") || tipe.contains("jpg") || tipe.contains("jpeg")
                            || tipe.contains("png") || tipe.contains("gif") || tipe.contains("webp");
                    if (isImage && l.getNamaTersimpan() != null) {
                        try {
                            Path imgPath = Paths.get("uploads").toAbsolutePath().resolve(l.getNamaTersimpan());
                            if (Files.exists(imgPath)) {
                                ImageData imageData = ImageDataFactory.create(imgPath.toUri().toURL());
                                Image img = new Image(imageData);
                                img.setMaxWidth(UnitValue.createPercentValue(80));
                                img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                                document.add(img);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }

            document.close();

            byte[] pdfBytes = baos.toByteArray();
            String filename = "laporan-pengaduan-" + id.toString() + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private void addTableRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "-")));
    }
}
