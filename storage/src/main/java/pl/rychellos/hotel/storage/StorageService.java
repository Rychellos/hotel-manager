package pl.rychellos.hotel.storage;

import java.io.IOException;

public interface StorageService {
    /**
     * Stores data at the given path.
     * @param data Raw byte data.
     * @param path Relative path for storage.
     * @return The canonical path where the file was stored.
     * @throws IOException If storage fails or size limit is exceeded.
     */
    String store(byte[] data, String path) throws IOException;

    /**
     * Loads data from the given path.
     * @param path Relative path.
     * @return Byte data.
     * @throws IOException If loading fails.
     */
    byte[] load(String path) throws IOException;

    /**
     * Deletes the file at the given path.
     * @param path Relative path.
     * @throws IOException If deletion fails.
     */
    void delete(String path) throws IOException;
}
