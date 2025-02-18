package com.chunsun.memberservice.domain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student extends Member{
	@Column(name = "is_exposed", nullable = false)
	private Boolean isExposed;

	@Column(name = "description")
	private String description;
}
