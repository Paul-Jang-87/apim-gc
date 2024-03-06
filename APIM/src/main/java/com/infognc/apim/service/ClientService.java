package com.infognc.apim.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ClientService {

	// 캠페인결과  
	public HashMap<String, Object> getCmpnRsltList(List<Map<String, Object>> reqBodyList) throws Exception; 
	
}
