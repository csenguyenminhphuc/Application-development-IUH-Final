/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.Date;

/**
 *
 * @author Administrator
 */
public class TongTienBanDuoc {
    private String maNV;
    private double tongTien;
    private Date ngay;

    public TongTienBanDuoc(String maNV, double tongTien, Date ngay) {
        this.maNV = maNV;
        this.tongTien = tongTien;
        this.ngay = ngay;
    }

    public TongTienBanDuoc() {
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public Date getNgay() {
        return ngay;
    }

    public void setNgay(Date ngay) {
        this.ngay = ngay;
    }

    @Override
    public String toString() {
        return "TongTienBanDuoc{" + "maNV=" + maNV + ", tongTien=" + tongTien + ", ngay=" + ngay + '}';
    }

}
