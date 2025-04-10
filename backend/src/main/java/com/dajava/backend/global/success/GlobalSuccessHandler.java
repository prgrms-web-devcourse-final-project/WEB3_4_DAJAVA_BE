// package com.dajava.backend.global.success;
//
// import static com.dajava.backend.global.exception.ErrorCode.*;
//
// import com.dajava.backend.domain.solution.exception.SolutionException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.core.MethodParameter;
// import org.springframework.http.MediaType;
// import org.springframework.http.converter.HttpMessageConverter;
// import org.springframework.http.server.ServerHttpRequest;
// import org.springframework.http.server.ServerHttpResponse;
// import org.springframework.web.bind.annotation.RestControllerAdvice;
// import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
// @RestControllerAdvice
// public class GlobalSuccessHandler implements ResponseBodyAdvice<Object> {
//
// 	private final ObjectMapper objectMapper = new ObjectMapper();
//
// 	@Override
// 	public boolean supports(MethodParameter returnType,
// 		Class<? extends HttpMessageConverter<?>> converterType) {
// 		return true;
// 	}
//
// 	@Override
// 	public Object beforeBodyWrite(Object body,
// 		MethodParameter returnType,
// 		MediaType selectedContentType,
// 		Class<? extends HttpMessageConverter<?>> selectedConverterType,
// 		ServerHttpRequest request,
// 		ServerHttpResponse response) {
//
// 		// 이미 감싸진 경우는 그대로 반환
// 		if (body instanceof SuccessData) {
// 			return body;
// 		}
//
// 		// String 응답은 따로 처리
// 		if (body instanceof String) {
// 			try {
// 				response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
// 				return objectMapper.writeValueAsString(SuccessData.create(body);
// 			} catch (Exception e) {
// 				throw new SolutionException(SUCCESS_DATA_TO_STRING_ERROR);
// 			}
// 		}
//
// 		// 일반 객체는 성공 코드와 함께 감싸서 반환
// 		return SuccessData.create(body);
// 	}
// }
