package com.chunsun.memberservice.domain.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Getter
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Student {
	@Id
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Member member;

	private Boolean isExposed;

	private String description;

	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	@Version
	private Long Version;

	public Student(Member member, Boolean isExposed, String description) {
		this.member = member;
		this.id = member.getId();
		this.isExposed = isExposed;
		this.description = description;
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	public void updateCard(Boolean isExposed, String description) {
		if(isExposed != null) this.isExposed = isExposed;
		if(description != null) this.description = description;
		updatedAt = LocalDateTime.now();
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
		this.member = null;
	}
}
