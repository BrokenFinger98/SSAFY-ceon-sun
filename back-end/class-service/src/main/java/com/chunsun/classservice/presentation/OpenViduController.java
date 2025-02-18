package com.chunsun.classservice.presentation;

import static com.chunsun.classservice.presentation.dto.ControllerDto.*;
import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.classservice.application.service.ClassService;
import com.chunsun.classservice.application.service.OpenViduService;
import com.chunsun.classservice.common.resolver.UserId;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/openvidu")
@RestController
public class OpenViduController {

	private final OpenViduService openViduService;

	@UserId
	@PostMapping("/sessions/{contractedClassId}")
	public ResponseEntity<GetSessionResponse> getSession(
		@UserId final Long memberId,
		@PathVariable final Long contractedClassId) throws OpenViduJavaClientException, OpenViduHttpException {
		final Connection connection = openViduService.getSession(memberId, contractedClassId);
		return ResponseEntity.status(CREATED).body(new GetSessionResponse(connection.getToken()));
	}

	@UserId
	@DeleteMapping("/sessions/{contractedClassId}")
	public ResponseEntity<Void> deleteSession(@UserId final Long memberId, @PathVariable final Long contractedClassId) {
		openViduService.deleteSession(memberId, contractedClassId);
		return ResponseEntity.noContent().build();
	}
}
