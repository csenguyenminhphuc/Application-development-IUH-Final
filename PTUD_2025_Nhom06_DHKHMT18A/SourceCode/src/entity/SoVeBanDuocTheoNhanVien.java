/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Administrator
 */
public class SoVeBanDuocTheoNhanVien {
    private String tenNhanVien;
    private int soVe;

    public SoVeBanDuocTheoNhanVien(String tenNhanVien, int soVe) {
        this.tenNhanVien = tenNhanVien;
        this.soVe = soVe;
    }

    public SoVeBanDuocTheoNhanVien() {
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public int getSoVe() {
        return soVe;
    }

    public void setSoVe(int soVe) {
        this.soVe = soVe;
    }

    @Override
    public String toString() {
        return "SoVeBanDuocTheoNhanVien{" + "tenNhanVien=" + tenNhanVien + ", soVe=" + soVe + '}';
    }
    
}
