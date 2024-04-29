package ru.cloudproject.cloud.cloudtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.cloudproject.cloud.cloudtest.dto.FileNameAndSizeDTO;
import ru.cloudproject.cloud.cloudtest.entities.File;
import ru.cloudproject.cloud.cloudtest.entities.User;
import ru.cloudproject.cloud.cloudtest.repositories.FileRepository;
import ru.cloudproject.cloud.cloudtest.repositories.UserRepository;
import ru.cloudproject.cloud.cloudtest.security.CustomUserDetails;
import ru.cloudproject.cloud.cloudtest.utils.FileProcessingException;
import ru.cloudproject.cloud.cloudtest.utils.FilenameNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    @Autowired
    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }




    public List<FileNameAndSizeDTO> getFileList(String email){
        return fileRepository.findAllFilenameAndSize(email);
    }

    public File saveFile(MultipartFile file) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> currentUser = Optional.ofNullable(userRepository.findUserByEmail(email));

        if(currentUser.isEmpty()){
            User newUser = new User();
            newUser.setPassword("password");
            newUser.setEmail("email2@gmail.com");
            userRepository.save(newUser);
            File newFile = new File();
            newFile.setFileName(file.getOriginalFilename());
            newFile.setSize((int) file.getSize());
            newFile.setFileData(file.getBytes());
            newFile.setUser(newUser);
            return fileRepository.save(newFile);
        }
        else{
            File newFile = new File();
            newFile.setFileName(file.getOriginalFilename());
            newFile.setSize((int) file.getSize());
            newFile.setFileData(file.getBytes());
            newFile.setUser(currentUser.get());
            return fileRepository.save(newFile);
        }


    }
    public File updateFile(String filename,String newFilename){
        Optional<File> optionalFile = fileRepository.findByFileName(filename);
        if(optionalFile.isPresent()){
            File existingFile = optionalFile.get();
            existingFile.setFileName(newFilename);
            return fileRepository.save(existingFile);
        }
        else{
            throw new FilenameNotFoundException("Filename " + filename + "not found");
        }
    }

    public void deleteFileByName(String filename){
        Optional<File> optionalFile = fileRepository.findByFileName(filename);
        if(optionalFile.isPresent()){
            fileRepository.delete(optionalFile.get());
        }
        else{
            throw new FilenameNotFoundException("Filename " + filename + "not found");
        }
    }

    public byte[] downloadFile(String filename){
        Optional<File> optionalFile = fileRepository.findByFileName(filename);
        if(optionalFile.isPresent()){
            File currentFile = optionalFile.get();
            byte[] fileBytes = currentFile.getFileData();
            return fileBytes;
        }
        else{
            return null;
        }
    }
}
