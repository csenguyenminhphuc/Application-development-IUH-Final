//package gui.app.hoso;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.text.SimpleDateFormat;
//import java.util.regex.Pattern;
//
//import javax.swing.ButtonGroup;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JRadioButton;
//import javax.swing.JTextField;
//
//import com.toedter.calendar.JDateChooser;
//
//import dao.NhanVienDAO;
//import entity.NhanVien;
//import net.miginfocom.swing.MigLayout;
//
//public class HoSoForm extends JPanel {
//	private JTextField txtFullName, txtEmail, txtPhone;
//	private JButton btnUpdate;
//	private JRadioButton rbtnMale, rbtnFemale;
//	private JDateChooser birthDateChooser, startDateChooser;
//
//	public HoSoForm(NhanVien nhanVien) {
//		setupUI(nhanVien);
//	}
//
//	private void setupUI(NhanVien nhanVien) {
//		setLayout(new MigLayout("wrap 2", "[right][grow, fill]", "[][]20[]20[]20[]20[]"));
//		setBackground(Color.WHITE);
//		setBounds(0, 0, 880, 570);
//		setupFormPanel(nhanVien);
//	}
//
//	private void setupFormPanel(NhanVien nhanVien) {
//		JPanel formPanel = new JPanel();
//		formPanel.setLayout(new MigLayout("wrap 2", "[right][grow, fill]", "[][]20[]20[]20[]20[]"));
//		formPanel.setBackground(Color.WHITE);
//
//		// Thêm các thành phần vào formPanel
//		addPersonalInfoComponents(formPanel, nhanVien);
//		addEmployeeInfoComponents(formPanel, nhanVien);
//
//		// Thêm nút cập nhật vào cuối
//		btnUpdate = new JButton("Cập nhật");
//		btnUpdate.setFont(new Font("Arial", Font.BOLD, 16));
//		btnUpdate.setBackground(new Color(70, 130, 180));
//		btnUpdate.setForeground(Color.WHITE);
//		btnUpdate.addActionListener(event -> updateEmployee(nhanVien));
//		formPanel.add(btnUpdate, "span 2, center");
//
//		add(formPanel);
//	}
//
//	private void addPersonalInfoComponents(JPanel panel, NhanVien nhanVien) {
//		panel.add(createLabel("Họ và tên:"));
//		panel.add(txtFullName = createTextField(nhanVien.getHoTen()));
//
//		panel.add(createLabel("Ngày sinh:"));
//		panel.add(birthDateChooser = createDatePicker(nhanVien.getNgaySinh()));
//
//		panel.add(createLabel("Ngày bắt đầu làm:"));
//		panel.add(startDateChooser = createDatePicker(nhanVien.getNgayBatDauLam()));
//
//		// Thêm radio buttons và căn ngang
//		JPanel genderPanel = new JPanel();
//		genderPanel.setLayout(new MigLayout("wrap 2", "[right][left]"));
//		rbtnMale = new JRadioButton("Nam");
//		rbtnFemale = new JRadioButton("Nữ");
//		ButtonGroup genderGroup = new ButtonGroup();
//		genderGroup.add(rbtnMale);
//		genderGroup.add(rbtnFemale);
//		genderPanel.add(rbtnMale);
//		genderPanel.add(rbtnFemale);
//		panel.add(createLabel("Giới tính:"));
//		panel.add(genderPanel);
//
//		if (nhanVien.isGioiTinh()) {
//			rbtnMale.setSelected(true);
//		} else {
//			rbtnFemale.setSelected(true);
//		}
//	}
//
//	private void addEmployeeInfoComponents(JPanel panel, NhanVien nhanVien) {
//		panel.add(createLabel("Email:"));
//		panel.add(txtEmail = createTextField(nhanVien.getEmail()));
//
//		panel.add(createLabel("SĐT:"));
//		panel.add(txtPhone = createTextField(nhanVien.getSoDienThoai()));
//
//		// Vai trò không thay đổi, chỉ hiển thị
//		JLabel lblEmployeeRole = new JLabel(nhanVien.getVaiTro());
//		lblEmployeeRole.setFont(new Font("Arial", Font.PLAIN, 14));
//		panel.add(createLabel("Vai trò:"));
//		panel.add(lblEmployeeRole);
//	}
//
//	private JTextField createTextField(String text) {
//		JTextField textField = new JTextField(text);
//		textField.setFont(new Font("Arial", Font.PLAIN, 14));
//		textField.setPreferredSize(new java.awt.Dimension(250, 30)); // Thêm kích thước cho ô text field
//		return textField;
//	}
//
//	private JLabel createLabel(String text) {
//		JLabel label = new JLabel(text);
//		label.setFont(new Font("Arial", Font.PLAIN, 14));
//		label.setPreferredSize(new java.awt.Dimension(150, 30)); // Căn chỉnh label
//		return label;
//	}
//
//	private JDateChooser createDatePicker(java.time.LocalDate date) {
//		JDateChooser dateChooser = new JDateChooser();
//		dateChooser.setDateFormatString("dd/MM/yyyy");
//		if (date != null) {
//			dateChooser.setDate(java.sql.Date.valueOf(date));
//		}
//		dateChooser.setPreferredSize(new java.awt.Dimension(250, 30)); // Đặt kích thước cho date picker
//		return dateChooser;
//	}
//
//	private void updateEmployee(NhanVien nhanVien) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//		String fullName = txtFullName.getText();
//		String email = txtEmail.getText();
//		String phone = txtPhone.getText();
//		String birthDateStr = getDateString(birthDateChooser, dateFormat);
//		String startDateStr = getDateString(startDateChooser, dateFormat);
//		boolean gioiTinh = rbtnMale.isSelected();
//
//		if (!validateInputs(fullName, email, phone, birthDateStr, startDateStr))
//			return;
//
//		nhanVien.setHoTen(fullName);
//		nhanVien.setGioiTinh(gioiTinh);
//		nhanVien.setEmail(email);
//		nhanVien.setSoDienThoai(phone);
//		nhanVien.setNgaySinh(java.time.LocalDate.parse(birthDateStr));
//		nhanVien.setNgayBatDauLam(java.time.LocalDate.parse(startDateStr));
//
//		NhanVienDAO dao = new NhanVienDAO();
//		if (dao.updateNhanVien(nhanVien)) {
//			javax.swing.JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
//		} else {
//			javax.swing.JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!");
//		}
//	}
//
//	private String getDateString(JDateChooser dateChooser, SimpleDateFormat dateFormat) {
//		return dateChooser.getDate() != null ? dateFormat.format(dateChooser.getDate()) : "";
//	}
//
//	private boolean validateInputs(String fullName, String email, String phone, String birthDateStr,
//			String startDateStr) {
//		if (!Pattern.matches("^[\\p{L} ]+$", fullName)) {
//			javax.swing.JOptionPane.showMessageDialog(this, "Tên không hợp lệ!");
//			return false;
//		}
//
//		if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
//			javax.swing.JOptionPane.showMessageDialog(this, "Email không hợp lệ!");
//			return false;
//		}
//
//		if (!Pattern.matches("^\\d{10}$", phone)) {
//			javax.swing.JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!");
//			return false;
//		}
//
//		if (birthDateStr.isEmpty() || startDateStr.isEmpty()) {
//			javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ ngày tháng!");
//			return false;
//		}
//
//		return true;
//	}
//}
