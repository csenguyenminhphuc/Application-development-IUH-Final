/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.quanlykhuyenmai;

import com.toedter.calendar.JDateChooser;
import connectdb.ConnectDB;
import entity.NhanVien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Random;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 *
 * @author HAI TAM
 */
public class QuanLyKhuyenMai extends javax.swing.JPanel {

    /**
     * Creates new form QuanLyKhachHang
     */
    private DefaultTableModel tableModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private ButtonGroup statusGroup;

    public QuanLyKhuyenMai(NhanVien nv) {
        initComponents();
        header1.jLabel14.setText("Quản Lý Khuyến Mãi");
        header1.jLabel13.setText("Chào Nhân Viên Quản Lý");
        header1.jButton4.setText(nv.getTenNV());
        setupTable();
        loadTableData();
        setupStatusRadioButtons();
        setupTableSelectionListener();
        jTextFieldmaKhuyenMai.setEditable(false); // Khóa mã khuyến mãi
        jTextFieldmaKhuyenMai.setEnabled(false);
        jTextFieldngayBatDau.setEditable(false); // Khóa ngày bắt đầu
        jTextFieldngayBatDau.setEnabled(false);
        jTextFieldngayKetThuc.setEditable(false); // Khóa ngày kết thúc
        jTextFieldngayKetThuc.setEnabled(false);
//        header1.jLabel14.setText("Quản lý khuyến mãi");
//        header1.jButton4.setText(nv.getTenNV());
    }
    
    private void setupTable() {
        tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Mã Khuyến Mãi", "Tên Khuyến Mãi", "Hệ Số Khuyến Mãi", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Tổng Tiền Tối Thiểu", "Tiền Khuyến Mãi Tối Đa", "Trạng Thái"}
        );
        jTabledanhSachKM.setModel(tableModel);
    }

    private void setupStatusRadioButtons() {
        statusGroup = new ButtonGroup();
        statusGroup.add(jRadioButtonconHieuLuc);
        statusGroup.add(jRadioButtondaHetHan);
        jRadioButtonconHieuLuc.setSelected(true);
    }

    private void setupTableSelectionListener() {
        jTabledanhSachKM.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = jTabledanhSachKM.getSelectedRow();
                    if (selectedRow >= 0) {
                        jTextFieldmaKhuyenMai.setText(tableModel.getValueAt(selectedRow, 0).toString());
                        jTextFieldtenKhuyenMai.setText(tableModel.getValueAt(selectedRow, 1).toString());
                        jTextFieldhesoKM.setText(tableModel.getValueAt(selectedRow, 2).toString());
                        jTextFieldngayBatDau.setText(tableModel.getValueAt(selectedRow, 3).toString());
                        jTextFieldngayKetThuc.setText(tableModel.getValueAt(selectedRow, 4).toString());
                        jTextFieldtienToiThieu.setText(tableModel.getValueAt(selectedRow, 5).toString());
                        jTextFieldtienKMToiDa.setText(tableModel.getValueAt(selectedRow, 6).toString());

                        String trangThai = tableModel.getValueAt(selectedRow, 7).toString();
                        jRadioButtonconHieuLuc.setSelected(trangThai.equals("Còn hiệu lực"));
                        jRadioButtondaHetHan.setSelected(trangThai.equals("Đã hết hạn"));
                    }
                }
            }
        });
    }

    private void loadTableData() {
        tableModel.setRowCount(0); // Xóa các hàng hiện có
        ConnectDB.getInstance().connect();
        Connection conn = ConnectDB.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectDB.getConnection();
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra kết nối.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "SELECT * FROM KhuyenMai ORDER BY maKhuyenMai ASC"; // Sử dụng cho SQL Server
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                String maKhuyenMai = rs.getString("maKhuyenMai");
                String tenKhuyenMai = rs.getString("tenKhuyenMai");
                double heSoKhuyenMai = rs.getDouble("heSoKhuyenMai");
                Date ngayBatDau = rs.getDate("ngayBatDau");
                Date ngayKetThuc = rs.getDate("ngayKetThuc");
                double tongTienToiThieu = rs.getDouble("tongTienToiThieu");
                double tienKhuyenMaiToiDa = rs.getDouble("tienKhuyenMaiToiDa");
                String trangThai = rs.getString("trangThai");

                tableModel.addRow(new Object[]{
                    maKhuyenMai,
                    tenKhuyenMai,
                    heSoKhuyenMai,
                    ngayBatDau != null ? sdf.format(ngayBatDau) : "",
                    ngayKetThuc != null ? sdf.format(ngayKetThuc) : "",
                    tongTienToiThieu,
                    tienKhuyenMaiToiDa,
                    trangThai
                });
            }
        } catch (SQLException e) {
            String errorMessage = "Lỗi tải danh sách khuyến mãi: " + e.getMessage();
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                errorMessage = "Mất kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra kết nối mạng hoặc cấu hình SQL Server.";
            }
            JOptionPane.showMessageDialog(this, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generatePromotionCode(Connection conn) {
    String prefix = "KM";
    String datePart = new SimpleDateFormat("ddMMyyyy").format(new Date());
    int sequence = 1;

    try {
        String likePattern = prefix + "-" + datePart + "-%";
        String maxQuery = "SELECT MAX(CAST(RIGHT(maKhuyenMai, 3) AS INT)) FROM KhuyenMai WHERE maKhuyenMai LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(maxQuery)) {
            pstmt.setString(1, likePattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int maxSeq = rs.getInt(1);
                    if (!rs.wasNull()) {
                        sequence = maxSeq + 1;
                    }
                }
            }
        }

        String code;
        while (true) {
            code = String.format("%s-%s-%03d", prefix, datePart, sequence);

            String checkQuery = "SELECT COUNT(*) FROM KhuyenMai WHERE maKhuyenMai = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, code);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        break; // Mã chưa tồn tại, có thể dùng được
                    }
                }
            }
            sequence++; // tăng số nếu bị trùng
        }

        return code;
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Lỗi tạo mã khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        return null;
    }
}

    private void suggestAmounts() {
        double suggestedMinTotal = 1000000; // 1,000,000 VND
        double suggestedMaxPromo = 500000;  // 500,000 VND

        if (jTextFieldtienToiThieu.getText().trim().isEmpty()) {
            jTextFieldtienToiThieu.setText(String.valueOf(suggestedMinTotal));
        }
        if (jTextFieldtienKMToiDa.getText().trim().isEmpty()) {
            jTextFieldtienKMToiDa.setText(String.valueOf(suggestedMaxPromo));
        }
    }

    private void clearForm() {
        jTextFieldmaKhuyenMai.setText("");
        jTextFieldtenKhuyenMai.setText("");
        jTextFieldngayBatDau.setText("");
        jTextFieldngayKetThuc.setText("");
        jTextFieldtienToiThieu.setText("");
        jTextFieldtienKMToiDa.setText("");
        jTextFieldhesoKM.setText("");
        jRadioButtonconHieuLuc.setSelected(true);
        jTabledanhSachKM.clearSelection();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jButtonthemKM = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabelmaKhuyenMai = new javax.swing.JLabel();
        jTextFieldmaKhuyenMai = new javax.swing.JTextField();
        jLabeltenKhuyenMai = new javax.swing.JLabel();
        jTextFieldtenKhuyenMai = new javax.swing.JTextField();
        jLabelngayBatDau = new javax.swing.JLabel();
        jTextFieldngayBatDau = new javax.swing.JTextField();
        jButtonngayBatDau = new javax.swing.JButton();
        jTextFieldngayKetThuc = new javax.swing.JTextField();
        jButtonngayKetThuc = new javax.swing.JButton();
        jLabeltongTienToiThieu = new javax.swing.JLabel();
        jLabelhesoKM = new javax.swing.JLabel();
        jTextFieldtienToiThieu = new javax.swing.JTextField();
        jTextFieldtienKMToiDa = new javax.swing.JTextField();
        jTextFieldhesoKM = new javax.swing.JTextField();
        jRadioButtonconHieuLuc = new javax.swing.JRadioButton();
        jRadioButtondaHetHan = new javax.swing.JRadioButton();
        jLabelngayKetThuc = new javax.swing.JLabel();
        jLabeltienKMToiDa = new javax.swing.JLabel();
        jLabeltrangThai = new javax.swing.JLabel();
        jButtoncapNhatKM = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabledanhSachKM = new javax.swing.JTable();
        jButtonxoaKM = new javax.swing.JButton();
        jButtontimKM = new javax.swing.JButton();
        header1 = new component.Header();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButtonthemKM.setBackground(new java.awt.Color(65, 165, 238));
        jButtonthemKM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonthemKM.setForeground(new java.awt.Color(255, 255, 255));
        jButtonthemKM.setText("Thêm Khuyến Mãi");
        jButtonthemKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonthemKMActionPerformed(evt);
            }
        });

        jLabelmaKhuyenMai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelmaKhuyenMai.setForeground(new java.awt.Color(13, 98, 255));
        jLabelmaKhuyenMai.setText("Mã Khuyến Mãi :");

        jLabeltenKhuyenMai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeltenKhuyenMai.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltenKhuyenMai.setText("Tên Khuyến Mãi :");

        jLabelngayBatDau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelngayBatDau.setForeground(new java.awt.Color(13, 98, 255));
        jLabelngayBatDau.setText("Ngày Bắt Đầu :");

        jButtonngayBatDau.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/calendar.png"))); // NOI18N
        jButtonngayBatDau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonngayBatDauActionPerformed(evt);
            }
        });

        jButtonngayKetThuc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/calendar.png"))); // NOI18N
        jButtonngayKetThuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonngayKetThucActionPerformed(evt);
            }
        });

        jLabeltongTienToiThieu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeltongTienToiThieu.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltongTienToiThieu.setText("Tổng Tiền Tối Thiểu :");

        jLabelhesoKM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelhesoKM.setForeground(new java.awt.Color(13, 98, 255));
        jLabelhesoKM.setText("Hệ Số Khuyến Mãi :");

        jTextFieldtienToiThieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldtienToiThieuActionPerformed(evt);
            }
        });

        jTextFieldtienKMToiDa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldtienKMToiDaActionPerformed(evt);
            }
        });

        jTextFieldhesoKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldhesoKMActionPerformed(evt);
            }
        });

        jRadioButtonconHieuLuc.setText("Còn hiệu lực");

        jRadioButtondaHetHan.setText("Đã hết hạn");

        jLabelngayKetThuc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelngayKetThuc.setForeground(new java.awt.Color(13, 98, 255));
        jLabelngayKetThuc.setText("Ngày Kết Thúc :");

        jLabeltienKMToiDa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeltienKMToiDa.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltienKMToiDa.setText("Tiền Khuyến Mãi Tối Đa :");

        jLabeltrangThai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeltrangThai.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltrangThai.setText("Trạng Thái :");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabeltenKhuyenMai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelngayBatDau, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelmaKhuyenMai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabeltongTienToiThieu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelhesoKM, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jTextFieldmaKhuyenMai)
                                .addGap(6, 6, 6))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTextFieldtienToiThieu, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jTextFieldngayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(jButtonngayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTextFieldhesoKM))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelngayKetThuc)
                                    .addComponent(jLabeltienKMToiDa)
                                    .addComponent(jLabeltrangThai))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jRadioButtonconHieuLuc)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jRadioButtondaHetHan)
                                        .addGap(74, 74, 74))
                                    .addComponent(jTextFieldtienKMToiDa)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jTextFieldngayKetThuc)
                                        .addGap(0, 0, 0)
                                        .addComponent(jButtonngayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jTextFieldtenKhuyenMai)
                        .addContainerGap())))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelmaKhuyenMai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldmaKhuyenMai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabeltenKhuyenMai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldtenKhuyenMai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldngayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelngayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldngayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelngayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonngayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonngayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabeltongTienToiThieu, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldtienToiThieu, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldtienKMToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabeltienKMToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelhesoKM, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldhesoKM, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButtonconHieuLuc)
                    .addComponent(jRadioButtondaHetHan)
                    .addComponent(jLabeltrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtoncapNhatKM.setBackground(new java.awt.Color(65, 165, 238));
        jButtoncapNhatKM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtoncapNhatKM.setForeground(new java.awt.Color(255, 255, 255));
        jButtoncapNhatKM.setText("Cập Nhật Khuyến Mãi");
        jButtoncapNhatKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtoncapNhatKMActionPerformed(evt);
            }
        });

        jTabledanhSachKM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Khuyến Mãi", "Tên Khuyến Mãi", "Hệ Số Khuyến Mãi", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Tổng Tiền Tối Thiểu ", "Tiền Khuyến Mãi Tối Đa", "Trạng Thái"
            }
        ));
        jScrollPane1.setViewportView(jTabledanhSachKM);

        jButtonxoaKM.setBackground(new java.awt.Color(65, 165, 238));
        jButtonxoaKM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonxoaKM.setForeground(new java.awt.Color(255, 255, 255));
        jButtonxoaKM.setText("Xóa khuyến mãi");
        jButtonxoaKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonxoaKMActionPerformed(evt);
            }
        });

        jButtontimKM.setBackground(new java.awt.Color(65, 165, 238));
        jButtontimKM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtontimKM.setForeground(new java.awt.Color(255, 255, 255));
        jButtontimKM.setText("Tìm Khuyến Mãi");
        jButtontimKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtontimKMActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1095, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtontimKM)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonthemKM)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonxoaKM)
                        .addGap(18, 18, 18)
                        .addComponent(jButtoncapNhatKM, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonthemKM, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonxoaKM, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtoncapNhatKM, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtontimKM, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(header1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonthemKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonthemKMActionPerformed
        // TODO add your handling code here: Xử lý thêm khuyến mãi
       
        String tenKhuyenMai = jTextFieldtenKhuyenMai.getText().trim();
        String ngayBatDauStr = jTextFieldngayBatDau.getText().trim();
        String ngayKetThucStr = jTextFieldngayKetThuc.getText().trim();
        String tongTienToiThieuStr = jTextFieldtienToiThieu.getText().trim();
        String tienKhuyenMaiToiDaStr = jTextFieldtienKMToiDa.getText().trim();
        String heSoKhuyenMaiStr = jTextFieldhesoKM.getText().trim();
        String trangThai = jRadioButtonconHieuLuc.isSelected() ? "Còn hiệu lực" : "Đã hết hạn";

        // Kiểm tra dữ liệu trống
        if (tenKhuyenMai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khuyến mãi.");
            return;
        }
        if (tongTienToiThieuStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tổng tiền tối thiểu.");
            return;
        }
        if (tienKhuyenMaiToiDaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền khuyến mãi tối đa.");
            return;
        }
        if (heSoKhuyenMaiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập hệ số khuyến mãi.");
            return;
        }

        try {
            double tongTienToiThieu = Double.parseDouble(tongTienToiThieuStr);
            double tienKhuyenMaiToiDa = Double.parseDouble(tienKhuyenMaiToiDaStr);
            double heSoKhuyenMai = Double.parseDouble(heSoKhuyenMaiStr);

            if (tongTienToiThieu < 0 || tienKhuyenMaiToiDa < 0 || heSoKhuyenMai <= 0) {
                JOptionPane.showMessageDialog(this, "Tổng tiền và hệ số khuyến mãi phải là số dương.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tongTienToiThieu <= tienKhuyenMaiToiDa) {
                JOptionPane.showMessageDialog(this, "Tổng tiền tối thiểu phải lớn hơn tiền khuyến mãi tối đa.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date ngayBatDau = sdf.parse(ngayBatDauStr);
            Date ngayKetThuc = sdf.parse(ngayKetThucStr);

            if (ngayKetThuc.before(ngayBatDau)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ConnectDB.getInstance().connect();
            Connection conn = ConnectDB.getConnection();

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gọi hàm tạo mã khuyến mãi với connection hiện tại
            String maKhuyenMai = generatePromotionCode(conn);
            if (maKhuyenMai == null) {
                // lỗi khi tạo mã, dừng
                return;
            }
            System.out.println("DEBUG: Generated promotion code = " + maKhuyenMai);

            String query = "INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, heSoKhuyenMai, ngayBatDau, ngayKetThuc, tongTienToiThieu, tienKhuyenMaiToiDa, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, maKhuyenMai);
                pstmt.setString(2, tenKhuyenMai);
                pstmt.setDouble(3, heSoKhuyenMai);
                pstmt.setDate(4, new java.sql.Date(ngayBatDau.getTime()));
                pstmt.setDate(5, new java.sql.Date(ngayKetThuc.getTime()));
                pstmt.setDouble(6, tongTienToiThieu);
                pstmt.setDouble(7, tienKhuyenMaiToiDa);
                pstmt.setString(8, trangThai);

                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTableData();

            conn.close();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tổng tiền và hệ số khuyến mãi phải là số hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ (dd-MM-yyyy).", "Lỗi", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khuyến mãi: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonthemKMActionPerformed

    private void jButtonngayBatDauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonngayBatDauActionPerformed
        // TODO add your handling code here:
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy");
        int result = JOptionPane.showConfirmDialog(this, dateChooser, "Chọn Ngày Bắt Đầu", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && dateChooser.getDate() != null) {
            jTextFieldngayBatDau.setText(sdf.format(dateChooser.getDate()));
        }
    }//GEN-LAST:event_jButtonngayBatDauActionPerformed

    private void jButtonngayKetThucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonngayKetThucActionPerformed
        // TODO add your handling code here:
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy");
        int result = JOptionPane.showConfirmDialog(this, dateChooser, "Chọn Ngày Kết Thúc", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && dateChooser.getDate() != null) {
            jTextFieldngayKetThuc.setText(sdf.format(dateChooser.getDate()));
        }
    }//GEN-LAST:event_jButtonngayKetThucActionPerformed

    private void jButtoncapNhatKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtoncapNhatKMActionPerformed
        // TODO add your handling code here: Xử lý cập nhật khuyến mãi
        String maKhuyenMai = jTextFieldmaKhuyenMai.getText().trim();

        if (maKhuyenMai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Khuyến Mãi để cập nhật.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = ConnectDB.getConnection();
        PreparedStatement selectStmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectQuery = "SELECT * FROM KhuyenMai WHERE maKhuyenMai = ?";
            selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, maKhuyenMai);
            rs = selectStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi với mã " + maKhuyenMai, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String currentTenKhuyenMai = rs.getString("tenKhuyenMai");
            double currentHeSoKhuyenMai = rs.getDouble("heSoKhuyenMai");
            Date currentNgayBatDau = rs.getDate("ngayBatDau");
            Date currentNgayKetThuc = rs.getDate("ngayKetThuc");
            double currentTongTienToiThieu = rs.getDouble("tongTienToiThieu");
            double currentTienKhuyenMaiToiDa = rs.getDouble("tienKhuyenMaiToiDa");
            String currentTrangThai = rs.getString("trangThai");
            ConnectDB.getInstance().closeItem(selectStmt, rs);

            String tenKhuyenMai = jTextFieldtenKhuyenMai.getText().trim();
            String ngayBatDauStr = jTextFieldngayBatDau.getText().trim();
            String ngayKetThucStr = jTextFieldngayKetThuc.getText().trim();
            String tongTienToiThieuStr = jTextFieldtienToiThieu.getText().trim();
            String tienKhuyenMaiToiDaStr = jTextFieldtienKMToiDa.getText().trim();
            String heSoKhuyenMaiStr = jTextFieldhesoKM.getText().trim();
            String trangThai = jRadioButtonconHieuLuc.isSelected() ? "Còn hiệu lực" : "Đã hết hạn";

            String newTenKhuyenMai = tenKhuyenMai.isEmpty() ? currentTenKhuyenMai : tenKhuyenMai;

            Date newNgayBatDau = currentNgayBatDau;
            if (!ngayBatDauStr.isEmpty()) {
                try {
                    newNgayBatDau = sdf.parse(ngayBatDauStr);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Định dạng ngày bắt đầu không hợp lệ (dd-MM-yyyy).", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Date newNgayKetThuc = currentNgayKetThuc;
            if (!ngayKetThucStr.isEmpty()) {
                try {
                    newNgayKetThuc = sdf.parse(ngayKetThucStr);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Định dạng ngày kết thúc không hợp lệ (dd-MM-yyyy).", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (newNgayKetThuc != null && newNgayBatDau != null && newNgayKetThuc.before(newNgayBatDau)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double newTongTienToiThieu = currentTongTienToiThieu;
            if (!tongTienToiThieuStr.isEmpty()) {
                try {
                    newTongTienToiThieu = Double.parseDouble(tongTienToiThieuStr);
                    if (newTongTienToiThieu < 0) {
                        JOptionPane.showMessageDialog(this, "Tổng tiền tối thiểu phải là số dương.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Tổng tiền tối thiểu phải là số hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            double newTienKhuyenMaiToiDa = currentTienKhuyenMaiToiDa;
            if (!tienKhuyenMaiToiDaStr.isEmpty()) {
                try {
                    newTienKhuyenMaiToiDa = Double.parseDouble(tienKhuyenMaiToiDaStr);
                    if (newTienKhuyenMaiToiDa < 0) {
                        JOptionPane.showMessageDialog(this, "Tiền khuyến mãi tối đa phải là số dương.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Tiền khuyến mãi tối đa phải là số hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (!tongTienToiThieuStr.isEmpty() || !tienKhuyenMaiToiDaStr.isEmpty()) {
                if (newTongTienToiThieu <= newTienKhuyenMaiToiDa) {
                    JOptionPane.showMessageDialog(this, "Tổng tiền tối thiểu phải lớn hơn tiền khuyến mãi tối đa.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            double newHeSoKhuyenMai = currentHeSoKhuyenMai;
            if (!heSoKhuyenMaiStr.isEmpty()) {
                try {
                    newHeSoKhuyenMai = Double.parseDouble(heSoKhuyenMaiStr);
                    if (newHeSoKhuyenMai <= 0) {
                        JOptionPane.showMessageDialog(this, "Hệ số khuyến mãi phải là số dương.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Hệ số khuyến mãi phải là số hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            String updateQuery = "UPDATE KhuyenMai SET tenKhuyenMai = ?, heSoKhuyenMai = ?, ngayBatDau = ?, ngayKetThuc = ?, tongTienToiThieu = ?, tienKhuyenMaiToiDa = ?, trangThai = ? WHERE maKhuyenMai = ?";
            pstmt = conn.prepareStatement(updateQuery);
            pstmt.setString(1, newTenKhuyenMai);
            pstmt.setDouble(2, newHeSoKhuyenMai);
            pstmt.setDate(3, new java.sql.Date(newNgayBatDau.getTime()));
            pstmt.setDate(4, new java.sql.Date(newNgayKetThuc.getTime()));
            pstmt.setDouble(5, newTongTienToiThieu);
            pstmt.setDouble(6, newTienKhuyenMaiToiDa);
            pstmt.setString(7, trangThai);
            pstmt.setString(8, maKhuyenMai);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi với mã " + maKhuyenMai, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectDB.getInstance().closeItem(selectStmt, rs);
            ConnectDB.getInstance().closeItem(pstmt, null);
        }
    }//GEN-LAST:event_jButtoncapNhatKMActionPerformed

    private void jButtonxoaKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonxoaKMActionPerformed
        // TODO add your handling code here: Xử lý xóa khuyến mãi
        int row = jTabledanhSachKM.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi để xóa.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maKhuyenMai = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khuyến mãi " + maKhuyenMai + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = null;
            try {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, maKhuyenMai);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi với mã " + maKhuyenMai, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } finally {
                ConnectDB.getInstance().closeItem(pstmt, null);
            }
        }
    }//GEN-LAST:event_jButtonxoaKMActionPerformed

    private void jButtontimKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtontimKMActionPerformed
        // TODO add your handling code here: Xử lý nút tìm khuyến mãi
        String maKhuyenMai = jTextFieldmaKhuyenMai.getText().trim();
        String tenKhuyenMai = jTextFieldtenKhuyenMai.getText().trim();

        if (maKhuyenMai.isEmpty() && tenKhuyenMai.isEmpty()) {
            loadTableData();
            JOptionPane.showMessageDialog(this, "Đã tải lại toàn bộ danh sách khuyến mãi.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);

        Connection conn = ConnectDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder query = new StringBuilder("SELECT TOP 5 * FROM KhuyenMai WHERE 1=1");
            int paramIndex = 1;
            if (!maKhuyenMai.isEmpty()) {
                query.append(" AND maKhuyenMai LIKE ?");
            }
            if (!tenKhuyenMai.isEmpty()) {
                query.append(" AND tenKhuyenMai LIKE ?");
            }

            pstmt = conn.prepareStatement(query.toString());
            if (!maKhuyenMai.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + maKhuyenMai + "%");
            }
            if (!tenKhuyenMai.isEmpty()) {
                pstmt.setString(paramIndex, "%" + tenKhuyenMai + "%");
            }

            rs = pstmt.executeQuery();
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                String maKM = rs.getString("maKhuyenMai");
                String tenKM = rs.getString("tenKhuyenMai");
                double heSoKhuyenMai = rs.getDouble("heSoKhuyenMai");
                Date ngayBatDau = rs.getDate("ngayBatDau");
                Date ngayKetThuc = rs.getDate("ngayKetThuc");
                double tongTienToiThieu = rs.getDouble("tongTienToiThieu");
                double tienKhuyenMaiToiDa = rs.getDouble("tienKhuyenMaiToiDa");
                String trangThai = rs.getString("trangThai");

                tableModel.addRow(new Object[]{
                    maKM,
                    tenKM,
                    heSoKhuyenMai,
                    ngayBatDau != null ? sdf.format(ngayBatDau) : "",
                    ngayKetThuc != null ? sdf.format(ngayKetThuc) : "",
                    tongTienToiThieu,
                    tienKhuyenMaiToiDa,
                    trangThai
                });
            }

            if (!hasResults) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Tìm kiếm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectDB.getInstance().closeItem(pstmt, rs);
        }
    }//GEN-LAST:event_jButtontimKMActionPerformed

    private void jTextFieldhesoKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldhesoKMActionPerformed
        // TODO add your handling code here:
        if (jTextFieldhesoKM.getText().trim().isEmpty()) {
            // Danh sách gợi ý hệ số khuyến mãi
            Double[] suggestions = {0.2, 0.3, 0.4, 0.5};
            JComboBox<Double> comboBox = new JComboBox<>(suggestions);
            int result = JOptionPane.showConfirmDialog(this, comboBox, "Chọn Hệ Số Khuyến Mãi", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && comboBox.getSelectedItem() != null) {
                jTextFieldhesoKM.setText(String.format("%.2f", comboBox.getSelectedItem()));
            }
        }
    }//GEN-LAST:event_jTextFieldhesoKMActionPerformed

    private void jTextFieldtienToiThieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldtienToiThieuActionPerformed
        // TODO add your handling code here:
        if (jTextFieldtienToiThieu.getText().trim().isEmpty()) {
            // Danh sách gợi ý tổng tiền tối thiểu
            Double[] suggestions = {100000.0, 200000.0, 500000.0};
            JComboBox<Double> comboBox = new JComboBox<>(suggestions);
            int result = JOptionPane.showConfirmDialog(this, comboBox, "Chọn Tổng Tiền Tối Thiểu", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && comboBox.getSelectedItem() != null) {
                jTextFieldtienToiThieu.setText(String.format("%.0f", comboBox.getSelectedItem()));
            }
        }
    }//GEN-LAST:event_jTextFieldtienToiThieuActionPerformed

    private void jTextFieldtienKMToiDaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldtienKMToiDaActionPerformed
        // TODO add your handling code here:
        if (jTextFieldtienKMToiDa.getText().trim().isEmpty()) {
            // Danh sách gợi ý tiền khuyến mãi tối đa
            Double[] suggestions = {100000.0, 200000.0, 500000.0};
            JComboBox<Double> comboBox = new JComboBox<>(suggestions);
            int result = JOptionPane.showConfirmDialog(this, comboBox, "Chọn Tiền Khuyến Mãi Tối Đa", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION && comboBox.getSelectedItem() != null) {
                jTextFieldtienKMToiDa.setText(String.format("%.0f", comboBox.getSelectedItem()));
            }
        }
    }//GEN-LAST:event_jTextFieldtienKMToiDaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Header header1;
    private javax.swing.JButton jButtoncapNhatKM;
    private javax.swing.JButton jButtonngayBatDau;
    private javax.swing.JButton jButtonngayKetThuc;
    private javax.swing.JButton jButtonthemKM;
    private javax.swing.JButton jButtontimKM;
    private javax.swing.JButton jButtonxoaKM;
    private javax.swing.JLabel jLabelhesoKM;
    private javax.swing.JLabel jLabelmaKhuyenMai;
    private javax.swing.JLabel jLabelngayBatDau;
    private javax.swing.JLabel jLabelngayKetThuc;
    private javax.swing.JLabel jLabeltenKhuyenMai;
    private javax.swing.JLabel jLabeltienKMToiDa;
    private javax.swing.JLabel jLabeltongTienToiThieu;
    private javax.swing.JLabel jLabeltrangThai;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButtonconHieuLuc;
    private javax.swing.JRadioButton jRadioButtondaHetHan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabledanhSachKM;
    private javax.swing.JTextField jTextFieldhesoKM;
    private javax.swing.JTextField jTextFieldmaKhuyenMai;
    private javax.swing.JTextField jTextFieldngayBatDau;
    private javax.swing.JTextField jTextFieldngayKetThuc;
    private javax.swing.JTextField jTextFieldtenKhuyenMai;
    private javax.swing.JTextField jTextFieldtienKMToiDa;
    private javax.swing.JTextField jTextFieldtienToiThieu;
    // End of variables declaration//GEN-END:variables

    
}
