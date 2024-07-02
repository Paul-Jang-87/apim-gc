package com.infognc.apim.service;

import com.infognc.apim.entities.postgre.Entity_CampMa;
import com.infognc.apim.entities.postgre.Entity_ContactLt;

public interface PostgreService {
	
	// 캠페인 마스터 (CAMPMA)
	Entity_CampMa InsertCampMa(Entity_CampMa entityCampMa);		// INSERT
	
	// 캠페인 리스트 (CONTACTLT)
	Integer InsertContactLt(Entity_ContactLt entityContactLt);	// INSERT
	
	void updateContactLt(Entity_ContactLt entityContactLt, String cpsq);	// update
	
	Entity_ContactLt findByCpidCpsq(String cpid, String cpsq);	// select
	
	// 특정 캠페인리스트 MAX 
	Integer selectMaxCpsq(String id);
	
	
}
