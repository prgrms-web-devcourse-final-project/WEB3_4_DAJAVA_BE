package com.dajava.backend.domain.event;

import java.util.ArrayList;
import java.util.List;

import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SolutionData extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String serialNumber;

	@OneToMany(mappedBy = "solutionData", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PointerEvent> pointerEvents = new ArrayList<>();


	// 정적 팩토리 메서드
	public static SolutionData create(String serialNumber) {

		return SolutionData.builder()
			.serialNumber(serialNumber)
			.build();

	}

	public void addPointerEvents(List<PointerEvent> events) {
		if (events == null) return;

		for (PointerEvent event : events) {
			this.pointerEvents.add(event);
		}
	}
}
