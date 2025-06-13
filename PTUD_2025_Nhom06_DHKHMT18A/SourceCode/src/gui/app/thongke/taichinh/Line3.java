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

public class Line3 extends JPanel {
    
    // Row 3: 3 biểu đồ
    private PieChart donutCancelRate;

    public Line3() {
        initComponents();
    }

    private void initComponents() {
        
        // Chart 2: PieChart dạng donut - Tỷ lệ hủy vé
        donutCancelRate = new PieChart();
        JLabel headerDonut = new JLabel("Tỷ Lệ Hủy Vé");
        headerDonut.putClientProperty(FlatClientProperties.STYLE, "font: +1");
        donutCancelRate.setHeader(headerDonut);
        donutCancelRate.setChartType(PieChart.ChartType.DONUT_CHART);
        donutCancelRate.getChartColor().addColor(new Color(0xf04147));
        donutCancelRate.getChartColor().addColor(new Color(0x288142));
        DefaultPieDataset<String> cancelData = new DefaultPieDataset<>();
        int cancel = randomInt(5, 30);
        cancelData.addValue("Vé bị hủy", cancel);
        cancelData.addValue("Vé bình thường", 100 - cancel);
        donutCancelRate.setDataset(cancelData);
        donutCancelRate.putClientProperty(FlatClientProperties.STYLE,
            "border:5,5,5,5,$Component.borderColor; arc:10");
        
        
        add(donutCancelRate, "grow");
    }
    
    // Hàm tiện ích sinh số ngẫu nhiên trong khoảng [min, max]
    private int randomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
