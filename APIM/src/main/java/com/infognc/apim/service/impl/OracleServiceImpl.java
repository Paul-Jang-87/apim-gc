package com.infognc.apim.service.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.infognc.apim.entities.oracle.Entity_IVR_SURVEY_PCUBE;
import com.infognc.apim.entities.oracle.Entity_IVR_SURVEY_UCUBE_W;
import com.infognc.apim.repositories.oracle.Repository_IvrSurPCUBE;
import com.infognc.apim.repositories.oracle.Repository_IvrSurUCUBE_W;
import com.infognc.apim.service.OracleService;


@Service
public class OracleServiceImpl implements OracleService{
//	private static final Logger logger = LoggerFactory.getLogger(OracleServiceImpl.class);
	private final Repository_IvrSurPCUBE ivrSurPcube;
	private final Repository_IvrSurUCUBE_W ivrSurUcube;
	
	public OracleServiceImpl(Repository_IvrSurPCUBE ivrSurPcube, Repository_IvrSurUCUBE_W ivrSurUcube) {
		this.ivrSurPcube = ivrSurPcube;
		this.ivrSurUcube = ivrSurUcube;
	}

	@Override
	public JSONArray selectPcube() throws Exception {
		JSONArray resultJsonArray = new JSONArray();
//		List<Entity_IVR_SURVEY_PCUBE> pcubeList =  ivrSurPcube.findBySurAnsDateAfterAndSurInputcodeAndSurSurvey1IsNotNull();
		List<Entity_IVR_SURVEY_PCUBE> pcubeList =  ivrSurPcube.findSurveyNative();
		
		if(pcubeList.size() > 0) {
			for (Entity_IVR_SURVEY_PCUBE entity : pcubeList) {
				JSONObject jsonObject = new JSONObject();
				
				jsonObject.put("seq_no", entity.getSeq_no());
//				jsonObject.put("sur_ani", entity.getSur_ani());
//            	jsonObject.put("sur_gubun", entity.getSur_gubun());
//				jsonObject.put("sur_inputcode", entity.getSur_inputcode());
				jsonObject.put("sur_survey1", entity.getSur_survey1());
				jsonObject.put("sur_survey2", entity.getSur_survey2());
//            	jsonObject.put("sur_survey3", entity.getSur_survey3());
				jsonObject.put("sur_ans_date", entity.getSur_ans_date());
				
				resultJsonArray.put(jsonObject);
			}
		}
		
		return resultJsonArray;
	}
	
	
	@Override
	public JSONArray selectUcube() throws Exception {
		JSONArray resultJsonArray = new JSONArray();
		List<Entity_IVR_SURVEY_UCUBE_W> ucubeList = ivrSurUcube.findAll();
		
		if(ucubeList.size() > 0) {
			// List to JSONArray
			for (Entity_IVR_SURVEY_UCUBE_W entity : ucubeList) {
				JSONObject jsonObject = new JSONObject();
				
				jsonObject.put("seq_no", entity.getSeq_no());
				jsonObject.put("sur_survey1", entity.getSur_survey1());
				jsonObject.put("sur_survey2", entity.getSur_survey2());
				jsonObject.put("sur_ans_date", entity.getSur_ans_date());
				
				resultJsonArray.put(jsonObject);
				
				// delete 
//				ivrSurUcube.deleteById(entity.getOrderid());
				ivrSurUcube.deleteById(entity.getSeq_no());
				
			}
		}
		
		return resultJsonArray;
	}
	
}
