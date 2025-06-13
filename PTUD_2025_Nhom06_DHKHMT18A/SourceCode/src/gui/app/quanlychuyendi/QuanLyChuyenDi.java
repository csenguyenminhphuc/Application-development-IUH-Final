/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.quanlychuyendi;

import entity.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.toedter.calendar.JDateChooser;
import connectdb.ConnectDB;
import dao.ChuyenDi_DAO;
import dao.ThoiGianDiChuyen_DAO;
import entity.ChuyenDi;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 *
 * @author HAI TAM
 */
public class QuanLyChuyenDi extends javax.swing.JPanel {
    private String selectedMaThoiGianDiChuyen;
    private NhanVien nhanVien;
    private Date thoiGianKhoiHanh;
    private Map<String, String> gaMap = new HashMap<>();
    private Map<String, String> tauMap = new HashMap<>();
    private Map<String, String> loaiGheMap = new HashMap<>();
//    private HashMap<String, String> loaiGheMap = new HashMap<>();
    private String maChuyenDi = null;
    /**
     * Creates new form QLChuyenDi
     */
    public QuanLyChuyenDi(NhanVien nv) {
        this.nhanVien = nv;
        initComponents();
        header1.jLabel14.setText("Quản Lý Chuyến Đi");
        header1.jLabel13.setText("Chào Nhân Viên Quản Lý");
        header1.jButton4.setText(nv.getTenNV());
        loadComboBoxData(); // Tải dữ liệu cho các JComboBox
        loadChuyenDiTable(); // Tải dữ liệu bảng chuyến đi
        addTableSelectionListener(); // Thêm sự kiện chọn hàng
        jTextFieldmaChuyenDi.setEditable(false); // Khóa mã chuyến đi
        jTextFieldmaChuyenDi.setEnabled(false);
        jTextFieldthoiGianKhoiHanh.setEditable(false); // Khóa thời gian khởi hành
        jTextFieldthoiGianKhoiHanh.setEnabled(false);
        jTextFieldthoiGianDuTinh.setEditable(false); // Khóa thời gian dự tính
        jTextFieldthoiGianDuTinh.setEnabled(false);
        jComboBoxloaiGhe.setEditable(false); // Khóa loại ghế
        jComboBoxloaiGhe.setEnabled(false);
//        header1.jLabel14.setText("Quản lý chuyến đi");
//        header1.jButton4.setText(nv.getTenNV());
    }

    // Tải dữ liệu cho các JComboBox
    private void loadComboBoxData() {
        ConnectDB.getInstance().connect();
        try  {
            // Load ga
            Connection conn = ConnectDB.getConnection();
            String sqlGa = "SELECT maGa, tenGa FROM Ga";
            try (PreparedStatement stmt = conn.prepareStatement(sqlGa);
                 ResultSet rs = stmt.executeQuery()) {

                DefaultComboBoxModel<String> modelGaDi = new DefaultComboBoxModel<>();
                DefaultComboBoxModel<String> modelGaDen = new DefaultComboBoxModel<>();

                gaMap.clear();

                while (rs.next()) {
                    String maGa = rs.getString("maGa");
                    String tenGa = rs.getString("tenGa");

                    modelGaDi.addElement(tenGa);
                    modelGaDen.addElement(tenGa);

                    gaMap.put(tenGa, maGa);
                }

                jComboBoxgaDi.setModel(modelGaDi);
                jComboBoxgaDen.setModel(modelGaDen);
            }

            // Load tau
            String sqlTau = "SELECT maTau, tenTau FROM Tau ORDER BY tenTau";
            try (PreparedStatement stmtTau = conn.prepareStatement(sqlTau);
                 ResultSet rsTau = stmtTau.executeQuery()) {

                DefaultComboBoxModel<String> modelTau = new DefaultComboBoxModel<>();
                tauMap.clear();

                while (rsTau.next()) {
                    String maTau = rsTau.getString("maTau");
                    String tenTau = rsTau.getString("tenTau");

                    tauMap.put(tenTau, maTau);
                    modelTau.addElement(tenTau);
                }

                jComboBoxTau.setModel(modelTau);
            }

            // Load loai ghe
            String sqlLoaiGhe = "SELECT maLoaiGhe, tenLoaiGhe FROM LoaiGhe ORDER BY tenLoaiGhe";
            try (PreparedStatement stmtLoaiGhe = conn.prepareStatement(sqlLoaiGhe);
                 ResultSet rsLoaiGhe = stmtLoaiGhe.executeQuery()) {

                DefaultComboBoxModel<String> modelLoaiGhe = new DefaultComboBoxModel<>();
                loaiGheMap.clear(); // Bạn cần khai báo HashMap<String,String> loaiGheMap = new HashMap<>();

                while (rsLoaiGhe.next()) {
                    String maLoaiGhe = rsLoaiGhe.getString("maLoaiGhe");
                    String tenLoaiGhe = rsLoaiGhe.getString("tenLoaiGhe");

                    modelLoaiGhe.addElement(tenLoaiGhe);
                    loaiGheMap.put(tenLoaiGhe, maLoaiGhe);
                }

                jComboBoxloaiGhe.setModel(modelLoaiGhe);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Tính thời gian dự tính dựa trên thời gian khởi hành và thời gian di chuyển
    private void calculateThoiGianDuTinh() {
        // Lấy tên ga đi và ga đến
        String tenGaDi = jComboBoxgaDi.getSelectedItem() != null ? jComboBoxgaDi.getSelectedItem().toString() : "";
        String tenGaDen = jComboBoxgaDen.getSelectedItem() != null ? jComboBoxgaDen.getSelectedItem().toString() : "";

        // Lấy mã ga qua map
        String maGaDi = gaMap.getOrDefault(tenGaDi, "");
        String maGaDen = gaMap.getOrDefault(tenGaDen, "");

        // Kiểm tra đầu vào
        if (maGaDi.isEmpty()) {
            jTextFieldthoiGianDuTinh.setText("Vui lòng chọn ga đi!");
            return;
        }
        if (maGaDen.isEmpty()) {
            jTextFieldthoiGianDuTinh.setText("Vui lòng chọn ga đến!");
            return;
        }
        if (maGaDi.equals(maGaDen)) {
            jTextFieldthoiGianDuTinh.setText("Ga đi và ga đến không được trùng!");
            return;
        }

        // Lấy thời gian khởi hành từ textfield (hoặc biến toàn cục)
        Date thoiGianKhoiHanh = null;
        String thoiGianKhoiHanhStr = jTextFieldthoiGianKhoiHanh.getText().trim();
        if (thoiGianKhoiHanhStr.isEmpty()) {
            jTextFieldthoiGianDuTinh.setText("Vui lòng chọn thời gian khởi hành!");
            return;
        }
        try {
            thoiGianKhoiHanh = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(thoiGianKhoiHanhStr);
        } catch (ParseException e) {
            jTextFieldthoiGianDuTinh.setText("Thời gian khởi hành không hợp lệ!");
            return;
        }

        // Lấy thời gian di chuyển
        ThoiGianDiChuyen_DAO thoiGianDao = new ThoiGianDiChuyen_DAO();
        int thoiGianDiChuyen = thoiGianDao.getThoiGianDiChuyen(maGaDi, maGaDen);

        if (thoiGianDiChuyen == -1) { // -1 = không tìm thấy lộ trình
            jTextFieldthoiGianDuTinh.setText("Không tìm thấy lộ trình từ " + tenGaDi + " đến " + tenGaDen + "!");
            return;
        }

        // Tính thời gian dự tính
        Calendar cal = Calendar.getInstance();
        cal.setTime(thoiGianKhoiHanh);
        cal.add(Calendar.MINUTE, thoiGianDiChuyen);
        Date thoiGianDuTinh = cal.getTime();

        // Hiển thị thời gian dự tính
        String formattedTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(thoiGianDuTinh);
        jTextFieldthoiGianDuTinh.setText(formattedTime);
    }

    // Tải dữ liệu bảng chuyến đi
    private void loadChuyenDiTable() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "Mã Chuyến Đi", "Ga Đi", "Ga Đến", "Thời Gian Di Chuyển", "Thời Gian Khởi Hành",
                "Thời Gian Dự Tính", "Tàu Di Chuyển", "Toa", "Khoang", "Số Ghế", "Loại Ghế",
                "Số Ghế Đã Đặt", "Số Ghế Còn Trống"
            }, 0
        );
        jTabledanhSachChuyenDi.setModel(model);

        ConnectDB.getInstance().connect();
        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this,
                    "Không thể kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra kết nối.",
                    "Lỗi kết nối",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query =
                "SELECT TOP 100 cd.maChuyenDi, " + // Thêm TOP 100 để giới hạn 50 bản ghi
                "       g1.tenGa AS tenGaDi, " +
                "       g2.tenGa AS tenGaDen, " +
                "       tg.thoiGianDiChuyen, " +
                "       cd.thoiGianKhoiHanh, " +
                "       cd.thoiGianDenDuTinh, " +
                "       t.tenTau, " +
                "       LEFT(toa.tenToaTau, CHARINDEX(' ', toa.tenToaTau + ' ', CHARINDEX(' ', toa.tenToaTau + ' ') + 1) - 1) AS tenToa, " +
                "       LEFT(kt.tenKhoangTau, CHARINDEX(' ', kt.tenKhoangTau + ' ', CHARINDEX(' ', kt.tenKhoangTau + ' ') + 1) - 1) AS tenKhoang, " +
                "       COUNT(g.maGhe) AS tongSoGhe, " +
                "       lg.tenLoaiGhe, " +
                "       ISNULL(SUM(vCount.soGheDaDat), 0) AS soGheDaDat, " +
                "       cd.soGheConTrong " +
                "FROM ChuyenDi cd " +
                "JOIN ThoiGianDiChuyen tg ON cd.maThoiGianDiChuyen = tg.maThoiGianDiChuyen " +
                "JOIN Ga g1 ON tg.maGaDi = g1.maGa " +
                "JOIN Ga g2 ON tg.maGaDen = g2.maGa " +
                "JOIN Tau t ON cd.maTau = t.maTau " +
                "LEFT JOIN ToaTau toa ON t.maTau = toa.maTau " +
                "LEFT JOIN KhoangTau kt ON toa.maToaTau = kt.maToaTau " +
                "LEFT JOIN Ghe g ON kt.maKhoangTau = g.maKhoangTau " +
                "LEFT JOIN LoaiGhe lg ON g.maLoaiGhe = lg.maLoaiGhe " +
                "OUTER APPLY ( " +
                "   SELECT COUNT(*) AS soGheDaDat " +
                "   FROM Ve v " +
                "   WHERE v.maChuyenDi = cd.maChuyenDi AND v.maGhe = g.maGhe " +
                "     AND v.trangThai IN (N'Đã đặt', N'Đã hoàn thành') " +
                ") vCount " +
                "GROUP BY cd.maChuyenDi, g1.tenGa, g2.tenGa, tg.thoiGianDiChuyen, " +
                "         cd.thoiGianKhoiHanh, cd.thoiGianDenDuTinh, t.tenTau, " +
                "         toa.tenToaTau, kt.tenKhoangTau, lg.tenLoaiGhe, cd.soGheConTrong";

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    JOptionPane.showMessageDialog(this,
                        "Không có chuyến đi nào trong tương lai để hiển thị.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                while (rs.next()) {
                    Object[] row = new Object[13];
                    row[0] = rs.getString("maChuyenDi");
                    row[1] = rs.getString("tenGaDi");
                    row[2] = rs.getString("tenGaDen");
                    row[3] = rs.getInt("thoiGianDiChuyen") + " phút";
                    row[4] = rs.getTimestamp("thoiGianKhoiHanh");
                    row[5] = rs.getTimestamp("thoiGianDenDuTinh");
                    row[6] = rs.getString("tenTau");
                    row[7] = rs.getString("tenToa");
                    row[8] = rs.getString("tenKhoang");
                    row[9] = rs.getInt("tongSoGhe") + " ghế";
                    row[10] = rs.getString("tenLoaiGhe");
                    row[11] = rs.getInt("soGheDaDat");
                    row[12] = rs.getInt("soGheConTrong");
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Lỗi tải danh sách chuyến đi: " + e.getMessage();
            if (e.getMessage().toLowerCase().contains("connection") || e.getMessage().toLowerCase().contains("timeout")) {
                errorMessage = "Mất kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra mạng hoặc cấu hình SQL Server.";
            } else if (e.getMessage().toLowerCase().contains("unsupported operation")) {
                errorMessage += " Vui lòng kiểm tra phiên bản driver JDBC hoặc cách xử lý kiểu thời gian.";
            } else if (e.getMessage().toLowerCase().contains("invalid column name") || e.getMessage().toLowerCase().contains("table")) {
                errorMessage += " Vui lòng kiểm tra cấu trúc bảng hoặc cột trong cơ sở dữ liệu.";
            }
            JOptionPane.showMessageDialog(this, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thêm sự kiện chọn hàng trong bảng
    private void addTableSelectionListener() {
        jTabledanhSachChuyenDi.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = jTabledanhSachChuyenDi.getSelectedRow();
                    if (selectedRow != -1) {
                        jTextFieldmaChuyenDi.setText(jTabledanhSachChuyenDi.getValueAt(selectedRow, 0).toString());
                        jTextFieldthoiGianKhoiHanh.setText(jTabledanhSachChuyenDi.getValueAt(selectedRow, 4).toString());
                        jTextFieldthoiGianDuTinh.setText(jTabledanhSachChuyenDi.getValueAt(selectedRow, 5).toString());
                        // Cập nhật JComboBox
                        String gaDi = jTabledanhSachChuyenDi.getValueAt(selectedRow, 1).toString();
                        String gaDen = jTabledanhSachChuyenDi.getValueAt(selectedRow, 2).toString();
                        String tenTau = jTabledanhSachChuyenDi.getValueAt(selectedRow, 6).toString();

                        // Tìm và set giá trị đúng cho JComboBox
                        for (int i = 0; i < jComboBoxgaDi.getItemCount(); i++) {
                            if (jComboBoxgaDi.getItemAt(i).contains(gaDi)) {
                                jComboBoxgaDi.setSelectedIndex(i);
                                break;
                            }
                        }
                        for (int i = 0; i < jComboBoxgaDen.getItemCount(); i++) {
                            if (jComboBoxgaDen.getItemAt(i).contains(gaDen)) {
                                jComboBoxgaDen.setSelectedIndex(i);
                                break;
                            }
                        }
                        // Set giá trị cho jComboBoxtau
                        for (int i = 0; i < jComboBoxTau.getItemCount(); i++) {
                            if (jComboBoxTau.getItemAt(i).equals(tenTau)) {
                                jComboBoxTau.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    // Xóa trắng các trường nhập liệu
    private void clearFields() {
        jTextFieldmaChuyenDi.setText("");
        jTextFieldthoiGianKhoiHanh.setText("");
        jTextFieldthoiGianDuTinh.setText("");
        jComboBoxgaDi.setSelectedIndex(-1);
        jComboBoxgaDen.setSelectedIndex(-1);
        jComboBoxTau.setSelectedIndex(-1);
        jComboBoxloaiGhe.setSelectedIndex(0);
        jComboBoxloaiGhe.setEnabled(false);
        jButtonLuu.setEnabled(false);
        selectedMaThoiGianDiChuyen = null;
        thoiGianKhoiHanh = null;
    }
    
    // Sinh mã chuyến đi theo định dạng CD-MT-DDMMYYYY-XXX
    private String generateMaChuyenDi(Connection conn, String maTau, Date ngayKhoiHanh) {
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Kết nối cơ sở dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (maTau == null || maTau.length() < 3 || ngayKhoiHanh == null) {
            JOptionPane.showMessageDialog(null, "Thiếu dữ liệu để sinh mã chuyến đi!");
            return null;
        }

        String mtCode = maTau.substring(maTau.length() - 3); // 3 ký tự cuối
        String dateCode = new SimpleDateFormat("ddMMyyyy").format(ngayKhoiHanh);
        String prefix = "CD";
        String likePattern = prefix + "-" + mtCode + "-" + dateCode + "-%";
        int sequence = 1;

        try {
            // Lấy số thứ tự lớn nhất hiện tại
            String maxQuery = "SELECT MAX(CAST(RIGHT(maChuyenDi, 3) AS INT)) FROM ChuyenDi WHERE maChuyenDi LIKE ?";
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
            // Kiểm tra trùng lặp, nếu có tăng sequence
            while (true) {
                code = String.format("%s-%s-%s-%03d", prefix, mtCode, dateCode, sequence);

                String checkQuery = "SELECT COUNT(*) FROM ChuyenDi WHERE maChuyenDi = ?";
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
            JOptionPane.showMessageDialog(null, "Lỗi khi sinh mã chuyến đi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
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
        jButtonthemChuyenDi = new javax.swing.JButton();
        jButtoncapNhatChuyenDi = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabelmaChuyenDi = new javax.swing.JLabel();
        jTextFieldmaChuyenDi = new javax.swing.JTextField();
        jLabelgaDen = new javax.swing.JLabel();
        jLabelthoiGianKhoiHanh = new javax.swing.JLabel();
        jTextFieldthoiGianKhoiHanh = new javax.swing.JTextField();
        jButtonthoiGianKhoiHanh = new javax.swing.JButton();
        jLabelthoiGianDuTinh = new javax.swing.JLabel();
        jLabelgaDi = new javax.swing.JLabel();
        jComboBoxgaDi = new javax.swing.JComboBox<>();
        jComboBoxgaDen = new javax.swing.JComboBox<>();
        jLabelTau = new javax.swing.JLabel();
        jComboBoxTau = new javax.swing.JComboBox<>();
        jTextFieldthoiGianDuTinh = new javax.swing.JTextField();
        jLabelloaiGhe = new javax.swing.JLabel();
        jComboBoxloaiGhe = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabledanhSachChuyenDi = new javax.swing.JTable();
        jButtonxoaChuyenDi = new javax.swing.JButton();
        jButtontimChuyenDi = new javax.swing.JButton();
        jButtonLuu = new javax.swing.JButton();
        header1 = new component.Header();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButtonthemChuyenDi.setBackground(new java.awt.Color(65, 165, 238));
        jButtonthemChuyenDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonthemChuyenDi.setForeground(new java.awt.Color(255, 255, 255));
        jButtonthemChuyenDi.setText("Thêm Chuyến Đi");
        jButtonthemChuyenDi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonthemChuyenDiActionPerformed(evt);
            }
        });

        jButtoncapNhatChuyenDi.setBackground(new java.awt.Color(65, 165, 238));
        jButtoncapNhatChuyenDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtoncapNhatChuyenDi.setForeground(new java.awt.Color(255, 255, 255));
        jButtoncapNhatChuyenDi.setText("Cập Nhật Chuyến Đi");
        jButtoncapNhatChuyenDi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtoncapNhatChuyenDiActionPerformed(evt);
            }
        });

        jLabelmaChuyenDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelmaChuyenDi.setForeground(new java.awt.Color(13, 98, 255));
        jLabelmaChuyenDi.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelmaChuyenDi.setText("Mã Chuyến Đi :");

        jLabelgaDen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelgaDen.setForeground(new java.awt.Color(13, 98, 255));
        jLabelgaDen.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelgaDen.setText("Ga đến :");

        jLabelthoiGianKhoiHanh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelthoiGianKhoiHanh.setForeground(new java.awt.Color(13, 98, 255));
        jLabelthoiGianKhoiHanh.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelthoiGianKhoiHanh.setText("Thời Gian Khởi Hành :");

        jButtonthoiGianKhoiHanh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/calendar.png"))); // NOI18N
        jButtonthoiGianKhoiHanh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonthoiGianKhoiHanhActionPerformed(evt);
            }
        });

        jLabelthoiGianDuTinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelthoiGianDuTinh.setForeground(new java.awt.Color(13, 98, 255));
        jLabelthoiGianDuTinh.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelthoiGianDuTinh.setText("Thời Gian Dự Tính:");

        jLabelgaDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelgaDi.setForeground(new java.awt.Color(13, 98, 255));
        jLabelgaDi.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelgaDi.setText("Ga đi :");

        jComboBoxgaDi.setBackground(new java.awt.Color(65, 165, 238));
        jComboBoxgaDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBoxgaDi.setForeground(new java.awt.Color(255, 255, 255));
        jComboBoxgaDi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxgaDi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxgaDiActionPerformed(evt);
            }
        });

        jComboBoxgaDen.setBackground(new java.awt.Color(65, 165, 238));
        jComboBoxgaDen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBoxgaDen.setForeground(new java.awt.Color(255, 255, 255));
        jComboBoxgaDen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxgaDen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxgaDenActionPerformed(evt);
            }
        });

        jLabelTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelTau.setForeground(new java.awt.Color(13, 98, 255));
        jLabelTau.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTau.setText("Tàu Di Chuyển :");

        jComboBoxTau.setBackground(new java.awt.Color(65, 165, 238));
        jComboBoxTau.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBoxTau.setForeground(new java.awt.Color(255, 255, 255));
        jComboBoxTau.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabelloaiGhe.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelloaiGhe.setForeground(new java.awt.Color(13, 98, 255));
        jLabelloaiGhe.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelloaiGhe.setText("Loại Ghế :");

        jComboBoxloaiGhe.setBackground(new java.awt.Color(65, 165, 238));
        jComboBoxloaiGhe.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBoxloaiGhe.setForeground(new java.awt.Color(255, 255, 255));
        jComboBoxloaiGhe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                            .addGap(63, 63, 63)
                            .addComponent(jLabelgaDi, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabelmaChuyenDi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelTau, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxgaDi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxTau, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelgaDen, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelloaiGhe))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxgaDen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxloaiGhe, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jTextFieldmaChuyenDi))
                .addGap(19, 19, 19)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelthoiGianKhoiHanh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelthoiGianDuTinh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jTextFieldthoiGianKhoiHanh)
                        .addGap(0, 0, 0)
                        .addComponent(jButtonthoiGianKhoiHanh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextFieldthoiGianDuTinh, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelmaChuyenDi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldmaChuyenDi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonthoiGianKhoiHanh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelgaDi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxgaDi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelgaDen, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxgaDen, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelthoiGianKhoiHanh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldthoiGianKhoiHanh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelthoiGianDuTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldthoiGianDuTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelloaiGhe, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxloaiGhe, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelTau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxTau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jTabledanhSachChuyenDi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Chuyến Đi", "Ga Đi", "Ga Đến", "Thời Gian Di Chuyển", "Thời Gian Khởi Hành", "Thời Gian Dự Tính", "Tàu Di Chuyển ", "Toa", "Khoang", "Số Ghế", "Loại Ghế", "Số Ghế Đã Đặt", "Số Ghế Còn Trống"
            }
        ));
        jScrollPane1.setViewportView(jTabledanhSachChuyenDi);

        jButtonxoaChuyenDi.setBackground(new java.awt.Color(65, 165, 238));
        jButtonxoaChuyenDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonxoaChuyenDi.setForeground(new java.awt.Color(255, 255, 255));
        jButtonxoaChuyenDi.setText("Xóa Chuyến Đi");
        jButtonxoaChuyenDi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonxoaChuyenDiActionPerformed(evt);
            }
        });

        jButtontimChuyenDi.setBackground(new java.awt.Color(65, 165, 238));
        jButtontimChuyenDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtontimChuyenDi.setForeground(new java.awt.Color(255, 255, 255));
        jButtontimChuyenDi.setText("Tìm Chuyến Đi");
        jButtontimChuyenDi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtontimChuyenDiActionPerformed(evt);
            }
        });

        jButtonLuu.setBackground(new java.awt.Color(65, 165, 238));
        jButtonLuu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonLuu.setForeground(new java.awt.Color(255, 255, 255));
        jButtonLuu.setText("Lưu");
        jButtonLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLuuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButtonLuu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 394, Short.MAX_VALUE)
                        .addComponent(jButtontimChuyenDi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonthemChuyenDi)
                        .addGap(11, 11, 11)
                        .addComponent(jButtonxoaChuyenDi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtoncapNhatChuyenDi, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtontimChuyenDi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonthemChuyenDi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonxoaChuyenDi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtoncapNhatChuyenDi, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLuu, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
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
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonthemChuyenDiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonthemChuyenDiActionPerformed
        // TODO add your handling code here:
        
    String tenGaDi = (String) jComboBoxgaDi.getSelectedItem();
    String tenGaDen = (String) jComboBoxgaDen.getSelectedItem();
    String tenTau = (String) jComboBoxTau.getSelectedItem();

    String maGaDi = gaMap.get(tenGaDi);
    String maGaDen = gaMap.get(tenGaDen);
    String thoiGianKhoiHanhStr = jTextFieldthoiGianKhoiHanh.getText().trim();

    if (maGaDi == null || maGaDen == null || thoiGianKhoiHanhStr.isEmpty() || tenTau == null || tenTau.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin và chọn TÊN TÀU!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (maGaDi.equals(maGaDen)) {
        JOptionPane.showMessageDialog(this, "Ga đi và ga đến không được trùng nhau!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Date thoiGianKhoiHanh = null;
    try {
        thoiGianKhoiHanh = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(thoiGianKhoiHanhStr);
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(this, "Thời gian khởi hành không hợp lệ! Định dạng đúng: dd-MM-yyyy HH:mm:ss", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (thoiGianKhoiHanh.before(new Date())) {
        JOptionPane.showMessageDialog(this, "Thời gian khởi hành phải lớn hơn thời gian hiện tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Lấy mã thời gian di chuyển và thời gian di chuyển
    String maThoiGianDiChuyen = null;
    int thoiGianDiChuyen = -1;
    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
             "SELECT maThoiGianDiChuyen, thoiGianDiChuyen FROM ThoiGianDiChuyen WHERE maGaDi = ? AND maGaDen = ?")) {
        stmt.setString(1, maGaDi);
        stmt.setString(2, maGaDen);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            maThoiGianDiChuyen = rs.getString("maThoiGianDiChuyen");
            thoiGianDiChuyen = rs.getInt("thoiGianDiChuyen");
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thời gian di chuyển cho lộ trình này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi lấy thời gian di chuyển: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (thoiGianDiChuyen <= 0) {
        JOptionPane.showMessageDialog(this, "Thời gian di chuyển không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Lấy mã tàu từ tên tàu
    String maTau = tauMap.get(tenTau);
    if (maTau == null || maTau.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy mã tàu tương ứng với tên tàu đã chọn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    ConnectDB.getInstance().connect();
    try (Connection conn = ConnectDB.getConnection()) {
        if (conn == null || conn.isClosed()) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sinh mã chuyến đi
        String maChuyenDi = generateMaChuyenDi(conn, maTau, thoiGianKhoiHanh);
        if (maChuyenDi == null) return;

        // Hiển thị mã chuyến đi lên textField nếu bạn có 1 ô jTextFieldMaChuyenDi
        if (jTextFieldmaChuyenDi != null) {
            jTextFieldmaChuyenDi.setText(maChuyenDi);
        }

        // Tính thời gian đến dự tính
        Calendar cal = Calendar.getInstance();
        cal.setTime(thoiGianKhoiHanh);
        cal.add(Calendar.MINUTE, thoiGianDiChuyen);
        Date thoiGianDenDuTinh = cal.getTime();

        int soGheConTrong = 100;

        // Thêm chuyến đi vào CSDL
        String insertSQL = "INSERT INTO ChuyenDi (maChuyenDi, maThoiGianDiChuyen, maTau, thoiGianKhoiHanh, thoiGianDenDuTinh, soGheConTrong) "
                         + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            stmt.setString(1, maChuyenDi);
            stmt.setString(2, maThoiGianDiChuyen);
            stmt.setString(3, maTau);
            stmt.setTimestamp(4, new Timestamp(thoiGianKhoiHanh.getTime()));
            stmt.setTimestamp(5, new Timestamp(thoiGianDenDuTinh.getTime()));
            stmt.setInt(6, soGheConTrong);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Thêm chuyến đi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadChuyenDiTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm chuyến đi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thao tác cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButtonthemChuyenDiActionPerformed

    private void jButtoncapNhatChuyenDiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtoncapNhatChuyenDiActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTabledanhSachChuyenDi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chuyến đi để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        maChuyenDi = jTabledanhSachChuyenDi.getValueAt(selectedRow, 0).toString();

        // Bật combobox loại ghế và nút Lưu
        jComboBoxloaiGhe.setEnabled(true);
        jButtonLuu.setEnabled(true);

        // Gợi ý loại ghế hiện tại cho người dùng
        String loaiGheHienTai = jTabledanhSachChuyenDi.getValueAt(selectedRow, 10).toString();
        jComboBoxloaiGhe.setSelectedItem(loaiGheHienTai);

        JOptionPane.showMessageDialog(this, "Bạn có thể chỉnh sửa rồi nhấn 'Lưu' !", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButtoncapNhatChuyenDiActionPerformed

    private void jButtonthoiGianKhoiHanhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonthoiGianKhoiHanhActionPerformed
        // TODO add your handling code here:
        JDialog dialog = new JDialog();
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy HH:mm:ss");
        dialog.add(dateChooser);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dateChooser.getDateEditor().addPropertyChangeListener("date", e -> {
            thoiGianKhoiHanh = dateChooser.getDate();
            if (thoiGianKhoiHanh != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                jTextFieldthoiGianKhoiHanh.setText(sdf.format(thoiGianKhoiHanh));
                calculateThoiGianDuTinh();
            }
            dialog.dispose();
        });
        dialog.setVisible(true);
    }//GEN-LAST:event_jButtonthoiGianKhoiHanhActionPerformed

    private void jButtonxoaChuyenDiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonxoaChuyenDiActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTabledanhSachChuyenDi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chuyến đi để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maChuyenDi = jTextFieldmaChuyenDi.getText().trim();
        if (maChuyenDi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mã chuyến đi để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM Ve WHERE maChuyenDi = ? AND trangThai IN (N'Đã đặt', N'Đã hoàn thành')")) {

            checkStmt.setString(1, maChuyenDi);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Không thể xóa vì đã có vé được đặt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra vé: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa chuyến đi " + maChuyenDi + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM ChuyenDi WHERE maChuyenDi = ?")) {

            deleteStmt.setString(1, maChuyenDi);
            int result = deleteStmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Xóa chuyến đi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadChuyenDiTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa chuyến đi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa chuyến đi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonxoaChuyenDiActionPerformed

    private void jButtontimChuyenDiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtontimChuyenDiActionPerformed
        // TODO add your handling code here:
        String tenGaDi = jComboBoxgaDi.getSelectedItem() != null ? jComboBoxgaDi.getSelectedItem().toString().trim() : "";
        String tenGaDen = jComboBoxgaDen.getSelectedItem() != null ? jComboBoxgaDen.getSelectedItem().toString().trim() : "";
        String thoiGianKhoiHanhStr = jTextFieldthoiGianKhoiHanh.getText().trim();

        StringBuilder query = new StringBuilder(
        "SELECT cd.maChuyenDi, g1.tenGa AS tenGaDi, g2.tenGa AS tenGaDen, tg.thoiGianDiChuyen, " +
        "cd.thoiGianKhoiHanh, cd.thoiGianDenDuTinh, t.tenTau, " +
        "toa.tenToaTau AS tenToa, " +
        "kt.tenKhoangTau AS tenKhoang, " +
        "COUNT(g.maGhe) AS tongSoGhe, lg.tenLoaiGhe, ISNULL(SUM(vCount.soGheDaDat), 0) AS soGheDaDat, cd.soGheConTrong " +
        "FROM ChuyenDi cd " +
        "JOIN ThoiGianDiChuyen tg ON cd.maThoiGianDiChuyen = tg.maThoiGianDiChuyen " +
        "JOIN Ga g1 ON tg.maGaDi = g1.maGa " +
        "JOIN Ga g2 ON tg.maGaDen = g2.maGa " +
        "JOIN Tau t ON cd.maTau = t.maTau " +
        "LEFT JOIN ToaTau toa ON t.maTau = toa.maTau " +
        "LEFT JOIN KhoangTau kt ON toa.maToaTau = kt.maToaTau " +
        "LEFT JOIN Ghe g ON kt.maKhoangTau = g.maKhoangTau " +
        "LEFT JOIN LoaiGhe lg ON g.maLoaiGhe = lg.maLoaiGhe " +
        "OUTER APPLY ( " +
        "   SELECT COUNT(*) AS soGheDaDat " +
        "   FROM Ve v " +
        "   WHERE v.maChuyenDi = cd.maChuyenDi AND v.maGhe = g.maGhe " +
        "     AND v.trangThai IN (N'Đã đặt', N'Đã hoàn thành') " +
        ") vCount "
    );

        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<String> params = new ArrayList<>();

        if (!tenGaDi.isEmpty()) {
            conditions.add("g1.tenGa LIKE ?");
            params.add("%" + tenGaDi + "%");
        }
        if (!tenGaDen.isEmpty()) {
            conditions.add("g2.tenGa LIKE ?");
            params.add("%" + tenGaDen + "%");
        }
        if (!thoiGianKhoiHanhStr.isEmpty()) {
            conditions.add("CAST(cd.thoiGianKhoiHanh AS DATE) = ?");
            params.add(thoiGianKhoiHanhStr);
        }

        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
}

            query.append(" GROUP BY cd.maChuyenDi, g1.tenGa, g2.tenGa, tg.thoiGianDiChuyen, " +
             "cd.thoiGianKhoiHanh, cd.thoiGianDenDuTinh, t.tenTau, toa.tenToaTau, kt.tenKhoangTau, lg.tenLoaiGhe, cd.soGheConTrong");

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã Chuyến Đi", "Ga Đi", "Ga Đến", "Thời Gian Di Chuyển", "Thời Gian Khởi Hành",
                         "Thời Gian Dự Tính", "Tàu Di Chuyển", "Toa", "Khoang", "Số Ghế", "Loại Ghế",
                         "Số Ghế Đã Đặt", "Số Ghế Còn Trống"}, 0
        );
        jTabledanhSachChuyenDi.setModel(model);
        ConnectDB.getInstance().connect();
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến đi phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                while (rs.next()) {
                    Object[] row = new Object[13];
                    row[0] = rs.getString("maChuyenDi");
                    row[1] = rs.getString("tenGaDi");
                    row[2] = rs.getString("tenGaDen");
                    row[3] = rs.getInt("thoiGianDiChuyen") + " phút";
                    row[4] = rs.getTimestamp("thoiGianKhoiHanh");
                    row[5] = rs.getTimestamp("thoiGianDenDuTinh");
                    row[6] = rs.getString("tenTau");
                    row[7] = rs.getString("tenToa");
                    row[8] = rs.getString("tenKhoang");
                    row[9] = rs.getInt("tongSoGhe") + " ghế";
                    row[10] = rs.getString("tenLoaiGhe");
                    row[11] = rs.getInt("soGheDaDat");
                    row[12] = rs.getInt("soGheConTrong");
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm chuyến đi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_jButtontimChuyenDiActionPerformed

    private void jComboBoxgaDiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxgaDiActionPerformed
        // TODO add your handling code here:
        calculateThoiGianDuTinh();
    }//GEN-LAST:event_jComboBoxgaDiActionPerformed

    private void jComboBoxgaDenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxgaDenActionPerformed
        // TODO add your handling code here:
        calculateThoiGianDuTinh();
    }//GEN-LAST:event_jComboBoxgaDenActionPerformed

    private void jButtonLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLuuActionPerformed
        // TODO add your handling code here:
        if (maChuyenDi == null || maChuyenDi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có chuyến đi để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object selected = jComboBoxloaiGhe.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại ghế hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tenLoaiGhe = selected.toString().trim();
        if (tenLoaiGhe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại ghế hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = ConnectDB.getConnection()) {
            // Lấy maLoaiGhe từ tenLoaiGhe
            String sqlGetMaLoaiGhe = "SELECT maLoaiGhe FROM LoaiGhe WHERE tenLoaiGhe = ?";
            String maLoaiGhe = null;
            try (PreparedStatement stmt = conn.prepareStatement(sqlGetMaLoaiGhe)) {
                stmt.setString(1, tenLoaiGhe);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        maLoaiGhe = rs.getString("maLoaiGhe");
                    } else {
                        JOptionPane.showMessageDialog(this, "Loại ghế không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Cập nhật maLoaiGhe trong bảng Ghe, các ghế thuộc chuyến đi
            String sqlUpdate = "UPDATE Ghe SET maLoaiGhe = ? WHERE maKhoangTau IN ( " +
                "SELECT kt.maKhoangTau FROM KhoangTau kt " +
                "JOIN ToaTau toa ON kt.maToaTau = toa.maToaTau " +
                "JOIN Tau t ON toa.maTau = t.maTau " +
                "JOIN ChuyenDi cd ON t.maTau = cd.maTau " +
                "WHERE cd.maChuyenDi = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, maLoaiGhe);
                stmt.setString(2, maChuyenDi);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật loại ghế thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadChuyenDiTable(); // Làm mới bảng
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Không có ghế nào được cập nhật!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButtonLuuActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Header header1;
    private javax.swing.JButton jButtonLuu;
    private javax.swing.JButton jButtoncapNhatChuyenDi;
    private javax.swing.JButton jButtonthemChuyenDi;
    private javax.swing.JButton jButtonthoiGianKhoiHanh;
    private javax.swing.JButton jButtontimChuyenDi;
    private javax.swing.JButton jButtonxoaChuyenDi;
    private javax.swing.JComboBox<String> jComboBoxTau;
    private javax.swing.JComboBox<String> jComboBoxgaDen;
    private javax.swing.JComboBox<String> jComboBoxgaDi;
    private javax.swing.JComboBox<String> jComboBoxloaiGhe;
    private javax.swing.JLabel jLabelTau;
    private javax.swing.JLabel jLabelgaDen;
    private javax.swing.JLabel jLabelgaDi;
    private javax.swing.JLabel jLabelloaiGhe;
    private javax.swing.JLabel jLabelmaChuyenDi;
    private javax.swing.JLabel jLabelthoiGianDuTinh;
    private javax.swing.JLabel jLabelthoiGianKhoiHanh;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabledanhSachChuyenDi;
    private javax.swing.JTextField jTextFieldmaChuyenDi;
    private javax.swing.JTextField jTextFieldthoiGianDuTinh;
    private javax.swing.JTextField jTextFieldthoiGianKhoiHanh;
    // End of variables declaration//GEN-END:variables

}
