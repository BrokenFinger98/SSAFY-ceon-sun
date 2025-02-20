package com.chunsun.memberservice.domain.Entity;

import org.hibernate.annotations.Where;

import com.chunsun.memberservice.common.entity.BaseEntity;
import com.chunsun.memberservice.domain.Enum.Bank;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teachers")
@Getter
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Teacher extends BaseEntity {
	@Id
	private Long id;

	private String description;

	private String careerDescription;

	private String careerProgress;

	private String classContents;

	private Integer totalClassCount = 0;

	private Integer totalClassHours = 0;

	private Boolean isWanted;

	@Enumerated(EnumType.STRING)
	private Bank bank;

	private String account;

	private Integer price;

	@Version
	private Long Version;

	public Teacher(Member member, String description, String careerDescription, String careerProgress, String classContents, Boolean isWanted, Bank bank, String account, Integer price) {
		this.id = member.getId();
		this.description = description;
		this.careerDescription = careerDescription;
		this.careerProgress = careerProgress;
		this.classContents = classContents;
		this.isWanted = isWanted;
		this.bank = bank;
		this.account = account;
		this.price = price;
	}

	public void updateCard(String description, String careerDescription, String careerProgress, String classContents, Boolean isWanted, Bank bank, String account, Integer price) {
		if (description != null) this.description = description;
		if (careerDescription != null) this.careerDescription = careerDescription;
		if (careerProgress != null) this.careerProgress = careerProgress;
		if (classContents != null) this.classContents = classContents;
		if (isWanted != null) this.isWanted = isWanted;
		if (bank != null) this.bank = bank;
		if (account != null) this.account = account;
		if (price != null) this.price = price;
	}

	public void updateClass(Integer totalClassCount, Integer totalClassHours) {
		this.totalClassCount = totalClassCount;
		this.totalClassHours = totalClassHours;
	}
}
