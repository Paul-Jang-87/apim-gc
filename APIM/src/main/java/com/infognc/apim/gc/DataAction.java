package com.infognc.apim.gc;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * G.C DataAction에서 넘어오는 Body 데이터를 세팅하기 위한 class
 */
@Getter
@Setter
public class DataAction {
	private String url;
	private String method;
	private String apimPath;
	
	@JsonProperty("apimQuery")
	private Map<String,Object> apimQuery;
	
	@JsonProperty("apimHeader")
	private Map<String,Object> apimHeader;
	
	@JsonProperty("apimBody")
	private Map<String,Object> apimBody;
}
