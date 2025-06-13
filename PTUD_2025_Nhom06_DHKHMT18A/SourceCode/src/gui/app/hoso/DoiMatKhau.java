//package gui.app.hoso;
//
//import java.awt.Color;
//import java.awt.Font;
//
//import javax.swing.BorderFactory;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JPasswordField;
//
////import dao.TaiKhoanDAO;
//import entity.NhanVien;
//import net.miginfocom.swing.MigLayout;
//
//public class DoiMatKhau extends JPanel {
//	private static final long serialVersionUID = 1L;
//
//	private JLabel currentPasswordLabel;
//	private JLabel newPasswordLabel;
//	private JLabel confirmNewPasswordLabel;
//	private JPasswordField currentPasswordTextField;
//	private JPasswordField newPasswordTextField;
//	private JPasswordField confirmNewPasswordTextField;
//	private JButton changePasswordButton;
//	private JLabel informationLabel;
//	private JLabel errorLabel;
////	private TaiKhoanDAO taiKhoanDAO;
//	private NhanVien nhanVien; // Khai báo biến nhanVien
//
//	public DoiMatKhau(NhanVien nhanVien) {
//		this.nhanVien = nhanVien; // Khởi tạo nhanVien
////		taiKhoanDAO = new TaiKhoanDAO();
//		initComponents(nhanVien);
//	}
//
//	private void initComponents(NhanVien nhanVien) {
//		setLayout(new MigLayout("wrap 2", "[right][grow, fill]", "[]20[]20[]20[]20[]"));
//		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//		// Tiêu đề và thông tin nhân viên
//		informationLabel = new JLabel(nhanVien.getTenNV() + " - " + nhanVien.getVaiTro());
//		informationLabel.setFont(new Font("Arial", Font.BOLD, 16));
//		add(informationLabel, "span 2, center");
//
//		// Nhãn và ô nhập mật khẩu hiện tại
//		currentPasswordLabel = new JLabel("Mật khẩu hiện tại:");
//		currentPasswordTextField = new JPasswordField(20);
//		add(currentPasswordLabel);
//		add(currentPasswordTextField);
//
//		// Nhãn và ô nhập mật khẩu mới
//		newPasswordLabel = new JLabel("Mật khẩu mới:");
//		newPasswordTextField = new JPasswordField(20);
//		add(newPasswordLabel);
//		add(newPasswordTextField);
//
//		// Nhãn và ô nhập xác nhận mật khẩu mới
//		confirmNewPasswordLabel = new JLabel("Xác nhận mật khẩu mới:");
//		confirmNewPasswordTextField = new JPasswordField(20);
//		add(confirmNewPasswordLabel);
//		add(confirmNewPasswordTextField);
//
//		// Nút đổi mật khẩu
//		changePasswordButton = new JButton("Đổi mật khẩu");
//		changePasswordButton.setFont(new Font("Arial", Font.BOLD, 14));
//		changePasswordButton.addActionListener(e -> handleChangePassword());
//		add(changePasswordButton, "span 2, center");
//
//		// Nhãn hiển thị lỗi
//		errorLabel = new JLabel("");
//		errorLabel.setForeground(Color.RED);
//		add(errorLabel, "span 2, center");
//	}
//
//	private void handleChangePassword() {
//		String currentPassword = new String(currentPasswordTextField.getPassword());
//		String newPassword = new String(newPasswordTextField.getPassword());
//		String confirmPassword = new String(confirmNewPasswordTextField.getPassword());
//
//		if (newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty()) {
//			errorLabel.setText("Vui lòng điền đầy đủ thông tin.");
//			return;
//		}
//
//		if (!newPassword.equals(confirmPassword)) {
//			errorLabel.setText("Mật khẩu mới và xác nhận không khớp.");
//			return;
//		}
//
//		boolean success = taiKhoanDAO.doiMatKhau(nhanVien.getTk().getTaiKhoan(), currentPassword, newPassword);
//		if (success) {
//			errorLabel.setText("Đổi mật khẩu thành công!");
//			errorLabel.setForeground(Color.GREEN);
//		} else {
//			errorLabel.setText("Mật khẩu hiện tại không đúng.");
//			errorLabel.setForeground(Color.RED);
//		}
//	}
//}
