package com.dajava.backend.global.component.scheduler;

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

@WebListener
@Slf4j
public class SchedulerInitializer implements ServletContextListener {

	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("서비스 요청 데이터 캐싱 스케쥴러 작동");
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		RegisterCacheService registerCacheService = context.getBean(RegisterCacheService.class);

		scheduler = Executors.newSingleThreadScheduledExecutor();

		scheduler.schedule(() -> {
			try {
				registerCacheService.refreshCache();
			} catch (Exception e) {
				log.error("Register 캐시 갱신 중 에러가 발생했습니다", e);
			}
		}, 0, TimeUnit.SECONDS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (scheduler != null && scheduler.isShutdown()) {
			scheduler.shutdownNow();
			log.info("Register 캐시 스케쥴러가 정상적으로 종료되었습니다.");
		}
	}
}
