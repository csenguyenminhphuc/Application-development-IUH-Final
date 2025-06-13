/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.NhanVien;
import entity.SoVeBanDuoc;
import entity.TaiKhoan;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class TaiKhoanDAOImpl {
    public TaiKhoan getTaiKhoanTheoTenDangNhap(String tenDangNhap) {
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT top 1 * FROM TaiKhoan WHERE tenDangNhap = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNhanVien = rs.getString("maNV");
                    String tenDangNhap1 = rs.getString("tenDangNhap");
                    String matKhau = rs.getString("matKhau");
                    
                    NhanVienDAOImpl nvdaoi = new NhanVienDAOImpl();
                    NhanVien nhanVien = nvdaoi.getNhanVienByMaNV(maNhanVien);

                    return new TaiKhoan(tenDangNhap1, matKhau, nhanVien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) {
        TaiKhoanDAOImpl aOImpl = new TaiKhoanDAOImpl();
        TaiKhoan banDuocs = aOImpl.getTaiKhoanTheoTenDangNhap("0328546227");
        if (banDuocs != null) {
            System.out.println(banDuocs);
        } else {
            System.out.println("Không tìm thấy nhân viên với mã: N227-V-0-04-938");
        }
    }
}
