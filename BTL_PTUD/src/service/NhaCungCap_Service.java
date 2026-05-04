package service;

import java.util.ArrayList;
import java.util.List;

import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

public class NhaCungCap_Service {
	private final NhaCungCap_DAO nhaCungCapDAO = new NhaCungCap_DAO();

	public List<String> layDanhSachTenNhaCungCap() {
		List<String> tenNhaCungCaps = new ArrayList<String>();
		for (NhaCungCap nhaCungCap : nhaCungCapDAO.getDSNhaCungCap()) {
			if (nhaCungCap == null || nhaCungCap.getTenNhaCungCap() == null) {
				continue;
			}

			String tenNhaCungCap = nhaCungCap.getTenNhaCungCap().trim();
			if (tenNhaCungCap.isEmpty() || tenNhaCungCaps.contains(tenNhaCungCap)) {
				continue;
			}
			tenNhaCungCaps.add(tenNhaCungCap);
		}
		return tenNhaCungCaps;
	}

	public NhaCungCap layNhaCungCapTheoTen(String ten) {
		if (ten == null || ten.trim().isEmpty()) return null;
		String canTim = ten.trim();
		for (NhaCungCap ncc : nhaCungCapDAO.getDSNhaCungCap()) {
			if (ncc.getTenNhaCungCap() != null && ncc.getTenNhaCungCap().trim().equalsIgnoreCase(canTim)) {
				return ncc;
			}
		}
		return null;
	}
}