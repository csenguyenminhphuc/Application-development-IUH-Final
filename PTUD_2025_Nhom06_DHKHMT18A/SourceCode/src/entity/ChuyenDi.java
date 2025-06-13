/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author PHAMGIAKHANH
 */
public class ChuyenDi {
    private String maChuyenDi;
    private ThoiGianDiChuyen thoiGianDiChuyen;
    private LocalDateTime thoiGianKhoiHanh;
    private LocalDateTime thoiGianDenDuTinh;
    private Tau tau;
    private int soGheDaDat;
    private int soGheConTrong;

    public ChuyenDi() {
    }

    public ChuyenDi(ThoiGianDiChuyen thoiGianDiChuyen, LocalDateTime thoiGianKhoiHanh, LocalDateTime thoiGianDenDuTinh, Tau tau) {
        this.thoiGianDiChuyen = thoiGianDiChuyen;
        this.thoiGianKhoiHanh = thoiGianKhoiHanh;
        this.thoiGianDenDuTinh = thoiGianDenDuTinh;
        this.tau = tau;
    }

    public String getMaChuyenDi() {
        return maChuyenDi;
    }

    public void setMaChuyenDi(String maChuyenDi) {
        this.maChuyenDi = maChuyenDi;
    }

    public ThoiGianDiChuyen getThoiGianDiChuyen() {
        return thoiGianDiChuyen;
    }

    public void setThoiGianDiChuyen(ThoiGianDiChuyen thoiGianDiChuyen) {
        this.thoiGianDiChuyen = thoiGianDiChuyen;
    }

    public LocalDateTime getThoiGianKhoiHanh() {
        return thoiGianKhoiHanh;
    }

    public void setThoiGianKhoiHanh(LocalDateTime thoiGianKhoiHanh) {
        this.thoiGianKhoiHanh = thoiGianKhoiHanh;
    }

    public LocalDateTime getThoiGianDenDuTinh() {
        return thoiGianDenDuTinh;
    }

    public void setThoiGianDenDuTinh(LocalDateTime thoiGianDenDuTinh) {
        this.thoiGianDenDuTinh = thoiGianDenDuTinh;
    }

    public Tau getTau() {
        return tau;
    }

    public void setTau(Tau tau) {
        this.tau = tau;
    }

    public int getSoGheDaDat() {
        return soGheDaDat;
    }

    public void setSoGheDaDat(int soGheDaDat) {
        this.soGheDaDat = soGheDaDat;
    }

    public int getSoGheConTrong() {
        return soGheConTrong;
    }

    public void setSoGheConTrong(int soGheConTrong) {
        this.soGheConTrong = soGheConTrong;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.maChuyenDi);
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
        final ChuyenDi other = (ChuyenDi) obj;
        return Objects.equals(this.maChuyenDi, other.maChuyenDi);
    }

    @Override
    public String toString() {
        return "ChuyenDi{" + "maChuyenDi=" + maChuyenDi + ", thoiGianDiChuyen=" + thoiGianDiChuyen + ", thoiGianKhoiHanh=" + thoiGianKhoiHanh + ", thoiGianDenDuTinh=" + thoiGianDenDuTinh + ", tau=" + tau + ", soGheDaDat=" + soGheDaDat + ", soGheConTrong=" + soGheConTrong + '}';
    }
    
    
}
