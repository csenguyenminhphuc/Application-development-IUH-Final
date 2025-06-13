/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.Ga;
import java.sql.*;
import java.util.ArrayList;

/**
 * Lớp Data Access Object cho việc quản lý ga tàu
 * @author Administrator
 */
public class QuanLyGaDao {
    private ArrayList<Ga> dsGa; // Danh sách ga
    private Connection con;
    private PreparedStatement stmt;
    private ResultSet rs;
    
    /**
     * Constructor mặc định
     * Khởi tạo kết nối database và danh sách ga
     */
    public QuanLyGaDao() {
        ConnectDB.getInstance().connect();
        dsGa = new ArrayList<>();
    }
    
    /**
     * Constructor với danh sách ga có sẵn
     * @param dsGa Danh sách ga ban đầu
     */
    public QuanLyGaDao(ArrayList<Ga> dsGa) {
        ConnectDB.getInstance().connect();
        this.dsGa = dsGa;
    }
    
    /**
     * Lấy tất cả các ga từ database
     * @return Danh sách tất cả các ga
     */
    public ArrayList<Ga> layTatCaGa() {
        dsGa = new ArrayList<>();
        try {
            con = ConnectDB.getConnection();
            String sql = "SELECT * FROM Ga";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                Ga ga = new Ga();
                ga.setMaGa(rs.getString("maGa"));
                ga.setTenGa(rs.getString("tenGa"));
                ga.setDiaChi(rs.getString("diaChi"));
                ga.setSoDienThoai(rs.getString("soDienThoai"));
                dsGa.add(ga);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return dsGa;
    }
    
    /**
     * Thêm một ga mới vào database
     * @param ga Đối tượng ga cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean themGa(Ga ga) {
        try {
            con = ConnectDB.getConnection();
            String sql = "INSERT INTO Ga (maGa, tenGa, diaChi, soDienThoai) VALUES (?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            
            stmt.setString(1, ga.getMaGa());
            stmt.setString(2, ga.getTenGa());
            stmt.setString(3, ga.getDiaChi());
            stmt.setString(4, ga.getSoDienThoai());
            
            return stmt.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Cập nhật thông tin ga
     * @param ga Đối tượng ga cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean capNhatGa(Ga ga) {
        try {
            con = ConnectDB.getConnection();
            String sql = "UPDATE Ga SET tenGa=?, diaChi=?, soDienThoai=? WHERE maGa=?";
            stmt = con.prepareStatement(sql);
            
            stmt.setString(1, ga.getTenGa());
            stmt.setString(2, ga.getDiaChi());
            stmt.setString(3, ga.getSoDienThoai());
            stmt.setString(4, ga.getMaGa());
            
            return stmt.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Xóa ga khỏi database
     * @param maGa Mã ga cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean xoaGa(String maGa) {
        try {
            con = ConnectDB.getConnection();
            String sql = "DELETE FROM Ga WHERE maGa=?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maGa);
            
            return stmt.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Tìm ga theo mã
     * @param maGa Mã ga cần tìm
     * @return Đối tượng ga nếu tìm thấy, null nếu không tìm thấy
     */
    public Ga timGaTheoMa(String maGa) {
        try {
            con = ConnectDB.getConnection();
            String sql = "SELECT * FROM Ga WHERE maGa=?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maGa);
            rs = stmt.executeQuery();
            
            if(rs.next()) {
                Ga ga = new Ga();
                ga.setMaGa(rs.getString("maGa"));
                ga.setTenGa(rs.getString("tenGa"));
                ga.setDiaChi(rs.getString("diaChi"));
                ga.setSoDienThoai(rs.getString("soDienThoai"));
                return ga;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }
    
    /**
     * Tìm ga theo tên (tìm kiếm mờ)
     * @param tenGa Tên ga cần tìm
     * @return Danh sách các ga có tên chứa chuỗi tìm kiếm
     */
    public ArrayList<Ga> timGaTheoTen(String tenGa) {
        ArrayList<Ga> ketQua = new ArrayList<>();
        try {
            con = ConnectDB.getConnection();
            String sql = "SELECT * FROM Ga WHERE tenGa LIKE ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, "%" + tenGa + "%");
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                Ga ga = new Ga();
                ga.setMaGa(rs.getString("maGa"));
                ga.setTenGa(rs.getString("tenGa"));
                ga.setDiaChi(rs.getString("diaChi"));
                ga.setSoDienThoai(rs.getString("soDienThoai"));
                ketQua.add(ga);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return ketQua;
    }
    
    /**
     * Tạo mã ga mới tự động
     * @return Mã ga mới theo định dạng GA-XXX
     */
    public String taoMaGaMoi() {
        try {
            con = ConnectDB.getConnection();
            String sql = "SELECT TOP 1 maGa FROM Ga ORDER BY maGa DESC";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if(rs.next()) {
                String maGaCuoi = rs.getString(1);
                int soThuTu = Integer.parseInt(maGaCuoi.substring(3)) + 1;
                return String.format("GA-%03d", soThuTu);
            } else {
                return "GA-001";
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Đóng các kết nối database
     */
    private void closeConnection() {
        try {
            if(rs != null) rs.close();
            if(stmt != null) stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
