/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.ve;

import dao.ChuyenDi_DAO;
import dao.ThongKeDAOImpl;
import entity.CaHienTai;
import entity.ChuyenDi;
import entity.NhanVien;
import entity.Ve;
import gui.app.ve.DatVe;
import gui.app.ve.DatVe_ChiTietVe;
import gui.app.ve.DatVe_ChonChuyen;
import gui.app.ve.DatVe_KhuHoi_ChonChuyen;
import gui.app.ve.DatVe_ThanhToan_HinhThucThanhToan;
import gui.app.ve.DatVe_ThanhToans;
import java.awt.CardLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author PHAMGIAKHANH
 */
public class DatVeRunAll extends javax.swing.JPanel {
    
    private CardLayout cardLayout;
    DatVe datve = new DatVe();
    DatVe_ChonChuyen dv_chonChuyen = new DatVe_ChonChuyen();
    DatVe_KhuHoi_ChonChuyen dv_kh_chonChuyen = new DatVe_KhuHoi_ChonChuyen();
    DatVe_ChiTietVe dv_chiTietVe = new DatVe_ChiTietVe();
    DatVe_ThanhToans dv_thanhtoan = new DatVe_ThanhToans();
    private ChuyenDi chuyenDi;
    DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("dd/MM/YYYY");
    DateTimeFormatter fmTime = DateTimeFormatter.ofPattern("HH:mm");
    private ThongKeDAOImpl dao = new ThongKeDAOImpl();
    
    private ArrayList<Ve> listVe = new ArrayList<>();
    private NhanVien nhanVien;
    /**
     * Creates new form DatVeRunAll
     */
    public DatVeRunAll(NhanVien nv) {
        initComponents();
        header1.jLabel14.setText("Đặt Vé");
        header1.jButton4.setText(nv.getTenNV());
        nhanVien = nv;
        //datve.setBounds(0, 0, jPanelChayDatVe.getWidth(), jPanelChayDatVe.getHeight());
        cardLayout = new CardLayout();
        jPanelChayDatVe.setLayout(cardLayout);
        jPanelChayDatVe.add(datve,"DATVE");
        jPanelChayDatVe.add(dv_chonChuyen,"DATVE_CHONCHUYEN");
        jPanelChayDatVe.add(dv_kh_chonChuyen,"DATVE_KHUHOI_CHONCHUYEN");
        jPanelChayDatVe.add(dv_chiTietVe,"DATVE_CHITIETVE");
        jPanelChayDatVe.add(dv_thanhtoan,"DATVE_THANHTOAN");
        cardLayout.show(jPanelChayDatVe,"DATVE");
        jPanelChayDatVe.validate();
        jPanelChayDatVe.repaint();
        datve.getBtnTimKiem().addActionListener(e -> {
                timKiemChuyenDi();  
            });
        dv_chonChuyen.getButtonTroVe().addActionListener(e -> hienThiJPanel("DATVE"));
        
        dv_chiTietVe.getButtonQuayLai().addActionListener(e -> hienThiJPanel("DATVE_CHONCHUYEN"));
        dv_chiTietVe.getButtonTiepTuc().addActionListener(e -> {
            if(dv_chiTietVe.kiemTraThongTinKhachHang() && dv_chiTietVe.kiemTraThongTinVe()){
                
                listVe = dv_chiTietVe.layTatCaVeDuocChon();
                dv_thanhtoan.setListVe(listVe);
                dv_thanhtoan.setNhanVien(nhanVien);
                CaHienTai caHienTai = dao.getThongTinCaHienTai();
                dv_thanhtoan.setMaCa(caHienTai.getMaCa());
                chuyenDi = dv_chiTietVe.getChuyenDi();
                dv_thanhtoan.getjLabelTitleMaTau().setText("Tàu: " + chuyenDi.getTau().getTenTau());
                dv_thanhtoan.getjLabelGaDi().setText(chuyenDi.getThoiGianDiChuyen().getGaDi().getTenGa());
                dv_thanhtoan.getjLabelGaDen().setText(chuyenDi.getThoiGianDiChuyen().getGaDen().getTenGa());
                String timekhoihanh = chuyenDi.getThoiGianKhoiHanh().format(fmTime)+ " " + chuyenDi.getThoiGianKhoiHanh().format(fmDate);
                dv_thanhtoan.getjLabelThoiGianKhoiHanh().setText(timekhoihanh);
                String timeden = chuyenDi.getThoiGianDenDuTinh().format(fmTime)+ " " + chuyenDi.getThoiGianKhoiHanh().format(fmDate);
                dv_thanhtoan.getjLabelthoiGianDenDuTinh().setText(timeden);
                dv_thanhtoan.setKhachHang(dv_chiTietVe.layThongTinKhachHang());
                dv_thanhtoan.setCardLayout(cardLayout);
                dv_thanhtoan.setjPanelChayDatVe(jPanelChayDatVe);
                hienThiJPanel("DATVE_THANHTOAN");
            }
            
            
        });
        
        dv_thanhtoan.getbuttontrove().addActionListener(e -> hienThiJPanel("DATVE"));
        
        dv_kh_chonChuyen.getButtonTroVe().addActionListener(e -> hienThiJPanel("DATVE"));
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }
    private void timKiemChuyenDi(){
        if(datve.getBtnLoaiChuyenDi().isSelected()==true){
            ArrayList<ChuyenDi> listChuyenDi = datve.timChuyenDi();
                
            if(listChuyenDi != null && !listChuyenDi.isEmpty()){
                System.out.println(listChuyenDi.size());
                hienThiJPanel("DATVE_CHONCHUYEN");
                dv_chonChuyen.hienThiTatCaChuyenDi(datve.timChuyenDi(),cardLayout,jPanelChayDatVe,dv_chiTietVe);
                String title = String.format("Tuyến %s -> %s, Ngày %s",datve.getGaDi(),datve.getGaDen(),datve.getNgayDi());
                dv_chonChuyen.getjLabelTieuDe().setText(title);
            }else{
                JOptionPane.showMessageDialog(datve, "Không tìm thấy chuyến đi!");
            }
        }else{
            ArrayList<ChuyenDi> listChuyenDi = datve.timChuyenDi();
            ArrayList<ChuyenDi> listChuyenVe = datve.timChuyenVe();
            
            if(listChuyenDi.isEmpty()){
                JOptionPane.showMessageDialog(datve, "Không tìm thấy chuyến đi!");
            }else if( listChuyenVe.isEmpty()){
                JOptionPane.showMessageDialog(datve, "Không tìm thấy chuyến về!");
            }else{
                System.out.println(listChuyenDi.size());
                hienThiJPanel("DATVE_KHUHOI_CHONCHUYEN");
                dv_kh_chonChuyen.hienThiTatCaChuyenDi(listChuyenDi, cardLayout, jPanelChayDatVe, dv_chiTietVe);
                dv_kh_chonChuyen.hienThiTatCaChuyenDi(listChuyenVe, cardLayout, jPanelChayDatVe, dv_chiTietVe);
            }
            
        }
    }
    private void hienThiJPanel(String maPanel){
        cardLayout.show(jPanelChayDatVe,maPanel);
        jPanelChayDatVe.revalidate();
        jPanelChayDatVe.repaint();
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        header1 = new component.Header();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelChayDatVe = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanelChayDatVeLayout = new javax.swing.GroupLayout(jPanelChayDatVe);
        jPanelChayDatVe.setLayout(jPanelChayDatVeLayout);
        jPanelChayDatVeLayout.setHorizontalGroup(
            jPanelChayDatVeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1384, Short.MAX_VALUE)
        );
        jPanelChayDatVeLayout.setVerticalGroup(
            jPanelChayDatVeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelChayDatVe);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header1, javax.swing.GroupLayout.DEFAULT_SIZE, 1384, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Header header1;
    private javax.swing.JPanel jPanelChayDatVe;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
