package com.chunsun.memberservice.common.util;

import java.util.Objects;

import com.chunsun.memberservice.common.error.GlobalErrorCodes;
import com.chunsun.memberservice.common.exception.BusinessException;

public class HeaderUtil {
	public static void validateUserId(Long pathId, Long headerId) {
		if(!Objects.equals(pathId, headerId)) {
			throw new BusinessException(GlobalErrorCodes.INVALID_USER_HEADER_ID);
		}
	}
}
