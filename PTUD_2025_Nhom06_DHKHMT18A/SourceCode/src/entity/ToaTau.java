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
public class ToaTau {
    private String maToaTau;
    private String tenToaTau;
    private int soKhoangTau;
    private Tau tau;

    public ToaTau() {
    }

    public ToaTau(String tenToaTau, int soKhoangTau, Tau tau) {
        this.tenToaTau = tenToaTau;
        this.soKhoangTau = soKhoangTau;
        this.tau = tau;
    }

    public String getMaToaTau() {
        return maToaTau;
    }

    public void setMaToaTau(String maToaTau) {
        this.maToaTau = maToaTau;
    }

    public String getTenToaTau() {
        return tenToaTau;
    }

    public void setTenToaTau(String tenToaTau) {
        this.tenToaTau = tenToaTau;
    }

    public int getSoKhoangTau() {
        return soKhoangTau;
    }

    public void setSoKhoangTau(int soKhoangTau) {
        this.soKhoangTau = soKhoangTau;
    }

    public Tau getTau() {
        return tau;
    }

    public void setTau(Tau tau) {
        this.tau = tau;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.maToaTau);
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
        final ToaTau other = (ToaTau) obj;
        return Objects.equals(this.maToaTau, other.maToaTau);
    }

    @Override
    public String toString() {
        return "ToaTau{" + "maToaTau=" + maToaTau + ", tenToaTau=" + tenToaTau + ", soKhoangTau=" + soKhoangTau + ", tau=" + tau + '}';
    }
    
    
}
