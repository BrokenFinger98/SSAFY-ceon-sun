package com.chunsun.memberservice.common.error;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCodes implements ErrorCode {

    /** =======================
     *  📌 공통 에러 (COMMON)
     *  ======================= */
    INTERNAL_SERVER_ERROR(HttpStatus.BAD_REQUEST, "COMMON4001", "내부 서버 오류"),
    NOT_FOUND_URL(HttpStatus.NOT_FOUND, "COMMON4002", "존재하지 않는 URL"),
    INVALID_JSON_DATA(HttpStatus.BAD_REQUEST, "COMMON4003", "잘못된 형식의 JSON 데이터"),
    INVALID_HEADER_DATA(HttpStatus.BAD_REQUEST, "COMMON4004", "잘못된 형식의 헤더 데이터"),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "COMMON4005", "잘못된 userId 형식"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON4011", "로그인이 필요합니다."),

    /** =======================
     *  📌 회원 관련 에러 (MEMBER)
     *  ======================= */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4041", "회원이 존재하지 않습니다."),
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4042", "학생 정보가 존재하지 않습니다."),
    TEACHER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4043", "강사 정보가 존재하지 않습니다."),
    GUEST_NOT_ALLOWED(HttpStatus.FORBIDDEN, "MEMBER4031", "GUEST는 이 기능을 사용할 수 없습니다."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, "MEMBER4001", "잘못된 성별 값입니다."),
    INVALID_AGE_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4003", "잘못된 연령대 형식입니다."),
    INVALID_AGE_RANGE(HttpStatus.BAD_REQUEST, "MEMBER4004", "잘못된 연령 범위입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER4091", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_KAKAO_ID(HttpStatus.CONFLICT, "MEMBER4092", "이미 가입된 카카오 ID입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER4093", "이미 가입된 이메일입니다."),
    ALREADY_DELETED_USER(HttpStatus.CONFLICT, "MEMBER4094", "이미 탈퇴한 회원입니다."),

    /** =======================
     *  📌 카테고리 관련 에러 (CATEGORY)
     *  ======================= */
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "CATEGORY4001", "해당 카테고리가 존재하지 않습니다."),
    DUPLICATE_CATEGORY(HttpStatus.CONFLICT, "CATEGORY4091", "이미 등록된 카테고리입니다."),

    /** =======================
     *  📌 교사 관련 에러 (TEACHER)
     *  ======================= */
    ALREADY_TEACHER(HttpStatus.CONFLICT, "TEACHER4091", "이미 선생 카드를 생성했습니다."),
    NOT_TEACHER(HttpStatus.FORBIDDEN, "TEACHER4031", "TEACHER 외 이 기능을 사용할 수 없습니다."),
    INVALID_CLASS_TIME(HttpStatus.BAD_REQUEST, "TEACHER4001", "수업 시간은 0보다 커야 합니다."),

    /** =======================
     *  📌 학생 관련 에러 (STUDENT)
     *  ======================= */
    ALREADY_STUDENT(HttpStatus.CONFLICT, "STUDENT4091", "이미 학생 카드를 생성했습니다."),
    NOT_STUDENT(HttpStatus.FORBIDDEN, "STUDENT4031", "STUDENT 외 이 기능을 사용할 수 없습니다."),


    INVALID_USER_HEADER_ID(HttpStatus.BAD_REQUEST, "MEMBER4032", "잘못된 유저 ID 입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
