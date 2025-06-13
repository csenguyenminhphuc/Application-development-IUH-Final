/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Administrator
 */
public class TyLeVeTheoLoaiGhe {
    private String maLoaiGhe;
    private double doanhThu;

    public TyLeVeTheoLoaiGhe(String maLoaiGhe, double doanhThu) {
        this.maLoaiGhe = maLoaiGhe;
        this.doanhThu = doanhThu;
    }

    public TyLeVeTheoLoaiGhe() {
    }

    public String getMaLoaiGhe() {
        return maLoaiGhe;
    }

    public void setMaLoaiGhe(String maLoaiGhe) {
        this.maLoaiGhe = maLoaiGhe;
    }

    public double getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(double doanhThu) {
        this.doanhThu = doanhThu;
    }

    @Override
    public String toString() {
        return "TyLeVeTheoLoaiGhe{" + "maLoaiGhe=" + maLoaiGhe + ", doanhThu=" + doanhThu + '}';
    }

    
    
}
