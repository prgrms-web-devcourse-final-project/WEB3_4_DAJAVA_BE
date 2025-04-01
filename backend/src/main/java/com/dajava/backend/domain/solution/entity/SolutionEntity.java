package com.dajava.backend.domain.solution.entity;

import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;

/**
 * AI를 통해 제공받은 솔루션 데이터를 저장하는 JPA 엔티티입니다.
 * 솔루션 신청(Register)와 연관되며, 텍스트 데이터가 포함.
 * BaseTimeEntity를 통해 생성 및 수정 시간이 자동으로 관리됩니다.
 */
@Entity
@Table(name = "solution")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SolutionEntity extends BaseTimeEntity {
	/**
	 * 솔루션 엔티티의 고유 식별자
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 솔루션의 내용 (텍스트 형식)
	 */
	@Lob
	@Column(nullable = false)
	private String text;
	/**
	 * Register 엔티티와 1:1 관계
	 * SolutionEntity는 반드시 Register를 가져야 합니다.
	 */
	@OneToOne
	@JoinColumn(name = "register", nullable = false)
	private Register register;
}
