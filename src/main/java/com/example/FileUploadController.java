package com.example;

import com.example.entity.FileRepository;
import com.example.entity.Upload;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@RestController
@EnableAutoConfiguration
@MultipartConfig(location = "/tmp", fileSizeThreshold = 1024 * 1024,
        maxFileSize = 30485760, maxRequestSize = 1024 * 1024 * 5 * 5)

@RequestMapping("/api")
public class FileUploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String UPLOAD_API = "/upload";
    public static final String UPLOAD_ID_META = "/upload/{id}/meta";
    public static final String DOWNLOAD_UUID = "/download/{uuid}";
    public static final String COUNT = "/count";

    @Value("${app.upload.folder}")
    private String uploadFolder;

    @Autowired private UploadService uploadService;

    @Autowired
    private FileRepository fileRepository;


    @GetMapping(value = COUNT, produces = APPLICATION_JSON_VALUE)
    public Object getFileCount() {
        final ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put(Constants.JsonKeys.COUNT, fileRepository.count());
        return objectNode;
    }

    // 1) API for uploading a file
    @Async
    @JsonView(JsonViews.MetaView.class)
    @PostMapping(value = UPLOAD_API)
    public Upload uploadFile(@RequestParam(name = "name", required = false) String fileName,
                             @RequestPart("file") @Valid @NotNull @NotBlank MultipartFile multipartFile)
            throws IOException {
        final Upload upload = uploadService.saveUpload(fileName, multipartFile);
        return upload;
    }


    // 2) API to view metadata
    @GetMapping(UPLOAD_ID_META)
//    @JsonView(JsonViews.CompleteView.class)
    @JsonView(JsonViews.MetaView.class)
    public Upload getMetaData(@PathVariable("id") Integer id) {
        final Upload one = fileRepository.findOne(id);
        if (one == null) {
            return null;
        } else return one;
    }

    // 3) API to download file
    @Async
    @ResponseBody
    @GetMapping(value = DOWNLOAD_UUID, produces = APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource getFile(@PathVariable("uuid") String uuid) {
        final Upload upload = fileRepository.findByUuid(uuid);
        fileRepository.incrementDownloadCount(upload.getId());
        final FileSystemResource fileSystemResource = new FileSystemResource(upload.getPath());
        return fileSystemResource;
    }

    // 4) API to search files
    @ResponseBody
    @JsonView(JsonViews.MetaView.class)
    @GetMapping(value = "upload/search", produces = APPLICATION_JSON_VALUE)
    public List<Upload> searchFile(@RequestParam("name") String name) {
        return uploadService.find(name);
    }

    public FileRepository getFileRepository() {
        return fileRepository;
    }

    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

}
