package os.org.filley.web;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import os.org.filley.entity.FileData;
import os.org.filley.service.FilleyService;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/files")
public class FilleyController {

    private final FilleyService fileService;

    public FilleyController(FilleyService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) {
        try {
            FileData metadata = fileService.getFileMetadata(id);
            InputStream inputStream = fileService.getFileStream(id);

            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(metadata.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + metadata.getFileName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
