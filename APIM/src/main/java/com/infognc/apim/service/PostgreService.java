package com.infognc.apim.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

public interface PostgreService {
	
	// Insert DB (CAMPMA)
	public Integer insertCampMa(List<Map<String,String>> hsList) throws Exception;
	// Insert DB (CAMPLT)
	public Integer insertCampLt(List<Map<String,String>> hsList) throws Exception;
	// Insert DB (CAMPRT)
	public Integer insertCampRt(List<Map<String,String>> hsList) throws Exception;
	
	// Select DB (CAMPMA)
	public List<Map<String, String>> selCampMa() throws Exception;
	// Select DB (CAMPLT)
	public List<Map<String, String>> selCampLt() throws Exception;
	// Select DB (CAMPRT)
	public List<Map<String, String>> selCampRt() throws Exception;
	
	
}
