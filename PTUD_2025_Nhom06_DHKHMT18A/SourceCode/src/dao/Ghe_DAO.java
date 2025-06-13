/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.Ghe;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class Ghe_DAO {
    private ArrayList<Ghe> listGhe;
    private LoaiGhe_DAO lg_dao = new LoaiGhe_DAO();
    private KhoangTau_DAO kt_dao = new KhoangTau_DAO();
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public Ghe_DAO() {
        ConnectDB.getInstance().connect();
        listGhe = new ArrayList<>();
    }
    public Ghe timGheTheoMa(String maGhe){
        Ghe ghe = null;
        con = ConnectDB.getConnection();
        try {
            stmt = con.prepareStatement("SELECT * FROM Ghe WHERE maGhe = ?");
            stmt.setString(1, maGhe);
            rs = stmt.executeQuery();
            while(rs.next()){
                ghe = new Ghe();
                ghe.setMaGhe(rs.getString(1));
                ghe.setViTri(rs.getString(2));
                ghe.setKhoangTau(kt_dao.timKhoangTauTheoMa(rs.getString(3)));
                ghe.setLoaiGhe(lg_dao.timLoaiGheTheoMa(rs.getString(4)));
                listGhe.add(ghe); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return ghe;
    }
    public ArrayList<Ghe> timGheTheoKhoang(String maKhoangTau){
        listGhe = new ArrayList<>();
        con = ConnectDB.getConnection();
        try {
            stmt = con.prepareStatement("SELECT * FROM Ghe WHERE maKhoangTau = ?");
            stmt.setString(1, maKhoangTau);
            rs = stmt.executeQuery();
            while(rs.next()){
                Ghe ghe = new Ghe();
                ghe.setMaGhe(rs.getString(1));
                ghe.setViTri(rs.getString(2));
                ghe.setLoaiGhe(lg_dao.timLoaiGheTheoMa(rs.getString(3)));
                listGhe.add(ghe); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return listGhe;
    }
    public String timKhoangTheoGhe(String maGhe){
        String maKhoang = null;
        con = ConnectDB.getConnection();
        try {
            stmt = con.prepareStatement("SELECT maKhoangTau FROM Ghe WHERE maGhe = ?");
            stmt.setString(1, maGhe);
            rs = stmt.executeQuery();
            if(rs.next()){
                maKhoang = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return maKhoang;
    }
}
