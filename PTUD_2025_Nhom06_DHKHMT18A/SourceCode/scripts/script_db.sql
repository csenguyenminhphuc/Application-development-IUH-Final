CREATE DATABASE quan_ly_ban_ve_tau;
GO

USE quan_ly_ban_ve_tau;
GO

-- Tạo bảng NhanVien
CREATE TABLE NhanVien (
    maNV VARCHAR(15) NOT NULL PRIMARY KEY,
    tenNV NVARCHAR(100) NOT NULL,
	gioiTinh BIT NOT NULL, -- True: nữ, False: nam 
    ngaySinh DATE NOT NULL,
    email VARCHAR(50) NOT NULL,
    soDienThoai VARCHAR(10) NOT NULL,
	cccd VARCHAR(12) NOT NULL unique,
	ngayBatDauLamViec DATE NOT NULL,
	vaiTro NVARCHAR(20) NOT NULL,
	trangThai NVARCHAR(13) NOT NULL,
	 -- Ràng buộc định dạng cho maNV: NV-[0-1]-YY-ZZZ-OOO
    CONSTRAINT CK_NhanVien_maNV_Format 
        CHECK (maNV LIKE 'NV-[0-1]-[0-9][0-9]-[0-9][0-9][0-9]-[0-9][0-9][0-9]'), 
	-- Ràng buộc ngày sinh: nhân viên phải đủ 18 tuổi (sử dụng DATEDIFF để tính tuổi)
    CONSTRAINT CK_NhanVien_ngaySinh 
        CHECK (DATEDIFF(YEAR, ngaySinh, GETDATE()) >= 18),
    
    -- Ràng buộc email: phải chứa ký tự @ và dấu chấm (không thay thế cho việc kiểm tra email hoàn chỉnh)
    CONSTRAINT CK_NhanVien_email 
        CHECK (email LIKE '%@%.%'),

    -- Ràng buộc số điện thoại: đúng 10 số và chỉ chứa số
    CONSTRAINT CK_NhanVien_soDienThoai 
        CHECK (LEN(soDienThoai) = 10 AND soDienThoai NOT LIKE '%[^0-9]%'),
    
    -- Ràng buộc CCCD: đúng 12 số và chỉ chứa số
    CONSTRAINT CK_NhanVien_cccd 
        CHECK (LEN(cccd) = 12 AND cccd NOT LIKE '%[^0-9]%'),
    
    -- Ràng buộc ngày bắt đầu làm việc: phải bé hơn ngày hiện tại
    CONSTRAINT CK_NhanVien_ngayBatDauLamViec 
        CHECK (ngayBatDauLamViec IS NULL OR ngayBatDauLamViec < CAST(GETDATE() AS DATE)),
    
    -- Ràng buộc vai trò: chỉ nhận “Nhân viên bán vé” hoặc “Nhân viên quản lý”
    CONSTRAINT CK_NhanVien_vaiTro 
        CHECK (vaiTro IN (N'Nhân viên bán vé', N'Nhân viên quản lý')),
    
    -- Ràng buộc trạng thái: chỉ nhận “Đang làm” hoặc “Đã nghỉ làm”
    CONSTRAINT CK_NhanVien_trangThai 
        CHECK (trangThai IN (N'Đang làm', N'Đã nghỉ làm')),
);


-- Tạo bảng Ca
CREATE TABLE Ca (
    maCa NVARCHAR(5) PRIMARY KEY NOT NULL,
	thoiGianBatDau TIME NOT NULL,
    thoiGianKetThuc TIME NOT NULL,

	-- Ràng buộc định dạng cho maCa theo mẫu: CA-XX
    CONSTRAINT CK_Ca_maCa_Format 
        CHECK (maCa LIKE 'CA-[0-9][0-9]'),
    
    -- Ràng buộc: thời gian kết thúc ca phải sau thời gian bắt đầu ca
    CONSTRAINT CK_Ca_ThoiGian 
        CHECK (thoiGianKetThuc > thoiGianBatDau)
);

CREATE TABLE CaLamViecNhanVien (
    maNV VARCHAR(15) NOT NULL,
    maCa NVARCHAR(5) NOT NULL,
    tongVeBan INT,
    tongTienBanDuoc FLOAT, -- Thuộc tính dẫn xuất
    ngay DATE NOT NULL,

    -- Khóa chính là sự kết hợp của maNV và maCa
    CONSTRAINT PK_NhanVien_Ca PRIMARY KEY (maNV, maCa, ngay),
    
    -- Khóa ngoại liên kết với bảng NhanVien
    CONSTRAINT FK_NhanVien_Ca_NhanVien 
        FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),

    -- Khóa ngoại liên kết với bảng Ca
    CONSTRAINT FK_NhanVien_Ca_Ca 
        FOREIGN KEY (maCa) REFERENCES Ca(maCa)
);

-- Tạo bảng TaiKhoan
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(10) NOT NULL PRIMARY KEY, -- Số điện thoại của nhân viên, đúng 10 số
    matKhau NVARCHAR(255) NOT NULL, 
    maNV VARCHAR(15) NOT NULL UNIQUE, -- Khóa ngoại liên kết với bảng NhanVien
    
    -- Ràng buộc cho tenDangNhap: phải chứa đúng 10 ký tự số
    CONSTRAINT CK_TaiKhoan_tenDangNhap CHECK (
        LEN(tenDangNhap) = 10 AND tenDangNhap NOT LIKE '%[^0-9]%'
    ),
    
    -- Ràng buộc cho matKhau:
    -- - Ít nhất 8 ký tự
    -- - Chứa ít nhất 1 ký tự số
    -- - Chứa ít nhất 1 ký tự in hoa
    -- - Chứa ít nhất 1 ký tự in thường
    -- - Chứa ít nhất 1 ký tự đặc biệt (khác chữ và số)
    /*CONSTRAINT CK_TaiKhoan_MatKhau CHECK (
        LEN(matKhau) >= 8
        AND matKhau LIKE '%[0-9]%'
        AND matKhau LIKE '%[A-Z]%'
        AND matKhau LIKE '%[a-z]%'
        AND matKhau LIKE '%[^a-zA-Z0-9]%'
    ),*/
    
    -- Khóa ngoại liên kết với bảng NhanVien
    CONSTRAINT FK_TaiKhoan_NhanVien 
		FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);


-- Tạo bảng KhachHang
CREATE TABLE KhachHang (
    maKH VARCHAR(17) NOT NULL PRIMARY KEY,  -- Định dạng: KH-YYY-DDMMYY-XXX
    tenKH NVARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(10) NOT NULL,
    cccd VARCHAR(12) NOT NULL unique,
    
    -- Ràng buộc định dạng cho maKH theo mẫu: KH-YYY-DDMMYY-XXX
    CONSTRAINT CK_KhachHang_maKH_Format 
        CHECK (maKH LIKE 'KH-[0-9][0-9][0-9]-[0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9]'),
    
    -- Ràng buộc cho số điện thoại: đúng 10 số
    CONSTRAINT CK_KhachHang_soDienThoai_Format 
        CHECK (LEN(soDienThoai) = 10 AND soDienThoai NOT LIKE '%[^0-9]%'),
    
    -- Ràng buộc cho CCCD: đúng 12 số
    CONSTRAINT CK_KhachHang_cccd_Format 
        CHECK (LEN(cccd) = 12 AND cccd NOT LIKE '%[^0-9]%')
);


CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(15) NOT NULL PRIMARY KEY,  -- Định dạng: KM-DDMMYYYY-XXX
    tenKhuyenMai NVARCHAR(100) NOT NULL,
    heSoKhuyenMai FLOAT NOT NULL,
    ngayBatDau DATETIME NOT NULL,
    ngayKetThuc DATETIME NOT NULL,
    tongTienToiThieu FLOAT NOT NULL,
    tienKhuyenMaiToiDa FLOAT NOT NULL,
    
    -- Ràng buộc định dạng cho maKhuyenMai theo mẫu: KM-DDMMYYYY-XXX
    CONSTRAINT CK_KhuyenMai_maKhuyenMai_Format 
        CHECK (maKhuyenMai LIKE 'KM-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9]'),
    
    -- Ràng buộc cho heSoKhuyenMai: phải nằm trong khoảng từ 0.0 đến 1.0
    CONSTRAINT CK_KhuyenMai_heSoKhuyenMai 
        CHECK (heSoKhuyenMai >= 0.0 AND heSoKhuyenMai <= 1.0),
    
    -- Ràng buộc: ngày bắt đầu phải nhỏ hơn ngày kết thúc
    CONSTRAINT CK_KhuyenMai_Ngay 
        CHECK (ngayBatDau < ngayKetThuc),
    
    -- Ràng buộc: tổng tiền tối thiểu phải lớn hơn 0
    CONSTRAINT CK_KhuyenMai_TongTienToiThieu 
        CHECK (tongTienToiThieu > 0)
);

ALTER TABLE KhuyenMai
ADD trangThai NVARCHAR(20)
    CONSTRAINT CK_KhuyenMai_TrangThai 
    CHECK (trangThai IN (N'Còn hiệu lực', N'Đã hết hạn'));


-- Tạo bảng Ga
CREATE TABLE Ga (
    maGa VARCHAR(10) NOT NULL PRIMARY KEY,  -- Định dạng: GA-{XXX}; Mã ga được sinh tự động
    tenGa NVARCHAR(100) NOT NULL,             -- Tên của nhà ga; không được rỗng
    diaChi NVARCHAR(100) NOT NULL,            -- Địa chỉ chi tiết của nhà ga; không được rỗng
    soDienThoai VARCHAR(11) NOT NULL,         -- Số điện thoại liên hệ; chỉ chứa số và độ dài 10 hoặc 11 ký tự

    -- Ràng buộc định dạng cho maGa theo mẫu: GA-{XXX} với {XXX} là 3 chữ số
    CONSTRAINT CK_Ga_maGa_Format CHECK (maGa LIKE 'GA-[0-9][0-9][0-9]'),
    
    -- Ràng buộc cho soDienThoai: chỉ chứa số và độ dài từ 10 đến 11 ký tự
    CONSTRAINT CK_Ga_soDienThoai_Format CHECK (
        (LEN(soDienThoai) = 10 OR LEN(soDienThoai) = 11)
        AND soDienThoai NOT LIKE '%[^0-9]%'
    )
);


-- Tạo bảng ThoiGianDiChuyen
CREATE TABLE ThoiGianDiChuyen (
    maThoiGianDiChuyen VARCHAR(25) NOT NULL PRIMARY KEY,  -- Định dạng: TGDC-{maGaDi}-{maGaDen}
    maGaDi VARCHAR(10) NOT NULL,   -- Mã nhà ga xuất phát, khóa ngoại tham chiếu bảng Ga
    maGaDen VARCHAR(10) NOT NULL,  -- Mã nhà ga đến, khóa ngoại tham chiếu bảng Ga
    thoiGianDiChuyen INT NOT NULL, -- Tổng thời gian di chuyển tính bằng phút, > 0
    soKmDiChuyen FLOAT NOT NULL,   -- Tổng số km di chuyển, > 0
    soTienMotKm FLOAT NOT NULL,    -- Giá tiền trên một km, > 0

    -- Ràng buộc định dạng cho maThoiGianDiChuyen: phải có dạng 'TGDC-{maGaDi}-{maGaDen}'
    CONSTRAINT CK_ThoiGianDiChuyen_Format 
        CHECK (maThoiGianDiChuyen LIKE 'TGDC-%-%'),
    
    -- Ràng buộc cho thời gian di chuyển: phải lớn hơn 0
    CONSTRAINT CK_ThoiGianDiChuyen_ThoiGian 
        CHECK (thoiGianDiChuyen > 0),
    
    -- Ràng buộc cho số km di chuyển: phải lớn hơn 0
    CONSTRAINT CK_ThoiGianDiChuyen_Km 
        CHECK (soKmDiChuyen > 0),
    
    -- Ràng buộc cho giá tiền trên 1 km: phải lớn hơn 0
    CONSTRAINT CK_ThoiGianDiChuyen_Tien 
        CHECK (soTienMotKm > 0),
    
    -- Khóa ngoại tham chiếu bảng Ga cho mã ga xuất phát
    CONSTRAINT FK_ThoiGianDiChuyen_GaDi 
        FOREIGN KEY (maGaDi) REFERENCES Ga(maGa),
    
    -- Khóa ngoại tham chiếu bảng Ga cho mã ga đến
    CONSTRAINT FK_ThoiGianDiChuyen_GaDen 
        FOREIGN KEY (maGaDen) REFERENCES Ga(maGa)
);



-- Tạo bảng Tau
CREATE TABLE Tau (
    maTau VARCHAR(10) NOT NULL PRIMARY KEY,  -- Định dạng: TAU-{XXX}, ví dụ: TAU-001
    tenTau NVARCHAR(100) NOT NULL,           -- Tên của tàu, không được rỗng
    soToaTau INT NOT NULL,                   -- Tổng số toa tàu, phải lớn hơn 0

    -- Ràng buộc định dạng cho maTau theo mẫu: TAU-{XXX}
    CONSTRAINT CK_Tau_maTau_Format CHECK (maTau LIKE 'TAU-[0-9][0-9][0-9]'),

    -- Ràng buộc cho soToaTau: phải lớn hơn 0
    CONSTRAINT CK_Tau_soToaTau CHECK (soToaTau > 0)
);



-- Tạo bảng ChuyenDi
CREATE TABLE ChuyenDi (
    maChuyenDi NVARCHAR(20) NOT NULL PRIMARY KEY,  -- Định dạng: CD-MT-DDMMYYYY-XXX
    maThoiGianDiChuyen VARCHAR(25) NOT NULL,         -- Là khóa ngoại của bảng ThoiGianDiChuyen
    thoiGianKhoiHanh DATETIME NOT NULL,              -- Phải lớn hơn thời gian hiện tại
    thoiGianDenDuTinh DATETIME NOT NULL,             -- Phải lớn hơn thoiGianKhoiHanh
    maTau VARCHAR(10) NOT NULL,                       -- Là khóa ngoại của bảng Tau
    soGheDaDat INT,                          -- Không âm, tối thiểu là 0 (ràng buộc so với tổng số ghế được xử lý ở tầng ứng dụng)
    soGheConTrong INT,                       -- Không âm

    -- Khóa ngoại tham chiếu bảng ThoiGianDiChuyen
    CONSTRAINT FK_ChuyenDi_ThoiGianDiChuyen 
        FOREIGN KEY (maThoiGianDiChuyen) REFERENCES ThoiGianDiChuyen(maThoiGianDiChuyen),
    
    -- Khóa ngoại tham chiếu bảng Tau
    CONSTRAINT FK_ChuyenDi_Tau 
        FOREIGN KEY (maTau) REFERENCES Tau(maTau),
    
    -- Ràng buộc định dạng cho maChuyenDi:
    -- Format: CD-MT-DDMMYYYY-XXX
    -- + "CD-" là tiền tố cố định
    -- + "MT" là 3 ký tự (thường là 3 ký tự cuối của mã tàu, được sinh tự động qua trigger/stored procedure)
    -- + "DDMMYYYY" là ngày, tháng, năm khởi hành (8 chữ số)
    -- + "XXX" là số thứ tự trong ngày (3 chữ số)
    CONSTRAINT CK_ChuyenDi_Format CHECK (
        maChuyenDi LIKE 'CD-___-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9]'
    ),
    
    -- Ràng buộc thời gian đến dự tính phải lớn hơn thời gian khởi hành
    CONSTRAINT CK_ChuyenDi_ThoiGianDenDuTinh CHECK (thoiGianDenDuTinh > thoiGianKhoiHanh),
    
    -- Ràng buộc số ghế đã đặt không âm
    CONSTRAINT CK_ChuyenDi_soGheDaDat CHECK (soGheDaDat >= 0),
    
    -- Ràng buộc số ghế còn trống không âm
    CONSTRAINT CK_ChuyenDi_soGheConTrong CHECK (soGheConTrong >= 0)
);



-- Tạo bảng ToaTau
CREATE TABLE ToaTau (
    maToaTau VARCHAR(10) NOT NULL PRIMARY KEY,  -- Định dạng: TT-MT-OO, với TT là tiền tố, MT là 3 ký tự cuối của mã tàu, OO là số thứ tự của toa
    tenToaTau NVARCHAR(100) NOT NULL,           -- Tên toa tàu, không được rỗng và tối đa 100 ký tự
    soKhoangTau INT NOT NULL,                   -- Số khoang tàu, giá trị > 0
    maTau VARCHAR(10) NOT NULL,                 -- Khóa ngoại tham chiếu đến bảng Tau
    
    -- Ràng buộc định dạng cho maToaTau theo mẫu: TT-MT-OO
    -- TT: tiền tố cố định "TT-"
    -- MT: 3 ký tự (có thể là chữ và số, tùy theo quy ước)
    -- OO: 2 chữ số của số thứ tự toa
    CONSTRAINT CK_ToaTau_maToaTau_Format 
        CHECK (maToaTau LIKE 'TT-___-[0-9][0-9]'),
    
    -- Ràng buộc cho soKhoangTau: phải lớn hơn 0
    CONSTRAINT CK_ToaTau_soKhoangTau CHECK (soKhoangTau > 0),
    
    -- Khóa ngoại liên kết với bảng Tau
    CONSTRAINT FK_ToaTau_Tau FOREIGN KEY (maTau) REFERENCES Tau(maTau)
);


-- Tạo bảng KhoangTau
CREATE TABLE KhoangTau (
    maKhoangTau VARCHAR(13) NOT NULL PRIMARY KEY,  -- Định dạng: KT-MTT-OO
    tenKhoangTau NVARCHAR(100) NOT NULL,             -- Tên khoang tàu, không được rỗng, tối đa 100 ký tự
    soGhe INT NOT NULL,                             -- Số ghế, giá trị > 0
    maToaTau VARCHAR(10) NOT NULL,                  -- Khóa ngoại liên kết với bảng ToaTau
    
    -- Ràng buộc định dạng cho maKhoangTau theo mẫu: KT-MTT-OO
    -- + KT: tiền tố cố định của khoang tàu
    -- + MTT: mã toa tàu (giả định là 6 ký tự, có thể là kết quả của việc lấy 6 ký tự cuối của maToaTau)
    -- + OO: số thứ tự của khoang tàu, dưới dạng 2 chữ số
    CONSTRAINT CK_KhoangTau_Format CHECK (
        maKhoangTau LIKE 'KT-______-[0-9][0-9]'
    ),
    
    -- Ràng buộc cho soGhe: phải lớn hơn 0
    CONSTRAINT CK_KhoangTau_soGhe CHECK (soGhe > 0),
    
    -- Khóa ngoại liên kết với bảng ToaTau
    CONSTRAINT FK_KhoangTau_ToaTau FOREIGN KEY (maToaTau) REFERENCES ToaTau(maToaTau)
);


CREATE TABLE LoaiGhe (
    maLoaiGhe VARCHAR(12) NOT NULL PRIMARY KEY,  -- Được sinh tự động; chỉ nhận 3 giá trị: 'GHE_NGOI_MEM', 'GIUONG_NAM_4', 'GIUONG_NAM_6'
    tenLoaiGhe NVARCHAR(100) NOT NULL,            -- Tên loại ghế, không được rỗng
    moTa NVARCHAR(100) NOT NULL,                  -- Mô tả, không được rỗng
    heSoGhe FLOAT NOT NULL,                       -- Hệ số ghế, theo quy định: GHE_NGOI_MEM = 1.0, GIUONG_NAM_4 = 1.3, GIUONG_NAM_6 = 1.2

    -- Ràng buộc cho maLoaiGhe chỉ cho phép 3 giá trị đã định nghĩa
    CONSTRAINT CK_LoaiGhe_maLoaiGhe CHECK (
        maLoaiGhe IN ('GHE_NGOI_MEM', 'GIUONG_NAM_4', 'GIUONG_NAM_6')
    ),

    -- Ràng buộc cho heSoGhe: đảm bảo hệ số tương ứng với loại ghế
    CONSTRAINT CK_LoaiGhe_heSoGhe CHECK (
        (maLoaiGhe = 'GHE_NGOI_MEM' AND heSoGhe = 1.0)
        OR (maLoaiGhe = 'GIUONG_NAM_4' AND heSoGhe = 1.3)
        OR (maLoaiGhe = 'GIUONG_NAM_6' AND heSoGhe = 1.2)
    )
);


-- Tạo bảng Ghe
CREATE TABLE Ghe (
    maGhe VARCHAR(20) NOT NULL PRIMARY KEY,  -- Định dạng: G-MKT-OOO
    viTri VARCHAR(9) NOT NULL,                -- Định dạng: H_XX_G_YY (ví dụ: H_01_G_02)
    maKhoangTau VARCHAR(13) NOT NULL,           -- Khóa ngoại của bảng KhoangTau
    maLoaiGhe VARCHAR(12) NOT NULL,             -- Khóa ngoại của bảng LoaiGhe

    -- Ràng buộc định dạng cho maGhe:
    -- 'G-' là tiền tố cố định, theo sau là MKT (mã khoang tàu) và sau đó là 3 chữ số (số thứ tự ghế)
    CONSTRAINT CK_Ghe_maGhe_Format CHECK (
        maGhe LIKE 'G-%-[0-9][0-9][0-9][0-9]'
    ),
    
    -- Ràng buộc định dạng cho viTri:
    -- Định dạng phải đúng: H_XX_G_YY, trong đó các dấu gạch dưới (_) là ký tự cố định,
    -- X và Y là một chữ số (ví dụ: "H_1_G_2")
    CONSTRAINT CK_Ghe_viTri_Format CHECK (
        viTri LIKE 'H\_[0-9][0-9]\_G\_[0-9][0-9]' ESCAPE '\'
    ),
    
    -- Khóa ngoại liên kết với bảng KhoangTau
    CONSTRAINT FK_Ghe_KhoangTau FOREIGN KEY (maKhoangTau) REFERENCES KhoangTau(maKhoangTau),
    
    -- Khóa ngoại liên kết với bảng LoaiGhe
    CONSTRAINT FK_Ghe_LoaiGhe FOREIGN KEY (maLoaiGhe) REFERENCES LoaiGhe(maLoaiGhe)
);


-- Tạo bảng HanhKhach
CREATE TABLE HanhKhach (
    maHanhKhach VARCHAR(16) NOT NULL PRIMARY KEY,  -- Định dạng: HK-YY-DDMMYY-XXX
    tenHanhKhach NVARCHAR(100) NOT NULL,
    cccd VARCHAR(12) NOT NULL Unique,
    
    -- Ràng buộc cho cccd: phải đúng 12 ký tự số
    CONSTRAINT CK_HanhKhach_cccd CHECK (
        LEN(cccd) = 12 AND cccd NOT LIKE '%[^0-9]%'
    ),
    
    -- Ràng buộc định dạng cho maHanhKhach theo mẫu: HK-YY-DDMMYY-XXX
    CONSTRAINT CK_HanhKhach_maHanhKhach_Format CHECK (
         maHanhKhach LIKE 'HK-[0-9][0-9]-[0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9]'
    )
);

ALTER TABLE HanhKhach
ADD namSinh int not null


-- Tạo bảng HoaDon
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(22) NOT NULL PRIMARY KEY,  -- Format: HD-XXX-Z-DDMMYYYY-UUUU
    ngayLapHoaDon DATETIME NOT NULL,
    VAT FLOAT NOT NULL CONSTRAINT DF_HoaDon_VAT DEFAULT (0.1),  -- Thuế mặc định 10%
    soVe INT,
    tongTien FLOAT,
    maNV VARCHAR(15) NOT NULL,
    maKH VARCHAR(17) NOT NULL,       -- Khóa ngoại từ bảng KhachHang
    maKhuyenMai VARCHAR(15),  -- Khóa ngoại từ bảng KhuyenMai

    -- Ràng buộc định dạng cho maHoaDon theo mẫu: HD-XXX-Z-DDMMYYYY-UUUU
    --  + 'HD-' là tiền tố cố định.
    --  + XXX: 3 chữ số (số thứ tự của nhân viên)
    --  + Z: 1 chữ số (số thứ tự ca trong ngày)
    --  + DDMMYYYY: 8 chữ số (ngày lập hóa đơn)
    --  + UUUU: 4 chữ số (số thứ tự hóa đơn trong ca, reset khi sang ca mới)
    CONSTRAINT CK_HoaDon_maHoaDon_Format CHECK (
        maHoaDon LIKE 'HD-[0-9][0-9][0-9]-[0-9]-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'
    ),
    
    -- Ràng buộc VAT: chỉ cho phép giá trị 0.1 (10%)
    CONSTRAINT CK_HoaDon_VAT CHECK (VAT = 0.1),
    
    -- Khóa ngoại liên kết với bảng NhanVien
    CONSTRAINT FK_HoaDon_Ca FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    
    -- Khóa ngoại liên kết với bảng KhachHang
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    
    -- Khóa ngoại liên kết với bảng KhuyenMai
    CONSTRAINT FK_HoaDon_KhuyenMai FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai)
);

ALTER TABLE [dbo].[HoaDon]
ALTER COLUMN maKhuyenMai VARCHAR(15) NULL;


-- Tạo bảng LoaiVe
CREATE TABLE LoaiVe (
    maLoaiVe VARCHAR(7) NOT NULL PRIMARY KEY,  -- Chỉ nhận 3 giá trị: 'LV-TE', 'LV-HSSV', 'LV-NL'
    tenLoaiVe NVARCHAR(100) NOT NULL,           -- Tên loại vé tương ứng:
                                               -- 'LV-TE'       => 'Vé dành cho trẻ em'
                                               -- 'LV-HSSV'     => 'Vé dành cho học sinh, sinh viên'
                                               -- 'LV-NL'       => 'Vé dành cho người lớn'
    moTaLoaiVe NVARCHAR(1000) NULL,
    heSoLoaiVe FLOAT NOT NULL,
    
    -- Ràng buộc cho maLoaiVe: chỉ cho phép 3 giá trị đã định nghĩa
    CONSTRAINT CK_LoaiVe_maLoaiVe CHECK (
        maLoaiVe IN ('LV-TE', 'LV-HSSV', 'LV-NL')
    ),
    
    -- Ràng buộc cho tenLoaiVe đảm bảo tên đúng với mã loại vé
    CONSTRAINT CK_LoaiVe_tenLoaiVe CHECK (
        (maLoaiVe = 'LV-TE' AND tenLoaiVe = N'Vé dành cho trẻ em')
        OR (maLoaiVe = 'LV-HSSV' AND tenLoaiVe = N'Vé dành cho học sinh, sinh viên')
        OR (maLoaiVe = 'LV-NL' AND tenLoaiVe = N'Vé dành cho người lớn')
    ),
	-- Ràng buộc cho heSoLoaiVe: phải lớn hơn 0 và nhỏ hơn hoặc bằng 1
    CONSTRAINT CK_LoaiVe_heSoLoaiVe CHECK (
        heSoLoaiVe > 0 AND heSoLoaiVe <= 1
    )
);


-- Tạo bảng Ve
CREATE TABLE Ve (
    maVe VARCHAR(56) NOT NULL PRIMARY KEY,  
    -- Định dạng: V-{maChuyen}-{maGhe}-{DDMMYYYY}-XXX
    -- V: tiền tố của class Vé
    -- {maChuyen}: mã chuyến đi của hành khách (đã được sinh tự động)
    -- {maGhe}: mã ghế (đã được sinh tự động)
    -- DDMMYYYY: ngày lập vé (8 chữ số)
    -- XXX: số thứ tự trong ngày (3 chữ số, được tăng dần, qua ngày reset về 001)
    
    maLoaiVe VARCHAR(7) NOT NULL,     -- Khóa ngoại từ bảng LoaiVe
    maChuyenDi NVARCHAR(20) NOT NULL,    -- Khóa ngoại từ bảng ChuyenDi
    maHanhKhach VARCHAR(16) NOT NULL,   -- Khóa ngoại từ bảng HanhKhach
    maGhe VARCHAR(20) NOT NULL,         -- Khóa ngoại từ bảng Ghe
    maHoaDon VARCHAR(22) NOT NULL,      -- Khóa ngoại từ bảng HoaDon
    trangThai NVARCHAR(13) NOT NULL,    -- Chỉ nhận 1 trong 3 giá trị: {N'Đã hoàn thành', N'Đã đặt', N'Đã hủy'}
    giaVe money NOT NULL,               -- Giá vé, phải > 0

    -- Ràng buộc định dạng cho maVe theo mẫu: V-{maChuyen}-{maGhe}-{DDMMYYYY}-XXX
    CONSTRAINT CK_Ve_maVe_Format CHECK (
        maVe LIKE 'V-%-%-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-%[0-9][0-9][0-9]'
    ),

    -- Ràng buộc cho trangThai: chỉ cho phép 3 giá trị đã định nghĩa
    CONSTRAINT CK_Ve_trangThai CHECK (
        trangThai IN (N'Đã hoàn thành', N'Đã đặt', N'Đã hủy')
    ),

    -- Ràng buộc cho giaVe: phải lớn hơn 0
    CONSTRAINT CK_Ve_giaVe CHECK (giaVe > 0),

    -- Khóa ngoại liên kết với bảng HanhKhach
    CONSTRAINT FK_Ve_HanhKhach FOREIGN KEY (maHanhKhach) REFERENCES HanhKhach(maHanhKhach),
    
    -- Khóa ngoại liên kết với bảng LoaiVe
    CONSTRAINT FK_Ve_LoaiVe FOREIGN KEY (maLoaiVe) REFERENCES LoaiVe(maLoaiVe),
    
    -- Khóa ngoại liên kết với bảng ChuyenDi
    CONSTRAINT FK_Ve_ChuyenDi FOREIGN KEY (maChuyenDi) REFERENCES ChuyenDi(maChuyenDi),
    
    -- Khóa ngoại liên kết với bảng Ghe
    CONSTRAINT FK_Ve_Ghe FOREIGN KEY (maGhe) REFERENCES Ghe(maGhe),
    
    -- Khóa ngoại liên kết với bảng HoaDon
    CONSTRAINT FK_Ve_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon)
);

----------------------------------- INSERT DATA -------------------------------------------------
GO
CREATE PROCEDURE InsertNhanVien
    @tenNV NVARCHAR(100),
    @gioiTinh BIT,                  -- 0: Nam, 1: Nữ
    @ngaySinh DATE,
    @email VARCHAR(100),
    @soDienThoai VARCHAR(10),
    @cccd VARCHAR(12),
    @ngayBatDauLamViec DATE,
    @vaiTro NVARCHAR(20),            -- Chỉ cho phép: 'Nhân viên bán vé' hoặc 'Nhân viên quản lý'
    @trangThai NVARCHAR(13)          -- Chỉ cho phép: 'Đang làm' hoặc 'Đã nghỉ làm'
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra tuổi >= 18 (so sánh bằng cách thêm 18 năm vào ngày sinh)
    IF DATEADD(year, 18, @ngaySinh) > GETDATE()
    BEGIN
        RAISERROR('Nhân viên phải đủ 18 tuổi.', 16, 1);
        RETURN;
    END

    -- Kiểm tra ngày bắt đầu làm việc phải lớn hơn ngày hiện tại
    IF @ngayBatDauLamViec <= GETDATE()
    BEGIN
        RAISERROR('Ngày bắt đầu làm việc phải lớn hơn ngày hiện tại.', 16, 1);
        RETURN;
    END

    -- Kiểm tra định dạng email (cách kiểm tra đơn giản)
    IF @email NOT LIKE '%@%.%'
    BEGIN
        RAISERROR('Email không đúng định dạng.', 16, 1);
        RETURN;
    END

    -- Kiểm tra số điện thoại: phải có đúng 10 ký tự số
    IF LEN(@soDienThoai) <> 10 OR @soDienThoai LIKE '%[^0-9]%'
    BEGIN
        RAISERROR('Số điện thoại không đúng định dạng (phải có 10 số).', 16, 1);
        RETURN;
    END

    -- Kiểm tra CCCD: phải có đúng 12 ký tự số
    IF LEN(@cccd) <> 12 OR @cccd LIKE '%[^0-9]%'
    BEGIN
        RAISERROR('CCCD không đúng định dạng (phải có 12 số).', 16, 1);
        RETURN;
    END

    -- Kiểm tra vai trò
    IF @vaiTro NOT IN (N'Nhân viên bán vé', N'Nhân viên quản lý')
    BEGIN
        RAISERROR('Vai trò không hợp lệ.', 16, 1);
        RETURN;
    END

    -- Kiểm tra trạng thái
    IF @trangThai NOT IN (N'Đang làm', N'Đã nghỉ làm')
    BEGIN
        RAISERROR('Trạng thái không hợp lệ.', 16, 1);
        RETURN;
    END

    -- Sinh mã nhân viên theo định dạng: NV-X-YY-ZZZ-OOO
    DECLARE @prefix VARCHAR(3) = 'NV-';
    -- X: 0 nếu nam, 1 nếu nữ
    DECLARE @gioiTinhChar CHAR(1) = CASE WHEN @gioiTinh = 1 THEN '1' ELSE '0' END;
    -- YY: lấy 2 số cuối của năm sinh
    DECLARE @yy VARCHAR(2) = RIGHT(CAST(YEAR(@ngaySinh) AS VARCHAR(4)), 2);
    -- ZZZ: 3 số cuối của CCCD
    DECLARE @zzz VARCHAR(3) = RIGHT(@cccd, 3);

    -- Lấy số thứ tự hiện tại (OOO) của nhân viên, tăng dần
    DECLARE @maxSerial INT;
    SELECT @maxSerial = MAX(CAST(RIGHT(maNV, 3) AS INT)) FROM NhanVien;
    IF @maxSerial IS NULL SET @maxSerial = 0;
    DECLARE @ooo INT = @maxSerial + 1;
    DECLARE @oooStr VARCHAR(3) = RIGHT('000' + CAST(@ooo AS VARCHAR(3)), 3);

    DECLARE @maNV VARCHAR(15) = @prefix + @gioiTinhChar + '-' + @yy + '-' + @zzz + '-' + @oooStr;

    -- Chèn dữ liệu vào bảng NhanVien
    INSERT INTO NhanVien
    (
        maNV, 
        tenNV, 
        gioiTinh, 
        ngaySinh, 
        email, 
        soDienThoai, 
        cccd, 
        ngayBatDauLamViec, 
        vaiTro, 
        trangThai
    )
    VALUES
    (
        @maNV, 
        @tenNV, 
        @gioiTinh, 
        @ngaySinh, 
        @email, 
        @soDienThoai, 
        @cccd, 
        @ngayBatDauLamViec, 
        @vaiTro, 
        @trangThai
    );

    -- Trả về mã nhân viên mới được tạo
    SELECT @maNV AS NewEmployeeID;
END
GO


CREATE PROCEDURE InsertTaiKhoan
    @tenDangNhap NVARCHAR(10),
    @matKhau NVARCHAR(255),
    @maNV NVARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra số điện thoại: phải chứa đúng 10 chữ số
    IF LEN(@tenDangNhap) <> 10 OR @tenDangNhap LIKE '%[^0-9]%'
    BEGIN
        RAISERROR('Số điện thoại phải chứa đúng 10 chữ số.', 16, 1);
        RETURN;
    END

    -- Kiểm tra sự tồn tại của mã nhân viên trong bảng NhanVien
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV = @maNV)
    BEGIN
        RAISERROR('Mã nhân viên không tồn tại.', 16, 1);
        RETURN;
    END

    -- Chèn bản ghi mới vào bảng TaiKhoan
    INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNV)
    VALUES (@tenDangNhap, @matKhau, @maNV);

    -- Trả về số điện thoại (tenDangNhap) của tài khoản vừa được tạo
    SELECT @tenDangNhap AS NewTaiKhoan;
END;
GO


CREATE PROCEDURE InsertKhachHang
    @tenKH NVARCHAR(100),
    @soDienThoai VARCHAR(10),
    @cccd VARCHAR(12)
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra số điện thoại: phải có đúng 10 số và chỉ chứa số
    IF LEN(@soDienThoai) <> 10 OR @soDienThoai LIKE '%[^0-9]%'
    BEGIN
         RAISERROR('Số điện thoại phải chứa đúng 10 số.', 16, 1);
         RETURN;
    END

    -- Kiểm tra CCCD: phải có đúng 12 số và chỉ chứa số
    IF LEN(@cccd) <> 12 OR @cccd LIKE '%[^0-9]%'
    BEGIN
         RAISERROR('CCCD phải chứa đúng 12 số.', 16, 1);
         RETURN;
    END

    /*
      Sinh mã khách hàng theo định dạng: KH-YYY-DDMMYY-XXX
        - 'KH-' là tiền tố cố định.
        - YYY: lấy 3 số cuối của CCCD.
        - DDMMYY: ngày hiện tại theo định dạng ddMMyy.
        - XXX: số thứ tự khách hàng trong ngày (tăng dần theo số khách đã được tạo hôm đó).
    */
    DECLARE @prefix VARCHAR(3) = 'KH-';
    DECLARE @yyy VARCHAR(3) = RIGHT(@cccd, 3);
    DECLARE @dateStr VARCHAR(6) = FORMAT(GETDATE(), 'ddMMyy');  -- SQL Server 2012+ hỗ trợ hàm FORMAT

    -- Đếm số khách hàng đã có trong ngày hiện tại (dựa trên phần ngày của maKH: từ vị trí 8 với độ dài 6)
    DECLARE @count INT;
    SELECT @count = COUNT(*) 
    FROM KhachHang
    WHERE SUBSTRING(maKH, 8, 6) = @dateStr;

    DECLARE @seq INT = @count + 1;
    DECLARE @seqStr VARCHAR(3) = RIGHT('000' + CAST(@seq AS VARCHAR(3)), 3);

    DECLARE @maKH VARCHAR(17) = @prefix + @yyy + '-' + @dateStr + '-' + @seqStr;

    -- Chèn dữ liệu vào bảng KhachHang
    INSERT INTO KhachHang (maKH, tenKH, soDienThoai, cccd)
    VALUES (@maKH, @tenKH, @soDienThoai, @cccd);

    -- Trả về mã khách hàng mới được tạo
    SELECT @maKH AS NewKhachHangID;
END;
GO

CREATE PROCEDURE InsertKhuyenMai
    @tenKhuyenMai NVARCHAR(100),
    @heSoKhuyenMai FLOAT,
    @ngayBatDau DATETIME,
    @ngayKetThuc DATETIME,
    @tongTienToiThieu FLOAT,
    @tienKhuyenMaiToiDa FLOAT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra: ngày bắt đầu phải nhỏ hơn ngày kết thúc
    IF @ngayBatDau >= @ngayKetThuc
    BEGIN
        RAISERROR('Ngày bắt đầu khuyến mãi phải nhỏ hơn ngày kết thúc.', 16, 1);
        RETURN;
    END

    -- Kiểm tra: tổng tiền tối thiểu phải > 0
    IF @tongTienToiThieu <= 0
    BEGIN
        RAISERROR('Tổng tiền tối thiểu phải lớn hơn 0.', 16, 1);
        RETURN;
    END

    -- Kiểm tra: hệ số khuyến mãi phải nằm trong khoảng từ 0.0 đến 1.0
    IF @heSoKhuyenMai < 0.0 OR @heSoKhuyenMai > 1.0
    BEGIN
        RAISERROR('Hệ số khuyến mãi phải nằm trong khoảng từ 0.0 đến 1.0.', 16, 1);
        RETURN;
    END

    /* Sinh mã khuyến mãi theo định dạng: KM-DDMMYYYY-XXX
       - DDMMYYYY: lấy từ GETDATE() theo định dạng ddMMyyyy (ngày tạo khuyến mãi)
       - XXX: số thứ tự khuyến mãi được tạo trong ngày; nếu chưa có thì là 001, ngược lại tăng dần.
    */
    DECLARE @dateStr VARCHAR(8) = FORMAT(GETDATE(), 'ddMMyyyy');  -- SQL Server 2012+ hỗ trợ hàm FORMAT

    -- Đếm số khuyến mãi đã được tạo hôm nay (dựa trên phần DDMMYYYY của maKhuyenMai)
    DECLARE @count INT;
    SELECT @count = COUNT(*)
    FROM KhuyenMai
    WHERE SUBSTRING(maKhuyenMai, 4, 8) = @dateStr;

    DECLARE @seq INT = @count + 1;
    DECLARE @seqStr VARCHAR(3) = RIGHT('000' + CAST(@seq AS VARCHAR(3)), 3);

    DECLARE @maKhuyenMai VARCHAR(15) = 'KM-' + @dateStr + '-' + @seqStr;

    -- Chèn dữ liệu vào bảng KhuyenMai
    INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, heSoKhuyenMai, ngayBatDau, ngayKetThuc, tongTienToiThieu, tienKhuyenMaiToiDa)
    VALUES (@maKhuyenMai, @tenKhuyenMai, @heSoKhuyenMai, @ngayBatDau, @ngayKetThuc, @tongTienToiThieu, @tienKhuyenMaiToiDa);

    -- Trả về mã khuyến mãi mới được tạo
    SELECT @maKhuyenMai AS NewMaKhuyenMai;
END;
GO

CREATE PROCEDURE InsertGa
    @tenGa NVARCHAR(100),
    @diaChi NVARCHAR(100),
    @soDienThoai VARCHAR(11)
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra số điện thoại: phải có độ dài 10 hoặc 11 và chỉ chứa chữ số
    IF LEN(@soDienThoai) NOT IN (10, 11) OR @soDienThoai LIKE '%[^0-9]%'
    BEGIN
         RAISERROR('Số điện thoại phải có 10 hoặc 11 số và chỉ chứa chữ số.', 16, 1);
         RETURN;
    END

    -- Sinh mã ga theo định dạng: GA-{XXX}
    -- Đếm số ga hiện có để sinh số thứ tự cho ga mới
    DECLARE @count INT;
    SELECT @count = COUNT(*) FROM Ga;
    DECLARE @newNumber INT = @count + 1;
    DECLARE @numberStr VARCHAR(3) = RIGHT('000' + CAST(@newNumber AS VARCHAR(3)), 3);
    DECLARE @maGa NVARCHAR(10) = 'GA-' + @numberStr;

    -- Chèn dữ liệu mới vào bảng Ga
    INSERT INTO Ga (maGa, tenGa, diaChi, soDienThoai)
    VALUES (@maGa, @tenGa, @diaChi, @soDienThoai);

    -- Trả về mã ga mới được tạo
    SELECT @maGa AS NewMaGa;
END;
GO

CREATE PROCEDURE InsertThoiGianDiChuyen
    @maGaDi VARCHAR(10),
    @maGaDen VARCHAR(10),
    @thoiGianDiChuyen INT,
    @soKmDiChuyen FLOAT,
    @soTienMotKm FLOAT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra các giá trị số phải lớn hơn 0
    IF @thoiGianDiChuyen <= 0
    BEGIN
         RAISERROR('Thời gian di chuyển phải lớn hơn 0.', 16, 1);
         RETURN;
    END

    IF @soKmDiChuyen <= 0
    BEGIN
         RAISERROR('Số km di chuyển phải lớn hơn 0.', 16, 1);
         RETURN;
    END

    IF @soTienMotKm <= 0
    BEGIN
         RAISERROR('Giá tiền trên một km phải lớn hơn 0.', 16, 1);
         RETURN;
    END

    -- Kiểm tra sự tồn tại của mã nhà ga xuất phát (maGaDi) trong bảng Ga
    IF NOT EXISTS (SELECT 1 FROM Ga WHERE maGa = @maGaDi)
    BEGIN
         RAISERROR('Mã nhà ga xuất phát không tồn tại.', 16, 1);
         RETURN;
    END

    -- Kiểm tra sự tồn tại của mã nhà ga đến (maGaDen) trong bảng Ga
    IF NOT EXISTS (SELECT 1 FROM Ga WHERE maGa = @maGaDen)
    BEGIN
         RAISERROR('Mã nhà ga đến không tồn tại.', 16, 1);
         RETURN;
    END

    -- Sinh mã ThoiGianDiChuyen theo định dạng: TGDC-{maGaDi}-{maGaDen}
    DECLARE @maThoiGianDiChuyen VARCHAR(25) = 'TGDC-' + @maGaDi + '-' + @maGaDen;

    -- Chèn dữ liệu vào bảng ThoiGianDiChuyen
    INSERT INTO ThoiGianDiChuyen 
        (maThoiGianDiChuyen, maGaDi, maGaDen, thoiGianDiChuyen, soKmDiChuyen, soTienMotKm)
    VALUES
        (@maThoiGianDiChuyen, @maGaDi, @maGaDen, @thoiGianDiChuyen, @soKmDiChuyen, @soTienMotKm);

    -- Trả về mã ThoiGianDiChuyen mới được tạo
    SELECT @maThoiGianDiChuyen AS NewThoiGianDiChuyenID;
END;
GO

CREATE PROCEDURE InsertTau
    @tenTau NVARCHAR(100),
    @soToaTau INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra số toa tàu phải lớn hơn 0
    IF @soToaTau <= 0
    BEGIN
         RAISERROR('Số toa tàu phải lớn hơn 0.', 16, 1);
         RETURN;
    END

    -- Sinh mã tàu theo định dạng: TAU-{XXX}
    DECLARE @count INT;
    SELECT @count = COUNT(*) FROM Tau;
    
    DECLARE @newNumber INT = @count + 1;
    DECLARE @numberStr VARCHAR(3) = RIGHT('000' + CAST(@newNumber AS VARCHAR(3)), 3);
    DECLARE @maTau NVARCHAR(10) = 'TAU-' + @numberStr;

    -- Chèn bản ghi mới vào bảng Tau
    INSERT INTO Tau (maTau, tenTau, soToaTau)
    VALUES (@maTau, @tenTau, @soToaTau);

    -- Trả về mã tàu mới được tạo
    SELECT @maTau AS NewMaTau;
END;
GO


CREATE PROCEDURE InsertChuyenDi
    @maThoiGianDiChuyen VARCHAR(25),
    @thoiGianKhoiHanh DATETIME2,
    @thoiGianDenDuTinh DATETIME2,
    @maTau VARCHAR(10),
    @soGheDaDat INT,
    @soGheConTrong INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra thời gian khởi hành phải lớn hơn thời gian hiện tại
    IF @thoiGianKhoiHanh <= GETDATE()
    BEGIN
        RAISERROR('Thời gian khởi hành phải lớn hơn thời gian hiện tại.', 16, 1);
        RETURN;
    END

    -- Kiểm tra thời gian đến dự tính phải lớn hơn thời gian khởi hành
    IF @thoiGianDenDuTinh <= @thoiGianKhoiHanh
    BEGIN
        RAISERROR('Thời gian đến dự tính phải lớn hơn thời gian khởi hành.', 16, 1);
        RETURN;
    END

    -- Kiểm tra số ghế đã đặt và số ghế còn trống phải không âm
    IF @soGheDaDat < 0 OR @soGheConTrong < 0
    BEGIN
         RAISERROR('Số ghế đã đặt và số ghế còn trống phải không âm.', 16, 1);
         RETURN;
    END

    -- Kiểm tra sự tồn tại của mã thời gian di chuyển
    IF NOT EXISTS (SELECT 1 FROM ThoiGianDiChuyen WHERE maThoiGianDiChuyen = @maThoiGianDiChuyen)
    BEGIN
         RAISERROR('Mã thời gian di chuyển không tồn tại.', 16, 1);
         RETURN;
    END

    -- Kiểm tra sự tồn tại của mã tàu
    IF NOT EXISTS (SELECT 1 FROM Tau WHERE maTau = @maTau)
    BEGIN
         RAISERROR('Mã tàu không tồn tại.', 16, 1);
         RETURN;
    END

    -- Sinh mã chuyến đi theo định dạng: CD-MT-DDMMYYYY-XXX
    DECLARE @prefix VARCHAR(3) = 'CD-';
    -- MT: lấy 3 ký tự cuối của mã tàu
    DECLARE @mt VARCHAR(3) = RIGHT(@maTau, 3);
    -- DDMMYYYY: chuyển đổi ngày khởi hành theo định dạng ddMMyyyy
    DECLARE @dateStr VARCHAR(8) = FORMAT(@thoiGianKhoiHanh, 'ddMMyyyy');  -- SQL Server 2012+ cần hỗ trợ hàm FORMAT
    -- Tính số thứ tự chuyến đi trong ngày đối với tàu @maTau
    DECLARE @count INT;
    SELECT @count = COUNT(*)
    FROM ChuyenDi
    WHERE maTau = @maTau
      AND CONVERT(date, thoiGianKhoiHanh) = CONVERT(date, @thoiGianKhoiHanh);
    DECLARE @seq INT = @count + 1;
    DECLARE @seqStr VARCHAR(3) = RIGHT('000' + CAST(@seq AS VARCHAR(3)), 3);

    DECLARE @maChuyenDi NVARCHAR(20) = @prefix + @mt + '-' + @dateStr + '-' + @seqStr;

    -- Chèn bản ghi mới vào bảng ChuyenDi
    INSERT INTO ChuyenDi
    (
        maChuyenDi,
        maThoiGianDiChuyen,
        thoiGianKhoiHanh,
        thoiGianDenDuTinh,
        maTau,
        soGheDaDat,
        soGheConTrong
    )
    VALUES
    (
        @maChuyenDi,
        @maThoiGianDiChuyen,
        @thoiGianKhoiHanh,
        @thoiGianDenDuTinh,
        @maTau,
        @soGheDaDat,
        @soGheConTrong
    );

    -- Trả về mã chuyến đi mới được tạo
    SELECT @maChuyenDi AS NewChuyenDiID;
END;
GO


CREATE PROCEDURE InsertToaTau
    @tenToaTau NVARCHAR(100),
    @soKhoangTau INT,
    @maTau VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra: tên toa tàu không được rỗng
    IF (@tenToaTau IS NULL OR LTRIM(RTRIM(@tenToaTau)) = '')
    BEGIN
        RAISERROR('Tên toa tàu không được rỗng.', 16, 1);
        RETURN;
    END

    -- Kiểm tra: số khoang tàu phải lớn hơn 0
    IF @soKhoangTau <= 0
    BEGIN
         RAISERROR('Số khoang tàu phải lớn hơn 0.', 16, 1);
         RETURN;
    END

    -- Kiểm tra sự tồn tại của mã tàu trong bảng Tau
    IF NOT EXISTS (SELECT 1 FROM Tau WHERE maTau = @maTau)
    BEGIN
         RAISERROR('Mã tàu không tồn tại.', 16, 1);
         RETURN;
    END

    /*
      Sinh mã toa tàu theo định dạng: TT-MT-OO
        - TT-: tiền tố cố định.
        - MT: 3 ký tự cuối của @maTau.
        - OO: số thứ tự của toa tàu của @maTau (định dạng 2 chữ số, padding số 0 nếu cần).
    */
    DECLARE @prefix VARCHAR(3) = 'TT-';
    DECLARE @mt VARCHAR(3) = RIGHT(@maTau, 3);

    -- Đếm số toa tàu đã có của tàu @maTau
    DECLARE @count INT;
    SELECT @count = COUNT(*) FROM ToaTau WHERE maTau = @maTau;
    DECLARE @seq INT = @count + 1;
    DECLARE @seqStr VARCHAR(2) = RIGHT('00' + CAST(@seq AS VARCHAR(2)), 2);

    DECLARE @maToaTau VARCHAR(10) = @prefix + @mt + '-' + @seqStr;

    -- Chèn bản ghi mới vào bảng ToaTau
    INSERT INTO ToaTau (maToaTau, tenToaTau, soKhoangTau, maTau)
    VALUES (@maToaTau, @tenToaTau, @soKhoangTau, @maTau);

    -- Trả về mã toa tàu mới được tạo
    SELECT @maToaTau AS NewMaToaTau;
END;
GO


CREATE PROCEDURE InsertKhoangTau
    @tenKhoangTau NVARCHAR(100),
    @soGhe INT,
    @maToaTau VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra: tên khoang tàu không được rỗng
    IF (LTRIM(RTRIM(@tenKhoangTau)) = '')
    BEGIN
         RAISERROR('Tên khoang tàu không được rỗng.', 16, 1);
         RETURN;
    END

    -- Kiểm tra: số ghế phải lớn hơn 0
    IF @soGhe <= 0
    BEGIN
         RAISERROR('Số ghế phải lớn hơn 0.', 16, 1);
         RETURN;
    END

    -- Kiểm tra sự tồn tại của mã toa tàu trong bảng ToaTau
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToaTau = @maToaTau)
    BEGIN
         RAISERROR('Mã toa tàu không tồn tại.', 16, 1);
         RETURN;
    END

    /*
       Sinh mã khoang tàu theo định dạng: KT-MTT-OO
       - KT-: tiền tố cố định.
       - MTT: lấy 3 ký tự từ @maToaTau, giả sử @maToaTau có định dạng "TT-XXX-OO".
         => Lấy từ vị trí 4 đến 6 của @maToaTau (ví dụ: nếu @maToaTau = 'TT-001-01', thì MTT = '001').
       - OO: số thứ tự của khoang tàu đối với toa tàu đó, được định dạng thành chuỗi 2 ký tự (padding số 0 nếu cần).
    */
    DECLARE @mt VARCHAR(3) = SUBSTRING(@maToaTau, 4, 3);

    -- Đếm số khoang tàu đã có của toa tàu @maToaTau
    DECLARE @count INT;
    SELECT @count = COUNT(*) FROM KhoangTau WHERE maToaTau = @maToaTau;

    DECLARE @seq INT = @count + 1;
    DECLARE @seqStr VARCHAR(2) = RIGHT('00' + CAST(@seq AS VARCHAR(2)), 2);

    DECLARE @maKhoangTau VARCHAR(10) = 'KT-' + @mt + '-' + @seqStr;

    -- Chèn bản ghi mới vào bảng KhoangTau
    INSERT INTO KhoangTau (maKhoangTau, tenKhoangTau, soGhe, maToaTau)
    VALUES (@maKhoangTau, @tenKhoangTau, @soGhe, @maToaTau);

    -- Trả về mã khoang tàu mới được tạo
    SELECT @maKhoangTau AS NewKhoangTauID;
END;
GO


CREATE PROCEDURE InsertLoaiGhe
    @Loai NVARCHAR(12),          -- Chỉ cho phép: 'GHE_NGOI_MEM', 'GIUONG_NAM_4', 'GIUONG_NAM_6'
    @tenLoaiGhe NVARCHAR(100),     -- Tên loại ghế
    @moTa NVARCHAR(100)            -- Mô tả
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra xem giá trị loại ghế có hợp lệ không
    IF @Loai NOT IN ('GHE_NGOI_MEM', 'GIUONG_NAM_4', 'GIUONG_NAM_6')
    BEGIN
        RAISERROR('Loại ghế không hợp lệ. Chỉ chấp nhận: GHE_NGOI_MEM, GIUONG_NAM_4, GIUONG_NAM_6.', 16, 1);
        RETURN;
    END

    -- Kiểm tra tên và mô tả không được rỗng
    IF LTRIM(RTRIM(@tenLoaiGhe)) = ''
    BEGIN
         RAISERROR('Tên loại ghế không được rỗng.', 16, 1);
         RETURN;
    END

    IF LTRIM(RTRIM(@moTa)) = ''
    BEGIN
         RAISERROR('Mô tả không được rỗng.', 16, 1);
         RETURN;
    END

    -- Tính hệ số ghế tự động dựa vào loại ghế
    DECLARE @heSoGhe FLOAT;
    SET @heSoGhe = CASE @Loai 
                     WHEN 'GHE_NGOI_MEM' THEN 1.0
                     WHEN 'GIUONG_NAM_4' THEN 1.3
                     WHEN 'GIUONG_NAM_6' THEN 1.2
                     ELSE NULL
                  END;

    -- Chèn bản ghi mới vào bảng LoaiGhe
    INSERT INTO LoaiGhe (maLoaiGhe, tenLoaiGhe, moTa, heSoGhe)
    VALUES (@Loai, @tenLoaiGhe, @moTa, @heSoGhe);

    -- Trả về mã loại ghế vừa được chèn (chính là giá trị @Loai)
    SELECT @Loai AS NewLoaiGheID;
END;
GO

CREATE PROCEDURE InsertGhe
    @viTri VARCHAR(9),
    @maKhoangTau VARCHAR(10),
    @maLoaiGhe VARCHAR(12)
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra: vị trí ghế không được rỗng
    IF LTRIM(RTRIM(@viTri)) = ''
    BEGIN
        RAISERROR('Vị trí ghế không được rỗng.', 16, 1);
        RETURN;
    END

    -- Kiểm tra định dạng vị trí ghế phải có 9 ký tự và khớp với mẫu 'H_??_G_??'
    IF LEN(@viTri) <> 9 OR @viTri NOT LIKE 'H_[0-9][0-9]_G_[0-9][0-9]'
    BEGIN
        RAISERROR('Vị trí ghế phải có định dạng H_XX_G_YY, ví dụ: H_01_G_02.', 16, 1);
        RETURN;
    END

    -- Kiểm tra sự tồn tại của mã khoang tàu
    IF NOT EXISTS (SELECT 1 FROM KhoangTau WHERE maToaTau = @maKhoangTau)
    BEGIN
        RAISERROR('Mã khoang tàu không tồn tại.', 16, 1);
        RETURN;
    END

    -- Kiểm tra sự tồn tại của mã loại ghế
    IF NOT EXISTS (SELECT 1 FROM LoaiGhe WHERE maLoaiGhe = @maLoaiGhe)
    BEGIN
        RAISERROR('Mã loại ghế không tồn tại.', 16, 1);
        RETURN;
    END

    /*
      Sinh mã ghế theo định dạng: G-{maKhoangTau}-{OOO}
        - "G-" là tiền tố cố định.
        - {maKhoangTau} được sử dụng nguyên vẹn.
        - OOO: số thứ tự của ghế trong khoang tàu đó, được định dạng thành chuỗi 3 chữ số (vd: 001, 002,...)
    */
    DECLARE @count INT;
    SELECT @count = COUNT(*) 
    FROM Ghe 
    WHERE maKhoangTau = @maKhoangTau;

    DECLARE @seq INT = @count + 1;
    DECLARE @seqStr VARCHAR(3) = RIGHT('000' + CAST(@seq AS VARCHAR(3)), 3);
    DECLARE @maGhe VARCHAR(20) = 'G-' + @maKhoangTau + '-' + @seqStr;

    -- Chèn bản ghi mới vào bảng Ghe
    INSERT INTO Ghe (maGhe, viTri, maKhoangTau, maLoaiGhe)
    VALUES (@maGhe, @viTri, @maKhoangTau, @maLoaiGhe);

    -- Trả về mã ghế mới được tạo
    SELECT @maGhe AS NewMaGhe;
END;
GO


CREATE PROCEDURE InsertHanhKhach
    @tenHanhKhach NVARCHAR(100),
    @cccd VARCHAR(12)
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Kiểm tra: CCCD phải chứa đúng 12 số
    IF LEN(@cccd) <> 12 OR @cccd LIKE '%[^0-9]%'
    BEGIN
         RAISERROR('CCCD phải chứa đúng 12 số.', 16, 1);
         RETURN;
    END

    -- Kiểm tra: Tên hành khách không được rỗng
    IF LTRIM(RTRIM(@tenHanhKhach)) = ''
    BEGIN
         RAISERROR('Tên hành khách không được rỗng.', 16, 1);
         RETURN;
    END

    /*
      Sinh maHanhKhach theo định dạng: HK-YY-DDMMYYYY-XXX
        - YY: lấy 2 số cuối của CCCD.
        - DDMMYYYY: ngày hiện tại theo định dạng ddMMyyyy.
        - XXX: số thứ tự của hành khách được tạo trong ngày, được định dạng thành chuỗi 3 số.
    */
    DECLARE @yy VARCHAR(2) = RIGHT(@cccd, 2);
    DECLARE @dateStr VARCHAR(8) = FORMAT(GETDATE(), 'ddMMyyyy');  -- SQL Server 2012+ hỗ trợ hàm FORMAT

    -- Đếm số hành khách đã được tạo trong ngày hiện tại dựa trên phần DDMMYYYY trong maHanhKhach.
    -- Với định dạng "HK-YY-DDMMYYYY-XXX", phần DDMMYYYY bắt đầu từ vị trí 7.
    DECLARE @count INT;
    SELECT @count = COUNT(*) 
    FROM HanhKhach
    WHERE SUBSTRING(maHanhKhach, 7, 8) = @dateStr;

    DECLARE @seq INT = @count + 1;
    DECLARE @seqStr VARCHAR(3) = RIGHT('000' + CAST(@seq AS VARCHAR(3)), 3);

    DECLARE @maHanhKhach VARCHAR(20) = 'HK-' + @yy + '-' + @dateStr + '-' + @seqStr;
    
    -- Chèn dữ liệu vào bảng HanhKhach
    INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd)
    VALUES (@maHanhKhach, @tenHanhKhach, @cccd);
    
    -- Trả về mã hành khách mới được tạo
    SELECT @maHanhKhach AS NewHanhKhachID;
END;
GO


/*CREATE PROCEDURE InsertHoaDon
    @maNV NVARCHAR(15),
    @maKH VARCHAR(17),
    @maKhuyenMai VARCHAR(15),
    @soVe INT,
    @ngayLapHoaDon DATETIME
AS
BEGIN
    SET NOCOUNT ON;

    -- VAT mặc định là 0.1 (10%)
    DECLARE @VAT FLOAT = 0.1;

    -- Sinh mã hóa đơn theo định dạng: HD-XXX-Z-DDMMYYYY-UUUU
    --  - XXX: lấy 3 ký tự cuối của maCa (ví dụ, nếu maCa = 'CA-001', thì XXX = '001').
    --   - Z: là ký tự cuối của maCa, chứa số thứ tự ca (vì maCa được sinh theo định dạng "CA-YY-DDMMYY-X").
    --   - DDMMYYYY: chuyển @ngayLapHoaDon thành chuỗi theo định dạng ddMMyyyy.
    -- - UUUU: số thứ tự hóa đơn của ca đó trong ngày; được tính dựa trên số hóa đơn có cùng maCa và cùng ngày.
    
    DECLARE @xxx VARCHAR(3) = RIGHT(@maNV, 3);
    DECLARE @shiftNum CHAR(1) = RIGHT(@maNV, 1);  -- Ký tự cuối của maCa là số ca
    DECLARE @dateStr VARCHAR(8) = FORMAT(@ngayLapHoaDon, 'ddMMyyyy');  -- Yêu cầu SQL Server 2012+

    -- Đếm số hóa đơn đã tạo cho ca @maCa trong ngày @ngayLapHoaDon
    DECLARE @invoiceCount INT;
    SELECT @invoiceCount = COUNT(*)
    FROM HoaDon
    WHERE maNV = @maNV
      AND CONVERT(date, ngayLapHoaDon) = CONVERT(date, @ngayLapHoaDon);

    DECLARE @invoiceSeq INT = @invoiceCount + 1;
    DECLARE @invoiceSeqStr VARCHAR(4) = RIGHT('0000' + CAST(@invoiceSeq AS VARCHAR(4)), 4);

    DECLARE @maHoaDon VARCHAR(22) = 'HD-' + @xxx + '-' + @shiftNum + '-' + @dateStr + '-' + @invoiceSeqStr;

    -- Chèn bản ghi mới vào bảng HoaDon, tongTien ban đầu được khởi tạo là 0
    INSERT INTO HoaDon (maHoaDon, ngayLapHoaDon, VAT, soVe, tongTien, maNV, maKH, maKhuyenMai)
    VALUES (@maHoaDon, @ngayLapHoaDon, @VAT, @soVe, 0, @maNV, @maKH, @maKhuyenMai);

    -- Trả về mã hóa đơn mới được tạo
    SELECT @maHoaDon AS NewHoaDonID;
END;
GO

*/



CREATE PROCEDURE InsertLoaiVe
    @maLoaiVe VARCHAR(5),
    @tenLoaiVe NVARCHAR(100),
    @moTaLoaiVe NVARCHAR(1000),
    @heSoLoaiVe FLOAT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra mã loại vé: chỉ cho phép 3 giá trị
    IF @maLoaiVe NOT IN ('LV-TE', 'LV-HSSV', 'LV-NL')
    BEGIN
         RAISERROR('Mã loại vé không hợp lệ. Chỉ chấp nhận: LV-TE, LV-HSSV, LV-NL.', 16, 1);
         RETURN;
    END

    -- Kiểm tra tên loại vé không được rỗng
    IF LTRIM(RTRIM(@tenLoaiVe)) = ''
    BEGIN
         RAISERROR('Tên loại vé không được rỗng.', 16, 1);
         RETURN;
    END

    -- Kiểm tra mô tả loại vé không được rỗng
    IF LTRIM(RTRIM(@moTaLoaiVe)) = ''
    BEGIN
         RAISERROR('Mô tả loại vé không được rỗng.', 16, 1);
         RETURN;
    END

    -- Kiểm tra hệ số loại vé: phải lớn hơn 0 và nhỏ hơn hoặc bằng 1
    IF @heSoLoaiVe <= 0 OR @heSoLoaiVe > 1
    BEGIN
         RAISERROR('Hệ số loại vé phải lớn hơn 0 và nhỏ hơn hoặc bằng 1.', 16, 1);
         RETURN;
    END

    -- Chèn dữ liệu vào bảng LoaiVe
    INSERT INTO LoaiVe (maLoaiVe, tenLoaiVe, moTaLoaiVe, heSoLoaiVe)
    VALUES (@maLoaiVe, @tenLoaiVe, @moTaLoaiVe, @heSoLoaiVe);

    -- Trả về mã loại vé vừa được thêm
    SELECT @maLoaiVe AS NewLoaiVeID;
END;
GO


CREATE PROCEDURE InsertVe
    @maHanhKhach VARCHAR(16),
    @maLoaiVe VARCHAR(5),
    @maChuyenDi VARCHAR(20),
    @maGhe VARCHAR(20),
    @maHoaDon VARCHAR(22),
    @trangThai NVARCHAR(13),
    @giaVe MONEY
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra trạng thái vé
    IF @trangThai NOT IN (N'Đã hoàn thành', N'Đã đặt', N'Đã hủy')
    BEGIN
         RAISERROR('Trạng thái vé không hợp lệ. Chỉ chấp nhận: Đã hoàn thành, Đã đặt, Đã hủy.', 16, 1);
         RETURN;
    END

    -- Kiểm tra giá vé > 0
    IF @giaVe <= 0
    BEGIN
         RAISERROR('Giá vé phải lớn hơn 0.', 16, 1);
         RETURN;
    END

    -- Kiểm tra sự tồn tại của các khóa ngoại
    IF NOT EXISTS (SELECT 1 FROM HanhKhach WHERE maHanhKhach = @maHanhKhach)
    BEGIN
         RAISERROR('Mã hành khách không tồn tại.', 16, 1);
         RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM LoaiVe WHERE maLoaiVe = @maLoaiVe)
    BEGIN
         RAISERROR('Mã loại vé không tồn tại.', 16, 1);
         RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM ChuyenDi WHERE maChuyenDi = @maChuyenDi)
    BEGIN
         RAISERROR('Mã chuyến đi không tồn tại.', 16, 1);
         RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM Ghe WHERE maGhe = @maGhe)
    BEGIN
         RAISERROR('Mã ghế không tồn tại.', 16, 1);
         RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon = @maHoaDon)
    BEGIN
         RAISERROR('Mã hóa đơn không tồn tại.', 16, 1);
         RETURN;
    END

    -- Xác định ngày phát hành vé là ngày hiện tại
    DECLARE @ticketDate DATETIME = GETDATE();
    DECLARE @dateStr VARCHAR(8) = FORMAT(@ticketDate, 'ddMMyyyy');  -- Yêu cầu SQL Server 2012+

    -- Tính số thứ tự của vé trong ngày
    -- Lọc các vé có định dạng chứa phần ngày bằng @dateStr (định dạng maVe đã theo mẫu "V-{maChuyenDi}-{maGhe}-{DDMMYYYY}-XXX")
    DECLARE @ticketCount INT;
    SELECT @ticketCount = COUNT(*)
    FROM Ve
    WHERE maVe LIKE 'V-%-%-' + @dateStr + '-%';

    DECLARE @seq INT = @ticketCount + 1;
    DECLARE @seqStr VARCHAR(3) = RIGHT('000' + CAST(@seq AS VARCHAR(3)), 3);

    -- Sinh mã vé theo định dạng: V-{maChuyenDi}-{maGhe}-{DDMMYYYY}-XXX
    DECLARE @maVe VARCHAR(52) = 'V-' + @maChuyenDi + '-' + @maGhe + '-' + @dateStr + '-' + @seqStr;

    -- Chèn bản ghi mới vào bảng Ve
    INSERT INTO Ve (maVe, maHanhKhach, maLoaiVe, maChuyenDi, maGhe, maHoaDon, trangThai, giaVe)
    VALUES (@maVe, @maHanhKhach, @maLoaiVe, @maChuyenDi, @maGhe, @maHoaDon, @trangThai, @giaVe);

    -- Trả về mã vé mới được tạo
    SELECT @maVe AS NewMaVe;
END;
GO

CREATE TRIGGER trg_UpdateHoaDonTongTien
ON Ve
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    -- Tập hợp các maHoaDon bị ảnh hưởng từ các thao tác INSERT/UPDATE/DELETE
    DECLARE @AffectedHoaDon TABLE (maHoaDon VARCHAR(22));

    INSERT INTO @AffectedHoaDon(maHoaDon)
    SELECT maHoaDon FROM inserted
    UNION
    SELECT maHoaDon FROM deleted;

    -- Cập nhật tongTien cho các hóa đơn bị ảnh hưởng
    UPDATE h
    SET h.tongTien = ISNULL(calc.sumGiaVe, 0) * (1 + h.VAT) * km.heSoKhuyenMai
    FROM HoaDon h
    LEFT JOIN (
        SELECT maHoaDon, SUM(giaVe) AS sumGiaVe
        FROM Ve
        GROUP BY maHoaDon
    ) calc ON h.maHoaDon = calc.maHoaDon
    INNER JOIN KhuyenMai km ON h.maKhuyenMai = km.maKhuyenMai
    WHERE h.maHoaDon IN (SELECT maHoaDon FROM @AffectedHoaDon);
END;
GO

CREATE TRIGGER trg_UpdateChuyenDi_SeatStatus
ON Ve
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    -- Lấy danh sách các maChuyenDi bị ảnh hưởng từ bảng inserted và deleted
    DECLARE @Affected TABLE (maChuyenDi VARCHAR(20));
    
    INSERT INTO @Affected(maChuyenDi)
    SELECT DISTINCT maChuyenDi FROM inserted
    UNION
    SELECT DISTINCT maChuyenDi FROM deleted;

    -- Cập nhật lại số ghế đã đặt và số ghế còn lại cho từng chuyến đi bị ảnh hưởng
    UPDATE cd
    SET 
        cd.soGheDaDat = ISNULL(v.newCount, 0),
        cd.soGheConTrong = totalSeats.total - ISNULL(v.newCount, 0)
    FROM ChuyenDi cd
    INNER JOIN @Affected a ON cd.maChuyenDi = a.maChuyenDi
    CROSS APPLY (
         -- Tính tổng số vé (số ghế đã đặt) hiện tại cho chuyến đi
         SELECT COUNT(*) AS newCount
         FROM Ve
         WHERE maChuyenDi = cd.maChuyenDi
    ) v
    CROSS APPLY (
         -- Tính tổng số ghế của tàu của chuyến đi, thông qua bảng Tau, ToaTau, KhoangTau
         SELECT ISNULL(SUM(kt.soGhe), 0) AS total
         FROM Tau t
         INNER JOIN ToaTau tt ON t.maTau = tt.maTau
         INNER JOIN KhoangTau kt ON tt.maToaTau = kt.maToaTau
         WHERE t.maTau = cd.maTau
    ) totalSeats;
END;
GO

--Thêm dữ liệu nhân viên

INSERT INTO NhanVien (maNV, tenNV, gioiTinh, ngaySinh, email, soDienThoai, cccd, ngayBatDauLamViec, vaiTro, trangThai) VALUES
(N'NV-0-89-382-001', N'Lê Văn Hưng', 0, '1989-03-12', 'levanhung89@gmail.com', '0905123456', '079089123382', '2023-07-15', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-95-764-002', N'Nguyễn Thị Hằng', 1, '1995-11-08', 'hangnguyen95@gmail.com', '0938456123', '001195765764', '2024-01-21', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-90-419-003', N'Phạm Văn Đức', 0, '1990-07-30', 'phamduc90@gmail.com', '0912233445', '048090874419', '2023-06-02', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-537-004', N'Trần Thị Mai', 1, '1992-05-17', 'maitran92@gmail.com', '0946333322', '031192556537', '2023-09-11', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-88-248-005', N'Hoàng Văn Tùng', 0, '1988-12-01', 'tunghoang88@gmail.com', '0906677889', '079088132248', '2023-04-19', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-91-309-006', N'Vũ Thị Huyền', 1, '1991-03-25', 'huyenvu91@gmail.com', '0969876543', '001191613309', '2023-11-02', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-87-584-007', N'Đinh Văn Bình', 0, '1987-08-14', 'binhdinh87@gmail.com', '0921234567', '048087912584', '2023-03-10', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-93-682-008', N'Bùi Thị Lan', 1, '1993-02-19', 'lanbui93@gmail.com', '0971112233', '031193341682', '2023-08-23', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-85-973-009', N'Ngô Văn Hòa', 0, '1985-09-05', 'hoango85@gmail.com', '0917654321', '079085274973', '2023-02-07', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-94-648-010', N'Phan Thị Ngọc', 1, '1994-10-29', 'ngocphan94@gmail.com', '0903344556', '001194877648', '2023-10-18', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-86-295-011', N'Vương Văn Kiên', 0, '1986-04-20', 'kienvuong86@gmail.com', '0934567890', '048086698295', '2023-01-15', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-90-731-012', N'Tạ Thị Nhung', 1, '1990-01-07', 'nhungta90@gmail.com', '0947896541', '031190123731', '2023-12-12', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-88-167-013', N'Huỳnh Văn Duy', 0, '1988-06-28', 'duyhuynh88@gmail.com', '0963334455', '079088456167', '2023-05-09', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-91-826-014', N'Đỗ Thị Hòa', 1, '1991-08-09', 'hoado91@gmail.com', '0927893210', '001191554826', '2023-07-25', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-89-146-015', N'Phạm Văn Sơn', 0, '1989-11-03', 'sonpham89@gmail.com', '0911112222', '048089799146', '2023-09-14', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-865-016', N'Trần Thị Thảo', 1, '1992-02-18', 'thaotran92@gmail.com', '0973332211', '031192333865', '2023-04-28', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-87-584-017', N'Nguyễn Văn Nam', 0, '1987-05-12', 'namnguyen87@gmail.com', '0937894561', '079087112584', '2023-06-06', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-93-619-018', N'Lê Thị Xuân', 1, '1993-12-20', 'xuanle93@gmail.com', '0956782345', '001193789619', '2023-08-15', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-85-746-019', N'Vũ Văn Đạt', 0, '1985-10-10', 'datvu85@gmail.com', '0925566778', '048085155746', '2023-03-29', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-94-917-020', N'Hoàng Thị Thanh', 1, '1994-07-23', 'thanhhoang94@gmail.com', '0901239876', '031194242917', '2023-11-05', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-89-486-021', N'Nguyễn Văn Thành', 0, '1989-01-22', 'thanhnguyen89@gmail.com', '0901234321', '079089348486', '2023-07-18', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-95-628-022', N'Lê Thị Hường', 1, '1995-06-03', 'huongle95@gmail.com', '0938123764', '001195483628', '2024-01-24', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-90-295-023', N'Phạm Hữu Dũng', 0, '1990-09-14', 'dungpham90@gmail.com', '0912900876', '048090349295', '2023-06-04', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-473-024', N'Trần Mỹ Hạnh', 1, '1992-04-08', 'hanhtran92@gmail.com', '0946339821', '031192234473', '2023-09-15', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-88-652-025', N'Hoàng Văn Nam', 0, '1988-07-26', 'namhoang88@gmail.com', '0906888192', '079088781652', '2023-04-23', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-91-364-026', N'Vũ Thị Nga', 1, '1991-03-06', 'ngavu91@gmail.com', '0969988776', '001191918364', '2023-11-03', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-87-183-027', N'Đinh Tiến Đạt', 0, '1987-12-27', 'datdinh87@gmail.com', '0927888991', '048087542183', '2023-03-17', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-93-291-028', N'Bùi Thị Thảo', 1, '1993-02-11', 'thaobui93@gmail.com', '0971123451', '031193334291', '2023-08-26', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-85-827-029', N'Ngô Minh Tuấn', 0, '1985-05-01', 'tuanngo85@gmail.com', '0917234567', '079085678827', '2023-02-19', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-94-518-030', N'Phan Thanh Tâm', 1, '1994-10-30', 'tamphan94@gmail.com', '0903456778', '001194119518', '2023-10-21', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-86-364-031', N'Vương Đức Khánh', 0, '1986-04-15', 'khanhvuong86@gmail.com', '0934578990', '048086963364', '2023-01-20', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-90-729-032', N'Tạ Thị Phượng', 1, '1990-01-18', 'phuongta90@gmail.com', '0947896512', '031190876729', '2023-12-15', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-88-567-033', N'Huỳnh Ngọc Sơn', 0, '1988-11-28', 'sonhuynh88@gmail.com', '0963344578', '079088443567', '2023-05-11', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-91-416-034', N'Đỗ Thị Thanh', 1, '1991-08-21', 'thanhdo91@gmail.com', '0927877766', '001191873416', '2023-07-27', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-89-681-035', N'Phạm Văn Hiếu', 0, '1989-10-05', 'hieupham89@gmail.com', '0911234599', '048089324681', '2023-09-17', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-745-036', N'Trần Kim Cúc', 1, '1992-03-22', 'cuctran92@gmail.com', '0973342567', '031192559745', '2023-04-30', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-87-432-037', N'Nguyễn Văn Hòa', 0, '1987-06-20', 'hoanguyen87@gmail.com', '0937894511', '079087199432', '2023-06-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-93-654-038', N'Lê Mỹ Linh', 1, '1993-12-05', 'linhle93@gmail.com', '0956721234', '001193786654', '2023-08-18', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-85-741-039', N'Vũ Văn Hùng', 0, '1985-10-13', 'hungvu85@gmail.com', '0925534767', '048085476741', '2023-03-31', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-94-963-040', N'Hoàng Thị Dung', 1, '1994-07-24', 'dunghoang94@gmail.com', '0901227890', '031194158963', '2023-11-07', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-90-458-041', N'Trịnh Văn Hưng', 0, '1990-03-14', 'hungtrinh90@gmail.com', '0904567812', '079090173458', '2023-06-30', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-91-762-042', N'Nguyễn Thị Yến', 1, '1991-12-12', 'yennguyen91@gmail.com', '0913345667', '001191682762', '2023-10-03', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-89-384-043', N'Hồ Minh Trí', 0, '1989-08-21', 'triminh89@gmail.com', '0931223345', '048089432384', '2023-05-22', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-641-044', N'Lý Thị Bích', 1, '1992-02-09', 'bichly92@gmail.com', '0964551231', '031192378641', '2023-08-14', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-87-273-045', N'Đào Văn Tân', 0, '1987-09-03', 'tandao87@gmail.com', '0906778899', '079087583273', '2023-04-11', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-93-895-046', N'Võ Thị Hồng', 1, '1993-07-25', 'hongvo93@gmail.com', '0945123456', '001193948895', '2023-07-02', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-88-382-047', N'Tống Văn Phúc', 0, '1988-11-18', 'phuctong88@gmail.com', '0924567890', '048088591382', '2023-09-06', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-94-723-048', N'Phạm Thị Hiền', 1, '1994-06-30', 'hienpham94@gmail.com', '0973312555', '031194448723', '2023-03-15', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-86-913-049', N'Tô Đức Thắng', 0, '1986-10-10', 'thangto86@gmail.com', '0956667744', '079086729913', '2023-02-18', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-95-658-050', N'Đinh Thị Quỳnh', 1, '1995-01-29', 'quynhdinh95@gmail.com', '0961237890', '001195327658', '2023-11-27', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-90-317-051', N'Bùi Văn Dương', 0, '1990-07-12', 'duongbui90@gmail.com', '0903345789', '048090432317', '2023-07-20', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-92-564-052', N'Trần Thị Mai', 1, '1992-05-08', 'maitran92@gmail.com', '0948765523', '031192777564', '2023-06-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-88-846-053', N'Lê Minh Nhật', 0, '1988-02-17', 'nhatle88@gmail.com', '0912223344', '079088989846', '2023-09-30', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-93-679-054', N'Ngô Thị Kim', 1, '1993-03-03', 'kimngo93@gmail.com', '0951232345', '001193215679', '2023-10-08', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-87-215-055', N'Vương Văn Đạt', 0, '1987-05-04', 'datvuong87@gmail.com', '0904567899', '048087636215', '2023-05-05', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-91-397-056', N'Hà Thị Hòa', 1, '1991-11-22', 'hoaha91@gmail.com', '0934567812', '031191998397', '2023-08-01', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-89-472-057', N'Phan Văn Minh', 0, '1989-12-19', 'minhphan89@gmail.com', '0949876543', '079089327472', '2023-04-27', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-94-518-058', N'Lương Thị Lệ', 1, '1994-08-14', 'leluong94@gmail.com', '0913456789', '001194456518', '2023-06-16', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-85-726-059', N'Nguyễn Hữu Phúc', 0, '1985-09-29', 'phucnguyen85@gmail.com', '0901112233', '048085991726', '2023-01-05', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-90-632-060', N'Huỳnh Thị Trinh', 1, '1990-04-27', 'trinhhuynh90@gmail.com', '0967891123', '031190722632', '2023-07-09', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-94-638-061', N'Nguyễn Thị Mỹ Hạnh', 1, '1994-10-02', 'hanh94@gmail.com', '0381029384', '048194940638', '2024-06-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-85-427-062', N'Lê Văn Hiệp', 0, '1985-01-11', 'hiep85@gmail.com', '0918273645', '031085010427', '2023-09-20', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-90-395-063', N'Phan Đức Minh', 0, '1990-12-22', 'minh90@gmail.com', '0968172736', '001090221395', '2022-12-03', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-93-584-064', N'Trương Thị Cẩm Tú', 1, '1993-03-17', 'tucam93@gmail.com', '0972718823', '079193170584', '2024-01-15', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-88-709-065', N'Vũ Quốc Hùng', 0, '1988-05-09', 'hung88@gmail.com', '0912378219', '096088090709', '2023-06-24', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-384-066', N'Hoàng Thị Bích Ngọc', 1, '1992-11-28', 'ngoc92@gmail.com', '0901819273', '011192281384', '2024-04-19', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-87-641-067', N'Nguyễn Minh Tâm', 0, '1987-07-04', 'tam87@gmail.com', '0321728374', '037087040641', '2023-10-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-91-782-068', N'Lê Thị Kiều Trang', 1, '1991-02-14', 'trang91@gmail.com', '0912837361', '031191140782', '2023-07-22', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-89-403-069', N'Phạm Văn Hòa', 0, '1989-09-27', 'hoa89@gmail.com', '0962738283', '001089270403', '2024-03-30', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-90-267-070', N'Đặng Thị Mai Linh', 1, '1990-06-05', 'linh90@gmail.com', '0352738172', '048190050267', '2023-08-14', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-93-845-071', N'Lý Minh Phúc', 0, '1993-04-17', 'phuc93@gmail.com', '0347281918', '031093170845', '2023-11-11', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-95-421-072', N'Nguyễn Thị Ánh Dương', 1, '1995-01-30', 'duong95@gmail.com', '0383727382', '079195300421', '2024-02-18', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-86-349-073', N'Trần Văn Dũng', 0, '1986-12-13', 'dung86@gmail.com', '0937382718', '048086131349', '2023-09-01', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-94-558-074', N'Võ Thị Mỹ Duyên', 1, '1994-03-11', 'duyen94@gmail.com', '0947382731', '096194110558', '2024-06-29', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-88-739-075', N'Bùi Văn Quý', 0, '1988-08-20', 'quy88@gmail.com', '0927382737', '001088200739', '2023-12-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-91-693-076', N'Huỳnh Thị Yến Nhi', 1, '1991-06-23', 'nhi91@gmail.com', '0938273645', '048191230693', '2023-07-05', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-90-816-077', N'Phạm Văn Lộc', 0, '1990-02-19', 'loc90@gmail.com', '0962837261', '031090190816', '2024-05-12', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-573-078', N'Lê Thị Thảo Vy', 1, '1992-09-04', 'vy92@gmail.com', '0918372645', '011192040573', '2023-10-19', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-87-368-079', N'Ngô Văn Tuấn', 0, '1987-05-06', 'tuan87@gmail.com', '0973826172', '048087060368', '2024-01-09', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-95-294-080', N'Trần Thị Phương Anh', 1, '1995-11-25', 'phuonganh95@gmail.com', '0391827364', '096195250294', '2024-03-01', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-89-721-081', N'Nguyễn Văn Tình', 0, '1989-03-02', 'tinh89@gmail.com', '0912837465', '001089020721', '2023-10-01', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-93-658-082', N'Hoàng Thị Tuyết', 1, '1993-07-16', 'tuyet93@gmail.com', '0923847261', '048193160658', '2023-11-20', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-86-904-083', N'Phạm Văn Thành', 0, '1986-09-28', 'thanh86@gmail.com', '0902738172', '031086280904', '2024-01-15', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-91-347-084', N'Lê Thị Hoa', 1, '1991-06-10', 'hoa91@gmail.com', '0961827361', '079191100347', '2024-03-12', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-87-672-085', N'Ngô Minh Trí', 0, '1987-01-23', 'tri87@gmail.com', '0948273645', '096087230672', '2023-09-30', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-90-285-086', N'Vũ Thị Thanh', 1, '1990-05-14', 'thanh90@gmail.com', '0937281827', '011190140285', '2023-08-22', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-88-509-087', N'Trần Văn Hải', 0, '1988-11-08', 'hai88@gmail.com', '0978273645', '031088080509', '2023-12-01', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-436-088', N'Nguyễn Thị Mai', 1, '1992-03-26', 'mai92@gmail.com', '0982736455', '001092260436', '2024-04-05', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-85-768-089', N'Lê Văn Duy', 0, '1985-08-18', 'duy85@gmail.com', '0902837462', '048085180768', '2024-02-20', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-94-613-090', N'Trương Thị Kim Yến', 1, '1994-12-01', 'yen94@gmail.com', '0918273645', '096194010613', '2023-07-09', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-89-587-091', N'Phan Văn Long', 0, '1989-10-03', 'long89@gmail.com', '0942738161', '001089030587', '2024-01-26', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-91-326-092', N'Lý Thị Mỹ Linh', 1, '1991-01-09', 'linh91@gmail.com', '0928172635', '048191090326', '2023-09-17', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-87-842-093', N'Đoàn Minh Quân', 0, '1987-07-25', 'quan87@gmail.com', '0938273611', '011087250842', '2023-10-28', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-95-294-094', N'Bùi Thị Phương', 1, '1995-04-12', 'phuong95@gmail.com', '0902736481', '079195120294', '2024-03-18', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-86-479-095', N'Hồ Văn Quý', 0, '1986-02-06', 'quy86@gmail.com', '0972718329', '031086060479', '2023-08-15', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-93-784-096', N'Lâm Thị Hồng', 1, '1993-06-19', 'hong93@gmail.com', '0968273741', '001093190784', '2023-12-05', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-88-631-097', N'Tô Văn Khánh', 0, '1988-09-30', 'khanh88@gmail.com', '0928371841', '048088300631', '2024-01-03', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-538-098', N'Trần Thị Thu', 1, '1992-02-28', 'thu92@gmail.com', '0938271638', '011192280538', '2024-02-10', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-85-617-099', N'Nguyễn Văn Đức', 0, '1985-03-12', 'duc85@gmail.com', '0952738482', '096085120617', '2023-11-07', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-90-473-100', N'Huỳnh Thị Bích', 1, '1990-09-07', 'bich90@gmail.com', '0918273659', '031090070473', '2024-04-01', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-90-458-101', N'Phạm Văn Hậu', 0, '1990-04-20', 'hau90@gmail.com', '0901234567', '001090200458', '2019-06-15', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-91-862-102', N'Lê Thị Yến', 1, '1991-10-10', 'yen91@gmail.com', '0912345678', '048191100862', '2018-12-05', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-92-341-103', N'Nguyễn Văn Bảo', 0, '1992-07-07', 'bao92@gmail.com', '0923456781', '079092070341', '2020-01-12', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-93-679-104', N'Trần Thị Minh', 1, '1993-08-08', 'minh93@gmail.com', '0934567812', '031193080679', '2021-05-25', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-89-485-105', N'Đỗ Văn Quân', 0, '1989-05-05', 'quan89@gmail.com', '0945678123', '096089050485', '2019-09-09', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-1-94-768-106', N'Hoàng Thị Thanh', 1, '1994-12-03', 'thanh94@gmail.com', '0956781234', '011194030768', '2018-08-20', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-91-927-107', N'Tô Minh Đức', 0, '1991-11-15', 'duc91@gmail.com', '0967812345', '031091150927', '2020-03-30', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-654-108', N'Nguyễn Thị Hạnh', 1, '1992-06-21', 'hanh92@gmail.com', '0978123456', '001092210654', '2019-11-11', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-93-583-109', N'Lâm Văn Hùng', 0, '1993-03-13', 'hung93@gmail.com', '0981234567', '048193130583', '2020-06-22', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-90-731-110', N'Võ Thị Kim', 1, '1990-09-17', 'kim90@gmail.com', '0992345678', '079190170731', '2021-01-14', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-88-942-111', N'Bùi Văn Sang', 0, '1988-02-18', 'sang88@gmail.com', '0903456789', '031088180942', '2020-10-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-95-348-112', N'Trần Thị Cúc', 1, '1995-01-26', 'cuc95@gmail.com', '0914567890', '001195260348', '2019-02-02', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-89-764-113', N'Ngô Văn Khoa', 0, '1989-12-22', 'khoa89@gmail.com', '0925678901', '048189221764', '2021-03-18', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-1-93-617-114', N'Phạm Thị Hường', 1, '1993-06-06', 'huong93@gmail.com', '0936789012', '096193060617', '2020-08-08', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-92-856-115', N'Nguyễn Văn Tú', 0, '1992-10-30', 'tu92@gmail.com', '0947890123', '011192301856', '2021-06-01', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-94-635-116', N'Dương Thị Hòa', 1, '1994-03-11', 'hoa94@gmail.com', '0958901234', '079194110635', '2019-05-19', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-91-479-117', N'Vũ Minh Lộc', 0, '1991-01-04', 'loc91@gmail.com', '0969012345', '031091040479', '2018-07-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-95-784-118', N'Trịnh Thị Thảo', 1, '1995-09-09', 'thao95@gmail.com', '0970123456', '001195090784', '2020-04-07', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-90-618-119', N'Huỳnh Văn Giang', 0, '1990-06-29', 'giang90@gmail.com', '0981230456', '048190290618', '2019-10-20', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-92-279-120', N'Phan Thị Thúy', 1, '1992-05-01', 'thuy92@gmail.com', '0992341567', '096192010279', '2021-12-12', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-01-274-121', N'Lê Văn Khánh', 0, '2001-03-11', 'khanh01@gmail.com', '0912345121', '001201110274', '2024-06-05', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-00-583-122', N'Trần Thị Mai', 1, '2000-08-15', 'mai00@gmail.com', '0923456122', '048300150583', '2023-10-12', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-02-847-123', N'Nguyễn Minh Quân', 0, '2002-11-20', 'quan02@gmail.com', '0934567123', '079202201847', '2023-11-25', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-03-769-124', N'Phạm Thị Hằng', 1, '2003-06-18', 'hang03@gmail.com', '0945678124', '096303180769', '2024-01-10', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-04-158-125', N'Huỳnh Quốc Đạt', 0, '2004-05-01', 'dat04@gmail.com', '0956789125', '011204010158', '2023-08-19', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-1-02-461-126', N'Ngô Thị Huyền', 1, '2002-12-05', 'huyen02@gmail.com', '0967890126', '031302051461', '2024-02-22', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-01-689-127', N'Bùi Văn Kiên', 0, '2001-01-27', 'kien01@gmail.com', '0978901127', '001201270689', '2023-07-30', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-00-392-128', N'Lý Thị Cúc', 1, '2000-02-14', 'cuc00@gmail.com', '0989012128', '079300140392', '2023-09-11', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-03-275-129', N'Tô Minh Dương', 0, '2003-04-30', 'duong03@gmail.com', '0990123129', '048203300275', '2023-10-05', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-04-834-130', N'Vũ Thị Hòa', 1, '2004-09-09', 'hoa04@gmail.com', '0901234130', '096304090834', '2024-03-02', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-00-681-131', N'Phan Văn Trung', 0, '2000-10-08', 'trung00@gmail.com', '0912345131', '011200080681', '2023-11-14', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-01-243-132', N'Đinh Thị Hiền', 1, '2001-06-06', 'hien01@gmail.com', '0923456132', '031301060243', '2023-12-20', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-02-967-133', N'Lâm Văn Hậu', 0, '2002-02-22', 'hau02@gmail.com', '0934567133', '048202220967', '2024-01-30', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-03-759-134', N'Nguyễn Thị Hạnh', 1, '2003-07-01', 'hanh03@gmail.com', '0945678134', '079303010759', '2023-07-19', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-04-832-135', N'Trịnh Văn Phát', 0, '2004-03-03', 'phat04@gmail.com', '0956789135', '096204030832', '2023-09-01', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-00-428-136', N'Lê Thị Ngân', 1, '2000-12-25', 'ngan00@gmail.com', '0967890136', '001300251428', '2024-02-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-01-354-137', N'Phạm Văn Tùng', 0, '2001-05-11', 'tung01@gmail.com', '0978901137', '048201110354', '2023-08-28', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-1-02-638-138', N'Nguyễn Thị Thơm', 1, '2002-11-09', 'thom02@gmail.com', '0989012138', '031302091638', '2024-01-01', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-03-951-139', N'Đoàn Văn Duy', 0, '2003-08-17', 'duy03@gmail.com', '0990123139', '079203170951', '2023-12-05', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-04-763-140', N'Hồ Thị Bích', 1, '2004-06-06', 'bich04@gmail.com', '0901234140', '096304060763', '2024-03-20', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-01-221-141', N'Nguyễn Văn Dũng', 0, '2001-09-19', 'dungnv01@gmail.com', '0912233141', '001201190221', '2023-09-05', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-1-02-624-142', N'Hoàng Thị Linh', 1, '2002-12-01', 'linhht02@gmail.com', '0922345142', '031302011624', '2023-10-28', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-03-735-143', N'Trần Minh Tú', 0, '2003-11-30', 'tumn03@gmail.com', '0932456143', '048203301735', '2024-01-14', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-04-496-144', N'Lê Thị Phượng', 1, '2004-04-22', 'phuonglt04@gmail.com', '0942567144', '079304220496', '2023-12-08', N'Nhân viên quản lý', N'Đã nghỉ làm'),
(N'NV-0-00-853-145', N'Phạm Đức Lộc', 0, '2000-03-08', 'locpd00@gmail.com', '0952678145', '096200080853', '2023-08-21', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-01-342-146', N'Tạ Thị Hạnh', 1, '2001-07-25', 'hanhtth01@gmail.com', '0962789146', '011301250342', '2023-11-17', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-02-618-147', N'Đặng Quang Vinh', 0, '2002-06-06', 'vinhdq02@gmail.com', '0972890147', '031202060618', '2024-03-02', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-1-03-785-148', N'Dương Thị Kim', 1, '2003-10-02', 'kimdt03@gmail.com', '0982901148', '048303021785', '2023-09-30', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-04-263-149', N'Vũ Hữu Đạt', 0, '2004-02-14', 'datvh04@gmail.com', '0992012149', '079204140263', '2024-01-10', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-00-578-150', N'Lý Thị Ngọc', 1, '2000-06-12', 'ngoclt00@gmail.com', '0902123150', '001300120578', '2023-10-25', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-01-698-151', N'Tô Hoàng Nam', 0, '2001-12-03', 'namth01@gmail.com', '0912233151', '031201030698', '2023-11-20', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-1-02-413-152', N'Trương Thị Hậu', 1, '2002-05-17', 'hautth02@gmail.com', '0922345152', '048302170413', '2023-12-15', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-03-861-153', N'Ngô Văn Đức', 0, '2003-06-01', 'ducnv03@gmail.com', '0932456153', '096203010861', '2023-08-05', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-04-597-154', N'Trần Thị Thu', 1, '2004-07-23', 'thutt04@gmail.com', '0942567154', '011304230597', '2024-03-22', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-00-749-155', N'Cao Minh Hải', 0, '2000-08-30', 'haicm00@gmail.com', '0952678155', '079200300749', '2023-07-31', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-1-01-139-156', N'Nguyễn Thị Tươi', 1, '2001-03-09', 'tuoint01@gmail.com', '0962789156', '031301090139', '2023-09-11', N'Nhân viên quản lý', N'Đang làm'),
(N'NV-0-02-531-157', N'Phan Thanh Quân', 0, '2002-09-22', 'quanpt02@gmail.com', '0972890157', '001202220531', '2024-02-05', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-03-764-158', N'Hồ Thị Mỹ', 1, '2003-02-02', 'myht03@gmail.com', '0982901158', '048303020764', '2023-10-15', N'Nhân viên bán vé', N'Đã nghỉ làm'),
(N'NV-0-04-437-159', N'Trịnh Hữu Phúc', 0, '2004-11-17', 'phucth04@gmail.com', '0992012159', '096204170437', '2023-12-25', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-1-03-445-160', N'Vũ Thị Thanh', 1, '2003-05-07', 'thanhvt03@gmail.com', '0902123160', '031303070445', '2023-11-05', N'Nhân viên quản lý', N'Đã nghỉ làm');
Go

--Thêm dữ liệu khách hàng

INSERT INTO KhachHang (maKH, tenKH, soDienThoai, cccd) VALUES 
('KH-123-080424-001', N'Nguyễn Văn An', '0912345678', '012345678123'),
('KH-456-080424-002', N'Trần Thị Mai', '0987654321', '012345678456'),
('KH-789-080424-003', N'Phạm Văn Khôi', '0938765432', '012345678789'),
('KH-012-080424-004', N'Lê Thị Hồng', '0901234567', '012345679012'),
('KH-345-080424-005', N'Vũ Văn Nam', '0978123456', '012345679345'),
('KH-678-080424-006', N'Hoàng Thị Lan', '0967234567', '012345679678'),
('KH-901-080424-007', N'Đỗ Văn Cường', '0945123456', '012345679901'),
('KH-234-080424-008', N'Bùi Thị Hoa', '0934123456', '012345680234'),
('KH-567-080424-009', N'Ngô Văn Minh', '0917234567', '012345680567'),
('KH-890-080424-010', N'Lý Thị Hạnh', '0908345678', '012345680890'),
('KH-321-080424-011', N'Tô Văn Hòa', '0923456712', '012345681321'),
('KH-654-080424-012', N'Đặng Thị Huyền', '0968456723', '012345681654'),
('KH-987-080424-013', N'Trịnh Văn Thái', '0939784561', '012345681987'),
('KH-210-080424-014', N'Cao Thị Nhàn', '0978564123', '012345682210'),
('KH-543-080424-015', N'Mai Văn Quang', '0945123678', '012345682543'),
('KH-876-080424-016', N'Hà Thị Ngọc', '0909781234', '012345682876'),
('KH-109-080424-017', N'Đinh Văn Bình', '0934234567', '012345683109'),
('KH-432-080424-018', N'Lâm Thị Tuyết', '0912347865', '012345683432'),
('KH-765-080424-019', N'Kiều Văn Sơn', '0987123564', '012345683765'),
('KH-098-080424-020', N'Châu Thị Xuân', '0978432123', '012345683098'),
('KH-391-080424-001', N'Trần Thị Mai', '0987432110', '079325241039'),
('KH-028-070424-001', N'Lê Văn Hùng', '0918543201', '048209991028'),
('KH-117-060424-001', N'Nguyễn Thanh Phong', '0903456789', '001215002117'),
('KH-742-050424-001', N'Đặng Thị Ngọc', '0976444499', '031320045742'),
('KH-561-040424-001', N'Vũ Minh Tuấn', '0937238123', '079219905561'),
('KH-098-030424-001', N'Phạm Hồng Nhung', '0965123456', '079319950098'),
('KH-014-020424-001', N'Ngô Bá Thành', '0324341234', '001223994014'),
('KH-728-010424-001', N'Trần Văn Dũng', '0854678901', '048224006728'),
('KH-683-310324-001', N'Lê Thị Quỳnh', '0945234561', '031321994683'),
('KH-325-300324-001', N'Đỗ Văn Quang', '0987654321', '001220030325'),
('KH-129-290324-001', N'Phan Thị Hồng', '0937771234', '079320010129'),
('KH-874-280324-001', N'Nguyễn Minh Trí', '0925667123', '048215993874'),
('KH-496-270324-001', N'Lê Hoàng Nhật', '0975456777', '031225006496'),
('KH-387-260324-001', N'Vũ Thị Lan', '0904223344', '001319990387'),
('KH-908-250324-001', N'Phạm Văn Đức', '0978665234', '079218001908'),
('KH-472-240324-001', N'Nguyễn Thị Dung', '0968123123', '048321984472'),
('KH-139-230324-001', N'Hoàng Văn Khánh', '0927888999', '031222993139'),
('KH-803-220324-001', N'Bùi Thị Thanh', '0911223344', '001319988803'),
('KH-370-210324-001', N'Lương Văn Bình', '0945332112', '079217001370'),
('KH-264-200324-001', N'Đặng Thị Hoa', '0908123456', '048320991264'),
('KH-157-190324-001', N'Trần Văn Kiên', '0933221100', '031221002157'),
('KH-681-180324-001', N'Phan Thị Tuyết', '0899667788', '001219983681'),
('KH-249-170324-001', N'Nguyễn Hữu Tài', '0987432123', '079223994249'),
('KH-904-160324-001', N'Trịnh Thị Hiền', '0912567890', '048222005904'),
('KH-703-150324-001', N'Đỗ Văn Hòa', '0932457891', '031221991703'),
('KH-418-140324-001', N'Vũ Thị Nhàn', '0964789123', '001323981418'),
('KH-320-130324-001', N'Trần Minh Nhật', '0903678901', '079218007320'),
('KH-999-120324-001', N'Hoàng Thị Tuyết', '0923888999', '048224003999'),
('KH-485-110324-001', N'Lê Văn Đạt', '0931234567', '031219990485'),
('KH-777-100324-001', N'Ngô Thị Thanh', '0974123981', '001220008777'),
('KH-616-090324-001', N'Phạm Văn Sơn', '0968327123', '079222996616'),
('KH-335-080324-001', N'Nguyễn Hồng Nhung', '0901234123', '048319985335'),
('KH-225-070324-001', N'Đinh Văn An', '0937651230', '031224999225'),
('KH-112-060324-001', N'Đỗ Thị Huyền', '0987364920', '001223991112'),
('KH-440-050324-001', N'Nguyễn Văn Quý', '0928765432', '079223995440'),
('KH-718-040324-001', N'Trần Thị Thanh', '0912673498', '048219003718'),
('KH-358-030324-001', N'Lê Hữu Nam', '0908967345', '031223990358'),
('KH-801-020324-001', N'Bùi Thị Ngọc', '0978123490', '001220006801'),
('KH-690-010324-001', N'Phạm Minh Tuấn', '0984237890', '079221004690'),
('KH-124-290224-001', N'Nguyễn Hoàng Nam', '0912345678', '079219994124'),
('KH-685-280224-001', N'Trần Thị Hải', '0909876543', '048320004685'),
('KH-912-270224-001', N'Phạm Văn Khôi', '0932123456', '031222003912'),
('KH-041-260224-001', N'Đỗ Thị Mai', '0978765432', '001219993041'),
('KH-278-250224-001', N'Lê Quang Dũng', '0961234987', '079218991278'),
('KH-594-240224-001', N'Ngô Thị Ngọc', '0945123890', '048224991594'),
('KH-312-230224-001', N'Bùi Văn Hòa', '0928341234', '031220000312'),
('KH-467-220224-001', N'Vũ Thị Thúy', '0909567123', '001319992467'),
('KH-726-210224-001', N'Trần Đức Minh', '0976347891', '079223997726'),
('KH-053-200224-001', N'Lê Thị Hồng', '0918772345', '048323982053'),
('KH-841-190224-001', N'Nguyễn Văn Sơn', '0984567890', '031225005841'),
('KH-320-180224-001', N'Đinh Thị Lan', '0937891234', '001220009320'),
('KH-492-170224-001', N'Phan Văn Lực', '0967123456', '079319985492'),
('KH-633-160224-001', N'Hoàng Thị Minh', '0901982345', '048321990633'),
('KH-184-150224-001', N'Nguyễn Hữu Phước', '0974123987', '031218994184'),
('KH-909-140224-001', N'Trần Thị Xuân', '0912349876', '001319987909'),
('KH-236-130224-001', N'Lê Văn Hoàng', '0987771234', '079220004236'),
('KH-674-120224-001', N'Đỗ Thị Hạnh', '0931236547', '048220999674'),
('KH-095-110224-001', N'Vũ Hồng Sơn', '0961234560', '031221002095'),
('KH-349-100224-001', N'Nguyễn Thị Kim', '0947890123', '001323992349'),
('KH-760-090224-001', N'Phạm Văn An', '0921456789', '079217999760'),
('KH-430-080224-001', N'Ngô Thị Duyên', '0912987654', '048224004430'),
('KH-815-070224-001', N'Lê Minh Nhật', '0901678345', '031220004815'),
('KH-109-060224-001', N'Trần Thị Hoa', '0978888881', '001222008109'),
('KH-508-050224-001', N'Hoàng Văn Thái', '0987456321', '079218990508'),
('KH-668-040224-001', N'Bùi Thị Thanh', '0965432187', '048219002668'),
('KH-292-030224-001', N'Nguyễn Văn Quý', '0933214890', '031222991292'),
('KH-700-020224-001', N'Đỗ Thị Phượng', '0909647382', '001223987700'),
('KH-429-010224-001', N'Trịnh Văn Long', '0912123123', '079222003429'),
('KH-318-310124-001', N'Phan Thị Quỳnh', '0983432123', '048223006318'),
('KH-835-300124-001', N'Nguyễn Hữu Lâm', '0972123123', '031223992835'),
('KH-112-290124-001', N'Vũ Thị Nhung', '0908887776', '001221007112'),
('KH-545-280124-001', N'Lê Văn Tài', '0935567890', '079218994545'),
('KH-689-270124-001', N'Trần Thị Hà', '0964345678', '048221009689'),
('KH-423-260124-001', N'Ngô Minh Quân', '0949876543', '031219003423'),
('KH-209-250124-001', N'Đỗ Thị Hồng', '0918888882', '001320006209'),
('KH-571-240124-001', N'Bùi Văn Trí', '0984445566', '079217990571'),
('KH-338-230124-001', N'Phạm Thị Huệ', '0901345678', '048220007338'),
('KH-711-220124-001', N'Nguyễn Minh Đạt', '0921123456', '031223991711'),
('KH-480-210124-001', N'Lê Thị Bích', '0979456781', '001219995480'),
('KH-405-200124-001', N'Đinh Nhật Linh', '0739219562', '048202241405'),
('KH-569-200124-002', N'Huỳnh Hải Nam', '0375382517', '031182998569'),
('KH-983-200124-003', N'Ngô Hữu Hà', '0550613436', '079199832983'),
('KH-526-200124-004', N'Bùi Ngọc Thảo', '0739279625', '079302151526'),
('KH-872-200124-005', N'Phạm Ngọc Đức', '0706332877', '048201236872'),
('KH-160-200124-006', N'Nguyễn Thanh Tú', '0348271911', '001300998160'),
('KH-381-200124-007', N'Lê Minh Hưng', '0381092637', '001225991381'),
('KH-214-210124-001', N'Hoàng Hữu Phát', '0395834567', '048219004214'),
('KH-630-210124-002', N'Ngô Minh Thư', '0769231543', '031221005630'),
('KH-784-210124-003', N'Trần Thị Nhàn', '0932145634', '001301001784'),
('KH-913-210124-004', N'Vũ Hồng Kỳ', '0912384756', '079222994913'),
('KH-287-210124-005', N'Nguyễn Đình Quang', '0907631452', '048222001287'),
('KH-148-220124-001', N'Trần Nhật Hào', '0381763452', '001319000148'),
('KH-509-220124-002', N'Lê Thị Thùy Dương', '0976528712', '031225996509'),
('KH-732-220124-003', N'Đoàn Minh Hiếu', '0921843543', '079219004732'),
('KH-284-220124-004', N'Nguyễn Thị Xuân', '0362918273', '048224003284'),
('KH-456-220124-005', N'Bùi Hồng Sơn', '0942173645', '048319995456'),
('KH-601-230124-001', N'Phan Thị Yến', '0701423765', '001301006601'),
('KH-812-230124-002', N'Ngô Tuấn Vũ', '0765439821', '031217993812'),
('KH-364-230124-003', N'Lê Văn Trường', '0901273645', '079220994364'),
('KH-137-230124-004', N'Vũ Ngọc Hương', '0932876543', '048220996137'),
('KH-289-230124-005', N'Nguyễn Thanh Hà', '0912938475', '001323990289'),
('KH-478-230124-006', N'Đặng Thị Tuyết', '0962819384', '031224990478'),
('KH-901-240124-001', N'Hoàng Đình Tài', '0987651234', '079218994901'),
('KH-516-240124-002', N'Nguyễn Thị Ngọc', '0909271843', '048221002516'),
('KH-639-240124-003', N'Bùi Văn Huy', '0928317654', '001220007639'),
('KH-784-240124-004', N'Phan Thị Mai', '0971239845', '031221001784'),
('KH-247-240124-005', N'Ngô Văn Hạnh', '0938127364', '048222996247'),
('KH-315-250124-001', N'Trần Thị Tuyết Mai', '0765381745', '001225995315'),
('KH-801-250124-002', N'Lê Minh Hòa', '0942173846', '079223990801'),
('KH-934-250124-003', N'Nguyễn Thị Hồng', '0901982374', '048221993934'),
('KH-472-250124-004', N'Vũ Thị Hạnh', '0912736458', '001318001472'),
('KH-158-260124-001', N'Đỗ Hồng Quân', '0981324871', '031219999158'),
('KH-603-260124-002', N'Trần Thanh Bình', '0938123456', '048220994603'),
('KH-720-260124-003', N'Ngô Thị Yến Nhi', '0961234890', '079219996720'),
('KH-397-260124-004', N'Phạm Hữu Nhân', '0709273645', '001323998397'),
('KH-114-260124-005', N'Bùi Thị Hoa', '0912378456', '048224007114'),
('KH-268-270124-001', N'Nguyễn Hữu Lộc', '0921873456', '031218002268'),
('KH-429-270124-002', N'Lê Thị Ngân', '0942173648', '001221004429'),
('KH-753-270124-003', N'Trần Quốc Dũng', '0909273845', '048223993753'),
('KH-305-270124-004', N'Vũ Đình An', '0981376452', '079220002305'),
('KH-690-280124-001', N'Đinh Ngọc Diệp', '0912876345', '031219990690'),
('KH-872-280124-002', N'Nguyễn Thị Lan', '0967381234', '001319000872'),
('KH-234-280124-003', N'Hoàng Văn Hưng', '0909871234', '048221004234'),
('KH-148-280124-004', N'Phạm Hồng Nhung', '0975381273', '079219997148'),
('KH-521-290124-001', N'Lê Quốc Huy', '0938476521', '001323994521'),
('KH-693-290124-002', N'Trần Thị Kim Dung', '0909384721', '031220001693'),
('KH-305-290124-003', N'Nguyễn Văn Lâm', '0923481273', '048224999305'),
('KH-719-290124-004', N'Đỗ Minh Tâm', '0912736481', '079217998719'),
('KH-834-290124-005', N'Ngô Hữu Nghĩa', '0987623415', '001220994834'),
('KH-694-300124-001', N'Trần Quốc Việt', '0938127345', '048222996694'),
('KH-518-300124-002', N'Lê Thị Kim Ngân', '0909873124', '031220006518'),
('KH-387-300124-003', N'Nguyễn Hoàng Nam', '0967128432', '001322999387'),
('KH-249-300124-004', N'Đặng Thị Hiền', '0972183456', '079224996249'),
('KH-180-300124-005', N'Phan Minh Châu', '0923847365', '048219007180'),
('KH-831-300124-006', N'Hoàng Thanh Bình', '0912375842', '001301999831'),
('KH-374-310124-001', N'Ngô Hữu Tài', '0987138462', '079218006374'),
('KH-652-310124-002', N'Vũ Thị Thanh', '0908374126', '048223006652'),
('KH-109-310124-003', N'Trần Đình Huy', '0921837465', '001319006109'),
('KH-475-310124-004', N'Nguyễn Ngọc Thảo', '0942173648', '031224007475'),
('KH-206-310124-005', N'Bùi Đức Trọng', '0971827364', '079217994206'),
('KH-718-310124-006', N'Lê Hồng Nhung', '0938712645', '048225996718'),
('KH-291-010224-001', N'Nguyễn Thị Thu Hà', '0909723487', '001323004291'),
('KH-624-010224-002', N'Phạm Hữu Đạt', '0912873649', '079221999624'),
('KH-842-010224-003', N'Trần Nhật Hoàng', '0968723645', '048220007842'),
('KH-397-010224-004', N'Đoàn Thị Như Quỳnh', '0923847163', '001300995397'),
('KH-105-010224-005', N'Ngô Văn Toàn', '0908374632', '031225005105'),
('KH-789-010224-006', N'Lý Hoàng Minh', '0938712365', '079220998789'),
('KH-524-010224-007', N'Trịnh Thị Duyên', '0987263412', '048221007524'),
('KH-342-020224-001', N'Phan Văn Lực', '0908127345', '001323994342'),
('KH-609-020224-002', N'Nguyễn Thị Kiều Trang', '0972381764', '031218996609'),
('KH-458-020224-003', N'Đặng Trọng Nhân', '0912873645', '048223999458'),
('KH-703-020224-004', N'Lê Thanh Ngọc', '0963821743', '079221996703'),
('KH-816-020224-005', N'Vũ Hoài Thương', '0938476523', '001301995816'),
('KH-280-030224-001', N'Nguyễn Hữu Nghĩa', '0921387462', '048224993280'),
('KH-134-030224-002', N'Hoàng Thị Lan', '0909283471', '001219005134'),
('KH-761-030224-003', N'Trần Minh Tâm', '0912387462', '031223005761'),
('KH-487-030224-004', N'Ngô Nhật Hào', '0972183746', '079223005487'),
('KH-673-030224-005', N'Phạm Thị Thanh Hương', '0938126345', '048225004673'),
('KH-362-030224-006', N'Lê Văn Đạt', '0962378145', '001319006362'),
('KH-509-040224-001', N'Nguyễn Thị Mỹ Linh', '0908273615', '031221003509'),
('KH-218-040224-002', N'Trần Quốc Đạt', '0981234765', '001220002218'),
('KH-867-040224-003', N'Phan Thị Mai Hương', '0912837465', '079222994867'),
('KH-430-040224-004', N'Đỗ Văn Hoàng', '0942837465', '048224998430'),
('KH-739-040224-005', N'Nguyễn Thị Duyên', '0923847162', '001301005739'),
('KH-195-040224-006', N'Vũ Trọng Tấn', '0961837462', '031218001195'),
('KH-622-050224-001', N'Bùi Hồng Sơn', '0932187463', '079221004622'),
('KH-318-050224-002', N'Trần Thị Hạnh', '0908723645', '048223007318'),
('KH-483-050224-003', N'Lê Văn Nghĩa', '0912837465', '001323999483'),
('KH-708-050224-004', N'Nguyễn Hữu Dũng', '0942134765', '031220006708'),
('KH-891-050224-005', N'Phan Thị Hoàng Yến', '0972381462', '048221997891'),
('KH-367-060224-001', N'Ngô Minh Thắng', '0923847461', '079220007367'),
('KH-259-060224-002', N'Vũ Thị Lệ Quyên', '0961823746', '001322004259'),
('KH-685-060224-003', N'Trịnh Văn Long', '0901283764', '048225005685'),
('KH-126-060224-004', N'Nguyễn Thị Như Quỳnh', '0912837462', '031224996126'),
('KH-418-060224-005', N'Hoàng Thanh Tùng', '0972813746', '001318001418'),
('KH-542-060224-006', N'Đặng Thị Thanh Huyền', '0938471623', '079223001542'),
('KH-214-070224-001', N'Nguyễn Thị Hồng Gấm', '0938273645', '001319005214'),
('KH-761-070224-002', N'Lê Văn Thịnh', '0909827364', '048224004761'),
('KH-385-070224-003', N'Trần Thị Kim Yến', '0961827364', '031222999385'),
('KH-498-070224-004', N'Phạm Hữu Nghĩa', '0928374652', '079218998498'),
('KH-670-070224-005', N'Đỗ Thị Thanh Hà', '0912873645', '001301007670'),
('KH-153-080224-001', N'Nguyễn Quốc Cường', '0902837465', '048221007153'),
('KH-487-080224-002', N'Lê Thị Thu Thủy', '0948273641', '001323006487'),
('KH-731-080224-003', N'Vũ Minh Khang', '0961283745', '031220004731'),
('KH-214-080224-004', N'Trần Văn Hiếu', '0982736451', '079224005214'),
('KH-638-080224-005', N'Hoàng Thị Tuyết Mai', '0938127364', '048225005638'),
('KH-320-080224-006', N'Ngô Văn Lộc', '0912837462', '001322006320'),
('KH-489-090224-001', N'Phan Thị Tuyền', '0909283746', '031223997489'),
('KH-764-090224-002', N'Trịnh Văn Đông', '0938127364', '079219994764'),
('KH-291-090224-003', N'Nguyễn Hữu Phát', '0921387462', '001300006291'),
('KH-670-090224-004', N'Đinh Thị Bích Vân', '0981237465', '048224999670'),
('KH-547-090224-005', N'Phạm Văn Tuấn', '0902378465', '031225996547'),
('KH-183-100224-001', N'Đặng Thanh Hùng', '0972837465', '001318005183'),
('KH-924-100224-002', N'Nguyễn Thị Nhàn', '0932173645', '079222007924'),
('KH-308-100224-003', N'Trần Quốc Hưng', '0921387452', '048220004308'),
('KH-492-100224-004', N'Vũ Thị Như Hoa', '0961827346', '001301006492'),
('KH-715-100224-005', N'Lê Hồng Quân', '0908723641', '031218996715'),
('KH-367-100224-006', N'Phan Thị Quỳnh Anh', '0912378462', '048223007367'),
('KH-280-100224-007', N'Nguyễn Thanh Tùng', '0981237456', '001323994280'),
('KH-681-110224-001', N'Đỗ Minh Đức', '0962378146', '001322006681'),
('KH-354-110224-002', N'Trịnh Thị Thanh Trúc', '0908127345', '048225005354'),
('KH-720-110224-003', N'Lê Anh Tuấn', '0938471623', '031224007720'),
('KH-198-110224-004', N'Vũ Thị Bảo Ngọc', '0912837462', '001319005198'),
('KH-583-110224-005', N'Ngô Quốc Việt', '0923847162', '079220997583'),
('KH-427-110224-006', N'Nguyễn Thị Tuyết Hương', '0972183746', '048222999427'),
('KH-291-120224-001', N'Nguyễn Thị Ngọc Mai', '0938123456', '001319002291'),
('KH-764-120224-002', N'Phạm Văn Đức', '0918234567', '079221004764'),
('KH-839-120224-003', N'Lê Thị Ngọc Diệp', '0971234567', '048225006839'),
('KH-715-120224-004', N'Trần Hữu Thắng', '0921234567', '031218997715'),
('KH-426-120224-005', N'Đinh Thị Bích Thủy', '0909123456', '001323006426'),
('KH-307-120224-006', N'Vũ Minh Phương', '0968123456', '048224005307'),
('KH-978-120224-007', N'Ngô Quang Vinh', '0989123456', '079220004978'),
('KH-182-120224-008', N'Nguyễn Văn Thái', '0911123456', '031221006182'),
('KH-550-130224-001', N'Phan Thị Mỹ Linh', '0932123456', '001300005550'),
('KH-667-130224-002', N'Lê Hồng Hạnh', '0907123456', '048222007667'),
('KH-903-130224-003', N'Trần Quốc Dũng', '0977123456', '079219004903'),
('KH-184-130224-004', N'Nguyễn Thị Hồng Nhung', '0919123456', '001322004184'),
('KH-350-130224-005', N'Đặng Minh Khoa', '0929123456', '031223006350'),
('KH-761-130224-006', N'Phạm Thanh Vân', '0964123456', '048225003761'),
('KH-289-130224-007', N'Ngô Trọng Tín', '0987123456', '001318007289'),
('KH-470-130224-008', N'Lê Văn Lâm', '0939123456', '079224006470'),
('KH-318-130224-009', N'Trần Thị Kim Tuyến', '0906123456', '031222994318'),
('KH-285-140224-001', N'Nguyễn Thị Tuyết Mai', '0911123478', '001323007285'),
('KH-364-140224-002', N'Phạm Hữu Duy', '0938223478', '048221006364'),
('KH-947-140224-003', N'Lê Minh Thắng', '0967823478', '031218005947'),
('KH-710-140224-004', N'Ngô Thanh Hà', '0971323478', '079225004710'),
('KH-157-140224-005', N'Trịnh Văn Sơn', '0917823478', '001322994157'),
('KH-804-140224-006', N'Vũ Thị Diễm Hương', '0928323478', '048224006804'),
('KH-329-140224-007', N'Nguyễn Quốc Huy', '0987323478', '031219994329'),
('KH-493-140224-008', N'Đặng Thị Kim Liên', '0909323478', '001320005493'),
('KH-759-150224-001', N'Phan Văn Thịnh', '0938123489', '048220004759'),
('KH-235-150224-002', N'Lê Thị Phương Anh', '0918123489', '001319005235'),
('KH-678-150224-003', N'Nguyễn Văn Tài', '0978123489', '079224996678'),
('KH-364-150224-004', N'Trần Thị Hạnh', '0908123489', '031220006364'),
('KH-901-150224-005', N'Đỗ Hữu Nghĩa', '0968123489', '048223995901'),
('KH-138-150224-006', N'Nguyễn Thanh Lam', '0919123489', '001301005138'),
('KH-421-150224-007', N'Vũ Thị Thu Trang', '0929123489', '031221007421'),
('KH-587-150224-008', N'Phạm Quốc Cường', '0981123489', '048225006587'),
('KH-314-150224-009', N'Ngô Kim Anh', '0909123489', '001323997314'),
('KH-286-150224-010', N'Lê Thị Bảo Hân', '0937123489', '079223005286'),
('KH-805-160224-001', N'Trần Hữu Trí', '0912123490', '001322997805'),
('KH-394-160224-002', N'Nguyễn Thị Bích Trâm', '0928123490', '048224005394'),
('KH-278-160224-003', N'Phan Minh Quân', '0908123490', '031225006278'),
('KH-507-160224-004', N'Đặng Thị Ánh Tuyết', '0978123490', '001319006507'),
('KH-693-160224-005', N'Vũ Quốc Đạt', '0967123490', '048221997693'),
('KH-124-160224-006', N'Lê Thanh Hiền', '0919123490', '031220007124'),
('KH-379-160224-007', N'Nguyễn Thị Ngọc Hân', '0938123490', '079220004379'),
('KH-840-160224-008', N'Ngô Văn Lợi', '0989123490', '001323996840'),
('KH-643-160224-009', N'Phạm Thị Như Quỳnh', '0908123490', '048225007643'),
('KH-265-160224-010', N'Đinh Văn Long', '0912123490', '031222006265'),
('KH-347-170224-001', N'Nguyễn Văn Hậu', '0913123491', '001322997347'),
('KH-591-170224-002', N'Trần Thị Mỹ Duyên', '0939123491', '048225006591'),
('KH-218-170224-003', N'Lê Minh Tân', '0928123491', '031223007218'),
('KH-879-170224-004', N'Phạm Hồng Nhung', '0978123491', '001319996879'),
('KH-642-170224-005', N'Vũ Quốc Cường', '0968123491', '048221994642'),
('KH-375-170224-006', N'Ngô Thị Hồng Hạnh', '0909123491', '031220005375'),
('KH-210-170224-007', N'Đặng Văn Tuấn', '0918123491', '079224006210'),
('KH-903-170224-008', N'Nguyễn Thị Lan Hương', '0938123491', '001321007903'),
('KH-487-180224-001', N'Lê Hữu Phước', '0987123492', '048223996487'),
('KH-126-180224-002', N'Phạm Thị Mai', '0907123492', '001322007126'),
('KH-712-180224-003', N'Trần Minh Hiếu', '0917123492', '031218007712'),
('KH-864-180224-004', N'Ngô Hồng Nhung', '0971123492', '079220005864'),
('KH-358-180224-005', N'Đinh Thị Hằng', '0969123492', '001319996358'),
('KH-493-180224-006', N'Nguyễn Quốc Đạt', '0929123492', '048224007493'),
('KH-230-180224-007', N'Lê Văn Long', '0931123492', '031222007230'),
('KH-607-180224-008', N'Trần Thị Bích Hường', '0909123492', '001323005607'),
('KH-782-190224-001', N'Phan Văn Giang', '0912123493', '079225007782'),
('KH-143-190224-002', N'Nguyễn Thị Kim Yến', '0928123493', '001320005143'),
('KH-468-190224-003', N'Vũ Hữu Thành', '0938123493', '048222997468'),
('KH-325-190224-004', N'Lê Thị Diễm Hương', '0908123493', '031221007325'),
('KH-999-190224-005', N'Ngô Minh Tâm', '0978123493', '001321997999'),
('KH-517-190224-006', N'Trần Thị Thảo', '0968123493', '048225005517'),
('KH-281-190224-007', N'Phạm Văn Hòa', '0918123493', '031224007281'),
('KH-630-190224-008', N'Đinh Thị Thu Hằng', '0989123493', '001322996630'),
('KH-719-190224-009', N'Nguyễn Hữu Nhân', '0929123493', '079223007719'),
('KH-382-200224-001', N'Lê Thanh Tú', '0913123494', '001319996382'),
('KH-674-200224-002', N'Trần Thị Kim Chi', '0909123494', '048221007674'),
('KH-230-200224-003', N'Nguyễn Đức Anh', '0979123494', '031223007230'),
('KH-816-200224-004', N'Phạm Thị Thúy Hằng', '0969123494', '079224006816'),
('KH-499-200224-005', N'Đỗ Văn Khoa', '0938123494', '001323997499'),
('KH-127-200224-006', N'Lê Minh Ngọc', '0912123494', '048224005127'),
('KH-358-200224-007', N'Nguyễn Thị Hoài Thu', '0908123494', '031220006358'),
('KH-968-200224-008', N'Trần Quốc Hưng', '0989123494', '001321996968'),
('KH-205-200224-009', N'Ngô Thị Hồng Loan', '0928123494', '048225007205'),
('KH-741-210224-001', N'Phạm Văn Tài', '0978123495', '031222994741'),
('KH-392-210224-002', N'Nguyễn Thị Thanh Trúc', '0908123495', '001320006392'),
('KH-564-210224-003', N'Trần Hữu Minh', '0918123495', '048224005564'),
('KH-832-210224-004', N'Lê Thị Mai Hương', '0938123495', '031218007832'),
('KH-317-210224-005', N'Ngô Thị Cẩm Vân', '0969123495', '001319006317'),
('KH-670-210224-006', N'Phạm Quốc Thắng', '0929123495', '079223994670'),
('KH-289-210224-007', N'Nguyễn Thanh Phong', '0989123495', '048221007289'),
('KH-156-210224-008', N'Trần Thị Thu Hà', '0909123495', '001323006156'),
('KH-417-210224-009', N'Lê Hữu Nghĩa', '0917123495', '031219006417'),
('KH-784-210224-010', N'Đặng Thị Hồng Gấm', '0932123495', '048223005784'),
('KH-294-220224-001', N'Nguyễn Văn Toàn', '0912345621', '001320996294'),
('KH-871-220224-002', N'Trần Thị Kim Liên', '0938456721', '031223006871'),
('KH-160-220224-003', N'Lê Hữu Trí', '0909654781', '048224996160'),
('KH-473-220224-004', N'Phạm Thị Cẩm Nhung', '0967893412', '079225007473'),
('KH-528-220224-005', N'Ngô Văn Hùng', '0978945631', '001322997528'),
('KH-614-220224-006', N'Đỗ Thị Thảo Vy', '0987654321', '031221007614'),
('KH-347-220224-007', N'Nguyễn Hữu Duy', '0918934567', '048223007347'),
('KH-785-220224-008', N'Trần Thị Thu Cúc', '0909345671', '001319997785'),
('KH-912-220224-009', N'Phan Văn Lực', '0938123456', '079224006912'),
('KH-203-230224-001', N'Nguyễn Quốc Việt', '0912983746', '001323997203'),
('KH-658-230224-002', N'Lê Thị Hồng Nhung', '0909182736', '048222996658'),
('KH-431-230224-003', N'Vũ Minh Tân', '0928934561', '031224007431'),
('KH-156-230224-004', N'Trần Thanh Hằng', '0987345216', '001320006156'),
('KH-389-230224-005', N'Ngô Văn Tuấn', '0938456723', '048223006389'),
('KH-627-230224-006', N'Đặng Thị Kim Dung', '0919238475', '031219007627'),
('KH-281-230224-007', N'Phạm Văn Minh', '0978234567', '001321007281'),
('KH-304-230224-008', N'Lê Thị Thúy Hằng', '0909238457', '079223006304'),
('KH-709-230224-009', N'Nguyễn Quốc Hùng', '0912345763', '048224007709'),
('KH-345-240224-001', N'Trần Thị Ngọc Lan', '0938123499', '001322996345'),
('KH-562-240224-002', N'Phạm Văn Lâm', '0912873456', '031221007562'),
('KH-684-240224-003', N'Lê Thị Như Quỳnh', '0909345781', '048225006684'),
('KH-229-240224-004', N'Ngô Văn Khánh', '0978123945', '001319996229'),
('KH-413-240224-005', N'Đặng Thị Thanh Huyền', '0967234591', '079225005413'),
('KH-758-240224-006', N'Nguyễn Hoàng Anh', '0928934567', '001320007758'),
('KH-132-240224-007', N'Trần Văn Hòa', '0912834567', '031222007132'),
('KH-605-240224-008', N'Phạm Thị Thu Hà', '0938123451', '048221996605'),
('KH-926-240224-009', N'Lê Quang Trường', '0987456721', '001319007926'),
('KH-174-250224-001', N'Nguyễn Hữu Tài', '0912938475', '079224006174'),
('KH-388-250224-002', N'Trần Thị Bích Vân', '0938123492', '001322996388'),
('KH-753-250224-003', N'Đặng Văn Phong', '0909874561', '031220006753'),
('KH-601-250224-004', N'Lê Thị Mai Anh', '0912345672', '048223006601'),
('KH-842-250224-005', N'Ngô Thị Hồng Ánh', '0978934561', '001321007842'),
('KH-291-250224-006', N'Phan Văn Bảo', '0928745631', '031223005291'),
('KH-430-250224-007', N'Vũ Minh Hằng', '0909837456', '048222996430'),
('KH-308-250224-008', N'Nguyễn Thị Thuỳ Trang', '0987123456', '001320007308'),
('KH-679-250224-009', N'Trần Văn Hạnh', '0912837456', '079223006679'),
('KH-574-250224-010', N'Phạm Thị Cẩm Tiên', '0938456722', '001319997574'),
('KH-185-260224-001', N'Lê Hữu Nghĩa', '0912834561', '048224007185'),
('KH-723-260224-002', N'Nguyễn Thị Hồng Tươi', '0909238475', '001322996723'),
('KH-294-260224-003', N'Trần Minh Cường', '0912938471', '031219006294'),
('KH-516-260224-004', N'Phạm Thị Kim Oanh', '0987654312', '001320006516'),
('KH-370-260224-005', N'Lê Văn Trí', '0967345217', '048223006370'),
('KH-879-260224-006', N'Ngô Thị Lệ Quyên', '0912837457', '001321006879'),
('KH-467-260224-007', N'Nguyễn Quốc Thái', '0938123453', '031224007467'),
('KH-134-260224-008', N'Trần Thị Tuyết Mai', '0909874563', '079225007134'),
('KH-608-260224-009', N'Phan Thị Kiều', '0978234562', '048221006608'),
('KH-352-260224-010', N'Đỗ Văn Lâm', '0912983457', '001319997352'),
('KH-712-270224-001', N'Nguyễn Hữu Nghĩa', '0909872345', '001322007712'),
('KH-493-270224-002', N'Trần Thị Bảo Trân', '0912839475', '031219997493'),
('KH-821-270224-003', N'Lê Văn Nam', '0928934563', '048224006821'),
('KH-154-270224-004', N'Ngô Thị Hồng Nhung', '0978123456', '001321007154'),
('KH-375-270224-005', N'Phạm Văn Khoa', '0909456123', '001320007375'),
('KH-684-270224-006', N'Đặng Thị Cẩm Tú', '0987456723', '048223007684'),
('KH-232-270224-007', N'Nguyễn Quốc Bảo', '0938123459', '031221006232'),
('KH-503-270224-008', N'Trần Thị Mỹ Linh', '0912839476', '079224006503'),
('KH-947-270224-009', N'Lê Minh Thành', '0909238476', '001319997947'),
('KH-138-280224-001', N'Nguyễn Thị Bích Hạnh', '0987654323', '001322996138'),
('KH-618-280224-002', N'Phan Văn Tuấn', '0912837465', '031220007618'),
('KH-435-280224-003', N'Trần Thị Lệ Hằng', '0909456723', '048221006435'),
('KH-789-280224-004', N'Ngô Văn Hùng', '0912345673', '001320007789'),
('KH-274-280224-005', N'Phạm Thị Kim Chi', '0978945612', '079223007274'),
('KH-350-280224-006', N'Lê Hữu Dũng', '0967893421', '001321007350'),
('KH-681-280224-007', N'Trần Thị Mỹ Duyên', '0912837453', '031222007681'),
('KH-927-280224-008', N'Nguyễn Văn Khang', '0938123498', '048223007927'),
('KH-519-280224-009', N'Phạm Minh Hoàng', '0909872346', '001319997519'),
('KH-241-290224-001', N'Lê Thị Như Quỳnh', '0909238474', '001322997241'),
('KH-482-290224-002', N'Trần Hữu Nghĩa', '0912839478', '031223006482'),
('KH-653-290224-003', N'Ngô Thị Thanh Mai', '0938123454', '048224006653'),
('KH-132-290224-004', N'Phạm Văn Hòa', '0909874564', '001321007132'),
('KH-381-290224-005', N'Nguyễn Thị Hồng Nhung', '0978934564', '001319997381'),
('KH-703-290224-006', N'Trần Quốc Trung', '0912983745', '079225007703'),
('KH-578-290224-007', N'Đặng Văn Lộc', '0987654315', '048222007578'),
('KH-804-290224-008', N'Lê Thị Minh Tuyết', '0909238465', '001320007804'),
('KH-159-290224-009', N'Nguyễn Hữu Tấn', '0938456729', '001319007159'),
('KH-476-010324-001', N'Phạm Thị Bảo Yến', '0912837451', '001322007476'),
('KH-825-010324-002', N'Ngô Văn Thành', '0909874568', '048223006825'),
('KH-243-010324-003', N'Trần Thị Mai Anh', '0967893424', '001320007243'),
('KH-639-010324-004', N'Phạm Quốc Khánh', '0912839477', '031222006639'),
('KH-912-010324-005', N'Lê Hữu Phúc', '0928734567', '001321007912'),
('KH-308-010324-006', N'Trần Thị Hồng Vân', '0909345782', '079223006308'),
('KH-783-010324-007', N'Nguyễn Văn Hưng', '0938123497', '048224006783'),
('KH-527-010324-008', N'Đỗ Thị Mỹ Linh', '0978945633', '001319997527'),
('KH-167-010324-009', N'Trần Hữu Đạt', '0987123498', '001320007167'),
('KH-391-010324-010', N'Phan Thị Xuân', '0912837452', '031221007391'),
('KH-710-020324-001', N'Nguyễn Thị Minh Thư', '0909872349', '001322007710'),
('KH-252-020324-002', N'Lê Văn Duy', '0967893422', '048222006252'),
('KH-630-020324-003', N'Phạm Thị Quỳnh Hoa', '0912839470', '001321007630'),
('KH-853-020324-004', N'Ngô Hữu Phát', '0909238470', '031223006853'),
('KH-407-020324-005', N'Trần Thị Thuỳ Trang', '0938123452', '001320007407'),
('KH-149-020324-006', N'Nguyễn Minh Đạt', '0978934565', '001319996149'),
('KH-698-020324-007', N'Lê Thị Hồng Diễm', '0928734561', '048221007698'),
('KH-384-020324-008', N'Phan Văn Hào', '0912345674', '001322007384'),
('KH-594-020324-009', N'Trần Thị Cẩm Nhung', '0909345780', '079225007594'),
('KH-217-020324-010', N'Nguyễn Hữu Tường', '0987654324', '001319006217');
Go

--Thêm dữ liệu khuyến mãi

INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, heSoKhuyenMai, ngayBatDau, ngayKetThuc, tongTienToiThieu, tienKhuyenMaiToiDa, trangThai)
VALUES
('KM-05032024-001', N'Mừng 8/3 - Giảm giá vé', 0.2, '2024-03-06', '2024-03-10', 300000, 70000, N'Đã hết hạn'),
('KM-27032024-001', N'Ưu đãi cuối tháng 3', 0.25, '2024-03-28', '2024-03-31', 400000, 90000, N'Đã hết hạn'),
('KM-01042024-001', N'Ưu đãi tháng 4', 0.3, '2024-04-02', '2024-04-10', 350000, 100000, N'Đã hết hạn'),
('KM-29042024-001', N'Giảm giá dịp lễ 30/4', 0.4, '2024-04-30', '2024-05-02', 500000, 120000, N'Đã hết hạn'),
('KM-10052024-001', N'Ưu đãi giữa tháng 5', 0.15, '2024-05-11', '2024-05-20', 250000, 60000, N'Đã hết hạn'),
('KM-31052024-001', N'Ưu đãi cuối tháng 5', 0.25, '2024-06-01', '2024-06-05', 300000, 80000, N'Đã hết hạn'),
('KM-01062024-001', N'Quốc tế thiếu nhi - Vé ưu đãi', 0.35, '2024-06-01', '2024-06-03', 400000, 110000, N'Đã hết hạn'),
('KM-20062024-001', N'Hè rực rỡ - Vé siêu rẻ', 0.3, '2024-06-21', '2024-06-30', 450000, 100000, N'Đã hết hạn'),
('KM-01072024-001', N'Khuyến mãi tháng 7', 0.2, '2024-07-01', '2024-07-10', 300000, 90000, N'Đã hết hạn'),
('KM-27072024-001', N'Ưu đãi cuối tháng 7', 0.25, '2024-07-28', '2024-07-31', 350000, 100000, N'Đã hết hạn'),
('KM-01082024-001', N'Giảm giá tháng 8', 0.3, '2024-08-02', '2024-08-08', 300000, 85000, N'Đã hết hạn'),
('KM-01092024-001', N'Chào thu - Vé ưu đãi', 0.3, '2024-09-01', '2024-09-05', 450000, 120000, N'Đã hết hạn'),
('KM-20092024-001', N'Mid-autumn Special', 0.4, '2024-09-21', '2024-09-25', 400000, 150000, N'Đã hết hạn'),
('KM-01012024-001', N'Chào năm mới 2024', 0.35, '2024-01-02', '2024-01-05', 500000, 130000, N'Đã hết hạn'),
('KM-28012024-001', N'Tết cận kề - Ưu đãi vé', 0.3, '2024-01-29', '2024-02-05', 600000, 150000, N'Đã hết hạn'),
('KM-08022024-001', N'Mừng xuân Giáp Thìn', 0.4, '2024-02-09', '2024-02-14', 700000, 180000, N'Đã hết hạn'),
('KM-01032024-001', N'Tháng 3 rực rỡ', 0.2, '2024-03-02', '2024-03-08', 350000, 90000, N'Đã hết hạn'),
('KM-01042025-001', N'Mừng tháng 4/2025', 0.25, '2025-04-01', '2025-04-07', 400000, 100000, N'Còn hiệu lực'),
('KM-05042025-001', N'Đặc biệt tuần đầu tháng 4', 0.2, '2025-04-06', '2025-04-10', 300000, 90000, N'Còn hiệu lực'),
('KM-08042025-001', N'Ưu đãi đầu tuần', 0.15, '2025-04-08', '2025-04-12', 250000, 80000, N'Còn hiệu lực'),
('KM-09042025-001', N'Ve vé rẻ tháng 4', 0.2, '2025-04-09', '2025-04-15', 300000, 95000, N'Còn hiệu lực'),
('KM-10042025-001', N'Ưu đãi giữa tháng 4', 0.2, '2025-04-11', '2025-04-17', 400000, 90000, N'Còn hiệu lực'),
('KM-11042025-001', N'Ve vé tiết kiệm tháng 4', 0.25, '2025-04-12', '2025-04-18', 350000, 95000, N'Còn hiệu lực'),
('KM-12042025-001', N'Khuyến mãi ngày 13/4', 0.3, '2025-04-13', '2025-04-16', 500000, 120000, N'Còn hiệu lực'),
('KM-13042025-001', N'Ưu đãi cuối tuần', 0.15, '2025-04-14', '2025-04-19', 300000, 85000, N'Còn hiệu lực'),
('KM-14042025-001', N'Ve vé gia đình', 0.2, '2025-04-15', '2025-04-20', 400000, 100000, N'Còn hiệu lực'),
('KM-15042025-001', N'Khuyến mãi mùa hè sớm', 0.3, '2025-04-16', '2025-04-22', 600000, 150000, N'Còn hiệu lực'),
('KM-16042025-001', N'Tuần lễ tri ân khách hàng', 0.35, '2025-04-17', '2025-04-24', 700000, 160000, N'Còn hiệu lực'),
('KM-17042025-001', N'Tặng vé ưu đãi tháng 4', 0.25, '2025-04-18', '2025-04-22', 450000, 100000, N'Còn hiệu lực'),
('KM-18042025-001', N'Ve vé ưu đãi học sinh sinh viên', 0.3, '2025-04-19', '2025-04-25', 300000, 80000, N'Còn hiệu lực'),
('KM-19042025-001', N'Chuyến đi mùa xuân', 0.2, '2025-04-20', '2025-04-26', 500000, 120000, N'Còn hiệu lực'),
('KM-20042025-001', N'Ưu đãi tháng tư cuối cùng', 0.25, '2025-04-21', '2025-04-27', 400000, 95000, N'Còn hiệu lực'),
('KM-21042025-001', N'Ve vé lễ 30/4 sớm', 0.4, '2025-04-22', '2025-04-30', 800000, 180000, N'Còn hiệu lực'),
('KM-22042025-001', N'Mua vé 1 được 1 giảm 50%', 0.5, '2025-04-23', '2025-04-30', 600000, 150000, N'Còn hiệu lực'),
('KM-23042025-001', N'Ve vé ưu đãi nhóm bạn', 0.3, '2025-04-24', '2025-04-29', 550000, 120000, N'Còn hiệu lực'),
('KM-24042025-001', N'Khuyến mãi 29/4', 0.35, '2025-04-25', '2025-04-29', 700000, 160000, N'Còn hiệu lực'),
('KM-25042025-001', N'Tiệc vé 30/4', 0.4, '2025-04-26', '2025-04-30', 800000, 180000, N'Còn hiệu lực'),
('KM-26042025-001', N'Lễ 30/4 – vé rẻ mỗi ngày', 0.5, '2025-04-27', '2025-04-30', 600000, 170000, N'Còn hiệu lực'),
('KM-27042025-001', N'Vui lễ – vé siêu rẻ', 0.3, '2025-04-28', '2025-04-30', 500000, 120000, N'Còn hiệu lực'),
('KM-28042025-001', N'Ưu đãi mừng đại lễ', 0.35, '2025-04-29', '2025-05-01', 700000, 160000, N'Còn hiệu lực'),
('KM-29042025-001', N'Vé rẻ đón 1/5', 0.4, '2025-04-30', '2025-05-01', 800000, 180000, N'Còn hiệu lực'),
('KM-15012022-001', N'Xuân Rộn Ràng 2022', 0.3, '2022-01-20', '2022-02-10', 500000, 120000, N'Đã hết hạn'),
('KM-01032022-001', N'Mừng 8/3 Rực Rỡ', 0.25, '2022-03-05', '2022-03-15', 300000, 80000, N'Đã hết hạn'),
('KM-20042022-001', N'Tết Độc Lập 30/4', 0.2, '2022-04-25', '2022-05-05', 400000, 100000, N'Đã hết hạn'),
('KM-01062022-001', N'Quốc Tế Thiếu Nhi', 0.35, '2022-06-01', '2022-06-10', 250000, 130000, N'Đã hết hạn'),
('KM-20072022-001', N'Hè Rực Nắng 2022', 0.15, '2022-07-22', '2022-08-01', 600000, 90000, N'Đã hết hạn'),
('KM-01092022-001', N'Chào Thu Tháng 9', 0.2, '2022-09-01', '2022-09-10', 350000, 110000, N'Đã hết hạn'),
('KM-20102022-001', N'Chào Halloween 2022', 0.3, '2022-10-25', '2022-10-31', 450000, 150000, N'Đã hết hạn'),
('KM-10112022-001', N'Tri Ân Nhà Giáo', 0.25, '2022-11-15', '2022-11-25', 300000, 95000, N'Đã hết hạn'),
('KM-01122022-001', N'Đón Giáng Sinh 2022', 0.4, '2022-12-05', '2022-12-31', 700000, 180000, N'Đã hết hạn'),
('KM-01012023-001', N'Năm Mới Bùng Nổ 2023', 0.35, '2023-01-02', '2023-01-15', 550000, 140000, N'Đã hết hạn'),
('KM-15022023-001', N'Valentine Ngọt Ngào', 0.3, '2023-02-14', '2023-02-20', 400000, 120000, N'Đã hết hạn'),
('KM-01042023-001', N'Tháng Tư Tưng Bừng', 0.2, '2023-04-02', '2023-04-12', 300000, 80000, N'Đã hết hạn'),
('KM-25052023-001', N'Hè Mới Ưu Đãi', 0.15, '2023-05-27', '2023-06-10', 450000, 90000, N'Đã hết hạn'),
('KM-01072023-001', N'Ưu Đãi Mùa Mưa', 0.2, '2023-07-05', '2023-07-15', 500000, 110000, N'Đã hết hạn'),
('KM-01082023-001', N'Mùa Vu Lan Ấm Áp', 0.3, '2023-08-05', '2023-08-12', 400000, 125000, N'Đã hết hạn'),
('KM-15102023-001', N'Lễ Hội Hóa Trang', 0.4, '2023-10-20', '2023-10-31', 600000, 170000, N'Đã hết hạn'),
('KM-01112023-001', N'Mùa Lễ Hội Bắt Đầu', 0.35, '2023-11-05', '2023-11-20', 750000, 180000, N'Đã hết hạn'),
('KM-01052025-001', N'Chào tháng 5 – Vé siêu rẻ', 0.3, '2025-05-01', '2025-05-07', 500000, 130000, N'Còn hiệu lực'),
('KM-02052025-001', N'Ưu đãi đầu tháng 5', 0.25, '2025-05-02', '2025-05-08', 400000, 100000, N'Còn hiệu lực'),
('KM-03052025-001', N'Tháng 5 – Vé gia đình', 0.2, '2025-05-03', '2025-05-10', 450000, 95000, N'Còn hiệu lực'),
('KM-04052025-001', N'Ve vé học sinh tháng 5', 0.3, '2025-05-04', '2025-05-11', 300000, 90000, N'Còn hiệu lực'),
('KM-05052025-001', N'Tuần lễ khuyến mãi tháng 5', 0.35, '2025-05-05', '2025-05-12', 600000, 150000, N'Còn hiệu lực'),
('KM-06052025-001', N'Tri ân khách hàng tháng 5', 0.25, '2025-05-06', '2025-05-13', 400000, 100000, N'Còn hiệu lực'),
('KM-07052025-001', N'Vui hè cùng vé rẻ', 0.3, '2025-05-07', '2025-05-14', 500000, 120000, N'Còn hiệu lực'),
('KM-08052025-001', N'Khuyến mãi đầu hè', 0.4, '2025-05-08', '2025-05-15', 700000, 160000, N'Còn hiệu lực'),
('KM-09052025-001', N'Tháng 5 rực rỡ', 0.2, '2025-05-09', '2025-05-16', 350000, 80000, N'Còn hiệu lực'),
('KM-10052025-001', N'Mua vé – Nhận ưu đãi', 0.25, '2025-05-10', '2025-05-17', 400000, 90000, N'Còn hiệu lực'),
('KM-11052025-001', N'Ve vé tiết kiệm gia đình', 0.3, '2025-05-11', '2025-05-18', 500000, 110000, N'Còn hiệu lực'),
('KM-12052025-001', N'Khuyến mãi nhóm bạn', 0.2, '2025-05-12', '2025-05-19', 300000, 85000, N'Còn hiệu lực'),
('KM-13052025-001', N'Tặng bạn – Vé ưu đãi', 0.25, '2025-05-13', '2025-05-20', 350000, 95000, N'Còn hiệu lực'),
('KM-14052025-001', N'Tháng 5 – Đi càng đông càng rẻ', 0.3, '2025-05-14', '2025-05-21', 450000, 120000, N'Còn hiệu lực'),
('KM-15052025-001', N'Ưu đãi vé dịp giữa tháng', 0.2, '2025-05-15', '2025-05-22', 400000, 90000, N'Còn hiệu lực'),
('KM-16052025-001', N'Giảm sâu – Vé hè', 0.35, '2025-05-16', '2025-05-23', 600000, 150000, N'Còn hiệu lực'),
('KM-17052025-001', N'Tháng 5 yêu thương', 0.3, '2025-05-17', '2025-05-24', 500000, 120000, N'Còn hiệu lực'),
('KM-18052025-001', N'Tuần lễ vé ưu đãi', 0.25, '2025-05-18', '2025-05-25', 400000, 100000, N'Còn hiệu lực'),
('KM-19052025-001', N'Ve vé hè siêu hời', 0.4, '2025-05-19', '2025-05-26', 700000, 160000, N'Còn hiệu lực'),
('KM-20052025-001', N'Ve vé 20/5 đặc biệt', 0.3, '2025-05-20', '2025-05-27', 500000, 130000, N'Còn hiệu lực'),
('KM-02022024-001', N'Ưu đãi tháng 2 nhẹ nhàng', 0.2, '2024-02-02', '2024-02-06', 300000, 80000, N'Đã hết hạn'),
('KM-15022024-001', N'Khuyến mãi giữa tháng 2', 0.25, '2024-02-15', '2024-02-20', 400000, 100000, N'Đã hết hạn'),
('KM-22022024-001', N'Tuần lễ vé giá rẻ', 0.3, '2024-02-22', '2024-02-26', 450000, 110000, N'Đã hết hạn'),
('KM-26022024-001', N'Ve vé cuối tháng 2', 0.2, '2024-02-27', '2024-03-01', 350000, 85000, N'Đã hết hạn'),
('KM-06032024-002', N'Tặng vé mừng ngày Quốc tế Phụ nữ', 0.3, '2024-03-06', '2024-03-09', 500000, 120000, N'Đã hết hạn'),
('KM-10032024-001', N'Khuyến mãi giữa tháng 3', 0.2, '2024-03-10', '2024-03-14', 300000, 80000, N'Đã hết hạn'),
('KM-15032024-001', N'Ưu đãi tháng 3 yêu thương', 0.25, '2024-03-15', '2024-03-18', 400000, 100000, N'Đã hết hạn'),
('KM-18032024-001', N'Ve vé mùa xuân rộn ràng', 0.3, '2024-03-18', '2024-03-22', 450000, 110000, N'Đã hết hạn'),
('KM-22032024-001', N'Chào xuân tháng 3', 0.2, '2024-03-22', '2024-03-26', 350000, 85000, N'Đã hết hạn'),
('KM-25032024-001', N'Vé tiết kiệm cuối tháng 3', 0.25, '2024-03-25', '2024-03-28', 400000, 95000, N'Đã hết hạn'),
('KM-01052024-002', N'Ve vé ưu đãi ngày Quốc tế Lao động', 0.3, '2024-05-01', '2024-05-03', 500000, 130000, N'Đã hết hạn'),
('KM-06052024-001', N'Tháng 5 bùng nổ khuyến mãi', 0.2, '2024-05-06', '2024-05-10', 300000, 80000, N'Đã hết hạn'),
('KM-21052024-001', N'Ve vé tháng 5 rực rỡ', 0.25, '2024-05-21', '2024-05-25', 400000, 100000, N'Đã hết hạn'),
('KM-26052024-001', N'Khuyến mãi tháng 5 kết thúc', 0.3, '2024-05-26', '2024-05-30', 450000, 110000, N'Đã hết hạn'),
('KM-15062024-001', N'Giữa tháng 6 ưu đãi lớn', 0.2, '2024-06-15', '2024-06-19', 350000, 85000, N'Đã hết hạn'),
('KM-25062024-001', N'Tuần lễ vàng tháng 6', 0.25, '2024-06-25', '2024-06-28', 400000, 95000, N'Đã hết hạn'),
('KM-05072024-001', N'Mở đầu tháng 7 siêu ưu đãi', 0.3, '2024-07-05', '2024-07-09', 500000, 120000, N'Đã hết hạn'),
('KM-15072024-001', N'Ưu đãi giữa tháng 7', 0.2, '2024-07-15', '2024-07-18', 300000, 80000, N'Đã hết hạn'),
('KM-20072024-001', N'Ve vé tháng 7 – càng đi càng rẻ', 0.25, '2024-07-20', '2024-07-24', 400000, 100000, N'Đã hết hạn'),
('KM-25072024-001', N'Ve vé tháng 7 sôi động', 0.3, '2024-07-25', '2024-07-27', 450000, 110000, N'Đã hết hạn'),
('KM-05082024-001', N'Ve vé tháng 8 nhẹ nhàng', 0.2, '2024-08-05', '2024-08-08', 350000, 85000, N'Đã hết hạn'),
('KM-10082024-001', N'Tháng 8 ưu đãi giữa tháng', 0.3, '2024-08-10', '2024-08-13', 500000, 120000, N'Đã hết hạn'),
('KM-15082024-001', N'Chào thu tháng 8', 0.25, '2024-08-15', '2024-08-18', 450000, 105000, N'Đã hết hạn'),
('KM-20082024-001', N'Ve vé ưu đãi cuối tháng 8', 0.2, '2024-08-20', '2024-08-24', 300000, 80000, N'Đã hết hạn'),
('KM-25082024-001', N'Khuyến mãi đặc biệt cuối tháng 8', 0.3, '2024-08-25', '2024-08-29', 500000, 130000, N'Đã hết hạn'),
('KM-06092024-001', N'Tuần lễ vàng tháng 9', 0.25, '2024-09-06', '2024-09-10', 400000, 95000, N'Đã hết hạn'),
('KM-11092024-001', N'Vé tiết kiệm giữa tháng 9', 0.3, '2024-09-11', '2024-09-15', 450000, 110000, N'Đã hết hạn'),
('KM-16092024-001', N'Ve vé mùa thu yêu thương', 0.2, '2024-09-16', '2024-09-19', 300000, 80000, N'Đã hết hạn'),
('KM-21092024-001', N'Khuyến mãi cuối tháng 9', 0.25, '2024-09-21', '2024-09-24', 400000, 100000, N'Đã hết hạn'),
('KM-25092024-001', N'Ve vé tháng 9 – càng đi càng vui', 0.3, '2024-09-25', '2024-09-28', 500000, 120000, N'Đã hết hạn'),
('KM-01102024-001', N'Khuyến mãi ngày Quốc tế Người cao tuổi', 0.25, '2024-10-01', '2024-10-04', 400000, 95000, N'Đã hết hạn'),
('KM-05102024-001', N'Tháng 10 khuyến mãi nhẹ', 0.2, '2024-10-05', '2024-10-08', 350000, 85000, N'Đã hết hạn'),
('KM-10102024-001', N'Mùa thu tháng 10 ưu đãi', 0.3, '2024-10-10', '2024-10-13', 450000, 110000, N'Đã hết hạn'),
('KM-14102024-001', N'Vé tiết kiệm giữa tháng 10', 0.25, '2024-10-14', '2024-10-17', 400000, 100000, N'Đã hết hạn'),
('KM-18102024-001', N'Khuyến mãi tháng 10 ấm áp', 0.2, '2024-10-18', '2024-10-21', 300000, 80000, N'Đã hết hạn'),
('KM-22102024-001', N'Ve vé tháng 10 cuối cùng', 0.3, '2024-10-22', '2024-10-26', 500000, 130000, N'Đã hết hạn'),
('KM-01112024-001', N'Khuyến mãi đầu tháng 11 rộn ràng', 0.25, '2024-11-01', '2024-11-04', 400000, 95000, N'Đã hết hạn'),
('KM-05112024-001', N'Ưu đãi tháng 11 – càng đi càng giảm', 0.2, '2024-11-05', '2024-11-08', 350000, 85000, N'Đã hết hạn'),
('KM-10112024-001', N'Ve vé tiết kiệm giữa tháng 11', 0.3, '2024-11-10', '2024-11-14', 500000, 120000, N'Đã hết hạn'),
('KM-15112024-001', N'Ưu đãi tháng 11 rực rỡ', 0.25, '2024-11-15', '2024-11-18', 400000, 95000, N'Đã hết hạn'),
('KM-19112024-001', N'Tặng bạn khuyến mãi tháng 11', 0.2, '2024-11-19', '2024-11-22', 350000, 85000, N'Đã hết hạn'),
('KM-23112024-001', N'Ve vé tháng 11 – ưu đãi cuối tháng', 0.3, '2024-11-23', '2024-11-27', 500000, 125000, N'Đã hết hạn'),
('KM-28112024-001', N'Khuyến mãi đặc biệt cuối tháng 11', 0.25, '2024-11-28', '2024-11-30', 400000, 100000, N'Đã hết hạn'),
('KM-01122024-001', N'Ưu đãi đầu tháng 12 – chào đông', 0.2, '2024-12-01', '2024-12-04', 300000, 75000, N'Đã hết hạn'),
('KM-05122024-001', N'Tháng 12 – rộn ràng khuyến mãi', 0.3, '2024-12-05', '2024-12-08', 500000, 130000, N'Đã hết hạn'),
('KM-09122024-001', N'Ve vé cuối năm – càng đi càng rẻ', 0.25, '2024-12-09', '2024-12-12', 400000, 95000, N'Đã hết hạn'),
('KM-13122024-001', N'Tuần lễ vàng tháng 12', 0.3, '2024-12-13', '2024-12-17', 500000, 125000, N'Đã hết hạn'),
('KM-18122024-001', N'Ve vé ưu đãi giữa tháng 12', 0.2, '2024-12-18', '2024-12-21', 350000, 85000, N'Đã hết hạn'),
('KM-22122024-001', N'Khuyến mãi Giáng Sinh sớm', 0.3, '2024-12-22', '2024-12-24', 500000, 120000, N'Đã hết hạn'),
('KM-25122024-001', N'Merry Christmas – Vé ưu đãi', 0.25, '2024-12-25', '2024-12-27', 400000, 95000, N'Đã hết hạn'),
('KM-28122024-001', N'Ve vé cuối năm – Tạm biệt 2024', 0.3, '2024-12-28', '2024-12-31', 500000, 130000, N'Đã hết hạn'),
('KM-30122024-001', N'Khuyến mãi chào năm mới sớm', 0.2, '2024-12-30', '2024-12-31', 300000, 80000, N'Đã hết hạn'),
('KM-04112024-001', N'Ưu đãi mini tháng 11', 0.2, '2024-11-04', '2024-11-06', 300000, 75000, N'Đã hết hạn'),
('KM-07112024-001', N'Ve vé giữa tháng 11 nhẹ nhàng', 0.25, '2024-11-07', '2024-11-09', 400000, 90000, N'Đã hết hạn'),
('KM-16122024-001', N'Giảm giá mùa lễ hội', 0.2, '2024-12-16', '2024-12-18', 350000, 85000, N'Đã hết hạn'),
('KM-20122024-001', N'Ưu đãi Noel – Chúc an lành', 0.3, '2024-12-20', '2024-12-23', 500000, 120000, N'Đã hết hạn'),
('KM-26122024-001', N'Vé giảm giá hậu Giáng Sinh', 0.25, '2024-12-26', '2024-12-28', 400000, 100000, N'Đã hết hạn'),
('KM-29122024-001', N'Ưu đãi ngày cuối cùng năm 2024', 0.3, '2024-12-29', '2024-12-31', 500000, 130000, N'Đã hết hạn'),
('KM-01012025-001', N'Chào năm mới – Vé rẻ như mơ', 0.3, '2025-01-01', '2025-01-05', 500000, 120000, N'Đã hết hạn'),
('KM-06012025-001', N'Tuần đầu năm – Ưu đãi ngập tràn', 0.25, '2025-01-06', '2025-01-10', 400000, 95000, N'Đã hết hạn'),
('KM-11012025-001', N'Ve rẻ đầu năm – Đi ngay thôi!', 0.2, '2025-01-11', '2025-01-14', 350000, 85000, N'Đã hết hạn'),
('KM-15012025-001', N'Tháng 1 ưu đãi đặc biệt', 0.3, '2025-01-15', '2025-01-19', 500000, 125000, N'Đã hết hạn'),
('KM-20012025-001', N'Vé giá tốt mùa xuân', 0.25, '2025-01-20', '2025-01-23', 400000, 100000, N'Đã hết hạn'),
('KM-24012025-001', N'Tết cận kề – Vé cực mê', 0.3, '2025-01-24', '2025-01-27', 500000, 130000, N'Đã hết hạn'),
('KM-28012025-001', N'Mừng xuân – Giảm giá vé tàu', 0.2, '2025-01-28', '2025-01-31', 300000, 75000, N'Đã hết hạn'),
('KM-01022025-001', N'Ưu đãi mồng Một – Khởi hành đầu năm', 0.3, '2025-02-01', '2025-02-04', 500000, 120000, N'Đã hết hạn'),
('KM-05022025-001', N'Tết sum vầy – Vé rẻ mỗi ngày', 0.25, '2025-02-05', '2025-02-08', 400000, 100000, N'Đã hết hạn'),
('KM-09022025-001', N'Ưu đãi xuân yêu thương', 0.3, '2025-02-09', '2025-02-13', 500000, 125000, N'Đã hết hạn'),
('KM-14022025-001', N'Khuyến mãi Valentine – Vé đôi cực rẻ', 0.3, '2025-02-14', '2025-02-15', 500000, 130000, N'Đã hết hạn'),
('KM-16022025-001', N'Sau lễ – Vé tiết kiệm quay lại', 0.2, '2025-02-16', '2025-02-19', 350000, 85000, N'Đã hết hạn'),
('KM-20022025-001', N'Tháng 2 khuyến mãi rực rỡ', 0.25, '2025-02-20', '2025-02-24', 400000, 95000, N'Đã hết hạn'),
('KM-25022025-001', N'Ve rẻ cuối tháng 2', 0.3, '2025-02-25', '2025-02-28', 500000, 120000, N'Đã hết hạn'),
('KM-01032025-001', N'Khởi đầu tháng 3 – Đi đâu cũng rẻ', 0.2, '2025-03-01', '2025-03-04', 350000, 80000, N'Đã hết hạn'),
('KM-05032025-001', N'Ưu đãi đặc biệt 8/3', 0.3, '2025-03-05', '2025-03-08', 500000, 125000, N'Đã hết hạn'),
('KM-09032025-001', N'Sau lễ – Giá vé nhẹ nhàng', 0.2, '2025-03-09', '2025-03-12', 300000, 75000, N'Đã hết hạn'),
('KM-13032025-001', N'Mừng xuân muộn – Ưu đãi thêm', 0.25, '2025-03-13', '2025-03-17', 400000, 95000, N'Đã hết hạn'),
('KM-18032025-001', N'Ve giá rẻ – Càng đi càng thích', 0.3, '2025-03-18', '2025-03-21', 500000, 125000, N'Đã hết hạn'),
('KM-22032025-001', N'Cuối tháng 3 – Vé siêu ưu đãi', 0.25, '2025-03-22', '2025-03-26', 400000, 100000, N'Đã hết hạn');
GO

--Thêm dữ liệu loại ghế

INSERT INTO LoaiGhe (maLoaiGhe, tenLoaiGhe, moTa, heSoGhe)
VALUES
('GHE_NGOI_MEM', N'Ghế ngồi mềm', N'Ghế ngồi có đệm, phù hợp với các tuyến ngắn và trung bình', 1.0),
('GIUONG_NAM_4', N'Giường nằm 4 chỗ', N'Khoang giường nằm 4 chỗ, phù hợp cho nhóm nhỏ hoặc gia đình', 1.3),
('GIUONG_NAM_6', N'Giường nằm 6 chỗ', N'Khoang giường nằm 6 chỗ, tiết kiệm chi phí cho nhóm đông người', 1.2);
GO

--Thêm dữ liệu loại vé

INSERT INTO LoaiVe (maLoaiVe, tenLoaiVe, moTaLoaiVe, heSoLoaiVe)
VALUES
('LV-TE', N'Vé dành cho trẻ em', N'Vé áp dụng cho hành khách dưới 10 tuổi, được giảm giá so với vé người lớn.', 0.5),
('LV-HSSV', N'Vé dành cho học sinh, sinh viên', N'Vé áp dụng cho học sinh, sinh viên có thẻ hợp lệ, được ưu đãi đặc biệt.', 0.7),
('LV-NL', N'Vé dành cho người lớn', N'Vé áp dụng cho hành khách từ 18 tuổi trở lên, không áp dụng ưu đãi.', 1.0);
GO

--Thêm dữ liệu hành khách
INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
('HK-07-030323-001', N'Phạm Thị Thùy Phúc', '015307070198', 2007),
('HK-93-030323-002', N'Đỗ Quốc Hải',        '052293391084', 1993),
('HK-06-030323-003', N'Nguyễn Văn Huy',     '070206377126', 2006),
('HK-86-030323-004', N'Hồ Thị Nhi',         '049386861155', 1986),
('HK-09-030323-005', N'Vũ Văn Phúc',        '011209393347', 2009),
('HK-08-030323-006', N'Bùi Thị Hương',      '033308083577', 2008),
('HK-85-030323-007', N'Lê Minh Dũng',       '080285627713', 1985);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
('HK-08-020323-001', N'Nguyễn Thị Lan Hương',  '079308051244', 2008),
('HK-93-020323-002', N'Phạm Văn Hoàng',        '031193740512', 1993),
('HK-07-020323-003', N'Lê Hoàng Long',         '004207021967', 2007),
('HK-99-020323-004', N'Trần Thị Mỹ Linh',      '050399151277', 1999),
('HK-06-020323-005', N'Vũ Đức Anh',            '024206120839', 2006),
('HK-92-020323-006', N'Ngô Thị Thu',           '046192510427', 1992),
('HK-85-020323-007', N'Bùi Minh Khôi',         '082185111022', 1985),
('HK-09-020323-008', N'Hà Thị Hạnh',           '027209110531', 2009),
('HK-04-020323-009', N'Đặng Quốc Đạt',         '003204871012', 2004),
('HK-84-020323-010', N'Tạ Ngọc Mai',           '066184960415', 1984),
('HK-10-020323-011', N'Lương Minh Nhật',       '075210680124', 2010),
('HK-01-020323-012', N'Trịnh Thị Ngọc Anh',    '001201970348', 2001),
('HK-87-020323-013', N'Cao Hữu Trung',         '053187620533', 1987),
('HK-03-020323-014', N'Tô Minh Tuấn',          '011203650710', 2003),
('HK-90-020323-015', N'Nguyễn Thị Tuyết',      '030190580911', 1990),
('HK-05-020323-016', N'Phạm Thanh Hằng',       '076205300628', 2005),
('HK-83-020323-017', N'Hoàng Thị Thảo',        '012183920733', 1983),
('HK-02-020323-018', N'Lý Quốc Khánh',         '048202090256', 2002),
('HK-88-020323-019', N'Võ Nhật Tân',           '063188331244', 1988),
('HK-00-020323-020', N'Nguyễn Thị Hồng',       '036200101127', 2000);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 01/03/2023
('HK-09-010323-001', N'Nguyễn Văn Hùng',        '020209101348', 2009),
('HK-91-010323-002', N'Trần Thị Bích Ngọc',     '045191830927', 1991),
('HK-04-010323-003', N'Lê Hữu Khánh',           '005204121744', 2004),
('HK-84-010323-004', N'Đinh Thị Hồng',          '033184970631', 1984),
('HK-03-010323-005', N'Phạm Minh Chiến',        '017203871059', 2003),
('HK-88-010323-006', N'Vũ Thị Yến',             '068188390524', 1988),
('HK-08-010323-007', N'Lý Nhật Tân',            '014208040357', 2008),

-- Ngày 29/02/2023
('HK-02-290223-001', N'Nguyễn Thị Kim Oanh',    '001202911145', 2002),
('HK-87-290223-002', N'Hoàng Văn Duy',          '059187640221', 1987),
('HK-07-290223-003', N'Trịnh Thị Hòa',          '036207090635', 2007),
('HK-90-290223-004', N'Bùi Quốc Anh',           '023190771003', 1990),
('HK-10-290223-005', N'Đoàn Thị Linh',          '081210610238', 2010),
('HK-06-290223-006', N'Ngô Thị Hà',             '002206320158', 2006),
('HK-99-290223-007', N'Tạ Văn Toàn',            '078199851225', 1999),
('HK-05-290223-008', N'Lâm Nhật Huy',           '041205560940', 2005),

-- Ngày 28/02/2023
('HK-01-280223-001', N'Nguyễn Nhật Minh',       '003201010337', 2001),
('HK-86-280223-002', N'Phan Thị Mai Hương',     '030186990614', 1986),
('HK-93-280223-003', N'Cao Thanh Tùng',         '062193750427', 1993),
('HK-08-280223-004', N'Vương Thị Cúc',          '079208920426', 2008),
('HK-92-280223-005', N'Đinh Văn Quý',           '034192830515', 1992);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 27/02/2023
('HK-04-270223-001', N'Nguyễn Đức Tài',         '005204110423', 2004),
('HK-91-270223-002', N'Lê Thị Mai',             '068191881036', 1991),
('HK-08-270223-003', N'Vũ Hữu Phước',           '024208120789', 2008),
('HK-86-270223-004', N'Trần Thị Hằng',          '042186550217', 1986),
('HK-02-270223-005', N'Phạm Văn Hòa',           '001202830344', 2002),

-- Ngày 26/02/2023
('HK-10-260223-001', N'Lương Thị Thu',          '079210670311', 2010),
('HK-07-260223-002', N'Hồ Đức Thành',           '019207991241', 2007),
('HK-85-260223-003', N'Ngô Thị Hương',          '073185320759', 1985),
('HK-03-260223-004', N'Đặng Văn Dũng',          '030203120422', 2003),
('HK-92-260223-005', N'Tô Minh Khoa',           '062192890205', 1992),
('HK-06-260223-006', N'Trịnh Thị Tâm',          '015206970514', 2006),

-- Ngày 25/02/2023
('HK-95-250223-001', N'Hoàng Văn Hải',          '023195280408', 1995),
('HK-09-250223-002', N'Lý Thị Phương',          '063209650221', 2009),
('HK-88-250223-003', N'Đinh Quốc Toàn',         '049188430116', 1988),
('HK-05-250223-004', N'Phạm Minh Nhật',         '035205770903', 2005),
('HK-96-250223-005', N'Bùi Văn Long',           '022196370237', 1996),
('HK-84-250223-006', N'Trần Hữu Quân',          '058184600157', 1984),
('HK-01-250223-007', N'Nguyễn Thị Hiền',        '004201110945', 2001),
('HK-99-250223-008', N'Cao Văn Hùng',           '072199390310', 1999);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 24/02/2023
('HK-02-240223-001', N'Nguyễn Văn Hoàng',       '028202060148', 2002),
('HK-93-240223-002', N'Trịnh Thị Kim',          '066193910302', 1993),
('HK-07-240223-003', N'Lê Quốc Khánh',          '049207150333', 2007),
('HK-86-240223-004', N'Hồ Minh Tuấn',           '061186430194', 1986),
('HK-04-240223-005', N'Phạm Thị Xuân',          '019204880276', 2004),

-- Ngày 23/02/2023
('HK-09-230223-001', N'Vũ Thị Huyền',            '035209160748', 2009),
('HK-97-230223-002', N'Cao Thành Đạt',           '075197450912', 1997),
('HK-05-230223-003', N'Lý Văn Hưng',             '013205990521', 2005),
('HK-92-230223-004', N'Tô Thị Thu Trang',        '070192690333', 1992),
('HK-88-230223-005', N'Ngô Đức Phát',            '036188750204', 1988),
('HK-10-230223-006', N'Đặng Minh Anh',           '080210390116', 2010),
('HK-03-230223-007', N'Bùi Văn Tài',             '001203880209', 2003),

-- Ngày 22/02/2023
('HK-06-220223-001', N'Trần Văn Hòa',            '044206060119', 2006),
('HK-95-220223-002', N'Lương Thị Thanh',        '021195730725', 1995),
('HK-84-220223-003', N'Phan Văn Khoa',           '058184250913', 1984),
('HK-08-220223-004', N'Huỳnh Kim Oanh',          '033208840251', 2008),
('HK-96-220223-005', N'Nguyễn Thị Hà',           '022196920317', 1996),
('HK-99-220223-006', N'Đào Nhật Trường',         '048199300611', 1999),
('HK-01-220223-007', N'Thái Quang Huy',          '004201490805', 2001);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 21/02/2023
('HK-02-210223-001', N'Lê Minh Hải',         '010202090817', 2002),
('HK-94-210223-002', N'Nguyễn Thị Hồng',     '057194420932', 1994),
('HK-09-210223-003', N'Phạm Quốc Trí',       '043209360427', 2009),
('HK-86-210223-004', N'Trần Hữu Duy',         '060186770240', 1986),
('HK-08-210223-005', N'Hoàng Thị Kim',       '079208800601', 2008),
('HK-01-210223-006', N'Vũ Đình Phúc',         '001201310228', 2001),

-- Ngày 20/02/2023
('HK-07-200223-001', N'Đinh Nhật Minh',       '023207880318', 2007),
('HK-99-200223-002', N'Cao Thị Yến',          '054199660450', 1999),
('HK-06-200223-003', N'Lương Văn Cường',      '039206450644', 2006),
('HK-84-200223-004', N'Nguyễn Hoài Nam',      '019184720177', 1984),
('HK-03-200223-005', N'Trịnh Thị Bích',       '031203580944', 2003),
('HK-92-200223-006', N'Phan Anh Tuấn',        '048192550381', 1992),

-- Ngày 19/02/2023
('HK-05-190223-001', N'Hồ Văn Hưng',          '076205160951', 2005),
('HK-96-190223-002', N'Lê Thị Lan',           '066196430119', 1996),
('HK-88-190223-003', N'Ngô Quốc Anh',         '021188950203', 1988),
('HK-04-190223-004', N'Đoàn Hồng Hà',         '017204140825', 2004),
('HK-10-190223-005', N'Thái Bảo Châu',        '082210340629', 2010),
('HK-95-190223-006', N'Tống Văn Hòa',         '059195130746', 1995),
('HK-02-190223-007', N'Lý Tuấn Kiệt',         '036202220419', 2002),
('HK-07-190223-008', N'Nguyễn Tố Như',        '027207150934', 2007),

-- Ngày 18/02/2023
('HK-03-180223-001', N'Trần Thị Thảo',        '031203890209', 2003),
('HK-91-180223-002', N'Phạm Văn Sơn',         '040191980317', 1991),
('HK-06-180223-003', N'Nguyễn Văn Tùng',      '014206240123', 2006),
('HK-82-180223-004', N'Huỳnh Thanh Bình',     '065182720550', 1982),
('HK-05-180223-005', N'Tống Mỹ Duyên',        '058205290248', 2005),

-- Ngày 17/02/2023
('HK-08-170223-001', N'Lê Văn Dũng',          '078208180630', 2008),
('HK-99-170223-002', N'Nguyễn Thị Giang',     '070199760444', 1999),
('HK-02-170223-003', N'Phan Thanh Hải',       '013202590935', 2002),
('HK-94-170223-004', N'Hà Mai Vy',            '019194340611', 1994),
('HK-84-170223-005', N'Lý Trung Hiếu',        '022184270927', 1984),

-- Ngày 16/02/2023
('HK-01-160223-001', N'Cao Hữu Lộc',          '004201110824', 2001),
('HK-93-160223-002', N'Lê Nhật Hào',          '032193420738', 1993),
('HK-07-160223-003', N'Trịnh Anh Khoa',       '016207850519', 2007),
('HK-92-160223-004', N'Ngô Thị Yến Nhi',      '038192370127', 1992),
('HK-10-160223-005', N'Đặng Minh Hương',      '079210460331', 2010),

-- Ngày 15/02/2023
('HK-04-150223-001', N'Nguyễn Gia Hân',       '021204680128', 2004),
('HK-85-150223-002', N'Đỗ Thành Long',        '057185890843', 1985),
('HK-06-150223-003', N'Trần Minh Trí',        '048206530951', 2006),
('HK-03-150223-004', N'Lương Mỹ Linh',        '033203610712', 2003),
('HK-96-150223-005', N'Vũ Hữu Toàn',          '066196180915', 1996);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 14/02/2023
('HK-06-140223-001', N'Nguyễn Thanh Tùng',   '037206840712', 2006),
('HK-97-140223-002', N'Lê Thị Thúy',         '041197620815', 1997),
('HK-03-140223-003', N'Đỗ Quang Minh',       '052203450428', 2003),
('HK-85-140223-004', N'Trương Anh Tuấn',     '018185350927', 1985),
('HK-09-140223-005', N'Vũ Ngọc Hân',         '080209980538', 2009),
('HK-94-140223-006', N'Hồ Minh Nhật',        '012194110720', 1994),

-- Ngày 13/02/2023
('HK-01-130223-001', N'Trần Văn Phúc',       '005201850804', 2001),
('HK-96-130223-002', N'Phan Thị Hoa',        '059196930335', 1996),
('HK-10-130223-003', N'Ngô Bảo Ngọc',        '064210450648', 2010),
('HK-07-130223-004', N'Lý Đức Tài',          '043207150227', 2007),
('HK-83-130223-005', N'Nguyễn Thế Bảo',      '029183880603', 1983),
('HK-02-130223-006', N'Tống Hoài Anh',       '038202650736', 2002),

-- Ngày 12/02/2023
('HK-05-120223-001', N'Vũ Hồng Sơn',         '026205470812', 2005),
('HK-99-120223-002', N'Trịnh Thị Như',       '075199530122', 1999),
('HK-04-120223-003', N'Lê Văn Dương',        '063204890324', 2004),
('HK-92-120223-004', N'Cao Nhật Linh',       '017192780215', 1992),
('HK-08-120223-005', N'Đinh Thị Tuyết',      '048208730144', 2008),
('HK-88-120223-006', N'Phạm Quốc Thịnh',     '035188910917', 1988),

-- Ngày 11/02/2023
('HK-07-110223-001', N'Huỳnh Văn Cường',     '014207280921', 2007),
('HK-95-110223-002', N'Nguyễn Bảo Trân',     '049195950303', 1995),
('HK-02-110223-003', N'Tống Duy Phong',      '034202140413', 2002),
('HK-91-110223-004', N'Phan Nhật Hào',       '069191310839', 1991),
('HK-03-110223-005', N'Lê Hữu Khang',        '042203510420', 2003),
('HK-86-110223-006', N'Ngô Thị Xuân',        '031186220658', 1986),
('HK-10-110223-007', N'Trần Gia Linh',       '076210180926', 2010),
('HK-06-110223-008', N'Đoàn Khánh Duy',      '023206340506', 2006),

-- Ngày 10/02/2023
('HK-94-100223-001', N'Trịnh Văn Hiếu',      '020194610415', 1994),
('HK-04-100223-002', N'Nguyễn Thị Lệ',       '011204770218', 2004),
('HK-98-100223-003', N'Hoàng Thanh Tú',      '051198880334', 1998),
('HK-05-100223-004', N'Phan Quốc Anh',       '039205590525', 2005),
('HK-83-100223-005', N'Lý Trọng Tấn',        '016183720701', 1983),
('HK-09-100223-006', N'Tạ Minh Khôi',        '060209760902', 2009),
('HK-93-100223-007', N'Lê Nhật Linh',        '068193890730', 1993),
('HK-01-100223-008', N'Trần Bích Hằng',      '003201610910', 2001);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 10/02/2023 (tiếp)
('HK-06-100223-009', N'Nguyễn Văn Hưng',     '021206140714', 2006),
('HK-87-100223-010', N'Lê Thị Cẩm Tiên',     '045187230412', 1987),

-- Ngày 09/02/2023
('HK-08-090223-001', N'Hoàng Minh Khoa',     '036208960119', 2008),
('HK-96-090223-002', N'Phạm Hồng Vân',       '058196740709', 1996),
('HK-04-090223-003', N'Trần Đức Mạnh',       '069204370530', 2004),
('HK-85-090223-004', N'Nguyễn Quang Tài',    '062185670210', 1985),
('HK-10-090223-005', N'Lê Minh Châu',        '074210960903', 2010),
('HK-03-090223-006', N'Vũ Thanh Long',       '040203750823', 2003),

-- Ngày 08/02/2023
('HK-07-080223-001', N'Đỗ Bảo Linh',         '014207820125', 2007),
('HK-95-080223-002', N'Lý Văn Nam',          '047195520617', 1995),
('HK-02-080223-003', N'Trịnh Thu Hằng',      '033202360326', 2002),
('HK-93-080223-004', N'Phan Hữu Đức',        '061193410915', 1993),
('HK-10-080223-005', N'Ngô Gia Bảo',         '078210250411', 2010),

-- Ngày 07/02/2023
('HK-06-070223-001', N'Nguyễn Minh Tuấn',    '030206180729', 2006),
('HK-98-070223-002', N'Lê Hồng Nhung',       '050198650217', 1998),
('HK-03-070223-003', N'Trần Quang Huy',      '059203910802', 2003),
('HK-89-070223-004', N'Phạm Văn Tâm',        '018189310514', 1989),
('HK-08-070223-005', N'Đoàn Thị Thúy',       '049208630924', 2008),
('HK-92-070223-006', N'Hoàng Nhật Minh',     '026192760911', 1992),
('HK-04-070223-007', N'Tống Hữu Nghĩa',      '065204490204', 2004),
('HK-01-070223-008', N'Nguyễn Bảo Hân',      '004201120327', 2001),

-- Ngày 09/02/2023 (tiếp)
('HK-88-090223-007', N'Cao Quốc Việt',       '022188450618', 1988),

-- Ngày 08/02/2023 (tiếp)
('HK-05-080223-006', N'Vũ Thị Hiền',         '035205510123', 2005),
('HK-97-080223-007', N'Lê Thanh Bình',       '057197920206', 1997),

-- Ngày 07/02/2023 (tiếp)
('HK-90-070223-009', N'Bùi Đức Lộc',         '066190340709', 1990),
('HK-05-070223-010', N'Nguyễn Thị Như Mai',  '039205270823', 2005),
('HK-07-070223-011', N'Trần Văn Tình',       '032207180708', 2007),

-- Ngày 09/02/2023 (tiếp)
('HK-01-090223-008', N'Lý Minh Hoàng',       '006201970618', 2001),
('HK-87-090223-009', N'Huỳnh Kim Thoa',      '043187330319', 1987),

-- Ngày 08/02/2023 (tiếp)
('HK-91-080223-008', N'Lê Quốc Dũng',        '073191650627', 1991),
('HK-09-080223-009', N'Trịnh Bảo Linh',      '060209970304', 2009),

-- Ngày 07/02/2023 (tiếp)
('HK-96-070223-012', N'Nguyễn Hữu Quân',     '048196880529', 1996),
('HK-10-070223-013', N'Tạ Hoàng Phúc',       '072210740831', 2010),
('HK-02-070223-014', N'Cao Thanh Hằng',      '037202530506', 2002);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 06/02/2023
('HK-04-060223-001', N'Phạm Minh Châu',      '070204280805', 2004),
('HK-97-060223-002', N'Nguyễn Đức Trí',     '057197910920', 1997),
('HK-01-060223-003', N'Võ Thị Ngọc Ánh',     '004201320131', 2001),
('HK-88-060223-004', N'Trần Nhật Duy',       '022188350629', 1988),
('HK-06-060223-005', N'Lê Hồng Như',         '021206510125', 2006),
('HK-94-060223-006', N'Nguyễn Văn Cường',    '063194720611', 1994),
('HK-03-060223-007', N'Hoàng Gia Huy',       '040203780802', 2003),
('HK-10-060223-008', N'Trịnh Tuấn Anh',      '074210940417', 2010),

-- Ngày 05/02/2023
('HK-05-050223-001', N'Ngô Thị Tuyết',       '035205650922', 2005),
('HK-92-050223-002', N'Đặng Minh Khôi',      '026192420413', 1992),
('HK-08-050223-003', N'Lý Gia Hân',          '049208610207', 2008),
('HK-89-050223-004', N'Bùi Minh Quân',       '018189230518', 1989),
('HK-07-050223-005', N'Phan Thị Cẩm Tú',     '014207350212', 2007),
('HK-96-050223-006', N'Tống Hữu Phúc',       '058196930314', 1996),
('HK-02-050223-007', N'Nguyễn Quỳnh Mai',    '033202520831', 2002),

-- Ngày 04/02/2023
('HK-95-040223-001', N'Lê Thị Thanh Tâm',    '047195270623', 1995),
('HK-06-040223-002', N'Ngô Đức Hưng',        '021206730511', 2006),
('HK-90-040223-003', N'Trần Trung Tín',      '066190580217', 1990),
('HK-03-040223-004', N'Lý Văn Hậu',          '059203690305', 2003),
('HK-85-040223-005', N'Phạm Thị Lan',        '062185350406', 1985),
('HK-01-040223-006', N'Nguyễn Nhật Hào',     '004201120110', 2001),

-- Ngày 03/02/2023
('HK-07-030223-001', N'Trần Minh Tú',        '014207850919', 2007),
('HK-93-030223-002', N'Nguyễn Văn Long',     '061193210227', 1993),
('HK-04-030223-003', N'Lê Kim Ngân',         '069204750710', 2004),
('HK-91-030223-004', N'Phan Quốc Hưng',      '073191940913', 1991),
('HK-09-030223-005', N'Trịnh Hoài Linh',     '060209970305', 2009),
('HK-10-030223-006', N'Tạ Hữu Phát',         '072210250612', 2010),
('HK-02-030223-007', N'Vũ Hoàng Oanh',       '037202360311', 2002),
('HK-96-030223-008', N'Tống Bảo An',         '048196750810', 1996),

-- Tiếp ngày 05/02/2023
('HK-87-050223-008', N'Cao Thị Hồng',        '043187510805', 1987),

-- Tiếp ngày 04/02/2023
('HK-08-040223-007', N'Trịnh Ngọc Lân',      '036208640420', 2008),
('HK-98-040223-008', N'Hoàng Gia Phúc',      '050198930621', 1998),

-- Tiếp ngày 03/02/2023
('HK-05-030223-009', N'Nguyễn Quang Lâm',    '035205910815', 2005),
('HK-92-030223-010', N'Lý Hữu Thành',        '026192680404', 1992),
('HK-06-030223-011', N'Vũ Thu Thảo',         '021206510526', 2006),
('HK-01-030223-012', N'Trần Ngọc Duy',       '004201380929', 2001);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 02/02/2023
('HK-03-020223-001', N'Nguyễn Thanh Tùng',   '040203750813', 2003),
('HK-95-020223-002', N'Lê Minh Nhật',        '047195920624', 1995),
('HK-08-020223-003', N'Trần Thị Mai',        '049208130317', 2008),
('HK-92-020223-004', N'Võ Đức Thịnh',        '026192240506', 1992),
('HK-07-020223-005', N'Phan Nhật Linh',      '014207470211', 2007),
('HK-10-020223-006', N'Tống Hồng Phúc',      '074210950927', 2010),

-- Ngày 01/02/2023
('HK-06-010223-001', N'Đỗ Thị Hằng',         '021206160101', 2006),
('HK-94-010223-002', N'Ngô Văn Tài',         '063194210811', 1994),
('HK-05-010223-003', N'Phạm Văn Sơn',        '035205180703', 2005),
('HK-89-010223-004', N'Vũ Hải Đăng',         '018189220406', 1989),
('HK-02-010223-005', N'Lê Thị Bích Ngọc',    '033202670910', 2002),
('HK-01-010223-006', N'Nguyễn Văn Đức',      '004201140517', 2001),

-- Ngày 31/01/2023
('HK-04-310123-001', N'Trịnh Hoài An',       '069204880201', 2004),
('HK-93-310123-002', N'Nguyễn Thế Hiển',     '061193190728', 1993),
('HK-09-310123-003', N'Ngô Gia Bảo',         '060209470330', 2009),
('HK-96-310123-004', N'Bùi Văn Tuấn',        '048196750502', 1996),
('HK-07-310123-005', N'Lý Quỳnh Hoa',        '014207230405', 2007),

-- Ngày 30/01/2023
('HK-03-300123-001', N'Vũ Thị Cẩm Tú',        '040203120723', 2003),
('HK-91-300123-002', N'Trần Đức Thành',       '073191810104', 1991),
('HK-08-300123-003', N'Tống Hải Yến',         '049208620408', 2008),
('HK-06-300123-004', N'Lê Văn Lâm',           '021206470920', 2006),
('HK-02-300123-005', N'Phan Thị Thu Trang',   '033202140325', 2002),
('HK-97-300123-006', N'Nguyễn Văn Bình',      '057197190719', 1997),
('HK-10-300123-007', N'Trịnh Văn Khôi',       '074210780917', 2010),
('HK-05-300123-008', N'Ngô Minh Tường',       '035205160131', 2005),

-- Tiếp ngày 31/01/2023
('HK-88-310123-006', N'Nguyễn Văn Hưng',      '022188350614', 1988),
('HK-98-310123-007', N'Lý Kim Quyên',         '050198910203', 1998),

-- Tiếp ngày 01/02/2023
('HK-99-010223-007', N'Vũ Ngọc Anh',          '032199910621', 1999),
('HK-85-010223-008', N'Phạm Quang Trung',     '062185850504', 1985),

-- Tiếp ngày 02/02/2023
('HK-87-020223-007', N'Cao Hồng Ân',          '043187230111', 1987),
('HK-90-020223-008', N'Lý Tuấn Khang',        '066190880220', 1990),

-- Bổ sung ngẫu nhiên để đủ 40 dòng
-- Ngày 02/02/2023
('HK-98-020223-009', N'Trần Gia Hân',         '050198140912', 1998),

-- Ngày 31/01/2023
('HK-01-310123-008', N'Nguyễn Thanh Hằng',    '004201590703', 2001),

-- Ngày 30/01/2023
('HK-04-300123-009', N'Lý Bảo Khánh',         '069204870324', 2004),
('HK-93-300123-010', N'Trịnh Quốc Huy',       '061193360517', 1993),
('HK-03-300123-011', N'Trần Thị Kim Yến',     '040203740229', 2003),
('HK-09-300123-012', N'Lê Hữu Tín',           '060209510412', 2009);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 29/01/2023
('HK-07-290123-001', N'Trần Văn Hòa',         '014207310320', 2007),
('HK-95-290123-002', N'Lê Thị Kim Oanh',      '047195150214', 1995),
('HK-06-290123-003', N'Nguyễn Thị Ánh Tuyết', '021206690801', 2006),
('HK-03-290123-004', N'Phạm Minh Thắng',      '040203210525', 2003),
('HK-91-290123-005', N'Võ Quang Vinh',        '073191540108', 1991),
('HK-04-290123-006', N'Lý Thị Thanh Mai',     '069204930117', 2004),

-- Ngày 28/01/2023
('HK-08-280123-001', N'Tống Minh Trí',        '049208570619', 2008),
('HK-93-280123-002', N'Ngô Đức Tài',          '061193130404', 1993),
('HK-02-280123-003', N'Lê Minh Hà',           '033202690225', 2002),
('HK-98-280123-004', N'Đỗ Văn Quân',          '050198780911', 1998),
('HK-10-280123-005', N'Trịnh Khánh Huyền',    '074210910405', 2010),

-- Ngày 27/01/2023
('HK-04-270123-001', N'Trương Mỹ Linh',       '069204420708', 2004),
('HK-06-270123-002', N'Nguyễn Văn Cường',     '021206710210', 2006),
('HK-96-270123-003', N'Lý Hồng Đào',          '048196870612', 1996),
('HK-09-270123-004', N'Tạ Gia Khang',         '060209430127', 2009),
('HK-94-270123-005', N'Phạm Văn Hiếu',        '063194390214', 1994),
('HK-01-270123-006', N'Vũ Thị Thu',           '004201300529', 2001),
('HK-05-270123-007', N'Ngô Thanh Tuấn',       '035205720813', 2005),

-- Ngày 26/01/2023
('HK-02-260123-001', N'Nguyễn Thị Xuân',      '033202660130', 2002),
('HK-99-260123-002', N'Lê Hoàng Việt',        '032199490803', 1999),
('HK-10-260123-003', N'Trịnh Đan Ngọc',       '074210620519', 2010),
('HK-03-260123-004', N'Vũ Gia Hưng',          '040203280728', 2003),
('HK-97-260123-005', N'Ngô Thị Mai',          '057197430122', 1997),
('HK-07-260123-006', N'Lý Minh Châu',         '014207870319', 2007),
('HK-08-260123-007', N'Tống Nhật Quang',      '049208740707', 2008),

-- Bổ sung cho đủ 40 dòng
-- Ngày 29/01/2023
('HK-89-290123-007', N'Cao Thế Anh',          '018189480706', 1989),
('HK-01-290123-008', N'Đinh Thị Hồng Nhung',  '004201270212', 2001),

-- Ngày 28/01/2023
('HK-05-280123-006', N'Trần Thị Hạnh',         '035205160715', 2005),
('HK-97-280123-007', N'Ngô Văn Sơn',           '057197360314', 1997),

-- Ngày 27/01/2023
('HK-90-270123-008', N'Bùi Minh Hương',        '066190780803', 1990),

-- Ngày 26/01/2023
('HK-95-260123-008', N'Trần Hữu Phước',        '047195910626', 1995),
('HK-04-260123-009', N'Đoàn Thu Ngân',         '069204270918', 2004),
('HK-06-260123-010', N'Lê Gia Khiêm',          '021206230324', 2006),
('HK-92-260123-011', N'Tống Thị Vân Anh',      '026192820810', 1992),
('HK-85-260123-012', N'Cao Xuân Trường',       '062185890315', 1985),

-- Ngày 29/01/2023 bổ sung
('HK-98-290123-009', N'Trịnh Thị Phương',      '050198260902', 1998),
('HK-88-290123-010', N'Vũ Quỳnh Trang',        '022188620217', 1988),
('HK-99-290123-011', N'Lê Tấn Tài',            '032199410104', 1999);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 25/01/2023
('HK-02-250123-001', N'Nguyễn Văn Hùng',      '033202210317', 2002),
('HK-95-250123-002', N'Lê Thị Kim Chi',       '047195860724', 1995),
('HK-05-250123-003', N'Phạm Hoàng Thịnh',     '035205710518', 2005),
('HK-04-250123-004', N'Ngô Thị Mỹ Duyên',     '069204340912', 2004),
('HK-07-250123-005', N'Trần Quốc Bảo',        '014207820707', 2007),
('HK-01-250123-006', N'Vũ Thanh Tuyền',       '004201950801', 2001),

-- Ngày 24/01/2023
('HK-08-240123-001', N'Tống Minh Hòa',        '049208190510', 2008),
('HK-96-240123-002', N'Lý Khánh Vy',          '048196120404', 1996),
('HK-03-240123-003', N'Trần Hải Đăng',        '040203180930', 2003),
('HK-98-240123-004', N'Nguyễn Minh Châu',     '050198500117', 1998),
('HK-09-240123-005', N'Tạ Văn Hưng',          '060209350620', 2009),
('HK-91-240123-006', N'Đỗ Văn Hạnh',          '073191600927', 1991),

-- Ngày 23/01/2023
('HK-06-230123-001', N'Nguyễn Thị Minh Hà',   '021206840615', 2006),
('HK-94-230123-002', N'Lê Thành Công',        '063194740902', 1994),
('HK-10-230123-003', N'Trịnh Phương Anh',     '074210180724', 2010),
('HK-05-230123-004', N'Ngô Minh Trường',      '035205220916', 2005),
('HK-93-230123-005', N'Vũ Quốc Toản',         '061193870331', 1993),
('HK-07-230123-006', N'Trần Nhật Lệ',         '014207540618', 2007),

-- Ngày 22/01/2023
('HK-04-220123-001', N'Phạm Thị Thảo',         '069204680315', 2004),
('HK-02-220123-002', N'Nguyễn Anh Tuấn',       '033202530708', 2002),
('HK-99-220123-003', N'Lê Khánh Ngọc',         '032199820202', 1999),
('HK-08-220123-004', N'Tống Gia Bảo',          '049208280625', 2008),
('HK-96-220123-005', N'Lý Ngọc Thảo',          '048196370424', 1996),
('HK-03-220123-006', N'Phạm Gia Linh',         '040203550321', 2003),
('HK-92-220123-007', N'Cao Văn Hào',           '026192150115', 1992),

-- Bổ sung cho đủ 40 dòng
-- Ngày 25/01/2023
('HK-03-250123-007', N'Phạm Nhật Quang',       '040203320204', 2003),
('HK-93-250123-008', N'Trịnh Văn Lâm',         '061193620307', 1993),

-- Ngày 24/01/2023
('HK-06-240123-007', N'Nguyễn Thanh Bình',     '021206490927', 2006),
('HK-97-240123-008', N'Lý Thị Hằng',           '057197780311', 1997),

-- Ngày 23/01/2023
('HK-01-230123-007', N'Vũ Gia Bảo',            '004201260829', 2001),
('HK-89-230123-008', N'Trần Thị Quỳnh Như',    '018189990418', 1989),

-- Ngày 22/01/2023
('HK-98-220123-008', N'Lê Minh Khoa',          '050198180919', 1998),
('HK-91-220123-009', N'Đinh Văn Thành',        '073191010627', 1991),
('HK-09-220123-010', N'Tạ Thị Kim Ngân',       '060209790112', 2009),
('HK-05-220123-011', N'Ngô Văn Dũng',          '035205640705', 2005),
('HK-94-220123-012', N'Lê Văn Minh',           '063194160416', 1994);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 21/01/2023
('HK-02-210123-001', N'Nguyễn Văn Duy',         '033202180405', 2002),
('HK-04-210123-002', N'Phạm Thị Yến Nhi',       '069204850611', 2004),
('HK-95-210123-003', N'Lê Văn Đức',             '047195660213', 1995),
('HK-08-210123-004', N'Tống Gia Minh',          '049208260909', 2008),
('HK-06-210123-005', N'Nguyễn Ngọc Ánh',        '021206490725', 2006),
('HK-91-210123-006', N'Đinh Văn Toản',          '073191110620', 1991),

-- Ngày 20/01/2023
('HK-03-200123-001', N'Trần Hoàng Minh',        '040203010302', 2003),
('HK-96-200123-002', N'Lý Thị Tuyết',           '048196700718', 1996),
('HK-01-200123-003', N'Vũ Văn Quý',             '004201580504', 2001),
('HK-07-200123-004', N'Trần Thanh Hằng',        '014207610725', 2007),
('HK-94-200123-005', N'Lê Văn Hòa',             '063194760215', 1994),

-- Ngày 19/01/2023
('HK-04-190123-001', N'Phạm Thành Đạt',         '069204390914', 2004),
('HK-98-190123-002', N'Lê Thị Thu',             '050198950418', 1998),
('HK-05-190123-003', N'Ngô Văn Bảo',            '035205310822', 2005),
('HK-92-190123-004', N'Cao Minh Quân',          '026192880913', 1992),
('HK-08-190123-005', N'Tống Quỳnh Trang',       '049208320526', 2008),
('HK-93-190123-006', N'Trịnh Văn Cường',        '061193540103', 1993),
('HK-09-190123-007', N'Tạ Hữu Nghĩa',           '060209670729', 2009),

-- Ngày 18/01/2023
('HK-02-180123-001', N'Nguyễn Văn Toàn',        '033202920208', 2002),
('HK-97-180123-002', N'Lý Ngọc Mai',            '057197370410', 1997),
('HK-06-180123-003', N'Nguyễn Thị Hồng',        '021206630822', 2006),
('HK-90-180123-004', N'Trần Văn Khánh',         '025190370319', 1990),
('HK-03-180123-005', N'Trần Thanh Thảo',        '040203620927', 2003),
('HK-95-180123-006', N'Lê Nhật Hào',            '047195410704', 1995),
('HK-10-180123-007', N'Trịnh Thị Lệ',           '074210180129', 2010),

-- Bổ sung thêm cho đủ 40 dòng:
-- Ngày 21/01/2023
('HK-07-210123-007', N'Trần Nhật Nam',          '014207320102', 2007),
('HK-10-210123-008', N'Trịnh Anh Thư',          '074210690825', 2010),

-- Ngày 20/01/2023
('HK-99-200123-006', N'Lê Thanh Ngân',          '032199480811', 1999),
('HK-08-200123-007', N'Tống Đức Tài',           '049208150517', 2008),

-- Ngày 19/01/2023
('HK-96-190123-008', N'Lý Hoàng Linh',          '048196710417', 1996),

-- Ngày 18/01/2023
('HK-01-180123-008', N'Vũ Minh Tiến',           '004201750722', 2001),
('HK-94-180123-009', N'Lê Kim Phượng',          '063194830602', 1994),
('HK-05-180123-010', N'Ngô Văn Lâm',            '035205610227', 2005),
('HK-91-180123-011', N'Đinh Thị Lan',           '073191920919', 1991),
('HK-89-180123-012', N'Trần Văn Dương',         '018189640701', 1989);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 17/01/2023
('HK-02-170123-001', N'Nguyễn Thị Hòa',          '033202540825', 2002),
('HK-94-170123-002', N'Lê Văn Hùng',             '063194230715', 1994),
('HK-06-170123-003', N'Nguyễn Minh Hương',       '021206580316', 2006),
('HK-95-170123-004', N'Lê Hoàng Tùng',           '047195900318', 1995),
('HK-08-170123-005', N'Tống Văn Kiên',           '049208160920', 2008),

-- Ngày 16/01/2023
('HK-04-160123-001', N'Phạm Thị Bích',           '069204470510', 2004),
('HK-03-160123-002', N'Trần Minh Khoa',          '040203690804', 2003),
('HK-92-160123-003', N'Cao Văn Khải',            '026192420929', 1992),
('HK-07-160123-004', N'Trần Thị Thu Hà',         '014207870801', 2007),

-- Ngày 15/01/2023
('HK-01-150123-001', N'Vũ Thị Ngọc Mai',         '004201280516', 2001),
('HK-93-150123-002', N'Trịnh Hoàng Sơn',         '061193470701', 1993),
('HK-05-150123-003', N'Ngô Quang Huy',           '035205250428', 2005),
('HK-90-150123-004', N'Trần Quang Vinh',         '025190830112', 1990),
('HK-09-150123-005', N'Tạ Thị Kim Ngân',         '060209770724', 2009),
('HK-10-150123-006', N'Trịnh Thị Linh',          '074210410828', 2010),

-- Ngày 14/01/2023
('HK-02-140123-001', N'Nguyễn Hữu Đức',          '033202630429', 2002),
('HK-91-140123-002', N'Đinh Văn Hiếu',           '073191760911', 1991),
('HK-06-140123-003', N'Nguyễn Thị Diễm',         '021206310307', 2006),
('HK-98-140123-004', N'Lê Văn Phú',              '050198520714', 1998),
('HK-08-140123-005', N'Tống Thị Nguyệt',         '049208250813', 2008),
('HK-97-140123-006', N'Lý Đức Tâm',              '057197130515', 1997),
('HK-04-140123-007', N'Phạm Minh Thư',           '069204760620', 2004),
('HK-89-140123-008', N'Trần Thị Tuyết',          '018189620728', 1989),

-- Thêm cho đủ 40 dòng:
-- Ngày 17/01/2023
('HK-97-170123-006', N'Lý Minh Châu',            '057197290406', 1997),
('HK-03-170123-007', N'Trần Văn Nhật',           '040203920914', 2003),

-- Ngày 16/01/2023
('HK-99-160123-005', N'Lê Thị Quỳnh',            '032199470717', 1999),
('HK-10-160123-006', N'Trịnh Thanh Tùng',        '074210390523', 2010),

-- Ngày 15/01/2023
('HK-07-150123-007', N'Trần Hoàng Giang',        '014207520930', 2007),
('HK-98-150123-008', N'Lê Thị Hà My',            '050198620426', 1998),

-- Ngày 14/01/2023
('HK-96-140123-009', N'Lý Minh Tuấn',            '048196510629', 1996),
('HK-05-140123-010', N'Ngô Văn Tài',             '035205710218', 2005),
('HK-94-140123-011', N'Lê Thị Ngọc',             '063194640601', 1994),
('HK-07-140123-012', N'Trần Văn Phúc',           '014207210322', 2007),
('HK-01-140123-013', N'Vũ Văn Hưng',             '004201940125', 2001);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 13/01/2023
('HK-03-130123-001', N'Trần Thanh Tú',           '040203840926', 2003),
('HK-08-130123-002', N'Tống Thị Yến',            '049208710216', 2008),
('HK-97-130123-003', N'Lý Thị Hương',            '057197630810', 1997),
('HK-06-130123-004', N'Nguyễn Thanh Lâm',        '021206290918', 2006),
('HK-91-130123-005', N'Đinh Quang Dũng',         '073191890802', 1991),
('HK-94-130123-006', N'Lê Minh Hà',              '063194230723', 1994),

-- Ngày 12/01/2023
('HK-01-120123-001', N'Vũ Trường Giang',         '004201350103', 2001),
('HK-04-120123-002', N'Phạm Thị Trang',          '069204920630', 2004),
('HK-90-120123-003', N'Trần Quốc Trung',         '025190820927', 1990),
('HK-95-120123-004', N'Lê Văn Nghĩa',            '047195730124', 1995),
('HK-02-120123-005', N'Nguyễn Ngọc Bích',        '033202580627', 2002),

-- Ngày 11/01/2023
('HK-92-110123-001', N'Cao Văn Duy',             '026192350529', 1992),
('HK-05-110123-002', N'Ngô Thị Lan',             '035205290814', 2005),
('HK-10-110123-003', N'Trịnh Văn Sơn',           '074210410616', 2010),
('HK-93-110123-004', N'Trịnh Minh Khoa',         '061193520703', 1993),
('HK-06-110123-005', N'Nguyễn Quang Hưng',       '021206790430', 2006),
('HK-98-110123-006', N'Lê Văn Cường',            '050198670917', 1998),

-- Ngày 10/01/2023
('HK-89-100123-001', N'Trần Thị Mai',            '018189540329', 1989),
('HK-07-100123-002', N'Trần Văn Lộc',            '014207940901', 2007),
('HK-01-100123-003', N'Vũ Thị Khánh',            '004201680528', 2001),
('HK-03-100123-004', N'Trần Văn Trường',         '040203420321', 2003),
('HK-97-100123-005', N'Lý Quốc Thắng',           '057197940704', 1997),
('HK-09-100123-006', N'Tạ Thị Hồng Nhung',       '060209310730', 2009),
('HK-02-100123-007', N'Nguyễn Trọng Phú',        '033202940224', 2002),
('HK-94-100123-008', N'Lê Bảo Ngọc',             '063194850820', 1994),

-- Bổ sung thêm hành khách để đủ 40 dòng:
-- Ngày 13/01/2023
('HK-10-130123-007', N'Trịnh Minh Tâm',          '074210320201', 2010),

-- Ngày 12/01/2023
('HK-96-120123-006', N'Lý Phương Thảo',          '048196650518', 1996),
('HK-08-120123-007', N'Tống Minh Tuấn',          '049208220728', 2008),

-- Ngày 11/01/2023
('HK-04-110123-007', N'Phạm Thị Hạnh',           '069204990326', 2004),
('HK-99-110123-008', N'Lê Thị Hằng',             '032199720707', 1999),

-- Ngày 10/01/2023
('HK-05-100123-009', N'Ngô Quang Hải',           '035205830110', 2005),
('HK-91-100123-010', N'Đinh Thanh Vân',          '073191780801', 1991),
('HK-06-100123-011', N'Nguyễn Thị Huyền',        '021206390913', 2006),
('HK-95-100123-012', N'Lê Thành Tâm',            '047195960927', 1995),
('HK-07-100123-013', N'Trần Minh Thành',         '014207270828', 2007),
('HK-98-100123-014', N'Lê Thị Mỹ Duyên',         '050198910304', 1998);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 31/03/2025
('HK-05-310325-001', N'Ngô Văn Long',            '035205680213', 2005),
('HK-93-310325-002', N'Trịnh Thị Hằng',          '061193340430', 1993),
('HK-01-310325-003', N'Vũ Thanh Bình',           '004201890725', 2001),
('HK-10-310325-004', N'Trịnh Văn Hải',           '074210460209', 2010),
('HK-96-310325-005', N'Lý Hồng Ngọc',            '048196950612', 1996),

-- Ngày 30/03/2025
('HK-94-300325-001', N'Lê Thị Cẩm Tú',           '063194410103', 1994),
('HK-03-300325-002', N'Trần Thị Hoa',            '040203750615', 2003),
('HK-02-300325-003', N'Nguyễn Văn Hùng',         '033202890427', 2002),
('HK-08-300325-004', N'Tống Văn Tài',            '049208330720', 2008),

-- Ngày 29/03/2025
('HK-06-290325-001', N'Nguyễn Hoàng Phúc',       '021206980318', 2006),
('HK-97-290325-002', N'Lý Thị Xuân',             '057197490816', 1997),
('HK-91-290325-003', N'Đinh Minh Khang',         '073191160128', 1991),
('HK-04-290325-004', N'Phạm Văn Nghĩa',          '069204520728', 2004),
('HK-95-290325-005', N'Lê Tấn Phát',             '047195290603', 1995),

-- Ngày 28/03/2025
('HK-07-280325-001', N'Trần Nhật Minh',          '014207310707', 2007),
('HK-92-280325-002', N'Cao Thị Bích',            '026192740205', 1992),
('HK-05-280325-003', N'Ngô Phương Anh',          '035205240610', 2005),
('HK-09-280325-004', N'Tạ Minh Quân',            '060209380910', 2009),

-- Ngày 27/03/2025
('HK-89-270325-001', N'Trần Hoài Thương',        '018189760504', 1989),
('HK-04-270325-002', N'Phạm Thị Dung',           '069204220415', 2004),
('HK-10-270325-003', N'Trịnh Hoàng Vũ',          '074210140226', 2010),
('HK-98-270325-004', N'Lê Quốc Việt',            '050198970719', 1998),

-- Ngày 26/03/2025
('HK-94-260325-001', N'Lê Bảo Khang',            '063194230920', 1994),
('HK-01-260325-002', N'Vũ Văn Sơn',              '004201780514', 2001),
('HK-97-260325-003', N'Lý Mỹ Linh',              '057197310911', 1997),
('HK-08-260325-004', N'Tống Quốc Huy',           '049208140127', 2008),
('HK-02-260325-005', N'Nguyễn Văn Đại',          '033202650628', 2002),

-- Ngày 25/03/2025
('HK-96-250325-001', N'Lý Thị Thanh Mai',        '048196730418', 1996),
('HK-06-250325-002', N'Nguyễn Nhật Linh',        '021206490509', 2006),
('HK-91-250325-003', N'Đinh Minh Trí',           '073191360111', 1991),

-- Ngày 24/03/2025
('HK-93-240325-001', N'Trịnh Văn Hoàng',         '061193550821', 1993),
('HK-05-240325-002', N'Ngô Thanh Hằng',          '035205820530', 2005),
('HK-03-240325-003', N'Trần Văn Đông',           '040203970804', 2003),
('HK-90-240325-004', N'Trần Thị Phương',         '025190330129', 1990),

-- Ngày 23/03/2025
('HK-95-230325-001', N'Lê Nhật Minh',            '047195670716', 1995),
('HK-04-230325-002', N'Phạm Hoàng Tuấn',         '069204110804', 2004),
('HK-10-230325-003', N'Trịnh Thanh Thảo',        '074210990523', 2010),
('HK-07-230325-004', N'Trần Phương Nhi',         '014207340925', 2007),
('HK-92-230325-005', N'Cao Văn Hùng',            '026192250105', 1992);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 22/03/2025
('HK-06-220325-001', N'Nguyễn Minh Hoàng',       '021206760613', 2006),
('HK-93-220325-002', N'Trịnh Hoài Phong',        '061193810121', 1993),
('HK-08-220325-003', N'Tống Thị Bích Hạnh',      '049208320419', 2008),
('HK-04-220325-004', N'Phạm Quốc Cường',         '069204680930', 2004),

-- Ngày 21/03/2025
('HK-05-210325-001', N'Ngô Thanh Sơn',           '035205970216', 2005),
('HK-92-210325-002', N'Cao Minh Tâm',            '026192580108', 1992),
('HK-09-210325-003', N'Tạ Văn Phước',            '060209290312', 2009),
('HK-01-210325-004', N'Vũ Thị Kiều Trang',       '004201620606', 2001),
('HK-95-210325-005', N'Lê Quang Duy',            '047195730615', 1995),

-- Ngày 20/03/2025
('HK-03-200325-001', N'Trần Quốc Vinh',          '040203840327', 2003),
('HK-96-200325-002', N'Lý Thành Công',           '048196240416', 1996),
('HK-07-200325-003', N'Trần Hoàng Nam',          '014207910922', 2007),
('HK-10-200325-004', N'Trịnh Bảo Hân',           '074210490801', 2010),
('HK-89-200325-005', N'Trần Minh Hậu',           '018189180702', 1989),
('HK-94-200325-006', N'Lê Văn Hào',              '063194630416', 1994),

-- Ngày 19/03/2025
('HK-98-190325-001', N'Lê Trọng Tấn',            '050198220330', 1998),
('HK-04-190325-002', N'Phạm Anh Đức',            '069204770328', 2004),
('HK-91-190325-003', N'Đinh Thị Hiền',           '073191330716', 1991),
('HK-02-190325-004', N'Nguyễn Thanh Tuyền',      '033202160213', 2002),
('HK-06-190325-005', N'Nguyễn Văn Lực',          '021206350828', 2006),

-- Ngày 18/03/2025
('HK-97-180325-001', N'Lý Kim Yến',              '057197470605', 1997),
('HK-92-180325-002', N'Cao Văn Đại',             '026192120123', 1992),
('HK-08-180325-003', N'Tống Mai Anh',            '049208530914', 2008),
('HK-95-180325-004', N'Lê Thị Tuyết',            '047195150721', 1995),
('HK-03-180325-005', N'Trần Hồng Ngọc',          '040203540930', 2003),

-- Ngày 17/03/2025
('HK-05-170325-001', N'Ngô Trọng Nhân',          '035205640911', 2005),
('HK-10-170325-002', N'Trịnh Quốc Bảo',          '074210750730', 2010),
('HK-93-170325-003', N'Trịnh Minh Thư',          '061193290211', 1993),
('HK-07-170325-004', N'Trần Bảo Ngọc',           '014207690404', 2007),

-- Ngày 16/03/2025
('HK-01-160325-001', N'Vũ Quốc Việt',            '004201290528', 2001),
('HK-96-160325-002', N'Lý Văn Đức',              '048196980703', 1996),
('HK-89-160325-003', N'Trần Hữu Tài',            '018189320104', 1989),
('HK-02-160325-004', N'Nguyễn Đăng Khoa',        '033202260209', 2002),
('HK-06-160325-005', N'Nguyễn Minh Tuấn',        '021206720811', 2006),

-- Ngày 15/03/2025
('HK-90-150325-001', N'Trần Khánh Hòa',          '025190840827', 1990),
('HK-94-150325-002', N'Lê Trung Hậu',            '063194360628', 1994),
('HK-09-150325-003', N'Tạ Hữu Đức',              '060209120321', 2009),
('HK-04-150325-004', N'Phạm Như Ý',              '069204110419', 2004),
('HK-97-150325-005', N'Lý Minh Thái',            '057197970928', 1997);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 14/03/2025
('HK-08-140325-001', N'Tống Minh Trí',           '049208190213', 2008),
('HK-93-140325-002', N'Trịnh Công Thành',        '061193240616', 1993),
('HK-04-140325-003', N'Phạm Thị Thanh Thảo',     '069204450119', 2004),
('HK-10-140325-004', N'Trịnh Nhật Hào',          '074210610311', 2010),

-- Ngày 13/03/2025
('HK-03-130325-001', N'Trần Hữu Thiện',          '040203390827', 2003),
('HK-06-130325-002', N'Nguyễn Thị Thùy Linh',     '021206330222', 2006),
('HK-94-130325-003', N'Lê Thành Hưng',           '063194130715', 1994),
('HK-97-130325-004', N'Lý Văn Quý',              '057197270220', 1997),

-- Ngày 12/03/2025
('HK-95-120325-001', N'Lê Quốc Thắng',           '047195950325', 1995),
('HK-05-120325-002', N'Ngô Minh Hiếu',           '035205710204', 2005),
('HK-01-120325-003', N'Vũ Thị Thu Hằng',         '004201140311', 2001),
('HK-09-120325-004', N'Tạ Thị Bảo Ngọc',          '060209310714', 2009),

-- Ngày 11/03/2025
('HK-92-110325-001', N'Cao Thái Sơn',            '026192170403', 1992),
('HK-89-110325-002', N'Trần Kim Oanh',           '018189740205', 1989),
('HK-04-110325-003', N'Phạm Quốc Thiên',         '069204360318', 2004),
('HK-10-110325-004', N'Trịnh Hải Đăng',          '074210180826', 2010),
('HK-90-110325-005', N'Trần Nhật Tân',           '025190260730', 1990),

-- Ngày 10/03/2025
('HK-02-100325-001', N'Nguyễn Thị Kim Anh',       '033202250818', 2002),
('HK-96-100325-002', N'Lý Tuấn Hưng',            '048196310314', 1996),
('HK-06-100325-003', N'Nguyễn Văn Nghĩa',        '021206930424', 2006),
('HK-07-100325-004', N'Trần Mai Vy',             '014207720619', 2007),
('HK-98-100325-005', N'Lê Tấn Phát',             '050198750702', 1998),

-- Ngày 09/03/2025
('HK-03-090325-001', N'Trần Phúc Hậu',           '040203920228', 2003),
('HK-91-090325-002', N'Đinh Minh Nhật',          '073191550709', 1991),
('HK-08-090325-003', N'Tống Thị Phương Uyên',    '049208830916', 2008),
('HK-95-090325-004', N'Lê Thị Minh Ngọc',        '047195160728', 1995),

-- Ngày 08/03/2025
('HK-94-080325-001', N'Lê Văn Bình',             '063194670727', 1994),
('HK-89-080325-002', N'Trần Minh Tiến',          '018189580212', 1989),
('HK-01-080325-003', N'Vũ Kim Ngân',             '004201690707', 2001),
('HK-07-080325-004', N'Trần Bảo Hưng',           '014207180213', 2007),

-- Ngày 07/03/2025
('HK-04-070325-001', N'Phạm Duy Long',           '069204870626', 2004),
('HK-97-070325-002', N'Lý Thị Như Quỳnh',        '057197130205', 1997),
('HK-92-070325-003', N'Cao Văn Minh',            '026192280406', 1992),
('HK-06-070325-004', N'Nguyễn Duy Phong',        '021206120603', 2006),

-- Ngày 06/03/2025
('HK-05-060325-001', N'Ngô Văn Khải',            '035205650321', 2005),
('HK-93-060325-002', N'Trịnh Anh Hào',           '061193690316', 1993),
('HK-02-060325-003', N'Nguyễn Thị Bích Loan',    '033202390817', 2002),
('HK-10-060325-004', N'Trịnh Minh Tâm',          '074210240427', 2010);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 05/03/2025
('HK-03-050325-001', N'Trần Duy Tân',             '040203470707', 2003),
('HK-95-050325-002', N'Lê Văn Thành',            '047195160322', 1995),
('HK-06-050325-003', N'Nguyễn Thị Hoa',          '021206740329', 2006),
('HK-94-050325-004', N'Lê Trung Hiếu',           '063194920909', 1994),

-- Ngày 04/03/2025
('HK-01-040325-001', N'Vũ Minh Quang',           '004201650801', 2001),
('HK-89-040325-002', N'Trần Văn Bảo',            '018189740719', 1989),
('HK-10-040325-003', N'Trịnh Nhật Minh',         '074210630527', 2010),
('HK-04-040325-004', N'Phạm Ngọc Trâm',          '069204230726', 2004),
('HK-08-040325-005', N'Tống Kim Hạnh',           '049208560921', 2008),

-- Ngày 03/03/2025
('HK-02-030325-001', N'Nguyễn Thành Đạt',        '033202990201', 2002),
('HK-93-030325-002', N'Trịnh Quốc Trung',        '061193820617', 1993),
('HK-97-030325-003', N'Lý Khánh Vân',            '057197210410', 1997),
('HK-07-030325-004', N'Trần Ngọc Hân',           '014207610730', 2007),

-- Ngày 02/03/2025
('HK-90-020325-001', N'Trần Thế Phong',          '025190180519', 1990),
('HK-05-020325-002', N'Ngô Phúc Lộc',            '035205250913', 2005),
('HK-92-020325-003', N'Cao Thị Kim Chi',         '026192470424', 1992),
('HK-06-020325-004', N'Nguyễn Hữu Tài',          '021206910405', 2006),

-- Ngày 01/03/2025
('HK-91-010325-001', N'Đinh Tấn Phát',           '073191410603', 1991),
('HK-10-010325-002', N'Trịnh Bảo An',            '074210690318', 2010),
('HK-04-010325-003', N'Phạm Thị Thanh Mai',      '069204890622', 2004),
('HK-95-010325-004', N'Lê Tuấn Kiệt',            '047195550108', 1995),
('HK-89-010325-005', N'Trần Nhật Hưng',          '018189120809', 1989),

-- Ngày 29/02/2025
('HK-94-290225-001', N'Lê Thị Tuyết Nhung',      '063194980204', 1994),
('HK-02-290225-002', N'Nguyễn Nhật Linh',        '033202860811', 2002),
('HK-08-290225-003', N'Tống Văn Đạt',            '049208490423', 2008),
('HK-06-290225-004', N'Nguyễn Văn Đông',         '021206330218', 2006),

-- Ngày 28/02/2025
('HK-07-280225-001', N'Trần Như Quỳnh',          '014207130417', 2007),
('HK-03-280225-002', N'Trần Minh Nhựt',          '040203370921', 2003),
('HK-96-280225-003', N'Lý Nhật Nam',             '048196410106', 1996),
('HK-04-280225-004', N'Phạm Bảo Khang',          '069204710915', 2004),
('HK-01-280225-005', N'Vũ Bích Thủy',            '004201780122', 2001),

-- Ngày 27/02/2025
('HK-92-270225-001', N'Cao Hữu Nghĩa',           '026192120310', 1992),
('HK-93-270225-002', N'Trịnh Hồng Phúc',         '061193240510', 1993),
('HK-05-270225-003', N'Ngô Thị Thanh Vân',       '035205930428', 2005),
('HK-97-270225-004', N'Lý Phương Uyên',          '057197540727', 1997);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 26/02/2025
('HK-08-260225-001', N'Tống Quỳnh Như',          '049208870618', 2008),
('HK-10-260225-002', N'Trịnh Nhật Linh',         '074210640921', 2010),
('HK-01-260225-003', N'Vũ Thanh Tùng',           '004201430113', 2001),
('HK-03-260225-004', N'Trần Minh Hải',           '040203210605', 2003),
('HK-92-260225-005', N'Cao Đức Thịnh',           '026192350212', 1992),

-- Ngày 25/02/2025
('HK-04-250225-001', N'Phạm Văn Cường',          '069204480302', 2004),
('HK-05-250225-002', N'Ngô Thị Bích Hằng',       '035205230815', 2005),
('HK-94-250225-003', N'Lê Minh Tâm',             '063194940821', 1994),
('HK-06-250225-004', N'Nguyễn Bảo Hân',          '021206370117', 2006),

-- Ngày 24/02/2025
('HK-89-240225-001', N'Trần Văn Minh',           '018189230105', 1989),
('HK-91-240225-002', N'Đinh Văn Duy',            '073191850225', 1991),
('HK-95-240225-003', N'Lê Hồng Sơn',             '047195390116', 1995),
('HK-07-240225-004', N'Trần Nhật Hoàng',         '014207570214', 2007),

-- Ngày 23/02/2025
('HK-02-230225-001', N'Nguyễn Thị Mỹ Linh',      '033202680427', 2002),
('HK-96-230225-002', N'Lý Gia Hưng',             '048196690525', 1996),
('HK-93-230225-003', N'Trịnh Xuân Khánh',        '061193390428', 1993),
('HK-10-230225-004', N'Trịnh Ngọc Trân',         '074210790301', 2010),
('HK-04-230225-005', N'Phạm Đức Minh',           '069204160310', 2004),

-- Ngày 22/02/2025
('HK-03-220225-001', N'Trần Thanh Hằng',         '040203850831', 2003),
('HK-08-220225-002', N'Tống Quang Huy',          '049208290929', 2008),
('HK-97-220225-003', N'Lý Bảo Long',             '057197810110', 1997),
('HK-06-220225-004', N'Nguyễn Hữu Thắng',        '021206510603', 2006),

-- Ngày 21/02/2025
('HK-05-210225-001', N'Ngô Trọng Nghĩa',         '035205220628', 2005),
('HK-01-210225-002', N'Vũ Minh Thư',             '004201880709', 2001),
('HK-92-210225-003', N'Cao Tuấn Anh',            '026192150204', 1992),
('HK-89-210225-004', N'Trần Kim Anh',            '018189590723', 1989),

-- Ngày 20/02/2025
('HK-90-200225-001', N'Trần Văn Giang',          '025190360612', 1990),
('HK-07-200225-002', N'Trần Diễm My',            '014207440927', 2007),
('HK-10-200225-003', N'Trịnh Đức Lợi',           '074210940821', 2010),
('HK-94-200225-004', N'Lê Đức Tài',              '063194770826', 1994),

-- Ngày 19/02/2025
('HK-93-190225-001', N'Trịnh Minh Châu',         '061193610917', 1993),
('HK-04-190225-002', N'Phạm Vĩnh Hưng',          '069204760201', 2004),
('HK-08-190225-003', N'Tống Lê Huyền',           '049208890603', 2008),
('HK-95-190225-004', N'Lê Nhật Quang',           '047195450815', 1995),

-- Ngày 18/02/2025
('HK-06-180225-001', N'Nguyễn Văn Linh',         '021206410812', 2006),
('HK-96-180225-002', N'Lý Hoài Nam',             '048196830324', 1996),
('HK-03-180225-003', N'Trần Thị Mỹ Duyên',       '040203580503', 2003),
('HK-91-180225-004', N'Đinh Văn Kiệt',           '073191960728', 1991),

-- Ngày 17/02/2025
('HK-02-170225-001', N'Nguyễn Hữu Đức',          '033202740619', 2002),
('HK-89-170225-002', N'Trần Ngọc Thái',          '018189610307', 1989),
('HK-07-170225-003', N'Trần Kim Phụng',          '014207290427', 2007);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 16/02/2025
('HK-08-160225-001', N'Tống Hoài Nam',            '049208890615', 2008),
('HK-10-160225-002', N'Trịnh Thị Nhung',          '074210920524', 2010),
('HK-01-160225-003', N'Vũ Hữu Cường',             '004201870912', 2001),
('HK-03-160225-004', N'Trần Diệu Linh',           '040203250216', 2003),

-- Ngày 15/02/2025
('HK-04-150225-001', N'Phạm Quang Huy',           '069204350319', 2004),
('HK-05-150225-002', N'Ngô Văn Kiên',             '035205160830', 2005),
('HK-94-150225-003', N'Lê Quốc Hùng',             '063194690108', 1994),
('HK-06-150225-004', N'Nguyễn Ngọc Bích',         '021206510919', 2006),
('HK-92-150225-005', N'Cao Minh Trí',             '026192360112', 1992),

-- Ngày 14/02/2025
('HK-89-140225-001', N'Trần Thị Hòa',             '018189270820', 1989),
('HK-91-140225-002', N'Đinh Văn Dũng',            '073191830405', 1991),
('HK-95-140225-003', N'Lê Bảo Phúc',              '047195420520', 1995),
('HK-07-140225-004', N'Trần Thanh Huyền',         '014207350911', 2007),

-- Ngày 13/02/2025
('HK-02-130225-001', N'Nguyễn Minh Phương',       '033202610225', 2002),
('HK-96-130225-002', N'Lý Văn Hậu',               '048196790707', 1996),
('HK-93-130225-003', N'Trịnh Thanh Tú',           '061193480912', 1993),
('HK-10-130225-004', N'Trịnh Văn Lộc',            '074210870930', 2010),

-- Ngày 12/02/2025
('HK-04-120225-001', N'Phạm Gia Bảo',             '069204580814', 2004),
('HK-03-120225-002', N'Trần Hữu Lộc',             '040203360525', 2003),
('HK-08-120225-003', N'Tống Minh Khoa',           '049208440318', 2008),
('HK-97-120225-004', N'Lý Thị Thùy Linh',         '057197650826', 1997),
('HK-05-120225-005', N'Ngô Hồng Hạnh',            '035205730910', 2005),

-- Ngày 11/02/2025
('HK-01-110225-001', N'Vũ Ngọc Mai',              '004201740729', 2001),
('HK-07-110225-002', N'Trần Văn Sơn',             '014207820223', 2007),
('HK-90-110225-003', N'Trần Thị Yến',             '025190540916', 1990),
('HK-94-110225-004', N'Lê Hồng Quang',            '063194450108', 1994),

-- Ngày 10/02/2025
('HK-06-100225-001', N'Nguyễn Hoàng Tùng',        '021206470915', 2006),
('HK-89-100225-002', N'Trần Thị Hoa',             '018189610301', 1989),
('HK-91-100225-003', N'Đinh Phúc Vinh',           '073191940208', 1991),
('HK-10-100225-004', N'Trịnh Gia Long',           '074210950202', 2010),

-- Ngày 09/02/2025
('HK-95-090225-001', N'Lê Văn Đạt',               '047195290722', 1995),
('HK-92-090225-002', N'Cao Thị Mỹ Dung',          '026192450810', 1992),
('HK-08-090225-003', N'Tống Khánh Linh',          '049208370202', 2008),
('HK-02-090225-004', N'Nguyễn Minh Anh',          '033202750419', 2002),

-- Ngày 08/02/2025
('HK-93-080225-001', N'Trịnh Bảo Châu',           '061193850307', 1993),
('HK-03-080225-002', N'Trần Đức Thịnh',           '040203260925', 2003),
('HK-04-080225-003', N'Phạm Thị Vân',             '069204320911', 2004),
('HK-96-080225-004', N'Lý Trung Kiên',            '048196840912', 1996);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 07/02/2025
('HK-07-070225-001', N'Trần Thanh Vân',           '014207980710', 2007),
('HK-06-070225-002', N'Nguyễn Văn Bình',          '021206330503', 2006),
('HK-91-070225-003', N'Đinh Hữu Nam',             '073191590916', 1991),
('HK-04-070225-004', N'Phạm Thị Mai',             '069204180927', 2004),

-- Ngày 06/02/2025
('HK-95-060225-001', N'Lê Thái Sơn',              '047195310810', 1995),
('HK-08-060225-002', N'Tống Thị Duyên',           '049208540327', 2008),
('HK-03-060225-003', N'Trần Quốc Việt',           '040203170814', 2003),
('HK-02-060225-004', N'Nguyễn Đức Long',          '033202680503', 2002),
('HK-93-060225-005', N'Trịnh Văn Kiệt',           '061193330415', 1993),

-- Ngày 05/02/2025
('HK-01-050225-001', N'Vũ Thị Thanh',             '004201830318', 2001),
('HK-96-050225-002', N'Lý Ngọc Lan',              '048196690924', 1996),
('HK-89-050225-003', N'Trần Văn Quý',             '018189120819', 1989),
('HK-04-050225-004', N'Phạm Minh Đức',            '069204260217', 2004),

-- Ngày 04/02/2025
('HK-07-040225-001', N'Trần Thị Ngọc',            '014207720515', 2007),
('HK-10-040225-002', N'Trịnh Gia Huy',            '074210860409', 2010),
('HK-05-040225-003', N'Ngô Trọng Nghĩa',          '035205920601', 2005),
('HK-90-040225-004', N'Trần Phương Nam',          '025190790209', 1990),

-- Ngày 03/02/2025
('HK-06-030225-001', N'Nguyễn Thanh Bình',        '021206180304', 2006),
('HK-94-030225-002', N'Lê Mỹ Linh',               '063194650725', 1994),
('HK-92-030225-003', N'Cao Minh Hiếu',            '026192270228', 1992),
('HK-91-030225-004', N'Đinh Quốc Hưng',           '073191780311', 1991),
('HK-08-030225-005', N'Tống Đình Quang',          '049208660421', 2008),

-- Ngày 02/02/2025
('HK-02-020225-001', N'Nguyễn Ngọc Quỳnh',        '033202830625', 2002),
('HK-95-020225-002', N'Lê Thanh Hà',              '047195140118', 1995),
('HK-03-020225-003', N'Trần Ngọc Anh',            '040203880818', 2003),
('HK-10-020225-004', N'Trịnh Tuấn Vũ',            '074210530601', 2010),

-- Ngày 01/02/2025
('HK-93-010225-001', N'Trịnh Thị Nhàn',           '061193510706', 1993),
('HK-05-010225-002', N'Ngô Văn Dũng',             '035205270125', 2005),
('HK-89-010225-003', N'Trần Hoàng Anh',           '018189850109', 1989),
('HK-01-010225-004', N'Vũ Ngọc Hòa',              '004201920403', 2001),

-- Ngày 31/01/2025
('HK-04-310125-001', N'Phạm Đức Thịnh',           '069204690930', 2004),
('HK-07-310125-002', N'Trần Thị Thanh Mai',       '014207410623', 2007),
('HK-96-310125-003', N'Lý Quốc Bảo',              '048196430301', 1996),
('HK-06-310125-004', N'Nguyễn Hoàng Anh',         '021206980821', 2006),
('HK-92-310125-005', N'Cao Gia Bảo',              '026192690315', 1992),

-- Ngày 30/01/2025
('HK-90-300125-001', N'Trần Hữu Duy',             '025190240828', 1990),
('HK-08-300125-002', N'Tống Thị Hiền',            '049208250714', 2008),
('HK-10-300125-003', N'Trịnh Văn Hoàng',          '074210680903', 2010),
('HK-94-300125-004', N'Lê Thanh Tùng',            '063194750418', 1994);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 29/01/2025
('HK-05-290125-001', N'Ngô Minh Châu',             '035205490812', 2005),
('HK-03-290125-002', N'Trần Hải Quân',             '040203270305', 2003),
('HK-07-290125-003', N'Trần Thị Yến Nhi',          '014207330914', 2007),
('HK-96-290125-004', N'Lý Nhật Minh',              '048196550726', 1996),

-- Ngày 28/01/2025
('HK-93-280125-001', N'Trịnh Minh Nhật',           '061193340409', 1993),
('HK-06-280125-002', N'Nguyễn Gia Hân',            '021206790928', 2006),
('HK-02-280125-003', N'Nguyễn Văn Thắng',          '033202460721', 2002),
('HK-08-280125-004', N'Tống Hữu Tài',              '049208290615', 2008),
('HK-01-280125-005', N'Vũ Thanh Long',             '004201840512', 2001),

-- Ngày 27/01/2025
('HK-04-270125-001', N'Phạm Thị Dung',             '069204540215', 2004),
('HK-94-270125-002', N'Lê Hồng Nhung',             '063194850301', 1994),
('HK-10-270125-003', N'Trịnh Quang Minh',          '074210480504', 2010),
('HK-89-270125-004', N'Trần Thanh Bình',           '018189350510', 1989),
('HK-07-270125-005', N'Trần Ngọc Quỳnh',           '014207750321', 2007),

-- Ngày 26/01/2025
('HK-91-260125-001', N'Đinh Nhật Hào',             '073191560407', 1991),
('HK-08-260125-002', N'Tống Thị Thảo',             '049208960920', 2008),
('HK-03-260125-003', N'Trần Minh Thành',           '040203120829', 2003),
('HK-05-260125-004', N'Ngô Văn Đức',               '035205030625', 2005),

-- Ngày 25/01/2025
('HK-92-250125-001', N'Cao Văn Khôi',              '026192180707', 1992),
('HK-90-250125-002', N'Trần Hải Nam',              '025190610206', 1990),
('HK-06-250125-003', N'Nguyễn Thị Hồng',           '021206740421', 2006),
('HK-95-250125-004', N'Lê Văn Duy',                '047195110102', 1995),

-- Ngày 24/01/2025
('HK-10-240125-001', N'Trịnh Văn Lộc',             '074210310715', 2010),
('HK-01-240125-002', N'Vũ Thị Minh',               '004201620305', 2001),
('HK-96-240125-003', N'Lý Thị Lan',                '048196710810', 1996),
('HK-89-240125-004', N'Trần Thị Yến',              '018189940115', 1989),

-- Ngày 23/01/2025
('HK-07-230125-001', N'Trần Thanh Hương',          '014207890824', 2007),
('HK-02-230125-002', N'Nguyễn Hữu Thắng',          '033202980117', 2002),
('HK-93-230125-003', N'Trịnh Quang Duy',           '061193640909', 1993),
('HK-04-230125-004', N'Phạm Thị Hòa',              '069204370306', 2004),

-- Ngày 22/01/2025
('HK-08-220125-001', N'Tống Văn Minh',             '049208450408', 2008),
('HK-91-220125-002', N'Đinh Trung Kiên',           '073191260924', 1991),
('HK-06-220125-003', N'Nguyễn Minh Khôi',          '021206920730', 2006),
('HK-94-220125-004', N'Lê Hoàng Hà',               '063194510212', 1994),

-- Ngày 21/01/2025
('HK-95-210125-001', N'Lê Thị Bích',               '047195210410', 1995),
('HK-03-210125-002', N'Trần Văn Thọ',              '040203460603', 2003),
('HK-92-210125-003', N'Cao Hữu Đạt',               '026192650109', 1992),
('HK-05-210125-004', N'Ngô Bảo Linh',              '035205970826', 2005);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 20/01/2025
('HK-07-200125-001', N'Trần Thanh Tùng',           '014207890217', 2007),
('HK-01-200125-002', N'Vũ Thị Hồng Nhung',         '004201460818', 2001),
('HK-08-200125-003', N'Tống Văn Khánh',            '049208230716', 2008),
('HK-92-200125-004', N'Cao Thị Mai',               '026192580101', 1992),
('HK-03-200125-005', N'Trần Văn Dũng',             '040203340506', 2003),

-- Ngày 19/01/2025
('HK-05-190125-001', N'Ngô Đức Anh',               '035205690903', 2005),
('HK-93-190125-002', N'Trịnh Minh Tài',            '061193240511', 1993),
('HK-06-190125-003', N'Nguyễn Thị Tuyết',          '021206920212', 2006),
('HK-10-190125-004', N'Trịnh Minh Hiếu',           '074210630824', 2010),

-- Ngày 18/01/2025
('HK-94-180125-001', N'Lê Phương Dung',            '063194320918', 1994),
('HK-04-180125-002', N'Phạm Thị Thanh',            '069204130205', 2004),
('HK-02-180125-003', N'Nguyễn Hữu Tài',            '033202640207', 2002),
('HK-96-180125-004', N'Lý Minh Tuấn',              '048196530727', 1996),
('HK-89-180125-005', N'Trần Văn Giáp',             '018189410318', 1989),

-- Ngày 17/01/2025
('HK-08-170125-001', N'Tống Thị Minh',             '049208340116', 2008),
('HK-91-170125-002', N'Đinh Quốc Trung',           '073191280728', 1991),
('HK-07-170125-003', N'Trần Văn Hải',              '014207580208', 2007),

-- Ngày 16/01/2025
('HK-06-160125-001', N'Nguyễn Thị Hồng',           '021206340904', 2006),
('HK-01-160125-002', N'Vũ Thanh Hà',               '004201230813', 2001),
('HK-95-160125-003', N'Lê Văn Bình',               '047195110220', 1995),

-- Ngày 15/01/2025
('HK-92-150125-001', N'Cao Hồng Phúc',             '026192650526', 1992),
('HK-93-150125-002', N'Trịnh Thanh Sơn',           '061193410430', 1993),
('HK-02-150125-003', N'Nguyễn Minh Vũ',            '033202950926', 2002),
('HK-05-150125-004', N'Ngô Thị Thu Trang',         '035205780330', 2005),
('HK-10-150125-005', N'Trịnh Bảo Anh',             '074210440106', 2010),

-- Ngày 14/01/2025
('HK-94-140125-001', N'Lê Thành Đạt',              '063194650220', 1994),
('HK-03-140125-002', N'Trần Văn Hùng',             '040203230813', 2003),
('HK-90-140125-003', N'Trần Văn Tài',              '025190480114', 1990),
('HK-96-140125-004', N'Lý Gia Khánh',              '048196770520', 1996),

-- Ngày 13/01/2025
('HK-07-130125-001', N'Trần Hồng Quang',           '014207810503', 2007),
('HK-08-130125-002', N'Tống Thị Diễm',             '049208120124', 2008),
('HK-91-130125-003', N'Đinh Minh Hòa',             '073191360119', 1991),

-- Ngày 12/01/2025
('HK-89-120125-001', N'Trần Minh Khôi',            '018189970825', 1989),
('HK-01-120125-002', N'Vũ Minh Thư',               '004201440815', 2001),
('HK-06-120125-003', N'Nguyễn Văn Quý',            '021206150713', 2006),

-- Ngày 11/01/2025
('HK-04-110125-001', N'Phạm Gia Bảo',              '069204980328', 2004),
('HK-95-110125-002', N'Lê Thị Ngọc Trinh',         '047195230704', 1995),
('HK-05-110125-003', N'Ngô Minh Nhật',             '035205220615', 2005);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 10/01/2025
('HK-90-100125-001', N'Trần Quốc Bảo',              '025190630528', 1990),
('HK-03-100125-002', N'Trần Thị Phương',            '040203140212', 2003),
('HK-96-100125-003', N'Lý Thị Lan',                 '048196540716', 1996),
('HK-07-100125-004', N'Trần Văn Nam',               '014207470304', 2007),

-- Ngày 09/01/2025
('HK-91-090125-001', N'Đinh Thị Mỹ Linh',           '073191890920', 1991),
('HK-04-090125-002', N'Phạm Văn Thành',             '069204760108', 2004),
('HK-94-090125-003', N'Lê Bảo Hân',                 '063194350313', 1994),
('HK-01-090125-004', N'Vũ Gia Bảo',                 '004201720924', 2001),

-- Ngày 08/01/2025
('HK-92-080125-001', N'Cao Văn Lâm',                '026192130802', 1992),
('HK-06-080125-002', N'Nguyễn Thị Bích',            '021206780327', 2006),
('HK-95-080125-003', N'Lê Minh Khang',              '047195880831', 1995),

-- Ngày 07/01/2025
('HK-89-070125-001', N'Trần Hồng Nhung',            '018189480214', 1989),
('HK-02-070125-002', N'Nguyễn Thành Trung',         '033202620706', 2002),
('HK-05-070125-003', N'Ngô Văn Hùng',               '035205440228', 2005),

-- Ngày 06/01/2025
('HK-93-060125-001', N'Trịnh Thị Huyền',            '061193610904', 1993),
('HK-08-060125-002', N'Tống Đức Thịnh',             '049208910430', 2008),
('HK-10-060125-003', N'Trịnh Minh Nhật',            '074210530718', 2010),
('HK-07-060125-004', N'Trần Quang Long',            '014207380622', 2007),

-- Ngày 05/01/2025
('HK-03-050125-001', N'Trần Minh Khoa',             '040203910723', 2003),
('HK-90-050125-002', N'Trần Anh Tú',                '025190550305', 1990),
('HK-06-050125-003', N'Nguyễn Thị Thanh',           '021206220208', 2006),
('HK-01-050125-004', N'Vũ Thị Thanh Bình',          '004201970522', 2001),

-- Ngày 04/01/2025
('HK-04-040125-001', N'Phạm Quốc Khánh',            '069204340409', 2004),
('HK-94-040125-002', N'Lê Thị Kiều My',             '063194710915', 1994),
('HK-95-040125-003', N'Lê Hoàng Vũ',                '047195250202', 1995),

-- Ngày 03/01/2025
('HK-89-030125-001', N'Trần Huy Hoàng',             '018189320810', 1989),
('HK-05-030125-002', N'Ngô Hồng Ân',                '035205930701', 2005),
('HK-08-030125-003', N'Tống Bảo An',                '049208420101', 2008),

-- Ngày 02/01/2025
('HK-96-020125-001', N'Lý Thị Kim Oanh',            '048196410217', 1996),
('HK-91-020125-002', N'Đinh Minh Phúc',             '073191330424', 1991),
('HK-10-020125-003', N'Trịnh Nhật Hào',             '074210660720', 2010),
('HK-06-020125-004', N'Nguyễn Đình Thắng',          '021206930212', 2006),

-- Ngày 01/01/2025
('HK-92-010125-001', N'Cao Thị Huyền',              '026192690512', 1992),
('HK-02-010125-002', N'Nguyễn Hoàng Anh',           '033202750627', 2002),
('HK-07-010125-003', N'Trần Như Quỳnh',             '014207260315', 2007),
('HK-03-010125-004', N'Trần Minh Nhật',             '040203550903', 2003),
('HK-04-010125-005', N'Phạm Thị Hà',                '069204850906', 2004);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 31/12/2024
('HK-95-311224-001', N'Lê Anh Tuấn',               '047195580630', 1995),
('HK-01-311224-002', N'Vũ Quỳnh Trang',            '004201240505', 2001),
('HK-06-311224-003', N'Nguyễn Văn Phong',          '021206950801', 2006),
('HK-08-311224-004', N'Tống Đức Minh',             '049208180216', 2008),

-- Ngày 30/12/2024
('HK-92-301224-001', N'Cao Thị Lệ Quyên',          '026192420430', 1992),
('HK-03-301224-002', N'Trần Minh Tú',              '040203280120', 2003),
('HK-07-301224-003', N'Trần Nhật Quang',           '014207110215', 2007),
('HK-04-301224-004', N'Phạm Trung Hiếu',           '069204660717', 2004),
('HK-10-301224-005', N'Trịnh Quốc Anh',            '074210340726', 2010),

-- Ngày 29/12/2024
('HK-94-291224-001', N'Lê Phương Uyên',            '063194290313', 1994),
('HK-89-291224-002', N'Trần Minh Khoa',            '018189910509', 1989),
('HK-02-291224-003', N'Nguyễn Thảo Vy',            '033202360121', 2002),

-- Ngày 28/12/2024
('HK-90-281224-001', N'Trần Văn Kiệt',             '025190260219', 1990),
('HK-96-281224-002', N'Lý Tấn Phát',               '048196840401', 1996),
('HK-06-281224-003', N'Nguyễn Thành Tín',          '021206620318', 2006),
('HK-01-281224-004', N'Vũ Huy Hoàng',              '004201380610', 2001),

-- Ngày 27/12/2024
('HK-04-271224-001', N'Phạm Bảo Long',             '069204710127', 2004),
('HK-93-271224-002', N'Trịnh Hồng Ân',             '061193910911', 1993),
('HK-08-271224-003', N'Tống Thị Ánh',              '049208620815', 2008),

-- Ngày 26/12/2024
('HK-91-261224-001', N'Đinh Quang Huy',            '073191610124', 1991),
('HK-03-261224-002', N'Trần Thảo Linh',            '040203740402', 2003),
('HK-07-261224-003', N'Trần Kim Ngân',             '014207120708', 2007),
('HK-05-261224-004', N'Ngô Minh Quân',             '035205160611', 2005),

-- Ngày 25/12/2024
('HK-92-251224-001', N'Cao Văn Hưng',              '026192360726', 1992),
('HK-10-251224-002', N'Trịnh Gia Hân',             '074210170927', 2010),
('HK-06-251224-003', N'Nguyễn Lan Hương',          '021206530819', 2006),

-- Ngày 24/12/2024
('HK-89-241224-001', N'Trần Nhật Hào',             '018189730820', 1989),
('HK-94-241224-002', N'Lê Minh Phương',            '063194640905', 1994),
('HK-04-241224-003', N'Phạm Văn Hải',              '069204130307', 2004),
('HK-02-241224-004', N'Nguyễn Thị Mai',            '033202980614', 2002),

-- Ngày 23/12/2024
('HK-90-231224-001', N'Trần Tuấn Dũng',            '025190910413', 1990),
('HK-95-231224-002', N'Lê Thị Hương',              '047195410722', 1995),
('HK-08-231224-003', N'Tống Hồng Minh',            '049208750626', 2008),
('HK-07-231224-004', N'Trần Huyền Anh',            '014207880218', 2007),

-- Ngày 22/12/2024
('HK-01-221224-001', N'Vũ Bảo Trân',               '004201230306', 2001),
('HK-06-221224-002', N'Nguyễn Minh Tâm',           '021206360908', 2006),
('HK-93-221224-003', N'Trịnh Thị Thùy Dung',       '061193870721', 1993);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 21/12/2024
('HK-92-211224-001', N'Cao Thị Mai',               '026192740211', 1992),
('HK-03-211224-002', N'Trần Văn Tài',              '040203990418', 2003),
('HK-07-211224-003', N'Trần Ngọc Duy',             '014207110930', 2007),
('HK-04-211224-004', N'Phạm Huy Hoàng',            '069204530103', 2004),

-- Ngày 20/12/2024
('HK-94-201224-001', N'Lê Minh Khang',             '063194380206', 1994),
('HK-89-201224-002', N'Trần Nhật Lâm',             '018189460815', 1989),
('HK-02-201224-003', N'Nguyễn Thị Hạnh',           '033202210514', 2002),

-- Ngày 19/12/2024
('HK-90-191224-001', N'Trần Trung Hiếu',           '025190190120', 1990),
('HK-96-191224-002', N'Lý Gia Huy',                '048196250524', 1996),
('HK-06-191224-003', N'Nguyễn Quang Hưng',         '021206390909', 2006),
('HK-01-191224-004', N'Vũ Đình Phúc',              '004201780603', 2001),

-- Ngày 18/12/2024
('HK-04-181224-001', N'Phạm Thảo Vy',              '069204280701', 2004),
('HK-93-181224-002', N'Trịnh Thanh Bình',          '061193950924', 1993),
('HK-08-181224-003', N'Tống Hữu Tài',              '049208840804', 2008),

-- Ngày 17/12/2024
('HK-91-171224-001', N'Đinh Tấn Lộc',              '073191140117', 1991),
('HK-03-171224-002', N'Trần Thị Khánh',            '040203620428', 2003),
('HK-07-171224-003', N'Trần Minh Ánh',             '014207120309', 2007),
('HK-05-171224-004', N'Ngô Gia Khánh',             '035205640826', 2005),

-- Ngày 16/12/2024
('HK-92-161224-001', N'Cao Minh Dũng',             '026192120212', 1992),
('HK-10-161224-002', N'Trịnh Ngọc Bảo',            '074210330828', 2010),
('HK-06-161224-003', N'Nguyễn Kim Ngân',           '021206550511', 2006),

-- Ngày 15/12/2024
('HK-89-151224-001', N'Trần Chí Công',             '018189610406', 1989),
('HK-94-151224-002', N'Lê Văn Phú',                '063194810314', 1994),
('HK-04-151224-003', N'Phạm Quỳnh Hoa',            '069204790921', 2004),
('HK-02-151224-004', N'Nguyễn Minh Châu',          '033202240616', 2002),

-- Ngày 14/12/2024
('HK-90-141224-001', N'Trần Tấn Hưng',             '025190700319', 1990),
('HK-95-141224-002', N'Lê Thanh Hòa',              '047195260713', 1995),
('HK-08-141224-003', N'Tống Vũ Hân',               '049208670509', 2008),
('HK-07-141224-004', N'Trần Diệu Linh',            '014207830203', 2007),

-- Ngày 13/12/2024
('HK-01-131224-001', N'Vũ Ngọc Minh',              '004201510227', 2001),
('HK-06-131224-002', N'Nguyễn Tiến Lộc',           '021206370511', 2006),
('HK-93-131224-003', N'Trịnh Minh Hà',             '061193940325', 1993);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 12/12/2024
('HK-95-121224-001', N'Lê Thị Mỹ Dung',            '047195990921', 1995),
('HK-10-121224-002', N'Trịnh Quốc Đạt',            '074210740504', 2010),
('HK-03-121224-003', N'Trần Phúc Thịnh',           '040203600116', 2003),
('HK-07-121224-004', N'Trần Nhật Hạ',              '014207720227', 2007),

-- Ngày 11/12/2024
('HK-91-111224-001', N'Đinh Thanh Tâm',            '073191110618', 1991),
('HK-06-111224-002', N'Nguyễn Tùng Lâm',           '021206290708', 2006),
('HK-89-111224-003', N'Trần Hoàng Lâm',            '018189120209', 1989),
('HK-94-111224-004', N'Lê Ngọc Bích',              '063194380725', 1994),

-- Ngày 10/12/2024
('HK-02-101224-001', N'Nguyễn Minh Tâm',           '033202640311', 2002),
('HK-04-101224-002', N'Phạm Thanh Hương',          '069204380420', 2004),
('HK-08-101224-003', N'Tống Hải Đăng',             '049208910112', 2008),

-- Ngày 09/12/2024
('HK-90-091224-001', N'Trần Quốc Vinh',            '025190230914', 1990),
('HK-93-091224-002', N'Trịnh Gia Hân',             '061193420506', 1993),
('HK-01-091224-003', N'Vũ Như Quỳnh',              '004201710809', 2001),

-- Ngày 08/12/2024
('HK-92-081224-001', N'Cao Hồng Sơn',              '026192980412', 1992),
('HK-07-081224-002', N'Trần Bảo Ngọc',             '014207860705', 2007),
('HK-10-081224-003', N'Trịnh Đức Anh',             '074210590128', 2010),

-- Ngày 07/12/2024
('HK-06-071224-001', N'Nguyễn Phương Uyên',        '021206150519', 2006),
('HK-04-071224-002', N'Phạm Hữu Tài',              '069204780830', 2004),
('HK-94-071224-003', N'Lê Khánh Linh',             '063194340121', 1994),

-- Ngày 06/12/2024
('HK-03-061224-001', N'Trần Đức Trọng',            '040203910922', 2003),
('HK-08-061224-002', N'Tống Hồng Quân',            '049208750502', 2008),
('HK-89-061224-003', N'Trần Thị Ngân',             '018189650727', 1989),

-- Ngày 05/12/2024
('HK-95-051224-001', N'Lê Gia Hưng',               '047195730317', 1995),
('HK-02-051224-002', N'Nguyễn Hồng Sơn',           '033202320824', 2002),
('HK-91-051224-003', N'Đinh Hương Giang',          '073191940608', 1991),

-- Ngày 04/12/2024
('HK-01-041224-001', N'Vũ Văn Minh',               '004201270803', 2001),
('HK-93-041224-002', N'Trịnh Thị Thanh',           '061193410710', 1993),
('HK-05-041224-003', N'Ngô Huy Hoàng',             '035205850613', 2005),

-- Ngày 03/12/2024
('HK-90-031224-001', N'Trần Quốc Hưng',            '025190610924', 1990),
('HK-06-031224-002', N'Nguyễn Hoàng Long',         '021206570828', 2006),
('HK-07-031224-003', N'Trần Gia Hân',              '014207290620', 2007),

-- Ngày 02/12/2024
('HK-04-021224-001', N'Phạm Minh Hằng',            '069204430515', 2004),
('HK-08-021224-002', N'Tống Nhật Anh',             '049208610726', 2008),
('HK-10-021224-003', N'Trịnh Văn Hậu',             '074210840321', 2010);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 01/12/2024
('HK-91-011224-001', N'Đinh Văn Thái',             '073191370114', 1991),
('HK-02-011224-002', N'Nguyễn Hải Đăng',           '033202510719', 2002),
('HK-06-011224-003', N'Nguyễn Văn Minh',           '021206120508', 2006),
('HK-93-011224-004', N'Trịnh Thu Trang',           '061193890930', 1993),

-- Ngày 30/11/2024
('HK-04-301124-001', N'Phạm Huy Hoàng',            '069204540716', 2004),
('HK-08-301124-002', N'Tống Thị Hồng',             '049208710421', 2008),
('HK-89-301124-003', N'Trần Đức Mạnh',             '018189480305', 1989),
('HK-01-301124-004', N'Vũ Minh Tâm',               '004201850822', 2001),

-- Ngày 29/11/2024
('HK-90-291124-001', N'Trần Văn Cường',            '025190130102', 1990),
('HK-05-291124-002', N'Ngô Mai Hương',             '035205410730', 2005),
('HK-95-291124-003', N'Lê Ngọc Hà',                '047195970406', 1995),

-- Ngày 28/11/2024
('HK-10-281124-001', N'Trịnh Gia Minh',            '074210310211', 2010),
('HK-07-281124-002', N'Trần Hữu Đức',              '014207840722', 2007),
('HK-92-281124-003', N'Cao Hoài Nam',              '026192790515', 1992),

-- Ngày 27/11/2024
('HK-06-271124-001', N'Nguyễn Minh Hiếu',          '021206380619', 2006),
('HK-91-271124-002', N'Đinh Trọng Dũng',           '073191610304', 1991),
('HK-93-271124-003', N'Trịnh Văn Hùng',            '061193780227', 1993),
('HK-02-271124-004', N'Nguyễn Văn Đức',            '033202930720', 2002),

-- Ngày 26/11/2024
('HK-04-261124-001', N'Phạm Quang Huy',            '069204320804', 2004),
('HK-08-261124-002', N'Tống Khánh Vân',            '049208160925', 2008),
('HK-89-261124-003', N'Trần Hồng Loan',            '018189720131', 1989),

-- Ngày 25/11/2024
('HK-01-251124-001', N'Vũ Hữu Tài',                '004201560123', 2001),
('HK-90-251124-002', N'Trần Thị Mai',              '025190850430', 1990),
('HK-05-251124-003', N'Ngô Trọng Phúc',            '035205190913', 2005),
('HK-10-251124-004', N'Trịnh Bảo Hân',             '074210140104', 2010),

-- Ngày 24/11/2024
('HK-07-241124-001', N'Trần Đức Anh',              '014207630619', 2007),
('HK-92-241124-002', N'Cao Minh Tâm',              '026192560811', 1992),
('HK-06-241124-003', N'Nguyễn Gia Hưng',           '021206440503', 2006),

-- Ngày 23/11/2024
('HK-91-231124-001', N'Đinh Quốc Bảo',             '073191320912', 1991),
('HK-04-231124-002', N'Phạm Hoàng Yến',            '069204390614', 2004),
('HK-02-231124-003', N'Nguyễn Mỹ Linh',            '033202360301', 2002),

-- Ngày 22/11/2024
('HK-95-221124-001', N'Lê Hồng Sơn',               '047195640419', 1995),
('HK-89-221124-002', N'Trần Nhật Quang',           '018189170622', 1989),
('HK-93-221124-003', N'Trịnh Đan Vy',              '061193300105', 1993),
('HK-01-221124-004', N'Vũ Gia Linh',               '004201270912', 2001);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 21/11/2024
('HK-06-211124-001', N'Nguyễn Văn Khánh',          '021206870820', 2006),
('HK-91-211124-002', N'Đinh Mai Hương',            '073191690122', 1991),
('HK-08-211124-003', N'Tống Văn Thịnh',            '049208930326', 2008),
('HK-04-211124-004', N'Phạm Thùy Trang',           '069204450925', 2004),

-- Ngày 20/11/2024
('HK-02-201124-001', N'Nguyễn Hoàng Long',         '033202540617', 2002),
('HK-93-201124-002', N'Trịnh Thị Kim Dung',        '061193780213', 1993),
('HK-05-201124-003', N'Ngô Đức Trí',               '035205120101', 2005),

-- Ngày 19/11/2024
('HK-10-191124-001', N'Trịnh Hữu Thắng',           '074210560927', 2010),
('HK-89-191124-002', N'Trần Ngọc Anh',             '018189850710', 1989),
('HK-07-191124-003', N'Trần Hoàng Tuấn',           '014207210115', 2007),

-- Ngày 18/11/2024
('HK-90-181124-001', N'Trần Văn Quý',              '025190130906', 1990),
('HK-06-181124-002', N'Nguyễn Đức Huy',            '021206300514', 2006),
('HK-95-181124-003', N'Lê Văn Khôi',               '047195490120', 1995),
('HK-92-181124-004', N'Cao Hồng Vân',              '026192710218', 1992),

-- Ngày 17/11/2024
('HK-01-171124-001', N'Vũ Minh Thư',               '004201440421', 2001),
('HK-04-171124-002', N'Phạm Quốc Cường',           '069204660731', 2004),
('HK-91-171124-003', N'Đinh Thị Bích',             '073191800828', 1991),

-- Ngày 16/11/2024
('HK-02-161124-001', N'Nguyễn Anh Tú',             '033202960720', 2002),
('HK-08-161124-002', N'Tống Văn Linh',             '049208880510', 2008),
('HK-07-161124-003', N'Trần Hữu Tình',             '014207790622', 2007),

-- Ngày 15/11/2024
('HK-10-151124-001', N'Trịnh Minh Anh',            '074210690303', 2010),
('HK-05-151124-002', N'Ngô Thùy Linh',             '035205750405', 2005),
('HK-06-151124-003', N'Nguyễn Văn Hòa',            '021206910728', 2006),
('HK-93-151124-004', N'Trịnh Hữu Phước',           '061193820924', 1993),

-- Ngày 14/11/2024
('HK-92-141124-001', N'Cao Đức Long',              '026192580216', 1992),
('HK-04-141124-002', N'Phạm Minh Duy',             '069204770817', 2004),
('HK-01-141124-003', N'Vũ Thị Kim Ngân',           '004201620810', 2001),

-- Ngày 13/11/2024
('HK-89-131124-001', N'Trần Quốc Hưng',            '018189340111', 1989),
('HK-90-131124-002', N'Trần Thanh Lam',            '025190240910', 1990),
('HK-95-131124-003', N'Lê Nhật Tân',               '047195320222', 1995),
('HK-91-131124-004', N'Đinh Hồng Phúc',            '073191530920', 1991),

-- Ngày 12/11/2024
('HK-07-121124-001', N'Trần Hải Băng',             '014207460725', 2007),
('HK-02-121124-002', N'Nguyễn Hữu Hậu',            '033202850204', 2002),
('HK-06-121124-003', N'Nguyễn Gia Bảo',            '021206950619', 2006),

-- Ngày 11/11/2024
('HK-08-111124-001', N'Tống Phương Uyên',          '049208390402', 2008),
('HK-10-111124-002', N'Trịnh Thùy Dương',          '074210730812', 2010),
('HK-05-111124-003', N'Ngô Đức Toàn',              '035205160622', 2005);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 10/11/2024
('HK-04-101124-001', N'Phạm Ngọc Linh',             '069204780218', 2004),
('HK-90-101124-002', N'Trần Hữu Tài',              '025190610625', 1990),
('HK-92-101124-003', N'Cao Minh Huy',              '026192910109', 1992),
('HK-06-101124-004', N'Nguyễn Thị Minh',           '021206540731', 2006),

-- Ngày 09/11/2024
('HK-01-091124-001', N'Vũ Quang Hưng',             '004201890110', 2001),
('HK-95-091124-002', N'Lê Hoàng Việt',             '047195670926', 1995),
('HK-08-091124-003', N'Tống Văn Hòa',              '049208410321', 2008),

-- Ngày 08/11/2024
('HK-89-081124-001', N'Trần Nhật Dương',           '018189750912', 1989),
('HK-07-081124-002', N'Trần Hữu Nghĩa',            '014207180815', 2007),
('HK-93-081124-003', N'Trịnh Hải Yến',             '061193610208', 1993),
('HK-10-081124-004', N'Trịnh Văn Đức',             '074210240319', 2010),

-- Ngày 07/11/2024
('HK-02-071124-001', N'Nguyễn Văn Cảnh',           '033202160212', 2002),
('HK-91-071124-002', N'Đinh Thị Thu',              '073191480120', 1991),
('HK-05-071124-003', N'Ngô Văn Trí',               '035205360820', 2005),

-- Ngày 06/11/2024
('HK-04-061124-001', N'Phạm Thị Huyền',            '069204520101', 2004),
('HK-06-061124-002', N'Nguyễn Thị Duyên',          '021206810514', 2006),
('HK-92-061124-003', N'Cao Đức Hạnh',              '026192670812', 1992),

-- Ngày 05/11/2024
('HK-08-051124-001', N'Tống Thị Minh',             '049208130523', 2008),
('HK-90-051124-002', N'Trần Anh Đức',              '025190410929', 1990),
('HK-93-051124-003', N'Trịnh Hồng Thắm',           '061193120722', 1993),
('HK-01-051124-004', N'Vũ Mạnh Thắng',             '004201250409', 2001),

-- Ngày 04/11/2024
('HK-07-041124-001', N'Trần Đức Huy',              '014207030415', 2007),
('HK-10-041124-002', N'Trịnh Gia Hân',             '074210160920', 2010),
('HK-89-041124-003', N'Trần Quỳnh Mai',            '018189640111', 1989),

-- Ngày 03/11/2024
('HK-05-031124-001', N'Ngô Thanh Sơn',             '035205820215', 2005),
('HK-91-031124-002', N'Đinh Ngọc Quang',           '073191150910', 1991),
('HK-02-031124-003', N'Nguyễn Hồng Lam',           '033202710330', 2002),
('HK-95-031124-004', N'Lê Văn Phong',              '047195340920', 1995),

-- Ngày 02/11/2024
('HK-06-021124-001', N'Nguyễn Văn Lợi',            '021206600812', 2006),
('HK-04-021124-002', N'Phạm Văn Hậu',              '069204920604', 2004),
('HK-92-021124-003', N'Cao Gia Khánh',             '026192380303', 1992),

-- Ngày 01/11/2024
('HK-93-011124-001', N'Trịnh Quang Nhật',          '061193590227', 1993),
('HK-08-011124-002', N'Tống Minh Châu',            '049208550912', 2008),
('HK-01-011124-003', N'Vũ Hữu Dũng',               '004201980105', 2001),
('HK-02-011124-004', N'Nguyễn Văn Tiến',           '033202880926', 2002);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 31/10/2024
('HK-07-311024-001', N'Trần Văn Nam',              '014207620301', 2007),
('HK-04-311024-002', N'Phạm Hồng Sơn',             '069204750927', 2004),
('HK-89-311024-003', N'Trần Văn Hùng',             '018189430620', 1989),
('HK-93-311024-004', N'Trịnh Gia Minh',            '061193260823', 1993),

-- Ngày 30/10/2024
('HK-06-301024-001', N'Nguyễn Nhật Hào',           '021206210408', 2006),
('HK-02-301024-002', N'Nguyễn Đức Cường',          '033202950902', 2002),
('HK-01-301024-003', N'Vũ Minh Phú',               '004201610926', 2001),

-- Ngày 29/10/2024
('HK-05-291024-001', N'Ngô Thị Hương',             '035205780717', 2005),
('HK-10-291024-002', N'Trịnh Văn Bình',            '074210810408', 2010),
('HK-95-291024-003', N'Lê Tuấn Kiệt',              '047195590106', 1995),
('HK-08-291024-004', N'Tống Thị Hạnh',             '049208660214', 2008),

-- Ngày 28/10/2024
('HK-90-281024-001', N'Trần Khánh Linh',           '025190710804', 1990),
('HK-04-281024-002', N'Phạm Văn Thịnh',            '069204620601', 2004),
('HK-06-281024-003', N'Nguyễn Quốc Anh',           '021206470126', 2006),

-- Ngày 27/10/2024
('HK-07-271024-001', N'Trần Bảo Trân',             '014207980716', 2007),
('HK-93-271024-002', N'Trịnh Văn Hòa',             '061193960213', 1993),
('HK-92-271024-003', N'Cao Thị Yến',               '026192440622', 1992),

-- Ngày 26/10/2024
('HK-91-261024-001', N'Đinh Quang Duy',            '073191240128', 1991),
('HK-02-261024-002', N'Nguyễn Thị Hà',             '033202210421', 2002),
('HK-89-261024-003', N'Trần Thị Bích',             '018189780314', 1989),

-- Ngày 25/10/2024
('HK-08-251024-001', N'Tống Văn Lợi',              '049208990721', 2008),
('HK-93-251024-002', N'Trịnh Thị Hòa',             '061193110525', 1993),
('HK-04-251024-003', N'Phạm Ngọc Toàn',            '069204480918', 2004),

-- Ngày 24/10/2024
('HK-05-241024-001', N'Ngô Thị Hòa',               '035205270927', 2005),
('HK-90-241024-002', N'Trần Hữu Dũng',             '025190970324', 1990),
('HK-06-241024-003', N'Nguyễn Minh Nhật',          '021206100420', 2006),

-- Ngày 23/10/2024
('HK-01-231024-001', N'Vũ Văn Phúc',               '004201540201', 2001),
('HK-95-231024-002', N'Lê Văn Khôi',               '047195260307', 1995),
('HK-07-231024-003', N'Trần Quốc Cường',           '014207610206', 2007),

-- Ngày 22/10/2024
('HK-10-221024-001', N'Trịnh Như Quỳnh',           '074210190829', 2010),
('HK-89-221024-002', N'Trần Văn Tiến',             '018189110921', 1989),
('HK-91-221024-003', N'Đinh Văn Long',             '073191680208', 1991),
('HK-02-221024-004', N'Nguyễn Trung Kiên',         '033202350104', 2002),

-- Ngày 21/10/2024
('HK-93-211024-001', N'Trịnh Thị Hạnh',            '061193940625', 1993),
('HK-06-211024-002', N'Nguyễn Thị Bích',           '021206850912', 2006),
('HK-04-211024-003', N'Phạm Văn Minh',             '069204190819', 2004),
('HK-07-211024-004', N'Trần Văn Sơn',              '014207420528', 2007);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 20/10/2024
('HK-02-201024-001', N'Nguyễn Thị Thanh',          '033202510317', 2002),
('HK-08-201024-002', N'Tống Văn Hải',              '049208630904', 2008),
('HK-91-201024-003', N'Đinh Thị Lan',              '073191410116', 1991),
('HK-06-201024-004', N'Nguyễn Hoàng Anh',          '021206640215', 2006),

-- Ngày 19/10/2024
('HK-10-191024-001', N'Trịnh Minh Hào',            '074210250811', 2010),
('HK-07-191024-002', N'Trần Thị Mai',              '014207340622', 2007),
('HK-89-191024-003', N'Trần Văn Hữu',              '018189720219', 1989),

-- Ngày 18/10/2024
('HK-90-181024-001', N'Trần Thanh Hương',          '025190830727', 1990),
('HK-05-181024-002', N'Ngô Văn Khôi',              '035205160601', 2005),
('HK-92-181024-003', N'Cao Thị Bích',              '026192450213', 1992),

-- Ngày 17/10/2024
('HK-04-171024-001', N'Phạm Văn Tài',              '069204470528', 2004),
('HK-93-171024-002', N'Trịnh Minh Tuấn',           '061193980820', 1993),
('HK-01-171024-003', N'Vũ Văn Toàn',               '004201560707', 2001),

-- Ngày 16/10/2024
('HK-02-161024-001', N'Nguyễn Văn Đức',            '033202300902', 2002),
('HK-91-161024-002', N'Đinh Thị Hòa',              '073191150416', 1991),
('HK-08-161024-003', N'Tống Thị Hương',            '049208710215', 2008),

-- Ngày 15/10/2024
('HK-07-151024-001', N'Trần Thị Hương',            '014207620218', 2007),
('HK-95-151024-002', N'Lê Văn Phát',               '047195720921', 1995),
('HK-90-151024-003', N'Trần Thanh Tâm',            '025190980311', 1990),

-- Ngày 14/10/2024
('HK-04-141024-001', N'Phạm Minh Huy',             '069204690707', 2004),
('HK-06-141024-002', N'Nguyễn Văn Thành',          '021206380525', 2006),
('HK-10-141024-003', N'Trịnh Văn Dũng',            '074210610129', 2010),

-- Ngày 13/10/2024
('HK-93-131024-001', N'Trịnh Văn Hà',              '061193960915', 1993),
('HK-05-131024-002', N'Ngô Quốc Khánh',            '035205390221', 2005),
('HK-02-131024-003', N'Nguyễn Thị Quỳnh',          '033202460603', 2002),

-- Ngày 12/10/2024
('HK-01-121024-001', N'Vũ Thị Lan',                '004201780309', 2001),
('HK-89-121024-002', N'Trần Quốc Vinh',            '018189440510', 1989),
('HK-92-121024-003', N'Cao Văn Minh',              '026192620823', 1992),

-- Ngày 11/10/2024
('HK-91-111024-001', N'Đinh Thị Thu',              '073191780112', 1991),
('HK-07-111024-002', N'Trần Minh Tuấn',            '014207330915', 2007),
('HK-04-111024-003', N'Phạm Văn Duy',              '069204840928', 2004),
('HK-08-111024-004', N'Tống Văn Tùng',             '049208920718', 2008),

-- Ngày 10/10/2024
('HK-90-101024-001', N'Trần Văn Minh',             '025190230601', 1990),
('HK-95-101024-002', N'Lê Minh Đức',               '047195880415', 1995),
('HK-06-101024-003', N'Nguyễn Đức Tài',            '021206720310', 2006),
('HK-10-101024-004', N'Trịnh Khánh Ngọc',          '074210540524', 2010);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 09/10/2024
('HK-03-091024-001', N'Lê Thị Ánh',                '012203120604', 2003),
('HK-06-091024-002', N'Nguyễn Văn Sơn',           '021206750815', 2006),
('HK-89-091024-003', N'Trần Văn Lâm',             '018189430307', 1989),

-- Ngày 08/10/2024
('HK-07-081024-001', N'Trần Thị Huyền',           '014207820119', 2007),
('HK-91-081024-002', N'Đinh Văn Khoa',            '073191690510', 1991),
('HK-92-081024-003', N'Cao Văn Đức',              '026192850413', 1992),
('HK-08-081024-004', N'Tống Văn Hải',             '049208660920', 2008),

-- Ngày 07/10/2024
('HK-04-071024-001', N'Phạm Thanh Bình',          '069204170528', 2004),
('HK-01-071024-002', N'Vũ Thị Hằng',              '004201960721', 2001),
('HK-95-071024-003', N'Lê Văn Đạt',               '047195430115', 1995),

-- Ngày 06/10/2024
('HK-10-061024-001', N'Trịnh Quang Huy',          '074210220416', 2010),
('HK-06-061024-002', N'Nguyễn Văn Thành',         '021206180307', 2006),
('HK-02-061024-003', N'Nguyễn Thị Mai',           '033202230224', 2002),

-- Ngày 05/10/2024
('HK-90-051024-001', N'Trần Minh Hòa',            '025190950829', 1990),
('HK-07-051024-002', N'Trần Văn Dương',           '014207490715', 2007),
('HK-89-051024-003', N'Trần Thị Nhàn',            '018189680810', 1989),
('HK-91-051024-004', N'Đinh Văn Hùng',            '073191470203', 1991),

-- Ngày 04/10/2024
('HK-08-041024-001', N'Tống Thanh Lan',           '049208240914', 2008),
('HK-04-041024-002', N'Phạm Văn Tuấn',            '069204730611', 2004),

-- Ngày 03/10/2024
('HK-06-031024-001', N'Nguyễn Văn Khánh',         '021206470113', 2006),
('HK-05-031024-002', N'Ngô Thị Lan',              '035205540920', 2005),
('HK-10-031024-003', N'Trịnh Minh Phong',         '074210710727', 2010),

-- Ngày 02/10/2024
('HK-92-021024-001', N'Cao Thị Mai',              '026192220412', 1992),
('HK-02-021024-002', N'Nguyễn Thị Quyên',         '033202860102', 2002),
('HK-01-021024-003', N'Vũ Minh Tâm',              '004201790619', 2001),

-- Ngày 01/10/2024
('HK-95-011024-001', N'Lê Văn Toàn',              '047195140206', 1995),
('HK-07-011024-002', N'Trần Văn Tùng',            '014207610329', 2007),
('HK-04-011024-003', N'Phạm Văn Duy',             '069204550223', 2004),
('HK-91-011024-004', N'Đinh Văn Lực',             '073191320308', 1991),

-- Ngày 30/09/2024
('HK-06-300924-001', N'Nguyễn Thị Trang',         '021206980625', 2006),
('HK-08-300924-002', N'Tống Văn Long',            '049208790505', 2008),
('HK-89-300924-003', N'Trần Văn Cường',           '018189120104', 1989),

-- Ngày 29/09/2024
('HK-10-290924-001', N'Trịnh Văn Khải',           '074210120901', 2010),
('HK-90-290924-002', N'Trần Thị Hiền',            '025190780226', 1990),
('HK-02-290924-003', N'Nguyễn Văn Phúc',          '033202360418', 2002),
('HK-04-290924-004', N'Phạm Thị Minh',            '069204260530', 2004);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 28/09/2024
('HK-91-280924-001', N'Đinh Thị Tuyết',           '073191240106', 1991),
('HK-06-280924-002', N'Nguyễn Văn Lưu',           '021206330311', 2006),
('HK-07-280924-003', N'Trần Văn An',              '014207630728', 2007),

-- Ngày 27/09/2024
('HK-08-270924-001', N'Tống Thị Kim',             '049208720314', 2008),
('HK-92-270924-002', N'Cao Văn Hoàng',            '026192690118', 1992),
('HK-01-270924-003', N'Vũ Văn Đức',               '004201230204', 2001),
('HK-04-270924-004', N'Phạm Văn Bảo',             '069204750928', 2004),

-- Ngày 26/09/2024
('HK-90-260924-001', N'Trần Quốc Thịnh',          '025190820805', 1990),
('HK-02-260924-002', N'Nguyễn Thị Thanh',         '033202670905', 2002),
('HK-89-260924-003', N'Trần Văn Hiệp',            '018189910111', 1989),
('HK-06-260924-004', N'Nguyễn Văn Tài',           '021206150217', 2006),

-- Ngày 25/09/2024
('HK-07-250924-001', N'Trần Thị Linh',            '014207410924', 2007),
('HK-93-250924-002', N'Trịnh Văn Hưng',           '061193060327', 1993),
('HK-95-250924-003', N'Lê Thị Hòa',               '047195380611', 1995),

-- Ngày 24/09/2024
('HK-10-240924-001', N'Trịnh Quang Hưng',         '074210940117', 2010),
('HK-06-240924-002', N'Nguyễn Văn Toản',          '021206550503', 2006),
('HK-04-240924-003', N'Phạm Minh Trí',            '069204280221', 2004),
('HK-08-240924-004', N'Tống Minh Nhật',           '049208860829', 2008),

-- Ngày 23/09/2024
('HK-91-230924-001', N'Đinh Văn Lâm',             '073191960106', 1991),
('HK-02-230924-002', N'Nguyễn Văn Thành',         '033202470215', 2002),
('HK-07-230924-003', N'Trần Thị Mỹ Linh',         '014207330824', 2007),

-- Ngày 22/09/2024
('HK-92-220924-001', N'Cao Thị Hạnh',             '026192730901', 1992),
('HK-05-220924-002', N'Ngô Văn Khải',             '035205640916', 2005),
('HK-10-220924-003', N'Trịnh Thị Lan',            '074210650726', 2010),

-- Ngày 21/09/2024
('HK-06-210924-001', N'Nguyễn Thanh Hương',       '021206280313', 2006),
('HK-01-210924-002', N'Vũ Văn Đạt',               '004201790404', 2001),
('HK-08-210924-003', N'Tống Thị Lệ',              '049208590218', 2008),

-- Ngày 20/09/2024
('HK-90-200924-001', N'Trần Văn Nghĩa',           '025190640722', 1990),
('HK-07-200924-002', N'Trần Thanh Hải',           '014207520604', 2007),
('HK-04-200924-003', N'Phạm Thị Cúc',             '069204390114', 2004),
('HK-91-200924-004', N'Đinh Văn Trung',           '073191850420', 1991),

-- Ngày 19/09/2024
('HK-93-190924-001', N'Trịnh Văn Nam',            '061193150201', 1993),
('HK-06-190924-002', N'Nguyễn Văn Hòa',           '021206310529', 2006),
('HK-02-190924-003', N'Nguyễn Thị Hương',         '033202490728', 2002),

-- Ngày 18/09/2024
('HK-95-180924-001', N'Lê Văn Khánh',             '047195140210', 1995),
('HK-01-180924-002', N'Vũ Thị Tuyết',             '004201610303', 2001),
('HK-08-180924-003', N'Tống Văn Cường',           '049208280219', 2008),
('HK-89-180924-004', N'Trần Quốc Bảo',            '018189750624', 1989);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 17/09/2024
('HK-06-170924-001', N'Nguyễn Văn Đô',            '021206330116', 2006),
('HK-91-170924-002', N'Đinh Thị Xuân',            '073191580904', 1991),
('HK-07-170924-003', N'Trần Văn Hùng',            '014207110629', 2007),
('HK-89-170924-004', N'Trần Quốc Cường',          '018189310125', 1989),

-- Ngày 16/09/2024
('HK-08-160924-001', N'Tống Minh Đạt',            '049208170514', 2008),
('HK-02-160924-002', N'Nguyễn Thị Phương',        '033202280908', 2002),
('HK-93-160924-003', N'Trịnh Văn Tú',             '061193660311', 1993),

-- Ngày 15/09/2024
('HK-90-150924-001', N'Trần Văn Hòa',             '025190490703', 1990),
('HK-10-150924-002', N'Trịnh Thị Lan Anh',        '074210620912', 2010),
('HK-04-150924-003', N'Phạm Thị Thanh',           '069204890711', 2004),

-- Ngày 14/09/2024
('HK-95-140924-001', N'Lê Thị Hoa',               '047195260215', 1995),
('HK-06-140924-002', N'Nguyễn Minh Hưng',         '021206330312', 2006),
('HK-01-140924-003', N'Vũ Thanh Tuyền',           '004201720301', 2001),

-- Ngày 13/09/2024
('HK-07-130924-001', N'Trần Văn Sơn',             '014207280416', 2007),
('HK-92-130924-002', N'Cao Văn Trí',              '026192380524', 1992),
('HK-89-130924-003', N'Trần Quốc Tính',           '018189930113', 1989),

-- Ngày 12/09/2024
('HK-08-120924-001', N'Tống Văn Hải',             '049208840706', 2008),
('HK-04-120924-002', N'Phạm Văn Hùng',            '069204550409', 2004),
('HK-02-120924-003', N'Nguyễn Thị Vân',           '033202760822', 2002),
('HK-06-120924-004', N'Nguyễn Minh Toàn',         '021206340921', 2006),

-- Ngày 11/09/2024
('HK-91-110924-001', N'Đinh Văn Hữu',             '073191720128', 1991),
('HK-93-110924-002', N'Trịnh Quang Tuấn',         '061193270718', 1993),
('HK-10-110924-003', N'Trịnh Thị Hương',          '074210310525', 2010),

-- Ngày 10/09/2024
('HK-07-100924-001', N'Trần Minh Tiến',           '014207560317', 2007),
('HK-01-100924-002', N'Vũ Văn Trường',            '004201840408', 2001),
('HK-95-100924-003', N'Lê Văn Cường',             '047195810820', 1995),

-- Ngày 09/09/2024
('HK-06-090924-001', N'Nguyễn Thị Thủy',          '021206230730', 2006),
('HK-08-090924-002', N'Tống Văn Minh',            '049208340318', 2008),
('HK-92-090924-003', N'Cao Văn Minh',             '026192980114', 1992),
('HK-02-090924-004', N'Nguyễn Thị Mai',           '033202490812', 2002),

-- Ngày 08/09/2024
('HK-04-080924-001', N'Phạm Văn Giang',           '069204770112', 2004),
('HK-89-080924-002', N'Trần Quốc Thắng',          '018189920522', 1989),
('HK-90-080924-003', N'Trần Thị Tươi',            '025190740702', 1990),

-- Ngày 07/09/2024
('HK-91-070924-001', N'Đinh Văn Ngọc',            '073191890317', 1991),
('HK-06-070924-002', N'Nguyễn Văn Lập',           '021206440626', 2006),
('HK-93-070924-003', N'Trịnh Văn Khoa',           '061193120409', 1993),
('HK-07-070924-004', N'Trần Thị Thanh',           '014207990209', 2007);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 06/09/2024
('HK-10-060924-001', N'Trịnh Văn Phúc',           '074210560925', 2010),
('HK-02-060924-002', N'Nguyễn Thị Lan',           '033202870913', 2002),
('HK-90-060924-003', N'Trần Văn Tâm',             '025190670420', 1990),
('HK-01-060924-004', N'Vũ Thị Kim',               '004201760505', 2001),

-- Ngày 05/09/2024
('HK-06-050924-001', N'Nguyễn Văn Đức',           '021206250814', 2006),
('HK-93-050924-002', N'Trịnh Quang Lâm',          '061193830703', 1993),
('HK-08-050924-003', N'Tống Thị Mai',             '049208610926', 2008),

-- Ngày 04/09/2024
('HK-07-040924-001', N'Trần Văn Nguyên',          '014207150112', 2007),
('HK-89-040924-002', N'Trần Quốc Tuấn',           '018189140101', 1989),
('HK-04-040924-003', N'Phạm Thị Dung',            '069204340303', 2004),

-- Ngày 03/09/2024
('HK-92-030924-001', N'Cao Thị Thảo',             '026192430712', 1992),
('HK-95-030924-002', N'Lê Văn Đạt',               '047195640909', 1995),
('HK-10-030924-003', N'Trịnh Văn Bình',           '074210880605', 2010),
('HK-91-030924-004', N'Đinh Văn Khôi',            '073191710321', 1991),

-- Ngày 02/09/2024
('HK-06-020924-001', N'Nguyễn Minh Châu',         '021206220304', 2006),
('HK-93-020924-002', N'Trịnh Thị Thảo',           '061193340411', 1993),
('HK-08-020924-003', N'Tống Văn Dũng',            '049208580312', 2008),

-- Ngày 01/09/2024
('HK-02-010924-001', N'Nguyễn Văn Thái',          '033202630913', 2002),
('HK-90-010924-002', N'Trần Thị Phúc',            '025190250601', 1990),
('HK-07-010924-003', N'Trần Quốc Việt',           '014207110304', 2007),

-- Ngày 31/08/2024
('HK-04-310824-001', N'Phạm Văn Cường',           '069204980228', 2004),
('HK-01-310824-002', N'Vũ Văn Hùng',              '004201540811', 2001),
('HK-91-310824-003', N'Đinh Văn Tài',             '073191620127', 1991),
('HK-92-310824-004', N'Cao Văn Đức',              '026192190718', 1992),

-- Ngày 30/08/2024
('HK-10-300824-001', N'Trịnh Văn Hậu',            '074210690520', 2010),
('HK-06-300824-002', N'Nguyễn Thị Ngọc',          '021206820304', 2006),
('HK-95-300824-003', N'Lê Thị Hằng',              '047195710105', 1995),

-- Ngày 29/08/2024
('HK-07-290824-001', N'Trần Văn Hưng',            '014207750213', 2007),
('HK-08-290824-002', N'Tống Minh Hòa',            '049208180621', 2008),
('HK-93-290824-003', N'Trịnh Văn Lợi',            '061193180211', 1993),

-- Ngày 28/08/2024
('HK-89-280824-001', N'Trần Quốc Bảo',            '018189860822', 1989),
('HK-04-280824-002', N'Phạm Văn Nghĩa',           '069204670215', 2004),
('HK-02-280824-003', N'Nguyễn Thị Phương',        '033202940919', 2002),

-- Ngày 27/08/2024
('HK-01-270824-001', N'Vũ Minh Đức',              '004201350630', 2001),
('HK-91-270824-002', N'Đinh Thị Thanh',           '073191570209', 1991),
('HK-90-270824-003', N'Trần Văn Trọng',           '025190540922', 1990),
('HK-06-270824-004', N'Nguyễn Văn Khánh',         '021206420815', 2006);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 26/08/2024
('HK-92-260824-001', N'Cao Thị Nhàn',             '026192450901', 1992),
('HK-95-260824-002', N'Lê Văn Hữu',               '047195710420', 1995),
('HK-10-260824-003', N'Trịnh Thị Hồng',           '074210550218', 2010),

-- Ngày 25/08/2024
('HK-06-250824-001', N'Nguyễn Văn Phong',         '021206640709', 2006),
('HK-93-250824-002', N'Trịnh Quang Khánh',        '061193170813', 1993),
('HK-08-250824-003', N'Tống Thị Xuân',            '049208360525', 2008),
('HK-89-250824-004', N'Trần Thị Hạnh',            '018189120816', 1989),

-- Ngày 24/08/2024
('HK-07-240824-001', N'Trần Minh Đức',            '014207810123', 2007),
('HK-04-240824-002', N'Phạm Thị Hoa',             '069204560912', 2004),
('HK-01-240824-003', N'Vũ Văn Duy',               '004201590607', 2001),

-- Ngày 23/08/2024
('HK-91-230824-001', N'Đinh Thị Ngọc',            '073191640306', 1991),
('HK-90-230824-002', N'Trần Văn Hoàng',           '025190330905', 1990),
('HK-02-230824-003', N'Nguyễn Thị Tuyết',         '033202820515', 2002),
('HK-06-230824-004', N'Nguyễn Văn Nhân',          '021206260520', 2006),

-- Ngày 22/08/2024
('HK-93-220824-001', N'Trịnh Văn Tuấn',           '061193890713', 1993),
('HK-08-220824-002', N'Tống Thị Mận',             '049208730914', 2008),
('HK-07-220824-003', N'Trần Văn Khải',            '014207990430', 2007),

-- Ngày 21/08/2024
('HK-04-210824-001', N'Phạm Văn Minh',            '069204490111', 2004),
('HK-92-210824-002', N'Cao Minh Trí',             '026192060825', 1992),
('HK-95-210824-003', N'Lê Văn Hào',               '047195850317', 1995),

-- Ngày 20/08/2024
('HK-10-200824-001', N'Trịnh Văn Dũng',           '074210690619', 2010),
('HK-01-200824-002', N'Vũ Thị Thanh',             '004201680326', 2001),
('HK-91-200824-003', N'Đinh Văn Định',            '073191410614', 1991),

-- Ngày 19/08/2024
('HK-06-190824-001', N'Nguyễn Minh Huy',          '021206530320', 2006),
('HK-89-190824-002', N'Trần Quốc Cường',          '018189940307', 1989),
('HK-08-190824-003', N'Tống Văn Khôi',            '049208480608', 2008),
('HK-93-190824-004', N'Trịnh Văn Linh',           '061193020312', 1993),

-- Ngày 18/08/2024
('HK-07-180824-001', N'Trần Văn Nhật',            '014207570404', 2007),
('HK-02-180824-002', N'Nguyễn Thị Bích',          '033202430805', 2002),
('HK-90-180824-003', N'Trần Thị Nga',             '025190220922', 1990),

-- Ngày 17/08/2024
('HK-04-170824-001', N'Phạm Văn Giang',           '069204160511', 2004),
('HK-95-170824-002', N'Lê Minh Khoa',             '047195730106', 1995),
('HK-10-170824-003', N'Trịnh Thị Mai',            '074210880115', 2010),

-- Ngày 16/08/2024
('HK-06-160824-001', N'Nguyễn Văn Lâm',           '021206250519', 2006),
('HK-92-160824-002', N'Cao Văn Thái',             '026192640421', 1992),
('HK-01-160824-003', N'Vũ Thị Mỹ',                '004201580204', 2001),
('HK-91-160824-004', N'Đinh Quốc Khánh',          '073191790915', 1991);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 15/08/2024
('HK-08-150824-001', N'Tống Văn Nam',             '049208640208', 2008),
('HK-07-150824-002', N'Trần Văn Hải',             '014207510926', 2007),
('HK-90-150824-003', N'Trần Minh Anh',            '025190810105', 1990),

-- Ngày 14/08/2024
('HK-04-140824-001', N'Phạm Quang Vinh',          '069204320405', 2004),
('HK-95-140824-002', N'Lê Thị Mai',               '047195120817', 1995),
('HK-10-140824-003', N'Trịnh Hữu Phát',           '074210710318', 2010),

-- Ngày 13/08/2024
('HK-06-130824-001', N'Nguyễn Thị Phương',        '021206870721', 2006),
('HK-92-130824-002', N'Cao Văn Tùng',             '026192060319', 1992),
('HK-01-130824-003', N'Vũ Văn Hoàng',             '004201250402', 2001),
('HK-91-130824-004', N'Đinh Văn Hậu',             '073191350630', 1991),

-- Ngày 12/08/2024
('HK-93-120824-001', N'Trịnh Thị Hà',             '061193410122', 1993),
('HK-08-120824-002', N'Tống Minh Khôi',           '049208930914', 2008),
('HK-07-120824-003', N'Trần Thị Thủy',            '014207680619', 2007),

-- Ngày 11/08/2024
('HK-04-110824-001', N'Phạm Thị Yến',             '069204280110', 2004),
('HK-95-110824-002', N'Lê Quốc Trí',              '047195420501', 1995),
('HK-10-110824-003', N'Trịnh Hồng Sơn',           '074210270304', 2010),

-- Ngày 10/08/2024
('HK-06-100824-001', N'Nguyễn Quang Hưng',        '021206390907', 2006),
('HK-89-100824-002', N'Trần Quốc Huy',            '018189640723', 1989),
('HK-08-100824-003', N'Tống Thị Huyền',           '049208570808', 2008),
('HK-93-100824-004', N'Trịnh Hữu Trọng',          '061193280914', 1993),

-- Ngày 09/08/2024
('HK-07-090824-001', N'Trần Đức Anh',             '014207120313', 2007),
('HK-02-090824-002', N'Nguyễn Văn Thiết',         '033202940521', 2002),
('HK-90-090824-003', N'Trần Minh Đức',            '025190310728', 1990),

-- Ngày 08/08/2024
('HK-04-080824-001', N'Phạm Thanh Hải',           '069204100405', 2004),
('HK-95-080824-002', N'Lê Minh Dũng',             '047195670706', 1995),
('HK-10-080824-003', N'Trịnh Thị Tươi',           '074210920113', 2010),

-- Ngày 07/08/2024
('HK-06-070824-001', N'Nguyễn Văn Cường',         '021206650520', 2006),
('HK-92-070824-002', N'Cao Văn Phúc',             '026192580106', 1992),
('HK-01-070824-003', N'Vũ Thị Trang',             '004201720811', 2001),

-- Ngày 06/08/2024
('HK-91-060824-001', N'Đinh Văn Nam',             '073191120119', 1991),
('HK-93-060824-002', N'Trịnh Thanh Tùng',         '061193990912', 1993),

-- Ngày 05/08/2024
('HK-07-050824-001', N'Trần Quốc Đạt',            '014207220105', 2007),
('HK-04-050824-002', N'Phạm Hoài An',             '069204890409', 2004),
('HK-10-050824-003', N'Trịnh Văn Kiên',           '074210170117', 2010),

-- Ngày 04/08/2024
('HK-06-040824-001', N'Nguyễn Hữu Tài',           '021206490522', 2006),
('HK-92-040824-002', N'Cao Thị Hường',            '026192330630', 1992),
('HK-95-040824-003', N'Lê Văn Thành',             '047195270909', 1995);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 03/08/2024
('HK-08-030824-001', N'Tống Hữu Tùng',            '049208570304', 2008),
('HK-07-030824-002', N'Trần Hồng Ngọc',           '014207420719', 2007),
('HK-90-030824-003', N'Trần Văn Hòa',             '025190130510', 1990),

-- Ngày 02/08/2024
('HK-04-020824-001', N'Phạm Thị Mai',             '069204740721', 2004),
('HK-95-020824-002', N'Lê Minh Tuấn',             '047195360202', 1995),
('HK-10-020824-003', N'Trịnh Minh Đức',           '074210820609', 2010),
('HK-06-020824-004', N'Nguyễn Quốc Duy',          '021206120813', 2006),

-- Ngày 01/08/2024
('HK-92-010824-001', N'Cao Ngọc Anh',             '026192250211', 1992),
('HK-01-010824-002', N'Vũ Hữu Lộc',               '004201450803', 2001),
('HK-91-010824-003', N'Đinh Minh Tâm',            '073191370518', 1991),

-- Ngày 31/07/2024
('HK-93-310724-001', N'Trịnh Hữu Thắng',          '061193850214', 1993),
('HK-08-310724-002', N'Tống Thanh Bình',          '049208350612', 2008),
('HK-07-310724-003', N'Trần Đức Tài',             '014207190718', 2007),

-- Ngày 30/07/2024
('HK-04-300724-001', N'Phạm Hồng Vân',            '069204260510', 2004),
('HK-95-300724-002', N'Lê Văn Sơn',               '047195710929', 1995),
('HK-10-300724-003', N'Trịnh Anh Minh',           '074210470102', 2010),

-- Ngày 29/07/2024
('HK-06-290724-001', N'Nguyễn Thị Hậu',           '021206230821', 2006),
('HK-89-290724-002', N'Trần Đức Cường',           '018189580918', 1989),
('HK-08-290724-003', N'Tống Quang Vinh',          '049208670515', 2008),

-- Ngày 28/07/2024
('HK-07-280724-001', N'Trần Quốc Phúc',           '014207710710', 2007),
('HK-02-280724-002', N'Nguyễn Thanh Hùng',        '033202130719', 2002),
('HK-90-280724-003', N'Trần Minh Hằng',           '025190980816', 1990),

-- Ngày 27/07/2024
('HK-04-270724-001', N'Phạm Đình Hưng',           '069204440606', 2004),
('HK-95-270724-002', N'Lê Bảo Khánh',             '047195220109', 1995),
('HK-10-270724-003', N'Trịnh Thị Quyên',          '074210610728', 2010),
('HK-06-270724-004', N'Nguyễn Quỳnh Mai',         '021206870416', 2006),

-- Ngày 26/07/2024
('HK-92-260724-001', N'Cao Hữu Phát',             '026192410914', 1992),
('HK-01-260724-002', N'Vũ Ngọc Hân',              '004201120109', 2001),
('HK-91-260724-003', N'Đinh Quốc Trung',          '073191910824', 1991),

-- Ngày 25/07/2024
('HK-93-250724-001', N'Trịnh Hoàng Hiệp',         '061193140317', 1993),
('HK-08-250724-002', N'Tống Thị Kim',             '049208310519', 2008),
('HK-07-250724-003', N'Trần Văn Quân',            '014207930208', 2007),

-- Ngày 24/07/2024
('HK-04-240724-001', N'Phạm Văn Hùng',            '069204600905', 2004),
('HK-95-240724-002', N'Lê Quốc Anh',              '047195830622', 1995),
('HK-10-240724-003', N'Trịnh Thị Huyền',          '074210290118', 2010);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 23/07/2024
('HK-06-230724-001', N'Nguyễn Hoàng Phúc',         '021206110702', 2006),
('HK-89-230724-002', N'Trần Hữu Lâm',              '018189470905', 1989),
('HK-08-230724-003', N'Tống Văn Tài',              '049208550214', 2008),

-- Ngày 22/07/2024
('HK-07-220724-001', N'Trần Thị Thảo',             '014207740709', 2007),
('HK-02-220724-002', N'Nguyễn Thành Đạt',          '033202690221', 2002),
('HK-90-220724-003', N'Trần Thị Huyền',            '025190310615', 1990),

-- Ngày 21/07/2024
('HK-04-210724-001', N'Phạm Quốc Việt',            '069204870830', 2004),
('HK-95-210724-002', N'Lê Văn Hưng',               '047195930305', 1995),
('HK-10-210724-003', N'Trịnh Quốc Đạt',            '074210290312', 2010),
('HK-06-210724-004', N'Nguyễn Thị Kiều',           '021206750115', 2006),

-- Ngày 20/07/2024
('HK-92-200724-001', N'Cao Văn Cường',             '026192570717', 1992),
('HK-01-200724-002', N'Vũ Quỳnh Như',              '004201350503', 2001),
('HK-91-200724-003', N'Đinh Văn Hiếu',             '073191240812', 1991),

-- Ngày 19/07/2024
('HK-93-190724-001', N'Trịnh Văn Minh',            '061193960304', 1993),
('HK-08-190724-002', N'Tống Thị Hoa',              '049208170921', 2008),
('HK-07-190724-003', N'Trần Văn Phúc',             '014207410408', 2007),

-- Ngày 18/07/2024
('HK-04-180724-001', N'Phạm Hoàng Long',           '069204340711', 2004),
('HK-95-180724-002', N'Lê Ngọc Châu',              '047195120118', 1995),
('HK-10-180724-003', N'Trịnh Hồng Gấm',            '074210850519', 2010),

-- Ngày 17/07/2024
('HK-06-170724-001', N'Nguyễn Minh Tú',            '021206310718', 2006),
('HK-89-170724-002', N'Trần Phương Anh',           '018189180319', 1989),
('HK-08-170724-003', N'Tống Hữu Lộc',              '049208650428', 2008),

-- Ngày 16/07/2024
('HK-07-160724-001', N'Trần Văn Hiếu',             '014207590116', 2007),
('HK-02-160724-002', N'Nguyễn Thị Nhàn',           '033202970214', 2002),
('HK-90-160724-003', N'Trần Thanh Hoa',            '025190610911', 1990),

-- Ngày 15/07/2024
('HK-04-150724-001', N'Phạm Minh Hạnh',            '069204960506', 2004),
('HK-95-150724-002', N'Lê Hữu Tài',                '047195340806', 1995),
('HK-10-150724-003', N'Trịnh Quang Vinh',          '074210510220', 2010),
('HK-06-150724-004', N'Nguyễn Thị Hương',          '021206240511', 2006),

-- Ngày 14/07/2024
('HK-92-140724-001', N'Cao Minh Dũng',             '026192110415', 1992),
('HK-01-140724-002', N'Vũ Thị Mỹ Duyên',           '004201050823', 2001),
('HK-91-140724-003', N'Đinh Hoàng Long',           '073191780224', 1991),

-- Ngày 13/07/2024
('HK-93-130724-001', N'Trịnh Đình Hòa',            '061193460723', 1993),
('HK-08-130724-002', N'Tống Nhật Tân',             '049208310622', 2008),
('HK-07-130724-003', N'Trần Thị Mỹ Duyên',         '014207920410', 2007);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 12/07/2024
('HK-04-120724-001', N'Phạm Minh Trí',             '069204540114', 2004),
('HK-95-120724-002', N'Lê Bảo Hân',                '047195250519', 1995),
('HK-10-120724-003', N'Trịnh Tuấn Dũng',           '074210980201', 2010),

-- Ngày 11/07/2024
('HK-06-110724-001', N'Nguyễn Thị Tuyết',          '021206330821', 2006),
('HK-89-110724-002', N'Trần Văn Toàn',             '018189460104', 1989),
('HK-08-110724-003', N'Tống Đức Mạnh',             '049208710914', 2008),

-- Ngày 10/07/2024
('HK-07-100724-001', N'Trần Nhật Tân',             '014207590701', 2007),
('HK-02-100724-002', N'Nguyễn Phương Trang',       '033202850917', 2002),
('HK-90-100724-003', N'Trần Quốc Hưng',            '025190690224', 1990),

-- Ngày 09/07/2024
('HK-04-090724-001', N'Phạm Văn Hào',              '069204230509', 2004),
('HK-95-090724-002', N'Lê Thị Hải',                '047195640725', 1995),
('HK-10-090724-003', N'Trịnh Minh Anh',            '074210720503', 2010),
('HK-06-090724-004', N'Nguyễn Văn Phúc',           '021206480413', 2006),

-- Ngày 08/07/2024
('HK-92-080724-001', N'Cao Thị Thanh',             '026192850307', 1992),
('HK-01-080724-002', N'Vũ Minh Hương',             '004201060808', 2001),
('HK-91-080724-003', N'Đinh Thanh Sơn',            '073191380920', 1991),

-- Ngày 07/07/2024
('HK-93-070724-001', N'Trịnh Đỗ Hoàng',            '061193610828', 1993),
('HK-08-070724-002', N'Tống Văn Trung',            '049208110415', 2008),
('HK-07-070724-003', N'Trần Văn Đức',              '014207640923', 2007),

-- Ngày 06/07/2024
('HK-04-060724-001', N'Phạm Hồng Phước',           '069204010129', 2004),
('HK-95-060724-002', N'Lê Mai Phương',             '047195990212', 1995),
('HK-10-060724-003', N'Trịnh Trọng Nhân',          '074210870926', 2010),

-- Ngày 05/07/2024
('HK-06-050724-001', N'Nguyễn Hữu Phước',          '021206620504', 2006),
('HK-89-050724-002', N'Trần Hồng Đăng',            '018189590118', 1989),
('HK-08-050724-003', N'Tống Bảo Ngọc',             '049208210220', 2008),

-- Ngày 04/07/2024
('HK-07-040724-001', N'Trần Thị Mỹ Linh',          '014207170320', 2007),
('HK-02-040724-002', N'Nguyễn Khắc Cường',         '033202210927', 2002),
('HK-90-040724-003', N'Trần Tấn Lộc',              '025190870304', 1990),

-- Ngày 03/07/2024
('HK-04-030724-001', N'Phạm Quốc Hưng',            '069204430123', 2004),
('HK-95-030724-002', N'Lê Kim Ngân',               '047195890821', 1995),
('HK-10-030724-003', N'Trịnh Tuấn Hưng',           '074210530312', 2010),
('HK-06-030724-004', N'Nguyễn Quốc Khánh',         '021206920102', 2006),

-- Ngày 02/07/2024
('HK-92-020724-001', N'Cao Đình Thắng',            '026192690723', 1992),
('HK-01-020724-002', N'Vũ Ngọc Diễm',              '004201230611', 2001),
('HK-91-020724-003', N'Đinh Huy Hoàng',            '073191610403', 1991);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 01/07/2024
('HK-93-010724-001', N'Trịnh Quốc Việt',           '061193140215', 1993),
('HK-08-010724-002', N'Tống Thị Mai',              '049208250814', 2008),
('HK-07-010724-003', N'Trần Hoàng Huy',            '014207980317', 2007),

-- Ngày 30/06/2024
('HK-04-300624-001', N'Phạm Trúc Anh',             '069204120728', 2004),
('HK-95-300624-002', N'Lê Đức Kiên',               '047195470208', 1995),
('HK-10-300624-003', N'Trịnh Bảo Long',            '074210930113', 2010),

-- Ngày 29/06/2024
('HK-06-290624-001', N'Nguyễn Hoàng Long',         '021206150901', 2006),
('HK-89-290624-002', N'Trần Quang Vinh',           '018189210211', 1989),
('HK-08-290624-003', N'Tống Ngọc Hân',             '049208360719', 2008),

-- Ngày 28/06/2024
('HK-07-280624-001', N'Trần Đức Thắng',            '014207470126', 2007),
('HK-02-280624-002', N'Nguyễn Nhật Linh',          '033202540810', 2002),
('HK-90-280624-003', N'Trần Phạm Ngọc Huyền',      '025190380610', 1990),

-- Ngày 27/06/2024
('HK-04-270624-001', N'Phạm Đức Trí',              '069204220417', 2004),
('HK-95-270624-002', N'Lê Thị Mỹ Dung',            '047195310523', 1995),
('HK-10-270624-003', N'Trịnh Nhật Minh',           '074210460301', 2010),

-- Ngày 26/06/2024
('HK-06-260624-001', N'Nguyễn Tuấn Kiệt',          '021206430619', 2006),
('HK-89-260624-002', N'Trần Văn Duy',              '018189550101', 1989),
('HK-08-260624-003', N'Tống Kim Phụng',            '049208290902', 2008),
('HK-07-260624-004', N'Trần Thị Lan Hương',        '014207890721', 2007),

-- Ngày 25/06/2024
('HK-92-250624-001', N'Cao Nhật Hạ',               '026192840415', 1992),
('HK-01-250624-002', N'Vũ Văn Lâm',                '004201250313', 2001),
('HK-91-250624-003', N'Đinh Thái Sơn',             '073191430111', 1991),

-- Ngày 24/06/2024
('HK-93-240624-001', N'Trịnh Anh Quân',            '061193390622', 1993),
('HK-08-240624-002', N'Tống Văn Hoàng',            '049208700929', 2008),
('HK-07-240624-003', N'Trần Huy Bảo',              '014207630408', 2007),

-- Ngày 23/06/2024
('HK-04-230624-001', N'Phạm Minh Anh',             '069204760610', 2004),
('HK-95-230624-002', N'Lê Đăng Dương',             '047195510622', 1995),
('HK-10-230624-003', N'Trịnh Thị Như Mai',         '074210890112', 2010),

-- Ngày 22/06/2024
('HK-06-220624-001', N'Nguyễn Đăng Khoa',          '021206710125', 2006),
('HK-89-220624-002', N'Trần Quang Đại',            '018189780513', 1989),
('HK-08-220624-003', N'Tống Thiện Phúc',           '049208810216', 2008),

-- Ngày 21/06/2024
('HK-07-210624-001', N'Trần Phương Thảo',          '014207520311', 2007),
('HK-02-210624-002', N'Nguyễn Quốc Thịnh',         '033202470118', 2002),
('HK-90-210624-003', N'Trần Văn Phát',             '025190280309', 1990),

-- Ngày 20/06/2024
('HK-04-200624-001', N'Phạm Gia Khang',            '069204870506', 2004),
('HK-95-200624-002', N'Lê Thanh Tùng',             '047195920119', 1995),
('HK-10-200624-003', N'Trịnh Minh Quân',           '074210120626', 2010),
('HK-06-200624-004', N'Nguyễn Thị Hạnh',           '021206930914', 2006);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 19/06/2024
('HK-89-190624-001', N'Trần Văn Phong',            '018189780930', 1989),
('HK-08-190624-002', N'Tống Gia Linh',             '049208670715', 2008),
('HK-07-190624-003', N'Trần Mai Vy',               '014207860201', 2007),

-- Ngày 18/06/2024
('HK-02-180624-001', N'Nguyễn Minh Hậu',           '033202490830', 2002),
('HK-90-180624-002', N'Trần Thế Nam',              '025190760512', 1990),
('HK-04-180624-003', N'Phạm Hoài Thương',          '069204560401', 2004),

-- Ngày 17/06/2024
('HK-95-170624-001', N'Lê Tuấn Anh',               '047195340206', 1995),
('HK-10-170624-002', N'Trịnh Vĩnh Khang',          '074210280403', 2010),
('HK-06-170624-003', N'Nguyễn Hồng Hạnh',          '021206650928', 2006),

-- Ngày 16/06/2024
('HK-89-160624-001', N'Trần Gia Huy',              '018189190927', 1989),
('HK-08-160624-002', N'Tống Thanh Hằng',           '049208480123', 2008),
('HK-07-160624-003', N'Trần Văn Mạnh',             '014207290217', 2007),

-- Ngày 15/06/2024
('HK-92-150624-001', N'Cao Đức Duy',               '026192510808', 1992),
('HK-01-150624-002', N'Vũ Minh Thảo',              '004201250907', 2001),
('HK-91-150624-003', N'Đinh Minh Hưng',            '073191620519', 1991),

-- Ngày 14/06/2024
('HK-93-140624-001', N'Trịnh Hải Âu',              '061193270630', 1993),
('HK-08-140624-002', N'Tống Kim Chi',              '049208720218', 2008),
('HK-07-140624-003', N'Trần Thùy Trang',           '014207490827', 2007),

-- Ngày 13/06/2024
('HK-04-130624-001', N'Phạm Ngọc Ánh',             '069204440330', 2004),
('HK-95-130624-002', N'Lê Văn Dũng',               '047195870727', 1995),
('HK-10-130624-003', N'Trịnh Hoài Nam',            '074210780915', 2010),

-- Ngày 12/06/2024
('HK-06-120624-001', N'Nguyễn Văn Trường',         '021206120105', 2006),
('HK-89-120624-002', N'Trần Nhật Hào',             '018189330925', 1989),
('HK-08-120624-003', N'Tống Ngọc Nhi',             '049208680104', 2008),

-- Ngày 11/06/2024
('HK-07-110624-001', N'Trần Minh Hậu',             '014207550701', 2007),
('HK-02-110624-002', N'Nguyễn Đức Anh',            '033202830412', 2002),
('HK-90-110624-003', N'Trần Huy Hoàng',            '025190440719', 1990),

-- Ngày 10/06/2024
('HK-04-100624-001', N'Phạm Hoàng Nam',            '069204670122', 2004),
('HK-95-100624-002', N'Lê Quốc Cường',             '047195940630', 1995),
('HK-10-100624-003', N'Trịnh Phương Anh',          '074210960202', 2010),

-- Ngày 09/06/2024
('HK-06-090624-001', N'Nguyễn Anh Duy',            '021206860105', 2006),
('HK-89-090624-002', N'Trần Thanh Phong',          '018189690728', 1989),
('HK-08-090624-003', N'Tống Nhật Minh',            '049208710520', 2008),

-- Ngày 08/06/2024
('HK-07-080624-001', N'Trần Huyền Trang',          '014207380617', 2007),
('HK-02-080624-002', N'Nguyễn Trung Kiên',         '033202920130', 2002),
('HK-90-080624-003', N'Trần Bảo Ngọc',             '025190240326', 1990);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 07/06/2024
('HK-92-070624-001', N'Cao Minh Tuấn',             '026192450506', 1992),
('HK-01-070624-002', N'Vũ Gia Bảo',                '004201580119', 2001),
('HK-91-070624-003', N'Đinh Thị Hồng',             '073191950212', 1991),

-- Ngày 06/06/2024
('HK-93-060624-001', N'Trịnh Hoàng Khang',         '061193870813', 1993),
('HK-08-060624-002', N'Tống Hữu Đức',              '049208340927', 2008),
('HK-07-060624-003', N'Trần Hoài Nam',             '014207120305', 2007),

-- Ngày 05/06/2024
('HK-04-050624-001', N'Phạm Gia Khánh',            '069204330718', 2004),
('HK-95-050624-002', N'Lê Vân Trang',              '047195130311', 1995),
('HK-10-050624-003', N'Trịnh Quỳnh Như',           '074210530723', 2010),

-- Ngày 04/06/2024
('HK-06-040624-001', N'Nguyễn Trọng Nhân',         '021206490824', 2006),
('HK-89-040624-002', N'Trần Quốc Đạt',             '018189780205', 1989),
('HK-08-040624-003', N'Tống Thị Thuỳ',             '049208860519', 2008),

-- Ngày 03/06/2024
('HK-07-030624-001', N'Trần Hải Đăng',             '014207260813', 2007),
('HK-02-030624-002', N'Nguyễn Ngọc Mai',           '033202010910', 2002),
('HK-90-030624-003', N'Trần Chí Cường',            '025190160701', 1990),

-- Ngày 02/06/2024
('HK-04-020624-001', N'Phạm Minh Triết',           '069204040208', 2004),
('HK-95-020624-002', N'Lê Như Huỳnh',              '047195270406', 1995),
('HK-10-020624-003', N'Trịnh Thanh Tùng',          '074210460227', 2010),

-- Ngày 01/06/2024
('HK-06-010624-001', N'Nguyễn Thảo Linh',          '021206310830', 2006),
('HK-89-010624-002', N'Trần Văn Kiệt',             '018189540604', 1989),
('HK-08-010624-003', N'Tống Quốc Toàn',            '049208120901', 2008),

-- Ngày 31/05/2024
('HK-07-310524-001', N'Trần Minh Hòa',             '014207970219', 2007),
('HK-02-310524-002', N'Nguyễn Phan Vũ',            '033202690723', 2002),
('HK-90-310524-003', N'Trần Nhật Lâm',             '025190880712', 1990),

-- Ngày 30/05/2024
('HK-04-300524-001', N'Phạm Hồng Diễm',            '069204850311', 2004),
('HK-95-300524-002', N'Lê Quang Tùng',             '047195740215', 1995),
('HK-10-300524-003', N'Trịnh Minh Khoa',           '074210160106', 2010),

-- Ngày 29/05/2024
('HK-06-290524-001', N'Nguyễn Quốc Huy',           '021206220327', 2006),
('HK-89-290524-002', N'Trần Thị Lan',              '018189910202', 1989),
('HK-08-290524-003', N'Tống Khánh Huyền',          '049208670306', 2008),

-- Ngày 28/05/2024
('HK-07-280524-001', N'Trần Thanh Hà',             '014207880519', 2007),
('HK-02-280524-002', N'Nguyễn Văn Bình',           '033202130824', 2002),
('HK-90-280524-003', N'Trần Thế Anh',              '025190470726', 1990);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 27/05/2024
('HK-04-270524-001', N'Phạm Ngọc Quang',           '069204950109', 2004),
('HK-95-270524-002', N'Lê Trọng Khôi',             '047195390713', 1995),
('HK-10-270524-003', N'Trịnh Hoài Phương',         '074210360812', 2010),

-- Ngày 26/05/2024
('HK-06-260524-001', N'Nguyễn Hải Đăng',           '021206560506', 2006),
('HK-89-260524-002', N'Trần Minh Thư',             '018189260219', 1989),
('HK-08-260524-003', N'Tống Hữu Phước',            '049208240903', 2008),

-- Ngày 25/05/2024
('HK-07-250524-001', N'Trần Bảo Trân',             '014207550911', 2007),
('HK-02-250524-002', N'Nguyễn Văn Toàn',           '033202920527', 2002),
('HK-90-250524-003', N'Trần Văn Hậu',              '025190190110', 1990),

-- Ngày 24/05/2024
('HK-04-240524-001', N'Phạm Thành Đạt',            '069204310725', 2004),
('HK-95-240524-002', N'Lê Hồng Phúc',              '047195610605', 1995),
('HK-10-240524-003', N'Trịnh Ngọc Hân',            '074210240921', 2010),

-- Ngày 23/05/2024
('HK-06-230524-001', N'Nguyễn Mai Linh',           '021206410404', 2006),
('HK-89-230524-002', N'Trần Thanh Tú',             '018189820708', 1989),
('HK-08-230524-003', N'Tống Hữu Đạt',              '049208370928', 2008),

-- Ngày 22/05/2024
('HK-07-220524-001', N'Trần Bích Thủy',            '014207430512', 2007),
('HK-02-220524-002', N'Nguyễn Gia Minh',           '033202310330', 2002),
('HK-90-220524-003', N'Trần Thái Bình',            '025190610202', 1990),

-- Ngày 21/05/2024
('HK-04-210524-001', N'Phạm Minh Trang',           '069204780723', 2004),
('HK-95-210524-002', N'Lê Hoàng Nam',              '047195850608', 1995),
('HK-10-210524-003', N'Trịnh Thanh Mai',           '074210940911', 2010),

-- Ngày 20/05/2024
('HK-06-200524-001', N'Nguyễn Văn Lâm',            '021206010225', 2006),
('HK-89-200524-002', N'Trần Anh Dũng',             '018189600725', 1989),
('HK-08-200524-003', N'Tống Như Ngọc',             '049208510706', 2008),

-- Ngày 19/05/2024
('HK-07-190524-001', N'Trần Nhật Minh',            '014207180115', 2007),
('HK-02-190524-002', N'Nguyễn Thị Mai',            '033202520210', 2002),
('HK-90-190524-003', N'Trần Tấn Phát',             '025190740503', 1990),

-- Ngày 18/05/2024
('HK-04-180524-001', N'Phạm Hải An',               '069204690715', 2004),
('HK-95-180524-002', N'Lê Trung Kiên',             '047195410303', 1995),
('HK-10-180524-003', N'Trịnh Hoàng Minh',          '074210710907', 2010),

-- Ngày 17/05/2024
('HK-06-170524-001', N'Nguyễn Tuấn Kiệt',          '021206820914', 2006),
('HK-89-170524-002', N'Trần Thị Yến',              '018189130320', 1989),
('HK-08-170524-003', N'Tống Minh Nhật',            '049208910811', 2008),

-- Ngày 16/05/2024
('HK-07-160524-001', N'Trần Kim Ngân',             '014207370805', 2007),
('HK-02-160524-002', N'Nguyễn Văn Nam',            '033202700617', 2002),
('HK-90-160524-003', N'Trần Ngọc Trinh',           '025190920310', 1990);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 15/05/2024
('HK-04-150524-001', N'Phạm Huy Khánh',            '069204620905', 2004),
('HK-95-150524-002', N'Lê Thị Bích',               '047195930528', 1995),
('HK-10-150524-003', N'Trịnh Minh Tâm',            '074210490515', 2010),

-- Ngày 14/05/2024
('HK-06-140524-001', N'Nguyễn Minh Hằng',          '021206430710', 2006),
('HK-89-140524-002', N'Trần Văn Nghĩa',            '018189230224', 1989),
('HK-08-140524-003', N'Tống Hải Yến',              '049208040911', 2008),

-- Ngày 13/05/2024
('HK-07-130524-001', N'Trần Quốc Bảo',             '014207910209', 2007),
('HK-02-130524-002', N'Nguyễn Hữu Tâm',            '033202650601', 2002),
('HK-90-130524-003', N'Trần Tố Uyên',              '025190820130', 1990),

-- Ngày 12/05/2024
('HK-04-120524-001', N'Phạm Quang Huy',            '069204520907', 2004),
('HK-95-120524-002', N'Lê Thị Ánh',                '047195730618', 1995),
('HK-10-120524-003', N'Trịnh Gia Khiêm',           '074210870807', 2010),

-- Ngày 11/05/2024
('HK-06-110524-001', N'Nguyễn Hữu Nam',            '021206640610', 2006),
('HK-89-110524-002', N'Trần Hoàng Vũ',             '018189140723', 1989),
('HK-08-110524-003', N'Tống Lan Hương',            '049208960105', 2008),

-- Ngày 10/05/2024
('HK-07-100524-001', N'Trần Như Quỳnh',            '014207060504', 2007),
('HK-02-100524-002', N'Nguyễn Thế An',             '033202470413', 2002),
('HK-90-100524-003', N'Trần Thị Thanh',            '025190630518', 1990),

-- Ngày 09/05/2024
('HK-04-090524-001', N'Phạm Văn Dương',            '069204870215', 2004),
('HK-95-090524-002', N'Lê Minh Phương',            '047195120304', 1995),
('HK-10-090524-003', N'Trịnh Hương Ly',            '074210990725', 2010),

-- Ngày 08/05/2024
('HK-06-080524-001', N'Nguyễn Thị Vân',            '021206110817', 2006),
('HK-89-080524-002', N'Trần Trung Đức',            '018189640430', 1989),
('HK-08-080524-003', N'Tống Quốc Cường',           '049208180322', 2008),

-- Ngày 07/05/2024
('HK-07-070524-001', N'Trần Kim Ánh',              '014207710715', 2007),
('HK-02-070524-002', N'Nguyễn Hải Quân',           '033202160122', 2002),
('HK-90-070524-003', N'Trần Hồng Mai',             '025190450301', 1990),

-- Ngày 06/05/2024
('HK-04-060524-001', N'Phạm Đức Thịnh',            '069204560524', 2004),
('HK-95-060524-002', N'Lê Tấn Lộc',                '047195610827', 1995),
('HK-10-060524-003', N'Trịnh Thị Hòa',             '074210730820', 2010),

-- Ngày 05/05/2024
('HK-06-050524-001', N'Nguyễn Bảo Trâm',           '021206850212', 2006),
('HK-89-050524-002', N'Trần Đức Hạnh',             '018189750613', 1989),
('HK-08-050524-003', N'Tống Khắc Hưng',            '049208220805', 2008),

-- Ngày 04/05/2024
('HK-07-040524-001', N'Trần Nhã Linh',             '014207350920', 2007),
('HK-02-040524-002', N'Nguyễn Thị Hiền',           '033202980226', 2002),
('HK-90-040524-003', N'Trần Thế Duy',              '025190240916', 1990);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 03/05/2024
('HK-04-030524-001', N'Phạm Hữu Lộc',              '069204420927', 2004),
('HK-95-030524-002', N'Lê Thị Mai',                '047195180126', 1995),
('HK-10-030524-003', N'Trịnh Quốc Duy',            '074210660918', 2010),

-- Ngày 02/05/2024
('HK-06-020524-001', N'Nguyễn Văn Phong',          '021206540108', 2006),
('HK-89-020524-002', N'Trần Thị Hường',            '018189810717', 1989),
('HK-08-020524-003', N'Tống Thế Long',             '049208390615', 2008),

-- Ngày 01/05/2024
('HK-07-010524-001', N'Trần Hồng Phúc',            '014207510521', 2007),
('HK-02-010524-002', N'Nguyễn Thành Danh',         '033202530105', 2002),
('HK-90-010524-003', N'Trần Thị Hà',               '025190160213', 1990),

-- Ngày 30/04/2024
('HK-04-300424-001', N'Phạm Thị Loan',             '069204770201', 2004),
('HK-95-300424-002', N'Lê Trọng Hiếu',             '047195370917', 1995),
('HK-10-300424-003', N'Trịnh Như Mai',             '074210390725', 2010),

-- Ngày 29/04/2024
('HK-06-290424-001', N'Nguyễn Thanh Trúc',         '021206120619', 2006),
('HK-89-290424-002', N'Trần Quang Minh',           '018189580404', 1989),
('HK-08-290424-003', N'Tống Hoàng Nam',            '049208810105', 2008),

-- Ngày 28/04/2024
('HK-07-280424-001', N'Trần Thị Tuyết',            '014207340811', 2007),
('HK-02-280424-002', N'Nguyễn Minh Hoàng',         '033202690903', 2002),
('HK-90-280424-003', N'Trần Thị Bích Ngọc',        '025190970114', 1990),

-- Ngày 27/04/2024
('HK-04-270424-001', N'Phạm Văn Lâm',              '069204890518', 2004),
('HK-95-270424-002', N'Lê Đức Tài',                '047195750210', 1995),
('HK-10-270424-003', N'Trịnh Gia Linh',            '074210260829', 2010),

-- Ngày 26/04/2024
('HK-06-260424-001', N'Nguyễn Khánh Linh',         '021206340604', 2006),
('HK-89-260424-002', N'Trần Hữu Phước',            '018189930507', 1989),
('HK-08-260424-003', N'Tống Như Thảo',             '049208490222', 2008),

-- Ngày 25/04/2024
('HK-07-250424-001', N'Trần Quốc Việt',            '014207670406', 2007),
('HK-02-250424-002', N'Nguyễn Như Quỳnh',          '033202350927', 2002),
('HK-90-250424-003', N'Trần Đăng Khoa',            '025190750812', 1990),

-- Ngày 24/04/2024
('HK-04-240424-001', N'Phạm Đức Minh',             '069204320816', 2004),
('HK-95-240424-002', N'Lê Tố Như',                 '047195950714', 1995),
('HK-10-240424-003', N'Trịnh Nhật Hào',            '074210850209', 2010),

-- Ngày 23/04/2024
('HK-06-230424-001', N'Nguyễn Minh Nhựt',          '021206710928', 2006),
('HK-89-230424-002', N'Trần Ngọc Trân',            '018189140819', 1989),
('HK-08-230424-003', N'Tống Nhật Minh',            '049208240330', 2008);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 22/04/2024
('HK-07-220424-001', N'Trần Hải Nam',              '014207910715', 2007),
('HK-02-220424-002', N'Nguyễn Phương Linh',        '033202880312', 2002),
('HK-90-220424-003', N'Trần Văn Long',             '025190280707', 1990),
('HK-06-220424-004', N'Nguyễn Đức Thắng',          '021206270114', 2006),

-- Ngày 21/04/2024
('HK-04-210424-001', N'Phạm Minh Huy',             '069204460905', 2004),
('HK-95-210424-002', N'Lê Thị Thanh',              '047195660225', 1995),
('HK-10-210424-003', N'Trịnh Hồng Quân',           '074210570828', 2010),
('HK-08-210424-004', N'Tống Thị Hòa',              '049208620621', 2008),

-- Ngày 20/04/2024
('HK-07-200424-001', N'Trần Nhật Hào',             '014207320926', 2007),
('HK-02-200424-002', N'Nguyễn Thái Sơn',           '033202180411', 2002),
('HK-90-200424-003', N'Trần Bảo Trâm',             '025190530920', 1990),

-- Ngày 19/04/2024
('HK-06-190424-001', N'Nguyễn Trọng Duy',          '021206800804', 2006),
('HK-89-190424-002', N'Trần Ngọc Linh',            '018189660512', 1989),
('HK-08-190424-003', N'Tống Gia Khang',            '049208110301', 2008),

-- Ngày 18/04/2024
('HK-04-180424-001', N'Phạm Văn Kiệt',             '069204050723', 2004),
('HK-95-180424-002', N'Lê Quốc Trung',             '047195870114', 1995),
('HK-10-180424-003', N'Trịnh Thanh Thảo',          '074210680629', 2010),

-- Ngày 17/04/2024
('HK-07-170424-001', N'Trần Duy Bảo',              '014207810930', 2007),
('HK-02-170424-002', N'Nguyễn Thị Yến',            '033202410517', 2002),
('HK-90-170424-003', N'Trần Hồng Nhung',           '025190190830', 1990),

-- Ngày 16/04/2024
('HK-06-160424-001', N'Nguyễn Anh Minh',           '021206670719', 2006),
('HK-89-160424-002', N'Trần Quốc Cường',           '018189320610', 1989),
('HK-08-160424-003', N'Tống Bảo Trúc',             '049208730318', 2008),

-- Ngày 15/04/2024
('HK-04-150424-001', N'Phạm Thanh Bình',           '069204930110', 2004),
('HK-95-150424-002', N'Lê Hữu Thịnh',              '047195440923', 1995),
('HK-10-150424-003', N'Trịnh Ngọc Hiếu',           '074210460725', 2010),
('HK-08-150424-004', N'Tống Trường An',            '049208520407', 2008),

-- Ngày 14/04/2024
('HK-07-140424-001', N'Trần Hải Quân',             '014207050821', 2007),
('HK-02-140424-002', N'Nguyễn Thị Tuyết Mai',      '033202750920', 2002),
('HK-90-140424-003', N'Trần Văn Khải',             '025190620802', 1990),

-- Ngày 13/04/2024
('HK-06-130424-001', N'Nguyễn Đức Minh',           '021206940226', 2006),
('HK-89-130424-002', N'Trần Thị Mỹ Duyên',         '018189410912', 1989),
('HK-08-130424-003', N'Tống Khánh Vy',             '049208330508', 2008),

-- Ngày 12/04/2024
('HK-04-120424-001', N'Phạm Đình Thái',            '069204280916', 2004),
('HK-95-120424-002', N'Lê Tuấn Kiệt',              '047195310617', 1995),
('HK-10-120424-003', N'Trịnh Thị Ngọc Diễm',       '074210920904', 2010),
('HK-02-120424-004', N'Nguyễn Hải Yến',            '033202660519', 2002);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 11/04/2024
('HK-07-110424-001', N'Trần Văn Quang',            '014207370327', 2007),
('HK-02-110424-002', N'Nguyễn Hoàng My',           '033202540219', 2002),
('HK-90-110424-003', N'Trần Minh Hiếu',            '025190690915', 1990),
('HK-06-110424-004', N'Nguyễn Thanh Hương',        '021206880405', 2006),
('HK-08-110424-005', N'Tống Duy Tân',              '049208370727', 2008),

-- Ngày 10/04/2024
('HK-04-100424-001', N'Phạm Nhật Hào',             '069204480111', 2004),
('HK-95-100424-002', N'Lê Thị Kim Ngân',           '047195710926', 1995),
('HK-10-100424-003', N'Trịnh Minh Thư',            '074210180403', 2010),
('HK-08-100424-004', N'Tống Văn Thái',             '049208020820', 2008),

-- Ngày 09/04/2024
('HK-07-090424-001', N'Trần Quốc Hưng',            '014207830715', 2007),
('HK-02-090424-002', N'Nguyễn Thu Thảo',           '033202150210', 2002),
('HK-90-090424-003', N'Trần Nhật Tân',             '025190480621', 1990),

-- Ngày 08/04/2024
('HK-06-080424-001', N'Nguyễn Trọng Nghĩa',        '021206710924', 2006),
('HK-89-080424-002', N'Trần Kim Ngọc',             '018189650505', 1989),
('HK-08-080424-003', N'Tống Thanh Lam',            '049208740716', 2008),

-- Ngày 07/04/2024
('HK-04-070424-001', N'Phạm Hoàng Duy',            '069204370813', 2004),
('HK-95-070424-002', N'Lê Đức Thịnh',              '047195900218', 1995),
('HK-10-070424-003', N'Trịnh Tường Vy',            '074210770130', 2010),

-- Ngày 06/04/2024
('HK-07-060424-001', N'Trần Thị Mai',              '014207240927', 2007),
('HK-02-060424-002', N'Nguyễn Gia Hưng',           '033202410711', 2002),
('HK-90-060424-003', N'Trần Khánh Duy',            '025190320616', 1990),

-- Ngày 05/04/2024
('HK-06-050424-001', N'Nguyễn Thị Hồng Hạnh',      '021206230509', 2006),
('HK-89-050424-002', N'Trần Văn Minh',             '018189580122', 1989),
('HK-08-050424-003', N'Tống Quốc Dũng',            '049208890320', 2008),
('HK-10-050424-004', N'Trịnh Kim Tuyến',           '074210270418', 2010),

-- Ngày 04/04/2024
('HK-04-040424-001', N'Phạm Quốc Vũ',              '069204790925', 2004),
('HK-95-040424-002', N'Lê Tường Linh',             '047195330213', 1995),
('HK-10-040424-003', N'Trịnh Hữu Khang',           '074210580405', 2010),

-- Ngày 03/04/2024
('HK-07-030424-001', N'Trần Minh Trí',             '014207460712', 2007),
('HK-02-030424-002', N'Nguyễn Thanh Nhã',          '033202870928', 2002),
('HK-90-030424-003', N'Trần Quốc Cường',           '025190810720', 1990),

-- Ngày 02/04/2024
('HK-06-020424-001', N'Nguyễn Ngọc Thảo',          '021206740928', 2006),
('HK-89-020424-002', N'Trần Tường Vy',             '018189640817', 1989),
('HK-08-020424-003', N'Tống Hoàng Minh',           '049208160206', 2008),
('HK-10-020424-004', N'Trịnh Hồng Vân',            '074210390913', 2010),

-- Ngày 01/04/2024
('HK-04-010424-001', N'Phạm Gia Hân',              '069204850807', 2004),
('HK-95-010424-002', N'Lê Hoàng Nam',              '047195010123', 1995),
('HK-10-010424-003', N'Trịnh Mai Phương',          '074210010816', 2010);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 31/03/2024
('HK-07-310324-001', N'Trần Văn Đức',              '014207280215', 2007),
('HK-02-310324-002', N'Nguyễn Minh Thảo',          '033202770529', 2002),
('HK-90-310324-003', N'Trần Thanh Tùng',           '025190450715', 1990),
('HK-06-310324-004', N'Nguyễn Hải Nam',            '021206950307', 2006),

-- Ngày 30/03/2024
('HK-08-300324-001', N'Tống Thị Lan',              '049208510920', 2008),
('HK-04-300324-002', N'Phạm Quốc Khánh',           '069204170325', 2004),
('HK-95-300324-003', N'Lê Đức Hiếu',               '047195160213', 1995),
('HK-10-300324-004', N'Trịnh Thanh Hằng',          '074210740314', 2010),
('HK-89-300324-005', N'Trần Văn Thắng',            '018189940718', 1989),

-- Ngày 29/03/2024
('HK-07-290324-001', N'Trần Quốc Huy',             '014207990626', 2007),
('HK-02-290324-002', N'Nguyễn Kim Oanh',           '033202300403', 2002),
('HK-90-290324-003', N'Trần Minh Trí',             '025190600320', 1990),

-- Ngày 28/03/2024
('HK-06-280324-001', N'Nguyễn Bảo Hân',            '021206570215', 2006),
('HK-89-280324-002', N'Trần Quốc Trung',           '018189120927', 1989),
('HK-08-280324-003', N'Tống Gia Khánh',            '049208640130', 2008),
('HK-10-280324-004', N'Trịnh Tường Vy',            '074210860720', 2010),

-- Ngày 27/03/2024
('HK-04-270324-001', N'Phạm Thị Hồng',             '069204920408', 2004),
('HK-95-270324-002', N'Lê Nhật Nam',               '047195700205', 1995),
('HK-10-270324-003', N'Trịnh Bảo Anh',             '074210210505', 2010),

-- Ngày 26/03/2024
('HK-07-260324-001', N'Trần Khánh Linh',           '014207370630', 2007),
('HK-02-260324-002', N'Nguyễn Hoàng Hưng',         '033202100808', 2002),
('HK-90-260324-003', N'Trần Thanh Hà',             '025190820127', 1990),

-- Ngày 25/03/2024
('HK-06-250324-001', N'Nguyễn Duy Khánh',          '021206300606', 2006),
('HK-89-250324-002', N'Trần Thị Lan',              '018189750119', 1989),
('HK-08-250324-003', N'Tống Trung Hậu',            '049208390408', 2008),

-- Ngày 24/03/2024
('HK-04-240324-001', N'Phạm Thanh Tâm',            '069204410902', 2004),
('HK-95-240324-002', N'Lê Bích Ngọc',              '047195530504', 1995),
('HK-10-240324-003', N'Trịnh Phương Uyên',         '074210620703', 2010),
('HK-07-240324-004', N'Trần Nhật Nam',             '014207880417', 2007),

-- Ngày 23/03/2024
('HK-02-230324-001', N'Nguyễn Gia Bảo',            '033202900120', 2002),
('HK-90-230324-002', N'Trần Hữu Nghĩa',            '025190180925', 1990),
('HK-06-230324-003', N'Nguyễn Kim Liên',           '021206580928', 2006),
('HK-08-230324-004', N'Tống Nhật Huy',             '049208290712', 2008),

-- Ngày 22/03/2024
('HK-04-220324-001', N'Phạm Thị Hằng',             '069204620407', 2004),
('HK-95-220324-002', N'Lê Thế Duy',                '047195450206', 1995),
('HK-10-220324-003', N'Trịnh Khánh Huyền',         '074210160615', 2010),
('HK-89-220324-004', N'Trần Minh Hạnh',            '018189650824', 1989);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 21/03/2024
('HK-07-210324-001', N'Trần Minh Phú',             '014207530419', 2007),
('HK-02-210324-002', N'Nguyễn Trúc Lam',           '033202310807', 2002),
('HK-90-210324-003', N'Trần Đức Lợi',              '025190730330', 1990),
('HK-06-210324-004', N'Nguyễn Khánh Chi',          '021206480604', 2006),
('HK-89-210324-005', N'Trần Văn Hùng',             '018189850909', 1989),

-- Ngày 20/03/2024
('HK-04-200324-002', N'Phạm Thị Mỹ Linh',          '069204670728', 2004),
('HK-95-200324-003', N'Lê Trọng Nghĩa',            '047195110301', 1995),
('HK-10-200324-004', N'Trịnh Hải Yến',             '074210720806', 2010),
('HK-07-200324-001', N'Trần Thái Sơn',             '014207170712', 2007),

-- Ngày 19/03/2024
('HK-02-190324-001', N'Nguyễn Phương Uyên',        '033202610930', 2002),
('HK-90-190324-002', N'Trần Văn Long',             '025190280407', 1990),
('HK-06-190324-003', N'Nguyễn Hoài An',            '021206620519', 2006),

-- Ngày 18/03/2024
('HK-08-180324-001', N'Tống Minh Huyền',           '049208410928', 2008),
('HK-04-180324-002', N'Phạm Quốc Bảo',             '069204010326', 2004),
('HK-95-180324-003', N'Lê Thanh Nam',              '047195340125', 1995),
('HK-10-180324-004', N'Trịnh Nhật Vy',             '074210480618', 2010),

-- Ngày 17/03/2024
('HK-07-170324-001', N'Trần Gia Linh',             '014207780205', 2007),
('HK-02-170324-002', N'Nguyễn Thái Dương',         '033202170829', 2002),
('HK-90-170324-003', N'Trần Văn Sơn',              '025190110917', 1990),

-- Ngày 16/03/2024
('HK-06-160324-001', N'Nguyễn Hữu Phước',          '021206990518', 2006),
('HK-89-160324-002', N'Trần Hải Minh',             '018189510709', 1989),
('HK-08-160324-003', N'Tống Quốc Tuấn',            '049208090807', 2008),

-- Ngày 15/03/2024
('HK-04-150324-001', N'Phạm Thị Hạnh',             '069204940118', 2004),
('HK-95-150324-002', N'Lê Hữu Đạt',                '047195090224', 1995),
('HK-10-150324-003', N'Trịnh Gia Bảo',             '074210300604', 2010),

-- Ngày 14/03/2024
('HK-07-140324-001', N'Trần Khánh Huyền',          '014207420916', 2007),
('HK-02-140324-002', N'Nguyễn Minh Nhật',          '033202450117', 2002),
('HK-90-140324-003', N'Trần Anh Đức',              '025190360310', 1990),

-- Ngày 13/03/2024
('HK-06-130324-001', N'Nguyễn Hải Yến',            '021206070506', 2006),
('HK-89-130324-002', N'Trần Tấn Phát',             '018189650402', 1989),
('HK-08-130324-003', N'Tống Tường Vy',             '049208670713', 2008),

-- Ngày 12/03/2024
('HK-04-120324-001', N'Phạm Nhật Minh',            '069204220626', 2004),
('HK-95-120324-002', N'Lê Trọng Khang',            '047195870320', 1995),
('HK-10-120324-003', N'Trịnh Bảo Ngọc',            '074210940401', 2010),
('HK-07-120324-004', N'Trần Quốc Toàn',            '014207140815', 2007),
('HK-02-120324-005', N'Nguyễn Thị Thanh',          '033202720627', 2002);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 11/03/2024
('HK-90-110324-001', N'Trần Nhật Hào',              '025190390707', 1990),
('HK-06-110324-002', N'Nguyễn Thanh Tùng',         '021206280802', 2006),
('HK-89-110324-003', N'Trần Thị Hoa',              '018189640211', 1989),
('HK-08-110324-004', N'Tống Ngọc Trâm',            '049208740328', 2008),
('HK-04-110324-005', N'Phạm Thanh Vân',            '069204310711', 2004),

-- Ngày 10/03/2024
('HK-95-100324-001', N'Lê Văn Bình',               '047195580601', 1995),
('HK-10-100324-002', N'Trịnh Hồng Nhung',          '074210150402', 2010),
('HK-07-100324-003', N'Trần Minh Hằng',            '014207180907', 2007),

-- Ngày 09/03/2024
('HK-02-090324-001', N'Nguyễn Quang Dũng',         '033202460208', 2002),
('HK-90-090324-002', N'Trần Văn Hiếu',             '025190090910', 1990),
('HK-06-090324-003', N'Nguyễn Diễm My',            '021206670306', 2006),

-- Ngày 08/03/2024
('HK-89-080324-001', N'Trần Nhật Linh',            '018189210217', 1989),
('HK-08-080324-002', N'Tống Trọng Nhân',           '049208660729', 2008),
('HK-04-080324-003', N'Phạm Thị Hoài',             '069204530328', 2004),
('HK-95-080324-004', N'Lê Gia Khánh',              '047195460103', 1995),

-- Ngày 07/03/2024
('HK-10-070324-001', N'Trịnh Quốc Huy',            '074210750910', 2010),
('HK-07-070324-002', N'Trần Hà Vy',                '014207980709', 2007),
('HK-02-070324-003', N'Nguyễn Quốc Toản',          '033202990216', 2002),

-- Ngày 06/03/2024
('HK-90-060324-001', N'Trần Kim Ngân',             '025190340812', 1990),
('HK-06-060324-002', N'Nguyễn Quang Huy',          '021206720520', 2006),
('HK-89-060324-003', N'Trần Bảo Châu',             '018189580104', 1989),
('HK-08-060324-004', N'Tống Hải Dương',            '049208050730', 2008),

-- Ngày 05/03/2024
('HK-04-050324-001', N'Phạm Gia Hân',              '069204380320', 2004),
('HK-95-050324-002', N'Lê Bảo Khang',              '047195780711', 1995),
('HK-10-050324-003', N'Trịnh Thanh Mai',           '074210610205', 2010),

-- Ngày 04/03/2024
('HK-07-040324-001', N'Trần Quỳnh Anh',            '014207120119', 2007),
('HK-02-040324-002', N'Nguyễn Thanh Phong',        '033202130501', 2002),
('HK-90-040324-003', N'Trần Văn Hậu',              '025190410405', 1990),

-- Ngày 03/03/2024
('HK-06-030324-001', N'Nguyễn Hoài Bảo',           '021206940416', 2006),
('HK-89-030324-002', N'Trần Khánh Toàn',           '018189720821', 1989),
('HK-08-030324-003', N'Tống Thị Thảo',             '049208820618', 2008),

-- Ngày 02/03/2024
('HK-04-020324-001', N'Phạm Huyền Trân',           '069204020918', 2004),
('HK-95-020324-002', N'Lê Trúc Nhân',              '047195900728', 1995),
('HK-10-020324-003', N'Trịnh Quốc Tuấn',           '074210840906', 2010),
('HK-07-020324-004', N'Trần Minh Trí',             '014207450823', 2007),

-- Ngày 01/03/2024
('HK-02-010324-001', N'Nguyễn Minh Hiếu',          '033202390227', 2002),
('HK-90-010324-002', N'Trần Thanh Lam',            '025190870322', 1990),
('HK-06-010324-003', N'Nguyễn Thị Lan',            '021206030912', 2006);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 29/02/2024
('HK-89-290224-001', N'Trần Quốc Trung',           '018189530129', 1989),
('HK-08-290224-002', N'Tống Diễm Quỳnh',           '049208390228', 2008),
('HK-04-290224-003', N'Phạm Bảo Trâm',             '069204620624', 2004),
('HK-95-290224-004', N'Lê Ngọc Hải',               '047195310715', 1995),

-- Ngày 28/02/2024
('HK-10-280224-001', N'Trịnh Đức Mạnh',            '074210240412', 2010),
('HK-02-280224-002', N'Nguyễn Hữu Lộc',            '033202980505', 2002),

-- Ngày 27/02/2024
('HK-90-270224-001', N'Trần Thị Ngọc Hân',         '025190570709', 1990),
('HK-06-270224-002', N'Nguyễn Thanh Tâm',          '021206110726', 2006),
('HK-89-270224-003', N'Trần Quốc Hưng',            '018189910821', 1989),

-- Ngày 26/02/2024
('HK-08-260224-001', N'Tống Gia Bảo',              '049208470804', 2008),
('HK-04-260224-002', N'Phạm Nhật Quang',           '069204180615', 2004),
('HK-95-260224-003', N'Lê Thị Phương',             '047195560801', 1995),

-- Ngày 25/02/2024
('HK-10-250224-001', N'Trịnh Thanh Hiền',          '074210350103', 2010),
('HK-07-250224-002', N'Trần Quốc Việt',            '014207140804', 2007),
('HK-02-250224-003', N'Nguyễn Thanh Hải',          '033202250315', 2002),
('HK-90-250224-004', N'Trần Thị Thanh',            '025190670728', 1990),

-- Ngày 24/02/2024
('HK-06-240224-001', N'Nguyễn Thanh Tuyền',        '021206540827', 2006),
('HK-89-240224-002', N'Trần Minh Đạt',             '018189360905', 1989),

-- Ngày 23/02/2024
('HK-08-230224-001', N'Tống Hữu Thiện',            '049208650111', 2008),
('HK-04-230224-002', N'Phạm Minh Tâm',             '069204900328', 2004),
('HK-95-230224-003', N'Lê Văn Sơn',                '047195740107', 1995),

-- Ngày 22/02/2024
('HK-10-220224-001', N'Trịnh Thanh Hà',            '074210270306', 2010),
('HK-07-220224-002', N'Trần Hồng Ngọc',            '014207430418', 2007),
('HK-02-220224-003', N'Nguyễn Khánh Linh',         '033202090914', 2002),

-- Ngày 21/02/2024
('HK-90-210224-001', N'Trần Gia An',               '025190780728', 1990),
('HK-06-210224-002', N'Nguyễn Hồng Đức',           '021206310421', 2006),
('HK-89-210224-003', N'Trần Ngọc Bích',            '018189630208', 1989),

-- Ngày 20/02/2024
('HK-08-200224-001', N'Tống Nhật Minh',            '049208890417', 2008),
('HK-04-200224-002', N'Phạm Hải Yến',              '069204200926', 2004),
('HK-95-200224-003', N'Lê Đình Khoa',              '047195360615', 1995),

-- Ngày 19/02/2024
('HK-10-190224-001', N'Trịnh Nhật Hào',            '074210410204', 2010),
('HK-07-190224-002', N'Trần Như Ý',                '014207230926', 2007),
('HK-02-190224-003', N'Nguyễn Thị Huyền',          '033202170302', 2002),

-- Ngày 18/02/2024
('HK-90-180224-001', N'Trần Hoàng Vũ',             '025190610219', 1990),
('HK-06-180224-002', N'Nguyễn Nhật Nam',           '021206650123', 2006),
('HK-89-180224-003', N'Trần Thị Diễm',             '018189040618', 1989);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 17/02/2024
('HK-08-170224-001', N'Tống Anh Tuấn',             '049208720914', 2008),
('HK-04-170224-002', N'Phạm Hoàng Ngân',           '069204810729', 2004),
('HK-95-170224-003', N'Lê Quang Dũng',             '047195680208', 1995),

-- Ngày 16/02/2024
('HK-10-160224-001', N'Trịnh Đức Lâm',             '074210020414', 2010),
('HK-07-160224-002', N'Trần Quỳnh Hương',          '014207860317', 2007),
('HK-02-160224-003', N'Nguyễn Tấn Phúc',           '033202600128', 2002),
('HK-90-160224-004', N'Trần Văn Hòa',              '025190450515', 1990),

-- Ngày 15/02/2024
('HK-06-150224-001', N'Nguyễn Minh Hiếu',          '021206290703', 2006),
('HK-89-150224-002', N'Trần Hữu Phước',            '018189740119', 1989),

-- Ngày 14/02/2024
('HK-08-140224-001', N'Tống Minh Thư',             '049208930628', 2008),
('HK-04-140224-002', N'Phạm Văn Long',             '069204360402', 2004),
('HK-95-140224-003', N'Lê Tấn Lợi',                '047195820911', 1995),

-- Ngày 13/02/2024
('HK-10-130224-001', N'Trịnh Quang Huy',           '074210640417', 2010),
('HK-07-130224-002', N'Trần Thị Như Quỳnh',        '014207470605', 2007),
('HK-02-130224-003', N'Nguyễn Văn Cảnh',           '033202130506', 2002),

-- Ngày 12/02/2024
('HK-90-120224-001', N'Trần Bá Sơn',               '025190320919', 1990),
('HK-06-120224-002', N'Nguyễn Thị Thắm',           '021206510610', 2006),
('HK-89-120224-003', N'Trần Hồng Phúc',            '018189350904', 1989),

-- Ngày 11/02/2024
('HK-08-110224-001', N'Tống Đức Anh',              '049208580219', 2008),
('HK-04-110224-002', N'Phạm Hồng Nhung',           '069204190307', 2004),
('HK-95-110224-003', N'Lê Văn Đạt',                '047195760217', 1995),

-- Ngày 10/02/2024
('HK-10-100224-001', N'Trịnh Gia Bảo',             '074210270522', 2010),
('HK-07-100224-002', N'Trần Ngọc Yến',             '014207720920', 2007),
('HK-02-100224-003', N'Nguyễn Thị Hoa',            '033202590610', 2002),

-- Ngày 09/02/2024
('HK-90-090224-001', N'Trần Quang Vinh',           '025190610812', 1990),
('HK-06-090224-002', N'Nguyễn Nhật Minh',          '021206150429', 2006),
('HK-89-090224-003', N'Trần Thu Huyền',            '018189870715', 1989),

-- Ngày 08/02/2024
('HK-08-080224-001', N'Tống Minh Tâm',             '049208740830', 2008),
('HK-04-080224-002', N'Phạm Gia Hưng',             '069204120109', 2004),
('HK-95-080224-003', N'Lê Hồng Sơn',               '047195430624', 1995),

-- Ngày 07/02/2024
('HK-10-070224-001', N'Trịnh Khánh Ngọc',          '074210990315', 2010),
('HK-07-070224-002', N'Trần Kim Ngân',             '014207360801', 2007),
('HK-02-070224-003', N'Nguyễn Văn Tiến',           '033202840905', 2002),

-- Ngày 06/02/2024
('HK-90-060224-001', N'Trần Hữu Tài',              '025190020405', 1990),
('HK-06-060224-002', N'Nguyễn Thanh Thảo',         '021206240707', 2006),
('HK-89-060224-003', N'Trần Minh Tường',           '018189580814', 1989);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 05/02/2024
('HK-08-050224-001', N'Tống Hồng Nhung',            '049208410112', 2008),
('HK-04-050224-002', N'Phạm Tuấn Dũng',             '069204730314', 2004),
('HK-95-050224-003', N'Lê Trọng Hào',               '047195660921', 1995),

-- Ngày 04/02/2024
('HK-10-040224-001', N'Trịnh Thái Hòa',             '074210820328', 2010),
('HK-07-040224-002', N'Trần Hữu Thiện',             '014207160218', 2007),
('HK-02-040224-003', N'Nguyễn Nhật Tân',            '033202270326', 2002),

-- Ngày 03/02/2024
('HK-90-030224-001', N'Trần Thị Kim Dung',          '025190990307', 1990),
('HK-06-030224-002', N'Nguyễn Minh Tâm',            '021206980115', 2006),
('HK-89-030224-003', N'Trần Trung Hiếu',            '018189740422', 1989),
('HK-08-030224-004', N'Tống Quốc Huy',              '049208540205', 2008),

-- Ngày 02/02/2024
('HK-04-020224-001', N'Phạm Thị Như Ý',             '069204240812', 2004),
('HK-10-020224-002', N'Trịnh Hữu Lộc',              '074210330921', 2010),

-- Ngày 01/02/2024
('HK-07-010224-001', N'Trần Thị Diễm Quỳnh',         '014207310703', 2007),
('HK-02-010224-002', N'Nguyễn Thanh Sơn',           '033202430215', 2002),
('HK-90-010224-003', N'Trần Đức Tài',               '025190170429', 1990),
('HK-06-010224-004', N'Nguyễn Thanh Trúc',          '021206610922', 2006),

-- Ngày 31/01/2024
('HK-89-310124-001', N'Trần Nhật Linh',             '018189640110', 1989),
('HK-08-310124-002', N'Tống Trúc Như',              '049208280916', 2008),
('HK-04-310124-003', N'Phạm Văn Khang',             '069204360608', 2004),

-- Ngày 30/01/2024
('HK-95-300124-001', N'Lê Thị Mai Anh',             '047195470304', 1995),
('HK-10-300124-002', N'Trịnh Văn Hoàng',            '074210060708', 2010),
('HK-07-300124-003', N'Trần Hồng Phúc',             '014207500215', 2007),

-- Ngày 29/01/2024
('HK-02-290124-001', N'Nguyễn Thị Kim Ngân',        '033202710118', 2002),
('HK-90-290124-002', N'Trần Bảo Khang',             '025190450601', 1990),
('HK-06-290124-003', N'Nguyễn Ngọc Hân',            '021206190923', 2006),
('HK-89-290124-004', N'Trần Minh Hoàng',            '018189580630', 1989),

-- Ngày 28/01/2024
('HK-08-280124-001', N'Tống Văn Tài',               '049208980420', 2008),
('HK-04-280124-002', N'Phạm Nhật Huy',              '069204850619', 2004),
('HK-95-280124-003', N'Lê Đức Trí',                 '047195710204', 1995),

-- Ngày 27/01/2024
('HK-10-270124-001', N'Trịnh Tuấn Kiệt',            '074210890616', 2010),
('HK-07-270124-002', N'Trần Trúc Lam',              '014207910805', 2007),
('HK-02-270124-003', N'Nguyễn Trọng Đại',           '033202530330', 2002),
('HK-90-270124-004', N'Trần Thị Cẩm Tú',            '025190230928', 1990);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 26/01/2024
('HK-06-260124-001', N'Nguyễn Hữu Lâm',             '021206620113', 2006),
('HK-89-260124-002', N'Trần Văn Phúc',              '018189740925', 1989),
('HK-08-260124-003', N'Tống Thị Lan',               '049208340224', 2008),
('HK-04-260124-004', N'Phạm Thanh Huyền',           '069204960818', 2004),

-- Ngày 25/01/2024
('HK-95-250124-001', N'Lê Thị Mỹ Linh',             '047195390711', 1995),
('HK-10-250124-002', N'Trịnh Khánh Toàn',           '074210550917', 2010),
('HK-07-250124-003', N'Trần Minh Dũng',             '014207770121', 2007),

-- Ngày 24/01/2024
('HK-02-240124-001', N'Nguyễn Thị Thảo',            '033202360406', 2002),
('HK-90-240124-002', N'Trần Thành Công',            '025190310130', 1990),
('HK-06-240124-003', N'Nguyễn Phan Thịnh',          '021206880707', 2006),
('HK-89-240124-004', N'Trần Quang Huy',             '018189110415', 1989),

-- Ngày 23/01/2024
('HK-08-230124-001', N'Tống Văn Bình',              '049208610629', 2008),
('HK-04-230124-002', N'Phạm Mai Trang',             '069204570723', 2004),
('HK-95-230124-003', N'Lê Hữu Lộc',                 '047195350425', 1995),

-- Ngày 22/01/2024
('HK-10-220124-001', N'Trịnh Nhật Tân',             '074210170106', 2010),
('HK-07-220124-002', N'Trần Thị Bảo Ngọc',          '014207210317', 2007),
('HK-02-220124-003', N'Nguyễn Đức Hiếu',            '033202960216', 2002),

-- Ngày 21/01/2024
('HK-90-210124-001', N'Trần Văn Tài',               '025190890731', 1990),
('HK-06-210124-002', N'Nguyễn Minh Quân',           '021206090419', 2006),
('HK-89-210124-003', N'Trần Minh Nhật',             '018189850320', 1989),

-- Ngày 20/01/2024
('HK-08-200124-001', N'Tống Gia Huy',               '049208100827', 2008),
('HK-04-200124-002', N'Phạm Bảo Hân',               '069204240908', 2004),
('HK-95-200124-003', N'Lê Văn Cường',               '047195710113', 1995),

-- Ngày 19/01/2024
('HK-10-190124-001', N'Trịnh Thị Ngọc Hà',          '074210930812', 2010),
('HK-07-190124-002', N'Trần Hải Đăng',              '014207310614', 2007),
('HK-02-190124-003', N'Nguyễn Bảo Trân',            '033202460929', 2002),
('HK-90-190124-004', N'Trần Văn Sơn',               '025190240718', 1990),

-- Ngày 18/01/2024
('HK-06-180124-001', N'Nguyễn Hữu Nghĩa',           '021206690310', 2006),
('HK-89-180124-002', N'Trần Văn Lợi',               '018189350125', 1989),
('HK-08-180124-003', N'Tống Ngọc Lan',              '049208480612', 2008),
('HK-04-180124-004', N'Phạm Thanh Tùng',            '069204980417', 2004),

-- Ngày 17/01/2024
('HK-95-170124-001', N'Lê Thị Thanh',               '047195180909', 1995),
('HK-10-170124-002', N'Trịnh Đình Phúc',            '074210370304', 2010),
('HK-07-170124-003', N'Trần Quốc Anh',              '014207040510', 2007),

-- Ngày 16/01/2024
('HK-02-160124-001', N'Nguyễn Anh Thư',             '033202770122', 2002),
('HK-90-160124-002', N'Trần Kim Thành',             '025190950527', 1990),
('HK-06-160124-003', N'Nguyễn Ngọc Thanh',          '021206250424', 2006);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 15/01/2024
('HK-89-150124-001', N'Trần Đức Tài',               '018189470415', 1989),
('HK-08-150124-002', N'Tống Hoài Phương',           '049208590804', 2008),
('HK-04-150124-003', N'Phạm Văn Hòa',               '069204270320', 2004),
('HK-95-150124-004', N'Lê Thị Thu Trang',           '047195880128', 1995),
('HK-10-150124-005', N'Trịnh Thảo Vy',              '074210430806', 2010),

-- Ngày 14/01/2024
('HK-07-140124-001', N'Trần Gia Hưng',              '014207660722', 2007),
('HK-02-140124-002', N'Nguyễn Mai Chi',             '033202310103', 2002),
('HK-90-140124-003', N'Trần Thanh Tâm',             '025190660521', 1990),
('HK-06-140124-004', N'Nguyễn Hồng Phúc',           '021206150915', 2006),

-- Ngày 13/01/2024
('HK-89-130124-001', N'Trần Quốc Cường',            '018189570217', 1989),
('HK-08-130124-002', N'Tống Thị Mai',               '049208440630', 2008),
('HK-04-130124-003', N'Phạm Minh Trí',              '069204360425', 2004),
('HK-95-130124-004', N'Lê Đình Quang',              '047195520507', 1995),

-- Ngày 12/01/2024
('HK-10-120124-001', N'Trịnh Văn Hào',              '074210610904', 2010),
('HK-07-120124-002', N'Trần Thanh Mai',             '014207280322', 2007),
('HK-02-120124-003', N'Nguyễn Minh Khang',          '033202010907', 2002),
('HK-90-120124-004', N'Trần Khắc Duy',              '025190710601', 1990),

-- Ngày 11/01/2024
('HK-06-110124-001', N'Nguyễn Hải Hà',              '021206520420', 2006),
('HK-89-110124-002', N'Trần Tuấn Kiệt',             '018189290929', 1989),
('HK-08-110124-003', N'Tống Hoàng Nhật',            '049208360109', 2008),

-- Ngày 10/01/2024
('HK-04-100124-001', N'Phạm Thị Mỹ',                '069204810831', 2004),
('HK-95-100124-002', N'Lê Văn Tiến',                '047195610715', 1995),
('HK-10-100124-003', N'Trịnh Hoàng Nam',            '074210870227', 2010),
('HK-07-100124-004', N'Trần Hải My',                '014207590118', 2007),

-- Ngày 09/01/2024
('HK-02-090124-001', N'Nguyễn Trúc Linh',           '033202700925', 2002),
('HK-90-090124-002', N'Trần Quốc Hòa',              '025190330630', 1990),
('HK-06-090124-003', N'Nguyễn Minh Nhật',           '021206030412', 2006),
('HK-89-090124-004', N'Trần Thái Dương',            '018189680711', 1989),

-- Ngày 08/01/2024
('HK-08-080124-001', N'Tống Quốc Hưng',             '049208930606', 2008),
('HK-04-080124-002', N'Phạm Đức Lợi',               '069204440801', 2004),
('HK-95-080124-003', N'Lê Thị Thúy Hằng',           '047195490524', 1995),

-- Ngày 07/01/2024
('HK-10-070124-001', N'Trịnh Anh Tú',               '074210020815', 2010),
('HK-07-070124-002', N'Trần Quỳnh Chi',             '014207160201', 2007),
('HK-02-070124-003', N'Nguyễn Văn Mạnh',            '033202510505', 2002),

-- Ngày 06/01/2024
('HK-90-060124-001', N'Trần Minh Tâm',              '025190440401', 1990),
('HK-06-060124-002', N'Nguyễn Thảo Nhi',            '021206840719', 2006),
('HK-89-060124-003', N'Trần Hoài Bảo',              '018189170129', 1989),
('HK-08-060124-004', N'Tống Nguyên Bảo',            '049208150610', 2008);
Go

INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh) VALUES
-- Ngày 05/01/2024
('HK-04-050124-001', N'Phạm Văn Minh',              '069204250801', 2004),
('HK-95-050124-002', N'Lê Thị Mai Hương',           '047195310125', 1995),
('HK-10-050124-003', N'Trịnh Gia Hân',              '074210160927', 2010),
('HK-07-050124-004', N'Trần Hải Âu',                '014207180331', 2007),
('HK-02-050124-005', N'Nguyễn Quốc Bảo',            '033202360528', 2002),

-- Ngày 04/01/2024
('HK-90-040124-001', N'Trần Ngọc Diệp',             '025190270413', 1990),
('HK-06-040124-002', N'Nguyễn Bảo Trân',            '021206790914', 2006),
('HK-89-040124-003', N'Trần Văn Hưng',              '018189390311', 1989),

-- Ngày 03/01/2024
('HK-08-030124-001', N'Tống Huyền Anh',             '049208680227', 2008),
('HK-04-030124-002', N'Phạm Minh Quân',             '069204520619', 2004),
('HK-95-030124-003', N'Lê Gia Huy',                 '047195840802', 1995),
('HK-10-030124-004', N'Trịnh Hồng Nhung',           '074210450715', 2010),

-- Ngày 02/01/2024
('HK-07-020124-001', N'Trần Ngọc Hạnh',             '014207300620', 2007),
('HK-02-020124-002', N'Nguyễn Hữu Lộc',             '033202550214', 2002),
('HK-90-020124-003', N'Trần Văn Hữu',               '025190210904', 1990),
('HK-06-020124-004', N'Nguyễn Thị Ngân',            '021206330625', 2006),

-- Ngày 01/01/2024
('HK-89-010124-001', N'Trần Văn Thành',             '018189710912', 1989),
('HK-08-010124-002', N'Tống Văn Linh',              '049208310529', 2008),
('HK-04-010124-003', N'Phạm Hữu Trí',               '069204600318', 2004),
('HK-95-010124-004', N'Lê Quỳnh Trang',             '047195140419', 1995),
('HK-10-010124-005', N'Trịnh Đức Tài',              '074210660723', 2010),

-- Ngày 31/12/2023
('HK-07-311223-001', N'Trần Bảo Khang',             '014207860303', 2007),
('HK-02-311223-002', N'Nguyễn Duy Anh',             '033202150927', 2002),
('HK-90-311223-003', N'Trần Minh Trí',              '025190560519', 1990),
('HK-06-311223-004', N'Nguyễn Mỹ Linh',             '021206920830', 2006),

-- Ngày 30/12/2023
('HK-89-301223-001', N'Trần Hữu Nghĩa',             '018189120312', 1989),
('HK-08-301223-002', N'Tống Thanh Hằng',            '049208370423', 2008),
('HK-04-301223-003', N'Phạm Minh Khoa',             '069204950918', 2004),
('HK-95-301223-004', N'Lê Văn Đức',                 '047195580310', 1995),

-- Ngày 29/12/2023
('HK-10-291223-001', N'Trịnh Thái Sơn',             '074210780724', 2010),
('HK-07-291223-002', N'Trần Thanh Hà',              '014207230126', 2007),
('HK-02-291223-003', N'Nguyễn Phương Anh',          '033202820325', 2002),

-- Ngày 28/12/2023
('HK-90-281223-001', N'Trần Quốc Đạt',              '025190910830', 1990),
('HK-06-281223-002', N'Nguyễn Đăng Khoa',           '021206070206', 2006),
('HK-89-281223-003', N'Trần Gia Bảo',               '018189370417', 1989),

-- Ngày 27/12/2023
('HK-08-271223-001', N'Tống Thanh Tùng',            '049208230530', 2008),
('HK-04-271223-002', N'Phạm Hồng Sơn',              '069204870204', 2004),
('HK-95-271223-003', N'Lê Kim Anh',                 '047195460313', 1995),
('HK-10-271223-004', N'Trịnh Ngọc Trâm',            '074210350928', 2010);
Go

--Tạo dữ liệu ga

INSERT INTO Ga (maGa, tenGa, diaChi, soDienThoai) VALUES
('GA-001', N'An Hòa', N'Lý Thái Tổ, Phường An Hòa, Thành phố Huế, Tỉnh Thừa Thiên Huế', '0987654321'), -- :contentReference[oaicite:0]{index=0}
('GA-002', N'Ấm Thượng', N'Khu 8, Thị trấn Hạ Hòa, Huyện Hạ Hòa, Tỉnh Phú Thọ', '0912345678'), -- :contentReference[oaicite:1]{index=1}
('GA-003', N'Bảo Sơn', N'Xã Bảo Sơn, Huyện Lục Nam, Tỉnh Bắc Giang', '0978123456'), -- :contentReference[oaicite:2]{index=2}
('GA-004', N'Bắc Thủy', N'Quốc Lộ 1 Cũ, Xã Bắc Thủy, Huyện Chi Lăng, Tỉnh Lạng Sơn', '0901234567'), -- :contentReference[oaicite:3]{index=3}
('GA-005', N'Bàn Cờ', N'T36 Khu 10 – Quang Trung, Thị Xã Uông Bí, Tỉnh Quảng Ninh', '0965432187'), -- :contentReference[oaicite:4]{index=4}
('GA-006', N'Bắc Giang', N'Xương Giang, Phường Trần Phú, Thành phố Bắc Giang, Tỉnh Bắc Giang', '0938765432'), -- :contentReference[oaicite:5]{index=5}
('GA-007', N'Bảo Hà', N'Xã Bảo Hà, Huyện Bảo Yên, Tỉnh Lào Cai', '0923456789'), -- :contentReference[oaicite:6]{index=6}
('GA-008', N'Biên Hòa', N'Đường Hưng Đạo Vương, Phường Trung Dũng, Thành phố Biên Hòa, Tỉnh Đồng Nai', '0911987654'), -- :contentReference[oaicite:7]{index=7}
('GA-009', N'Bắc Lệ', N'Thôn Bắc Lệ, Xã Tân Thành, Huyện Hữu Lũng, Tỉnh Lạng Sơn', '0943210987'), -- :contentReference[oaicite:8]{index=8}
('GA-010', N'Bắc Ninh', N'Phường Ninh Xá, Thành phố Bắc Ninh, Tỉnh Bắc Ninh', '0954321098'), -- :contentReference[oaicite:9]{index=9}
('GA-011', N'Bồng Sơn', N'Quốc lộ 1, Khu phố 1, Phường Bồng Sơn, Thị xã Hoài Nhơn, Tỉnh Bình Định', '0932109876'), -- :contentReference[oaicite:10]{index=10}
('GA-012', N'Bỉm Sơn', N'Thị xã Bỉm Sơn, Tỉnh Thanh Hóa', '0921098765'), -- :contentReference[oaicite:11]{index=11}
('GA-013', N'Bản Thí', N'Xã Vân Thủy, Huyện Chi Lăng, Tỉnh Lạng Sơn', '0913456789'), -- :contentReference[oaicite:12]{index=12}
('GA-014', N'Chí Chủ', N'Số 312, Xã Chí Tiên, Huyện Thanh Ba, Tỉnh Phú Thọ', '0976543210'), -- :contentReference[oaicite:13]{index=13}
('GA-015', N'Cẩm Giàng', N'ĐT388, Xã Kim Giang, Huyện Cẩm Giàng, Tỉnh Hải Dương', '0986543212'), -- :contentReference[oaicite:14]{index=14}
('GA-016', N'Cầu Giát', N'Xã Quỳnh Giang, Huyện Quỳnh Lưu, Tỉnh Nghệ An', '0967891234'),
('GA-017', N'Cầu Hai', N'Xã Lộc Trì, Huyện Phú Lộc, Tỉnh Thừa Thiên Huế', '0953217890'),
('GA-018', N'Chi Lăng', N'Xã Chi Lăng, Huyện Chi Lăng, Tỉnh Lạng Sơn', '0905678912'),
('GA-019', N'Chu Lễ', N'Xã Chu Lễ, Huyện Nam Giang, Tỉnh Quảng Nam', '0987123456'),
('GA-020', N'Chí Linh', N'Phường Cộng Hòa, Thành phố Chí Linh', '0906655443'),
('GA-021', N'Cổ Loa', N'Xã Việt Hùng, Huyện Đông Anh, Thành phố Hà Nội', '0912345678'), -- :contentReference[oaicite:0]{index=0}
('GA-022', N'Cẩm Lý', N'Xã Cẩm Lý, Huyện Lục Nam, Tỉnh Bắc Giang', '0923456789'), -- :contentReference[oaicite:1]{index=1}
('GA-023', N'Cà Ná', N'Xã Cà Ná, Huyện Thuận Nam, Tỉnh Ninh Thuận', '0934567890'), -- :contentReference[oaicite:2]{index=2}
('GA-024', N'Cổ Phúc', N'Thị trấn Cổ Phúc, Huyện Trấn Yên, Tỉnh Yên Bái', '0945678901'), -- :contentReference[oaicite:3]{index=3}
('GA-025', N'Chợ Sy', N'Xã Diễn Kỷ, Huyện Diễn Châu, Tỉnh Nghệ An', '0956789012'), -- :contentReference[oaicite:4]{index=4}
('GA-026', N'Đông Anh', N'Thị trấn Đông Anh, Huyện Đông Anh, Thành phố Hà Nội', '0967890123'), -- :contentReference[oaicite:5]{index=5}
('GA-027', N'Đa Phúc', N'Xã Mai Đình, Huyện Sóc Sơn, Thành phố Hà Nội', '0978901234'), -- :contentReference[oaicite:6]{index=6}
('GA-028', N'Đà Lạt', N'Số 1 Quang Trung, Phường 10, Thành phố Đà Lạt, Tỉnh Lâm Đồng', '0989012345'), -- :contentReference[oaicite:7]{index=7}
('GA-029', N'Đồng Chuối', N'Xã Phước Hưng, Huyện Tuy Phước, Tỉnh Bình Định', '0990123456'), -- :contentReference[oaicite:8]{index=8}
('GA-030', N'Đồng Đăng', N'Thị trấn Đồng Đăng, Huyện Cao Lộc, Tỉnh Lạng Sơn', '0901234567'), -- :contentReference[oaicite:9]{index=9}
('GA-031', N'Đồng Hà', N'2 Lê Thánh Tôn, Phường 1, Thành phố Đông Hà, Tỉnh Quảng Trị', '0912345678'), -- :contentReference[oaicite:0]{index=0}
('GA-032', N'Đồng Hới', N'Thành phố Đồng Hới, Tỉnh Quảng Bình', '0923456789'), -- :contentReference[oaicite:1]{index=1}
('GA-033', N'Dĩ An', N'Thị xã Dĩ An, Tỉnh Bình Dương', '0934567890'), -- :contentReference[oaicite:2]{index=2}
('GA-034', N'Đại Lãnh', N'Xã Đại Lãnh, Huyện Vạn Ninh, Tỉnh Khánh Hòa', '0945678901'), -- :contentReference[oaicite:3]{index=3}
('GA-035', N'Đồng Lê', N'Huyện Tuyên Hóa, Tỉnh Quảng Bình', '0956789012'), -- :contentReference[oaicite:4]{index=4}
('GA-036', N'Đồng Mỏ', N'Thị trấn Đồng Mỏ, Huyện Chi Lăng, Tỉnh Lạng Sơn', '0967890123'), -- :contentReference[oaicite:5]{index=5}
('GA-037', N'Đà Nẵng', N'Thành phố Đà Nẵng', '0978901234'), -- :contentReference[oaicite:6]{index=6}
('GA-038', N'Đông Triều', N'Hà Lôi Hạ, Thị xã Đông Triều, Tỉnh Quảng Ninh', '0989012345'), -- :contentReference[oaicite:7]{index=7}
('GA-039', N'Đức Phổ', N'Phổ Ninh, Thị xã Đức Phổ, Tỉnh Quảng Ngãi', '0990123456'), -- :contentReference[oaicite:8]{index=8}
('GA-040', N'Diên Sanh', N'Huyện Hải Lăng, Tỉnh Quảng Trị', '0901234567'), -- :contentReference[oaicite:9]{index=9}
('GA-041', N'Đông Tác', N'Phường Phú Thạnh, Thành phố Tuy Hòa, Tỉnh Phú Yên', '0912345678'), -- :contentReference[oaicite:10]{index=10}
('GA-042', N'Đoan Thượng', N'Xã Đan Thượng, Huyện Hạ Hòa, Tỉnh Phú Thọ', '0923456789'), -- :contentReference[oaicite:11]{index=11}
('GA-043', N'Diêu Trì', N'Thôn Vân Hội 2, Thị trấn Diêu Trì, Huyện Tuy Phước, Tỉnh Bình Định', '0934567890'), -- :contentReference[oaicite:12]{index=12}
('GA-044', N'Đức Lạc', N'Huyện Đức Thọ, Tỉnh Hà Tĩnh', '0945678901'), -- :contentReference[oaicite:13]{index=13}
('GA-045', N'Giáp Bát', N'366 Giải Phóng, Phường Định Công, Quận Hoàng Mai, Thành phố Hà Nội', '0956789012'), -- :contentReference[oaicite:14]{index=14}
('GA-046', N'Gia Huynh', N'Xã Gia Huynh, Huyện Tánh Linh, Tỉnh Bình Thuận', '0967890123'),
('GA-047', N'Giã', N'Xã Phước Thành, Huyện Tuy Phước, Tỉnh Bình Định', '0978901234'),
('GA-048', N'Gia Lâm', N'Phường Gia Thụy, Quận Long Biên, Thành phố Hà Nội', '0989012345'),
('GA-049', N'Gia Ray', N'Thị trấn Gia Ray, Huyện Xuân Lộc, Tỉnh Đồng Nai', '0912345678'),
('GA-050', N'Hải Dương', N'Phường Quang Trung, Thành phố Hải Dương, Tỉnh Hải Dương', '0923456789'),
('GA-051', N'Hoàn Lão', N'Thị trấn Hoàn Lão, Huyện Bố Trạch, Tỉnh Quảng Bình', '0934567890'),
('GA-052', N'Hạ Long', N'Phường Bãi Cháy, Thành phố Hạ Long, Tỉnh Quảng Ninh', '0945678901'),
('GA-053', N'Hà Nội', N'120 Lê Duẩn, Phường Cửa Nam, Quận Hoàn Kiếm, Thành phố Hà Nội', '0956789012'), -- [https://vi.wikipedia.org/wiki/Ga_H%C3%A0_N%E1%BB%99i]
('GA-054', N'Hòa Duyệt', N'Xã Đức Hòa, Huyện Đức Thọ, Tỉnh Hà Tĩnh', '0967890123'),
('GA-055', N'Hải Phòng', N'Số 75 Lương Khánh Thiện, Quận Ngô Quyền, Thành phố Hải Phòng', '0978901234'), -- [https://vi.wikipedia.org/wiki/Ga_H%E1%BA%A3i_Ph%C3%B2ng]
('GA-056', N'Hương Phố', N'Thị trấn Hương Khê, Huyện Hương Khê, Tỉnh Hà Tĩnh', '0912345678'),
('GA-057', N'Hiền Sỹ', N'Xã Phong Sơn, Huyện Phong Điền, Tỉnh Thừa Thiên Huế', '0923456789'),
('GA-058', N'Hà Thanh', N'Xã Nhơn Phong, Thị xã An Nhơn, Tỉnh Bình Định', '0934567890'),
('GA-059', N'Huế', N'02 Bùi Thị Xuân, Phường Phường Đúc, Thành phố Huế, Tỉnh Thừa Thiên Huế', '0945678901'), -- [https://vi.wikipedia.org/wiki/Ga_Hu%E1%BA%BF]
('GA-060', N'Kép', N'Thị trấn Kép, Huyện Lạng Giang, Tỉnh Bắc Giang', '0956789012'),
('GA-061', N'Kim Liên', N'Phường Hòa Hiệp Bắc, Quận Liên Chiểu, Thành phố Đà Nẵng', '0967890123'),
('GA-062', N'Kim Lũ', N'Xã Kim Hóa, Huyện Tuyên Hóa, Tỉnh Quảng Bình', '0912345678'),
('GA-063', N'Lạc Giao', N'Phường Tự An, Thành phố Buôn Ma Thuột, Tỉnh Đắk Lắk', '0923456789'),
('GA-064', N'La Khê', N'Phường La Khê, Quận Hà Đông, Thành phố Hà Nội', '0934567890'),
('GA-065', N'Lạc Sơn', N'Xã Châu Thái, Huyện Quỳ Hợp, Tỉnh Nghệ An', '0945678901'),
('GA-066', N'Long Biên', N'Phường Đồng Xuân, Quận Hoàn Kiếm, Thành phố Hà Nội', '0956789012'),
('GA-067', N'Lào Cai', N'Phường Phố Mới, Thành phố Lào Cai, Tỉnh Lào Cai', '0967890123'),
('GA-068', N'Lăng Cô', N'Thị trấn Lăng Cô, Huyện Phú Lộc, Tỉnh Thừa Thiên Huế', '0978901234'),
('GA-069', N'Long Đại', N'Xã Hiền Ninh, Huyện Quảng Ninh, Tỉnh Quảng Bình', '0989012345'),
('GA-070', N'Lệ Sơn', N'Xã Văn Hóa, Huyện Tuyên Hóa, Tỉnh Quảng Bình', '0990123456'),
('GA-071', N'Lâm Giang', N'Xã Lâm Giang, Huyện Văn Yên, Tỉnh Yên Bái', '0901234567'),
('GA-072', N'La Hai', N'Thị trấn La Hai, Huyện Đồng Xuân, Tỉnh Phú Yên', '0912345678'),
('GA-073', N'Lim', N'Thị trấn Lim, Huyện Tiên Du, Tỉnh Bắc Ninh', '0923456789'),
('GA-074', N'Lang Khay', N'Xã Sơn Hà, Huyện Hữu Lũng, Tỉnh Lạng Sơn', '0934567890'),
('GA-075', N'Long Khánh', N'Phường Xuân An, Thành phố Long Khánh, Tỉnh Đồng Nai', '0945678901'),
('GA-076', N'Lệ Kỳ', N'Xã Vĩnh Ninh, Huyện Quảng Ninh, Tỉnh Quảng Bình', '0956789012'),
('GA-077', N'Lan Mẫu', N'Xã Lan Mẫu, Huyện Lục Nam, Tỉnh Bắc Giang', '0967890123'),
('GA-078', N'Lạng Sơn', N'Phường Chi Lăng, Thành phố Lạng Sơn, Tỉnh Lạng Sơn', '0978901234'),
('GA-079', N'Lang Thíp', N'Xã Lang Thíp, Huyện Văn Yên, Tỉnh Yên Bái', '0989012345'),
('GA-080', N'Lương Sơn', N'Thị trấn Lương Sơn, Huyện Lương Sơn, Tỉnh Hòa Bình', '0990123456'),
('GA-081', N'Lưu Xá', N'Phường Phú Xá, Thành phố Thái Nguyên, Tỉnh Thái Nguyên', '0901234567'),
('GA-082', N'Mậu A', N'Thị trấn Mậu A, Huyện Văn Yên, Tỉnh Yên Bái', '0912345678'),
('GA-083', N'Mạo Khê', N'Phường Mạo Khê, Thị xã Đông Triều, Tỉnh Quảng Ninh', '0923456789'),
('GA-084', N'Minh Cầm', N'Xã Xuân Ninh, Huyện Quảng Ninh, Tỉnh Quảng Bình', '0934567890'),
('GA-085', N'Mỹ Chánh', N'Xã Hải Chánh, Huyện Hải Lăng, Tỉnh Quảng Trị', '0945678901'),
('GA-086', N'Mỹ Đức', N'Xã Bình Mỹ, Huyện Bình Sơn, Tỉnh Quảng Ngãi', '0956789012'),
('GA-087', N'Minh Khôi', N'Xã Minh Khôi, Huyện Nông Cống, Tỉnh Thanh Hóa', '0967890123'), -- :contentReference[oaicite:0]{index=0}
('GA-088', N'Ma Lâm', N'Thị trấn Ma Lâm, Huyện Hàm Thuận Bắc, Tỉnh Bình Thuận', '0978901234'),
('GA-089', N'Minh Lệ', N'Xã Quảng Minh, Thị xã Ba Đồn, Tỉnh Quảng Bình', '0989012345'),
('GA-090', N'Bình Thuận', N'Xã Mương Mán, Huyện Hàm Thuận Nam, Tỉnh Bình Thuận', '0990123456'),
('GA-091', N'Mỹ Trạch', N'Xã Mỹ Trạch, Huyện Bố Trạch, Tỉnh Quảng Bình', '0901234567'),
('GA-092', N'Ngã Ba', N'Xã Hưng Thủy, Huyện Lệ Thủy, Tỉnh Quảng Bình', '0912345678'),
('GA-093', N'Ninh Bình', N'Phường Nam Thành, Thành phố Ninh Bình, Tỉnh Ninh Bình', '0923456789'),
('GA-094', N'Nam Định', N'Phường Quang Trung, Thành phố Nam Định, Tỉnh Nam Định', '0934567890'),
('GA-095', N'Ninh Hòa', N'Phường Ninh Hiệp, Thị xã Ninh Hòa, Tỉnh Khánh Hòa', '0945678901'),
('GA-096', N'Ngòi Hóp', N'Xã Văn Phú, Thành phố Yên Bái, Tỉnh Yên Bái', '0956789012'),
('GA-097', N'Nam Khê', N'Phường Nam Khê, Thành phố Uông Bí, Tỉnh Quảng Ninh', '0967890123'),
('GA-098', N'Ngọc Lâm', N'Xã Ngọc Lâm, Huyện Mỹ Hào, Tỉnh Hưng Yên', '0978901234'),
('GA-099', N'Ngân Sơn', N'Thị trấn Nà Phặc, Huyện Ngân Sơn, Tỉnh Bắc Kạn', '0989012345'),
('GA-100', N'Núi Thành', N'Thị trấn Núi Thành, Huyện Núi Thành, Tỉnh Quảng Nam', '0990123456'),
('GA-101', N'Nha Trang', N'17 Thái Nguyên, Phường Phước Tân, Thành phố Nha Trang, Tỉnh Khánh Hòa', '0901234567'), -- :contentReference[oaicite:1]{index=1}
('GA-102', N'Phú Cang', N'Xã Vạn Phú, Huyện Vạn Ninh, Tỉnh Khánh Hòa', '0912345678'),
('GA-103', N'Phú Diễn', N'Phường Phú Diễn, Quận Bắc Từ Liêm, Thành phố Hà Nội', '0923456789'),
('GA-104', N'Phủ Đức', N'Xã Phước Thạnh, Huyện Củ Chi, Thành phố Hồ Chí Minh', '0934567890'),
('GA-105', N'Phú Hiệp', N'Xã Hòa Hiệp Trung, Thị xã Đông Hòa, Tỉnh Phú Yên', '0945678901'),
('GA-106', N'Phú Hòa', N'Xã Phú Hòa, Huyện Định Quán, Tỉnh Đồng Nai', '0956789012'),
('GA-107', N'Phò Trạch', N'Xã Phong Bình, Huyện Phong Điền, Tỉnh Thừa Thiên Huế', '0967890123'),
('GA-108', N'Phổ Yên', N'Phường Ba Hàng, Thị xã Phổ Yên, Tỉnh Thái Nguyên', '0978901234'),
('GA-109', N'Phước Lãnh', N'Xã Phước Lãnh, Huyện Tuy Phước, Tỉnh Bình Định', '0989012345'),
('GA-110', N'Phố Lu', N'Thị trấn Phố Lu, Huyện Bảo Thắng, Tỉnh Lào Cai', '0990123456'),
('GA-111', N'Phủ Lý', N'Phường Minh Khai, Thành phố Phủ Lý, Tỉnh Hà Nam', '0901234567'),
('GA-112', N'Phường Mỗ', N'Phường Mỗ Lao, Quận Hà Đông, Thành phố Hà Nội', '0912345678'),
('GA-113', N'Phú Thái', N'Thị trấn Phú Thái, Huyện Kim Thành, Tỉnh Hải Dương', '0923456789'),
('GA-114', N'Phan Thiết', N'Phường Phong Nẫm, Thành phố Phan Thiết, Tỉnh Bình Thuận', '0934567890'),
('GA-115', N'Phú Thọ', N'Thị trấn Phú Thọ, Huyện Phù Ninh, Tỉnh Phú Thọ', '0945678901'),
('GA-116', N'Phố Tráng', N'Xã Yên Trạch, Huyện Phú Lương, Tỉnh Thái Nguyên', '0956789012'),
('GA-117', N'Phúc Tự', N'Xã Phúc Tự, Huyện Tân Yên, Tỉnh Bắc Giang', '0967890123'),
('GA-118', N'Phúc Trạch', N'Xã Phúc Trạch, Huyện Bố Trạch, Tỉnh Quảng Bình', '0978901234'),
('GA-119', N'Phố Vị', N'Thị trấn Phố Vị, Huyện Hữu Lũng, Tỉnh Lạng Sơn', '0989012345'),
('GA-120', N'Phúc Yên', N'Phường Phúc Thắng, Thành phố Phúc Yên, Tỉnh Vĩnh Phúc', '0990123456'),
('GA-121', N'Quảng Ngãi', N'Phường Quảng Phú, Thành phố Quảng Ngãi, Tỉnh Quảng Ngãi', '0901234567'),
('GA-122', N'Quán Hành', N'Xã Nghi Trung, Huyện Nghi Lộc, Tỉnh Nghệ An', '0934567890'),
('GA-123', N'Quán Toan', N'Phường Quán Toan, Quận Hồng Bàng, Thành phố Hải Phòng', '0945678901'),
('GA-124', N'Quy Nhơn', N'Phường Trần Hưng Đạo, Thành phố Quy Nhơn, Tỉnh Bình Định', '0912345678'),
('GA-125', N'Quảng Trị', N'Phường 1, Thị xã Quảng Trị, Tỉnh Quảng Trị', '0923456789'),
('GA-126', N'Quán Triều', N'Phường Quán Triều, Thành phố Thái Nguyên, Tỉnh Thái Nguyên', '0934567890'),
('GA-127', N'Sài Gòn', N'01 Nguyễn Thông, Phường 9, Quận 3, Thành phố Hồ Chí Minh', '0945678901'),
('GA-128', N'Sen Hồ', N'Xã Hương Mạc, Thị xã Từ Sơn, Tỉnh Bắc Ninh', '0956789012'),
('GA-129', N'Suối Kiết', N'Xã Suối Kiết, Huyện Tánh Linh, Tỉnh Bình Thuận', '0967890123'),
('GA-130', N'Sa Lung', N'Xã Vĩnh Long, Huyện Vĩnh Linh, Tỉnh Quảng Trị', '0978901234'),
('GA-131', N'Sông Mao', N'Xã Phan Tiến, Huyện Bắc Bình, Tỉnh Bình Thuận', '0989012345'),
('GA-132', N'Sông Hóa', N'Xã Quỳnh Vinh, Thị xã Hoàng Mai, Tỉnh Nghệ An', '0990123456'),
('GA-133', N'Sóng Thần', N'Phường Dĩ An, Thành phố Dĩ An, Tỉnh Bình Dương', '0901234567'),
('GA-134', N'Tiên An', N'Xã Tiên An, Huyện Tiên Phước, Tỉnh Quảng Nam', '0912345678'),
('GA-135', N'Tân Ấp', N'Xã Hương Hóa, Huyện Tuyên Hóa, Tỉnh Quảng Bình', '0923456789'),
('GA-136', N'Tu Bông', N'Xã Vạn Phước, Huyện Vạn Ninh, Tỉnh Khánh Hòa', '0934567890'),
('GA-137', N'Thị Cầu', N'Phường Thị Cầu, Thành phố Bắc Ninh, Tỉnh Bắc Ninh', '0945678901'),
('GA-138', N'Tháp Chàm', N'Phường Đô Vinh, Thành phố Phan Rang-Tháp Chàm, Tỉnh Ninh Thuận', '0956789012'),
('GA-139', N'Trung Giã', N'Xã Trung Giã, Huyện Sóc Sơn, Thành phố Hà Nội', '0967890123'),
('GA-140', N'Tuy Hòa', N'Phường 9, Thành phố Tuy Hòa, Tỉnh Phú Yên', '0978901234'),
('GA-141', N'Thượng Lý', N'Phường Sở Dầu, Quận Hồng Bàng, Thành phố Hải Phòng', '0989012345'),
('GA-142', N'Thanh Hóa', N'Phường Đông Thọ, Thành phố Thanh Hóa, Tỉnh Thanh Hóa', '0990123456'),
('GA-143', N'Trái Hút', N'Xã Yên Hưng, Huyện Văn Yên, Tỉnh Yên Bái', '0901234567'),
('GA-144', N'Tiên Kiên', N'Xã Tiên Kiên, Huyện Lâm Thao, Tỉnh Phú Thọ', '0912345678'),
('GA-145', N'Trà Kiệu', N'Xã Duy Sơn, Huyện Duy Xuyên, Tỉnh Quảng Nam', '0923456789'),
('GA-146', N'Tam Kỳ', N'Phường An Sơn, Thành phố Tam Kỳ, Tỉnh Quảng Nam', '0934567890'),
('GA-147', N'Thượng Lâm', N'Xã Thượng Lâm, Huyện Mỹ Đức, Thành phố Hà Nội', '0945678901'),
('GA-148', N'Thọ Lộc', N'Xã Thọ Lộc, Huyện Phúc Thọ, Thành phố Hà Nội', '0956789012'),
('GA-149', N'Thạch Lỗi', N'Xã Thạch Lỗi, Huyện Mê Linh, Thành phố Hà Nội', '0923456789'),
('GA-150', N'Thanh Luyện', N'Xã Thanh Luyện, Huyện Thanh Liêm, Tỉnh Hà Nam', '0934567890'),
('GA-151', N'Trại Mát', N'Phường 11, Thành phố Đà Lạt, Tỉnh Lâm Đồng', '0945678901'),
('GA-152', N'Thái Nguyên', N'Phường Quang Trung, Thành phố Thái Nguyên, Tỉnh Thái Nguyên', '0956789012'),
('GA-153', N'Thái Niên', N'Xã Thái Niên, Huyện Bảo Thắng, Tỉnh Lào Cai', '0967890123'),
('GA-154', N'Từ Sơn', N'Phường Đông Ngàn, Thị xã Từ Sơn, Tỉnh Bắc Ninh', '0978901234'),
('GA-155', N'Thái Văn', N'Xã Thái Văn, Huyện Thái Thụy, Tỉnh Thái Bình', '0989012345'),
('GA-156', N'Uông Bí', N'Phường Quang Trung, Thành phố Uông Bí, Tỉnh Quảng Ninh', '0990123456'),
('GA-157', N'Vân Canh', N'Xã Vân Canh, Huyện Hoài Đức, Thành phố Hà Nội', '0901234567'),
('GA-158', N'Văn Điển', N'Thị trấn Văn Điển, Huyện Thanh Trì, Thành phố Hà Nội', '0912345678'),
('GA-159', N'Vũ Ẻn', N'Xã Vũ Ẻn, Huyện Thanh Ba, Tỉnh Phú Thọ', '0923456789'),
('GA-160', N'Vinh', N'Phường Quán Bàu, Thành phố Vinh, Tỉnh Nghệ An', '0934567890'),
('GA-161', N'Văn Phú', N'Xã Văn Phú, Huyện Thường Tín, Thành phố Hà Nội', '0945678901'),
('GA-162', N'Vĩnh Thủy', N'Xã Vĩnh Thủy, Huyện Vĩnh Linh, Tỉnh Quảng Trị', '0956789012'),
('GA-163', N'Việt Trì', N'Phường Tiên Cát, Thành phố Việt Trì, Tỉnh Phú Thọ', '0967890123'),
('GA-164', N'Văn Xá', N'Xã Văn Xá, Huyện Kim Bảng, Tỉnh Hà Nam', '0978901234'),
('GA-165', N'Voi Xô', N'Xã Voi Xô, Huyện Sơn Động, Tỉnh Bắc Giang', '0989012345'),
('GA-166', N'Vĩnh Yên', N'Phường Liên Bảo, Thành phố Vĩnh Yên, Tỉnh Vĩnh Phúc', '0990123456'),
('GA-167', N'Yên Bái', N'Phường Yên Ninh, Thành phố Yên Bái, Tỉnh Yên Bái', '0901234567');
Go

--Tạo dữ liệu thời gian di chuyển giữa 2 ga

CREATE PROCEDURE sp_TaoThoiGianDiChuyen1
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM ThoiGianDiChuyen;

    INSERT INTO ThoiGianDiChuyen (
        maThoiGianDiChuyen,
        maGaDi,
        maGaDen,
        thoiGianDiChuyen,
        soKmDiChuyen,
        soTienMotKm
    )
    SELECT 
        'TGDC-' + ga1.maGa + '-' + ga2.maGa,
        ga1.maGa,
        ga2.maGa,
        CAST((ABS(CHECKSUM(NEWID())) % 600 + 60) AS INT),
        ROUND(ABS(CHECKSUM(NEWID())) % 900 + 50, 1),
        600
    FROM Ga ga1
    CROSS JOIN Ga ga2
    WHERE ga1.maGa <> ga2.maGa;
END;
Go

EXEC sp_TaoThoiGianDiChuyen1;
Go

INSERT INTO Tau (maTau, tenTau, soToaTau) VALUES
('TAU-001', N'Tàu Thống Nhất SE1', 8),
('TAU-002', N'Tàu Thống Nhất SE2', 6),
('TAU-003', N'Tàu Thống Nhất SE3', 7),
('TAU-004', N'Tàu Thống Nhất SE4', 8),
('TAU-005', N'Tàu Thống Nhất SE5', 6),
('TAU-006', N'Tàu Thống Nhất SE6', 7),
('TAU-007', N'Tàu Thống Nhất SE7', 6),
('TAU-008', N'Tàu Thống Nhất SE8', 6),
('TAU-009', N'Tàu TN1', 7),
('TAU-010', N'Tàu TN2', 6),
('TAU-011', N'Tàu Sài Gòn – Nha Trang SNT1', 8),
('TAU-012', N'Tàu Sài Gòn – Nha Trang SNT2', 6),
('TAU-013', N'Tàu Hà Nội – Lào Cai SP1', 8),
('TAU-014', N'Tàu Hà Nội – Lào Cai SP2', 7),
('TAU-015', N'Tàu Hà Nội – Vinh NA1', 6),
('TAU-016', N'Tàu Hà Nội – Vinh NA2', 6),
('TAU-017', N'Tàu Thống Nhất SE9', 7),
('TAU-018', N'Tàu Thống Nhất SE10', 6),
('TAU-019', N'Tàu Thống Nhất SE11', 8),
('TAU-020', N'Tàu Thống Nhất SE12', 6);
Go

--Tạo dữ liệu cho bảng Toa tàu

CREATE PROCEDURE sp_ThemToaTau
AS
BEGIN
    DECLARE @maTau VARCHAR(10);
    DECLARE @soToaTau INT;
    DECLARE @i INT;
    DECLARE @maToaTau VARCHAR(10);
    DECLARE @tenToaTau NVARCHAR(100);
    DECLARE @soKhoangTau INT;
    DECLARE @mt VARCHAR(3);
    
    -- Lấy danh sách các tàu từ bảng Tau
    DECLARE cur CURSOR FOR
    SELECT maTau, soToaTau
    FROM Tau;

    OPEN cur;
    FETCH NEXT FROM cur INTO @maTau, @soToaTau;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Debug: Kiểm tra giá trị của @maTau và @soToaTau
        PRINT 'Mã tàu: ' + @maTau + ', Số toa: ' + CAST(@soToaTau AS VARCHAR);

        -- Lấy 3 ký tự cuối của mã tàu
        SET @mt = RIGHT(@maTau, 3);
        
        -- Tạo toa cho mỗi tàu
        SET @i = 1;
        WHILE @i <= @soToaTau
        BEGIN
            -- Tạo mã toa theo định dạng TT-MT-OO
            SET @maToaTau = 'TT-' + @mt + '-' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2);
            
            -- Tên toa tàu (có thể tùy chỉnh thêm theo logic)
            SET @tenToaTau = N'Toa ' + CAST(@i AS VARCHAR(3)) + N' của tàu ' + @maTau;
            
            -- Số khoang của toa (random từ 5 đến 10 khoang)
            SET @soKhoangTau = 6;
            
            -- Insert vào bảng ToaTau
            INSERT INTO ToaTau (maToaTau, tenToaTau, soKhoangTau, maTau)
            VALUES (@maToaTau, @tenToaTau, @soKhoangTau, @maTau);
            
            SET @i = @i + 1;
        END

        FETCH NEXT FROM cur INTO @maTau, @soToaTau;
    END

    CLOSE cur;
    DEALLOCATE cur;
END;
Go

EXEC sp_ThemToaTau;
Go

CREATE PROCEDURE sp_ThemKhoangTau
AS
BEGIN
    DECLARE @maToaTau VARCHAR(10);
    DECLARE @soKhoangTau INT;
    DECLARE @i INT;
    DECLARE @maKhoangTau VARCHAR(13);
    DECLARE @tenKhoangTau NVARCHAR(100);
    DECLARE @soGhe INT;
    DECLARE @mtt VARCHAR(6);
    DECLARE @soKhoang8 INT;
    DECLARE @soKhoang6 INT;
    DECLARE @soKhoang4 INT;
	DECLARE @suffix VarChar(2);

    -- Lấy danh sách các toa tàu từ bảng ToaTau
    DECLARE cur CURSOR FOR
    SELECT maToaTau, soKhoangTau
    FROM ToaTau;

    OPEN cur;
    FETCH NEXT FROM cur INTO @maToaTau, @soKhoangTau;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Lấy 6 ký tự cuối của mã toa tàu
        SET @mtt = RIGHT(@maToaTau, 6);
        
        SET @suffix = RIGHT(@maToaTau, 2);

        -- Xác định số ghế của các khoang dựa trên 2 ký tự cuối của mã toa tàu
        IF @suffix IN ('01', '02', '03')
            SET @soGhe = 8;
        ELSE IF @suffix IN ('04', '05')
            SET @soGhe = 6;
        ELSE
            SET @soGhe = 4;
        -- Tạo khoang cho mỗi toa
        SET @i = 1;
        WHILE @i <= @soKhoangTau
        BEGIN
            -- Tạo mã khoang tàu theo định dạng KT-MTT-OO
            SET @maKhoangTau = 'KT-' + @mtt + '-' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2);
            
            -- Tên khoang tàu (có thể tùy chỉnh theo logic)
            SET @tenKhoangTau = N'Khoang ' + CAST(@i AS VARCHAR(3)) + N' của toa ' + @maToaTau;

            -- Insert vào bảng KhoangTau
            INSERT INTO KhoangTau (maKhoangTau, tenKhoangTau, soGhe, maToaTau)
            VALUES (@maKhoangTau, @tenKhoangTau, @soGhe, @maToaTau);
            
            SET @i = @i + 1;
        END

        FETCH NEXT FROM cur INTO @maToaTau, @soKhoangTau;
    END

    CLOSE cur;
    DEALLOCATE cur;
END;
GO

EXEC sp_ThemKhoangTau;
Go

CREATE PROCEDURE sp_ThemGhe
AS
BEGIN
    DECLARE @maKhoangTau VARCHAR(20);
    DECLARE @soGhe INT;
    DECLARE @maLoaiGhe VARCHAR(20);
    DECLARE @i INT;
    DECLARE @hang INT;
    DECLARE @cot INT;
    DECLARE @viTri VARCHAR(20);
    DECLARE @maGhe VARCHAR(30);
    DECLARE @mkt VARCHAR(20);
    DECLARE @maToaHienTai VARCHAR(20);
    DECLARE @sttGhe INT;

    -- Cursor duyệt qua các khoang tàu, sắp xếp theo mã toa tàu để ghép đúng thứ tự
    DECLARE cur CURSOR FOR
    SELECT maKhoangTau, soGhe
    FROM KhoangTau
    ORDER BY SUBSTRING(maKhoangTau, 4, 6), maKhoangTau;

    OPEN cur;
    FETCH NEXT FROM cur INTO @maKhoangTau, @soGhe;

    SET @maToaHienTai = '';
    SET @sttGhe = 1; -- bắt đầu đánh số ghế từ 1

    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Lấy phần mã toa từ maKhoangTau (giả định ký tự 8 đến 12 là mã toa)
        DECLARE @maToaHienThoi VARCHAR(6) = SUBSTRING(@maKhoangTau, 4, 6);

        -- Nếu đổi toa, reset lại stt ghế
        IF @maToaHienThoi <> @maToaHienTai
        BEGIN
            SET @maToaHienTai = @maToaHienThoi;
            SET @sttGhe = 1;
        END

        -- Xác định loại ghế theo số ghế
        IF @soGhe = 8
            SET @maLoaiGhe = 'GHE_NGOI_MEM';
        ELSE IF @soGhe = 6
            SET @maLoaiGhe = 'GIUONG_NAM_6';
        ELSE IF @soGhe = 4
            SET @maLoaiGhe = 'GIUONG_NAM_4';

        SET @i = 1;
        SET @mkt = RIGHT(@maKhoangTau, 9); -- phần dùng để tạo mã ghế

        WHILE @i <= @soGhe
        BEGIN
            SET @hang = CASE 
                            WHEN @i <= @soGhe / 2 THEN 1 
                            ELSE 2 
                        END;

            SET @cot = CASE 
                            WHEN @hang = 1 THEN @i 
                            ELSE @i - (@soGhe / 2) 
                        END;

            -- Tạo mã ghế: G-<maKhoangTau>-<số thứ tự ghế theo toa, 4 chữ số>
            SET @maGhe = 'G-' + @mkt + '-' + RIGHT('0000' + CAST(@sttGhe AS VARCHAR), 4);

            -- Tạo vị trí: H_XX_G_YY
            SET @viTri = 'H_' + RIGHT('00' + CAST(@hang AS VARCHAR), 2) + '_G_' + RIGHT('00' + CAST(@cot AS VARCHAR), 2);

            INSERT INTO Ghe (maGhe, viTri, maKhoangTau, maLoaiGhe)
            VALUES (@maGhe, @viTri, @maKhoangTau, @maLoaiGhe);

            SET @i = @i + 1;
            SET @sttGhe = @sttGhe + 1;
        END

        FETCH NEXT FROM cur INTO @maKhoangTau, @soGhe;
    END

    CLOSE cur;
    DEALLOCATE cur;
END;
GO


EXEC sp_ThemGhe;
Go

--Thêm dữ liệu ca
INSERT INTO Ca (maCa, thoiGianBatDau, thoiGianKetThuc)
VALUES
    ('CA-01', '07:30', '11:30'),   -- Ca buổi sáng: từ 07:30 đến 11:30
    ('CA-02', '13:00', '17:00');   -- Ca buổi chiều: từ 13:00 đến 17:00
Go

--Thêm dữ liệu ca làm việc nhân viên

CREATE PROCEDURE sp_ThemCaLamViecNhanVien
AS
BEGIN
    DECLARE @ngay DATE = '2025-01-01';
    DECLARE @maCa1 NVARCHAR(5), @maCa2 NVARCHAR(5);
    DECLARE @soNhanVien INT, @index INT = 1;

    -- Giả sử có đúng 2 ca mỗi ngày
    SELECT @maCa1 = maCa FROM (SELECT maCa, ROW_NUMBER() OVER (ORDER BY maCa) AS rn FROM Ca) AS t WHERE rn = 1;
    SELECT @maCa2 = maCa FROM (SELECT maCa, ROW_NUMBER() OVER (ORDER BY maCa) AS rn FROM Ca) AS t WHERE rn = 2;

    -- Lưu danh sách nhân viên đang làm vào bảng tạm
    IF OBJECT_ID('tempdb..#NhanVienDangLam') IS NOT NULL DROP TABLE #NhanVienDangLam;

    SELECT maNV
    INTO #NhanVienDangLam
    FROM NhanVien;
    -- WHERE trangThai = 'Đang làm';

    SELECT @soNhanVien = COUNT(*) FROM #NhanVienDangLam;

    -- Lặp từ 1/1/2025 đến hiện tại
    WHILE @ngay <= CAST(GETDATE() AS DATE)
    BEGIN
        -- Nhân viên cho ca 1
        DECLARE @nv1 VARCHAR(15) = (SELECT maNV FROM (
            SELECT maNV, ROW_NUMBER() OVER (ORDER BY maNV) AS rn FROM #NhanVienDangLam
        ) AS t WHERE rn = ((@index - 1) % @soNhanVien) + 1);

        -- Nhân viên cho ca 2
        DECLARE @nv2 VARCHAR(15) = (SELECT maNV FROM (
            SELECT maNV, ROW_NUMBER() OVER (ORDER BY maNV) AS rn FROM #NhanVienDangLam
        ) AS t WHERE rn = ((@index) % @soNhanVien) + 1);

        -- Chèn ca 1
        INSERT INTO CaLamViecNhanVien (maNV, maCa, ngay)
        VALUES (@nv1, @maCa1, @ngay);

        -- Chèn ca 2
        INSERT INTO CaLamViecNhanVien (maNV, maCa, ngay)
        VALUES (@nv2, @maCa2, @ngay);

        -- Tăng chỉ số lên 2 cho ngày hôm sau
        SET @index = @index + 2;
        SET @ngay = DATEADD(DAY, 1, @ngay);
    END

    DROP TABLE #NhanVienDangLam;
END;
Go

-- Thực thi thủ tục
EXEC sp_ThemCaLamViecNhanVien;
Go

UPDATE [dbo].[CaLamViecNhanVien]
SET maNV = 'NV-0-00-681-131'
WHERE maNV = 'NV-0-00-853-145';
go
UPDATE [dbo].[CaLamViecNhanVien]
SET maNV = 'NV-0-00-681-131'
WHERE maNV = 'NV-0-01-274-121';
go
UPDATE [dbo].[CaLamViecNhanVien]
SET maNV = 'NV-0-00-681-131'
WHERE maNV = 'NV-0-01-698-151';
go

--Tạo dữ liệu cho bảng chuyến đi
CREATE PROCEDURE sp_ThemChuyenDi
	@ngayBatDau DATE = '2025-04-11',
    @soNgay INT = 5,
    @soChuyenMoiNgay INT = 3
AS
BEGIN

    DECLARE @i INT = 0;
    WHILE @i < @soNgay
    BEGIN
        DECLARE @currentDate DATE = DATEADD(DAY, @i, @ngayBatDau);
        DECLARE @j INT = 0;

        WHILE @j < @soChuyenMoiNgay
        BEGIN
            -- Chọn ngẫu nhiên 1 dòng từ ThoiGianDiChuyen
            DECLARE @maThoiGianDiChuyen VARCHAR(25);
            DECLARE @thoiGianDiChuyen INT;

            SELECT TOP 1 
                @maThoiGianDiChuyen = maThoiGianDiChuyen,
                @thoiGianDiChuyen = thoiGianDiChuyen
            FROM ThoiGianDiChuyen
            ORDER BY NEWID();

            -- Chọn ngẫu nhiên 1 tàu
            DECLARE @maTau VARCHAR(10);
            SELECT TOP 1 @maTau = maTau FROM Tau ORDER BY NEWID();

            -- Lấy tổng số ghế từ tàu
            DECLARE @totalSeats INT = 0;
            DECLARE @maToaTau VARCHAR(10);
            DECLARE @soGhe INT;

            -- Duyệt qua các toa tàu của tàu đã chọn
            DECLARE toa_cursor CURSOR FOR
                SELECT maToaTau
                FROM ToaTau
                WHERE maTau = @maTau;

            OPEN toa_cursor;
            FETCH NEXT FROM toa_cursor INTO @maToaTau;

            WHILE @@FETCH_STATUS = 0
            BEGIN
                -- Tính tổng số ghế từ các khoang trong toa
                DECLARE @soGheTrongToa INT = 0;
                DECLARE @maKhoangTau VARCHAR(13);

                DECLARE khoang_cursor CURSOR FOR
                    SELECT maKhoangTau
                    FROM KhoangTau
                    WHERE maToaTau = @maToaTau;

                OPEN khoang_cursor;
                FETCH NEXT FROM khoang_cursor INTO @maKhoangTau;

                WHILE @@FETCH_STATUS = 0
                BEGIN
                    -- Lấy số ghế trong khoang
                    SELECT @soGhe = soGhe FROM KhoangTau WHERE maKhoangTau = @maKhoangTau;
                    SET @soGheTrongToa = @soGheTrongToa + @soGhe;
                    FETCH NEXT FROM khoang_cursor INTO @maKhoangTau;
                END

                CLOSE khoang_cursor;
                DEALLOCATE khoang_cursor;

                -- Cộng số ghế từ toa vào tổng số ghế
                SET @totalSeats = @totalSeats + @soGheTrongToa;
                FETCH NEXT FROM toa_cursor INTO @maToaTau;
            END

            CLOSE toa_cursor;
            DEALLOCATE toa_cursor;

            -- Tạo mã chuyến đi: CD-MT-DDMMYYYY-XXX
            DECLARE @mtCode VARCHAR(3) = RIGHT(@maTau, 3);
            DECLARE @dateCode VARCHAR(8) = FORMAT(@currentDate, 'ddMMyyyy');
            DECLARE @sttCode VARCHAR(3) = RIGHT('000' + CAST(@j + 1 AS VARCHAR), 3);
            DECLARE @maChuyenDi NVARCHAR(20) = 'CD-' + @mtCode + '-' + @dateCode + '-' + @sttCode;

            -- Tạo thời gian khởi hành (6h sáng + mỗi chuyến cách nhau 2h)
            DECLARE @dateTimeBase DATETIME = CAST(@currentDate AS DATETIME);
            DECLARE @thoiGianKhoiHanh DATETIME = DATEADD(HOUR, 6 + (@j * 2), @dateTimeBase);

            -- Tính thời gian đến dự kiến
            DECLARE @thoiGianDenDuTinh DATETIME = DATEADD(MINUTE, @thoiGianDiChuyen, @thoiGianKhoiHanh);

            -- Thêm vào bảng ChuyenDi
            INSERT INTO ChuyenDi (
                maChuyenDi, maThoiGianDiChuyen, thoiGianKhoiHanh, thoiGianDenDuTinh,
                maTau, soGheDaDat, soGheConTrong
            )
            VALUES (
                @maChuyenDi, @maThoiGianDiChuyen, @thoiGianKhoiHanh, @thoiGianDenDuTinh,
                @maTau, 0, @totalSeats
            );

            SET @j += 1;
        END

        SET @i += 1;
    END
END;
Go

EXEC sp_ThemChuyenDi  
		@ngayBatDau = '2024-01-01',
		@soNgay = 800,
		@soChuyenMoiNgay = 20;
Go

ALTER TABLE ChuyenDi
    ADD CONSTRAINT CK_ChuyenDi_ThoiGianKhoiHanh
    CHECK (thoiGianKhoiHanh > GETDATE());
Go

CREATE PROCEDURE sp_ThemHoaDon2
    @soHoaDon INT = 10  -- Số lượng hóa đơn muốn tạo
AS
BEGIN
    DECLARE @i INT = 1;
    
    -- Vòng lặp để tạo nhiều hóa đơn
    WHILE @i <= @soHoaDon
    BEGIN
        -- Tạo mã hóa đơn (HD-XXX-Z-DDMMYYYY-UUUU)
        DECLARE @maNV VARCHAR(15);  -- Mã nhân viên
        DECLARE @maKH VARCHAR(17);  -- Mã khách hàng
        DECLARE @maKhuyenMai VARCHAR(15);  -- Mã khuyến mãi
        DECLARE @soVe INT;  -- Số vé
        DECLARE @VAT FLOAT = 0.1;  -- Thuế VAT 10%
        DECLARE @maHoaDon VARCHAR(22);  -- Mã hóa đơn
        DECLARE @caLamViec VARCHAR(5);  -- Mã ca làm việc
        DECLARE @ngayLapHoaDon DATE;  -- Ngày làm việc
        DECLARE @shiftCode VARCHAR(1);  -- Mã ca làm việc
        DECLARE @dateCode VARCHAR(8);  -- DDMMYYYY
        DECLARE @billNumber INT;  -- Số thứ tự hóa đơn trong cùng ca làm việc và ngày
        
        -- Giả sử bạn lấy ngẫu nhiên một nhân viên và khách hàng
        SELECT TOP 1 @maNV = maNV FROM NhanVien ORDER BY NEWID();
        SELECT TOP 1 @maKH = maKH FROM KhachHang ORDER BY NEWID();
        SELECT TOP 1 @maKhuyenMai = maKhuyenMai FROM KhuyenMai ORDER BY NEWID();
        
        -- Lấy thông tin ca làm việc của nhân viên
        SELECT TOP 1 @caLamViec = maCa, @ngayLapHoaDon = ngay
        FROM CaLamViecNhanVien
        WHERE maNV = @maNV
        ORDER BY NEWID();  -- Chọn ca ngẫu nhiên của nhân viên

        -- Giả sử số vé và tổng tiền ngẫu nhiên
        SET @soVe = 1 + (RAND() * 10);  -- Số vé ngẫu nhiên từ 1 đến 5
        
        -- Tạo mã ca làm việc (Lấy ca làm việc từ bảng)
        SET @shiftCode = RIGHT(@caLamViec, 1);  -- Mã ca làm việc (Lấy từ bảng CaLamViecNhanVien)

        -- Tạo mã ngày (DDMMYYYY)
        DECLARE @day VARCHAR(2) = RIGHT('00' + CAST(DAY(@ngayLapHoaDon) AS VARCHAR(2)), 2);
        DECLARE @month VARCHAR(2) = RIGHT('00' + CAST(MONTH(@ngayLapHoaDon) AS VARCHAR(2)), 2);
        DECLARE @year VARCHAR(4) = CAST(YEAR(@ngayLapHoaDon) AS VARCHAR(4));
        SET @dateCode = @day + @month + @year;  -- DDMMYYYY

        -- Lấy số thứ tự hóa đơn trong ngày và ca làm việc
        SELECT @billNumber = ISNULL(MAX(CAST(SUBSTRING(maHoaDon, 19, 4) AS INT)), 0) + 1
        FROM HoaDon
        WHERE SUBSTRING(maHoaDon, 10, 8) = @dateCode  -- So sánh ngày trong mã hóa đơn
          AND SUBSTRING(maHoaDon, 8, 1) = @shiftCode;  -- So sánh mã ca trong mã hóa đơn

        -- Lấy 3 chữ số cuối của mã nhân viên (XXX)
        DECLARE @employeeCode VARCHAR(3);  -- Mã nhân viên 3 chữ số cuối
        SET @employeeCode = RIGHT(@maNV, 3);  -- Lấy 3 chữ số cuối của mã nhân viên

        -- Tạo mã hóa đơn
        SET @maHoaDon = 'HD-' + @employeeCode + '-' + @shiftCode + '-' + @dateCode + '-' + RIGHT('0000' + CAST(@billNumber AS VARCHAR), 4);

        -- Chèn dữ liệu vào bảng HoaDon
        INSERT INTO HoaDon (maHoaDon, ngayLapHoaDon, VAT, soVe, maNV, maKH, maKhuyenMai)
        VALUES (@maHoaDon, @ngayLapHoaDon, @VAT, @soVe, @maNV, @maKH, @maKhuyenMai);

        -- Tăng biến đếm
        SET @i = @i + 1;
    END
END;
Go

EXEC sp_ThemHoaDon2
	@soHoaDon = 8000;
Go

SELECT * 
FROM [dbo].[HoaDon] 
WHERE DAY(ngayLapHoaDon) = 29
AND MONTH(ngayLapHoaDon) = 1
AND YEAR(ngayLapHoaDon) = 2025;
Go


CREATE PROCEDURE sp_ThemVeTuHoaDon6
AS
BEGIN
    DECLARE @maHoaDon VARCHAR(22), @soVe INT, @ngay DATE, @i INT;
    DECLARE @maLoaiVe VARCHAR(7), @maChuyenDi NVARCHAR(20), @maHanhKhach VARCHAR(16), @maGhe VARCHAR(20);
    DECLARE @trangThai NVARCHAR(13), @giaVe MONEY;
    DECLARE @maVe VARCHAR(56), @maVeSoThuTu INT, @ddmmyyyy VARCHAR(8);
    DECLARE @maLoaiGhe VARCHAR(12), @heSoGhe FLOAT, @heSoLoaiVe FLOAT, @giaCoBan MONEY;

    -- Bảng tạm lưu số thứ tự vé cho từng ngày
    DECLARE @SoThuTu TABLE (
        ngay DATE PRIMARY KEY,
        stt INT
    );

    DECLARE hoaDon_cursor CURSOR FOR
        SELECT maHoaDon, soVe, ngayLapHoaDon
        FROM HoaDon;

    OPEN hoaDon_cursor;
    FETCH NEXT FROM hoaDon_cursor INTO @maHoaDon, @soVe, @ngay;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        SET @i = 1;
        WHILE @i <= @soVe
        BEGIN
            -- Random dữ liệu cho vé
			SELECT TOP 1 @maLoaiVe = maLoaiVe FROM LoaiVe ORDER BY NEWID();
			SELECT TOP 1 @maChuyenDi = maChuyenDi FROM ChuyenDi ORDER BY NEWID();
			SELECT TOP 1 @maHanhKhach = maHanhKhach FROM HanhKhach ORDER BY NEWID();
			SELECT TOP 1 @maGhe = maGhe 
			FROM Ghe 
			WHERE SUBSTRING(maGhe, 3, 3) = SUBSTRING(@maChuyenDi, 4, 3)
			ORDER BY NEWID();


			PRINT 'maHoaDon: ' + @maHoaDon;
			PRINT 'maChuyenDi: ' + @maChuyenDi;
			PRINT 'maGhe: ' + @maGhe;
			PRINT 'maLoaiVe: ' + @maLoaiVe;

            SET @trangThai = CASE ABS(CHECKSUM(NEWID())) % 3 
                             WHEN 0 THEN N'Đã hoàn thành'
                             WHEN 1 THEN N'Đã đặt'
                             ELSE N'Đã hủy' END;

            -- Tính giá vé
            SELECT @maLoaiGhe = maLoaiGhe FROM Ghe WHERE maGhe = @maGhe;
            SELECT @heSoGhe = heSoGhe FROM LoaiGhe WHERE maLoaiGhe = @maLoaiGhe;
            SELECT @heSoLoaiVe = heSoLoaiVe FROM LoaiVe WHERE maLoaiVe = @maLoaiVe;

			Print 'maLoaiGhe = ' + CAST(@maLoaiGhe AS VARCHAR);

            SELECT @giaCoBan = soKMDiChuyen * soTienMotKM
            FROM ChuyenDi cd
            JOIN ThoiGianDiChuyen tg ON cd.maThoiGianDiChuyen = tg.maThoiGianDiChuyen
            WHERE cd.maChuyenDi = @maChuyenDi;

			PRINT 'heSoGhe = ' + CAST(@heSoGhe AS VARCHAR);
			PRINT 'giaCoBan = ' + CAST(@giaCoBan AS VARCHAR);


            SET @giaVe = @giaCoBan * @heSoGhe * @heSoLoaiVe;
			PRINT 'giaVe = ' + CAST(@giaVe AS VARCHAR);
            -- Tạo mã vé
            SET @ddmmyyyy = RIGHT('00' + CAST(DAY(@ngay) AS VARCHAR), 2) +
                            RIGHT('00' + CAST(MONTH(@ngay) AS VARCHAR), 2) +
                            CAST(YEAR(@ngay) AS VARCHAR);

            -- Lấy số thứ tự vé trong ngày
            IF EXISTS (SELECT 1 FROM @SoThuTu WHERE ngay = @ngay)
            BEGIN
                SELECT @maVeSoThuTu = stt + 1 FROM @SoThuTu WHERE ngay = @ngay;
                UPDATE @SoThuTu SET stt = @maVeSoThuTu WHERE ngay = @ngay;
            END
            ELSE
            BEGIN
                SET @maVeSoThuTu = 1;
                INSERT INTO @SoThuTu (ngay, stt) VALUES (@ngay, @maVeSoThuTu);
            END

            SET @maVe = 'V-' + @maChuyenDi + '-' + @maGhe + '-' + @ddmmyyyy + '-' + RIGHT('000' + CAST(@maVeSoThuTu AS VARCHAR), 3);

            -- Thêm vé
            INSERT INTO Ve (maVe, maLoaiVe, maChuyenDi, maHanhKhach, maGhe, maHoaDon, trangThai, giaVe)
            VALUES (@maVe, @maLoaiVe, @maChuyenDi, @maHanhKhach, @maGhe, @maHoaDon, @trangThai, @giaVe);

            SET @i = @i + 1;
        END

        FETCH NEXT FROM hoaDon_cursor INTO @maHoaDon, @soVe, @ngay;
    END

    CLOSE hoaDon_cursor;
    DEALLOCATE hoaDon_cursor;
END;
Go

EXEC sp_ThemVeTuHoaDon6;
Go

Create procedure sp_ThemTongTienHoaDon
as 
Begin

	DECLARE @maHoaDon VARCHAR(22), @soVe INT, @ngay DATE;
	DECLARE @tongTien Money, @temp money;
	DECLARE hoaDon_cursor CURSOR FOR
        SELECT maHoaDon, soVe
        FROM HoaDon;

	OPEN hoaDon_cursor;
    FETCH NEXT FROM hoaDon_cursor INTO @maHoaDon, @soVe;

	While @@FETCH_STATUS = 0
	Begin
		SELECT @tongTien = sum(giaVe) FROM Ve WHERE maHoaDon = @maHoaDon;
		SET @tongTien = ISNULL(@tongTien, 0);
		UPDATE HoaDon
        SET tongTien = @tongTien
        WHERE maHoaDon = @maHoaDon;
		FETCH NEXT FROM hoaDon_cursor INTO @maHoaDon, @soVe;
	end

	CLOSE hoaDon_cursor;
    DEALLOCATE hoaDon_cursor;
end;
go

EXEC sp_ThemTongTienHoaDon;
go

Create procedure sp_ThemThongTinCaLamViecVer2_9
as 
Begin

	DECLARE @maNV VARCHAR(15), @soVe INT, @ngay DATE;
	DECLARE @tongTien Money, @maHoaDon varchar(22), @maCa NVARCHAR(5);

	DECLARE CaLamViec_cursor CURSOR FOR
        SELECT maNV, ngay, maCa
        FROM CaLamViecNhanVien;

	OPEN CaLamViec_cursor;
    FETCH NEXT FROM CaLamViec_cursor INTO @maNV, @ngay, @maCa;

	While @@FETCH_STATUS = 0
	Begin

		SET @soVe = 0;
		SET @tongTien = 0;

		DECLARE HoaDonTheoNgay_cursor CURSOR FOR
			SELECT hd.maHoaDon
			FROM HoaDon hd
			WHERE @maNV = hd.maNV and Right(@maCa, 1) = SUBSTRING(hd.maHoaDon, 8, 1)
			and day(@ngay) = day(hd.ngayLapHoaDon)
			and month(@ngay) = month(hd.ngayLapHoaDon)
			and year(@ngay) = year(hd.ngayLapHoaDon);

		OPEN HoaDonTheoNgay_cursor;
		FETCH NEXT FROM HoaDonTheoNgay_cursor INTO @maHoaDon;
		print 'maCa: ' + RIGHT(@maCa, 1)
		print 'maCatrongHD: ' + SUBSTRING(@maHoaDon, 8, 1)
		Print 'maHD: ' + @maHoaDon

		While @@FETCH_STATUS = 0
		Begin
			SELECT distinct @soVe = @soVe + COUNT(maVe),
			       @tongTien = @tongTien + ISNULL(SUM(giaVe), 0)
			from Ve
			where maHoaDon = @maHoaDon
			FETCH NEXT FROM HoaDonTheoNgay_cursor INTO @maHoaDon;
		end

		UPDATE CaLamViecNhanVien
        SET tongVeBan = @soVe,
            tongTienBanDuoc = @tongTien
        WHERE maNV = @maNV AND ngay = @ngay AND maCa = @maCa;

		CLOSE HoaDonTheoNgay_cursor;
		DEALLOCATE HoaDonTheoNgay_cursor;
		FETCH NEXT FROM CaLamViec_cursor INTO @maNV, @ngay, @maCa;
	end

	CLOSE CaLamViec_cursor;
    DEALLOCATE CaLamViec_cursor;
end;
go

EXEC sp_ThemThongTinCaLamViecVer2_9;
go

select sum(soVe) from [dbo].[HoaDon]
where day([ngayLapHoaDon]) = 1
and MONTH([ngayLapHoaDon]) = 1
and year([ngayLapHoaDon]) = 2025
and substring(maHoaDon, 8, 1) = 1;
Go

select * from [dbo].[CaLamViecNhanVien]
where day([ngay]) = 1
and MONTH([ngay]) = 1
and year([ngay]) = 2025;
go

INSERT INTO NhanVien (maNV, tenNV, gioiTinh, ngaySinh, email, soDienThoai, cccd, ngayBatDauLamViec, vaiTro, trangThai) VALUES
(N'NV-0-04-938-227', N'Nguyễn Thành Trọng', 0, '2004-10-16', 'tronggg@gmail.com', '0328546227', '083204000938', '2023-07-15', N'Nhân viên bán vé', N'Đang làm'),
(N'NV-0-04-939-141', N'Nguyễn Minh Phúc', 0, '2004-10-16', 'phuc123@gmail.com', '0981247141', '083204000939', '2023-07-15', N'Nhân viên quản lý', N'Đang làm')
go

INSERT INTO NhanVien (maNV, tenNV, gioiTinh, ngaySinh, email, soDienThoai, cccd, ngayBatDauLamViec, vaiTro, trangThai) VALUES
(N'NV-0-04-941-678', N'Phạm Gia Khánh', 0, '2004-10-16', 'tronggg1@gmail.com', '0312345678', '083204000941', '2023-07-15', N'Nhân viên quản lý', N'Đang làm')
go

--Thêm dữ liệu tài khoản
Insert into TaiKhoan([tenDangNhap], [matKhau], [maNV]) values
('0981247141', '$2a$10$qshwvMWqFcl2VTbNUw23cOvRHsJmQ5qZb0ETyGxxTXKaSAb.kJPOO', 'NV-0-04-939-141'),
('0328546227', '$2a$10$qshwvMWqFcl2VTbNUw23cOvRHsJmQ5qZb0ETyGxxTXKaSAb.kJPOO', 'NV-0-04-938-227');
go

Insert into TaiKhoan([tenDangNhap], [matKhau], [maNV]) values
('0312345678', '$2a$10$qshwvMWqFcl2VTbNUw23cOvRHsJmQ5qZb0ETyGxxTXKaSAb.kJPOO', 'NV-0-04-941-678')
go

-- Chỉ mục cho CaLamViecNhanVien
CREATE INDEX idx_calamviecnhanvien_manv_ngay ON CaLamViecNhanVien(maNV, ngay);
CREATE INDEX idx_calamviecnhanvien_maca ON CaLamViecNhanVien(maCa);

-- Chỉ mục cho Ca
CREATE INDEX idx_ca_maca ON Ca(maCa);

-- Chỉ mục cho Ve
CREATE INDEX idx_ve_maloaive ON Ve(maLoaiVe);
CREATE INDEX idx_ve_trangthai ON Ve(trangThai);
CREATE INDEX idx_ve_maghe ON Ve(maGhe);

-- Chỉ mục cho Ghe
CREATE INDEX idx_ghe_maghe ON Ghe(maGhe);
CREATE INDEX idx_ghe_maloaighe ON Ghe(maLoaiGhe);

-- Chỉ mục cho ChuyenDi
CREATE INDEX idx_chuyendi_mathoigiandichuyen ON ChuyenDi(maThoiGianDiChuyen);

-- Chỉ mục cho ThoiGianDiChuyen
CREATE INDEX idx_thoigiandichuyen_mathoigiandichuyen ON ThoiGianDiChuyen(maThoiGianDiChuyen);
CREATE INDEX idx_thoigiandichuyen_maga ON ThoiGianDiChuyen(maGaDi, maGaDen);

-- Chỉ mục cho Ga
CREATE INDEX idx_ga_maga ON Ga(maGa);
go

SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID('Ga');
go

-- Index cho bảng ChuyenDi
CREATE INDEX idx_maThoiGianDiChuyen_soGheDaDat ON [dbo].[ChuyenDi](maThoiGianDiChuyen, soGheDaDat);

-- Index cho bảng ThoiGianDiChuyen
CREATE INDEX idx_maThoiGianDiChuyen_ga ON [dbo].[ThoiGianDiChuyen](maThoiGianDiChuyen, maGaDi, maGaDen);
go

--- Xóa thủ tục nếu đã tồn tại
IF OBJECT_ID('sp_TraCuuVeTheoMaVe', 'P') IS NOT NULL
    DROP PROCEDURE sp_TraCuuVeTheoMaVe;
GO

-- Tạo lại thủ tục
CREATE PROCEDURE sp_TraCuuVeTheoMaVe
    @maVe NVARCHAR(100) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        v.maVe,
        lv.tenLoaiVe AS LoaiVe,
        g1.tenGa AS GaDi,
        g2.tenGa AS GaDen,
        hk.tenHanhKhach AS HanhKhach,
        lg.tenLoaiGhe AS LoaiGhe,
        v.maHoaDon,
        v.trangThai,
        v.giaVe,
        cd.thoiGianKhoiHanh AS NgayDi,
        cd.thoiGianDenDuTinh AS NgayDen,
        t.tenTau AS Tau,
        kt.tenKhoangTau AS KhoangTau,
        tt.tenToaTau AS ToaTau
    FROM Ve v
    LEFT JOIN ChuyenDi cd ON v.maChuyenDi = cd.maChuyenDi
    LEFT JOIN ThoiGianDiChuyen tg ON cd.maThoiGianDiChuyen = tg.maThoiGianDiChuyen
    LEFT JOIN Ga g1 ON tg.maGaDi = g1.maGa
    LEFT JOIN Ga g2 ON tg.maGaDen = g2.maGa
    LEFT JOIN Ghe g ON v.maGhe = g.maGhe
    LEFT JOIN LoaiGhe lg ON g.maLoaiGhe = lg.maLoaiGhe
    LEFT JOIN KhoangTau kt ON g.maKhoangTau = kt.maKhoangTau
    LEFT JOIN ToaTau tt ON kt.maToaTau = tt.maToaTau
    LEFT JOIN LoaiVe lv ON v.maLoaiVe = lv.maLoaiVe
    LEFT JOIN Tau t ON cd.maTau = t.maTau
    LEFT JOIN HanhKhach hk ON v.maHanhKhach = hk.maHanhKhach
    WHERE (@maVe IS NULL OR v.maVe = @maVe);
END;
GO


EXEC sp_TraCuuVeTheoMaVe NULL;
go

ALTER LOGIN sa WITH CHECK_EXPIRATION = OFF, CHECK_POLICY = OFF;


IF OBJECT_ID('dbo.InsertHanhKhach', 'P') IS NOT NULL
  DROP PROCEDURE dbo.InsertHanhKhach;
GO

CREATE PROCEDURE InsertHanhKhach
    @tenHanhKhach NVARCHAR(100),
    @cccd         VARCHAR(12),
    @namSinh      INT
AS
BEGIN
    SET NOCOUNT ON;

    IF LEN(@cccd) <> 12 OR @cccd LIKE '%[^0-9]%'  
    BEGIN
        RAISERROR('CCCD phải chứa đúng 12 số.', 16, 1);
        RETURN;
    END
    IF LTRIM(RTRIM(@tenHanhKhach)) = ''
    BEGIN
        RAISERROR('Tên hành khách không được rỗng.', 16, 1);
        RETURN;
    END

    DECLARE @yy      VARCHAR(2) = RIGHT(@cccd, 2);
    -- CHỈ format 6 ký tự: ddMMyy
    DECLARE @dateStr VARCHAR(6) = FORMAT(GETDATE(), 'ddMMyy');

    DECLARE @count   INT;
    SELECT @count = COUNT(*) 
      FROM HanhKhach
     WHERE SUBSTRING(maHanhKhach, 7, 6) = @dateStr;

    DECLARE @seq     INT         = @count + 1;
    DECLARE @seqStr  VARCHAR(3)  = RIGHT('000' + CAST(@seq AS VARCHAR(3)), 3);

    -- Tổng độ dài 16 ký tự: 3 + 2 + 1 + 6 + 1 + 3 = 16
    DECLARE @maHanhKhach VARCHAR(16) 
      = 'HK-' + @yy + '-' + @dateStr + '-' + @seqStr;

    INSERT INTO HanhKhach (maHanhKhach, tenHanhKhach, cccd, namSinh)
    VALUES (@maHanhKhach, @tenHanhKhach, @cccd, @namSinh);

    SELECT @maHanhKhach AS NewHanhKhachID;
END;
GO

IF OBJECT_ID('dbo.InsertHoaDon', 'P') IS NOT NULL
  DROP PROCEDURE dbo.InsertHoaDon;
GO

CREATE PROCEDURE InsertHoaDon
    @maNV             NVARCHAR(15),
    @maKH             VARCHAR(17),
    @maKhuyenMai      VARCHAR(15),
    @soVe             INT,
    @ngayLapHoaDon    DATETIME,
    @maCa             NVARCHAR(5)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @VAT        FLOAT       = 0.1;
    DECLARE @xxx        VARCHAR(3)  = RIGHT(@maNV, 3);
    DECLARE @dateStr    VARCHAR(8)  = FORMAT(@ngayLapHoaDon, 'ddMMyyyy');
    DECLARE @shiftCode  CHAR(1)     = RIGHT(@maCa, 1);

    -- (Tuỳ chọn) kiểm tra maCa
    IF @maCa NOT IN (SELECT maCa FROM Ca)
    BEGIN
        RAISERROR('Mã ca truyền vào không tồn tại.',16,1);
        RETURN;
    END

    -- Đếm số hóa đơn đã có cho cùng ca + ngày
    DECLARE @invoiceCount INT;
    SELECT @invoiceCount = COUNT(*)
    FROM HoaDon
    WHERE maNV = @maNV
      AND maHoaDon LIKE 'HD-' + @xxx + '-' + @shiftCode + '-' + @dateStr + '-%';

    DECLARE @invoiceSeqStr VARCHAR(4) 
      = RIGHT('0000' + CAST(@invoiceCount + 1 AS VARCHAR(4)), 4);

    DECLARE @maHoaDon VARCHAR(50) 
      = 'HD-' + @xxx + '-' + @shiftCode + '-' + @dateStr + '-' + @invoiceSeqStr;

    INSERT INTO HoaDon
      (maHoaDon, ngayLapHoaDon, VAT, soVe, tongTien, maNV, maKH, maKhuyenMai)
    VALUES
      (@maHoaDon, @ngayLapHoaDon, @VAT, @soVe, 0, @maNV, @maKH, @maKhuyenMai);

    SELECT @maHoaDon AS NewHoaDonID;
END;
GO

IF OBJECT_ID('dbo.InsertVe', 'P') IS NOT NULL
  DROP PROCEDURE dbo.InsertVe;
GO

CREATE PROCEDURE InsertVe
    @maHanhKhach VARCHAR(16),
    @maLoaiVe VARCHAR(7),
    @maChuyenDi VARCHAR(20),
    @maGhe VARCHAR(20),
    @maHoaDon VARCHAR(22),
    @trangThai NVARCHAR(13),
    @giaVe MONEY
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra trạng thái vé
    IF @trangThai NOT IN (N'Đã hoàn thành', N'Đã đặt', N'Đã hủy')
    BEGIN
         RAISERROR('Trạng thái vé không hợp lệ. Chỉ chấp nhận: Đã hoàn thành, Đã đặt, Đã hủy.', 16, 1);
         RETURN;
    END

    -- Kiểm tra giá vé > 0
    IF @giaVe <= 0
    BEGIN
         RAISERROR('Giá vé phải lớn hơn 0.', 16, 1);
         RETURN;
    END

    -- Kiểm tra sự tồn tại của các khóa ngoại
    IF NOT EXISTS (SELECT 1 FROM HanhKhach WHERE maHanhKhach = @maHanhKhach)
    BEGIN
         RAISERROR('Mã hành khách không tồn tại.', 16, 1);
         RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM LoaiVe WHERE maLoaiVe = @maLoaiVe)
    BEGIN
         RAISERROR('Mã loại vé không tồn tại.', 16, 1);
         RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM ChuyenDi WHERE maChuyenDi = @maChuyenDi)
    BEGIN
         RAISERROR('Mã chuyến đi không tồn tại.', 16, 1);
         RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM Ghe WHERE maGhe = @maGhe)
    BEGIN
         RAISERROR('Mã ghế không tồn tại.', 16, 1);
         RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon = @maHoaDon)
    BEGIN
         RAISERROR('Mã hóa đơn không tồn tại.', 16, 1);
         RETURN;
    END

    -- Xác định ngày phát hành vé là ngày hiện tại
    DECLARE @ticketDate DATETIME = GETDATE();
    DECLARE @dateStr VARCHAR(8) = FORMAT(@ticketDate, 'ddMMyyyy');  -- Yêu cầu SQL Server 2012+

    -- Tính số thứ tự của vé trong ngày
    -- Lọc các vé có định dạng chứa phần ngày bằng @dateStr (định dạng maVe đã theo mẫu "V-{maChuyenDi}-{maGhe}-{DDMMYYYY}-XXX")
    DECLARE @ticketCount INT;
    SELECT @ticketCount = COUNT(*)
    FROM Ve
    WHERE maVe LIKE 'V-%-%-' + @dateStr + '-%';

    DECLARE @seq INT = @ticketCount + 1;
    DECLARE @seqStr VARCHAR(3) = RIGHT('000' + CAST(@seq AS VARCHAR(3)), 3);

    -- Sinh mã vé theo định dạng: V-{maChuyenDi}-{maGhe}-{DDMMYYYY}-XXX
    DECLARE @maVe VARCHAR(52) = 'V-' + @maChuyenDi + '-' + @maGhe + '-' + @dateStr + '-' + @seqStr;

    -- Chèn bản ghi mới vào bảng Ve
    INSERT INTO Ve (maVe, maHanhKhach, maLoaiVe, maChuyenDi, maGhe, maHoaDon, trangThai, giaVe)
    VALUES (@maVe, @maHanhKhach, @maLoaiVe, @maChuyenDi, @maGhe, @maHoaDon, @trangThai, @giaVe);

    -- Trả về mã vé mới được tạo
    SELECT @maVe AS NewMaVe;
END;
GO

CREATE PROCEDURE sp_ThemCaLamViecNhanVienTuongLai
AS
BEGIN
    DECLARE @ngay DATE = '2025-05-26';
    DECLARE @maCa1 NVARCHAR(5), @maCa2 NVARCHAR(5);
    DECLARE @soNhanVien INT, @index INT = 1;

    -- Giả sử có đúng 2 ca mỗi ngày
    SELECT @maCa1 = maCa FROM (SELECT maCa, ROW_NUMBER() OVER (ORDER BY maCa) AS rn FROM Ca) AS t WHERE rn = 1;
    SELECT @maCa2 = maCa FROM (SELECT maCa, ROW_NUMBER() OVER (ORDER BY maCa) AS rn FROM Ca) AS t WHERE rn = 2;

    -- Lưu danh sách nhân viên đang làm vào bảng tạm
    IF OBJECT_ID('tempdb..#NhanVienDangLam') IS NOT NULL DROP TABLE #NhanVienDangLam;

    SELECT maNV
    INTO #NhanVienDangLam
    FROM NhanVien;
    -- WHERE trangThai = 'Đang làm';

    SELECT @soNhanVien = COUNT(*) FROM #NhanVienDangLam;

    -- Lặp từ 1/1/2025 đến hiện tại
    WHILE @ngay <= CAST('2025-06-26' AS DATE)
    BEGIN
        -- Nhân viên cho ca 1
        DECLARE @nv1 VARCHAR(15) = (SELECT maNV FROM (
            SELECT maNV, ROW_NUMBER() OVER (ORDER BY maNV) AS rn FROM #NhanVienDangLam
        ) AS t WHERE rn = ((@index - 1) % @soNhanVien) + 1);

        -- Nhân viên cho ca 2
        DECLARE @nv2 VARCHAR(15) = (SELECT maNV FROM (
            SELECT maNV, ROW_NUMBER() OVER (ORDER BY maNV) AS rn FROM #NhanVienDangLam
        ) AS t WHERE rn = ((@index) % @soNhanVien) + 1);

        -- Chèn ca 1
        INSERT INTO CaLamViecNhanVien (maNV, maCa, ngay)
        VALUES (@nv1, @maCa1, @ngay);

        -- Chèn ca 2
        INSERT INTO CaLamViecNhanVien (maNV, maCa, ngay)
        VALUES (@nv2, @maCa2, @ngay);

        -- Tăng chỉ số lên 2 cho ngày hôm sau
        SET @index = @index + 2;
        SET @ngay = DATEADD(DAY, 1, @ngay);
    END

    DROP TABLE #NhanVienDangLam;
END;
Go

-- Thực thi thủ tục
EXEC sp_ThemCaLamViecNhanVienTuongLai;
Go

SELECT * FROM CaLamViecNhanVien WHERE ngay >= CAST('2025-05-26' AS DATE);

UPDATE CaLamViecNhanVien
SET maNV = 'NV-0-04-938-227'
WHERE ngay >= CAST('2025-05-26' AS DATE);

update [dbo].[NhanVien]
set vaiTro = 'Nhân viên bán vé'
where maNV = 'NV-0-04-939-141'