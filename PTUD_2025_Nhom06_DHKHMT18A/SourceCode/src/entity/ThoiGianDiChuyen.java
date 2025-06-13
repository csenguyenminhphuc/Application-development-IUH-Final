/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.Objects;

/**
 *
 * @author PHAMGIAKHANH
 */
public class ThoiGianDiChuyen {
    private String maThoiGianDiChuyen;
    private Ga gaDi;
    private Ga gaDen;
    private int thoiGianDiChuyen;
    private double soKmDiChuyen;
    private double soTienMotKm;

    public ThoiGianDiChuyen() {
    }

    public ThoiGianDiChuyen(Ga gaDi, Ga gaDen, int thoiGianDiChuyen, double soKmDiChuyen, double soTienMotKm) {
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.thoiGianDiChuyen = thoiGianDiChuyen;
        this.soKmDiChuyen = soKmDiChuyen;
        this.soTienMotKm = soTienMotKm;
    }
    
    

    public String getMaThoiGianDiChuyen() {
        return maThoiGianDiChuyen;
    }

    public void setMaThoiGianDiChuyen(String maThoiGianDiChuyen) {
        this.maThoiGianDiChuyen = maThoiGianDiChuyen;
    }

    public Ga getGaDi() {
        return gaDi;
    }

    public void setGaDi(Ga gaDi) {
        this.gaDi = gaDi;
    }

    public Ga getGaDen() {
        return gaDen;
    }

    public void setGaDen(Ga gaDen) {
        this.gaDen = gaDen;
    }

    public int getThoiGianDiChuyen() {
        return thoiGianDiChuyen;
    }

    public void setThoiGianDiChuyen(int thoiGianDiChuyen) {
        this.thoiGianDiChuyen = thoiGianDiChuyen;
    }

    public double getSoKmDiChuyen() {
        return soKmDiChuyen;
    }

    public void setSoKmDiChuyen(double soKmDiChuyen) {
        this.soKmDiChuyen = soKmDiChuyen;
    }

    public double getSoTienMotKm() {
        return soTienMotKm;
    }

    public void setSoTienMotKm(double soTienMotKm) {
        this.soTienMotKm = soTienMotKm;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.maThoiGianDiChuyen);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ThoiGianDiChuyen other = (ThoiGianDiChuyen) obj;
        return Objects.equals(this.maThoiGianDiChuyen, other.maThoiGianDiChuyen);
    }

    @Override
    public String toString() {
        return "ThoiGianDiChuyen{" + "maThoiGianDiChuyen=" + maThoiGianDiChuyen + ", gaDi=" + gaDi + ", gaDen=" + gaDen + ", thoiGianDiChuyen=" + thoiGianDiChuyen + ", soKmDiChuyen=" + soKmDiChuyen + ", soTienMotKm=" + soTienMotKm + '}';
    }
    
    
}
