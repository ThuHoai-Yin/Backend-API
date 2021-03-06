package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.exception.ExceptionCustom;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.FileResponse;
import com.example.demo.model.FileUpload;
import com.example.demo.model.InfoFile;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.InfoFileRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileService;
import com.example.demo.service.UserService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/admin")

public class AdminController {

	/** User service custom */
	@Autowired
	private UserService userServiceCustom;
	
	/** User service */
	@Autowired
	private UserRepository userService;
	
	/** Role service */
	@Autowired
	private RoleRepository roleService;
	
	/** File service */
	@Autowired
	private FileRepository fileRepository;
	
	/** File service custom */
	@Autowired
	private FileService fileService;
	
	/** Info code of page */
	private final String infoCode = "admin";

	/**
	 * Get all users
	 * 
	 * @return List<User>
	 */
	@GetMapping("/getUser")
	public List<User> findAllUser() {
		return userService.findAll();
	}

	/**
	 * Get all role
	 * 
	 * @return List<User>
	 */
	@GetMapping("/getRole")
	public List<Role> findAllRole() {
		return roleService.findAll();
	}

	/**
	 * Create role
	 * 
	 * @param role
	 * @return role
	 */
	@PutMapping
	public Role createRole(@RequestBody Role role) {
		return roleService.save(role);
	}

	/**
	 * Add role for user
	 * 
	 * @param username
	 * @param rolename
	 * @return User
	 */
	@PostMapping
	public CustomUserDetails addRoletoUser(@RequestParam String username, @RequestParam String rolename) {

		CustomUserDetails user = (CustomUserDetails) userServiceCustom.loadUserByUsername(username);
		Role role = roleService.findByName(rolename);
		user.addRoleToUser(role);
		userService.save(user.getUser());
		return user;

	}

	/**
	 * Update role
	 * 
	 * @param id
	 * @param role
	 * @return Role
	 */
	@PostMapping("/updateRole")
	public Role updateRole(@RequestParam int id, @RequestBody Role role) {
		
		Role findRole = roleService.getById(id);
		findRole = role;
		return roleService.save(findRole);

	}

	/**
	 * Upload file
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") List<MultipartFile> file)
			throws IllegalStateException, IOException {
		
		fileService.uploadFile(file, infoCode);
		return new ResponseEntity<>("File uploaded!", HttpStatus.OK);
		
	}

	/**
	 * Override file has existed
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@PostMapping("/overrideFile")
	public ResponseEntity<String> overrideFile(@RequestBody MultipartFile file) throws IllegalStateException, IOException {
		
		FileUpload fileUp = new FileUpload();
		fileUp.setFilename(file.getOriginalFilename());
		List<FileUpload> checkExist = fileRepository.findByFilename(fileUp.getFilename());
		for (FileUpload fileUpload : checkExist) {
			// check same info code, true:override file
			if (fileUpload.getCode_info_file().equals(infoCode)) {
				fileUp = fileUpload;
				fileUp.setFirst_update_date(new Date());
				fileRepository.save(fileUp);
				file.transferTo(new File("D:\\Spring Boot\\FileServer\\" + file.getOriginalFilename()));
				return new ResponseEntity<>("Override file success!", HttpStatus.OK);
			}
		}
		return new ResponseEntity<>("Don't find file to override!", HttpStatus.BAD_REQUEST);
		
	}

	/**
	 * Delete file by filename
	 * 
	 * @param filename
	 * @return ResponseEntity<String>
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteFile(@RequestParam("filename") String filename)
			throws IllegalStateException, IOException {
		
		fileService.deleteFile(filename, infoCode);
		return new ResponseEntity<>("Delete success!", HttpStatus.OK);
		
	}

	/**
	 * Get list uploaded file
	 * 
	 * @return List<FileResponse>
	 */
	@GetMapping("/files")
	public List<FileResponse> getListFile() {
		
		return fileService.getListFileByInfoCode(infoCode);
		
	}

	/**
	 * Delete all file in page
	 * 
	 */
	@DeleteMapping("/deleteAllFile")
	public void deleteAllFile() {
		
		fileRepository.deleteAll();
		
	}
}
