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
public class Tau {
    private String maTau;
    private String tenTau;
    private int soToaTau;

    public Tau() {
    }

    public Tau(String tenTau, int soToaTau) {
        this.tenTau = tenTau;
        this.soToaTau = soToaTau;
    }

    public String getMaTau() {
        return maTau;
    }

    public void setMaTau(String maTau) {
        this.maTau = maTau;
    }

    public String getTenTau() {
        return tenTau;
    }

    public void setTenTau(String tenTau) {
        this.tenTau = tenTau;
    }

    public int getSoToaTau() {
        return soToaTau;
    }

    public void setSoToaTau(int soToaTau) {
        this.soToaTau = soToaTau;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.maTau);
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
        final Tau other = (Tau) obj;
        return Objects.equals(this.maTau, other.maTau);
    }
    
    

    @Override
    public String toString() {
        return "Tau{" + "maTau=" + maTau + ", tenTau=" + tenTau + ", soToaTau=" + soToaTau + '}';
    }
    
    
}
