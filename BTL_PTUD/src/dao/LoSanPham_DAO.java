package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.KeSanPham;
import entity.LoSanPham;
import entity.PhieuNhap;
import entity.SanPham;

public class LoSanPham_DAO {
    private Connection con;

    public ArrayList<LoSanPham> getDSLoSanPham() {
        ArrayList<LoSanPham> ds = new ArrayList<LoSanPham>();
        String sql = "SELECT MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai FROM LoSanPham ORDER BY MaLoSanPham";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    LoSanPham lo = new LoSanPham();
                    lo.setMaLoSanPham(rs.getString("MaLoSanPham"));
                    lo.setSanPham(new SanPham(rs.getString("MaSanPham")));
                    lo.setPhieuNhap(new PhieuNhap(rs.getString("MaPhieuNhap")));
                    lo.setKeSanPham(new KeSanPham(rs.getString("MaKeSanPham")));
                    lo.setSoLuong(rs.getInt("SoLuong"));
                    lo.setDonViTinh(rs.getString("DonViTinh"));
                    Date hsd = rs.getDate("HanSuDung");
                    lo.setHanSuDung(hsd == null ? null : hsd.toLocalDate());
                    lo.setTrangThai(rs.getBoolean("TrangThai"));
                    ds.add(lo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean them(LoSanPham lo) {
        String sql = "INSERT INTO LoSanPham (MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, lo.getMaLoSanPham());
                ps.setString(2, lo.getSanPham().getMaSanPham());
                ps.setString(3, lo.getPhieuNhap().getMaPhieuNhap());
                ps.setString(4, lo.getKeSanPham().getMaKeSanPham());
                ps.setInt(5, lo.getSoLuong());
                ps.setString(6, lo.getDonViTinh());
                if (lo.getHanSuDung() == null) {
                    ps.setDate(7, null);
                } else {
                    ps.setDate(7, Date.valueOf(lo.getHanSuDung()));
                }
                ps.setBoolean(8, lo.isTrangThai());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
