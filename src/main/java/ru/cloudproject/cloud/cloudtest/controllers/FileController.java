package ru.cloudproject.cloud.cloudtest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.cloudproject.cloud.cloudtest.dto.FileNameAndSizeDTO;
import ru.cloudproject.cloud.cloudtest.services.FileService;
import ru.cloudproject.cloud.cloudtest.utils.FilenameNotFoundException;
import ru.cloudproject.cloud.cloudtest.security.JwtUtil;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cloud")
public class FileController {
    private final FileService fileService;
    private final JwtUtil jwtUtil;

    @Autowired
    public FileController(FileService fileService, JwtUtil jwtUtil) {
        this.fileService = fileService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileNameAndSizeDTO>> getAllFiles(@RequestHeader(name = "Authorization") String token, Principal principal) {
        String email = principal.getName();
        String normalToken = token.substring(7);
        if (token == null || !email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FileNameAndSizeDTO> files = fileService.getFileList(email);
        return ResponseEntity.ok(files);
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader(name = "Authorization") String token, Principal principal) throws IOException {
        String email = principal.getName();
        String normalToken = token.substring(7);

        if (!email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            fileService.saveFile(file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam String filename, @RequestHeader(name = "Authorization") String token, Principal principal) {
        String email = principal.getName();
        String normalToken = token.substring(7);

        if (!email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized Error");
        }

        try {
            fileService.deleteFileByName(filename);
            return ResponseEntity.ok("Success deleted");
        } catch (FilenameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error input data");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error delete file");
        }
    }
    @PutMapping("/file")
    public ResponseEntity<?> editFileName(@RequestParam(name = "filename") String filename,
                                          @RequestBody Map<String, String> requestBody,
                                          @RequestHeader(name = "Authorization") String token,
                                          Principal principal) {
        String email = principal.getName();
        String normalToken = token.substring(7);

        if (!email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized Error");
        }

        try {
            String newName = requestBody.get("name");
            if (newName == null || newName.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Error input data");
            } else {
                fileService.updateFile(filename, newName);
                return ResponseEntity.ok("Success upload");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error update file");
        }
    }
    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestParam String filename, @RequestHeader(name = "Authorization") String token, Principal principal) {
        String email = principal.getName();
        String normalToken = token.substring(7);

        if (!email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized error");
        }

        try {
            byte[] fileBytes = fileService.downloadFile(filename);
            if (fileBytes == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("File not found");
            }

            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            return ResponseEntity.ok()
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error download file");
        }
    }
}