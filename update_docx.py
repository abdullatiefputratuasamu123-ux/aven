#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Update Tubes_OOP.docx:
  - Judul   : SISTEM PELAYANAN MASYARAKAT (SmartPelayanan)
  - Kelompok: 5
  - Anggota :
      Adi Nugraha                  152024105
      Abdul Latief Putra Tuasamu   152024124
      Resa Aulia A                 152024128
      Dila Amelisa S               152024158

Cara pakai:
  python update_docx.py
atau double-click update_docx.bat
"""

import os, shutil, zipfile, sys

# ── Paths ─────────────────────────────────────────────────────────────────
BASE  = r'C:\Users\MyBook Hype AMD\Downloads\prakoopp'
SRC   = os.path.join(BASE, 'Tubes_OOP.docx')
BAK   = os.path.join(BASE, 'Tubes_OOP_original.bak')

# ── Tabel penggantian teks (urutan penting: panjang dulu) ─────────────────
# Format: (teks_lama, teks_baru)
REPLACEMENTS = [
    # ── Judul utama (cover) ──────────────────────────────────────────────
    ('SISTEM PELAYANAN DONOR DARAH MASYARAKAT',
     'SISTEM PELAYANAN MASYARAKAT (SmartPelayanan)'),

    # ── Dalam kalimat (huruf besar-kecil campuran) ───────────────────────
    ('Sistem Pelayanan Donor Darah Masyarakat',
     'Sistem Pelayanan Masyarakat (SmartPelayanan)'),

    ('"Sistem Pelayanan Donor Darah Masyarakat"',
     '"Sistem Pelayanan Masyarakat (SmartPelayanan)"'),

    # ── Donor Darah → Pelayanan Masyarakat ──────────────────────────────
    ('sistem manajemen donor darah berbasis web',
     'sistem pelayanan masyarakat berbasis web'),
    ('Sistem Manajemen Donor Darah Berbasis Web',
     'Sistem Pelayanan Masyarakat Berbasis Web (SmartPelayanan)'),
    ('Sistem Manajemen Donor Darah berbasis web',
     'Sistem Pelayanan Masyarakat berbasis web'),
    ('sistem manajemen donor darah',
     'sistem pelayanan masyarakat'),
    ('Sistem Manajemen Donor Darah',
     'Sistem Pelayanan Masyarakat'),

    # ── Kata Pengantar ───────────────────────────────────────────────────
    ('dalam menerapkan teknologi informatika untuk mendukung pelayanan masyarakat di bidang Peningkatan Pelayanan Masyarakat. Proposal ini membahas mengenai perancangan sistem manajemen donor darah berbasis web yang dapat membantu dalam pengelolaan data donor, pemantauan kebutuhan darah, serta proses pendaftaran donor secara online. Melalui sistem ini diharapkan pelayanan donor darah dapat dilakukan dengan lebih efektif, efisien, dan terorganisir.',
     'dalam menerapkan teknologi informatika untuk mendukung pelayanan masyarakat di bidang Peningkatan Layanan Publik. Proposal ini membahas mengenai perancangan sistem pelayanan masyarakat berbasis web (SmartPelayanan) yang dapat membantu dalam pengelolaan pengaduan, layanan administrasi, dan komunikasi antara warga dengan pemerintah secara online. Melalui sistem ini diharapkan pelayanan masyarakat dapat dilakukan dengan lebih efektif, efisien, dan terorganisir.'),

    # ── Latar Belakang ───────────────────────────────────────────────────
    ('Salah satu penerapannya adalah dalam sistem manajemen donor darah yang dapat membantu proses pencatatan, pemantauan, serta distribusi darah secara lebih terstruktur.',
     'Salah satu penerapannya adalah dalam sistem pelayanan masyarakat digital yang dapat membantu proses pencatatan pengaduan, pengelolaan layanan administrasi, serta komunikasi antara warga dan pemerintah secara lebih terstruktur.'),

    ('Ketersediaan darah merupakan kebutuhan penting dalam pelayanan medis, terutama dalam kondisi darurat maupun tindakan medis tertentu. Namun, dalam praktiknya, masih sering terjadi ketidakseimbangan antara ketersediaan dan kebutuhan darah di rumah sakit. Hal ini disebabkan oleh kurangnya sistem yang mampu mengelola data donor dan kebutuhan darah secara terintegrasi serta terbatasnya akses informasi bagi masyarakat.',
     'Pelayanan publik yang efektif dan transparan merupakan hak dasar setiap warga negara. Namun dalam praktiknya, masih banyak warga yang mengalami kesulitan dalam menyampaikan pengaduan, mengakses layanan administrasi, maupun memantau status permohonan mereka. Hal ini disebabkan oleh kurangnya sistem yang mampu mengelola komunikasi antara warga dan pemerintah secara terintegrasi.'),

    ('Proses pencarian donor darah saat ini masih banyak dilakukan secara manual, seperti melalui media sosial atau komunikasi langsung, sehingga informasi tidak selalu tersampaikan secara cepat dan tepat. Selain itu, pengelolaan data donor, jadwal donor, serta riwayat donor masih belum terorganisir dengan baik dalam suatu sistem terpusat.',
     'Proses penyampaian pengaduan masyarakat saat ini masih banyak dilakukan secara manual, seperti melalui surat fisik atau kunjungan langsung ke kantor pelayanan, sehingga informasi tidak selalu tersampaikan secara cepat dan tepat. Selain itu, pengelolaan data pengaduan dan layanan administrasi masih belum terorganisir dengan baik dalam suatu sistem terpusat.'),

    ('Oleh karena itu, dalam penelitian ini akan dirancang sebuah sistem manajemen donor darah berbasis web yang dapat menghubungkan rumah sakit dengan masyarakat sebagai donor, menyediakan informasi kebutuhan darah, serta mempermudah proses pendaftaran donor secara online. Sistem ini diharapkan dapat meningkatkan kualitas pelayanan masyarakat di bidang kesehatan.',
     'Oleh karena itu, dalam penelitian ini akan dirancang sebuah sistem pelayanan masyarakat berbasis web (SmartPelayanan) yang dapat menghubungkan warga dengan pemerintah, menyediakan layanan pengaduan dan administrasi secara online, serta mempermudah pemantauan status layanan secara real-time. Sistem ini diharapkan dapat meningkatkan kualitas pelayanan publik secara keseluruhan.'),

    # ── Rumusan Masalah ──────────────────────────────────────────────────
    ('Bagaimana merancang sistem manajemen donor darah berbasis web yang dapat meningkatkan pelayanan masyarakat?',
     'Bagaimana merancang sistem pelayanan masyarakat berbasis web (SmartPelayanan) yang dapat meningkatkan kualitas layanan publik?'),
    ('Bagaimana mengelola data donor dan kebutuhan darah secara terstruktur dalam sistem?',
     'Bagaimana mengelola data pengaduan dan layanan administrasi masyarakat secara terstruktur dalam sistem?'),
    ('Bagaimana menyediakan fitur pendaftaran donor darah secara online?',
     'Bagaimana menyediakan fitur pengaduan dan permohonan layanan administrasi secara online?'),
    ('Bagaimana menyajikan informasi ketersediaan dan kebutuhan darah secara efektif?',
     'Bagaimana menyajikan informasi status layanan dan pengaduan kepada masyarakat secara efektif?'),

    # ── Tujuan ───────────────────────────────────────────────────────────
    ('Merancang sistem manajemen donor darah berbasis web untuk meningkatkan pelayanan masyarakat',
     'Merancang sistem pelayanan masyarakat berbasis web (SmartPelayanan) untuk meningkatkan kualitas layanan publik'),
    ('Mengelola data donor dan kebutuhan darah secara terintegrasi',
     'Mengelola data pengaduan dan layanan administrasi masyarakat secara terintegrasi'),
    ('Mengembangkan fitur pendaftaran donor darah secara online',
     'Mengembangkan fitur pengaduan dan permohonan layanan administrasi secara online'),
    ('Menyediakan informasi terkait ketersediaan darah secara lebih efektif',
     'Menyediakan informasi status layanan dan pengaduan kepada masyarakat secara lebih efektif'),

    # ── Manfaat ──────────────────────────────────────────────────────────
    ('Rumah Sakit Membantu dalam mengelola data donor darah serta memantau kebutuhan dan ketersediaan darah secara lebih terstruktur dan efisien.',
     'Pemerintah/Instansi Membantu dalam mengelola pengaduan masyarakat serta memantau dan merespons layanan administrasi secara lebih terstruktur dan efisien.'),
    ('Masyarakat Mempermudah dalam memperoleh informasi terkait kebutuhan darah serta melakukan pendaftaran donor darah secara online.',
     'Masyarakat Mempermudah dalam menyampaikan pengaduan, mengajukan layanan administrasi, dan memantau status permohonan secara online.'),
    ('Peneliti Menambah wawasan dan pengalaman dalam merancang serta mengembangkan sistem berbasis web di bidang pelayanan kesehatan.',
     'Peneliti Menambah wawasan dan pengalaman dalam merancang serta mengembangkan sistem berbasis web di bidang pelayanan publik dan e-government.'),

    # ── Batasan Masalah ──────────────────────────────────────────────────
    ('Sistem difokuskan pada pengelolaan data donor dan kebutuhan darah',
     'Sistem difokuskan pada pengelolaan pengaduan masyarakat dan layanan administrasi'),
    ('Fitur yang disediakan meliputi pendaftaran donor, data donor, dan informasi kebutuhan darah',
     'Fitur yang disediakan meliputi pengaduan masyarakat, layanan administrasi, notifikasi, dan manajemen pengguna'),
    ('Sistem tidak mencakup proses medis dalam donor darah',
     'Sistem tidak mencakup proses fisik pelayanan di lapangan'),

    # ── Studi Kasus ──────────────────────────────────────────────────────
    ('Sistem yang dirancang adalah Sistem Pelayanan Donor Darah Masyarakat . Sistem ini bertujuan untuk membantu masyarakat dalam memperoleh informasi dan layanan terkait donor darah secara lebih cepat, transparan, dan efisien.',
     'Sistem yang dirancang adalah Sistem Pelayanan Masyarakat (SmartPelayanan). Sistem ini bertujuan untuk membantu masyarakat dalam menyampaikan pengaduan, mengakses layanan administrasi, dan memantau status permohonan secara lebih cepat, transparan, dan efisien.'),
    ('Saat ini, proses pelayanan donor darah di banyak tempat seperti PMI masih dilakukan secara manual atau belum terintegrasi dengan baik. Masyarakat sering mengalami kesulitan dalam menemukan lokasi donor darah terdekat, mengetahui ketersediaan stok darah, serta melakukan permintaan darah secara cepat. Selain itu, belum tersedia sistem yang dapat menampung pengaduan masyarakat terkait pelayanan donor darah.',
     'Saat ini, proses pelayanan administrasi dan penanganan pengaduan masyarakat di banyak instansi masih dilakukan secara manual atau belum terintegrasi dengan baik. Masyarakat sering mengalami kesulitan dalam menyampaikan keluhan, mengetahui status permohonan, serta mengakses layanan administrasi secara cepat. Selain itu, belum tersedia sistem yang dapat mengelola pengaduan dan layanan masyarakat secara terpadu dalam satu platform.'),

    # ── Sisa referensi donor darah umum ─────────────────────────────────
    ('pelayanan donor darah', 'pelayanan masyarakat'),
    ('donor darah', 'pelayanan masyarakat'),
    ('sistem donor darah', 'sistem pelayanan masyarakat'),
    ('data donor darah', 'data pengaduan masyarakat'),
    ('stok darah', 'data layanan'),
    ('kebutuhan darah', 'kebutuhan layanan masyarakat'),
    ('permintaan darah', 'permohonan layanan'),
    ('PMI', 'instansi pelayanan'),
    ('golongan darah', 'kategori layanan'),
]

# ── Fungsi utama ──────────────────────────────────────────────────────────────

def apply_replacements(content: str) -> str:
    """Apply all text replacements sequentially."""
    for old, new in REPLACEMENTS:
        if old in content:
            content = content.replace(old, new)
            print(f'  ✓ Replaced: "{old[:60]}..."')
    return content


def update_docx(src: str, dst: str, bak: str):
    # ── 1. Backup ──────────────────────────────────────────────────────
    if not os.path.exists(bak):
        shutil.copy2(src, bak)
        print(f'✓ Backup dibuat: {os.path.basename(bak)}')
    else:
        print(f'  Backup sudah ada: {os.path.basename(bak)}')

    # ── 2. Baca semua file di dalam zip (docx) ─────────────────────────
    with zipfile.ZipFile(src, 'r') as zin:
        names = zin.namelist()
        files = {}
        for name in names:
            files[name] = zin.read(name)

    # ── 3. Proses XML files ────────────────────────────────────────────
    xml_targets = [n for n in names if n.endswith('.xml') or n.endswith('.rels')]

    changed = 0
    for name in xml_targets:
        original_bytes = files[name]
        try:
            text = original_bytes.decode('utf-8')
        except UnicodeDecodeError:
            continue  # skip binary

        updated = apply_replacements(text)
        if updated != text:
            files[name] = updated.encode('utf-8')
            changed += 1

    print(f'\n✓ Total file XML diubah: {changed}')

    # ── 4. Tulis kembali ke docx ───────────────────────────────────────
    tmp = dst + '.tmp'
    with zipfile.ZipFile(tmp, 'w', zipfile.ZIP_DEFLATED) as zout:
        for name in names:
            zout.writestr(name, files[name])

    os.replace(tmp, dst)
    print(f'✅ Dokumen berhasil disimpan: {os.path.basename(dst)}')

    # ── 5. Verifikasi ──────────────────────────────────────────────────
    print('\n── Verifikasi isi cover ──')
    import xml.etree.ElementTree as ET
    W = 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'
    with zipfile.ZipFile(dst, 'r') as z:
        tree = ET.parse(z.open('word/document.xml'))
        root = tree.getroot()
        paragraphs = root.findall(f'.//{{{W}}}p')
        count = 0
        for p in paragraphs:
            txt = ''.join(t.text or '' for t in p.iter(f'{{{W}}}t'))
            if txt.strip():
                print(f'  {txt[:100]}')
                count += 1
                if count >= 25:
                    break


if __name__ == '__main__':
    print('=' * 55)
    print('  UPDATE Tubes_OOP.docx → SmartPelayanan')
    print('=' * 55)
    print()

    if not os.path.exists(SRC):
        print(f'[ERROR] File tidak ditemukan: {SRC}')
        sys.exit(1)

    update_docx(SRC, SRC, BAK)
    print('\nSelesai!')
