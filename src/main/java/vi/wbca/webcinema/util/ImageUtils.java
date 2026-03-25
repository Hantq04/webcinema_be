package vi.wbca.webcinema.util;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

public class ImageUtils {

    private static final String UPLOAD_DIR = "D:/project/uploads/";
    private static final String BASE_URL = "http://localhost:8080/uploads/";

    public static String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        String originalName = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + Objects.requireNonNull(originalName);
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);
        return BASE_URL + fileName;
    }
}
