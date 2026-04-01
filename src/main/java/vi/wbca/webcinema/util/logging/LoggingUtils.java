package vi.wbca.webcinema.util.logging;

import vi.wbca.webcinema.util.logging.logForm.ErrorLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingUtils {
    private static final Logger logger = Logger.getLogger("ErrorLogger");
    private static final Path DIRECTORY = Paths.get(System.getProperty("user.dir")).resolve(Paths.get("logging"));
    private static final int MAX_FILE_SIZE = 1024 * 1024;
    private static final int FILE_COUNT = 10;

    private static FileHandler createLoggingFolder() {
        try {
            if (!Files.exists(DIRECTORY)) Files.createDirectories(DIRECTORY);
            String fileName = DIRECTORY + "/log-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt";
            FileHandler fileHandler = new FileHandler(fileName, MAX_FILE_SIZE, FILE_COUNT, true);
            fileHandler.setFormatter(new SimpleFormatter());
            return fileHandler;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loggingError(Exception error) {
        ErrorLog errorForm = ErrorLog.builder()
                .errorLevel(error.getClass().getSimpleName())
                .description(error.getMessage())
                .location(Arrays.toString(Arrays.copyOfRange(error.getStackTrace(), 0, 3)))
                .build();
        FileHandler fileHandler = createLoggingFolder();
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        logger.warning("[" + errorForm.getErrorLevel()
                + "][" + errorForm.getDescription()
                + "][" + errorForm.getLocation() + "]"
        );
        assert fileHandler != null;
        fileHandler.close();
    }
}
