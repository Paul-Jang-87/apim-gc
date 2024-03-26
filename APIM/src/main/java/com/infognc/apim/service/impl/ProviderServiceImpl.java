package com.infognc.apim.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.infognc.apim.embeddable.ContactLt;
import com.infognc.apim.entities.Entity_ContactLt;
import com.infognc.apim.gc.ClientAction;
import com.infognc.apim.service.PostgreService;
import com.infognc.apim.service.ProviderService;
import com.infognc.apim.utl.ApiUtil;

@Service
public class ProviderServiceImpl implements ProviderService {
	private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);
	private final PostgreService postgreService;
	private final ClientAction clientAction;

	public ProviderServiceImpl(PostgreService postgreService, ClientAction clientAction) {
		this.postgreService = postgreService;
		this.clientAction = clientAction;
	}

	/**
	 * 캠페인 리스트 전송 (To G.C)
	 */
	@Override
	public Integer sendCampListToGc(List<Map<String, String>> inParamList) throws Exception {
		Entity_ContactLt enContactLt = new Entity_ContactLt();
		JSONObject reqBody = null;
		JSONArray bodyList = new JSONArray();
		
		ContactLt contactLt = new ContactLt();
		Integer uCnt = 0;
		Integer iUcnt = 0;
		for (int i = 0; i < inParamList.size(); i++) {
			String cpid = inParamList.get(i).get("cpid");
			String cpsq = inParamList.get(i).get("cpsq");
			String cske = inParamList.get(i).get("cske");
			String csna = inParamList.get(i).get("csna");
			String tno1 = new String(Base64.decodeBase64(ApiUtil.nullToString(inParamList.get(i).get("tno1"))  ));
			String tno2 = new String(Base64.decodeBase64(ApiUtil.nullToString(inParamList.get(i).get("tno2"))  ));
			String tno3 = new String(Base64.decodeBase64(ApiUtil.nullToString(inParamList.get(i).get("tno3"))  ));
			String tkda = inParamList.get(i).get("tkda");
			String flag = inParamList.get(i).get("flag");
			
			contactLt.setCpid(cpid);
			contactLt.setCpsq(Integer.parseInt(cpsq));
			enContactLt.setId(contactLt);
			enContactLt.setCske(cske);
			enContactLt.setCsna(csna);
			enContactLt.setTno1(tno1);
			enContactLt.setTno2(tno2);
			enContactLt.setTno3(tno3);
			enContactLt.setTkda(tkda);
			enContactLt.setFlag(flag);
			
			// GC API 호출
			clientAction.init();
			
			reqBody = new JSONObject();
			String gcUrl = "/api/v2/outbound/campaigns/{campaignId}";
			// CampID로 ContactListId 가져온다.
			// API Enpoint [GET] /api/v2/outbound/campaigns/{campaignId}
			String contactListId = "";
			JSONObject cmpList = clientAction.callApiRestTemplate_GET(gcUrl, cpid);
			if(cmpList != null) {
				contactListId = ((JSONObject) cmpList.get("contactList")).getString("id");
			}

			reqBody.put("cpid", cpid);
			reqBody.put("cpsq", cpsq);
			reqBody.put("cske", cske);
			reqBody.put("csna", csna);
			reqBody.put("tno1", tno1);
			reqBody.put("tno2", tno2);
			reqBody.put("tno3", tno3);
			reqBody.put("tkda", tkda);
//			reqBody.put("flag", flag);
			reqBody.put("tmzo", "Asia/Seoul (+09:00)");

			// API Enpoint [POST] /api/v2/outbound/contactlists/{contactListId}/contacts
			gcUrl = "/api/v2/outbound/contactlists/{contactListId}/contacts";
			System.out.println("## client ready (gcUrl = " + gcUrl + ")");
			System.out.println("## reqBody :: " + reqBody);
			bodyList.put(reqBody);
			
//			clientAction.callApiRestTemplate_POST(gcUrl, contactListId, bodyList);

			// db인서트
			try {
				iUcnt = postgreService.InsertContactLt(enContactLt);
				logger.info("## IF-API-076702 INSERT RESULT >> {} (1:성공, 0:실패)", iUcnt);
				
			} catch (DataIntegrityViolationException ex) {
				ex.printStackTrace();
				logger.error("DataIntegrityViolationException 발생 : {}", ex.getMessage());
			} catch (DataAccessException ex) {
				ex.printStackTrace();
				logger.error("DataAccessException 발생 : {}", ex.getMessage());
			}
			uCnt++;
		}
		
		return iUcnt;
	}
	
	/**
	 * ARS 만족도 실시간 자료전송 ('C', PCUBEX
	 * BS 고객만족도 조사 수행 ('BS', UCUBE)
	 * 
	 */
	@Override
	public Integer sendArsStafData(List<Map<String, String>> inParamList) throws Exception {
		Integer resInt = 0;
		ApiUtil apiUtil = new ApiUtil();
		JSONObject reqBody = null;
		String gcUrl = "";
		
		// G.C로 보낼 contact data 
		String contactListId 	= "";
		
		String cpid 	= "";
		String cpsq		= "";
		String cske		= "";
		String csna		= "";
		String tno1		= "";
		String tno2		= "";
		String tno3		= "";
		String tkda		= "";
		
		try {
			for(int i=0; i<inParamList.size(); i++) {
				String seqNo 	= inParamList.get(i).get("seqNo");
				String surAni 	= inParamList.get(i).get("surAni");
				String surGubun = inParamList.get(i).get("surGubun");
				
				inParamList.get(i).replace("surAni", apiUtil.decode(surAni));
				
				if("BS".equals(surGubun)) {		// UCUBE - BS고객만족도 조사수행
					// SET G.C Send Data 
					cpid = "11";
					tno1 = surAni;
					tkda = "8443" + "||" + seqNo + "||" + surAni + "||" + surGubun;

				} else if("C".equals(surGubun)) {	// PCUBE - ARS 고객만족도 실시간 자료전송
					// SET G.C Send Data 
					cpid = "9";
					tno1 = surAni;
					tkda = "5996" + "||" + seqNo + "||" + surAni + "||" + surGubun;
					
				} else {
				}
				
				// 공휴일 체크? 휴일 아닐때 CAMPLT로 데이터 저장
				// - 사용유무 확인 필요하나, 현재는 사용되지 않는 것으로 추측.
				// (AS-IS) TB_SMS_HOLIDAY_CHECK 테이블에 2022년까지만 공휴일 데이터가 들어가 있고 이후는 없음.
				
				/*
				 * [CAMPLT]
				 * CPID = "11" or "9"
				 * CPSQ = SQ_PCUBE.nextval or SQ_UCUBE.nextval
				 * CSK2 = SEQ_NO
				 * CSK3 = SUR_GUBUN
				 * TNO1 = SUR_ANI
				 * TKDA = '8443'||SQE_NO||SUR_ANI||SUR_GUBUN or '5996'||SQE_NO||SUR_ANI||SUR_GUBUN
				 * FLAG = 'A'
				 * CRDT = sysdate
				 * 
				 * 불필요한 데이터는 전송 X ( ex. FLAG, CRDT )
				 * 
				 */
				clientAction.init();
				
				gcUrl = "/api/v2/outbound/campaigns/{campaignId}";
				// CampID로 ContactListId 가져온다.
				// API Enpoint [GET] /api/v2/outbound/campaigns/{campaignId}
				JSONObject cmpList = clientAction.callApiRestTemplate_GET(gcUrl, cpid);
				if(cmpList != null) {
					contactListId = ((JSONObject) cmpList.get("contactList")).getString("id");
				}
				
				reqBody = new JSONObject();
				reqBody.put("CPID", cpid);
				reqBody.put("CPSQ", cpsq);
				reqBody.put("CSKE", cske);
				reqBody.put("CSNA", csna);
				reqBody.put("TNO1", tno1);
				reqBody.put("TNO2", tno2);
				reqBody.put("TNO3", tno3);
				reqBody.put("TKDA", tkda);
				reqBody.put("tmzo", "Asia/Seoul (+09:00)");
				
				// API Enpoint [POST] /api/v2/outbound/contactlists/{contactListId}/contacts
				gcUrl = "/api/v2/outbound/contactlists/{contactListId}/contacts";
				
				clientAction.callApiRestTemplate_POST(gcUrl, contactListId, reqBody);
				resInt = 1;
				
			}
		}catch(Exception e) {
			logger.error("## ERROR!!  : {} " + e.getMessage());
			return 0;
		}
		
		return resInt;
	}
	
	
	
	
}
