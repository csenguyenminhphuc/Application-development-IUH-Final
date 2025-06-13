package gui.app.thongke.taichinh;

import java.awt.Color;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import dao.ThongKeDAOImpl;
import entity.TyLeLoaiVe;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.List;

import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.pie.PieChart;

public class TyLeVeBanDuocTheoLoai extends JPanel {

    private PieChart tyLeVeBanTheoLoaiBieuDo;
    

    public TyLeVeBanDuocTheoLoai() {
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        initComponents();
        
    }

    private void initComponents() {
        // Sử dụng MigLayout, không đặt row/column constraints cứng - sử dụng "wrap" để xuống hàng
        setLayout(new MigLayout("insets 10, gap 10", "", ""));
        // -------------------
        // ROW 2: 2 biểu đồ
        // -------------------
        // Biểu đồ Pie: Tỷ lệ loại vé bán được
        tyLeVeBanTheoLoaiBieuDo = new PieChart();
        tyLeVeBanTheoLoaiBieuDo.setOpaque(false);
        tyLeVeBanTheoLoaiBieuDo.setBackground(new Color(255, 255, 255));
        JLabel headerPie = new JLabel("Tỷ Lệ Loại Vé Bán Được");
        headerPie.setFont(headerPie.getFont().deriveFont(Font.BOLD, 15));
        tyLeVeBanTheoLoaiBieuDo.setHeader(headerPie);
//        tyLeVeBanTheoLoaiBieuDo.setChartType(PieChart.ChartType.DONUT_CHART);
        tyLeVeBanTheoLoaiBieuDo.getChartColor().addColor(new Color(0x0D62FF));
        tyLeVeBanTheoLoaiBieuDo.getChartColor().addColor(new Color(0xffc2f0));
        tyLeVeBanTheoLoaiBieuDo.getChartColor().addColor(new Color(0x5CE9E6));
        if (!java.beans.Beans.isDesignTime()) {
            ThongKeDAOImpl dao = new ThongKeDAOImpl();
            List<TyLeLoaiVe> results = dao.getSoLuongVeTheoLoai();
            DefaultPieDataset<String> ticketTypeData = new DefaultPieDataset<>();

            for (TyLeLoaiVe tyLeLoaiVe : results) {
                String code = tyLeLoaiVe.getMaLoaiVe();
                String displayName;
                // Sử dụng switch-case để đổi code thành tên hiển thị tương ứng
                switch (code) {
                    case "LV-TE":
                        displayName = "Vé trẻ em";
                        break;
                    case "LV-NL":
                        displayName = "Vé người lớn";
                        break;
                    case "LV-HSSV":
                        displayName = "Vé HSSV";
                        break;
                    default:
                        // Nếu không khớp, hiển thị mã gốc
                        displayName = code;
                        break;
                }
                ticketTypeData.addValue(displayName, tyLeLoaiVe.getSoVe());
            }
            tyLeVeBanTheoLoaiBieuDo.setDataset(ticketTypeData);
        }
        try {
            // Lấy trường "panelLegend" trong PieChart (nếu tên trường không thay đổi trong phiên bản của bạn)
            Field fieldLegend = PieChart.class.getDeclaredField("panelLegend");
            fieldLegend.setAccessible(true);
            Object legendObj = fieldLegend.get(tyLeVeBanTheoLoaiBieuDo);
            if (legendObj instanceof JPanel) {
                JPanel panelLegend = (JPanel) legendObj;
                panelLegend.setOpaque(false);
                panelLegend.setBackground(new Color(0, 0, 0, 0));
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
