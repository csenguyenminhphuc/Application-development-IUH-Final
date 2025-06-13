/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.Ve;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class Ve_DAO {
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;
    LoaiVe_DAO lv_dao = new LoaiVe_DAO();
    ChuyenDi_DAO cd_dao = new ChuyenDi_DAO();
    HanhKhach_DAO hk_dao = new HanhKhach_DAO();
    Ghe_DAO ghe_dao = new Ghe_DAO();
    HoaDon_DAO hd_dao = new HoaDon_DAO();
    // Khoảng thời gian tối thiểu trước giờ khởi hành (8 giờ)
    private static final long MIN_HOURS_BEFORE_DEPARTURE = 8;

    // Ngưỡng 48 giờ để phân biệt mức phí
    private static final long THRESHOLD_48_HOURS = 48;

    // Phí khi hủy trước >48h: 10%
    private static final double FEE_RATE_LONG = 0.10;
    // Phí khi hủy trong khoảng 8–48h: 20%
    private static final double FEE_RATE_SHORT = 0.20;
    public Ve_DAO() {
        ConnectDB.getInstance().connect();
    }
    
    public String themVe(Ve ve){
        String maVe= null;
        String sql = "{ CALL InsertVe( ?, ?, ?, ?, ?, ?, ? ) }";
        ConnectDB.getInstance().connect();
        try (Connection con = ConnectDB.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            // 2) Thiết lập các tham số đầu vào theo thứ tự khai báo trong proc
            cs.setString(1, ve.getHanhKhach().getMaHanhKhach());
            cs.setString(2, ve.getLoaiVe().getMaLoaiVe());
            cs.setString(3, ve.getChuyenDi().getMaChuyenDi());
            cs.setString(4, ve.getGhe().getMaGhe());
            cs.setString(5, ve.getHoaDon().getMaHoaDon());
            cs.setString(6, ve.getTrangThai());
            cs.setDouble(7, ve.getGiaVe());

            // 3) Thực thi và lấy ResultSet trả về từ cuối proc (SELECT @maVe AS NewVeID)
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    // Nếu proc SELECT với alias, bạn có thể gọi:
                    maVe = rs.getString("NewMaVe");
                    // hoặc lấy cột 1:
                    // maVe = rs.getString(1);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return maVe;
    }
    
    public double tinhTongTienVe(String maLoaiVe, String maChuyenDi,String maGhe){
        con = ConnectDB.getConnection();
        double giaVe = 0.0;

        try {
            // 1. Lấy heSoLoaiVe từ bảng LoaiVe
            String sqlLoaiVe = "SELECT heSoLoaiVe FROM LoaiVe WHERE maLoaiVe = ?";
            PreparedStatement stmtLoaiVe = con.prepareStatement(sqlLoaiVe);
            stmtLoaiVe.setString(1, maLoaiVe);  // Truyền mã loại vé
            ResultSet rsLoaiVe = stmtLoaiVe.executeQuery();
            double heSoLoaiVe = 0.0;
            if (rsLoaiVe.next()) {
                heSoLoaiVe = rsLoaiVe.getDouble("heSoLoaiVe");
            }
            rsLoaiVe.close();
            stmtLoaiVe.close();

            // 2. Lấy soTienMotKm và soKmDiChuyen từ bảng ThoiGianDiChuyen
            String sqlThoiGianDiChuyen = "SELECT soTienMotKm, soKmDiChuyen FROM ThoiGianDiChuyen WHERE maThoiGianDiChuyen = ?";
            PreparedStatement stmtTGC = con.prepareStatement(sqlThoiGianDiChuyen);
            stmtTGC.setString(1, maChuyenDi);  // Truyền mã chuyến đi
            ResultSet rsTGC = stmtTGC.executeQuery();
            double soTienMotKm = 0.0;
            double soKmDiChuyen = 0.0;
            if (rsTGC.next()) {
                soTienMotKm = rsTGC.getDouble("soTienMotKm");
                soKmDiChuyen = rsTGC.getDouble("soKmDiChuyen");
            }
            rsTGC.close();
            stmtTGC.close();

            // 3. Lấy heSoGhe từ bảng LoaiGhe
            String sqlLoaiGhe = "SELECT heSoGhe FROM LoaiGhe WHERE maLoaiGhe = ?";
            PreparedStatement stmtLoaiGhe = con.prepareStatement(sqlLoaiGhe);
            stmtLoaiGhe.setString(1, maGhe);  // Truyền mã ghế
            ResultSet rsLoaiGhe = stmtLoaiGhe.executeQuery();
            double heSoGhe = 0.0;
            if (rsLoaiGhe.next()) {
                heSoGhe = rsLoaiGhe.getDouble("heSoGhe");
            }
            rsLoaiGhe.close();
            stmtLoaiGhe.close();

            // 4. Tính giá vé trong Java
            giaVe = heSoLoaiVe * soTienMotKm * soKmDiChuyen * heSoGhe;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return giaVe;
            
    }
    public Ve timVeTheoMa(String maVe){
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        Ve ve =null;
        try {
            String sqlVe = "SELECT * FROM Ve WHERE maVe = ?";
            PreparedStatement stmtVe = con.prepareStatement(sqlVe);
            stmtVe.setString(1, maVe);
            ResultSet rsVe = stmtVe.executeQuery();
            
            if(rsVe.next()){
                ve =new Ve();
                ve.setMaVe(rsVe.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ve;
    }
    public Ve layThongTinVeTheoMaVe(String maVe){
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        Ve ve =null;
        

        try {
            String sqlVe = "SELECT * FROM Ve WHERE maVe = ?";
            PreparedStatement stmtVe = con.prepareStatement(sqlVe);
            stmtVe.setString(1, maVe);
            ResultSet rsVe = stmtVe.executeQuery();
            
            if(rsVe.next()){
                ve =new Ve();
                ve.setMaVe(rsVe.getString(1));
                ve.setLoaiVe(lv_dao.timLoaiVeTheoMa(rsVe.getString(2)));
                ve.setChuyenDi(cd_dao.timChuyenDiTheoMa(rsVe.getString(3)));
                ve.setHanhKhach(hk_dao.timHanhKhachTheoMa(rsVe.getString(4)));
                ve.setGhe(ghe_dao.timGheTheoMa(rsVe.getString(5)));
                ve.setHoaDon(hd_dao.timHoaDonTheoMa(rsVe.getString(6)));
                ve.setTrangThai(rsVe.getString(7));
                ve.setGiaVe(rsVe.getDouble(8));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ve;
    }
    public boolean checkTrangThaiVeTheoMa(String maVe){
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        String trangThai = "";
        

        try {
            String sqlVe = "SELECT trangThai FROM Ve WHERE maVe = ?";
            PreparedStatement stmtVe = con.prepareStatement(sqlVe);
            stmtVe.setString(1, maVe);
            ResultSet rsVe = stmtVe.executeQuery();
            
            if(rsVe.next()){
                trangThai = rsVe.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(trangThai.equals("Đã đặt")){
            return true;
        }
        return false;
    }
    public boolean checkDieuKienTraVe(String maVe) throws SQLException{
        String sql =
            "SELECT DATEDIFF(HOUR, GETDATE(), cd.thoiGianKhoiHanh) AS hoursLeft " +
            "FROM Ve v " +
            "JOIN ChuyenDi cd ON v.maChuyenDi = cd.maChuyenDi " +
            "WHERE v.maVe = ?";

        ConnectDB.getInstance().connect();
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maVe);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;   // không có vé → không cho trả
                }
                int hoursLeft = rs.getInt("hoursLeft");
                System.out.println("dao.Ve_DAO.hoursLeft(): "+ hoursLeft);
                // chỉ khi hoursLeft > 8 mới đủ điều kiện
                return hoursLeft > MIN_HOURS_BEFORE_DEPARTURE;
            }
        }
    }
    public boolean capNhatTrangThaiVe(String maVe) throws SQLException {
    String sql = "UPDATE Ve SET trangThai = ? WHERE maVe = ?";
    ConnectDB.getInstance().connect();
    try (Connection con = ConnectDB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, "Đã hủy");  // đúng với CHECK
        ps.setString(2, maVe);
        return ps.executeUpdate() > 0;
    }
}
}
