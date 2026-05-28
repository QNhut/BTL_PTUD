# BTL_PTUD — Phần mềm Quản lý Bán hàng

Ứng dụng desktop Java Swing quản lý bán hàng: hóa đơn, kho hàng, khách hàng, nhân viên, thống kê doanh thu và trợ lý AI tư vấn.

---

## Yêu cầu hệ thống

| Thành phần | Phiên bản tối thiểu |
|---|---|
| Java JDK | 11 trở lên |
| SQL Server | 2017 trở lên (hoặc SQL Server Express miễn phí) |
| IDE | Eclipse 2022+ hoặc IntelliJ IDEA 2022+ |
| RAM | 4 GB trở lên |
| HĐH | Windows 10/11, macOS 12+, Ubuntu 20.04+ |

---

## 1. Tải dự án về máy

### Cách 1 — Tải file ZIP (không cần Git)

1. Vào **https://github.com/QNhut/BTL_PTUD**
2. Nhấn nút **Code** (màu xanh) → **Download ZIP**
3. Giải nén ra thư mục bất kỳ, ví dụ `D:\BTL_PTUD`

### Cách 2 — Clone bằng Git

```bash
git clone https://github.com/QNhut/BTL_PTUD.git
cd BTL_PTUD
```

Sau khi tải về, thư mục dự án có cấu trúc:

```
BTL_PTUD/
├── BTL_PTUD/           ← thư mục project chính
│   ├── src/            ← mã nguồn Java
│   ├── lib/            ← thư viện JAR
│   ├── data/
│   │   ├── sql/        ← file tạo CSDL
│   │   └── img/        ← ảnh giao diện
│   └── bin/            ← file .class sau khi biên dịch
└── README.md
```

---

## 2. Cài đặt SQL Server

### Windows — SQL Server Express (miễn phí)

1. Tải tại: **https://www.microsoft.com/en-us/sql-server/sql-server-downloads** → chọn **Express**
2. Cài đặt với tùy chọn **Mixed Mode Authentication**
3. Trong quá trình cài, đặt mật khẩu cho tài khoản **sa** là:
   ```
   YourStrong!Passw0rd
   ```
4. Đảm bảo **SQL Server Browser** và **SQL Server (SQLEXPRESS)** đang chạy trong Services
5. Bật **TCP/IP** trong SQL Server Configuration Manager → SQL Server Network Configuration → Protocols → TCP/IP → Enable

### macOS / Linux — Docker (nhanh nhất)

```bash
docker run -e "ACCEPT_EULA=Y" \
           -e "SA_PASSWORD=YourStrong!Passw0rd" \
           -p 1433:1433 \
           --name sqlserver \
           -d mcr.microsoft.com/mssql/server:2022-latest
```

---

## 3. Tạo cơ sở dữ liệu

1. Mở **SQL Server Management Studio (SSMS)** hoặc **Azure Data Studio**
2. Kết nối với: `localhost,1433` | tài khoản `sa` | mật khẩu `YourStrong!Passw0rd`
3. Mở file `BTL_PTUD/data/sql/QLBanHang_HTT.sql`
4. Nhấn **Execute (F5)** để chạy toàn bộ script — script tự tạo database `QLBanHang` và chèn dữ liệu mẫu

> **Lưu ý:** Nếu muốn thêm ~500 hóa đơn mẫu, chạy thêm file `data/sql/them_500_hoadon.sql`

---

## 4. Cấu hình kết nối database

Mở file `BTL_PTUD/src/ConnectDB/ConnectDB.java`, kiểm tra dòng:

```java
String url = "jdbc:sqlserver://localhost:1433;databaseName=QLBanHang";
String user = "sa";
String password = "YourStrong!Passw0rd";
```

Nếu bạn dùng instance name khác hoặc mật khẩu khác, sửa tại đây rồi biên dịch lại.

---

## 5. Mở dự án trong IDE

### Eclipse

1. **File → Import → Existing Projects into Workspace**
2. Chọn thư mục `BTL_PTUD/BTL_PTUD` → **Finish**
3. Click phải project → **Build Path → Configure Build Path → Libraries → Add JARs**
4. Thêm toàn bộ file `.jar` trong thư mục `lib/`
5. Click phải vào `src/gui/Main_GUI.java` → **Run As → Java Application**

### IntelliJ IDEA

1. **File → Open** → chọn thư mục `BTL_PTUD/BTL_PTUD`
2. **File → Project Structure → Libraries → + → Java**
3. Chọn thư mục `lib/` → OK
4. Mở `src/gui/Main_GUI.java` → nhấn nút **Run** (▶)

---

## 6. Cấu hình tính năng AI Chat (tuỳ chọn)

Tính năng trợ lý AI cần API key. Mở `BTL_PTUD/src/constants/ApiConfig.java`:

```java
// Chọn provider: "GROQ" hoặc "GEMINI"
public static final String PROVIDER = "GROQ";

// Lấy Groq key miễn phí tại https://console.groq.com/keys
public static final String GROQ_API_KEY = "";   // ← dán key vào đây

// Lấy Gemini key tại https://aistudio.google.com/apikey
public static final String GEMINI_API_KEY = ""; // ← hoặc dán key vào đây
```

> ⚠️ **Không commit API key lên GitHub.** Thêm `ApiConfig.java` vào `.gitignore` hoặc chỉ để key trên máy local.

---

## 7. Tài khoản đăng nhập mặc định

| Vai trò | Tên đăng nhập | Mật khẩu |
|---|---|---|
| Admin | `admin` | `123456` |
| Quản lý | `manager` | `123456` |
| Nhân viên | `nhanvien` | `123456` |

---

## 8. Xử lý lỗi thường gặp

| Lỗi | Nguyên nhân | Cách sửa |
|---|---|---|
| `Connection refused` | SQL Server chưa chạy hoặc sai port | Kiểm tra SQL Server service và TCP/IP port 1433 |
| `Login failed for user 'sa'` | Sai mật khẩu hoặc chưa bật Mixed Mode | Đặt lại mật khẩu sa trong SSMS |
| `Cannot find main class` | Chưa add thư viện JAR | Thêm toàn bộ JAR trong `lib/` vào Build Path |
| Màn hình trắng khi chạy | Thiếu thư mục `data/img/` | Đảm bảo chạy từ thư mục gốc `BTL_PTUD/BTL_PTUD` |
| AI chat không phản hồi | Chưa điền API key | Điền key vào `ApiConfig.java` theo bước 6 |
