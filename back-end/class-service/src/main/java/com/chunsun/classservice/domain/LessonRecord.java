package com.chunsun.classservice.domain;

import static jakarta.persistence.FetchType.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.chunsun.classservice.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "lesson_records")
public class LessonRecord extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "contracted_class_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ContractedClass contractedClass;

	@Column(name = "teacher_join_time")
	private LocalDateTime teacherJoinTime;

	@Column(name = "student_join_time")
	private LocalDateTime studentJoinTime;

	@Column(name = "teacher_exit_time")
	private LocalDateTime teacherExitTime;

	@Column(name = "student_exit_time")
	private LocalDateTime studentExitTime;

	@Column(name = "lesson_time")
	private LocalTime lessonTime;

	public LessonRecord(ContractedClass contractedClass) {
		this.contractedClass = contractedClass;
	}

	public void updateStudentJoinTime() {
		this.studentJoinTime = LocalDateTime.now();
	}

	public void updateTeacherJoinTime() {
		this.teacherJoinTime = LocalDateTime.now();
	}

	public void updateStudentExitTime() {
		this.studentExitTime = LocalDateTime.now();
	}

	public void updateTeacherExitTime() {
		this.teacherExitTime = LocalDateTime.now();
	}

	public void calculateLessonTime() {
		if (teacherJoinTime != null && studentJoinTime != null && teacherExitTime != null && studentExitTime != null) {
			LocalDateTime actualJoinTime = teacherJoinTime.isAfter(studentJoinTime) ? teacherJoinTime : studentJoinTime;
			LocalDateTime actualExitTime = teacherExitTime.isBefore(studentExitTime) ? teacherExitTime : studentExitTime;

			if (actualExitTime.isAfter(actualJoinTime)) {
				Duration overlap = Duration.between(actualJoinTime, actualExitTime);
				this.lessonTime = LocalTime.MIDNIGHT.plus(overlap);
			} else {
				this.lessonTime = LocalTime.MIDNIGHT;
			}
		}
	}
}
