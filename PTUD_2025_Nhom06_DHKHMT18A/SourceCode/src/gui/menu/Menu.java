package gui.menu;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import gui.menu.mode.ToolBarAccentColor;

public class Menu extends JPanel implements MouseListener {

    private static final long serialVersionUID = 1L;
    private final List<MenuEvent> events = new ArrayList<>();
    private boolean menuFull = true;
    private final String headerName = "Ga 8386";
    protected final boolean hideMenuTitleOnMinimum = true;
    protected final int menuTitleLeftInset = 5;
    protected final int menuTitleVgap = 5;
    protected final int menuMaxWidth = 260;
    protected final int menuMinWidth = 60;
    protected final int headerFullHgap = 5;
    private static final String[][] QUAN_LY_MENU_ITEMS = {
        {"Trang chủ"},
        {"Tra cứu", "Nhân viên"},
//        {"Thống kê", "Hiệu suất làm việc", "Số lượng nhân viên"},
        {"Thống kê", "Hiệu suất làm việc"},
        {"Quản lý Tàu"},
        {"Quản lý Địa điểm ga"},
        {"Quản lý Nhân viên"},
        {"Quản lý Khuyến mãi"},
        {"Quản lý Chuyến đi"},
        {"AI trợ giúp"},
        {"Tài khoản"},
        {"Trợ giúp"},
        //{"Cài đặt"},
        {"Đăng xuất"}
    };

    private static final String[][] NHAN_VIEN_MENU_ITEMS = {
        {"Trang chủ"},
        {"Vé", "Đặt vé", "Đổi vé", "Trả vé"},
        {"Tra cứu", "Khuyến mãi", "Danh sách chuyến đi", "Tàu", "Vé", "Ga", "Khách hàng"},
//        {"Thống kê", "Tài chính", "Khách hàng"},
        {"Thống kê", "Tài chính"},
//        {"Quản lý Khách hàng"},
        {"AI trợ giúp"},
        {"Tài khoản"},
        {"Trợ giúp"},
        //{"Cài đặt"},
        {"Đăng xuất"}
    };
    private JLabel header;
    private JScrollPane scroll;
    private JPanel panelMenu;
//	private LightDarkMode lightDarkMode;
    private ToolBarAccentColor toolBarAccentColor;
    private String special = "A";
    private int count = 0;

    private boolean menuExpanded = true;
    private Timer collapseTimer;

    public boolean isMenuFull() {
        return menuFull;
    }

    public void setMenuFull(boolean menuFull) {
        this.menuFull = menuFull;
        if (menuFull) {
            header.setText(headerName);
            header.setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? JLabel.LEFT : JLabel.RIGHT);
        } else {
            header.setText("");
            header.setHorizontalAlignment(JLabel.CENTER);
            header.setAlignmentX(CENTER_ALIGNMENT);
        }
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).setFull(menuFull);
            }
        }
//		lightDarkMode.setMenuFull(menuFull);
        toolBarAccentColor.setMenuFull(menuFull);
    }

    public Menu() {
        // Nếu bạn muốn mặc định role nào đó:
        this("DefaultRole");
    }

    public Menu(String role) {
        init(role);
    }

    private void init(String role) {
        setLayout(new MenuLayout());
        putClientProperty(FlatClientProperties.STYLE,
                "" + "border:20,2,2,2;" + "background:$Menu.background;" + "arc:10");
        header = new JLabel(headerName);
        header.putClientProperty(FlatClientProperties.STYLE,
                "" + "font:$Menu.header.font;" + "foreground:$Menu.foreground");
        FlatSVGIcon icon = new FlatSVGIcon("gui/icon/svg/cahn.svg", 40, 40);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter() {
            @Override
            public java.awt.Color filter(java.awt.Color color) {
                return java.awt.Color.WHITE; // Đổi tất cả màu thành trắng
            }
        });
        header.setIcon(icon);

        // Menu
        scroll = new JScrollPane();
        panelMenu = new JPanel(new MenuItemLayout(this));
        panelMenu.putClientProperty(FlatClientProperties.STYLE, "" + "border:5,5,5,5;" + "background:$Menu.background");

        scroll.setViewportView(panelMenu);
        scroll.putClientProperty(FlatClientProperties.STYLE, "" + "border:null");
        JScrollBar vscroll = scroll.getVerticalScrollBar();
        vscroll.setUnitIncrement(10);
        vscroll.putClientProperty(FlatClientProperties.STYLE,
                "" + "width:$Menu.scroll.width;" + "trackInsets:$Menu.scroll.trackInsets;"
                + "thumbInsets:$Menu.scroll.thumbInsets;" + "background:$Menu.ScrollBar.background;"
                + "thumb:$Menu.ScrollBar.thumb");
        createMenu(role);
//		lightDarkMode = new LightDarkMode();
        toolBarAccentColor = new ToolBarAccentColor(this);
        toolBarAccentColor.setVisible(FlatUIUtils.getUIBoolean("AccentControl.show", false));
        add(header);
        add(scroll);
//		add(lightDarkMode);
        add(toolBarAccentColor);
    }

    private void createMenu(String role) {
        String[][] menuItems = role.equalsIgnoreCase("Nhân viên quản lý") ? QUAN_LY_MENU_ITEMS : NHAN_VIEN_MENU_ITEMS;
        int index = 0;
        for (int i = 0; i < menuItems.length; i++) {
            String menuName = menuItems[i][0];
            if (menuName.startsWith("~") && menuName.endsWith("~")) {
                panelMenu.add(createTitle(menuName));
            } else {
                if (role.equalsIgnoreCase("Employee") && i == 3) {
                    continue;
                }
                if (menuItems[i].length > 1) {
                    special = "A";
                    count++;
                    special += Integer.toString(count);
                }
                MenuItem menuItem = new MenuItem(this, menuItems[i], index++, events, role, special);
                panelMenu.add(menuItem);
            }
        }
    }

    private JLabel createTitle(String title) {
        String menuName = title.substring(1, title.length() - 1);
        JLabel lbTitle = new JLabel(menuName);
        lbTitle.putClientProperty(FlatClientProperties.STYLE,
                "" + "font:bold $Menu.label.font;" + "foreground:$Menu.title.foreground");
//		lbTitle.setHorizontalAlignment(JLabel.CENTER);
        return lbTitle;
    }

    public void setSelectedMenu(int index, int subIndex) {
        runEvent(index, subIndex);
    }

    protected void setSelected(int index, int subIndex) {
        int size = panelMenu.getComponentCount();
        for (int i = 0; i < size; i++) {
            Component com = panelMenu.getComponent(i);
            if (com instanceof MenuItem) {
                MenuItem item = (MenuItem) com;
                if (item.getMenuIndex() == index) {
                    item.setSelectedIndex(subIndex);
                } else {
                    item.setSelectedIndex(-1);
                }
            }
        }
    }

    protected void runEvent(int index, int subIndex) {
        MenuAction menuAction = new MenuAction();
        for (MenuEvent event : events) {
            event.menuSelected(index, subIndex, menuAction);
        }
        if (!menuAction.isCancel()) {
            setSelected(index, subIndex);
        }
    }

    public void addMenuEvent(MenuEvent event) {
        events.add(event);
    }

    public void hideMenuItem() {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).hideMenuItem();
            }
        }
        revalidate();
    }

    public boolean isHideMenuTitleOnMinimum() {
        return hideMenuTitleOnMinimum;
    }

    public int getMenuTitleLeftInset() {
        return menuTitleLeftInset;
    }

    public int getMenuTitleVgap() {
        return menuTitleVgap;
    }

    public int getMenuMaxWidth() {
        return menuMaxWidth;
    }

    public int getMenuMinWidth() {
        return menuMinWidth;
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
                return new Dimension(5, 5);
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
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int gap = UIScale.scale(5);
                int sheaderFullHgap = UIScale.scale(headerFullHgap);
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int iconWidth = width;
                int iconHeight = header.getPreferredSize().height;
                int hgap = menuFull ? sheaderFullHgap : 0;
                int accentColorHeight = 0;
                if (toolBarAccentColor.isVisible()) {
                    accentColorHeight = toolBarAccentColor.getPreferredSize().height + gap;
                }

                header.setBounds(x + hgap, y, iconWidth - (hgap * 2), iconHeight);
                int ldgap = UIScale.scale(10);
                int ldWidth = width - ldgap * 2;
//				int ldHeight = lightDarkMode.getPreferredSize().height;
                int ldx = x + ldgap;
                int ldy = y + height - ldgap - accentColorHeight;

                int menux = x;
                int menuy = y + iconHeight + gap;
                int menuWidth = width;
                int menuHeight = height - (iconHeight + gap) - (ldgap * 2) - (accentColorHeight);
                scroll.setBounds(menux, menuy, menuWidth, menuHeight);

//				lightDarkMode.setBounds(ldx, ldy, ldWidth, ldHeight);
//				if (toolBarAccentColor.isVisible()) {
//					int tbheight = toolBarAccentColor.getPreferredSize().height;
//					int tbwidth = Math.min(toolBarAccentColor.getPreferredSize().width, ldWidth);
//					int tby = y + height - tbheight - ldgap;
//					int tbx = ldx + ((ldWidth - tbwidth) / 2);
//					toolBarAccentColor.setBounds(tbx, tby, tbwidth, tbheight);
//				}
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    
    public static void main(String[] args) {
        new Menu().setVisible(true);
    }
}
