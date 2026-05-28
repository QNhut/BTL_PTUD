# Tong quan he thong va rang buoc bien (BTL_PTUD)

Cap nhat: 2026-05-28
Pham vi doc: src/constants, src/entity, src/dao, src/service, src/gui, src/util

## 1) Kien truc he thong

Ung dung theo mo hinh desktop Java Swing:

- ConnectDB: quan ly ket noi CSDL SQL Server (singleton).
- DAO: thao tac SQL truc tiep (CRUD, thong ke, sinh ma tu dong).
- Service: xu ly nghiep vu, transaction, quy doi don vi, tinh toan hoa don, quan ly session.
- Entity: doi tuong mien nghiep vu + rang buoc du lieu muc domain.
- GUI: giao dien Swing, validate dau vao nguoi dung, xu ly luong thao tac.
- constants/util/exception: hang so dung chung, helper, validator, custom component.

## Bang tra cuu nhanh: gia tri rang buoc + vi tri file

| Gia tri / Quy tac | Y nghia | File | Dong |
|---|---|---|---|
| NGUONG_SAP_HET = 50 | Nguong ton kho "sap het" | src/service/SanPham_Service.java | 17 |
| tonKho <= 0 => HET_HANG | Trang thai het hang | src/service/SanPham_Service.java | 34 |
| tonKho < NGUONG_SAP_HET => SAP_HET | Trang thai sap het | src/service/SanPham_Service.java | 35 |
| SAP_HET_HAN (<= 90 ngay) | Nguong sap het han theo lo | src/service/LoSanPham_Service.java | 122, 137 |
| soNgay > 7 | Qua han doi hang | src/service/DoiHang_Service.java | 69 |
| soNgay > 7 | Qua han tra hang | src/service/TraHang_Service.java | 39 |
| 08:00 | Gio mo cua Thu 2-7 | src/gui/DatTruoc_GUI.java | 43 |
| 22:00 | Gio dong cua Thu 2-7 | src/gui/DatTruoc_GUI.java | 44 |
| 09:00 | Gio mo cua Chu nhat | src/gui/DatTruoc_GUI.java | 45 |
| 20:00 | Gio dong cua Chu nhat | src/gui/DatTruoc_GUI.java | 46 |
| plusMinutes(30) | Slot gio nhan 30 phut | src/gui/DatTruoc_GUI.java | 744 |
| ngayNhanLD.isBefore(LocalDate.now()) | Chan ngay nhan qua khu | src/gui/DatTruoc_GUI.java | 915 |
| IDLE_TIMEOUT_MILLIS_DEFAULT = 3h | Timeout khong hoat dong | src/service/TaiKhoan_Service.java | 20 |
| MAX_SESSION_MILLIS_DEFAULT = 24h | Han toi da session | src/service/TaiKhoan_Service.java | 21 |
| maxSessionMillis < idleTimeoutMillis | Cau hinh session khong hop le | src/service/TaiKhoan_Service.java | 36, 56 |
| VND_PER_POINT_EARN = 10000 | 10,000 VND duoc 1 diem | src/service/HoaDon_Service.java | 29 |
| VND_PER_POINT_USE = 10 | 1 diem giam 10 VND | src/service/HoaDon_Service.java | 31 |
| tienGiamTuDiem > maxGiamDiem | Gioi han diem de tranh am tien | src/service/HoaDon_Service.java | 139 |
| Math.max(0, tienHang + tienThue - tienGiamTuDiem) | Chan thanh tien am | src/service/HoaDon_Service.java | 143 |
| PT_SDT_VN = ^0\\d{9}$ | SDT VN 10 so bat dau 0 | src/util/Validators.java | 23 |
| ngay dd/MM/yyyy | Dinh dang ngay dau vao | src/util/Validators.java | 17, 72, 80 |
| cccd = \\d{12} | CCCD 12 so | src/util/Validators.java | 127 |
| phanTram [0, 100] | Gioi han % | src/util/Validators.java | 134 |
| HAVING SUM(SoLuong) < 20 | Nguong sap het trong chat context | src/service/ChatDataContext.java | 170, 178 |

## 2) Rang buoc thoi gian va nguong canh bao quan trong

### 2.1 Lo san pham, han su dung, canh bao het han

Nguon chinh:
- service/LoSanPham_Service.java
- dao/LoSanPham_DAO.java
- service/SanPham_Service.java
- exception/ProductTableRenderer.java

Quy tac:

1. Trang thai lo theo han su dung:
- HET_HAN: ngay con lai < 0
- SAP_HET_HAN: ngay con lai <= 90
- CON_HAN: con lai > 90 hoac khong co han su dung

2. Tong ton kho hop le:
- Chi tinh lo co TrangThai = true
- Khong tinh lo da het han (HanSuDung < hom nay)

3. Chien luoc tru kho:
- Dung FEFO (lo gan het han nhat truoc)
- Lo HanSuDung = null xep sau cung
- Neu khong du hang thi throw loi de rollback transaction

4. Nguong san pham sap het trong ton kho tong:
- NGUONG_SAP_HET = 50 (service/SanPham_Service)
- tonKho <= 0 => HET_HANG
- tonKho < 50 => SAP_HET
- con lai => CON_HANG

5. Nguong canh bao trong context chat:
- "Sap het hang" khi tong ton < 20 (service/ChatDataContext)

Ghi chu: He thong hien co 2 nguong sap het (50 cho giao dien ton kho tong, 20 trong context chat).

### 2.2 Doi/tra hang

Nguon chinh:
- service/DoiHang_Service.java
- service/TraHang_Service.java

Quy tac:

1. Han doi/tra:
- Qua 7 ngay ke tu NgayLap hoa don => khong cho doi/tra

2. Rang buoc trang thai hoa don:
- Hoa don "Cho thanh toan" khong duoc doi/tra
- Hoa don da o trang thai "Doi hang" hoac "Tra hang" khong duoc xu ly tiep

### 2.3 Dat truoc va gio nhan hang

Nguon chinh:
- gui/DatTruoc_GUI.java
- service/HoaDon_Service.java

Quy tac:

1. Khung gio nhan hang:
- Thu 2 den Thu 7: 08:00 -> 22:00
- Chu nhat: 09:00 -> 20:00
- Slot chia 30 phut

2. Ngay nhan:
- Phai la hom nay hoac tuong lai (khong cho ngay qua khu)

3. Dat truoc va ton kho:
- Tao hoa don "Cho thanh toan" co the cho phep khi ton kho chua du
- Tru ton kho chi thuc hien luc xac nhan thanh toan

4. Ghi chu tre/ dung hen khi xac nhan:
- Tinh do tre dua tren ngay gio du kien va thoi diem thuc te
- Neu tre > 0 phut thi ghi chu "Nhan tre ..."
- Neu dung/ som thi ghi chu "Nhan dung hen"

### 2.4 Session dang nhap, timeout

Nguon chinh:
- service/TaiKhoan_Service.java
- entity/PhienDangNhap.java

Quy tac:

1. Idle timeout mac dinh:
- 3 gio (IDLE_TIMEOUT_MILLIS_DEFAULT = 3 * 60 * 60 * 1000)

2. Thoi gian toi da cua session:
- 24 gio (MAX_SESSION_MILLIS_DEFAULT = 24 * 60 * 60 * 1000)

3. Dieu kien cau hinh hop le:
- idleTimeout > 0
- maxSession > 0
- maxSession >= idleTimeout

4. Session het han neu:
- Qua han toi da (absolute)
- Hoac qua han khong hoat dong (idle)

## 3) Rang buoc tinh tien, diem, khuyen mai

Nguon chinh:
- service/HoaDon_Service.java
- entity/SanPham.java
- entity/KhuyenMai.java

Quy tac:

1. Quy doi diem:
- Tich diem: 10,000 VND => 1 diem (VND_PER_POINT_EARN = 10000)
- Su dung diem: 1 diem => 10 VND giam (VND_PER_POINT_USE = 10)

2. Gioi han diem su dung:
- Khong cho diem am
- So diem dung khong vuot diem hien co
- Tien giam tu diem khong vuot (tienHang + tienThue)
- Thanh tien khong am (Math.max(0, ...))

3. Khuyen mai san pham co hieu luc khi:
- KhuyenMai != null
- TrangThai khuyen mai = true
- phanTramGG > 0
- Hom nay nam trong [NgayBatDau, NgayKetThuc]

4. Gia sau khuyen mai:
- giaSauKM = floor(giaThanh * (100 - pct) / 100)

## 4) Rang buoc validate dau vao (util/Validators)

Nguon chinh:
- util/Validators.java

Quy tac:

- required: khong duoc rong
- tenNguoi: regex ^[\\p{L}][\\p{L} .'\\-]{0,79}$
- soDienThoai VN: ^0\\d{9}$
- email: regex mail co ban
- ngay: dd/MM/yyyy
- soNguyenKhongAm: >= 0
- soNguyenDuong: > 0
- soThucDuong: > 0
- cccd: dung 12 chu so
- phanTram: [0, 100]

## 5) Rang buoc domain trong cac entity (tong hop)

Da quet tat ca file src/entity/*.java, cac quy tac chinh:

1. Khong cho de trong cac ma/ten:
- ma hoa don, ma san pham, ma lo, ma thue, ma khuyen mai, ma nhan vien, ma khach hang, ma tai khoan...

2. So luong/gia tri tien khong am:
- ChiTietHoaDon: soLuong, donGia, giaGoc >= 0
- ChiTietPhieuNhap: soLuong, giaNhap >= 0
- HoaDon: tongTien, tienHang, tienThue, tienGiamGia, diemSuDung, thanhTien >= 0
- LoSanPham: soLuong >= 0
- Thue: phanTramThue >= 0

3. So dien thoai/email/CCCD:
- KhachHang: SDT 10 so, email hop le neu co
- NhanVien: SDT 10 so, CCCD 12 so, email hop le neu co
- NhaCungCap: SDT 9-12 so, email bat buoc hop le

4. Han su dung san pham:
- hanSuDung (thang) phai la so nguyen duong > 0 neu co gia tri

5. Khuyen mai:
- phanTramGG >= 0
- NgayKetThuc khong duoc truoc NgayBatDau

## 6) Quy tac sinh ma tu dong

Nguon chinh:
- dao/HoaDon_DAO.java
- dao/LoSanPham_DAO.java
- dao/NhanVien_DAO.java
- dao/KhuyenMai_DAO.java
- dao/Thue_DAO.java

Mau chung:
- PREFIX + nam hien tai + so thu tu tang dan
- Thuong dung truy van SELECT MAX(...) voi LIKE theo pattern nam hien tai

## 7) Cac diem can luu y khi bao tri

1. Nguong "sap het" dang khong dong nhat:
- 50 (SanPham_Service)
- 20 (ChatDataContext)
- Nen dua ve 1 hang so chung trong constants.

2. Du lieu API key dang hardcode trong constants/ApiConfig.java:
- Nen chuyen sang bien moi truong hoac file cau hinh local khong commit.

3. Quy doi diem hien tai:
- Comment ghi 1 diem = 1.000d nhung VND_PER_POINT_USE = 10.
- Can chot nghiep vu va dong bo comment + code.

## 8) Danh sach file da doi chieu chinh

- src/service/LoSanPham_Service.java
- src/service/SanPham_Service.java
- src/dao/LoSanPham_DAO.java
- src/service/HoaDon_Service.java
- src/service/DoiHang_Service.java
- src/service/TraHang_Service.java
- src/service/TaiKhoan_Service.java
- src/gui/DatTruoc_GUI.java
- src/util/Validators.java
- src/entity/*.java (quet tong hop rang buoc)
- src/exception/ProductTableRenderer.java
- src/constants/Formats.java
- src/constants/ApiConfig.java
