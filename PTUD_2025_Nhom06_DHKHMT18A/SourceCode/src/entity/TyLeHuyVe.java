/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Administrator
 */
public class TyLeHuyVe {
    private String trangThai;
    private int soVe;

    public TyLeHuyVe(String trangThai, int soVe) {
        this.trangThai = trangThai;
        this.soVe = soVe;
    }

    public TyLeHuyVe() {
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getSoVe() {
        return soVe;
    }

    public void setSoVe(int soVe) {
        this.soVe = soVe;
    }

    @Override
    public String toString() {
        return "TyLeHuyVe{" + "trangThai=" + trangThai + ", soVe=" + soVe + '}';
    }
    
    
}
