package ru.cloudproject.cloud.cloudtest.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.cloudproject.cloud.cloudtest.dto.FileNameAndSizeDTO;
import ru.cloudproject.cloud.cloudtest.entities.File;
import ru.cloudproject.cloud.cloudtest.entities.User;
import ru.cloudproject.cloud.cloudtest.repositories.FileRepository;
import ru.cloudproject.cloud.cloudtest.repositories.UserRepository;
import ru.cloudproject.cloud.cloudtest.services.FileService;
import ru.cloudproject.cloud.cloudtest.utils.FilenameNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FileService fileService;

    @Test
    public void testSaveFile() throws IOException {

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());

        // Mock Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken("test@gmail.com", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock userRepository behavior
        when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(new User());

        // Mock fileRepository behavior
        when(fileRepository.save(any(File.class))).thenReturn(new File());

        // Test
        assertDoesNotThrow(() -> fileService.saveFile(multipartFile));
    }

    @Test
    public void testUpdateFile() {

        when(fileRepository.findByFileName("test.txt")).thenReturn(Optional.of(new File()));

        assertDoesNotThrow(() -> fileService.updateFile("test.txt", "newTest.txt"));
    }

    @Test
    public void testUpdateNonExistingFile() {
        FileService fileService = new FileService(fileRepository, userRepository);

        // Mock fileRepository behavior
        when(fileRepository.findByFileName("test.txt")).thenReturn(Optional.empty());

        // Test
        assertThrows(FilenameNotFoundException.class, () -> fileService.updateFile("test.txt", "newTest.txt"));
    }



    @Test
    public void testGetFileList() {

        // Arrange
        String userEmail = "test@gmail.com";
        List<FileNameAndSizeDTO> expectedList = new ArrayList<>();
        expectedList.add(new FileNameAndSizeDTO("test1.txt", 100));
        expectedList.add(new FileNameAndSizeDTO("test2.txt", 200));


        when(fileRepository.findAllFilenameAndSize(userEmail)).thenReturn(expectedList);


        List<FileNameAndSizeDTO> resultList = fileService.getFileList(userEmail);

        // Assert
        assertEquals(expectedList.size(), resultList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            FileNameAndSizeDTO expectedDto = expectedList.get(i);
            FileNameAndSizeDTO resultDto = resultList.get(i);
            assertEquals(expectedDto.getName(), resultDto.getName());
            assertEquals(expectedDto.getSize(), resultDto.getSize());
        }
    }

    @Test
    public void testDeleteFileByName_FileNotExists() {

        // Arrange
        String filename = "nonexistent.txt";
        when(fileRepository.findByFileName(filename)).thenReturn(Optional.empty());

        // Act & Assert
        FilenameNotFoundException exception = assertThrows(FilenameNotFoundException.class,
                () -> fileService.deleteFileByName(filename));
        assertEquals("Filename " + filename + "not found", exception.getMessage());
        verify(fileRepository, never()).delete(any());
    }

    @Test
    public void testDownloadFile_FileDoesNotExist_ReturnNull() {

        // Mock data
        String filename = "non_existing_file.txt";

        // Mock repository behavior
        when(fileRepository.findByFileName(filename)).thenReturn(Optional.empty());

        // Test
        byte[] downloadedFileBytes = fileService.downloadFile(filename);

        // Assertion
        assertNull(downloadedFileBytes);
    }
}
