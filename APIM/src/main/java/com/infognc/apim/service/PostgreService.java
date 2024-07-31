package com.infognc.apim.service;

import com.infognc.apim.entities.postgre.Entity_ContactLt;

public interface PostgreService {
	
	// 캠페인 리스트 (CONTACTLT)
	Integer insertContactLt(Entity_ContactLt entityContactLt);	// INSERT

	// 특정 캠페인리스트 MAX 
	Integer selectMaxCpsq(String id);
}
