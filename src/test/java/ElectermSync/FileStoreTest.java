package test.java.ElectermSync;

import ElectermSync.Config;
import ElectermSync.FileStore;
import ElectermSync.ReadResult;
import ElectermSync.WriteResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileStoreTest {
    private final Config dotenv = new Config();
    private final String userId = "testUserId";
    private Path testFilePath;

    @BeforeEach
    public void setup() {
        testFilePath = FileStore.getFilePath(userId, dotenv);
    }

    @AfterEach
    public void tearDown() {
        try {
            Files.deleteIfExists(testFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWriteAndRead() {
        String jsonBody = "{\"key\": \"value\"}";

        WriteResult writeResult = FileStore.write(jsonBody, userId, dotenv);
        assertEquals(200, writeResult.statusCode);
        assertEquals("ok", writeResult.message);

        ReadResult readResult = FileStore.read(userId, dotenv);
        assertEquals(200, readResult.statusCode);
        assertEquals(jsonBody, readResult.fileData);
    }

    @Test
    public void testInvalidRead() {
        ReadResult readResult = FileStore.read(userId + "xx", dotenv);
        assertEquals(404, readResult.statusCode);
        assertEquals("File not found", readResult.fileData);
    }
}
