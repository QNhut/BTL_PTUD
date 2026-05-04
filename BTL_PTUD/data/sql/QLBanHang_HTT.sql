CREATE DATABASE QLBANHANG
GO

USE QLBANHANG
GO
-- Chuẩn bị bản chức vụ
CREATE TABLE ChucVu (
    MaChucVu VARCHAR(15) NOT NULL PRIMARY KEY,
    TenChucVu NVARCHAR(50) NOT NULL,
    MoTa NVARCHAR(100)
);

GO

-- Chuẩn bị data cho chức vụ
INSERT INTO ChucVu (MaChucVu, TenChucVu, MoTa)
VALUES
    ('CV2025001', N'Admin',N'Quản trị hệ thống, toàn quyền truy cập'),
    ('CV2025002', N'Quản lý cửa hàng',N'Quản lý hoạt động và nhân sự cửa hàng'),
    ('CV2025003', N'Nhân viên bán hàng',N'Tư vấn và bán hàng trực tiếp cho khách'),
    ('CV2025004', N'Nhân viên kho',N'Quản lý nhập xuất và kiểm kê hàng hóa');

GO

-- Chuẩn bị bản nhân viên
CREATE TABLE NhanVien (
    MaNhanVien VARCHAR(15) NOT NULL PRIMARY KEY,
    TenNhanVien NVARCHAR(60) NOT NULL,
    GioiTinh BIT NOT NULL,
    SoDienThoai VARCHAR(10) NOT NULL,
    Email VARCHAR(255) NOT NULL,
    CCCD VARCHAR(12) NOT NULL,
    DiaChi NVARCHAR(255),
    TrangThai BIT,
    MaChucVu VARCHAR(15) NOT NULL,
    HinhAnh VARCHAR(255)
)

GO

-- Chuẩn bị data nhân viên
INSERT INTO NhanVien (MaNhanVien, TenNhanVien, GioiTinh, SoDienThoai, Email, CCCD, DiaChi, TrangThai, MaChucVu, HinhAnh)
VALUES 
    ('NV2025001', N'Nguyễn Quốc Nhựt',    1, '0367627363', 'nv001@gmail.com', '082205017345', N'256 Dương Quảng Hàm, Q Gò Vấp, TP.HCM',   1, 'CV2025002', 'nhanVien/nhanvien1.png'),
    ('NV2025002', N'Nguyễn Thế Luân',     1, '0365868345', 'nv002@gmail.com', '081205017676', N'256 Nguyễn Văn Khối, Q Gò Vấp, TP.HCM',   1, 'CV2025002', 'nhanVien/nhanvien2.png'),
    ('NV2025003', N'Phan Khánh Khoa',     1, '0966762657', 'nv003@gmail.com', '081105034345', N'123 Võ Thị Sáu, Q1, TP.HCM',              1, 'CV2025002', 'nhanVien/nhanvien3.png'),
    ('NV2025004', N'Hồ Hoàng Minh',       1, '0954342657', 'nv004@gmail.com', '081105034543', N'12 Đinh Tiên Hoàng, Q1, TP.HCM',          1, 'CV2025003', 'nhanVien/nhanvien4.png'),
    ('NV2025005', N'Trần Minh Hoàng',     1, '0978123456', 'nv005@gmail.com', '082305017111', N'45 Lê Văn Sỹ, Q3, TP.HCM',               1, 'CV2025003', 'nhanVien/nhanvien5.png'),
    ('NV2025006', N'Lê Thị Thanh Hằng',   0, '0987654321', 'nv006@gmail.com', '082405017222', N'78 Phan Xích Long, Phú Nhuận, TP.HCM',    1, 'CV2025003', 'nhanVien/nhanvien6.png'),
    ('NV2025007', N'Ngô Đức Anh',         1, '0935123456', 'nv007@gmail.com', '082505017333', N'22 Nguyễn Oanh, Gò Vấp, TP.HCM',         1, 'CV2025003', 'nhanVien/nhanvien7.png'),
    ('NV2025008', N'Phạm Thị Mỹ Linh',    0, '0912345678', 'nv008@gmail.com', '082605017444', N'90 Cộng Hòa, Tân Bình, TP.HCM',          1, 'CV2025004', 'nhanVien/nhanvien8.png'),
    ('NV2025009', N'Đặng Quốc Bảo',       1, '0923456789', 'nv009@gmail.com', '082705017555', N'11 Trường Chinh, Tân Bình, TP.HCM',      1, 'CV2025004', 'nhanVien/nhanvien9.png'),
    ('NV2025010', N'Võ Thị Ngọc Anh',     0, '0945678123', 'nv010@gmail.com', '082805017666', N'56 Lý Thường Kiệt, Q10, TP.HCM',         1, 'CV2025004', 'nhanVien/nhanvien10.png'),
    ('NV2025011', N'Tài Khoản Test',       1, '0900000011', 'nv011@gmail.com', '082905017777', N'1 Test Street, Q1, TP.HCM',              1, 'CV2025001', 'users/people.png');

-- select * from NhanVien

GO

CREATE TABLE TaiKhoan (
    TenTaiKhoan VARCHAR(50) NOT NULL PRIMARY KEY,
    MatKhau VARCHAR(50) NOT NULL,
	NgayTao DATETIME DEFAULT GETDATE(),
    TrangThai BIT DEFAULT 0,
    MaNhanVien VARCHAR(15) NOT NULL UNIQUE,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE
)
GO

INSERT INTO TaiKhoan (TenTaiKhoan, MatKhau, MaNhanVien)
VALUES
('NQNhut', '123456', 'NV2025001'),
('NTLuan', '123456', 'NV2025002'),
('PKKhoa', '123456', 'NV2025003'),
('HHMinh', '123456', 'NV2025004'),
('TMHoang', '123456', 'NV2025005'),
('LTTHang', '123456', 'NV2025006'),
('NDAnh', '123456', 'NV2025007'),
('PTMLinh', '123456', 'NV2025008'),
('DQBao', '123456', 'NV2025009'),
('VTNAnh', '123456', 'NV2025010'),
('TKTest', '123456', 'NV2025011')

-- Reset online state de test login/logout theo service
UPDATE TaiKhoan SET TrangThai = 0

-- Tai khoan test khuyen nghi:
-- username: TKTest
-- password: 123456

-- Query kiem tra nhanh trang thai sau login/logout
-- SELECT TenTaiKhoan, TrangThai, MaNhanVien FROM TaiKhoan ORDER BY TenDangNhap

GO
-- Chuẩn bị bản thuế
CREATE TABLE Thue (
    MaThue VARCHAR(15) NOT NULL PRIMARY KEY,
    TenThue NVARCHAR(255) NOT NULL,
    PhanTramThue FLOAT,
    MoTa NVARCHAR(255)
);
GO
-- Chuẩn bị data thuế
INSERT INTO Thue (MaThue, TenThue, PhanTramThue, MoTa)
VALUES
    ('T2026001', N'Không thuế', 0, N'Áp dụng cho sản phẩm không chịu thuế'),
    ('T2026002', N'VAT 5%', 0.05, N'Áp dụng cho một số mặt hàng thiết yếu'),
    ('T2026003', N'VAT 8%', 0.08, N'Áp dụng theo chính sách giảm thuế tạm thời'),
    ('T2026004', N'VAT 10%', 0.1, N'Thuế giá trị gia tăng tiêu chuẩn'),
    ('T2026005', N'Thuế đặc biệt 15%', 0.15, N'Áp dụng cho hàng hóa đặc biệt');
GO

-- Chuẩn bị bản loại sản phẩm
CREATE TABLE LoaiSanPham (
    MaLoaiSanPham VARCHAR(15) NOT NULL PRIMARY KEY,
    TenLoaiSanPham NVARCHAR(255) NOT NULL,
    MoTa NVARCHAR(255)
);

GO
-- Chuẩn bị bản khuyến mãi
CREATE TABLE KhuyenMai (
    MaKhuyenMai VARCHAR(15) NOT NULL PRIMARY KEY,
    TenKhuyenMai NVARCHAR(255) NOT NULL,
    PhanTramGG FLOAT DEFAULT 0,
    NgayBatDau DATETIME DEFAULT GETDATE(),
    NgayKetThuc DATETIME DEFAULT GETDATE(),
    TrangThai BIT,
);

GO
-- Chuẩn bị data khuyễn mãi
INSERT INTO KhuyenMai (
    MaKhuyenMai, TenKhuyenMai, PhanTramGG, NgayBatDau, NgayKetThuc, TrangThai
)
VALUES
    ('KM2025001', N'Giảm giá toàn bộ sản phẩm', 5, '2025-01-01', '2025-12-31', 1),
    ('KM2025002', N'Ưu đãi cuối tuần', 7, '2025-01-01', '2025-12-31', 1),
    ('KM2025003', N'Khuyến mãi ngày lễ', 10, '2025-01-01', '2025-12-31', 1),
    ('KM2025004', N'Giảm giá mùa hè', 8, '2025-05-01', '2025-08-31', 1),
    ('KM2025005', N'Giảm giá mùa đông', 8, '2025-11-01', '2025-12-31', 1),
    ('KM2025006', N'Giảm giá vật tư y tế', 10, '2025-01-01', '2025-12-31', 1),
    ('KM2025007', N'Giảm giá theo lô nhập', 15, '2025-01-01', '2025-12-31', 1),
    ('KM2025009', N'Ưu đãi đặc biệt trong tháng', 7, '2025-01-01', '2025-12-31', 1);

GO
-- Chuẩn bị data loại sản phẩm

GO
-- Chuẩn bị bản sản phẩm
CREATE TABLE SanPham (
    MaSanPham VARCHAR(15) NOT NULL PRIMARY KEY,
    TenSanPham NVARCHAR(150) NOT NULL,
    CongDung NVARCHAR(255) NOT NULL,
    ThanhPhan NVARCHAR(255) NOT NULL,
    HanSuDung INT NOT NULL,
    GiaThanh FLOAT NOT NULL,
    NoiSanXuat NVARCHAR(10) NOT NULL,
    MaLoaiSanPham VARCHAR(15) NOT NULL,
    MaKhuyenMai VARCHAR(15) NOT NULL,
    MaThue VARCHAR(15) NOT NULL,
    TrangThai BIT,
    hinhAnh VARCHAR(255),
    FOREIGN KEY (MaLoaiSanPham) REFERENCES LoaiSanPham(MaLoaiSanPham) ON DELETE CASCADE,
    FOREIGN KEY (MaKhuyenMai) REFERENCES KhuyenMai(MaKhuyenMai) ON DELETE CASCADE,
    FOREIGN KEY (MaThue) REFERENCES Thue(MaThue) ON DELETE CASCADE
);
GO
-- Chuẩn bị data sản phẩm

GO
-- Chuẩn bị bản nhà cung cấp
CREATE TABLE NhaCungCap (
    MaNhaCungCap VARCHAR(15) NOT NULL PRIMARY KEY,
    TenNhaCungCap NVARCHAR(255) NOT NULL,
    DiaChi NVARCHAR(255) NOT NULL,
    Email NVARCHAR(255) NOT NULL,
    SoDienThoai VARCHAR(12) NOT NULL,
    MoTa NVARCHAR(255),
    TrangThai BIT
);

GO
-- Chuẩn bị data nhà cung cấp
INSERT INTO NhaCungCap (
    MaNhaCungCap, TenNhaCungCap, DiaChi, Email, SoDienThoai, MoTa, TrangThai
)
VALUES
    ('NCC2026001', N'Công ty Dược Hậu Giang', N'Cần Thơ, Việt Nam', 'dhg@duochaugiang.com', '02923891234', N'Nhà sản xuất dược phẩm lớn tại Việt Nam', 1),
    ('NCC2026002', N'Công ty Dược Traphaco', N'Hà Nội, Việt Nam', 'info@traphaco.com.vn', '02435678901', N'Chuyên thuốc đông dược và thảo dược', 1),
    ('NCC2026003', N'Công ty Dược Imexpharm', N'Đồng Tháp, Việt Nam', 'contact@imexpharm.com', '02773891234', N'Chuyên sản xuất thuốc kháng sinh', 1),
    ('NCC2026004', N'Công ty Sanofi Việt Nam', N'TP.HCM, Việt Nam', 'vn@sanofi.com', '02838234567', N'Công ty dược phẩm đa quốc gia', 1),
    ('NCC2026005', N'Công ty DH Healthcare', N'TP.HCM, Việt Nam', 'support@dhhealth.vn', '02837654321', N'Phân phối thiết bị và thuốc', 1),
    ('NCC2026006', N'Công ty Mega Lifesciences', N'Thái Lan', 'info@megawecare.com', '006622345678', N'Chuyên thực phẩm chức năng', 1),
    ('NCC2026007', N'Công ty OPC Pharma', N'TP.HCM, Việt Nam', 'opc@opcpharma.com', '02839234567', N'Chuyên đông dược', 1),
    ('NCC2026008', N'Công ty Pharmacity Supply', N'TP.HCM, Việt Nam', 'supply@pharmacity.vn', '02839998888', N'Chuỗi phân phối thuốc bán lẻ', 1),
    ('NCC2026009', N'Công ty Zuellig Pharma', N'Singapore', 'contact@zuelligpharma.com', '006526789012', N'Phân phối dược phẩm quốc tế', 1),
    ('NCC2026010', N'Công ty Pymepharco', N'Phú Yên, Việt Nam', 'info@pymepharco.com', '02573891234', N'Sản xuất thuốc generic', 1);

GO
-- Chuẩn bị bản phiếu nhập
CREATE TABLE PhieuNhap (
    MaPhieuNhap VARCHAR(15) NOT NULL PRIMARY KEY,
    ngayNhap DATE NOT NULL,
    MaNhanVien VARCHAR(15) NOT NULL,
    MaNhaCungCap VARCHAR(15) NOT NULL,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE,
    FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap)ON DELETE CASCADE,
);

GO
-- Chuẩn bị data phiếu nhập
GO
-- Chuẩn bị bản chi tiết phiếu nhập
CREATE TABLE ChiTietPhieuNhap (
    MaPhieuNhap VARCHAR(15) NOT NULL,
    MaSanPham VARCHAR(15) NOT NULL,
    soLuong INT,
    giaNhap FLOAT,
    PRIMARY KEY (MaPhieuNhap, MaSanPham),
    FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap) ON DELETE CASCADE,
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham) ON DELETE CASCADE
);
GO
-- Chuẩn bị data chi tiết phiếu nhập
GO
-- Chuẩn bị bản phương thức thanh toán
CREATE TABLE PhuongThucThanhToan (
    MaPTTT VARCHAR(15) NOT NULL PRIMARY KEY,
    TenPTTT NVARCHAR(255) NOT NULL,
    MoTa NVARCHAR(255),
    TrangThai BIT
);

GO
-- Chuẩn bị data phương thức thanh toán

INSERT INTO PhuongThucThanhToan (maPTTT, tenPTTT, moTa, trangThai)
VALUES
        ('PTTT2025001', N'Tiền mặt', N'Thanh toán trực tiếp tại cửa hàng', 1),
        ('PTTT20250002', N'Chuyển khoản ngân hàng', N'Thanh toán qua tài khoản ngân hàng', 1),
        ('PTTT2025003', N'Ví MoMo', N'Thanh toán qua ví điện tử MoMo', 1),
        ('PTTT2025004', N'QR Code', N'Quét mã QR để thanh toán nhanh', 1),
        ('PTTT2025005', N'Thẻ ATM / Thẻ tín dụng', N'Thanh toán bằng thẻ ngân hàng', 1);

GO
-- Chuẩn bị bản khách hàng

CREATE TABLE KhachHang (
    MaKhachHang VARCHAR(15) NOT NULL PRIMARY KEY,
    TenKhachHang NVARCHAR(70) NOT NULL,
    SoDienThoai VARCHAR(10) NOT NULL,
    Email VARCHAR(255),
    GioiTinh BIT NOT NULL,
    DiemTichLuy INT DEFAULT 0,
    TrangThai BIT NOT NULL
);

GO
-- Chuẩn bị data khách hàng
INSERT INTO KhachHang (
    MaKhachHang, TenKhachHang, SoDienThoai, Email, GioiTinh, DiemTichLuy, TrangThai
)
VALUES
('KH2025001', N'Nguyễn Văn An', '0901234567', 'an.nguyen@gmail.com', 1, 120, 1),
('KH2025002', N'Trần Thị Bình', '0912345678', 'binh.tran@gmail.com', 0, 250, 1),
('KH2025003', N'Lê Hoàng Nam', '0923456789', 'nam.le@gmail.com', 1, 80, 1),
('KH2025004', N'Phạm Thị Mai', '0934567890', 'mai.pham@gmail.com', 0, 300, 1),
('KH2025005', N'Hoàng Minh Đức', '0945678901', 'duc.hoang@gmail.com', 1, 50, 1),
('KH2025006', N'Ngô Thị Lan', '0956789012', 'lan.ngo@gmail.com', 0, 400, 1),
('KH2025007', N'Đỗ Quang Huy', '0967890123', 'huy.do@gmail.com', 1, 0, 1),
('KH2025008', N'Võ Thị Hạnh', '0978901234', 'hanh.vo@gmail.com', 0, 150, 1),
('KH2025009', N'Bùi Thanh Tùng', '0989012345', 'tung.bui@gmail.com', 1, 220, 1),
('KH2025010', N'Đặng Ngọc Anh', '0390123456', 'anh.dang@gmail.com', 0, 180, 1),

('KH2025011', N'Phan Quốc Bảo', '0381234567', 'bao.phan@gmail.com', 1, 75, 1),
('KH2025012', N'Lý Thị Thu', '0372345678', 'thu.ly@gmail.com', 0, 90, 1),
('KH2025013', N'Nguyễn Minh Tuấn', '0363456789', 'tuan.nguyen@gmail.com', 1, 500, 1),
('KH2025014', N'Trịnh Thị Hoa', '0354567890', 'hoa.trinh@gmail.com', 0, 60, 1),
('KH2025015', N'Phùng Gia Khánh', '0345678901', 'khanh.phung@gmail.com', 1, 130, 1),

('KH2025016', N'Đinh Thị Yến', '0336789012', 'yen.dinh@gmail.com', 0, 210, 0),
('KH2025017', N'La Văn Sơn', '0327890123', 'son.la@gmail.com', 1, 40, 0),
('KH2025018', N'Chu Thị Ngọc', '0318901234', 'ngoc.chu@gmail.com', 0, 95, 1),
('KH2025019', N'Nguyễn Quốc Hưng', '0309012345', 'hung.nguyen@gmail.com', 1, 310, 1),
('KH2025020', N'Tạ Minh Phúc', '0290123456', 'phuc.ta@gmail.com', 1, 275, 1);

GO
-- Chuẩn bị bản hoá đơn
CREATE TABLE HoaDon (
    MaHoaDon VARCHAR(15) NOT NULL PRIMARY KEY,
    NgayLap DATE NOT NULL,
    MaKhachHang VARCHAR(15) NOT NULL,
    MaNhanVien VARCHAR(15) NOT NULL,
    MaPTTT VARCHAR(15) NOT NULL,
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang) ON DELETE CASCADE,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE,
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT) ON DELETE CASCADE
);
GO
-- Chuẩn bị data hoá đơn 
GO 
-- Chuẩn bị bản chi tiết hoá đơn
CREATE TABLE ChiTietHoaDon (
    MaHoaDon VARCHAR(15) NOT NULL,
    MaSanPham VARCHAR(15) NOT NULL,
    SoLuong INT,
    DonGia FLOAT,
    PRIMARY KEY (MaHoaDon, MaSanPham),
    FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);
GO 
-- Chuẩn bị data chi tiết hoá đơn

GO
-- Chuẩn bị bản kệ sản phẩm
CREATE TABLE KeSanPham (
    MaKeSanPham VARCHAR(15) NOT NULL PRIMARY KEY,
    TenKeSanPham NVARCHAR(255) NOT NULL,
    ViTri NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(255)
);
GO
-- Chuẩn bị data kệ sản phẩm
INSERT INTO KeSanPham (MaKeSanPham, TenKeSanPham, ViTri, MoTa)
VALUES
    ('KSP2026001', N'Kệ thuốc giảm đau', N'Dãy A - Tầng 1', N'Chứa thuốc giảm đau, hạ sốt'),
    ('KSP2026002', N'Kệ thuốc kháng sinh', N'Dãy A - Tầng 2', N'Chứa thuốc kháng sinh theo đơn'),
    ('KSP2026003', N'Kệ thực phẩm chức năng', N'Dãy B - Tầng 1', N'Vitamin, bổ sung sức khỏe'),
    ('KSP2026004', N'Kệ vật tư y tế', N'Dãy B - Tầng 2', N'Khẩu trang, băng gạc, dụng cụ y tế'),
    ('KSP2026005', N'Kệ thuốc cảm cúm', N'Dãy C - Tầng 1', N'Thuốc ho, sổ mũi, cảm lạnh'),
    ('KSP2026006', N'Kệ thuốc tiêu hóa', N'Dãy C - Tầng 2', N'Thuốc dạ dày, tiêu hóa'),
    ('KSP2026007', N'Kệ thuốc tim mạch', N'Dãy D - Tầng 1', N'Thuốc huyết áp, tim mạch'),
    ('KSP2026008', N'Kệ thuốc tiểu đường', N'Dãy D - Tầng 2', N'Thuốc hỗ trợ tiểu đường'),
    ('KSP2026009', N'Kệ hàng cận date', N'Kho phụ', N'Sản phẩm gần hết hạn'),
    ('KSP2026010', N'Kệ hàng mới nhập', N'Kho chính', N'Hàng mới chưa trưng bày');

GO  
-- Chuẩn bị bản Lô sản phẩm
CREATE TABLE LoSanPham (
    MaLoSanPham VARCHAR(15) NOT NULL PRIMARY KEY,
    MaSanPham VARCHAR(15) NOT NULL,
    MaPhieuNhap VARCHAR(15) NOT NULL,
    MaKeSanPham VARCHAR(15) NOT NULL,
    SoLuong INT,
    DonViTinh NVARCHAR(30),
    HanSuDung DATE,
    TrangThai BIT,
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
    FOREIGN KEY (MaKeSanPham) REFERENCES KeSanPham(MaKeSanPham)
);
GO 
-- Chuẩn bị data Lô sản phẩm