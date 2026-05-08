package service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 * Cache ảnh sản phẩm — chỉ load + scale 1 lần, dùng lại mãi.
 * 
 * Cách dùng: ImageCache.getInstance().getImage("abc.png", 180, 110, icon -> {
 * label.setIcon(icon); label.repaint(); });
 */
public class ImageCache {

	private static final ImageCache INSTANCE = new ImageCache();
	private static final String IMG_DIR = "data/img/image_san_pham/";

	// Cache key = "filename_WxH" → scaled ImageIcon
	private final Map<String, ImageIcon> cache = new ConcurrentHashMap<>();

	// Thread pool để load ảnh background (2 threads đủ rồi)
	private final ExecutorService executor = Executors.newFixedThreadPool(2);

	private ImageCache() {
	}

	public static ImageCache getInstance() {
		return INSTANCE;
	}

	/**
	 * Lấy ảnh từ cache (sync). Trả null nếu chưa load.
	 */
	public ImageIcon getCached(String fileName, int w, int h) {
		if (fileName == null || fileName.trim().isEmpty())
			return null;
		return cache.get(cacheKey(fileName, w, h));
	}

	/**
	 * Lấy ảnh — nếu đã cache trả ngay, chưa thì load background rồi callback.
	 * 
	 * @param fileName tên file ảnh (VD: "sp001.png")
	 * @param w        chiều rộng cần scale
	 * @param h        chiều cao cần scale
	 * @param onLoaded callback trên EDT khi ảnh sẵn sàng (có thể null nếu chỉ cần
	 *                 preload)
	 * @return ImageIcon nếu đã cache, null nếu đang load
	 */
	public ImageIcon getImage(String fileName, int w, int h, Consumer<ImageIcon> onLoaded) {
		if (fileName == null || fileName.trim().isEmpty())
			return null;

		String key = cacheKey(fileName, w, h);

		// Đã cache → trả ngay
		ImageIcon cached = cache.get(key);
		if (cached != null)
			return cached;

		// Chưa cache → load background
		executor.submit(() -> {
			try {
				File file = new File(IMG_DIR + fileName);
				if (!file.exists())
					return;

				BufferedImage raw = ImageIO.read(file);
				if (raw == null)
					return;

				Image scaled = raw.getScaledInstance(w, h, Image.SCALE_SMOOTH);
				ImageIcon icon = new ImageIcon(scaled);
				cache.put(key, icon);

				// Callback trên EDT
				if (onLoaded != null) {
					SwingUtilities.invokeLater(() -> onLoaded.accept(icon));
				}
			} catch (Exception e) {
				// Bỏ qua — ảnh lỗi thì giữ placeholder
			}
		});
		return null; // đang load
	}

	/**
	 * Preload tất cả ảnh (gọi 1 lần lúc khởi động).
	 */
	public void preload(java.util.List<String> fileNames, int w, int h) {
		for (String fn : fileNames) {
			getImage(fn, w, h, null);
		}
	}

	/**
	 * Xóa cache (khi cần reload).
	 */
	public void clearCache() {
		cache.clear();
	}

	/** Xóa cache cho 1 file cụ thể (sau khi upload ảnh mới). */
	public void clearCacheByFileName(String fileName) {
		if (fileName == null) return;
		cache.entrySet().removeIf(e -> e.getKey().startsWith(fileName.trim() + "_"));
	}

	public int cacheSize() {
		return cache.size();
	}

	private String cacheKey(String fileName, int w, int h) {
		return fileName.trim() + "_" + w + "x" + h;
	}
}
