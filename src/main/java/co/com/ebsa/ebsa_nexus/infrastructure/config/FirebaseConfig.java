package co.com.ebsa.ebsa_nexus.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    @PostConstruct
    public void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            GoogleCredentials credentials = loadCredentials();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setStorageBucket(storageBucket)
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public Storage firebaseStorage() throws IOException {
        GoogleCredentials credentials = loadCredentials();

        String projectId = storageBucket.split("\\.")[0];
        
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build()
                .getService();
    }

    @Bean
    public StorageClient storageClient() {
        return StorageClient.getInstance();
    }

    /**
     * Load credentials from environment variable (preferred) or file path.
     * Priority: FIREBASE_CREDENTIALS_JSON env var > file path
     */
    private GoogleCredentials loadCredentials() throws IOException {
        // OPTION 1: Try to load from environment variable (for Render deployment)
        String credentialsJson = System.getenv("FIREBASE_CREDENTIALS_JSON");
        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            InputStream credentialsStream = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
            return GoogleCredentials.fromStream(credentialsStream);
        }

        // OPTION 2: Try to load from file path
        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IOException("Firebase credentials not configured. Set FIREBASE_CREDENTIALS_JSON environment variable or firebase.credentials.path property.");
        }

        InputStream credentialsStream;
        
        // Check if path is absolute (e.g., /etc/secrets/firebaseServiceAccount)
        if (credentialsPath.startsWith("/") || credentialsPath.startsWith("C:") || credentialsPath.startsWith("D:")) {
            if (Files.exists(Paths.get(credentialsPath))) {
                credentialsStream = new FileInputStream(credentialsPath);
            } else {
                throw new IOException("Firebase credentials file not found at: " + credentialsPath);
            }
        } else {
            // Load from classpath (for local development)
            credentialsStream = new ClassPathResource(credentialsPath).getInputStream();
        }
        
        return GoogleCredentials.fromStream(credentialsStream);
    }
}
