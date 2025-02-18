package com.chunsun.memberservice.domain.Entity;

import static jakarta.persistence.EnumType.STRING;

import com.chunsun.memberservice.domain.Enum.Bank;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher extends Member{

	@Column(name = "description")
	private String description;

	@Column(name = "career_description")
	private String careerDescription;

	@Column(name = "class_contents")
	private String classContents;

	@Column(name = "class_progress")
	private String classProgress;

	@Column(name = "total_class_count", nullable = false)
	private Integer totalClassCount;

	@Column(name = "total_class_hours", nullable = false)
	private Integer totalClassHours;

	@Column(name = "is_wanted", nullable = false)
	private Boolean isWanted;

	@Enumerated(STRING)
	@Column(name = "bank", nullable = false)
	private Bank bank;

	@Column(name = "account", nullable = false, length = 20)
	private String account;

	@Column(name = "price", nullable = false)
	private Integer price;
}
