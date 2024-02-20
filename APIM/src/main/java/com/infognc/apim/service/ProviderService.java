package com.infognc.apim.service;

import java.util.List;
import java.util.Map;

public interface ProviderService {

	// IF-CRM-010  유큐브 캠패인 대상자 전송
	public Integer sendCampListToGc(List<Map<String, String>> inParamList) throws Exception;
	
}
