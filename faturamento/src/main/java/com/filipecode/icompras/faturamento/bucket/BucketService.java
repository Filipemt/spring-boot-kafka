package com.filipecode.icompras.faturamento.bucket;

import com.filipecode.icompras.faturamento.config.props.MinioProps;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BucketService {

    private final MinioClient minioClient;
    private final MinioProps minioProps;

    public void upload(BucketFile file) {
        try {
            var object = PutObjectArgs.builder()
                    .bucket(minioProps.getBucketName())
                    .object(file.name())
                    .stream(file.inputStream(), file.size(), -1)
                    .contentType(file.mediaType().toString())
                    .build();

            minioClient.putObject(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getUrl(String fileName) {
        try {
            var object = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioProps.getBucketName())
                    .object(fileName)
                    .expiry(1, TimeUnit.HOURS)
                    .build();

            return minioClient.getPresignedObjectUrl(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
