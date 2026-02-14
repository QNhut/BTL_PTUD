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
        ('NV004', N'Hồ Hoàng Minh', '0954342657', '081105034543', N'12 Đinh Tiên Hoàng, Q1, TP.HCM', N'Nam', N'Nhân viên bán hàng', N'Ca tối', N'Đang làm', 'image_nv4.png')

-- select * from NhanVien

GO

CREATE TABLE TaiKhoan (
    TenDangNhap VARCHAR(50) NOT NULL PRIMARY KEY,
    MatKhau VARCHAR(50) NOT NULL,
    VaiTro NVARCHAR(50),
	NgayTao DATETIME DEFAULT GETDATE(),
    TrangThaiOnline BIT DEFAULT 0,
    MaNhanVien VARCHAR(10) NOT NULL UNIQUE,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE
)
GO

INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, MaNhanVien) VALUES ('NQNhut', '123456', N'Nhân viên', 'NV001'),
                                                                        ('NTLuan', '123456', N'Nhân viên', 'NV002'),
                                                                        ('PKKhoa', '123456', N'Quản lý', 'NV003'),
                                                                        ('HHMinh', '123456', N'Nhân viên', 'NV004')

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
('NCC001', N'Công ty TNHH Nước Giải Khát Sài Gòn', 
 N'123 Võ Văn Tần, Quận 3, TP. Hồ Chí Minh', '0901112224', 'info@ngksaigon.com'),

('NCC002', N'Nhà Phân Phối Chính Thức Vinamilk', 
 N'Lô 10, KCN Tân Tạo, Quận Bình Tân, TP. Hồ Chí Minh', '0912223333', 'phanphoi@vinamilk.com.vn'),

('NCC003', N'Công ty TNHH Thực Phẩm Orion Vina', 
 N'KCN Mỹ Phước 2, Bến Cát, Bình Dương', '0983334443', 'lienhe@orionvina.vn'),

('NCC004', N'Công ty Cổ phần Acecook Việt Nam', 
 N'Lô II-3, KCN Tân Bình, Quận Tân Phú, TP. Hồ Chí Minh', '0283815406', 'info@acecook.com.vn'),

('NCC005', N'Bếp Trung Tâm FreshFood Việt', 
 N'45B Cống Quỳnh, Quận 1, TP. Hồ Chí Minh', '0905556664', 'dathang@freshfood.vn'),

('NCC006', N'Công ty TNHH Unilever Việt Nam', 
 N'156 Nguyễn Lương Bằng, Quận 7, TP. Hồ Chí Minh', '0285411889', 'cskh@unilever.com'),

('NCC007', N'Công ty Giấy Sài Gòn', 
 N'KCN Sóng Thần, Dĩ An, Bình Dương', '0907778884', 'kinhdoanh@saigonpaper.com'),

('NCC008', N'Tổng Công Ty Sabeco', 
 N'02 Tôn Đức Thắng, Quận 1, TP. Hồ Chí Minh', '0283829408', 'info@sabeco.com.vn')

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
('LSP001', N'Nước ngọt & Có gas', N'Các loại nước giải khát có ga như cola, soda, nước tăng lực'),
('LSP002', N'Sữa & Chế phẩm từ sữa', N'Sữa tươi, sữa tiệt trùng, sữa chua, phô mai và các sản phẩm liên quan'),
('LSP003', N'Snack & Bim bim', N'Các loại khoai tây chiên, snack ngô, snack hải sản và thực phẩm ăn vặt đóng gói'),
('LSP004', N'Mì ăn liền', N'Các loại mì, phở, miến, hủ tiếu ăn liền nhiều hương vị'),
('LSP005', N'Đồ ăn chế biến sẵn', N'Các món ăn tiện lợi như cơm nắm, bánh mì kẹp, salad dùng trong ngày'),
('LSP006', N'Hóa mỹ phẩm', N'Sản phẩm chăm sóc cá nhân: dầu gội, sữa tắm, sữa rửa mặt và mỹ phẩm cơ bản'),
('LSP007', N'Vệ sinh cá nhân', N'Bàn chải, kem đánh răng, khăn giấy và vật dụng vệ sinh hằng ngày'),
('LSP008', N'Bia', N'Các loại bia nội địa và nhập khẩu với nhiều thương hiệu khác nhau'),
('LSP009', N'Thẻ cào & Dịch vụ', N'Thẻ cào điện thoại, thẻ game và các dịch vụ tiện ích liên quan')

-- SELECT * FROM LoaiSanPham

GO

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

INSERT INTO SanPham (MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
('SP001', N'Coca Cola', 'NCC001', 'LSP001', 10000, 100, N'Lon', N'12 tháng', 'coca_cola.png'),
('SP002', N'Trà Xanh Không Độ', 'NCC001', 'LSP001', 8000, 120, N'Chai', N'9 tháng', 'tra_xanh_khong_do.png'),
('SP003', N'Pepsi', 'NCC001', 'LSP001', 10000, 100, N'Lon', N'12 tháng', 'pepsi.png'),
('SP004', N'Sting Dâu', 'NCC001', 'LSP001', 12000, 80, N'Chai', N'9 tháng', 'sting_dau.png'),
('SP005', N'Nước suối Aquafina', 'NCC001', 'LSP001', 5000, 200, N'Chai', N'12 tháng', 'aquafina.png')

INSERT INTO SanPham (MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
('SP006', N'Sữa tươi Vinamilk', 'NCC002', 'LSP002', 30000, 10, N'Hộp', N'6 tháng', 'vinamilk_tuoi.png'),
('SP007', N'Sữa chua Vinamilk có đường', 'NCC002', 'LSP002', 6000, 150, N'Hộp', N'1 tháng', 'sua_chua_hop.png'),
('SP008', N'Sữa đặc Ông Thọ', 'NCC002', 'LSP002', 22000, 20, N'Lon', N'12 tháng', 'ong_tho.png'),
('SP009', N'Phô mai Bò Cười', 'NCC002', 'LSP002', 35000, 10, N'Hộp', N'9 tháng', 'bo_cuoi.png'),
('SP010', N'Sữa Fami Đậu Nành', 'NCC002', 'LSP002', 20000, 40, N'Hộp', N'6 tháng', 'fami.png');

INSERT INTO SanPham (MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
('SP011', N'Bánh ChocoPie 6 cái', 'NCC003', 'LSP003', 32000, 50, N'Hộp', N'6 tháng', 'choco_pie.png'),
('SP012', N'Snack O''Star Tôm Chua Cay', 'NCC003', 'LSP003', 6000, 150, N'Gói', N'3 tháng', 'ostar_tom.png'),
('SP013', N'Snack O''Star Khoai Tây Lon', 'NCC003', 'LSP003', 25000, 20, N'Lon', N'6 tháng', 'ostar_lon.png'),
('SP014', N'Bánh Gouté Mè', 'NCC003', 'LSP003', 45000, 30, N'Hộp', N'9 tháng', 'goute.png'),
('SP015', N'Thùng ChocoPie 12 cái', 'NCC003', 'LSP003', 60000, 10, N'Thùng', N'6 tháng', 'thung_choco_pie.png')

INSERT INTO SanPham (MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
('SP016', N'Mì Hảo Hảo Tôm Chua Cay', 'NCC004', 'LSP004', 3500, 40, N'Gói', N'3 tháng', 'hao_hao.png'),
('SP017', N'Mì ly Modern Lẩu Thái', 'NCC004', 'LSP004', 10000, 0, N'Hộp', N'3 tháng', 'modern.png'),
('SP018', N'Phở Đệ Nhất Bò', 'NCC004', 'LSP004', 6000, 100, N'Gói', N'3 tháng', 'de_nhat.png'),
('SP019', N'Thùng Mì Hảo Hảo (30 gói)', 'NCC004', 'LSP004', 100000, 20, N'Thùng', N'3 tháng', 'thung_hao_hao.png'),
('SP020', N'Miến Phú Hương Yến', 'NCC004', 'LSP004', 8000, 70, N'Gói', N'6 tháng', 'phu_huong.png')

INSERT INTO SanPham (MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
('SP021', N'Cơm nấm cá ngừ', 'NCC005', 'LSP005', 12000, 30, N'Cái', N'1 tháng', 'com_nam_ca_ngu.png'),
('SP022', N'Bánh mì Sandwich Gà', 'NCC005', 'LSP005', 18000, 25, N'Gói', N'1 tháng', 'sandwich_ga.png'),
('SP023', N'Salad trộn cá ngừ', 'NCC005', 'LSP005', 30000, 15, N'Hộp', N'1 tháng', 'salad_ca_ngu.png'),
('SP024', N'Hot dog phô mai', 'NCC005', 'LSP005', 15000, 40, N'Cái', N'1 tháng', 'hotdog.png'),
('SP025', N'Cơm cuộn Kimbap', 'NCC005', 'LSP005', 25000, 20, N'Hộp', N'1 tháng', 'kimbap.png')

INSERT INTO SanPham (MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
('SP026', N'Dầu gội Clear Bạc Hà', 'NCC006', 'LSP006', 80000, 50, N'Chai', N'12 tháng', 'clear.png'),
('SP027', N'Sữa tắm Lifebuoy', 'NCC006', 'LSP006', 120000, 30, N'Chai', N'12 tháng', 'lifebuoy.png'),
('SP028', N'Kem đánh răng P/S', 'NCC006', 'LSP006', 25000, 100, N'Hộp', N'12 tháng', 'ps.png'),
('SP029', N'Dầu gội Sunsilk', 'NCC006', 'LSP006', 2000, 200, N'Gói', N'12 tháng', 'sunsilk_goi.png'),
('SP030', N'Sữa rửa mặt Pond''s', 'NCC006', 'LSP006', 60000, 40, N'Hộp', N'12 tháng', 'ponds.png')

INSERT INTO SanPham (MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
('SP031', N'Khăn giấy Sài Gòn', 'NCC007', 'LSP007', 15000, 100, N'Gói', N'12 tháng', 'khan_giay_sai_gon.png'),
('SP032', N'Giấy vệ sinh cuộn An An', 'NCC007', 'LSP007', 4000, 300, N'Cái', N'12 tháng', 'giay_an_an.png'),
('SP033', N'Khăn ướt Bobby', 'NCC007', 'LSP007', 30000, 60, N'Gói', N'12 tháng', 'khan_uot_bobby.png'),
('SP034', N'Băng vệ sinh Diana', 'NCC007', 'LSP007', 18000, 10, N'Gói', N'12 tháng', 'diana.png'),
('SP035', N'Tã quần Bobby', 'NCC007', 'LSP007', 150000, 20, N'Gói', N'12 tháng', 'ta_bobby.png')

INSERT INTO SanPham (MaSP, TenSP, MaNCC, MaLoaiSP, GiaBan, SoLuong, DonViTinh, HanSuDung, HinhAnh)
VALUES
('SP036', N'Bia Sài Gòn', 'NCC008', 'LSP008', 12000, 0, N'Lon', N'12 tháng', 'bia_sai_gon.png'),
('SP037', N'Bia Tiger', 'NCC008', 'LSP008', 16000, 150, N'Lon', N'12 tháng', 'bia_tiger.png'),
('SP038', N'Bia Heineken', 'NCC008', 'LSP008', 20000, 150, N'Chai', N'12 tháng', 'bia_heineken.png'),
('SP039', N'Thùng Bia Sài Gòn (24 lon)', 'NCC008', 'LSP008', 280000, 30, N'Thùng', N'12 tháng', 'thung_bia_sai_gon.png'),
('SP040', N'Bia 333', 'NCC008', 'LSP008', 11000, 100, N'Lon', N'12 tháng', 'bia_333.png')


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
('PN001', '2025-11-13 00:00:00', 'NCC001', 'NV001', N'Nhập hàng nước giải khát đầu tuần'),
('PN002', '2025-11-13 00:00:00', 'NCC004', 'NV001', N'Nhập mì gói các loại'),
('PN003', '2025-11-14 00:00:00', 'NCC006', 'NV001', N'Nhập hóa mỹ phẩm'),
('PN004', '2025-11-15 00:00:00', 'NCC008', 'NV001', N'Nhập bia cho cuối tuần'),
('PN005', '2025-11-15 00:00:00', 'NCC002', 'NV001', N'Bổ sung sữa')

INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNCC, MaNhanVien, GhiChu)
VALUES
('PN006', '2025-11-13 00:00:00', 'NCC003', 'NV002', N'Nhập snack và bánh kẹo'),
('PN007', '2025-11-13 00:00:00', 'NCC005', 'NV002', N'Nhập đồ ăn sẵn cho ca chiều'),
('PN008', '2025-11-14 00:00:00', 'NCC007', 'NV002', N'Nhập khăn giấy, vật dụng vệ sinh'),
('PN009', '2025-11-15 00:00:00', 'NCC001', 'NV002', N'Nhập thêm nước suối'),
('PN010', '2025-11-15 00:00:00', 'NCC004', 'NV002', N'Nhập thêm mì ly')

INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNCC, MaNhanVien, GhiChu)
VALUES
('PN011', '2025-11-13 00:00:00', 'NCC002', 'NV003', N'Quản lý duyệt nhập sữa'),
('PN012', '2025-11-14 00:00:00', 'NCC006', 'NV003', N'Nhập hàng Unilever (deal mới)'),
('PN013', '2025-11-14 00:00:00', 'NCC008', 'NV003', N'Kiểm kê và nhập bia'),
('PN014', '2025-11-15 00:00:00', 'NCC003', 'NV003', N'Nhập hàng Orion (deal mới)'),
('PN015', '2025-11-15 00:00:00', 'NCC005', 'NV003', N'Kiểm tra và nhập đồ ăn sẵn')

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
('PN001', 'SP001', 100, 7000),
('PN001', 'SP002', 50, 5000), 
('PN002', 'SP016', 200, 3000), 
('PN002', 'SP017', 50, 8000),   
('PN003', 'SP026', 30, 65000),
('PN003', 'SP028', 50, 19000), 
('PN004', 'SP036', 120, 10000),
('PN004', 'SP037', 120, 13500), 
('PN005', 'SP006', 40, 25000), 
('PN005', 'SP007', 100, 4500)

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN006', 'SP011', 50, 26000), 
('PN006', 'SP012', 100, 4500), 
('PN007', 'SP021', 40, 8000),  
('PN007', 'SP022', 30, 13000), 
('PN008', 'SP031', 50, 11000), 
('PN008', 'SP032', 200, 3000),  
('PN009', 'SP005', 150, 3500), 
('PN010', 'SP019', 20, 90000), 
('PN010', 'SP020', 30, 6000)

INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSP, SoLuong, DonGia)
VALUES
('PN011', 'SP008', 30, 18000),
('PN011', 'SP009', 20, 29000), 
('PN012', 'SP027', 30, 100000), 
('PN012', 'SP030', 25, 48000),  
('PN013', 'SP038', 100, 17000), 
('PN013', 'SP039', 10, 260000), 
('PN014', 'SP014', 20, 38000), 
('PN014', 'SP015', 10, 52000),  
('PN015', 'SP024', 40, 10000), 
('PN015', 'SP025', 30, 18000)

-- select * froKm ChiTietPhieuNhap
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
('KH005', N'Đặng Hoàng Long', 1985, '0933444555', N'Nam', N'TP. Thủ Đức, TP.HCM', 0)

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
('HD00001', 'SP001', 2, 10000), 
('HD00001', 'SP012', 1, 6000),
('HD00002', 'SP016', 5, 3500), 
('HD00002', 'SP005', 1, 5000),
('HD00003', 'SP037', 1, 16000),
('HD00004', 'SP006', 1, 30000),
('HD00004', 'SP007', 4, 6000),
('HD00005', 'SP030', 1, 60000)

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia)
VALUES 
('HD00006', 'SP011', 1, 32000), 
('HD00006', 'SP008', 1, 22000),
('HD00007', 'SP021', 1, 12000), 
('HD00007', 'SP003', 1, 10000),
('HD00008', 'SP038', 2, 20000), 
('HD00009', 'SP028', 1, 25000), 
('HD00009', 'SP032', 3, 4000),
('HD00010', 'SP017', 2, 10000), 
('HD00010', 'SP024', 1, 15000)

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia)
VALUES 
('HD00011', 'SP026', 1, 80000),
('HD00012', 'SP019', 1, 100000), 
('HD00013', 'SP014', 1, 45000),  
('HD00013', 'SP010', 1, 20000),
('HD00014', 'SP039', 1, 280000), 
('HD00015', 'SP022', 2, 18000), 
('HD00015', 'SP004', 1, 12000)
