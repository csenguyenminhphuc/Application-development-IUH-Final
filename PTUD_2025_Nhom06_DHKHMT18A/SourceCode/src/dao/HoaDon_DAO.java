/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.HoaDon;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class HoaDon_DAO {
    private ArrayList<HoaDon> listHDD;
    NhanVienDAOImpl nv_dao = new NhanVienDAOImpl();
    KhachHang_DAO kh_dao = new KhachHang_DAO();
    KhuyenMai_DAO km_dao = new KhuyenMai_DAO();
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public HoaDon_DAO() {
        ConnectDB.getInstance().connect();
        listHDD = new ArrayList<>();
    }
    
    public String themHoaDon(HoaDon hd,String maCa){
        String maHoaDon = null;
        // 1) Chuẩn bị câu lệnh CALL
        String sql = "{ CALL InsertHoaDon(?,?,?,?,?,?) }";
        ConnectDB.getInstance().connect();
        try (Connection con = ConnectDB.getConnection();
             CallableStatement cs = con.prepareCall(sql)){
            cs.setString(1, hd.getNhanVien().getMaNV());
            cs.setString(2, hd.getKhachHang().getMaKH());
            if (hd.getKhuyenMai() != null) {
                cs.setString(3, hd.getKhuyenMai().getMaKhuyenMai());
            } else {
                cs.setNull(3, java.sql.Types.VARCHAR);
            }
            cs.setInt(4,    hd.getSoVe());
            cs.setTimestamp(5, Timestamp.valueOf(hd.getNgayLapHoaDon()));
            cs.setString(6, maCa);  // ví dụ "CA-01"

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    maHoaDon = rs.getString("NewHoaDonID");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return maHoaDon;
    }
    public int tinhTongVeTheoMaHoaDon(String maHoaDon){
        con = ConnectDB.getConnection();
        int tongVe = 0;
        try{
            stmt = con.prepareStatement("SELECT COUNT(*) AS tongVe FROM Ve WHERE maHoaDon = ? GROUP BY maHoaDon");
            stmt.setString(1, maHoaDon);
            rs = stmt.executeQuery();
            if(rs.next()){
                tongVe = rs.getInt(1);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return tongVe;
    }
    public boolean  capNhatSoVeChoHoaDon(String maHoaDon){
        con = ConnectDB.getConnection();
        int soVe = tinhTongVeTheoMaHoaDon(maHoaDon);
        try {
            stmt =con.prepareStatement("UPDATE HoaDon SET soVe = ? WHERE maHoaDon = ?");
            stmt.setInt(1, soVe);
            stmt.setString(2, maHoaDon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
            
        }
    }
    public HoaDon timHoaDonTheoMa(String maHD){
        ConnectDB.getInstance().connect();
        con = ConnectDB.getConnection();
        HoaDon hd =null;
        try {
            stmt = con.prepareStatement("SELECT * FROM HoaDon WHERE maHoaDon = ? ");
            stmt.setString(1, maHD);
            
            rs = stmt.executeQuery();
            if(rs.next()){
                hd = new HoaDon();
                hd.setMaHoaDon(rs.getString(1));
                hd.setNgayLapHoaDon(rs.getTimestamp(2).toLocalDateTime());
                hd.setVAT(rs.getDouble(3));
                hd.setSoVe(rs.getInt(4));
                hd.setTongTien(rs.getDouble(5));
                hd.setNhanVien(nv_dao.getNhanVienByMaNV(rs.getString(6)));
                hd.setKhachHang(kh_dao.timKhachHangTheoMa(rs.getString(7)));
                hd.setKhuyenMai(km_dao.timKhuyenMaiTheoMa(rs.getString(8)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return hd;
    }
    public boolean updateTongTienHoaDon(HoaDon hd) throws SQLException{
        String sql = "UPDATE HoaDon SET tongTien = ? WHERE maHoaDon = ?";
        ConnectDB.getInstance().connect();
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, hd.getTongTien());  // đúng với CHECK
            ps.setString(2, hd.getMaHoaDon());
            return ps.executeUpdate() > 0;
        }
    }
}
