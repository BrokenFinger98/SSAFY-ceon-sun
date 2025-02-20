package com.chunsun.memberservice.domain.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.chunsun.memberservice.common.entity.BaseEntity;
import com.chunsun.memberservice.domain.Enum.Gender;
import com.chunsun.memberservice.domain.Enum.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "deleted_at IS NULL")
public class Member extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String kakaoId;

	private String name;

	private String nickname;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String email;

	private String profileImage;

	private LocalDate birthdate;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MemberCategory> memberCategories = new ArrayList<>();

	@Builder
	public Member(String kakaoId, String email, String name, String nickname, LocalDate birthdate, Role role,Gender gender) {
		this.kakaoId = kakaoId;
		this.email = email;
		this.name = name;
		this.nickname = nickname;
		this.birthdate = birthdate;
		this.role = role;
		this.gender = gender;
	}

	public Member(Long memberId) {
		this.id = memberId;
	}

	public void updateInfo(String nickname, String profileImage) {
		if(nickname != null) this.nickname = nickname;
		if(profileImage != null) this.profileImage = profileImage;
	}

	public void updateRole(Role role) {
		this.role = role;
	}

}
