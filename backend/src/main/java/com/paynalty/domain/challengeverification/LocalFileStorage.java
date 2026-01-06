package com.paynalty.domain.challengeverification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileStorage implements FileStorage{

    @Value("${file.upload.path}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList(".jpg", ".jpeg", ".png", ".gif");

    @Override
    public String upload(MultipartFile file){
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("파일이 비어있습니다");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);

            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new RuntimeException("지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 가능)");
            }

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String savedFileName = UUID.randomUUID() + extension;

            Path filePath = uploadPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), filePath);

            // 저장된 파일명 반환 (상대 경로)
            return savedFileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 파일을 삭제합니다.
     * @param fileName 삭제할 파일명
     */
    public void delete(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        
        try {
            Path filePath = Paths.get(uploadDir, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
