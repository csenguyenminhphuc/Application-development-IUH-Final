/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.ve;


import dao.ChuyenDi_DAO;
import dao.ThongKeDAOImpl;
import dao.Ve_DAO;
import entity.CaHienTai;
import entity.ChuyenDi;
import entity.NhanVien;
import entity.Ve;
import java.awt.CardLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 *
 * @author PHAMGIAKHANH
 */
public class DoiVeRunAll extends javax.swing.JPanel {
    
    private CardLayout cardLayout;
    DoiVe_TimVeDoi doive_timvedoi = new DoiVe_TimVeDoi();
    DoiVe_ChonVeDoi doive_chonchuyen = new DoiVe_ChonVeDoi();
    DatVe_ChiTietVe doive_chitietve = new DatVe_ChiTietVe();
    DoiVe_ThanhToan doive_thanhtoan = new DoiVe_ThanhToan();
    
    private ChuyenDi chuyenDi;
    DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("dd/MM/YYYY");
    DateTimeFormatter fmTime = DateTimeFormatter.ofPattern("HH:mm");
    
    private ThongKeDAOImpl dao = new ThongKeDAOImpl();
    private Ve ve;
    Ve_DAO ve_dao = new Ve_DAO();
    ChuyenDi_DAO cd_dao = new ChuyenDi_DAO();
    private ArrayList<Ve> listVe = new ArrayList<>();
    private NhanVien nhanVien;
    /**
     * Creates new form DatVeRunAll
     */
    public DoiVeRunAll(NhanVien nv) {
        initComponents();
        header1.jLabel14.setText("Đổi Vé");
        header1.jButton4.setText(nv.getTenNV());
        nhanVien = nv;
        
        cardLayout = new CardLayout();
        jPanelChayDoiVe.setLayout(cardLayout);
        jPanelChayDoiVe.add(doive_timvedoi,"DOIVE_TIMVEDOI");
        jPanelChayDoiVe.add(doive_chonchuyen,"DOIVE_CHONCHUYEN");
        jPanelChayDoiVe.add(doive_chitietve,"DATVE_CHITIETVE");
        jPanelChayDoiVe.add(doive_thanhtoan,"DOIVE_THANHTOAN");
        
        doive_timvedoi.getButtonTimVe().addActionListener((e) -> {
            System.out.println("gui.app.ve.DoiVeRunAll.<init>()");
            timKiemVeDoi();
        });
        
        doive_timvedoi.getButtonChonVeMoi().addActionListener((e) -> {
            System.out.println("gui.app.ve.DoiVeRunAll.<init>() Chon ve moi");
            timKiemChuyenDi();
        });
        doive_chonchuyen.getButtonTroVe().addActionListener((e) -> {
            hienThiJPanel("DOIVE_TIMVEDOI");
        });
        doive_chitietve.getButtonQuayLai().addActionListener((e) -> {
            hienThiJPanel("DOIVE_CHONCHUYEN");
        });
        
        doive_chitietve.getButtonTiepTuc().addActionListener(e -> {
            if(doive_chitietve.kiemTraThongTinKhachHang() && doive_chitietve.kiemTraThongTinVe()){
                listVe = doive_chitietve.layTatCaVeDuocChon();
                if(listVe.size() > 1){
                    JOptionPane.showMessageDialog(doive_chitietve, "Chỉ được phép đổi một vé!!!");
                }else{
                    //doive_thanhtoan.setVe(ve);
                    doive_thanhtoan.setListVe(listVe);
                    doive_thanhtoan.setVedoi(ve);
                    doive_thanhtoan.setNhanVien(nhanVien);
                    CaHienTai caHienTai = dao.getThongTinCaHienTai();
                    doive_thanhtoan.setMaCa(caHienTai.getMaCa());
                    chuyenDi = doive_chitietve.getChuyenDi();
                    doive_thanhtoan.getjLabelTitleMaTau().setText("Tàu: " + chuyenDi.getTau().getTenTau());
                    doive_thanhtoan.getjLabelGaDi().setText(chuyenDi.getThoiGianDiChuyen().getGaDi().getTenGa());
                    doive_thanhtoan.getjLabelGaDen().setText(chuyenDi.getThoiGianDiChuyen().getGaDen().getTenGa());
                    String timekhoihanh = chuyenDi.getThoiGianKhoiHanh().format(fmTime)+ " " + chuyenDi.getThoiGianKhoiHanh().format(fmDate);
                    doive_thanhtoan.getjLabelThoiGianKhoiHanh().setText(timekhoihanh);
                    String timeden = chuyenDi.getThoiGianDenDuTinh().format(fmTime)+ " " + chuyenDi.getThoiGianKhoiHanh().format(fmDate);
                    doive_thanhtoan.getjLabelthoiGianDenDuTinh().setText(timeden);
                    doive_thanhtoan.setKhachHang(ve.getHoaDon().getKhachHang());
                    doive_thanhtoan.setTienHuyVe(ve.getGiaVe()* 0.1);
                    doive_thanhtoan.setCardLayout(cardLayout);
                    doive_thanhtoan.setjPanelChayDatVe(jPanelChayDoiVe);
                    hienThiJPanel("DOIVE_THANHTOAN");
                }
            }
            
            
            
            
        });
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }
    private void timKiemChuyenDi(){
        ArrayList<ChuyenDi> listChuyenDi = new ArrayList<>();
        listChuyenDi.add(cd_dao.timChuyenDiTheoMa(ve.getChuyenDi().getMaChuyenDi()));
        
        if(listChuyenDi != null && !listChuyenDi.isEmpty()){
            System.out.println(listChuyenDi.size());
            hienThiJPanel("DOIVE_CHONCHUYEN");
            doive_chonchuyen.hienThiTatCaChuyenDi(listChuyenDi,cardLayout,jPanelChayDoiVe,doive_chitietve);
            ve.getChuyenDi().getThoiGianKhoiHanh();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM").withLocale(new Locale("vi"));
            
            String title = String.format("Tuyến %s -> %s, Ngày %s",
                                            ve.getChuyenDi().getThoiGianDiChuyen().getGaDi().getTenGa(),
                                            ve.getChuyenDi().getThoiGianDiChuyen().getGaDen().getTenGa(),
                                            ve.getChuyenDi().getThoiGianKhoiHanh().format(fmt)
                );
            doive_chonchuyen.getjLabelTieuDe().setText(title);
            doive_chitietve.getButtonTitle1().setText("TÌM VÉ ĐỔI");
            doive_chitietve.getButtonTitle2().setText("CHỌN VÉ ĐỔI");
            doive_chitietve.getTextFieldHotenKH().setEnabled(false);
            doive_chitietve.getTextFieldHotenKH().setText(ve.getHoaDon().getKhachHang().getTenKH());
            doive_chitietve.getTextFieldSDTKH().setEnabled(false);
            doive_chitietve.getTextFieldSDTKH().setText(ve.getHoaDon().getKhachHang().getSoDienThoai());
            doive_chitietve.getTextFieldCCCDKH().setEnabled(false);
            doive_chitietve.getTextFieldCCCDKH().setText(ve.getHoaDon().getKhachHang().getCccd());
        
        }else{
            JOptionPane.showMessageDialog(doive_chonchuyen, "Không tìm thấy chuyến đi!");
        }
    }
    public void timKiemVeDoi(){
        String maVe = doive_timvedoi.getTextFieldMaVeTim().getText().trim();
        System.out.println("gui.app.ve.DoiVeRunAll.timKiemVeDoi()"+ maVe);
        Ve timve = new Ve();
        timve = ve_dao.timVeTheoMa(maVe);
        if(timve != null){
            if(ve_dao.checkTrangThaiVeTheoMa(maVe)){
                ve = ve_dao.layThongTinVeTheoMaVe(maVe);
                doive_timvedoi.dienThongTinVe(ve);
            }else{
                JOptionPane.showMessageDialog(this, "Vé đã hoàn thành hoặc đã hủy!");
                doive_timvedoi.getTextFieldMaVeTim().selectAll();
                doive_timvedoi.getTextFieldMaVeTim().requestFocus();
            }
        }else{
            JOptionPane.showMessageDialog(this, "Vé không tồn tại. Vui lòng nhập lại mã vé!");
            doive_timvedoi.getTextFieldMaVeTim().selectAll();
            doive_timvedoi.getTextFieldMaVeTim().requestFocus();
        }
        
    }
    
    private void hienThiJPanel(String maPanel){
        cardLayout.show(jPanelChayDoiVe,maPanel);
        jPanelChayDoiVe.revalidate();
        jPanelChayDoiVe.repaint();
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelChayDoiVe = new javax.swing.JPanel();
        header1 = new component.Header();

        javax.swing.GroupLayout jPanelChayDoiVeLayout = new javax.swing.GroupLayout(jPanelChayDoiVe);
        jPanelChayDoiVe.setLayout(jPanelChayDoiVeLayout);
        jPanelChayDoiVeLayout.setHorizontalGroup(
            jPanelChayDoiVeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1384, Short.MAX_VALUE)
        );
        jPanelChayDoiVeLayout.setVerticalGroup(
            jPanelChayDoiVeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelChayDoiVe);

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
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Header header1;
    private javax.swing.JPanel jPanelChayDoiVe;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
