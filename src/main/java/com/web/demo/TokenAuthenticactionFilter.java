/**
 * TODO #0819 : 토큰 필터 구현
 * - 종류(아래 둘중 하나를 상속받아서 구현후 등록하여 사용)
 * 	- OncePerRequestFilter  : 동일한 요청에 대해서 한번만 필터링 하는 처리 용도
 *  - GenericFilter         : 일반적인 필터
 *  
 * - 토큰 필터에 DI 처리는 생성자 방식을 사용한다!
 * - TokenAuthenticactionFilter 객체가 만들어질때 주입 => 생성자의 파라미터로 세팅
 */

package com.web.demo;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.web.demo.entity.RefreshToken;
import com.web.demo.entity.SnsUser;
import com.web.demo.service.TokenProvider;
import com.web.demo.service.TokenService;
import com.web.demo.service.UserService;
import com.web.demo.service.UtilService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenAuthenticactionFilter extends OncePerRequestFilter{
	
	
	private final TokenProvider tokenProvider;
	// 생성자 방식으로 정의하면 줄줄이 수정해야함
	//@Autowired
	//private UtilService utilService;
	//@Autowired
	//private TokenService tokenService;
	
	// 상수
	private final static String HEADER_AUTH = "Authentication";
	private final static String ACCESS_TOKEN_PREFIX = "Bearer ";
	
	/**
	 * 필터링 내용 구현
	 * jwt 토큰 사용하여 인증 => Bearer 토큰 형식을 취함
	 * 	- http 헤더를 통해서 전달 -> 웹앱, spa(리액트 등) 사용 유저만 적용
	 * 		- Authentication : Bearer 토큰값
	 * 	- 여기는 쿠키 처리 방식으로 진행
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 1. 모든 요청들 중에서 그냥 통과할 내용들은 모두 통과
		//    정적 데이터, h2-console, /user, 기타 필요시 추가
		String url = request.getRequestURI();
//		System.out.println(url);
		if ( url.startsWith("/user") || url.startsWith("/dist")
				||url.startsWith("/plugins")||url.startsWith("/h2-console")
				||url.startsWith("/favicon.ico")) {
				// 요청을 url 매핑메소드에서 전달 (정상적인 요청이다)
				filterChain.doFilter(request, response);
				return;
		}
		//2. 쿠키 정보를 접근 => 토큰획득 => 유효성 검사 진행 => 시나리오 대응
		Cookie[] list = request.getCookies();
		String accessToken = "";
		String refreshToken = "";
//		if(list.length>=2) {   // for 활용 가능, 로직 변경 가능함
		
		for(Cookie cookie : list) {
			if(cookie.getName().equals("access_token")) {
				accessToken = cookie.getValue();
				
			}else if(cookie.getName().equals("refresh_token")) {
				refreshToken = cookie.getValue();
			}
		}
		// 웹앱, spa 등 서비스에서 진입했을 때 => restapi 방식으로 진입했을때
		if(accessToken.isEmpty()) {
			// 쿠기에서 토큰을 뽑아봤는데 없었다 => http 헤더에서 추출
			// 엑세스토큰만 체크, 리프레시는 생략
			String token_candidate = request.getHeader(HEADER_AUTH);
			if(token_candidate != null
					&& token_candidate.startsWith(ACCESS_TOKEN_PREFIX)) {
				accessToken = token_candidate.substring(ACCESS_TOKEN_PREFIX.length());
			}
		}
		System.out.println(accessToken);
		System.out.println(refreshToken);
		
		// 3. 엑세스 토큰이 유효한가
		if (tokenProvider.isValidToken(accessToken)) {  // 유효함 => 정상처리 => 인증정보 세팅해서 해당 페이지로 이동
			System.out.println("유효한 토큰");
			// 인증 정보 생성 후 -> 인증정보 관리 객체(시큐리티 컨텍스트 홀더)에 추가, 1회성
			// 원하는 페이지로 이동
			filterChain.doFilter(request, response);
		} else { // 유효하지 않다 => 리프레시 토큰 활용해서 재발급=> 이후 진행
			System.out.println("유효하지 않은 토큰");
			// 목표 : 엑세스 토큰 재발행
			// 리프레시 토큰 검증 진행(토큰 자체는 문제 없는지, 디비에 저장되어 있는지)
			if(!tokenProvider.isValidToken(refreshToken)) {
				System.out.println("리프레시 토큰 자체가 문제가 존재함");
				response.sendRedirect("/user/singup");
				return;
			}
			// 리프레시 오류나면 => 회원가입으로 유도
			if(!tokenProvider.isValidRefreshToken(refreshToken)) {
				System.out.println("리프레시 토큰이 디비에 등록되어 있지 않다");
				response.sendRedirect("/user/singup");
				return;
			}
			// 엑세스 토큰 재발행 => 쿠키저장 => 원하는 페이지로 이동
			// refreshToken => user id
//			RefreshToken rToken = tokenService.findByRefreshToken(refreshToken);
			// userid => SnsUser 객체 부터 획득
//			SnsUser user = userService.findById(rToken.getUserId());
			
//			accessToken  = this.tokenProvider.generateToken(user, Duration.ofDays(7));
			
			
			/*
			
			
			*/
			
			// 요청에 대한 처리 권한을 다른쪽(컨트롤러)으로 보내서 해당 컨트롤러에서 해결 
			response.sendRedirect("/user/reissue/"+refreshToken);

		}
		
		
	}
	
}
