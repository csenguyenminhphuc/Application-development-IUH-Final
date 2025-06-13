package gui.app.thongke.taichinh;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import com.formdev.flatlaf.FlatClientProperties;
import raven2.chart.CurveLineChart;
import raven2.chart.ModelChart;
import com.raven.chart.Chart;
import java.awt.Font;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.pie.PieChart;
import raven2.panel.PanelShadow;

public class Line2 extends JPanel {

    // Row 1: 4 CurveLineChart được bọc trong PanelShadow riêng biệt
    private CurveLineChart chartAmount;
    private CurveLineChart chartCost;
    private CurveLineChart chartProfit;
    private CurveLineChart chartOrders;
    private PanelShadow shadow1, shadow2, shadow3, shadow4;
    
    // Row 2: 2 biểu đồ
    private PieChart pieTicketType;
    private Chart bieuDoDoanhThu;
    
    // Row 3: 3 biểu đồ
    private JPanel panelTopRoutes;
    private PieChart donutCancelRate;
    private Chart bieuDoDoanhThuTheoLoaiGhe;

    public Line2() {
        initComponents();
    }

    private void initComponents() {
        // Sử dụng MigLayout, không đặt row/column constraints cứng - sử dụng "wrap" để xuống hàng
        setLayout(new MigLayout("insets 10, gap 10", "", ""));
        // -------------------
        // ROW 2: 2 biểu đồ
        // -------------------
        // Biểu đồ Pie: Tỷ lệ loại vé bán được
        pieTicketType = new PieChart();
        JLabel headerPie = new JLabel("Tỷ Lệ Loại Vé Bán Được");
        headerPie.putClientProperty(FlatClientProperties.STYLE, "font: +1");
        pieTicketType.setHeader(headerPie);
        pieTicketType.setChartType(PieChart.ChartType.DONUT_CHART);
        pieTicketType.getChartColor().addColor(new Color(0x0D62FF));
        pieTicketType.getChartColor().addColor(new Color(0xffc2f0));
        pieTicketType.getChartColor().addColor(new Color(0x5CE9E6));
        DefaultPieDataset<String> ticketTypeData = new DefaultPieDataset<>();
        ticketTypeData.addValue("Người lớn", randomInt(50, 100));
        ticketTypeData.addValue("Trẻ em", randomInt(20, 70));
        ticketTypeData.addValue("SV", randomInt(30, 80));
        pieTicketType.setDataset(ticketTypeData);
        pieTicketType.putClientProperty(FlatClientProperties.STYLE,
            "border:5,5,5,5,$Component.borderColor; arc:10");
        
        // Biểu đồ Bar: Doanh thu
        bieuDoDoanhThu = new Chart();
        bieuDoDoanhThu.setFont(new Font("sansserif", Font.PLAIN, 18));
        bieuDoDoanhThu.clear();
        bieuDoDoanhThu.addData(new com.raven.chart.ModelChart("January", new double[]{500, 200, 80, 89}));
        bieuDoDoanhThu.addData(new com.raven.chart.ModelChart("February", new double[]{600, 750, 90, 150}));
        bieuDoDoanhThu.addData(new com.raven.chart.ModelChart("March", new double[]{200, 350, 460, 900}));
        bieuDoDoanhThu.addData(new com.raven.chart.ModelChart("April", new double[]{480, 150, 750, 700}));
        bieuDoDoanhThu.addData(new com.raven.chart.ModelChart("May", new double[]{350, 540, 300, 150}));
        bieuDoDoanhThu.addData(new com.raven.chart.ModelChart("June", new double[]{190, 280, 81, 200}));
        bieuDoDoanhThu.start();
        
        // Add Row 2: Sử dụng "split 2, grow"
        add(pieTicketType, "split 2, grow");
        add(bieuDoDoanhThu, "grow, wrap");
        
//        // -------------------
//        // ROW 3: 3 biểu đồ
//        // -------------------
//        // Chart 1: Danh sách tuyến đường bán chạy
//        panelTopRoutes = new JPanel();
//        panelTopRoutes.setLayout(new BoxLayout(panelTopRoutes, BoxLayout.Y_AXIS));
//        JLabel headerTopRoutes = new JLabel("Chuyến Đi Ưa Chuộng");
//        headerTopRoutes.putClientProperty(FlatClientProperties.STYLE, "font: +1");
//        panelTopRoutes.add(headerTopRoutes);
//        String[] routes = {"Hà Nội - Sài Gòn", "Hà Nội - Đà Nẵng", "Sài Gòn - Cần Thơ",
//                             "Đà Nẵng - Huế", "Sài Gòn - Vũng Tàu"};
//        for (int i = 0; i < routes.length; i++) {
//            int sold = randomInt(50, 200);
//            JLabel routeLabel = new JLabel((i + 1) + ". " + routes[i] + " - " + sold + " vé");
//            panelTopRoutes.add(routeLabel);
//        }
//        panelTopRoutes.putClientProperty(FlatClientProperties.STYLE,
//            "border:5,5,5,5,$Component.borderColor; arc:10");
//        panelTopRoutes.setAlignmentY(TOP_ALIGNMENT);
//        
//        // Chart 2: PieChart dạng donut - Tỷ lệ hủy vé
//        donutCancelRate = new PieChart();
//        JLabel headerDonut = new JLabel("Tỷ Lệ Hủy Vé");
//        headerDonut.putClientProperty(FlatClientProperties.STYLE, "font: +1");
//        donutCancelRate.setHeader(headerDonut);
//        donutCancelRate.setChartType(PieChart.ChartType.DONUT_CHART);
//        donutCancelRate.getChartColor().addColor(new Color(0xf04147));
//        donutCancelRate.getChartColor().addColor(new Color(0x288142));
//        DefaultPieDataset<String> cancelData = new DefaultPieDataset<>();
//        int cancel = randomInt(5, 30);
//        cancelData.addValue("Vé bị hủy", cancel);
//        cancelData.addValue("Vé bình thường", 100 - cancel);
//        donutCancelRate.setDataset(cancelData);
//        donutCancelRate.putClientProperty(FlatClientProperties.STYLE,
//            "border:5,5,5,5,$Component.borderColor; arc:10");
//        
//        // Chart 3: Biểu đồ Bar doanh thu theo loại ghế
//        bieuDoDoanhThuTheoLoaiGhe = new Chart();
//        bieuDoDoanhThuTheoLoaiGhe.setFont(new Font("sansserif", Font.PLAIN, 18));
//        bieuDoDoanhThuTheoLoaiGhe.clear();
//        bieuDoDoanhThuTheoLoaiGhe.addData(new com.raven.chart.ModelChart("January", new double[]{500, 200, 80, 89}));
//        bieuDoDoanhThuTheoLoaiGhe.addData(new com.raven.chart.ModelChart("February", new double[]{600, 750, 90, 150}));
//        bieuDoDoanhThuTheoLoaiGhe.addData(new com.raven.chart.ModelChart("March", new double[]{200, 350, 460, 900}));
//        bieuDoDoanhThuTheoLoaiGhe.addData(new com.raven.chart.ModelChart("April", new double[]{480, 150, 750, 700}));
//        bieuDoDoanhThuTheoLoaiGhe.addData(new com.raven.chart.ModelChart("May", new double[]{350, 540, 300, 150}));
//        bieuDoDoanhThuTheoLoaiGhe.addData(new com.raven.chart.ModelChart("June", new double[]{190, 280, 81, 200}));
//        bieuDoDoanhThuTheoLoaiGhe.start();
//        
//        // Add Row 3: Sử dụng "split 3, grow"
//        add(panelTopRoutes, "split 3, grow");
//        add(donutCancelRate, "grow");
//        add(bieuDoDoanhThuTheoLoaiGhe, "grow, wrap");
    }
    
    // Hàm tiện ích sinh số ngẫu nhiên trong khoảng [min, max]
    private int randomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
