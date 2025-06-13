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
public class SoVeBanDuoc {
    private String maNV;
    private int soVe;
    private Date ngay;

    public SoVeBanDuoc() {
    }

    public SoVeBanDuoc(String maNV, int soVe, Date ngay) {
        this.maNV = maNV;
        this.soVe = soVe;
        this.ngay = ngay;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public int getSoVe() {
        return soVe;
    }

    public void setSoVe(int soVe) {
        this.soVe = soVe;
    }

    public Date getNgay() {
        return ngay;
    }

    public void setNgay(Date ngay) {
        this.ngay = ngay;
    }

    @Override
    public String toString() {
        return "SoVeBanDuoc{" + "maNV=" + maNV + ", soVe=" + soVe + ", ngay=" + ngay + '}';
    }

}
