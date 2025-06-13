/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.hoso;

import connectdb.ConnectDB;
import dao.QuanLyGaDao;
import entity.Ga;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
/**
 * Giao diện quản lý ga tàu
 * @author Admin Poi
 */
public class QuanLyGa extends javax.swing.JPanel {
    
    private QuanLyGaDao gaDao;
    private DefaultTableModel modelGa;
    private boolean dangCapNhat = false;
    private boolean dangThem = false;
    private String maGaTam = null; // Biến lưu mã ga tạm khi thêm mới
    
    /**
     * Khởi tạo giao diện quản lý ga
     */
    public QuanLyGa() {
        initComponents();
        header1.jLabel14.setText("Quản Lý Địa Điểm Ga");
        header1.jLabel13.setText("Chào Nhân Viên Quản Lý");
        header1.jButton4.setText("Phạm Gia Khánh");
        gaDao = new QuanLyGaDao();
        khoiTaoBang();
        loadDuLieuVaoBang();
        jTextFieldmaGa.setEditable(false);
        capNhatTrangThaiNut(TrangThai.KHOI_TAO);
        
        // Thêm sự kiện chọn dòng trong bảng
        jTableQuanLyDiaDiemGa.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int dongChon = jTableQuanLyDiaDiemGa.getSelectedRow();
                    if (dongChon != -1) {
                        hienThiThongTinGa(dongChon);
                    }
                }
            }
        });
    }
    
    /**
     * Khởi tạo bảng hiển thị danh sách ga
     */
    private void khoiTaoBang() {
        modelGa = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Mã Ga", "Tên Ga", "Địa Chỉ", "Số Điện Thoại"}
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        jTableQuanLyDiaDiemGa.setModel(modelGa);
    }
    
    /**
     * Load dữ liệu từ database vào bảng
     */
    private void loadDuLieuVaoBang() {
        modelGa.setRowCount(0);
        ArrayList<Ga> dsGa = gaDao.layTatCaGa();
        for (Ga ga : dsGa) {
            Object[] dong = new Object[]{
                ga.getMaGa(),
                ga.getTenGa(),
                ga.getDiaChi(),
                ga.getSoDienThoai()
            };
            modelGa.addRow(dong);
        }
    }
    
    /**
     * Hiển thị thông tin ga từ dòng được chọn trong bảng
     */
    private void hienThiThongTinGa(int dongChon) {
        // Nếu đang thêm mới thì không lấy mã ga từ bảng
        if (!dangThem) {
            String maGa = modelGa.getValueAt(dongChon, 0).toString();
            jTextFieldmaGa.setText(maGa);
        }
        String tenGa = modelGa.getValueAt(dongChon, 1).toString();
        String diaChi = modelGa.getValueAt(dongChon, 2).toString();
        String soDienThoai = modelGa.getValueAt(dongChon, 3).toString();
        
        jTextFieldtenNhanVien.setText(tenGa);
        jTextFielddiaChi.setText(diaChi);
        jTextFieldsoDienThoai.setText(soDienThoai);
    }

    // Enum để quản lý trạng thái của form
    private enum TrangThai {
        KHOI_TAO,
        THEM_MOI,
        CAP_NHAT,
        TIM_KIEM
    }
    
    /**
     * Cập nhật trạng thái các nút theo trạng thái form
     */
    private void capNhatTrangThaiNut(TrangThai trangThai) {
        switch (trangThai) {
            case KHOI_TAO:
                jButtonthemGa.setEnabled(true);
                jButtoncapNhatNhanVien.setEnabled(true);
                jButtonxoaGa.setEnabled(true);
                jButtontimGa.setEnabled(true);
                jButtonluu.setEnabled(false);
                jButtonHuy.setEnabled(false);
                jButtonxoaTrang.setEnabled(true);
                break;
                
            case THEM_MOI:
                jButtonthemGa.setEnabled(false);
                jButtoncapNhatNhanVien.setEnabled(false);
                jButtonxoaGa.setEnabled(false);
                jButtontimGa.setEnabled(false);
                jButtonluu.setEnabled(true);
                jButtonHuy.setEnabled(true);
                jButtonxoaTrang.setEnabled(true);
                break;
                
            case CAP_NHAT:
                jButtonthemGa.setEnabled(false);
                jButtoncapNhatNhanVien.setEnabled(false);
                jButtonxoaGa.setEnabled(false);
                jButtontimGa.setEnabled(false);
                jButtonluu.setEnabled(true);
                jButtonHuy.setEnabled(true);
                jButtonxoaTrang.setEnabled(true);
                break;
                
            case TIM_KIEM:
                jButtonthemGa.setEnabled(false);
                jButtoncapNhatNhanVien.setEnabled(true);
                jButtonxoaGa.setEnabled(true);
                jButtontimGa.setEnabled(false);
                jButtonluu.setEnabled(false);
                jButtonHuy.setEnabled(true);
                jButtonxoaTrang.setEnabled(true);
                break;
        }
    }

    private void jButtonthemGaActionPerformed(java.awt.event.ActionEvent evt) {
        // Tạo mã ga tạm thời theo quy tắc GA-XXX
        maGaTam = gaDao.taoMaGaMoi();
        if (maGaTam == null) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo mã ga!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Hiển thị mã ga tạm lên giao diện và xóa trắng các trường khác
        dangThem = true;
        jTextFieldmaGa.setText(maGaTam);
        jTextFieldtenNhanVien.setText("");
        jTextFielddiaChi.setText("");
        jTextFieldsoDienThoai.setText("");
        jTableQuanLyDiaDiemGa.clearSelection();
        capNhatTrangThaiNut(TrangThai.THEM_MOI);
    }

    /**
     * Xóa trắng các trường trừ mã ga
     */
    private void xoaTrang() {
        if (!dangThem) {
            jTextFieldmaGa.setText("");
            maGaTam = null;
        } else {
            // Khi đang thêm mới, giữ lại mã ga tạm
            jTextFieldmaGa.setText(maGaTam);
        }
        jTextFieldtenNhanVien.setText("");
        jTextFielddiaChi.setText("");
        jTextFieldsoDienThoai.setText("");
        jTableQuanLyDiaDiemGa.clearSelection();
    }

    private void jButtoncapNhatNhanVienActionPerformed(java.awt.event.ActionEvent evt) {
        int dongChon = jTableQuanLyDiaDiemGa.getSelectedRow();
        if (dongChon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ga cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        hienThiThongTinGa(dongChon);
        dangCapNhat = true;
        capNhatTrangThaiNut(TrangThai.CAP_NHAT);
    }

    private void jButtonluuActionPerformed(java.awt.event.ActionEvent evt) {
        if (dangThem) {
            themGaMoi();
        } else if (dangCapNhat) {
            capNhatGa();
        }
        dangThem = false;
        dangCapNhat = false;
        capNhatTrangThaiNut(TrangThai.KHOI_TAO);
        loadDuLieuVaoBang();
    }

    private void jButtonHuyActionPerformed(java.awt.event.ActionEvent evt) {
        xoaTrang();
        dangThem = false;
        maGaTam = null; // Xóa mã ga tạm khi hủy
        capNhatTrangThaiNut(TrangThai.KHOI_TAO);
        loadDuLieuVaoBang();
    }

    private void themGaMoi() {
        // Lấy dữ liệu từ form
        String tenGa = jTextFieldtenNhanVien.getText().trim();
        String diaChi = jTextFielddiaChi.getText().trim();
        String soDienThoai = jTextFieldsoDienThoai.getText().trim();
        
        // Kiểm tra dữ liệu
        StringBuilder loiNhapLieu = new StringBuilder();
        
        if (tenGa.isEmpty()) {
            loiNhapLieu.append("Tên ga không được để trống!\n");
        }
        
        if (diaChi.isEmpty()) {
            loiNhapLieu.append("Địa chỉ không được để trống!\n");
        }
        
        if (soDienThoai.isEmpty()) {
            loiNhapLieu.append("Số điện thoại không được để trống!\n");
        } else if (!soDienThoai.matches("\\d{10}")) {
            loiNhapLieu.append("Số điện thoại phải có đúng 10 chữ số!\n");
        }
        
        // Nếu có lỗi thì hiển thị và dừng lại
        if (loiNhapLieu.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng sửa các lỗi sau:\n" + loiNhapLieu.toString(), 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Tạo đối tượng ga mới với mã ga tạm
        Ga ga = new Ga(tenGa, diaChi, soDienThoai);
        ga.setMaGa(maGaTam); // Sử dụng mã ga tạm đã tạo trước đó
        
        // Thêm vào database
        if (gaDao.themGa(ga)) {
            JOptionPane.showMessageDialog(this, "Thêm ga thành công!");
            loadDuLieuVaoBang();
            xoaTrang();
            dangThem = false;
            maGaTam = null; // Xóa mã ga tạm sau khi thêm thành công
            capNhatTrangThaiNut(TrangThai.KHOI_TAO);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Thêm ga thất bại! Vui lòng kiểm tra lại thông tin.", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButtontimGaActionPerformed(java.awt.event.ActionEvent evt) {
        capNhatTrangThaiNut(TrangThai.TIM_KIEM);
        
        // Tạo dialog tìm kiếm với 2 options
        String[] options = {"Tìm theo mã ga", "Tìm theo tên ga"};
        int choice = JOptionPane.showOptionDialog(this,
            "Chọn cách tìm kiếm:",
            "Tìm kiếm ga",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
            
        if (choice == -1) { // Người dùng đóng dialog
            capNhatTrangThaiNut(TrangThai.KHOI_TAO);
            return;
        }
        
        String searchValue = JOptionPane.showInputDialog(this, 
            choice == 0 ? "Nhập mã ga cần tìm:" : "Nhập tên ga cần tìm:", 
            "Tìm kiếm ga", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (searchValue != null) {
            searchValue = searchValue.trim();
            if (!searchValue.isEmpty()) {
                modelGa.setRowCount(0);
                
                if (choice == 0) { // Tìm theo mã ga
                    Ga ga = gaDao.timGaTheoMa(searchValue);
                    if (ga != null) {
                        Object[] dong = new Object[]{
                            ga.getMaGa(),
                            ga.getTenGa(),
                            ga.getDiaChi(),
                            ga.getSoDienThoai()
                        };
                        modelGa.addRow(dong);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Không tìm thấy ga nào!", 
                            "Thông báo", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadDuLieuVaoBang();
                        capNhatTrangThaiNut(TrangThai.KHOI_TAO);
                        return;
                    }
                } else { // Tìm theo tên ga
                    ArrayList<Ga> dsGa = gaDao.timGaTheoTen(searchValue);
                    if (dsGa.isEmpty()) {
                        JOptionPane.showMessageDialog(this, 
                            "Không tìm thấy ga nào!", 
                            "Thông báo", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadDuLieuVaoBang();
                        capNhatTrangThaiNut(TrangThai.KHOI_TAO);
                        return;
                    }
                    
                    for (Ga ga : dsGa) {
                        Object[] dong = new Object[]{
                            ga.getMaGa(),
                            ga.getTenGa(),
                            ga.getDiaChi(),
                            ga.getSoDienThoai()
                        };
                        modelGa.addRow(dong);
                    }
                }
            } else {
                loadDuLieuVaoBang();
                capNhatTrangThaiNut(TrangThai.KHOI_TAO);
            }
        } else {
            capNhatTrangThaiNut(TrangThai.KHOI_TAO);
        }
    }

    private void jButtonxoaGaActionPerformed(java.awt.event.ActionEvent evt) {
        int dongChon = jTableQuanLyDiaDiemGa.getSelectedRow();
        if (dongChon == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ga cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        hienThiThongTinGa(dongChon);
        String maGa = jTextFieldmaGa.getText();
        
        int xacNhan = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa ga này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
            
        if (xacNhan == JOptionPane.YES_OPTION) {
            if (gaDao.xoaGa(maGa)) {
                JOptionPane.showMessageDialog(this, "Xóa ga thành công!");
                loadDuLieuVaoBang();
                xoaTrang();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Xóa ga thất bại! Vui lòng kiểm tra lại thông tin.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void capNhatGa() {
        // Lấy dữ liệu từ form
        String maGa = jTextFieldmaGa.getText().trim();
        String tenGa = jTextFieldtenNhanVien.getText().trim();
        String diaChi = jTextFielddiaChi.getText().trim();
        String soDienThoai = jTextFieldsoDienThoai.getText().trim();
        
        // Kiểm tra dữ liệu
        StringBuilder loiNhapLieu = new StringBuilder();
        
        if (tenGa.isEmpty()) {
            loiNhapLieu.append("Tên ga không được để trống!\n");
        }
        
        if (diaChi.isEmpty()) {
            loiNhapLieu.append("Địa chỉ không được để trống!\n");
        }
        
        if (soDienThoai.isEmpty()) {
            loiNhapLieu.append("Số điện thoại không được để trống!\n");
        } else if (!soDienThoai.matches("\\d{10}")) {
            loiNhapLieu.append("Số điện thoại phải có đúng 10 chữ số!\n");
        }
        
        // Nếu có lỗi thì hiển thị và dừng lại
        if (loiNhapLieu.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng sửa các lỗi sau:\n" + loiNhapLieu.toString(), 
                "Lỗi nhập liệu", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Tạo đối tượng ga và cập nhật
        Ga ga = new Ga(tenGa, diaChi, soDienThoai);
        ga.setMaGa(maGa);
        
        if (gaDao.capNhatGa(ga)) {
            JOptionPane.showMessageDialog(this, "Cập nhật ga thành công!");
            loadDuLieuVaoBang();
            xoaTrang();
            dangCapNhat = false;
            capNhatTrangThaiNut(TrangThai.KHOI_TAO);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Cập nhật ga thất bại! Vui lòng kiểm tra lại thông tin.", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButtonxoaTrangActionPerformed(java.awt.event.ActionEvent evt) {
        xoaTrang();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupGioiTinh = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jButtonthemGa = new javax.swing.JButton();
        jButtoncapNhatNhanVien = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabelmaGa = new javax.swing.JLabel();
        jTextFieldmaGa = new javax.swing.JTextField();
        jLabelsoDienThoai = new javax.swing.JLabel();
        jTextFieldsoDienThoai = new javax.swing.JTextField();
        jLabeldiaChi = new javax.swing.JLabel();
        jTextFielddiaChi = new javax.swing.JTextField();
        jLabeltenGa = new javax.swing.JLabel();
        jTextFieldtenNhanVien = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableQuanLyDiaDiemGa = new javax.swing.JTable();
        jButtonxoaGa = new javax.swing.JButton();
        jButtontimGa = new javax.swing.JButton();
        jButtonxoaTrang = new javax.swing.JButton();
        jButtonluu = new javax.swing.JButton();
        jButtonHuy = new javax.swing.JButton();
        header1 = new component.Header();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButtonthemGa.setBackground(new java.awt.Color(65, 165, 238));
        jButtonthemGa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonthemGa.setForeground(new java.awt.Color(255, 255, 255));
        jButtonthemGa.setText("Thêm Ga");
        jButtonthemGa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonthemGaActionPerformed(evt);
            }
        });

        jButtoncapNhatNhanVien.setBackground(new java.awt.Color(65, 165, 238));
        jButtoncapNhatNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtoncapNhatNhanVien.setForeground(new java.awt.Color(255, 255, 255));
        jButtoncapNhatNhanVien.setText("Cập nhật Ga");
        jButtoncapNhatNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtoncapNhatNhanVienActionPerformed(evt);
            }
        });

        jLabelmaGa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelmaGa.setForeground(new java.awt.Color(13, 98, 255));
        jLabelmaGa.setText("Mã Ga :");

        jLabelsoDienThoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelsoDienThoai.setForeground(new java.awt.Color(13, 98, 255));
        jLabelsoDienThoai.setText("Số điện thoại :");

        jLabeldiaChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeldiaChi.setForeground(new java.awt.Color(13, 98, 255));
        jLabeldiaChi.setText("Địa Chỉ :");

        jLabeltenGa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeltenGa.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltenGa.setText("Tên Ga :");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabeldiaChi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(jLabelmaGa, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelsoDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldsoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jTextFieldmaGa, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jLabeltenGa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldtenNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextFielddiaChi))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextFieldtenNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabeltenGa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextFieldmaGa, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelmaGa, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldsoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelsoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabeldiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFielddiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(25, 25, 25))
        );

        jTableQuanLyDiaDiemGa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã Ga", "Tên Ga", "Số điện thoại", "Địa Chỉ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableQuanLyDiaDiemGa);

        jButtonxoaGa.setBackground(new java.awt.Color(65, 165, 238));
        jButtonxoaGa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonxoaGa.setForeground(new java.awt.Color(255, 255, 255));
        jButtonxoaGa.setText("Xóa Ga");
        jButtonxoaGa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonxoaGaActionPerformed(evt);
            }
        });

        jButtontimGa.setBackground(new java.awt.Color(65, 165, 238));
        jButtontimGa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtontimGa.setForeground(new java.awt.Color(255, 255, 255));
        jButtontimGa.setText("Tìm Ga");
        jButtontimGa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtontimGaActionPerformed(evt);
            }
        });

        jButtonxoaTrang.setBackground(new java.awt.Color(65, 165, 238));
        jButtonxoaTrang.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonxoaTrang.setForeground(new java.awt.Color(255, 255, 255));
        jButtonxoaTrang.setText("Xóa trắng");
        jButtonxoaTrang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonxoaTrangActionPerformed(evt);
            }
        });

        jButtonluu.setBackground(new java.awt.Color(65, 165, 238));
        jButtonluu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonluu.setForeground(new java.awt.Color(255, 255, 255));
        jButtonluu.setText("Lưu");
        jButtonluu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonluuActionPerformed(evt);
            }
        });

        jButtonHuy.setBackground(new java.awt.Color(65, 165, 238));
        jButtonHuy.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonHuy.setForeground(new java.awt.Color(255, 255, 255));
        jButtonHuy.setText("Hủy");
        jButtonHuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHuyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButtonxoaTrang)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonluu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonHuy)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtontimGa)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonthemGa)
                                .addGap(11, 11, 11)
                                .addComponent(jButtonxoaGa)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtoncapNhatNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonthemGa, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonxoaGa, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtoncapNhatNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtontimGa, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonxoaTrang, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonluu, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(header1, javax.swing.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonNamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonNamActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonNamActionPerformed

    private void jRadioButtonNuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonNuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonNuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupGioiTinh;
    private component.Header header1;
    private javax.swing.JButton jButtonHuy;
    private javax.swing.JButton jButtoncapNhatNhanVien;
    private javax.swing.JButton jButtonluu;
    private javax.swing.JButton jButtonthemGa;
    private javax.swing.JButton jButtontimGa;
    private javax.swing.JButton jButtonxoaGa;
    private javax.swing.JButton jButtonxoaTrang;
    private javax.swing.JLabel jLabeldiaChi;
    private javax.swing.JLabel jLabelmaGa;
    private javax.swing.JLabel jLabelsoDienThoai;
    private javax.swing.JLabel jLabeltenGa;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableQuanLyDiaDiemGa;
    private javax.swing.JTextField jTextFielddiaChi;
    private javax.swing.JTextField jTextFieldmaGa;
    private javax.swing.JTextField jTextFieldsoDienThoai;
    private javax.swing.JTextField jTextFieldtenNhanVien;
    // End of variables declaration//GEN-END:variables
}
