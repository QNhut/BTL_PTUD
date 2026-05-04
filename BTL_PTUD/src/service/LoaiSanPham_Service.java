package service;

import java.util.List;

import dao.LoaiSanPham_DAO;
import entity.LoaiSanPham;

public class LoaiSanPham_Service {
	private final LoaiSanPham_DAO loaiSanPhamDAO;

	public LoaiSanPham_Service() {
		this.loaiSanPhamDAO = new LoaiSanPham_DAO();
	}

	/**
	 * Lấy toàn bộ danh sách loại sản phẩm
	 */
	public List<LoaiSanPham> layDanhSachLoaiSanPham() {
		return loaiSanPhamDAO.getDSLoaiSanPham();
	}

	/**
	 * Lấy loại sản phẩm theo mã
	 */
	public LoaiSanPham layLoaiSanPhamTheoMa(String maLoaiSP) {
		return loaiSanPhamDAO.layLoaiSPTheoMaLSP(maLoaiSP);
	}

	/**
	 * Lấy tên loại sản phẩm theo mã
	 */
	public String layTenLoaiSanPham(String maLoaiSP) {
		return loaiSanPhamDAO.layTenLoaiSP(maLoaiSP);
	}
}
