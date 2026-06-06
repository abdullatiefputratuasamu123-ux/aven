CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS tb_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nama_lengkap VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    no_telp VARCHAR(255),
    alamat TEXT,
    role VARCHAR(50) NOT NULL,
    status_aktif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS tb_kategori_layanan (
    id SERIAL PRIMARY KEY,
    nama_kategori VARCHAR(255) NOT NULL,
    deskripsi TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS tb_pengaduan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    judul VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    lokasi VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'BARU',
    prioritas VARCHAR(50) NOT NULL DEFAULT 'SEDANG',
    foto_bukti VARCHAR(255),
    tanggal_kejadian DATE,
    catatan_admin TEXT,
    user_id UUID REFERENCES tb_user(id),
    nama_pelapor VARCHAR(255),
    kontak_pelapor VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tb_layanan_administrasi (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nomor_permohonan VARCHAR(255) UNIQUE,
    keperluan TEXT,
    dokumen_pendukung VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'MENUNGGU',
    catatan_petugas TEXT,
    tgl_diajukan DATE,
    tgl_selesai DATE,
    user_id UUID NOT NULL REFERENCES tb_user(id),
    kategori_id INTEGER NOT NULL REFERENCES tb_kategori_layanan(id)
);

CREATE TABLE IF NOT EXISTS tb_form_field (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    label VARCHAR(255) NOT NULL,
    tipe VARCHAR(50) NOT NULL,
    required BOOLEAN DEFAULT FALSE,
    urutan INTEGER DEFAULT 0,
    opsi TEXT,
    placeholder VARCHAR(255),
    kategori_id INTEGER NOT NULL REFERENCES tb_kategori_layanan(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_jawaban_form (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    layanan_id UUID NOT NULL REFERENCES tb_layanan_administrasi(id) ON DELETE CASCADE,
    field_id UUID NOT NULL REFERENCES tb_form_field(id) ON DELETE CASCADE,
    nilai TEXT
);

CREATE TABLE IF NOT EXISTS tb_lampiran_file (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nama_file VARCHAR(255) NOT NULL,
    nama_tersimpan VARCHAR(255) NOT NULL,
    tipe_file VARCHAR(255),
    ukuran_file BIGINT,
    url_file VARCHAR(255),
    referensi_id VARCHAR(255),
    tipe_referensi VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS tb_notifikasi (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    judul VARCHAR(255) NOT NULL,
    pesan TEXT,
    tipe VARCHAR(100),
    referensi_id VARCHAR(255),
    sudah_dibaca BOOLEAN DEFAULT FALSE,
    user_id UUID NOT NULL REFERENCES tb_user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_respon_pengaduan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    isi_respon TEXT NOT NULL,
    tgl_respon TIMESTAMP NOT NULL,
    pengaduan_id UUID NOT NULL REFERENCES tb_pengaduan(id) ON DELETE CASCADE,
    admin_id UUID NOT NULL REFERENCES tb_user(id)
);

CREATE TABLE IF NOT EXISTS tb_riwayat_status (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    referensi_id UUID NOT NULL,
    tipe_referensi VARCHAR(50) NOT NULL,
    status_lama VARCHAR(50),
    status_baru VARCHAR(50),
    alasan_perubahan TEXT,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    admin_id UUID NOT NULL REFERENCES tb_user(id)
);

CREATE TABLE IF NOT EXISTS tb_riwayat_status_layanan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    layanan_id UUID NOT NULL REFERENCES tb_layanan_administrasi(id) ON DELETE CASCADE,
    status_lama VARCHAR(50),
    status_baru VARCHAR(50),
    catatan TEXT,
    diubah_oleh VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_pengaduan_user_id ON tb_pengaduan(user_id);
CREATE INDEX IF NOT EXISTS idx_layanan_user_id ON tb_layanan_administrasi(user_id);
CREATE INDEX IF NOT EXISTS idx_layanan_kategori_id ON tb_layanan_administrasi(kategori_id);
CREATE INDEX IF NOT EXISTS idx_form_field_kategori_id ON tb_form_field(kategori_id);
CREATE INDEX IF NOT EXISTS idx_jawaban_form_layanan_id ON tb_jawaban_form(layanan_id);
CREATE INDEX IF NOT EXISTS idx_lampiran_referensi_id ON tb_lampiran_file(referensi_id);
CREATE INDEX IF NOT EXISTS idx_notifikasi_user_id ON tb_notifikasi(user_id);
CREATE INDEX IF NOT EXISTS idx_riwayat_status_referensi_id ON tb_riwayat_status(referensi_id);
CREATE INDEX IF NOT EXISTS idx_riwayat_layanan_layanan_id ON tb_riwayat_status_layanan(layanan_id);
