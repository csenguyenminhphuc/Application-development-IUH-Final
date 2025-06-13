/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDate;

/**
 *
 * @author Administrator
 */
public class CaHienTai {
    private String maNhanVien;
    private String tenNhanVien;
    private String vaiTro;
    private String maCa;
    private LocalDate ngay;
    private int tongVeBan;
    private double tongTienBanDuoc;
    private int tongSoVeHienTai;
    private double tongTienHienTai;

    public CaHienTai(String maNhanVien, String tenNhanVien, String vaiTro, String maCa, LocalDate ngay, int tongVeBan, double tongTienBanDuoc, int tongSoVeHienTai, double tongTienHienTai) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.vaiTro = vaiTro;
        this.maCa = maCa;
        this.ngay = ngay;
        this.tongVeBan = tongVeBan;
        this.tongTienBanDuoc = tongTienBanDuoc;
        this.tongSoVeHienTai = tongSoVeHienTai;
        this.tongTienHienTai = tongTienHienTai;
    }

    public CaHienTai() {
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public int getTongVeBan() {
        return tongVeBan;
    }

    public void setTongVeBan(int tongVeBan) {
        this.tongVeBan = tongVeBan;
    }

    public double getTongTienBanDuoc() {
        return tongTienBanDuoc;
    }

    public void setTongTienBanDuoc(double tongTienBanDuoc) {
        this.tongTienBanDuoc = tongTienBanDuoc;
    }

    public int getTongSoVeHienTai() {
        return tongSoVeHienTai;
    }

    public void setTongSoVeHienTai(int tongSoVeHienTai) {
        this.tongSoVeHienTai = tongSoVeHienTai;
    }

    public double getTongTienHienTai() {
        return tongTienHienTai;
    }

    public void setTongTienHienTai(double tongTienHienTai) {
        this.tongTienHienTai = tongTienHienTai;
    }

    @Override
    public String toString() {
        return "CaHienTai{" + "maNhanVien=" + maNhanVien + ", tenNhanVien=" + tenNhanVien + ", vaiTro=" + vaiTro + ", maCa=" + maCa + ", ngay=" + ngay + ", tongVeBan=" + tongVeBan + ", tongTienBanDuoc=" + tongTienBanDuoc + ", tongSoVeHienTai=" + tongSoVeHienTai + ", tongTienHienTai=" + tongTienHienTai + '}';
    }

    
}
