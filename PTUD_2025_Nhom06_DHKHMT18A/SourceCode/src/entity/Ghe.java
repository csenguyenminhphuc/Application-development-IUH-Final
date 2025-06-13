/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.Objects;

/**
 *
 * @author PHAMGIAKHANH
 */
public class Ghe {
    private String maGhe;
    private String viTri;
    private KhoangTau khoangTau;
    private LoaiGhe loaiGhe;

    public Ghe() {
    }

    public Ghe(String viTri, KhoangTau khoangTau, LoaiGhe loaiGhe) {
        this.viTri = viTri;
        this.khoangTau = khoangTau;
        this.loaiGhe = loaiGhe;
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public KhoangTau getKhoangTau() {
        return khoangTau;
    }

    public void setKhoangTau(KhoangTau khoangTau) {
        this.khoangTau = khoangTau;
    }

    public LoaiGhe getLoaiGhe() {
        return loaiGhe;
    }

    public void setLoaiGhe(LoaiGhe loaiGhe) {
        this.loaiGhe = loaiGhe;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.maGhe);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ghe other = (Ghe) obj;
        return Objects.equals(this.maGhe, other.maGhe);
    }

    @Override
    public String toString() {
        return "Ghe{" + "maGhe=" + maGhe + ", viTri=" + viTri + ", khoangTau=" + khoangTau + ", loaiGhe=" + loaiGhe + '}';
    }
    
    
}
