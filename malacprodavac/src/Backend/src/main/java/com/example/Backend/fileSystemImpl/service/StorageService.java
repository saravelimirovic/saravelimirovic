package com.example.Backend.fileSystemImpl.service;

import com.example.Backend.fileSystemImpl.enums.ImageType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Path;

public interface StorageService {
    void store(String identificationString, MultipartFile multipartFile, ImageType imageType);

    Path getFileLocation(String identificationString, ImageType imageType);

    Resource loadImageAsResource(String identificationString, ImageType imageType);
}
