/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class QuanLyNhanVienDao {
    private static int soThuTuNhanVien = 1;
    
    public String generateMaNhanVien(NhanVien nv) {
        String maNV = "NV-";
        // Thêm giới tính (0: nam, 1: nữ)
        maNV += nv.getGioiTinh() ? "1-" : "0-";
        
        // Thêm năm sinh (YY)
        String namSinh = String.valueOf(nv.getNgaySinh().getYear());
        maNV += namSinh.substring(2) + "-";
        
        // Thêm 3 số cuối CCCD
        String cccd = nv.getCccd();
        maNV += cccd.substring(cccd.length() - 3) + "-";
        
        // Thêm số thứ tự
        maNV += String.format("%03d", soThuTuNhanVien++);
        
        return maNV;
    }
    
    public boolean themNhanVien(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNV, tenNV, gioiTinh, ngaySinh, email, soDienThoai, cccd, ngayBatDauLamViec, vaiTro, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement stmt = null;
        
        try {
            con = ConnectDB.getInstance().getConnection();
            stmt = con.prepareStatement(sql);
            
            String maNV = generateMaNhanVien(nv);
            nv.setMaNV(maNV);
            
            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getTenNV());
            stmt.setBoolean(3, nv.getGioiTinh());
            stmt.setDate(4, java.sql.Date.valueOf(nv.getNgaySinh()));
            stmt.setString(5, nv.getEmail());
            stmt.setString(6, nv.getSoDienThoai());
            stmt.setString(7, nv.getCccd());
            stmt.setDate(8, java.sql.Date.valueOf(nv.getNgayBatDauLamViec()));
            stmt.setString(9, nv.getVaiTro());
            stmt.setString(10, nv.getTrangThai());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean capNhatNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET tenNV=?, gioiTinh=?, ngaySinh=?, email=?, soDienThoai=?, cccd=?, ngayBatDauLamViec=?, vaiTro=?, trangThai=? WHERE maNV=?";
        Connection con = null;
        PreparedStatement stmt = null;
        
        try {
            con = ConnectDB.getInstance().getConnection();
            stmt = con.prepareStatement(sql);
            
            stmt.setString(1, nv.getTenNV());
            stmt.setBoolean(2, nv.getGioiTinh());
            stmt.setDate(3, java.sql.Date.valueOf(nv.getNgaySinh()));
            stmt.setString(4, nv.getEmail());
            stmt.setString(5, nv.getSoDienThoai());
            stmt.setString(6, nv.getCccd());
            stmt.setDate(7, java.sql.Date.valueOf(nv.getNgayBatDauLamViec()));
            stmt.setString(8, nv.getVaiTro());
            stmt.setString(9, nv.getTrangThai());
            stmt.setString(10, nv.getMaNV());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean xoaNhanVien(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV=?";
        Connection con = null;
        PreparedStatement stmt = null;
        
        try {
            con = ConnectDB.getInstance().getConnection();
            stmt = con.prepareStatement(sql);
            
            stmt.setString(1, maNV);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> dsNhanVien = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY maNV";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = ConnectDB.getInstance().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setGioiTinh(rs.getBoolean("gioiTinh"));
                nv.setNgaySinh(rs.getDate("ngaySinh").toLocalDate());
                nv.setEmail(rs.getString("email"));
                nv.setSoDienThoai(rs.getString("soDienThoai"));
                nv.setCccd(rs.getString("cccd"));
                nv.setNgayBatDauLamViec(rs.getDate("ngayBatDauLamViec").toLocalDate());
                nv.setVaiTro(rs.getString("vaiTro"));
                nv.setTrangThai(rs.getString("trangThai"));
                
                dsNhanVien.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dsNhanVien;
    }
    
    public NhanVien getNhanVienByMa(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE maNV=?";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = ConnectDB.getInstance().getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maNV);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setGioiTinh(rs.getBoolean("gioiTinh"));
                nv.setNgaySinh(rs.getDate("ngaySinh").toLocalDate());
                nv.setEmail(rs.getString("email"));
                nv.setSoDienThoai(rs.getString("soDienThoai"));
                nv.setCccd(rs.getString("cccd"));
                nv.setNgayBatDauLamViec(rs.getDate("ngayBatDauLamViec").toLocalDate());
                nv.setVaiTro(rs.getString("vaiTro"));
                nv.setTrangThai(rs.getString("trangThai"));
                return nv;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Lấy số thứ tự mới cho nhân viên
     * Tìm số thứ tự lớn nhất trong các mã nhân viên hiện có và cộng thêm 1
     * 
     * @return Số thứ tự mới cho nhân viên tiếp theo
     */
    public int laySoThuTuNhanVienMoi() {
        String sql = "SELECT MAX(CAST(RIGHT(maNV, 3) AS INT)) AS maxSTT FROM NhanVien";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = ConnectDB.getInstance().getConnection();
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int maxSTT = rs.getInt("maxSTT");
                return maxSTT + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 1; // Nếu chưa có nhân viên nào, bắt đầu từ 1
    }
    
    public List<NhanVien> timKiemNhanVien(String keyword) {
        List<NhanVien> dsNhanVien = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE maNV LIKE ? OR tenNV LIKE ? OR email LIKE ? OR soDienThoai LIKE ? OR cccd LIKE ?";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = ConnectDB.getInstance().getConnection();
            stmt = con.prepareStatement(sql);
            
            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, searchPattern);
            }
            
            rs = stmt.executeQuery();
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setGioiTinh(rs.getBoolean("gioiTinh"));
                nv.setNgaySinh(rs.getDate("ngaySinh").toLocalDate());
                nv.setEmail(rs.getString("email"));
                nv.setSoDienThoai(rs.getString("soDienThoai"));
                nv.setCccd(rs.getString("cccd"));
                nv.setNgayBatDauLamViec(rs.getDate("ngayBatDauLamViec").toLocalDate());
                nv.setVaiTro(rs.getString("vaiTro"));
                nv.setTrangThai(rs.getString("trangThai"));
                
                dsNhanVien.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dsNhanVien;
    }
}
