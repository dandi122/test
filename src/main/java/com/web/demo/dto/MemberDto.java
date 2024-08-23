/**
 * 사용자 정보를 FormData 형식으로 통으로 보냈다
 * 사용자 정보를 통으로 받아줄 형식이 필요 => DTO로 받겠다
 * 통신단에서 사용하는 DTO
 */

package com.web.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto {
	private int age;
	private String name;
	private String addr;
	private String _csrf;
	
	@Builder
	public MemberDto(int age, String name, String addr, String _csrf) {
		super();
		this.age = age;
		this.name = name;
		this.addr = addr;
		this._csrf = _csrf;
	}

}
