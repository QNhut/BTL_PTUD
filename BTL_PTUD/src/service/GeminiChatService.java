package service;

import constants.ApiConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Gọi Google Gemini hoặc Groq API để tạo chatbot hỗ trợ người dùng.
 *
 * <p>Dùng java.net.http.HttpClient (Java 11+) — không cần thư viện bên ngoài.
 * Chọn provider qua {@link constants.ApiConfig#PROVIDER}: "GEMINI" hoặc "GROQ".
 */
public class GeminiChatService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
            + ApiConfig.GEMINI_MODEL
            + ":generateContent?key=";

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    /** Số lượt hội thoại tối đa giữ lại để tránh vượt token limit. */
    private static final int MAX_HISTORY = 20;

    /** System prompt mô tả ngữ cảnh của phần mềm POS. */
    private static final String SYSTEM_INSTRUCTION =
            "Bạn là trợ lý AI thông minh tích hợp trong phần mềm quản lý bán hàng (POS). "
            + "Hệ thống gồm các chức năng chính:\n"
            + "- Quản lý sản phẩm (thêm/sửa/xóa/tìm kiếm sản phẩm, phân loại)\n"
            + "- Bán hàng (tạo hóa đơn, chọn sản phẩm, thanh toán, in hóa đơn)\n"
            + "- Nhập hàng (tạo phiếu nhập từ nhà cung cấp, quản lý lô hàng)\n"
            + "- Quản lý khách hàng (thêm/sửa/tìm khách, lịch sử mua)\n"
            + "- Quản lý nhân viên & tài khoản đăng nhập\n"
            + "- Khuyến mãi (tạo/quản lý chương trình giảm giá)\n"
            + "- Thuế (cấu hình mức thuế)\n"
            + "- Thống kê (doanh thu, sản phẩm bán chạy, khách hàng thân thiết)\n"
            + "- Đổi/trả hàng\n\n"
            + "Đôi khi trong tin nhắn của người dùng có kèm theo DỮ LIỆU HỆ THỐNG "
            + "(được đánh dấu bằng [DỮ LIỆU HỆ THỐNG] ... [HẾT DỮ LIỆU]). "
            + "Hãy sử dụng dữ liệu đó để trả lời chính xác, nhưng KHÔNG nhắc đến "
            + "việc bạn nhận được dữ liệu từ hệ thống — hãy trả lời tự nhiên như "
            + "bạn đã biết thông tin đó.\n\n"
            + "Hãy trả lời ngắn gọn, thân thiện bằng tiếng Việt. "
            + "Nếu câu hỏi hoàn toàn ngoài phạm vi phần mềm, hãy lịch sự nhắc người dùng "
            + "rằng bạn chuyên hỗ trợ về phần mềm quản lý bán hàng này.";

    // ---- Lưu lịch sử: mỗi phần tử là {role, text} ----
    private final List<String[]> history = new ArrayList<>();

    private final HttpClient httpClient;

    public GeminiChatService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Gửi tin nhắn — tự động dispatch đến Gemini hoặc Groq theo ApiConfig.PROVIDER.
     */
    public String chat(String userMessage) throws Exception {
        if ("GROQ".equals(ApiConfig.PROVIDER)) {
            return chatGroq(userMessage);
        } else {
            return chatGemini(userMessage);
        }
    }

    // ------------------------------------------------------------------
    //  Gemini
    // ------------------------------------------------------------------
    private String chatGemini(String userMessage) throws Exception {
        if (ApiConfig.GEMINI_API_KEY == null || ApiConfig.GEMINI_API_KEY.equals("YOUR_API_KEY_HERE")) {
            throw new Exception("API_KEY_NOT_SET");
        }

        history.add(new String[]{"user", userMessage});

        // Cắt bớt lịch sử nếu quá dài
        while (history.size() > MAX_HISTORY) {
            history.remove(0);
        }

        String body = buildRequestBody();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_URL + ApiConfig.GEMINI_API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception networkEx) {
            // Mạng lỗi → rollback tin nhắn khỏi history
            rollbackLastUserMessage();
            throw networkEx;
        }

        if (response.statusCode() != 200) {
            rollbackLastUserMessage();
            String errBody = response.body();
            // Với 429: thêm retry delay vào thông điệp để hiển thị thân thiện hơn
            if (response.statusCode() == 429) {
                String delay = extractRetryDelay(errBody);
                throw new Exception("HTTP 429: RETRY_AFTER=" + delay);
            }
            throw new Exception("HTTP " + response.statusCode() + ": " + extractErrorMessage(errBody));
        }

        String answer = extractTextFromResponse(response.body());
        history.add(new String[]{"model", answer});
        return answer;
    }

    /** Xóa tin nhắn user cuối cùng khỏi history khi API call thất bại. */
    private void rollbackLastUserMessage() {
        if (!history.isEmpty() && "user".equals(history.get(history.size() - 1)[0])) {
            history.remove(history.size() - 1);
        }
    }

    /** Xóa lịch sử hội thoại. */
    public void clearHistory() {
        history.clear();
    }

    // ----------------------------------------------------------
    //  Private helpers
    // ----------------------------------------------------------

    private String buildRequestBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // systemInstruction
        sb.append("\"systemInstruction\":{\"parts\":[{\"text\":\"")
          .append(escapeJson(SYSTEM_INSTRUCTION))
          .append("\"}]},");

        // contents (lịch sử hội thoại)
        sb.append("\"contents\":[");
        for (int i = 0; i < history.size(); i++) {
            String[] entry = history.get(i);
            sb.append("{\"role\":\"").append(entry[0]).append("\",")
              .append("\"parts\":[{\"text\":\"").append(escapeJson(entry[1])).append("\"}]}");
            if (i < history.size() - 1) sb.append(",");
        }
        sb.append("],");

        // generationConfig
        sb.append("\"generationConfig\":{")
          .append("\"temperature\":0.7,")
          .append("\"maxOutputTokens\":1024")
          .append("}");

        sb.append("}");
        return sb.toString();
    }

    /** Thoát các ký tự đặc biệt trong JSON string. */
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Trích xuất nội dung text từ phản hồi JSON của Gemini.
     * Cấu trúc: candidates[0].content.parts[0].text
     */
    private static String extractTextFromResponse(String json) {
        try {
            int candidatesIdx = json.indexOf("\"candidates\"");
            if (candidatesIdx == -1) return "Không nhận được phản hồi từ AI.";

            int textIdx = json.indexOf("\"text\"", candidatesIdx);
            if (textIdx == -1) return "AI không có nội dung trả lời.";

            int colonIdx = json.indexOf(":", textIdx);
            int quoteStart = json.indexOf("\"", colonIdx) + 1;

            StringBuilder result = new StringBuilder();
            int i = quoteStart;
            while (i < json.length()) {
                char c = json.charAt(i);
                if (c == '\\' && i + 1 < json.length()) {
                    char next = json.charAt(i + 1);
                    switch (next) {
                        case '"':  result.append('"');  break;
                        case 'n':  result.append('\n'); break;
                        case 't':  result.append('\t'); break;
                        case '\\': result.append('\\'); break;
                        case 'r':  /* bỏ \r */         break;
                        default:   result.append(next); break;
                    }
                    i += 2;
                } else if (c == '"') {
                    break;
                } else {
                    result.append(c);
                    i++;
                }
            }
            String answer = result.toString().trim();
            return answer.isEmpty() ? "AI không có phản hồi." : answer;
        } catch (Exception e) {
            return "Lỗi đọc phản hồi: " + e.getMessage();
        }
    }

    /** Trích thời gian retry từ lỗi 429 ("retryDelay":"28.6s"). */
    private static String extractRetryDelay(String json) {
        try {
            int idx = json.indexOf("retryDelay");
            if (idx == -1) return "?s";
            int start = json.indexOf('"', idx + 12) + 1;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        } catch (Exception e) {
            return "?s";
        }
    }

    /** Trích thông điệp lỗi ngắn từ JSON lỗi của Google API. */
    private static String extractErrorMessage(String json) {
        try {
            int msgIdx = json.indexOf("\"message\"");
            if (msgIdx == -1) return json.length() > 200 ? json.substring(0, 200) : json;
            int colonIdx = json.indexOf(":", msgIdx);
            int quoteStart = json.indexOf("\"", colonIdx) + 1;
            int quoteEnd = json.indexOf("\"", quoteStart);
            return json.substring(quoteStart, quoteEnd);
        } catch (Exception e) {
            return json;
        }
    }

    // ------------------------------------------------------------------
    //  Groq  (OpenAI-compatible API)
    // ------------------------------------------------------------------
    private String chatGroq(String userMessage) throws Exception {
        if (ApiConfig.GROQ_API_KEY == null || ApiConfig.GROQ_API_KEY.equals("YOUR_GROQ_KEY_HERE")) {
            throw new Exception("API_KEY_NOT_SET");
        }

        history.add(new String[]{"user", userMessage});
        while (history.size() > MAX_HISTORY) history.remove(0);

        String body = buildGroqRequestBody();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ApiConfig.GROQ_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception networkEx) {
            rollbackLastUserMessage();
            throw networkEx;
        }

        if (response.statusCode() != 200) {
            rollbackLastUserMessage();
            if (response.statusCode() == 429) {
                String delay = extractRetryDelay(response.body());
                throw new Exception("HTTP 429: RETRY_AFTER=" + delay);
            }
            throw new Exception("HTTP " + response.statusCode() + ": " + extractErrorMessage(response.body()));
        }

        String answer = extractGroqText(response.body());
        history.add(new String[]{"model", answer});
        return answer;
    }

    private String buildGroqRequestBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"model\":\"").append(ApiConfig.GROQ_MODEL).append("\",");
        sb.append("\"messages\":[");
        // System message đầu tiên
        sb.append("{\"role\":\"system\",\"content\":\"")
          .append(escapeJson(SYSTEM_INSTRUCTION))
          .append("\"}");
        // Lịch sử hội thoại
        for (String[] entry : history) {
            String role = "user".equals(entry[0]) ? "user" : "assistant";
            sb.append(",{\"role\":\"").append(role)
              .append("\",\"content\":\"").append(escapeJson(entry[1])).append("\"}");
        }
        sb.append("],");
        sb.append("\"temperature\":0.7,");
        sb.append("\"max_tokens\":1024");
        sb.append("}");
        return sb.toString();
    }

    /** Trích text từ phản hồi Groq/OpenAI: choices[0].message.content */
    private static String extractGroqText(String json) {
        try {
            int idx = json.indexOf("\"content\"");
            if (idx == -1) return "AI không có nội dung trả lời.";
            int colonIdx = json.indexOf(":", idx);
            int quoteStart = json.indexOf('"', colonIdx) + 1;
            StringBuilder result = new StringBuilder();
            int i = quoteStart;
            while (i < json.length()) {
                char c = json.charAt(i);
                if (c == '\\' && i + 1 < json.length()) {
                    char next = json.charAt(i + 1);
                    switch (next) {
                        case '"':  result.append('"');  break;
                        case 'n':  result.append('\n'); break;
                        case 't':  result.append('\t'); break;
                        case '\\': result.append('\\'); break;
                        case 'r':  break;
                        default:   result.append(next); break;
                    }
                    i += 2;
                } else if (c == '"') {
                    break;
                } else {
                    result.append(c);
                    i++;
                }
            }
            String answer = result.toString().trim();
            return answer.isEmpty() ? "AI không có phản hồi." : answer;
        } catch (Exception e) {
            return "Lỗi đọc phản hồi Groq: " + e.getMessage();
        }
    }
}
