package vi.wbca.webcinema.util;

import java.util.Random;

public class GenerateOTP {
    public static String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
