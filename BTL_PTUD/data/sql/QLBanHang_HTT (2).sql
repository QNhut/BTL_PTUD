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
    HinhAnh VARCHAR(255),
    FOREIGN KEY (MaChucVu) REFERENCES ChucVu(MaChucVu) ON DELETE CASCADE
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
    ('KM2025001', N'Giảm giá toàn bộ sản phẩm', 5, '2026-01-01', '2026-12-31', 1),
    ('KM2025002', N'Ưu đãi cuối tuần', 7, '2026-01-01', '2026-12-31', 1),
    ('KM2025003', N'Khuyến mãi ngày lễ', 10, '2026-01-01', '2026-12-31', 1),
    ('KM2025004', N'Giảm giá mùa hè', 8, '2026-05-01', '2026-08-31', 1),
    ('KM2025005', N'Giảm giá mùa đông', 8, '2026-11-01', '2026-12-31', 1),
    ('KM2025006', N'Giảm giá vật tư y tế', 10, '2026-01-01', '2026-12-31', 1),
    ('KM2025007', N'Giảm giá theo lô nhập', 15, '2026-01-01', '2026-12-31', 1),
    ('KM2025009', N'Ưu đãi đặc biệt trong', 7, '2026-01-01', '2026-12-31', 1);

GO
-- Chuẩn bị data loại sản phẩm
INSERT INTO LoaiSanPham (MaLoaiSanPham, TenLoaiSanPham, MoTa) 
VALUES
--THỰC PHẨM CHỨC NĂNG
('LSP2026001', N'Hỗ trợ sức khỏe', N'Hỗ trợ các vấn đề liên quan đến sức khỏe'),
('LSP2026002', N'Dinh dưỡng', N'Các thực phẩm bổ sung dinh dưỡng'),
('LSP2026003', N'Làm đẹp', N'Thực phẩm hỗ trợ làm đẹp da, tóc, móng, ...'),

--DƯỢC MỸ PHẨM
('LSP2026004', N'Chăm sóc làn da', N'Sản phẩm chăm sóc da mặt, cơ thể'),
('LSP2026005', N'Chăm sóc tóc', N'Dầu gội, dưỡng tóc da đầu'),
('LSP2026006', N'Mỹ phẩm tổng hợp', N'Mỹ phẩm đa dạng các loại'),
('LSP2026007', N'Chăm sóc vùng mắt', N'Kem mắt và serum chuyên biệt'),
('LSP2026008', N'Sản phẩm tự nhiên', N'Mỹ phẩm từ thành phần thiên nhiên'),

--THUỐC
('LSP2026009', N'Thuốc hô hấp - Tai mũi họng', N'Thuốc điều trị các vấn đề hô hấp và liên quan đến tai mũi họng'),
('LSP2026010', N'Thuốc tiêu hóa - Gan mật', N'Thuốc điều trị bao tử gan mật'),
('LSP2026011', N'Thuốc tim mạch', N'Thuốc điều trị các bệnh về tim mạch'),
('LSP2026012', N'Thuốc thần kinh', N'Thuốc điều trị các bệnh thần kinh não bộ'),
('LSP2026013', N'Thuốc cơ xương khớp', N'Thuốc điều trị các bệnh về cơ xương khớp'),
('LSP2026014', N'Thuốc da liễu', N'Thuốc điều trị các bệnh về da'),
('LSP2026015', N'Khác', N'Kháng sinh, vitamin, thuốc đặc trị'),

--CHĂM SÓC CÁ NHÂN
('LSP2026016', N'Thực phẩm - Đồ uống', N'Thực phẩm đồ uống tổng hợp'),
('LSP2026017', N'Vệ sinh cá nhân', N'Sản phẩm vệ sinh cá nhân'),
('LSP2026018', N'Chăm sóc răng miệng', N'Kem đánh răng, nước súc miệng'),
('LSP2026019', N'Đồ dùng gia đình', N'Các đồ dùng gia đình hỗ trợ'),
('LSP2026020', N'Hàng tổng hợp', N'Sản phẩm hàng tổng hợp'),
('LSP2026021', N'Tinh dầu các loại', N'Tinh dầu thiên nhiên'),
('LSP2026022', N'Thiết bị làm đẹp', N'Máy móc thiết bị làm đẹp'),

--THIẾT BỊ Y TẾ
('LSP2026023', N'Dụng cụ y tế', N'Các dụng cụ y tế cơ bản'),
('LSP2026024', N'Dụng cụ theo dõi', N'Máy đo huyết áp, máy đo đường'),
('LSP2026025', N'Dụng cụ sơ cứu', N'Bộ sơ cứu cấp cứu'),
('LSP2026026', N'Thiết bị khác', N'Các loại thiết bị khác');
GO
-- Chuẩn bị bản sản phẩm
CREATE TABLE SanPham (
    MaSanPham VARCHAR(15) NOT NULL PRIMARY KEY,
    TenSanPham NVARCHAR(150) NOT NULL,
    CongDung NVARCHAR(255) NOT NULL,
    ThanhPhan NVARCHAR(MAX) NOT NULL,
    HanSuDung INT NOT NULL,
    GiaThanh FLOAT NOT NULL,
    NoiSanXuat NVARCHAR(50) NOT NULL,
    DonViTinh NVARCHAR(50) NOT NULL,
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
INSERT INTO SanPham
(MaSanPham, TenSanPham, CongDung, ThanhPhan, HanSuDung, GiaThanh, NoiSanXuat, MaLoaiSanPham, MaKhuyenMai, MaThue, TrangThai, hinhAnh, DonViTinh)
VALUES

-- ========== LSP2026001: Hỗ trợ sức khỏe ==========
('SP2026001', N'BANG CEVRAI 60V', N'Viên nam lực cải thiện chức năng sinh lý cho người trưởng thành Bang Cevrai (60 viên)', N'Bột rễ gừng 150mg, Vitamin C 60mg, Zinc gluconate 75mg, Bột ớt 30mg, Bột hạt Guarana 150mg, Rễ nhân sâm 150mg', 24, 530000.0, N'Pháp', 'LSP2026001', 'KM2025001', 'T2026002', 1, 'bang_cevrai_60v.png', N'Hộp'),
('SP2026002', N'CƯỜNG GÂN CỐT - VAI GÁY KINGPHAR 60V', N'Viên uống hỗ trợ bổ thận, giúp hạn chế đau nhức khớp xương do phong thấp Cường Gân Cốt - Vai Gáy Kingphar (60 viên)', N'Cao thiên niên kiện 150mg, Cao độc hoạt 150mg, Cao Cốt Toái Bổ 100mg, Cao đương quy 50mg, Cao Thổ Phục linh 50mg, Tá dược vừa đủ , Cao uy linh tiên 120mg, Cao Tang kí sinh 100mg, Cao ngưu tất 80mg, Cao bạch quả 100mg, Cao kê huyết đằng 150mg', 10, 203000.0, N'Việt Nam', 'LSP2026001', 'KM2025001', 'T2026001', 1, 'cuong_gan_cot_vai_gay_kingphar_60v.png', N'Hộp'),
('SP2026003', N'KIM ĐỞM KHANG HỒNG BÀNG 3X10', N'Viên uống hỗ trợ sỏi mật Kim Đởm Khang Hồng Bàng (3 vỉ x 10 viên)', N'Diệp hạ châu 60mg, Nhân trần 20mg, Chi tử 50mg, Uất Kim 100mg, Hoàng bá 150mg, Sài Hồ 120mg, Chỉ xác 30mg, Tá dược vừa đủ  ', 24, 160000.0, N'Việt Nam', 'LSP2026001', 'KM2025001', 'T2026005', 1, 'kim_om_khang_hong_bang_3x10.png', N'Hộp'),
('SP2026004', N'LIVERWELL TRƯỜNG THỌ 12X5', N'Viên uống hỗ trợ thanh nhiệt, mát gan, giải độc, bảo vệ gan LiverWell (12 vỉ x 5 viên)', N'Atisô 75mg, Diệp hạ châu 150mg, Nhân trần 100mg, Sài đất 75mg, Cà gai leo 100mg, Rau má 75mg, Vitamin B1 3mg, Vitamin B6 3mg, Vitamin B2 1mg, Tá dược vừa đủ ', 7, 140000.0, N'Việt Nam', 'LSP2026001', 'KM2025001', 'T2026002', 1, 'liverwell_truong_tho_12x5.png', N'Hộp'),
('SP2026005', N'OTIV ECOGREEN 60V', N'Viên uống giúp tăng cường dưỡng chất cho não Ecogreen OTiV (60 viên)', N'Ginkgo biloba extract 80mg, Blueberry Extract (4:1) 100mg', 15, 670000.0, N'Hoa Kỳ', 'LSP2026001', 'KM2025001', 'T2026004', 1, 'otiv_ecogreen_60v.png', N'Lọ'),
('SP2026006', N'VIÊN UỐNG BỔ KHỚP VITATREE GLUCOSAMINE 1500 PLUS SHARK CARTILAGE 100V', N'Viên uống hỗ trợ duy trì chức năng của khớp, sụn khớp Vitatree Glucosamine 1500 Plus Shark Cartilage (100 viên)', N'bột sụn vi cá mập 250mg, Glucosamine Hydrochloride 1500mg', 14, 643000.0, N'Úc', 'LSP2026001', 'KM2025001', 'T2026005', 1, 'vien_uong_bo_khop_vitatree_glucosamine_1500_plus_shark_cartilage_100v.png', N'Lọ'),
('SP2026007', N'VIÊN UỐNG BỔ NÃO ÍCH TRÍ GOLD 60V', N'Viên uống tăng cường tuần hoàn não, giảm rối loạn tiền đình Bổ Não Ích Trí Gold (60 viên)', N'Ginkgo biloba 160mg, Rau đắng biển 134mg, Magnesium gluconate 100mg, Thục địa 200mg, Ngưu tất (Rễ) 200mg, Đan sâm 200mg, Ích mẫu 200mg, Bạch thược 100mg, Đương quy 100mg, Tá dược vừa đủ 2Viên', 8, 210000.0, N'Việt Nam', 'LSP2026001', 'KM2025001', 'T2026005', 1, 'vien_uong_bo_nao_ich_tri_gold_60v.png', N'Hộp'),
('SP2026008', N'VIÊN UỐNG CẢI THIỆN TRÍ NHỚ, BỔ NÃO MIND ENERGY JPANWELL 60V', N'Viên uống bổ não, cải thiện trí nhớ Mind Energy Jpanwell (60 viên)', N'Tá dược vừa đủ , Chiết xuất sò điệp 24mg', 29, 2300000.0, N'Nhật Bản', 'LSP2026001', 'KM2025001', 'T2026005', 1, 'vien_uong_cai_thien_tri_nho_bo_nao_mind_energy_jpanwell_60v.png', N'Lọ'),
('SP2026009', N'VIÊN UỐNG HỖ TRỢ GIẢM ĐAU KHỚP, THOÁI HÓA KHỚP RELAX PLUS JPANWELL 60V', N'Viên uống Relax Plus Jpanwell hỗ trợ giảm đau khớp, khó vận động do viêm khớp (60 viên)', N'Metyl Sulfonyl Metan 500mg, Dolomite 160mg, Hà thủ ô 100mg, Glucosamin 60mg, Vỏ quế 40mg, Tảo xoắn 20mg, Nấm linh chi 20mg, Vitamin C 18.34mg, Đương quy 10mg, Bạch quả 6mg, Chondroitin 8mg, Chiết xuất nghệ 6mg, Vitamin D 6.001mg, Cam thảo 6mg, Magie 10mg, Sắt Heme 6mg, Vitamin B3 2.16mg, Vitamin E 1.06mg, Chiết xuất vỏ cây thông 1mg, Vitamin B5 0.96mg, Vitamin B2 0.24mg, Vitamin B1 0.22mg, Vitamin B6 0.22mg, Vitamin A 0.154mg, Axit Folic 0.044mg, Vitamin B12 0.00048mg', 10, 990000.0, N'Nhật Bản', 'LSP2026001', 'KM2025001', 'T2026002', 1, 'vien_uong_ho_tro_giam_au_khop_thoai_hoa_khop_relax_plus_jpanwell_60v.png', N'Lọ'),
('SP2026010', N'VIÊN UỐNG HỖ TRỢ ỔN ĐỊNH ĐƯỜNG HUYẾT BLOOD SUGAR CONTROL VITAMINS FOR LIFE 60V', N'Viên uống ổn định đường huyết, tốt cho người bị tiểu đường Blood Sugar Control Vitamins For Life (60 viên)', N'Fenugreek 166mg, hạt Cỏ ca ri 166mg, Mướp đắng (Quả) 50mg, Dây thìa canh 66mg, Dâu tằm 66mg, Yến Mạch 50mg, alpha Lipoic acid 33mg, Biotin 34mg, Chromium picolinate 200Mcg', 28, 380000.0, N'Hoa Kỳ', 'LSP2026001', 'KM2025001', 'T2026005', 1, 'vien_uong_ho_tro_on_inh_uong_huyet_blood_sugar_control_vitamins_for_life_60v.png', N'Lọ'),

-- ========== LSP2026002: Dinh dưỡng ==========
('SP2026011', N'NƯỚC YẾN SÀO CAO CẤP CHO TRẺ EM NUNEST KID VỊ CHUỐI 6 HŨ X 70ML', N'Nước Yến Sào Cao Cấp Cho Trẻ Em Nunest Kid Vị Chuối (6 hũ x 70ml)', N'Yến sào chưng sẵn 15%, Beta Glucan 42.8mg, Lysine 85.7mg, Vitamin D3 7mcg, Nước , Đường phèn , Sucrose , Canxi lactate , Chất ổn định , Chuối , Hương chuối tổng hợp , Chất bảo quản ', 9, 225000.0, N'Việt Nam', 'LSP2026002', 'KM2025001', 'T2026002', 1, 'nuoc_yen_sao_cao_cap_cho_tre_em_nunest_kid_vi_chuoi_6_hu_x_70ml.png', N'Hộp'),
('SP2026012', N'NƯỚC YẾN SÀO CHO TRẺ EM HƯƠNG VANI GREENBABI 4 HŨ X72G', N'Nước Yến Sào Cho Trẻ Em Hương Vani Greenbabi (4 hũ x 72g)', N'Nước (aqua) , Sợi yến 20%, Đường phèn , Chất làm dày , Taurine , Chất ổn định , Hương vani giống tự nhiên , Prebiotics ', 18 , 164000.0, N'Việt Nam', 'LSP2026002', 'KM2025001', 'T2026003', 1, 'nuoc_yen_sao_cho_tre_em_huong_vani_greenbabi_4_hu_x72g.png', N'Hộp'),
('SP2026013', N'SỮA ABBOTT ENSURE GOLD STRENGTHPRO HƯƠNG LÚA MẠCH 800G', N'Sữa tăng cường sức khỏe khối cơ tăng miễn dịch Ensure Gold StrengthPro hương lúa mạch (800g)', N'Tinh bột bắp thủy phân , Dầu thực vật , Sucrose , Natri caseinat , Đạm đậu nành , Oligofructose , Đạm whey , Khoáng chất , CaHMB , Hương liệu lúa mạch tổng hợp , Beta Glucan , Vitamins , Choline Chloride , Taurine , L-Carnitine ', 6 , 932000.0, N'Hoa Kỳ', 'LSP2026002', 'KM2025001', 'T2026004', 1, 'sua_abbott_ensure_gold_strengthpro_huong_lua_mach_800g.png', N'Lon'),
('SP2026014', N'SỮA ABBOTT ENSURE GOLD STRENGTHPRO HƯƠNG VANI ÍT NGỌT 800G', N'Sữa tăng cường sức khỏe khối cơ tăng miễn dịch Ensure Gold StrengthPro hương vani ít ngọt (800g)', N'Tinh bột bắp thủy phân , Dầu thực vật , Sucrose , Natri caseinat , Đạm đậu nành , Oligofructose , Đạm whey , Khoáng chất , CaHMB , Hương vani tổng hợp , Beta Glucan , Vitamins , Choline Chloride , Taurine , L-Carnitine ', 13 , 932000.0, N'Hoa Kỳ', 'LSP2026002', 'KM2025001', 'T2026003', 1, 'sua_abbott_ensure_gold_strengthpro_huong_vani_it_ngot_800g.png', N'Lon'),
('SP2026015', N'SỮA ANLENE HEART PLUS 750G', N'Sữa bổ sung hệ dưỡng chất dành cho sức khỏe tim mạch Anlene Heart Plus (750g)', N'Sữa bột từ sữa bò , Inulin , Collagen , DHA , Vitamin ', 29 , 580000.0, N'New Zealand', 'LSP2026002', 'KM2025001', 'T2026003', 1, 'sua_anlene_heart_plus_750g.png', N'Lon'),
('SP2026016', N'SỮA GLUCERNA ABBOTT 800G', N'Sữa bổ sung dinh dưỡng đặc biệt cho người đái tháo đường Glucerna Abbott (800g)', N'Choline , Chất béo thực vật , Đạm whey , Vitamin A , Vitamin D3 , Lecithins, soya , Dầu đậu nành , dầu hướng dương , Fructose , Protein , Glycerin ', 18 , 922000.0, N'Hoa Kỳ', 'LSP2026002', 'KM2025001', 'T2026005', 1, 'sua_glucerna_abbott_800g.png', N'Lon'),
('SP2026017', N'SỮA NUTREN JUNIOR 1-10 TUỔI 800G', N'Sữa thay thế bữa ăn hàng ngày cho trẻ suy dinh dưỡng Nutren Junior ( 800g)', N'Maltodextrin , Sucrose , Milk protein , Vegetable Oils , Medium chain triglyceride , Minerals , Prebiotic Fibres , Emulsifier , Nature Identical Flavour , Choline bitartrate , Acidity Probiotics , Taurine , L-Carnitine ', 34 , 600000.0, N'Thụy Sĩ', 'LSP2026002', 'KM2025001', 'T2026004', 1, 'sua_nutren_junior_1_10_tuoi_800g.png', N'Lon'),
('SP2026018', N'SỮA PEPTAMEN NESTLE 400G', N'Sữa cải thiện việc hấp thu đạm không gây tiêu chảy Peptamen Nestlé (400g)', N'Maltodextrin , Đạm Whey thủy phân bằng enzym , Dầu Cọ , Dầu dừa , Sucrose , Dầu hạt cải , Khoáng chất , Chất nhũ hoá , Clorua bitartrate , Taurine , L-Carnitine , Vitamin A , Vitamin B1 , Vitamin B2 , Vitamin B5 , Vitamin B6 , Vitamin C , Vitamin D , Vitamin E , Vitamin K1 , Biotin , Acid folic , Nicotinamide , Hương liệu giống tự nhiên ', 29 , 539000.0, N'Thụy Sĩ', 'LSP2026002', 'KM2025001', 'T2026001', 1, 'sua_peptamen_nestle_400g.png', N'Lon'),
('SP2026019', N'SỮA PROSURE VANI ABBOTT 380G', N'Sữa bổ sung dinh dưỡng chuyên biệt cho người đang sụt cân Prosure Abbott (380g)', N'Chất Xơ 2.4 , Vitamin tổng hợp , Năng lượng 1.25 , Protein thuỷ phân 21%, EPA 1.1 , Taurine , Carnitine ', 12 , 528000.0, N'Hoa Kỳ', 'LSP2026002', 'KM2025001', 'T2026004', 1, 'sua_prosure_vani_abbott_380g.png', N'Lon'),
('SP2026020', N'TRÀ THẢO MỘC ĐÔNG TRÙNG HẠ THẢO TÚI LỌC DATINO PREMIUM TEA 20 TÚI X 2,5G', N'Trà thảo mộc đông trùng hạ thảo túi lọc Datino premium tea (20 túi x 2,5g)', N'Đông trùng hạ thảo 87.5%, Cỏ ngọt , Cam thảo ', 28, 99000.0, N'Việt Nam', 'LSP2026002', 'KM2025001', 'T2026002', 1, 'tra_thao_moc_ong_trung_ha_thao_tui_loc_datino_premium_tea_20_tui_x_2_5g.png', N'Hộp'),

-- ========== LSP2026004: Chăm sóc làn da ==========
('SP2026021', N'DẦU GIÓ LĂN RED LION MEDICATED OIL YELLOW AGIMEXPHARM PEPPERMINT CLOVE BUD 10ML', N'Dầu gió lăn Red Lion Medicated Oil Yellow Agimexpharm Peppermint Clove Bud (10ml)', N'Methyl salicylate 350mg, Peppermint oil 2680mg, Menthol 4500mg', 18 , 39000.0, N'Việt Nam', 'LSP2026004', 'KM2025001', 'T2026005', 1, 'dau_gio_lan_red_lion_medicated_oil_yellow_agimexpharm_peppermint_clove_bud_10ml.png', N'Chai'),
('SP2026022', N'GEL RỬA MẶT VÀ LOẠI BỎ TẾ BÀO CHẾT KHÔNG CHỨA XÀ PHÒNG SVR SEBIACLEAR GEL MOUSSANT 55ML', N'Gel rửa mặt và loại bỏ tế bào chết không chứa xà phòng SVR Sebiaclear Gel Moussant 55ml', N'Aqua , Disodium Laureth Sulfosuccinate , Coco-betaine , PEG-7 Glyceryl Cocoate , Glycerin , Sodium Laureth Sulfate , Gluconolactone , Peg-120 Methyl Glucose Dioleate , Calcium gluconate , Sarcosine , Butylene glycol , Lactic Acid , Pentylene Glycol ', 9 , 140000.0, N'Pháp', 'LSP2026004', 'KM2025001', 'T2026004', 1, 'gel_rua_mat_va_loai_bo_te_bao_chet_khong_chua_xa_phong_svr_sebiaclear_gel_moussant_55ml.png', N'Tuýp'),
('SP2026023', N'KEM BÔI NGỪA MỤN FIXDERMA SALYZAP LOTION FOR ACNE 20ML', N'Kem bôi ngừa mụn Fixderma Salyzap Lotion For Acne 20ml', N'Propanol-2  , Butylene glycol  , 10-Hydroxydecanoic Acid  , Sebacic Acid  , 1,10-Decanediol  , Kaolin  , Glycerol  , Salicylic acid  , Panthenol  , Camellia Sinensis Leaf Extract  , Tocopheryl acetate  , Melaleuca Alternifolia (Tea Tree) Oil  ', 31 , 345000.0, N'Mỹ', 'LSP2026004', 'KM2025001', 'T2026004', 1, 'kem_boi_ngua_mun_fixderma_salyzap_lotion_for_acne_20ml.png', N'Tuýp'),
('SP2026024', N'KEM CHỐNG NẮNG GIẢM BÓNG NHỜN LA ROCHE-POSAY LABORATOIRE DERMATOLOGIQUE ANTHELIOS XL ANTI-SHINE NON-PERFUMED DRY TOUCH GEL-CREAM 50ML', N'Kem chống nắng giảm bóng nhờn La Roche-posay Laboratoire Dermatologique Anthelios Xl Anti-shine Non-perfumed Dry Touch Gel-cream SPF 50+ PA++++ 50ml', N'Aqua , Homosalate , Silica , Octocrylene , Ethylhexyl Salicylate , Butyl Methoxydibenzoylmethane , Propylene glycol , Caprylyl glycol , P-Anisic Acid , Stearic acid , Glycerin , Drometrizole trisiloxane , Dimethicone , Acrylates , Perlite , Aluminum Starch Octenylsuccinate , Potassium Cetyl Phosphate , Pentylene Glycol , Bis-ethylhexyloxyphenol Methoxyphenyl Triazine , Ethylhexyl Triazone , Acrylates Copolymer , Titanium dioxide , Xanthan gum , Tocopherol , Zinc gluconate , Phenoxyethanol , Aluminum hydroxide , Stearyl alcohol , Scutellaria Baicalensis , Inulin Lauryl Carbamate , Peg-8 Laurate , Silica Silylate , Disodium EDTA , triethanolamine , Terephthalylidene dicamphor sulfonic acid ', 27, 590000.0, N'Pháp', 'LSP2026004', 'KM2025001', 'T2026001', 1, 'kem_chong_nang_giam_bong_nhon_la_roche_posay_laboratoire_dermatologique_anthelios_xl_anti_shine_non.png', N'Tuýp'),
('SP2026025', N'KEM DƯỠNG GIẢM VÀ NGĂN NGỪA THÂM NÁM LA ROCHE POSAY MELA B3 SPF30 ANTI DARK SPOTS CORRECTIVE PROTECTIVE CARE ANTI RECURRENCE 40ML', N'Kem dưỡng La Roche-Posay Mela B3 Cream SPF 30 dưỡng da mềm mịn, mờ thâm nám, dưỡng da sáng và đều màu ', N'Aqua , C15-19 Alkane , Diisopropyl Sebacate , Niacinamide , Ethylhexyl Triazone , Glycerin , Bis-ethylhexyloxyphenol Methoxyphenyl Triazine , Silica , Drometrizole trisiloxane ', 24 , 1049000.0, N'Pháp', 'LSP2026004', 'KM2025001', 'T2026002', 1, 'kem_duong_giam_va_ngan_ngua_tham_nam_la_roche_posay_mela_b3_spf30_anti_dark_spots_corrective_protect.png', N'Tuýp'),
('SP2026026', N'KEM DƯỠNG LA ROCHEPOSAY CICAPLAST BAUME B5+ LÀM DỊU VÀ PHỤC HỒI DA CHO TRẺ EM VÀ TRẺ SƠ SINH HỘP 100ML', N'Kem bôi phục hồi và dịu da La Roche-posay Cicaplast Baume B5+ 100ml', N'CI 77891 , Pentaerythrityl Tetra-di-t-butyl Hydroxyhydrocinnamate , Tocopherols , Polyglyceryl-4 Isostearate , Maltodextrin , Acetylated Glycol Stearate , Lactobacillus , Trisodium Ethylenediamine Disuccinate , Citric Acid , Vitreoscilla Ferment , Caprylyl glycol , Capryloyl Glycine , Mannose , Magnesium sulfate , Aluminum hydroxide , Silica , Alpha-Glucan Oligosaccharide , Manganese gluconate , Madecassoside , Zinc gluconate , Polymnia Sonchifolia Root Juice , Centella Asiatica Leaf Extract , Trihydroxystearin , Cetyl Peg/Ppg-10/1 Dimethicone , Butylene glycol , Propanediol , Zea mays starch , Panthenol , Butyrospermum Parkii Butter , Glycerin , Dimethicone , Hydrogenated Polyisobutene , Aqua/ Water ', 9 , 880000.0, N'Pháp', 'LSP2026004', 'KM2025001', 'T2026003', 1, 'kem_duong_la_rocheposay_cicaplast_baume_b5_lam_diu_va_phuc_hoi_da_cho_tre_em_va_tre_so_sinh_hop_100.png', N'Tuýp'),
('SP2026027', N'KEM DƯỠNG ẨM SÁNG DA FIXDERMA FACE21 FACE CREAM 50G', N'Kem dưỡng ẩm sáng da Fixderma Face21 Face Cream 50g', N'Water , Isononyl Isononanoate , Octocrylene , Niacinamide , Butyrospermum Parkii Butter , Titanium dioxide , Xylitylglucoside, Anhydroxylitol, Xylitol , Cetearyl alcohol, Dicetyl Phosphate, Ceteth-10 , cetearyl alcohol, Dicetyl Phosphate, Ceteth-10 Phosphate , alpha-Arbutin , lecithin, arbutin, linolenic acid, lenoleic acid, tocopheryl acetate, ascorbyl palmitate, glutathione, alcohol, aqua , Phenoxyethanol, Ethylhexyglycerin , Tocopheryl acetate , Phytic Acid , Fragrance ', 7 , 393000.0, N'Mỹ', 'LSP2026004', 'KM2025001', 'T2026003', 1, 'kem_duong_am_sang_da_fixderma_face21_face_cream_50g.png', N'Tuýp'),
('SP2026028', N'KEM HỖ TRỢ ĐIỀU TRỊ MỤN - LA ROCHE-POSAY EFFACLAR DUO+ 40ML', N'Kem dưỡng da La Roche-Posay Effaclar Duo (+) hỗ trợ giảm viêm và giảm mụn đỏ tấy trong vòng 12 giờ (40ml)', N'Aqua , Glycerin , Dimethicone , Niacinamide , Isopropyl Lauroyl Sarcosinate , Silica , Methyl Methacrylate Crosspolymer , Potassium Cetyl Phosphate , Zinc PCA , Glyceryl stearate SE , Isohexadecane , Sodium hydroxide , Myristyl Myristate , Aluminum Starch Octenylsuccinate , Mannose , Disodium EDTA , Capryloyl Salicylic Acid , Caprylyl glycol , Xanthan gum , Polysorbate 80 , Acrylamide/Sodium Acrylate Copolymer , Salicylic acid , Piroctone Olamine , Parfum (Fragrance) , Isocetyl Stearate , Ammonium Polyacryloyldimethyl Taurate , 2-Oleamido-1,3-Octadecanediol , Poloxamer 338 , Vitreoscilla Ferment ', 23 , 475000.0, N'Pháp', 'LSP2026004', 'KM2025001', 'T2026002', 1, 'kem_ho_tro_ieu_tri_mun_la_roche_posay_effaclar_duo_40ml.png', N'Tuýp'),
('SP2026029', N'NƯỚC TẨY TRANG JMSOLUTION WATER LUMINOUS S.O.S RINGER LÀM SẠCH SÂU VÀ CẤP ẨM 500ML', N'Nước tẩy trang JMSolution Water Luminous S.O.S Ringer làm sạch sâu và cấp ẩm (500ml)', N'Water , Peg-6 Caprylic , Butylene glycol , Dipropylene glycol , 1,2-Hexanediol , Phenoxyethanol , PEG-7 Glyceryl Cocoate , Decyl glucoside , Peg-40 Hydrogenated Castor Oil , Disodium EDTA , Ethylhexylglycerin , Fragrance , Sodium Citrate , Alcohol , Citric Acid , Glycerin , Tripropylene glycol , Sodium carbonate ', 13 , 199000.0, N'Hàn Quốc', 'LSP2026004', 'KM2025001', 'T2026002', 1, 'nuoc_tay_trang_jmsolution_water_luminous_s_o_s_ringer_lam_sach_sau_va_cap_am_500ml.png', N'Chai'),
('SP2026030', N'NƯỚC TẮM THẢO DƯỢC SACHI 0+ MONTH 250ML - LÀM DỊU DA, GIÚP PHÒNG NGỪA RÔM SẢY', N'Nước tắm gội thảo dược cho bé Sachi Làm dịu mát da và phòng ngừa rôm sảy chai 250ml', N'Nước tinh khiết , Chiết xuất trà xanh , Tinh dầu tràm , Chiết xuất khổ qua , Chiết xuất rau má , Chiết xuất sài đất , Chiết xuất lá tre , Chiết xuất cúc la mã , chiết xuất đu đủ , Chiết xuất kim ngân hoa , Chiết xuất lá khế , Chiết xuất cỏ mần trầu , Tinh dầu cây mùi , Diệp lục tố , Polysorbate 20 , Xanthan gum , Carbomethylcellulose sodium , Cl 19140 , CI 42090 , Cl 45430 , Sodium benzoate , Potassium sorbate , Citric acid anhydrous ', 11 , 139000.0, N'Việt Nam', 'LSP2026004', 'KM2025001', 'T2026003', 1, 'nuoc_tam_thao_duoc_sachi_0_month_250ml_lam_diu_da_giup_phong_ngua_rom_say.png', N'Chai'),

-- ========== LSP2026005: Chăm sóc tóc ==========
('SP2026031', N'CETAPHIL BABY SHAMPOO HAIR 200ML - DẦU GỘI ĐẦU TRẺ EM', N'Dầu gội cho bé Cetaphil Baby Shampoo With Natural Chamomile (200ml)', N'Aqua ', 34, 150000.0, N'Mỹ', 'LSP2026005', 'KM2025001', 'T2026005', 1, 'cetaphil_baby_shampoo_hair_200ml_dau_goi_au_tre_em.png', N'Chai'),
('SP2026032', N'DẦU GỘI DƯỢC LIỆU NGUYÊN XUÂN XANH LÁ DƯỠNG TÓC, PHỤC HỒI TÓC HƯ TỔN HỘP 260G', N'Dầu gội dược liệu Nguyên Xuân xanh lá dưỡng tóc, phục hồi tóc hư tổn (260g)', N'Purified water , Sodium Laureth Sulfate , Cocamidopropyl betaine , Dimethiconol , Dimethicone , Propylene glycol , Sodium Chloride , Fragrance , Carbomer , Olive Oil PEG-7 Esters , Gleditsia Triacanthos extract , Eleusine Indica , Morus Alba extract , Oroxylum Indicum , Ageratum Conyzoides Extract , Polygonum Multiflorum Root Extract , Ginkgo biloba extract ', 29 , 94000.0, N'Việt Nam', 'LSP2026005', 'KM2025001', 'T2026003', 1, 'dau_goi_duoc_lieu_nguyen_xuan_xanh_la_duong_toc_phuc_hoi_toc_hu_ton_hop_260g.png', N'Chai'),
('SP2026033', N'DẦU GỘI KHÔ BATISTE DRY SHAMPOO FRUITY VÀ CHEEKY CHERRY LÀM SẠCH TÓC, GIẢM BẾT DẦU HƯƠNG CHERRY 200ML', N'Dầu gội khô Batiste Dry Shampoo Fruity và Cheeky Cherry làm sạch tóc, giảm bết dầu hương cherry (200ml)', N'Isobutane , Propane , Oryza Sativa (Rice) Bran , Limonene , Parfum (Fragrance) , Cetrimonium Chloride , Distearyldimonium , Linalool , Alcohol Denat ', 31 , 156000.0, N'Hoa Kỳ', 'LSP2026005', 'KM2025001', 'T2026005', 1, 'dau_goi_kho_batiste_dry_shampoo_fruity_va_cheeky_cherry_lam_sach_toc_giam_bet_dau_huong_cherry_200m.png', N'Chai'),
('SP2026034', N'DẦU GỘI KÍCH THÍCH MỌC TÓC RADICAL MED ANTI HAIR LOSS SHAMPOO 300ML', N'Dầu gội kích thích mọc tóc Radical Med Anti Hair Loss Shampoo 300ml', N'Cỏ đuôi ngựa , Cocamidopropyl betaine , Provitamin B5 ', 20 , 485000.0, N'Ba Lan', 'LSP2026005', 'KM2025001', 'T2026001', 1, 'dau_goi_kich_thich_moc_toc_radical_med_anti_hair_loss_shampoo_300ml.png', N'Chai'),
('SP2026035', N'DẦU GỘI SẠCH GÀU REDWIN TEA TREE SHAMPOO HAIR REVITALISER 250ML', N'Dầu gội sạch gàu Redwin Tea Tree Shampoo Hair Revitaliser 250ml', N'Water , Sodium Laureth Sulphate , Sodium Chloride , Melaleuca Alternifolia (Tea Tree) Leaf , Citric Acid , Methylchloroisothiazolinone , Methylisothiazolinone , TEA-lauryl sulphate , Cocomide DEA , Terasodium EDTA , CI19140 , CI42090 ', 22 , 299000.0, N'Úc', 'LSP2026005', 'KM2025001', 'T2026002', 1, 'dau_goi_sach_gau_redwin_tea_tree_shampoo_hair_revitaliser_250ml.png', N'Chai'),
('SP2026036', N'DẦU XẢ THẢO DƯỢC LA BEAUTY 250ML – NUÔI DƯỠNG DA ĐẦU VÀ TÓC SUÔN MƯỢT', N'Dầu xả Thảo Dược La Beauty nuôi dưỡng da đầu và tóc suôn mượt (250ml)', N'Tầm Gửi , IEA-Dodecylbenzenesulfonate , Dầu dừa , Trideceth-10 , Dimethiconol , Aqua/ Water , Glycerin , Behentrimonium Chloride , Stearyl alcohol , Cetostearyl alcohol , Hoàng kỳ , Cetrimonium Chloride , Tía Tô , Frangrance , Cây Giần Sàng , Nấm phục linh , Hà thủ ô , Địa hoàng , Thương truật , Kinh giới , Giấp cá , 1-2-Hexanediol , Ngũ vị tử bắc , Amodimethicone , Phenoxyethanol , Ethylhexylglycerin , Guar hydroxypropyltrimcnnium chloride , Disodium EDTA , Citric Acid ', 29 , 95000.0, N'Việt Nam', 'LSP2026005', 'KM2025001', 'T2026004', 1, 'dau_xa_thao_duoc_la_beauty_250ml_nuoi_duong_da_au_va_toc_suon_muot.png', N'Chai'),
('SP2026037', N'GỘI PHỦ BẠC FLASH LAVOX MÀU NÂU CÀ PHÊ G04 150ML', N'Gội phủ bạc Flash Lavox màu nâu cà phê G04 (150ml)', N'Deionized Water , Sodium laurilsulfate , Cocamido Propyl Belaine , Acrylates/Steareth-20 Methacrylate , Sodium Cocoyl Isethionate , Ethanolamine , propylen glycol , Polyquaternium-39 , p-Phenylenediamine , m-Aminophenol , Peg-40 Hydrogenated Castor Oil , Perfume , Sodium sulfide , Resorcinol , Sodium Erythorbate , p-Aminophenol , Sodium edetate , Macadamia Ternifolia Seed Oil , Olive Oil PEG-7 Esters , Argania Spinosa Kernel Oil , Panthenol , Camellia japonica seed oil , Polyquatermium-7 , 4-amino-2-hydroxybenzoic acid ', 25 , 158000.0, N'N/A', 'LSP2026005', 'KM2025001', 'T2026003', 1, 'goi_phu_bac_flash_lavox_mau_nau_ca_phe_g04_150ml.png', N'Chai'),
('SP2026038', N'NƯỚC NHUỘM PHỦ BẠC - NÂU ĐEN SEEDBEE WATER COLORING DARK BROWN NBIYAN 30G', N'Nước nhuộm phủ bạc - nâu đen Seedbee Water Coloring Dark Brown Nbiyan (30g)', N'Zea mays starch , Sorbitol , Maltodextrin , Cellulose gum , Sodium Polyacrylate , Xylitol , Sodium sulfate , Coix Lacryma-Jobi Ma-Yuen Seed Extract , Salvia officinalis leaf extract , Camellia Sinensis Leaf Extract , Acacia Concinna Fruit Powder , 2,4-Diaminophenoxyethanol HCL , p-Phenylenediamine , m-Aminophenol , Aloe Barbadensis Extract , p-Aminophenol , 4-Nitro-o-Phenylenediamine , Panthenol , Ascorbic Acid , Biotin , Disodium EDTA , Oryza Sativa (Rice) Bran , Glycine max seed , Phaseolus Radiatus Seed Powder , Sesamum Indicum (Sesame) Seed Oil (Sesamum Indicum) , Hordeum Vulgare Powder , Polygonum Fagopyrum (Buckwheat) Flour , Vigna Angularis Seed Powder , Glycyrrhiza Glabra (Licorice) Root Powder , Sophora flavescens root , Avena Sativa (oat) kernel meal , Rosmarinus Officinalis (Rosemary) Extract , Lavandula angustifolia (lavender) oil , Chamomilla Recutita (Matricaria) Flower Extract , Jasminum Officinale (Jasmine) Flower Powder , Thymus Vulgaris Extract , Melissa Officinalis Leaf Extract , Anethum Graveolens (Dill) Leaf Powder , Ocimum basilicum , Eucalyptus Globulus Leaf Extract , Eclipta Prostrata Leaf Powder , Laminaria Angustata Extract , Phaseolus Angularis Seed Powder , Salvia Hispanica (Hạt chia) , Undaria pinnatifida , Yeast Ribes Nigrum (Black Currant) Fruit Powder , Pueraria Lobata , Pinus Densiflora Leaf Extract , Ginkgo biloba extract , Artemisia Princeps Leaf Extract , Cnidium Officinale Rhizome Extract ', 10 , 299000.0, N'Hàn Quốc', 'LSP2026005', 'KM2025001', 'T2026005', 1, 'nuoc_nhuom_phu_bac_nau_en_seedbee_water_coloring_dark_brown_nbiyan_30g.png', N'Hộp'),
('SP2026039', N'NƯỚC TẮM GỘI THẢO DƯỢC CHO BÉ KUTIESKIN LÀM SẠCH, DƯỠNG MỀM DA VÀ TÓC 200ML', N'Nước tắm gội thảo dược cho bé Kutieskin làm sạch, dưỡng mềm da và tóc (200ml)', N'Nano Curcumin , Sài đất , Kinh giới , Khổ qua , Chiết xuất trà xanh , Bồ Công Anh , Cỏ mần trầu , Rau má , Tràm Gió , Nha đam , Tinh dầu Sả chanh ', 7 , 130000.0, N'Việt Nam', 'LSP2026005', 'KM2025001', 'T2026002', 1, 'nuoc_tam_goi_thao_duoc_cho_be_kutieskin_lam_sach_duong_mem_da_va_toc_200ml.png', N'Chai'),
('SP2026040', N'VICHY DERCOS ENERGISING SHAMPOO HAIRLOSS 200ML M9032400 M9032402 M9032403 M9032420 M9032422 - DẦU GỘI TĂNG CƯỜNG DƯỠNG TÓC, GIẢM RỤNG TÓC', N'Dầu gội ngăn ngừa và giảm rụng tóc Vichy Dercos Energising (200ml) ', N'Aqua ', 11 , 415000.0, N'Pháp', 'LSP2026005', 'KM2025001', 'T2026002', 1, 'vichy_dercos_energising_shampoo_hairloss_200ml_m9032400_m9032402_m9032403_m9032420_m9032422_dau_go.png', N'Chai'),

-- ========== LSP2026006: Mỹ phẩm tổng hợp ==========
('SP2026041', N'KEM DƯỠNG ẨM VASELINE PURE OPC HƯƠNG DÂU LÀM MỀM, CHỐNG NỨT NẺ 10G', N'Kem dưỡng ẩm Vaseline Pure OPC hương dâu làm mềm, chống nứt nẻ (10g)', N'Vaseline 9.82, Fragrance 0.18', 32 , 24000.0, N'N/A', 'LSP2026006', 'KM2025001', 'T2026003', 1, 'kem_duong_am_vaseline_pure_opc_huong_dau_lam_mem_chong_nut_ne_10g.png', N'Hũ'),
('SP2026042', N'LIPICE LIPBALM STRAWBERRY ROHTO 4.3G - SON DƯỠNG HƯƠNG DÂU', N'Son dưỡng Lipice Lipbalm hương dâu hỗ trợ dưỡng ẩm, bảo vệ môi (4.3g)', N'N/A', 21 , 50000.0, N'Nhật Bản', 'LSP2026006', 'KM2025001', 'T2026002', 1, 'lipice_lipbalm_strawberry_rohto_4_3g_son_duong_huong_dau.png', N'Thỏi'),
('SP2026043', N'MẶT NẠ NGỦ MÔI DƯỠNG ẨM CARE:NEL BERRY HƯƠNG DÂU 5G', N'Mặt nạ ngủ môi dưỡng ẩm Care:nel Berry hương dâu (5g)', N'Polyisobutene , Mineral oil , Petrolatum , Disostearyl Malate , Apis mellifera (bees) wax , Euphorbia Cerifera (Candelilla) Wax , Ceresin , Sorbitan Sesquioleate , Caprylyl glycol , Fragrance , Titanium dioxide , Hydrogenated Styrene/Isoprene Copolymer , Polyglyceryl-2 Trisostearate , Synthetic Fluorphlogopite , Tocopheryl acetate , 1,2 Hexanediol , CI 19140 , CI 15850 , Tin Oxide , Olea Europaea (Olive) Fruit Oil , Butylene glycol , Macadamia Integrifolia Seed Oil , Tocopherol , Lavandula Angustifolia (Lavender) Flower Extract , Water , Rosa Canina Flower Extract , Chamomilla Recutita (Matricaria) Flower Extract , Paeonia Officinalis Flower Extract , Rosa Centifolia Flower Extract , Camellia Japonica Rower Extract , Lillum Tigrinum Extract , Rubus idaeus (Raspberry) Fruit Extract , Fragaria Chiloensis (Strawberry) Fruit Extract , Punica Granatum Fruit Extract , Tricholoma Matsutaks Extract , Nelumbo Nucifera Flower Extract , Schizandra Chinensis Fruit Extract , Panax ginseng root extract , Moringa Pterygosperma Seed Extract ', 14, 70000.0, N'Hàn Quốc', 'LSP2026006', 'KM2025001', 'T2026005', 1, 'mat_na_ngu_moi_duong_am_care_nel_berry_huong_dau_5g.png', N'Hũ'),
('SP2026044', N'SON DƯỠNG MÔI SEBAMED LIP DEFENSE DƯỠNG ẨM, NGĂN NGỪA KHÔ, NỨT MÔI 4.8G', N'Son dưỡng môi Sebamed Lip Defense dưỡng ẩm, ngăn ngừa khô, nứt môi (4.8g)', N'Parfum , Bisabolol , Butyl Methoxydibenzoylmethane , Glyceryl Ricinoleate , Simmondsia Chinensis Seed Oil , Oryza Sativa Bran Wax , C12-15 Alkyl Benzoate , Tocopheryl acetate , Ethylhexyl Salicylate , Caprylic/capric triglyceride , Ricinus communis seed oil , Isoamyl p-Methoxycinnamate , Cera alba , Caprylic/Capric/Succinic Triglyceride ', 22 , 149000.0, N'Đức', 'LSP2026006', 'KM2025001', 'T2026001', 1, 'son_duong_moi_sebamed_lip_defense_duong_am_ngan_ngua_kho_nut_moi_4_8g.png', N'Thỏi'),
('SP2026045', N'SON DƯỠNG MÔI VASELINE LIP HỒNG XINH DƯỠNG ẨM, MÀU HỒNG TỰ NHIÊN 7G', N'Son dưỡng môi Vaseline Lip hồng xinh dưỡng ẩm, màu hồng tự nhiên (7g)', N'Vaseline ', 11 , 87000.0, N'Hàn Quốc', 'LSP2026006', 'KM2025001', 'T2026003', 1, 'son_duong_moi_vaseline_lip_hong_xinh_duong_am_mau_hong_tu_nhien_7g.png', N'Hũ'),
('SP2026046', N'SON DƯỠNG MÔI VASELINE STICK CHỐNG KHÔ NỨT 4.8G', N'Son dưỡng môi Vaseline Stick chống khô nứt 4.8g', N'N/A', 15 , 64000.0, N'Anh', 'LSP2026006', 'KM2025001', 'T2026005', 1, 'son_duong_moi_vaseline_stick_chong_kho_nut_4_8g.png', N'Thỏi'),
('SP2026047', N'SON DƯỠNG ẨM BẠC HÀ OMI BROTHERHOOD MENTURM DÀNH CHO MÔI KHÔ, NỨT NẺ 4G', N'Son dưỡng ẩm bạc hà Omi Brotherhood Menturm dành cho môi khô, nứt nẻ (4g)', N'Mineral oil , Paraffin , Petrolatum , Lanolin , Beeswax , Isopropyl myristate , Ceresin , Menthol , Camphor , Eucalyptus Globulus Leaf Oil , Methyl salicylate , Turpentine ', 33 , 39000.0, N'Nhật Bản', 'LSP2026006', 'KM2025001', 'T2026005', 1, 'son_duong_am_bac_ha_omi_brotherhood_menturm_danh_cho_moi_kho_nut_ne_4g.png', N'Thỏi'),
('SP2026048', N'SON DƯỠNG ẨM NIVEA PEACH SHINE 4.8G DƯỠNG MÔI MỀM MẠI 4.8G', N'Son dưỡng ẩm Nivea Peach Shine 4.8g dưỡng môi mềm mại (4.8g)', N'Octyldodecanol , Ricinus communis seed oil , Cera alba , Bis-Diglyceryl Polyacyladipate-2 , Cocoglycerides , Butyrospermum Parkii Butter , Hydrogenated castor oil , Tocopherols , Aroma , Ascorbyl palmitate , Helianthus Annuus Seed Oil , Simmondsia Chinensis Seed Oil , Persea Gratissima Fruit Extract , CI 77891 , CI 15985 , CI 15850 ', 25 , 77000.0, N'Đức', 'LSP2026006', 'KM2025001', 'T2026005', 1, 'son_duong_am_nivea_peach_shine_4_8g_duong_moi_mem_mai_4_8g.png', N'Thỏi'),
('SP2026049', N'SON DƯỠNG ẨM VASELINE ROSY LIPS GIÚP LÀM MỀM MÔI, MÀU HỒNG TỰ NHIÊN 4.8G', N'Son dưỡng ẩm Vaseline Rosy Lips giúp làm mềm môi, màu hồng tự nhiên (4.8g)', N'Octyldodecanol , Caprylic/capric triglyceride , Ricinus communis seed oil , Petrolatum , Myristyl Myristate , Water , Butyrospermum Parkii (Shea) Butter , Cetearyl Alcohol , Beeswax , Butylene glycol , Glycerin , Olive oil , Tinh dầu hoa hồng , Fragrance , Vaccinium macrocarpon fruit extract , Microcrystalline wax , Polyglyceryl-3 distearate , Copernicia cerifera wax , C20-40 Alkyl stearate , Iron oxides , Red 7 lake , Rubus idaeus fruit extract , Cetyl palmitate , Prunus Amygdalus Dulcis Oil , Tocopheryl acetate , Iron oxide saccharated ', 10 , 70000.0, N'Anh', 'LSP2026006', 'KM2025001', 'T2026001', 1, 'son_duong_am_vaseline_rosy_lips_giup_lam_mem_moi_mau_hong_tu_nhien_4_8g.png', N'Thỏi'),
('SP2026050', N'THANH CHE KHUYẾT ĐIỂM DƯỠNG TRẮNG, DƯỠNG ẨM SPF 50+ VÀ PA++++TRANSINO UV CONCEALER', N'Thanh che khuyết điểm dưỡng trắng, dưỡng ẩm SPF 50+ và PA++++Transino Concealer', N'Hydrogenated Polydencene , Titanium dioxide , Triethy hexanoin , Cetyl ethylhexanoate , Cera Microcristallina (Microcrystalline Wax) , Synthetic Wax , Phytosteryl/Behenyl/Octyldodecyl Lauroyl Glutamate , Silica , Paraffin , CI 77492 , Tranexamic acid , Aluminum hydroxide , Kaolin , Styrene/DVB Cross polymer , Sorbitan Sesquiisostearate , Polyglyceryl-5 Polyricinoleate , Stearic acid , CI 77491 , CI 77499 , Butylene glycol , Dimethicone , Tocopherol , ethylhexyl methoxycinnamate , Squalane , Paeonia Suffruticosa Root Extract , Prunus Persica Leaf Extract , Engelhardtia Chrysolepis Leaf Extract , Scutellaria Baicalensis Root Extract , Trehalose , Polyphosphorylcholine Glycol Acrylate ', 27, 720000.0, N'Nhật Bản', 'LSP2026006', 'KM2025001', 'T2026005', 1, 'thanh_che_khuyet_iem_duong_trang_duong_am_spf_50_va_pa_transino_uv_concealer.png', N'Thỏi'),

-- ========== LSP2026007: Chăm sóc vùng mắt ==========
('SP2026051', N'AUWHITE SERUM AUVI NATURE 30ML', N'Serum Auwhite Auvi Nature giúp làm mờ đốm nâu, dưỡng ẩm và làm sáng da (30ml)', N'Water , Glycerin , Butylene glycol , 1,2-Hexanediol , alpha-Arbutin , 3-O-Ethyl Ascorbic Acid , Dimethicone , Glycereth-26 , Ammonium Acryloyldimethyltaurate/VP Copolymer , Phenoxyethanol , Allantoin , Sodium hyaluronate , Hydrogenated Lecithin , Carnosine , Hydroxyacetophenone , Disodium EDTA , Xanthan gum , Fragrance ', 30, 200000.0, N'Úc', 'LSP2026007', 'KM2025001', 'T2026001', 1, 'auwhite_serum_auvi_nature_30ml.png', N'Lọ'),
('SP2026052', N'EUCERIN PRO ACNE SUPER SERUM 30ML 89751 - TINH CHẤT TRỊ MỤN', N'Serum Eucerin Pro Acne Solution Super giảm nhờn mụn (30ml)', N'Aqua , Cyclomethicone , Glycerin , Tapioca Starch , Methylpropanediol , Lactic Acid , Dimethiconol , Xanthan gum , Decylene Glycol , Parfum , Sodium Stearoyl Glutamate , Sodium Chloride ', 27, 50000.0, N'Đức', 'LSP2026007', 'KM2025001', 'T2026004', 1, 'eucerin_pro_acne_super_serum_30ml_89751_tinh_chat_tri_mun.png', N'Chai'),
('SP2026053', N'MẶT NẠ XÔNG HƠI MẮT MEGRHYTHM HƯƠNG HOA CÚC', N'Mặt nạ xông hơi mắt MegRhythm KAO hương hoa cúc dễ chịu, thư giãn đôi mắt (5 miếng)', N'N/A', 15, 140000.0, N'Nhật Bản', 'LSP2026007', 'KM2025001', 'T2026004', 1, 'mat_na_xong_hoi_mat_megrhythm_huong_hoa_cuc.png', N'Hộp'),
('SP2026054', N'MẶT NẠ XÔNG HƠI MẮT MEGRHYTHM HƯƠNG HOA OẢI HƯƠNG', N'Mặt nạ xông hơi mắt MegRhythm KAO hương hoa oải hương dễ chịu, thư giãn đôi mắt (5 miếng)', N'N/A', 14, 140000.0, N'Nhật Bản', 'LSP2026007', 'KM2025001', 'T2026002', 1, 'mat_na_xong_hoi_mat_megrhythm_huong_hoa_oai_huong.png', N'Hộp'),
('SP2026055', N'MẶT NẠ XÔNG HƠI MẮT MEGRHYTHM KHÔNG HƯƠNG', N'Mặt nạ xông hơi mắt MegRhythm KAO không hương dễ chịu, thư giãn đôi mắt (5 miếng)', N'N/A', 16, 140000.0, N'Nhật Bản', 'LSP2026007', 'KM2025001', 'T2026005', 1, 'mat_na_xong_hoi_mat_megrhythm_khong_huong.png', N'Hộp'),
('SP2026056', N'TINH CHẤT DƯỠNG ẨM PAX MOLY MOISTURE SERUM (BLUE) GIÚP DA MỀM MỊN, CẢI THIỆN ĐỘ ĐÀN HỒI 30ML', N'Tinh chất dưỡng ẩm Pax Moly Moisture Serum (Blue) giúp da mềm mịn, cải thiện độ đàn hồi (30ml)', N'Water , Niacinamide , Hyaluronic acid , Glycerin , Panthenol , Butylene glycol , 1,2-Hexanediol , Methylpropanediol , Carbomer , Tromethamine , Xanthan gum , Allantoin ', 27 , 300000.0, N'Hàn Quốc', 'LSP2026007', 'KM2025001', 'T2026004', 1, 'tinh_chat_duong_am_pax_moly_moisture_serum_blue_giup_da_mem_min_cai_thien_o_an_hoi_30ml.png', N'Lọ'),
('SP2026057', N'TINH CHẤT DƯỠNG ẨM, SÁNG DA PAX MOLY BLEMISH CARE (ORANGE) 30ML', N'Tinh chất dưỡng ẩm, sáng da Pax Moly Blemish Care (orange) 30ml', N'Water , Niacinamide , Hippophae Rhamnoides Kernel Extract , Glycerin , Panthenol , Butylene glycol , 1,2-Hexanediol , Methylpropanediol , Tromethamine , Adenosin , Honey , Citrus aurantium dulcis peel oil , Vitis Vinifera Fruit ', 13 , 300000.0, N'Hàn Quốc', 'LSP2026007', 'KM2025001', 'T2026002', 1, 'tinh_chat_duong_am_sang_da_pax_moly_blemish_care_orange_30ml.png', N'Lọ'),
('SP2026058', N'TINH CHẤT LÀM DỊU, PHỤC HỒI DA TỔN THƯƠNG CICA DAILY SERUM URIAGE 30ML', N'Tinh chất làm dịu, phục hồi da tổn thương Uriage Cica Daily Serum (30ml)', N'Aqua , Glycerin , Diglycerin , 1,2-Hexanediol , Inulin , Panthenol , Xanthan gum , Sodium hyaluronate , Propanediol , Centella Asiatica Leaf Extract , Citric Acid , Zinc gluconate , Glutamylamidoethyl Indole ', 25 , 869000.0, N'Pháp', 'LSP2026007', 'KM2025001', 'T2026003', 1, 'tinh_chat_lam_diu_phuc_hoi_da_ton_thuong_cica_daily_serum_uriage_30ml.png', N'Lọ'),
('SP2026059', N'TINH CHẤT NGỪA MỤN GARNIER SKIN NATURALS BRIGHT COMPLETE ANTI ACNE BOOSTER SERUM 30ML', N'Tinh chất ngừa mụn Garnier Skin Naturals Bright Complete Anti Acne Booster Serum 30ml', N'Fragrance , PEG-60 Hydrogenated castor oil , Phytic Acid , Lactic Acid , Citrus Limon Fruit Extract , Hydroxyethylpiperazine Ethane Sulfonic Acid , Hydroxyacetophenone , Hydroxypropyl guar , Ascorbyl Glucoside , Salicylic acid , Sodium hydroxide , Propanediol , Glycerin , Niacinamide , Alcohol Denat , Aqua/ Water ', 7, 349000.0, N'Pháp', 'LSP2026007', 'KM2025001', 'T2026003', 1, 'tinh_chat_ngua_mun_garnier_skin_naturals_bright_complete_anti_acne_booster_serum_30ml.png', N'Lọ'),
('SP2026060', N'TINH CHẤT TĂNG ĐỘ ĐÀN HỒI, GIÚP GIA MỀM MỊN PAX MOLY ELASTIC (ROSE) 30ML', N'Tinh chất tăng độ đàn hồi, giúp gia mềm mịn Pax Moly Elastic (rose) 30ml', N'Water , Niacinamide , Hydrolyzed Collagen , 1,2-Hexanediol , Methylpropanediol , Carbomer , Chamomile recutita , Freesia Refacta , Hydrolyzed Extensin , Actinidia Chinensis Fruit Extract , Vitis Vinifera Fruit ', 11 , 300000.0, N'Hàn Quốc', 'LSP2026007', 'KM2025001', 'T2026004', 1, 'tinh_chat_tang_o_an_hoi_giup_gia_mem_min_pax_moly_elastic_rose_30ml.png', N'Lọ'),

-- ========== LSP2026008: Sản phẩm tự nhiên ==========
('SP2026061', N'DẦU MASSAGE CHO BÉ JOHNSONS BABY OIL 200ML', N'Dầu dưỡng ẩm mát xa Johnson''s Baby Oil (200ml)', N'Mineral oil , Fragrance ', 11, 109000.0, N'Hoa Kỳ', 'LSP2026008', 'KM2025001', 'T2026005', 1, 'dau_massage_cho_be_johnsons_baby_oil_200ml.png', N'Chai'),
('SP2026062', N'DẦU MASSAGE CHO BÉ JOHNSONS BABY OIL 50ML', N'Dầu dưỡng ẩm mát xa Johnson''s Baby Oil (50ml)', N'Mineral oil , Fragrance ', 28 , 40000.0, N'Hoa Kỳ', 'LSP2026008', 'KM2025001', 'T2026001', 1, 'dau_massage_cho_be_johnsons_baby_oil_50ml.png', N'Chai'),
('SP2026063', N'TINH DẦU TRÀM MỆ ĐOAN GIÚP GIỮ ẤM, GIẢM TRIỆU CHỨNG HO, SỔ MŨI 50ML', N'Tinh dầu tràm Mệ Đoan giúp giữ ấm, giảm triệu chứng ho, sổ mũi (50ml)', N'Eucalyptol , α- terpineol ', 21 , 135000.0, N'Việt Nam', 'LSP2026008', 'KM2025001', 'T2026002', 1, 'tinh_dau_tram_me_oan_giup_giu_am_giam_trieu_chung_ho_so_mui_50ml.png', N'Chai'),
('SP2026064', N'TINH DẦU TRÀM NGÂM CỦ NÉN MỆ ĐOAN GIÚP XOA BÓP, GIẢM ĐAU 50ML', N'Tinh dầu tràm ngâm củ nén Mệ Đoan giúp xoa bóp, giảm đau (50ml)', N'Eucalyptol , α- terpineol ', 11 , 185000.0, N'Việt Nam', 'LSP2026008', 'KM2025001', 'T2026001', 1, 'tinh_dau_tram_ngam_cu_nen_me_oan_giup_xoa_bop_giam_au_50ml.png', N'Chai'),
('SP2026065', N'TINH DẦU VỎ BƯỞI XỊT DƯỠNG TÓC THẢO NGUYÊN 100ML', N'Tinh dầu vỏ bưởi dưỡng tóc Thảo Nguyên kích thích mọc tóc, ngừa rụng tóc (100ml)', N'Limonene ', 18 , 149000.0, N'N/A', 'LSP2026008', 'KM2025001', 'T2026003', 1, 'tinh_dau_vo_buoi_xit_duong_toc_thao_nguyen_100ml.png', N'Chai'),
('SP2026066', N'TINH DẦU XÔNG GIẢI CẢM - THẢI ĐỘC THẢO NGUYÊN 500ML', N'Tinh dầu xông Thảo Nguyên giải cảm, thải độc ngừa bệnh lây qua đường hô hấp (500ml)', N'Tinh dầu khuynh diệp , Tinh dầu hương nhu , Tinh dầu tràm , Tinh dầu hương Thảo , Ngải cứu , Kinh giới , Xuyên tâm liên , Cúc tần , Ngũ trảo , Tinh Dầu Vỏ Chanh , Cỏ Xạ Hương ', 28, 189000.0, N'Việt Nam', 'LSP2026008', 'KM2025001', 'T2026005', 1, 'tinh_dau_xong_giai_cam_thai_oc_thao_nguyen_500ml.png', N'Chai'),
('SP2026067', N'TINH DẦU ĐUỔI MUỖI VÀ CÔN TRÙNG, KHỬ KHUẨN THẢO NGUYÊN HƯƠNG SẢ CHANH 30ML', N'Tinh dầu Thảo Nguyên hương sả chanh 30ml đuổi muỗi và côn trùng, khử khuẩn', N'Propylene glycol , Pinene , Camphene , 3-Carene , Limonene , Eucalyptol , Terpinen-4-ol , Menthol , Menthone , Linalool , Citral , Menthyl Acetate , Neomenthol ', 12, 69000.0, N'Việt Nam', 'LSP2026008', 'KM2025001', 'T2026003', 1, 'tinh_dau_uoi_muoi_va_con_trung_khu_khuan_thao_nguyen_huong_sa_chanh_30ml.png', N'Chai'),

-- ========== LSP2026009: Thuốc hô hấp - Tai mũi họng ==========
('SP2026068', N'HO CẢM ÍCH NHI NAM DƯỢC 30 GÓI X 5ML', N'Siro hỗ trợ giảm ho tiêu đờm Ích Nhi Nam Dược (30 gói x 5ml)', N'Tá dược vừa đủ , Mạch môn 1 , Mật ong 0.42 , Húng chanh 1 , Cát cánh 0.5 , Quất 0.42, Đường phèn 0.42, Gừng 0.17', 18, 100000.0, N'Việt Nam', 'LSP2026009', 'KM2025001', 'T2026005', 1, 'ho_cam_ich_nhi_nam_duoc_30_goi_x_5ml.png', N'Hộp'),
('SP2026069', N'HOASTEX-S OPC 30 GÓI X 5ML', N'Siro HoAstex-S OPC điều trị ho, giảm ho trong viêm họng, viêm phế quản (30 gói x 5ml)', N'Húng chanh 2.5g, Núc nác 0.625g, Tinh dầu bạch đàn 6.64mg', 12, 3400.0, N'Việt Nam', 'LSP2026009', 'KM2025001', 'T2026001', 1, 'hoastex_s_opc_30_goi_x_5ml.png', N'Hộp'),
('SP2026070', N'OLESOM GRACURE 100ML', N'Siro Olesom Gracure điều trị bệnh hô hấp cấp và mãn tính, viêm phế quản (100ml) ', N'Ambroxol 30mg', 18, 53900.0, N'Ấn Độ', 'LSP2026009', 'KM2025001', 'T2026002', 1, 'olesom_gracure_100ml.png', N'Chai'),
('SP2026071', N'PROSPAN ENGELHARD 100ML', N'Siro ho Prospan Engelhard điều trị viêm phế quản mạn tính (100ml)', N'Chiết xuất cao khô lá thường xuân 17.5mg', 8, 93000.0, N'Đức', 'LSP2026009', 'KM2025001', 'T2026003', 1, 'prospan_engelhard_100ml.png', N'Chai'),
('SP2026072', N'SIRO ÍCH NHI 3+ NAM DƯỢC 90ML', N'Siro hỗ trợ giảm ho tiêu đờm Ích Nhi 3+ Nam Dược (90ml)', N'Húng chanh 4.5 , Quất 18 , Mật ong 3.6 , Cát cánh 9 , Mạch môn 18 , Gừng 0.45 , Tinh dầu bạc hà 4.5mg', 22, 65000.0, N'N/A', 'LSP2026009', 'KM2025001', 'T2026003', 1, 'siro_ich_nhi_3_nam_duoc_90ml.png', N'Chai'),
('SP2026073', N'THUỐC HO BỔ PHẾ HÓA ĐỜM BẢO THANH HOA LINH 125ML', N'Siro ho Bảo Thanh điều trị các chứng ho, viêm phổi (125ml)', N'Xuyên bối mẫu 5g, Tỳ bà diệp 12.5g, Sa sâm 2.5g, Phục Linh 2.5g, Trần bì 2.5g, Cát cánh 10g, Bán hạ 2.5g, Ngũ vị tử 1.25g, Qua lâu (Quả) 5g, Viễn chí 2.5g, Khổ hạnh nhân 5g, Can khương 2.5g, Mơ muối 12.5g, Mạch môn 2.5g, Thiên môn đông 1.25g, Cam thảo 2.5g, Tinh dầu bạc hà 17.5g, Mật ong 25g', 26, 67000.0, N'Việt Nam', 'LSP2026009', 'KM2025001', 'T2026001', 1, 'thuoc_ho_bo_phe_hoa_om_bao_thanh_hoa_linh_125ml.png', N'Chai'),
('SP2026074', N'TOÀN LỘC BỔ PHẾ KINGPHAR 60V', N'Viên uống giúp thanh nhiệt, nhuận phế, giảm ho Toàn Lộc Bổ Phế Kingphar (60 viên)', N'Đạm trúc diệp 100mg, Thiên môn đông 100mg, Thiên hoa phấn 80mg, Lá tía tô 80mg, Hoàng cầm 60mg, Cát cánh 60mg, Tri mẫu 50mg, Khoản đông 60mg, Ngũ vị tử 50mg, Tang bạch bì 50mg, Mạch môn 60mg, Cam thảo 50mg', 29, 225000.0, N'Việt Nam', 'LSP2026009', 'KM2025001', 'T2026001', 1, 'toan_loc_bo_phe_kingphar_60v.png', N'Hộp'),
('SP2026075', N'VIÊN NGẬM STREPSILS SOOTHING HONEY AND LEMON 2X12', N'Viên ngậm Strepsils Soothing mật ong & chanh giảm đau họng (2 vỉ x 12 viên)', N'Dichlorobenzyl alcohol 1.2mg, Amylmetacresol 0.6mg', 26, 37500.0, N'Anh', 'LSP2026009', 'KM2025001', 'T2026005', 1, 'vien_ngam_strepsils_soothing_honey_and_lemon_2x12.png', N'Hộp'),
('SP2026076', N'VIÊN TRỊ HO TUSSIDAY OPC 10X10', N'Viên trị ho Tussiday OPC điều trị các chứng ho, đau họng, sổ mũi (10 vỉ x 10 viên)', N'Eucalyptol 100mg, Tinh dầu Tần Dày Lá 0.6mg, Tinh dầu gừng 1mg', 11, 880.0, N'Việt Nam', 'LSP2026009', 'KM2025001', 'T2026005', 1, 'vien_tri_ho_tussiday_opc_10x10.png', N'Hộp'),

-- ========== LSP2026010: Thuốc tiêu hóa - Gan mật ==========
('SP2026077', N'GLUMIDTAB 600 PHARBACO HỘP 1 LỌ BỘT VÀ 1 ỐNG DUNG MÔI', N'Bột pha tiêm và dung môi Glumidtab 600 Pharbaco hỗ trợ giảm độc tính trên thần kinh của xạ trị ', N'Glutathione 600mg', 23, 150000.0, N'Việt Nam', 'LSP2026010', 'KM2025001', 'T2026004', 1, 'glumidtab_600_pharbaco_hop_1_lo_bot_va_1_ong_dung_moi.png', N'Hộp'),
('SP2026078', N'JADENU 180MG NOVARTIS 3X10', N'Thuốc Jadenu 180mg Novartis điều trị quá tải sắt mạn tính do truyền máu thường xuyên (3 vỉ x 10 viên)', N'Deferasirox 180mg', 14, 50000.0, N'Thụy Sĩ', 'LSP2026010', 'KM2025001', 'T2026004', 1, 'jadenu_180mg_novartis_3x10.png', N'Hộp'),
('SP2026079', N'METHIONIN 250MG DOMESCO 100V', N'Thuốc Methionin 250mg Domesco điều trị quá liều paracetamol (100 viên)', N'Methionin 250mg', 24, 150000.0, N'Việt Nam', 'LSP2026010', 'KM2025001', 'T2026004', 1, 'methionin_250mg_domesco_100v.png', N'Lọ'),
('SP2026080', N'MIFROS 300 MG DAVIPHARM 3X10', N'Thuốc Mifros 300mg Davipharm điều trị bệnh Wilson, bệnh cystin niệu, bệnh viêm khớp (3 vỉ x 10 viên)', N'Penicillamin 300mg', 6, 150000.0, N'Việt Nam', 'LSP2026010', 'KM2025001', 'T2026001', 1, 'mifros_300_mg_davipharm_3x10.png', N'Hộp'),
('SP2026081', N'SEVELAMER CARBONATE TABLETS 800MG DR. REDDY 270V', N'Thuốc Sevelamer Carbonate Tablets 800mg Dr. Reddy''s kiểm soát tình trạng tăng phosphat huyết (270 viên)', N'Sevelamer carbonate 800mg', 32, 150000.0, N'Ấn Độ', 'LSP2026010', 'KM2025001', 'T2026001', 1, 'sevelamer_carbonate_tablets_800mg_dr_reddy_270v.png', N'Lọ'),
('SP2026082', N'VINLUTA 600MG VINPHACO 1 LỌ BỘT + 1 ỐNG NCPT 10ML', N'Thuốc tiêm Vinluta 600 Vinphaco giảm độc tính của xạ trị và hóa chất điều trị ung thư (1 lọ bột + 1 ống nước cất tiêm 10ml)', N'Glutathion 600mg', 19, 150000.0, N'Việt Nam', 'LSP2026010', 'KM2025001', 'T2026002', 1, 'vinluta_600mg_vinphaco_1_lo_bot_1_ong_ncpt_10ml.png', N'Hộp'),

-- ========== LSP2026011: Thuốc tim mạch ==========
('SP2026083', N'COENZYME Q10 DOPPELHERZ 2X15', N'Viên uống hỗ trợ sức khỏe tim mạch Coenzyme Q10 Doppelherz (2 vỉ x 15 viên)', N'Coenzyme Q10 30mg, Chiết xuất sơn tra 50mg, Vitamin B6 0.7mg, Vitamin B1 0.55mg, Vitamin B12 1.25mcg', 24, 375000.0, N'Đức', 'LSP2026011', 'KM2025001', 'T2026003', 1, 'coenzyme_q10_doppelherz_2x15.jpg', N'Hộp'),
('SP2026084', N'COENZYME Q10 THÀNH CÔNG 30V', N'Viên uống hỗ trợ tốt cho tim mạch, giảm cholesterol máu Co Enzyme Q10 & Evening primrose (30 viên)', N'Tá dược vừa đủ , Vitamin E 5IU, Dầu cá 100mg, Tinh dầu hoa anh thảo 100mg, Coenzyme Q10 40mg', 24, 141000.0, N'Việt Nam', 'LSP2026011', 'KM2025001', 'T2026001', 1, 'coenzyme_q10_thanh_cong_30v.jpg', N'Hộp'),
('SP2026085', N'COQ10 PHARMEKAL 30V', N'Viên uống hỗ trợ sức khỏe tim mạch, giảm cholesterol máu CoQ10 30mg Pharmekal (30 viên)', N'Sáp ong vàng , Dầu lecithin đậu nành , Coenzyme Q10 30mg, Tá dược vừa đủ ', 24, 169000.0, N'N/A', 'LSP2026011', 'KM2025001', 'T2026001', 1, 'coq10_pharmekal_30v.jpg', N'Lọ'),
('SP2026086', N'GIẢO CỔ LAM TUỆ LINH 100V', N'Viên giúp giảm nguy cơ đường huyết cao Giảo Cổ Lam Tuệ Linh (100 viên)', N'Giảo cổ lam 500mg', 24,210000.0, N'Việt Nam', 'LSP2026011', 'KM2025001', 'T2026004', 1, 'giao_co_lam_tue_linh_100v.jpg', N'Hộp'),
('SP2026087', N'NINH TÂM VƯƠNG ĐÔNG TÂY 3X10', N'Viên uống giảm hồi hộp tim đập nhanh Ninh Tâm Vương Hồng Bàng (3 vỉ x 10 viên)', N'L-Carnitine 50mg, Taurine 50mg, Đan sâm 100mg, Khổ sâm bắc 155mg, Nattokinase 150mg, Hoàng đằng 50mg, Magie 7.5mg, Tá dược vừa đủ  ', 24, 180000.0, N'Việt Nam', 'LSP2026011', 'KM2025001', 'T2026002', 1, 'ninh_tam_vuong_ong_tay_3x10.jpg', N'Hộp'),
('SP2026088', N'SWISSE ULTIBOOST ODOURLESS WILD FISH OIL 1000MG 400V', N'Viên uống tốt cho tim mạch, mắt, não Odourless Wild Fish Oil Swisse (400 viên)', N'Dầu cá tự nhiên 1g', 24, 1799000.0, N'Úc', 'LSP2026011', 'KM2025001', 'T2026001', 1, 'swisse_ultiboost_odourless_wild_fish_oil_1000mg_400v.jpg', N'Lọ'),
('SP2026089', N'VIÊN UỐNG CẢI THIỆN TIM MẠCH HATO GOLD JPANWELL 60V', N'Viên uống hỗ trợ tim mạch, bổ sung các chất chống oxi hóa cho cơ thể Hato Gold Jpanwell (60 viên)', N'Coenzyme Q10 105mg, Nho 1mg, Bạch quả 1mg, Chiết xuất Maca 1mg, Vừng đen 5.24mg, Nattokinase 5.24mg, Vitamin B12 2mg, Vitamin B6 2mg, Vitamin B2 2mg, Vitamin B1 2mg, Eicosapentaenoic acid 1mg, nhân sâm Hàn Quốc 21mg, Magie 10mg, DHA 9mg', 24, 995000.0, N'Nhật Bản', 'LSP2026011', 'KM2025001', 'T2026004', 1, 'vien_uong_cai_thien_tim_mach_hato_gold_jpanwell_60v.jpg', N'Hộp'),
('SP2026090', N'VIÊN UỐNG HỖ TRỢ GIẢM CHOLESTEROL CHO NGƯỜI BỊ MỠ MÁU-CHOLESTEROL AID VITAMINS FOR LIFE 60V', N'Viên uống hỗ trợ giảm cholesterol Cholesterol Aid Vitamins For Life (60 viên)', N'Chromium 200Mcg, Cao Trầm 110mg, Men gạo đỏ 375mg', 24, 435000.0, N'Hoa Kỳ', 'LSP2026011', 'KM2025001', 'T2026002', 1, 'vien_uong_ho_tro_giam_cholesterol_cho_nguoi_bi_mo_mau_cholesterol_aid_vitamins_for_life_60v.jpg', N'Lọ'),
('SP2026091', N'VIÊN UỐNG HỖ TRỢ TIM MẠCH HEART ACE SUPPORT VITAMINS FOR LIFE 30V', N'Viên uống hỗ trợ sức khỏe tim mạch, ổn định nhịp tim Heart Ace Support Vitamins For Life (30 viên)', N'Coenzym Q10 40mg, Nattokinase 10.000Mcg, Red Yeast Rice Extract 500mg, Resveratrol 20mg, Lecithin 400mg', 24, 437000.0, N'Mỹ', 'LSP2026011', 'KM2025001', 'T2026005', 1, 'vien_uong_ho_tro_tim_mach_heart_ace_support_vitamins_for_life_30v.jpg', N'Lọ'),

-- ========== LSP2026012: Thuốc thần kinh ==========
('SP2026092', N'VIÊN UỐNG HỖ TRỢ GIẢM CĂNG THẲNG, CẢI THIỆN MẤT NGỦ, TĂNG CƯỜNG HOẠT ĐỘNG NÃO BỘ CEBRATON PREMIUM TRAPHACO 60V', N'Viên uống hỗ trợ giảm căng thẳng, cải thiện mất ngủ, tăng cường hoạt động não bộ Cebraton Premium Traphaco (Hộp 60 viên)', N'GABA 25mg, Chiết xuất Đinh Lăng 120mg, Cao khô lá Bạch quả 50mg, Citicolin Natri 45mg, Vitamin B1 2mg, Vitamin B2 2mg, Vitamin B6 2mg, Vitamin C 20mg, magnesi 40mg, Kẽm 4mg', 29, 550000.0, N'Việt Nam', 'LSP2026012', 'KM2025001', 'T2026002', 1, 'vien_uong_ho_tro_giam_cang_thang_cai_thien_mat_ngu_tang_cuong_hoat_ong_nao_bo_cebraton_premium_tr.png', N'Hộp'),
('SP2026093', N'VƯƠNG LÃO KIỆN HỒNG BÀNG 3X10', N'Viên uống giảm tê nhứt chân tay Vương Lão Kiện (3 vỉ x 10 viên)', N'Cao Câu đằng 30mg, Cao Thiên ma 25mg, Cao Hà thủ ô đỏ 15mg, Cao Câu kỷ tử 15mg, Cao đinh lăng 10mg, Cao Xà sàng tử 10mg, Cao Nhục thung dung 10mg, alpha Lipoic acid 10mg, L-Carnitine Fumarate 10mg, magnesi 4mg, Mẫu lệ 15mg', 35, 185000.0, N'Việt Nam', 'LSP2026012', 'KM2025001', 'T2026002', 1, 'vuong_lao_kien_hong_bang_3x10.png', N'Hộp'),

-- ========== LSP2026013: Thuốc cơ xương khớp ==========
('SP2026094', N'DẦU NÓNG MẶT TRỜI OPC 60ML', N'Dầu nóng Mặt Trời OPC điều trị nhức mỏi, tê thấp, đau lưng, cảm mạo, cúm (60ml)', N'Methyl salicylate 6.21, Tinh dầu quế 0.11, Tinh dầu bạc hà 2.48, Gừng 0.63, Camphor 2.1', 6, 68000.0, N'Việt Nam', 'LSP2026013', 'KM2025001', 'T2026005', 1, 'dau_nong_mat_troi_opc_60ml.png', N'Chai'),
('SP2026095', N'DẦU NÓNG QUẢNG ĐÀ 10ML', N'Dầu nóng Quảng Đà Danapha giảm đau lưng, đau gáy, đau dây thần kinh tọa (10ml)', N'Menthol 2.56 , Camphor 1.43 , Methyl Salicylat 4.08 , Tinh dầu tràm 0.10 , Tinh dầu quế 18.75 , Tá dược vừa đủ 10ml', 21, 29000.0, N'Việt Nam', 'LSP2026013', 'KM2025001', 'T2026005', 1, 'dau_nong_quang_a_10ml.png', N'Chai'),
('SP2026096', N'KEM BÔI GIẢM ĐAU VOLTOGEL MASS 30G - GIÚP GIẢM ĐAU CƠ, ĐAU VAI, BẦM TÍM, BONG GÂN', N'Kem bôi giảm đau Voltogel mass 30g giúp giảm đau cơ, đau vai, bầm tím, bong gân', N'D-panthenol 100mg, Methyl salicylate 500mg, Quế 0.16g, Tinh dầu tràm 1.5g, Tam thất 1g, Độc hoạt 1.6g, Bạc hà 2.4g, Ngải cứu 3.3g, Tế tân 3.3g, Bạch cập 3.3g, Địa liền 5g, Khương hoạt 5g, Long não 6g', 34, 75000.0, N'Việt Nam', 'LSP2026013', 'KM2025001', 'T2026002', 1, 'kem_boi_giam_au_voltogel_mass_30g_giup_giam_au_co_au_vai_bam_tim_bong_gan.png', N'Tuýp'),
('SP2026097', N'KIDDIECAL CATALENT 30V', N'Viên nhai KiddieCal Catalent bổ sung canxi và vitamin D (30 viên)', N'Calcium 200mg, Vitamin D3 200iu, Vitamin K1 30mcg', 8, 275000.0, N'Úc', 'LSP2026013', 'KM2025001', 'T2026003', 1, 'kiddiecal_catalent_30v.png', N'Lọ'),
('SP2026098', N'MIẾNG DÁN GIỮ NHIỆT SALONPAS JIKABARI HISAMITSU 8.4X13CM (8 MIẾNG) - LÀM ẤM, LÀM DỄ CHỊU VÙNG DA ĐAU VÀ TÊ CỨNG', N'Miếng dán giữ nhiệt Salonpas Jikabari Hisamitsu 8.4x13cm (8 miếng) giúp làm ấm, làm dễ chịu vùng da đau và tê cứng', N'Keo dán ', 11, 160000.0, N'Nhật Bản', 'LSP2026013', 'KM2025001', 'T2026005', 1, 'mieng_dan_giu_nhiet_salonpas_jikabari_hisamitsu_8_4x13cm_8_mieng_lam_am_lam_de_chiu_vung_da_au_va_te_cung.png', N'Hộp'),
('SP2026099', N'MIẾNG DÁN HẠ SỐT LION HIEPITA FOR CHILD (8 GÓI X 2 MIẾNG) - DÙNG CHO TRẺ TRÊN 2 TUỔI', N'Miếng dán hạ sốt Lion Hiepita for child (8 gói x 2 miếng) dùng cho trẻ trên 2 tuổi', N'Miếng dán ', 32, 130000.0, N'Nhật Bản', 'LSP2026013', 'KM2025001', 'T2026001', 1, 'mieng_dan_ha_sot_lion_hiepita_for_child_8_goi_x_2_mieng_dung_cho_tre_tren_2_tuoi.png', N'Hộp'),
('SP2026100', N'SALONPAS DICLOFENAC PATCH HISAMITSU 15 GÓI X 2 MIẾNG', N'Cao dán Salonpas Diclofenac Patch Hisamitsu giảm đau, kháng viêm đau cơ, đau vai (15 gói x 2 miếng)', N'Diclofenac Sodium 15mg', 35, 45000.0, N'Nhật Bản', 'LSP2026013', 'KM2025001', 'T2026004', 1, 'salonpas_diclofenac_patch_hisamitsu_15_goi_x_2_mieng.png', N'Hộp'),
('SP2026101', N'SALONPAS PAIN RELIEF PATCH HISAMITSU 5 MIẾNG', N'Cao dán giảm đau Salonpas Pain Relief Patch dùng trong các cơn đau vai, đau cổ (7cm x 10 cm - 5 miếng)', N'Methyl salicylate 10%, L-menthol 3%', 10, 45000.0, N'Việt Nam', 'LSP2026013', 'KM2025001', 'T2026002', 1, 'salonpas_pain_relief_patch_hisamitsu_5_mieng.png', N'Gói'),
('SP2026102', N'VIÊN KHỚP ABIPHA 3X10', N'Viên khớp Abipha điều trị thấp khớp, viêm khớp, thoái hóa khớp, đau mạn tính các khớp (3 vỉ x 10 viên)', N'Độc hoạt 510mg, Quế 340mg, Phòng phong 340mg, Đương quy 340mg, Tế tân 340mg, Xuyên khung 340mg, Tần giao 340mg, Bạch thược 340mg, Tang ký sinh 340mg, Địa hoàng 340mg, Đỗ trọng 340mg, Nhân Sâm 340mg, Ngưu tất 340mg, Phục Linh 340mg, Cam thảo 340mg', 15, 6000.0, N'Việt Nam', 'LSP2026013', 'KM2025001', 'T2026004', 1, 'vien_khop_abipha_3x10.png', N'Hộp'),

-- ========== LSP2026014: Thuốc da liễu ==========
('SP2026103', N'ATSIROX 1% AN THIEN 15G', N'Kem bôi da Atsirox 1% An Thiên điều trị các bệnh nhiễm nấm (15g)', N'Ciclopiroxolamine 100mg', 19, 65000.0, N'Việt Nam', 'LSP2026014', 'KM2025001', 'T2026001', 1, 'atsirox_1_an_thien_15g.png', N'Tuýp'),
('SP2026104', N'EMLA 5%  TUBE', N'Thuốc Emla 5% Aspen gây tê bề mặt da (5 tuýp x 5g)', N'Lidocain 125mg, Prilocain 125mg', 13, 150000.0, N'Anh', 'LSP2026014', 'KM2025001', 'T2026002', 1, 'emla_5_tube.png', N'Hộp'),
('SP2026105', N'FLUCONA-DENK 150MG 1V', N'Thuốc Flucona-Denk 150mg điều trị nhiễm nấm candida ở người lớn (1 viên)', N'Fluconazole 150mg', 22, 150000.0, N'Đức', 'LSP2026014', 'KM2025001', 'T2026003', 1, 'flucona_denk_150mg_1v.png', N'Hộp'),
('SP2026106', N'LIDOCAIN 10%', N'Thuốc phun mù Lidocain 10% Egis dùng trong chỉ định gây tê tại chỗ (38g)', N'Lidocaine 10%', 18, 150000.0, N'Hungary', 'LSP2026014', 'KM2025001', 'T2026003', 1, 'lidocain_10.png', N'Chai'),
('SP2026107', N'NYST 25000IU OPC 10 GÓI', N'Thuốc rơ miệng Nyst 25.000IU OPC dự phòng và điều trị bệnh Candida miệng (10 gói)', N'Nystatin 25000iu', 23, 1900.0, N'Việt Nam', 'LSP2026014', 'KM2025001', 'T2026005', 1, 'nyst_25000iu_opc_10_goi.png', N'Hộp'),
('SP2026108', N'SHAMPOO CICLOPIROX VCP 100ML - DẦU GỘI TRỊ NẤM', N'Dầu gội Shampoo Ciclopirox VCP hỗ trợ điều trị nấm (100ml)', N'Ciclopirox 1g', 23, 230000.0, N'Việt Nam', 'LSP2026014', 'KM2025001', 'T2026001', 1, 'shampoo_ciclopirox_vcp_100ml_dau_goi_tri_nam.png', N'Chai'),
('SP2026109', N'SHAMPOO CLOBETASOL VCP 100ML', N'Dầu gội đầu Shampoo Clobetasol 100ml VCP điều trị tại chỗ các bệnh vảy nến da', N'Clobetasol propionate 0.5mg', 32, 98000.0, N'N/A', 'LSP2026014', 'KM2025001', 'T2026004', 1, 'shampoo_clobetasol_vcp_100ml.png', N'Chai'),
('SP2026110', N'TIMBOV FARMAPRIM 1X3', N'Viên đặt âm đạo Timbov Farmaprim dùng cho nhiễm khuẩn âm đạo, viêm âm đạo kèm theo huyết trắng (1 vỉ x 3 viên)', N'Clotrimazole 500mg', 10, 68333.0, N'Moldova', 'LSP2026014', 'KM2025001', 'T2026004', 1, 'timbov_farmaprim_1x3.png', N'Hộp'),
('SP2026111', N'XYLOCAINE 2% ASTRAZENECA 30G', N'Gel Xylocaine Jelly 2% AstraZeneca gây tê bôi trơn bề mặt (30g)', N'Lidocain Hydroclorid Khan 2%', 29, 150000.0, N'Anh', 'LSP2026014', 'KM2025001', 'T2026005', 1, 'xylocaine_2_astrazeneca_30g.png', N'Tuýp'),

-- ========== LSP2026015: Khác ==========
('SP2026112', N'CETIRIZIN 10MG VIDIPHA 10X10', N'Thuốc Cetirizin 10mg Vidipha điều trị triệu chứng viêm mũi dị ứng, mày đay (10 vỉ x 10 viên)', N'Cetirizin 10mg', 11, 240.0, N'Việt Nam', 'LSP2026015', 'KM2025001', 'T2026004', 1, 'cetirizin_10mg_vidipha_10x10.png', N'Hộp'),
('SP2026113', N'CETIRIZINE STADA 10MG 10X10', N'Thuốc Cetirizine Stada 10mg điều trị viêm mũi dị ứng, mày đay (10 vỉ x 10 viên) ', N'Cetirizin 10mg', 14, 480.0, N'Việt Nam', 'LSP2026015', 'KM2025001', 'T2026003', 1, 'cetirizine_stada_10mg_10x10.png', N'Hộp'),
('SP2026114', N'FUGACAR 500MG LUSOMEDICAMENTA 1V', N'Thuốc Fugacar 500mg Janssen điều trị nhiễm giun (1 viên)', N'Mebendazole 500mg', 26, 22000.0, N'Hoa Kỳ', 'LSP2026015', 'KM2025001', 'T2026005', 1, 'fugacar_500mg_lusomedicamenta_1v.png', N'Hộp'),
('SP2026115', N'GLUCOPHAGE XR 1G MERCK 3X10', N'Thuốc Glucophage XR 1000mg Merck điều trị tiểu đường típ 2 (3 vỉ x 10 viên)', N'Metformin 1000mg', 24, 150000.0, N'Đức', 'LSP2026015', 'KM2025001', 'T2026001', 1, 'glucophage_xr_1g_merck_3x10.png', N'Hộp'),
('SP2026116', N'IMEXIME 200MG IMEXPHARM 2X10', N'Thuốc Imexime 200 Imexpharm điều trị nhiễm khuẩn đường tiết niệu, viêm thận - bể thận (2 vỉ x 10 viên)', N'Cefixim 200mg', 29, 200000.0, N'Việt Nam', 'LSP2026015', 'KM2025001', 'T2026003', 1, 'imexime_200mg_imexpharm_2x10.png', N'Hộp'),
('SP2026117', N'MIXTARD 100IU NOVO NORDISK LỌ 10ML', N'Hỗn dịch tiêm Mixtard 30 Novo Nordisk điều trị bệnh đái tháo đường (10ml)', N'Solube fraction 300iu, Isophane insulin crystals 700iu', 12, 150000.0, N'Đan Mạch', 'LSP2026015', 'KM2025001', 'T2026005', 1, 'mixtard_100iu_novo_nordisk_lo_10ml.png', N'Lọ'),
('SP2026118', N'NATRI CLORID 0.9% MẮT MŨI PHARMEDIC 10ML', N'Thuốc nhỏ mắt, nhỏ mũi Natri Clorid 0,9% Pharmedic hỗ trợ rửa mắt, rửa mũi, phụ trị nghẹt mũi, sổ mũi (10ml)', N'Natri clorid 90mg', 27, 3500.0, N'Việt Nam', 'LSP2026015', 'KM2025001', 'T2026002', 1, 'natri_clorid_0_9_mat_mui_pharmedic_10ml.png', N'Lọ'),
('SP2026119', N'VIÊN UỐNG HỖ TRỢ THỊ LỰC SKILLMAX OCAVILL 2X15', N'Viên uống hỗ trợ tăng cường thị lực, cải thiện khô mắt, mỏi mắt SkillMax Ocavill (2 vỉ x 15 viên)', N'Dầu nhuyễn thể 1000mg, Lutein 10mg, Zeaxanthin 2mg, Vitamin A 0.04mg, Eicosapentaenoic acid 120mg, Astaxanthin 0.2mg, Phospholipids 440mg', 18, 670000.0, N'Pháp', 'LSP2026015', 'KM2025001', 'T2026001', 1, 'vien_uong_ho_tro_thi_luc_skillmax_ocavill_2x15.png', N'Hộp'),
('SP2026120', N'WIT ECOGREEN 60V', N'Viên uống tăng cường thị lực giảm mờ và nhòe mắt WIT For Your Eyes (60 viên)', N'Pre-mixed powder contains vitamin and minerals 205mg, BroccoPhane 5000mcg, NovoOmega 65mg, Black Currant PE 4:1 100ml, Bilberry PE 5:1 100mg, Tagetes Erecta Extract 100mg', 9, 590000.0, N'Hoa Kỳ', 'LSP2026015', 'KM2025001', 'T2026005', 1, 'wit_ecogreen_60v.png', N'Lọ'),

-- ========== LSP2026016: Thực phẩm - Đồ uống ==========
('SP2026121', N'BỘT CẦN TÂY NGUYÊN CHẤT DATINO 3GX15 GÓI', N'Bột Cần Tây nguyên chất Datino (3g x 15 gói)', N'Cần tây  ', 34, 140000.0, N'Việt Nam', 'LSP2026016', 'KM2025001', 'T2026004', 1, 'bot_can_tay_nguyen_chat_datino_3gx15_goi.png', N'Hộp'),
('SP2026122', N'BỘT CẦN TÂY VỊ TÁO DATINO 3GX15 GÓI', N'Bột Cần Tây vị táo Datino (3g x 15 gói)', N'Cần tây , Tảo xanh ', 9, 155000.0, N'Việt Nam', 'LSP2026016', 'KM2025001', 'T2026004', 1, 'bot_can_tay_vi_tao_datino_3gx15_goi.png', N'Hộp'),
('SP2026123', N'BỘT RAU MÁ DATINO 3GX15 GÓI', N'Bột Rau Má Datino (15 gói x 3g)', N'Rau má  ', 20, 85000.0, N'N/A', 'LSP2026016', 'KM2025001', 'T2026002', 1, 'bot_rau_ma_datino_3gx15_goi.png', N'Hộp'),
('SP2026124', N'KẸO NGẬM KHÔNG ĐƯỜNG PULMOLL EUCALYPTUS MENTHOL 45G', N'Kẹo Ngậm Không Đường Pulmoll Eucalyptus Menthol (45g)', N'Chất tạo ngọt tổng hợp , Tinh dầu bạc hà , Hương liệu , chiết xuất trà trắng , Chất tạo ngọt tự nhiên ', 19, 55000.0, N'Đức', 'LSP2026016', 'KM2025001', 'T2026005', 1, 'keo_ngam_khong_uong_pulmoll_eucalyptus_menthol_45g.png', N'Hộp'),
('SP2026125', N'KẸO THE SIÊU MÁT LẠNH HƯƠNG BẠC HÀ PLAYMORE HŨ 22G', N'Kẹo The Siêu Mát Lạnh Hương Bạc Hà Playmore (hũ 22g)', N'Chất tạo ngọt tổng hợp , Tinh dầu bạc hà , chất chống đóng vón , Hương bạc hà nhân tạo , màu Brilliant blue FCF nhân tạo ', 25, 31000.0, N'N/A', 'LSP2026016', 'KM2025001', 'T2026003', 1, 'keo_the_sieu_mat_lanh_huong_bac_ha_playmore_hu_22g.png', N'Hũ'),
('SP2026126', N'TINH BỘT NGHỆ HONIMORE 130G', N'Tinh Bột Nghệ Honimore (130g)', N'Nghệ ', 14, 138000.0, N'Việt Nam', 'LSP2026016', 'KM2025001', 'T2026002', 1, 'tinh_bot_nghe_honimore_130g.png', N'Hũ'),
('SP2026127', N'VIÊN NGẬM HOBEZUT KHÔNG ĐƯỜNG 5 VỈ X 4 VIÊN', N'Viên ngậm Hobezut Vinacare không đường, giảm triệu chứng ho cảm (5 vỉ x 4 viên)', N'Cao lá thường xuân 9mg, Tinh dầu chanh 1mg, Tinh dầu gừng 0.6mg, Tinh dầu quế 0.5mg, Tinh dầu tràm gió 0.5mg, Tinh dầu húng chanh 0.2mg, Tinh dầu tắc 0.1mg', 25, 65000.0, N'Việt Nam', 'LSP2026016', 'KM2025001', 'T2026002', 1, 'vien_ngam_hobezut_khong_uong_5_vi_x_4_vien.png', N'Hộp'),
('SP2026128', N'VIÊN TINH NGHỆ MẬT ONG SỮA CHÚA HONEYLAND 120G', N'Viên Tinh Nghệ Mật Ong Sữa Chúa Honeyland (120g)', N'Curcumin 60%, Mật ong 20%, Sữa ong chúa 20%', 29, 196000.0, N'Việt Nam', 'LSP2026016', 'KM2025001', 'T2026005', 1, 'vien_tinh_nghe_mat_ong_sua_chua_honeyland_120g.png', N'Hũ'),

-- ========== LSP2026017: Vệ sinh cá nhân ==========
('SP2026129', N'BAO CAO SU OKAMOTO CROWN KÍCH CỠ NHỎ, SIÊU MỎNG VÀ MỀM MẠI HỘP 10 CÁI', N'Bao cao su Okamoto Crown kích cỡ nhỏ, siêu mỏng, mềm mại dùng để phòng tránh thai và ngăn ngừa HIV (10 cái)', N'Cao su thiên nhiên ', 22, 205000.0, N'Nhật Bản', 'LSP2026017', 'KM2025001', 'T2026005', 1, 'bao_cao_su_okamoto_crown_kich_co_nho_sieu_mong_va_mem_mai_hop_10_cai.png', N'Hộp'),
('SP2026130', N'BCS GEL MÁT LẠNH TĂNG CẢM XÚC S52 SAFEFIT FREEZER MAX HỘP 10 CÁI', N'Bao cao su Safefit Freezer Max S52 chứa nhiều gel làm mát, kéo dài thời gian (10 cái)', N'Chất bôi trơn  , Cao su thiên nhiên  ', 20, 49000.0, N'Việt Nam', 'LSP2026017', 'KM2025001', 'T2026002', 1, 'bcs_gel_mat_lanh_tang_cam_xuc_s52_safefit_freezer_max_hop_10_cai.png', N'Hộp'),
('SP2026131', N'BCS SAGAMI 0.01MM SIÊU MỎNG CHẠM ĐỈNH CHÂN THẬT, KHÔNG MÀU, KHÔNG MÙI, KHÔNG KÍCH ỨNG 1 CÁI', N'Bao cao su Sagami siêu mỏng, không gây dị ứng, phòng tránh thai và bệnh lây qua đường tình dục (0,01mm - 1 cái)', N'Polyurethane ', 29, 150000.0, N'Nhật Bản', 'LSP2026017', 'KM2025001', 'T2026004', 1, 'bcs_sagami_0_01mm_sieu_mong_cham_inh_chan_that_khong_mau_khong_mui_khong_kich_ung_1_cai.png', N'Cái'),
('SP2026132', N'BÔNG TẨY TRANG MIẾNG VUÔNG SILCOT HỘP 66 MIẾNG', N'Bông trang điểm cao cấp Silcot Soft Touch Premium Cotton (66 miếng)', N'Bông tự nhiên 100%', 33, 41000.0, N'N/A', 'LSP2026017', 'KM2025001', 'T2026004', 1, 'bong_tay_trang_mieng_vuong_silcot_hop_66_mieng.png', N'Hộp'),
('SP2026133', N'BĂNG VỆ SINH HÀNG NGÀY KHÔNG MÙI LAURIER ACTIVE FIT QUICK DRY AND FRESH 20 MIẾNG', N'Băng vệ sinh Laurier Active Fit Quick Dry And Fresh hàng ngày hút ẩm hiệu quả, không gây hầm hơi (20 miếng)', N'N/A', 10, 18000.0, N'Nhật Bản', 'LSP2026017', 'KM2025001', 'T2026001', 1, 'bang_ve_sinh_hang_ngay_khong_mui_laurier_active_fit_quick_dry_and_fresh_20_mieng.png', N'Gói'),
('SP2026134', N'GYNAPAX VIDIPHA 30 GÓI X 5G', N'Thuốc vệ sinh phụ nữ Gynapax Vidipha hỗ trợ vệ sinh và tẩy trùng niêm mạc phụ khoa (30 gói x 5g)', N'Berberin Clorid 2mg, Phèn chua 0.6, Acid Boric 4.35', 27, 29000.0, N'Việt Nam', 'LSP2026017', 'KM2025001', 'T2026004', 1, 'gynapax_vidipha_30_goi_x_5g.png', N'Hộp'),
('SP2026135', N'KHĂN GIẤY ƯỚT CỒN LET-GREEN 10 MIẾNG', N'Khăn giấy ướt cồn Let-Green làm sạch, kháng khuẩn và giữ vệ sinh (10 miếng)', N'vải không dệt trắng , Nước tinh khiết , Benzalkonium Chloride , Citric Acid , Cồn thực phẩm , Nước tinh khiết R.O , Polyaminopropyl Biguanide ', 28, 9000.0, N'Việt Nam', 'LSP2026017', 'KM2025001', 'T2026004', 1, 'khan_giay_uot_con_let_green_10_mieng.png', N'Gói'),
('SP2026136', N'KHĂN GIẤY ƯỚT HƯƠNG LÔ HỘI LIVING ALOE VERA CHOK CHOK 10 TỜ', N'Khăn giấy ướt Living Chok Chok Aloe Vera mềm mại, không xơ, an toàn (10 miếng)', N'Polyester , Nước tinh khiết , Lô hội , Caprylyl glycol , Sodium benzoate , hương thơm , Tơ nhân tạo , Bạch dương , Cocamidopropyl – PG – dimomium chloride phosphate , Hexandediol , Pentylene Glycol ', 24, 9500.0, N'Hàn Quốc', 'LSP2026017', 'KM2025001', 'T2026005', 1, 'khan_giay_uot_huong_lo_hoi_living_aloe_vera_chok_chok_10_to.png', N'Gói'),
('SP2026137', N'TĂM BÔNG TRẺ EM KAMICARE HỘP TRÒN 160 QUE - 1 ĐẦU XOẮN 1 ĐẦU THƯỜNG', N'Tăm bông KamiCare dành cho trẻ em 1 đầu xoắn 1 đầu thường vệ sinh tai, mũi (160 chiếc)', N'Chitosan , Polyvinyl alcohol , Bông Cotton 100%, Giấy ', 33, 32000.0, N'Việt Nam', 'LSP2026017', 'KM2025001', 'T2026004', 1, 'tam_bong_tre_em_kamicare_hop_tron_160_que_1_au_xoan_1_au_thuong.png', N'Hộp'),

-- ========== LSP2026018: Chăm sóc răng miệng ==========
('SP2026138', N'BETADINE SÚC HỌNG 125ML', N'Thuốc súc họng, súc miệng Betadine Gargle and Mouthwash điều trị viêm và nhiễm khuẩn miệng, họng (125ml)', N'Povidone Iodine 1% w/v', 33, 75000.0, N'Síp', 'LSP2026018', 'KM2025001', 'T2026005', 1, 'betadine_suc_hong_125ml.png', N'Chai'),
('SP2026139', N'KEM ĐÁNH RĂNG DƯỢC LIỆU LIPZO 110G', N'Kem đánh răng dược liệu Lipzo làm sạch, giảm viêm, giảm ê buốt nướu lợi (110g)', N'Sorbitol , Water , Hydrated silica , Sodium Chloride , PEG-32 , Xylitol , Sodium lauryl sulfate , Fragrance , Cellulose , Gum , Sodium saccharin , Psidium Guajava Fruit , Troxerutin , Chiết xuất Keo ong ', 19, 55000.0, N'Việt Nam', 'LSP2026018', 'KM2025001', 'T2026005', 1, 'kem_anh_rang_duoc_lieu_lipzo_110g.png', N'Tuýp'),
('SP2026140', N'MÁY TĂM NƯỚC 6 CHẾ ĐỘ HALIO PROFESSIONAL CORDLESS ORAL IRRIGATOR', N'Máy tăm nước 6 chế độ Halio Professional Cordless Oral Irrigator', N'Nhựa ', 16, 1800000.0, N'Mỹ', 'LSP2026018', 'KM2025001', 'T2026003', 1, 'may_tam_nuoc_6_che_o_halio_professional_cordless_oral_irrigator.png', N'Máy'),
('SP2026141', N'NATRI CLORID 0.9% F.T 1000ML', N'Dung dịch vô trùng Natri Clorid 0.9% Dược 3-2 rửa vết thương hở và kín, súc miệng (1000ml)', N'Natri clorid 0.9g', 32, 16000.0, N'Việt Nam', 'LSP2026018', 'KM2025001', 'T2026001', 1, 'natri_clorid_0_9_f_t_1000ml.png', N'Chai'),
('SP2026142', N'NƯỚC SÚC MIỆNG CHLOR-RINSE PLUS PEARLIE WHITE 250ML', N'Nước Súc Miệng Pearlie White Chlor-Rinse Plus (250ml)', N'Aqua , Sorbitol , Glycerin , Polysorbate 20 , Potassium acesulfanem , Chlohexidin digluconate , Potassium sorbate , Xylitol , Allantoin , Menthol , Tocopheryl acetate , Aloes (aloe barbadensis) , Thymol , Cetylpyridinium chloride , Flavor , Lonicera Caprifolium Extract , Chamomilla Recutita Flower extract , Salvia officinalis leaf extract , Commiphora myrrha resin ', 35, 175000.0, N'Singapore', 'LSP2026018', 'KM2025001', 'T2026002', 1, 'nuoc_suc_mieng_chlor_rinse_plus_pearlie_white_250ml.png', N'Chai'),
('SP2026143', N'NƯỚC SÚC MIỆNG ION MUỐI FUJIWA 680ML - HỖ TRỢ NGĂN CHẶN VI KHUẨN, GIẢM VIÊM HỌNG', N'Nước súc miệng Ion muối Fujiwa 680ml hỗ trợ ngăn chặn vi khuẩn, giảm viêm họng', N'Sodium clorid  , Ion Alkaline  ', 23, 27000.0, N'Nhật Bản', 'LSP2026018', 'KM2025001', 'T2026004', 1, 'nuoc_suc_mieng_ion_muoi_fujiwa_680ml_ho_tro_ngan_chan_vi_khuan_giam_viem_hong.png', N'Chai'),
('SP2026144', N'NƯỚC SÚC MIỆNG KIN GINGIVAL MOUTHWASH 250ML', N'Dung dịch súc miệng Kin Gingival 250ml hỗ trợ chăm sóc nướu, ngăn ngừa các mảng bám trên răng', N'Chlorhexidine digluconate 0.12%, Sodium fluoride , Aqua , Sorbitol , Glycerin , Peg-40 Hydrogenated Castor Oil , Citric Acid , Aroma , Menthol , Eugenol , Methyl salicylate , Sodium saccharin , Sodium Methylparaben , CI 14720 , D-limonene , Cinnamal ', 31, 142000.0, N'Tây Ban Nha', 'LSP2026018', 'KM2025001', 'T2026005', 1, 'nuoc_suc_mieng_kin_gingival_mouthwash_250ml.png', N'Chai'),
('SP2026145', N'NƯỚC SÚC MIỆNG PEARLIE WHITE FLUORINZE CHAI 750ML', N'Nước súc miệng Pearlie White Fluorinze Anti-Bacterial Fluoride (750ml)', N'Aqua , Polysorbate 20 , Glycerin , Sodium benzoate , Potassium acesulfanem , Flavor , Sodium fluoride , Xylitol , Cetylpyridinium chloride , Menthol , Tocopheryl acetate , Anthemis Nobilis Flower , Potassium Chloride , Melaleuca Alternifolia Leaf Oil , Thymol ', 17, 165000.0, N'Singapore', 'LSP2026018', 'KM2025001', 'T2026002', 1, 'nuoc_suc_mieng_pearlie_white_fluorinze_chai_750ml.png', N'Chai'),
('SP2026146', N'NƯỚC SÚC MIỆNG THÁI DƯƠNG BẠC HÀ 500ML', N'Nước súc miệng Thái Dương hương bạc hà, sát trùng răng, miệng, vòm họng (500ml)', N'Tinh dầu bạc hà 0.2, Menthol 0.2, Long não 2', 10, 30000.0, N'Việt Nam', 'LSP2026018', 'KM2025001', 'T2026002', 1, 'nuoc_suc_mieng_thai_duong_bac_ha_500ml.png', N'Chai'),

-- ========== LSP2026019: Đồ dùng gia đình ==========
('SP2026147', N'BÀN CHẢI ĐÁNH RĂNG TRẺ EM ORAL-B VITALITY D12 DISNEY CARS - SẠC ĐIỆN', N'Bàn chải đánh răng trẻ em Oral-B Vitality D12 Disney Cars (sạc điện)', N'Nhựa , PA , TPE , POM , PIN ', 29, 999000.0, N'Hoa Kỳ', 'LSP2026019', 'KM2025001', 'T2026003', 1, 'ban_chai_anh_rang_tre_em_oral_b_vitality_d12_disney_cars_sac_ien.png', N'Hộp'),
('SP2026148', N'BÀN CHẢI ĐÁNH RĂNG TRẺ EM ORAL-B VITALITY D12 DISNEY FROZEN - SẠC ĐIỆN', N'Bàn chải đánh răng trẻ em Oral-B Vitality D12 Disney Frozen (sạc điện)', N'Nhựa , PA , TPE , POM , PIN ', 13, 999000.0, N'Hoa Kỳ', 'LSP2026019', 'KM2025001', 'T2026003', 1, 'ban_chai_anh_rang_tre_em_oral_b_vitality_d12_disney_frozen_sac_ien.png', N'Hộp'),
('SP2026149', N'CAO ĐUỔI MUỖI CHO BÉ, GIẢM SƯNG TẤY DO CÔN TRÙNG ĐỐT OLA PAPI 20G', N'Cao Đuổi Muỗi Cho Bé, Giảm Sưng Tấy Do Côn Trùng Đốt Ola Papi (20g)', N'Tinh dầu tràm , Tinh dầu Sả chanh , Vaseline dưỡng da chiết xuất từ thiên nhiên ', 16, 95000.0, N'Việt Nam', 'LSP2026019', 'KM2025001', 'T2026002', 1, 'cao_uoi_muoi_cho_be_giam_sung_tay_do_con_trung_ot_ola_papi_20g.png', N'Hũ'),
('SP2026150', N'KEM CHỐNG MUỖI CHO GIA ĐÌNH HƯƠNG CAM REMOS ROHTO 70G', N'Kem chống muỗi Remos Rohto hương cam suốt 10 giờ (70g)', N'Diethyltoluamide 15%', 8, 29000.0, N'Nhật Bản', 'LSP2026019', 'KM2025001', 'T2026002', 1, 'kem_chong_muoi_cho_gia_inh_huong_cam_remos_rohto_70g.png', N'Tuýp'),
('SP2026151', N'TINH DẦU KHỬ MÙI CƠ THỂ THẢO NGUYÊN 50ML', N'Tinh dầu khử mùi cơ thể Thảo Nguyên giúp cân bằng độ ẩm cho da (50ml)', N'Alpha-pinene , Beta-pinene , Beta-myrcene , Limonene , beta-phellandrene , Cymol , Linalool , Alpha-Terpineol , Alpha-citral , Estragole , Beta-citral , Anethole , Benzaldehyde , 4-methoxy , Triacetin ', 33, 89000.0, N'Việt Nam', 'LSP2026019', 'KM2025001', 'T2026002', 1, 'tinh_dau_khu_mui_co_the_thao_nguyen_50ml.png', N'Chai'),
('SP2026152', N'TINH DẦU NẤM GÀU CHÀM THẢO NGUYÊN 30ML', N'Tinh dầu Thảo Nguyên điều trị nấm gàu chàm (30ml)', N'Alpha-pinene , Beta-pinene , beta-phellandrene , beta-phellandrene , Beta-myrcene , Limonene , beta-phellandrene , beta-phellandrene , Cymol , Gamma-terpinene , Alpha-Terpineol , Eugenol , alpha-curcumene , Beta-bisabolene , Beta-sesquiphellandrene ', 18, 149000.0, N'Việt Nam', 'LSP2026019', 'KM2025001', 'T2026001', 1, 'tinh_dau_nam_gau_cham_thao_nguyen_30ml.png', N'Chai'),
('SP2026153', N'XỊT CHỐNG MUỖI CHO GIA ĐÌNH HƯƠNG SẢ CHANH REMOS ROHTO 150ML', N'Xịt chống muỗi Remos hương sả chanh xua muỗi suốt 10 giờ (150ml)', N'Diethyltoluamide 15%', 6, 70000.0, N'Nhật Bản', 'LSP2026019', 'KM2025001', 'T2026005', 1, 'xit_chong_muoi_cho_gia_inh_huong_sa_chanh_remos_rohto_150ml.png', N'Chai'),
('SP2026154', N'XỊT CHỐNG MUỖI HƯƠNG KHUYNH DIỆP CHO BÉ TỪ 6 TUỔI REMOS ROHTO 70ML', N'Xịt xua muỗi Remos hương Khuynh Diệp xua muỗi suốt 6 giờ cho bé từ 6 tuổi (70ml)', N'Picaridin 15%', 15, 76000.0, N'Nhật Bản', 'LSP2026019', 'KM2025001', 'T2026002', 1, 'xit_chong_muoi_huong_khuynh_diep_cho_be_tu_6_thang_tuoi_remos_rohto_70ml.png', N'Chai'),

-- ========== LSP2026020: Hàng tổng hợp ==========
('SP2026155', N'BÔNG TẨY TRANG MIẾNG VUÔNG SILCOT HỘP 82 MIẾNG', N'Bông trang điểm Silcot Nhật Bản không xù bông, thấm hút dung dịch giúp tiết kiệm mỹ phẩm dưỡng da (82 miếng)', N'Bông tự nhiên ', 10, 38000.0, N'N/A', 'LSP2026020', 'KM2025001', 'T2026005', 1, 'bong_tay_trang_mieng_vuong_silcot_hop_82_mieng.png', N'Hộp'),
('SP2026156', N'BÔNG TẨY TRANG TRÒN KAMICARE 120 MIẾNG', N'Bông tẩy trang tròn KamiCare Cotton Pads thấm hút tốt giúp sạch bụi bẩn, tẩy tế bào chết trên da (120 miếng)', N'Bông tự nhiên 100%', 7, 33000.0, N'Việt Nam', 'LSP2026020', 'KM2025001', 'T2026002', 1, 'bong_tay_trang_tron_kamicare_120_mieng.png', N'Hộp'),
('SP2026157', N'GEL BÔI TRƠN KLY TIỆT TRÙNG 82G BỔ SUNG LƯỢNG CHẤT NHỜN TỰ NHIÊN', N'Gel bôi trơn KLY tiệt trùng 82g bổ sung lượng chất nhờn tự nhiên', N'Deionized Water , Cellulose hydroxyethylate , Glycerin , Propylene glycol , Ethylhexyl Glycerin , Carbomer , Sodium hydroxide ', 31, 82000.0, N'Thổ Nhĩ Kỳ', 'LSP2026020', 'KM2025001', 'T2026005', 1, 'gel_boi_tron_kly_tiet_trung_82g_bo_sung_luong_chat_nhon_tu_nhien.png', N'Tuýp'),
('SP2026158', N'GEL NGẬM HỌNG OTOSAN THROAT GEL FORTE 14 GÓI X 10ML - HỖ TRỢ ĐIỀU TRỊ VIÊM HỌNG CẤP TÍNH', N'Gel ngậm họng Otosan Throat Gel Forte 14 gói x 10ml hỗ trợ điều trị viêm họng cấp tính', N'Mật ong multiflora , Tinh dầu bạc hà , Tinh dầu khuynh diệp , Tinh dầu cỏ xạ hương trắng , Tinh dầu chanh hữu cơ , Chiết xuất hedge mustard , Đường tùng , Chiết xuất rêu Iceland ', 15, 290000.0, N'Ý', 'LSP2026020', 'KM2025001', 'T2026004', 1, 'gel_ngam_hong_otosan_throat_gel_forte_14_goi_x_10ml_ho_tro_ieu_tri_viem_hong_cap_tinh.png', N'Hộp'),
('SP2026159', N'KEM BÔI GIẢM ĐAU VOLTOGEL MASS 50G - GIÚP GIẢM ĐAU CƠ, ĐAU VAI, BẦM TÍM, BONG GÂN', N'Kem bôi giảm đau Voltogel mass 50g giúp giảm đau cơ, đau vai, bầm tím, bong gân', N'Menthol 5g, Camphor 2g, Ngải cứu 3.3g, Tam thất 0.5g, Khương hoạt 2.5g, Tinh dầu quế 0.16g, Địa liền 2.5g, Bạch cập 1.5g, Tế tân 1.5g, Độc hoạt 0.8g, Methyl Salicylat 500mg, Vitamin B5 100mg', 9, 110000.0, N'Việt Nam', 'LSP2026020', 'KM2025001', 'T2026005', 1, 'kem_boi_giam_au_voltogel_mass_50g_giup_giam_au_co_au_vai_bam_tim_bong_gan.png', N'Tuýp'),
('SP2026160', N'KEM BÔI HĂM TÃ, CHÀM SỮA, BỎNG, KÍCH ỨNG DA PROCREAM 30G', N'Kem bôi hăm tã, chàm sữa, bỏng, kích ứng da Procream 30g', N'Purified water , Perfume , Chlorhexidine Gluconate , BHT , Benzalkonium Chloride , Polysorbate 60 , Isohexadecane , Hydroethyl acrylate , Peg 400 , EDTA , Ceteareth 20 , Ceteraryl Alcohol , IPM , Vaselin , Almond oil , Lanolin , Zinc oxide , Panthenol , Silver Nano Solution , Vitamin E , Ceramide ', 25, 99000.0, N'Việt Nam', 'LSP2026020', 'KM2025001', 'T2026002', 1, 'kem_boi_ham_ta_cham_sua_bong_kich_ung_da_procream_30g.png', N'Tuýp'),
('SP2026161', N'KHĂN HẠ SỐT DR.PAPIE 0+ STARMED (5 GÓI X 5 MIẾNG) - HẠ NHIỆT GIẢM SỐT DÙNG CHO TRẺ SƠ SINH', N'Khăn hạ sốt Dr.Papie 0+ Starmed (5 gói x 5 miếng) hạ nhiệt giảm sốt dùng cho trẻ sơ sinh', N'Chlorophyll , Phenoxyethanol , Vitamin E , Chanh , Glycerin , Cỏ nhọ nồi , Nước tinh khiết , Lô hội , Gôm Xanthan , Tía Tô ', 23, 147000.0, N'Việt Nam', 'LSP2026020', 'KM2025001', 'T2026002', 1, 'khan_ha_sot_dr_papie_0_starmed_5_goi_x_5_mieng_ha_nhiet_giam_sot_dung_cho_tre_so_sinh.png', N'Hộp'),
('SP2026162', N'MIẾNG DÁN MỤN BAN NGÀY DERMA ANGEL (HỘP 12 MIẾNG) HÚT MỤN, LÀM LÀNH VẾT THƯƠNG SAU MỤN', N'Miếng dán mụn ban ngày Derma Angel (12 miếng) hút mụn, làm lành vết thương sau mụn ', N'Hydrocolloid ', 32, 50000.0, N'Đài Loan', 'LSP2026020', 'KM2025001', 'T2026002', 1, 'mieng_dan_mun_ban_ngay_derma_angel_hop_12_mieng_hut_mun_lam_lanh_vet_thuong_sau_mun.png', N'Hộp'),
('SP2026163', N'XỊT GIẢM ĐAU NHỨC RĂNG, VIÊM NƯỚU TOOTH CARE 20ML', N'Xịt giảm đau nhức răng, viêm nướu Tooth Care 20ml', N'Nước tinh khiết , Tinh dầu bạc hà , Natri benzoat , lá đào , hoa đu đủ đực , Lá trầu không , Kim ngân hoa ', 17, 89000.0, N'Việt Nam', 'LSP2026020', 'KM2025001', 'T2026004', 1, 'xit_giam_au_nhuc_rang_viem_nuou_tooth_care_20ml.png', N'Chai'),

-- ========== LSP2026022: Thiết bị làm đẹp ==========
('SP2026164', N'BÀN CHẢI ĐÁNH RĂNG ĐIỆN ORAL-B PRO 500', N'Bàn chải đánh răng điện Oral-B Pro 500', N'PIN , TPE , POM , Nhựa ASA , Polypropylen , polyamide ', 13, 1749000.0, N'Hoa Kỳ', 'LSP2026022', 'KM2025001', 'T2026004', 1, 'ban_chai_anh_rang_ien_oral_b_pro_500.png', N'Hộp'),
('SP2026165', N'BÀN CHẢI ĐÁNH RĂNG ĐIỆN ORAL-B VITALITY CROSSACTION BLUE', N'Bàn chải đánh răng điện Oral-B Vitality Crossaction Blue', N'PIN , POM , TPE , Polypropylen , polyamide ', 30, 999000.0, N'Hoa Kỳ', 'LSP2026022', 'KM2025001', 'T2026005', 1, 'ban_chai_anh_rang_ien_oral_b_vitality_crossaction_blue.png', N'Hộp'),
('SP2026166', N'BÀN CHẢI ĐÁNH RĂNG ĐIỆN ORAL-B VITALITY D12.513', N'Bàn chải đánh răng điện Oral-B Vitality D12.513', N'PIN , POM , TPE , Polypropylen , polyamide ', 26, 999000.0, N'Hoa Kỳ', 'LSP2026022', 'KM2025001', 'T2026001', 1, 'ban_chai_anh_rang_ien_oral_b_vitality_d12_513.png', N'Hộp'),
('SP2026167', N'DAO CẠO RÂU SIÊU MỎNG CÁN VÀNG GILLETTE SUPER THIN II GÓI 2 CÁI', N'Dao cạo râu cán vàng Gillette Super Thin cạo sát, giảm khả năng trầy xước (2 cái)', N'Thép không gỉ  , Nhựa  ', 12, 15000.0, N'Hoa Kỳ', 'LSP2026022', 'KM2025001', 'T2026001', 1, 'dao_cao_rau_sieu_mong_can_vang_gillette_super_thin_ii_goi_2_cai.png', N'Gói'),
('SP2026168', N'KEM TẨY LÔNG DÀNH CHO DA NHẠY CẢM VEET 50G', N'Kem tẩy lông Veet Silk & Fresh Aloe Vera & Vitamin E cho da nhạy cảm (50g)', N'Aqua , Urea , Paraffinum Liquidum , Cetearyl Alcohol , Potassium Thioglycolate , Calcium Hydroxide , Talc , Ceteareth 20 , Glycerin , Potassium carbonate , Sorbitol , Magnesium Trisilicate , Sodium benzoate , propylen glycol , Potassium sorbate , Hexyl cinnamal , Parfum , Propylene Glycol Dicaprylate/Dicaprate , Cl 77891 , Linalool , Butylphenyl Methylpropional , Acrylates Copolymer , Sodium Gluconate , Lithium Magnesium Sodium Silicate , Aloe barbadensis leaf juice , Tocopheryl acetate ', 6, 70000.0, N'Pakistan', 'LSP2026022', 'KM2025001', 'T2026002', 1, 'kem_tay_long_danh_cho_da_nhay_cam_veet_50g.png', N'Tuýp'),
('SP2026169', N'KEM TẨY LÔNG DÀNH CHO DA THƯỜNG PURE VEET 50G', N'Kem tẩy lông Veet Pure 50g cho da, hiệu quả chỉ từ 3 đến 6 phút', N'Aqua , Cetearyl Alcohol , Potassium Thioglycolate , Paraffinum Liquidum , Calcium Hydroxide , Ceteareth-20 , Talc , Glycerin , Parfum , Polyethylene , Sodium Gluconate , Butyrospermum Parkii Butter , Limonene , Hexyl cinnamal , Linalool ', 25, 70000.0, N'Pakistan', 'LSP2026022', 'KM2025001', 'T2026005', 1, 'kem_tay_long_danh_cho_da_thuong_pure_veet_50g.png', N'Tuýp'),

-- ========== LSP2026023: Dụng cụ y tế ==========
('SP2026170', N'BÔNG GÒN QUICK NURSE 1KG DÙNG CHO VẾT THƯƠNG NGOÀI DA, SÁT TRÙNG, VỆ SINH', N'Bông gòn Quick Nurse 1kg dùng cho vết thương ngoài da, sát trùng, vệ sinh ', N'Cotton  ', 9, 210000.0, N'Việt Nam', 'LSP2026023', 'KM2025001', 'T2026001', 1, 'bong_gon_quick_nurse_1kg_dung_cho_vet_thuong_ngoai_da_sat_trung_ve_sinh.png', N'Gói'),
('SP2026171', N'BĂNG CUỘN LỤA PLAID YOUNG PLASTER-SILK 1.25CMX5M GIÚP CỐ ĐỊNH BĂNG GẠC VÀ DỤNG CỤ Y TẾ - YOUNG CHEMICAL', N'Băng cuộn lụa Plaid Young Plaster-Silk 1.25cmx5m giúp cố định băng gạc và dụng cụ y tế Young Chemical', N'Vải lụa , Acrylic ', 10, 19000.0, N'Hàn Quốc', 'LSP2026023', 'KM2025001', 'T2026003', 1, 'bang_cuon_lua_plaid_young_plaster_silk_1_25cmx5m_giup_co_inh_bang_gac_va_dung_cu_y_te_young_chemi.png', N'Cuộn'),
('SP2026172', N'BĂNG CÁ NHÂN VẢI PLAID ACE BAND-F 60X19MM (100 MIẾNG) - YOUNG CHEMICAL', N'Băng cá nhân vải Plaid Ace Band-F 60x19mm (100 miếng) Young Chemical', N'Sợi vải co giãn Viscose 70% 70%, polyamide 30%, vải không dệt trắng , Acrylic ', 34, 56000.0, N'Hàn Quốc', 'LSP2026023', 'KM2025001', 'T2026001', 1, 'bang_ca_nhan_vai_plaid_ace_band_f_60x19mm_100_mieng_young_chemical.png', N'Hộp'),
('SP2026173', N'KHẨU TRANG HAMITA ADVANCED 4D MASK MÀU ĐEN (5 CÁI)', N'Khẩu trang 4 lớp Hamita Advanced 4D mask màu đen (5 cái)', N'Dây đeo có tính đàn hồi , Vải không dệt ', 36, 35000.0, N'Việt Nam', 'LSP2026023', 'KM2025001', 'T2026002', 1, 'khau_trang_hamita_advanced_4d_mask_mau_en_5_cai.png', N'Gói'),
('SP2026174', N'KHẨU TRANG TRẺ EM HAMITA 3 LỚP (50 CÁI) - NGĂN KHÓI BỤI, VI KHUẨN VÀ GIỌT BẮN', N'Khẩu trang trẻ em 3 lớp Hamita (50 cái) ngăn khói bụi, vi khuẩn và giọt bắn', N'vải không dệt trắng , giấy kháng khuẩn MELTBLOWN , Thun đeo cấu tạo từ Polyester và Spandex , Nẹp định hình bằng nhựa ', 29, 59000.0, N'Việt Nam', 'LSP2026023', 'KM2025001', 'T2026002', 1, 'khau_trang_tre_em_hamita_3_lop_50_cai_ngan_khoi_bui_vi_khuan_va_giot_ban.png', N'Hộp'),
('SP2026175', N'KIM LẤY MÁU ACCU CHEK SOFTCLIX (25 CÁI) - DÙNG CHO MÁY ACCU CHEK INSTANT, ACTIVE', N'Kim lấy máu Accu Chek Softclix (25 cái) dùng cho máy Accu Chek Instant, Active', N'Nhựa  , Thép không gỉ  ', 30, 71000.0, N'Đức', 'LSP2026023', 'KM2025001', 'T2026002', 1, 'kim_lay_mau_accu_chek_softclix_25_cai_dung_cho_may_accu_chek_instant_active.png', N'Hộp'),
('SP2026176', N'NƯỚC MUỐI SINH LÝ FYSOLINE HYPERTONIQUE ƯU TRƯƠNG (20 ỐNG X 5ML) - GIẢM PHÙ NỀ VÀ GIỮ ẨM NIÊM MẠC MŨI, DÙNG ĐƯỢC CHO TRẺ SƠ SINH', N'Nước muối sinh lý Fysoline Hypertonique ưu trương (20 ống x 5ml) giúp giảm phù nề và giữ ẩm niêm mạc mũi', N'Natri clorid 2.3g, Natri hyaluronat 0.06%, Nước tinh khiết 100ml', 18, 193000.0, N'Pháp', 'LSP2026023', 'KM2025001', 'T2026005', 1, 'nuoc_muoi_sinh_ly_fysoline_hypertonique_uu_truong_20_ong_x_5ml_giam_phu_ne_va_giu_am_niem_mac_mu.png', N'Hộp'),
('SP2026177', N'NƯỚC MẮT NHÂN TẠO EASIMIST EYE DROPS GIẢM KHÔ MỎI MẮT 5ML', N'Nước mắt nhân tạo Easimist Eye Drops 5ml giảm khô mỏi mắt', N'Sodium Carboxymethylcellulose BP 5mg, Stabilized Oxychloro Complex 0.05mg', 18, 39000.0, N'N/A', 'LSP2026023', 'KM2025001', 'T2026001', 1, 'nuoc_mat_nhan_tao_easimist_eye_drops_giam_kho_moi_mat_5ml.png', N'Lọ'),
('SP2026178', N'QUE THỬ THAI SAFEFIT DẠNG BÚT', N'Que thử thai Safefit Test dạng bút giúp phát hiện sớm thai kỳ sau 7-10 ngày thụ thai', N'Nhựa ', 20, 35000.0, N'Việt Nam', 'LSP2026023', 'KM2025001', 'T2026003', 1, 'que_thu_thai_safefit_dang_but.png', N'Hộp'),

-- ========== LSP2026024: Dụng cụ theo dõi ==========
('SP2026179', N'COMBO 3 HỘP QUE THỬ ĐƯỜNG HUYẾT EASY MAX (25 QUE) - TẶNG MÁY ĐO ĐƯỜNG HUYẾT EASY MAX TAG', N'Combo 3 hộp que thử đường huyết Easy Max (25 cái) - tặng máy đo đường huyết Easy Max Tag', N'Que thử đường huyết , Máy đo đường huyết ', 33, 699000.0, N'Hoa Kỳ', 'LSP2026024', 'KM2025001', 'T2026005', 1, 'combo_3_hop_que_thu_uong_huyet_easy_max_25_que_tang_may_o_uong_huyet_easy_max_tag.png', N'Hộp'),
('SP2026180', N'MÁY ĐO HUYẾT ÁP BẮP TAY AND UA-1020 AFIB HỖ TRỢ ĐO HUYẾT ÁP, CẢNH BÁO ĐỘT QUỴ', N'Máy đo huyết áp bắp tay AND UA-1020 Afib hỗ trợ đo huyết áp, cảnh báo đột quỵ', N'Máy đo ', 27, 1450000.0, N'Nhật Bản', 'LSP2026024', 'KM2025001', 'T2026001', 1, 'may_o_huyet_ap_bap_tay_and_ua_1020_afib_ho_tro_o_huyet_ap_canh_bao_ot_quy.png', N'Máy'),
('SP2026181', N'MÁY ĐO HUYẾT ÁP BẮP TAY CAO CẤP KHÔNG DÂY YUWELL YE630CR HỖ TRỢ ĐO HUYẾT ÁP, NHỊP TIM', N'Máy đo huyết áp bắp tay cao cấp không dây Yuwell YE630CR hỗ trợ đo huyết áp, nhịp tim', N'Nhựa ', 36, 1890000.0, N'Trung Quốc', 'LSP2026024', 'KM2025001', 'T2026003', 1, 'may_o_huyet_ap_bap_tay_cao_cap_khong_day_yuwell_ye630cr_ho_tro_o_huyet_ap_nhip_tim.png', N'Máy'),
('SP2026182', N'MÁY ĐO HUYẾT ÁP BẮP TAY MICROLIFE B3 AFIB ADVANCED HỖ TRỢ ĐO HUYẾT ÁP, CẢNH BÁO ĐỘT QUỴ', N'Máy đo huyết áp bắp tay Microlife B3 Afib Advanced hỗ trợ đo huyết áp, cảnh báo đột quỵ', N'Nhựa tổng hợp ', 16, 2400000.0, N'Thụy Sĩ', 'LSP2026024', 'KM2025001', 'T2026004', 1, 'may_o_huyet_ap_bap_tay_microlife_b3_afib_advanced_ho_tro_o_huyet_ap_canh_bao_ot_quy.png', N'Máy'),
('SP2026183', N'MÁY ĐO HUYẾT ÁP BẮP TAY YUWELL YE610D HỖ TRỢ ĐO HUYẾT ÁP, NHỊP TIM', N'Máy đo huyết áp bắp tay Yuwell YE610D hỗ trợ đo huyết áp, nhịp tim', N'Nhựa ', 16, 699000.0, N'Trung Quốc', 'LSP2026024', 'KM2025001', 'T2026002', 1, 'may_o_huyet_ap_bap_tay_yuwell_ye610d_ho_tro_o_huyet_ap_nhip_tim.png', N'Máy'),
('SP2026184', N'NHIỆT KẾ HỒNG NGOẠI ĐO TRÁN MEDIUSA TP-336N NEW', N'Nhiệt kế hồng ngoại đo trán Mediusa TP-336N New', N'Nhựa ', 28, 650000.0, N'Hoa Kỳ', 'LSP2026024', 'KM2025001', 'T2026005', 1, 'nhiet_ke_hong_ngoai_o_tran_mediusa_tp_336n_new.png', N'Cái'),
('SP2026185', N'NHIỆT KẾ HỒNG NGOẠI ĐO TRÁN OMRON MC720', N'Nhiệt kế hồng ngoại đo trán Omron MC720', N'Nhựa ', 12, 945000.0, N'Nhật Bản', 'LSP2026024', 'KM2025001', 'T2026003', 1, 'nhiet_ke_hong_ngoai_o_tran_omron_mc720.png', N'Cái'),
('SP2026186', N'NHIỆT KẾ Y KHOA ĐO THÂN NHIỆT CƠ THỂ AURORA CRW-23 12 CÁI', N'Nhiệt kế thuỷ ngân đo thân nhiệt cơ thể Aurora CRW-23', N'Kim loại ', 29, 29000.0, N'Đức', 'LSP2026024', 'KM2025001', 'T2026002', 1, 'nhiet_ke_y_khoa_o_than_nhiet_co_the_aurora_crw_23_12_cai.png', N'Cái'),
('SP2026187', N'QUE THỬ ĐƯỜNG HUYẾT EASY MAX (25 CÁI)', N'Que thử đường huyết Easy Max (hộp 25 que)', N'Kim loại ', 8, 169000.0, N'Hoa Kỳ', 'LSP2026024', 'KM2025001', 'T2026003', 1, 'que_thu_uong_huyet_easy_max_25_cai.png', N'Hộp'),

-- ========== LSP2026025: Dụng cụ sơ cứu ==========
('SP2026188', N'DẦU GIÓ NHỊ THIÊN ĐƯỜNG 1.5ML (10 CHAI) - DÙNG KHI CẢM MẠO PHONG HÀN, SỔ MŨI, CHÓNG MẶT, NHỨC ĐẦU, SAY TÀU XE', N'Dầu gió Nhị Thiên Đường 1.5ml (10 chai) dùng khi cảm mạo phong hàn, sổ mũi, chóng mặt, nhức đầu, say tàu xe', N'Bạc hà 0.675g, Menthol 0.675g, Camphor 0.038g, Tinh dầu quế 0.053g, Đinh hương 0.03g', 17, 12000.0, N'Việt Nam', 'LSP2026025', 'KM2025001', 'T2026004', 1, 'dau_gio_nhi_thien_uong_1_5ml_10_chai_dung_khi_cam_mao_phong_han_so_mui_chong_mat_nhuc_au_s.png', N'Chai'),
('SP2026189', N'XỊT PHỦ VẾT THƯƠNG, VẾT BỎNG HYALO4 SILVER SPRAY 50ML - BẢO VỆ LÀM LÀNH VẾT THƯƠNG', N'Xịt phủ vết thương, vết bỏng Hyalo4 Silver Spray 50ml giúp bảo vệ làm lành vết thương', N'Vitamin E , Metallic silver , Hyaluronic acid sodium salt ', 7, 590000.0, N'Ý', 'LSP2026025', 'KM2025001', 'T2026005', 1, 'xit_phu_vet_thuong_vet_bong_hyalo4_silver_spray_50ml_bao_ve_lam_lanh_vet_thuong.png', N'Chai'),

-- ========== LSP2026026: Thiết bị khác ==========
('SP2026190', N'GEL BÔI MIỆNG ALOCLAIR PLUS GEL ALLIANCE 8ML -  GIẢM NHIỆT MIỆNG, TAY CHÂN MIỆNG CHO MỌI LỨA TUỔI', N'Gel bôi miệng Aloclair Plus Alliance 8ml giảm nhiệt miệng, tay chân miệng cho mọi lứa tuổi', N'Nước (aqua) , Sodium hyaluronate , Aloe Barbadencis Extract , Polyvinylpyrrolidone K30 , Maltodextrin , Propylene glycol , Sodium benzoate , Xanthan gum , Benzalkonium Chloride , Potassium sorbate , Sodium saccharin , Aroma , Peg-40 Hydrogenated Castor Oil , Dipotassium Glycyrrhizate , Disodium EDTA ', 14, 175000.0, N'Anh', 'LSP2026026', 'KM2025001', 'T2026001', 1, 'gel_boi_mieng_aloclair_plus_gel_alliance_8ml_giam_nhiet_mieng_tay_chan_mieng_cho_moi_lua_tuoi.png', N'Tuýp'),
('SP2026191', N'GEL REDISOLV AURORA ĐIỀU TRỊ HỘI CHỨNG RUỘT KÍCH THÍCH (IBS), GIẢM NGAY TRIỆU CHỨNG TIÊU CHẢY, ĐẦY HƠI, CHƯỚNG BỤNG, RỐI LOẠN PHÂN HỘP 14 GÓI', N'Gel Redisolv Aurora điều trị hội chứng ruột kích thích (IBS) (14 gói) giảm ngay triệu chứng tiêu chảy, đầy hơi, chướng bụng, rối loạn phân', N'Simethicone , Guar gum ', 31, 35000.0, N'Ý', 'LSP2026026', 'KM2025001', 'T2026004', 1, 'gel_redisolv_aurora_ieu_tri_hoi_chung_ruot_kich_thich_ibs_giam_ngay_trieu_chung_tieu_chay_ay_h.png', N'Hộp'),
('SP2026192', N'KEM BÔI MỤN CÓC TIMODORE 5ML - HỖ TRỢ LOẠI BỎ MỤN CÓC, VẾT CHAI SẦN', N'Kem bôi mụn cóc Timodore 5ml hỗ trợ loại bỏ mụn cóc, vết chai sần', N'Salicylic acid 33%', 36, 199000.0, N'Ý', 'LSP2026026', 'KM2025001', 'T2026004', 1, 'kem_boi_mun_coc_timodore_5ml_ho_tro_loai_bo_mun_coc_vet_chai_san.png', N'Tuýp'),
('SP2026193', N'KEM BÔI SẸO REBAC BIOPHARM 15G - HỖ TRỢ LÀM PHẲNG, MỀM VÀ MỜ SẸO', N'Kem bôi sẹo Rebac Biopharm 15g hỗ trợ làm phẳng, mềm và mờ sẹo', N'Dimethicone , Dimethicone Crosspolymer , Cydopentasiloxane , Tocopheryl acetate , Isodecyl Neopentanoate , Peg-10 dimethicone , Hydrogenated Polydencene , Sodium hyaluronate , sh-oligopeptide-1 , Allium Cepa Bulb Extract , Centella Asiatica Extract , Polyurethane-35 , Glyceryl Cappylate ', 10, 450000.0, N'Thái Lan', 'LSP2026026', 'KM2025001', 'T2026005', 1, 'kem_boi_seo_rebac_biopharm_15g_ho_tro_lam_phang_mem_va_mo_seo.png', N'Tuýp'),
('SP2026194', N'KEM BÔI SẸO REBAC BIOPHARM 5G - HỖ TRỢ LÀM PHẲNG, MỀM VÀ MỜ SẸO', N'Kem bôi sẹo Rebac Biopharm 5g hỗ trợ làm phẳng, mềm và mờ sẹo', N'Dimethicone , Dimethicone Crosspolymer , Cydopentasiloxane , Tocopheryl acetate , Isodecyl Neopentanoate , Peg-10 dimethicone , Hydrogenated Polydencene , Sodium hyaluronate , sh-oligopeptide-1 , Allium Cepa Bulb Extract , Centella Asiatica Extract , Polyurethane-35 , Glyceryl Cappylate ', 28, 239000.0, N'Thái Lan', 'LSP2026026', 'KM2025001', 'T2026002', 1, 'kem_boi_seo_rebac_biopharm_5g_ho_tro_lam_phang_mem_va_mo_seo.png', N'Tuýp'),
('SP2026195', N'MIẾNG DÁN MỤN BAN NGÀY DERMA ANGEL PLUS (HỘP 12 MIẾNG) HÚT DỊCH MỦ, LÀM LÀNH VẾT THƯƠNG NHANH CHÓNG', N'Miếng dán mụn ban ngày Derma Angel Plus (12 miếng) hút dịch mủ, làm lành vết thương nhanh chóng', N'Hydrocolloid ', 32, 58000.0, N'Đài Loan', 'LSP2026026', 'KM2025001', 'T2026004', 1, 'mieng_dan_mun_ban_ngay_derma_angel_plus_hop_12_mieng_hut_dich_mu_lam_lanh_vet_thuong_nhanh_chong.png', N'Hộp'),
('SP2026196', N'MIẾNG DÁN MỤN BAN NGÀY VÀ ĐÊM DERMA ANGEL 12D+6N HÚT MỤN, LÀM LÀNH VẾT THƯƠNG SAU MỤN', N'Miếng dán mụn ban ngày và đêm Derma Angel (12Đ + 6N miếng) hút mụn, làm lành vết thương sau mụn ', N'Hydrocolloid ', 25, 69000.0, N'Đài Loan', 'LSP2026026', 'KM2025001', 'T2026001', 1, 'mieng_dan_mun_ban_ngay_va_em_derma_angel_12d_6n_hut_mun_lam_lanh_vet_thuong_sau_mun.png', N'Hộp'),
('SP2026197', N'MIẾNG DÁN MỤN CÓC KANGDI FOBELIFE (6 MIẾNG)', N'Miếng dán mụn cóc Kangdi Fobelife (6 miếng)', N'Salicylic acid , Polyvinyl Alkyl , Ether Adhesive , Titanium dioxide , Liquid Paraffin , Antioxidant , Red iron oxide (E172) , Black Iron Oxide ', 36, 58000.0, N'Trung Quốc', 'LSP2026026', 'KM2025001', 'T2026005', 1, 'mieng_dan_mun_coc_kangdi_fobelife_6_mieng.png', N'Hộp'),
('SP2026198', N'VI BỘT CAPTOGASTRIL GIẢM NHANH ACID, ĐAU DẠ DÀY, Ợ NÓNG, ĐẦY HƠI, BUỒN NÔN, KHÓ TIÊU HỘP 20 GÓI', N'Vi bột Captogastril giảm nhanh acid, đau dạ dày, ợ nóng, đầy hơi, buồn nôn, khó tiêu hộp 20 gói', N'Simethicon 80mg, natri bicarbonate 135mg, Magie cacbonat 100mg, Magie Hydroxit 150mg', 7, 240000.0, N'Ý', 'LSP2026026', 'KM2025001', 'T2026001', 1, 'vi_bot_captogastril_giam_nhanh_acid_au_da_day_o_nong_ay_hoi_buon_non_kho_tieu_hop_20_goi.png', N'Hộp');

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
-- PhieuNhap
INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNhanVien, MaNhaCungCap)
VALUES
    ('PN20260001', '2026-01-10', 'NV2025001', 'NCC2026001'),
    ('PN20260002', '2026-01-25', 'NV2025001', 'NCC2026002'),
    ('PN20260003', '2026-02-08', 'NV2025001', 'NCC2026003'),
    ('PN20260004', '2026-02-22', 'NV2025001', 'NCC2026004'),
    ('PN20260005', '2026-03-05', 'NV2025001', 'NCC2026005'),
    ('PN20260006', '2026-03-18', 'NV2025001', 'NCC2026006'),
    ('PN20260007', '2026-04-02', 'NV2025001', 'NCC2026007'),
    ('PN20260008', '2026-04-15', 'NV2025001', 'NCC2026008'),
    ('PN20260009', '2026-04-28', 'NV2025001', 'NCC2026009'),
    ('PN20260010', '2026-05-01', 'NV2025001', 'NCC2026010');
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
-- ChiTietPhieuNhap
INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, GiaNhap)
VALUES
    ('PN20260001', 'SP2026001', 8, 30000),
    ('PN20260002', 'SP2026002', 12, 80000),
    ('PN20260003', 'SP2026003', 8, 400000),
    ('PN20260004', 'SP2026004', 8, 100000),
    ('PN20260005', 'SP2026005', 5, 300000),
    ('PN20260006', 'SP2026006', 12, 250000),
    ('PN20260007', 'SP2026007', 10, 200000),
    ('PN20260008', 'SP2026008', 12, 150000),
    ('PN20260009', 'SP2026009', 8, 150000),
    ('PN20260010', 'SP2026010', 5, 250000),
    ('PN20260001', 'SP2026011', 8, 300000),
    ('PN20260002', 'SP2026012', 12, 50000),
    ('PN20260003', 'SP2026013', 8, 100000),
    ('PN20260004', 'SP2026014', 30, 500000),
    ('PN20260005', 'SP2026015', 20, 100000),
    ('PN20260006', 'SP2026016', 10, 300000),
    ('PN20260007', 'SP2026017', 30, 200000),
    ('PN20260008', 'SP2026018', 20, 30000),
    ('PN20260009', 'SP2026019', 25, 120000),
    ('PN20260010', 'SP2026020', 20, 100000),
    ('PN20260001', 'SP2026021', 30, 80000),
    ('PN20260002', 'SP2026022', 15, 400000),
    ('PN20260003', 'SP2026023', 20, 100000),
    ('PN20260004', 'SP2026024', 8, 30000),
    ('PN20260005', 'SP2026025', 25, 400000),
    ('PN20260006', 'SP2026026', 30, 300000),
    ('PN20260007', 'SP2026027', 8, 500000),
    ('PN20260008', 'SP2026028', 8, 120000),
    ('PN20260009', 'SP2026029', 5, 120000),
    ('PN20260010', 'SP2026030', 8, 500000),
    ('PN20260001', 'SP2026031', 12, 80000),
    ('PN20260002', 'SP2026032', 5, 400000),
    ('PN20260003', 'SP2026033', 8, 150000),
    ('PN20260004', 'SP2026034', 12, 400000),
    ('PN20260005', 'SP2026035', 8, 300000),
    ('PN20260006', 'SP2026036', 10, 120000),
    ('PN20260007', 'SP2026037', 12, 300000),
    ('PN20260008', 'SP2026038', 25, 500000),
    ('PN20260009', 'SP2026039', 30, 50000),
    ('PN20260010', 'SP2026040', 20, 30000),
    ('PN20260001', 'SP2026041', 12, 30000),
    ('PN20260002', 'SP2026042', 8, 30000),
    ('PN20260003', 'SP2026043', 12, 120000),
    ('PN20260004', 'SP2026044', 10, 400000),
    ('PN20260005', 'SP2026045', 30, 200000),
    ('PN20260006', 'SP2026046', 25, 150000),
    ('PN20260007', 'SP2026047', 5, 500000),
    ('PN20260008', 'SP2026048', 25, 150000),
    ('PN20260009', 'SP2026049', 12, 300000),
    ('PN20260010', 'SP2026050', 10, 120000),
    ('PN20260001', 'SP2026051', 30, 300000),
    ('PN20260002', 'SP2026052', 5, 50000),
    ('PN20260003', 'SP2026053', 30, 250000),
    ('PN20260004', 'SP2026054', 10, 200000),
    ('PN20260005', 'SP2026055', 30, 120000),
    ('PN20260006', 'SP2026056', 10, 100000),
    ('PN20260007', 'SP2026057', 5, 150000),
    ('PN20260008', 'SP2026058', 30, 300000),
    ('PN20260009', 'SP2026059', 8, 80000),
    ('PN20260010', 'SP2026060', 12, 200000),
    ('PN20260001', 'SP2026061', 5, 400000),
    ('PN20260002', 'SP2026062', 20, 120000),
    ('PN20260003', 'SP2026063', 15, 200000),
    ('PN20260004', 'SP2026064', 20, 50000),
    ('PN20260005', 'SP2026065', 8, 50000),
    ('PN20260006', 'SP2026066', 15, 80000),
    ('PN20260007', 'SP2026067', 20, 120000),
    ('PN20260008', 'SP2026068', 15, 400000),
    ('PN20260009', 'SP2026069', 15, 500000),
    ('PN20260010', 'SP2026070', 8, 50000),
    ('PN20260001', 'SP2026071', 15, 120000),
    ('PN20260002', 'SP2026072', 12, 500000),
    ('PN20260003', 'SP2026073', 30, 120000),
    ('PN20260004', 'SP2026074', 15, 30000),
    ('PN20260005', 'SP2026075', 15, 80000),
    ('PN20260006', 'SP2026076', 25, 300000),
    ('PN20260007', 'SP2026077', 10, 300000),
    ('PN20260008', 'SP2026078', 10, 200000),
    ('PN20260009', 'SP2026079', 20, 30000),
    ('PN20260010', 'SP2026080', 8, 150000),
    ('PN20260001', 'SP2026081', 10, 100000),
    ('PN20260002', 'SP2026082', 5, 80000),
    ('PN20260003', 'SP2026083', 12, 120000),
    ('PN20260004', 'SP2026084', 5, 250000),
    ('PN20260005', 'SP2026085', 20, 120000),
    ('PN20260006', 'SP2026086', 12, 200000),
    ('PN20260007', 'SP2026087', 15, 150000),
    ('PN20260008', 'SP2026088', 20, 30000),
    ('PN20260009', 'SP2026089', 15, 30000),
    ('PN20260010', 'SP2026090', 20, 150000),
    ('PN20260001', 'SP2026091', 8, 200000),
    ('PN20260002', 'SP2026092', 5, 200000),
    ('PN20260003', 'SP2026093', 12, 150000),
    ('PN20260004', 'SP2026094', 20, 500000),
    ('PN20260005', 'SP2026095', 15, 500000),
    ('PN20260006', 'SP2026096', 15, 300000),
    ('PN20260007', 'SP2026097', 25, 500000),
    ('PN20260008', 'SP2026098', 15, 200000),
    ('PN20260009', 'SP2026099', 15, 100000),
    ('PN20260010', 'SP2026100', 20, 250000),
    ('PN20260001', 'SP2026101', 30, 80000),
    ('PN20260002', 'SP2026102', 20, 50000),
    ('PN20260003', 'SP2026103', 12, 80000),
    ('PN20260004', 'SP2026104', 30, 400000),
    ('PN20260005', 'SP2026105', 12, 200000),
    ('PN20260006', 'SP2026106', 10, 500000),
    ('PN20260007', 'SP2026107', 25, 100000),
    ('PN20260008', 'SP2026108', 5, 300000),
    ('PN20260009', 'SP2026109', 10, 250000),
    ('PN20260010', 'SP2026110', 20, 250000),
    ('PN20260001', 'SP2026111', 30, 80000),
    ('PN20260002', 'SP2026112', 15, 100000),
    ('PN20260003', 'SP2026113', 30, 500000),
    ('PN20260004', 'SP2026114', 8, 120000),
    ('PN20260005', 'SP2026115', 20, 300000),
    ('PN20260006', 'SP2026116', 12, 200000),
    ('PN20260007', 'SP2026117', 8, 200000),
    ('PN20260008', 'SP2026118', 30, 200000),
    ('PN20260009', 'SP2026119', 25, 400000),
    ('PN20260010', 'SP2026120', 25, 250000),
    ('PN20260001', 'SP2026121', 25, 200000),
    ('PN20260002', 'SP2026122', 12, 250000),
    ('PN20260003', 'SP2026123', 30, 30000),
    ('PN20260004', 'SP2026124', 10, 250000),
    ('PN20260005', 'SP2026125', 5, 200000),
    ('PN20260006', 'SP2026126', 8, 500000),
    ('PN20260007', 'SP2026127', 10, 30000),
    ('PN20260008', 'SP2026128', 12, 250000),
    ('PN20260009', 'SP2026129', 15, 200000),
    ('PN20260010', 'SP2026130', 5, 300000),
    ('PN20260001', 'SP2026131', 8, 500000),
    ('PN20260002', 'SP2026132', 12, 30000),
    ('PN20260003', 'SP2026133', 10, 250000),
    ('PN20260004', 'SP2026134', 12, 250000),
    ('PN20260005', 'SP2026135', 10, 400000),
    ('PN20260006', 'SP2026136', 15, 50000),
    ('PN20260007', 'SP2026137', 25, 200000),
    ('PN20260008', 'SP2026138', 12, 50000),
    ('PN20260009', 'SP2026139', 8, 400000),
    ('PN20260010', 'SP2026140', 25, 500000),
    ('PN20260001', 'SP2026141', 20, 30000),
    ('PN20260002', 'SP2026142', 25, 150000),
    ('PN20260003', 'SP2026143', 25, 80000),
    ('PN20260004', 'SP2026144', 30, 250000),
    ('PN20260005', 'SP2026145', 20, 100000),
    ('PN20260006', 'SP2026146', 12, 250000),
    ('PN20260007', 'SP2026147', 20, 30000),
    ('PN20260008', 'SP2026148', 30, 100000),
    ('PN20260009', 'SP2026149', 15, 400000),
    ('PN20260010', 'SP2026150', 5, 300000),
    ('PN20260001', 'SP2026151', 25, 250000),
    ('PN20260002', 'SP2026152', 30, 250000),
    ('PN20260003', 'SP2026153', 12, 200000),
    ('PN20260004', 'SP2026154', 20, 250000),
    ('PN20260005', 'SP2026155', 25, 300000),
    ('PN20260006', 'SP2026156', 15, 120000),
    ('PN20260007', 'SP2026157', 12, 150000),
    ('PN20260008', 'SP2026158', 12, 100000),
    ('PN20260009', 'SP2026159', 15, 50000),
    ('PN20260010', 'SP2026160', 20, 80000),
    ('PN20260001', 'SP2026161', 10, 120000),
    ('PN20260002', 'SP2026162', 15, 80000),
    ('PN20260003', 'SP2026163', 5, 400000),
    ('PN20260004', 'SP2026164', 30, 150000),
    ('PN20260005', 'SP2026165', 30, 50000),
    ('PN20260006', 'SP2026166', 8, 400000),
    ('PN20260007', 'SP2026167', 10, 400000),
    ('PN20260008', 'SP2026168', 8, 300000),
    ('PN20260009', 'SP2026169', 12, 300000),
    ('PN20260010', 'SP2026170', 15, 400000),
    ('PN20260001', 'SP2026171', 5, 400000),
    ('PN20260002', 'SP2026172', 12, 120000),
    ('PN20260003', 'SP2026173', 12, 80000),
    ('PN20260004', 'SP2026174', 5, 200000),
    ('PN20260005', 'SP2026175', 15, 30000),
    ('PN20260006', 'SP2026176', 30, 50000),
    ('PN20260007', 'SP2026177', 12, 200000),
    ('PN20260008', 'SP2026178', 10, 120000),
    ('PN20260009', 'SP2026179', 10, 120000),
    ('PN20260010', 'SP2026180', 30, 50000),
    ('PN20260001', 'SP2026181', 15, 300000),
    ('PN20260002', 'SP2026182', 8, 400000),
    ('PN20260003', 'SP2026183', 15, 30000),
    ('PN20260004', 'SP2026184', 5, 500000),
    ('PN20260005', 'SP2026185', 10, 250000),
    ('PN20260006', 'SP2026186', 10, 400000),
    ('PN20260007', 'SP2026187', 30, 150000),
    ('PN20260008', 'SP2026188', 8, 80000),
    ('PN20260009', 'SP2026189', 15, 500000),
    ('PN20260010', 'SP2026190', 30, 50000),
    ('PN20260001', 'SP2026191', 8, 200000),
    ('PN20260002', 'SP2026192', 30, 200000),
    ('PN20260003', 'SP2026193', 20, 400000),
    ('PN20260004', 'SP2026194', 12, 120000),
    ('PN20260005', 'SP2026195', 30, 250000),
    ('PN20260006', 'SP2026196', 12, 80000),
    ('PN20260007', 'SP2026197', 25, 50000),
    ('PN20260008', 'SP2026198', 8, 500000);
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
        ('PTTT2025002', N'Chuyển khoản ngân hàng', N'Thanh toán qua tài khoản ngân hàng', 1),
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
    NgayLap DATETIME NOT NULL,
    MaKhachHang VARCHAR(15) NOT NULL,
    MaNhanVien VARCHAR(15) NOT NULL,
    MaPTTT VARCHAR(15) NOT NULL,
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang) ON DELETE CASCADE,
    FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE,
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT) ON DELETE CASCADE
);
GO
-- Chuẩn bị data hoá đơn 
INSERT INTO HoaDon (MaHoaDon, NgayLap, MaKhachHang, MaNhanVien, MaPTTT)
VALUES
    -- 2026-05-01
    ('HD20260001', '2026-05-01 08:15:00', 'KH2025001', 'NV2025004', 'PTTT2025001'),
    ('HD20260002', '2026-05-01 09:30:00', 'KH2025002', 'NV2025005', 'PTTT2025002'),
    ('HD20260003', '2026-05-01 11:00:00', 'KH2025003', 'NV2025006', 'PTTT2025003'),
    ('HD20260004', '2026-05-01 14:45:00', 'KH2025004', 'NV2025007', 'PTTT2025004'),
    -- 2026-05-02
    ('HD20260005', '2026-05-02 08:20:00', 'KH2025005', 'NV2025004', 'PTTT2025005'),
    ('HD20260006', '2026-05-02 10:10:00', 'KH2025006', 'NV2025005', 'PTTT2025001'),
    ('HD20260007', '2026-05-02 13:25:00', 'KH2025007', 'NV2025006', 'PTTT2025002'),
    ('HD20260008', '2026-05-02 15:50:00', 'KH2025008', 'NV2025007', 'PTTT2025003'),
    -- 2026-05-03
    ('HD20260009', '2026-05-03 08:05:00', 'KH2025009', 'NV2025004', 'PTTT2025004'),
    ('HD20260010', '2026-05-03 09:55:00', 'KH2025010', 'NV2025005', 'PTTT2025005'),
    ('HD20260011', '2026-05-03 11:30:00', 'KH2025011', 'NV2025006', 'PTTT2025001'),
    ('HD20260012', '2026-05-03 16:00:00', 'KH2025012', 'NV2025007', 'PTTT2025002'),
    -- 2026-05-04
    ('HD20260013', '2026-05-04 08:40:00', 'KH2025013', 'NV2025004', 'PTTT2025003'),
    ('HD20260014', '2026-05-04 10:20:00', 'KH2025014', 'NV2025005', 'PTTT2025004'),
    ('HD20260015', '2026-05-04 13:15:00', 'KH2025015', 'NV2025006', 'PTTT2025005'),
    ('HD20260016', '2026-05-04 15:35:00', 'KH2025001', 'NV2025007', 'PTTT2025001'),
    -- 2026-05-05
    ('HD20260017', '2026-05-05 08:00:00', 'KH2025002', 'NV2025004', 'PTTT2025002'),
    ('HD20260018', '2026-05-05 09:45:00', 'KH2025003', 'NV2025005', 'PTTT2025003'),
    ('HD20260019', '2026-05-05 12:10:00', 'KH2025004', 'NV2025006', 'PTTT2025004'),
    ('HD20260020', '2026-05-05 14:30:00', 'KH2025005', 'NV2025007', 'PTTT2025005'),
    -- 2026-05-06
    ('HD20260021', '2026-05-06 08:55:00', 'KH2025006', 'NV2025004', 'PTTT2025001'),
    ('HD20260022', '2026-05-06 10:35:00', 'KH2025007', 'NV2025005', 'PTTT2025002'),
    ('HD20260023', '2026-05-06 13:50:00', 'KH2025008', 'NV2025006', 'PTTT2025003'),
    ('HD20260024', '2026-05-06 16:20:00', 'KH2025009', 'NV2025007', 'PTTT2025004'),
    -- 2026-05-07
    ('HD20260025', '2026-05-07 08:30:00', 'KH2025010', 'NV2025004', 'PTTT2025005'),
    ('HD20260026', '2026-05-07 09:15:00', 'KH2025018', 'NV2025005', 'PTTT2025001'),
    ('HD20260027', '2026-05-07 11:45:00', 'KH2025019', 'NV2025006', 'PTTT2025002'),
    ('HD20260028', '2026-05-07 14:00:00', 'KH2025020', 'NV2025007', 'PTTT2025003'),
    -- 2026-05-08
    ('HD20260029', '2026-05-08 08:10:00', 'KH2025011', 'NV2025004', 'PTTT2025004'),
    ('HD20260030', '2026-05-08 09:50:00', 'KH2025012', 'NV2025005', 'PTTT2025001');
GO 
-- Chuẩn bị bản chi tiết hoá đơn
CREATE TABLE ChiTietHoaDon (
    MaHoaDon VARCHAR(15) NOT NULL,
    MaSanPham VARCHAR(15) NOT NULL,
    SoLuong INT,
    DonGia FLOAT,
    PRIMARY KEY (MaHoaDon, MaSanPham),
    FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE,
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham) ON DELETE CASCADE
);
GO 
-- Chuẩn bị data chi tiết hoá đơn
INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia)
VALUES
    -- HD20260001: Nguyễn Văn An - 01/05
    ('HD20260001', 'SP2026002', 2, 203000),
    ('HD20260001', 'SP2026007', 1, 210000),
    -- HD20260002: Trần Thị Bình - 01/05
    ('HD20260002', 'SP2026013', 1, 932000),
    ('HD20260002', 'SP2026020', 3, 99000),
    -- HD20260003: Lê Hoàng Nam - 01/05
    ('HD20260003', 'SP2026024', 2, 590000),
    ('HD20260003', 'SP2026028', 1, 475000),
    -- HD20260004: Phạm Thị Mai - 01/05
    ('HD20260004', 'SP2026001', 1, 530000),
    ('HD20260004', 'SP2026009', 1, 990000),
    ('HD20260004', 'SP2026010', 2, 380000),
    -- HD20260005: Hoàng Minh Đức - 02/05
    ('HD20260005', 'SP2026003', 2, 160000),
    ('HD20260005', 'SP2026004', 3, 140000),
    -- HD20260006: Ngô Thị Lan - 02/05
    ('HD20260006', 'SP2026015', 1, 580000),
    ('HD20260006', 'SP2026018', 2, 539000),
    -- HD20260007: Đỗ Quang Huy - 02/05
    ('HD20260007', 'SP2026022', 1, 140000),
    ('HD20260007', 'SP2026023', 2, 345000),
    ('HD20260007', 'SP2026029', 1, 199000),
    -- HD20260008: Võ Thị Hạnh - 02/05
    ('HD20260008', 'SP2026005', 1, 670000),
    ('HD20260008', 'SP2026011', 2, 225000),
    -- HD20260009: Bùi Thanh Tùng - 03/05
    ('HD20260009', 'SP2026031', 1, 150000),
    ('HD20260009', 'SP2026032', 2, 94000),
    ('HD20260009', 'SP2026035', 1, 299000),
    -- HD20260010: Đặng Ngọc Anh - 03/05
    ('HD20260010', 'SP2026006', 1, 643000),
    ('HD20260010', 'SP2026016', 1, 922000),
    -- HD20260011: Phan Quốc Bảo - 03/05
    ('HD20260011', 'SP2026025', 1, 1049000),
    ('HD20260011', 'SP2026026', 1, 880000),
    -- HD20260012: Lý Thị Thu - 03/05
    ('HD20260012', 'SP2026033', 2, 156000),
    ('HD20260012', 'SP2026036', 3, 95000),
    ('HD20260012', 'SP2026037', 1, 158000),
    -- HD20260013: Nguyễn Minh Tuấn - 04/05
    ('HD20260013', 'SP2026008', 1, 2300000),
    -- HD20260014: Trịnh Thị Hoa - 04/05
    ('HD20260014', 'SP2026012', 2, 164000),
    ('HD20260014', 'SP2026017', 1, 600000),
    -- HD20260015: Phùng Gia Khánh - 04/05
    ('HD20260015', 'SP2026019', 1, 528000),
    ('HD20260015', 'SP2026021', 5, 39000),
    -- HD20260016: Nguyễn Văn An - 04/05
    ('HD20260016', 'SP2026027', 2, 393000),
    ('HD20260016', 'SP2026030', 3, 139000),
    -- HD20260017: Trần Thị Bình - 05/05
    ('HD20260017', 'SP2026014', 1, 932000),
    ('HD20260017', 'SP2026020', 2, 99000),
    -- HD20260018: Lê Hoàng Nam - 05/05
    ('HD20260018', 'SP2026034', 1, 485000),
    ('HD20260018', 'SP2026035', 2, 299000),
    -- HD20260019: Phạm Thị Mai - 05/05
    ('HD20260019', 'SP2026006', 2, 643000),
    -- HD20260020: Hoàng Minh Đức - 05/05
    ('HD20260020', 'SP2026007', 3, 210000),
    ('HD20260020', 'SP2026010', 1, 380000),
    ('HD20260020', 'SP2026011', 2, 225000),
    -- HD20260021: Ngô Thị Lan - 06/05
    ('HD20260021', 'SP2026002', 2, 203000),
    ('HD20260021', 'SP2026009', 1, 990000),
    -- HD20260022: Đỗ Quang Huy - 06/05
    ('HD20260022', 'SP2026023', 1, 345000),
    ('HD20260022', 'SP2026024', 1, 590000),
    -- HD20260023: Võ Thị Hạnh - 06/05
    ('HD20260023', 'SP2026026', 1, 880000),
    ('HD20260023', 'SP2026027', 1, 393000),
    ('HD20260023', 'SP2026028', 1, 475000),
    -- HD20260024: Bùi Thanh Tùng - 06/05
    ('HD20260024', 'SP2026013', 1, 932000),
    ('HD20260024', 'SP2026016', 1, 922000),
    -- HD20260025: Đặng Ngọc Anh - 07/05
    ('HD20260025', 'SP2026005', 2, 670000),
    -- HD20260026: Chu Thị Ngọc - 07/05
    ('HD20260026', 'SP2026031', 1, 150000),
    ('HD20260026', 'SP2026032', 3, 94000),
    ('HD20260026', 'SP2026033', 2, 156000),
    -- HD20260027: Nguyễn Quốc Hưng - 07/05
    ('HD20260027', 'SP2026003', 1, 160000),
    ('HD20260027', 'SP2026004', 2, 140000),
    -- HD20260028: Tạ Minh Phúc - 07/05
    ('HD20260028', 'SP2026001', 1, 530000),
    ('HD20260028', 'SP2026008', 1, 2300000),
    -- HD20260029: Phan Quốc Bảo - 08/05
    ('HD20260029', 'SP2026015', 2, 580000),
    ('HD20260029', 'SP2026017', 1, 600000),
    -- HD20260030: Lý Thị Thu - 08/05
    ('HD20260030', 'SP2026025', 1, 1049000),
    ('HD20260030', 'SP2026029', 2, 199000),
    ('HD20260030', 'SP2026030', 2, 139000);
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
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham) ON DELETE CASCADE,
    FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap) ON DELETE CASCADE,
    FOREIGN KEY (MaKeSanPham) REFERENCES KeSanPham(MaKeSanPham) ON DELETE CASCADE
);
GO 
-- Chuẩn bị data Lô sản phẩm
-- LoSanPham
INSERT INTO LoSanPham (MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai)
VALUES
    ('LSP20260001', 'SP2026001', 'PN20260001', 'KSP2026001', 240, N'Gói', '2027-07-10', 1),
    ('LSP20260002', 'SP2026002', 'PN20260002', 'KSP2026002', 120, N'Gói', '2029-01-25', 1),
    ('LSP20260003', 'SP2026003', 'PN20260003', 'KSP2026003', 80, N'Hộp', '2027-02-08', 1),
    ('LSP20260004', 'SP2026004', 'PN20260004', 'KSP2026004', 800, N'Viên', '2029-02-22', 1),
    ('LSP20260005', 'SP2026005', 'PN20260005', 'KSP2026005', 500, N'Viên', '2028-09-05', 1),
    ('LSP20260006', 'SP2026006', 'PN20260006', 'KSP2026006', 360, N'Gói', '2027-03-18', 1),
    ('LSP20260007', 'SP2026007', 'PN20260007', 'KSP2026007', 300, N'Hộp', '2027-10-02', 1),
    ('LSP20260008', 'SP2026008', 'PN20260008', 'KSP2026008', 120, N'Viên', '2028-10-15', 1),
    ('LSP20260009', 'SP2026009', 'PN20260009', 'KSP2026009', 800, N'Hộp', '2028-04-28', 1),
    ('LSP20260010', 'SP2026010', 'PN20260010', 'KSP2026010', 50, N'Gói', '2028-11-01', 1),
    ('LSP20260011', 'SP2026011', 'PN20260001', 'KSP2026001', 800, N'Hộp', '2028-01-10', 1),
    ('LSP20260012', 'SP2026012', 'PN20260002', 'KSP2026002', 240, N'Viên', '2028-01-25', 1),
    ('LSP20260013', 'SP2026013', 'PN20260003', 'KSP2026003', 400, N'Viên', '2028-02-08', 1),
    ('LSP20260014', 'SP2026014', 'PN20260004', 'KSP2026004', 600, N'Hộp', '2028-02-22', 1),
    ('LSP20260015', 'SP2026015', 'PN20260005', 'KSP2026005', 600, N'Gói', '2027-03-05', 1),
    ('LSP20260016', 'SP2026016', 'PN20260006', 'KSP2026006', 200, N'Gói', '2027-09-18', 1),
    ('LSP20260017', 'SP2026017', 'PN20260007', 'KSP2026007', 3000, N'Hộp', '2027-10-02', 1),
    ('LSP20260018', 'SP2026018', 'PN20260008', 'KSP2026008', 200, N'Viên', '2028-04-15', 1),
    ('LSP20260019', 'SP2026019', 'PN20260009', 'KSP2026009', 500, N'Viên', '2029-04-28', 1),
    ('LSP20260020', 'SP2026020', 'PN20260010', 'KSP2026010', 1000, N'Gói', '2028-11-01', 1),
    ('LSP20260021', 'SP2026021', 'PN20260001', 'KSP2026001', 600, N'Hộp', '2027-07-10', 1),
    ('LSP20260022', 'SP2026022', 'PN20260002', 'KSP2026002', 1500, N'Hộp', '2028-07-25', 1),
    ('LSP20260023', 'SP2026023', 'PN20260003', 'KSP2026003', 2000, N'Viên', '2028-08-08', 1),
    ('LSP20260024', 'SP2026024', 'PN20260004', 'KSP2026004', 160, N'Viên', '2027-08-22', 1),
    ('LSP20260025', 'SP2026025', 'PN20260005', 'KSP2026005', 1250, N'Viên', '2028-09-05', 1),
    ('LSP20260026', 'SP2026026', 'PN20260006', 'KSP2026006', 3000, N'Hộp', '2027-03-18', 1),
    ('LSP20260027', 'SP2026027', 'PN20260007', 'KSP2026007', 240, N'Gói', '2028-04-02', 1),
    ('LSP20260028', 'SP2026028', 'PN20260008', 'KSP2026008', 160, N'Hộp', '2028-10-15', 1),
    ('LSP20260029', 'SP2026029', 'PN20260009', 'KSP2026009', 100, N'Gói', '2029-04-28', 1),
    ('LSP20260030', 'SP2026030', 'PN20260010', 'KSP2026010', 800, N'Hộp', '2029-05-01', 1),
    ('LSP20260031', 'SP2026031', 'PN20260001', 'KSP2026001', 240, N'Hộp', '2029-01-10', 1),
    ('LSP20260032', 'SP2026032', 'PN20260002', 'KSP2026002', 250, N'Hộp', '2027-01-25', 1),
    ('LSP20260033', 'SP2026033', 'PN20260003', 'KSP2026003', 160, N'Hộp', '2027-02-08', 1),
    ('LSP20260034', 'SP2026034', 'PN20260004', 'KSP2026004', 120, N'Viên', '2028-08-22', 1),
    ('LSP20260035', 'SP2026035', 'PN20260005', 'KSP2026005', 160, N'Viên', '2028-09-05', 1),
    ('LSP20260036', 'SP2026036', 'PN20260006', 'KSP2026006', 1000, N'Gói', '2028-09-18', 1),
    ('LSP20260037', 'SP2026037', 'PN20260007', 'KSP2026007', 240, N'Gói', '2028-04-02', 1),
    ('LSP20260038', 'SP2026038', 'PN20260008', 'KSP2026008', 750, N'Gói', '2028-10-15', 1),
    ('LSP20260039', 'SP2026039', 'PN20260009', 'KSP2026009', 600, N'Viên', '2027-04-28', 1),
    ('LSP20260040', 'SP2026040', 'PN20260010', 'KSP2026010', 2000, N'Gói', '2027-11-01', 1),
    ('LSP20260041', 'SP2026041', 'PN20260001', 'KSP2026001', 120, N'Viên', '2027-07-10', 1),
    ('LSP20260042', 'SP2026042', 'PN20260002', 'KSP2026002', 80, N'Hộp', '2029-01-25', 1),
    ('LSP20260043', 'SP2026043', 'PN20260003', 'KSP2026003', 600, N'Gói', '2027-08-08', 1),
    ('LSP20260044', 'SP2026044', 'PN20260004', 'KSP2026004', 500, N'Gói', '2027-08-22', 1),
    ('LSP20260045', 'SP2026045', 'PN20260005', 'KSP2026005', 300, N'Viên', '2027-03-05', 1),
    ('LSP20260046', 'SP2026046', 'PN20260006', 'KSP2026006', 1250, N'Hộp', '2028-09-18', 1),
    ('LSP20260047', 'SP2026047', 'PN20260007', 'KSP2026007', 50, N'Gói', '2027-04-02', 1),
    ('LSP20260048', 'SP2026048', 'PN20260008', 'KSP2026008', 500, N'Viên', '2027-10-15', 1),
    ('LSP20260049', 'SP2026049', 'PN20260009', 'KSP2026009', 240, N'Hộp', '2028-10-28', 1),
    ('LSP20260050', 'SP2026050', 'PN20260010', 'KSP2026010', 200, N'Hộp', '2027-05-01', 1),
    ('LSP20260051', 'SP2026051', 'PN20260001', 'KSP2026001', 300, N'Viên', '2029-01-10', 1),
    ('LSP20260052', 'SP2026052', 'PN20260002', 'KSP2026002', 100, N'Viên', '2028-07-25', 1),
    ('LSP20260053', 'SP2026053', 'PN20260003', 'KSP2026003', 1500, N'Viên', '2027-02-08', 1),
    ('LSP20260054', 'SP2026054', 'PN20260004', 'KSP2026004', 500, N'Viên', '2028-02-22', 1),
    ('LSP20260055', 'SP2026055', 'PN20260005', 'KSP2026005', 3000, N'Hộp', '2028-09-05', 1),
    ('LSP20260056', 'SP2026056', 'PN20260006', 'KSP2026006', 200, N'Hộp', '2027-03-18', 1),
    ('LSP20260057', 'SP2026057', 'PN20260007', 'KSP2026007', 50, N'Viên', '2029-04-02', 1),
    ('LSP20260058', 'SP2026058', 'PN20260008', 'KSP2026008', 600, N'Gói', '2027-04-15', 1),
    ('LSP20260059', 'SP2026059', 'PN20260009', 'KSP2026009', 800, N'Viên', '2027-04-28', 1),
    ('LSP20260060', 'SP2026060', 'PN20260010', 'KSP2026010', 1200, N'Viên', '2027-11-01', 1),
    ('LSP20260061', 'SP2026061', 'PN20260001', 'KSP2026001', 250, N'Viên', '2029-01-10', 1),
    ('LSP20260062', 'SP2026062', 'PN20260002', 'KSP2026002', 600, N'Viên', '2027-07-25', 1),
    ('LSP20260063', 'SP2026063', 'PN20260003', 'KSP2026003', 450, N'Viên', '2028-08-08', 1),
    ('LSP20260064', 'SP2026064', 'PN20260004', 'KSP2026004', 1000, N'Viên', '2029-02-22', 1),
    ('LSP20260065', 'SP2026065', 'PN20260005', 'KSP2026005', 160, N'Gói', '2029-03-05', 1),
    ('LSP20260066', 'SP2026066', 'PN20260006', 'KSP2026006', 150, N'Hộp', '2027-09-18', 1),
    ('LSP20260067', 'SP2026067', 'PN20260007', 'KSP2026007', 1000, N'Viên', '2029-04-02', 1),
    ('LSP20260068', 'SP2026068', 'PN20260008', 'KSP2026008', 1500, N'Gói', '2027-04-15', 1),
    ('LSP20260069', 'SP2026069', 'PN20260009', 'KSP2026009', 300, N'Viên', '2028-04-28', 1),
    ('LSP20260070', 'SP2026070', 'PN20260010', 'KSP2026010', 800, N'Gói', '2027-11-01', 1),
    ('LSP20260071', 'SP2026071', 'PN20260001', 'KSP2026001', 300, N'Gói', '2028-01-10', 1),
    ('LSP20260072', 'SP2026072', 'PN20260002', 'KSP2026002', 360, N'Gói', '2029-01-25', 1),
    ('LSP20260073', 'SP2026073', 'PN20260003', 'KSP2026003', 300, N'Viên', '2028-08-08', 1),
    ('LSP20260074', 'SP2026074', 'PN20260004', 'KSP2026004', 450, N'Viên', '2027-08-22', 1),
    ('LSP20260075', 'SP2026075', 'PN20260005', 'KSP2026005', 750, N'Gói', '2029-03-05', 1),
    ('LSP20260076', 'SP2026076', 'PN20260006', 'KSP2026006', 250, N'Viên', '2027-03-18', 1),
    ('LSP20260077', 'SP2026077', 'PN20260007', 'KSP2026007', 300, N'Viên', '2029-04-02', 1),
    ('LSP20260078', 'SP2026078', 'PN20260008', 'KSP2026008', 100, N'Viên', '2028-04-15', 1),
    ('LSP20260079', 'SP2026079', 'PN20260009', 'KSP2026009', 400, N'Hộp', '2027-10-28', 1),
    ('LSP20260080', 'SP2026080', 'PN20260010', 'KSP2026010', 400, N'Gói', '2029-05-01', 1),
    ('LSP20260081', 'SP2026081', 'PN20260001', 'KSP2026001', 200, N'Viên', '2028-07-10', 1),
    ('LSP20260082', 'SP2026082', 'PN20260002', 'KSP2026002', 150, N'Gói', '2028-07-25', 1),
    ('LSP20260083', 'SP2026083', 'PN20260003', 'KSP2026003', 120, N'Viên', '2028-08-08', 1),
    ('LSP20260084', 'SP2026084', 'PN20260004', 'KSP2026004', 100, N'Viên', '2028-08-22', 1),
    ('LSP20260085', 'SP2026085', 'PN20260005', 'KSP2026005', 400, N'Viên', '2027-03-05', 1),
    ('LSP20260086', 'SP2026086', 'PN20260006', 'KSP2026006', 360, N'Hộp', '2027-03-18', 1),
    ('LSP20260087', 'SP2026087', 'PN20260007', 'KSP2026007', 1500, N'Gói', '2028-10-02', 1),
    ('LSP20260088', 'SP2026088', 'PN20260008', 'KSP2026008', 600, N'Viên', '2027-10-15', 1),
    ('LSP20260089', 'SP2026089', 'PN20260009', 'KSP2026009', 1500, N'Viên', '2028-10-28', 1),
    ('LSP20260090', 'SP2026090', 'PN20260010', 'KSP2026010', 2000, N'Hộp', '2029-05-01', 1),
    ('LSP20260091', 'SP2026091', 'PN20260001', 'KSP2026001', 160, N'Gói', '2028-01-10', 1),
    ('LSP20260092', 'SP2026092', 'PN20260002', 'KSP2026002', 500, N'Viên', '2029-01-25', 1),
    ('LSP20260093', 'SP2026093', 'PN20260003', 'KSP2026003', 120, N'Hộp', '2028-02-08', 1),
    ('LSP20260094', 'SP2026094', 'PN20260004', 'KSP2026004', 600, N'Viên', '2029-02-22', 1),
    ('LSP20260095', 'SP2026095', 'PN20260005', 'KSP2026005', 450, N'Hộp', '2028-09-05', 1),
    ('LSP20260096', 'SP2026096', 'PN20260006', 'KSP2026006', 300, N'Viên', '2028-09-18', 1),
    ('LSP20260097', 'SP2026097', 'PN20260007', 'KSP2026007', 500, N'Gói', '2029-04-02', 1),
    ('LSP20260098', 'SP2026098', 'PN20260008', 'KSP2026008', 150, N'Gói', '2028-04-15', 1),
    ('LSP20260099', 'SP2026099', 'PN20260009', 'KSP2026009', 1500, N'Hộp', '2029-04-28', 1),
    ('LSP20260100', 'SP2026100', 'PN20260010', 'KSP2026010', 1000, N'Hộp', '2027-11-01', 1),
    ('LSP20260101', 'SP2026101', 'PN20260001', 'KSP2026001', 300, N'Gói', '2028-01-10', 1),
    ('LSP20260102', 'SP2026102', 'PN20260002', 'KSP2026002', 600, N'Viên', '2027-07-25', 1),
    ('LSP20260103', 'SP2026103', 'PN20260003', 'KSP2026003', 120, N'Viên', '2027-08-08', 1),
    ('LSP20260104', 'SP2026104', 'PN20260004', 'KSP2026004', 1500, N'Viên', '2028-08-22', 1),
    ('LSP20260105', 'SP2026105', 'PN20260005', 'KSP2026005', 600, N'Hộp', '2027-09-05', 1),
    ('LSP20260106', 'SP2026106', 'PN20260006', 'KSP2026006', 100, N'Gói', '2027-03-18', 1),
    ('LSP20260107', 'SP2026107', 'PN20260007', 'KSP2026007', 2500, N'Viên', '2028-10-02', 1),
    ('LSP20260108', 'SP2026108', 'PN20260008', 'KSP2026008', 50, N'Viên', '2028-10-15', 1),
    ('LSP20260109', 'SP2026109', 'PN20260009', 'KSP2026009', 1000, N'Gói', '2029-04-28', 1),
    ('LSP20260110', 'SP2026110', 'PN20260010', 'KSP2026010', 2000, N'Gói', '2028-11-01', 1),
    ('LSP20260111', 'SP2026111', 'PN20260001', 'KSP2026001', 1500, N'Gói', '2028-07-10', 1),
    ('LSP20260112', 'SP2026112', 'PN20260002', 'KSP2026002', 450, N'Gói', '2029-01-25', 1),
    ('LSP20260113', 'SP2026113', 'PN20260003', 'KSP2026003', 900, N'Viên', '2028-08-08', 1),
    ('LSP20260114', 'SP2026114', 'PN20260004', 'KSP2026004', 240, N'Viên', '2028-02-22', 1),
    ('LSP20260115', 'SP2026115', 'PN20260005', 'KSP2026005', 400, N'Viên', '2027-09-05', 1),
    ('LSP20260116', 'SP2026116', 'PN20260006', 'KSP2026006', 240, N'Gói', '2027-09-18', 1),
    ('LSP20260117', 'SP2026117', 'PN20260007', 'KSP2026007', 240, N'Hộp', '2029-04-02', 1),
    ('LSP20260118', 'SP2026118', 'PN20260008', 'KSP2026008', 600, N'Viên', '2028-10-15', 1),
    ('LSP20260119', 'SP2026119', 'PN20260009', 'KSP2026009', 250, N'Gói', '2029-04-28', 1),
    ('LSP20260120', 'SP2026120', 'PN20260010', 'KSP2026010', 750, N'Viên', '2028-05-01', 1),
    ('LSP20260121', 'SP2026121', 'PN20260001', 'KSP2026001', 2500, N'Gói', '2029-01-10', 1),
    ('LSP20260122', 'SP2026122', 'PN20260002', 'KSP2026002', 360, N'Viên', '2028-07-25', 1),
    ('LSP20260123', 'SP2026123', 'PN20260003', 'KSP2026003', 900, N'Hộp', '2028-08-08', 1),
    ('LSP20260124', 'SP2026124', 'PN20260004', 'KSP2026004', 1000, N'Viên', '2029-02-22', 1),
    ('LSP20260125', 'SP2026125', 'PN20260005', 'KSP2026005', 500, N'Gói', '2027-03-05', 1),
    ('LSP20260126', 'SP2026126', 'PN20260006', 'KSP2026006', 160, N'Hộp', '2028-09-18', 1),
    ('LSP20260127', 'SP2026127', 'PN20260007', 'KSP2026007', 500, N'Hộp', '2028-04-02', 1),
    ('LSP20260128', 'SP2026128', 'PN20260008', 'KSP2026008', 360, N'Hộp', '2028-10-15', 1),
    ('LSP20260129', 'SP2026129', 'PN20260009', 'KSP2026009', 150, N'Hộp', '2028-10-28', 1),
    ('LSP20260130', 'SP2026130', 'PN20260010', 'KSP2026010', 150, N'Viên', '2027-11-01', 1),
    ('LSP20260131', 'SP2026131', 'PN20260001', 'KSP2026001', 80, N'Viên', '2027-07-10', 1),
    ('LSP20260132', 'SP2026132', 'PN20260002', 'KSP2026002', 240, N'Gói', '2027-07-25', 1),
    ('LSP20260133', 'SP2026133', 'PN20260003', 'KSP2026003', 100, N'Gói', '2029-02-08', 1),
    ('LSP20260134', 'SP2026134', 'PN20260004', 'KSP2026004', 360, N'Gói', '2028-02-22', 1),
    ('LSP20260135', 'SP2026135', 'PN20260005', 'KSP2026005', 100, N'Gói', '2027-09-05', 1),
    ('LSP20260136', 'SP2026136', 'PN20260006', 'KSP2026006', 150, N'Gói', '2028-03-18', 1),
    ('LSP20260137', 'SP2026137', 'PN20260007', 'KSP2026007', 500, N'Gói', '2027-04-02', 1),
    ('LSP20260138', 'SP2026138', 'PN20260008', 'KSP2026008', 360, N'Gói', '2029-04-15', 1),
    ('LSP20260139', 'SP2026139', 'PN20260009', 'KSP2026009', 240, N'Viên', '2029-04-28', 1),
    ('LSP20260140', 'SP2026140', 'PN20260010', 'KSP2026010', 250, N'Hộp', '2029-05-01', 1),
    ('LSP20260141', 'SP2026141', 'PN20260001', 'KSP2026001', 1000, N'Hộp', '2027-01-10', 1),
    ('LSP20260142', 'SP2026142', 'PN20260002', 'KSP2026002', 1250, N'Gói', '2027-07-25', 1),
    ('LSP20260143', 'SP2026143', 'PN20260003', 'KSP2026003', 2500, N'Gói', '2028-02-08', 1),
    ('LSP20260144', 'SP2026144', 'PN20260004', 'KSP2026004', 3000, N'Hộp', '2028-02-22', 1),
    ('LSP20260145', 'SP2026145', 'PN20260005', 'KSP2026005', 600, N'Viên', '2028-09-05', 1),
    ('LSP20260146', 'SP2026146', 'PN20260006', 'KSP2026006', 1200, N'Gói', '2028-09-18', 1),
    ('LSP20260147', 'SP2026147', 'PN20260007', 'KSP2026007', 600, N'Hộp', '2027-10-02', 1),
    ('LSP20260148', 'SP2026148', 'PN20260008', 'KSP2026008', 900, N'Hộp', '2028-04-15', 1),
    ('LSP20260149', 'SP2026149', 'PN20260009', 'KSP2026009', 450, N'Gói', '2029-04-28', 1),
    ('LSP20260150', 'SP2026150', 'PN20260010', 'KSP2026010', 50, N'Viên', '2027-11-01', 1),
    ('LSP20260151', 'SP2026151', 'PN20260001', 'KSP2026001', 500, N'Gói', '2028-07-10', 1),
    ('LSP20260152', 'SP2026152', 'PN20260002', 'KSP2026002', 300, N'Viên', '2028-01-25', 1),
    ('LSP20260153', 'SP2026153', 'PN20260003', 'KSP2026003', 240, N'Gói', '2028-02-08', 1),
    ('LSP20260154', 'SP2026154', 'PN20260004', 'KSP2026004', 2000, N'Gói', '2028-02-22', 1),
    ('LSP20260155', 'SP2026155', 'PN20260005', 'KSP2026005', 750, N'Hộp', '2028-09-05', 1),
    ('LSP20260156', 'SP2026156', 'PN20260006', 'KSP2026006', 300, N'Hộp', '2027-03-18', 1),
    ('LSP20260157', 'SP2026157', 'PN20260007', 'KSP2026007', 1200, N'Viên', '2027-10-02', 1),
    ('LSP20260158', 'SP2026158', 'PN20260008', 'KSP2026008', 600, N'Gói', '2028-04-15', 1),
    ('LSP20260159', 'SP2026159', 'PN20260009', 'KSP2026009', 450, N'Viên', '2027-10-28', 1),
    ('LSP20260160', 'SP2026160', 'PN20260010', 'KSP2026010', 200, N'Hộp', '2029-05-01', 1),
    ('LSP20260161', 'SP2026161', 'PN20260001', 'KSP2026001', 100, N'Viên', '2029-01-10', 1),
    ('LSP20260162', 'SP2026162', 'PN20260002', 'KSP2026002', 750, N'Gói', '2027-01-25', 1),
    ('LSP20260163', 'SP2026163', 'PN20260003', 'KSP2026003', 250, N'Hộp', '2028-08-08', 1),
    ('LSP20260164', 'SP2026164', 'PN20260004', 'KSP2026004', 300, N'Viên', '2028-02-22', 1),
    ('LSP20260165', 'SP2026165', 'PN20260005', 'KSP2026005', 1500, N'Viên', '2028-09-05', 1),
    ('LSP20260166', 'SP2026166', 'PN20260006', 'KSP2026006', 80, N'Gói', '2027-09-18', 1),
    ('LSP20260167', 'SP2026167', 'PN20260007', 'KSP2026007', 100, N'Hộp', '2027-10-02', 1),
    ('LSP20260168', 'SP2026168', 'PN20260008', 'KSP2026008', 800, N'Hộp', '2029-04-15', 1),
    ('LSP20260169', 'SP2026169', 'PN20260009', 'KSP2026009', 600, N'Hộp', '2028-10-28', 1),
    ('LSP20260170', 'SP2026170', 'PN20260010', 'KSP2026010', 450, N'Hộp', '2029-05-01', 1),
    ('LSP20260171', 'SP2026171', 'PN20260001', 'KSP2026001', 50, N'Gói', '2027-07-10', 1),
    ('LSP20260172', 'SP2026172', 'PN20260002', 'KSP2026002', 120, N'Gói', '2027-07-25', 1),
    ('LSP20260173', 'SP2026173', 'PN20260003', 'KSP2026003', 120, N'Gói', '2027-08-08', 1),
    ('LSP20260174', 'SP2026174', 'PN20260004', 'KSP2026004', 500, N'Hộp', '2028-08-22', 1),
    ('LSP20260175', 'SP2026175', 'PN20260005', 'KSP2026005', 450, N'Viên', '2028-03-05', 1),
    ('LSP20260176', 'SP2026176', 'PN20260006', 'KSP2026006', 600, N'Gói', '2028-03-18', 1),
    ('LSP20260177', 'SP2026177', 'PN20260007', 'KSP2026007', 1200, N'Viên', '2027-10-02', 1),
    ('LSP20260178', 'SP2026178', 'PN20260008', 'KSP2026008', 100, N'Viên', '2027-04-15', 1),
    ('LSP20260179', 'SP2026179', 'PN20260009', 'KSP2026009', 1000, N'Gói', '2028-04-28', 1),
    ('LSP20260180', 'SP2026180', 'PN20260010', 'KSP2026010', 900, N'Hộp', '2028-11-01', 1),
    ('LSP20260181', 'SP2026181', 'PN20260001', 'KSP2026001', 750, N'Gói', '2028-07-10', 1),
    ('LSP20260182', 'SP2026182', 'PN20260002', 'KSP2026002', 400, N'Viên', '2028-01-25', 1),
    ('LSP20260183', 'SP2026183', 'PN20260003', 'KSP2026003', 300, N'Viên', '2029-02-08', 1),
    ('LSP20260184', 'SP2026184', 'PN20260004', 'KSP2026004', 500, N'Hộp', '2027-02-22', 1),
    ('LSP20260185', 'SP2026185', 'PN20260005', 'KSP2026005', 500, N'Gói', '2028-03-05', 1),
    ('LSP20260186', 'SP2026186', 'PN20260006', 'KSP2026006', 500, N'Hộp', '2027-03-18', 1),
    ('LSP20260187', 'SP2026187', 'PN20260007', 'KSP2026007', 900, N'Hộp', '2028-04-02', 1),
    ('LSP20260188', 'SP2026188', 'PN20260008', 'KSP2026008', 400, N'Hộp', '2028-10-15', 1),
    ('LSP20260189', 'SP2026189', 'PN20260009', 'KSP2026009', 1500, N'Hộp', '2027-04-28', 1),
    ('LSP20260190', 'SP2026190', 'PN20260010', 'KSP2026010', 900, N'Hộp', '2028-05-01', 1),
    ('LSP20260191', 'SP2026191', 'PN20260001', 'KSP2026001', 80, N'Gói', '2029-01-10', 1),
    ('LSP20260192', 'SP2026192', 'PN20260002', 'KSP2026002', 600, N'Viên', '2029-01-25', 1),
    ('LSP20260193', 'SP2026193', 'PN20260003', 'KSP2026003', 1000, N'Hộp', '2027-02-08', 1),
    ('LSP20260194', 'SP2026194', 'PN20260004', 'KSP2026004', 240, N'Gói', '2028-02-22', 1),
    ('LSP20260195', 'SP2026195', 'PN20260005', 'KSP2026005', 300, N'Viên', '2029-03-05', 1),
    ('LSP20260196', 'SP2026196', 'PN20260006', 'KSP2026006', 1200, N'Hộp', '2027-03-18', 1),
    ('LSP20260197', 'SP2026197', 'PN20260007', 'KSP2026007', 250, N'Viên', '2028-10-02', 1),
    ('LSP20260198', 'SP2026198', 'PN20260008', 'KSP2026008', 400, N'Viên', '2028-04-15', 1),
    ('LSP20260199', 'SP2026001', 'PN20260001', 'KSP2026009', 80, N'Viên', '2025-12-01', 1),
    ('LSP20260200', 'SP2026010', 'PN20260001', 'KSP2026009', 50, N'Viên', '2026-01-15', 1),
    ('LSP20260201', 'SP2026020', 'PN20260003', 'KSP2026009', 30, N'Gói', '2025-11-01', 1),
    ('LSP20260202', 'SP2026050', 'PN20260005', 'KSP2026001', 0, N'Viên', '2027-06-01', 1),
    ('LSP20260203', 'SP2026100', 'PN20260005', 'KSP2026001', 0, N'Hộp', '2027-08-01', 1),
    ('LSP20260204', 'SP2026030', 'PN20260003', 'KSP2026006', 25, N'Viên', '2027-09-01', 1),
    ('LSP20260205', 'SP2026080', 'PN20260008', 'KSP2026003', 40, N'Gói', '2027-10-01', 1);   -- Đã bán hết
GO



