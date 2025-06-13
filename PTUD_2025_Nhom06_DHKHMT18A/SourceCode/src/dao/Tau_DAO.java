/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import connectdb.ConnectDB;
import entity.Tau;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author PHAMGIAKHANH
 */
public class Tau_DAO {
    private ArrayList<Tau> listTau;
    Connection con = null;
    PreparedStatement stmt= null;
    ResultSet rs= null;

    public Tau_DAO() {
        ConnectDB.getInstance().connect();
        listTau = new ArrayList<>();
    }
    public ArrayList<Tau> docTatCaTau(){
        listTau = new ArrayList<>();
        con = ConnectDB.getConnection();
        try{
            stmt = con.prepareStatement("SELECT * FROM Tau");
            rs = stmt.executeQuery();
            while(rs.next()){
                Tau tau = new Tau();
                tau.setMaTau(rs.getString(1));
                tau.setTenTau(rs.getString(2));
                tau.setSoToaTau(rs.getInt(3));
            }
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return listTau;
    }
    public Tau timTauTheoMa(String maTau){
        con = ConnectDB.getConnection();
        Tau tau = null;
        try{
            stmt = con.prepareStatement("SELECT * FROM Tau WHERE maTau = ?");
            stmt.setString(1, maTau);
            rs = stmt.executeQuery();
            if(rs.next()){
                tau = new Tau();
                tau.setMaTau(rs.getString(1));
                tau.setTenTau(rs.getString(2));
                tau.setSoToaTau(rs.getInt(3));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return tau;
    }
    
    // Đây là phần tôi thêm
    public ArrayList<Tau> getAllTau() {
        return docTatCaTau();
    }
    public List<Tau> timTauTheoTen(String tenTau) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Tau> result = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM Tau WHERE tenTau LIKE ?");
            stmt.setString(1, "%" + tenTau + "%");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Tau tau = new Tau();
                tau.setMaTau(rs.getString(1));
                tau.setTenTau(rs.getString(2));
                tau.setSoToaTau(rs.getInt(3));
                result.add(tau);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi tìm tàu theo tên: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            ConnectDB.getInstance().closeItem(stmt, rs);
        }
        return result;
    }

}
