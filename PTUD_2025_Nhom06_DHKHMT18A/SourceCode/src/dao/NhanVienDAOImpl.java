package dao;

import connectdb.ConnectDB;
import entity.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import org.mindrot.jbcrypt.BCrypt;

public class NhanVienDAOImpl {
    public NhanVien getNhanVienByMaNV(String maNhanVien) {
        NhanVien nhanVien = null;
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT [maNV], [tenNV], [gioiTinh], [ngaySinh], [email], " +
                    "[soDienThoai], [cccd], [ngayBatDauLamViec], [vaiTro], [trangThai] " +
                    "FROM NhanVien WHERE [maNV] = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maNV = rs.getString("maNV");
                    String tenNV = rs.getString("tenNV");
                    Boolean gioiTinh = rs.getInt("gioiTinh") == 1; // 1 = nữ (true), 0 = nam (false)
                    LocalDate ngaySinh = rs.getDate("ngaySinh").toLocalDate();
                    String email = rs.getString("email");
                    String soDienThoai = rs.getString("soDienThoai");
                    String cccd = rs.getString("cccd");
                    LocalDate ngayBatDauLamViec = rs.getDate("ngayBatDauLamViec").toLocalDate();
                    String vaiTro = rs.getString("vaiTro");
                    String trangThai = rs.getString("trangThai");

                    nhanVien = new NhanVien(tenNV, gioiTinh, ngaySinh, email, soDienThoai,
                                            cccd, ngayBatDauLamViec, vaiTro, trangThai);
                    nhanVien.setMaNV(maNV); // Set maNV riêng vì constructor không có
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nhanVien;
    }
    
    public NhanVien getNhanVienTheoTaiKhoan(String tenDangNhap, boolean authentication) {
        NhanVien nhanVien = null;
        TaiKhoanDAOImpl impl = new TaiKhoanDAOImpl();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        
        String sql = "SELECT nv.[maNV], nv.[tenNV], nv.[gioiTinh], nv.[ngaySinh], nv.[email], \n" +
                    "nv.[soDienThoai], nv.[cccd], nv.[ngayBatDauLamViec], nv.[vaiTro], nv.[trangThai]\n" +
                    "FROM NhanVien nv\n" +
                    "JOIN TaiKhoan tk ON nv.[maNV] = tk.[maNV]\n" +
                    "WHERE tk.[tenDangNhap] = ?";
        if (impl.getTaiKhoanTheoTenDangNhap(tenDangNhap) != null && authentication) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tenDangNhap);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String maNV = rs.getString("maNV");
                        String tenNV = rs.getString("tenNV");
                        Boolean gioiTinh = rs.getInt("gioiTinh") == 1; // 1 = nữ (true), 0 = nam (false)
                        LocalDate ngaySinh = rs.getDate("ngaySinh").toLocalDate();
                        String email = rs.getString("email");
                        String soDienThoai = rs.getString("soDienThoai");
                        String cccd = rs.getString("cccd");
                        LocalDate ngayBatDauLamViec = rs.getDate("ngayBatDauLamViec").toLocalDate();
                        String vaiTro = rs.getString("vaiTro");
                        String trangThai = rs.getString("trangThai");

                        nhanVien = new NhanVien(tenNV, gioiTinh, ngaySinh, email, soDienThoai,
                                                cccd, ngayBatDauLamViec, vaiTro, trangThai);
                        nhanVien.setMaNV(maNV); // Set maNV riêng vì constructor không có
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return nhanVien;
        }
        return null;
    }
    
    public boolean resetMatKhau(String soDienThoai, String cccd, String email) {
        ConnectDB.getInstance().connect();
        String queryCheck = "SELECT COUNT(*) FROM NhanVien nv JOIN TaiKhoan tk ON nv.maNV = tk.maNV WHERE nv.soDienThoai = ? AND nv.cccd = ? AND nv.email = ? AND tk.tenDangNhap = ?";
        String queryUpdate = "UPDATE TaiKhoan SET matKhau = ? WHERE tenDangNhap = ?";

        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) {
                System.out.println("Kết nối thất bại!");
                return false;
            }
            PreparedStatement stmtCheck = conn.prepareStatement(queryCheck);
            stmtCheck.setString(1, soDienThoai);
            stmtCheck.setString(2, cccd);
            stmtCheck.setString(3, email);
            stmtCheck.setString(4, soDienThoai); // tenDangNhap = soDienThoai
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Số bản ghi khớp: " + count);
                if (count > 0) {
                    String hashedPassword = BCrypt.hashpw("1111", BCrypt.gensalt());
                    PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate);
                    stmtUpdate.setString(1, hashedPassword);
                    stmtUpdate.setString(2, soDienThoai); // tenDangNhap = soDienThoai
                    int rowsUpdated = stmtUpdate.executeUpdate();
                    System.out.println("Số hàng cập nhật: " + rowsUpdated);
                    return rowsUpdated > 0;
                } else {
                    System.out.println("Không tìm thấy tài khoản với thông tin: soDienThoai=" + soDienThoai + ", cccd=" + cccd + ", email=" + email);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
        return false;
    } 
    
    
    public String layMatKhau(String soDienThoai, String cccd, String email) {
        ConnectDB.getInstance().connect();
        String matKhau = null;
        String query = "SELECT tk.matKhau FROM NhanVien nv JOIN TaiKhoan tk ON nv.maNV = tk.maNV WHERE nv.soDienThoai = ? AND nv.cccd = ? AND nv.email = ? AND tk.tenDangNhap = ?";

        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) {
                System.out.println("Kết nối thất bại!");
                return null;
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, soDienThoai);
            stmt.setString(2, cccd);
            stmt.setString(3, email);
            stmt.setString(4, soDienThoai); // tenDangNhap = soDienThoai
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                matKhau = rs.getString("matKhau");
            } else {
                System.out.println("Không tìm thấy mật khẩu với thông tin: soDienThoai=" + soDienThoai + ", cccd=" + cccd + ", email=" + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
        return matKhau;
    }
    
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        ConnectDB.getInstance().connect();
        String query = "UPDATE TaiKhoan SET matKhau = ? WHERE tenDangNhap = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) return false;
            String hashedPassword = BCrypt.hashpw(matKhauMoi, BCrypt.gensalt());
            stmt.setString(1, hashedPassword);
            stmt.setString(2, tenDangNhap);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Đây là phần tôi thêm
    public NhanVien timNhanVien(String tenNV, String sdt, String cccd) {
        String sql = "SELECT TOP 1 * FROM NhanVien WHERE tenNV = ? AND soDienThoai = ? AND cccd = ?";
        NhanVien nhanVien = null;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu.");
            }

            // Thiết lập các tham số cho truy vấn
            pstmt.setString(1, tenNV.trim());
            pstmt.setString(2, sdt.trim());
            pstmt.setString(3, cccd.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nhanVien = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNV"),
                        rs.getBoolean("gioiTinh"),
                        rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                        rs.getString("email"),
                        rs.getString("soDienThoai"),
                        rs.getString("cccd"),
                        rs.getDate("ngayBatDauLamViec") != null ? rs.getDate("ngayBatDauLamViec").toLocalDate() : null,
                        rs.getString("vaiTro"),
                        rs.getString("trangThai")
                    );
                } else {
                    System.out.println("Không tìm thấy nhân viên với thông tin đã cung cấp.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn nhân viên: " + e.getMessage());
            throw new RuntimeException("Không thể tìm nhân viên: " + e.getMessage(), e);
        }

        return nhanVien;
    }

    
    
    public static void main(String[] args) {
        NhanVienDAOImpl aOImpl = new NhanVienDAOImpl();
        NhanVien banDuocs = aOImpl.getNhanVienTheoTaiKhoan("0328546227", true);
        if (banDuocs != null) {
            System.out.println(banDuocs);
        } else {
            System.out.println("Không tìm thấy nhân viên với mã: N227-V-0-04-938");
        }
    }
}