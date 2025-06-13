/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.KhoangTau;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class KhoangTau_DAO {
    private ArrayList<KhoangTau> listKhoangTau;
    private ToaTau_DAO tt_dao = new ToaTau_DAO();
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public KhoangTau_DAO() {
        ConnectDB.getInstance().connect();
        listKhoangTau = new ArrayList<>();
    }
    
    public ArrayList<KhoangTau> layKhoangTauTheoToaTau(String maToaTau){
        listKhoangTau = new ArrayList<>();
        con = ConnectDB.getConnection();
        try {
            stmt = con.prepareStatement("SELECT * FROM KhoangTau WHERE maToaTau = ?");
            stmt.setString(1, maToaTau);
            rs = stmt.executeQuery();
            while(rs.next()){
                KhoangTau kt = new KhoangTau();
                kt.setMaKhoangTau(rs.getString(1));
                kt.setTenKhoangTau(rs.getString(2));
                kt.setSoGhe(rs.getInt(3));
                kt.setToaTau(tt_dao.timToaTauTheoMa(rs.getString(4)));
                listKhoangTau.add(kt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return listKhoangTau;
    }
    public KhoangTau timKhoangTauTheoMa(String maKT){
        con = ConnectDB.getConnection();
        KhoangTau kt = null;
        try {
            stmt = con.prepareStatement("SELECT * FROM KhoangTau WHERE maKhoangTau = ?");
            stmt.setString(1, maKT);
            rs = stmt.executeQuery();
            if(rs.next()){
                kt = new KhoangTau();
                kt.setMaKhoangTau(rs.getString(1));
                kt.setTenKhoangTau(rs.getString(2));
                kt.setSoGhe(rs.getInt(3));
                kt.setToaTau(tt_dao.timToaTauTheoMa(rs.getString(4)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kt;
    }
}
