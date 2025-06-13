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
public class SoVeTrungBinhMoiGio {
    private String maNV;
    private int soVe;
    private float soGio;
    private Date ngay;
    private float soVeTrungBinh;

    public SoVeTrungBinhMoiGio(String maNV, int soVe, float soGio, Date ngay) {
        this.maNV = maNV;
        this.soVe = soVe;
        this.soGio = soGio;
        this.ngay = ngay;
        setSoVeTrungBinh();
    }

    public SoVeTrungBinhMoiGio() {
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

    public float getSoGio() {
        return soGio;
    }

    public void setSoGio(float soGio) {
        this.soGio = soGio;
    }

    public Date getNgay() {
        return ngay;
    }

    public void setNgay(Date ngay) {
        this.ngay = ngay;
    }

    public float getSoVeTrungBinh() {
        return soVeTrungBinh;
    }

    public void setSoVeTrungBinh() {
        double result = (double) soVe / soGio;
        this.soVeTrungBinh = (int)(result + 0.5);
    }

    @Override
    public String toString() {
        return "SoVeTrungBinhMoiGio{" + "maNV=" + maNV + ", soVe=" + soVe + ", soGio=" + soGio + ", ngay=" + ngay + ", soVeTrungBinh=" + soVeTrungBinh + '}';
    }

    
    
}
