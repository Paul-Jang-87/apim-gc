package com.infognc.apim.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infognc.apim.gc.ClientAction;
import com.infognc.apim.service.PostgreService;
import com.infognc.apim.service.ProviderService;

@Service	
public class ProviderServiceImpl implements ProviderService{
	private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);
	private final PostgreService postgreService;
	private final ClientAction clientAction;	
	
	@Autowired
	public ProviderServiceImpl(PostgreService postgreService, ClientAction clientAction) {
		this.postgreService = postgreService;
		this.clientAction = clientAction;
	}
	
	@Override
	public Integer sendCampListToGc(List<Map<String,String>> inParamList) throws Exception {
		Integer uCnt = 0;
		HashMap<String,String> insUcMap = new HashMap<String, String>();
		List<Map<String,String>> hsList = new ArrayList<Map<String,String>>();
		JSONObject reqBody = null;
		
		for(int i=0; i<inParamList.size(); i++) {
			insUcMap = new HashMap<String, String>();
			insUcMap.put("cpid", inParamList.get(i).get("cpid"));
			insUcMap.put("cpsq", inParamList.get(i).get("cpsq"));
			insUcMap.put("cske", inParamList.get(i).get("cske"));
			insUcMap.put("csna", inParamList.get(i).get("csna"));
			insUcMap.put("tno1", new String(Base64.decodeBase64(inParamList.get(i).get("tno1"))));
			insUcMap.put("tno2", new String(Base64.decodeBase64(inParamList.get(i).get("tno2"))));
			insUcMap.put("tno3", new String(Base64.decodeBase64(inParamList.get(i).get("tno3"))));
			insUcMap.put("tkda", inParamList.get(i).get("tkda"));
			insUcMap.put("flag", inParamList.get(i).get("flag"));
			
			hsList.add(uCnt, insUcMap);
			
			
			// GC API 호출
			reqBody 		= new JSONObject();
			String gcUrl 	= "/api/v2/outbound/campaigns/{campaignId}";	 
			// CampID로 ContactListId 가져온다.
			// API Enpoint [GET] /api/v2/outbound/campaigns/{campaignId}
			JSONObject cmpList = clientAction.callApiRestTemplate_GET(gcUrl, insUcMap.get("cpid"));
			String contactListId = ((JSONObject) cmpList.get("contactList")).getString("id");
			
			reqBody.put("cpid", insUcMap.get("cpid"));
			reqBody.put("cpsq", insUcMap.get("cpsq"));
			reqBody.put("cske", insUcMap.get("cske"));
			reqBody.put("csna", insUcMap.get("csna"));
			reqBody.put("tno1", insUcMap.get("tno1"));
			reqBody.put("tno2", insUcMap.get("tno2"));
			reqBody.put("tno3", insUcMap.get("tno3"));
			reqBody.put("tkda", insUcMap.get("tkda"));
			reqBody.put("flag", insUcMap.get("flag"));
			reqBody.put("tmzo", "Asia/Seoul (+09:00)");
			
			// API Enpoint [POST] /api/v2/outbound/contactlists/{contactListId}/contacts
			gcUrl = "/api/v2/outbound/contactlists/{contactListId}/contacts";
			clientAction.callApiRestTemplate_POST(gcUrl, contactListId, reqBody);
			
			uCnt++;
		}
		logger.info("## IF-API-039302 INSERT DATA >> " + hsList);
		logger.info("## IF-API-039302 INSERT DATA SIZE >> " + hsList.size());
		
		// UCUBE INSERT
		Integer iUcnt = 0;
		if(hsList.size()>0) {
			iUcnt = postgreService.insertCampLt(hsList);
			logger.info("## IF-API-039302 INSERT RESULT >> " + iUcnt + " (1:성공, 0:실패)");
		}
		
		return iUcnt;
	}
	
}
