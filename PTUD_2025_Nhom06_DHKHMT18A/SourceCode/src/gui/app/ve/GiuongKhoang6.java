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
public class GiuongKhoang6 extends javax.swing.JPanel {
    private JPanel chiTietVe;
    private Ghe_DAO ghe_dao = new Ghe_DAO();
    private ToaTau toaTau;
    private ArrayList<KhoangTau> listKhoangTau;
    private ArrayList<Ghe> listGhe;
    private ArrayList<Ghe> gheDaDat;
    private ArrayList<button> seatButtons;
    private Map<String, button> buttonMap;
    private boolean allSelected = false;
    private final Color DEFAULT_BG = new Color(77,160,166);
    private final Color SELECTED_BG = new Color(252,90,90);
    private final Set<String> selectedSeatIds = new HashSet<>();
    private final Map<String, ThongTinChiTietVe> detailPanels = new HashMap<>();
    private final Map<String,Integer> seatToCompartment = new HashMap<>();
    private char soToa;
    private double giaVe;
    private ChuyenDi chuyenDi;
    private final static double heSoGhe = 1.2;
    /**
     * Creates new form GheNgoiMem
     */
    public GiuongKhoang6(char soToa,ChuyenDi chuyenDi,ArrayList<Ghe> gheDaDat,ToaTau toaTau,ArrayList<KhoangTau> listKhoangTau,JPanel chiTietVe ) {
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
        jLabelTitle.setText(String.format("Toa số %c: Giường Nằm Khoang 6 Điều Hòa", soToa));
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
                        String varName = f.getName();      // "khoang6dieuhoa"
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
            
            ThongTinChiTietVe tt = new ThongTinChiTietVe(chuyenDi,soToa,String.valueOf(seatToCompartment.get(seatId)),seatId.substring("khoang6dieuhoa".length()),giaVe);
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
                        
                        int viTri = 3*(hang - 1)+ soghe+ 6*(soKhoang -1);
                        System.out.println("gui.app.ve.GheNgoiMem.capNhatTrangThaiGhe() "+viTri);
                        button btn = buttonMap.get("khoang6dieuhoa"+String.valueOf(viTri));
                        
                        btn.setEnabled(false);
                        btn.setBackground(new Color(146,146,146));
                    }
                }
            }
        }
    }
    private double tinhGiaVe(double soKm,double soTienMotKm){
        return heSoGhe*soKm*soTienMotKm;
    }
    private void initSeatButtons() {
        System.out.println("panelContainer count = " + jPanelTatCaGhe.getComponentCount());
        seatButtons = new ArrayList<>();
        for (Component c : jPanelTatCaGhe.getComponents()) {
            if (c instanceof guiCustom.button b) {
                String id = b.getName();             // giờ không còn null
                if (id != null && id.startsWith("khoang6dieuhoa")) {
                    seatButtons.add(b);
                }
            }
        }
        System.out.println("Total mapped seats: " + seatButtons.size());
        seatButtons.sort(Comparator.comparingInt(b -> {
            String name = b.getName();           
            String num  = name.substring("khoang6dieuhoa".length());
            return Integer.parseInt(num);
        }));
    }
    
    private void chiaKhoangTau() {
        for (int i = 1; i <= 36; i++) {
            String key = "khoang6dieuhoa" + i;     
            int compartment = ((i - 1) / 6) + 1;     
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
        khoang6dieuhoa1 = new guiCustom.button();
        khoang6dieuhoa4 = new guiCustom.button();
        khoang6dieuhoa5 = new guiCustom.button();
        khoang6dieuhoa2 = new guiCustom.button();
        khoang6dieuhoa3 = new guiCustom.button();
        khoang6dieuhoa6 = new guiCustom.button();
        khoang6dieuhoa7 = new guiCustom.button();
        khoang6dieuhoa10 = new guiCustom.button();
        khoang6dieuhoa8 = new guiCustom.button();
        khoang6dieuhoa11 = new guiCustom.button();
        khoang6dieuhoa9 = new guiCustom.button();
        khoang6dieuhoa12 = new guiCustom.button();
        khoang6dieuhoa17 = new guiCustom.button();
        khoang6dieuhoa15 = new guiCustom.button();
        khoang6dieuhoa16 = new guiCustom.button();
        khoang6dieuhoa14 = new guiCustom.button();
        khoang6dieuhoa13 = new guiCustom.button();
        khoang6dieuhoa18 = new guiCustom.button();
        khoang6dieuhoa23 = new guiCustom.button();
        khoang6dieuhoa19 = new guiCustom.button();
        khoang6dieuhoa20 = new guiCustom.button();
        khoang6dieuhoa24 = new guiCustom.button();
        khoang6dieuhoa22 = new guiCustom.button();
        khoang6dieuhoa21 = new guiCustom.button();
        khoang6dieuhoa28 = new guiCustom.button();
        khoang6dieuhoa30 = new guiCustom.button();
        khoang6dieuhoa25 = new guiCustom.button();
        khoang6dieuhoa27 = new guiCustom.button();
        khoang6dieuhoa29 = new guiCustom.button();
        khoang6dieuhoa26 = new guiCustom.button();
        khoang6dieuhoa35 = new guiCustom.button();
        khoang6dieuhoa36 = new guiCustom.button();
        khoang6dieuhoa31 = new guiCustom.button();
        khoang6dieuhoa34 = new guiCustom.button();
        khoang6dieuhoa33 = new guiCustom.button();
        khoang6dieuhoa32 = new guiCustom.button();
        chonTatCaGhe = new guiCustom.button();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("Toa số 2: Giường Nằm Khoang 6 Điều Hòa");

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

        khoang6dieuhoa1.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa1.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa1.setText("1");
        khoang6dieuhoa1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa4.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa4.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa4.setText("4");
        khoang6dieuhoa4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa4ActionPerformed(evt);
            }
        });

        khoang6dieuhoa5.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa5.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa5.setText("5");
        khoang6dieuhoa5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa5ActionPerformed(evt);
            }
        });

        khoang6dieuhoa2.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa2.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa2.setText("2");
        khoang6dieuhoa2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa3.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa3.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa3.setText("3");
        khoang6dieuhoa3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa6.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa6.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa6.setText("6");
        khoang6dieuhoa6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa6ActionPerformed(evt);
            }
        });

        khoang6dieuhoa7.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa7.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa7.setText("7");
        khoang6dieuhoa7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa10.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa10.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa10.setText("10");
        khoang6dieuhoa10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa10ActionPerformed(evt);
            }
        });

        khoang6dieuhoa8.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa8.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa8.setText("8");
        khoang6dieuhoa8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa11.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa11.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa11.setText("11");
        khoang6dieuhoa11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa11ActionPerformed(evt);
            }
        });

        khoang6dieuhoa9.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa9.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa9.setText("9");
        khoang6dieuhoa9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa12.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa12.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa12.setText("12");
        khoang6dieuhoa12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa12ActionPerformed(evt);
            }
        });

        khoang6dieuhoa17.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa17.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa17.setText("17");
        khoang6dieuhoa17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa17ActionPerformed(evt);
            }
        });

        khoang6dieuhoa15.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa15.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa15.setText("15");
        khoang6dieuhoa15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa16.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa16.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa16.setText("16");
        khoang6dieuhoa16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa16ActionPerformed(evt);
            }
        });

        khoang6dieuhoa14.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa14.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa14.setText("14");
        khoang6dieuhoa14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa13.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa13.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa13.setText("13");
        khoang6dieuhoa13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa18.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa18.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa18.setText("18");
        khoang6dieuhoa18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa18ActionPerformed(evt);
            }
        });

        khoang6dieuhoa23.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa23.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa23.setText("23");
        khoang6dieuhoa23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa23ActionPerformed(evt);
            }
        });

        khoang6dieuhoa19.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa19.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa19.setText("19");
        khoang6dieuhoa19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa20.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa20.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa20.setText("20");
        khoang6dieuhoa20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa24.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa24.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa24.setText("24");
        khoang6dieuhoa24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa24ActionPerformed(evt);
            }
        });

        khoang6dieuhoa22.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa22.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa22.setText("22");
        khoang6dieuhoa22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa22ActionPerformed(evt);
            }
        });

        khoang6dieuhoa21.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa21.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa21.setText("21");
        khoang6dieuhoa21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa28.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa28.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa28.setText("28");
        khoang6dieuhoa28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa28ActionPerformed(evt);
            }
        });

        khoang6dieuhoa30.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa30.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa30.setText("30");
        khoang6dieuhoa30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa30ActionPerformed(evt);
            }
        });

        khoang6dieuhoa25.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa25.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa25.setText("25");
        khoang6dieuhoa25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa27.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa27.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa27.setText("27");
        khoang6dieuhoa27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa29.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa29.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa29.setText("29");
        khoang6dieuhoa29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa29ActionPerformed(evt);
            }
        });

        khoang6dieuhoa26.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa26.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa26.setText("26");
        khoang6dieuhoa26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa35.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa35.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa35.setText("35");
        khoang6dieuhoa35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa35ActionPerformed(evt);
            }
        });

        khoang6dieuhoa36.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa36.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa36.setText("36");
        khoang6dieuhoa36.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa36ActionPerformed(evt);
            }
        });

        khoang6dieuhoa31.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa31.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa31.setText("31");
        khoang6dieuhoa31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa34.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa34.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa34.setText("34");
        khoang6dieuhoa34.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        khoang6dieuhoa34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                khoang6dieuhoa34ActionPerformed(evt);
            }
        });

        khoang6dieuhoa33.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa33.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa33.setText("33");
        khoang6dieuhoa33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        khoang6dieuhoa32.setBackground(new java.awt.Color(77, 160, 166));
        khoang6dieuhoa32.setForeground(new java.awt.Color(255, 255, 255));
        khoang6dieuhoa32.setText("32");
        khoang6dieuhoa32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

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
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(khoang6dieuhoa1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(khoang6dieuhoa2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(khoang6dieuhoa5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(khoang6dieuhoa4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button50, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button51, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)))
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                                .addComponent(khoang6dieuhoa19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                                .addComponent(khoang6dieuhoa25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button54, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(khoang6dieuhoa33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(khoang6dieuhoa36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(button49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(khoang6dieuhoa1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(khoang6dieuhoa4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(khoang6dieuhoa2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(khoang6dieuhoa5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(khoang6dieuhoa3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(khoang6dieuhoa6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(button50, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(khoang6dieuhoa7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(khoang6dieuhoa10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(khoang6dieuhoa8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(khoang6dieuhoa11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(khoang6dieuhoa9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(khoang6dieuhoa12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(khoang6dieuhoa13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(khoang6dieuhoa16, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(khoang6dieuhoa14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(khoang6dieuhoa17, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(khoang6dieuhoa15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(khoang6dieuhoa18, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa19, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa22, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa20, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa23, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa21, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa24, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa25, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa28, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa26, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa29, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa27, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa30, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(button54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa31, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa34, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa32, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa35, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(khoang6dieuhoa33, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(khoang6dieuhoa36, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(button55, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(32, 32, 32))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelTatCaGhe, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void khoang6dieuhoa17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa17ActionPerformed

    private void khoang6dieuhoa16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa16ActionPerformed

    private void khoang6dieuhoa18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa18ActionPerformed

    private void khoang6dieuhoa23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa23ActionPerformed

    private void khoang6dieuhoa24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa24ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa24ActionPerformed

    private void khoang6dieuhoa22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa22ActionPerformed

    private void khoang6dieuhoa28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa28ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa28ActionPerformed

    private void khoang6dieuhoa30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa30ActionPerformed

    private void khoang6dieuhoa29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa29ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa29ActionPerformed

    private void khoang6dieuhoa35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa35ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa35ActionPerformed

    private void khoang6dieuhoa36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa36ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa36ActionPerformed

    private void khoang6dieuhoa34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa34ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa34ActionPerformed

    private void khoang6dieuhoa6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa6ActionPerformed

    private void khoang6dieuhoa5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa5ActionPerformed

    private void khoang6dieuhoa4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa4ActionPerformed

    private void khoang6dieuhoa12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa12ActionPerformed

    private void khoang6dieuhoa11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa11ActionPerformed

    private void khoang6dieuhoa10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_khoang6dieuhoa10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_khoang6dieuhoa10ActionPerformed

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
                        ThongTinChiTietVe tt = new ThongTinChiTietVe(chuyenDi,soToa,String.valueOf(seatToCompartment.get(seatId)),seatId.substring("ghengoimem".length()),giaVe);
                        
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
    private guiCustom.button khoang6dieuhoa1;
    private guiCustom.button khoang6dieuhoa10;
    private guiCustom.button khoang6dieuhoa11;
    private guiCustom.button khoang6dieuhoa12;
    private guiCustom.button khoang6dieuhoa13;
    private guiCustom.button khoang6dieuhoa14;
    private guiCustom.button khoang6dieuhoa15;
    private guiCustom.button khoang6dieuhoa16;
    private guiCustom.button khoang6dieuhoa17;
    private guiCustom.button khoang6dieuhoa18;
    private guiCustom.button khoang6dieuhoa19;
    private guiCustom.button khoang6dieuhoa2;
    private guiCustom.button khoang6dieuhoa20;
    private guiCustom.button khoang6dieuhoa21;
    private guiCustom.button khoang6dieuhoa22;
    private guiCustom.button khoang6dieuhoa23;
    private guiCustom.button khoang6dieuhoa24;
    private guiCustom.button khoang6dieuhoa25;
    private guiCustom.button khoang6dieuhoa26;
    private guiCustom.button khoang6dieuhoa27;
    private guiCustom.button khoang6dieuhoa28;
    private guiCustom.button khoang6dieuhoa29;
    private guiCustom.button khoang6dieuhoa3;
    private guiCustom.button khoang6dieuhoa30;
    private guiCustom.button khoang6dieuhoa31;
    private guiCustom.button khoang6dieuhoa32;
    private guiCustom.button khoang6dieuhoa33;
    private guiCustom.button khoang6dieuhoa34;
    private guiCustom.button khoang6dieuhoa35;
    private guiCustom.button khoang6dieuhoa36;
    private guiCustom.button khoang6dieuhoa4;
    private guiCustom.button khoang6dieuhoa5;
    private guiCustom.button khoang6dieuhoa6;
    private guiCustom.button khoang6dieuhoa7;
    private guiCustom.button khoang6dieuhoa8;
    private guiCustom.button khoang6dieuhoa9;
    // End of variables declaration//GEN-END:variables
}
