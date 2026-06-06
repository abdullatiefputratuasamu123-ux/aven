package com.smartpelayanan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_form_field")
public class FormField extends BaseEntity {

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "tipe", nullable = false)
    private String tipe; // text | number | textarea | select | date | file

    @Column(name = "required")
    private Boolean required = true;

    @Column(name = "urutan")
    private Integer urutan = 0;

    @Column(name = "opsi", columnDefinition = "TEXT")
    private String opsi; // JSON array string untuk tipe select: ["Opsi1","Opsi2"]

    @Column(name = "placeholder")
    private String placeholder;

    @ManyToOne
    @JoinColumn(name = "kategori_id", nullable = false)
    private KategoriLayanan kategori;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public Boolean getRequired() { return required; }
    public void setRequired(Boolean required) { this.required = required; }

    public Integer getUrutan() { return urutan; }
    public void setUrutan(Integer urutan) { this.urutan = urutan; }

    public String getOpsi() { return opsi; }
    public void setOpsi(String opsi) { this.opsi = opsi; }

    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

    public KategoriLayanan getKategori() { return kategori; }
    public void setKategori(KategoriLayanan kategori) { this.kategori = kategori; }
}
