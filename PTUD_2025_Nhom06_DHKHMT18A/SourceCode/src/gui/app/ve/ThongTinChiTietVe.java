/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.ve;

import dao.Ghe_DAO;
import dao.HanhKhach_DAO;
import dao.LoaiVe_DAO;
import entity.ChuyenDi;
import entity.Ghe;
import entity.HanhKhach;
import entity.LoaiVe;
import entity.Ve;
import guiCustom.button;
import java.awt.Color;
import java.time.Year;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author PHAMGIAKHANH
 */
public class ThongTinChiTietVe extends javax.swing.JPanel {
    LoaiVe_DAO loaive_dao = new LoaiVe_DAO();
    Ghe_DAO ghe_dao = new Ghe_DAO();
    ArrayList<LoaiVe> listLoaiVe;
    private ChuyenDi chuyenDi;
    private char soToaTau;
    private String soKhoangTau;
    private String viTri;
    private double giaVe;
    private double giaTheoLoaive;
    /**
     * Creates new form ThongTinChiTietVe
     */
    public ThongTinChiTietVe(ChuyenDi chuyenDi,char soToaTau,String soKhoangTau,String viTri, double giaVe) {
        initComponents();
        
        this.chuyenDi = chuyenDi;
        this.soToaTau  = soToaTau;
        this.soKhoangTau = soKhoangTau;
        this.viTri = viTri;
        this.giaVe = giaVe;
        
        listLoaiVe = loaive_dao.layTatCaLoaiVe();
        jYearChooserNS.setYear(1990);
        System.out.println("gui.app.ve.ThongTinChiTietVe.<init>()"+listLoaiVe.size());
        capNhatLoaiVe();
        jComboBoxLoaiVe.setSelectedIndex(0);
        capNhatViTriVe();
        giaTheoLoaive = giaVe * listLoaiVe.get(0).getHeSoLoaiVe();
        capNhatGiaVe(giaTheoLoaive);
        
    }

    public button getButtonDienNhanh() {
        return buttonDienNhanh;
    }

    public void setButtonDienNhanh(button buttonDienNhanh) {
        this.buttonDienNhanh = buttonDienNhanh;
    }
    
    public char getSoToaTau() {
        return soToaTau;
    }

    public String getSoKhoangTau() {
        return soKhoangTau;
    }
    public void dienThongTinVe(String hoTen, String cccd,int namsinh){
        textFieldHoTen.setText(hoTen);
        textFieldCCCD.setText(cccd);
        jYearChooserNS.setYear(namsinh);
    }
    public void lockButtonDienNhanh(){
        buttonDienNhanh.setEnabled(false);
        buttonDienNhanh.setBackground(new Color(153,153,153));
    }
    public void unlockButtonDienNhanh(){
        buttonDienNhanh.setEnabled(true);
        buttonDienNhanh.setBackground(new Color(87,174,132));
    }
    public void xoaThongTin(){
        textFieldHoTen.setText("");
        textFieldCCCD.setText("");
        jYearChooserNS.setYear(1990);
    }
    public HanhKhach taoHanhKhach(){
        HanhKhach hk =new HanhKhach();
        hk.setTenHanhKhach(textFieldHoTen.getText().trim());
        hk.setCccd(textFieldCCCD.getText().trim());
        hk.setNamSinh(jYearChooserNS.getYear());
        return hk;
    }
    public Ve taoVeTheoThongTin(){
        Ve ve = new Ve();
        ve.setChuyenDi(chuyenDi);
        System.out.println("ma chuyen di: "+ ve.getChuyenDi().getMaChuyenDi());
        System.out.println("Select loai ve: "+jComboBoxLoaiVe.getSelectedIndex());
        ve.setLoaiVe(listLoaiVe.get(jComboBoxLoaiVe.getSelectedIndex()));
        ve.setHanhKhach(taoHanhKhach());
        String matau = chuyenDi.getTau().getMaTau();       // ví dụ: "T-005"
        String tau = matau.substring("TAU-".length());  
        int toaSo = Character.getNumericValue(soToaTau); // '6' -> 6
        int khoangSo = Integer.parseInt(soKhoangTau);    // "3" -> 3
        int viTriSo = Integer.parseInt(viTri);           // "11" -> 11

        String toaFormatted = String.format("%02d", toaSo);         // 6 -> "06"
        String khoangFormatted = String.format("%02d", khoangSo);   // 3 -> "03"
        String viTriFormatted = String.format("%04d", viTriSo);     // 11 -> "0011"

        // Tạo mã ghế chuẩn
        String maGhe = "G-" + tau + "-" + toaFormatted + "-" + khoangFormatted + "-" + viTriFormatted;
        System.out.println("Mã ghế được tạo: " + maGhe);
        Ghe ghe = ghe_dao.timGheTheoMa(maGhe);
        System.out.println("Mã ghế tim thay: " + ghe.toString());
        ve.setGhe(ghe);
        ve.setTrangThai("Đã đặt");
        System.out.println(giaVe);
        ve.setGiaVe(Math.round(giaTheoLoaive));
        return ve;
    }
    public boolean kiemTraThongTinVe(){
        String hoten = textFieldHoTen.getText().trim();
        String cccd = textFieldCCCD.getText().trim();
        int namsinh = jYearChooserNS.getYear();
        int namHienTai = Year.now().getValue();

        if (hoten.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập họ tên cho vị trí, Toa: "+ soToaTau+ " Khoang: "+ soKhoangTau, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!cccd.matches("\\d{12}")) {
            JOptionPane.showMessageDialog(null, "CCCD phải gồm đúng 12 chữ số cho vị trí, Toa: "+ soToaTau+ " Khoang: "+ soKhoangTau, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (namsinh > namHienTai || namsinh < 1900) {
            JOptionPane.showMessageDialog(null, "Năm sinh không hợp lệ cho vị trí, Toa: "+ soToaTau+ " Khoang: "+ soKhoangTau, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    public void capNhatViTriVe(){
        viTriVe.setText("Toa số: "+soToaTau +", Khoang: "+ soKhoangTau +", Ghế: " + viTri);
    }
    private void capNhatGiaVe(double gia){
        double rounded = Math.round(gia);
        String out = String.format("%.1f", rounded);
        textFieldTienVe.setText(out);
    }
    private void capNhatLoaiVe(){
        jComboBoxLoaiVe.removeAllItems();
        for(LoaiVe lv : listLoaiVe){
            jComboBoxLoaiVe.addItem(lv.getTenLoaiVe());
        }
        jComboBoxLoaiVe.setSelectedIndex(0);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        viTriVe = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        textFieldHoTen = new guiCustom.TextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        textFieldCCCD = new guiCustom.TextField();
        jLabel8 = new javax.swing.JLabel();
        jComboBoxLoaiVe = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        textFieldTienVe = new guiCustom.TextField();
        jLabel10 = new javax.swing.JLabel();
        jYearChooserNS = new com.toedter.calendar.JYearChooser();
        buttonDienNhanh = new guiCustom.button();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        setPreferredSize(new java.awt.Dimension(600, 200));
        setRequestFocusEnabled(false);

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 51));
        jLabel1.setText("Chi Tiết Vé:");

        viTriVe.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        viTriVe.setForeground(new java.awt.Color(255, 51, 51));
        viTriVe.setText("Toa Số 1 - Ghế 8");

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 51));
        jLabel3.setText("______________");

        textFieldHoTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldHoTenActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(65, 165, 238));
        jLabel6.setText("Họ Tên");

        jLabel7.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(65, 165, 238));
        jLabel7.setText("CCCD");

        textFieldCCCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldCCCDActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(65, 165, 238));
        jLabel8.setText("Loại Vé");

        jComboBoxLoaiVe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxLoaiVe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLoaiVeActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 51, 51));
        jLabel9.setText("Tiền Vé");

        textFieldTienVe.setForeground(new java.awt.Color(255, 51, 51));
        textFieldTienVe.setText("650.00 đ");
        textFieldTienVe.setDisabledTextColor(new java.awt.Color(255, 51, 51));
        textFieldTienVe.setEnabled(false);
        textFieldTienVe.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        textFieldTienVe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldTienVeActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(65, 165, 238));
        jLabel10.setText("Năm Sinh");

        buttonDienNhanh.setBackground(new java.awt.Color(87, 174, 132));
        buttonDienNhanh.setForeground(new java.awt.Color(255, 255, 255));
        buttonDienNhanh.setText("Điền nhanh");
        buttonDienNhanh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viTriVe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(110, 110, 110))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textFieldTienVe, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(textFieldHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(textFieldCCCD, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxLoaiVe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jYearChooserNS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(66, 66, 66))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonDienNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(viTriVe)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldCCCD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldTienVe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonDienNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jYearChooserNS, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxLoaiVe, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(7, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldHoTenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldHoTenActionPerformed
        // TODO add your handling code here:
        textFieldHoTen.requestFocus();
        
    }//GEN-LAST:event_textFieldHoTenActionPerformed

    private void textFieldCCCDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldCCCDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldCCCDActionPerformed

    private void textFieldTienVeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldTienVeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldTienVeActionPerformed

    private void jComboBoxLoaiVeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxLoaiVeActionPerformed
        // TODO add your handling code here:
        System.out.println("gui.app.ve.ThongTinChiTietVe.jComboBoxLoaiVeActionPerformed()"+jComboBoxLoaiVe.getSelectedIndex());
        int index = jComboBoxLoaiVe.getSelectedIndex();
        if(index >=0 ){
            switch (index) {
            case 0 -> giaTheoLoaive = giaVe * listLoaiVe.get(0).getHeSoLoaiVe();
            case 1 -> giaTheoLoaive = giaVe * listLoaiVe.get(1).getHeSoLoaiVe();
            case 2 -> giaTheoLoaive = giaVe * listLoaiVe.get(2).getHeSoLoaiVe();
            default -> System.out.println("Chưa chọn loại vé hợp lệ.");
                
            }
            capNhatGiaVe(giaTheoLoaive);
        }
        
        
    }//GEN-LAST:event_jComboBoxLoaiVeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private guiCustom.button buttonDienNhanh;
    private javax.swing.JComboBox<String> jComboBoxLoaiVe;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private com.toedter.calendar.JYearChooser jYearChooserNS;
    private guiCustom.TextField textFieldCCCD;
    private guiCustom.TextField textFieldHoTen;
    private guiCustom.TextField textFieldTienVe;
    private javax.swing.JLabel viTriVe;
    // End of variables declaration//GEN-END:variables
}
