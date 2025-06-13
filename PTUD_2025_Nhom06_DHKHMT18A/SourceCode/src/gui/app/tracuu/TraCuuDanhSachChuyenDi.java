/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.tracuu;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import connectdb.ConnectDB;
import dao.ChuyenDi_DAO;
import dao.Ga_DAO;
import dao.Tau_DAO;
import entity.ChuyenDi;
import entity.Ga;
import entity.NhanVien;
import entity.Tau;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

/**
 *
 * @author HAI TAM
 */
public class TraCuuDanhSachChuyenDi extends javax.swing.JPanel {
    private final Ga_DAO gaDAO;
    private final Tau_DAO tauDAO;
    private final ChuyenDi_DAO chuyenDiDAO;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    /**
     * Creates new form TraCuuDanhSachChuyenDi
     */
    public TraCuuDanhSachChuyenDi(NhanVien nv) {
        gaDAO = new Ga_DAO();
        tauDAO = new Tau_DAO();
        chuyenDiDAO = new ChuyenDi_DAO();
        initComponents();
        initializeTable();
        initializeComboBoxes();
        jTextFieldngayDi.setText("");
        ConnectDB.getInstance().connect();
        
        // Tự động hiển thị tất cả chuyến đi khi giao diện mở
        loadAllTrips();
//        header1.jLabel14.setText("Tra cứu danh sách chuyến đi");
//        header1.jButton4.setText(nv.getTenNV());

    }
    
    private void initializeTable() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"STT", "Ga Đi", "Ga Đến", "Cự Ly (Km)", "Ngày Đi", "Giờ Đi", "Ngày Đến", "Giờ Đến", "Tên Tàu"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTabledanhSachChuyenDi.setModel(model);
        jTabledanhSachChuyenDi.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTabledanhSachChuyenDi.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTabledanhSachChuyenDi.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTabledanhSachChuyenDi.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTabledanhSachChuyenDi.getColumnModel().getColumn(8).setPreferredWidth(100);
    }

    private void initializeComboBoxes() {
        ArrayList<String> stations = new ArrayList<>();
        stations.add(""); // Empty option
        try {
            ArrayList<Ga> gaList = gaDAO.loadTatCaCaGa();
            if (gaList != null && !gaList.isEmpty()) {
                for (Ga ga : gaList) {
                    stations.add(ga.getTenGa());
                }
            } else {
                stations.add("Không có ga");
                JOptionPane.showMessageDialog(this, "Danh sách ga trống.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách ga: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            stations.add("Không có ga");
        }
        jComboBoxgaDi.setModel(new DefaultComboBoxModel<>(stations.toArray(new String[0])));
        jComboBoxgaDen.setModel(new DefaultComboBoxModel<>(stations.toArray(new String[0])));
    }

    private void loadAllTrips() {
        DefaultTableModel model = (DefaultTableModel) jTabledanhSachChuyenDi.getModel();
        model.setRowCount(0);

        ArrayList<ChuyenDi> results;
        try {
            results = chuyenDiDAO.layTatCaChuyenDiTrongTuongLai();
            System.out.println("loadAllTrips: Fetched " + (results != null ? results.size() : 0) + " trips");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy danh sách chuyến đi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        if (results == null || results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có chuyến đi nào trong tương lai.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            clearFields();
            return;
        }

        long stt = 1;
        for (ChuyenDi cd : results) {
            Timestamp tsDi = Timestamp.valueOf(cd.getThoiGianKhoiHanh());
            Timestamp tsDen = Timestamp.valueOf(cd.getThoiGianDenDuTinh());

            if (tsDi == null || tsDen == null) {
                System.out.println("Invalid timestamp for trip: " + cd.getMaChuyenDi());
                continue;
            }

            Date ngayGioDi = new Date(tsDi.getTime());
            Date ngayGioDen = new Date(tsDen.getTime());

            int soPhut = cd.getThoiGianDiChuyen() != null ? cd.getThoiGianDiChuyen().getThoiGianDiChuyen() : 0;
            int gio = soPhut / 60;
            int phut = soPhut % 60;

            String tenGaDi = cd.getThoiGianDiChuyen() != null && cd.getThoiGianDiChuyen().getGaDi() != null ?
                             cd.getThoiGianDiChuyen().getGaDi().getTenGa() : "Không xác định";
            String tenGaDen = cd.getThoiGianDiChuyen() != null && cd.getThoiGianDiChuyen().getGaDen() != null ?
                              cd.getThoiGianDiChuyen().getGaDen().getTenGa() : "Không xác định";

            int soKm = cd.getThoiGianDiChuyen() != null ? (int) cd.getThoiGianDiChuyen().getSoKmDiChuyen() : 0;

            Object[] row = {
                stt++,
                tenGaDi,
                tenGaDen,
                soKm,
                DATE_FORMAT.format(ngayGioDi),
                TIME_FORMAT.format(ngayGioDi),
                DATE_FORMAT.format(ngayGioDen),
                TIME_FORMAT.format(ngayGioDen),
                cd.getTau() != null ? cd.getTau().getTenTau() : "Không xác định"
            };
            model.addRow(row);
        }
    }

    private void loadTableData(String gaDi, String gaDen, String ngayDiStr, boolean onlyEmptyTrains) {
        System.out.println("loadTableData: gaDi=" + gaDi + ", gaDen=" + gaDen + ", ngayDiStr=" + ngayDiStr + ", onlyEmptyTrains=" + onlyEmptyTrains);

        DefaultTableModel model = (DefaultTableModel) jTabledanhSachChuyenDi.getModel();
        model.setRowCount(0);

        // Validate inputs
        if (gaDi == null || gaDi.isEmpty() || gaDi.equals("Không có ga")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ga Đi hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gaDen == null || gaDen.isEmpty() || gaDen.equals("Không có ga")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ga Đến hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gaDi.equals(gaDen)) {
            JOptionPane.showMessageDialog(this, "Ga Đi và Ga Đến không được trùng nhau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ngayDiStr == null || ngayDiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Ngày Đi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate ngayDi = null;
        Date parsedNgayDi = null;
        DATE_FORMAT.setLenient(false);
        try {
            parsedNgayDi = DATE_FORMAT.parse(ngayDiStr);
            ngayDi = parsedNgayDi.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            System.out.println("Parsed ngayDi: " + parsedNgayDi + ", LocalDate: " + ngayDi);

            // Allow current date or future
            Calendar calInput = Calendar.getInstance();
            calInput.setTime(parsedNgayDi);
            Calendar calToday = Calendar.getInstance();
            calToday.setTime(new Date());
            calToday.set(Calendar.HOUR_OF_DAY, 0);
            calToday.set(Calendar.MINUTE, 0);
            calToday.set(Calendar.SECOND, 0);
            calToday.set(Calendar.MILLISECOND, 0);

            if (calInput.getTime().before(calToday.getTime())) {
                JOptionPane.showMessageDialog(this, "Ngày Đi phải là ngày hiện tại hoặc trong tương lai.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày Đi không đúng định dạng (dd-MM-yyyy).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Query trips
        ArrayList<ChuyenDi> results = null;
        try {
            results = chuyenDiDAO.timChuyenDiTheoThoiGianKhoiHanhVaGaDiGaDen(gaDi, gaDen, ngayDi);
            System.out.println("Query returned " + (results != null ? results.size() : 0) + " trips");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm chuyến đi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        if (results == null || results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến đi phù hợp.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            clearFields();
            return;
        }

        // Filter by empty trains if required
        ArrayList<ChuyenDi> filteredResults = new ArrayList<>();
        for (ChuyenDi cd : results) {
            if (cd.getTau() == null || cd.getThoiGianDiChuyen() == null) {
                System.out.println("Skipping trip " + cd.getMaChuyenDi() + ": Missing tau or thoiGianDiChuyen");
                continue;
            }
            boolean isEmpty = onlyEmptyTrains ? cd.getSoGheDaDat() == 0 : true;
            if (isEmpty) {
                filteredResults.add(cd);
            }
        }

        System.out.println("Filtered " + filteredResults.size() + " trips");
        if (filteredResults.isEmpty()) {
            String message = onlyEmptyTrains ? "Không tìm thấy tàu trống cho chuyến đi này." :
                                             "Không tìm thấy chuyến đi phù hợp sau khi lọc.";
            JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
            clearFields();
            return;
        }

        // Populate table
        boolean isFirstRow = true;
        long stt = 1;
        for (ChuyenDi cd : filteredResults) {
            Timestamp tsDi = Timestamp.valueOf(cd.getThoiGianKhoiHanh());
            Timestamp tsDen = Timestamp.valueOf(cd.getThoiGianDenDuTinh());

            if (tsDi == null || tsDen == null) {
                System.out.println("Invalid timestamp for trip: " + cd.getMaChuyenDi());
                continue;
            }

            Date ngayGioDi = new Date(tsDi.getTime());
            Date ngayGioDen = new Date(tsDen.getTime());

            int soPhut = cd.getThoiGianDiChuyen().getThoiGianDiChuyen();
            int gio = soPhut / 60;
            int phut = soPhut % 60;

            String tenGaDi = cd.getThoiGianDiChuyen().getGaDi() != null ? cd.getThoiGianDiChuyen().getGaDi().getTenGa() : "Không xác định";
            String tenGaDen = cd.getThoiGianDiChuyen().getGaDen() != null ? cd.getThoiGianDiChuyen().getGaDen().getTenGa() : "Không xác định";

            Object[] row = {
                stt++,
                tenGaDi,
                tenGaDen,
                cd.getThoiGianDiChuyen().getSoKmDiChuyen(),
                DATE_FORMAT.format(ngayGioDi),
                TIME_FORMAT.format(ngayGioDi),
                DATE_FORMAT.format(ngayGioDen),
                TIME_FORMAT.format(ngayGioDen),
                cd.getTau().getTenTau()
            };
            model.addRow(row);

            if (isFirstRow) {
                jLabelgaDi.setText(tenGaDi);
                jLabelgaDen.setText(tenGaDen);
                jLabelngayDi.setText("Ngày Đi: " + DATE_FORMAT.format(ngayGioDi));
                jLabelngayDen.setText("Ngày Đến: " + DATE_FORMAT.format(ngayGioDen));
                jLabelthoiGianHanhTrinh.setText("Thời Gian Hành Trình: " + gio + " giờ " + phut + " phút");
                isFirstRow = false;
            }
        }
    }

    private void clearFields() {
        jLabelgaDi.setText("Ga Đi");
        jLabelgaDen.setText("Ga Đến");
        jLabelngayDi.setText("Ngày Đi");
        jLabelngayDen.setText("Ngày Đến");
        jLabelthoiGianHanhTrinh.setText("Thời Gian Hành Trình:");
    }

       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabledanhSachChuyenDi = new javax.swing.JTable();
        jButtontraCuu = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabelgaDi = new javax.swing.JLabel();
        jLabelgaDen = new javax.swing.JLabel();
        jLabelngayDi = new javax.swing.JLabel();
        jLabelngayDen = new javax.swing.JLabel();
        jLabelthoiGianHanhTrinh = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxgaDi = new javax.swing.JComboBox<>();
        jComboBoxgaDen = new javax.swing.JComboBox<>();
        jTextFieldngayDi = new javax.swing.JTextField();
        jLabelgaden = new javax.swing.JLabel();
        jLabelgadi = new javax.swing.JLabel();
        jLabelngaydi = new javax.swing.JLabel();
        jButtonngayDi = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTabledanhSachChuyenDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTabledanhSachChuyenDi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Ga Đi", "Ga Đến", "Cự Ly(Km)", "Ngày Đi", "Giờ Đi", "Ngày Đến", "Giờ Đến"
            }
        ));
        jScrollPane1.setViewportView(jTabledanhSachChuyenDi);

        jButtontraCuu.setBackground(new java.awt.Color(65, 165, 238));
        jButtontraCuu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtontraCuu.setForeground(new java.awt.Color(255, 255, 255));
        jButtontraCuu.setText("Tra Cứu");
        jButtontraCuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtontraCuuActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(65, 165, 238));

        jLabelgaDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelgaDi.setForeground(new java.awt.Color(255, 255, 255));
        jLabelgaDi.setText("Ga Đi");

        jLabelgaDen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelgaDen.setForeground(new java.awt.Color(255, 255, 255));
        jLabelgaDen.setText("Ga Đến");

        jLabelngayDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelngayDi.setForeground(new java.awt.Color(255, 255, 255));
        jLabelngayDi.setText("Ngày Đi");

        jLabelngayDen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelngayDen.setForeground(new java.awt.Color(255, 255, 255));
        jLabelngayDen.setText("Ngày Đến");

        jLabelthoiGianHanhTrinh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelthoiGianHanhTrinh.setForeground(new java.awt.Color(255, 255, 255));
        jLabelthoiGianHanhTrinh.setText("Thời Gian Hành Trình :");

        jLabel4.setIcon(new javax.swing.ImageIcon("C:\\Users\\HAI TAM\\Downloads\\muiten.png")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(233, 233, 233)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelngayDi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(396, 396, 396))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelgaDi)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(204, 204, 204)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelgaDen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(17, 17, 17))
                    .addComponent(jLabelngayDen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(234, 234, 234))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(430, 430, 430)
                .addComponent(jLabelthoiGianHanhTrinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelgaDi)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3))
                    .addComponent(jLabelgaDen))
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelngayDi)
                    .addComponent(jLabelngayDen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelthoiGianHanhTrinh)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        jTextFieldngayDi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextFieldngayDi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldngayDiActionPerformed(evt);
            }
        });

        jLabelgaden.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelgaden.setForeground(new java.awt.Color(13, 98, 255));
        jLabelgaden.setText("Ga Đến :");

        jLabelgadi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelgadi.setForeground(new java.awt.Color(13, 98, 255));
        jLabelgadi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelgadi.setText("Ga Đi :");

        jLabelngaydi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelngaydi.setForeground(new java.awt.Color(13, 98, 255));
        jLabelngaydi.setText("Ngày Đi :");

        jButtonngayDi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonngayDi.setForeground(new java.awt.Color(255, 255, 255));
        jButtonngayDi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/calendar.png"))); // NOI18N
        jButtonngayDi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonngayDiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jLabelgadi, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxgaDi, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelgaden, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxgaDen, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelngaydi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldngayDi, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jButtonngayDi)
                        .addGap(0, 81, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtontraCuu)
                        .addGap(12, 12, 12)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonngayDi, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxgaDen, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldngayDi, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelgaden, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelngaydi, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxgaDi, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelgadi, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtontraCuu)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(78, 133, 248));
        jPanel4.setPreferredSize(new java.awt.Dimension(400, 300));

        jButton3.setBackground(new java.awt.Color(78, 133, 248));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/user-interface.png"))); // NOI18N
        jButton3.setBorder(null);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Chào nhân viên bán vé");
        jLabel13.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Thống kê Tài Chính");

        jButton4.setBackground(new java.awt.Color(78, 133, 248));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setText("Nguyen Minh Phuc");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(57, 57, 57))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1007, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtontraCuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtontraCuuActionPerformed
        // TODO add your handling code here:
        String gaDi = (String) jComboBoxgaDi.getSelectedItem();
        String gaDen = (String) jComboBoxgaDen.getSelectedItem();
        String ngayDiStr = jTextFieldngayDi.getText().trim();

        System.out.println("Search inputs: gaDi=" + gaDi + ", gaDen=" + gaDen + ", ngayDiStr=" + ngayDiStr);

        if (gaDi == null || gaDi.isEmpty() || gaDi.equals("Không có ga")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ga Đi hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gaDen == null || gaDen.isEmpty() || gaDen.equals("Không có ga")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ga Đến hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gaDi.equals(gaDen)) {
            JOptionPane.showMessageDialog(this, "Ga Đi và Ga Đến không được trùng nhau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ngayDiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Ngày Đi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadTableData(gaDi, gaDen, ngayDiStr, false);
    }//GEN-LAST:event_jButtontraCuuActionPerformed

    private void jTextFieldngayDiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldngayDiActionPerformed
        // TODO add your handling code here:
        String ngayDiStr = jTextFieldngayDi.getText().trim();
        if (!ngayDiStr.isEmpty()) {
            DATE_FORMAT.setLenient(false);
            try {
                Date ngayDi = DATE_FORMAT.parse(ngayDiStr);
                jLabelngayDi.setText("Ngày Đi: " + ngayDiStr);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Ngày Đi không đúng định dạng (dd-MM-yyyy)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTextFieldngayDiActionPerformed

    private void jComboBoxgaDiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxgaDiActionPerformed
        // TODO add your handling code here:
        String selectedGaDi = (String) jComboBoxgaDi.getSelectedItem();
        jLabelgaDi.setText(selectedGaDi != null && !selectedGaDi.isEmpty() ? selectedGaDi : "Ga Đi");
    }//GEN-LAST:event_jComboBoxgaDiActionPerformed

    private void jComboBoxgaDenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxgaDenActionPerformed
        // TODO add your handling code here:
        String selectedGaDen = (String) jComboBoxgaDen.getSelectedItem();
        jLabelgaDen.setText(selectedGaDen != null && !selectedGaDen.isEmpty() ? selectedGaDen : "Ga Đến");
    }//GEN-LAST:event_jComboBoxgaDenActionPerformed

    private void jButtonngayDiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonngayDiActionPerformed
        // TODO add your handling code here:
        JDialog dialog = new JDialog();
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy");
        dialog.add(dateChooser);
        dialog.setSize(300, 70);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);

        dateChooser.getDateEditor().addPropertyChangeListener("date", e -> {
            if (e.getNewValue() != null) {
                String selectedDate = DATE_FORMAT.format(dateChooser.getDate());
                jTextFieldngayDi.setText(selectedDate);
                jLabelngayDi.setText("Ngày Đi: " + selectedDate);
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }//GEN-LAST:event_jButtonngayDiActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    public javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonngayDi;
    private javax.swing.JButton jButtontraCuu;
    private javax.swing.JComboBox<String> jComboBoxgaDen;
    private javax.swing.JComboBox<String> jComboBoxgaDi;
    private javax.swing.JLabel jLabel13;
    public javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelgaDen;
    private javax.swing.JLabel jLabelgaDi;
    private javax.swing.JLabel jLabelgaden;
    private javax.swing.JLabel jLabelgadi;
    private javax.swing.JLabel jLabelngayDen;
    private javax.swing.JLabel jLabelngayDi;
    private javax.swing.JLabel jLabelngaydi;
    private javax.swing.JLabel jLabelthoiGianHanhTrinh;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabledanhSachChuyenDi;
    private javax.swing.JTextField jTextFieldngayDi;
    // End of variables declaration//GEN-END:variables
}
