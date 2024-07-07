package com.abhishek.fileserver.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileController {
	
	public FileController(){
		System.out.println("FileController bean created and placed in the IOC container");
	}
	

	@GetMapping(value = {"/home","/"})
	public String home() {
		return "index";
	}
	
}
