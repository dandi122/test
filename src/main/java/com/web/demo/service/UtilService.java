package com.web.demo.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//FIXME #REFACT: 유틸리티, 서비스 전반에 필요한 유틸리티, 중복기능 등을 제공하는 서비스
@Service
@Slf4j
public class UtilService {
	public void setCookie(String key, String value, int expiration, HttpServletResponse response) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(expiration);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	public int toSecoundOfDay(int day) {
		return 60*60*24*day;
	}
	
	// 미디어 타입에 대한 자동계산, 파일명 => 미디어 타입 추출
	public MediaType getMediaType(ServletContext servletContext, String filename) {
		// application/json, image/png, ....
		String mimeType = servletContext.getMimeType(filename);
		log.info(mimeType);
		try {
			MediaType mediaType = MediaType.parseMediaType(mimeType);
			return mediaType;
		} catch (Exception e) {
			// 마입타입 획득 오류
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}
}
