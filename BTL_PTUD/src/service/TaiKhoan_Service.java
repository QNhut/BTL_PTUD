package service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dao.TaiKhoan_DAO;
import entity.PhienDangNhap;
import entity.TaiKhoan;

public class TaiKhoan_Service {
	private static final long IDLE_TIMEOUT_MILLIS_DEFAULT = 3 * 60 * 60 * 1000;
	private static final long MAX_SESSION_MILLIS_DEFAULT = 24 * 60 * 60 * 1000;
	private static final Path SESSION_FILE = Paths.get(System.getProperty("user.home"), ".happyhealth",
			"session.properties");

	private final TaiKhoan_DAO taiKhoanDAO;
	private final Map<String, PhienDangNhap> sessionsByToken;
	private final Map<String, String> tokenByUsername;
	private final long idleTimeoutMillis;
	private final long maxSessionMillis;

	public TaiKhoan_Service() {
		this(IDLE_TIMEOUT_MILLIS_DEFAULT, MAX_SESSION_MILLIS_DEFAULT);
	}

	public TaiKhoan_Service(long idleTimeoutMillis, long maxSessionMillis) {
		if (idleTimeoutMillis <= 0 || maxSessionMillis <= 0 || maxSessionMillis < idleTimeoutMillis) {
			throw new IllegalArgumentException("Cau hinh session khong hop le");
		}
		this.taiKhoanDAO = new TaiKhoan_DAO();
		this.sessionsByToken = new ConcurrentHashMap<String, PhienDangNhap>();
		this.tokenByUsername = new ConcurrentHashMap<String, String>();
		this.idleTimeoutMillis = idleTimeoutMillis;
		this.maxSessionMillis = maxSessionMillis;
		khoiPhucPhienTuFile();
	}

	public String dangNhap(String tenDangNhap, String matKhau) {
		return dangNhap(tenDangNhap, matKhau, idleTimeoutMillis, maxSessionMillis);
	}

	public String dangNhap(String tenDangNhap, String matKhau, long ttlMillis) {
		return dangNhap(tenDangNhap, matKhau, ttlMillis, maxSessionMillis);
	}

	public String dangNhap(String tenDangNhap, String matKhau, long idleTimeoutMillis, long maxSessionMillis) {
		if (idleTimeoutMillis <= 0 || maxSessionMillis <= 0 || maxSessionMillis < idleTimeoutMillis) {
			throw new IllegalArgumentException("Cau hinh timeout khong hop le");
		}

		donDepTokenHetHan();
		TaiKhoan taiKhoan = taiKhoanDAO.login(tenDangNhap, matKhau);
		if (taiKhoan == null) {
			return null;
		}

		if (!taiKhoanDAO.capNhatTrangThai(taiKhoan.getTenDangNhap(), true)) {
			return null;
		}

		String oldToken = tokenByUsername.remove(taiKhoan.getTenDangNhap());
		if (oldToken != null) {
			sessionsByToken.remove(oldToken);
		}

		Instant now = Instant.now();
		Instant absoluteExpiresAt = now.plusMillis(maxSessionMillis);
		String token = UUID.randomUUID().toString();
		PhienDangNhap phien = new PhienDangNhap(token, taiKhoan, now, now, absoluteExpiresAt);

		sessionsByToken.put(token, phien);
		tokenByUsername.put(taiKhoan.getTenDangNhap(), token);
		luuPhienVaoFile(phien);
		return token;
	}

	public TaiKhoan layTaiKhoanTheoToken(String token) {
		PhienDangNhap phien = sessionsByToken.get(token);
		if (phien == null) {
			return null;
		}
		if (phien.daHetHanToiDa() || phien.daHetHanKhongHoatDong(idleTimeoutMillis)) {
			xoaPhien(token, phien.getTaiKhoan().getTenDangNhap());
			return null;
		}
		phien.capNhatTruyCap();
		luuPhienVaoFile(phien);
		return phien.getTaiKhoan();
	}

	public boolean tokenHopLe(String token) {
		return layTaiKhoanTheoToken(token) != null;
	}

	public boolean giaHanToken(String token, long ttlMillis) {
		if (ttlMillis <= 0) {
			return false;
		}
		PhienDangNhap phien = sessionsByToken.get(token);
		if (phien == null || phien.daHetHanToiDa() || phien.daHetHanKhongHoatDong(idleTimeoutMillis)) {
			if (phien != null) {
				xoaPhien(token, phien.getTaiKhoan().getTenDangNhap());
			}
			return false;
		}
		phien.capNhatTruyCap();
		return true;
	}

	public boolean dangXuat(String token) {
		PhienDangNhap phien = sessionsByToken.get(token);
		if (phien == null) {
			return false;
		}
		return xoaPhien(token, phien.getTaiKhoan().getTenDangNhap());
	}

	public int donDepTokenHetHan() {
		int removed = 0;
		for (Map.Entry<String, PhienDangNhap> entry : sessionsByToken.entrySet()) {
			String token = entry.getKey();
			PhienDangNhap phien = entry.getValue();
			if ((phien.daHetHanToiDa() || phien.daHetHanKhongHoatDong(idleTimeoutMillis))
					&& xoaPhien(token, phien.getTaiKhoan().getTenDangNhap())) {
				removed++;
			}
		}
		return removed;
	}

	private boolean xoaPhien(String token, String tenDangNhap) {
		sessionsByToken.remove(token);
		tokenByUsername.remove(tenDangNhap, token);
		xoaFilePhienDaLuu();
		return taiKhoanDAO.capNhatTrangThai(tenDangNhap, false);
	}

	public String layTokenDaLuu() {
		for (String token : sessionsByToken.keySet()) {
			return token;
		}
		return null;
	}

	private void khoiPhucPhienTuFile() {
		if (!Files.exists(SESSION_FILE)) {
			return;
		}

		Properties properties = new Properties();
		try (InputStream inputStream = Files.newInputStream(SESSION_FILE)) {
			properties.load(inputStream);
			String token = properties.getProperty("token");
			String tenDangNhap = properties.getProperty("username");
			String createdAtText = properties.getProperty("createdAt");
			String lastAccessAtText = properties.getProperty("lastAccessAt");
			String absoluteExpiresAtText = properties.getProperty("absoluteExpiresAt");

			if (token == null || tenDangNhap == null || createdAtText == null || lastAccessAtText == null
					|| absoluteExpiresAtText == null) {
				xoaFilePhienDaLuu();
				return;
			}

			long createdAtEpoch = Long.parseLong(createdAtText);
			long lastAccessAtEpoch = Long.parseLong(lastAccessAtText);
			long absoluteExpiresAtEpoch = Long.parseLong(absoluteExpiresAtText);

			TaiKhoan taiKhoan = taiKhoanDAO.layTaiKhoanTheoTenDangNhap(tenDangNhap);
			if (taiKhoan == null) {
				xoaFilePhienDaLuu();
				return;
			}

			PhienDangNhap phien = new PhienDangNhap(token, taiKhoan, Instant.ofEpochMilli(createdAtEpoch),
					Instant.ofEpochMilli(lastAccessAtEpoch), Instant.ofEpochMilli(absoluteExpiresAtEpoch));

			if (phien.daHetHanToiDa() || phien.daHetHanKhongHoatDong(idleTimeoutMillis)) {
				taiKhoanDAO.capNhatTrangThai(tenDangNhap, false);
				xoaFilePhienDaLuu();
				return;
			}

			sessionsByToken.put(token, phien);
			tokenByUsername.put(tenDangNhap, token);
			taiKhoanDAO.capNhatTrangThai(tenDangNhap, true);
		} catch (IOException | NumberFormatException e) {
			xoaFilePhienDaLuu();
		}
	}

	private void luuPhienVaoFile(PhienDangNhap phien) {
		if (phien == null || phien.getTaiKhoan() == null) {
			return;
		}
		Properties properties = new Properties();
		properties.setProperty("token", phien.getToken());
		properties.setProperty("username", phien.getTaiKhoan().getTenDangNhap());
		properties.setProperty("createdAt", String.valueOf(phien.getThoiDiemTao().toEpochMilli()));
		properties.setProperty("lastAccessAt", String.valueOf(phien.getThoiDiemTruyCapCuoi().toEpochMilli()));
		properties.setProperty("absoluteExpiresAt", String.valueOf(phien.getThoiDiemHetHanToiDa().toEpochMilli()));

		try {
			Files.createDirectories(SESSION_FILE.getParent());
			try (OutputStream outputStream = Files.newOutputStream(SESSION_FILE)) {
				properties.store(outputStream, "Happy Health login session");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void xoaFilePhienDaLuu() {
		try {
			Files.deleteIfExists(SESSION_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}