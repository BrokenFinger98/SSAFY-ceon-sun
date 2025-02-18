package com.chunsun.classservice.domain;

import static com.chunsun.classservice.domain.Status.*;
import static jakarta.persistence.EnumType.*;

import com.chunsun.classservice.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "contracted_class")
public class ContractedClass extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "teacher_id", nullable = false)
	private Long teacherId;

	@Column(name = "student_id", nullable = false)
	private Long studentId;

	@Column(name = "remain_class", nullable = false)
	private Integer remainClass;

	@Enumerated(STRING)
	private Status status;

	public ContractedClass(Long teacherId, Long studentId){
		this.teacherId = teacherId;
		this.studentId = studentId;
		this.remainClass = 0;
		this.status = PROGRESS;
	}

	public void UpdateRemainClass(Integer count){
		this.remainClass += count;
		this.status = PROGRESS;
	}

	public void DecreaseRemainClass(){
		if(remainClass > 0){
			this.remainClass--;
		}
	}

	public void EndContractedClass(){
		this.status = END;
	}
}
