package vi.wbca.webcinema.util.generate;

import java.util.UUID;

public class GenerateCode {
    public static String generateCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public static String generateTradingCode() {
        return "VN" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
