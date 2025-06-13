/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.hoso;

import connectdb.ConnectDB;
import dao.QuanLyNhanVienDao;
import entity.NhanVien;
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
/**
 *
 * @author Admin Poi
 */
public class QuanLyNhanVien extends javax.swing.JPanel {
    private QuanLyNhanVienDao nhanVienDao;
    private DefaultTableModel tableModel;
    private NhanVien nhanVienHienTai;
    private boolean isAddingNew = false;
    private boolean isEditing = false;
    private String maNhanVienTam = null; // Biến lưu mã nhân viên tạm

    public QuanLyNhanVien(NhanVien nhanVien) {
        this.nhanVienHienTai = nhanVien;
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + e.getMessage());
        }
        
        initComponents();
        header1.jLabel14.setText("Quản Lý Nhân Viên");
        header1.jLabel13.setText("Chào Nhân Viên Quản Lý");
        header1.jButton4.setText(nhanVien.getTenNV());
        nhanVienDao = new QuanLyNhanVienDao();
        setupTable();
//        taiDuLieuLenBang();
        
        // Mặc định disable các trường nhập liệu và nút Lưu
        datTrangThaiNut(false);
        jButtonluu.setEnabled(false);
        
        // Mặc định enable các nút chức năng chính
        jButtonthemNhanVien.setEnabled(true);
        jButtonxoaNhanVien.setEnabled(true);
        jButtoncapNhatNhanVien.setEnabled(true);
        jButtonxoaTrang.setEnabled(true);
        jButtontimNhanVien.setEnabled(true);
        
        // Thiết lập ComboBox
        setupComboBoxes();
        
        // Kiểm tra vai trò để phân quyền
        if (nhanVienHienTai != null && !nhanVienHienTai.getVaiTro().equalsIgnoreCase("Nhân viên quản lý")) {
            // Nếu không phải quản lý thì disable các nút chức năng
            jButtonthemNhanVien.setEnabled(false);
            jButtonxoaNhanVien.setEnabled(false);
            jButtoncapNhatNhanVien.setEnabled(false);
            jButtonxoaTrang.setEnabled(false);
            jButtonluu.setEnabled(false);
        }
    }

    // Constructor mặc định (có thể cần cho một số trường hợp)
    public QuanLyNhanVien() {
        this(null); // Gọi constructor có tham số với giá trị null
    }

    private void setupTable() {
        String[] columnNames = {"Mã Nhân Viên", "Tên Nhân Viên", "Giới tính", "Ngày sinh", "Email", "Số điện thoại", "CCCD", "Ngày bắt đầu làm", "Vai Trò", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableQuanLyNhanVien.setModel(tableModel);
        
        // Thêm sự kiện khi click vào một dòng trong bảng
        jTableQuanLyNhanVien.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = jTableQuanLyNhanVien.getSelectedRow();
                    if (selectedRow >= 0) {
                        String maNV = (String) jTableQuanLyNhanVien.getValueAt(selectedRow, 0);
                        NhanVien nv = nhanVienDao.getNhanVienByMa(maNV);
                        if (nv != null) {
                            hienThiThongTinNhanVien(nv);
                        }
                    }
                }
            }
        });
    }

    private void setupComboBoxes() {
        // Thiết lập model cho ComboBox vai trò
        jComboBoxvaiTro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Nhân viên quản lý", "Nhân viên bán vé"}));
        
        // Thiết lập model cho ComboBox trạng thái
        jComboBoxtrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Đang làm", "Đã nghỉ làm"}));
    }

    private void hienThiThongTinNhanVien(NhanVien nv) {
        // Nếu đang trong quá trình thêm mới, giữ lại mã nhân viên tạm
        if (!isAddingNew) {
            jTextFieldmaNhanVien.setText(nv.getMaNV());
        }
        jTextFieldmaNhanVien.setEnabled(false);
        jTextFieldtenNhanVien.setText(nv.getTenNV());
        
        // Thay đổi phần hiển thị giới tính - true là Nữ, false là Nam
        boolean gioiTinh = nv.getGioiTinh();
        if (gioiTinh) {
            jRadioNu.setSelected(true);
        } else {
            jRadioNam.setSelected(true);
        }
        
        // Format ngày sinh theo định dạng dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        jTextFieldngaySinh.setText(nv.getNgaySinh().format(formatter));
        
        jTextFieldemail.setText(nv.getEmail());
        jTextFieldsoDienThoai.setText(nv.getSoDienThoai());
        jTextFieldCCCD.setText(nv.getCccd());
        jComboBoxvaiTro.setSelectedItem(nv.getVaiTro());
        jComboBoxtrangThai.setSelectedItem(nv.getTrangThai());
        
        // Hiển thị ngày bắt đầu làm việc
        jTextFieldngayBatDauLamViec.setText(nv.getNgayBatDauLamViec().format(formatter));
    }

    private void taiDuLieuLenBang() {
        try {
            // Đảm bảo kết nối được mở
            if (ConnectDB.getInstance().getConnection().isClosed()) {
                ConnectDB.getInstance().connect();
            }
            
            System.out.println("Đang tải dữ liệu lên bảng...");
            tableModel.setRowCount(0); // Xóa tất cả dữ liệu cũ
            List<NhanVien> dsNhanVien = nhanVienDao.getAllNhanVien();
            
            System.out.println("Tìm thấy " + dsNhanVien.size() + " nhân viên");
            
            for (NhanVien nv : dsNhanVien) {
                Object[] row = {
                    nv.getMaNV(),
                    nv.getTenNV(),
                    nv.getGioiTinh() ? "Nữ" : "Nam",
                    nv.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    nv.getEmail(),
                    nv.getSoDienThoai(),
                    nv.getCccd(),
                    nv.getNgayBatDauLamViec().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    nv.getVaiTro(),
                    nv.getTrangThai()
                };
                tableModel.addRow(row);
                System.out.println("Đã thêm dòng: " + nv.getMaNV() + " - " + nv.getTenNV());
            }
            
            if (tableModel.getRowCount() > 0) {
                jTableQuanLyNhanVien.setRowSelectionInterval(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void xoaTrangForm() {
        // Xử lý mã nhân viên dựa trên trạng thái
        if (isAddingNew && maNhanVienTam != null) {
            // Nếu đang thêm mới, giữ lại mã nhân viên tạm
            System.out.println("Giữ lại mã nhân viên tạm: " + maNhanVienTam);
            jTextFieldmaNhanVien.setText(maNhanVienTam);
        } else {
            // Nếu không phải thêm mới hoặc không có mã tạm, xóa trắng
            jTextFieldmaNhanVien.setText("");
        }
        
        // Xóa trắng các trường còn lại
        jTextFieldtenNhanVien.setText("");
        jRadioNam.setSelected(true);
        jTextFieldngaySinh.setText("");
        jTextFieldemail.setText("");
        jTextFieldsoDienThoai.setText("");
        jTextFieldCCCD.setText("");
        jComboBoxvaiTro.setSelectedIndex(0);
        jComboBoxtrangThai.setSelectedIndex(0);
        jTextFieldngayBatDauLamViec.setText("");
        
        // Đảm bảo mã nhân viên không được chỉnh sửa
        jTextFieldmaNhanVien.setEditable(false);
    }

    private void datTrangThaiNut(boolean choPhepChinhSua) {
        jTextFieldtenNhanVien.setEditable(choPhepChinhSua);
        jRadioNam.setEnabled(choPhepChinhSua);
        jRadioNu.setEnabled(choPhepChinhSua);
        jTextFieldngaySinh.setEditable(choPhepChinhSua);
        jTextFieldemail.setEditable(choPhepChinhSua);
        jTextFieldsoDienThoai.setEditable(choPhepChinhSua);
        jTextFieldCCCD.setEditable(choPhepChinhSua);
        jComboBoxvaiTro.setEnabled(choPhepChinhSua);
        jComboBoxtrangThai.setEnabled(choPhepChinhSua);
        jTextFieldmaNhanVien.setEditable(false);
        jTextFieldngayBatDauLamViec.setEditable(choPhepChinhSua);
    }

    private void datLaiTrangThaiNut() {
        isAddingNew = false;
        isEditing = false;
        datTrangThaiNut(false);
        
        // Reset trạng thái các nút về mặc định
        if (nhanVienHienTai != null && nhanVienHienTai.getVaiTro().equalsIgnoreCase("Nhân viên quản lý")) {
            jButtonthemNhanVien.setEnabled(true);
            jButtonxoaNhanVien.setEnabled(true);
            jButtoncapNhatNhanVien.setEnabled(true);
            jButtonxoaTrang.setEnabled(true);
        }
        jButtontimNhanVien.setEnabled(true);
        jButtonluu.setEnabled(false);
        jButtonHuy.setEnabled(true);
    }

    private NhanVien layThongTinNhanVienTuForm() throws Exception {
        NhanVien nv = new NhanVien();
        
        // Validate tên nhân viên
        String tenNV = jTextFieldtenNhanVien.getText().trim();
        if (tenNV.isEmpty()) {
            jTextFieldtenNhanVien.requestFocus();
            throw new Exception("Tên nhân viên không được để trống");
        }
        if (!tenNV.matches("^[\\p{L}\\s]+$")) {
            jTextFieldtenNhanVien.requestFocus();
            throw new Exception("Tên nhân viên chỉ được chứa chữ cái và khoảng trắng");
        }
        nv.setTenNV(tenNV);
        
        // Validate giới tính
        nv.setGioiTinh(jRadioNu.isSelected());
        
        // Validate ngày sinh
        String ngaySinhStr = jTextFieldngaySinh.getText().trim();
        if (ngaySinhStr.isEmpty()) {
            jTextFieldngaySinh.requestFocus();
            throw new Exception("Ngày sinh không được để trống");
        }
        try {
            String[] ngaySinhParts = ngaySinhStr.split("/");
            if (ngaySinhParts.length != 3) {
                jTextFieldngaySinh.requestFocus();
                throw new Exception("Ngày sinh phải có định dạng dd/MM/yyyy");
            }
            int ngay = Integer.parseInt(ngaySinhParts[0]);
            int thang = Integer.parseInt(ngaySinhParts[1]);
            int nam = Integer.parseInt(ngaySinhParts[2]);
            
            LocalDate ngaySinh = LocalDate.of(nam, thang, ngay);
            if (ngaySinh.isAfter(LocalDate.now())) {
                jTextFieldngaySinh.requestFocus();
                throw new Exception("Ngày sinh không thể là ngày trong tương lai");
            }
            if (ngaySinh.plusYears(18).isAfter(LocalDate.now())) {
                jTextFieldngaySinh.requestFocus();
                throw new Exception("Nhân viên phải đủ 18 tuổi");
            }
            nv.setNgaySinh(ngaySinh);
        } catch (DateTimeException e) {
            jTextFieldngaySinh.requestFocus();
            throw new Exception("Ngày sinh không hợp lệ: " + e.getMessage());
        } catch (NumberFormatException e) {
            jTextFieldngaySinh.requestFocus();
            throw new Exception("Ngày sinh phải có định dạng dd/MM/yyyy");
        }
        
        // Validate email
        String email = jTextFieldemail.getText().trim();
        if (email.isEmpty()) {
            jTextFieldemail.requestFocus();
            throw new Exception("Email không được để trống");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            jTextFieldemail.requestFocus();
            throw new Exception("Email không hợp lệ (ví dụ: example@domain.com)");
        }
        nv.setEmail(email);
        
        // Validate số điện thoại
        String sdt = jTextFieldsoDienThoai.getText().trim();
        if (sdt.isEmpty()) {
            jTextFieldsoDienThoai.requestFocus();
            throw new Exception("Số điện thoại không được để trống");
        }
        if (!sdt.matches("^0\\d{9}$")) {
            jTextFieldsoDienThoai.requestFocus();
            throw new Exception("Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số");
        }
        nv.setSoDienThoai(sdt);
        
        // Validate CCCD
        String cccd = jTextFieldCCCD.getText().trim();
        if (cccd.isEmpty()) {
            jTextFieldCCCD.requestFocus();
            throw new Exception("CCCD không được để trống");
        }
        if (!cccd.matches("\\d{12}")) {
            jTextFieldCCCD.requestFocus();
            throw new Exception("CCCD phải có đúng 12 chữ số");
        }
        nv.setCccd(cccd);
        
        // Lấy vai trò và trạng thái từ ComboBox
        nv.setVaiTro((String) jComboBoxvaiTro.getSelectedItem());
        nv.setTrangThai((String) jComboBoxtrangThai.getSelectedItem());
        
        // Validate ngày bắt đầu làm việc
        String ngayBDLVStr = jTextFieldngayBatDauLamViec.getText().trim();
        if (ngayBDLVStr.isEmpty()) {
            jTextFieldngayBatDauLamViec.requestFocus();
            throw new Exception("Ngày bắt đầu làm việc không được để trống");
        }
        try {
            String[] ngayBDLVParts = ngayBDLVStr.split("/");
            if (ngayBDLVParts.length != 3) {
                jTextFieldngayBatDauLamViec.requestFocus();
                throw new Exception("Ngày bắt đầu làm việc phải có định dạng dd/MM/yyyy");
            }
            int ngay = Integer.parseInt(ngayBDLVParts[0]);
            int thang = Integer.parseInt(ngayBDLVParts[1]);
            int nam = Integer.parseInt(ngayBDLVParts[2]);
            
            LocalDate ngayBDLV = LocalDate.of(nam, thang, ngay);
            if (ngayBDLV.isBefore(nv.getNgaySinh().plusYears(18))) {
                jTextFieldngayBatDauLamViec.requestFocus();
                throw new Exception("Ngày bắt đầu làm việc phải sau ngày đủ 18 tuổi");
            }
            if (ngayBDLV.isAfter(LocalDate.now().minusDays(1))) {
                jTextFieldngayBatDauLamViec.requestFocus();
                throw new Exception("Ngày bắt đầu làm việc không thể sau ngày hôm qua");
            }
            nv.setNgayBatDauLamViec(ngayBDLV);
        } catch (DateTimeException e) {
            jTextFieldngayBatDauLamViec.requestFocus();
            throw new Exception("Ngày bắt đầu làm việc không hợp lệ: " + e.getMessage());
        } catch (NumberFormatException e) {
            jTextFieldngayBatDauLamViec.requestFocus();
            throw new Exception("Ngày bắt đầu làm việc phải có định dạng dd/MM/yyyy");
        }
        
        return nv;
    }

    private String taoMaNhanVien(NhanVien nv) throws Exception {
        // Đảm bảo kết nối được mở
        if (ConnectDB.getInstance().getConnection().isClosed()) {
            ConnectDB.getInstance().connect();
        }
        
        StringBuilder maNV = new StringBuilder("NV-");
        
        // X: 0 nếu nam, 1 nếu nữ
        boolean gioiTinh = jRadioNu.isSelected(); // Lấy giới tính từ RadioButton
        maNV.append(gioiTinh ? "1" : "0").append("-");
        
        // YY: 2 số cuối của năm hiện tại
        LocalDate now = LocalDate.now();
        String namHienTai = String.valueOf(now.getYear());
        maNV.append(namHienTai.substring(namHienTai.length() - 2)).append("-");
        
        // ZZZ: 3 số cuối của CCCD (tạm thời là 000 vì chưa có CCCD)
        maNV.append("000").append("-");
        
        // OOO: số thứ tự của nhân viên
        int soThuTu = nhanVienDao.laySoThuTuNhanVienMoi();
        String maNVFinal = maNV.toString() + String.format("%03d", soThuTu);
        
        System.out.println("Đã tạo mã nhân viên mới: " + maNVFinal);
        return maNVFinal;
    }

    // Event handlers
    private void jButtonxoaTrangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonxoaTrangActionPerformed
        xoaTrangForm();
        if (isAddingNew || isEditing) {
            datLaiTrangThaiNut();
        }
    }//GEN-LAST:event_jButtonxoaTrangActionPerformed

    private void jButtonthemNhanVienActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Đảm bảo kết nối được mở
            if (ConnectDB.getInstance().getConnection().isClosed()) {
                ConnectDB.getInstance().connect();
            }
            
            // Thiết lập trạng thái thêm mới
            isAddingNew = true;
            isEditing = false;
            
            // Xóa trắng form trước khi tạo mã mới
            xoaTrangForm();
            
            // Tạo mã nhân viên tạm
            NhanVien nvTemp = new NhanVien();
            nvTemp.setGioiTinh(jRadioNu.isSelected()); // Lấy giới tính từ RadioButton
            nvTemp.setNgaySinh(LocalDate.now());
            nvTemp.setCccd("000000000000");
            
            // Tạo và lưu mã nhân viên tạm
            maNhanVienTam = taoMaNhanVien(nvTemp);
            System.out.println("Đã tạo mã nhân viên tạm: " + maNhanVienTam);
            
            // Hiển thị mã nhân viên tạm lên form ngay lập tức
            jTextFieldmaNhanVien.setText("");
            jTextFieldmaNhanVien.setEditable(false);
            
            // Thiết lập các giá trị mặc định khác
            LocalDate ngayBatDau = LocalDate.now().minusDays(1);
            jTextFieldngayBatDauLamViec.setText(ngayBatDau.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            jComboBoxtrangThai.setSelectedItem("Đang làm");
            
            // Cập nhật trạng thái các nút và form
            datTrangThaiNut(true);
            datTrangThaiNutChucNang(false);
            
            // Đảm bảo focus vào trường nhập liệu đầu tiên
            jTextFieldtenNhanVien.requestFocus();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo mã nhân viên: " + e.getMessage());
            datLaiTrangThaiNut();
            isAddingNew = false;
            maNhanVienTam = null;
        }
    }

    private void jButtoncapNhatNhanVienActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTableQuanLyNhanVien.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật!");
            return;
        }

        String maNV = (String) jTableQuanLyNhanVien.getValueAt(selectedRow, 0);
        
        // Kiểm tra xem có phải đang cập nhật chính mình không
        if (maNV.equals(nhanVienHienTai.getMaNV())) {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật thông tin của chính mình!");
            return;
        }

        isEditing = true;
        isAddingNew = false;
        datTrangThaiNut(true);
        
        // Hiển thị thông tin của nhân viên được chọn
        NhanVien nv = nhanVienDao.getNhanVienByMa(maNV);
        if (nv != null) {
            hienThiThongTinNhanVien(nv);
        }
        
        // Cập nhật trạng thái các nút
        datTrangThaiNutChucNang(false);
    }

    private void jButtonxoaNhanVienActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int selectedRow = jTableQuanLyNhanVien.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }

            String maNV = (String) jTableQuanLyNhanVien.getValueAt(selectedRow, 0);
            
            // Kiểm tra xem có phải đang xóa chính mình không
            if (maNV.equals(nhanVienHienTai.getMaNV())) {
                JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản của chính mình!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân viên này?\nThao tác này sẽ xóa cả tài khoản của nhân viên.",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Connection con = null;
                PreparedStatement stmtTaiKhoan = null;
                PreparedStatement stmtNhanVien = null;
                
                try {
                    con = ConnectDB.getInstance().getConnection();
                    con.setAutoCommit(false); // Bắt đầu transaction
                    
                    // Xóa tài khoản trước
                    String sqlTaiKhoan = "DELETE FROM TaiKhoan WHERE maNV = ?";
                    stmtTaiKhoan = con.prepareStatement(sqlTaiKhoan);
                    stmtTaiKhoan.setString(1, maNV);
                    stmtTaiKhoan.executeUpdate();
                    
                    // Sau đó xóa nhân viên
                    if (nhanVienDao.xoaNhanVien(maNV)) {
                        con.commit(); // Commit transaction nếu thành công
                        JOptionPane.showMessageDialog(this, "Xóa nhân viên và tài khoản thành công!");
                        
                        // Tải lại dữ liệu và cập nhật giao diện
                        taiLaiDuLieuVaCapNhatGiaoDien();
                    } else {
                        con.rollback(); // Rollback nếu xóa nhân viên thất bại
                        JOptionPane.showMessageDialog(this, "Xóa nhân viên thất bại!");
                    }
                } catch (SQLException e) {
                    if (con != null) {
                        try {
                            con.rollback(); // Rollback nếu có lỗi
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
                } finally {
                    try {
                        if (stmtTaiKhoan != null) stmtTaiKhoan.close();
                        if (stmtNhanVien != null) stmtNhanVien.close();
                        if (con != null) {
                            con.setAutoCommit(true);
                            con.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void jButtontimNhanVienActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Đảm bảo kết nối được mở
            if (ConnectDB.getInstance().getConnection().isClosed()) {
                ConnectDB.getInstance().connect();
            }
            
            String keyword = JOptionPane.showInputDialog(this, "Nhập từ khóa tìm kiếm:");
            if (keyword != null && !keyword.trim().isEmpty()) {
                List<NhanVien> ketQua = nhanVienDao.timKiemNhanVien(keyword);
                tableModel.setRowCount(0);
                
                for (NhanVien nv : ketQua) {
                    Object[] row = {
                        nv.getMaNV(),
                        nv.getTenNV(),
                        nv.getGioiTinh() ? "Nữ" : "Nam",
                        nv.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        nv.getEmail(),
                        nv.getSoDienThoai(),
                        nv.getCccd(),
                        nv.getNgayBatDauLamViec().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        nv.getVaiTro(),
                        nv.getTrangThai()
                    };
                    tableModel.addRow(row);
                }
                
                if (ketQua.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên nào!");
                    // Tải lại toàn bộ dữ liệu nếu không tìm thấy kết quả
                    taiLaiDuLieuVaCapNhatGiaoDien();
                } else {
                    // Chọn dòng đầu tiên trong kết quả tìm kiếm
                    jTableQuanLyNhanVien.setRowSelectionInterval(0, 0);
                    String maNV = (String) jTableQuanLyNhanVien.getValueAt(0, 0);
                    NhanVien nv = nhanVienDao.getNhanVienByMa(maNV);
                    if (nv != null) {
                        hienThiThongTinNhanVien(nv);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
            try {
                ConnectDB.getInstance().connect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void jButtonHuyActionPerformed(java.awt.event.ActionEvent evt) {
        // Reset các biến trạng thái
        isAddingNew = false;
        isEditing = false;
        maNhanVienTam = null;
        
        // Tải lại dữ liệu và cập nhật giao diện
        taiLaiDuLieuVaCapNhatGiaoDien();
    }

    private void jButtonluuActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Đảm bảo kết nối được mở
            if (ConnectDB.getInstance().getConnection().isClosed()) {
                ConnectDB.getInstance().connect();
            }
            
            if (isAddingNew) {
                // Lấy thông tin từ form
                NhanVien nv = layThongTinNhanVienTuForm();
                
                // Cập nhật mã nhân viên với CCCD mới
                String cccd = nv.getCccd();
                if (cccd != null && cccd.length() >= 3) {
                    String maNVMoi = maNhanVienTam.substring(0, maNhanVienTam.indexOf("-", 6) + 1) + 
                                   cccd.substring(cccd.length() - 3) + "-" +
                                   maNhanVienTam.substring(maNhanVienTam.lastIndexOf("-") + 1);
                    nv.setMaNV(maNVMoi);
                } else {
                    nv.setMaNV(maNhanVienTam);
                }
                
                if (nhanVienDao.themNhanVien(nv)) {
                    // Tạo tài khoản cho nhân viên mới
                    if (taoTaiKhoanNhanVien(nv)) {
                        JOptionPane.showMessageDialog(this, 
                            "Thêm nhân viên thành công!\n" +
                            "Mã nhân viên: " + nv.getMaNV() + "\n" +
                            "Tài khoản đã được tạo:\n" +
                            "- Tên đăng nhập: " + nv.getSoDienThoai() + "\n" +
                            "- Mật khẩu mặc định: 1111");
                        
                        // Tải lại dữ liệu và cập nhật giao diện
                        taiLaiDuLieuVaCapNhatGiaoDien();
                    } else {
                        // Nếu không tạo được tài khoản, xóa nhân viên đã thêm
                        nhanVienDao.xoaNhanVien(nv.getMaNV());
                        JOptionPane.showMessageDialog(this, "Không thể tạo tài khoản. Đã hủy thêm nhân viên!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!");
                }
            } else if (isEditing) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn cập nhật thông tin nhân viên này?",
                    "Xác nhận cập nhật",
                    JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    NhanVien nv = layThongTinNhanVienTuForm();
                    nv.setMaNV(jTextFieldmaNhanVien.getText());

                    if (nhanVienDao.capNhatNhanVien(nv)) {
                        JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
                        taiLaiDuLieuVaCapNhatGiaoDien();
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            try {
                ConnectDB.getInstance().connect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean taoTaiKhoanNhanVien(NhanVien nv) {
        Connection con = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        
        try {
            // Kết nối database
            con = ConnectDB.getInstance().getConnection();
            
            // Kiểm tra xem tài khoản đã tồn tại chưa
            String checkSql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ? OR maNV = ?";
            checkStmt = con.prepareStatement(checkSql);
            checkStmt.setString(1, nv.getSoDienThoai());
            checkStmt.setString(2, nv.getMaNV());
            rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Tài khoản đã tồn tại
                JOptionPane.showMessageDialog(this, "Tài khoản hoặc mã nhân viên đã tồn tại trong hệ thống!");
                return false;
            }
            
            // Tạo mật khẩu hash với BCrypt
            String matKhauMacDinh = "1111";
            String matKhauHash = BCrypt.hashpw(matKhauMacDinh, BCrypt.gensalt(10));
            
            // Tạo tài khoản mới
            String insertSql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNV) VALUES (?, ?, ?)";
            insertStmt = con.prepareStatement(insertSql);
            insertStmt.setString(1, nv.getSoDienThoai());
            insertStmt.setString(2, matKhauHash);
            insertStmt.setString(3, nv.getMaNV());
            
            int result = insertStmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo tài khoản: " + e.getMessage());
            return false;
        } finally {
            // Đóng các resource
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (insertStmt != null) insertStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void datTrangThaiNutChucNang(boolean choPhepThaoTac) {
        // Các nút chức năng chính
        jButtonthemNhanVien.setEnabled(choPhepThaoTac);
        jButtonxoaNhanVien.setEnabled(choPhepThaoTac);
        jButtoncapNhatNhanVien.setEnabled(choPhepThaoTac);
        jButtontimNhanVien.setEnabled(choPhepThaoTac);
        
        // Các nút thao tác
        jButtonluu.setEnabled(!choPhepThaoTac);
        jButtonxoaTrang.setEnabled(true); // Luôn cho phép xóa trắng
        jButtonHuy.setEnabled(true); // Luôn cho phép hủy
    }

    private void taiLaiDuLieuVaCapNhatGiaoDien() {
        try {
            // Đảm bảo kết nối được mở
            if (ConnectDB.getInstance().getConnection().isClosed()) {
                ConnectDB.getInstance().connect();
            }
            
            // Tải lại dữ liệu
//            taiDuLieuLenBang();
            
            // Chọn dòng đầu tiên nếu có dữ liệu
            if (jTableQuanLyNhanVien.getRowCount() > 0) {
                jTableQuanLyNhanVien.setRowSelectionInterval(0, 0);
                String maNV = (String) jTableQuanLyNhanVien.getValueAt(0, 0);
                NhanVien nv = nhanVienDao.getNhanVienByMa(maNV);
                if (nv != null) {
                    hienThiThongTinNhanVien(nv);
                }
            } else {
                xoaTrangForm();
            }
            
            // Reset trạng thái
            isAddingNew = false;
            isEditing = false;
            maNhanVienTam = null;
            
            // Cập nhật trạng thái các nút
            datTrangThaiNutChucNang(true);
            datTrangThaiNut(false);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lại dữ liệu: " + e.getMessage());
            try {
                // Thử kết nối lại nếu có lỗi
                ConnectDB.getInstance().connect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupGioiTinh = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jButtonthemNhanVien = new javax.swing.JButton();
        jButtoncapNhatNhanVien = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabelmaNhanVien = new javax.swing.JLabel();
        jTextFieldmaNhanVien = new javax.swing.JTextField();
        jLabelgioiTinh = new javax.swing.JLabel();
        jLabelngaySinh = new javax.swing.JLabel();
        jTextFieldngaySinh = new javax.swing.JTextField();
        jLabelngayBatDauLamViec = new javax.swing.JLabel();
        jTextFieldngayBatDauLamViec = new javax.swing.JTextField();
        jLabelemail = new javax.swing.JLabel();
        jTextFieldemail = new javax.swing.JTextField();
        jLabelsoDienThoai = new javax.swing.JLabel();
        jTextFieldsoDienThoai = new javax.swing.JTextField();
        jLabeltrangThai = new javax.swing.JLabel();
        jLabeltenNhanVien = new javax.swing.JLabel();
        jTextFieldtenNhanVien = new javax.swing.JTextField();
        jTextFieldCCCD = new javax.swing.JTextField();
        jLabelCCCD = new javax.swing.JLabel();
        jLabelvaiTro = new javax.swing.JLabel();
        jComboBoxvaiTro = new javax.swing.JComboBox<>();
        jComboBoxtrangThai = new javax.swing.JComboBox<>();
        jRadioNam = new javax.swing.JRadioButton();
        jRadioNu = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableQuanLyNhanVien = new javax.swing.JTable();
        jButtonxoaNhanVien = new javax.swing.JButton();
        jButtontimNhanVien = new javax.swing.JButton();
        jButtonxoaTrang = new javax.swing.JButton();
        jButtonluu = new javax.swing.JButton();
        jButtonHuy = new javax.swing.JButton();
        header1 = new component.Header();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButtonthemNhanVien.setBackground(new java.awt.Color(65, 165, 238));
        jButtonthemNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonthemNhanVien.setForeground(new java.awt.Color(255, 255, 255));
        jButtonthemNhanVien.setText("Thêm Nhân Viên");
        jButtonthemNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonthemNhanVienActionPerformed(evt);
            }
        });

        jButtoncapNhatNhanVien.setBackground(new java.awt.Color(65, 165, 238));
        jButtoncapNhatNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtoncapNhatNhanVien.setForeground(new java.awt.Color(255, 255, 255));
        jButtoncapNhatNhanVien.setText("Cập nhật nhân viên");
        jButtoncapNhatNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtoncapNhatNhanVienActionPerformed(evt);
            }
        });

        jLabelmaNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelmaNhanVien.setForeground(new java.awt.Color(13, 98, 255));
        jLabelmaNhanVien.setText("Mã Nhân Viên :");

        jLabelgioiTinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelgioiTinh.setForeground(new java.awt.Color(13, 98, 255));
        jLabelgioiTinh.setText("Giới tính :");

        jLabelngaySinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelngaySinh.setForeground(new java.awt.Color(13, 98, 255));
        jLabelngaySinh.setText("Ngày sinh :");

        jLabelngayBatDauLamViec.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelngayBatDauLamViec.setForeground(new java.awt.Color(13, 98, 255));
        jLabelngayBatDauLamViec.setText("Ngày bắt đầu làm :");

        jLabelemail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelemail.setForeground(new java.awt.Color(13, 98, 255));
        jLabelemail.setText("Email :");

        jLabelsoDienThoai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelsoDienThoai.setForeground(new java.awt.Color(13, 98, 255));
        jLabelsoDienThoai.setText("Số điện thoại:");

        jLabeltrangThai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeltrangThai.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltrangThai.setText("Trạng Thái :");

        jLabeltenNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabeltenNhanVien.setForeground(new java.awt.Color(13, 98, 255));
        jLabeltenNhanVien.setText("Tên Nhân Viên :");

        jLabelCCCD.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelCCCD.setForeground(new java.awt.Color(13, 98, 255));
        jLabelCCCD.setText("CCCD :");

        jLabelvaiTro.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelvaiTro.setForeground(new java.awt.Color(13, 98, 255));
        jLabelvaiTro.setText("Vai Trò :");

        jComboBoxvaiTro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nhân viên quản lý", "Nhân viên bán vé" }));

        jComboBoxtrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đang làm", "Đã nghỉ làm" }));

        buttonGroupGioiTinh.add(jRadioNam);
        jRadioNam.setText("Nam");

        buttonGroupGioiTinh.add(jRadioNu);
        jRadioNu.setText("Nữ");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelngaySinh, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                            .addComponent(jLabelmaNhanVien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelemail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelgioiTinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextFieldngaySinh, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                                .addComponent(jTextFieldemail)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                    .addComponent(jRadioNam)
                                    .addGap(26, 26, 26)
                                    .addComponent(jRadioNu)))
                            .addComponent(jTextFieldmaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabelsoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldsoDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)))
                .addGap(48, 48, 48)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabeltenNhanVien)
                            .addComponent(jLabelCCCD))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldCCCD, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                            .addComponent(jTextFieldtenNhanVien)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabeltrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelngayBatDauLamViec, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jLabelvaiTro)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldngayBatDauLamViec, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jComboBoxtrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBoxvaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabeltenNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldtenNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabelmaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldmaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelgioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioNam)
                            .addComponent(jRadioNu)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldCCCD, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCCCD, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(12, 12, 12)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelngayBatDauLamViec, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldngayBatDauLamViec, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldngaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelngaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldemail, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                            .addComponent(jLabelemail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelvaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxvaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxtrangThai)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelsoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldsoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabeltrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 5, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTableQuanLyNhanVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Nhân Viên", "Tên Nhân Viên", "Giới tính", "Ngày sinh", "Email", "Số điện thoại", "CCCD", "Ngày bắt đầu làm", "Vai Trò", "Trạng thái"
            }
        ));
        jScrollPane1.setViewportView(jTableQuanLyNhanVien);

        jButtonxoaNhanVien.setBackground(new java.awt.Color(65, 165, 238));
        jButtonxoaNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonxoaNhanVien.setForeground(new java.awt.Color(255, 255, 255));
        jButtonxoaNhanVien.setText("Xóa Nhân Viên");
        jButtonxoaNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonxoaNhanVienActionPerformed(evt);
            }
        });

        jButtontimNhanVien.setBackground(new java.awt.Color(65, 165, 238));
        jButtontimNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtontimNhanVien.setForeground(new java.awt.Color(255, 255, 255));
        jButtontimNhanVien.setText("Tìm Nhân Viên");
        jButtontimNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtontimNhanVienActionPerformed(evt);
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
                                .addComponent(jButtontimNhanVien)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonthemNhanVien)
                                .addGap(11, 11, 11)
                                .addComponent(jButtonxoaNhanVien)
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
                    .addComponent(jButtonthemNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonxoaNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtoncapNhatNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtontimNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonxoaTrang, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonluu, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
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
    private javax.swing.JButton jButtonthemNhanVien;
    private javax.swing.JButton jButtontimNhanVien;
    private javax.swing.JButton jButtonxoaNhanVien;
    private javax.swing.JButton jButtonxoaTrang;
    private javax.swing.JComboBox<String> jComboBoxtrangThai;
    private javax.swing.JComboBox<String> jComboBoxvaiTro;
    private javax.swing.JLabel jLabelCCCD;
    private javax.swing.JLabel jLabelemail;
    private javax.swing.JLabel jLabelgioiTinh;
    private javax.swing.JLabel jLabelmaNhanVien;
    private javax.swing.JLabel jLabelngayBatDauLamViec;
    private javax.swing.JLabel jLabelngaySinh;
    private javax.swing.JLabel jLabelsoDienThoai;
    private javax.swing.JLabel jLabeltenNhanVien;
    private javax.swing.JLabel jLabeltrangThai;
    private javax.swing.JLabel jLabelvaiTro;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioNam;
    private javax.swing.JRadioButton jRadioNu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableQuanLyNhanVien;
    private javax.swing.JTextField jTextFieldCCCD;
    private javax.swing.JTextField jTextFieldemail;
    private javax.swing.JTextField jTextFieldmaNhanVien;
    private javax.swing.JTextField jTextFieldngayBatDauLamViec;
    private javax.swing.JTextField jTextFieldngaySinh;
    private javax.swing.JTextField jTextFieldsoDienThoai;
    private javax.swing.JTextField jTextFieldtenNhanVien;
    // End of variables declaration//GEN-END:variables
}
