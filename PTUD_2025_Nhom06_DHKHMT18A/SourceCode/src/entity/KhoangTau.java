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
public class KhoangTau {
    private String maKhoangTau;
    private String tenKhoangTau;
    private int soGhe;
    private ToaTau toaTau;

    public KhoangTau() {
    }

    public KhoangTau(String tenKhoangTau, int soGhe, ToaTau toaTau) {
        this.tenKhoangTau = tenKhoangTau;
        this.soGhe = soGhe;
        this.toaTau = toaTau;
    }

    public String getMaKhoangTau() {
        return maKhoangTau;
    }

    public void setMaKhoangTau(String maKhoangTau) {
        this.maKhoangTau = maKhoangTau;
    }

    public String getTenKhoaTau() {
        return tenKhoangTau;
    }

    public void setTenKhoangTau(String tenKhoangTau) {
        this.tenKhoangTau = tenKhoangTau;
    }

    public int getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(int soGhe) {
        this.soGhe = soGhe;
    }

    public ToaTau getToaTau() {
        return toaTau;
    }

    public void setToaTau(ToaTau toaTau) {
        this.toaTau = toaTau;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.maKhoangTau);
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
        final KhoangTau other = (KhoangTau) obj;
        return Objects.equals(this.maKhoangTau, other.maKhoangTau);
    }

    @Override
    public String toString() {
        return "KhoangTau{" + "maKhoangTau=" + maKhoangTau + ", tenKhoaTau=" + tenKhoangTau + ", soGhe=" + soGhe + ", toaTau=" + toaTau + '}';
    }
    
    
    
}
