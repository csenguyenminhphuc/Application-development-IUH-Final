/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author PHAMGIAKHANH
 */
public class KhuyenMai {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private double heSoKhuyenMai;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayketThuc;
    private double tongTienToiThieu;
    private double tienKhuyenMaiToiDa;
    private String trangThai;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, double heSoKhuyenMai, LocalDateTime ngayBatDau, LocalDateTime ngayketThuc, double tongTienToiThieu, double tienKhuyenMaiToiDa) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.heSoKhuyenMai = heSoKhuyenMai;
        this.ngayBatDau = ngayBatDau;
        this.ngayketThuc = ngayketThuc;
        this.tongTienToiThieu = tongTienToiThieu;
        this.tienKhuyenMaiToiDa = tienKhuyenMaiToiDa;
    }

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, double heSoKhuyenMai, LocalDateTime ngayBatDau, LocalDateTime ngayketThuc, double tongTienToiThieu, double tienKhuyenMaiToiDa, String trangThai) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.heSoKhuyenMai = heSoKhuyenMai;
        this.ngayBatDau = ngayBatDau;
        this.ngayketThuc = ngayketThuc;
        this.tongTienToiThieu = tongTienToiThieu;
        this.tienKhuyenMaiToiDa = tienKhuyenMaiToiDa;
        this.trangThai = trangThai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    
    

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getTenKhuyenMai() {
        return tenKhuyenMai;
    }

    public void setTenKhuyenMai(String tenKhuyenMai) {
        this.tenKhuyenMai = tenKhuyenMai;
    }

    public double getHeSoKhuyenMai() {
        return heSoKhuyenMai;
    }

    public void setHeSoKhuyenMai(double heSoKhuyenMai) {
        this.heSoKhuyenMai = heSoKhuyenMai;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDateTime ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDateTime getNgayketThuc() {
        return ngayketThuc;
    }

    public void setNgayketThuc(LocalDateTime ngayketThuc) {
        this.ngayketThuc = ngayketThuc;
    }

    public double getTongTienToiThieu() {
        return tongTienToiThieu;
    }

    public void setTongTienToiThieu(double tongTienToiThieu) {
        this.tongTienToiThieu = tongTienToiThieu;
    }

    public double getTienKhuyenMaiToiDa() {
        return tienKhuyenMaiToiDa;
    }

    public void setTienKhuyenMaiToiDa(double tienKhuyenMaiToiDa) {
        this.tienKhuyenMaiToiDa = tienKhuyenMaiToiDa;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.maKhuyenMai);
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
        final KhuyenMai other = (KhuyenMai) obj;
        return Objects.equals(this.maKhuyenMai, other.maKhuyenMai);
    }

    @Override
    public String toString() {
        return "KhuyenMai{" + "maKhuyenMai=" + maKhuyenMai + ", tenKhuyenMai=" + tenKhuyenMai + ", heSoKhuyenMai=" + heSoKhuyenMai + ", ngayBatDau=" + ngayBatDau + ", ngayketThuc=" + ngayketThuc + ", tongTienToiThieu=" + tongTienToiThieu + ", tienKhuyenMaiToiDa=" + tienKhuyenMaiToiDa + ", trangThai=" + trangThai + '}';
    }


    
    
    
}
