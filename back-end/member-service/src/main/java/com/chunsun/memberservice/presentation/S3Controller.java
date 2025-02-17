package com.chunsun.memberservice.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chunsun.memberservice.application.service.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping ("/s3")
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
		try {
			String result = s3Service.uploadImage(file);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("이미지 업로드 실패" +e.getMessage());
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteImage(@RequestParam("imageUrl") String imageUrl) {
		try {
			String result = s3Service.deleteImage(imageUrl);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("이미지 삭제 실패: " + e.getMessage());
		}
	}
}
