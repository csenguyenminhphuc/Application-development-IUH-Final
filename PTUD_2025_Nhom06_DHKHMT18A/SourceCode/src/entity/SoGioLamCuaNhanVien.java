/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Administrator
 */
public class SoGioLamCuaNhanVien {
    private String maNhanVien;
    private int soGio;

    public SoGioLamCuaNhanVien(String maNhanVien, int soGio) {
        this.maNhanVien = maNhanVien;
        this.soGio = soGio;
    }

    public SoGioLamCuaNhanVien() {
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public int getSoGio() {
        return soGio;
    }

    public void setSoGio(int soGio) {
        this.soGio = soGio;
    }
    
}
