package com.abhishek.fileserver.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abhishek.fileserver.models.FileDetails;
import com.abhishek.fileserver.models.ResponseMessage;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
public class FileRestController {

    private static final Logger logger = Logger.getLogger(FileRestController.class.getName());

    @PostMapping("/createFolder")
    public ResponseEntity<ResponseMessage> createFolder(@RequestParam String folderName,
            @RequestParam String folderPath, HttpServletRequest request) {
        String fullPath = request.getServletContext().getRealPath("static").split("static")[0]+"WEB-INF/classes/static"  + File.separator + folderPath + File.separator + folderName;
        File newFolder = new File(fullPath);
        ResponseMessage response = new ResponseMessage();

        if (!newFolder.exists()) {
            boolean created = newFolder.mkdir();
            if (created) {
                response.setResult(true);
                response.setMessage("Folder created successfully.");
                logger.info("Folder created: " + fullPath);
            } else {
                response.setResult(false);
                response.setMessage("Error creating folder.");
                logger.warning("Error creating folder: " + fullPath);
            }
        } else {
            response.setResult(false);
            response.setMessage("Folder already exists.");
            logger.warning("Folder already exists: " + fullPath);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("path") String uploadPath, HttpServletRequest request) {
        Path uploadDir = Paths.get(request.getServletContext().getRealPath("static").split("static")[0]+"WEB-INF/classes/static"  + File.separator + uploadPath);
        ResponseMessage response = new ResponseMessage();

        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(file.getOriginalFilename());
            file.transferTo(filePath);
            response.setResult(true);
            response.setMessage("File uploaded successfully.");
            logger.info("File uploaded: " + filePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            response.setResult(false);
            response.setMessage("Error uploading file.");
            logger.severe("Error uploading file: " + e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDetails>> listFiles(@RequestParam String folderPath, HttpServletRequest request) {
        String fullPath = request.getServletContext().getRealPath("static").split("static")[0]+"WEB-INF/classes/static"  + File.separator + folderPath;
        logger.info("List files method: " + fullPath);

        File folder = new File(fullPath);
        List<FileDetails> fileList = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        for (File file : files) {
            FileDetails fileDetails = new FileDetails();
            fileDetails.setName(file.getName());
            fileDetails.setPath(file.getAbsolutePath().replace("\\", "/").split("static")[1].substring(1));
            fileDetails.setType(file.isDirectory() ? "folder" : "file");
            fileList.add(fileDetails);
        }

        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filePath, HttpServletRequest request) {
        String fullPath = request.getServletContext().getRealPath("static").split("static")[0]+"WEB-INF/classes/static" + File.separator + filePath;
        logger.info("Download file method: " + fullPath);

        File file = new File(fullPath);

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);
            logger.info("File found for download: " + filePath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", file.getName());
            headers.setContentType(Files.probeContentType(file.toPath()) != null
                    ? org.springframework.http.MediaType.parseMediaType(Files.probeContentType(file.toPath()))
                    : org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error downloading file: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("filePath") String filePath, HttpServletRequest request) {
        logger.info("Delete controller got invoked");
        filePath = request.getServletContext().getRealPath("static").split("static")[0]+"WEB-INF/classes/static" + File.separator + filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.status(404).body("File or folder not found");
        }

        try {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
            logger.info("File or folder deleted: " + filePath);
            return ResponseEntity.ok("File or folder deleted successfully");
        } catch (IOException e) {
            logger.severe("Error deleting file or folder: " + e.getMessage());
            return ResponseEntity.status(500).body("Error deleting file or folder: " + e.getMessage());
        }
    }

    private void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }

        directory.delete();
    }
}
