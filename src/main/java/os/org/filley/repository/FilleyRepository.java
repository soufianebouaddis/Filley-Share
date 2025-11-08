package os.org.filley.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import os.org.filley.entity.FileData;

public interface FilleyRepository extends JpaRepository<FileData, Long> {
    java.util.List<FileData> findAllByOrderByUploadDateDesc();
}
