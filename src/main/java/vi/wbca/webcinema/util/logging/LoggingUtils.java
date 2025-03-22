package vi.wbca.webcinema.util.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingUtils {
    private static final Logger logger = Logger.getLogger("ErrorLogger");
    private static final Path DIRECTORY = Paths.get(System.getProperty("user.dir")).resolve(Paths.get("logging"));
    private static final int MAX_FILE_SIZE = 1024 * 1024;
    private static final int FILE_COUNT = 5;

    private static FileHandler createLoggingFolder() {
        try {
            if (!Files.exists(DIRECTORY)) Files.createDirectories(DIRECTORY);

            String fileName = DIRECTORY + "/log-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt";
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
