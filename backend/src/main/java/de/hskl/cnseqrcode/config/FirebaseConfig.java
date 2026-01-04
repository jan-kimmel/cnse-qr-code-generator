package de.hskl.cnseqrcode.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        System.out.println("=== Firebase Initialisierung gestartet ===");
        
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                String credentials = System.getenv("FIREBASE_CREDENTIALS");
                System.out.println("Environment Variable vorhanden: " + (credentials != null && !credentials.isEmpty()));
                
                if (credentials != null && !credentials.isEmpty()) {
                    System.out.println("Lade Firebase Credentials aus Environment Variable...");
                    try (InputStream serviceAccount = 
                            new ByteArrayInputStream(credentials.getBytes())) {
                        FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                        FirebaseApp.initializeApp(options);
                        System.out.println("Firebase initialisiert (via Environment Variable)!");
                    }
                } else {
                    System.out.println("Lade Firebase Credentials aus Classpath...");
                    ClassPathResource resource = new ClassPathResource(
                        "cnse-qr-code-generator-firebase-adminsdk-fbsvc-4272e0126f.json"
                    );
                    System.out.println("Resource existiert: " + resource.exists());
                    
                    try (InputStream serviceAccount = resource.getInputStream()) {
                        FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                        FirebaseApp.initializeApp(options);
                        System.out.println("Firebase initialisiert (via Classpath)!");
                    }
                }
            } else {
                System.out.println("Firebase bereits initialisiert!");
            }
        } catch (Exception e) {
            System.err.println("FEHLER bei Firebase-Initialisierung:");
            e.printStackTrace();
            throw new RuntimeException("Firebase konnte nicht initialisiert werden", e);
        }
        
        System.out.println("=== Firebase Initialisierung beendet ===");
    }
}