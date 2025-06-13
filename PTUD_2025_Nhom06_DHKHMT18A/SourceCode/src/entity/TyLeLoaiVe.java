/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Administrator
 */
public class TyLeLoaiVe {
    private String maLoaiVe;
    private int soVe;

    public TyLeLoaiVe(String maLoaiVe, int soVe) {
        this.maLoaiVe = maLoaiVe;
        this.soVe = soVe;
    }

    public TyLeLoaiVe() {
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public int getSoVe() {
        return soVe;
    }

    public void setSoVe(int soVe) {
        this.soVe = soVe;
    }

    @Override
    public String toString() {
        return "TyLeLoaiVe{" + "maLoaiVe=" + maLoaiVe + ", soVe=" + soVe + '}';
    }
    
    
}
