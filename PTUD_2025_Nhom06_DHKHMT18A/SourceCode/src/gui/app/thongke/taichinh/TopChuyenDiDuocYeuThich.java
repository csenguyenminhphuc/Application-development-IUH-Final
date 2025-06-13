package gui.app.thongke.taichinh;

import dao.ThongKeDAOImpl;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TopChuyenDiDuocYeuThich extends JPanel {
    
    private ArrayList<String> routeNames;
    private ArrayList<String> ticketCounts;
    private final Color[] dotColors = {
        new Color(150, 150, 150), 
        new Color(128, 0, 128),   
        new Color(0, 100, 0),     
        new Color(255, 0, 0),
    };

    private final String title = "Chuyến Đi Được Ưa Chuộng";
    private final int padding = 10;
    private final int dotSize = 14;
    private final int gapBetweenDotAndText = 8;
    private final int lineSpacing = 10;
    private final int textLineSpacing = 5;

    private double[] dotYs;

    public TopChuyenDiDuocYeuThich() {
        setOpaque(true);
        setBackground(new Color(255, 255, 255));
        
        // Khởi tạo để tránh NullPointerException
        routeNames = new ArrayList<>();
        ticketCounts = new ArrayList<>();

        if (java.beans.Beans.isDesignTime()) {
            // Dữ liệu giả cho chế độ design
            routeNames.add("Từ Hà Nội đến Sài Gòn");
            routeNames.add("Từ Đà Nẵng đến Huế");
            routeNames.add("Từ Đà Nẵng đến Huế");
            ticketCounts.add("100 vé đã bán được");
            ticketCounts.add("50 vé đã bán được");
            ticketCounts.add("50 vé đã bán được");
        } else {
            // Tải dữ liệu thật khi chạy ứng dụng
            loadData();
        }
        
        dotYs = new double[routeNames.size()];
        revalidate();
        repaint();
    }

    public void loadData() {
        try {
            ThongKeDAOImpl dao = new ThongKeDAOImpl();
            List<entity.TopChuyenDi> list = dao.getTopChuyenDi();
            routeNames.clear();
            ticketCounts.clear();
            if (list != null) {
                for (entity.TopChuyenDi top : list) {
                    routeNames.add("Từ " + top.getMaGaDi() + " đến " + top.getMaGaDen());
                    ticketCounts.add(top.getSoVe() + " vé đã bán được");
                }
            }
            dotYs = new double[routeNames.size()];
            revalidate();
            repaint();
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu: " + e.getMessage());
            routeNames.add("Lỗi tải dữ liệu");
            ticketCounts.add("N/A");
            dotYs = new double[routeNames.size()];
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ nền và viền
        int arc = 10;
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2.setColor(new Color(200, 200, 200));

        // Thiết lập font
        Font titleFont = new Font("Dialog", Font.BOLD, 16);
        Font routeFont = new Font("Dialog", Font.BOLD, 14);
        Font ticketFont = new Font("Dialog", Font.PLAIN, 14);
        g2.setFont(titleFont);

        // Vẽ tiêu đề
        FontMetrics titleMetrics = g2.getFontMetrics();
        int titleHeight = titleMetrics.getHeight();
        int x = padding;
        int y = padding + titleMetrics.getAscent();
        g2.setColor(Color.BLACK);
        g2.setFont(titleFont);
        g2.drawString(title, x, y);

        // Vẽ các tuyến đường
        g2.setFont(routeFont);
        FontMetrics routeMetrics = g2.getFontMetrics();
        int routeHeight = routeMetrics.getHeight();
        y += titleHeight + lineSpacing;

        // Tính trước tọa độ y của các chấm tròn
        for (int i = 0; i < routeNames.size(); i++) {
            int yStart = y;
            y += routeHeight + textLineSpacing;
            int yDot = yStart + (y - yStart) / 2;
            dotYs[i] = yDot;
            y += routeHeight + lineSpacing;
        }

        // Vẽ đường thẳng nối các chấm tròn trước (để nó nằm dưới)
        if (dotYs.length > 1) {
            g2.setColor(new Color(231, 239, 255));
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.draw(new Line2D.Double(x + dotSize / 2, dotYs[0], x + dotSize / 2, dotYs[dotYs.length - 1]));
        }

        // Reset y để vẽ các chấm tròn và văn bản lên trên
        y = padding + titleMetrics.getAscent() + titleHeight + lineSpacing;

        for (int i = 0; i < routeNames.size(); i++) {
            int yStart = y;
                
            // Vẽ tên tuyến đường
            g2.setFont(routeFont);
            g2.setColor(Color.BLACK);
            int textX = x + dotSize + gapBetweenDotAndText;
            g2.drawString(routeNames.get(i), textX, y + routeMetrics.getAscent() / 2);
            g2.setFont(ticketFont);
            g2.setColor(new Color(152, 152, 152));
            // Vẽ số vé đã bán ở dòng tiếp theo
            y += routeHeight + textLineSpacing;
            g2.drawString(ticketCounts.get(i), textX, y + routeMetrics.getAscent() / 2);

            // Vẽ chấm tròn ở giữa hai dòng
            // Sử dụng modulo để lặp lại màu nếu vượt quá số lượng màu trong dotColors
            g2.setColor(dotColors[i % dotColors.length]);
            g2.fill(new Ellipse2D.Double(x, dotYs[i] - dotSize / 2, dotSize, dotSize));

            // Chuyển đến tuyến đường tiếp theo
            y += routeHeight + lineSpacing;
        }

        g2.dispose();
    }

    @Override
    public java.awt.Dimension getPreferredSize() {
        Font titleFont = new Font("Dialog", Font.BOLD, 16);
        Font routeFont = new Font("Dialog", Font.BOLD, 14);
        FontMetrics titleMetrics = getFontMetrics(titleFont);
        FontMetrics routeMetrics = getFontMetrics(routeFont);

        // Tính chiều rộng
        int maxWidth = titleMetrics.stringWidth(title);
        for (int i = 0; i < routeNames.size(); i++) {
            int routeWidth = routeMetrics.stringWidth(routeNames.get(i));
            int ticketWidth = routeMetrics.stringWidth(ticketCounts.get(i));
            maxWidth = Math.max(maxWidth, Math.max(routeWidth, ticketWidth) + dotSize + gapBetweenDotAndText);
        }
        maxWidth += 2 * padding;

        // Tính chiều cao
        int titleHeight = titleMetrics.getHeight();
        int routeHeight = routeMetrics.getHeight();
        int totalHeight = padding + titleHeight + lineSpacing + (routeHeight * 2 + textLineSpacing + lineSpacing) * routeNames.size() + padding;

        return new java.awt.Dimension(maxWidth, totalHeight);
    }

    @Override
    public java.awt.Dimension getMinimumSize() {
        return new java.awt.Dimension(200, 100); // Kích thước tối thiểu
    }
}