package gui.app;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import entity.NhanVien;
import gui.app.form.DangNhap;
import gui.app.form.MainForm;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    private static Main app;
    private MainForm mainForm;
    private static DangNhap loginForm; 

    public Main() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        app = this;
    }

    public static Main getInstance() {
        return app;
    }

    public MainForm getMainForm() {
        return mainForm;
    }

    public static DangNhap getLoginForm() {
        return loginForm;
    }

    public void createMainForm(NhanVien employee) {
        mainForm = new MainForm(employee);
        setContentPane(mainForm);
        revalidate();
        repaint();
    }

    public static void showMainForm(Component component) {
        component.applyComponentOrientation(app.getComponentOrientation());
        app.mainForm.showForm(component);
    }

    public static void setSelectedMenu(int index, int subIndex) {
        app.mainForm.setSelectedMenu(index, subIndex);
    }

    public static void logout() {
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            loginForm.resetLogin();
            loginForm.setVisible(true);
            loginForm.setLocationRelativeTo(null); 
            app.setVisible(false);
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(false);
        setResizable(true);

        setSize(1020, 680); // Kích thước khớp với DangNhap
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("gui.theme");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 16));
        FlatMacLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            // Tạo splash screen
            showSplashScreen(() -> {
                // Hiển thị DangNhap sau khi splash screen kết thúc
                loginForm = new DangNhap();
                loginForm.setLocationRelativeTo(null);
                loginForm.setVisible(true);
            });
        });
    }

    private static void showSplashScreen(Runnable onSplashFinished) {
        JWindow splashScreen = new JWindow();
        splashScreen.getContentPane().setBackground(new Color(191, 185, 165));

        JLabel imageLabel = new JLabel(new ImageIcon("src/gui/menu/icon/train_loading.jpeg"), SwingConstants.CENTER);
        splashScreen.getContentPane().add(imageLabel, BorderLayout.CENTER);

        ImageIcon originalIcon = (ImageIcon) imageLabel.getIcon();
        Image originalImage = originalIcon.getImage();
        int newWidth = 400;
        int newHeight = 400;
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        imageLabel.setIcon(scaledIcon);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        splashScreen.getContentPane().add(progressBar, BorderLayout.SOUTH);

        splashScreen.setBounds(450, 150, 400, 400);
        splashScreen.setLocationRelativeTo(null);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(2000); // Hiển thị splash 3 giây
                return null;
            }

            @Override
            protected void done() {
                splashScreen.setVisible(false);
                splashScreen.dispose();
                SwingUtilities.invokeLater(onSplashFinished);
            }
        };

        new Thread(() -> {
            int value = 0;
            while (value <= 100) {
                progressBar.setValue(value);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                value++;
            }
        }).start();

        splashScreen.setVisible(true);
        worker.execute();
    }
}