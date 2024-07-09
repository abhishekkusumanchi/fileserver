package com.abhishek.fileserver.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FileController {
	
	public FileController(){
		System.out.println("FileController bean created and placed in the IOC container");
	}
	

	@GetMapping(value = "/" )
	public String home(HttpServletRequest request) {
		System.out.println(request.getServletContext().getRealPath("home"));
		return "index";
	}
	
}
