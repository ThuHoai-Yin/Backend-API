package com.example.demo.service.imp;

import org.springframework.stereotype.Service;

import com.example.demo.model.FileUpload;
import com.example.demo.model.InfoFile;
import com.example.demo.service.InfoFileService;
@Service
public class InfoFileServiceImp implements InfoFileService{

	@Override
	public boolean checkFile(FileUpload file, InfoFile inforFile) {
		if(!inforFile.getExtension().contains(file.getExtension())) {
			return false;
		}
		if(file.getSize()>inforFile.getMaxSize()) {
			return false;
		}
		return true;
	}

}
