package ru.cloudproject.cloud.cloudtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.cloudproject.cloud.cloudtest.dto.FileNameAndSizeDTO;
import ru.cloudproject.cloud.cloudtest.entities.File;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File,Long> {

    Optional<File> findByFileName(String filename);

    Boolean existsByFileName(String username);

    @Query("SELECT NEW ru.cloudproject.cloud.cloudtest.dto.FileNameAndSizeDTO(f.fileName, f.size) " +
            "FROM File f " +
            "WHERE f.user.email = :userEmail")
    List<FileNameAndSizeDTO> findAllFilenameAndSize(@Param("userEmail") String userEmail);
}