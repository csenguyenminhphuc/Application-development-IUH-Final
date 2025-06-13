package gui.app.thongke.hieusuatnhanvien;


import java.awt.Color;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import dao.ThongKeDAOImpl;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.List;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.pie.PieChart;

public class TyLeNhanVienTheoNhomNangSuat extends JPanel {

    private PieChart tyLeVeBanTheoLoaiBieuDo;
    

    public TyLeNhanVienTheoNhomNangSuat() {
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        initComponents();
        
    }

    private void initComponents() {
        setLayout(new MigLayout("insets 10, gap 10", "", ""));
        tyLeVeBanTheoLoaiBieuDo = new PieChart();
        tyLeVeBanTheoLoaiBieuDo.setOpaque(false);
        tyLeVeBanTheoLoaiBieuDo.setBackground(new Color(255, 255, 255));
        JLabel headerPie = new JLabel("Tỷ Lệ Nhân Viên Theo Nhóm Năng Suất");
        headerPie.setFont(headerPie.getFont().deriveFont(Font.BOLD, 15));
        tyLeVeBanTheoLoaiBieuDo.setHeader(headerPie);
        tyLeVeBanTheoLoaiBieuDo.setChartType(PieChart.ChartType.DONUT_CHART);
        tyLeVeBanTheoLoaiBieuDo.getChartColor().addColor(new Color(0x9cdbf0));
        tyLeVeBanTheoLoaiBieuDo.getChartColor().addColor(new Color(0xb8efa5));
        tyLeVeBanTheoLoaiBieuDo.getChartColor().addColor(new Color(0xff9b9f));
        if (!java.beans.Beans.isDesignTime()) {
            ThongKeDAOImpl dao = new ThongKeDAOImpl();
            List<entity.TyLeNhanVienTheoNhomNangSuat> results = dao.getTyLeNhanVienTheoNhomNangSuat();
            DefaultPieDataset<String> ticketTypeData = new DefaultPieDataset<>();

            for (entity.TyLeNhanVienTheoNhomNangSuat tyLeHuyVe : results) {
                ticketTypeData.addValue(tyLeHuyVe.getTenNhomNangSuat(), tyLeHuyVe.getSoNhanVien());
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
