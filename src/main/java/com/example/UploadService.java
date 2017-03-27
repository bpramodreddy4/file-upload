package com.example;

import com.example.entity.FileRepository;
import com.example.entity.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {

    @Autowired
    private FileRepository fileRepository;

    @Value("${app.upload.folder}")
    private String uploadFolder;

    public Upload saveUpload(String fileName, MultipartFile multipartFile) throws IOException {
        final String originalFilename = multipartFile.getOriginalFilename();
        final UUID uuid = UUID.randomUUID();
        final String extension = extractExtension(originalFilename);
        final String uuidString = uuid.toString();
        final String filePath = this.uploadFolder + "/" + uuidString + "." + extension;
        final File fileObject = new File(filePath);
        multipartFile.transferTo(fileObject);

        final Upload upload = new Upload();
        upload.setPath(fileObject.getAbsolutePath());
        upload.setUuid(uuidString);
        upload.setFileSize(fileObject.length());
        upload.setOriginalFileName(originalFilename);
        upload.setUploadedAt(LocalDateTime.now());
        upload.setDownloadLink("download/" + uuidString);
        fileRepository.save(upload);
        return upload;
    }


    public String extractExtension(String fileName) {
        if (fileName == null) return "";
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public List<Upload> find(String name) {
        if (name != null) {
            return fileRepository.findByOriginalFileNameContaining(name);
        }
        return Collections.emptyList();
    }
}
