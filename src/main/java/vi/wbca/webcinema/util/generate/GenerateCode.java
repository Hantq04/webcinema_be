package vi.wbca.webcinema.util.generate;

import java.util.UUID;
import java.util.Random;

public class GenerateCode {
    private static final Random random = new Random();

    public static String generateCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public static String generateTradingCode() {
        // Format: XXXX-XXXX-XXXX-XXXX (4 groups of 4 random digits)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i > 0) sb.append("-");
            for (int j = 0; j < 4; j++) {
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }
}
