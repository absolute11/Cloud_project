package ru.cloudproject.cloud.cloudtest.controllers;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.cloudproject.cloud.cloudtest.dto.FileNameAndSizeDTO;
import ru.cloudproject.cloud.cloudtest.services.FileService;
import ru.cloudproject.cloud.cloudtest.utils.FilenameNotFoundException;
import ru.cloudproject.cloud.cloudtest.utils.JwtUtil;

import java.io.FileInputStream;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testGetAllFiles() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        List<FileNameAndSizeDTO> files = Collections.singletonList(new FileNameAndSizeDTO("test.txt", 100));
        when(fileService.getFileList("test@example.com")).thenReturn(files);


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");






        mockMvc.perform(get("/cloud/list")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal)) // Provide the mock Principal
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test.txt"))
                .andExpect(jsonPath("$[0].size").value(100));


        verify(fileService).getFileList("test@example.com");
    }

    @Test
    void testUploadFile_Successful() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");


        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/cloud/file")
                        .file(mockFile)
                        .header("Authorization", "Bearer " + token)
                        .principal(principal)) // Provide the mock Principal
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded successfully"));


        verify(fileService).saveFile(mockFile);
    }
    @Test
    void testUploadFile_Unauthorized() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("anotheruser@example.com");


        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());


        mockMvc.perform(MockMvcRequestBuilders.multipart("/cloud/file")
                        .file(mockFile)
                        .header("Authorization", "Bearer " + token)
                        .principal(principal))
                .andExpect(status().isUnauthorized());


        verify(fileService, never()).saveFile(mockFile);
    }
    @Test
    void testUploadFile_InternalServerError() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");


        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());


        doThrow(new RuntimeException("Error saving file")).when(fileService).saveFile(mockFile);


        mockMvc.perform(MockMvcRequestBuilders.multipart("/cloud/file")
                        .file(mockFile)
                        .header("Authorization", "Bearer " + token)
                        .principal(principal))
                .andExpect(status().isInternalServerError());



        verify(fileService).saveFile(mockFile);
    }

    @Test
    void testDeleteFile_Successful() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");


        mockMvc.perform(MockMvcRequestBuilders.delete("/cloud/file")
                        .param("filename", "test.txt")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("Success deleted"));


        verify(fileService).deleteFileByName("test.txt");
    }

    @Test
    void testDeleteFile_Unauthorized() throws Exception {
        // Mocking principal
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("anotheruser@example.com");


        mockMvc.perform(MockMvcRequestBuilders.delete("/cloud/file")
                        .param("filename", "test.txt")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal)) // Provide the mock Principal
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized Error"));


        verify(fileService, never()).deleteFileByName(anyString());
    }

    @Test
    void testDeleteFile_FilenameNotFound() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");


        doThrow(new FilenameNotFoundException("File not found")).when(fileService).deleteFileByName("test.txt");


        mockMvc.perform(MockMvcRequestBuilders.delete("/cloud/file")
                        .param("filename", "test.txt")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error input data"));


        verify(fileService).deleteFileByName("test.txt");
    }

    @Test
    void testDeleteFile_InternalServerError() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");


        doThrow(new RuntimeException("Error deleting file")).when(fileService).deleteFileByName("test.txt");


        mockMvc.perform(MockMvcRequestBuilders.delete("/cloud/file")
                        .param("filename", "test.txt")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal)) // Provide the mock Principal
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error delete file"));


        verify(fileService).deleteFileByName("test.txt");
    }
    @Test
    void testDownloadFile_Successful() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");


        byte[] fileContent = "Hello, world!".getBytes();
        when(fileService.downloadFile("test.txt")).thenReturn(fileContent);


        mockMvc.perform(get("/cloud/file")
                        .param("filename", "test.txt")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent));


        verify(fileService).downloadFile("test.txt");
    }
    @Test
    void testDownloadFile_Unauthorized() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("anotheruser@example.com");


        mockMvc.perform(get("/cloud/file")
                        .param("filename", "test.txt")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal)) // Provide the mock Principal
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized error"));


        verify(fileService, never()).downloadFile(anyString());
    }
    @Test
    void testDownloadFile_FileNotFound() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");


        when(fileService.downloadFile("test.txt")).thenReturn(null);


        mockMvc.perform(get("/cloud/file")
                        .param("filename", "test.txt")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(content().string("File not found"));


        verify(fileService).downloadFile("test.txt");
    }

    @Test
    void testDownloadFile_InternalServerError() throws Exception {

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");


        doThrow(new RuntimeException("Error downloading file")).when(fileService).downloadFile("test.txt");


        mockMvc.perform(get("/cloud/file")
                        .param("filename", "test.txt")
                        .header("Authorization", "Bearer " + token)
                        .principal(principal))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error download file"));


        verify(fileService).downloadFile("test.txt");
    }
    @Test
    public void testEditFileName_Success() throws Exception {

        String filename = "test.txt";
        String newName = "new_name.txt";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");


        String token = "my_mocked_token";
        when(jwtUtil.validateTokenAndRetrieveSubject(token)).thenReturn("test@example.com");
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", newName);
        mockMvc.perform(MockMvcRequestBuilders.put("/cloud/file")
                        .header("Authorization", "Bearer " + token)
                        .param("filename", filename)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody))
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("Success upload"));
    }

}