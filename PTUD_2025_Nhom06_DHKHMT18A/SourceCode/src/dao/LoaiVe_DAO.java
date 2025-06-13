/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.LoaiVe;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class LoaiVe_DAO {
    private ArrayList<LoaiVe> listLoaiVe;
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public LoaiVe_DAO() {
        ConnectDB.getInstance().connect();
        listLoaiVe = new ArrayList<>();
    }
    
    public ArrayList<LoaiVe> layTatCaLoaiVe(){
        con = ConnectDB.getConnection();
        listLoaiVe = new ArrayList<>();
        
        try {
            stmt = con.prepareStatement("SELECT * FROM LoaiVe");
            rs = stmt.executeQuery();
            while(rs.next()){
                LoaiVe lv = new LoaiVe();
                lv.setMaLoaiVe(rs.getString(1));
                lv.setTenLoaiVe(rs.getString(2));
                lv.setMoTaLoaiVe(rs.getString(3));
                lv.setHeSoLoaiVe(rs.getDouble(4));
                listLoaiVe.add(lv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listLoaiVe;
    }
    
    public LoaiVe timLoaiVeTheoMa(String maLV){
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        LoaiVe loaive =null;
        

        try {
            String sqlVe = "SELECT * FROM LoaiVe WHERE maLoaiVe = ?";
            PreparedStatement stmtVe = con.prepareStatement(sqlVe);
            stmtVe.setString(1, maLV);
            ResultSet rslv = stmtVe.executeQuery();
            
            if(rslv.next()){
                loaive =new LoaiVe();
                loaive.setMaLoaiVe(rslv.getString(1));
                loaive.setTenLoaiVe(rslv.getString(2));
                loaive.setMoTaLoaiVe(rslv.getString(3));
                loaive.setHeSoLoaiVe(rslv.getDouble(4));
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaive;
    }
}
