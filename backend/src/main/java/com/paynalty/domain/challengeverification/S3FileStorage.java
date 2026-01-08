package com.paynalty.domain.challengeverification;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList(".jpg", ".jpeg", ".png", ".gif");

    @Override
    public String upload(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("파일이 비어있습니다");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);

            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new RuntimeException("지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 가능)");
            }

            String savedFileName = UUID.randomUUID() + extension;

            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            try (InputStream is = file.getInputStream()) {
                s3Template.upload(bucket, savedFileName, is, 
                        ObjectMetadata.builder().contentType(contentType).build());
            }

            return savedFileName;

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public void delete(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        s3Template.deleteObject(bucket, fileName);
    }

    @Override
    public String getFileUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        // 이미 전체 URL이거나 http로 시작하는 경우 그대로 반환
        if (fileName.startsWith("http")) {
            return fileName;
        }
        // S3 Public URL 형식: https://[bucket].s3.[region].amazonaws.com/[fileName]
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
