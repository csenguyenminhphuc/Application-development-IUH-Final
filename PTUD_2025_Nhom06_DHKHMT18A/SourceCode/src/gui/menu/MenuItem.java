package gui.menu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.util.HashMap;
import java.util.Map;

public class MenuItem extends JPanel {
	private static final long serialVersionUID = 1L;
	private final List<MenuEvent> events;
	private Menu menu;
	private final String menus[];
	private int menuIndex;
	private final int menuItemHeight = 38;
	private final int subMenuItemHeight = 35;
	private final int subMenuLeftGap = 34;
	private final int firstGap = 5;
	private final int bottomGap = 5;
	private boolean menuShow;
	private float animate;
	private PopupSubmenu popup;
        private final String special;
        
        private static final Map<String, String> ICON_MAPPING = new HashMap<>();
        static {
            // Quản lý
            ICON_MAPPING.put("Trang chủ", "trangchu");
            ICON_MAPPING.put("Tra cứu", "tracuu");
            ICON_MAPPING.put("Tra cứu_Nhân viên", "tracuu_nhanvien");
            ICON_MAPPING.put("Thống kê", "thongke");
            ICON_MAPPING.put("Thống kê_Số lượng nhân viên", "thongke_soluongnhanvien");
            ICON_MAPPING.put("Thống kê_Hiệu suất làm việc", "thongke_hieusuat");
            ICON_MAPPING.put("Quản lý Tàu", "quanlytau");
            ICON_MAPPING.put("Quản lý Địa điểm ga", "quanlyga");
            ICON_MAPPING.put("Quản lý Nhân viên", "quanlynhanvien");
            ICON_MAPPING.put("Quản lý Khuyến mãi", "quanlykhuyenmai");
            ICON_MAPPING.put("Quản lý Chuyến đi", "quanlychuyendi");
            ICON_MAPPING.put("AI trợ giúp", "aitrogiup");
            ICON_MAPPING.put("Tài khoản", "taikhoan");
            ICON_MAPPING.put("Trợ giúp", "trogiup");
            ICON_MAPPING.put("Cài đặt", "caidat");
            ICON_MAPPING.put("Đăng xuất", "dangxuat");

            // Nhân viên
            ICON_MAPPING.put("Vé", "ve");
            ICON_MAPPING.put("Vé_Đặt vé", "ve_datve");
            ICON_MAPPING.put("Vé_Đổi vé", "ve_doive");
            ICON_MAPPING.put("Vé_Trả vé", "ve_trave");
            ICON_MAPPING.put("Tra cứu_Khuyến mãi", "tracuu_khuyenmai");
            ICON_MAPPING.put("Tra cứu_Danh sách chuyến đi", "tracuu_chuyendi");
            ICON_MAPPING.put("Tra cứu_Tàu", "tracuu_tau");
            ICON_MAPPING.put("Tra cứu_Vé", "tracuu_ve");
            ICON_MAPPING.put("Tra cứu_Ga", "tracuu_ga");
            ICON_MAPPING.put("Tra cứu_Khách hàng", "tracuu_khachhang");
            ICON_MAPPING.put("Quản lý Khách hàng", "quanlykhachhang");
            ICON_MAPPING.put("Thống kê_Tài chính", "thongke_taichinh");
            ICON_MAPPING.put("Thống kê_Khách hàng", "thongke_khachhang");
        }

	public MenuItem(Menu menu, String menus[], int menuIndex, List<MenuEvent> events, String role, String special) {
        this.menu = menu;
        this.menus = menus;
        this.menuIndex = menuIndex;
        this.events = events;
        this.special = special;
        init(role, special);
    }

    public boolean isMenuShow() {
        return menuShow;
    }

    public void setMenuShow(boolean menuShow) {
        this.menuShow = menuShow;
    }

    public float getAnimate() {
        return animate;
    }

    public void setAnimate(float animate) {
        this.animate = animate;
    }

    public String[] getMenus() {
        return menus;
    }

    public int getMenuIndex() {
        return menuIndex;
    }

    private Icon getIcon(String menuName) {
        String iconName = ICON_MAPPING.getOrDefault(menuName, "default");
        Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("gui/menu/icon/" + iconName + ".svg", 25, 25);
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#000000"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
    }

    private Icon getSubMenuIcon(int subIndex, String menuName) {
        String key = menuName + "_" + menus[subIndex];
        String iconName = ICON_MAPPING.getOrDefault(key, "default");
        Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("gui/menu/icon/" + iconName + ".svg", 25, 25);
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#000000"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
    }

    private Icon getSelectedIcon(String menuName) {
        String iconName = ICON_MAPPING.getOrDefault(menuName, "default");
        Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("gui/menu/icon/" + iconName + "_selected.svg", 25, 25);
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#000000"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
    }

    private Icon getSelectedSubMenuIcon(int subIndex, String menuName) {
        String key = menuName + "_" + menus[subIndex];
        String iconName = ICON_MAPPING.getOrDefault(key, "default");
        Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon("gui/menu/icon/" + iconName + "_selected.svg", 25, 25);
        FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
        f.add(Color.decode("#000000"), lightColor, darkColor);
        icon.setColorFilter(f);
        return icon;
    }

    private void init(String role, String special) {
        setLayout(new MenuLayout());
        putClientProperty(FlatClientProperties.STYLE,
                "" + "background:$Menu.background;" + "foreground:$Menu.lineColor");
        for (int i = 0; i < menus.length; i++) {
            JButton menuItem = createButtonItem(menus[i]);
            menuItem.setHorizontalAlignment(
                    menuItem.getComponentOrientation().isLeftToRight() ? JButton.LEADING : JButton.TRAILING);
            if (i == 0) {
                menuItem.setIcon(getIcon(menus[0]));
                menuItem.setSelectedIcon(getSelectedIcon(menus[0]));
                menuItem.addActionListener((ActionEvent e) -> {
                    if (menus.length > 1) {
                        if (menu.isMenuFull()) {
                            MenuAnimation.animate(MenuItem.this, !menuShow);
                        } else {
                            popup.show(MenuItem.this, (int) MenuItem.this.getWidth() + UIScale.scale(5),
                                    UIScale.scale(menuItemHeight) / 2);
                        }
                    } else {
                        menu.runEvent(menuIndex, 0);
                    }
                });
            } else {
                final int subIndex = i;
                menuItem.setIcon(getSubMenuIcon(subIndex, menus[0]));
                menuItem.setSelectedIcon(getSelectedSubMenuIcon(subIndex, menus[0]));
                menuItem.addActionListener((ActionEvent e) -> {
                    menu.runEvent(menuIndex, subIndex);
                });
            }
            add(menuItem);
        }
        popup = new PopupSubmenu(getComponentOrientation(), menu, menuIndex, menus);
    }

    protected void setSelectedIndex(int index) {
        int size = getComponentCount();
        boolean selected = false;
        for (int i = 0; i < size; i++) {
            Component com = getComponent(i);
            if (com instanceof JButton) {
                ((JButton) com).setSelected(i == index);
                if (i == index) {
                    selected = true;
                }
            }
        }
        ((JButton) getComponent(0)).setSelected(selected);
        popup.setSelectedIndex(index);
    }

    private JButton createButtonItem(String text) {
        JButton button = new JButton(text);
        button.putClientProperty(FlatClientProperties.STYLE,
                "" + "background:$Menu.background;" + "foreground:$Menu.foreground;"
                        + "selectedBackground:$Menu.button.selectedBackground;"
                        + "selectedForeground:$Menu.button.selectedForeground;" + "borderWidth:0;" + "focusWidth:0;"
                        + "innerFocusWidth:0;" + "arc:10;" + "iconTextGap:10;" + "margin:3,11,3,11");
        return button;
    }

    public void hideMenuItem() {
        animate = 0;
        menuShow = false;
    }

    public void setFull(boolean full) {
        if (full) {
            int size = getComponentCount();
            for (int i = 0; i < size; i++) {
                Component com = getComponent(i);
                if (com instanceof JButton) {
                    JButton button = (JButton) com;
                    button.setText(menus[i]);
                    button.setHorizontalAlignment(
                            getComponentOrientation().isLeftToRight() ? JButton.LEFT : JButton.RIGHT);
                }
            }
        } else {
            for (Component com : getComponents()) {
                if (com instanceof JButton) {
                    JButton button = (JButton) com;
                    button.setText("");
                    button.setHorizontalAlignment(JButton.CENTER);
                }
            }
            animate = 0f;
            menuShow = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (animate > 0) {
            int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
            int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
            int smenuItemHeight = UIScale.scale(menuItemHeight);
            int sfirstGap = UIScale.scale(firstGap);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Path2D.Double p = new Path2D.Double();
            int last = getComponent(getComponentCount() - 1).getY() + (ssubMenuItemHeight / 2);
            boolean ltr = getComponentOrientation().isLeftToRight();
            int round = UIScale.scale(10);
            int x = ltr ? (ssubMenuLeftGap - round) : (getWidth() - (ssubMenuLeftGap - round));
            p.moveTo(x, smenuItemHeight + sfirstGap);
            p.lineTo(x, last);
            for (int i = 1; i < getComponentCount(); i++) {
                int comY = getComponent(i).getY() + (ssubMenuItemHeight / 2);
                p.moveTo(x, comY);
                p.lineTo(x + (ltr ? 20 : -20), comY);
            }
            g2.setColor(getForeground());
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setStroke(new BasicStroke(UIScale.scale(1f)));
            g2.draw(p);
            g2.dispose();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (menus.length > 1) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(FlatUIUtils.getUIColor("Menu.arrowColor", getForeground()));
            int smenuItemHeight = UIScale.scale(menuItemHeight);
            boolean ltr = getComponentOrientation().isLeftToRight();
            g2.setStroke(new BasicStroke(UIScale.scale(1f)));
            if (menu.isMenuFull()) {
                int arrowWidth = UIScale.scale(10);
                int arrowHeight = UIScale.scale(5);
                int ax = ltr ? (getWidth() - arrowWidth * 2) : arrowWidth;
                int ay = (smenuItemHeight - arrowHeight) / 2;
                Path2D p = new Path2D.Double();
                p.moveTo(0, animate * arrowHeight);
                p.lineTo(arrowWidth / 2, (1f - animate) * arrowHeight);
                p.lineTo(arrowWidth, animate * arrowHeight);
                g2.translate(ax, ay);
                g2.draw(p);
            } else {
                int arrowWidth = UIScale.scale(4);
                int arrowHeight = UIScale.scale(8);
                int ax = ltr ? (getWidth() - arrowWidth - UIScale.scale(3)) : UIScale.scale(3);
                int ay = (smenuItemHeight - arrowHeight) / 2;
                Path2D p = new Path2D.Double();
                if (ltr) {
                    p.moveTo(0, 0);
                    p.lineTo(arrowWidth, arrowHeight / 2);
                    p.lineTo(0, arrowHeight);
                } else {
                    p.moveTo(arrowWidth, 0);
                    p.lineTo(0, arrowHeight / 2);
                    p.lineTo(arrowWidth, arrowHeight);
                }
                g2.translate(ax, ay);
                g2.draw(p);
            }
            g2.dispose();
        }
    }

    private Shape createCurve(int round, int x, int y, boolean ltr) {
        Path2D p2 = new Path2D.Double();
        p2.moveTo(x, y - round);
        p2.lineTo(x, y);
        return p2;
    }

    public List<MenuEvent> getEvents() {
        return events;
    }

    private class MenuLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets inset = parent.getInsets();
                int width = parent.getWidth();
                int height = inset.top + inset.bottom;
                int size = parent.getComponentCount();
                Component item = parent.getComponent(0);
                height += UIScale.scale(menuItemHeight);
                if (item.isVisible()) {
                    int subMenuHeight = size > 1 ? UIScale.scale(firstGap) + UIScale.scale(bottomGap) : 0;
                    for (int i = 1; i < size; i++) {
                        Component com = parent.getComponent(i);
                        if (com.isVisible()) {
                            subMenuHeight += UIScale.scale(subMenuItemHeight);
                        }
                    }
                    height += (subMenuHeight * animate);
                } else {
                    height = 0;
                }
                return new Dimension(width, height);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int size = parent.getComponentCount();
                for (int i = 0; i < size; i++) {
                    Component com = parent.getComponent(i);
                    if (com.isVisible()) {
                        if (i == 0) {
                            int smenuItemHeight = UIScale.scale(menuItemHeight);
                            int sfirstGap = UIScale.scale(firstGap);
                            com.setBounds(x, y, width, smenuItemHeight);
                            y += smenuItemHeight + sfirstGap;
                        } else {
                            int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
                            int subMenuX = ltr ? ssubMenuLeftGap : 0;
                            int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
                            com.setBounds(x + subMenuX, y, width - ssubMenuLeftGap, ssubMenuItemHeight);
                            y += ssubMenuItemHeight;
                        }
                    }
                }
            }
        }
    }
}