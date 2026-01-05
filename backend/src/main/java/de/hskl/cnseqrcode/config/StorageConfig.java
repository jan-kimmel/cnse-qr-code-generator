package de.hskl.cnseqrcode.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
public class StorageConfig {
    @Bean
    @Profile({"dev", "docker"})
    public Firestore firestoreEmulator(
        @Value("${firestore.emulator-host}") String emulatorHost,
        @Value("${firestore.project-id}") String projectId) {
    
        System.out.println("Firestore Emulator: " + emulatorHost + " | Project: " + projectId);
    
        System.setProperty("FIRESTORE_EMULATOR_HOST", emulatorHost);
    
        FirestoreOptions options = FirestoreOptions.newBuilder()
            .setProjectId(projectId)
            .setHost(emulatorHost)
            .build();
    
        return options.getService();
    }
    
    @Bean
    @Profile("prod")
    public Firestore firestoreProduction(
        @Value("${google.cloud.project-id}") String projectId) throws IOException {
        
        System.out.println("Firestore Production: " + projectId);
        
        FirestoreOptions options = FirestoreOptions.newBuilder()
            .setProjectId(projectId)
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .build();
        
        return options.getService();
    }
    
    @Bean
    @Profile({"dev", "docker"})
    public MinioClient minioClient(
        @Value("${minio.url}") String url,
        @Value("${minio.access-key}") String accessKey,
        @Value("${minio.secret-key}") String secretKey,
        @Value("${app.storage.bucket}") String bucketName) throws Exception {
        
        System.out.println("MinIO: " + url + " | Bucket: " + bucketName);
        
        MinioClient client = MinioClient.builder()
            .endpoint(url)
            .credentials(accessKey, secretKey)
            .build();
        
        boolean exists = client.bucketExists(
            BucketExistsArgs.builder().bucket(bucketName).build()
        );
        
        if (!exists) {
            System.out.println("Creating bucket: " + bucketName);
            
            client.makeBucket(
                MakeBucketArgs.builder().bucket(bucketName).build()
            );
            
            String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": "*"},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucketName);
            
            client.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build()
            );
        }
        
        return client;
    }
    
    @Bean
    @Profile("prod")
    public Storage googleCloudStorage() throws IOException {
        System.out.println("Google Cloud Storage");
        return StorageOptions.getDefaultInstance().getService();
    }
}