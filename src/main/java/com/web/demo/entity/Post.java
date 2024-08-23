package com.web.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Post {
	
	// json 변경시 무시, id에서는 미사용 -> 리뷰를 가져올려면 필요
//	@JsonIgnore
	@Id
	//오라클 11g미지원, 18 이상은 지원
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator1")
    @SequenceGenerator(name = "sequence_generator1", sequenceName = "sequence_name1", allocationSize = 1)
	private Integer id;
	
	@Column(length = 128)
	private String subject;
	
	// FIXME #REFACT: 오라클 지원형태로 변경(TEXT 제외)
	@Column(length = 1024)
	private String content;
	
	private LocalDateTime createDate;
	
	// json 변경시 무시
	@JsonIgnore
	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
	private List<Review> reviewList;

	
	public Post(String subject, String content, LocalDateTime createDate) {
		super();
		this.subject = subject;
		this.content = content;
		this.createDate = createDate;
	}


	public Post(Integer id, String subject, String content, LocalDateTime createDate) {
		super();
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.createDate = createDate;
	}
	
	
	
	
	// @Transient => 클레스 속성으로만 존재
	
	
}
