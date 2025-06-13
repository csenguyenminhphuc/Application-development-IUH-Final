/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.ve;


import dao.ThongKeDAOImpl;
import dao.Ve_DAO;
import entity.CaHienTai;
import entity.NhanVien;
import entity.Ve;
import java.awt.CardLayout;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 *
 * @author PHAMGIAKHANH
 */
public class TraVeRunAll extends javax.swing.JPanel {
    
    private CardLayout cardLayout;
    TraVe_ChonVeTra trave_chonvetra = new TraVe_ChonVeTra();
    TraVe_XacNhan trave_xacnhan = new TraVe_XacNhan();
    TraVe_TraVe trave_trave = new TraVe_TraVe();
    private ThongKeDAOImpl dao = new ThongKeDAOImpl();
    private Ve ve;
    Ve_DAO ve_dao = new Ve_DAO();
    private NhanVien nhanVien;
    /**
     * Creates new form DatVeRunAll
     */
    public TraVeRunAll(NhanVien nv) {
        initComponents();
        header1.jLabel14.setText("Trả Vé");
        header1.jButton4.setText(nv.getTenNV());
        nhanVien = nv;
        
        cardLayout = new CardLayout();
        jPanelChayTraVe.setLayout(cardLayout);
        jPanelChayTraVe.add(trave_chonvetra,"TRAVE_CHONVETRA");
        jPanelChayTraVe.add(trave_xacnhan,"TRAVE_XACNHAN");
        jPanelChayTraVe.add(trave_trave,"TRAVE_TRAVE");
        cardLayout.show(jPanelChayTraVe,"TRAVE_CHONVETRA");
        jPanelChayTraVe.revalidate();
        jPanelChayTraVe.repaint();
        trave_chonvetra.getButtonTraCuuVe().addActionListener(e -> {
                System.out.println("gui.app.ve.TraVeRunAll.<init>()");
                timKiemVeTra();
            });
        trave_xacnhan.getButtonQuayLai().addActionListener(e -> {
                hienThiJPanel("TRAVE_CHONVETRA");
            });
        trave_xacnhan.getButtonXacNhanTraVe().addActionListener(e -> {
                hienThiJPanel("TRAVE_TRAVE");
                Double lephi = trave_xacnhan.getLePhi();
                Double tienHoanLai = trave_xacnhan.tienHoanLai(ve.getGiaVe(), lephi);
                CaHienTai caHienTai = dao.getThongTinCaHienTai();
                trave_trave.setMaCa(caHienTai.getMaCa());
            try {
                if(trave_trave.capNhatTrangThaiVe(ve) && trave_trave.themHoaDonVaoDB(tienHoanLai, nv, ve)){
                    trave_trave.dienTatCaThongTinHoaDonTra(ve, nv,lephi);
                    JOptionPane.showMessageDialog(trave_trave, "Trả Vé Thành Công!. Vui Lòng Xuất Hóa Đơn.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(TraVeRunAll.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            });
        trave_trave.getButtonTroVeTrangChu().addActionListener(e -> {
                hienThiJPanel("TRAVE_CHONVETRA");
            });
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }
    public void timKiemVeTra(){
        String maVe = trave_chonvetra.getTextFieldMaVe().getText().trim();
        System.out.println("gui.app.ve.TraVeRunAll.timKiemVeTra(): "+maVe);
        Ve timve = new Ve();
        timve = ve_dao.timVeTheoMa(maVe);
        if(timve != null){
            if(ve_dao.checkTrangThaiVeTheoMa(maVe)){
                ve = ve_dao.layThongTinVeTheoMaVe(maVe);
                hienThiJPanel("TRAVE_XACNHAN");
                trave_xacnhan.dienThongTinVe(ve);
                
            }else{
                JOptionPane.showMessageDialog(this, "Vé đã hoàn thành hoặc đã hủy!");
                trave_chonvetra.getTextFieldMaVe().selectAll();
                trave_chonvetra.getTextFieldMaVe().requestFocus();
            }
        }else{
            JOptionPane.showMessageDialog(this, "Vé không tồn tại. Vui lòng nhập lại mã vé!");
            trave_chonvetra.getTextFieldMaVe().selectAll();
            trave_chonvetra.getTextFieldMaVe().requestFocus();
        }
        
        
//        Ve ve_c = ve_dao.timVeTheoMa(maVe);
//        if(ve_c != null){
//            if(ve_dao.checkTrangThaiVeTheoMa(maVe)){
//                try {
//                    if(ve_dao.checkDieuKienTraVe(maVe)){
//                        ve = ve_dao.layThongTinVeTheoMaVe(maVe);
//                        cardLayout.show(jpanel,"DATVE_XACNHAN");
//                    }else{
//                        JOptionPane.showMessageDialog(this, "Vé của bạn đã vượt 8 tiếng trước thời gian khởi hành!");
//                        textFieldMaVe.selectAll();
//                        textFieldMaVe.requestFocus();
//                    }
//                } catch (SQLException ex) {
//                    Logger.getLogger(TraVe_ChonVeTra.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }else{
//                JOptionPane.showMessageDialog(this, "Vé của bạn đã hoàn thành hoặc đã được trả!");
//                textFieldMaVe.selectAll();
//                textFieldMaVe.requestFocus();
//
//            }
//        }else{
//            JOptionPane.showMessageDialog(this, "Vé không tồn tại. Vui lòng nhập lại mã vé!");
//            textFieldMaVe.selectAll();
//            textFieldMaVe.requestFocus();
//        }
    }
    private void hienThiJPanel(String maPanel){
        cardLayout.show(jPanelChayTraVe,maPanel);
        jPanelChayTraVe.revalidate();
        jPanelChayTraVe.repaint();
        
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
        jPanelChayTraVe = new javax.swing.JPanel();
        header1 = new component.Header();

        javax.swing.GroupLayout jPanelChayTraVeLayout = new javax.swing.GroupLayout(jPanelChayTraVe);
        jPanelChayTraVe.setLayout(jPanelChayTraVeLayout);
        jPanelChayTraVeLayout.setHorizontalGroup(
            jPanelChayTraVeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1384, Short.MAX_VALUE)
        );
        jPanelChayTraVeLayout.setVerticalGroup(
            jPanelChayTraVeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelChayTraVe);

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
    private javax.swing.JPanel jPanelChayTraVe;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
