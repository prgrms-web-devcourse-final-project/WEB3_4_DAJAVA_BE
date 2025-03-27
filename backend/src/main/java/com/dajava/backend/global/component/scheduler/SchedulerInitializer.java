package com.dajava.backend.global.component.scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dajava.backend.domain.register.service.RegisterCacheService;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 서버 시작 직후 및 매 정각 + 1초 마다 Register 관련 캐싱 Set 을 Refresh 하는 스케쥴러입니다.
 * @author Metronon
 * @since 2025-03-27
 */
@WebListener
@Slf4j
public class SchedulerInitializer implements ServletContextListener {

	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("서비스 요청 데이터 캐싱 스케쥴러 작동");

		// RegisterCacheService 빈 획득
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		RegisterCacheService registerCacheService = context.getBean(RegisterCacheService.class);

		try {
			registerCacheService.refreshCache();
			log.info("서버 시작 Register 캐시 초기화 완료");
		} catch (Exception e) {
			log.error("Register 캐시 갱신 중 에러가 발생했습니다", e);
		}

		scheduler = Executors.newSingleThreadScheduledExecutor();

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextHour = now.plusHours(1).withMinute(0).withSecond(1).withNano(0);
		long initialDelay = Duration.between(now, nextHour).getSeconds();
		log.info("다음 Register 캐시 초기화까지 남은 시간: {} 초", initialDelay);

		scheduler.scheduleAtFixedRate(() -> {
			try {
				registerCacheService.refreshCache();
			} catch (Exception e) {
				log.error("Register 캐시 갱신 중 에러가 발생했습니다", e);
			}
		}, initialDelay, 3600, TimeUnit.SECONDS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdownNow();
			log.info("Register 캐시 스케쥴러가 정상적으로 종료되었습니다.");
		}
	}
}
