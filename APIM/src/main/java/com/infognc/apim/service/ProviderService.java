package com.infognc.apim.service;

import java.util.List;
import java.util.Map;

public interface ProviderService {

	// IF-CRM-010  유큐브 캠패인 대상자 전송
	public Integer sendCampListToGc(List<Map<String, String>> inParamList) throws Exception;

	// IF-CCS-852, IF-CCSN-001  ARS 고객만족도 실시간 자료전송, BS고객만족도 조사수행
	public Integer insertDBUCube(List<Map<String, String>> inParamList) throws Exception;
	
}
