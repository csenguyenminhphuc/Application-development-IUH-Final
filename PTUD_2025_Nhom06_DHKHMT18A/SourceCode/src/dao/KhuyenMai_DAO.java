/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.KhuyenMai;
import entity.KhuyenMaiResult;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 *
 * @author PHAMGIAKHANH
 */
public class KhuyenMai_DAO {

    private ArrayList<KhuyenMai> listKhuyenMai;

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    public KhuyenMai_DAO() {
        ConnectDB.getInstance().connect();
        listKhuyenMai = new ArrayList<>();
    }

    public KhuyenMaiResult timKhuyenMaiTotNhat(double tongTienHoaDon) {
        con = ConnectDB.getConnection();
        KhuyenMaiResult kmr = null;
        KhuyenMai km = null;
        double tienGiam = 0.0;
        try {
            String string_sql = "DECLARE @tongTienHoaDon MONEY; "
                    + "SET @tongTienHoaDon = ?; "
                    + "SELECT TOP 1 km.maKhuyenMai, km.tenKhuyenMai, km.heSoKhuyenMai, km.tienKhuyenMaiToiDa, km.tongTienToiThieu,"
                    + "CASE "
                    + "   WHEN (km.heSoKhuyenMai * @tongTienHoaDon) > km.tienKhuyenMaiToiDa THEN km.tienKhuyenMaiToiDa "
                    + "   ELSE (km.heSoKhuyenMai * @tongTienHoaDon) "
                    + "END AS tienGiam "
                    + "FROM KhuyenMai km "
                    + "WHERE km.trangThai = N'Còn hiệu lực' "
                    + "AND @tongTienHoaDon >= km.tongTienToiThieu "
                    + "ORDER BY tienGiam DESC";
            stmt = con.prepareStatement(string_sql);
            stmt.setDouble(1, tongTienHoaDon);
            rs = stmt.executeQuery();
            if (rs.next()) {
                km = new KhuyenMai();
                km.setMaKhuyenMai(rs.getString("maKhuyenMai"));
                km.setTenKhuyenMai(rs.getString("tenKhuyenMai"));
                km.setHeSoKhuyenMai(rs.getDouble("heSoKhuyenMai"));
                km.setTienKhuyenMaiToiDa(rs.getDouble("tienKhuyenMaiToiDa"));
                tienGiam = rs.getDouble("tienGiam");
                kmr = new KhuyenMaiResult(km, tienGiam);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kmr;
    }

    public KhuyenMai timKhuyenMaiTheoMa(String maKM) {
        con = ConnectDB.getConnection();
        KhuyenMai km = null;
        try {
            String string_sql = "SELECT * FROM KhuyenMai WHERE maKhuyenMai =?";
            stmt = con.prepareStatement(string_sql);
            stmt.setString(1, maKM);
            rs = stmt.executeQuery();
            if (rs.next()) {
                km = new KhuyenMai();
                km.setMaKhuyenMai(rs.getString("maKhuyenMai"));
                km.setTenKhuyenMai(rs.getString("tenKhuyenMai"));
                km.setHeSoKhuyenMai(rs.getDouble("heSoKhuyenMai"));
                km.setTienKhuyenMaiToiDa(rs.getDouble("tienKhuyenMaiToiDa"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return km;
    }

    // Đây là phần tôi thêm
    public ArrayList<KhuyenMai> timKhuyenMai(Double tongTienToiThieu, Double tienKhuyenMaiToiDa, String trangThai) {
        listKhuyenMai.clear();
        con = ConnectDB.getConnection();
        stmt = null;
        rs = null;

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM KhuyenMai WHERE 1=1");
            ArrayList<Object> params = new ArrayList<>();

            if (tongTienToiThieu != null) {
                sql.append(" AND tongTienToiThieu <= ?");
                params.add(tongTienToiThieu);
            }

            if (tienKhuyenMaiToiDa != null) {
                sql.append(" AND tienKhuyenMaiToiDa <= ?");
                params.add(tienKhuyenMaiToiDa);
            }

            if (trangThai != null && !trangThai.equalsIgnoreCase("Tất cả")) {
                sql.append(" AND trangThai = ?");
                if (trangThai.equalsIgnoreCase("Còn hiệu lực")) {
                    params.add("Còn hiệu lực");
                } else if (trangThai.equalsIgnoreCase("Đã hết hạn")) {
                    params.add("Đã hết hạn");
                } else {
                    params.add(trangThai);
                }
            }

            stmt = con.prepareStatement(sql.toString());

            // Gán tham số
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                KhuyenMai km = new KhuyenMai();
                km.setMaKhuyenMai(rs.getString("maKhuyenMai"));
                km.setTenKhuyenMai(rs.getString("tenKhuyenMai"));
                km.setHeSoKhuyenMai(rs.getDouble("heSoKhuyenMai"));

                Timestamp ngayBatDauTimestamp = rs.getTimestamp("ngayBatDau");
                if (ngayBatDauTimestamp != null) {
                    km.setNgayBatDau(ngayBatDauTimestamp.toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime());
                }

                Timestamp ngayKetThucTimestamp = rs.getTimestamp("ngayKetThuc");
                if (ngayKetThucTimestamp != null) {
                    km.setNgayketThuc(ngayKetThucTimestamp.toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime());
                }

                km.setTongTienToiThieu(rs.getDouble("tongTienToiThieu"));
                km.setTienKhuyenMaiToiDa(rs.getDouble("tienKhuyenMaiToiDa"));
                km.setTrangThai(rs.getString("trangThai"));

                listKhuyenMai.add(km);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage());
            throw new RuntimeException("Không thể tra cứu khuyến mãi", e);
        } finally {
            ConnectDB.getInstance().closeItem(stmt, rs);
        }

        return listKhuyenMai;
    }

}
