package entity;

import java.time.Instant;
import java.util.Objects;

public class PhienDangNhap {
	private String token;
	private TaiKhoan taiKhoan;
	private Instant thoiDiemTao;
	private Instant thoiDiemTruyCapCuoi;
	private Instant thoiDiemHetHanToiDa;

	public PhienDangNhap(String token, TaiKhoan taiKhoan, Instant thoiDiemTao, Instant thoiDiemTruyCapCuoi,
			Instant thoiDiemHetHanToiDa) {
		this.token = token;
		this.taiKhoan = taiKhoan;
		this.thoiDiemTao = thoiDiemTao;
		this.thoiDiemTruyCapCuoi = thoiDiemTruyCapCuoi;
		this.thoiDiemHetHanToiDa = thoiDiemHetHanToiDa;
	}

	public String getToken() {
		return token;
	}

	public TaiKhoan getTaiKhoan() {
		return taiKhoan;
	}

	public Instant getThoiDiemTao() {
		return thoiDiemTao;
	}

	public Instant getThoiDiemTruyCapCuoi() {
		return thoiDiemTruyCapCuoi;
	}

	public void capNhatTruyCap() {
		this.thoiDiemTruyCapCuoi = Instant.now();
	}

	public Instant getThoiDiemHetHanToiDa() {
		return thoiDiemHetHanToiDa;
	}

	public boolean daHetHanKhongHoatDong(long idleTimeoutMillis) {
		if (idleTimeoutMillis <= 0 || thoiDiemTruyCapCuoi == null) {
			return true;
		}
		Instant idleDeadline = thoiDiemTruyCapCuoi.plusMillis(idleTimeoutMillis);
		return Instant.now().isAfter(idleDeadline);
	}

	public boolean daHetHanToiDa() {
		return thoiDiemHetHanToiDa == null || Instant.now().isAfter(thoiDiemHetHanToiDa);
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhienDangNhap other = (PhienDangNhap) obj;
		return Objects.equals(token, other.token);
	}
}
