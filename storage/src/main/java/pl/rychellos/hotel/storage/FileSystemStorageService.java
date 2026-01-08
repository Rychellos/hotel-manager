package pl.rychellos.hotel.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {
    private final Path rootLocation;
    private final long maxFileSize;

    public FileSystemStorageService(
        @Value("${storage.location:uploads}") String location,
        @Value("${storage.max-size:26214400}") long maxFileSize
    ) {
        this.rootLocation = Paths.get(location);
        this.maxFileSize = maxFileSize;

        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            log.error("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(byte[] data, String path) throws IOException {
        if (data.length > maxFileSize) {
            throw new IOException("File exceeds 25MB limit");
        }

        Path targetPath = rootLocation.resolve(path);
        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, data);

        return path;
    }

    @Override
    public byte[] load(String path) throws IOException {
        return Files.readAllBytes(rootLocation.resolve(path));
    }

    @Override
    public void delete(String path) throws IOException {
        Files.deleteIfExists(rootLocation.resolve(path));
    }
}
