package com.dajava.backend.domain.home;

import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Sample extends BaseTimeEntity {

	@Id @GeneratedValue
	private long id;
}
