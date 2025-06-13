/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author PHAMGIAKHANH
 */
public class KhuyenMaiResult {
    private KhuyenMai khuyenMai;
    private double tienGiam;

    public KhuyenMaiResult() {
    }

    public KhuyenMaiResult(KhuyenMai khuyenMai, double tienGiam) {
        this.khuyenMai = khuyenMai;
        this.tienGiam = tienGiam;
    }
    
    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public double getTienGiam() {
        return tienGiam;
    }

    public void setTienGiam(double tienGiam) {
        this.tienGiam = tienGiam;
    }
    
    
}
