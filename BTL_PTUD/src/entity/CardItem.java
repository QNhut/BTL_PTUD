package entity;

public class CardItem {
	private SanPham sp;
	private int soLuong;
	
	public CardItem(SanPham sp, int soLuong) {
		this.sp = sp;
		this.soLuong = soLuong;
	}

	public CardItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SanPham getSp() {
		return sp;
	}

	public void setSp(SanPham sp) {
		this.sp = sp;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
	
	
}