/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import connectdb.ConnectDB;
import entity.KhachHang;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class KhachHang_DAO {
    private ArrayList<KhachHang> listKhachHang;
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public KhachHang_DAO() {
        ConnectDB.getInstance().connect();
        listKhachHang = new ArrayList<>();
    }
    
    public String themKhachHang(KhachHang kh) {
        String newMaKH = null;
        String sql = "{ CALL InsertKhachHang(?, ?, ?) }";
        ConnectDB.getInstance().connect();
        try (Connection con = ConnectDB.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            // 1) Thiết lập tham số vào
            cs.setString(1, kh.getTenKH());
            cs.setString(2, kh.getSoDienThoai());
            cs.setString(3, kh.getCccd());

            // 2) Thực thi và lấy ResultSet từ SELECT cuối proc
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    // Lấy theo alias bạn dùng trong proc
                    newMaKH = rs.getString("NewKhachHangID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newMaKH;
    }
    public KhachHang timKhachHangTheoCCCD(String cccd){
        con = ConnectDB.getConnection();
        KhachHang kh =null;
        try {
            stmt = con.prepareStatement("SELECT * FROM KhachHang WHERE cccd = ? ");
            stmt.setString(1, cccd);
            
            rs = stmt.executeQuery();
            if(rs.next()){
                kh = new KhachHang();
                kh.setMaKH(rs.getString(1));
                kh.setTenKH(rs.getString(2));
                kh.setSoDienThoai(rs.getString(3));
                kh.setCccd(rs.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return kh;
    }
    public KhachHang timKhachHangTheoMa(String maKH){
        con = ConnectDB.getConnection();
        KhachHang kh =null;
        try {
            stmt = con.prepareStatement("SELECT * FROM KhachHang WHERE maKH = ? ");
            stmt.setString(1, maKH);
            
            rs = stmt.executeQuery();
            if(rs.next()){
                kh = new KhachHang();
                kh.setMaKH(rs.getString(1));
                kh.setTenKH(rs.getString(2));
                kh.setSoDienThoai(rs.getString(3));
                kh.setCccd(rs.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return kh;
    }
    // Đây là phần tôi thêm
    public ArrayList<KhachHang> timKhachHang(String tenKH, String soDienThoai, String cccd) {
        ArrayList<KhachHang> listKhachHang = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT maKH, tenKH, soDienThoai, cccd FROM KhachHang WHERE 1=1");
        ArrayList<String> params = new ArrayList<>();

        Connection con = ConnectDB.getConnection();
        if (con == null) {
            System.err.println("Lỗi kết nối: Không thể kết nối đến cơ sở dữ liệu.");
            return listKhachHang;
        }

        try {
            if (isNotEmpty(tenKH)) {
                sql.append(" AND LOWER(tenKH) LIKE LOWER(?)");
                params.add("%" + tenKH.trim() + "%");
            }
            if (isNotEmpty(soDienThoai)) {
                sql.append(" AND soDienThoai LIKE ?");
                params.add("%" + soDienThoai.trim() + "%");
            }
            if (isNotEmpty(cccd)) {
                sql.append(" AND cccd = ?");
                params.add(cccd.trim());
            }

            stmt = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getString("maKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setSoDienThoai(rs.getString("soDienThoai"));
                kh.setCccd(rs.getString("cccd"));
                listKhachHang.add(kh);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm khách hàng: " + e.getMessage());
        } finally {
            ConnectDB.getInstance().closeItem(stmt, rs);
        }

        return listKhachHang;
    }
    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    public ArrayList<KhachHang> layTatCaKhachHang() {
        ArrayList<KhachHang> listKhachHang = new ArrayList<>();
        String sql = "SELECT maKH, tenKH, soDienThoai, cccd FROM KhachHang";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Check if connection is valid
            if (con == null || con.isClosed()) {
                throw new SQLException("Database connection is null or closed");
            }

            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getString("maKH"));
                kh.setTenKH(rs.getString("tenKH"));
                kh.setSoDienThoai(rs.getString("soDienThoai"));
                kh.setCccd(rs.getString("cccd"));
                listKhachHang.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return listKhachHang; // Return empty list on error
        }
        return listKhachHang;
    }

}
