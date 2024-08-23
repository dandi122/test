package com.web.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.web.demo.entity.ChatbotSheet;
import com.web.demo.entity.NaverReviews;
import com.web.demo.repository.NaverReviewRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class NaverReviewsService {
	@Autowired
	private NaverReviewRepository naverReviewRepository;
	
	// 페이징 계산시 기준이 되는 페이지당 게시물의 개수 => 환경변수도 가능, 10개로 설정
	private final int PAGE_SIZE = 10;
	
	/**
	 * 입력 : 특정 페이지 번호
	 * 결과 : 해당페이지에 속한 게시물 데이터(DTO제공, 페이징 객체를 활용 제공)
	 */
	
	public Page<NaverReviews> getList(int Page) {
		
		/**
		 * 기능정리
		 * PageRequest -> 페이지번호, 페이지당 데이터개수, 정렬조건, .. -> SQL 생성
		 * Pageable -> sql 실행하는 기능담당, 레포지토리가 sql실행시 관여 -> 결과를 제공
		 * Page -> 페이지 번호, 페이지당 데ㅐ이터 개수, 정렬조건 부함하는 결과 셋을 가지고 있다
		 * 차후 보안 이슈로 조정 => naverReviews는 엔티티이다 DTO로 대체처리 혹은 교환 처리
		 */
		// 데이터 정렬은 미포함 상태
		// 내부적으로 특정 위치에서 특저 ㅇ개수만큼 JPA를 통해 데이터를 획득하여 Pageable 형태로 반환
		Pageable pageable = PageRequest.of(Page, PAGE_SIZE);
		return naverReviewRepository.findAll(pageable);
		
	}
	
	public Page<NaverReviews> getList2(int Page, String keyword) {
		// 1. 정렬(요구사항에는 없었지만 추가) 
		// order by a, b, c.... desc(or asc)
		List<Sort.Order> sorts = new ArrayList<>();
		// 검색 결과를 id 값 기준으로 내림차순 정렬
		sorts.add(Sort.Order.desc("id"));
		// 추가할려면  ....add(), add(),  ..... 조건추가
		Pageable pageable = PageRequest.of(Page, PAGE_SIZE, Sort.by(sorts));
		
		// 2. 검색 처리하는 함수 별도 구성 -> 필요시 조인, where 조건을 여러개, 집계 -> 그룹화, having등등 사용
		// 	  Specification 객체가 복잡한 조회 sql select문 구성하는 요소
		Specification<NaverReviews> sf = complexSearch(keyword);
		
		return naverReviewRepository.findAll(sf, pageable);
		
	}
	
	
	/**
	 * 
	 * @param keyword
	 * @return : 복잡한 쿼리문을 대변하는 Specification<NaverReviews> 객체
	 */
	private Specification<NaverReviews> complexSearch(String keyword) {
		// TODO Auto-generated method stub
		return new Specification<>(){
			
			/**
			 * 
			 * @param root : 기준이 되는 엔티티 객체
			 * @param query : 쿼리에 추가설정 작업
			 * @param criteriaBuilder : 
			 * @return
			 */
			@Override
			public Predicate toPredicate(Root<NaverReviews> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				// 중복제거 => distinct
				query.distinct(true);; // 결과값중 중복 제거
				// 조인이 필요하면, NverReviews와 ChatbotSheet간 left 조인 수행,
				// label컬럼 값이 일치하는 ChatbotSheet 데이터 + NverReviews 모든 데이터
				// Join<NaverReviews, ChatbotSheet> r2 = root.join("label", JoinType.LEFT);
				
				// 해당 컬럼에 검색어가 존재만 하면 다 가져온다
				// like => %검색어, 검색어%, %검색어%
				return criteriaBuilder.like(root.get("docment"), "%"+keyword+"%");
				
//				return criteriaBuilder.or(
//							criteriaBuilder.like(root.get("docment"), "%"+keyword+"%"),
//							criteriaBuilder.like(r2.get("question"), "%"+keyword+"%"),
//							criteriaBuilder.like(r2.get("answer"), "%"+keyword+"%")
//						)
//						
			}
			
		};
	}
	
	
}
