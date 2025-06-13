/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.ThoiGianDiChuyen;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class ThoiGianDiChuyen_DAO {
    private ArrayList<ThoiGianDiChuyen> listThoiGianDiChuyen;
    private Ga_DAO ga_dao = new Ga_DAO();
    Connection con=null;
    PreparedStatement stmt=null;
    ResultSet rs=null;

    public ThoiGianDiChuyen_DAO() {
        ConnectDB.getInstance().connect();
        listThoiGianDiChuyen = new ArrayList<>();
    }
    public ThoiGianDiChuyen timThoiGianDiChuyenTheoMa(String maThoiGianDiChuyen){
        con = ConnectDB.getConnection();
        ThoiGianDiChuyen tgdc = null;
        try {
            stmt = con.prepareStatement("SELECT * FROM ThoiGianDiChuyen WHERE maThoiGianDiChuyen=?");
            stmt.setString(1, maThoiGianDiChuyen);
            rs = stmt.executeQuery();
            if(rs.next()){
                tgdc = new ThoiGianDiChuyen();
                tgdc.setMaThoiGianDiChuyen(rs.getString(1));
                tgdc.setGaDi(ga_dao.timGaTheoMa(rs.getString(2)));
                tgdc.setGaDen(ga_dao.timGaTheoMa(rs.getString(3)));
                tgdc.setThoiGianDiChuyen(rs.getInt(4));
                tgdc.setSoKmDiChuyen(rs.getDouble(5));
                tgdc.setSoTienMotKm(rs.getDouble(6));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return tgdc;
    }
    
    public String timMaTGDCTheoMaGaDiGaDen(String maGaDi,String maGaDen){
        con = ConnectDB.getConnection();
        String maTGDC = null;
        try{
            stmt = con.prepareStatement("SELECT maThoiGianDiChuyen FROM ThoiGianDiChuyen "
                                        + "WHERE maGaDi = ? AND maGaDen = ? ");
            stmt.setString(1, maGaDi);
            stmt.setString(2, maGaDen);
            rs = stmt.executeQuery();
            if(rs.next()){
                maTGDC = rs.getString(1);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return maTGDC;
    }
    
    public int getThoiGianDiChuyen(String maGaDi, String maGaDen) {
        ConnectDB.getInstance().connect();

        Connection conn = ConnectDB.getConnection();

        if (conn == null) {
            System.err.println("Không thể kết nối đến database!");
            return 0;
        }

        String sql = "SELECT thoiGianDiChuyen FROM ThoiGianDiChuyen WHERE maGaDi = ? AND maGaDen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maGaDi);
            stmt.setString(2, maGaDen);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("thoiGianDiChuyen");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
