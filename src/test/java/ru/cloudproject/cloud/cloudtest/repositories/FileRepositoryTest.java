package ru.cloudproject.cloud.cloudtest.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.cloudproject.cloud.cloudtest.dto.FileNameAndSizeDTO;
import ru.cloudproject.cloud.cloudtest.entities.File;
import ru.cloudproject.cloud.cloudtest.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class FileRepositoryTest {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private File file;
    @BeforeEach
    public void init() {

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        userRepository.save(user);


        file = new File();
        file.setFileName("test.txt");
        file.setSize(1024);
        file.setUser(user);
    }

    @Test
    public void testFindByFileName() {

        fileRepository.save(file);


        Optional<File> foundFile = fileRepository.findByFileName(file.getFileName());


        Assertions.assertTrue(foundFile.isPresent());
        Assertions.assertEquals(file.getFileName(), foundFile.get().getFileName());
    }

    @Test
    public void testExistsByFileName() {

        fileRepository.save(file);


        Assertions.assertTrue(fileRepository.existsByFileName(file.getFileName()));
    }

    @Test
    public void testFindAllFilenameAndSize() {

        fileRepository.save(file);


        FileNameAndSizeDTO dto1 = new FileNameAndSizeDTO("file1.txt", 2048); // Размер 2048 байт
        FileNameAndSizeDTO dto2 = new FileNameAndSizeDTO("file2.txt", 4096); // Размер 4096 байт


        List<FileNameAndSizeDTO> files = fileRepository.findAllFilenameAndSize(user.getEmail());

         Assertions.assertEquals(1, files.size());

    }

}
