package com.s3.api.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public interface IS3Service {

    // Crear bucket en S3
    String createBucket(String bucketName);

    //Saber si un bucket existe
    String checkIfBucketExist(String bucketName);

    // Listar buckets
    List<String> getAllBuckets();

    // Cargar un archivo a un bucket
    Boolean uploadFile(String bucketName, String key, Path fileLocation);

    // Descargar un archivo de un bucket
    void downloadFile(String bucket, String key) throws IOException;

    // Generar URL prefirmada para subir archivos
    String generatePresignedUploadUrl(String bucketName, String key, Duration duration);

    // Generar URL prefirmada para descargar archivos
    String geberatePresignedDownloadUrl(String bucketName, String key, Duration duration);
}