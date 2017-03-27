package com.example.entity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

public interface FileRepository extends CrudRepository<Upload, Integer> {
    Upload findByUuid(String fileName);

    @Modifying
    @Transactional
    @Query("UPDATE Upload upload SET upload.downloads = upload.downloads + 1 WHERE upload.id = :uploadId")
    void incrementDownloadCount(@Param("uploadId") int uploadId);

    List<Upload> findByOriginalFileNameContaining(String nameLike);

    List<Upload> findByIdGreaterThanEqual(int id);

}
