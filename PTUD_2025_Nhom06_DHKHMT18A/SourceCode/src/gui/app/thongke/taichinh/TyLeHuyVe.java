package gui.app.thongke.taichinh;


import java.awt.Color;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import com.formdev.flatlaf.FlatClientProperties;
import dao.ThongKeDAOImpl;
import entity.TyLeLoaiVe;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.List;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.pie.PieChart;

public class TyLeHuyVe extends JPanel {

    private PieChart tyLeVeBanTheoLoaiBieuDo;
    

    public TyLeHuyVe() {
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        initComponents();
        
    }

    private void initComponents() {
        setLayout(new MigLayout("insets 10, gap 10", "", ""));
        tyLeVeBanTheoLoaiBieuDo = new PieChart();
        tyLeVeBanTheoLoaiBieuDo.setOpaque(false);
        tyLeVeBanTheoLoaiBieuDo.setBackground(new Color(255, 255, 255));
        JLabel headerPie = new JLabel("Tỷ Lệ Hủy Vé");
        headerPie.setFont(headerPie.getFont().deriveFont(Font.BOLD, 15));
        tyLeVeBanTheoLoaiBieuDo.setHeader(headerPie);
        tyLeVeBanTheoLoaiBieuDo.setChartType(PieChart.ChartType.DONUT_CHART);
        tyLeVeBanTheoLoaiBieuDo.getChartColor().addColor(new Color(0x288142));
        tyLeVeBanTheoLoaiBieuDo.getChartColor().addColor(new Color(0xf04147));
        if (!java.beans.Beans.isDesignTime()) {
            ThongKeDAOImpl dao = new ThongKeDAOImpl();
            List<entity.TyLeHuyVe> results = dao.getTyLeHuyVe();
            DefaultPieDataset<String> ticketTypeData = new DefaultPieDataset<>();

            for (entity.TyLeHuyVe tyLeHuyVe : results) {
                ticketTypeData.addValue(tyLeHuyVe.getTrangThai(), tyLeHuyVe.getSoVe());
            }
            tyLeVeBanTheoLoaiBieuDo.setDataset(ticketTypeData);
        }
        try {
            Field fieldLegend = PieChart.class.getDeclaredField("panelLegend");
            fieldLegend.setAccessible(true);
            Object legendObj = fieldLegend.get(tyLeVeBanTheoLoaiBieuDo);
            if (legendObj instanceof JPanel) {
                JPanel panelLegend = (JPanel) legendObj;
                panelLegend.setOpaque(false);
                panelLegend.setBackground(new Color(0, 0, 0, 0));
                panelLegend.setFont(new Font(panelLegend.getFont().getFontName(), panelLegend.getFont().getStyle(), 25));
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        add(tyLeVeBanTheoLoaiBieuDo, "grow");
    }
    
    // Hàm tiện ích sinh số ngẫu nhiên trong khoảng [min, max]
    private int randomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
