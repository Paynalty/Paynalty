package com.paynalty.domain.challengeverification;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    String upload(MultipartFile file);
    void delete(String fileName);
    String getFileUrl(String fileName);
}
