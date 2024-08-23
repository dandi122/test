/**
 * 파일 업로드 화면 제공
 * 파일 업로드 실제 처리 제공
 * 파일 다운로드 실제 처리 제공
 * 단, 클라우드상 스토리지 저장은 생략, 데이터베이스 연동 생략
 * url prefix/file, 컨트롤러 설정
 * 테스트 편의상 jwt, csrf 설정 고려
 */


package com.web.demo.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.web.demo.dto.FileUploadResultDto;
import com.web.demo.dto.MemberDto;
import com.web.demo.service.UtilService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

// 롬복에서 제공하는 자바 로깅 API
@Slf4j
@Controller
@RequestMapping("/file")
public class FileApiController {
	
	@Autowired
	private UtilService utilService;
	@Autowired
	private ServletContext servletContext;
	
	
	// 파일 업로드 화면 제공
	// ~/file/upload
	@GetMapping("/upload")
	public String upload() {
		return "file_process";   // file_process.html 생성
		
	}
	// 파일 업로드 실제 처리 제공
	// 일반데이터 + 파일 동시 전송
	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<FileUploadResultDto> upload(
//			MemberDto member,      // 나이, 이름, 주소 정보를 통으로 받아서 전달
			@RequestParam("uploadFile") MultipartFile uploadFile,          // 파일을 받아서 전달
			HttpServletRequest req) {
		// 1. 파일 저장할 경로 획득(여기서는 톰켓 내부, 실제는 크라우드의 스토리지 선택)
		String path = req.getServletContext().getRealPath("");
		System.out.println("path:" + path);
		String filename = uploadFile.getOriginalFilename(); // 파일명
		System.out.println("filename:" + filename);
		
		// 2. 저장 -> 클라우드상의 스토리지에 저장 권장
		// i/o => 반드시 예외 상황이 발생할될 수 있다(파일io, 네트워크io, 디비io...) => 예외처리
		try {
			// 원래는 원본파일명, 변조파일명(해싱처리, 사용자 계정 혹은 인증정보, 시간정보 같이 섞어서 구성)
			// 클라우드에 올리면(firebase에 업로드하면 자동 변조됨), s3는 직접 변조 필요
			log.info(path + "/" + filename);
			File file = new File(path +"/"+ filename);
			// 자바프록르ㅐㅁ(판단기준): 업로드된 파일은 메모리에 존재(객체형태) -> 스트림오픈 -> 하드디스크로 데이터 전송
			BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(file));
			// MultipartFile -> 출력스트림 -> file
			bos.write(uploadFile.getBytes());
			// bos.flush(); 내부적으로 자동 flush에서 강제로 쏜다(대표적인 system.out.println())
			bos.close();
			
		} catch (Exception e) {
			log.error("파일 저장시 오류:" + e.getMessage());
			FileUploadResultDto res = FileUploadResultDto.builder().code(-1).message(e.getMessage()).build();
			return ResponseEntity.status(HttpStatus.OK).body(res);
		}
		
		// 3. 결과 응답 -> dto -> json
//		return "응답";
		FileUploadResultDto res = FileUploadResultDto.builder().code(1).message(filename).build();
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	// 파일 다운로드 실제 처리 제공
	@GetMapping("/download")
	@ResponseBody
	public ResponseEntity<InputStreamResource> download(@RequestParam(value="filename") String filename,
			HttpServletRequest req) throws Exception {
		log.info("전달내용: " + filename);
		//1. 파일이 존재하는 경로 획득
		String path = req.getServletContext().getRealPath("");
		
		//2. 파일까지 스트림 연결				
		try {
			// file -> File -> FileInputStream -> InputStreamResource -> 클라이이언트로 전송
			File file = new File(path+"/"+filename);
			InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
			//3. 파일 데이터를 읽어서 응답
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
					.contentType(utilService.getMediaType(servletContext, filename))
					.contentLength(file.length())
					.body(isr);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("파일 다운로드 오류 "+e.getMessage());
		}
			
		
		
	}
}
