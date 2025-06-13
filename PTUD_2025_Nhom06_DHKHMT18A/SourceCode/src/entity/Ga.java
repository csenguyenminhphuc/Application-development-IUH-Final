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
public class Ga {
    private String maGa;
    private String tenGa;
    private String diaChi;
    private String soDienThoai;

    public Ga() {
    }

    public Ga(String tenGa, String diaChi, String soDienThoai) {
        this.tenGa = tenGa;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
    }

    public String getMaGa() {
        return maGa;
    }

    public void setMaGa(String maGa) {
        this.maGa = maGa;
    }

    public String getTenGa() {
        return tenGa;
    }

    public void setTenGa(String tenGa) {
        this.tenGa = tenGa;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.maGa);
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
        final Ga other = (Ga) obj;
        return Objects.equals(this.maGa, other.maGa);
    }

    @Override
    public String toString() {
        return "Ga{" + "maGa=" + maGa + ", tenGa=" + tenGa + ", diaChi=" + diaChi + ", soDienThoai=" + soDienThoai + '}';
    }
    
    
}
