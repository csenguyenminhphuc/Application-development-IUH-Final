/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.Ga;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PHAMGIAKHANH
 */
public class Ga_DAO {
    private ArrayList<Ga> listGa;
    Connection con=null;
    PreparedStatement stmt=null;
    ResultSet rs=null;

    public Ga_DAO() {
        ConnectDB.getInstance().connect();
        listGa = new ArrayList<>();
    }
    public ArrayList<Ga> loadTatCaCaGa(){
        con = ConnectDB.getConnection();
        listGa = new ArrayList<>();
        try{
            stmt = con.prepareStatement("SELECT * FROM Ga");
            rs = stmt.executeQuery();
            while(rs.next()){
                Ga ga = new Ga();
                ga.setMaGa(rs.getString(1));
                ga.setTenGa(rs.getString(2));
                ga.setDiaChi(rs.getString(3));
                ga.setSoDienThoai(rs.getString(4));
                listGa.add(ga);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return listGa;
    }
    public Ga timGaTheoMa(String maGa){
        con = ConnectDB.getConnection();
        Ga ga = null;
        try{
            stmt = con.prepareStatement("SELECT * FROM Ga WHERE maGa = ?");
            stmt.setString(1, maGa);
            rs = stmt.executeQuery();
            if(rs.next()){
                ga = new Ga();
                ga.setMaGa(rs.getString(1));
                ga.setTenGa(rs.getString(2));
                ga.setDiaChi(rs.getString(3));
                ga.setSoDienThoai(rs.getString(4));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return ga;
        
    }
    public String timMaGaTheoTen(String tenGa){
        ConnectDB.getInstance().connect();
        con = ConnectDB.getConnection();
        String maGa = null;
        try{
            stmt = con.prepareStatement("SELECT maGa FROM Ga WHERE tenGa = ? ");
            stmt.setString(1, tenGa);
            rs = stmt.executeQuery();
            if(rs.next()){
                maGa = rs.getString(1);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return maGa;
        
    }
    
    // Đây là phần tôi thêm
    public ArrayList<Ga> timGaTheoTen(String tenGa) {
        con = ConnectDB.getConnection();
        listGa = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM Ga WHERE tenGa LIKE ?");
            stmt.setString(1, "%" + tenGa + "%"); // Use LIKE for partial matching
            rs = stmt.executeQuery();
            while (rs.next()) {
                Ga ga = new Ga();
                ga.setMaGa(rs.getString(1));
                ga.setTenGa(rs.getString(2));
                ga.setDiaChi(rs.getString(3));
                ga.setSoDienThoai(rs.getString(4));
                listGa.add(ga);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listGa;
    }

    public ArrayList<Ga> getAllGa() {
        return loadTatCaCaGa();    
    }

}
