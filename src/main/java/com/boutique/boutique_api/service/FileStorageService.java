package com.boutique.boutique_api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file);
    Resource loadFileAsResource(String fileName); // Nécessaire pour afficher l'image
    boolean deleteFile(String filename);
}