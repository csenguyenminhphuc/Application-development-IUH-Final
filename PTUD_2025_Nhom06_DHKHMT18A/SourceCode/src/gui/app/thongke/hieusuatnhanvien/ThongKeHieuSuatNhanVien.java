/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.thongke.hieusuatnhanvien;

import entity.NhanVien;
import java.awt.Font;

/**
 *
 * @author Administrator
 */
public class ThongKeHieuSuatNhanVien extends javax.swing.JPanel {

    /**
     * Creates new form ThongKeTaiChinh2
     */
    public ThongKeHieuSuatNhanVien(NhanVien nv) {
        initComponents();
        header1.jButton4.setText(nv.getTenNV());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        header1 = new component.Header();
        bieuDoTop5LamViec1 = new gui.app.thongke.hieusuatnhanvien.BieuDoTop5LamViec();
        bieuDoTyLeNhanVienTheoNhomNangSuat1 = new gui.app.thongke.hieusuatnhanvien.BieuDoTyLeNhanVienTheoNhomNangSuat();
        bieuDoTop5BanVe2 = new gui.app.thongke.hieusuatnhanvien.BieuDoTop5BanVe();

        jMenu1.setText("jMenu1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(header1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1198, Short.MAX_VALUE)
                    .addComponent(bieuDoTop5LamViec1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(bieuDoTop5BanVe2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bieuDoTyLeNhanVienTheoNhomNangSuat1, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bieuDoTop5LamViec1, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bieuDoTyLeNhanVienTheoNhomNangSuat1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bieuDoTop5BanVe2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gui.app.thongke.hieusuatnhanvien.BieuDoTop5BanVe bieuDoTop5BanVe2;
    private gui.app.thongke.hieusuatnhanvien.BieuDoTop5LamViec bieuDoTop5LamViec1;
    private gui.app.thongke.hieusuatnhanvien.BieuDoTyLeNhanVienTheoNhomNangSuat bieuDoTyLeNhanVienTheoNhomNangSuat1;
    private component.Header header1;
    private javax.swing.JMenu jMenu1;
    // End of variables declaration//GEN-END:variables
}
