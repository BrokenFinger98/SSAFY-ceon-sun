package com.chunsun.memberservice.domain.Entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.JOINED;

import java.time.LocalDate;

import com.chunsun.memberservice.common.entity.BaseEntity;
import com.chunsun.memberservice.domain.Enum.Gender;
import com.chunsun.memberservice.domain.Enum.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = JOINED)
@Entity
@Table(name = "members")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "kakao_id", nullable = false, unique = true, length = 255)
	private String kakaoId;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "nickname", nullable = false, unique = true, length = 50)
	private String nickname;

	@Enumerated(STRING)
	@Column(name = "gender")
	private Gender gender;

	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "profile_image", length = 255)
	private String profileImage;

	@Column(name = "birthdate", nullable = false)
	private LocalDate birthdate;

	@Enumerated(STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@Builder
	public Member(Role role, LocalDate birthdate, String profileImage, String email, Gender gender, String nickname,
		String name, String kakaoId) {
		this.role = role;
		this.birthdate = birthdate;
		this.profileImage = profileImage;
		this.email = email;
		this.gender = gender;
		this.nickname = nickname;
		this.name = name;
		this.kakaoId = kakaoId;
	}

	public void updateInfo(String nickname, String profile) {
		this.nickname = nickname;
		this.profileImage = profile;
	}
}
