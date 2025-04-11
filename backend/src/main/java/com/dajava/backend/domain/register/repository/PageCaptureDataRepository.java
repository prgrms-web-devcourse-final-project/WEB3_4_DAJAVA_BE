package com.dajava.backend.domain.register.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dajava.backend.domain.register.entity.PageCaptureData;

public interface PageCaptureDataRepository extends JpaRepository<PageCaptureData, Long> {

	PageCaptureData findByPageUrl(String pageCaptureUrl);
}
