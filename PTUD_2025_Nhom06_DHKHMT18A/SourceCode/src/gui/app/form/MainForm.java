package gui.app.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;

import entity.NhanVien;
import gui.app.ve.DatVe;
import gui.app.Main;
import gui.app.ai.Ai;
import gui.app.hoso.QuanLyGa;
import gui.app.hoso.QuanLyNhanVien;
import gui.app.hoso.TaiKhoanNhanVien;
import gui.app.hoso.TaiKhoanQuanLy;
import gui.app.quanlychuyendi.QuanLyChuyenDi;
import gui.app.quanlykhuyenmai.QuanLyKhuyenMai;
import gui.app.quanlytau.QuanLyTau;
import gui.app.thongke.hieusuatnhanvien.ThongKeHieuSuatNhanVien;
import gui.app.thongke.taichinh.ThongKeTaiChinh;
import gui.app.tracuu.TraCuuDanhSachChuyenDi;
import gui.app.tracuu.TraCuuGa;
import gui.app.tracuu.TraCuuKhachHang;
import gui.app.tracuu.TraCuuKhuyenMai;
import gui.app.tracuu.TraCuuNhanVien;
import gui.app.tracuu.TraCuuTau;
import gui.app.tracuu.TraCuuVe;
import gui.app.trangchu.TrangChuNhanVien;
import gui.app.trangchu.TrangChuQuanLy;
import gui.app.trogiup.TroGiupNhanVien;
import gui.app.trogiup.TroGiupQuanLy;
import gui.app.ve.DatVe_ChonChuyen;
import gui.app.ve.DoiVe_TimVeDoi;
import gui.app.ve.TraVe_ChonVeTra;
import gui.menu.Menu;
import gui.menu.MenuAction;
import gui.app.ve.DatVeRunAll;
import gui.app.ve.DoiVeRunAll;
import gui.app.ve.TraVeRunAll;

public class MainForm extends JLayeredPane {

    private static final long serialVersionUID = 1L;
    private Menu menu;
    private JPanel panelBody;
    private JButton menuButton;

    public MainForm(NhanVien employee) {
        init(employee);
    }

    private void init(NhanVien employee) {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainFormLayout());
        menu = new Menu(employee.getVaiTro());
        panelBody = new JPanel(new BorderLayout());
        initMenuArrowIcon();
        menuButton.putClientProperty(FlatClientProperties.STYLE,
                "" + "background:$Menu.button.background;" + "arc:999;" + "focusWidth:0;" + "borderWidth:0");
        menuButton.addActionListener((ActionEvent e) -> {
            setMenuFull(!menu.isMenuFull());
        });
        initMenuEvent(employee);
        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        add(menuButton);
        add(menu);
        add(panelBody);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        initMenuArrowIcon();
    }

    private void initMenuArrowIcon() {
        if (menuButton == null) {
            menuButton = new JButton();
        }
        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
        menuButton.setIcon(new FlatSVGIcon("gui/menu/icon/" + icon, 0.8f));
    }

    private void initMenuEvent(NhanVien employee) {
        menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {
            if (employee.getVaiTro().equalsIgnoreCase("Nhân viên quản lý")) {
                switch (index) {
                    case 0: //Trang chủ
                        Main.showMainForm(new TrangChuQuanLy(employee));
                        break;
                    case 1: //Tra cứu
                        switch (subIndex) {
                            case 1: //Tra cứu nhân viên
                                Main.showMainForm(new TraCuuNhanVien(employee));
                                break;
                            default:
                                action.cancel();
                                break;
                        }
                        break;
                    case 2: //Thống kê
                        switch (subIndex) {
                            case 1: //Hiệu suất làm việc
                                Main.showMainForm(new ThongKeHieuSuatNhanVien(employee));
                                break;
                            case 2: //Số lượng nhân viên
//								Main.showMainForm(new QuanLyNuocUongGUI());
                                break;
                            default:
                                action.cancel();
                                break;
                        }
                        break;
                    case 3: //Quản lý tàu
                        Main.showMainForm(new QuanLyTau(employee));
                        break;
                    case 4: //Quản lý địa điểm ga
                        Main.showMainForm(new QuanLyGa());
                        break;
                    case 5: //Quản lý nhân viên
                        Main.showMainForm(new QuanLyNhanVien(employee));
                        break;
                    case 6: //Quản lý khuyến mãi
                        Main.showMainForm(new QuanLyKhuyenMai(employee));
                        break;
                    case 7: //Quản lý chuyến đi
                        Main.showMainForm(new QuanLyChuyenDi(employee));
                        break;
                    case 8: //AI trợ giúp
                        Main.showMainForm(new Ai(employee));
                        break;
                    case 9: //Tài khoản
                        Main.showMainForm(new TaiKhoanQuanLy(employee));
                        break;
                    case 10: //Trợ giúp
                        Main.showMainForm(new TroGiupQuanLy(employee));
                        break;
  //                  case 11: //Cài đặt
//                        Main.showMainForm(new TaiKhoanNhanVien());
 //                       break;
                    case 11: //Đăng xuất
                        Main.logout();
                        break;
                    default:
                        action.cancel();
                        break;
                }
            } else { //Nhân viên
                switch (index) {
                    case 0: //Trang chủ
                        Main.showMainForm(new TrangChuNhanVien(employee));
                        break;
                    case 1: //Vé
                        switch (subIndex) {
                            case 1: //Đặt vé
                                Main.showMainForm(new DatVeRunAll(employee));
                                
                                break;
                            case 2: //Đổi vé
                                Main.showMainForm(new DoiVeRunAll(employee));
                                break;
                            case 3: //Hủy vé
                                Main.showMainForm(new TraVeRunAll(employee));
                                break;
                            default:
                                action.cancel();
                                break;
                        }
                        break;
                    case 2: //Tra cứu
                        switch (subIndex) {
                            case 1: //Tra cứu khuyến mãi
                                Main.showMainForm(new TraCuuKhuyenMai(employee));
                                break;
                            case 2: //Tra cứu danh sách chuyến đi
                                Main.showMainForm(new TraCuuDanhSachChuyenDi(employee));
                                break;
                            case 3: //Tra cứu tàu
				Main.showMainForm(new TraCuuTau(employee));
                                break;
                            case 4: //Tra cứu vé
				Main.showMainForm(new TraCuuVe(employee));
                                break;
                            case 5: //Tra cứu ga
				Main.showMainForm(new TraCuuGa(employee));
                                break;
                            case 6: //Tra cứu khách hàng
				Main.showMainForm(new TraCuuKhachHang(employee));
                                break;
                            default:
                                action.cancel();
                                break;
                        }
                        break;
                    case 3: //Thống kê
                        switch (subIndex) {
                            case 1: //Thống kê tài chính
                                Main.showMainForm(new ThongKeTaiChinh(employee));
                                break;
                            case 2: //Thống kê khách hàng
//								Main.showMainForm(new QuanLyNuocUongGUI());
                                break;
                            default:
                                action.cancel();
                                break;
                        }
                        break;
//                    case 4: //Quản lý khách hàng
//
//                        break;
                    case 4: //AI trợ giúp
                        Main.showMainForm(new Ai(employee));
                        break;
                    case 5: //Tài khoản
                        Main.showMainForm(new TaiKhoanNhanVien(employee));
                        break;
                    case 6: //Trợ giúp
                        Main.showMainForm(new TroGiupNhanVien(employee));
                        break;
 //                   case 7: //Cài đặt
//                        Main.showMainForm(new TaiKhoanNhanVien());
 //                       break;
                    case 7: //Đăng xuất
                        Main.logout();
                        break;
                    default:
                        action.cancel();
                        break;
                }
            }
        });

    }

    private void setMenuFull(boolean full) {
        String icon;
        if (getComponentOrientation().isLeftToRight()) {
            icon = (full) ? "menu_left.svg" : "menu_right.svg";
        } else {
            icon = (full) ? "menu_right.svg" : "menu_left.svg";
        }
        menuButton.setIcon(new FlatSVGIcon("gui/menu/icon/" + icon, 0.8f));
        menu.setMenuFull(full);
        revalidate();
    }

    public void hideMenu() {
        menu.hideMenuItem();
    }

    public void showForm(Component component) {
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }

    public void setSelectedMenu(int index, int subIndex) {
        menu.setSelectedMenu(index, subIndex);
    }

    private class MainFormLayout implements LayoutManager {

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
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                int menuX = ltr ? x : x + width - menuWidth;
                menu.setBounds(menuX, y, menuWidth, height);
                int menuButtonWidth = menuButton.getPreferredSize().width;
                int menuButtonHeight = menuButton.getPreferredSize().height;
                int menubX;
                if (ltr) {
                    menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                } else {
                    menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
                }
                menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = ltr ? (x + menuWidth + gap) : x;
                int bodyy = y;
                panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
            }
        }
    }
}
