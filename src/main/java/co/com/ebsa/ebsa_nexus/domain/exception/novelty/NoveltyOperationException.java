package co.com.ebsa.ebsa_nexus.domain.exception.novelty;

/**
 * Exception thrown when a novelty operation is invalid.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
public class NoveltyOperationException extends RuntimeException {
    public NoveltyOperationException(String message) {
        super(message);
    }
}
