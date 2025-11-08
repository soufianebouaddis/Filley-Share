package os.org.filley.service;

import org.springframework.web.multipart.MultipartFile;
import os.org.filley.entity.FileData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public interface FilleyService {
    FileData storeFile(MultipartFile file) throws IOException ;
    FileData storeFile(String fileName, InputStream inputStream, String mimeType) throws IOException;
    List<FileData> getAllFiles() ;
    FileData getFileMetadata(Long id);
    InputStream getFileStream(Long id) throws IOException ;
    void deleteFile(Long id) throws IOException ;
    String formatFileSize(Long size) ;
}
