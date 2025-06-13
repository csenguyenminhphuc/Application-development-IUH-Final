/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.quanlytau;

import com.toedter.calendar.JDateChooser;
import connectdb.ConnectDB;
import entity.NhanVien;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 *
 * @author HAI TAM
 */
public class QuanLyTau extends javax.swing.JPanel {

    /**
     * Creates new form QuanLyTau
     */
    public QuanLyTau(NhanVien employee) {
        initComponents();
        initializeTableModel();
        loadTableData(null); // Load tất cả danh sách tàu
        addTableMouseListener();
        jTextFieldmaTau.setEditable(false); // Khóa mã khuyến mãi
        jTextFieldmaTau.setEnabled(false);
    }

    private void initializeTableModel() {
        // Initialize table model with no initial rows
        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Mã Tàu", "Tên Tàu", "Số Toa Tàu"}
        );
        jTabledanhSachTau.setModel(model);
    }
    
    private void loadTableData(String searchCriteria) {
        DefaultTableModel model = (DefaultTableModel) jTabledanhSachTau.getModel();
        model.setRowCount(0); // Clear existing rows

        String query = "SELECT maTau, tenTau, soToaTau FROM Tau";
        if (searchCriteria != null) {
            query += " WHERE " + searchCriteria;
        }

        try {
            // Khởi tạo kết nối
            ConnectDB.getInstance().connect();
            Connection conn = ConnectDB.getConnection();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("maTau"),
                        rs.getString("tenTau"),
                        rs.getInt("soToaTau")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println("System Error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTableMouseListener() {
        jTabledanhSachTau.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = jTabledanhSachTau.getSelectedRow();
                if (row >= 0) {
                    jTextFieldmaTau.setText(jTabledanhSachTau.getValueAt(row, 0).toString());
                    jTextFieldtenTau.setText(jTabledanhSachTau.getValueAt(row, 1).toString());
                    jTextFieldsoToaTau.setText(jTabledanhSachTau.getValueAt(row, 2).toString());
                }
            }
        });
    }

    private void clearFields() {
        jTextFieldmaTau.setText("");
        jTextFieldtenTau.setText("");
        jTextFieldsoToaTau.setText("");
    }

    private boolean validateInput(String maTau, String tenTau, String soToaTauStr) {
        if (maTau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã tàu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (tenTau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên tàu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (soToaTauStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số toa tàu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (maTau.matches(".*[;']+.*") || tenTau.matches(".*[;']+.*")) {
            JOptionPane.showMessageDialog(this, "Mã tàu và tên tàu không được chứa ký tự ; hoặc '!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            int soToaTau = Integer.parseInt(soToaTauStr);
            if (soToaTau <= 0) {
                JOptionPane.showMessageDialog(this, "Số toa tàu phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số toa tàu phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    public String sinhMaTauTuDong() throws SQLException {
        Connection conn = ConnectDB.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT maTau FROM Tau ORDER BY maTau DESC");

        int maxNumber = 0;
        if (rs.next()) {
            String lastMa = rs.getString("maTau");
            String[] parts = lastMa.split("-");
            if (parts.length == 2) {
                maxNumber = Integer.parseInt(parts[1]);
            }
        }

        rs.close();
        stmt.close();
        return String.format("TAU-%03d", maxNumber + 1);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel9 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabelmaTau = new javax.swing.JLabel();
        jTextFieldmaTau = new javax.swing.JTextField();
        jLabeltenTau = new javax.swing.JLabel();
        jTextFieldtenTau = new javax.swing.JTextField();
        jLabelsoToaTau = new javax.swing.JLabel();
        jTextFieldsoToaTau = new javax.swing.JTextField();
        jButtontimTau = new javax.swing.JButton();
        jButtonthemTau = new javax.swing.JButton();
        jButtonxoaTau = new javax.swing.JButton();
        jButtoncapNhatTau = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabledanhSachTau = new javax.swing.JTable();

        jPanel9.setBackground(new java.awt.Color(78, 133, 248));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Chào Nhân Viên Quản Lý");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Thông Tin Quản Lý Tàu");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabelmaTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelmaTau.setForeground(new java.awt.Color(13, 98, 255));
        jLabelmaTau.setText("Mã Tàu :");

        jTextFieldmaTau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldmaTauActionPerformed(evt);
            }
        });

        jLabeltenTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeltenTau.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltenTau.setText("Tên Tàu :");

        jTextFieldtenTau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldtenTauActionPerformed(evt);
            }
        });

        jLabelsoToaTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelsoToaTau.setForeground(new java.awt.Color(13, 98, 255));
        jLabelsoToaTau.setText("Số Toa Tàu :");

        jTextFieldsoToaTau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldsoToaTauActionPerformed(evt);
            }
        });

        jButtontimTau.setBackground(new java.awt.Color(65, 165, 238));
        jButtontimTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtontimTau.setForeground(new java.awt.Color(255, 255, 255));
        jButtontimTau.setText("Tìm Tàu");
        jButtontimTau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtontimTauActionPerformed(evt);
            }
        });

        jButtonthemTau.setBackground(new java.awt.Color(65, 165, 238));
        jButtonthemTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonthemTau.setForeground(new java.awt.Color(255, 255, 255));
        jButtonthemTau.setText("Thêm Tàu");
        jButtonthemTau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonthemTauActionPerformed(evt);
            }
        });

        jButtonxoaTau.setBackground(new java.awt.Color(65, 165, 238));
        jButtonxoaTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonxoaTau.setForeground(new java.awt.Color(255, 255, 255));
        jButtonxoaTau.setText("Xóa Tàu");
        jButtonxoaTau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonxoaTauActionPerformed(evt);
            }
        });

        jButtoncapNhatTau.setBackground(new java.awt.Color(65, 165, 238));
        jButtoncapNhatTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtoncapNhatTau.setForeground(new java.awt.Color(255, 255, 255));
        jButtoncapNhatTau.setText("Cập Nhật Tàu");
        jButtoncapNhatTau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtoncapNhatTauActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelmaTau, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabeltenTau, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jTextFieldtenTau)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelsoToaTau)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldsoToaTau))
                            .addComponent(jTextFieldmaTau)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap(502, Short.MAX_VALUE)
                        .addComponent(jButtontimTau)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonthemTau)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonxoaTau)
                        .addGap(18, 18, 18)
                        .addComponent(jButtoncapNhatTau, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelmaTau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldmaTau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabeltenTau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldtenTau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelsoToaTau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldsoToaTau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonthemTau, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonxoaTau, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtoncapNhatTau, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtontimTau, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabledanhSachTau.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã Tàu", "Tên Tàu", "Số Toa Tàu"
            }
        ));
        jScrollPane1.setViewportView(jTabledanhSachTau);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonthemTauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonthemTauActionPerformed
        String tenTau = jTextFieldtenTau.getText().trim();
    String soToaTauStr = jTextFieldsoToaTau.getText().trim();

    if (!validateInput("dummy", tenTau, soToaTauStr)) return;

    int soToaTau;
    try {
        soToaTau = Integer.parseInt(soToaTauStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Số toa tàu phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        ConnectDB.getInstance().connect();
        Connection conn = ConnectDB.getConnection();

        String maTau = sinhMaTauTuDong();  // Tự sinh mã tàu

        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Tau (maTau, tenTau, soToaTau) VALUES (?, ?, ?)"
        );
        stmt.setString(1, maTau);
        stmt.setString(2, tenTau);
        stmt.setInt(3, soToaTau);

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Thêm tàu thành công! Mã tàu: " + maTau, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadTableData(null);
        } else {
            JOptionPane.showMessageDialog(this, "Thêm tàu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thêm tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButtonthemTauActionPerformed

    private void jButtoncapNhatTauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtoncapNhatTauActionPerformed
        int selectedRow = jTabledanhSachTau.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn tàu để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String maTau = (String) jTabledanhSachTau.getValueAt(selectedRow, 0);  // String thay vì int
    String tenTau = jTextFieldtenTau.getText().trim();
    String soToaTauStr = jTextFieldsoToaTau.getText().trim();

    if (!validateInput("dummy", tenTau, soToaTauStr)) return;

    int soToaTau;
    try {
        soToaTau = Integer.parseInt(soToaTauStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Số toa tàu phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        Connection conn = ConnectDB.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
            "UPDATE Tau SET tenTau = ?, soToaTau = ? WHERE maTau = ?");

        stmt.setString(1, tenTau);
        stmt.setInt(2, soToaTau);
        stmt.setString(3, maTau);

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Cập nhật tàu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadTableData(null);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tàu với mã " + maTau + "!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButtoncapNhatTauActionPerformed

    private void jButtonxoaTauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonxoaTauActionPerformed
        int selectedRow = jTabledanhSachTau.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn tàu để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String maTau = (String) jTabledanhSachTau.getValueAt(selectedRow, 0); // Lấy mã tàu kiểu String

    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tàu với mã " + maTau + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        // Đảm bảo kết nối luôn mở
        ConnectDB.getInstance().connect();
        Connection conn = ConnectDB.getConnection();

        // Kiểm tra xem tàu đã được sử dụng trong bảng ChuyenDi chưa
        String checkSQL = "SELECT COUNT(*) FROM ChuyenDi WHERE maTau = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
        checkStmt.setString(1, maTau);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(this, "Không thể xoá tàu vì đã được sử dụng trong chuyến đi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            checkStmt.close();
            return;
        }
        checkStmt.close();

        // Thực hiện xoá tàu
        String deleteSQL = "DELETE FROM Tau WHERE maTau = ?";
        PreparedStatement stmt = conn.prepareStatement(deleteSQL);
        stmt.setString(1, maTau);
        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Xóa tàu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadTableData(null);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tàu với mã " + maTau + "!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi xóa tàu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButtonxoaTauActionPerformed

    private void jButtontimTauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtontimTauActionPerformed
        String tenTau = jTextFieldtenTau.getText().trim();
        String searchCriteria = null;

        if (!tenTau.isEmpty()) {
            searchCriteria = "tenTau LIKE '%" + tenTau.replace("'", "''") + "%'";
        }

        loadTableData(searchCriteria);
        clearFields();
    }//GEN-LAST:event_jButtontimTauActionPerformed

    private void jTextFieldsoToaTauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldsoToaTauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldsoToaTauActionPerformed

    private void jTextFieldtenTauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldtenTauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldtenTauActionPerformed

    private void jTextFieldmaTauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldmaTauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldmaTauActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtoncapNhatTau;
    private javax.swing.JButton jButtonthemTau;
    private javax.swing.JButton jButtontimTau;
    private javax.swing.JButton jButtonxoaTau;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelmaTau;
    private javax.swing.JLabel jLabelsoToaTau;
    private javax.swing.JLabel jLabeltenTau;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabledanhSachTau;
    private javax.swing.JTextField jTextFieldmaTau;
    private javax.swing.JTextField jTextFieldsoToaTau;
    private javax.swing.JTextField jTextFieldtenTau;
    // End of variables declaration//GEN-END:variables
}
