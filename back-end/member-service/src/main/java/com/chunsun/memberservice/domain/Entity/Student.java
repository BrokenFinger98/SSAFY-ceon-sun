package com.chunsun.memberservice.domain.Entity;

import org.hibernate.annotations.Where;

import com.chunsun.memberservice.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Getter
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Student extends BaseEntity {
	@Id
	private Long id;

	private Boolean isExposed;

	private String description;

	@Version
	private Long Version;

	public Student(Member member, Boolean isExposed, String description) {
		this.id = member.getId();
		this.isExposed = isExposed;
		this.description = description;
	}

	public void updateCard(Boolean isExposed, String description) {
		if(isExposed != null) this.isExposed = isExposed;
		if(description != null) this.description = description;
	}
}
