package co.com.ebsa.ebsa_nexus.infrastructure.storage;

import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.ImageUploadResultDTO;
import co.com.ebsa.ebsa_nexus.domain.exception.storage.StorageException;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FirebaseStorageAdapter {

    private final Storage storage;
    
    @Value("${firebase.storage.bucket}")
    private String bucketName;
    
    @Value("${firebase.storage.max-file-size:26214400}")
    private long maxFileSize;
    
    @Value("${firebase.storage.allowed-types}")
    private String allowedTypes;

    public FirebaseStorageAdapter(Storage storage) {
        this.storage = storage;
    }

    /**
     * Upload multiple images to Firebase Storage
     */
    public List<ImageUploadResultDTO> uploadImages(List<MultipartFile> files, String folder) {
        List<ImageUploadResultDTO> results = new ArrayList<>();
        
        if (files == null || files.isEmpty()) {
            return results;
        }
        
        if (files.size() > 10) {
            throw new StorageException("Maximum 10 images allowed per novelty");
        }
        
        for (MultipartFile file : files) {
            try {
                ImageUploadResultDTO result = uploadSingleImage(file, folder);
                results.add(result);
            } catch (Exception e) {
                log.error("Error uploading file: {}", file.getOriginalFilename(), e);
                results.add(ImageUploadResultDTO.builder()
                    .originalFileName(file.getOriginalFilename())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build());
            }
        }
        
        return results;
    }

    /**
     * Upload single image to Firebase Storage
     */
    private ImageUploadResultDTO uploadSingleImage(MultipartFile file, String folder) throws IOException {
        // Validations
        validateFile(file);
        
        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String storedFileName = generateFileName(extension);
        String fullPath = folder + "/" + storedFileName;
        
        // Upload to Firebase
        BlobId blobId = BlobId.of(bucketName, fullPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(file.getContentType())
            .setMetadata(Map.of(
                "originalName", originalFileName != null ? originalFileName : "unknown",
                "uploadedAt", LocalDateTime.now().toString()
            ))
            .build();
        
        Blob blob = storage.create(blobInfo, file.getBytes());
        
        // Generate signed URL (valid for 7 days)
        String publicUrl = blob.signUrl(7, TimeUnit.DAYS).toString();
        
        log.info("Image uploaded successfully: {}", fullPath);
        
        return ImageUploadResultDTO.builder()
            .originalFileName(originalFileName)
            .storedFileName(storedFileName)
            .publicUrl(publicUrl)
            .size(file.getSize())
            .contentType(file.getContentType())
            .success(true)
            .build();
    }

    /**
     * Validate file size and type
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new StorageException(
                String.format("File size exceeds maximum allowed: %d MB", maxFileSize / (1024 * 1024))
            );
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(allowedTypes.split(",")).contains(contentType)) {
            throw new StorageException(
                "Invalid file type. Allowed types: " + allowedTypes
            );
        }
    }

    /**
     * Generate unique filename with timestamp
     */
    private String generateFileName(String extension) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%s%s", timestamp, uuid, extension);
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * Delete image from Firebase Storage
     */
    public void deleteImage(String filePath) {
        try {
            BlobId blobId = BlobId.of(bucketName, filePath);
            boolean deleted = storage.delete(blobId);
            
            if (deleted) {
                log.info("Image deleted successfully: {}", filePath);
            } else {
                log.warn("Image not found for deletion: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Error deleting image: {}", filePath, e);
            throw new StorageException("Error deleting image: " + e.getMessage());
        }
    }

    /**
     * Delete multiple images
     */
    public void deleteImages(List<String> filePaths) {
        for (String filePath : filePaths) {
            try {
                deleteImage(filePath);
            } catch (Exception e) {
                log.error("Error deleting image: {}", filePath, e);
            }
        }
    }
}
