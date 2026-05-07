package service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Dịch vụ upload ảnh sản phẩm.
 *
 * Luồng sử dụng:
 *   1. Người dùng chọn file → chuanBiUpload(file) → trả về tên file tạm
 *   2. Nếu xác nhận lưu   → xacNhanLuu(tenFileTam) → copy vào data/img/image_san_pham/
 *   3. Nếu huỷ            → huyUpload(tenFileTam)   → xoá file tạm
 */
public class ImageUpload_Service {

    private static final String IMG_DIR    = "data/img/image_san_pham/";
    private static final String TEMP_DIR   = "data/img/image_san_pham/temp/";
    private static final int    THUMB_W    = 400;
    private static final int    THUMB_H    = 400;

    private static final ImageUpload_Service INSTANCE = new ImageUpload_Service();
    private ImageUpload_Service() {
        new File(IMG_DIR).mkdirs();
        new File(TEMP_DIR).mkdirs();
    }

    public static ImageUpload_Service getInstance() { return INSTANCE; }

    /**
     * Sao chép file ảnh vào thư mục tạm, scale về THUMB_W × THUMB_H.
     * @param src File nguồn người dùng chọn
     * @return Tên file tạm (chưa lưu vĩnh viễn)
     * @throws IOException nếu không đọc/ghi được file
     */
    public String chuanBiUpload(File src) throws IOException {
        if (src == null || !src.exists())
            throw new IOException("File không tồn tại");

        String ext = layExtension(src.getName());
        String tenTam = "tmp_" + UUID.randomUUID().toString() + "." + ext;
        File dest = new File(TEMP_DIR + tenTam);

        // Scale và lưu
        BufferedImage raw = ImageIO.read(src);
        if (raw == null)
            throw new IOException("Không đọc được ảnh: " + src.getName());

        Image scaled = raw.getScaledInstance(THUMB_W, THUMB_H, Image.SCALE_SMOOTH);
        BufferedImage out = new BufferedImage(THUMB_W, THUMB_H, BufferedImage.TYPE_INT_RGB);
        out.getGraphics().drawImage(scaled, 0, 0, null);
        ImageIO.write(out, ext.equals("jpg") || ext.equals("jpeg") ? "JPEG" : "PNG", dest);

        return tenTam;
    }

    /**
     * Xác nhận lưu: di chuyển file tạm sang thư mục chính.
     * @param tenFileTam tên trả về từ chuanBiUpload()
     * @param tenFileDich tên file đích (thường = maSanPham + ".png")
     * @return tên file đích đã lưu
     */
    public String xacNhanLuu(String tenFileTam, String tenFileDich) throws IOException {
        File src  = new File(TEMP_DIR + tenFileTam);
        File dest = new File(IMG_DIR  + tenFileDich);
        if (!src.exists()) throw new IOException("File tạm không còn tồn tại");
        Files.move(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Xoá cache cũ để load lại
        ImageCache.getInstance().clearCacheByFileName(tenFileDich);
        return tenFileDich;
    }

    /**
     * Huỷ upload: xoá file tạm.
     */
    public void huyUpload(String tenFileTam) {
        if (tenFileTam == null) return;
        File f = new File(TEMP_DIR + tenFileTam);
        if (f.exists()) f.delete();
    }

    /**
     * Tải preview ảnh từ file tạm (hiển thị trước khi lưu).
     */
    public ImageIcon taiPreview(String tenFileTam, int w, int h) {
        if (tenFileTam == null) return null;
        try {
            File f = new File(TEMP_DIR + tenFileTam);
            if (!f.exists()) return null;
            BufferedImage img = ImageIO.read(f);
            if (img == null) return null;
            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }

    private String layExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0) return "png";
        return fileName.substring(dot + 1).toLowerCase();
    }
}
