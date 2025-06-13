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
public class HanhKhach {
    private String maHanhKhach;
    private String tenHanhKhach;
    private String cccd;
    private int namSinh;

    public HanhKhach() {
    }

    public HanhKhach(String maHanhKhach, String tenHanhKhach, String cccd, int namSinh) {
        this.maHanhKhach = maHanhKhach;
        this.tenHanhKhach = tenHanhKhach;
        this.cccd = cccd;
        this.namSinh = namSinh;
    }

    public int getNamSinh() {
        return namSinh;
    }

    public void setNamSinh(int namSinh) {
        this.namSinh = namSinh;
    }

    public String getMaHanhKhach() {
        return maHanhKhach;
    }

    public void setMaHanhKhach(String maHanhKhach) {
        this.maHanhKhach = maHanhKhach;
    }

    public String getTenHanhKhach() {
        return tenHanhKhach;
    }

    public void setTenHanhKhach(String tenHanhKhach) {
        this.tenHanhKhach = tenHanhKhach;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.maHanhKhach);
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
        final HanhKhach other = (HanhKhach) obj;
        return Objects.equals(this.maHanhKhach, other.maHanhKhach);
    }

    @Override
    public String toString() {
        return "HanhKhach{" + "maHanhKhach=" + maHanhKhach + ", tenHanhKhach=" + tenHanhKhach + ", cccd=" + cccd + ", namSinh=" + namSinh + '}';
    }

    
    
    
}
