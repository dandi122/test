/*
 * 네이버 영화 리뷰 데이터를 담는 테이블과 연관된 엔티티
 */
package com.web.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
public class NaverReviews {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator5")
    @SequenceGenerator(name = "sequence_generator5", sequenceName = "sequence_name5", allocationSize = 1)
	private Long id;
	
	@Column(length = 2048)
	private String docment;
	
	private Integer label;

}
