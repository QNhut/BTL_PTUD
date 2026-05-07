# Hướng dẫn sử dụng `StyledTable`

`StyledTable` là component bảng dựng sẵn, kế thừa `JScrollPane`, có thể gắn vào bất kỳ panel nào.  
Mọi entity (NhanVien, KhachHang, SanPham...) đều dùng chung một class này.

---

## Mục lục

1. [Khởi tạo](#1-khởi-tạo)
2. [Cấu hình cơ bản](#2-cấu-hình-cơ-bản)
3. [Các loại cột dựng sẵn](#3-các-loại-cột-dựng-sẵn)
4. [Cột thao tác (Chi tiết / Xóa)](#4-cột-thao-tác-chi-tiết--xóa)
5. [Cập nhật dữ liệu](#5-cập-nhật-dữ-liệu)
6. [Ví dụ đầy đủ](#6-ví-dụ-đầy-đủ)
7. [Cấu trúc bên trong](#7-cấu-trúc-bên-trong)

---

## 1. Khởi tạo

```java
// Khai báo tên cột — chuỗi rỗng "" cho cột nút thao tác
String[] cols = {"Nhân viên", "Chức vụ", "Liên hệ", "Trạng thái", "", ""};

// list là ArrayList<NhanVien> (hoặc bất kỳ entity nào)
ArrayList<NhanVien> list = new ArrayList<>();

StyledTable table = new StyledTable(cols, list);
panel.add(table); // thêm vào panel như JScrollPane thông thường
```

> **Lưu ý:** `list` được truyền theo tham chiếu. Mọi thay đổi trên `list` sẽ được phản ánh sau khi gọi `table.refresh()`.

---

## 2. Cấu hình cơ bản

| Phương thức | Công dụng |
|---|---|
| `getTable()` | Lấy `JTable` gốc để tùy biến thêm (sorter, listener...) |
| `setRowHeight(int height)` | Đặt chiều cao dòng (mặc định 75px) |
| `setColumnWidth(int col, int width)` | Đặt độ rộng ưu tiên cho cột |
| `setColumnRenderer(int col, renderer)` | Gán renderer tùy chỉnh bất kỳ |
| `refresh()` | Vẽ lại bảng sau khi `list` thay đổi |

---

## 3. Các loại cột dựng sẵn

### 3.1 Cột Avatar + 2 dòng text

```java
table.setAvatarColumn(
    0,    // chỉ số cột
    220,  // độ rộng (px)
    v -> ((NhanVien) v).getTenNhanVien(),  // dòng 1: tên in đậm
    v -> ((NhanVien) v).getMaNhanVien()   // dòng 2: mã nhỏ hơn
);
```

Hiển thị: hình tròn màu + chữ viết tắt bên trái, tên + mã bên phải.

---

### 3.2 Cột 2 dòng text

```java
table.setTwoLineColumn(
    1, 180,
    v -> ((NhanVien) v).getChucVu().getTenChucVu(),  // dòng 1 in đậm
    v -> ((NhanVien) v).getEmail()                   // dòng 2 nhỏ
);
```

---

### 3.3 Cột 2 dòng có icon đầu dòng

```java
table.setIconTwoLineColumn(
    2, 220,
    "\u2709", v -> ((NhanVien) v).getEmail(),      // ✉ + email
    "\u260E", v -> ((NhanVien) v).getSoDienThoai() // ☎ + sđt
);
```

Dùng unicode hoặc emoji cho tham số icon.

---

### 3.4 Cột 1 dòng chữ

```java
table.setSingleTextColumn(
    3, 100,
    v -> ((NhanVien) v).isGioiTinh() ? "Nam" : "Nữ"
);
```

---

### 3.5 Cột Badge trạng thái

```java
table.setBadgeColumn(
    4, 130,
    v -> ((NhanVien) v).isTrangThai(), // true = đang hoạt động
    "Đang làm",   // nhãn khi active  → nền xanh
    "Nghỉ việc"   // nhãn khi inactive → nền xám
);
```

---

## 4. Cột thao tác (Chi tiết / Xóa)

### 4.1 Cột "Chi tiết"

```java
// Bước 1: gán renderer (nút xám "Chi tiết")
table.setActionColumn(5, 80);

// Bước 2: đăng ký callback — chạy khi người dùng click
table.setActionColumnListener((row, obj) -> {
    NhanVien nv = (NhanVien) obj;
    // mở dialog chi tiết...
});
```

### 4.2 Cột "Xóa"

```java
// Bước 1: gán renderer (nút đỏ "Xóa")
table.setDeleteButtonColumn(6, 80);

// Bước 2: đăng ký callback
table.setDeleteColumnListener((row, obj) -> {
    NhanVien nv = (NhanVien) obj;
    int ok = JOptionPane.showConfirmDialog(null,
        "Xóa nhân viên \"" + nv.getTenNhanVien() + "\"?",
        "Xác nhận", JOptionPane.YES_NO_OPTION);
    if (ok == JOptionPane.YES_OPTION) {
        service.xoaNhanVien(nv.getMaNhanVien()); // xóa DB
        list.remove(row.intValue());             // xóa list
        table.refresh();                         // vẽ lại bảng
    }
});
```

> **Cơ chế hoạt động:** Cả 2 cột dùng chung **1 MouseListener** đặt trong `installTableClickListener()`.  
> Khi click, listener kiểm tra chỉ số cột và điều phối đến đúng callback.  
> Renderer chỉ có nhiệm vụ **vẽ** — không có ActionListener thật.

---

## 5. Cập nhật dữ liệu

```java
// Thêm mới
list.add(nvMoi);
table.refresh();

// Xóa theo index
list.remove(rowIndex);
table.refresh();

// Tải lại toàn bộ
list.clear();
list.addAll(service.getDanhSach());
table.refresh();
```

---

## 6. Ví dụ đầy đủ

```java
// --- Khai báo ---
private ArrayList<NhanVien> list = new ArrayList<>();
private StyledTable tblNhanVien;
private static final String[] COLS = {
    "Nhân viên", "Chức vụ", "Liên hệ", "Giới tính", "Trạng thái", "", ""
};

// --- Khởi tạo trong constructor/initComponents ---
tblNhanVien = new StyledTable(COLS, list);

tblNhanVien.setAvatarColumn(0, 220,
    v -> ((NhanVien) v).getTenNhanVien(),
    v -> ((NhanVien) v).getMaNhanVien());

tblNhanVien.setTwoLineColumn(1, 180,
    v -> ((NhanVien) v).getChucVu().getTenChucVu(),
    v -> ((NhanVien) v).getEmail());

tblNhanVien.setIconTwoLineColumn(2, 220,
    "\u2709", v -> ((NhanVien) v).getEmail(),
    "\u260E", v -> ((NhanVien) v).getSoDienThoai());

tblNhanVien.setSingleTextColumn(3, 100,
    v -> ((NhanVien) v).isGioiTinh() ? "Nam" : "Nữ");

tblNhanVien.setBadgeColumn(4, 130,
    v -> true, "Đang làm", "Nghỉ việc");

tblNhanVien.setActionColumn(5, 80);
tblNhanVien.setDeleteButtonColumn(6, 80);

tblNhanVien.setActionColumnListener((row, obj) -> {
    moDialogChiTiet((NhanVien) obj);
});

tblNhanVien.setDeleteColumnListener((row, obj) -> {
    NhanVien nv = (NhanVien) obj;
    int ok = JOptionPane.showConfirmDialog(null,
        "Xóa \"" + nv.getTenNhanVien() + "\"?", "Xóa",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (ok == JOptionPane.YES_OPTION) {
        service.xoaNhanVien(nv.getMaNhanVien());
        list.remove(row.intValue());
        tblNhanVien.refresh();
    }
});

panel.add(tblNhanVien);

// --- Nạp dữ liệu ---
list.addAll(service.getDSNhanVien());
tblNhanVien.refresh();
```

---

## 7. Cấu trúc bên trong

```
StyledTable (extends JScrollPane)
│
├── 1. Fields            — columnNames, data, model, index cột, listener, cờ
├── 2. Constructor       — nhận cols + data, gọi initTable()
├── 3. Khởi tạo nội bộ
│   ├── initTable()              — tạo JTable + model, gọi styleHeader + installMouseListener
│   ├── styleHeader()            — font/màu header
│   └── installTableClickListener() — 1 MouseListener duy nhất, điều phối click
│
├── 4. API Cơ bản        — getTable, setRowHeight, setColumnWidth, setColumnRenderer, refresh
├── 5. API Cột dựng sẵn — setAvatarColumn, setTwoLineColumn, setIconTwoLineColumn,
│                          setSingleTextColumn, setBadgeColumn, setActionColumn, setDeleteButtonColumn
├── 6. API Sự kiện       — setActionColumnListener, setDeleteColumnListener
├── 7. Tiện ích nội bộ  — AVATAR_COLORS, getInitials(), getAvatarColor()
└── 8. Renderer nội bộ
    ├── AvatarTwoLineRenderer  — avatar tròn + 2 dòng
    ├── TwoLineRenderer        — 2 dòng chữ
    ├── IconTwoLineRenderer    — 2 dòng có icon
    ├── SingleTextRenderer     — 1 dòng chữ
    ├── BadgeRenderer          — badge trạng thái
    ├── ActionDotsRenderer     — nút "Chi tiết"
    └── DeleteButtonRenderer   — nút "Xóa"
```
