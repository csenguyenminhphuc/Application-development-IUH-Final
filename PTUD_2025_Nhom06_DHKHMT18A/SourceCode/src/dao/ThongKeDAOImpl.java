package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import connectdb.ConnectDB;
import entity.CaHienTai;
import entity.SoGioLamCuaNhanVien;
import entity.SoVeTrungBinhMoiGio;
import entity.SoVeBanDuoc;
import entity.SoVeBanDuocTheoNhanVien;
import entity.TongTienBanDuoc;
import entity.TopChuyenDi;
import entity.TyLeHuyVe;
import entity.TyLeLoaiVe;
import entity.TyLeNhanVienTheoNhomNangSuat;
import entity.TyLeVeTheoLoaiGhe;
import java.sql.Date;
import java.util.List;

public class ThongKeDAOImpl {

    public List<SoVeBanDuoc> getSoVeBanDuocTheoNgay(String maNhanVien) {
        List<SoVeBanDuoc> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT top 7 [maNV], [tongVeBan], ngay FROM CaLamViecNhanVien " +
                     "WHERE [maNV] = ? ORDER BY ngay desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("maNV");
                    int tongVeBan = rs.getInt("tongVeBan");
                    Date ngay = rs.getDate("ngay");

                    list.add(new SoVeBanDuoc(maNV, tongVeBan, ngay));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<SoVeTrungBinhMoiGio> getSoVeTrungBinhMoiGio(String maNhanVien) {
        List<SoVeTrungBinhMoiGio> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT top 7 ngay,\n" +
                    "       maNV,\n" +
                    "	   c.[tongVeBan] as soVe,\n" +
                    "       DATEDIFF(minute, ca.[thoiGianBatDau], ca.[thoiGianKetThuc]) AS soPhut\n" +
                    "FROM [dbo].[CaLamViecNhanVien] c \n" +
                    "JOIN [dbo].[Ca] ca ON ca.maCa = c.maCa \n" +
                    "WHERE c.[maNV] = ? \n" + 
                    "order by ngay desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("maNV");
                    float soGio = rs.getInt("soPhut")/60;
                    int soVe = rs.getInt("soVe");
                    Date ngay = rs.getDate("ngay");
                    

                    list.add(new SoVeTrungBinhMoiGio(maNV, soVe, soGio, ngay));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<TongTienBanDuoc> getTongTienBanDuoc(String maNhanVien) throws SQLException {
        List<TongTienBanDuoc> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT top 7 [maNV], [tongTienBanDuoc], ngay FROM CaLamViecNhanVien " +
                     "WHERE [maNV] = ? ORDER BY ngay desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("maNV");
                    double tongTien = rs.getInt("tongTienBanDuoc");
                    Date ngay = rs.getDate("ngay");

                    list.add(new TongTienBanDuoc(maNV, tongTien, ngay));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.close();
        return list;
    }
    
    public List<TongTienBanDuoc> getDoanhThuTangTruong(String maNhanVien) {
        List<TongTienBanDuoc> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT top 2 [maNV], [tongTienBanDuoc], ngay FROM CaLamViecNhanVien " +
                     "WHERE [maNV] = ? ORDER BY ngay desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("maNV");
                    double tongTien = rs.getInt("tongTienBanDuoc");
                    Date ngay = rs.getDate("ngay");

                    list.add(new TongTienBanDuoc(maNV, tongTien, ngay));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<TyLeLoaiVe> getSoLuongVeTheoLoai() {
        List<TyLeLoaiVe> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "select maLoaiVe, count(*) as soVe from [dbo].[Ve]\n" +
                    "group by maLoaiVe\n" +
                    "order by maLoaiVe desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maLoaiVe = rs.getString("maLoaiVe");
                    int soVe = rs.getInt("soVe");

                    list.add(new TyLeLoaiVe(maLoaiVe, soVe));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<TyLeVeTheoLoaiGhe> getSoLuongVeTheoLoaiGhe() {
        List<TyLeVeTheoLoaiGhe> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "select g.maLoaiGhe, sum(ve.giaVe) as giaVe\n" +
                    "from [dbo].[Ve] ve join [dbo].[Ghe] g \n" +
                    "on ve.[maGhe] = g.[maGhe]\n" +
                    "group by g.maLoaiGhe\n" +
                    "order by g.maLoaiGhe desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maLoaiVe = rs.getString("maLoaiGhe");
                    double soVe = rs.getDouble("giaVe");

                    list.add(new TyLeVeTheoLoaiGhe(maLoaiVe, soVe));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<TongTienBanDuoc> getTongTienBanDuocTheoThang() {
        List<TongTienBanDuoc> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT TOP 12 " +
                     "       MONTH(ngay) AS thang, " +
                     "       YEAR(ngay) AS nam, " +
                     "       SUM(tongTienBanDuoc) AS tongTien " +
                     "FROM [dbo].[CaLamViecNhanVien] " +
                     "GROUP BY YEAR(ngay), MONTH(ngay) " +
                     "ORDER BY nam DESC, thang DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int thang = rs.getInt("thang");
                    int nam = rs.getInt("nam");
                    double tongTien = rs.getDouble("tongTien");
                    java.sql.Date date = java.sql.Date.valueOf(java.time.LocalDate.of(nam, thang, 1));
                    list.add(new TongTienBanDuoc(null, tongTien, date));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<TongTienBanDuoc> getTongTienBanDuocTheoNgay() {
        List<TongTienBanDuoc> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT TOP 7 " +
                     "       DAY(ngay) AS ngay, " +
                     "       MONTH(ngay) AS thang, " +
                     "       YEAR(ngay) AS nam, " +
                     "       SUM(tongTienBanDuoc) AS tongTien " +
                     "FROM [dbo].[CaLamViecNhanVien] " +
                     "GROUP BY YEAR(ngay), MONTH(ngay), DAY(ngay) " +
                     "ORDER BY nam DESC, thang DESC, ngay DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ngay = rs.getInt("ngay");
                    int thang = rs.getInt("thang");
                    int nam = rs.getInt("nam");
                    double tongTien = rs.getDouble("tongTien");
                    java.sql.Date date = java.sql.Date.valueOf(java.time.LocalDate.of(nam, thang, ngay));
                    list.add(new TongTienBanDuoc(null, tongTien, date));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<TongTienBanDuoc> getTongTienBanDuocTheoNam() {
        List<TongTienBanDuoc> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT TOP 5 " +
                     "       YEAR(ngay) AS nam, " +
                     "       SUM(tongTienBanDuoc) AS tongTien " +
                     "FROM [dbo].[CaLamViecNhanVien] " +
                     "GROUP BY YEAR(ngay) " +
                     "ORDER BY nam DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int nam = rs.getInt("nam");
                    double tongTien = rs.getDouble("tongTien");
                    java.sql.Date date = java.sql.Date.valueOf(java.time.LocalDate.of(nam, 1, 1));
                    list.add(new TongTienBanDuoc(null, tongTien, date));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TyLeHuyVe> getTyLeHuyVe() {
        List<TyLeHuyVe> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "select trangThai, count(*) as soVe from [dbo].[Ve]\n" +
                    "where trangThai = N'Đã hoàn thành' or trangThai = N'Đã hủy'\n" +
                    "group by trangThai\n" +
                    "order by trangThai";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String trangThai = rs.getString("trangThai");
                    int soVe = rs.getInt("soVe");
                    list.add(new TyLeHuyVe(trangThai, soVe));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<TyLeNhanVienTheoNhomNangSuat> getTyLeNhanVienTheoNhomNangSuat() {
        List<TyLeNhanVienTheoNhomNangSuat> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "WITH EmployeeProductivity AS (\n" +
                    "    SELECT \n" +
                    "        dsca.maNV,\n" +
                    "        nv.tenNV,\n" +
                    "        SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio,\n" +
                    "        CASE \n" +
                    "            WHEN SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 = 0 \n" +
                    "            THEN 0 \n" +
                    "            ELSE SUM(dsca.tongVeBan) / (SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0) \n" +
                    "        END AS soVeTrungBinhMoiGio\n" +
                    "    FROM [dbo].[CaLamViecNhanVien] dsca \n" +
                    "    JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa\n" +
                    "    JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV\n" +
                    "    GROUP BY dsca.maNV, nv.tenNV\n" +
                    "),\n" +
                    "CategorizedEmployees AS (\n" +
                    "    SELECT \n" +
                    "        CASE \n" +
                    "            WHEN soVeTrungBinhMoiGio > 50 THEN N'Cao (>50 vé/giờ)'\n" +
                    "            WHEN soVeTrungBinhMoiGio >= 25 THEN N'Trung bình (>25 vé/giờ)'\n" +
                    "            ELSE N'Thấp (<25 vé/giờ)'\n" +
                    "        END AS NhomNangSuat,\n" +
                    "        maNV,\n" +
                    "        tenNV,\n" +
                    "        soVeTrungBinhMoiGio\n" +
                    "    FROM EmployeeProductivity\n" +
                    ")\n" +
                    "SELECT \n" +
                    "    NhomNangSuat,\n" +
                    "    COUNT(*) AS SoNhanVien\n" +
                    "FROM CategorizedEmployees\n" +
                    "GROUP BY NhomNangSuat\n" +
                    "ORDER BY \n" +
                    "    CASE \n" +
                    "        WHEN NhomNangSuat = N'Cao (>50 vé/giờ)' THEN 1\n" +
                    "        WHEN NhomNangSuat = N'Trung bình (>25 vé/giờ)' THEN 2\n" +
                    "        ELSE 3\n" +
                    "    END;";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String trangThai = rs.getString("NhomNangSuat");
                    int soVe = rs.getInt("SoNhanVien");
                    list.add(new TyLeNhanVienTheoNhomNangSuat(trangThai, soVe));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<SoVeBanDuocTheoNhanVien> getSoVeNhanVienBanDuoc() {
        List<SoVeBanDuocTheoNhanVien> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "select top 5 nv.tenNV as tenNhanVien, sum(dsca.tongVeBan) as tongVeBan\n" +
                        "from [dbo].[CaLamViecNhanVien] dsca join [dbo].[NhanVien] nv on nv.maNV = dsca.maNV\n" +
                        "group by nv.tenNV\n" +
                        "order by tongVeBan desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("tenNhanVien");
                    int tongVeBan = rs.getInt("tongVeBan");

                    list.add(new SoVeBanDuocTheoNhanVien(maNV, tongVeBan));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<SoGioLamCuaNhanVien> getSoGioLamCuaNhanVienTheoTuan() {
        List<SoGioLamCuaNhanVien> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT top 5 dsca.maNV, nv.tenNV as tenNhanVien, SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio \n" +
                    "FROM [dbo].[CaLamViecNhanVien] dsca \n" +
                    "JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa\n" +
                    "JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV\n" +
                    "WHERE dsca.ngay >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE)\n" +
                    "  AND dsca.ngay <= CAST(GETDATE() AS DATE)\n" +
                    "GROUP BY dsca.maNV, nv.tenNV\n" +
                    "order by soGio desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("tenNhanVien");
                    int soGio = rs.getInt("soGio");
                    list.add(new SoGioLamCuaNhanVien(maNV, soGio));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<SoGioLamCuaNhanVien> getSoGioLamCuaNhanVienTheoThang() {
        List<SoGioLamCuaNhanVien> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT top 5 dsca.maNV, nv.tenNV as tenNhanVien, SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio \n" +
                    "FROM [dbo].[CaLamViecNhanVien] dsca \n" +
                    "JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa\n" +
                    "JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV\n" +
                    "WHERE dsca.ngay >= CAST(DATEADD(MONTH, -1, GETDATE()) AS DATE)\n" +
                    "  AND dsca.ngay <= CAST(GETDATE() AS DATE)\n" +
                    "GROUP BY dsca.maNV, nv.tenNV\n" +
                    "order by soGio desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("tenNhanVien");
                    int soGio = rs.getInt("soGio");
                    list.add(new SoGioLamCuaNhanVien(maNV, soGio));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<SoGioLamCuaNhanVien> getSoGioLamCuaNhanVienTheoNam() {
        List<SoGioLamCuaNhanVien> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT top 5 dsca.maNV, nv.tenNV as tenNhanVien, SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio \n" +
                    "FROM [dbo].[CaLamViecNhanVien] dsca \n" +
                    "JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa\n" +
                    "JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV\n" +
                    "WHERE dsca.ngay >= CAST(DATEADD(YEAR, -1, GETDATE()) AS DATE)\n" +
                    "  AND dsca.ngay <= CAST(GETDATE() AS DATE)\n" +
                    "GROUP BY dsca.maNV, nv.tenNV\n" +
                    "order by soGio desc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("tenNhanVien");
                    int soGio = rs.getInt("soGio");
                    list.add(new SoGioLamCuaNhanVien(maNV, soGio));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TopChuyenDi> getTopChuyenDi() {
        List<TopChuyenDi> list = new ArrayList<>();
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sqlMain = "SELECT TOP 3 " +
                        "tgdc.maGaDi, " +
                        "tgdc.maGaDen, " +
                        "SUM(cd.soGheDaDat) AS tongSoVeDaDat " +
                        "FROM [dbo].[ChuyenDi] cd " +
                        "JOIN [dbo].[ThoiGianDiChuyen] tgdc ON cd.maThoiGianDiChuyen = tgdc.maThoiGianDiChuyen " +
                        "GROUP BY tgdc.maGaDi, tgdc.maGaDen " +
                        "ORDER BY tongSoVeDaDat DESC";

        String sqlGa = "SELECT tenGa FROM [dbo].[Ga] WHERE maGa = ?";

        try (PreparedStatement psMain = con.prepareStatement(sqlMain)) {
            try (ResultSet rsMain = psMain.executeQuery()) {
                while (rsMain.next()) {
                    String maGaDi = rsMain.getString("maGaDi");
                    String maGaDen = rsMain.getString("maGaDen");
                    int soVe = rsMain.getInt("tongSoVeDaDat");

                    String tenGaDi = getTenGa(con, sqlGa, maGaDi);
                    String tenGaDen = getTenGa(con, sqlGa, maGaDen);

                    list.add(new TopChuyenDi(tenGaDi, tenGaDen, soVe));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private String getTenGa(Connection con, String sql, String maGa) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maGa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tenGa");
                }
            }
        }
        return null; 
    }
    
    public CaHienTai getThongTinCaHienTai() {
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();

        String sql = "SELECT \n" +
                    "	nv.maNV,\n" +
                    "    nv.tenNV,\n" +
                    "    nv.vaiTro,\n" +
                    "	dsca.maCa,\n" +
                    "	dsca.ngay,\n" +
                    "    COALESCE(dsca.tongVeBan, 0) AS soVeBanTrongCa,\n" +
                    "    COALESCE(dsca.tongTienBanDuoc, 0) AS soTienBanTrongCa,\n" +
                    "    (SELECT SUM(tongVeBan) \n" +
                    "     FROM [dbo].[CaLamViecNhanVien]) AS tongSoVeHienTai,\n" +
                    "    (SELECT SUM(tongTienBanDuoc) \n" +
                    "     FROM [dbo].[CaLamViecNhanVien]) AS tongTienHienTai\n" +
                    "FROM [dbo].[CaLamViecNhanVien] dsca \n" +
                    "JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa\n" +
                    "join [dbo].[NhanVien] nv on nv.maNV = dsca.maNV\n" +
                    "WHERE \n" +
                    "    dsca.ngay = CAST('2025-05-26' AS DATE)\n" +
                    "    AND (\n" +
                    "        -- Case 1: Shift does not cross midnight (start time <= end time)\n" +
                    "        (ca.thoiGianBatDau <= ca.thoiGianKetThuc \n" +
                    "         AND CAST('07:30:00' AS TIME) BETWEEN ca.thoiGianBatDau AND ca.thoiGianKetThuc)\n" +
                    "    )\n" +
                    "GROUP BY nv.maNV, nv.tenNV, nv.vaiTro, dsca.maCa, dsca.ngay, dsca.tongVeBan, dsca.tongTienBanDuoc";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("maNV");
                    String tenNV = rs.getString("tenNV");
                    String vaiTro = rs.getString("vaiTro");
                    String maCa = rs.getString("maCa");
                    java.sql.Date ngay = rs.getDate("ngay");
                    int soVeBanTrongCa = rs.getInt("soVeBanTrongCa"); // Đã được COALESCE xử lý NULL thành 0
                    double soTienBanTrongCa = rs.getDouble("soTienBanTrongCa");
                    int tongSoVeHienTai = rs.getInt("tongSoVeHienTai");
                    double tongTienHienTai = rs.getDouble("tongTienHienTai");

                    return new CaHienTai(maNV, tenNV, vaiTro, maCa, ngay.toLocalDate(), 
                                                       soVeBanTrongCa, soTienBanTrongCa, 
                                                       tongSoVeHienTai, tongTienHienTai);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public static void main(String[] args) {
        ThongKeDAOImpl aOImpl = new ThongKeDAOImpl();
        List<TongTienBanDuoc> banDuocs = aOImpl.getTongTienBanDuocTheoNgay();
//        CaHienTai caHienTai = aOImpl.getThongTinCaHienTai();
//        System.out.println(caHienTai);
        banDuocs.stream().forEach(tx -> System.out.println(tx));
    }
}
