package os.org.filley.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@Getter
@Setter
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    public FileData() {
        this.uploadDate = LocalDateTime.now();
    }

    public FileData(String fileName, String filePath, Long fileSize, String contentType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploadDate = LocalDateTime.now();
    }

}
