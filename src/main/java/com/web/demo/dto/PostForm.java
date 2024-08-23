package com.web.demo.dto;

import java.time.LocalDateTime;

import com.web.demo.entity.Post;

import groovy.transform.ToString;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostForm {
	
	@Transient
	private Integer id;
	
	@NotEmpty(message="제목은 반드시 입력해야 하는 필수 항목입니다.")
	// 디비상 테이블의 해당 컬럼의 크기와 같이 연동, 100Byte 이내 작성 가능
	@Size(max=100)
	private String subject;
	
	// null, 공백 => X
	@NotEmpty(message="본문 내용을 반드시 입력해야 하는 필수 항목입니다.")
	private String content;
	
	
	@Builder
	public PostForm(String subject, String content) {
		super();
		this.subject = subject;
		this.content = content;
	}
	
	//id는 자동생성, 생성시간은 현재시간, 에닡티 생성
	public Post toEntity() {
		return new Post(subject, content, LocalDateTime.now());
	}
	
	public Post toEntityModify() {
		return new Post(id, subject, content, LocalDateTime.now());
	}
	
	
	
	
}
