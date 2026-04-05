CREATE DATABASE QLBANTHUOC
GO

USE QLBANTHUOC
GO

-- =============================================
-- BẢNG NHÂN VIÊN
-- PK: MaNV
-- =============================================
CREATE TABLE NhanVien (
    MaNV        VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenNV       NVARCHAR(50)  NOT NULL,
    GioiTinh    NVARCHAR(5)   NOT NULL,
    SoDienThoai VARCHAR(10)   NOT NULL,
    Email       VARCHAR(100),
    CCCD        VARCHAR(12)   NOT NULL,
    DiaChi      NVARCHAR(255),
    ChucVu      NVARCHAR(50)  NOT NULL,
    CaLam       NVARCHAR(50)  NOT NULL,
    TrangThai   BIT           NOT NULL DEFAULT 1,
    LinkHinhAnh VARCHAR(255)
)
GO

INSERT INTO NhanVien (MaNV, TenNV, GioiTinh, SoDienThoai, CCCD, DiaChi, ChucVu, CaLam, TrangThai, LinkHinhAnh)
VALUES
('NV001', N'Nguyễn Quốc Nhựt', N'Nam', '0367627363', '082205017345', N'256 Dương Quảng Hàm, Q Gò Vấp, TP.HCM', N'Dược sĩ',              N'Ca sáng',        1, 'image_nv1.png'),
('NV002', N'Nguyễn Thế Luân',  N'Nam', '0365868345', '081205017676', N'256 Nguyễn Văn Khối, Q Gò Vấp, TP.HCM', N'Dược sĩ',              N'Ca chiều',       1, 'image_nv2.png'),
('NV003', N'Phan Khánh Khoa',  N'Nam', '0966762657', '081105034345', N'123 Võ Thị Sáu, Q1, TP.HCM',             N'Quản lý nhà thuốc',   N'Toàn thời gian', 1, 'image_nv3.png'),
('NV004', N'Hồ Hoàng Minh',   N'Nam', '0954342657', '081105034543', N'12 Đinh Tiên Hoàng, Q1, TP.HCM',         N'Dược sĩ',              N'Ca tối',         1, 'image_nv4.png'),
('NV005', N'Lê Thị Hồng Nhung', N'Nữ', '0912345678', '079301012345', N'45 Lý Thường Kiệt, Q10, TP.HCM',          N'Dược sĩ',              N'Ca sáng',        1, 'image_nv5.png'),
('NV006', N'Trần Thị Mai Anh',  N'Nữ', '0978123456', '079502023456', N'89 Nguyễn Trãi, Q5, TP.HCM',              N'Dược sĩ',              N'Ca chiều',       1, 'image_nv6.png'),
('NV007', N'Võ Minh Tuấn',      N'Nam', '0938765432', '080103034567', N'12 Trần Hưng Đạo, Q1, TP.HCM',            N'Thu ngân',              N'Ca sáng',        1, 'image_nv7.png'),
('NV008', N'Phạm Ngọc Trinh',   N'Nữ', '0907654321', '079804045678', N'67 Phan Đình Phùng, Q Phú Nhuận, TP.HCM', N'Dược sĩ',              N'Ca tối',         1, 'image_nv8.png'),
('NV009', N'Bùi Đức Thịnh',     N'Nam', '0889876543', '080205056789', N'23 Hùng Vương, Q6, TP.HCM',               N'Nhân viên kho',        N'Toàn thời gian', 1, 'image_nv9.png'),
('NV010', N'Nguyễn Thị Lan',    N'Nữ', '0919988776', '079706067890', N'56 Hai Bà Trưng, Q3, TP.HCM',             N'Phó quản lý',          N'Toàn thời gian', 1, 'image_nv10.png')
GO

-- =============================================
-- BẢNG TÀI KHOẢN
-- PK: TenTK
-- FK: MaNV -> NhanVien(MaNV)
-- =============================================
CREATE TABLE TaiKhoan (
    TenTK           VARCHAR(50)  NOT NULL PRIMARY KEY,
    MatKhau         VARCHAR(255) NOT NULL,
    VaiTro          NVARCHAR(50) DEFAULT N'Nhân viên',
    TrangThai       BIT          DEFAULT 1,
    MaNV            VARCHAR(10)  NOT NULL UNIQUE,
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV) ON DELETE CASCADE
)
GO

INSERT INTO TaiKhoan (TenTK, MatKhau, VaiTro, MaNV)
VALUES
('NQNhut', '123456', N'Nhân viên', 'NV001'),
('NTLuan', '123456', N'Nhân viên', 'NV002'),
('PKKhoa', '123456', N'Quản lý',   'NV003'),
('HHMinh', '123456', N'Nhân viên', 'NV004'),
('LTHNhung','123456', N'Nhân viên', 'NV005'),
('TTMAnh',  '123456', N'Nhân viên', 'NV006'),
('VMTuan',  '123456', N'Nhân viên', 'NV007'),
('PNTrinh', '123456', N'Nhân viên', 'NV008'),
('BDThinh', '123456', N'Nhân viên', 'NV009'),
('NTLan',   '123456', N'Quản lý',   'NV010')
GO

-- =============================================
-- BẢNG NHÀ CUNG CẤP
-- PK: MaNCC
-- =============================================
CREATE TABLE NhaCungCap (
    MaNCC     VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenNCC    NVARCHAR(255) NOT NULL,
    DiaChi    NVARCHAR(500),
    Email     VARCHAR(100),
    SDT       VARCHAR(10),
    TrangThai BIT           NOT NULL DEFAULT 1
)
GO

INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, Email, SDT, TrangThai)
VALUES
('NCC001', N'Công ty Cổ phần Dược Hậu Giang (DHG Pharma)',  N'288 Bis Nguyễn Văn Cừ, Ninh Kiều, Cần Thơ',          'info@dhgpharma.com.vn',    '0292383080', 1),
('NCC002', N'Công ty Cổ phần Dược phẩm Imexpharm',          N'04 Đường 30/4, Cao Lãnh, Đồng Tháp',                  'contact@imexpharm.com',    '0277385155', 1),
('NCC003', N'Công ty TNHH Sanofi-Aventis Việt Nam',         N'123 Nguyễn Khoái, Quận 4, TP.HCM',                    'info@sanofi.com.vn',       '0283943200', 1),
('NCC004', N'Công ty Cổ phần Pymepharco',                   N'166 Nguyễn Huệ, TP Tuy Hòa, Phú Yên',                'info@pymepharco.com.vn',   '0257382533', 1),
('NCC005', N'Công ty Cổ phần Traphaco',                     N'75 Yên Ninh, Ba Đình, Hà Nội',                        'info@traphaco.com.vn',     '0243716226', 1),
('NCC006', N'Công ty TNHH GlaxoSmithKline Việt Nam',        N'Tầng 11, Mê Linh Point, 02 Ngô Đức Kế, Quận 1, TP.HCM', 'cskh@gsk.com.vn',     '0283825180', 1),
('NCC007', N'Công ty Cổ phần Dược phẩm OPC',                N'1017 Hồng Bàng, Quận 6, TP.HCM',                      'info@opcpharma.com',       '0283751400', 1),
('NCC008', N'Công ty TNHH Abbott Việt Nam',                  N'Lầu 7, Toà nhà Centec, 72-74 Nguyễn Thị Minh Khai, Quận 3, TP.HCM', 'info@abbott.com.vn', '0283910640', 1),
('NCC009', N'Công ty CP Dược phẩm Mediplantex',          N'358 Giải Phóng, Thanh Xuân, Hà Nội',                    'info@mediplantex.com',     '0243864370', 1),
('NCC010', N'Công ty Cổ phần Dược phẩm SaVi',            N'Lô Z.01-02, KCN Tân Đức, Đức Hòa, Long An',            'info@savipharma.com.vn',   '0723769018', 1),
('NCC011', N'Công ty TNHH Johnson & Johnson Việt Nam',    N'Tầng 5, 235 Đồng Khởi, Quận 1, TP.HCM',               'contact@jnj.com.vn',       '0283823660', 1),
('NCC012', N'Công ty Cổ phần Dược phẩm Nam Hà',          N'415 Hàn Thuyên, TP Nam Định, Nam Định',                'info@namhapharma.com.vn',  '0228384930', 1)
GO

-- =============================================
-- BẢNG KỆ SẢN PHẨM
-- PK: MaKSP
-- =============================================
CREATE TABLE KeSanPham (
    MaKSP  VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenKSP NVARCHAR(100) NOT NULL,
    ViTri  NVARCHAR(100)
)
GO

INSERT INTO KeSanPham (MaKSP, TenKSP, ViTri)
VALUES
('KE001', N'Kệ Thuốc giảm đau - Hạ sốt',        N'Khu A - Kệ 1'),
('KE002', N'Kệ Thuốc kháng sinh',                  N'Khu A - Kệ 2'),
('KE003', N'Kệ Thuốc ho & Cảm cúm',               N'Khu B - Kệ 1'),
('KE004', N'Kệ Vitamin & Thực phẩm chức năng',    N'Khu B - Kệ 2'),
('KE005', N'Kệ Thuốc tiêu hóa - Dạ dày',          N'Khu C - Kệ 1'),
('KE006', N'Kệ Thuốc da liễu & Dị ứng',           N'Khu C - Kệ 2'),
('KE007', N'Kệ Dụng cụ y tế',                      N'Khu D - Kệ 1'),
('KE008', N'Kệ Thuốc nhỏ mắt & Tai mũi họng',    N'Khu D - Kệ 2'),
('KE009', N'Kệ Thuốc kê đơn',                      N'Quầy dược sĩ'),
('KE010', N'Kệ Thuốc tim mạch - Huyết áp',    N'Khu E - Kệ 1'),
('KE011', N'Kệ Sản phẩm chăm sóc mẹ & bé',   N'Khu E - Kệ 2'),
('KE012', N'Kệ Thuốc thần kinh - Giấc ngủ',   N'Khu F - Kệ 1')
GO

-- =============================================
-- BẢNG SẢN PHẨM
-- PK: MaSP
-- FK: MaNCC -> NhaCungCap(MaNCC)
-- =============================================
CREATE TABLE SanPham (
    MaSP        VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenSP       NVARCHAR(255) NOT NULL,
    LoaiSP      NVARCHAR(100),
    CongDung    NVARCHAR(500),
    ThanhPhan   NVARCHAR(500),
    SoLuong     INT           DEFAULT 0,
    Luong       DECIMAL(18,2),
    GiaThanh    DECIMAL(18,2) NOT NULL,
    HanSuDung   NVARCHAR(50),
    DonViTinh   NVARCHAR(50)  NOT NULL,
    TrangThai   BIT           DEFAULT 1,
    LinkHinhAnh VARCHAR(500),
    MaNCC       VARCHAR(10)   NOT NULL,
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC)
)
GO

-- Thuốc giảm đau - Hạ sốt (NCC001 - DHG Pharma)
-- Thuốc kháng sinh (NCC002 - Imexpharm)
-- Thuốc ho & Cảm cúm (NCC003 - Sanofi)
-- Vitamin & TPCN (NCC004 - Pymepharco)
-- Thuốc tiêu hóa (NCC005 - Traphaco)
-- Thuốc da liễu & Dị ứng (NCC006 - GSK)
-- Dụng cụ y tế (NCC007 - OPC)
-- Thuốc nhỏ mắt & TMH (NCC008 - Abbott)

INSERT INTO SanPham (MaSP, TenSP, LoaiSP, CongDung, SoLuong, GiaThanh, DonViTinh, HanSuDung, TrangThai, LinkHinhAnh, MaNCC)
VALUES
-- Thuốc giảm đau - Hạ sốt
('SP001', N'Hapacol 500mg (Paracetamol)',   N'Thuốc giảm đau - Hạ sốt', N'Giảm đau, hạ sốt',                      100, 25000,  N'Vỉ',   N'24 tháng', 1, 'hapacol_500.png',      'NCC001'),
('SP002', N'Panadol Extra',                 N'Thuốc giảm đau - Hạ sốt', N'Giảm đau đầu, đau răng, hạ sốt',         80, 35000,  N'Vỉ',   N'24 tháng', 1, 'panadol_extra.png',    'NCC001'),
('SP003', N'Efferalgan 500mg sủi bọt',     N'Thuốc giảm đau - Hạ sốt', N'Hạ sốt, giảm đau nhanh',                 60, 55000,  N'Tuýp', N'24 tháng', 1, 'efferalgan.png',       'NCC001'),
('SP004', N'Ibuprofen 400mg',              N'Thuốc giảm đau - Hạ sốt', N'Giảm đau, kháng viêm',                    90, 20000,  N'Vỉ',   N'24 tháng', 1, 'ibuprofen_400.png',    'NCC001'),
('SP005', N'Aspirin pH8 81mg',             N'Thuốc giảm đau - Hạ sốt', N'Phòng ngừa huyết khối, giảm đau nhẹ',     50, 45000,  N'Hộp',  N'24 tháng', 1, 'aspirin_81.png',       'NCC001'),

-- Thuốc kháng sinh
('SP006', N'Amoxicillin 500mg',            N'Thuốc kháng sinh', N'Kháng sinh điều trị nhiễm khuẩn',                120, 18000,  N'Vỉ',   N'24 tháng', 1, 'amoxicillin_500.png',  'NCC002'),
('SP007', N'Augmentin 625mg',             N'Thuốc kháng sinh', N'Kháng sinh phổ rộng',                              40, 95000,  N'Hộp',  N'24 tháng', 1, 'augmentin_625.png',    'NCC002'),
('SP008', N'Cephalexin 500mg',            N'Thuốc kháng sinh', N'Điều trị nhiễm khuẩn hô hấp, da',                100, 15000,  N'Vỉ',   N'24 tháng', 1, 'cephalexin_500.png',   'NCC002'),
('SP009', N'Azithromycin 250mg',           N'Thuốc kháng sinh', N'Kháng sinh điều trị nhiễm khuẩn hô hấp',          60, 65000,  N'Hộp',  N'24 tháng', 1, 'azithromycin_250.png', 'NCC002'),
('SP010', N'Ciprofloxacin 500mg',          N'Thuốc kháng sinh', N'Kháng sinh điều trị nhiễm khuẩn tiết niệu',       80, 22000,  N'Vỉ',   N'24 tháng', 1, 'ciprofloxacin_500.png','NCC002'),

-- Thuốc ho & Cảm cúm
('SP011', N'Thuốc ho Bổ phế Nam Hà',      N'Thuốc ho & Cảm cúm', N'Trị ho, đau họng, viêm phế quản',              50, 35000,  N'Chai', N'24 tháng', 1, 'bo_phe_nam_ha.png',    'NCC003'),
('SP012', N'Decolgen Forte',               N'Thuốc ho & Cảm cúm', N'Giảm triệu chứng cảm cúm, nghẹt mũi',       150, 18000,  N'Vỉ',   N'24 tháng', 1, 'decolgen.png',         'NCC003'),
('SP013', N'Tiffy Dey',                    N'Thuốc ho & Cảm cúm', N'Trị cảm cúm, sổ mũi, nhức đầu',              200, 12000,  N'Vỉ',   N'24 tháng', 1, 'tiffy_dey.png',        'NCC003'),
('SP014', N'Prospan thuốc ho thảo dược',   N'Thuốc ho & Cảm cúm', N'Trị ho do viêm đường hô hấp',                  30, 120000, N'Chai', N'24 tháng', 1, 'prospan.png',          'NCC003'),
('SP015', N'Actifed',                       N'Thuốc ho & Cảm cúm', N'Giảm nghẹt mũi, sổ mũi, hắt hơi',          100, 20000,  N'Vỉ',   N'24 tháng', 1, 'actifed.png',          'NCC003'),

-- Vitamin & Thực phẩm chức năng
('SP016', N'Vitamin C 1000mg viên sủi',    N'Vitamin & TPCN', N'Tăng sức đề kháng, bổ sung vitamin C',             80, 65000,  N'Tuýp', N'24 tháng', 1, 'vitamin_c_sui.png',    'NCC004'),
('SP017', N'Vitamin B Complex',             N'Vitamin & TPCN', N'Bổ sung vitamin nhóm B',                           60, 45000,  N'Lọ',   N'24 tháng', 1, 'vitamin_b.png',        'NCC004'),
('SP018', N'Canxi D3 viên nén',            N'Vitamin & TPCN', N'Bổ sung canxi, phòng loãng xương',                 50, 85000,  N'Hộp',  N'24 tháng', 1, 'canxi_d3.png',         'NCC004'),
('SP019', N'Dầu cá Omega 3',               N'Vitamin & TPCN', N'Bổ sung DHA, EPA, tốt cho tim mạch',               40, 180000, N'Lọ',   N'24 tháng', 1, 'omega3.png',           'NCC004'),
('SP020', N'Vitamin E 400IU',              N'Vitamin & TPCN', N'Chống oxy hóa, đẹp da',                            70, 55000,  N'Lọ',   N'24 tháng', 1, 'vitamin_e.png',        'NCC004'),

-- Thuốc tiêu hóa - Dạ dày
('SP021', N'Omeprazol 20mg',               N'Thuốc tiêu hóa', N'Điều trị trào ngược dạ dày, viêm loét',           90, 15000,  N'Vỉ',   N'24 tháng', 1, 'omeprazol.png',        'NCC005'),
('SP022', N'Phosphalugel (thuốc dạ dày)',  N'Thuốc tiêu hóa', N'Trung hòa acid dạ dày, giảm đau dạ dày',          80, 45000,  N'Hộp',  N'18 tháng', 1, 'phosphalugel.png',     'NCC005'),
('SP023', N'Smecta',                        N'Thuốc tiêu hóa', N'Điều trị tiêu chảy, bảo vệ niêm mạc ruột',       60, 55000,  N'Hộp',  N'24 tháng', 1, 'smecta.png',           'NCC005'),
('SP024', N'Berberin',                      N'Thuốc tiêu hóa', N'Điều trị tiêu chảy, nhiễm khuẩn đường ruột',    100, 25000,  N'Lọ',   N'24 tháng', 1, 'berberin.png',         'NCC005'),
('SP025', N'Domperidon 10mg',              N'Thuốc tiêu hóa', N'Chống nôn, trị đầy hơi, khó tiêu',                70, 12000,  N'Vỉ',   N'24 tháng', 1, 'domperidon.png',       'NCC005'),

-- Thuốc da liễu & Dị ứng
('SP026', N'Cetirizin 10mg',               N'Thuốc da liễu & Dị ứng', N'Chống dị ứng, viêm mũi dị ứng',          120,  8000,  N'Vỉ',   N'24 tháng', 1, 'cetirizin.png',        'NCC006'),
('SP027', N'Loratadin 10mg',               N'Thuốc da liễu & Dị ứng', N'Chống dị ứng, mề đay, viêm mũi',         100, 10000,  N'Vỉ',   N'24 tháng', 1, 'loratadin.png',        'NCC006'),
('SP028', N'Gentrisone cream',             N'Thuốc da liễu & Dị ứng', N'Trị viêm da, nấm da, dị ứng da',          50, 35000,  N'Tuýp', N'24 tháng', 1, 'gentrisone.png',       'NCC006'),
('SP029', N'Phenergan cream',              N'Thuốc da liễu & Dị ứng', N'Chống ngứa, dị ứng ngoài da',              40, 28000,  N'Tuýp', N'24 tháng', 1, 'phenergan.png',        'NCC006'),
('SP030', N'Betadine 10%',                 N'Thuốc da liễu & Dị ứng', N'Sát khuẩn vết thương, khử trùng',          80, 48000,  N'Chai', N'36 tháng', 1, 'betadine.png',         'NCC006'),

-- Dụng cụ y tế
('SP031', N'Khẩu trang y tế (hộp 50 cái)',N'Dụng cụ y tế', N'Phòng ngừa lây nhiễm, bảo vệ hô hấp',             200, 30000,  N'Hộp',  N'36 tháng', 1, 'khau_trang.png',       'NCC007'),
('SP032', N'Bông gạc y tế',               N'Dụng cụ y tế', N'Băng bó vết thương, sơ cứu',                       150, 15000,  N'Gói',  N'36 tháng', 1, 'bong_gac.png',         'NCC007'),
('SP033', N'Băng keo cá nhân Urgo',       N'Dụng cụ y tế', N'Bảo vệ vết thương nhỏ',                            100, 22000,  N'Hộp',  N'36 tháng', 1, 'urgo.png',             'NCC007'),
('SP034', N'Nhiệt kế điện tử',            N'Dụng cụ y tế', N'Đo nhiệt độ cơ thể',                                 30, 85000,  N'Cái',  N'60 tháng', 1, 'nhiet_ke.png',         'NCC007'),
('SP035', N'Nước muối sinh lý NaCl 0.9%', N'Dụng cụ y tế', N'Rửa mũi, rửa vết thương, vệ sinh',                300, 12000,  N'Chai', N'24 tháng', 1, 'nacl_09.png',          'NCC007'),

-- Thuốc nhỏ mắt & Tai mũi họng
('SP036', N'V.Rohto nhỏ mắt',             N'Thuốc nhỏ mắt & TMH', N'Giảm mỏi mắt, đỏ mắt, khô mắt',             80, 38000,  N'Lọ',   N'24 tháng', 1, 'v_rohto.png',          'NCC008'),
('SP037', N'Otrivin nhỏ mũi 0.1%',        N'Thuốc nhỏ mắt & TMH', N'Thông mũi, giảm nghẹt mũi',                  60, 48000,  N'Lọ',   N'24 tháng', 1, 'otrivin.png',          'NCC008'),
('SP038', N'Listerine nước súc miệng',    N'Thuốc nhỏ mắt & TMH', N'Sát khuẩn miệng, ngừa viêm nướu',            50, 68000,  N'Chai', N'36 tháng', 1, 'listerine.png',        'NCC008'),
('SP039', N'Betadine Throat Spray',        N'Thuốc nhỏ mắt & TMH', N'Sát khuẩn họng, giảm đau họng',               40, 89000,  N'Chai', N'24 tháng', 1, 'betadine_spray.png',   'NCC008'),
('SP040', N'Systane Ultra nhỏ mắt',       N'Thuốc nhỏ mắt & TMH', N'Điều trị khô mắt, bôi trơn mắt',             30, 125000, N'Lọ',   N'24 tháng', 1, 'systane_ultra.png',    'NCC008'),

-- Thuốc tim mạch (NCC009 - Mediplantex)
('SP041', N'Amlodipine 5mg',               N'Thuốc tim mạch',           N'Điều trị tăng huyết áp, đau thắt ngực',    100, 18000,  N'Vỉ',   N'24 tháng', 1, 'amlodipine_5.png',     'NCC009'),
('SP042', N'Losartan 50mg',                N'Thuốc tim mạch',           N'Điều trị tăng huyết áp',                     80, 35000,  N'Vỉ',   N'24 tháng', 1, 'losartan_50.png',      'NCC009'),
('SP043', N'Atorvastatin 20mg',            N'Thuốc tim mạch',           N'Giảm cholesterol, phòng xơ vữa động mạch',   60, 45000,  N'Hộp',  N'24 tháng', 1, 'atorvastatin_20.png',  'NCC009'),
('SP044', N'Captopril 25mg',               N'Thuốc tim mạch',           N'Điều trị tăng huyết áp, suy tim',            90, 12000,  N'Vỉ',   N'24 tháng', 1, 'captopril_25.png',     'NCC009'),

-- Thuốc giảm đau bổ sung (NCC010 - SaVi)
('SP045', N'Savi Meloxicam 7.5mg',         N'Thuốc giảm đau - Hạ sốt', N'Giảm đau, kháng viêm xương khớp',            70, 28000,  N'Vỉ',   N'24 tháng', 1, 'meloxicam_75.png',     'NCC010'),
('SP046', N'Savi Diclofenac 50mg',         N'Thuốc giảm đau - Hạ sốt', N'Giảm đau, kháng viêm',                       50, 15000,  N'Vỉ',   N'24 tháng', 1, 'diclofenac_50.png',    'NCC010'),
('SP047', N'Savi Naproxen 500mg',          N'Thuốc giảm đau - Hạ sốt', N'Giảm đau, kháng viêm khớp',                  40, 32000,  N'Vỉ',   N'24 tháng', 1, 'naproxen_500.png',     'NCC010'),

-- Sản phẩm chăm sóc mẹ & bé (NCC011 - J&J)
('SP048', N'Dầu em bé Johnson''s Baby Oil', N'Sản phẩm mẹ & bé',       N'Dưỡng ẩm da cho bé',                         80, 55000,  N'Chai', N'36 tháng', 1, 'johnsons_oil.png',     'NCC011'),
('SP049', N'Phấn rôm Johnson Baby Powder', N'Sản phẩm mẹ & bé',        N'Giữ da khô thoáng cho bé',                   60, 45000,  N'Hộp',  N'36 tháng', 1, 'johnsons_powder.png',  'NCC011'),
('SP050', N'Sữa tắm Johnson''s Top to Toe',N'Sản phẩm mẹ & bé',       N'Tắm gội toàn thân cho bé',                   50, 78000,  N'Chai', N'36 tháng', 1, 'johnsons_toptotoe.png','NCC011'),
('SP051', N'Kem chống hăm Desitin',        N'Sản phẩm mẹ & bé',        N'Phòng và trị hăm tã cho bé',                 40, 95000,  N'Tuýp', N'36 tháng', 1, 'desitin.png',          'NCC011'),

-- Thuốc ho bổ sung (NCC012 - Nam Hà)
('SP052', N'Thuốc ho Bảo Thanh',           N'Thuốc ho & Cảm cúm',      N'Trị ho, đau rát cổ họng',                    60, 42000,  N'Chai', N'24 tháng', 1, 'bao_thanh.png',        'NCC012'),
('SP053', N'Siro ho Astex',                N'Thuốc ho & Cảm cúm',      N'Trị ho khan, ho có đờm',                     50, 38000,  N'Chai', N'24 tháng', 1, 'astex.png',            'NCC012'),

-- Thuốc thần kinh - Giấc ngủ (NCC009)
('SP054', N'Rotunda 30mg',                 N'Thuốc thần kinh',          N'An thần, hỗ trợ giấc ngủ',                   40, 22000,  N'Vỉ',   N'24 tháng', 1, 'rotunda.png',          'NCC009'),
('SP055', N'Seduxen 5mg (Diazepam)',       N'Thuốc thần kinh',          N'An thần, chống co giật',                     30, 18000,  N'Vỉ',   N'24 tháng', 1, 'seduxen.png',          'NCC009'),

-- Dụng cụ y tế bổ sung (NCC007)
('SP056', N'Máy đo huyết áp Omron',       N'Dụng cụ y tế',             N'Đo huyết áp tự động tại nhà',                20, 850000, N'Cái',  N'60 tháng', 1, 'may_do_huyet_ap.png',  'NCC007'),
('SP057', N'Kim tiêm 3ml (hộp 100)',       N'Dụng cụ y tế',             N'Dùng cho tiêm thuốc',                        40, 75000,  N'Hộp',  N'36 tháng', 1, 'kim_tiem.png',         'NCC007'),

-- Vitamin bổ sung (NCC004)
('SP058', N'Sắt Folic (viên bổ máu)',     N'Vitamin & TPCN',           N'Bổ sung sắt, acid folic cho phụ nữ',         90, 35000,  N'Lọ',   N'24 tháng', 1, 'sat_folic.png',        'NCC004'),
('SP059', N'Kẽm Zinc 10mg',               N'Vitamin & TPCN',           N'Bổ sung kẽm, tăng sức đề kháng',             80, 42000,  N'Lọ',   N'24 tháng', 1, 'zinc_10.png',          'NCC004'),
('SP060', N'Glucosamin 1500mg',            N'Vitamin & TPCN',           N'Hỗ trợ xương khớp, giảm đau khớp',           50, 195000, N'Hộp',  N'24 tháng', 1, 'glucosamin.png',       'NCC004')
GO

-- =============================================
-- BẢNG LÔ SẢN PHẨM
-- PK: MaLo
-- FK: MaSP  -> SanPham(MaSP)
-- FK: MaPN  -> PhieuNhap(MaPN)
-- FK: MaKSP -> KeSanPham(MaKSP)
-- =============================================
CREATE TABLE LoSanPham (
    MaLo      VARCHAR(10)  NOT NULL PRIMARY KEY,
    MaSP      VARCHAR(10)  NOT NULL,
    MaPN      VARCHAR(10)  NOT NULL,
    MaKSP     VARCHAR(10)  NOT NULL,
    SoLuong   INT          NOT NULL DEFAULT 0,
    DonViTinh NVARCHAR(50) NOT NULL,
    HanSuDung DATE,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT N'Còn hàng',
    FOREIGN KEY (MaSP)  REFERENCES SanPham(MaSP),
    FOREIGN KEY (MaKSP) REFERENCES KeSanPham(MaKSP)
    -- MaPN sẽ FK sau khi tạo PhieuNhap
)
GO

-- =============================================
-- BẢNG KHUYẾN MÃI
-- PK: MaKM
-- FK: MaSP -> SanPham(MaSP)
-- FK: MaNV -> NhanVien(MaNV)
-- =============================================
CREATE TABLE KhuyenMai (
    MaKM        VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenKM       NVARCHAR(255) NOT NULL,
    MaSP        VARCHAR(10)   NOT NULL,
    PhamTramGG  DECIMAL(5,2)  NOT NULL CHECK (PhamTramGG BETWEEN 0 AND 100),
    NgayBatDau  DATE          NOT NULL,
    NgayKetThuc DATE          NOT NULL,
    TrangThai   NVARCHAR(20)  NOT NULL DEFAULT N'Đang diễn ra',
    MaNV        VARCHAR(10)   NOT NULL,
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
)
GO

INSERT INTO KhuyenMai (MaKM, TenKM, MaSP, PhamTramGG, NgayBatDau, NgayKetThuc, TrangThai, MaNV)
VALUES
('KM001', N'Giảm giá Hapacol cuối tuần',      'SP001', 10.00, '2025-11-15', '2025-11-16', N'Đã kết thúc', 'NV003'),
('KM002', N'Ưu đãi Vitamin C tháng 11',       'SP016', 15.00, '2025-11-01', '2025-11-30', N'Đã kết thúc', 'NV003'),
('KM003', N'Flash Sale Thuốc ho Bổ phế',      'SP011',  5.00, '2025-11-14', '2025-11-14', N'Đã kết thúc', 'NV003'),
('KM004', N'Giảm giá Panadol Extra đầu tháng 12',   'SP002',  8.00, '2025-12-01', '2025-12-07', N'Đã kết thúc', 'NV003'),
('KM005', N'Combo Vitamin E + Omega 3',                'SP020', 12.00, '2025-12-10', '2025-12-31', N'Đã kết thúc', 'NV010'),
('KM006', N'Khuyến mãi Betadine cuối năm',             'SP030', 10.00, '2025-12-20', '2025-12-31', N'Đã kết thúc', 'NV003'),
('KM007', N'Giảm giá Khẩu trang Tết',                 'SP031', 20.00, '2026-01-15', '2026-02-10', N'Đã kết thúc', 'NV010'),
('KM008', N'Ưu đãi Vitamin C mùa nắng',               'SP016', 10.00, '2026-03-01', '2026-03-31', N'Đã kết thúc', 'NV003'),
('KM009', N'Flash Sale Thuốc dạ dày Phosphalugel',   'SP022', 15.00, '2026-04-01', '2026-04-05', N'Đang diễn ra', 'NV010'),
('KM010', N'Giảm giá Glucosamin cho người cao tuổi', 'SP060', 10.00, '2026-04-01', '2026-04-30', N'Đang diễn ra', 'NV003')
GO

-- =============================================
-- BẢNG PHIẾU NHẬP
-- PK: MaPN
-- FK: MaNV  -> NhanVien(MaNV)
-- FK: MaNCC -> NhaCungCap(MaNCC)
-- =============================================
CREATE TABLE PhieuNhap (
    MaPN    VARCHAR(10)   NOT NULL PRIMARY KEY,
    NgayNhap DATETIME     DEFAULT GETDATE(),
    MaNV    VARCHAR(10)   NOT NULL,
    MaNCC   VARCHAR(10)   NOT NULL,
    GhiChu  NVARCHAR(255),
    FOREIGN KEY (MaNV)  REFERENCES NhanVien(MaNV)     ON DELETE CASCADE,
    FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC)
)
GO

INSERT INTO PhieuNhap (MaPN, NgayNhap, MaNV, MaNCC, GhiChu)
VALUES
('PN001', '2025-11-13', 'NV001', 'NCC001', N'Nhập thuốc giảm đau, hạ sốt'),
('PN002', '2025-11-13', 'NV001', 'NCC004', N'Nhập vitamin và thực phẩm chức năng'),
('PN003', '2025-11-14', 'NV001', 'NCC006', N'Nhập thuốc da liễu, dị ứng'),
('PN004', '2025-11-15', 'NV001', 'NCC008', N'Nhập thuốc nhỏ mắt, tai mũi họng'),
('PN005', '2025-11-15', 'NV001', 'NCC002', N'Nhập thuốc kháng sinh'),
('PN006', '2025-11-13', 'NV002', 'NCC003', N'Nhập thuốc ho và cảm cúm'),
('PN007', '2025-11-13', 'NV002', 'NCC005', N'Nhập thuốc tiêu hóa, dạ dày'),
('PN008', '2025-11-14', 'NV002', 'NCC007', N'Nhập dụng cụ y tế, vật tư tiêu hao'),
('PN009', '2025-11-15', 'NV002', 'NCC001', N'Bổ sung thuốc giảm đau'),
('PN010', '2025-11-15', 'NV002', 'NCC004', N'Nhập thêm vitamin, Omega 3'),
('PN011', '2025-11-13', 'NV003', 'NCC002', N'Quản lý duyệt nhập kháng sinh'),
('PN012', '2025-11-14', 'NV003', 'NCC006', N'Nhập hàng GSK (đợt mới)'),
('PN013', '2025-11-14', 'NV003', 'NCC008', N'Nhập thuốc nhỏ mắt, nước súc miệng'),
('PN014', '2025-11-15', 'NV003', 'NCC003', N'Nhập thuốc ho Prospan, Actifed'),
('PN015', '2025-11-15', 'NV003', 'NCC005', N'Nhập thuốc tiêu hóa bổ sung'),
('PN016', '2025-12-01', 'NV001', 'NCC009', N'Nhập thuốc tim mạch, huyết áp'),
('PN017', '2025-12-01', 'NV001', 'NCC010', N'Nhập thuốc giảm đau SaVi'),
('PN018', '2025-12-05', 'NV002', 'NCC011', N'Nhập sản phẩm chăm sóc mẹ & bé'),
('PN019', '2025-12-05', 'NV002', 'NCC012', N'Nhập thuốc ho Nam Hà'),
('PN020', '2025-12-10', 'NV003', 'NCC009', N'Nhập thuốc thần kinh, an thần'),
('PN021', '2025-12-10', 'NV003', 'NCC007', N'Bổ sung dụng cụ y tế, máy đo HA'),
('PN022', '2025-12-15', 'NV005', 'NCC004', N'Nhập thêm vitamin, TPCN'),
('PN023', '2026-01-05', 'NV006', 'NCC001', N'Nhập thuốc giảm đau đầu năm'),
('PN024', '2026-01-10', 'NV005', 'NCC002', N'Bổ sung kháng sinh sau Tết'),
('PN025', '2026-02-01', 'NV010', 'NCC005', N'Nhập thuốc tiêu hóa tháng 2')
GO

-- Thêm FK MaPN vào LoSanPham sau khi PhieuNhap đã tạo
ALTER TABLE LoSanPham
ADD CONSTRAINT FK_LoSanPham_PhieuNhap
FOREIGN KEY (MaPN) REFERENCES PhieuNhap(MaPN)
GO

INSERT INTO LoSanPham (MaLo, MaSP, MaPN, MaKSP, SoLuong, DonViTinh, HanSuDung, TrangThai)
VALUES
('LO001', 'SP001', 'PN001', 'KE001', 100, N'Vỉ',   '2027-11-13', N'Còn hàng'),
('LO002', 'SP002', 'PN001', 'KE001',  80, N'Vỉ',   '2027-08-13', N'Còn hàng'),
('LO003', 'SP016', 'PN002', 'KE004',  80, N'Tuýp', '2027-05-13', N'Còn hàng'),
('LO004', 'SP017', 'PN002', 'KE004',  60, N'Lọ',   '2027-05-13', N'Còn hàng'),
('LO005', 'SP026', 'PN003', 'KE006', 120, N'Vỉ',   '2027-11-13', N'Còn hàng'),
('LO006', 'SP028', 'PN003', 'KE006',  50, N'Tuýp', '2027-11-13', N'Còn hàng'),
('LO007', 'SP036', 'PN004', 'KE008',  80, N'Lọ',   '2027-11-13', N'Còn hàng'),
('LO008', 'SP037', 'PN004', 'KE008',  60, N'Lọ',   '2027-11-13', N'Còn hàng'),
('LO009', 'SP006', 'PN005', 'KE002', 120, N'Vỉ',   '2027-05-13', N'Còn hàng'),
('LO010', 'SP007', 'PN005', 'KE002',  40, N'Hộp',  '2026-12-13', N'Còn hàng'),
('LO011', 'SP011', 'PN006', 'KE003',  50, N'Chai', '2027-05-13', N'Còn hàng'),
('LO012', 'SP012', 'PN006', 'KE003', 150, N'Vỉ',   '2027-02-13', N'Còn hàng'),
('LO013', 'SP021', 'PN007', 'KE005',  90, N'Vỉ',   '2027-12-13', N'Còn hàng'),
('LO014', 'SP022', 'PN007', 'KE005',  80, N'Hộp',  '2027-06-13', N'Còn hàng'),
('LO015', 'SP031', 'PN008', 'KE007', 200, N'Hộp',  '2028-11-13', N'Còn hàng'),
('LO016', 'SP032', 'PN008', 'KE007', 150, N'Gói',  '2028-11-13', N'Còn hàng'),
('LO017', 'SP005', 'PN009', 'KE001',  50, N'Hộp',  '2027-11-13', N'Còn hàng'),
('LO018', 'SP019', 'PN010', 'KE004',  40, N'Lọ',   '2027-05-13', N'Còn hàng'),
('LO019', 'SP020', 'PN010', 'KE004',  70, N'Lọ',   '2027-08-13', N'Còn hàng'),
('LO020', 'SP008', 'PN011', 'KE002', 100, N'Vỉ',   '2027-11-13', N'Còn hàng'),
('LO021', 'SP009', 'PN011', 'KE002',  60, N'Hộp',  '2027-08-13', N'Còn hàng'),
('LO022', 'SP027', 'PN012', 'KE006', 100, N'Vỉ',   '2027-11-13', N'Còn hàng'),
('LO023', 'SP030', 'PN012', 'KE006',  80, N'Chai', '2028-11-13', N'Còn hàng'),
('LO024', 'SP038', 'PN013', 'KE008',  50, N'Chai', '2028-11-13', N'Còn hàng'),
('LO025', 'SP039', 'PN013', 'KE008',  40, N'Chai', '2027-11-13', N'Còn hàng'),
('LO026', 'SP014', 'PN014', 'KE003',  30, N'Chai', '2027-08-13', N'Còn hàng'),
('LO027', 'SP015', 'PN014', 'KE003', 100, N'Vỉ',   '2027-05-13', N'Còn hàng'),
('LO028', 'SP024', 'PN015', 'KE005', 100, N'Lọ',   '2027-12-13', N'Còn hàng'),
('LO029', 'SP025', 'PN015', 'KE005',  70, N'Vỉ',   '2027-12-13', N'Còn hàng'),
('LO030', 'SP041', 'PN016', 'KE010', 100, N'Vỉ',   '2027-12-01', N'Còn hàng'),
('LO031', 'SP042', 'PN016', 'KE010',  80, N'Vỉ',   '2027-12-01', N'Còn hàng'),
('LO032', 'SP043', 'PN016', 'KE010',  60, N'Hộp',  '2027-12-01', N'Còn hàng'),
('LO033', 'SP044', 'PN016', 'KE010',  90, N'Vỉ',   '2027-12-01', N'Còn hàng'),
('LO034', 'SP045', 'PN017', 'KE001',  70, N'Vỉ',   '2027-12-01', N'Còn hàng'),
('LO035', 'SP046', 'PN017', 'KE001',  50, N'Vỉ',   '2027-12-01', N'Còn hàng'),
('LO036', 'SP047', 'PN017', 'KE001',  40, N'Vỉ',   '2027-12-01', N'Còn hàng'),
('LO037', 'SP048', 'PN018', 'KE011',  80, N'Chai', '2028-12-05', N'Còn hàng'),
('LO038', 'SP049', 'PN018', 'KE011',  60, N'Hộp',  '2028-12-05', N'Còn hàng'),
('LO039', 'SP050', 'PN018', 'KE011',  50, N'Chai', '2028-12-05', N'Còn hàng'),
('LO040', 'SP051', 'PN018', 'KE011',  40, N'Tuýp', '2028-12-05', N'Còn hàng'),
('LO041', 'SP052', 'PN019', 'KE003',  60, N'Chai', '2027-12-05', N'Còn hàng'),
('LO042', 'SP053', 'PN019', 'KE003',  50, N'Chai', '2027-12-05', N'Còn hàng'),
('LO043', 'SP054', 'PN020', 'KE012',  40, N'Vỉ',   '2027-12-10', N'Còn hàng'),
('LO044', 'SP055', 'PN020', 'KE012',  30, N'Vỉ',   '2027-12-10', N'Còn hàng'),
('LO045', 'SP056', 'PN021', 'KE007',  20, N'Cái',  '2030-12-10', N'Còn hàng'),
('LO046', 'SP057', 'PN021', 'KE007',  40, N'Hộp',  '2028-12-10', N'Còn hàng'),
('LO047', 'SP058', 'PN022', 'KE004',  90, N'Lọ',   '2027-12-15', N'Còn hàng'),
('LO048', 'SP059', 'PN022', 'KE004',  80, N'Lọ',   '2027-12-15', N'Còn hàng'),
('LO049', 'SP060', 'PN022', 'KE004',  50, N'Hộp',  '2027-12-15', N'Còn hàng'),
('LO050', 'SP001', 'PN023', 'KE001', 150, N'Vỉ',   '2028-01-05', N'Còn hàng'),
('LO051', 'SP003', 'PN023', 'KE001',  80, N'Tuýp', '2028-01-05', N'Còn hàng'),
('LO052', 'SP006', 'PN024', 'KE002', 100, N'Vỉ',   '2028-01-10', N'Còn hàng'),
('LO053', 'SP010', 'PN024', 'KE002',  60, N'Vỉ',   '2028-01-10', N'Còn hàng'),
('LO054', 'SP023', 'PN025', 'KE005',  50, N'Hộp',  '2028-02-01', N'Còn hàng'),
('LO055', 'SP024', 'PN025', 'KE005',  80, N'Lọ',   '2028-02-01', N'Còn hàng')
GO

-- =============================================
-- BẢNG CHI TIẾT PHIẾU NHẬP
-- PK: (MaPN, MaSP)
-- FK: MaPN -> PhieuNhap(MaPN)
-- FK: MaSP -> SanPham(MaSP)
-- =============================================
CREATE TABLE ChiTietPhieuNhap (
    MaPN      VARCHAR(10)   NOT NULL,
    MaSP      VARCHAR(10)   NOT NULL,
    SoLuong   INT           NOT NULL CHECK (SoLuong > 0),
    GiaNhap   DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (MaPN, MaSP),
    FOREIGN KEY (MaPN) REFERENCES PhieuNhap(MaPN) ON DELETE CASCADE,
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)   ON DELETE CASCADE
)
GO

INSERT INTO ChiTietPhieuNhap (MaPN, MaSP, SoLuong, GiaNhap)
VALUES
('PN001','SP001',100, 17000), ('PN001','SP002', 80, 24000),
('PN002','SP016', 80, 45000), ('PN002','SP017', 60, 32000),
('PN003','SP026',120,  5500), ('PN003','SP028', 50, 24000),
('PN004','SP036', 80, 26000), ('PN004','SP037', 60, 33000),
('PN005','SP006',120, 12000), ('PN005','SP007', 40, 68000),
('PN006','SP011', 50, 24000), ('PN006','SP012',150, 12000),
('PN007','SP021', 90, 10000), ('PN007','SP022', 80, 32000),
('PN008','SP031',200, 20000), ('PN008','SP032',150, 10000),
('PN009','SP005', 50, 32000),
('PN010','SP019', 40,130000), ('PN010','SP020', 70, 38000),
('PN011','SP008',100, 10000), ('PN011','SP009', 60, 45000),
('PN012','SP027',100,  7000), ('PN012','SP030', 80, 34000),
('PN013','SP038', 50, 47000), ('PN013','SP039', 40, 63000),
('PN014','SP014', 30, 85000), ('PN014','SP015',100, 14000),
('PN015','SP024',100, 17000), ('PN015','SP025', 70,  8000),
('PN016','SP041',100, 12000), ('PN016','SP042', 80, 24000), ('PN016','SP043', 60, 32000), ('PN016','SP044', 90,  8000),
('PN017','SP045', 70, 19000), ('PN017','SP046', 50, 10000), ('PN017','SP047', 40, 22000),
('PN018','SP048', 80, 38000), ('PN018','SP049', 60, 32000), ('PN018','SP050', 50, 55000), ('PN018','SP051', 40, 68000),
('PN019','SP052', 60, 30000), ('PN019','SP053', 50, 26000),
('PN020','SP054', 40, 15000), ('PN020','SP055', 30, 12000),
('PN021','SP056', 20,600000), ('PN021','SP057', 40, 52000),
('PN022','SP058', 90, 24000), ('PN022','SP059', 80, 29000), ('PN022','SP060', 50,140000),
('PN023','SP001',150, 17000), ('PN023','SP003', 80, 38000),
('PN024','SP006',100, 12000), ('PN024','SP010', 60, 15000),
('PN025','SP023', 50, 38000), ('PN025','SP024', 80, 17000)
GO

-- =============================================
-- BẢNG KHÁCH HÀNG
-- PK: MaKH
-- =============================================
CREATE TABLE KhachHang (
    MaKH        VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenKH       NVARCHAR(100) NOT NULL,
    NamSinh     INT,
    SDT         VARCHAR(10)   NOT NULL UNIQUE,
    Email       VARCHAR(100),
    GioiTinh    BIT           NOT NULL DEFAULT 1,
    DiaChi      NVARCHAR(255),
    DiemTichLuy INT           DEFAULT 0
)
GO

INSERT INTO KhachHang (MaKH, TenKH, NamSinh, SDT, GioiTinh, DiaChi, DiemTichLuy)
VALUES
('KH000', N'Khách vãng lai',  NULL, '0000000000', 1, NULL,                           0),
('KH001', N'Nguyễn Văn An',   1998, '0987654322', 1, N'Quận 1, TP.HCM',          185),
('KH002', N'Trần Thị Bích',   2001, '0987654321', 0, N'Quận 3, TP.HCM',          211),
('KH003', N'Lê Văn Minh',     1990, '0123456789', 1, N'Quận Gò Vấp, TP.HCM',    101),
('KH004', N'Phạm Thu Hằng',   2003, '0369888777', 0, N'Quận Tân Bình, TP.HCM',   225),
('KH005', N'Đặng Hoàng Long', 1985, '0933444555', 1, N'TP. Thủ Đức, TP.HCM',     229),
('KH006', N'Lý Minh Châu',    1975, '0902113344', 0, N'Quận Bình Thạnh, TP.HCM',  320),
('KH007', N'Trương Văn Hảo',  1988, '0915667788', 1, N'Quận 7, TP.HCM',             95),
('KH008', N'Ngô Thị Yến',     1992, '0926778899', 0, N'Quận 2, TP.HCM',            410),
('KH009', N'Huỳnh Đức Tài',   1965, '0937889900', 1, N'Quận 12, TP.HCM',           180),
('KH010', N'Đỗ Thanh Tùng',   2000, '0948990011', 1, N'Quận Bình Tân, TP.HCM',      60),
('KH011', N'Vũ Thị Hạnh',     1980, '0959001122', 0, N'Quận 4, TP.HCM',            250),
('KH012', N'Đinh Công Phát',   1970, '0960112233', 1, N'Quận 8, TP.HCM',            520),
('KH013', N'Tô Ngọc Linh',    1995, '0971223344', 0, N'Quận Phú Nhuận, TP.HCM',    145),
('KH014', N'Lâm Quốc Bảo',    1983, '0982334455', 1, N'Quận 9, TP.HCM',             30),
('KH015', N'Mai Thị Hương',    1978, '0993445566', 0, N'Quận Tân Phú, TP.HCM',      385)
GO

-- =============================================
-- BẢNG HÓA ĐƠN
-- PK: MaHD
-- FK: MaKH -> KhachHang(MaKH)
-- FK: MaNV -> NhanVien(MaNV)
-- =============================================
CREATE TABLE HoaDon (
    MaHD                VARCHAR(10)   NOT NULL PRIMARY KEY,
    NgayLap             DATETIME      NOT NULL DEFAULT GETDATE(),
    TongTien            DECIMAL(18,2) DEFAULT 0,
    DiemTichLuy         INT           DEFAULT 0,
    PhuongThucThanhToan NVARCHAR(50)  NOT NULL,
    MaNV                VARCHAR(10)   NOT NULL,
    MaKH                VARCHAR(10)   NOT NULL,
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)  ON DELETE CASCADE,
    FOREIGN KEY (MaKH) REFERENCES KhachHang(MaKH) ON DELETE CASCADE
)
GO

INSERT INTO HoaDon (MaHD, NgayLap, TongTien, DiemTichLuy, PhuongThucThanhToan, MaNV, MaKH)
VALUES
('HD00001','2025-11-13',  99000,   0, N'Tiền mặt',    'NV001','KH000'),
('HD00002','2025-11-13', 126000, 126, N'Chuyển khoản','NV001','KH002'),
('HD00003','2025-11-14',  84000,   0, N'Tiền mặt',    'NV001','KH000'),
('HD00004','2025-11-14', 101000, 101, N'Quẹt thẻ',    'NV001','KH003'),
('HD00005','2025-11-15',  96000,  96, N'Chuyển khoản','NV001','KH004'),
('HD00006','2025-11-13',  85000,  85, N'Quẹt thẻ',    'NV002','KH002'),
('HD00007','2025-11-13',  54000,  54, N'Tiền mặt',    'NV002','KH005'),
('HD00008','2025-11-14', 136000,   0, N'Tiền mặt',    'NV002','KH000'),
('HD00009','2025-11-14',  80000,  80, N'Chuyển khoản','NV002','KH001'),
('HD00010','2025-11-15',  63000,   0, N'Quẹt thẻ',    'NV002','KH000'),
('HD00011','2025-11-13', 129000, 129, N'Quẹt thẻ',    'NV003','KH004'),
('HD00012','2025-11-14', 180000,   0, N'Tiền mặt',    'NV003','KH000'),
('HD00013','2025-11-14', 175000, 175, N'Chuyển khoản','NV003','KH005'),
('HD00014','2025-11-15', 339000,   0, N'Quẹt thẻ',    'NV003','KH000'),
('HD00015','2025-11-15', 105000, 105, N'Tiền mặt',    'NV003','KH001'),
('HD00016','2025-12-01',  72000,  72, N'Tiền mặt',    'NV005','KH006'),
('HD00017','2025-12-02', 161000,   0, N'Tiền mặt',    'NV005','KH000'),
('HD00018','2025-12-05', 100000, 100, N'Chuyển khoản','NV006','KH007'),
('HD00019','2025-12-05', 165000, 165, N'Quẹt thẻ',    'NV006','KH008'),
('HD00020','2025-12-10', 115000, 115, N'Chuyển khoản','NV001','KH009'),
('HD00021','2025-12-10',  78000,   0, N'Tiền mặt',    'NV001','KH000'),
('HD00022','2025-12-15',  91000,  91, N'Quẹt thẻ',    'NV002','KH010'),
('HD00023','2025-12-15', 126000, 126, N'Chuyển khoản','NV002','KH011'),
('HD00024','2026-01-05', 935000, 935, N'Quẹt thẻ',    'NV003','KH012'),
('HD00025','2026-01-05', 190000,   0, N'Tiền mặt',    'NV003','KH000'),
('HD00026','2026-01-10', 279000, 279, N'Chuyển khoản','NV005','KH013'),
('HD00027','2026-02-01', 165000, 165, N'Tiền mặt',    'NV006','KH014'),
('HD00028','2026-02-15', 290000, 290, N'Quẹt thẻ',    'NV010','KH015'),
('HD00029','2026-03-01', 144000, 144, N'Chuyển khoản','NV001','KH006'),
('HD00030','2026-03-15', 204000, 204, N'Tiền mặt',    'NV002','KH008')
GO

-- =============================================
-- BẢNG CHI TIẾT HÓA ĐƠN
-- PK: (MaHD, MaSP)
-- FK: MaHD -> HoaDon(MaHD)
-- FK: MaSP -> SanPham(MaSP)
-- =============================================
CREATE TABLE ChiTietHoaDon (
    MaHD      VARCHAR(10)   NOT NULL,
    MaSP      VARCHAR(10)   NOT NULL,
    SoLuong   INT           NOT NULL CHECK (SoLuong > 0),
    DonGia    DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (MaHD, MaSP),
    FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD)  ON DELETE CASCADE,
    FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP) ON DELETE CASCADE
)
GO

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGia)
VALUES
('HD00001','SP001', 3, 25000), ('HD00001','SP035', 2, 12000),
('HD00002','SP022', 2, 45000), ('HD00002','SP013', 3, 12000),
('HD00003','SP037', 1, 48000), ('HD00003','SP035', 3, 12000),
('HD00004','SP006', 2, 18000), ('HD00004','SP009', 1, 65000),
('HD00005','SP030', 2, 48000),
('HD00006','SP011', 1, 35000), ('HD00006','SP024', 2, 25000),
('HD00007','SP021', 2, 15000), ('HD00007','SP026', 3,  8000),
('HD00008','SP038', 2, 68000),
('HD00009','SP028', 1, 35000), ('HD00009','SP032', 3, 15000),
('HD00010','SP017', 1, 45000), ('HD00010','SP012', 1, 18000),
('HD00011','SP034', 1, 85000), ('HD00011','SP033', 2, 22000),
('HD00012','SP019', 1,180000),
('HD00013','SP014', 1,120000), ('HD00013','SP020', 1, 55000),
('HD00014','SP040', 2,125000), ('HD00014','SP039', 1, 89000),
('HD00015','SP018', 1, 85000), ('HD00015','SP004', 1, 20000),
('HD00016','SP041', 2, 18000), ('HD00016','SP044', 3, 12000),
('HD00017','SP001', 5, 25000), ('HD00017','SP035', 3, 12000),
('HD00018','SP048', 1, 55000), ('HD00018','SP049', 1, 45000),
('HD00019','SP016', 2, 65000), ('HD00019','SP058', 1, 35000),
('HD00020','SP043', 1, 45000), ('HD00020','SP042', 2, 35000),
('HD00021','SP012', 3, 18000), ('HD00021','SP013', 2, 12000),
('HD00022','SP045', 2, 28000), ('HD00022','SP002', 1, 35000),
('HD00023','SP022', 2, 45000), ('HD00023','SP025', 3, 12000),
('HD00024','SP056', 1,850000), ('HD00024','SP034', 1, 85000),
('HD00025','SP003', 2, 55000), ('HD00025','SP015', 4, 20000),
('HD00026','SP060', 1,195000), ('HD00026','SP059', 2, 42000),
('HD00027','SP036', 2, 38000), ('HD00027','SP039', 1, 89000),
('HD00028','SP019', 1,180000), ('HD00028','SP020', 2, 55000),
('HD00029','SP041', 3, 18000), ('HD00029','SP026', 5,  8000), ('HD00029','SP024', 2, 25000),
('HD00030','SP014', 1,120000), ('HD00030','SP052', 2, 42000)
GO
