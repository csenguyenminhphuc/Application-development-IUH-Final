/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.LoaiGhe;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class LoaiGhe_DAO {
    private ArrayList<LoaiGhe> listLoaiGhe;
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public LoaiGhe_DAO() {
        ConnectDB.getInstance().connect();
        listLoaiGhe = new ArrayList<>();
    }
    
    public LoaiGhe timLoaiGheTheoMa(String maLoaiGhe){
        LoaiGhe lg = null;
        con = ConnectDB.getConnection();
        try{
            stmt = con.prepareStatement("SELECT * FROM LoaiGhe WHERE maLoaiGhe = ?");
            stmt.setString(1, maLoaiGhe);
            rs = stmt.executeQuery();
            if(rs.next()){
                lg = new LoaiGhe();
                lg.setMaLoaiGhe(rs.getString(1));
                lg.setTenLoaiGhe(rs.getString(2));
                lg.setMoTa(rs.getString(3));
                lg.setHeSoGhe(rs.getDouble(4));
                
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return lg;
    }
    
    
}
