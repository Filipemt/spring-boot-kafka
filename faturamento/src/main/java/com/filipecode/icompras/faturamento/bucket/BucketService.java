package com.filipecode.icompras.faturamento.bucket;

import com.filipecode.icompras.faturamento.config.props.MinioProps;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BucketService {

    private final MinioClient minioClient;
    private final MinioProps minioProps;

    public void upload(BucketFile file) {

    }

    public String getUrl(String fileName) {
        
    }
}
