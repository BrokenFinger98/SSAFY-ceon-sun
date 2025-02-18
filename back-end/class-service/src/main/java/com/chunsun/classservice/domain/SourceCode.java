package com.chunsun.classservice.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.chunsun.classservice.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "source_codes")
public class SourceCode extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "contracted_class_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ContractedClass contractedClass;

	@Lob
	@Column(name = "code_content", columnDefinition = "BLOB", nullable = false)
	private byte[] codeContent;

	public SourceCode(ContractedClass contractedClass, byte[] codeContent) {
		this.contractedClass = contractedClass;
		this.codeContent = codeContent;
	}
}
