/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDate;

/**
 *
 * @author Admin Poi
 */
public class NhanVien {
    private String maNV;
    private String tenNV;
    private boolean gioiTinh; // True: nữ, False: nam
    private LocalDate ngaySinh;
    private String email;
    private String soDienThoai;
    private String cccd;
    private LocalDate ngayBatDauLamViec;
    private String vaiTro;
    private String trangThai;

    // Constructor không tham số
    public NhanVien() {
    }

    // Constructor có đầy đủ tham số
    public NhanVien(String tenNV, boolean gioiTinh, LocalDate ngaySinh, String email, String soDienThoai,
                    String cccd, LocalDate ngayBatDauLamViec, String vaiTro, String trangThai) {
        this.tenNV = tenNV;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.cccd = cccd;
        this.ngayBatDauLamViec = ngayBatDauLamViec;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }

    public NhanVien(String maNV, String tenNV, boolean gioiTinh, LocalDate ngaySinh, String email, String soDienThoai, String cccd, LocalDate ngayBatDauLamViec, String vaiTro, String trangThai) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.cccd = cccd;
        this.ngayBatDauLamViec = ngayBatDauLamViec;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
    }
    
    

    // Getters và Setters
    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public boolean getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public LocalDate getNgayBatDauLamViec() {
        return ngayBatDauLamViec;
    }

    public void setNgayBatDauLamViec(LocalDate ngayBatDauLamViec) {
        this.ngayBatDauLamViec = ngayBatDauLamViec;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNV='" + maNV + '\'' +
                ", tenNV='" + tenNV + '\'' +
                ", gioiTinh=" + gioiTinh +
                ", ngaySinh=" + ngaySinh +
                ", email='" + email + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", cccd='" + cccd + '\'' +
                ", ngayBatDauLamViec=" + ngayBatDauLamViec +
                ", vaiTro='" + vaiTro + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
