/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.app.ve;

import dao.Ghe_DAO;
import entity.ChuyenDi;
import entity.Ghe;
import entity.KhoangTau;
import entity.ToaTau;
import guiCustom.button;
import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author PHAMGIAKHANH
 */
public class GiuongKhoang4 extends javax.swing.JPanel {
    private JPanel chiTietVe;
    private Ghe_DAO ghe_dao = new Ghe_DAO();
    private ToaTau toaTau;
    private ArrayList<KhoangTau> listKhoangTau;
    private ArrayList<Ghe> listGhe;
    private ArrayList<Ghe> gheDaDat;
    private ArrayList<button> seatButtons;
    private Map<String, button> buttonMap;
    private boolean allSelected = false;
    private final Color DEFAULT_BG = new Color(70,184,122);
    private final Color SELECTED_BG = new Color(252,90,90);
    private final Set<String> selectedSeatIds = new HashSet<>();
    private final Map<String, ThongTinChiTietVe> detailPanels = new HashMap<>();
    private final Map<String,Integer> seatToCompartment = new HashMap<>();
    private char soToa;
    private double giaVe;
    private ChuyenDi chuyenDi;
    private final static double heSoGhe = 1.3;
    /**
     * Creates new form GheNgoiMem
     */
    public GiuongKhoang4(char soToa,ChuyenDi chuyenDi,ArrayList<Ghe> gheDaDat,ToaTau toaTau,ArrayList<KhoangTau> listKhoangTau,JPanel chiTietVe) {
        initComponents();
        chiaKhoangTau();
        updateNameBtn();
        initSeatButtons();
        buttonMap = new HashMap<>();
        for (button b : seatButtons) {
            buttonMap.put(b.getName(), b);
        }
        this.chuyenDi = chuyenDi;
        this.soToa = soToa;
        this.chiTietVe = chiTietVe;
        this.gheDaDat = gheDaDat;
        this.toaTau = toaTau;
        this.listKhoangTau = listKhoangTau;
        giaVe = tinhGiaVe(chuyenDi.getThoiGianDiChuyen().getSoKmDiChuyen(), chuyenDi.getThoiGianDiChuyen().getSoTienMotKm());
        capNhatTrangThaiGhe();
        jLabelTitle.setText(String.format("Toa số %c: Giường Nằm Khoang 4 Điều Hòa", soToa));
        initSeatHandlers();
    }
    
    private void updateNameBtn(){
        // 1. Duyệt tất cả các field của this
        for (Field f : getClass().getDeclaredFields()) {
            // 2. Lọc ra chỉ những field kiểu guiCustom.button
            if (f.getType() == button.class) {
                f.setAccessible(true);
                try {
                    // 3. Lấy instance của field
                    Object o = f.get(this);
                    if (o instanceof button b) {
                        // 4. Gán luôn name = tên biến
                        String varName = f.getName();      // "khoang4dieuhoa"
                        b.setName(varName);
                        b.setActionCommand(varName);       // nếu muốn dùng actionCommand
                    }
                } catch (IllegalAccessException ignore) {}
            }
        }
    }
    private void initSeatHandlers() {
        // Khởi selected = false cho mọi button
        seatButtons.forEach(btn -> btn.putClientProperty("selected", Boolean.FALSE));
        // Chỉ attach listener cho button enabled
        seatButtons.forEach(btn -> {
            if (btn.isEnabled()) {
                btn.addActionListener(e -> updateStatusGhe(btn));
                System.out.println("Click on: "+ btn.getName());
            }
        });
    }
    private void updateStatusGhe(button btn) {
        // Nếu đã disabled, bỏ qua
        if (!btn.isEnabled()) {
            return;
        }
        // Lấy trạng thái cũ, đảo và cập nhật
        boolean selected = (Boolean) btn.getClientProperty("selected");
        selected = !selected;
        btn.putClientProperty("selected", selected);
        btn.setBackground(selected ? SELECTED_BG : DEFAULT_BG);
        String seatId = btn.getActionCommand();
        if (selected) {
            // tạo panel mới và ghi vào map
            
            ThongTinChiTietVe tt = new ThongTinChiTietVe(chuyenDi,soToa,String.valueOf(seatToCompartment.get(seatId)),seatId.substring("khonag4dieuhoa".length()),giaVe);
            // nếu ThongTinChiTietVe có method setSeatId, gọi luôn:
            //tt.capNhatViTriVe(seatId);
            detailPanels.put(seatId, tt);
            chiTietVe.add(tt);
            selectedSeatIds.add(seatId);
        }
        else {
            ThongTinChiTietVe tt = detailPanels.remove(seatId);
            if (tt != null) {
                chiTietVe.remove(tt);
            }
            selectedSeatIds.remove(seatId);
        }
        chiTietVe.revalidate();
        chiTietVe.repaint();
        System.out.printf("Ghế %s %s chọn %n",seatId, selected ? "Chon" : "Khong chon");
    }

    
    private void capNhatTrangThaiGhe(){
        for(KhoangTau kt: listKhoangTau){
            //System.out.println("gui.app.ve.GheNgoiMem.capNhatTrangThaiGhe()Khoang"+ kt.getMaKhoangTau());
            ArrayList<Ghe> listghe = ghe_dao.timGheTheoKhoang(kt.getMaKhoangTau());
            
            for(Ghe ghe : listghe){
                //System.out.println("gui.app.ve.GheNgoiMem.capNhatTrangThaiGhe()"+ ghe.getMaGhe());
                for(Ghe g : gheDaDat){
                    if(ghe.getMaGhe().equals(g.getMaGhe())){
                        String[] parts = ghe.getViTri().split("_");
                        // parts = ["H","01","G","03"]
                        int hang  = Integer.parseInt(parts[1]);  // "01" → 1
                        int soghe = Integer.parseInt(parts[3]);  // "03" → 3
                        char lastChar = kt.getMaKhoangTau().charAt(kt.getMaKhoangTau().length() - 1);          // '1'
                        int soKhoang = Character.getNumericValue(lastChar); // 1
                        
                        int viTri = 2*(hang - 1)+ soghe+ 4*(soKhoang -1);
                        System.out.println("gui.app.ve.GheNgoiMem.capNhatTrangThaiGhe() "+viTri);
                        button btn = buttonMap.get("khoang4dieuhoa"+String.valueOf(viTri));
                        
                        btn.setEnabled(false);
                        btn.setBackground(new Color(146,146,146));
                    }
                }
            }
        }
    }
    private double tinhGiaVe(double soKm,double soTienMotKm){
        return heSoGhe * soKm * soTienMotKm;
    }
    private void initSeatButtons() {
        System.out.println("panelContainer count = " + jPanelTatCaGhe.getComponentCount());
        seatButtons = new ArrayList<>();
        for (Component c : jPanelTatCaGhe.getComponents()) {
            if (c instanceof guiCustom.button b) {
                String id = b.getName();             // giờ không còn null
                if (id != null && id.startsWith("khoang4dieuhoa")) {
                    seatButtons.add(b);
                }
            }
        }
        System.out.println("Total mapped seats: " + seatButtons.size());
        seatButtons.sort(Comparator.comparingInt(b -> {
            String name = b.getName();           
            String num  = name.substring("khoang4dieuhoa".length());
            return Integer.parseInt(num);
        }));
    }
    
    private void chiaKhoangTau() {
        for (int i = 1; i <= 24; i++) {
            String key = "khoang4dieuhoa" + i;     
            int compartment = ((i - 1) / 4) + 1;     
            seatToCompartment.put(key, compartment);
        }
        // Debug
        seatToCompartment.forEach((k,v) ->
            System.out.println(k + " → khoang " + v)
        );
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTitle = new javax.swing.JLabel();
        jPanelTatCaGhe = new javax.swing.JPanel();
        button49 = new guiCustom.button();
        jLabel4 = new javax.swing.JLabel();
        button50 = new guiCustom.button();
        jLabel5 = new javax.swing.JLabel();
        button51 = new guiCustom.button();
        jLabel6 = new javax.swing.JLabel();
        button52 = new guiCustom.button();
        jLabel7 = new javax.swing.JLabel();
        button53 = new guiCustom.button();
        button54 = new guiCustom.button();
        jLabel8 = new javax.swing.JLabel();
        button55 = new guiCustom.button();
        jLabel9 = new javax.swing.JLabel();
        khoang4dieuhoa1 = new guiCustom.button();
        khoang4dieuhoa3 = new guiCustom.button();
        khoang4dieuhoa4 = new guiCustom.button();
        khoang4dieuhoa2 = new guiCustom.button();
        khoang4dieuhoa5 = new guiCustom.button();
        khoang4dieuhoa7 = new guiCustom.button();
        khoang4dieuhoa6 = new guiCustom.button();
        khoang4dieuhoa8 = new guiCustom.button();
        khoang4dieuhoa12 = new guiCustom.button();
        khoang4dieuhoa11 = new guiCustom.button();
        khoang4dieuhoa10 = new guiCustom.button();
        khoang4dieuhoa9 = new guiCustom.button();
        khoang4dieuhoa16 = new guiCustom.button();
        khoang4dieuhoa13 = new guiCustom.button();
        khoang4dieuhoa14 = new guiCustom.button();
        khoang4dieuhoa15 = new guiCustom.button();
        khoang4dieuhoa19 = new guiCustom.button();
        khoang4dieuhoa17 = new guiCustom.button();
        khoang4dieuhoa20 = new guiCustom.button();
        khoang4dieuhoa18 = new guiCustom.button();
        khoang4dieuhoa24 = new guiCustom.button();
        khoang4dieuhoa21 = new guiCustom.button();
        khoang4dieuhoa23 = new guiCustom.button();
        khoang4dieuhoa22 = new guiCustom.button();
        chonTatCaGhe = new guiCustom.button();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("Toa số 3: Giường Nằm Khoang 4 Điều Hòa");

        jPanelTatCaGhe.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(83, 127, 215), 2));

        button49.setBackground(new java.awt.Color(71, 161, 255));
        button49.setEnabled(false);
        button49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button49ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Khoang 1");

        button50.setBackground(new java.awt.Color(71, 161, 255));
        button50.setEnabled(false);
        button50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button50ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Khoang 2");

        button51.setBackground(new java.awt.Color(71, 161, 255));
        button51.setEnabled(false);
        button51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button51ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Khoang 3");

        button52.setBackground(new java.awt.Color(71, 161, 255));
        button52.setEnabled(false);
        button52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button52ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Khoang 4");

        button53.setBackground(new java.awt.Color(71, 161, 255));
        button53.setEnabled(false);
        button53.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button53ActionPerformed(evt);
            }
        });

        button54.setBackground(new java.awt.Color(71, 161, 255));
        button54.setEnabled(false);
        button54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button54ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Khoang 5");

        button55.setBackground(new java.awt.Color(71, 161, 255));
        button55.setEnabled(false);
        button55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button55ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Khoang 6");

        khoang4dieuhoa1.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa1.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa1.setText("1");
        khoang4dieuhoa1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa3.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa3.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa3.setText("3");
        khoang4dieuhoa3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa3ActionPerformed(evt);
            }
        });

        khoang4dieuhoa4.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa4.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa4.setText("4");
        khoang4dieuhoa4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa4ActionPerformed(evt);
            }
        });

        khoang4dieuhoa2.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa2.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa2.setText("2");
        khoang4dieuhoa2.setToolTipText("");
        khoang4dieuhoa2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa5.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa5.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa5.setText("5");
        khoang4dieuhoa5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa7.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa7.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa7.setText("7");
        khoang4dieuhoa7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa7ActionPerformed(evt);
            }
        });

        khoang4dieuhoa6.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa6.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa6.setText("6");
        khoang4dieuhoa6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa8.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa8.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa8.setText("8");
        khoang4dieuhoa8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa8ActionPerformed(evt);
            }
        });

        khoang4dieuhoa12.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa12.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa12.setText("12");
        khoang4dieuhoa12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa12ActionPerformed(evt);
            }
        });

        khoang4dieuhoa11.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa11.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa11.setText("11");
        khoang4dieuhoa11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa11ActionPerformed(evt);
            }
        });

        khoang4dieuhoa10.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa10.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa10.setText("10");
        khoang4dieuhoa10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa9.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa9.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa9.setText("9");
        khoang4dieuhoa9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa16.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa16.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa16.setText("16");
        khoang4dieuhoa16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa16ActionPerformed(evt);
            }
        });

        khoang4dieuhoa13.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa13.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa13.setText("13");
        khoang4dieuhoa13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa14.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa14.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa14.setText("14");
        khoang4dieuhoa14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa15.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa15.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa15.setText("15");
        khoang4dieuhoa15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa15ActionPerformed(evt);
            }
        });

        khoang4dieuhoa19.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa19.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa19.setText("19");
        khoang4dieuhoa19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa19ActionPerformed(evt);
            }
        });

        khoang4dieuhoa17.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa17.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa17.setText("17");
        khoang4dieuhoa17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa20.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa20.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa20.setText("20");
        khoang4dieuhoa20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa20ActionPerformed(evt);
            }
        });

        khoang4dieuhoa18.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa18.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa18.setText("18");
        khoang4dieuhoa18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa24.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa24.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa24.setText("24");
        khoang4dieuhoa24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa24ActionPerformed(evt);
            }
        });

        khoang4dieuhoa21.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa21.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa21.setText("21");
        khoang4dieuhoa21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang4dieuhoa23.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa23.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa23.setText("23");
        khoang4dieuhoa23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang4dieuhoa23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang4dieuhoa23ActionPerformed(evt);
            }
        });

        khoang4dieuhoa22.setBackground(new java.awt.Color(70, 184, 122));
        khoang4dieuhoa22.setForeground(new java.awt.Color(255, 255, 255));
        khoang4dieuhoa22.setText("22");
        khoang4dieuhoa22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanelTatCaGheLayout = new javax.swing.GroupLayout(jPanelTatCaGhe);
        jPanelTatCaGhe.setLayout(jPanelTatCaGheLayout);
        jPanelTatCaGheLayout.setHorizontalGroup(
            jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(button49, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(khoang4dieuhoa1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(khoang4dieuhoa2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(khoang4dieuhoa4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(khoang4dieuhoa3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button50, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button51, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)))
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button52, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)))
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button53, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)))
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button54, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang4dieuhoa22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang4dieuhoa24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button55, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)))
                .addGap(8, 8, 8))
        );
        jPanelTatCaGheLayout.setVerticalGroup(
            jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(khoang4dieuhoa21, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(khoang4dieuhoa23, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(khoang4dieuhoa22, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(khoang4dieuhoa24, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(button55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTatCaGheLayout.createSequentialGroup()
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa17, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa19, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa18, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa20, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(button54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTatCaGheLayout.createSequentialGroup()
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa16, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTatCaGheLayout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelTatCaGheLayout.createSequentialGroup()
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button52, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(button51, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(khoang4dieuhoa2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(khoang4dieuhoa4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(button50, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                .addComponent(button49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        chonTatCaGhe.setBackground(new java.awt.Color(0, 153, 0));
        chonTatCaGhe.setForeground(new java.awt.Color(255, 255, 255));
        chonTatCaGhe.setText("Chọn Tất Cả Ghế");
        chonTatCaGhe.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chonTatCaGhe.setName("chonTatCaGhe"); // NOI18N
        chonTatCaGhe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chonTatCaGheActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelTatCaGhe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chonTatCaGhe, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chonTatCaGhe, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTatCaGhe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button49ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button49ActionPerformed

    private void button50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button50ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button50ActionPerformed

    private void button51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button51ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button51ActionPerformed

    private void button52ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button52ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button52ActionPerformed

    private void button53ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button53ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button53ActionPerformed

    private void button54ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button54ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button54ActionPerformed

    private void button55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button55ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_button55ActionPerformed

    private void khoang4dieuhoa12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa12ActionPerformed

    private void khoang4dieuhoa11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa11ActionPerformed

    private void khoang4dieuhoa16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa16ActionPerformed

    private void khoang4dieuhoa15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa15ActionPerformed

    private void khoang4dieuhoa19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa19ActionPerformed

    private void khoang4dieuhoa20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa20ActionPerformed

    private void khoang4dieuhoa24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa24ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa24ActionPerformed

    private void khoang4dieuhoa23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa23ActionPerformed

    private void khoang4dieuhoa4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa4ActionPerformed

    private void khoang4dieuhoa3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa3ActionPerformed

    private void khoang4dieuhoa8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa8ActionPerformed

    private void khoang4dieuhoa7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang4dieuhoa7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang4dieuhoa7ActionPerformed

    private void chonTatCaGheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chonTatCaGheActionPerformed
        // TODO add your handling code here:
        chonTatCaGhe.setText(allSelected ? "Chọn Tất Cả Ghế" : "Bỏ Chọn Tất Cả");
        if(allSelected){
            for(button b:seatButtons){
                String seatId = b.getActionCommand();
                if(b.isEnabled()){
                    ThongTinChiTietVe tt = detailPanels.remove(seatId);
                    if (tt != null) {
                        chiTietVe.remove(tt);
                    }
                    b.setBackground(DEFAULT_BG);
                    b.putClientProperty("selected", Boolean.FALSE);
                }
            }
            allSelected = false;
        }else{
            for(button b:seatButtons){
                String seatId = b.getActionCommand();
                if(b.isEnabled()){
                    if (!detailPanels.containsKey(seatId)) {
                        ThongTinChiTietVe tt = new ThongTinChiTietVe(chuyenDi,soToa,String.valueOf(seatToCompartment.get(seatId)),seatId.substring("khoang4dieuhoa".length()),giaVe);
                        
                        detailPanels.put(seatId, tt);
                        chiTietVe.add(tt);
                    }
                    b.setBackground(SELECTED_BG);
                    b.putClientProperty("selected", Boolean.TRUE);
                }
            }
            allSelected = true;
        }


    }//GEN-LAST:event_chonTatCaGheActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private guiCustom.button button49;
    private guiCustom.button button50;
    private guiCustom.button button51;
    private guiCustom.button button52;
    private guiCustom.button button53;
    private guiCustom.button button54;
    private guiCustom.button button55;
    private guiCustom.button chonTatCaGhe;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanelTatCaGhe;
    private guiCustom.button khoang4dieuhoa1;
    private guiCustom.button khoang4dieuhoa10;
    private guiCustom.button khoang4dieuhoa11;
    private guiCustom.button khoang4dieuhoa12;
    private guiCustom.button khoang4dieuhoa13;
    private guiCustom.button khoang4dieuhoa14;
    private guiCustom.button khoang4dieuhoa15;
    private guiCustom.button khoang4dieuhoa16;
    private guiCustom.button khoang4dieuhoa17;
    private guiCustom.button khoang4dieuhoa18;
    private guiCustom.button khoang4dieuhoa19;
    private guiCustom.button khoang4dieuhoa2;
    private guiCustom.button khoang4dieuhoa20;
    private guiCustom.button khoang4dieuhoa21;
    private guiCustom.button khoang4dieuhoa22;
    private guiCustom.button khoang4dieuhoa23;
    private guiCustom.button khoang4dieuhoa24;
    private guiCustom.button khoang4dieuhoa3;
    private guiCustom.button khoang4dieuhoa4;
    private guiCustom.button khoang4dieuhoa5;
    private guiCustom.button khoang4dieuhoa6;
    private guiCustom.button khoang4dieuhoa7;
    private guiCustom.button khoang4dieuhoa8;
    private guiCustom.button khoang4dieuhoa9;
    // End of variables declaration//GEN-END:variables
}
