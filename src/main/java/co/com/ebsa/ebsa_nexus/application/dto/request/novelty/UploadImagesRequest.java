package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for uploading images to a novelty.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadImagesRequest {
    
    /**
     * List of image URLs from Firebase.
     * Must have at least one image.
     * Maximum 10 images per novelty (validated in service).
     */
    @Size(min = 1, max = 10, message = "Must upload between 1 and 10 images")
    private List<@NotBlank(message = "Image URL cannot be blank") String> imageUrls;
}
