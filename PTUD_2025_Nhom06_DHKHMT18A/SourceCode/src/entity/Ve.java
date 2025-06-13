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
public class Ve {
    private String maVe;
    private LoaiVe loaiVe;
    private ChuyenDi chuyenDi;
    private HanhKhach hanhKhach;
    private Ghe ghe;
    private HoaDon hoaDon;
    private String trangThai;
    private double giaVe;

    public Ve() {
    }

    public Ve(LoaiVe loaiVe, ChuyenDi chuyenDi, HanhKhach hanhKhach, Ghe ghe, HoaDon hoaDon, String trangThai) {
        this.loaiVe = loaiVe;
        this.chuyenDi = chuyenDi;
        this.hanhKhach = hanhKhach;
        this.ghe = ghe;
        this.hoaDon = hoaDon;
        this.trangThai = trangThai;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public LoaiVe getLoaiVe() {
        return loaiVe;
    }

    public void setLoaiVe(LoaiVe loaiVe) {
        this.loaiVe = loaiVe;
    }

    public ChuyenDi getChuyenDi() {
        return chuyenDi;
    }

    public void setChuyenDi(ChuyenDi chuyenDi) {
        this.chuyenDi = chuyenDi;
    }

    public HanhKhach getHanhKhach() {
        return hanhKhach;
    }

    public void setHanhKhach(HanhKhach hanhKhach) {
        this.hanhKhach = hanhKhach;
    }

    public Ghe getGhe() {
        return ghe;
    }

    public void setGhe(Ghe ghe) {
        this.ghe = ghe;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public double getGiaVe() {
        return giaVe;
    }

    public void setGiaVe(double giaVe) {
        this.giaVe = giaVe;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.maVe);
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
        final Ve other = (Ve) obj;
        return Objects.equals(this.maVe, other.maVe);
    }

    @Override
    public String toString() {
        return "Ve{" + "maVe=" + maVe + ", loaiVe=" + loaiVe + ", chuyenDi=" + chuyenDi + ", hanhKhach=" + hanhKhach + ", ghe=" + ghe + ", hoaDon=" + hoaDon + ", trangThai=" + trangThai + ", giaVe=" + giaVe + '}';
    }
    
    
    
}
