package com.chunsun.memberservice.application.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

	String uploadImage(MultipartFile image) throws IOException;

	String deleteImage(String imageURL);
}
