/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Administrator
 */
public class TyLeNhanVienTheoNhomNangSuat {
    private String tenNhomNangSuat;
    private int soNhanVien;

    public TyLeNhanVienTheoNhomNangSuat(String tenNhomNangSuat, int soNhanVien) {
        this.tenNhomNangSuat = tenNhomNangSuat;
        this.soNhanVien = soNhanVien;
    }

    public TyLeNhanVienTheoNhomNangSuat() {
    }

    public String getTenNhomNangSuat() {
        return tenNhomNangSuat;
    }

    public void setTenNhomNangSuat(String tenNhomNangSuat) {
        this.tenNhomNangSuat = tenNhomNangSuat;
    }

    public int getSoNhanVien() {
        return soNhanVien;
    }

    public void setSoNhanVien(int soNhanVien) {
        this.soNhanVien = soNhanVien;
    }

    @Override
    public String toString() {
        return "TyLeNhanVienTheoNhomNangSuat{" + "tenNhomNangSuat=" + tenNhomNangSuat + ", soNhanVien=" + soNhanVien + '}';
    }

    
    
    
}
