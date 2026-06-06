package com.smartpelayanan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_jawaban_form")
public class JawabanForm extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "layanan_id", nullable = false)
    private LayananAdministrasi layanan;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private FormField field;

    @Column(name = "nilai", columnDefinition = "TEXT")
    private String nilai; // nilai teks atau URL file untuk tipe file

    public LayananAdministrasi getLayanan() { return layanan; }
    public void setLayanan(LayananAdministrasi layanan) { this.layanan = layanan; }

    public FormField getField() { return field; }
    public void setField(FormField field) { this.field = field; }

    public String getNilai() { return nilai; }
    public void setNilai(String nilai) { this.nilai = nilai; }
}
