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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.path}")
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
     * Load credentials from either classpath or absolute path.
     * This supports both local development (classpath) and production (Render Secret Files).
     */
    private GoogleCredentials loadCredentials() throws IOException {
        InputStream credentialsStream;
        
        // Check if path is absolute (e.g., /etc/secrets/firebase-service-account.json)
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
