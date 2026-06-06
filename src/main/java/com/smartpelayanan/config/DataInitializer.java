package com.smartpelayanan.config;

import com.smartpelayanan.entity.*;
import com.smartpelayanan.enums.*;
import com.smartpelayanan.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final KategoriLayananRepository kategoriLayananRepository;
    private final PengaduanRepository pengaduanRepository;
    private final LayananAdministrasiRepository layananAdministrasiRepository;
    private final PasswordEncoder passwordEncoder;
    private final FormFieldRepository formFieldRepository;

    public DataInitializer(UserRepository userRepository, 
                           KategoriLayananRepository kategoriLayananRepository,
                           PengaduanRepository pengaduanRepository,
                           LayananAdministrasiRepository layananAdministrasiRepository,
                           PasswordEncoder passwordEncoder,
                           FormFieldRepository formFieldRepository) {
        this.userRepository = userRepository;
        this.kategoriLayananRepository = kategoriLayananRepository;
        this.pengaduanRepository = pengaduanRepository;
        this.layananAdministrasiRepository = layananAdministrasiRepository;
        this.passwordEncoder = passwordEncoder;
        this.formFieldRepository = formFieldRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.existsByEmail("admin@smartpelayanan.com")) {
            System.out.println("Data already initialized, skipping...");
            return;
        }

        // Create Admin
        User admin = new User();
        admin.setNamaLengkap("Administrator");
        admin.setEmail("admin@smartpelayanan.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setNoTelp("081234567890");
        admin.setAlamat("Kantor Desa");
        admin.setRole(RoleEnum.ADMIN);
        admin.setStatusAktif(true);
        userRepository.save(admin);

        // Create Warga (Citizens)
        User warga1 = createWarga("Budi Santoso", "warga@gmail.com", "081111111111", "Jl. Merdeka No. 10");
        User warga2 = createWarga("Siti Aminah", "warga2@gmail.com", "081222222222", "Jl. Sudirman No. 25");
        User warga3 = createWarga("Ahmad Fauzi", "ahmad@gmail.com", "081333333333", "Jl. Diponegoro No. 5");
        User warga4 = createWarga("Diana Putri", "diana@gmail.com", "081444444444", "Jl. Gatot Subroto No. 15");

        // Create Kategori Layanan (6 kategori sesuai rencana)
        KategoriLayanan ktp = createKategori("KTP - Kartu Tanda Penduduk", "Pengajuan dan perbaikan KTP", true);
        KategoriLayanan kk = createKategori("KK - Kartu Keluarga", "Pembuatan dan perubahan KK", true);
        KategoriLayanan suratKeterangan = createKategori("Surat Keterangan", "Surat keterangan domisili, usaha, dll", true);
        KategoriLayanan izinUsaha = createKategori("Izin Usaha", "Perizinan usaha dan kegiatan", true);
        KategoriLayanan suratKematian = createKategori("Surat Kematian", "Pengurusan surat kematian", true);
        KategoriLayanan aktaKelahiran = createKategori("Akta Kelahiran", "Pengurusan akta kelahiran", true);

        // Seed form fields untuk 6 kategori
        if (formFieldRepository.count() == 0) {
            seedFieldsKTP(ktp);
            seedFieldsKK(kk);
            seedFieldsSuratKeterangan(suratKeterangan);
            seedFieldsIzinUsaha(izinUsaha);
            seedFieldsSuratKematian(suratKematian);
            seedFieldsAktaKelahiran(aktaKelahiran);
        }

        // Create Pengaduan
        createPengaduan(warga1, "Jalan Rusak Parah", "Jalan di RT 03 sangat rusak dan berbahaya", 
            "Jl. Raya RT 03", PrioritasEnum.TINGGI, StatusPengaduanEnum.BARU);
        createPengaduan(warga2, "Sampah Menumpuk", "Sampah di depan rumah sudah 3 hari tidak diambil", 
            "Jl. Sudirman", PrioritasEnum.SEDANG, StatusPengaduanEnum.DIPROSES);
        createPengaduan(warga3, "Listrik Mati", "Listrik di lingkungan sudah 2 hari mati total", 
            "Jl. Diponegoro", PrioritasEnum.TINGGI, StatusPengaduanEnum.SELESAI);
        createPengaduan(warga4, "Air PDAM Tidak Lancar", "Air PDAM jarang mengalir especially pagi hari", 
            "Jl. Gatot Subroto", PrioritasEnum.SEDANG, StatusPengaduanEnum.BARU);
        createPengaduan(warga1, "Lampu Jalan Mati", "Lampu jalan di JL. Merdeka sudah 1 minggu mati", 
            "Jl. Merdeka", PrioritasEnum.RENDAH, StatusPengaduanEnum.DITOLAK);

        // Create Layanan Administrasi
        createLayanan(warga1, ktp, "Permohonan KTP Baru", StatusLayananEnum.MENUNGGU);
        createLayanan(warga2, kk, "Perubahan data KK karena lahiran", StatusLayananEnum.DIPROSES);
        createLayanan(warga3, suratKeterangan, "Surat keterangan domisili", StatusLayananEnum.SELESAI);
        createLayanan(warga4, izinUsaha, "Izin operasional toko", StatusLayananEnum.MENUNGGU);
        createLayanan(warga1, aktaKelahiran, "Akta kelahiran anak pertama", StatusLayananEnum.DITOLAK);

        System.out.println("=== DATA BERHASIL DIINISIALISASI ===");
        System.out.println("Admin:      admin@smartpelayanan.com / admin123");
        System.out.println("Warga 1:    warga@gmail.com / warga123");
        System.out.println("Warga 2:    warga2@gmail.com / warga123");
    }

    private void seedFieldsKTP(KategoriLayanan kategori) {
        formFieldRepository.save(createField("NIK", "number", true, 1, null, "Masukkan 16 digit NIK", kategori));
        formFieldRepository.save(createField("Nama Lengkap", "text", true, 2, null, "Nama sesuai dokumen", kategori));
        formFieldRepository.save(createField("Alamat", "textarea", true, 3, null, "Alamat lengkap sesuai domisili", kategori));
        formFieldRepository.save(createField("Keperluan KTP", "select", true, 4, "[\"Baru\",\"Perpanjang\",\"Hilang\"]", null, kategori));
        formFieldRepository.save(createField("Foto KTP Lama", "file", false, 5, null, "Upload foto KTP lama (jika ada)", kategori));
    }

    private void seedFieldsKK(KategoriLayanan kategori) {
        formFieldRepository.save(createField("NIK Kepala Keluarga", "number", true, 1, null, "Masukkan 16 digit NIK", kategori));
        formFieldRepository.save(createField("Jenis Perubahan", "select", true, 2, "[\"Baru\",\"Tambah Anggota\",\"Pisah KK\"]", null, kategori));
        formFieldRepository.save(createField("Nama Anggota Baru", "text", false, 3, null, "Isi jika menambah anggota", kategori));
        formFieldRepository.save(createField("Dokumen Pendukung", "file", true, 4, null, "Upload dokumen pendukung", kategori));
    }

    private void seedFieldsSuratKeterangan(KategoriLayanan kategori) {
        formFieldRepository.save(createField("Jenis Surat", "select", true, 1, "[\"Domisili\",\"Tidak Mampu\",\"Usaha\",\"Lainnya\"]", null, kategori));
        formFieldRepository.save(createField("Tujuan Surat", "text", true, 2, null, "Tujuan pembuatan surat", kategori));
        formFieldRepository.save(createField("Ditujukan Kepada", "text", true, 3, null, "Instansi/pihak yang dituju", kategori));
        formFieldRepository.save(createField("Keperluan", "textarea", true, 4, null, "Jelaskan keperluan surat", kategori));
    }

    private void seedFieldsIzinUsaha(KategoriLayanan kategori) {
        formFieldRepository.save(createField("Nama Usaha", "text", true, 1, null, "Nama usaha/bisnis", kategori));
        formFieldRepository.save(createField("Jenis Usaha", "text", true, 2, null, "Jenis/bidang usaha", kategori));
        formFieldRepository.save(createField("Alamat Usaha", "textarea", true, 3, null, "Alamat lengkap tempat usaha", kategori));
        formFieldRepository.save(createField("Modal Usaha", "number", false, 4, null, "Estimasi modal usaha (Rp)", kategori));
        formFieldRepository.save(createField("Foto Tempat Usaha", "file", true, 5, null, "Upload foto tempat usaha", kategori));
        formFieldRepository.save(createField("Dokumen Identitas", "file", true, 6, null, "Upload KTP/identitas pemilik", kategori));
    }

    private void seedFieldsSuratKematian(KategoriLayanan kategori) {
        formFieldRepository.save(createField("Nama Almarhum", "text", true, 1, null, "Nama lengkap almarhum/almarhumah", kategori));
        formFieldRepository.save(createField("NIK Almarhum", "number", true, 2, null, "NIK almarhum/almarhumah", kategori));
        formFieldRepository.save(createField("Tanggal Meninggal", "date", true, 3, null, null, kategori));
        formFieldRepository.save(createField("Tempat Meninggal", "text", true, 4, null, "RS/rumah/tempat meninggal", kategori));
        formFieldRepository.save(createField("Penyebab Kematian", "text", false, 5, null, "Penyebab kematian (opsional)", kategori));
        formFieldRepository.save(createField("Surat Keterangan RS/Dokter", "file", true, 6, null, "Upload surat keterangan dari RS atau dokter", kategori));
    }

    private void seedFieldsAktaKelahiran(KategoriLayanan kategori) {
        formFieldRepository.save(createField("Nama Bayi", "text", true, 1, null, "Nama lengkap bayi", kategori));
        formFieldRepository.save(createField("Tanggal Lahir", "date", true, 2, null, null, kategori));
        formFieldRepository.save(createField("Tempat Lahir", "text", true, 3, null, "Kota/kabupaten tempat lahir", kategori));
        formFieldRepository.save(createField("Jenis Kelamin", "select", true, 4, "[\"Laki-laki\",\"Perempuan\"]", null, kategori));
        formFieldRepository.save(createField("Nama Ayah", "text", true, 5, null, "Nama lengkap ayah", kategori));
        formFieldRepository.save(createField("Nama Ibu", "text", true, 6, null, "Nama lengkap ibu", kategori));
        formFieldRepository.save(createField("Surat Keterangan Lahir RS", "file", true, 7, null, "Upload surat keterangan lahir dari RS/bidan", kategori));
    }

    private FormField createField(String label, String tipe, boolean required, int urutan, String opsi, String placeholder, KategoriLayanan kategori) {
        FormField field = new FormField();
        field.setLabel(label);
        field.setTipe(tipe);
        field.setRequired(required);
        field.setUrutan(urutan);
        field.setOpsi(opsi);
        field.setPlaceholder(placeholder);
        field.setKategori(kategori);
        return field;
    }

    private User createWarga(String nama, String email, String noTelp, String alamat) {
        User user = new User();
        user.setNamaLengkap(nama);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("warga123"));
        user.setNoTelp(noTelp);
        user.setAlamat(alamat);
        user.setRole(RoleEnum.WARGA);
        user.setStatusAktif(true);
        return userRepository.save(user);
    }

    private KategoriLayanan createKategori(String nama, String deskripsi, boolean isActive) {
        KategoriLayanan kategori = new KategoriLayanan();
        kategori.setNamaKategori(nama);
        kategori.setDeskripsi(deskripsi);
        kategori.setIsActive(isActive);
        return kategoriLayananRepository.save(kategori);
    }

    private Pengaduan createPengaduan(User user, String judul, String deskripsi, String lokasi, 
                                       PrioritasEnum prioritas, StatusPengaduanEnum status) {
        Pengaduan pengaduan = new Pengaduan();
        pengaduan.setUser(user);
        pengaduan.setJudul(judul);
        pengaduan.setDeskripsi(deskripsi);
        pengaduan.setLokasi(lokasi);
        pengaduan.setPrioritas(prioritas);
        pengaduan.setStatus(status);
        pengaduan.setTanggalKejadian(LocalDate.now());
        return pengaduanRepository.save(pengaduan);
    }

    private LayananAdministrasi createLayanan(User user, KategoriLayanan kategori, String keperluan, 
                                               StatusLayananEnum status) {
        LayananAdministrasi layanan = new LayananAdministrasi();
        layanan.setUser(user);
        layanan.setKategori(kategori);
        layanan.setKeperluan(keperluan);
        layanan.setStatus(status);
        layanan.setNomorPermohonan("PLY-" + System.currentTimeMillis());
        layanan.setTglDiajukan(LocalDate.now());
        return layananAdministrasiRepository.save(layanan);
    }
}
