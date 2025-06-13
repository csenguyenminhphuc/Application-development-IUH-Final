/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDate;

/**
 *
 * @author PHAMGIAKHANH
 */
public class CaLamViecNhanVien {
    private NhanVien nhanVien;
    private Ca ca;
    private int tongVeBan;
    private double tongTienBanDuoc;
    private LocalDate ngay;

    public CaLamViecNhanVien() {
    }

    public CaLamViecNhanVien(NhanVien nhanVien, Ca ca, LocalDate ngay) {
        this.nhanVien = nhanVien;
        this.ca = ca;
        this.ngay = ngay;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Ca getCa() {
        return ca;
    }

    public void setCa(Ca ca) {
        this.ca = ca;
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

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    @Override
    public String toString() {
        return "CaLamViecNhanVien{" + "nhanVien=" + nhanVien + ", ca=" + ca + ", tongVeBan=" + tongVeBan + ", tongTienBanDuoc=" + tongTienBanDuoc + ", ngay=" + ngay + '}';
    }
    
}
