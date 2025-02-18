package com.chunsun.classservice.presentation;

import static com.chunsun.classservice.application.dto.ServiceDto.*;
import static com.chunsun.classservice.presentation.dto.ControllerDto.*;
import static org.springframework.http.HttpStatus.CREATED;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.classservice.application.service.ClassService;
import com.chunsun.classservice.common.resolver.UserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/class")
@RestController
public class ClassController {

	private final ClassService classService;

	@PostMapping("/upload")
	public ResponseEntity<Void> uploadSourceCode(@Valid @RequestBody final UploadSourceCodeControllerRequest request) {
		final byte[] codeContent = request.sourceCode().getBytes(StandardCharsets.UTF_8);
		classService.saveSourceCode(request.contractedClassId(), codeContent);
		return ResponseEntity.status(CREATED).build();
	}

	@PostMapping("/download")
	public ResponseEntity<Resource> downloadSourceCode(
		@Valid @RequestBody final DownloadSourceCodeControllerRequest request) {
		final SearchSourceCodeResponse response = classService.searchSourceCode(request.sourceCodeId());
		final byte[] codeContent = response.codeContent();
		final ByteArrayResource resource = new ByteArrayResource(codeContent);

		final HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION,
			String.format("attachment; filename=\"source_code_%s_%s.txt\"", response.id(), response.createdAt()));
		return ResponseEntity
			.status(CREATED)
			.headers(headers)
			.contentLength(codeContent.length)
			.contentType(MediaType.TEXT_PLAIN)
			.body(resource);
	}

	@GetMapping("/sourceCodes/{contractedClassId}")
	public ResponseEntity<Page<SearchSourceCodesResponse>> searchSourceCodes(
		@PathVariable(name = "contractedClassId") final Long contractedClassId,
		@PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(classService.searchSourceCodes(contractedClassId, pageable));
	}

	@GetMapping("/lesson-records/{contractedClassId}")
	public ResponseEntity<Page<SearchLessonRecordResponse>> searchLessonRecords(
		@PathVariable(name = "contractedClassId") final Long contractedClassId,
		@PageableDefault(size = 10) Pageable pageable){
		return ResponseEntity.ok(classService.searchLessonRecords(contractedClassId, pageable));
	}

	@UserId
	@PatchMapping("/status/{contractedClassId}")
	public ResponseEntity<Void> updateStatus(
		@UserId final Long teacherId,
		@PathVariable(name = "contractedClassId") final Long contractedClassId) {
		classService.updateStatus(teacherId, contractedClassId);
		return ResponseEntity.noContent().build();
	}
}
