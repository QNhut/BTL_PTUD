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
        ('NV010', N'Võ Thị Ngọc Anh', '0945678123', '082805017666', N'56 Lý Thường Kiệt, Q10, TP.HCM', N'Nữ', N'Nhân viên bán hàng', N'Ca tối', N'Đang làm', 'image_nv10.png'),
        ('NV011', N'Tài Khoản Test', '0900000011', '082905017777', N'1 Test Street, Q1, TP.HCM', N'Nam', N'Nhân viên bán hàng', N'Ca sáng', N'Đang làm', 'users/people.png')

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
('VTNAnh', '123456', 'NV010'),
('TKTest', '123456', 'NV011')

-- Reset online state de test login/logout theo service
UPDATE TaiKhoan SET TrangThaiOnline = 0

-- Tai khoan test khuyen nghi:
-- username: TKTest
-- password: 123456

-- Query kiem tra nhanh trang thai sau login/logout
-- SELECT TenDangNhap, TrangThaiOnline, MaNhanVien FROM TaiKhoan ORDER BY TenDangNhap

