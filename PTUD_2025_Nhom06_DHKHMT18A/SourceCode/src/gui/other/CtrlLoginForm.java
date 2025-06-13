package gui.other;

import dao.NhanVienDAOImpl;
import dao.TaiKhoanDAOImpl;
import dao.ThongKeDAOImpl;
import entity.CaHienTai;
import org.mindrot.jbcrypt.BCrypt;

//import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;

public class CtrlLoginForm {

    private TaiKhoanDAOImpl tkDAO;
    private NhanVienDAOImpl nvdaoi;
    private static String currentTenDangNhap;
    private ThongKeDAOImpl aOImpl;
    private CaHienTai caHienTai;
    private String errorMessage;

    public CtrlLoginForm() {
        tkDAO = new TaiKhoanDAOImpl();
        nvdaoi = new NhanVienDAOImpl();
        aOImpl = new ThongKeDAOImpl();
        caHienTai = aOImpl.getThongTinCaHienTai();
        errorMessage = "";
    }

    public boolean checkCredentials(String username, String password) {
        // Reset thông điệp lỗi
        errorMessage = "";

        // Kiểm tra thông tin đăng nhập (username và password)
        TaiKhoan tk = tkDAO.getTaiKhoanTheoTenDangNhap(username);
        if (tk == null || !BCrypt.checkpw(password, tk.getMatkhau())) {
            errorMessage = "Sai tài khoản hoặc mật khẩu";
            return false;
        }

        // Lấy thông tin nhân viên dựa trên tài khoản
        NhanVien nhanVien = nvdaoi.getNhanVienTheoTaiKhoan(username, true);
        if (nhanVien == null) {
            errorMessage = "Không tìm thấy thông tin nhân viên";
            return false;
        }
        System.out.println(nhanVien);
        // Kiểm tra vai trò của nhân viên
        if (nhanVien.getVaiTro().equals("Nhân viên quản lý")) {
            // Nếu là quản lý, cho phép đăng nhập ngay mà không cần kiểm tra ca
            this.currentTenDangNhap = username;
            return true;
        }

        // Nếu là nhân viên bán vé, kiểm tra ca hiện tại
        if (caHienTai == null) {
            errorMessage = "Hiện tại không có ca làm việc nào đang diễn ra";
            return false;
        }

        // So sánh maNV của nhân viên với maNV trong ca hiện tại
        if (!nhanVien.getMaNV().equals(caHienTai.getMaNhanVien())) {
            errorMessage = "Nhân viên không được phân công vào ca hiện tại";
            return false;
        }

        // Nếu tất cả điều kiện đều thỏa mãn, lưu tên đăng nhập và cho phép đăng nhập
        this.currentTenDangNhap = username;
        return true;
    }

    // Getter để lấy thông điệp lỗi
    public String getErrorMessage() {
        return errorMessage;
    }

    public static String getTenDangNhap() {
        return currentTenDangNhap;
    }

    public NhanVien getEmployeeByAccount(String username, String password) {
        boolean isValid = checkCredentials(username, password);
        return nvdaoi.getNhanVienTheoTaiKhoan(username, isValid);
    }
}