/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Administrator
 */
public class TopChuyenDi {
    private String maGaDi;
    private String maGaDen;
    private int soVe;

    public TopChuyenDi(String maGaDi, String maGaDen, int soVe) {
        this.maGaDi = maGaDi;
        this.maGaDen = maGaDen;
        this.soVe = soVe;
    }

    public TopChuyenDi() {
    }

    public String getMaGaDi() {
        return maGaDi;
    }

    public void setMaGaDi(String maGaDi) {
        this.maGaDi = maGaDi;
    }

    public String getMaGaDen() {
        return maGaDen;
    }

    public void setMaGaDen(String maGaDen) {
        this.maGaDen = maGaDen;
    }

    public int getSoVe() {
        return soVe;
    }

    public void setSoVe(int soVe) {
        this.soVe = soVe;
    }

    @Override
    public String toString() {
        return "TopChuyenDi{" + "maGaDi=" + maGaDi + ", maGaDen=" + maGaDen + ", soVe=" + soVe + '}';
    }
    
    
}
