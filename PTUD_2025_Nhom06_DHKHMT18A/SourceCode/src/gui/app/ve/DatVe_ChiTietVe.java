/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.ve;

import dao.ChuyenDi_DAO;
import dao.Ghe_DAO;
import dao.KhoangTau_DAO;
import dao.Tau_DAO;
import dao.ToaTau_DAO;
import entity.ChuyenDi;
import entity.Ghe;
import entity.KhachHang;
import entity.KhoangTau;
import entity.ToaTau;
import entity.Ve;
import gui.app.ve.ThongTinChiTietVe;
import guiCustom.TextField;
import guiCustom.button;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author PHAMGIAKHANH
 */
public class DatVe_ChiTietVe extends javax.swing.JPanel {
    private ToaTau_DAO toatau_dao = new ToaTau_DAO();
    private KhoangTau_DAO khoangtau_dao = new KhoangTau_DAO();
    private ChuyenDi_DAO chuyendi_dao = new ChuyenDi_DAO();
    private Ghe_DAO ghe_dao = new Ghe_DAO();
    private ChuyenDi chuyenDi;
    private KhachHang khachHang;
    private ThongTinChiTietVe currentQuickFilled = null;
    /**
     * Creates new form DatVe_ChonChuyen
     */
    public DatVe_ChiTietVe() {
        initComponents();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                hienThiTatCaToaTau();
                System.out.println(".componentShown()");
            }
        });
        jPanelThongTinVe.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                if (!(e.getChild() instanceof ThongTinChiTietVe)) return;
                ThongTinChiTietVe added = (ThongTinChiTietVe) e.getChild();
                int selectedCount = 0;
                for (Component c : jPanelThongTinVe.getComponents()) {
                    if (c instanceof ThongTinChiTietVe) {
                        selectedCount++;
                    }
                }
                if (selectedCount > 8) {
                    return;
                }
                // Lấy toa/khoang của vé vừa thêm
                char newToa       = added.getSoToaTau();
                String newKhoang = added.getSoKhoangTau();

                // Duyệt qua tất cả các vé trước đó
                for (Component c : jPanelThongTinVe.getComponents()) {
                    if (c == added) continue;              // bỏ qua chính nó
                    if (c instanceof ThongTinChiTietVe info) {
                        // 1. Nếu toa khác
                        if (info.getSoToaTau() != newToa) {
                            JOptionPane.showMessageDialog(
                                DatVe_ChiTietVe.this,
                                String.format("Ghế mới ở toa %c, nhưng trước đó đã chọn toa %c.",
                                              newToa, info.getSoToaTau()),
                                "Cảnh báo", JOptionPane.WARNING_MESSAGE
                            );
                            break;
                            // Không break nếu bạn muốn cảnh báo với tất cả các vé khác nhau
                        }
                        // 2. Nếu khoang khác
                        if (! info.getSoKhoangTau().equals(newKhoang)) {
                            JOptionPane.showMessageDialog(
                                DatVe_ChiTietVe.this,
                                String.format("Ghế mới ở khoang %s, nhưng trước đó đã chọn khoang %s.",
                                              newKhoang, info.getSoKhoangTau()),
                                "Cảnh báo", JOptionPane.WARNING_MESSAGE
                            );
                            break;
                        }
                    }
                }
            }
        });
        jPanelThongTinVe.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                if (!(e.getChild() instanceof ThongTinChiTietVe)) return;
                ThongTinChiTietVe child = (ThongTinChiTietVe) e.getChild();
                JButton btn = child.getButtonDienNhanh();

                // Luôn gán listener cho nút, bất kể currentQuickFilled là gì
                btn.addActionListener(evt -> {
                    if (!kiemTraThongTinKhachHang()) return;

                    // Nếu panel này đang quick-fill rồi, thì đây là click "Hủy"
                    if (child == currentQuickFilled) {
                        // Xóa thông tin ở chính panel đó
                        child.xoaThongTin();
                        // Mở khóa tất cả panel
                        unlockAllDetailPanels();
                        // Reset label cho tất cả nút
                        updateAllButtonsLabel("Điền nhanh");
                        currentQuickFilled = null;
                    }
                    else {
                        // Đây là lần bấm "Điền nhanh" mới
                        // 1) Xóa dữ liệu panel cũ nếu có
                        if (currentQuickFilled != null) {
                            currentQuickFilled.xoaThongTin();
                        }
                        // 2) Điền dữ liệu vào panel mới
                        String hoten = textFieldHotenKH.getText().trim();
                        String cccd  = textFieldCCCDKH.getText().trim();
                        child.dienThongTinVe(hoten, cccd, 1990);

                        currentQuickFilled = child;
                        // 3) Khoá mọi panel khác, mở khoá panel này
                        lockAllExcept(child);
                        // 4) Đổi label cho tất cả nút: panel này "Hủy", panel khác cũng "Hủy"
                        btn.setText("Hủy điền nhanh");
                    }
                });
            }
        });
    }

    private void lockAllExcept(ThongTinChiTietVe allow) {
        for (Component c : jPanelThongTinVe.getComponents()) {
            if (c instanceof ThongTinChiTietVe p) {
                if (p == allow) p.unlockButtonDienNhanh();
                else p.lockButtonDienNhanh();
            }
        }
    }

    private void unlockAllDetailPanels() {
        for (Component c : jPanelThongTinVe.getComponents()) {
            if (c instanceof ThongTinChiTietVe p) {
                p.unlockButtonDienNhanh();
            }
        }
    }

    private void updateAllButtonsLabel(String text) {
        for (Component c : jPanelThongTinVe.getComponents()) {
            if (c instanceof ThongTinChiTietVe p) {
                p.getButtonDienNhanh().setText(text);
            }
        }
    }
    public JLabel getjLabelTittle() {
        return jLabelTittle;
    }

    public void setjLabelTittle(String title) {
        this.jLabelTittle.setText(title);
    }
    
    public ChuyenDi getChuyenDi() {
        return chuyenDi;
    }

    public void setChuyenDi(ChuyenDi chuyenDi) {
        this.chuyenDi = chuyenDi;
    }

    public TextField getTextFieldCCCDKH() {
        return textFieldCCCDKH;
    }

    public TextField getTextFieldHotenKH() {
        return textFieldHotenKH;
    }

    public TextField getTextFieldSDTKH() {
        return textFieldSDTKH;
    }
    
    
    private void hienThiTatCaToaTau(){
        ArrayList<Ghe> gheDaDat = chuyendi_dao.timTatCaGheDaDatCuaTauTheoChuyenDi(chuyenDi.getTau().getMaTau(),chuyenDi.getMaChuyenDi());
        
        ArrayList<ToaTau> listToaTau = toatau_dao.layCacToaCuaTau(chuyenDi.getTau().getMaTau());
        ArrayList<Ghe> listGhe=null;
        for(ToaTau tt:listToaTau){
            int loaiToa = 0;
            ArrayList<KhoangTau> listKhoangTau = khoangtau_dao.layKhoangTauTheoToaTau(tt.getMaToaTau());
            for(KhoangTau kt : listKhoangTau){
                switch (kt.getSoGhe()) {
                    case 8 -> loaiToa = 1;
                    case 6 -> loaiToa = 2;
                    case 4 -> loaiToa = 3;
                    default -> {
                    }
                }
                
            }
            char soToa = tt.getMaToaTau().charAt(tt.getMaToaTau().length() - 1);
            switch (loaiToa) {
                case 1 -> {
                    GheNgoiMem ghengoimem = new GheNgoiMem(soToa,chuyenDi,gheDaDat,tt, listKhoangTau,jPanelThongTinVe);
                    jPanelTatCaToa.add(ghengoimem);
                }
                case 2 -> {
                    GiuongKhoang6 giuong6 = new GiuongKhoang6(soToa,chuyenDi,gheDaDat,tt, listKhoangTau,jPanelThongTinVe);
                    jPanelTatCaToa.add(giuong6);
                }
                case 3 -> {
                    GiuongKhoang4 giuong4 = new GiuongKhoang4(soToa,chuyenDi,gheDaDat,tt, listKhoangTau,jPanelThongTinVe);
                    jPanelTatCaToa.add(giuong4);
                }
                default -> {
                }
            }
        }
        jPanelTatCaToa.revalidate();
        jPanelTatCaToa.repaint();
        
    }

    public button getButtonTitle1() {
        return buttonTitle1;
    }

    public button getButtonTitle2() {
        return buttonTitle2;
    }

    public button getButtonTitle3() {
        return buttonTitle3;
    }
    
    public JButton getButtonQuayLai() {
        return buttonQuayLai;
    }

    public JButton getButtonTiepTuc() {
        return buttonTiepTuc;
    }
    public KhachHang layThongTinKhachHang(){
        
        String hoten = textFieldHotenKH.getText().trim();
        String sdt = textFieldSDTKH.getText().trim();
        String cccd = textFieldCCCDKH.getText().trim();
        khachHang = new KhachHang(hoten, sdt, cccd);
        return khachHang;
    }
    public ArrayList<Ve> layTatCaVeDuocChon(){
        ArrayList<Ve> listVe = new ArrayList<>();
        for(Component c : jPanelThongTinVe.getComponents()){
            if(c instanceof ThongTinChiTietVe chiTietVe){
                listVe.add(chiTietVe.taoVeTheoThongTin());
            }
        }
        return listVe;
    }
    public boolean kiemTraThongTinKhachHang(){
        String hoten = textFieldHotenKH.getText().trim();
        String sdt = textFieldSDTKH.getText().trim();
        String cccd = textFieldCCCDKH.getText().trim();
        // Kiểm tra họ tên
        if (hoten.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập họ tên của khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra số điện thoại (10 chữ số, bắt đầu từ số 0)
        if (!sdt.matches("0\\d{9}")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại khách hàng phải gồm 10 chữ số và bắt đầu bằng số 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra CCCD (12 chữ số)
        if (!cccd.matches("\\d{12}")) {
            JOptionPane.showMessageDialog(null, "CCCD khách hàng phải gồm đúng 12 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    public boolean kiemTraThongTinVe(){
        
        for (Component c : jPanelThongTinVe.getComponents()) {
            if (c instanceof ThongTinChiTietVe p) {
                if(p.kiemTraThongTinVe() == false){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rSMaterialButtonCircle1 = new rojerusan.RSMaterialButtonCircle();
        buttonTitle1 = new guiCustom.button();
        rSMaterialButtonCircle2 = new rojerusan.RSMaterialButtonCircle();
        buttonTitle2 = new guiCustom.button();
        buttonTitle3 = new guiCustom.button();
        rSMaterialButtonCircle4 = new rojerusan.RSMaterialButtonCircle();
        jPanel1 = new javax.swing.JPanel();
        jLabelTittle = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelTatCaToa = new javax.swing.JPanel();
        button3 = new guiCustom.button();
        button5 = new guiCustom.button();
        button6 = new guiCustom.button();
        button7 = new guiCustom.button();
        button8 = new guiCustom.button();
        jLabel18 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanelThongTinVe = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        textFieldHotenKH = new guiCustom.TextField();
        jLabel9 = new javax.swing.JLabel();
        textFieldSDTKH = new guiCustom.TextField();
        jLabel10 = new javax.swing.JLabel();
        textFieldCCCDKH = new guiCustom.TextField();
        buttonTiepTuc = new guiCustom.button();
        buttonQuayLai = new guiCustom.button();

        setBackground(new java.awt.Color(248, 251, 255));

        rSMaterialButtonCircle1.setBackground(new java.awt.Color(184, 184, 184));
        rSMaterialButtonCircle1.setForeground(new java.awt.Color(0, 0, 0));
        rSMaterialButtonCircle1.setText("1");
        rSMaterialButtonCircle1.setFont(new java.awt.Font("Roboto Medium", 1, 17)); // NOI18N
        rSMaterialButtonCircle1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle1ActionPerformed(evt);
            }
        });

        buttonTitle1.setBackground(new java.awt.Color(212, 212, 212));
        buttonTitle1.setForeground(new java.awt.Color(0, 0, 0));
        buttonTitle1.setText("CHỌN CHUYẾN");
        buttonTitle1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        buttonTitle1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonTitle1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTitle1ActionPerformed(evt);
            }
        });

        rSMaterialButtonCircle2.setBackground(new java.awt.Color(7, 124, 209));
        rSMaterialButtonCircle2.setText("2");
        rSMaterialButtonCircle2.setFont(new java.awt.Font("Roboto Medium", 1, 17)); // NOI18N
        rSMaterialButtonCircle2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle2ActionPerformed(evt);
            }
        });

        buttonTitle2.setBackground(new java.awt.Color(7, 124, 209));
        buttonTitle2.setForeground(new java.awt.Color(255, 255, 255));
        buttonTitle2.setText("CHI TIẾT VÉ");
        buttonTitle2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        buttonTitle2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonTitle2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTitle2ActionPerformed(evt);
            }
        });

        buttonTitle3.setBackground(new java.awt.Color(212, 212, 212));
        buttonTitle3.setForeground(new java.awt.Color(0, 0, 0));
        buttonTitle3.setText("THANH TOÁN");
        buttonTitle3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        buttonTitle3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonTitle3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTitle3ActionPerformed(evt);
            }
        });

        rSMaterialButtonCircle4.setBackground(new java.awt.Color(184, 184, 184));
        rSMaterialButtonCircle4.setForeground(new java.awt.Color(0, 0, 0));
        rSMaterialButtonCircle4.setText("3");
        rSMaterialButtonCircle4.setFont(new java.awt.Font("Roboto Medium", 1, 17)); // NOI18N
        rSMaterialButtonCircle4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle4ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelTittle.setBackground(new java.awt.Color(207, 223, 254));
        jLabelTittle.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabelTittle.setForeground(new java.awt.Color(65, 165, 238));
        jLabelTittle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/chonChuyen.png"))); // NOI18N
        jLabelTittle.setText("TUYẾN ABC -> XYZ, NGÀY 13/04 18:10");
        jLabelTittle.setToolTipText("");
        jLabelTittle.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5));
        jLabelTittle.setIconTextGap(20);
        jLabelTittle.setOpaque(true);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanelTatCaToa.setLayout(new javax.swing.BoxLayout(jPanelTatCaToa, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanelTatCaToa);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
        );

        button3.setBackground(new java.awt.Color(77, 160, 166));
        button3.setEnabled(false);
        button3.setPreferredSize(new java.awt.Dimension(25, 25));

        button5.setBackground(new java.awt.Color(129, 185, 226));
        button5.setEnabled(false);
        button5.setPreferredSize(new java.awt.Dimension(25, 25));
        button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button5ActionPerformed(evt);
            }
        });

        button6.setBackground(new java.awt.Color(70, 184, 122));
        button6.setEnabled(false);
        button6.setPreferredSize(new java.awt.Dimension(25, 25));
        button6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button6ActionPerformed(evt);
            }
        });

        button7.setBackground(new java.awt.Color(252, 90, 90));
        button7.setEnabled(false);
        button7.setPreferredSize(new java.awt.Dimension(25, 25));
        button7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button7ActionPerformed(evt);
            }
        });

        button8.setBackground(new java.awt.Color(146, 146, 146));
        button8.setEnabled(false);
        button8.setPreferredSize(new java.awt.Dimension(25, 25));
        button8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button8ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 98, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("CHỌN VỊ TRÍ");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Ghế Ngồi Mềm Điều Hòa");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Giường Nằm Khoang 6 Điều Hòa");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Giường Nằm Khoang 4 Điều Hòa");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Ghế Đang Chọn");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Ghế Đã Chọn");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTittle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(78, 78, 78)
                .addComponent(button6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(97, 97, 97)
                .addComponent(button7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(button8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(27, 27, 27))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabelTittle, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setBackground(new java.awt.Color(207, 223, 254));
        jLabel6.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(65, 165, 238));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/info.png"))); // NOI18N
        jLabel6.setText("THÔNG TIN KHÁCH HÀNG");
        jLabel6.setToolTipText("");
        jLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5));
        jLabel6.setIconTextGap(20);
        jLabel6.setOpaque(true);

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanelThongTinVe.setLayout(new javax.swing.BoxLayout(jPanelThongTinVe, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(jPanelThongTinVe);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
        );

        jLabel8.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(65, 165, 238));
        jLabel8.setText("Họ Tên");

        textFieldHotenKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldHotenKHActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(65, 165, 238));
        jLabel9.setText("Số Điện Thoại");

        textFieldSDTKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldSDTKHActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(65, 165, 238));
        jLabel10.setText("CCCD");

        textFieldCCCDKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldCCCDKHActionPerformed(evt);
            }
        });

        buttonTiepTuc.setBackground(new java.awt.Color(51, 171, 59));
        buttonTiepTuc.setForeground(new java.awt.Color(255, 255, 255));
        buttonTiepTuc.setText("Tiếp Tục");
        buttonTiepTuc.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        buttonTiepTuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTiepTucActionPerformed(evt);
            }
        });

        buttonQuayLai.setBackground(new java.awt.Color(0, 98, 255));
        buttonQuayLai.setForeground(new java.awt.Color(255, 255, 255));
        buttonQuayLai.setText("Quay Lại");
        buttonQuayLai.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        buttonQuayLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonQuayLaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldHotenKH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textFieldSDTKH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(textFieldCCCDKH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(32, 32, 32))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(306, 306, 306)
                .addComponent(buttonQuayLai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonTiepTuc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldHotenKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldSDTKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldCCCDKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonTiepTuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonQuayLai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(rSMaterialButtonCircle1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonTitle1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(191, 191, 191)
                        .addComponent(rSMaterialButtonCircle2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonTitle2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(182, 182, 182)
                        .addComponent(rSMaterialButtonCircle4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonTitle3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(71, 71, 71))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(27, 27, 27)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(23, 23, 23))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(buttonTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonTitle3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rSMaterialButtonCircle4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rSMaterialButtonCircle2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rSMaterialButtonCircle1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonTitle1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rSMaterialButtonCircle2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonCircle2ActionPerformed

    private void rSMaterialButtonCircle4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonCircle4ActionPerformed

    private void buttonTitle2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTitle2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonTitle2ActionPerformed

    private void rSMaterialButtonCircle1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonCircle1ActionPerformed

    private void buttonTitle1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTitle1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonTitle1ActionPerformed

    private void buttonTitle3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTitle3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonTitle3ActionPerformed

    private void button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button5ActionPerformed

    private void button6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button6ActionPerformed

    private void button7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button7ActionPerformed

    private void button8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button8ActionPerformed

    private void textFieldHotenKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldHotenKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldHotenKHActionPerformed

    private void textFieldSDTKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldSDTKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldSDTKHActionPerformed

    private void textFieldCCCDKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldCCCDKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldCCCDKHActionPerformed

    private void buttonQuayLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonQuayLaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonQuayLaiActionPerformed

    private void buttonTiepTucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTiepTucActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_buttonTiepTucActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private guiCustom.button button3;
    private guiCustom.button button5;
    private guiCustom.button button6;
    private guiCustom.button button7;
    private guiCustom.button button8;
    private guiCustom.button buttonQuayLai;
    private guiCustom.button buttonTiepTuc;
    private guiCustom.button buttonTitle1;
    private guiCustom.button buttonTitle2;
    private guiCustom.button buttonTitle3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelTittle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelTatCaToa;
    private javax.swing.JPanel jPanelThongTinVe;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle1;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle2;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle4;
    private guiCustom.TextField textFieldCCCDKH;
    private guiCustom.TextField textFieldHotenKH;
    private guiCustom.TextField textFieldSDTKH;
    // End of variables declaration//GEN-END:variables
}
