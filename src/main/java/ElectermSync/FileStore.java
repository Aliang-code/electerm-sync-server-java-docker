
package ElectermSync;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStore {

    public static void main(String[] args) {
        // Test code
    }

    public static Path getFilePath (String userId, Config dotenv) {
        String storePath = dotenv.getValue("FILE_STORE_PATH");
        Path folder = storePath != null ? Path.of(storePath) : Path.of(System.getProperty("user.dir"));
        return folder.resolve(userId + ".json");
    }

    public static WriteResult write(String jsonBody, String userId, Config dotenv) {
        Path filePath = getFilePath(userId, dotenv);
        try {
            Files.writeString(filePath, jsonBody);
        } catch (IOException e) {
            return new WriteResult("Error writing file", 500);
        }

        return new WriteResult("ok", 200);
    }

    public static ReadResult read(String userId, Config dotenv) {
        ObjectMapper objectMapper = new ObjectMapper();
        Path filePath = getFilePath(userId, dotenv);
        File file = filePath.toFile();

        if (file.isFile()) {
            String fileContent;
            try {
                fileContent = Files.readString(filePath).toString();
                return new ReadResult(fileContent, 200);
            } catch (IOException e) {
                return new ReadResult("File read error", 500);
            }
        } else {
            return new ReadResult("File not found", 404);
        }
    }
}
