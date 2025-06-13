SELECT ngay,
       maNV,
	   c.[tongVeBan],
       DATEDIFF(minute, ca.[thoiGianBatDau], ca.[thoiGianKetThuc]) AS soPhut
FROM [dbo].[CaLamViecNhanVien] c 
JOIN [dbo].[Ca] ca ON ca.maCa = c.maCa 
WHERE c.[maNV] = 'NV-0-00-681-131'


SELECT top 7 [maNV], [tongTienBanDuoc],ngay FROM CaLamViecNhanVien
WHERE [maNV] = 'NV-0-00-681-131' ORDER BY ngay desc

select maLoaiVe, count(*) from [dbo].[Ve]
group by maLoaiVe
order by maLoaiVe desc

SELECT top 12
       MONTH(ngay) AS thang,
       YEAR(ngay) AS nam,
       SUM(tongTienBanDuoc) AS tongTien
FROM [dbo].[CaLamViecNhanVien]
GROUP BY YEAR(ngay), MONTH(ngay)
order by nam desc, thang desc

SELECT top 7 day(ngay) as ngay,
       MONTH(ngay) AS thang,
       YEAR(ngay) AS nam,
       SUM(tongTienBanDuoc) AS tongTien
FROM [dbo].[CaLamViecNhanVien]
GROUP BY YEAR(ngay), MONTH(ngay), day(ngay)
order by nam desc, thang desc, day(ngay) desc

select trangThai, count(*) as soVe from [dbo].[Ve]
where trangThai = N'Đã hoàn thành' or trangThai = N'Đã hủy'
group by trangThai
order by trangThai


select g.maLoaiGhe, sum(ve.giaVe) as giaVe
from [dbo].[Ve] ve join [dbo].[Ghe] g 
on ve.[maGhe] = g.[maGhe]
group by g.maLoaiGhe
order by g.maLoaiGhe desc

select tgdc.maGaDi as gaDi, tgdc.maGaDen as gaDen, sum(cd.soGheDaDat) as tongSoVeDaDat from [dbo].[ChuyenDi] cd
join [dbo].[ThoiGianDiChuyen] tgdc on cd.maThoiGianDiChuyen = tgdc.maThoiGianDiChuyen
group by tgdc.maGaDi, tgdc.maGaDen 
order by tgdc.maGaDi, tgdc.maGaDen 

SELECT  TOP 4
    tgdc.maGaDi, 
    g1.tenGa AS tenGaDi, 
    tgdc.maGaDen, 
    g2.tenGa AS tenGaDen, 
    SUM(cd.soGheDaDat) AS tongSoVeDaDat 
FROM [dbo].[ChuyenDi] cd
JOIN [dbo].[ThoiGianDiChuyen] tgdc ON cd.maThoiGianDiChuyen = tgdc.maThoiGianDiChuyen
JOIN [dbo].[Ga] g1 ON tgdc.maGaDi = g1.maGa
JOIN [dbo].[Ga] g2 ON tgdc.maGaDen = g2.maGa
GROUP BY 
    tgdc.maGaDi, 
    g1.tenGa, 
    tgdc.maGaDen, 
    g2.tenGa
order by tongSoVeDaDat desc

select * from [dbo].[NhanVien]
where [maNV] = 'NV-0-04-938-227'

SELECT top 1 * FROM TaiKhoan WHERE tenDangNhap = '0328546227'

SELECT nv.[maNV], nv.[tenNV], nv.[gioiTinh], nv.[ngaySinh], nv.[email], 
       nv.[soDienThoai], nv.[cccd], nv.[ngayBatDauLamViec], nv.[vaiTro], nv.[trangThai]
FROM NhanVien nv
JOIN TaiKhoan tk ON nv.[maNV] = tk.[maNV]
WHERE tk.[tenDangNhap] = '0328546227'

select [maThoiGianDiChuyen], [thoiGianKhoiHanh], count(*) from [dbo].[ChuyenDi]
group by [maThoiGianDiChuyen], [thoiGianKhoiHanh]
go

SELECT TOP 4
    tgdc.maGaDi AS tenGaDi, 
    tgdc.maGaDen AS tenGaDen, 
    SUM(cd.soGheDaDat) AS tongSoVeDaDat 
FROM [dbo].[ChuyenDi] cd
JOIN [dbo].[ThoiGianDiChuyen] tgdc ON cd.maThoiGianDiChuyen = tgdc.maThoiGianDiChuyen
GROUP BY 
    tgdc.maGaDi, 
    tgdc.maGaDen
ORDER BY tongSoVeDaDat DESC;

select * from [dbo].[Ga]
where [maGa] = 'GA-007'

SELECT top 5 dsca.maNV, nv.tenNV as tenNhanVien, SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio 
FROM [dbo].[CaLamViecNhanVien] dsca 
JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV
WHERE dsca.ngay >= CAST(DATEADD(DAY, -7, GETDATE()) AS DATE)
  AND dsca.ngay <= CAST(GETDATE() AS DATE)
GROUP BY dsca.maNV, nv.tenNV
order by soGio desc

SELECT top 5 dsca.maNV, nv.tenNV as tenNhanVien, SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio 
FROM [dbo].[CaLamViecNhanVien] dsca 
JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV
WHERE dsca.ngay >= CAST(DATEADD(MONTH, -1, GETDATE()) AS DATE)
  AND dsca.ngay <= CAST(GETDATE() AS DATE)
GROUP BY dsca.maNV, nv.tenNV
order by soGio desc

SELECT top 5 dsca.maNV, nv.tenNV as tenNhanVien, SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio 
FROM [dbo].[CaLamViecNhanVien] dsca 
JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV
WHERE dsca.ngay >= CAST(DATEADD(YEAR, -1, GETDATE()) AS DATE)
  AND dsca.ngay <= CAST(GETDATE() AS DATE)
GROUP BY dsca.maNV, nv.tenNV
order by soGio desc


SELECT top 5 dsca.maNV, nv.tenNV as tenNhanVien, SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio 
FROM [dbo].[CaLamViecNhanVien] dsca 
JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV
WHERE dsca.ngay >= CAST(DATEADD(YEAR, -1, GETDATE()) AS DATE)
  AND dsca.ngay <= CAST(GETDATE() AS DATE)
GROUP BY dsca.maNV, nv.tenNV
order by soGio desc

SELECT TOP 5 
    dsca.maNV, 
    nv.tenNV AS tenNhanVien, 
	dsca.tongVeBan,
    SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio,
    CASE 
        WHEN SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 = 0 
        THEN 0 
        ELSE SUM(dsca.tongVeBan) / (SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0) 
    END AS soVeTrungBinhMoiGio
FROM [dbo].[CaLamViecNhanVien] dsca 
JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV
WHERE dsca.ngay >= CAST(DATEADD(YEAR, -1, GETDATE()) AS DATE)
  AND dsca.ngay <= CAST(GETDATE() AS DATE)
GROUP BY dsca.maNV, nv.tenNV, dsca.tongVeBan
ORDER BY soVeTrungBinhMoiGio DESC


WITH EmployeeProductivity AS (
    SELECT 
        dsca.maNV,
        nv.tenNV,
        SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 AS soGio,
        CASE 
            WHEN SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0 = 0 
            THEN 0 
            ELSE SUM(dsca.tongVeBan) / (SUM(DATEDIFF(MINUTE, ca.thoiGianBatDau, ca.thoiGianKetThuc)) / 60.0) 
        END AS soVeTrungBinhMoiGio
    FROM [dbo].[CaLamViecNhanVien] dsca 
    JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
    JOIN [dbo].[NhanVien] nv ON dsca.maNV = nv.maNV
    GROUP BY dsca.maNV, nv.tenNV
),
CategorizedEmployees AS (
    SELECT 
        CASE 
            WHEN soVeTrungBinhMoiGio > 50 THEN 'Nhom Nang Suat Cao'
            WHEN soVeTrungBinhMoiGio >= 25 THEN 'Nhom Nang Suat Trung Binh'
            ELSE 'Nhom Nang Suat Thap'
        END AS NhomNangSuat,
        maNV,
        tenNV,
        soVeTrungBinhMoiGio
    FROM EmployeeProductivity
)
SELECT 
    NhomNangSuat,
    COUNT(*) AS SoNhanVien
FROM CategorizedEmployees
GROUP BY NhomNangSuat
ORDER BY 
    CASE 
        WHEN NhomNangSuat = 'Nhom Nang Suat Cao' THEN 1
        WHEN NhomNangSuat = 'Nhom Nang Suat Trung Binh' THEN 2
        ELSE 3
    END;
go

select top 5 nv.tenNV as tenNhanVien, sum(dsca.tongVeBan) as tongVeBan
from [dbo].[CaLamViecNhanVien] dsca join [dbo].[NhanVien] nv on nv.maNV = dsca.maNV
group by nv.tenNV
order by tongVeBan desc

SELECT *, CAST(GETDATE() AS DATE) as today
FROM [dbo].[CaLamViecNhanVien] dsca 
JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
WHERE 
    dsca.ngay = CAST('2025-05-24' AS DATE)
ORDER BY dsca.ngay;


SELECT *, CAST(GETDATE() AS DATE) as today
FROM [dbo].[CaLamViecNhanVien] dsca 
JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
WHERE 
    dsca.ngay = CAST(GETDATE() AS DATE)
    AND (
        -- Case 1: Shift does not cross midnight (start time <= end time)
        (ca.thoiGianBatDau <= ca.thoiGianKetThuc 
         AND CAST(GETDATE() AS TIME) BETWEEN ca.thoiGianBatDau AND ca.thoiGianKetThuc)
    )
ORDER BY dsca.ngay;

select CAST(GETDATE() AS DATE) as today


SELECT 
	nv.maNV,
    nv.tenNV,
	nv.vaiTro,
	dsca.maCa,
	dsca.ngay,
    COALESCE(dsca.tongVeBan, 0) AS soVeBanTrongCa,
    COALESCE(dsca.tongTienBanDuoc, 0) AS soTienBanTrongCa,
    (SELECT SUM(tongVeBan) 
     FROM [dbo].[CaLamViecNhanVien]) AS tongSoVeHienTai,
    (SELECT SUM(tongTienBanDuoc) 
     FROM [dbo].[CaLamViecNhanVien]) AS tongTienHienTai
FROM [dbo].[CaLamViecNhanVien] dsca 
JOIN [dbo].[Ca] ca ON ca.maCa = dsca.maCa
join [dbo].[NhanVien] nv on nv.maNV = dsca.maNV
WHERE 
    dsca.ngay = CAST('2025-05-26' AS DATE)
    AND (
        -- Case 1: Shift does not cross midnight (start time <= end time)
        (ca.thoiGianBatDau <= ca.thoiGianKetThuc 
         AND CAST('07:30:00' AS TIME) BETWEEN ca.thoiGianBatDau AND ca.thoiGianKetThuc)
    )
GROUP BY nv.maNV, nv.tenNV, nv.vaiTro, dsca.maCa, dsca.ngay, dsca.tongVeBan, dsca.tongTienBanDuoc

update [dbo].[NhanVien]
set vaiTro = 'Nhân viên bán vé'
where maNV = 'NV-0-04-939-141'

select * from [dbo].[NhanVien]
where maNV = 'NV-0-04-939-141'