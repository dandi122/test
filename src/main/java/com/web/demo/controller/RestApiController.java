package com.web.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.demo.dto.FileUploadResultDto;
import com.web.demo.dto.PostForm;
import com.web.demo.entity.Post;
import com.web.demo.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestApiController {
	
	private final PostService postService;
	
	//get
	//3분, 아래 포스트에서 특정 번호 입력하면 데이터를 가져오는 메소드 구현, 필요시 글 미리 등록
	// 문제점
	//	-본글에 참조키로 연결된 리뷰 데이터가 존재할 경우 응답값이 무한반복이 됨 -> 본문따로, 리뷰따로(페이징)
	//	- 본글만 있으면 문제 없음
	// 	-노출하면 안되는 값들도 모두 전달된다 -> 무시전략(설정 json 변경시 노출 안되게 처리)
	@GetMapping("/post/{id}")
	@Tag(name="포스트게시판", description = "Post 엔티티를 이용한 Post조회 api")
	public Post post(@PathVariable("id") Integer id) {
		
		Post p = postService.getOnePost(id);
		
		return p;
	}
	
	
	
	
	//Post => postForm으로 대체  : 보안상의 문제로 컨트롤러에서는 엔티티가 노출되면 안됨 -> dto로 대체, 엔티티 전체가 아닌 필요한 부분만 노출 
	//DTO를 사용시 원하는 데이터만 추출 가능(통제가능)
	@GetMapping("/post2/{id}")
	@Tag(name="포스트게시판", description = "Post DTO를 이용한 Post조회 api")
	public PostForm post2(@PathVariable("id") Integer id) {
		
		PostForm p = postService.getOnePostForm(id);
		
		return p;
	}
	
	@GetMapping("/post3/{id}")
	//@Tag : 같은 비즈니스 계열 api간 그룹화 처리 가능
	@Tag(name="포스트게시판", description = "ResponseEntity를 이용한 Post조회 api")
	@Parameters({
		@Parameter(name="id", description = "포스트 글의 고유번호값", example = "1")
	})
	// 통신 성공후, 응답 데이터에 대한 의미를 코드로 전달할때, 그 값을 체크하는 용도
	// 통신 프로토콜표 = 전문 = TR
	@ApiResponses(value={
			@ApiResponse(responseCode = "1000", description = "정상적인 통신 성공"),
			@ApiResponse(responseCode = "2000", description = "사용자 인증 오류"),
			@ApiResponse(responseCode = "3000", description = "서버측 장애")
	})
	// 그룹내 api별 구분용도 사용
	@Operation(summary="포스트 정보 조회(헤더조작가능)", description = "포스트 내용 조회후 응답시 헤더를 커스텀하게 조정할 수 있다")
	// ResponseEntity는 사용하면 -> 응답 헤더 조정 가능함
	public ResponseEntity<Post> post3(@PathVariable("id") Integer id) {
		
		Post p = postService.getOnePost(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(p);
	}
	
	
	//post -> post 등록
	@PostMapping("/post")
	@Tag(name="포스트게시판", description = "post등록")
	@Operation(summary = "Post 등록")
	public Post create(@RequestBody PostForm postForm) {
		// PostForm -> Post 엔티티 객체 -> toEntity()
		Post p = postForm.toEntity();
		// 디비 입력
		return postService.createEx(p);
	}
	
	//put -> post 수정(patch, 스프링 미지원)
	//post 조회 -> 수정: (post 번호, 제목, 내용)
	@PutMapping("/post")
	@Tag(name="포스트게시판", description = "post 수정")
	@Operation(summary = "Post 수정")
	public FileUploadResultDto update(@RequestBody PostForm postForm) {
		Post p = postForm.toEntityModify();
		postService.modify(p);
		return FileUploadResultDto.builder().code(1).message("수정성공").build();
	}
	
	//delete -> post 삭제
	@DeleteMapping("/post/{id}")
	@Tag(name="포스트게시판", description = "post 삭제")
	@Operation(summary = "Post 삭제")
	public FileUploadResultDto delete(@PathVariable("id") Integer id) {
		Post p = postService.getOnePost(id);
		//p가 없는 경우 예외처리 필요(생략)
		postService.delete(p);
		return FileUploadResultDto.builder().code(1).message("삭제성공").build();
	}
	
}
