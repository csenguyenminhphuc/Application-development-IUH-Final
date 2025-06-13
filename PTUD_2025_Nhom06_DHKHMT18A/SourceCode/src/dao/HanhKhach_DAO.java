/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.HanhKhach;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author PHAMGIAKHANH
 */
public class HanhKhach_DAO {
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public HanhKhach_DAO() {
        ConnectDB.getInstance().connect();
    }
    
    public String themMotHanhKhach(HanhKhach hk) {
        String mahk = null;
        // 1) Mở lại connection (nếu còn đóng)
        ConnectDB.getInstance().connect();
        String sql = "{ CALL InsertHanhKhach(?, ?, ?) }";

        try (Connection con = ConnectDB.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            // 2) Thiết lập tham số theo đúng thứ tự proc khai báo
            cs.setString(1, hk.getTenHanhKhach());
            cs.setString(2, hk.getCccd());
            cs.setInt(   3, hk.getNamSinh());

            // 3) Thực thi và đọc ResultSet từ SELECT cuối proc
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    // Dùng alias trong proc: NewHanhKhachID
                    mahk = rs.getString("NewHanhKhachID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mahk;
    }
    public HanhKhach timHanhKhachTheoCCCD(String cccd){
        ConnectDB.getInstance().connect();
        con = ConnectDB.getConnection();
        HanhKhach hk =null;
        try {
            stmt = con.prepareStatement("SELECT * FROM HanhKhach WHERE cccd = ? ");
            stmt.setString(1, cccd);
            
            rs = stmt.executeQuery();
            if(rs.next()){
                hk = new HanhKhach();
                hk.setMaHanhKhach(rs.getString(1));
                hk.setTenHanhKhach(rs.getString(2));
                hk.setCccd(rs.getString(3));
                hk.setNamSinh(rs.getInt(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return hk;
    }
    public HanhKhach timHanhKhachTheoMa(String maHK){
        ConnectDB.getInstance().connect();
        con = ConnectDB.getConnection();
        HanhKhach hk =null;
        try {
            stmt = con.prepareStatement("SELECT * FROM HanhKhach WHERE maHanhKhach = ? ");
            stmt.setString(1, maHK);
            
            rs = stmt.executeQuery();
            if(rs.next()){
                hk = new HanhKhach();
                hk.setMaHanhKhach(rs.getString(1));
                hk.setTenHanhKhach(rs.getString(2));
                hk.setCccd(rs.getString(3));
                hk.setNamSinh(rs.getInt(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return hk;
    }
    
}
