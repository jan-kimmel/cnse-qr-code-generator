package de.hskl.cnseqrcode.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        System.out.println("=== Firebase Initialisierung gestartet ===");

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = null;

                // Google Cloud
                String secretPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
                if (secretPath != null && !secretPath.isEmpty()) {
                    File secretFile = new File(secretPath);
                    if (secretFile.exists()) {
                        System.out.println("Lade Firebase Credentials aus GCloud Secret: " + secretPath);
                        serviceAccount = new FileInputStream(secretFile);
                    } else {
                        System.err.println("Secret-Pfad gesetzt, aber Datei nicht gefunden: " + secretPath);
                    }
                }

                // Lokal (Docker Compose)
                if (serviceAccount == null) {
                    String localFile = "firebase/cnse-qr-code-generator-firebase.json";
                    File localJsonFile = new File(localFile);
                    
                    if (localJsonFile.exists()) {
                        System.out.println("Lade Firebase Credentials lokal: " + localFile);
                        serviceAccount = new FileInputStream(localJsonFile);
                    } else {
                        System.out.println("Lokale Datei nicht gefunden, versuche Classpath...");
                        ClassPathResource resource = new ClassPathResource(localFile);
                        if (resource.exists()) {
                            System.out.println("Lade Firebase Credentials aus Classpath: " + localFile);
                            serviceAccount = resource.getInputStream();
                        }
                    }
                }

                if (serviceAccount == null) {
                    throw new RuntimeException(
                        "Firebase Credentials nicht gefunden!\n" +
                        "GCloud: Setze FIREBASE_CREDENTIALS_PATH\n" +
                        "Lokal: Lege firebase/cnse-qr-code-generator-firebase.json ab"
                    );
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);

                System.out.println("Firebase erfolgreich initialisiert!");
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
