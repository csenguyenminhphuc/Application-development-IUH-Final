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
import java.awt.Container;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author PHAMGIAKHANH
 */
public class GheNgoiMem extends javax.swing.JPanel {
    private JPanel chiTietVe;
    private Ghe_DAO ghe_dao = new Ghe_DAO();
    private ToaTau toaTau;
    private ArrayList<KhoangTau> listKhoangTau;
    private ArrayList<Ghe> listGhe;
    private ArrayList<Ghe> gheDaDat;
    private ArrayList<button> seatButtons;
    private Map<String, button> buttonMap;
    private boolean allSelected = false;
    private final Color DEFAULT_BG = new Color(129,185,226);
    private final Color SELECTED_BG = new Color(252,90,90);
    private final Set<String> selectedSeatIds = new HashSet<>();
    private final Map<String, ThongTinChiTietVe> detailPanels = new HashMap<>();
    private final Map<String,Integer> seatToCompartment = new HashMap<>();
    private char soToa;
    private double giaVe;
    private ChuyenDi chuyenDi;

    /**
     * Creates new form GheNgoiMem
     */
    public GheNgoiMem(char soToa,ChuyenDi chuyenDi,ArrayList<Ghe> gheDaDat,ToaTau toaTau,ArrayList<KhoangTau> listKhoangTau,JPanel chiTietVe ) {
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
        jLabelTitle.setText(String.format("Toa số %c: Ngồi mềm điều hòa", soToa));
        
        
        System.out.println("gui.app.ve.GheNgoiMem.<init>() Size seats: "+ seatButtons.size());
        
        initSeatHandlers();
        
    }
    // 1) Gán listener chỉ cho ghế còn trống (enabled)
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
    private void chiaKhoangTau() {
        for (int i = 1; i <= 48; i++) {
            String key = "ghengoimem" + i;           // tên button
            int compartment = ((i - 1) / 8) + 1;     // 1–8 → 1, 9–16 → 2, …, 41–48 → 6
            seatToCompartment.put(key, compartment);
        }
        // Debug
        seatToCompartment.forEach((k,v) ->
            System.out.println(k + " → khoang " + v)
        );
    }

    // 2) Trong updateStatusGhe, vẫn có thể guard thêm
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
            
            ThongTinChiTietVe tt = new ThongTinChiTietVe(chuyenDi,soToa,String.valueOf(seatToCompartment.get(seatId)),seatId.substring("ghengoimem".length()),giaVe);
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
        //themThongTinChiTietVe();
        System.out.printf("Ghế %s %s chọn %n",seatId, selected ? "Chon" : "Khong chon");
    }
//    private void themThongTinChiTietVe(){
//        ThongTinChiTietVe ttctv = new ThongTinChiTietVe();
//        chiTietVe.add(ttctv);
//        chiTietVe.revalidate();
//        chiTietVe.repaint();
//    }
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
                        String varName = f.getName();      // "ghengoimem1"
                        b.setName(varName);
                        b.setActionCommand(varName);       // nếu muốn dùng actionCommand
                    }
                } catch (IllegalAccessException ignore) {}
            }
        }
    }
    private void initSeatButtons() {
        System.out.println("panelContainer count = " + jPanelTatCaGhe.getComponentCount());
        seatButtons = new ArrayList<>();
        for (Component c : jPanelTatCaGhe.getComponents()) {
            if (c instanceof guiCustom.button b) {
                String id = b.getName();             // giờ không còn null
                if (id != null && id.startsWith("ghengoimem")) {
                    seatButtons.add(b);
                }
            }
        }
        System.out.println("Total mapped seats: " + seatButtons.size());
        seatButtons.sort(Comparator.comparingInt(b -> {
            String name = b.getName();                  // "ghengoimem17"
            String num  = name.substring("ghengoimem".length());
            return Integer.parseInt(num);
        }));
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
                        
                        int viTri = 4*(hang - 1)+ soghe+ 8*(soKhoang -1);
                        System.out.println("gui.app.ve.GheNgoiMem.capNhatTrangThaiGhe() "+viTri);
                        button btn = buttonMap.get("ghengoimem"+String.valueOf(viTri));
                        
                        btn.setEnabled(false);
                        btn.setBackground(new Color(146,146,146));
                    }
                }
            }
        }
    }

    private double tinhGiaVe(double soKm,double soTienMotKm){
        System.out.println("gui.app.ve.GheNgoiMem.tinhGiaVe()"+ soKm*soTienMotKm);
        return soKm*soTienMotKm;
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
        ghengoimem1 = new guiCustom.button();
        ghengoimem5 = new guiCustom.button();
        ghengoimem6 = new guiCustom.button();
        ghengoimem2 = new guiCustom.button();
        ghengoimem3 = new guiCustom.button();
        ghengoimem7 = new guiCustom.button();
        ghengoimem4 = new guiCustom.button();
        ghengoimem8 = new guiCustom.button();
        ghengoimem9 = new guiCustom.button();
        ghengoimem13 = new guiCustom.button();
        ghengoimem10 = new guiCustom.button();
        ghengoimem14 = new guiCustom.button();
        ghengoimem11 = new guiCustom.button();
        ghengoimem15 = new guiCustom.button();
        ghengoimem12 = new guiCustom.button();
        ghengoimem16 = new guiCustom.button();
        ghengoimem20 = new guiCustom.button();
        ghengoimem22 = new guiCustom.button();
        ghengoimem19 = new guiCustom.button();
        ghengoimem21 = new guiCustom.button();
        ghengoimem18 = new guiCustom.button();
        ghengoimem17 = new guiCustom.button();
        ghengoimem23 = new guiCustom.button();
        ghengoimem24 = new guiCustom.button();
        ghengoimem30 = new guiCustom.button();
        ghengoimem25 = new guiCustom.button();
        ghengoimem26 = new guiCustom.button();
        ghengoimem31 = new guiCustom.button();
        ghengoimem32 = new guiCustom.button();
        ghengoimem29 = new guiCustom.button();
        ghengoimem28 = new guiCustom.button();
        ghengoimem27 = new guiCustom.button();
        ghengoimem37 = new guiCustom.button();
        ghengoimem39 = new guiCustom.button();
        ghengoimem33 = new guiCustom.button();
        ghengoimem35 = new guiCustom.button();
        ghengoimem38 = new guiCustom.button();
        ghengoimem34 = new guiCustom.button();
        ghengoimem36 = new guiCustom.button();
        ghengoimem40 = new guiCustom.button();
        ghengoimem48 = new guiCustom.button();
        ghengoimem44 = new guiCustom.button();
        ghengoimem46 = new guiCustom.button();
        ghengoimem47 = new guiCustom.button();
        ghengoimem41 = new guiCustom.button();
        ghengoimem45 = new guiCustom.button();
        ghengoimem43 = new guiCustom.button();
        ghengoimem42 = new guiCustom.button();
        chonTatCaGhe = new guiCustom.button();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("Toa số 1: Ngồi Mềm Điều Hòa");

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

        ghengoimem1.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem1.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem1.setText("1");
        ghengoimem1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem1.setName(""); // NOI18N

        ghengoimem5.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem5.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem5.setText("5");
        ghengoimem5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem5ActionPerformed(evt);
            }
        });

        ghengoimem6.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem6.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem6.setText("6");
        ghengoimem6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem6ActionPerformed(evt);
            }
        });

        ghengoimem2.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem2.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem2.setText("2");
        ghengoimem2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem3.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem3.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem3.setText("3");
        ghengoimem3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem7.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem7.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem7.setText("7");
        ghengoimem7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem7ActionPerformed(evt);
            }
        });

        ghengoimem4.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem4.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem4.setText("4");
        ghengoimem4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem8.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem8.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem8.setText("8");
        ghengoimem8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem8ActionPerformed(evt);
            }
        });

        ghengoimem9.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem9.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem9.setText("9");
        ghengoimem9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem13.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem13.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem13.setText("13");
        ghengoimem13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem13ActionPerformed(evt);
            }
        });

        ghengoimem10.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem10.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem10.setText("10");
        ghengoimem10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem14.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem14.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem14.setText("14");
        ghengoimem14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem14ActionPerformed(evt);
            }
        });

        ghengoimem11.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem11.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem11.setText("11");
        ghengoimem11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem15.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem15.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem15.setText("15");
        ghengoimem15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem15ActionPerformed(evt);
            }
        });

        ghengoimem12.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem12.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem12.setText("12");
        ghengoimem12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem16.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem16.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem16.setText("16");
        ghengoimem16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem16ActionPerformed(evt);
            }
        });

        ghengoimem20.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem20.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem20.setText("20");
        ghengoimem20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem22.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem22.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem22.setText("22");
        ghengoimem22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem22ActionPerformed(evt);
            }
        });

        ghengoimem19.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem19.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem19.setText("19");
        ghengoimem19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem21.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem21.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem21.setText("21");
        ghengoimem21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem21ActionPerformed(evt);
            }
        });

        ghengoimem18.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem18.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem18.setText("18");
        ghengoimem18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem17.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem17.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem17.setText("17");
        ghengoimem17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem23.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem23.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem23.setText("23");
        ghengoimem23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem23ActionPerformed(evt);
            }
        });

        ghengoimem24.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem24.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem24.setText("24");
        ghengoimem24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem24ActionPerformed(evt);
            }
        });

        ghengoimem30.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem30.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem30.setText("30");
        ghengoimem30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem30ActionPerformed(evt);
            }
        });

        ghengoimem25.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem25.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem25.setText("25");
        ghengoimem25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem26.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem26.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem26.setText("26");
        ghengoimem26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem31.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem31.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem31.setText("31");
        ghengoimem31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem31ActionPerformed(evt);
            }
        });

        ghengoimem32.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem32.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem32.setText("32");
        ghengoimem32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem32ActionPerformed(evt);
            }
        });

        ghengoimem29.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem29.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem29.setText("29");
        ghengoimem29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem29ActionPerformed(evt);
            }
        });

        ghengoimem28.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem28.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem28.setText("28");
        ghengoimem28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem27.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem27.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem27.setText("27");
        ghengoimem27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem37.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem37.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem37.setText("37");
        ghengoimem37.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem37ActionPerformed(evt);
            }
        });

        ghengoimem39.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem39.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem39.setText("39");
        ghengoimem39.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem39ActionPerformed(evt);
            }
        });

        ghengoimem33.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem33.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem33.setText("33");
        ghengoimem33.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem35.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem35.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem35.setText("35");
        ghengoimem35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem38.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem38.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem38.setText("38");
        ghengoimem38.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem38ActionPerformed(evt);
            }
        });

        ghengoimem34.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem34.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem34.setText("34");
        ghengoimem34.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem36.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem36.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem36.setText("36");
        ghengoimem36.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem40.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem40.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem40.setText("40");
        ghengoimem40.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem40ActionPerformed(evt);
            }
        });

        ghengoimem48.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem48.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem48.setText("48");
        ghengoimem48.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem48ActionPerformed(evt);
            }
        });

        ghengoimem44.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem44.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem44.setText("44");
        ghengoimem44.setToolTipText("");
        ghengoimem44.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem46.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem46.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem46.setText("46");
        ghengoimem46.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem46ActionPerformed(evt);
            }
        });

        ghengoimem47.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem47.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem47.setText("47");
        ghengoimem47.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem47ActionPerformed(evt);
            }
        });

        ghengoimem41.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem41.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem41.setText("41");
        ghengoimem41.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem45.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem45.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem45.setText("45");
        ghengoimem45.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ghengoimem45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ghengoimem45ActionPerformed(evt);
            }
        });

        ghengoimem43.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem43.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem43.setText("43");
        ghengoimem43.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        ghengoimem42.setBackground(new java.awt.Color(129, 185, 226));
        ghengoimem42.setForeground(new java.awt.Color(255, 255, 255));
        ghengoimem42.setText("42");
        ghengoimem42.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanelTatCaGheLayout = new javax.swing.GroupLayout(jPanelTatCaGhe);
        jPanelTatCaGhe.setLayout(jPanelTatCaGheLayout);
        jPanelTatCaGheLayout.setHorizontalGroup(
            jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(button49, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem3, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem7, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ghengoimem1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ghengoimem2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ghengoimem6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ghengoimem5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button50, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem9, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem13, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button51, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)))
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem17, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem21, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                                .addComponent(ghengoimem25, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem29, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                                .addComponent(ghengoimem33, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem37, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button54, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem41, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem45, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addComponent(ghengoimem44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ghengoimem48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem25, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem29, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem26, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem30, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem27, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem31, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem28, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem32, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem33, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem37, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem34, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem38, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem35, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem39, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem36, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem40, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem16, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(ghengoimem17, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ghengoimem21, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(ghengoimem18, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ghengoimem22, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(ghengoimem19, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ghengoimem23, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(ghengoimem20, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ghengoimem24, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(button51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button49, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(button50, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTatCaGheLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button55, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelTatCaGheLayout.createSequentialGroup()
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem41, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem45, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem42, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem46, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem43, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem47, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelTatCaGheLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ghengoimem44, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ghengoimem48, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))))
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
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chonTatCaGhe, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelTatCaGhe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void ghengoimem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem22ActionPerformed

    private void ghengoimem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem21ActionPerformed

    private void ghengoimem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem23ActionPerformed

    private void ghengoimem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem24ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem24ActionPerformed

    private void ghengoimem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem30ActionPerformed

    private void ghengoimem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem31ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem31ActionPerformed

    private void ghengoimem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem32ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem32ActionPerformed

    private void ghengoimem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem29ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem29ActionPerformed

    private void ghengoimem37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem37ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem37ActionPerformed

    private void ghengoimem39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem39ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem39ActionPerformed

    private void ghengoimem38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem38ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem38ActionPerformed

    private void ghengoimem40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem40ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem40ActionPerformed

    private void ghengoimem46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem46ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem46ActionPerformed

    private void ghengoimem47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem47ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem47ActionPerformed

    private void ghengoimem45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem45ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem45ActionPerformed

    private void ghengoimem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem8ActionPerformed

    private void ghengoimem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem7ActionPerformed

    private void ghengoimem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem6ActionPerformed

    private void ghengoimem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem5ActionPerformed

    private void ghengoimem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem16ActionPerformed

    private void ghengoimem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem15ActionPerformed

    private void ghengoimem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem14ActionPerformed

    private void ghengoimem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem13ActionPerformed

    private void ghengoimem48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghengoimem48ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ghengoimem48ActionPerformed

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
    private guiCustom.button ghengoimem1;
    private guiCustom.button ghengoimem10;
    private guiCustom.button ghengoimem11;
    private guiCustom.button ghengoimem12;
    private guiCustom.button ghengoimem13;
    private guiCustom.button ghengoimem14;
    private guiCustom.button ghengoimem15;
    private guiCustom.button ghengoimem16;
    private guiCustom.button ghengoimem17;
    private guiCustom.button ghengoimem18;
    private guiCustom.button ghengoimem19;
    private guiCustom.button ghengoimem2;
    private guiCustom.button ghengoimem20;
    private guiCustom.button ghengoimem21;
    private guiCustom.button ghengoimem22;
    private guiCustom.button ghengoimem23;
    private guiCustom.button ghengoimem24;
    private guiCustom.button ghengoimem25;
    private guiCustom.button ghengoimem26;
    private guiCustom.button ghengoimem27;
    private guiCustom.button ghengoimem28;
    private guiCustom.button ghengoimem29;
    private guiCustom.button ghengoimem3;
    private guiCustom.button ghengoimem30;
    private guiCustom.button ghengoimem31;
    private guiCustom.button ghengoimem32;
    private guiCustom.button ghengoimem33;
    private guiCustom.button ghengoimem34;
    private guiCustom.button ghengoimem35;
    private guiCustom.button ghengoimem36;
    private guiCustom.button ghengoimem37;
    private guiCustom.button ghengoimem38;
    private guiCustom.button ghengoimem39;
    private guiCustom.button ghengoimem4;
    private guiCustom.button ghengoimem40;
    private guiCustom.button ghengoimem41;
    private guiCustom.button ghengoimem42;
    private guiCustom.button ghengoimem43;
    private guiCustom.button ghengoimem44;
    private guiCustom.button ghengoimem45;
    private guiCustom.button ghengoimem46;
    private guiCustom.button ghengoimem47;
    private guiCustom.button ghengoimem48;
    private guiCustom.button ghengoimem5;
    private guiCustom.button ghengoimem6;
    private guiCustom.button ghengoimem7;
    private guiCustom.button ghengoimem8;
    private guiCustom.button ghengoimem9;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanelTatCaGhe;
    // End of variables declaration//GEN-END:variables
}
