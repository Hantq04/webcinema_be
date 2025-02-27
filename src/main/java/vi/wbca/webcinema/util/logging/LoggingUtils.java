package vi.wbca.webcinema.util.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class LoggingUtils {
    private static final Path DIRECTORY = Paths.get(System.getProperty("user.dir")).resolve(Paths.get("loggingError"));

    public static FileHandler createLoggingFolder() {
        try {
            if (!Files.exists(DIRECTORY)) {
                Files.createDirectories(DIRECTORY);
            }
            FileHandler fileHandler = new FileHandler(DIRECTORY + "/log-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            return fileHandler;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
