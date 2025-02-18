package com.chunsun.classservice.application.service;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;

public interface OpenViduService {

	Connection getSession(final Long memberId, final Long contractedClassId)
		throws OpenViduJavaClientException, OpenViduHttpException;

	void deleteSession(final Long memberId, final Long contractedClassId);
}
