package vi.wbca.webcinema.util.generate;

import java.text.Normalizer;
import java.util.Random;

public class GenerateCinemaCode {
    public static String generateCinemaCode(String location) {
        String name = "BETA";
        String normalizedLocation = removeVietnameseAccents(location);
        String cityCode = extractCityCode(normalizedLocation);

        int randomNum = new Random().nextInt(900) + 100;
        return name + "_" + cityCode + randomNum;
    }

    private static String removeVietnameseAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").replaceAll("Đ", "D").replaceAll("đ", "d");
    }

    private static String extractCityCode(String location) {
        String[] words = location.split("\\s+");
        StringBuilder code = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                code.append(word.charAt(0));
            }
        }
        return code.length() >= 2 ? code.substring(0, 2).toUpperCase() : code.toString().toUpperCase();
    }
}
