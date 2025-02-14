package com.chunsun.memberservice.domain.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.chunsun.memberservice.domain.Enum.Bank;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "teachers")
@Getter
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Teacher {
	@Id
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Member member;

	private String description;

	private String careerDescription;

	private String classProgress;

	private String classContents;

	private Integer totalClassCount = 0;

	private Integer totalClassHours = 0;

	private Boolean isWanted;

	// 은행 추가 필요
	@Enumerated(EnumType.STRING)
	private Bank bank;

	private String account;

	private Integer price;

	@Version
	private Long Version;

	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	public Teacher(Member member, String description, String careerDescription, String classProgress, String classContents, Boolean isWanted, Bank bank, String account, Integer price) {
		this.member = member;
		this.id = member.getId();
		this.description = description;
		this.careerDescription = careerDescription;
		this.classProgress = classProgress;
		this.classContents = classContents;
		this.isWanted = isWanted;
		this.bank = bank;
		this.account = account;
		this.price = price;
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	public void updateCard(String description, String careerDescription, String classProgress, String classContents, Boolean isWanted, Bank bank, String account, Integer price) {
		if (description != null) this.description = description;
		if (careerDescription != null) this.careerDescription = careerDescription;
		if (classProgress != null) this.classProgress = classProgress;
		if (classContents != null) this.classContents = classContents;
		if (isWanted != null) this.isWanted = isWanted;
		if (bank != null) this.bank = bank;
		if (account != null) this.account = account;
		if (price != null) this.price = price;
		updatedAt = LocalDateTime.now();
	}

	public void updateClass(Integer totalClassCount, Integer totalClassHours) {
		this.totalClassCount = totalClassCount;
		this.totalClassHours = totalClassHours;
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
		this.member = null;
	}
}
