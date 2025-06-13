/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import connectdb.ConnectDB;
import entity.ChuyenDi;
import entity.Ghe;
import entity.ThoiGianDiChuyen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JOptionPane;


/**
 *
 * @author PHAMGIAKHANH
 */
public class ChuyenDi_DAO {
    private ArrayList<ChuyenDi> listChuyenDi;
    private ThoiGianDiChuyen_DAO tgdc_dao = new ThoiGianDiChuyen_DAO();
    private Tau_DAO tau_dao = new Tau_DAO();
    private Ga_DAO ga_dao = new Ga_DAO();
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public ChuyenDi_DAO() {
        ConnectDB.getInstance().connect();
        listChuyenDi = new ArrayList<>();
    }
    
    public ArrayList<ChuyenDi> timChuyenDiTheoThoiGianKhoiHanhVaGaDiGaDen(String tenGaDi,String tenGaDen,LocalDate thoiGianKhoiHanh){
        String maGaDi = ga_dao.timMaGaTheoTen(tenGaDi);
        String maGaden = ga_dao.timMaGaTheoTen(tenGaDen);
        String maTGDC = tgdc_dao.timMaTGDCTheoMaGaDiGaDen(maGaDi, maGaden);
        ConnectDB.getInstance().connect();
        listChuyenDi = new ArrayList<>();
        con = ConnectDB.getConnection();
        try{
            stmt = con.prepareStatement("SELECT * FROM ChuyenDi WHERE CONVERT(DATE, thoiGianKhoiHanh) = ? AND maThoiGianDiChuyen = ?");
            stmt.setDate(1, Date.valueOf(thoiGianKhoiHanh));
            stmt.setString(2, maTGDC);
            rs = stmt.executeQuery();
            while(rs.next()){
                ChuyenDi cd = new ChuyenDi();
                cd.setMaChuyenDi(rs.getString(1));
                cd.setThoiGianDiChuyen(tgdc_dao.timThoiGianDiChuyenTheoMa(rs.getString(2)));
                cd.setThoiGianKhoiHanh(rs.getTimestamp(3).toLocalDateTime());
                cd.setThoiGianDenDuTinh(rs.getTimestamp(4).toLocalDateTime());
                cd.setTau(tau_dao.timTauTheoMa(rs.getString(5)));
                cd.setSoGheDaDat(rs.getInt(6));
                cd.setSoGheConTrong(rs.getInt(7));
                listChuyenDi.add(cd);
            }
            
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return listChuyenDi;
    }
    
    public String timMaTauCuaChuyenDi(String maChuyenDi){
        con = ConnectDB.getConnection();
        String maTau = null;
        try{
            stmt = con.prepareStatement("SELECT maTau FROM ChuyenDi WHERE maChuyenDi = ?");
            stmt.setString(1, maChuyenDi);
            rs = stmt.executeQuery();
            if(rs.next()){
                maTau = rs.getString(1);
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        return maTau;
    }
    
    public ArrayList<Ghe> timTatCaGheDaDatCuaTauTheoChuyenDi(String maTau, String maChuyenDi){
        ArrayList<Ghe> listGheDaDat = new ArrayList<>();
        con = ConnectDB.getConnection();
        try{
            stmt = con.prepareStatement("SELECT g.maGhe, g.viTri " +
                     "FROM Ve v " +
                     "JOIN Ghe g ON v.maGhe = g.maGhe " +
                     "JOIN ChuyenDi cd ON v.maChuyenDi = cd.maChuyenDi " +
                     "WHERE cd.maTau = ? " +
                     "AND cd.maChuyenDi = ? " +
                     "AND v.trangThai IN (N'Đã đặt')");
            stmt.setString(1, maTau);
            stmt.setString(2, maChuyenDi);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Ghe ghe = new Ghe();
                ghe.setMaGhe(rs.getString("maGhe"));
                ghe.setViTri(rs.getString("viTri"));
                listGheDaDat.add(ghe);
            }
            
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return listGheDaDat;
    }
    
    
    public ChuyenDi timChuyenDiTheoMa(String maCD){
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        ChuyenDi chuyenDi =null;
        

        try {
            String sqlCD = "SELECT * FROM ChuyenDi WHERE maChuyenDi = ?";
            PreparedStatement stmtVe = con.prepareStatement(sqlCD);
            stmtVe.setString(1, maCD);
            ResultSet rscd = stmtVe.executeQuery();
            
            if(rscd.next()){
                chuyenDi =new ChuyenDi();
                chuyenDi.setMaChuyenDi(rscd.getString(1));
                chuyenDi.setThoiGianDiChuyen(tgdc_dao.timThoiGianDiChuyenTheoMa(rscd.getString(2)));
                chuyenDi.setThoiGianKhoiHanh(rscd.getTimestamp(3).toLocalDateTime());
                chuyenDi.setThoiGianDenDuTinh(rscd.getTimestamp(4).toLocalDateTime());
                chuyenDi.setTau(tau_dao.timTauTheoMa(rscd.getString(5)));
                chuyenDi.setSoGheDaDat(rscd.getInt(6));
                chuyenDi.setSoGheConTrong(rscd.getInt(7));
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chuyenDi;
    }
    
    public ArrayList<ChuyenDi> layTatCaChuyenDiTrongTuongLai() {
        ArrayList<ChuyenDi> results = new ArrayList<>();
        con = ConnectDB.getConnection();
        try {
            // Sử dụng NOW() cho MySQL hoặc GETDATE() cho SQL Server
            stmt = con.prepareStatement(
                "SELECT top 5 cd.*, tgd.*, g1.tenGa AS tenGaDi, g2.tenGa AS tenGaDen " +
                "FROM ChuyenDi cd " +
                "JOIN ThoiGianDiChuyen tgd ON cd.maThoiGianDiChuyen = tgd.maThoiGianDiChuyen " +
                "JOIN Ga g1 ON tgd.maGaDi = g1.maGa " +
                "JOIN Ga g2 ON tgd.maGaDen = g2.maGa " +
                "WHERE cd.thoiGianKhoiHanh > GETDATE()" // Thay bằng GETDATE() nếu dùng SQL Server
            );
            rs = stmt.executeQuery();
            while (rs.next()) {
                ChuyenDi cd = new ChuyenDi();
                cd.setMaChuyenDi(rs.getString("maChuyenDi"));
                // Lấy ThoiGianDiChuyen từ maThoiGianDiChuyen
                String maTGDC = rs.getString("maThoiGianDiChuyen");
                ThoiGianDiChuyen tgdc = tgdc_dao.timThoiGianDiChuyenTheoMa(maTGDC);
                cd.setThoiGianDiChuyen(tgdc);
                cd.setThoiGianKhoiHanh(rs.getTimestamp("thoiGianKhoiHanh").toLocalDateTime());
                cd.setThoiGianDenDuTinh(rs.getTimestamp("thoiGianDenDuTinh").toLocalDateTime());
                cd.setTau(tau_dao.timTauTheoMa(rs.getString("maTau")));
                cd.setSoGheDaDat(rs.getInt("soGheDaDat"));
                cd.setSoGheConTrong(rs.getInt("soGheConTrong"));
                results.add(cd);
            }
            System.out.println("Tổng số chuyến đi trong tương lai: " + results.size());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy tất cả chuyến đi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            ConnectDB.getInstance().closeItem(stmt, rs);
        }
        return results;
    }    

}
