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
public class LoaiVe {
    private String maLoaiVe;
    private String tenLoaiVe;
    private String moTaLoaiVe;
    private double heSoLoaiVe;

    public LoaiVe() {
    }

    public LoaiVe(String tenLoaiVe, double heSoLoaiVe) {
        this.tenLoaiVe = tenLoaiVe;
        this.heSoLoaiVe = heSoLoaiVe;
    }

    public LoaiVe(String tenLoaiVe, String moTaLoaiVe, double heSoLoaiVe) {
        this.tenLoaiVe = tenLoaiVe;
        this.moTaLoaiVe = moTaLoaiVe;
        this.heSoLoaiVe = heSoLoaiVe;
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public String getTenLoaiVe() {
        return tenLoaiVe;
    }

    public void setTenLoaiVe(String tenLoaiVe) {
        this.tenLoaiVe = tenLoaiVe;
    }

    public String getMoTaLoaiVe() {
        return moTaLoaiVe;
    }

    public void setMoTaLoaiVe(String moTaLoaiVe) {
        this.moTaLoaiVe = moTaLoaiVe;
    }

    public double getHeSoLoaiVe() {
        return heSoLoaiVe;
    }

    public void setHeSoLoaiVe(double heSoLoaiVe) {
        this.heSoLoaiVe = heSoLoaiVe;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.maLoaiVe);
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
        final LoaiVe other = (LoaiVe) obj;
        return Objects.equals(this.maLoaiVe, other.maLoaiVe);
    }

    @Override
    public String toString() {
        return "LoaiVe{" + "maLoaiVe=" + maLoaiVe + ", tenLoaiVe=" + tenLoaiVe + ", moTaLoaiVe=" + moTaLoaiVe + ", heSoLoaiVe=" + heSoLoaiVe + '}';
    }
    
    
}
