CREATE DATABASE QLBANHANG
GO

USE QLBANHANG
GO

-- Chuẩn bị bản nhân viên

CREATE TABLE NhanVien (
    MaNhanVien VARCHAR(10) NOT NULL PRIMARY KEY,
    TenNhanVien NVARCHAR(50) NOT NULL,
    SoDienThoai VARCHAR(10) NOT NULL,
    CCCD VARCHAR(12) NOT NULL,
    DiaChi NVARCHAR(255),
    GioiTinh NVARCHAR(5) NOT NULL,
    ChucVu NVARCHAR(50) NOT NULL,
    CaLam NVARCHAR(50) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL,
    HinhAnh VARCHAR(255)
)

GO

-- Chuẩn bị data nhân viên
INSERT INTO NhanVien (MaNhanVien, TenNhanVien, SoDienThoai, CCCD, DiaChi, GioiTinh, ChucVu, CaLam, TrangThai, HinhAnh)
VALUES ('NV001', N'Nguyễn Quốc Nhựt', '0367627363', '082205017345', N'256 Dương Quảng Hàm, Q Gò Vấp, TP.HCM', N'Nam', N'Nhân viên bán hàng', N'Ca sáng', N'Đang làm', 'image_nv1.png'),
        ('NV002', N'Nguyễn Thế Luân', '0365868345', '081205017676', N'256 Nguyễn Văn Khối, Q Gò Vấp, TP.HCM', N'Nam', N'Nhân viên bán hàng', N'Ca chiều', N'Đang làm', 'image_nv2.png'),
        ('NV003', N'Phan Khánh Khoa', '0966762657', '081105034345', N'123 Võ Thị Sáu, Q1, TP.HCM', N'Nam', N'Quản lý', N'Toàn thời gian', N'Đang làm', 'image_nv3.png'),
        ('NV004', N'Hồ Hoàng Minh', '0954342657', '081105034543', N'12 Đinh Tiên Hoàng, Q1, TP.HCM', N'Nam', N'Nhân viên bán hàng', N'Ca tối', N'Đang làm', 'image_nv4.png'),
		('NV005', N'Trần Minh Hoàng', '0978123456', '082305017111', N'45 Lê Văn Sỹ, Q3, TP.HCM', N'Nam', N'Nhân viên bán hàng', N'Ca sáng', N'Đang làm', 'image_nv5.png'),
        ('NV006', N'Lê Thị Thanh Hằng', '0987654321', '082405017222', N'78 Phan Xích Long, Phú Nhuận, TP.HCM', N'Nữ', N'Nhân viên bán hàng', N'Ca chiều', N'Đang làm', 'image_nv6.png'),
        ('NV007', N'Ngô Đức Anh', '0935123456', '082505017333', N'22 Nguyễn Oanh, Gò Vấp, TP.HCM', N'Nam', N'Nhân viên bán hàng', N'Ca tối', N'Đang làm', 'image_nv7.png'),
        ('NV008', N'Phạm Thị Mỹ Linh', '0912345678', '082605017444', N'90 Cộng Hòa, Tân Bình, TP.HCM', N'Nữ', N'Nhân viên bán hàng', N'Ca sáng', N'Đang làm', 'image_nv8.png'),
        ('NV009', N'Đặng Quốc Bảo', '0923456789', '082705017555', N'11 Trường Chinh, Tân Bình, TP.HCM', N'Nam', N'Nhân viên bán hàng', N'Ca chiều', N'Đang làm', 'image_nv9.png'),
        ('NV010', N'Võ Thị Ngọc Anh', '0945678123', '082805017666', N'56 Lý Thường Kiệt, Q10, TP.HCM', N'Nữ', N'Nhân viên bán hàng', N'Ca tối', N'Đang làm', 'image_nv10.png')

-- select * from NhanVien

GO

CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(50) NOT NULL PRIMARY KEY,
    MatKhau VARCHAR(50) NOT NULL,
	NgayTao DATETIME DEFAULT GETDATE(),
    TrangThaiOnline BIT DEFAULT 0,
    MaNhanVien VARCHAR(10) NOT NULL UNIQUE,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE
)
GO

INSERT INTO TaiKhoan (TenDangNhap, MatKhau, MaNhanVien)
VALUES
('NQNhut', '123456', 'NV001'),
('NTLuan', '123456', 'NV002'),
('PKKhoa', '123456', 'NV003'),
('HHMinh', '123456', 'NV004'),
('TMHoang', '123456', 'NV005'),
('LTTHang', '123456', 'NV006'),
('NDAnh', '123456', 'NV007'),
('PTMLinh', '123456', 'NV008'),
('DQBao', '123456', 'NV009'),
('VTNAnh', '123456', 'NV010')

-- select * from TaiKhoan
GO 

CREATE TABLE NhaCungCap (
    MaNCC VARCHAR(10) NOT NULL PRIMARY KEY,        
    TenNCC NVARCHAR(255) NOT NULL,        
    DiaChi NVARCHAR(500),
    SoDienThoai VARCHAR(10), 
    Email VARCHAR(100)
)

GO

INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, SoDienThoai, Email)
VALUES
('NCC001', N'Công ty Dược Hậu Giang', 
 N'288 Bis Nguyễn Văn Cừ, Quận Ninh Kiều, Cần Thơ', '0292389123', 'info@dhgpharma.com.vn'),

('NCC002', N'Công ty CP Dược phẩm Traphaco', 
 N'75 Yên Ninh, Ba Đình, Hà Nội', '0243845134', 'contact@traphaco.com.vn'),

('NCC003', N'Công ty CP Dược phẩm Imexpharm', 
 N'04 Đường 30/4, TP. Cao Lãnh, Đồng Tháp', '0277385134', 'info@imexpharm.com'),

('NCC004', N'Công ty CP Pymepharco', 
 N'166-170 Nguyễn Huệ, TP. Tuy Hòa, Phú Yên', '0257389234', 'info@pymepharco.com'),

('NCC005', N'Công ty TNHH Sanofi Việt Nam', 
 N'10 Đại lộ Tự Do, KCN Việt Nam - Singapore, Bình Dương', '0274375124', 'contact@sanofi.com'),

('NCC006', N'Công ty TNHH Dược phẩm GSK Việt Nam', 
 N'Le Meridien Tower, Quận 1, TP.HCM', '0283821234', 'info@gsk.com'),

('NCC007', N'Công ty TNHH Pfizer Việt Nam', 
 N'Saigon Centre, Quận 1, TP.HCM', '0283823234', 'contact@pfizer.com'),

('NCC008', N'Công ty TNHH Bayer Việt Nam', 
 N'Melinh Point Tower, Quận 1, TP.HCM', '0283824234', 'info@bayer.com'),

('NCC009', N'Công ty CP Dược phẩm OPC', 
 N'1017 Hồng Bàng, Quận 6, TP.HCM', '0283855124', 'contact@opcpharma.com'),

('NCC010', N'Công ty CP Dược phẩm TV.Pharm', 
 N'27 Nguyễn Chí Thanh, TP. Trà Vinh', '0294389134', 'info@tvpharm.com.vn'),

('NCC011', N'Công ty CP Dược phẩm Domesco', 
 N'66 Quốc lộ 30, TP. Cao Lãnh, Đồng Tháp', '0277386123', 'contact@domesco.com'),

('NCC012', N'Công ty CP Dược phẩm Mekophar', 
 N'297/5 Lý Thường Kiệt, Quận 11, TP.HCM', '0283865134', 'info@mekophar.com'),

('NCC013', N'Công ty CP Dược phẩm Hà Tây', 
 N'10A Quang Trung, Hà Đông, Hà Nội', '0243355123', 'contact@hataypharma.com'),

('NCC014', N'Công ty CP Dược phẩm Bidiphar', 
 N'498 Nguyễn Thái Học, TP. Quy Nhơn, Bình Định', '0256389234', 'info@bidiphar.com'),

('NCC015', N'Công ty CP Dược phẩm Agimexpharm', 
 N'27 Nguyễn Thị Minh Khai, TP. Long Xuyên, An Giang', '0296398134', 'contact@agimexpharm.com'),

('NCC016', N'Công ty CP Dược phẩm Boston Việt Nam', 
 N'12 Đường 3/2, Quận 10, TP.HCM', '0283866124', 'info@bostonpharma.com.vn'),

('NCC017', N'Công ty TNHH Abbott Việt Nam', 
 N'35 Đại lộ Tự Do, KCN VSIP, Bình Dương', '0274376234', 'contact@abbott.com'),

('NCC018', N'Công ty TNHH DKSH Việt Nam', 
 N'23 Đại lộ Hữu Nghị, VSIP, Bình Dương', '0274377123', 'info@dksh.com'),

('NCC019', N'Công ty CP Dược phẩm Nam Hà', 
 N'415 Hàn Thuyên, TP. Nam Định', '0228389123', 'contact@namhapharma.com'),

('NCC020', N'Công ty CP Dược phẩm CPC1 Hà Nội', 
 N'356 Giải Phóng, Hà Nội', '0243867134', 'info@cpc1hn.com.vn')


-- SELECT * from NhaCungCap
-- DELETE FROM NhaCungCap;

GO

CREATE TABLE LoaiSanPham (
    MaLoaiSP VARCHAR(10) NOT NULL PRIMARY KEY,
    TenLoaiSP NVARCHAR(255) NOT NULL,
    MoTa NVARCHAR(500)
)

INSERT INTO LoaiSanPham (MaLoaiSP, TenLoaiSP, MoTa)
VALUES
('LSP001', N'Thuốc giảm đau - hạ sốt', N'Paracetamol, Ibuprofen và các thuốc giảm đau thông dụng'),
('LSP002', N'Thuốc kháng sinh', N'Các loại kháng sinh theo toa bác sĩ'),
('LSP003', N'Thuốc cảm cúm', N'Thuốc điều trị cảm, ho, sổ mũi, viêm họng'),
('LSP004', N'Thuốc tiêu hóa', N'Thuốc đau dạ dày, tiêu chảy, táo bón'),
('LSP005', N'Thuốc tim mạch', N'Thuốc huyết áp, mỡ máu, hỗ trợ tim'),
('LSP006', N'Thuốc tiểu đường', N'Thuốc điều trị và kiểm soát đường huyết'),
('LSP007', N'Thuốc bổ & Vitamin', N'Vitamin tổng hợp, khoáng chất, thực phẩm bổ sung'),
('LSP008', N'Dụng cụ y tế', N'Nhiệt kế, máy đo huyết áp, bông băng, khẩu trang'),
('LSP009', N'Mỹ phẩm dược', N'Sữa rửa mặt, kem trị mụn, kem dưỡng da y khoa'),
('LSP010', N'Sữa & Dinh dưỡng y học', N'Sữa cho người bệnh, người già, trẻ em'),
('LSP011', N'Thuốc da liễu', N'Thuốc trị nấm, viêm da, dị ứng'),
('LSP012', N'Thuốc mắt - tai - mũi - họng', N'Thuốc nhỏ mắt, nhỏ mũi, xịt họng'),
('LSP013', N'Thuốc xương khớp', N'Thuốc giảm đau khớp, hỗ trợ xương'),
('LSP014', N'Thuốc phụ khoa', N'Thuốc điều trị viêm nhiễm phụ khoa'),
('LSP015', N'Thuốc nam & đông y', N'Sản phẩm đông y, thuốc thảo dược'),
('LSP016', N'Thuốc thần kinh', N'Thuốc hỗ trợ điều trị mất ngủ, lo âu, trầm cảm theo chỉ định'),
('LSP017', N'Thuốc hô hấp', N'Thuốc điều trị hen suyễn, viêm phế quản, xịt mũi dị ứng'),
('LSP018', N'Thuốc nội tiết', N'Thuốc điều trị rối loạn nội tiết, tuyến giáp'),
('LSP019', N'Thuốc chống dị ứng', N'Thuốc kháng histamine và điều trị phản ứng dị ứng'),
('LSP020', N'Thuốc sát trùng & khử khuẩn', N'Cồn y tế, dung dịch sát khuẩn, oxy già')


-- SELECT * FROM LoaiSanPham

GO

-- ĐÃ SỬA LẠI MaLoaiSP CHO ĐÚNG
CREATE TABLE SanPham (
    MaSP VARCHAR(10) NOT NULL PRIMARY KEY,
    TenSP NVARCHAR(255) NOT NULL,
    MaNCC VARCHAR(10) NOT NULL,
    MaLoaiSP VARCHAR(10) NOT NULL,
    GiaBan DECIMAL(18,2) NOT NULL,
    SoLuong INT DEFAULT 0,
    DonViTinh NVARCHAR(50) NOT NULL,
    HanSuDung NVARCHAR(10),
    HinhAnh VARCHAR(500),
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC),
    FOREIGN KEY (MaLoaiSP) REFERENCES LoaiSanPham(MaLoaiSP)
)

GO

-- PHẦN INSERT SanPham ĐÃ ĐƯỢC SỬA CHỮA
INSERT INTO SanPham 
(MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
-- LSP001: Thuốc giảm đau - hạ sốt
('SP001', N'Paracetamol 500mg', 'NCC001', 'LSP001', 15000, 200, N'Hộp', N'36 tháng', 'paracetamol.png'),
('SP002', N'Panadol Extra', 'NCC001', 'LSP001', 25000, 150, N'Hộp', N'36 tháng', 'panadol.png'),

-- LSP002: Thuốc kháng sinh
('SP003', N'Amoxicillin 500mg', 'NCC002', 'LSP002', 40000, 100, N'Hộp', N'24 tháng', 'amoxicillin.png'),
('SP004', N'Cefixime 200mg', 'NCC002', 'LSP002', 85000, 80, N'Hộp', N'24 tháng', 'cefixime.png'),

-- LSP003: Thuốc cảm cúm (ĐÃ SỬA: trước đây SP009, SP010 nằm ở LSP005)
('SP009', N'Siro ho Prospan', 'NCC005', 'LSP003', 75000, 50, N'Chai', N'24 tháng', 'prospan.png'),
('SP010', N'Tiffy Cảm Cúm', 'NCC005', 'LSP003', 35000, 90, N'Hộp', N'36 tháng', 'tiffy.png'),

-- LSP004: Thuốc tiêu hóa
('SP007', N'Men tiêu hóa Enterogermina', 'NCC004', 'LSP004', 120000, 70, N'Hộp', N'24 tháng', 'enterogermina.png'),
('SP008', N'Berberin 100mg', 'NCC004', 'LSP004', 30000, 100, N'Hộp', N'36 tháng', 'berberin.png'),

-- LSP007: Thuốc bổ & Vitamin (ĐÃ SỬA: trước đây SP005, SP006 nằm ở LSP003)
('SP005', N'Vitamin C 1000mg', 'NCC003', 'LSP007', 90000, 120, N'Lọ', N'36 tháng', 'vitamin_c.png'),
('SP006', N'Vitamin Tổng Hợp Centrum', 'NCC003', 'LSP007', 250000, 60, N'Lọ', N'36 tháng', 'centrum.png'),

-- LSP008: Dụng cụ y tế (ĐÃ SỬA: trước đây SP011-SP014 nằm ở LSP006, LSP007)
('SP011', N'Nhiệt kế điện tử Omron', 'NCC006', 'LSP008', 120000, 40, N'Cái', N'60 tháng', 'nhietke.png'),
('SP012', N'Máy đo huyết áp Omron', 'NCC006', 'LSP008', 850000, 20, N'Cái', N'60 tháng', 'maydo_huyetap.png'),
('SP013', N'Bông gòn y tế', 'NCC007', 'LSP008', 20000, 150, N'Gói', N'60 tháng', 'bong_gon.png'),
('SP014', N'Gạc tiệt trùng', 'NCC007', 'LSP008', 15000, 200, N'Hộp', N'60 tháng', 'gac_tiet_trung.png'),

-- LSP018: Thuốc nội tiết (ĐÃ SỬA: trước đây SP019, SP020 nằm ở LSP010)
('SP019', N'Glucophage 500mg', 'NCC010', 'LSP018', 65000, 70, N'Hộp', N'36 tháng', 'glucophage.png'),
('SP020', N'Euthyrox 50mcg', 'NCC010', 'LSP018', 85000, 60, N'Hộp', N'36 tháng', 'euthyrox.png'),

-- LSP019: Thuốc chống dị ứng (ĐÃ SỬA: trước đây SP015, SP016 nằm ở LSP008)
('SP015', N'Loratadin 10mg', 'NCC008', 'LSP019', 45000, 100, N'Hộp', N'36 tháng', 'loratadin.png'),
('SP016', N'Cetirizine 10mg', 'NCC008', 'LSP019', 40000, 120, N'Hộp', N'36 tháng', 'cetirizine.png'),

-- LSP020: Thuốc sát trùng & khử khuẩn (ĐÃ SỬA: trước đây SP017, SP018 nằm ở LSP009)
('SP017', N'Cồn 70 độ', 'NCC009', 'LSP020', 25000, 100, N'Chai', N'60 tháng', 'con70.png'),
('SP018', N'Dung dịch Betadine', 'NCC009', 'LSP020', 35000, 80, N'Chai', N'36 tháng', 'betadine.png');


-- SELECT * from SanPham

GO

CREATE TABLE PhieuNhap (
    MaPhieuNhap VARCHAR(10) NOT NULL PRIMARY KEY,
    NgayNhap DATETIME DEFAULT GETDATE(), 
    MaNCC VARCHAR(10) NOT NULL,
    MaNhanVien VARCHAR(10) NOT NULL,
    GhiChu NVARCHAR(255),
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC),
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE
)

GO

INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNCC, MaNhanVien, GhiChu)
VALUES
('PN001', '2025-11-13 08:30:00', 'NCC001', 'NV001', N'Nhập thuốc giảm đau và hạ sốt'),
('PN002', '2025-11-13 09:00:00', 'NCC002', 'NV001', N'Nhập thuốc kháng sinh theo đơn'),
('PN003', '2025-11-14 10:15:00', 'NCC003', 'NV001', N'Nhập vitamin và thực phẩm chức năng'),
('PN004', '2025-11-15 14:00:00', 'NCC004', 'NV001', N'Nhập thuốc tiêu hóa'),
('PN005', '2025-11-15 16:30:00', 'NCC005', 'NV001', N'Nhập thuốc ho và cảm cúm');


INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNCC, MaNhanVien, GhiChu)
VALUES
('PN006', '2025-11-13 08:45:00', 'NCC006', 'NV002', N'Nhập thiết bị y tế'),
('PN007', '2025-11-13 11:00:00', 'NCC007', 'NV002', N'Nhập bông gạc và vật tư y tế'),
('PN008', '2025-11-14 13:20:00', 'NCC008', 'NV002', N'Nhập thuốc chống dị ứng'),
('PN009', '2025-11-15 09:40:00', 'NCC009', 'NV002', N'Nhập dung dịch sát khuẩn'),
('PN010', '2025-11-15 15:10:00', 'NCC010', 'NV002', N'Nhập thuốc nội tiết');


INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNCC, MaNhanVien, GhiChu)
VALUES
('PN011', '2025-11-13 17:00:00', 'NCC002', 'NV003', N'Quản lý duyệt nhập thuốc kê đơn'),
('PN012', '2025-11-14 16:45:00', 'NCC003', 'NV003', N'Nhập bổ sung vitamin bán chạy'),
('PN013', '2025-11-14 18:00:00', 'NCC008', 'NV003', N'Kiểm kê và nhập thuốc dị ứng'),
('PN014', '2025-11-15 17:20:00', 'NCC004', 'NV003', N'Nhập thêm thuốc tiêu hóa'),
('PN015', '2025-11-15 19:00:00', 'NCC005', 'NV003', N'Kiểm tra và nhập thuốc ho cảm');

-- DELETE FROM PhieuNhap

GO

CREATE TABLE ChiTietPhieuNhap (
    MaPhieuNhap VARCHAR(10 ) NOT NULL,
    MaSP VARCHAR(10) NOT NULL,  
    SoLuong INT NOT NULL,    
    DonGia DECIMAL(18,2) NOT NULL,      
    ThanhTien AS (SoLuong * DonGia) PERSISTED,                   
    PRIMARY KEY (MaPhieuNhap, MaSP),         
    FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap) ON DELETE CASCADE,
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP) ON DELETE CASCADE
)

GO

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN001', 'SP001', 200, 10000),
('PN001', 'SP002', 150, 18000);


INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN002', 'SP003', 100, 30000),
('PN002', 'SP004', 80, 70000);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN003', 'SP005', 120, 70000),
('PN003', 'SP006', 60, 200000);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN004', 'SP007', 70, 95000),
('PN004', 'SP008', 100, 20000);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN005', 'SP009', 50, 60000),
('PN005', 'SP010', 90, 25000);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN006', 'SP011', 40, 95000),
('PN006', 'SP012', 20, 700000);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN007', 'SP013', 150, 15000),
('PN007', 'SP014', 200, 10000);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN008', 'SP015', 100, 35000),
('PN008', 'SP016', 120, 30000);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN009', 'SP017', 100, 18000),
('PN009', 'SP018', 80, 25000);

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN010', 'SP019', 70, 50000),
('PN010', 'SP020', 60, 70000);


-- select * from ChiTietPhieuNhap
-- DELETE FROM ChiTietPhieuNhap

GO

CREATE TABLE KhachHang (
    MaKH VARCHAR(10) NOT NULL PRIMARY KEY,       
    TenKH NVARCHAR(100) NOT NULL,    
    NamSinh INT,                  
    Sdt VARCHAR(10) NOT NULL UNIQUE,
    GioiTinh NVARCHAR(10) NOT NULL,  
    DiaChi NVARCHAR(255),                     
    DiemTichLuy INT DEFAULT 0
)

GO

INSERT INTO KhachHang (MaKH, TenKH, NamSinh, Sdt, GioiTinh, DiaChi, DiemTichLuy)
VALUES 
('KH000', N'Khách vãng lai', NULL, '0000000000', N'Không rõ', NULL, 0);

INSERT INTO KhachHang (MaKH, TenKH, NamSinh, Sdt, GioiTinh, DiaChi, DiemTichLuy)
VALUES 
('KH001', N'Nguyễn Văn An', 1998, '0987654322', N'Nam', N'Quận 1, TP.HCM', 1200),
('KH002', N'Trần Thị Bích', 2001, '0987654321', N'Nữ', N'Quận 3, TP.HCM', 350),
('KH003', N'Lê Văn Minh', 1990, '0123456789', N'Nam', N'Quận Gò Vấp, TP.HCM', 2100),
('KH004', N'Phạm Thu Hằng', 2003, '0369888777', N'Nữ', N'Quận Tân Bình, TP.HCM', 50),
('KH005', N'Đặng Hoàng Long', 1985, '0933444555', N'Nam', N'TP. Thủ Đức, TP.HCM', 0),
('KH006', N'Nguyễn Thị Mai', 1995, '0911222333', N'Nữ', N'Quận 5, TP.HCM', 780),
('KH007', N'Phan Quốc Huy', 1988, '0909988776', N'Nam', N'Quận Bình Thạnh, TP.HCM', 1500),
('KH008', N'Võ Thanh Tâm', 2000, '0388123456', N'Nữ', N'Quận 10, TP.HCM', 420),
('KH009', N'Bùi Gia Hưng', 1993, '0977555444', N'Nam', N'Quận 7, TP.HCM', 980),
('KH010', N'Lý Ngọc Trâm', 1999, '0356677889', N'Nữ', N'Quận Phú Nhuận, TP.HCM', 260),
('KH011', N'Hoàng Minh Đức', 1982, '0944333222', N'Nam', N'Quận 12, TP.HCM', 3200),
('KH012', N'Trịnh Mỹ Linh', 2004, '0377999888', N'Nữ', N'Quận Bình Tân, TP.HCM', 110),
('KH013', N'Đoàn Nhật Quang', 1997, '0966123456', N'Nam', N'Hóc Môn, TP.HCM', 640),
('KH014', N'Ngô Thảo Vy', 2002, '0333444556', N'Nữ', N'Quận 6, TP.HCM', 75),
('KH015', N'Phùng Anh Khoa', 1991, '0922333444', N'Nam', N'Quận 8, TP.HCM', 1850)

GO

CREATE TABLE HoaDon (
    MaHD VARCHAR(10) NOT NULL PRIMARY KEY, 
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    TongTien DECIMAL(18,2) DEFAULT 0,
    DiemTichLuy INT DEFAULT 0,  
    PhuongThucThanhToan NVARCHAR(50) NOT NULL, 
    MaNV VARCHAR(10) NOT NULL, 
    MaKH VARCHAR(10) NOT NULL,
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE,
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH) ON DELETE CASCADE
)

GO

INSERT INTO HoaDon (MaHD, NgayLap, TongTien, DiemTichLuy, PhuongThucThanhToan, MaNV, MaKH)
VALUES 
('HD00001', '2025-11-13 00:00:00', 26520, 0, N'Tiền mặt', 'NV001', 'KH000'),
('HD00002', '2025-11-13 00:00:00', 22950, 22, N'Chuyển khoản', 'NV001', 'KH002'),
('HD00003', '2025-11-14 00:00:00', 16320, 0, N'Tiền mặt', 'NV001', 'KH000'),
('HD00004', '2025-11-14 00:00:00', 55080, 54, N'Quẹt thẻ', 'NV001', 'KH003'),
('HD00005', '2025-11-15 00:00:00', 61200, 60, N'Chuyển khoản', 'NV001', 'KH004')

INSERT INTO HoaDon (MaHD, NgayLap, TongTien, DiemTichLuy, PhuongThucThanhToan, MaNV, MaKH)
VALUES 
('HD00006', '2025-11-13 00:00:00', 55080, 54, N'Quẹt thẻ', 'NV002', 'KH002'),
('HD00007', '2025-11-13 00:00:00', 22440, 22, N'Tiền mặt', 'NV002', 'KH005'),
('HD00008', '2025-11-14 00:00:00', 40800, 0, N'Tiền mặt', 'NV002', 'KH000'),
('HD00009', '2025-11-14 17:30:00', 37740, 37, N'Chuyển khoản', 'NV002', 'KH001'),
('HD00010', '2025-11-15 00:00:00', 35700, 0, N'Quẹt thẻ', 'NV002', 'KH000')

INSERT INTO HoaDon (MaHD, NgayLap, TongTien, DiemTichLuy, PhuongThucThanhToan, MaNV, MaKH)
VALUES 
('HD00011', '2025-11-13 00:00:00', 81600, 80, N'Quẹt thẻ', 'NV003', 'KH004'),
('HD00012', '2025-11-14 00:00:00', 102000, 0, N'Tiền mặt', 'NV003', 'KH000'),
('HD00013', '2025-11-14 00:00:00', 66300, 65, N'Chuyển khoản', 'NV003', 'KH005'),
('HD00014', '2025-11-15 00:00:00', 285600, 0, N'Quẹt thẻ', 'NV003', 'KH000'),
('HD00015', '2025-11-15 00:00:00', 48960, 48, N'Tiền mặt', 'NV003', 'KH001')

INSERT INTO HoaDon (MaHD, NgayLap, TongTien, DiemTichLuy, PhuongThucThanhToan, MaNV, MaKH)
VALUES 
('HD00016', '2025-11-16 09:15:00', 95000, 95, N'Chuyển khoản', 'NV001', 'KH006'),
('HD00017', '2025-11-16 10:20:00', 180000, 180, N'Quẹt thẻ', 'NV001', 'KH007'),
('HD00018', '2025-11-16 11:05:00', 45000, 0, N'Tiền mặt', 'NV001', 'KH000'),
('HD00019', '2025-11-16 14:40:00', 72000, 72, N'Chuyển khoản', 'NV001', 'KH008'),
('HD00020', '2025-11-16 16:00:00', 125000, 125, N'Quẹt thẻ', 'NV001', 'KH009');

INSERT INTO HoaDon (MaHD, NgayLap, TongTien, DiemTichLuy, PhuongThucThanhToan, MaNV, MaKH)
VALUES 
('HD00021', '2025-11-16 09:30:00', 60000, 60, N'Tiền mặt', 'NV002', 'KH010'),
('HD00022', '2025-11-16 13:00:00', 240000, 240, N'Chuyển khoản', 'NV002', 'KH011'),
('HD00023', '2025-11-16 15:10:00', 35000, 0, N'Tiền mặt', 'NV002', 'KH000'),
('HD00024', '2025-11-16 17:25:00', 88000, 88, N'Quẹt thẻ', 'NV002', 'KH012'),
('HD00025', '2025-11-16 19:10:00', 150000, 150, N'Chuyển khoản', 'NV002', 'KH013');

INSERT INTO HoaDon (MaHD, NgayLap, TongTien, DiemTichLuy, PhuongThucThanhToan, MaNV, MaKH)
VALUES 
('HD00026', '2025-11-16 08:45:00', 300000, 300, N'Quẹt thẻ', 'NV003', 'KH014'),
('HD00027', '2025-11-16 10:50:00', 52000, 52, N'Tiền mặt', 'NV003', 'KH015'),
('HD00028', '2025-11-16 12:30:00', 99000, 99, N'Chuyển khoản', 'NV003', 'KH006'),
('HD00029', '2025-11-16 15:40:00', 42000, 0, N'Tiền mặt', 'NV003', 'KH000'),
('HD00030', '2025-11-16 18:00:00', 175000, 175, N'Quẹt thẻ', 'NV003', 'KH007');

-- DELETE FROM HoaDon

GO

CREATE TABLE ChiTietHoaDon (
    MaHD VARCHAR(10) NOT NULL,
    MaSP VARCHAR(10) NOT NULL, 
    SoLuong INT NOT NULL CHECK(SoLuong > 0),
    DonGia DECIMAL(18,2) NOT NULL, 
    ThanhTien AS (SoLuong * DonGia) PERSISTED, 
    PRIMARY KEY (maHD, maSP),
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD) ON DELETE CASCADE,
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP) ON DELETE CASCADE
)

GO

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia)
VALUES 
('HD00001', 'SP001', 2, 15000),
('HD00001', 'SP005', 1, 90000),
('HD00002', 'SP010', 1, 35000),
('HD00002', 'SP015', 1, 45000),
('HD00003', 'SP009', 1, 75000),
('HD00004', 'SP003', 1, 40000),
('HD00004', 'SP004', 1, 85000),
('HD00005', 'SP006', 1, 250000);


INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia)
VALUES 
('HD00006', 'SP011', 1, 120000),
('HD00006', 'SP008', 2, 30000),
('HD00007', 'SP007', 1, 120000),
('HD00008', 'SP017', 2, 25000),
('HD00009', 'SP005', 1, 90000),
('HD00009', 'SP016', 1, 40000),
('HD00010', 'SP013', 2, 20000);


INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia)
VALUES 
('HD00011', 'SP012', 1, 850000),
('HD00012', 'SP019', 1, 65000),
('HD00013', 'SP014', 2, 15000),
('HD00013', 'SP010', 1, 35000),
('HD00014', 'SP012', 1, 850000),
('HD00015', 'SP002', 2, 25000),
('HD00015', 'SP015', 1, 45000);

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia)
VALUES
('HD00016', 'SP003', 1, 40000),
('HD00016', 'SP009', 1, 75000),
('HD00017', 'SP012', 1, 850000),
('HD00018', 'SP001', 2, 15000),
('HD00018', 'SP017', 1, 25000),
('HD00019', 'SP005', 1, 90000),
('HD00020', 'SP007', 1, 120000);