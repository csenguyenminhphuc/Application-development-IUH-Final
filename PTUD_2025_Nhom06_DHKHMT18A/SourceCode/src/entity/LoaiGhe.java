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
public class LoaiGhe {
    private String maLoaiGhe;
    private String tenLoaiGhe;
    private String moTa;
    private double heSoGhe;

    public LoaiGhe() {
    }

    public LoaiGhe(String tenLoaiGhe, String moTa, double heSoGhe) {
        this.tenLoaiGhe = tenLoaiGhe;
        this.moTa = moTa;
        this.heSoGhe = heSoGhe;
    }

    public LoaiGhe(String maLoaiGhe, String tenLoaiGhe, String moTa, double heSoGhe) {
        this.maLoaiGhe = maLoaiGhe;
        this.tenLoaiGhe = tenLoaiGhe;
        this.moTa = moTa;
        this.heSoGhe = heSoGhe;
    }

    public String getMaLoaiGhe() {
        return maLoaiGhe;
    }

    public void setMaLoaiGhe(String maLoaiGhe) {
        this.maLoaiGhe = maLoaiGhe;
    }

    public String getTenLoaiGhe() {
        return tenLoaiGhe;
    }

    public void setTenLoaiGhe(String tenLoaiGhe) {
        this.tenLoaiGhe = tenLoaiGhe;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public double getHeSoGhe() {
        return heSoGhe;
    }

    public void setHeSoGhe(double heSoGhe) {
        this.heSoGhe = heSoGhe;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.maLoaiGhe);
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
        final LoaiGhe other = (LoaiGhe) obj;
        return Objects.equals(this.maLoaiGhe, other.maLoaiGhe);
    }

    @Override
    public String toString() {
        return "LoaiGhe{" + "maLoaiGhe=" + maLoaiGhe + ", tenLoaiGhe=" + tenLoaiGhe + ", moTa=" + moTa + ", heSoGhe=" + heSoGhe + '}';
    }
    
    
}
