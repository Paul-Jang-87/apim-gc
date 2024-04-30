package com.infognc.apim.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.infognc.apim.embeddable.ContactLt;
import com.infognc.apim.entities.postgre.Entity_ContactLt;
import com.infognc.apim.gc.ClientAction;
import com.infognc.apim.service.PostgreService;
import com.infognc.apim.service.ProviderService;
import com.infognc.apim.utl.ApiUtil;
import com.infognc.apim.utl.Configure;

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
		
//		List<Map<String,Object>> reqList = new ArrayList<Map<String, Object>>();
//		HashMap<String,Object> reqBody = new HashMap<String, Object>();
//		HashMap<String,String> reqData = new HashMap<String, String>();
		List<JSONObject> reqList = new ArrayList<JSONObject>();
		JSONObject reqBody = new JSONObject();
		JSONObject reqData = new JSONObject();
		
		String cpid = (String) inParamList.get(0).get("cpid");
		String queueid = "";
		
		// GC API 호출
		clientAction.init();
		
		String gcUrl = "/api/v2/outbound/campaigns/{campaignId}";
		// CampID로 ContactListId 가져온다.
		// API Enpoint [GET] /api/v2/outbound/campaigns/{campaignId}
		String contactListId = "";
		String resCmpList = clientAction.callApiRestTemplate_GET(gcUrl, cpid);
		JSONObject cmpList = new JSONObject(resCmpList);
		if(!cmpList.isEmpty()) {
			contactListId = ((JSONObject) cmpList.optJSONObject("contactList", new JSONObject())).optString("id", "");
		/*	queueid = ((JSONObject) cmpList.get("queue")).getString("id") != null ? ((JSONObject) cmpList.get("queue")).getString("id") : "";  */
			queueid = ((JSONObject) cmpList.optJSONObject("queue", new JSONObject())).optString("id", "");	// 찾으려는 key값이 null이 아닌 경우 저장, null인경우 defaultValue(2번째 파라미터)로 세팅
		}
		
		
		ContactLt contactLt = new ContactLt();
		Integer uCnt = 0;
		Integer iUcnt = 0;
		for (int i = 0; i < inParamList.size(); i++) {
			
			String cpsq = inParamList.get(i).get("cpsq");
			String cske = inParamList.get(i).get("cske");
			String csna = inParamList.get(i).get("csna");
			String tno1 = new String(Base64.decodeBase64(ApiUtil.nullToString(inParamList.get(i).get("tno1"))  ));
			String tno2 = new String(Base64.decodeBase64(ApiUtil.nullToString(inParamList.get(i).get("tno2"))  ));
			String tno3 = new String(Base64.decodeBase64(ApiUtil.nullToString(inParamList.get(i).get("tno3"))  ));
			String tno4 = "";
			String tno5 = "";
			String tlno = "";
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
			
			// id
			reqBody.put("id", cpsq);
			
			// contactList id
			reqBody.put("contactListId", contactListId);
			
			// data
			reqData.put("cpid", cpid);
			reqData.put("cpsq", cpsq);
			reqData.put("cske", cske);
			reqData.put("csna", csna);
			reqData.put("tno1", tno1);
			reqData.put("tno2", tno2);
			reqData.put("tno3", tno3);
			reqData.put("tno4", tno4);
			reqData.put("tno5", tno5);
			reqData.put("tlno", tlno);
			reqData.put("tkda", tkda);
			reqData.put("queueid", queueid);
			reqData.put("trycnt", "0");
			reqData.put("tmzo", "Asia/Seoul (+09:00)");
			
			reqBody.put("data", reqData);
			
			// clear 안하고 계속 add - 2024.04.18
			// contact data add 전에 clear 필요 - 2024.04.03 추가 JJH
			// API Enpoint [POST] /api/v2/outbound/contactlists/{contactListId}/clear
//			gcUrl = "/api/v2/outbound/contactlists/{contactListId}/clear";
//			clientAction.callApiRestTemplate_POST(gcUrl, contactListId);
			
			
			// API Enpoint [POST] /api/v2/outbound/contactlists/{contactListId}/contacts
			gcUrl = "/api/v2/outbound/contactlists/{contactListId}/contacts";
			System.out.println("## client ready (gcUrl = " + gcUrl + ")");
			System.out.println("## reqBody :: " + reqBody);
			reqList.add(reqBody);
			
			String resResult = clientAction.callApiRestTemplate_POST(gcUrl, contactListId, reqList);

			if(!"".equals(resResult)) {
				logger.info("## Genesys Cloud API Success : \n {}", resResult);
			}
			
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
	 * 
	 * IF-API-076701 
	 * ARS 만족도 실시간 자료전송 ('C', PCUBE)
	 * BS 고객만족도 조사 수행 ('BS', UCUBE)
	 * 
	 */
	@Override
	public Integer sendArsStafData(List<Map<String, String>> inParamList) throws Exception {
		Integer resInt = 1;
//		ApiUtil apiUtil = new ApiUtil();
		
//		List<Map<String,Object>> reqList = new ArrayList<Map<String, Object>>();
//		HashMap<String,Object> reqBody = new HashMap<String, Object>();
//		HashMap<String,String> reqData = new HashMap<String, String>();

		List<JSONObject> reqList = new ArrayList<JSONObject>();
		JSONObject reqBody = new JSONObject();
		JSONObject reqData = new JSONObject();
		
		// 휴일 체크? 휴일 아닐때 CAMPLT로 데이터 저장
		// (AS-IS) TB_SMS_HOLIDAY_CHECK 테이블에 2022년까지만 공휴일 데이터가 들어가 있고 이후는 없음.
		// 휴일 체크 사용한다면, 휴일(토,일,공휴일)이 아닐때만 Genesys Cloud로 대상자 리스트 전송.
		boolean flagHoliday = true;
		if(flagHoliday) {
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
			String tno4		= "";
			String tno5		= "";
			String tlno		= "";
			String tkda		= "";
			String flag		= "";
			String queueid 	= "";
			
			clientAction.init();
			
			try {
				for(int i=0; i<inParamList.size(); i++) {
					
					logger.info("## inParamList :: {} ", inParamList.get(i).toString());
					
					String seqNo 	= inParamList.get(i).get("seqNo");
					String surAni 	= inParamList.get(i).get("surAni");
					String surGubun = inParamList.get(i).get("surGubun");
					
					surAni = new String(Base64.decodeBase64(surAni.getBytes()));
					
					if("BS".equals(surGubun)) {		// UCUBE - BS고객만족도 조사수행
						// SET G.C Send Data 
//						cpid = Configure.get("API.076701.BS.CPID") == "" ? "ee4d3744-be6c-473c-b2cc-22d8cbbb526e": Configure.get("API.076701.BS.CPID");
						cpid = Configure.get("API.076701.BS.CPID");
						if(cpid.equals("") || cpid == null) cpid = "ee4d3744-be6c-473c-b2cc-22d8cbbb526e";
						tno1 = surAni;
						tkda = "8443" + "||" + seqNo + "||" + surAni + "||" + surGubun;
						
					} else if("C".equals(surGubun)) {	// PCUBE - ARS 고객만족도 실시간 자료전송
						// SET G.C Send Data 
						cpid = Configure.get("API.076701.ARS.CPID") == "" ? "9" : Configure.get("API.076701.ARS.CPID");
						tno1 = surAni;
						tkda = "5996" + "||" + seqNo + "||" + surAni + "||" + surGubun;
						
					} else {
					}
					
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
					
					gcUrl = "/api/v2/outbound/campaigns/{campaignId}";
					// CampID로 ContactListId 가져온다.
					// API Enpoint [GET] /api/v2/outbound/campaigns/{campaignId}
					String resCmpList = clientAction.callApiRestTemplate_GET(gcUrl, cpid);
					JSONObject cmpList = new JSONObject(resCmpList);
					if(!cmpList.isEmpty()) {
						contactListId = ((JSONObject) cmpList.optJSONObject("contactList", new JSONObject())).optString("id", "");
						queueid = ((JSONObject) cmpList.optJSONObject("queue", new JSONObject())).optString("id", "");	// 찾으려는 key값이 null이 아닌 경우 저장, null인경우 defaultValue(2번째 파라미터)로 세팅
					}
					
					ContactLt contactLt = new ContactLt();
					// AS-IS 기준으로 DB TRIGGER에서 SQ_TB_CALL_PDS_UCUBE.nextval, SQ_TB_CALL_PDS_PCUBE.nextval로 CPSQ 설정
					// postgre DB CONTACTLT TABLE CPSQ
					// CPID로 조회한 CPSQ MAX + 1
					int maxCpsq = postgreService.selectMaxCpsq(cpid);
					cpsq = String.valueOf(maxCpsq + 1);
					
					Entity_ContactLt entityContactLt = postgreService.findByCpidCpsq(cpid, String.valueOf(maxCpsq));
					// contactlt update
					// UPDATE CONTACTLT
//					ContactLt contactLt = new ContactLt();
//					contactLt.setCpid(cpid);
//					postgreService.updateContactLt(entityContactLt, cpsq);
					
					contactLt.setCpid(cpid);
					contactLt.setCpsq(Integer.parseInt(cpsq));
					entityContactLt.setId(contactLt);
					entityContactLt.setCske(cske);
					entityContactLt.setCsna(csna);
					entityContactLt.setTno1(tno1);
					entityContactLt.setTno2(tno2);
					entityContactLt.setTno3(tno3);
					entityContactLt.setTkda(tkda);
					entityContactLt.setFlag(flag);
					
					// id
					reqBody.put("id", cpsq);
					
					// contactList id
					reqBody.put("contactListId", contactListId);
					
					reqData.put("cpid", cpid);
					reqData.put("cpsq", cpsq);
					reqData.put("cske", cske);
					reqData.put("csna", csna);
					reqData.put("tno1", tno1);
					reqData.put("tno2", tno2);
					reqData.put("tno3", tno3);
					reqData.put("tno4", tno4);
					reqData.put("tno5", tno5);
					reqData.put("tlno", tlno);
					reqData.put("tkda", tkda);
					reqData.put("queueid", queueid);
					reqData.put("trycnt", "0");
					reqData.put("tmzo", "Asia/Seoul (+09:00)");
					
					reqBody.put("data", reqData);
					
					reqList.add(reqBody);
					
					// clear 대신 계속 add
					// contact data add 전에 clear 필요 - 2024.04.03 추가 JJH
					// API Enpoint [POST] /api/v2/outbound/contactlists/{contactListId}/clear
//				gcUrl = "/api/v2/outbound/contactlists/{contactListId}/clear";
//				clientAction.callApiRestTemplate_POST(gcUrl, contactListId);
					
					// API Enpoint [POST] /api/v2/outbound/contactlists/{contactListId}/contacts
					gcUrl = "/api/v2/outbound/contactlists/{contactListId}/contacts";
					
					String resResult = clientAction.callApiRestTemplate_POST(gcUrl, contactListId, reqList);
					if(!"".equals(resResult)) {
						resInt = postgreService.InsertContactLt(entityContactLt);
						if(resInt==1) {
							logger.info("## DB INSERT SUCCESS !! , [CONTACTLT TABLE] ");
						}
						logger.info("## Genesys Cloud API Success : \n {}", resResult);
					}
				}
			}catch(Exception e) {
				logger.error("## ERROR!!  : {} ", e.getMessage());
				e.printStackTrace();
				return 0;
			}
			
		} 
		return resInt;
	}
	
	
	
	
}
