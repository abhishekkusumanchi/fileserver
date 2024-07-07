package com.abhishek.fileserver.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

@RestController
@RequestMapping("/file")
public class FileRestController {

	@PostMapping("/createFolder")
	public ResponseEntity<ResponseMessage> createFolder(@RequestParam String folderName,
			@RequestParam String folderPath) {
		String fullPath = File.separator + folderPath + File.separator + folderName;
		File newFolder = new File(fullPath);
		ResponseMessage response = new ResponseMessage();

		if (!newFolder.exists()) {
			boolean created = newFolder.mkdir();
			if (created) {
				response.setResult(true);
				response.setMessage("Folder created successfully.");
			} else {
				response.setResult(false);
				response.setMessage("Error creating folder.");
			}
		} else {
			response.setResult(false);
			response.setMessage("Folder already exists.");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/uploadFile")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam("path") String uploadPath) {
		Path uploadDir = Paths.get(uploadPath);
		ResponseMessage response = new ResponseMessage();

		try {
			if (!Files.exists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}
			Path filePath = uploadDir.resolve(file.getOriginalFilename());
			file.transferTo(filePath);
			response.setResult(true);
			response.setMessage("File uploaded successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			response.setResult(false);
			response.setMessage("Error uploading file.");
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/list")
	public ResponseEntity<List<FileDetails>> listFiles(@RequestParam String folderPath) {
		String fullPath = folderPath;
		File folder = new File(fullPath);
		List<FileDetails> fileList = new ArrayList<>();

		if (!folder.exists() || !folder.isDirectory()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		if (folderPath.toUpperCase().startsWith("C:") || folderPath.toUpperCase().startsWith("D:")) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		File[] files = folder.listFiles();
		if (files == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		for (File file : files) {
			FileDetails fileDetails = new FileDetails();
			fileDetails.setName(file.getName());
			fileDetails.setPath(file.getAbsolutePath().replace("\\", "/"));
			fileDetails.setType(file.isDirectory() ? "folder" : "file");
			fileList.add(fileDetails);
		}

		return new ResponseEntity<>(fileList, HttpStatus.OK);
	}

	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadFile(@RequestParam String filePath) {
		String fullPath = filePath;
		System.out.println(filePath);
		File file = new File(fullPath);

		if (!file.exists()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			byte[] fileContent = new byte[(int) file.length()];
			fileInputStream.read(fileContent);
			System.out.println("Filefound for download:" + filePath);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDispositionFormData("attachment", file.getName());
			headers.setContentType(Files.probeContentType(file.toPath()) != null
					? org.springframework.http.MediaType.parseMediaType(Files.probeContentType(file.toPath()))
					: org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
			return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
