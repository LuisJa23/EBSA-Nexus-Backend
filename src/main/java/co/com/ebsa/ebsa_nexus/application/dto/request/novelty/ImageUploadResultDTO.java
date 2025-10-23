package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResultDTO {
    private String originalFileName;
    private String storedFileName;
    private String publicUrl;
    private Long size;
    private String contentType;
    private boolean success;
    private String errorMessage;
}
