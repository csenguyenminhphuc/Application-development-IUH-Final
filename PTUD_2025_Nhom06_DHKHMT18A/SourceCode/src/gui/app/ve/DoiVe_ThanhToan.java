/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.ve;

import dao.HanhKhach_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.KhuyenMai_DAO;
import dao.Ve_DAO;
import entity.Ca;
import entity.HanhKhach;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.KhuyenMaiResult;
import entity.NhanVien;
import entity.Ve;
import gui.other.TaoHoaDon;
import gui.other.TaoHoaDonDoi;
import gui.other.TaoVe;
import guiCustom.button;
import java.awt.CardLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 *
 * @author PHAMGIAKHANH
 */
public class DoiVe_ThanhToan extends javax.swing.JPanel {
    private CardLayout cardLayout;
    private JPanel jPanelChayDatVe;
    private KhuyenMai_DAO km_dao = new KhuyenMai_DAO();
    private HoaDon_DAO hd_dao = new HoaDon_DAO();
    private Ve_DAO ve_dao = new Ve_DAO();
    private KhachHang_DAO kh_dao = new KhachHang_DAO();
    private HanhKhach_DAO hk_dao = new HanhKhach_DAO();
    private ArrayList<Ve> listVe = new ArrayList<>();
    private Ve ve;
    private Ve vedoi;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private KhuyenMaiResult kmr;
    private double tongTien;
    private double tienHuyVe;
    private DatVe_ThanhToan_HinhThucThanhToan dv_httt = new DatVe_ThanhToan_HinhThucThanhToan();
    private DatVe_ThanhToan_QRThanhToan qrtt;
    private HoaDon hd;
    private Ca ca;
    private boolean flag = false;
    DatVe_ThanhToan_InHoaDon dv_tt_ihd;
    private String maCa;
        
    /**
     * Creates new form DatVe_ChonChuyen
     */
    public DoiVe_ThanhToan() {
        initComponents();
        dv_tt_ihd = new DatVe_ThanhToan_InHoaDon(cardLayout,jPanelChayDatVe,tongTien);
        dv_tt_ihd.getButtonInHoaDon().addActionListener(e -> {
            TaoHoaDonDoi.taoHD(listVe, hd, kmr,tienHuyVe);
            flag = true;
        });
        dv_tt_ihd.getButtonInVe().addActionListener(e -> {
            for (Ve v : listVe) TaoVe.taoVe(v);
            flag = true;
        });
        dv_tt_ihd.getButtonTroVe().addActionListener(e -> {
            if (flag) {
                cardLayout.show(jPanelChayDatVe, "DOIVE_TIMVEDOI");
                jPanelChayDatVe.revalidate();
                jPanelChayDatVe.repaint();
                flag = false;
            } else {
                JOptionPane.showMessageDialog(this,
                  "Bạn cần In vé hoặc In hóa đơn trước khi trở về!");
            }
        });
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                hienThiTatCaChiTietVe();
                capNhatThongTinHoaDon();
                themPhuongThucThanhToan();
                System.out.println(".componentShown()");
            }
        });
        
        
    }

    public JLabel getjLabelGaDen() {
        return jLabelGaDen;
    }

    public JLabel getjLabelGaDi() {
        return jLabelGaDi;
    }

    public JLabel getjLabelThoiGianKhoiHanh() {
        return jLabelThoiGianKhoiHanh;
    }

    public JLabel getjLabelTitleMaTau() {
        return jLabelTitleMaTau;
    }

    public JLabel getjLabelthoiGianDenDuTinh() {
        return jLabelthoiGianDenDuTinh;
    }

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }
    
    
    public Ve getVedoi() {
        return vedoi;
    }

    public void setVedoi(Ve vedoi) {
        this.vedoi = vedoi;
    }

    public void setjPanelChayDatVe(JPanel jPanelChayDatVe) {
        this.jPanelChayDatVe = jPanelChayDatVe;
    }

    public void setCardLayout(CardLayout cardLayout) {
        this.cardLayout = cardLayout;
    }
    

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Ve getVe() {
        return ve;
    }

    public void setVe(Ve ve) {
        this.ve = ve;
    }
    
    public Ca getCa() {
        return ca;
    }

    public void setCa(Ca ca) {
        this.ca = ca;
    }
    
    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public HoaDon getHd() {
        return hd;
    }

    public void setHd(HoaDon hd) {
        this.hd = hd;
    }

    public ArrayList<Ve> getListVe() {
        return listVe;
    }

    public void setListVe(ArrayList<Ve> listVe) {
        this.listVe = listVe;
    }

    public double getTienHuyVe() {
        return tienHuyVe;
    }

    public void setTienHuyVe(double tienHuyVe) {
        this.tienHuyVe = tienHuyVe;
    }
    
    public boolean themHoaDonHuyVe(){
        try {
            HoaDon hdh = new HoaDon();
            hdh.setNgayLapHoaDon(LocalDateTime.now());
            hdh.setVAT(0);
            hdh.setSoVe(0);
            hdh.setTongTien(-tienHuyVe);
            hdh.setNhanVien(nhanVien);
            hdh.setKhachHang(khachHang);
            KhuyenMai km = null;
            hdh.setKhuyenMai(km);
            String mahoadon = hd_dao.themHoaDon(hdh,maCa);
            System.out.println("ma hoa don huy: "+mahoadon );
            hdh.setMaHoaDon(mahoadon);
            hd_dao.updateTongTienHoaDon(hdh);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }
    public boolean themHoaDonVaoDB(){
        try {
            hd = new HoaDon();
            hd.setNgayLapHoaDon(LocalDateTime.now());
            hd.setVAT(0.1);
            hd.setSoVe(1);
            hd.setTongTien(tongTien);
            //nhanVien = new NhanVien();
            //nhanVien.setMaNV("NV-0-00-853-145");
            hd.setNhanVien(nhanVien);
            hd.setKhachHang(khachHang);
            hd.setKhuyenMai(kmr.getKhuyenMai());
            String mahoadon = hd_dao.themHoaDon(hd,"CA-01");
            System.out.println("ma hoa don doi: "+mahoadon );
            hd.setMaHoaDon(mahoadon);
            hd_dao.updateTongTienHoaDon(hd);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean themVeVaoDB(){
        try {
            ve = listVe.get(0);
            ve.setHoaDon(hd);
            HanhKhach hk_c = hk_dao.timHanhKhachTheoCCCD(ve.getHanhKhach().getCccd());
            if(hk_c != null){
                ve.setHanhKhach(hk_c);
                System.out.println("ma hanh khach: "+ve.getHanhKhach().getMaHanhKhach() );
            }else{
                String mahk = hk_dao.themMotHanhKhach(ve.getHanhKhach());
                System.out.println("ma hanh khach: "+mahk );
                ve.getHanhKhach().setMaHanhKhach(mahk);
            }
            String maVe = ve_dao.themVe(ve);
            System.out.println("ma ve: "+ maVe);
            ve.setMaVe(maVe);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void hienThiQRThanhToan(){
        qrtt = new DatVe_ThanhToan_QRThanhToan(cardLayout,jPanelChayDatVe,tongTien);
        qrtt.getButtonQuayLai().addActionListener(e -> {
            themPhuongThucThanhToan();
        });
        qrtt.getButtonXNTT().addActionListener(e -> {
            try {
                if(themHoaDonHuyVe() && themHoaDonVaoDB() && themVeVaoDB() && capNhatTrangThaiVe()){
                    hienThiInHoaDon();
                }else{
                    JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi!");
                }
            } catch (SQLException ex) {
                Logger.getLogger(DoiVe_ThanhToan.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        jPanelPTTT.removeAll();
        jPanelPTTT.add(qrtt);
        jPanelPTTT.revalidate();
        jPanelPTTT.repaint();
    }
    public boolean capNhatTrangThaiVe() throws SQLException{
        return ve_dao.capNhatTrangThaiVe(vedoi.getMaVe());
    }
    public button getbuttontrove(){
        return dv_httt.getButtonQuayLai();
    }
    public void themPhuongThucThanhToan(){
        jPanelPTTT.removeAll();
        dv_httt = new DatVe_ThanhToan_HinhThucThanhToan(cardLayout,jPanelChayDatVe,tongTien);
        dv_httt.getButtonTiepTuc().addActionListener(e -> {
            
            if(dv_httt.getjRadioButtonCK().isSelected()){
                hienThiQRThanhToan();
            }else{
                try {
                    if(themHoaDonHuyVe() && themHoaDonVaoDB() && themVeVaoDB() && capNhatTrangThaiVe()){
                        hienThiInHoaDon();
                    }else{
                        JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi!");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DoiVe_ThanhToan.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        jPanelPTTT.add(dv_httt);
        jPanelPTTT.revalidate();
        jPanelPTTT.repaint();
    }
    public void hienThiInHoaDon(){
        flag = false;
        dv_tt_ihd.setTongTien(tongTien);
        
        jPanelPTTT.removeAll();
        jPanelPTTT.add(dv_tt_ihd);
        jPanelPTTT.revalidate();
        jPanelPTTT.repaint();
    }
    public void capNhatThongTinHoaDon(){
        textFieldTenKh.setText(khachHang.getTenKH());
        textFieldSdtKh.setText(khachHang.getSoDienThoai());
        //textFieldNV.setText(nhanVien.getTenNV());
        textFieldNgayLap.setText(LocalDate.now().toString());
        double thanhTien = tinhThanhTien();
        textFieldThanhTien.setText(String.valueOf(thanhTien));
        kmr = km_dao.timKhuyenMaiTotNhat(thanhTien);
        if(kmr == null){
            kmr = new KhuyenMaiResult();
            kmr.setTienGiam(0.0);
        }
        textFieldKm.setText("-"+String.valueOf(kmr.getTienGiam()));
        double tienVAT = 0.1 * thanhTien;
        textFieldVAT.setText(String.valueOf(tienVAT));
        textFieldPhiDoiVe.setText(String.valueOf(tienHuyVe));
        tongTien = thanhTien - kmr.getTienGiam() + tienVAT + tienHuyVe;
        textFieldTongTien.setText(String.valueOf(tongTien));
    }
    private double tinhThanhTien(){
        double thanhTien = 0.0;
        for(Ve ve: listVe){
            thanhTien += ve.getGiaVe();
        }
        return thanhTien;
    }
    public void hienThiTatCaChiTietVe(){
        jPanelChiTietVe_HoaDon.removeAll();
        for(Ve ve: listVe){
            DatVe_ChiTietVe_HoaDon ctvhd = new DatVe_ChiTietVe_HoaDon(ve);
            jPanelChiTietVe_HoaDon.add(ctvhd);
        }
        jPanelChiTietVe_HoaDon.revalidate();
        jPanelChiTietVe_HoaDon.repaint();
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
        button1 = new guiCustom.button();
        rSMaterialButtonCircle2 = new rojerusan.RSMaterialButtonCircle();
        button2 = new guiCustom.button();
        button4 = new guiCustom.button();
        rSMaterialButtonCircle4 = new rojerusan.RSMaterialButtonCircle();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelChiTietVe_HoaDon = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        textFieldTenKh = new guiCustom.TextField();
        jLabel16 = new javax.swing.JLabel();
        textFieldSdtKh = new guiCustom.TextField();
        jLabel20 = new javax.swing.JLabel();
        textFieldThanhTien = new guiCustom.TextField();
        jLabel21 = new javax.swing.JLabel();
        textFieldKm = new guiCustom.TextField();
        textFieldVAT = new guiCustom.TextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        textFieldNV = new guiCustom.TextField();
        textFieldNgayLap = new guiCustom.TextField();
        textFieldTongTien = new guiCustom.TextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        textFieldPhiDoiVe = new guiCustom.TextField();
        jLabelGaDi = new javax.swing.JLabel();
        jLabelThoiGianKhoiHanh = new javax.swing.JLabel();
        jLabelGaDen = new javax.swing.JLabel();
        jLabelthoiGianDenDuTinh = new javax.swing.JLabel();
        jLabelTitleMaTau = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanelPTTT = new javax.swing.JPanel();

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

        button1.setBackground(new java.awt.Color(212, 212, 212));
        button1.setForeground(new java.awt.Color(0, 0, 0));
        button1.setText("TÌM VÉ ĐỔI");
        button1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        button1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        rSMaterialButtonCircle2.setBackground(new java.awt.Color(212, 212, 212));
        rSMaterialButtonCircle2.setForeground(new java.awt.Color(0, 0, 0));
        rSMaterialButtonCircle2.setText("2");
        rSMaterialButtonCircle2.setFont(new java.awt.Font("Roboto Medium", 1, 17)); // NOI18N
        rSMaterialButtonCircle2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle2ActionPerformed(evt);
            }
        });

        button2.setBackground(new java.awt.Color(212, 212, 212));
        button2.setForeground(new java.awt.Color(0, 0, 0));
        button2.setText("CHỌN VÉ ĐỔI");
        button2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        button2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });

        button4.setBackground(new java.awt.Color(7, 124, 209));
        button4.setForeground(new java.awt.Color(255, 255, 255));
        button4.setText("THANH TOÁN");
        button4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        button4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });

        rSMaterialButtonCircle4.setBackground(new java.awt.Color(7, 124, 209));
        rSMaterialButtonCircle4.setText("3");
        rSMaterialButtonCircle4.setFont(new java.awt.Font("Roboto Medium", 1, 17)); // NOI18N
        rSMaterialButtonCircle4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle4ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setBackground(new java.awt.Color(207, 223, 254));
        jLabel5.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(65, 165, 238));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cthd.png"))); // NOI18N
        jLabel5.setText("CHI TIẾT HÓA ĐƠN");
        jLabel5.setToolTipText("");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5));
        jLabel5.setIconTextGap(20);
        jLabel5.setOpaque(true);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/muiTenChuyenDi - Copy.png"))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanelChiTietVe_HoaDon.setLayout(new javax.swing.BoxLayout(jPanelChiTietVe_HoaDon, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanelChiTietVe_HoaDon);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 102, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("THÔNG TIN HÓA ĐƠN");

        jLabel15.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 153, 255));
        jLabel15.setText("Họ Và Tên");

        textFieldTenKh.setText("Nguyễn Thành Trọng");
        textFieldTenKh.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        textFieldTenKh.setEnabled(false);
        textFieldTenKh.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        textFieldTenKh.setShadowColor(new java.awt.Color(121, 180, 206));

        jLabel16.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 153, 255));
        jLabel16.setText("Số Điện Thoại");

        textFieldSdtKh.setText("Nguyễn Thành Trọng");
        textFieldSdtKh.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        textFieldSdtKh.setEnabled(false);
        textFieldSdtKh.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        textFieldSdtKh.setShadowColor(new java.awt.Color(121, 180, 206));

        jLabel20.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 51, 204));
        jLabel20.setText("Thành Tiền");

        textFieldThanhTien.setText("Nguyễn Thành Trọng");
        textFieldThanhTien.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        textFieldThanhTien.setEnabled(false);
        textFieldThanhTien.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        textFieldThanhTien.setShadowColor(new java.awt.Color(121, 180, 206));

        jLabel21.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 51, 204));
        jLabel21.setText("Khuyến Mãi");

        textFieldKm.setText("Nguyễn Thành Trọng");
        textFieldKm.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        textFieldKm.setEnabled(false);
        textFieldKm.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        textFieldKm.setShadowColor(new java.awt.Color(121, 180, 206));

        textFieldVAT.setText("Nguyễn Thành Trọng");
        textFieldVAT.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        textFieldVAT.setEnabled(false);
        textFieldVAT.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        textFieldVAT.setShadowColor(new java.awt.Color(121, 180, 206));

        jLabel22.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 51, 204));
        jLabel22.setText("VAT(10%)");

        jLabel17.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 153, 255));
        jLabel17.setText("Nhân Viên");

        jLabel18.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 153, 255));
        jLabel18.setText("Ngày Lập");

        textFieldNV.setText("Nguyễn Thành Trọng");
        textFieldNV.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        textFieldNV.setEnabled(false);
        textFieldNV.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        textFieldNV.setShadowColor(new java.awt.Color(121, 180, 206));

        textFieldNgayLap.setText("Nguyễn Thành Trọng");
        textFieldNgayLap.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        textFieldNgayLap.setEnabled(false);
        textFieldNgayLap.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        textFieldNgayLap.setShadowColor(new java.awt.Color(121, 180, 206));

        textFieldTongTien.setForeground(new java.awt.Color(255, 51, 51));
        textFieldTongTien.setText("1.350.000 đ");
        textFieldTongTien.setDisabledTextColor(new java.awt.Color(255, 51, 51));
        textFieldTongTien.setEnabled(false);
        textFieldTongTien.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        textFieldTongTien.setShadowColor(new java.awt.Color(121, 180, 206));
        textFieldTongTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldTongTienActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 51, 51));
        jLabel23.setText("Tổng Tiền");

        jLabel24.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 51, 204));
        jLabel24.setText("Phí đổi vé");

        textFieldPhiDoiVe.setText("Nguyễn Thành Trọng");
        textFieldPhiDoiVe.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        textFieldPhiDoiVe.setEnabled(false);
        textFieldPhiDoiVe.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        textFieldPhiDoiVe.setShadowColor(new java.awt.Color(121, 180, 206));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(textFieldTenKh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(80, 80, 80)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(textFieldNV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(textFieldSdtKh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(80, 80, 80)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(textFieldNgayLap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(20, 20, 20)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(textFieldThanhTien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(textFieldKm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(342, 342, 342))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(textFieldVAT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(20, 20, 20)
                                                .addComponent(textFieldPhiDoiVe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGap(79, 79, 79)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(textFieldTongTien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(89, 89, 89)))))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldTenKh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldSdtKh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldNgayLap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(textFieldThanhTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(textFieldKm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(textFieldTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldPhiDoiVe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldVAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabelGaDi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelGaDi.setForeground(new java.awt.Color(65, 165, 238));
        jLabelGaDi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelGaDi.setText("MỸ TRẠCH");

        jLabelThoiGianKhoiHanh.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelThoiGianKhoiHanh.setForeground(new java.awt.Color(255, 51, 51));
        jLabelThoiGianKhoiHanh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelThoiGianKhoiHanh.setText("18:10 13/04/2025");

        jLabelGaDen.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelGaDen.setForeground(new java.awt.Color(65, 165, 238));
        jLabelGaDen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelGaDen.setText("SÀI GÒN");

        jLabelthoiGianDenDuTinh.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelthoiGianDenDuTinh.setForeground(new java.awt.Color(255, 51, 51));
        jLabelthoiGianDenDuTinh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelthoiGianDenDuTinh.setText("18:10 14/04/2025");

        jLabelTitleMaTau.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTitleMaTau.setForeground(new java.awt.Color(255, 51, 51));
        jLabelTitleMaTau.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitleMaTau.setText("TÀU: TAU-SK7");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 102, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("CHI TIẾT VÉ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabelThoiGianKhoiHanh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jLabelGaDi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(31, 31, 31)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelthoiGianDenDuTinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabelGaDen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(31, 31, 31)))
                .addGap(51, 51, 51))
            .addComponent(jLabelTitleMaTau, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelGaDen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelthoiGianDenDuTinh))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelTitleMaTau)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabelGaDi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelThoiGianKhoiHanh))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11)))))
                .addGap(52, 52, 52)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(169, 169, 169)
                    .addComponent(jLabel14)
                    .addContainerGap(491, Short.MAX_VALUE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setBackground(new java.awt.Color(207, 223, 254));
        jLabel6.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(65, 165, 238));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pttt.png"))); // NOI18N
        jLabel6.setText("PHƯƠNG THỨC THANH TOÁN");
        jLabel6.setToolTipText("");
        jLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5));
        jLabel6.setIconTextGap(20);
        jLabel6.setOpaque(true);

        jPanelPTTT.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelPTTT.setLayout(new javax.swing.BoxLayout(jPanelPTTT, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(jPanelPTTT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(67, 67, 67))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82)
                .addComponent(jPanelPTTT, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
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
                        .addComponent(button1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(191, 191, 191)
                        .addComponent(rSMaterialButtonCircle2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(182, 182, 182)
                        .addComponent(rSMaterialButtonCircle4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rSMaterialButtonCircle4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rSMaterialButtonCircle2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rSMaterialButtonCircle1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button2ActionPerformed

    private void rSMaterialButtonCircle1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rSMaterialButtonCircle1ActionPerformed

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button1ActionPerformed

    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button4ActionPerformed

    private void textFieldTongTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldTongTienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldTongTienActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private guiCustom.button button1;
    private guiCustom.button button2;
    private guiCustom.button button4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelGaDen;
    private javax.swing.JLabel jLabelGaDi;
    private javax.swing.JLabel jLabelThoiGianKhoiHanh;
    private javax.swing.JLabel jLabelTitleMaTau;
    private javax.swing.JLabel jLabelthoiGianDenDuTinh;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelChiTietVe_HoaDon;
    private javax.swing.JPanel jPanelPTTT;
    private javax.swing.JScrollPane jScrollPane1;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle1;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle2;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle4;
    private guiCustom.TextField textFieldKm;
    private guiCustom.TextField textFieldNV;
    private guiCustom.TextField textFieldNgayLap;
    private guiCustom.TextField textFieldPhiDoiVe;
    private guiCustom.TextField textFieldSdtKh;
    private guiCustom.TextField textFieldTenKh;
    private guiCustom.TextField textFieldThanhTien;
    private guiCustom.TextField textFieldTongTien;
    private guiCustom.TextField textFieldVAT;
    // End of variables declaration//GEN-END:variables
}
