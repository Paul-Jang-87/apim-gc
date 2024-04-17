package com.infognc.apim.service;

import com.infognc.apim.entities.postgre.Entity_CampMa;
import com.infognc.apim.entities.postgre.Entity_ContactLt;

public interface PostgreService {
	
	// 캠페인 마스터 (CAMPMA)
	Entity_CampMa InsertCampMa(Entity_CampMa entityCampMa);		// INSERT
	
	// 캠페인 리스트 (CONTACTLT)
	Integer InsertContactLt(Entity_ContactLt entityContactLt);	// INSERT
	
	// 캠페인 결과 (CAMPRT)
//	Entity_CampRt InsertCampRt(Entity_CampRt entityCampRt);		// INSERT
//	Entity_CampRt selectCampRtByCpid(String cpid);				// SELECT by 캠페인 ID
	
	
}
