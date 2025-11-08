package os.org.filley.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import os.org.filley.entity.FileData;
import os.org.filley.repository.FilleyRepository;
import os.org.filley.service.FilleyService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FilleyServiceImpl implements FilleyService {

    private final FilleyRepository repository;
    private final Path uploadPath;

    public FilleyServiceImpl(FilleyRepository repository,
                              @Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.repository = repository;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }

    public FileData storeFile(String fileName, InputStream inputStream, String mimeType) throws IOException {
        String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String uniqueFileName = UUID.randomUUID().toString() + "_" + sanitizedFileName;
        Path targetLocation = this.uploadPath.resolve(uniqueFileName);

        // Get file size by copying stream
        long fileSize = Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

        FileData metadata = new FileData(
                fileName,
                uniqueFileName,
                fileSize,
                mimeType != null ? mimeType : "application/octet-stream"
        );

        return repository.save(metadata);
    }

    public FileData storeFile(MultipartFile file) throws IOException {
        return storeFile(file.getOriginalFilename(), file.getInputStream(), file.getContentType());
    }

    public List<FileData> getAllFiles() {
        return repository.findAllByOrderByUploadDateDesc();
    }

    public FileData getFileData(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
    }

    public InputStream getFileStream(Long id) throws IOException {
        FileData metadata = getFileData(id);
        Path filePath = uploadPath.resolve(metadata.getFilePath());
        return new FileInputStream(filePath.toFile());
    }

    public void deleteFile(Long id) throws IOException {
        FileData metadata = getFileData(id);
        Path filePath = uploadPath.resolve(metadata.getFilePath());
        Files.deleteIfExists(filePath);
        repository.deleteById(id);
    }

    public String formatFileSize(Long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }
    public FileData getFileMetadata(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
    }

}