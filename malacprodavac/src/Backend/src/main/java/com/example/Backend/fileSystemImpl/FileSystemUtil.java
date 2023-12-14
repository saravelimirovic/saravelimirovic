package com.example.Backend.fileSystemImpl;

import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.fileSystemImpl.model.CustomMultipartFile;
import com.example.Backend.fileSystemImpl.service.StorageService;
import com.example.Backend.fileSystemImpl.utilities.FolderUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FileSystemUtil {
    @Autowired
    private StorageService storageService;

    // za kupljenje iz file systema
    public byte[] getImageInBytes(String identificationString, ImageType imageType){
        Resource resource = storageService.loadImageAsResource(identificationString, imageType);
        try {
            return resource == null ? null : FolderUtility.convertResourceToByteArray(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // za cuvanje u file system
    public void saveImage(String identificationString, byte[] picture, ImageType imageType){
        CustomMultipartFile customMultipartFile = null;
        if(picture != null && picture.length > 0)
            customMultipartFile = new CustomMultipartFile(picture);

        storageService.store(identificationString, customMultipartFile, imageType);
    }
}
