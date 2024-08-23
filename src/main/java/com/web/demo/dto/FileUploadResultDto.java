package com.web.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 파일 업로드 처리후 응답 결과용 dto => json 형태로 보내짐
 */

@Getter
@Setter
public class FileUploadResultDto {
	private int code;        // -1: 에러, 1: 성공
	private String message;  // 메세지
	
	@Builder
	public FileUploadResultDto(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	

}
