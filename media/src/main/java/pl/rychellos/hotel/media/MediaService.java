package pl.rychellos.hotel.media;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.media.dto.MediaDTO;
import pl.rychellos.hotel.media.dto.MediaFilterDTO;
import pl.rychellos.hotel.storage.StorageService;

@Slf4j
@Service
public class MediaService extends GenericService<MediaEntity, MediaDTO, MediaFilterDTO, MediaRepository> {
    private final StorageService storageService;

    public MediaService(
            LangUtil langUtil,
            MediaMapper mapper,
            MediaRepository repository,
            ApplicationExceptionFactory exceptionFactory,
            ObjectMapper objectMapper,
            StorageService storageService) {
        super(langUtil, MediaDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.storageService = storageService;
    }

    public MediaDTO upload(MultipartFile file, Long ownerId, boolean isPublic) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        UUID publicId = UUID.randomUUID();
        String uuidStr = publicId.toString();
        String shard = uuidStr.substring(0, 2) + "/" + uuidStr.substring(2, 4);
        String extension = getExtension(originalFilename);
        String storedPath = shard + "/" + uuidStr + extension;

        storageService.store(file.getBytes(), storedPath);

        MediaEntity entity = MediaEntity.builder()
                .publicId(publicId)
                .originalFilename(originalFilename)
                .contentType(contentType)
                .fileSize(fileSize)
                .storedPath(storedPath)
                .ownerId(ownerId)
                .isPublic(isPublic)
                .type(contentType != null && contentType.startsWith("image/") ? MediaType.IMAGE : MediaType.FILE)
                .url("/api/v1/media/" + uuidStr)
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains("."))
            return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    @Override
    protected void fetchRelations(MediaEntity entity, MediaDTO dto) throws ApplicationException {
    }
}
