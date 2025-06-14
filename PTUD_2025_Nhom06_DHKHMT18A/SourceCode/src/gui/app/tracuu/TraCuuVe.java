/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.tracuu;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import connectdb.ConnectDB;
import dao.Ve_DAO;
import entity.NhanVien;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author HAI TAM
 */
public class TraCuuVe extends javax.swing.JPanel {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,### VNĐ");
    private final Ve_DAO veDAO;
    /**
     * Creates new form TraCuuVe
     */
    public TraCuuVe(NhanVien nv) {
        ConnectDB.getInstance().connect();
        veDAO = new Ve_DAO();
        initComponents();
        // header1.jLabel14.setText("Tra cứu vé");
        // header1.jButton4.setText(nv.getTenNV());
        clearFields();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtontraCuu = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldmaVe = new javax.swing.JTextField();
        jLabelinMaVe = new javax.swing.JLabel();
        jLabelchuyenDi = new javax.swing.JLabel();
        jLabeltau = new javax.swing.JLabel();
        jLabelngayDi = new javax.swing.JLabel();
        jLabelngayDen = new javax.swing.JLabel();
        jLabelgiaVe = new javax.swing.JLabel();
        jButtonBrowse = new javax.swing.JButton();
        jLabelkhoangTau = new javax.swing.JLabel();
        jLabeltoaTau = new javax.swing.JLabel();
        jLabelanhQR = new javax.swing.JLabel();
        jLabelCCCD = new javax.swing.JLabel();
        jLabelinhoTen = new javax.swing.JLabel();
        jLabelloaiVe = new javax.swing.JLabel();
        jLabelloaiGhe = new javax.swing.JLabel();
        jLabelghe = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButtontraCuu.setBackground(new java.awt.Color(65, 165, 238));
        jButtontraCuu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtontraCuu.setForeground(new java.awt.Color(255, 255, 255));
        jButtontraCuu.setText("Tra Cứu");
        jButtontraCuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtontraCuuActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(13, 98, 255));
        jLabel1.setText("Mã Vé :");

        jTextFieldmaVe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldmaVeActionPerformed(evt);
            }
        });

        jLabelinMaVe.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelinMaVe.setForeground(new java.awt.Color(13, 98, 255));
        jLabelinMaVe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelinMaVe.setText("Mã vé :");
        jLabelinMaVe.setMaximumSize(new java.awt.Dimension(50, 20));
        jLabelinMaVe.setMinimumSize(new java.awt.Dimension(50, 20));
        jLabelinMaVe.setPreferredSize(new java.awt.Dimension(50, 20));

        jLabelchuyenDi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelchuyenDi.setForeground(new java.awt.Color(13, 98, 255));
        jLabelchuyenDi.setText("Chuyến đi:");

        jLabeltau.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabeltau.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltau.setText("Tàu");

        jLabelngayDi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelngayDi.setForeground(new java.awt.Color(13, 98, 255));
        jLabelngayDi.setText("Ngày đi :");

        jLabelngayDen.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelngayDen.setForeground(new java.awt.Color(13, 98, 255));
        jLabelngayDen.setText("Ngày đến :");

        jLabelgiaVe.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelgiaVe.setForeground(new java.awt.Color(13, 98, 255));
        jLabelgiaVe.setText("Giá vé :");

        jButtonBrowse.setBackground(new java.awt.Color(65, 165, 238));
        jButtonBrowse.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonBrowse.setForeground(new java.awt.Color(255, 255, 255));
        jButtonBrowse.setText("Chọn ảnh");
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jLabelkhoangTau.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelkhoangTau.setForeground(new java.awt.Color(13, 98, 255));
        jLabelkhoangTau.setText("Khoang tàu :");

        jLabeltoaTau.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabeltoaTau.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltoaTau.setText("Toa tàu :");

        jLabelanhQR.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelanhQR.setText("Ảnh QR");
        jLabelanhQR.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabelCCCD.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelCCCD.setForeground(new java.awt.Color(13, 98, 255));
        jLabelCCCD.setText("CCCD :");
        jLabelCCCD.setMaximumSize(new java.awt.Dimension(50, 20));
        jLabelCCCD.setMinimumSize(new java.awt.Dimension(50, 20));
        jLabelCCCD.setPreferredSize(new java.awt.Dimension(50, 20));

        jLabelinhoTen.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelinhoTen.setForeground(new java.awt.Color(13, 98, 255));
        jLabelinhoTen.setText("Họ và tên :");
        jLabelinhoTen.setMaximumSize(new java.awt.Dimension(50, 20));
        jLabelinhoTen.setMinimumSize(new java.awt.Dimension(50, 20));
        jLabelinhoTen.setPreferredSize(new java.awt.Dimension(50, 20));

        jLabelloaiVe.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelloaiVe.setForeground(new java.awt.Color(13, 98, 255));
        jLabelloaiVe.setText("Loại vé :");

        jLabelloaiGhe.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelloaiGhe.setForeground(new java.awt.Color(13, 98, 255));
        jLabelloaiGhe.setText("Loại ghế :");

        jLabelghe.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelghe.setForeground(new java.awt.Color(13, 98, 255));
        jLabelghe.setText("Ghế :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1)
                        .addGap(12, 12, 12)
                        .addComponent(jTextFieldmaVe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBrowse))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtontraCuu)))
                .addGap(6, 6, 6))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelinMaVe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabeltau, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                                    .addComponent(jLabelgiaVe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabelngayDi, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                            .addComponent(jLabelanhQR, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelngayDen, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabeltoaTau, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                                .addComponent(jLabelloaiVe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelinhoTen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelchuyenDi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelghe, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelCCCD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelkhoangTau, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                            .addComponent(jLabelloaiGhe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldmaVe, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowse))
                .addGap(21, 21, 21)
                .addComponent(jButtontraCuu)
                .addGap(50, 50, 50)
                .addComponent(jLabelinMaVe, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelchuyenDi)
                    .addComponent(jLabelkhoangTau))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelinhoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCCCD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelghe, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelloaiGhe))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelngayDen)
                    .addComponent(jLabelngayDi))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabeltoaTau)
                    .addComponent(jLabeltau))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelgiaVe)
                    .addComponent(jLabelloaiVe))
                .addGap(18, 18, 18)
                .addComponent(jLabelanhQR, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(78, 133, 248));
        jPanel6.setPreferredSize(new java.awt.Dimension(400, 300));

        jButton7.setBackground(new java.awt.Color(78, 133, 248));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/user-interface.png"))); // NOI18N
        jButton7.setBorder(null);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Chào nhân viên bán vé");
        jLabel17.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Thống kê Tài Chính");

        jButton8.setBackground(new java.awt.Color(78, 133, 248));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton8.setText("Nguyen Minh Phuc");
        jButton8.setBorder(null);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addGap(57, 57, 57))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    
    private void jButtontraCuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtontraCuuActionPerformed
        // TODO add your handling code here:
        String maVe = jTextFieldmaVe.getText().trim();
        if (maVe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = ConnectDB.getInstance().getConnection();
            CallableStatement stmt = conn.prepareCall("{CALL sp_TraCuuVeTheoMaVe(?)}");
            stmt.setString(1, maVe);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Hiển thị các label thông tin, không hiển thị jLabelanhQR
                    jLabelinMaVe.setVisible(true);
                    jLabelchuyenDi.setVisible(true);
                    jLabeltau.setVisible(true);
                    jLabelngayDi.setVisible(true);
                    jLabelngayDen.setVisible(true);
                    jLabelgiaVe.setVisible(true);
                    jLabelkhoangTau.setVisible(true);
                    jLabeltoaTau.setVisible(true);
                    jLabelCCCD.setVisible(true);
                    jLabelinhoTen.setVisible(true);
                    jLabelloaiVe.setVisible(true);
                    jLabelloaiGhe.setVisible(true);
                    jLabelghe.setVisible(true);
                    jLabelanhQR.setVisible(false); // Ẩn label ảnh QR

                    // Hiển thị thông tin từ database
                    jLabelinMaVe.setText(rs.getString("MaVe") != null ? "Mã vé: " + rs.getString("MaVe") : "Mã vé :N/A");
                    jLabelchuyenDi.setText(
                            "Chuyến đi: " + (rs.getString("GaDi") != null ? rs.getString("GaDi") : "N/A") + " - " +
                            (rs.getString("GaDen") != null ? rs.getString("GaDen") : "N/A"));
                    jLabeltau.setText("Tàu: " + (rs.getString("Tau") != null ? rs.getString("Tau") : "N/A"));
                    jLabelngayDi.setText("Ngày đi: " + (rs.getTimestamp("NgayDi") != null ? DATE_FORMAT.format(rs.getTimestamp("NgayDi")) : "N/A"));
                    jLabelngayDen.setText("Ngày đến: " + (rs.getTimestamp("NgayDen") != null ? DATE_FORMAT.format(rs.getTimestamp("NgayDen")) : "N/A"));
                    jLabelgiaVe.setText("Giá vé: " + (rs.getDouble("GiaVe") != 0 ? CURRENCY_FORMAT.format(rs.getDouble("GiaVe")) : "N/A"));
                    jLabelkhoangTau.setText("Khoang tàu: " + (rs.getString("KhoangTau") != null ? rs.getString("KhoangTau") : "N/A"));
                    jLabeltoaTau.setText("Toa tàu: " + (rs.getString("ToaTau") != null ? rs.getString("ToaTau") : "N/A"));
//                    jLabelCCCD.setText("CCCD: " + (rs.getString("CCCD") != null ? rs.getString("CCCD") : "N/A"));
                    jLabelinhoTen.setText("Họ và tên: " + (rs.getString("HanhKhach") != null ? rs.getString("HanhKhach") : "N/A"));
                    jLabelloaiVe.setText("Loại vé: " + (rs.getString("LoaiVe") != null ? rs.getString("LoaiVe") : "N/A"));
                    jLabelloaiGhe.setText("Loại ghế: " + (rs.getString("LoaiGhe") != null ? rs.getString("LoaiGhe") : "N/A"));
//                    jLabelghe.setText("Ghế: " + (rs.getString("MaGhe") != null ? rs.getString("MaGhe") : "N/A"));
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy vé với mã: " + maVe, "Thông báo", JOptionPane.WARNING_MESSAGE);
                    clearFields();
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu vé: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi khi tra cứu vé: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            clearFields();
        }
    }//GEN-LAST:event_jButtontraCuuActionPerformed

    private void jTextFieldmaVeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldmaVeActionPerformed
        // TODO add your handling code here:
        jButtontraCuuActionPerformed(evt);
    }//GEN-LAST:event_jTextFieldmaVeActionPerformed

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Kiểm tra file tồn tại và đọc được
                if (!selectedFile.exists() || !selectedFile.canRead()) {
                    throw new IllegalArgumentException("Không thể đọc file ảnh: " + selectedFile.getAbsolutePath());
                }

                // Đọc ảnh từ file
                BufferedImage image = ImageIO.read(selectedFile);
                if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                    throw new IllegalArgumentException("Ảnh không hợp lệ hoặc bị lỗi.");
                }

                // Quét mã QR từ ảnh
                BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(new BufferedImageLuminanceSource(image))
                );
                Result qrResult = new MultiFormatReader().decode(bitmap);
                String maVe = qrResult.getText();

                // Hiển thị mã vé vào textfield
                jTextFieldmaVe.setText(maVe);

                // Hiển thị ảnh QR scaled
                int labelWidth = 150;
                int labelHeight = 150;

                Image scaledImage = image.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImage);
                jLabelanhQR.setIcon(icon);
                jLabelanhQR.setText("");
                jLabelanhQR.setVisible(true);

                // Tự động tra cứu mã vé
                jButtontraCuuActionPerformed(evt);

            } catch (Exception e) {
                // Lỗi khi quét mã QR hoặc đọc ảnh
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi đọc mã QR: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);

                jLabelanhQR.setIcon(null);
                jLabelanhQR.setText("Ảnh QR");
                jLabelanhQR.setVisible(false);
            }
        }

    }//GEN-LAST:event_jButtonBrowseActionPerformed
    
    private void clearFields() {
        // Ẩn toàn bộ label hiển thị thông tin vé
        jLabelinMaVe.setVisible(false);
        jLabelchuyenDi.setVisible(false);
        jLabeltau.setVisible(false);
        jLabelngayDi.setVisible(false);
        jLabelngayDen.setVisible(false);
        jLabelgiaVe.setVisible(false);
        jLabelkhoangTau.setVisible(false);
        jLabeltoaTau.setVisible(false);
        jLabelCCCD.setVisible(false);
        jLabelinhoTen.setVisible(false);
        jLabelloaiVe.setVisible(false);
        jLabelloaiGhe.setVisible(false);
        jLabelghe.setVisible(false);
        jLabelanhQR.setVisible(false); // Ẩn luôn ảnh QR

        // Nếu muốn reset nội dung để khi hiện lại là đúng dữ liệu mặc định
        jLabelinMaVe.setText("Mã vé");
        jLabelchuyenDi.setText("Chuyến đi");
        jLabeltau.setText("Tàu");
        jLabelngayDi.setText("Ngày đi");
        jLabelngayDen.setText("Ngày đến");
        jLabelgiaVe.setText("Giá vé");
        jLabelkhoangTau.setText("Khoang tàu");
        jLabeltoaTau.setText("Toa tàu");
        jLabelCCCD.setText("CCCD");
        jLabelinhoTen.setText("Họ và tên");
        jLabelloaiVe.setText("Loại vé");
        jLabelloaiGhe.setText("Loại ghế");
        jLabelghe.setText("Ghế");

//        // Xóa ảnh QR nếu có
//        jLabelanhQR.setIcon(null);
//        jLabelanhQR.setText("Ảnh QR");
    }
    
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed

    }//GEN-LAST:event_jButton8ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton7;
    public javax.swing.JButton jButton8;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtontraCuu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    public javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabelCCCD;
    private javax.swing.JLabel jLabelanhQR;
    private javax.swing.JLabel jLabelchuyenDi;
    private javax.swing.JLabel jLabelghe;
    private javax.swing.JLabel jLabelgiaVe;
    private javax.swing.JLabel jLabelinMaVe;
    private javax.swing.JLabel jLabelinhoTen;
    private javax.swing.JLabel jLabelkhoangTau;
    private javax.swing.JLabel jLabelloaiGhe;
    private javax.swing.JLabel jLabelloaiVe;
    private javax.swing.JLabel jLabelngayDen;
    private javax.swing.JLabel jLabelngayDi;
    private javax.swing.JLabel jLabeltau;
    private javax.swing.JLabel jLabeltoaTau;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField jTextFieldmaVe;
    // End of variables declaration//GEN-END:variables
}
