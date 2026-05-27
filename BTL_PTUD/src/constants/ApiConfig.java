package constants;

/**
 * =====================================================================
 *  CẤU HÌNH AI PROVIDER
 * =====================================================================
 *
 *  Đổi PROVIDER để chọn AI:
 *    "GEMINI" → Google Gemini  (free: 15 req/phút)
 *    "GROQ"   → Groq API       (free: 30 req/phút, NHANH HƠN, ít lỗi quota)
 *
 *  LẤY GROQ API KEY MIỄN PHÍ (khuyến nghị dùng Groq thay Gemini):
 *  BƯỚC 1 – Truy cập  https://console.groq.com/
 *  BƯỚC 2 – Đăng ký / Đăng nhập bằng tài khoản Google
 *  BƯỚC 3 – Vào API Keys → Create API Key → Copy key
 *  BƯỚC 4 – Dán vào GROQ_API_KEY bên dưới và đổi PROVIDER = "GROQ"
 *
 *  LẤY GEMINI API KEY:
 *  BƯỚC 1 – Truy cập  https://aistudio.google.com/
 *  BƯỚC 2 – Get API key → Create API key → Copy key
 *  BƯỚC 3 – Dán vào GEMINI_API_KEY và đổi PROVIDER = "GEMINI"
 * =====================================================================
 */
public final class ApiConfig {

    private ApiConfig() {}

    /** Chuyển đổi giữa hai provider: "GEMINI" hoặc "GROQ" */
    public static final String PROVIDER = "GROQ";

    // ===== GOOGLE GEMINI =====
    public static final String GEMINI_API_KEY = "";
    public static final String GEMINI_MODEL   = "gemini-2.0-flash";

    // ===== GROQ (https://console.groq.com/) =====
    /** Dán Groq API key vào đây. */
    public static final String GROQ_API_KEY = "";
    /** Model đề xuất: llama-3.3-70b-versatile (nhanh, miễn phí). */
    public static final String GROQ_MODEL   = "llama-3.3-70b-versatile";
}
