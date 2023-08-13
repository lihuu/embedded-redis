package redis.embedded.util;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static com.google.common.base.StandardSystemProperty.JAVA_IO_TMPDIR;

public class JarUtil {

    public static File extractExecutableFromJar(String executable) throws IOException {
        File tmpDir = Files.createTempDir();
        tmpDir.deleteOnExit();

        File command = new File(tmpDir, executable);
        FileUtils.copyURLToFile(Resources.getResource(executable), command);
        command.deleteOnExit();
        boolean result = command.setExecutable(true);
        if(!result){
            throw new IOException("can't set executable flag for file: " + command.getAbsolutePath());
        }
        return command;
    }
}
