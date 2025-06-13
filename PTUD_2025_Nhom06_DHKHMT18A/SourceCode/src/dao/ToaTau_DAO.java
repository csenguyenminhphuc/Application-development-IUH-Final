/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.ToaTau;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class ToaTau_DAO {
    private ArrayList<ToaTau> listToaTau;
    private Tau_DAO tau_dao = new Tau_DAO();
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public ToaTau_DAO() {
        ConnectDB.getInstance().connect();
        listToaTau = new ArrayList<>();
    }
    
    public ArrayList<ToaTau> layCacToaCuaTau(String maTau){
        listToaTau = new ArrayList<>();
        con = ConnectDB.getConnection();
        try{
            stmt = con.prepareStatement("SELECT * FROM ToaTau WHERE maTau = ?");
            stmt.setString(1, maTau);
            rs = stmt.executeQuery();
            while(rs.next()){
                ToaTau toaTau = new ToaTau();
                toaTau.setMaToaTau(rs.getString(1));
                toaTau.setTenToaTau(rs.getString(2));
                toaTau.setSoKhoangTau(rs.getInt(3));
                toaTau.setTau(tau_dao.timTauTheoMa(rs.getString(4)));
                listToaTau.add(toaTau);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return listToaTau;
    }
    public ToaTau timToaTauTheoMa(String maToaTau){
        ToaTau tt = null;
        con = ConnectDB.getConnection();
        try {
            stmt = con.prepareStatement("SELECT * FROM ToaTau WHERE maToaTau = ?");
            stmt.setString(1, maToaTau);
            rs = stmt.executeQuery();
            if(rs.next()){
                tt = new ToaTau();
                tt.setMaToaTau(rs.getString(1));
                tt.setTenToaTau(rs.getString(2));
                tt.setSoKhoangTau(rs.getInt(3));
                tt.setTau(tau_dao.timTauTheoMa(rs.getString(4)));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return tt;
    }
    
}
