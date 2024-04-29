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
import ru.cloudproject.cloud.cloudtest.utils.FilenameNotFoundException;
import ru.cloudproject.cloud.cloudtest.utils.UnauthorizedExeption;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/cloud")
public class FileController {
    private final FileService fileService;
    private final UserService userService;
    @Autowired
    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }




    @GetMapping("/list")
    public ResponseEntity<List<FileNameAndSizeDTO>> getAllFiles(Principal principal) {

            String email = principal.getName();

            List<FileNameAndSizeDTO> files = fileService.getFileList(email);



            return new ResponseEntity<>(files,HttpStatus.OK);

    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            fileService.saveFile(file);
            return ResponseEntity.ok("File uploaded successfully");

        } catch (UnauthorizedExeption e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Error("Unauthorized error"));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Error("Error upload file"));
        }

    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam String filename) {
        try {
            fileService.deleteFileByName(filename);
            return ResponseEntity.ok("Success deleted");
        } catch (FilenameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error input data");

        } catch (UnauthorizedExeption e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Unauthorized Error");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error delete file");
        }
    }
    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestParam String filename) {
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
        } catch (UnauthorizedExeption e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Error("Unauthorized error"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Error("Error download file"));
        }
    }



    @PutMapping("/file")
    public ResponseEntity<?> editFileName(@RequestParam(name = "filename") String filename,
                                          @RequestBody Map<String, String> requestBody) {
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

        } catch (UnauthorizedExeption e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Unauthorized Error");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Error update file");
        }
    }




}
