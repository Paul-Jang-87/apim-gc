package com.infognc.apim.service;


import com.infognc.apim.entities.Entity_CampMa;
import com.infognc.apim.entities.Entity_CampRt;
import com.infognc.apim.entities.Entity_ContactLt;


public interface PostgreService {
	
	//insert
	Entity_CampRt InsertCampRt(Entity_CampRt entityCampRt);
	Entity_CampMa InsertCampMa(Entity_CampMa entityCampMa);
	Integer InsertContactLt(Entity_ContactLt entityContactLt);
	
//	// Select DB (CAMPMA)
//	public List<Map<String, String>> selCampMa() throws Exception;
//	// Select DB (CAMPLT)
//	public List<Map<String, String>> selCampLt() throws Exception;
//	// Select DB (CAMPRT)
//	public List<Map<String, String>> selCampRt() throws Exception;
	
	
}
