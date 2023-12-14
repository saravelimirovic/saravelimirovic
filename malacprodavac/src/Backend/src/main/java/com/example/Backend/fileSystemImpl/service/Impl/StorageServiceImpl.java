package com.example.Backend.fileSystemImpl.service.Impl;

import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.fileSystemImpl.service.StorageService;
import com.example.Backend.fileSystemImpl.utilities.Base64Coder;
import com.example.Backend.fileSystemImpl.utilities.FolderUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class StorageServiceImpl implements StorageService {
    @Value("${filesystem.path}")
    private String generalPath;

    @Override
    public void store(String identificationString, MultipartFile multipartFile, ImageType imageType) {
        // podesavanje putanje u zavisnosti od toga da li se slike cuvaju za user-a ili product-e
        Path path = Paths.get(generalPath + generateFolderPath(identificationString, imageType));
        System.out.println(path);
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(!FolderUtility.isFolderEmpty(path))
            FolderUtility.deleteFolderContent(path);

        if(multipartFile != null){
            Path destinationFile = path.resolve(multipartFile.getOriginalFilename());
            System.out.println(destinationFile);
            try {
                Files.copy(multipartFile.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Path getFileLocation(String identificationString, ImageType imageType) {
        return Paths.get(generalPath + generateFolderPath(identificationString, imageType) + "/");
    }

    @Override
    public Resource loadImageAsResource(String identificationString, ImageType imageType) {
        Path path = getFileLocation(identificationString, imageType);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path pathFile : directoryStream) {
                Resource resource = new UrlResource(pathFile.toUri());
                if (resource.exists())
                    return resource;
            }
        } catch (NoSuchFileException ex){
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String generateFolderPath(String identificationString, ImageType imageType) {
        String folder = "";
        if(imageType == ImageType.USER)
            folder = "/users/" + Base64Coder.encodeString("user" + identificationString);
        else if(imageType == ImageType.PRODUCT)
            folder = "/products/" + Base64Coder.encodeString("product" + identificationString);
        else if(imageType == ImageType.LOGO)
            folder = "/logo/" + Base64Coder.encodeString("logo" + identificationString);

        return folder;
    }
}
