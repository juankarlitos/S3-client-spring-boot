package com.s3.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Service
public class S3ServiceImpl implements IS3Service{

    @Value("${spring.destination.folder}")
    private String destinationFolder;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3AsyncClient s3AsyncClient;

    @Override
    public String createBucket(String bucketName) {
        CreateBucketResponse response = this.s3Client.createBucket(bucketBuilder -> bucketBuilder.bucket(bucketName) );
        return "Bucket creado en la ubicacion: " + response.location();
    }
    @Override
    public String checkIfBucketExist(String bucketName) {
        try {
            this.s3Client.headBucket(headBucket -> headBucket.bucket(bucketName));
            return "El bucket " + bucketName + " si existe ";
        } catch (S3Exception exception) {
            return "El bucket " + bucketName + "no existe";
        }
    }
    @Override
    public List<String> getAllBuckets() {
        ListBucketsResponse bucketsResponse =  this.s3Client.listBuckets();
        if (bucketsResponse.hasBuckets()){
            return bucketsResponse.buckets()
                    .stream()
                    .map(Bucket::name)
                    .toList();
        } else {
            return List.of();
        }
    }
    @Override
    public Boolean uploadFile(String bucketName, String key, Path fileLocation) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
       PutObjectResponse putObjectResponse = this.s3Client.putObject(putObjectRequest,fileLocation);
        return putObjectResponse.sdkHttpResponse().isSuccessful();
    }
    @Override
    public void downloadFile(String bucket, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> objectBytes = this.s3Client.getObjectAsBytes(getObjectRequest);

        String filename;
        if (key.contains("/")){
            filename = key.substring(key.lastIndexOf("/") );
        } else {

            filename = key;
        }
            String filePath = Paths.get(destinationFolder, filename).toString();
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try(FileOutputStream fos = new FileOutputStream(file)) {
                 fos.write(objectBytes.asByteArray());
            }catch (IOException exception){
                throw  new IOException("Error al descargar el carchivo " + exception.getCause());
            }
    }
    @Override
    public String generatePresignedUploadUrl(String bucketName, String key, Duration duration) {
        return null;
    }
    @Override
    public String geberatePresignedDownloadUrl(String bucketName, String key, Duration duration) {
        return null;
    }
}