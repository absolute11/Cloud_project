package ru.cloudproject.cloud.cloudtest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.cloudproject.cloud.cloudtest.dto.FileNameAndSizeDTO;
import ru.cloudproject.cloud.cloudtest.entities.File;
import ru.cloudproject.cloud.cloudtest.entities.User;
import ru.cloudproject.cloud.cloudtest.services.FileService;
import ru.cloudproject.cloud.cloudtest.services.UserService;
import ru.cloudproject.cloud.cloudtest.services.UserTokenService;
import ru.cloudproject.cloud.cloudtest.utils.FilenameNotFoundException;
import ru.cloudproject.cloud.cloudtest.utils.JwtUtil;
import ru.cloudproject.cloud.cloudtest.utils.UnauthorizedExeption;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/cloud")
public class FileController {
    private final FileService fileService;
    private final UserService userService;
    private final UserTokenService userTokenService;
    private final JwtUtil jwtUtil;
    @Autowired
    public FileController(FileService fileService, UserService userService,UserTokenService userTokenService,JwtUtil jwtUtil) {
        this.fileService = fileService;
        this.userService = userService;
        this.userTokenService = userTokenService;
        this.jwtUtil = jwtUtil;
    }




    @GetMapping("/list")
    public ResponseEntity<List<FileNameAndSizeDTO>> getAllFiles(@RequestHeader(name = "Authorization") String token,Principal principal) {
        String email = principal.getName();
        String normalToken = token.substring(7);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<FileNameAndSizeDTO> files = fileService.getFileList(email);



        return new ResponseEntity<>(files,HttpStatus.OK);

    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader(name = "Authorization") String token, Principal principal) throws IOException {
        String email = principal.getName();
        String normalToken = token.substring(7);

        if (!email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Error("Unauthorized error"));
        }

        try {
            fileService.saveFile(file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Error("Error upload file"));
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam String filename, @RequestHeader(name = "Authorization") String token, Principal principal) {
        String email = principal.getName();
        String normalToken = token.substring(7);

        if (!email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Unauthorized Error");
        }

        try {
            fileService.deleteFileByName(filename);
            return ResponseEntity.ok("Success deleted");
        } catch (FilenameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error input data");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error delete file");
        }
    }
    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestParam String filename, @RequestHeader(name = "Authorization") String token, Principal principal) {
        String email = principal.getName();
        String normalToken = token.substring(7);

        if (!email.equals(jwtUtil.validateTokenAndRetrieveSubject(normalToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Error("Unauthorized error"));
        }

        try {
            byte[] fileBytes = fileService.downloadFile(filename);
            if (fileBytes == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Error("File not found"));
            }

            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Error("Error download file"));
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
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Unauthorized Error");
        }

        try {
            String newName = requestBody.get("name");
            if (newName == null || newName.isEmpty()) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Error input data");
            } else {
                fileService.updateFile(filename, newName);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Success upload");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error update file");
        }
    }




}
