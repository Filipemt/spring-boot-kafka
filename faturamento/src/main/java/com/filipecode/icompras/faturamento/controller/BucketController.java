package com.filipecode.icompras.faturamento.controller;

import com.filipecode.icompras.faturamento.bucket.BucketFile;
import com.filipecode.icompras.faturamento.bucket.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/bucket")
@RequiredArgsConstructor
public class BucketController {

    private final BucketService bucketService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            MediaType mediaType = MediaType.parseMediaType(file.getContentType());
            var bucketFile = new BucketFile(file.getOriginalFilename(), inputStream, mediaType, file.getSize());

            bucketService.upload(bucketFile);
            return ResponseEntity.ok("Arquivo enviado com sucesso!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar o arquivo: " + e.getMessage());
        }
    }
}
