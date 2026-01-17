package pl.rychellos.hotel.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileSystemStorageService storageService;
    private final long maxFileSize = 100; // Small limit for testing

    @BeforeEach
    void setUp() {
        storageService = new FileSystemStorageService(tempDir.toString(), maxFileSize);
    }

    @Test
    void store_ShouldSaveFile_WhenUnderLimit() throws IOException {
        // Given
        byte[] data = "Hello World".getBytes();
        String path = "test.txt";

        // When
        String result = storageService.store(data, path);

        // Then
        assertEquals(path, result);
        assertTrue(Files.exists(tempDir.resolve(path)));
        assertArrayEquals(data, Files.readAllBytes(tempDir.resolve(path)));
    }

    @Test
    void store_ShouldThrowException_WhenOverLimit() {
        // Given
        byte[] data = new byte[(int) maxFileSize + 1];
        String path = "too_large.txt";

        // When & Then
        IOException exception = assertThrows(IOException.class, () -> storageService.store(data, path));
        assertTrue(exception.getMessage().contains("limit"));
    }

    @Test
    void load_ShouldReturnData() throws IOException {
        // Given
        byte[] data = "Hello World".getBytes();
        String path = "load_test.txt";
        Files.write(tempDir.resolve(path), data);

        // When
        byte[] result = storageService.load(path);

        // Then
        assertArrayEquals(data, result);
    }

    @Test
    void delete_ShouldRemoveFile() throws IOException {
        // Given
        String path = "delete_test.txt";
        Files.createFile(tempDir.resolve(path));

        // When
        storageService.delete(path);

        // Then
        assertFalse(Files.exists(tempDir.resolve(path)));
    }

    @Test
    void delete_ShouldNotThrow_WhenFileDoesNotExist() {
        // When & Then
        assertDoesNotThrow(() -> storageService.delete("non_existent.txt"));
    }
}
