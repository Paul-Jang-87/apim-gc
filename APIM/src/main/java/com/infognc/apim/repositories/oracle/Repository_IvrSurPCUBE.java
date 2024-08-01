package com.infognc.apim.repositories.oracle;

import java.util.List;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.infognc.apim.entities.oracle.Entity_IVR_SURVEY_PCUBE;

import jakarta.persistence.LockModeType;

@Repository
public interface Repository_IvrSurPCUBE extends CrudRepository<Entity_IVR_SURVEY_PCUBE, Integer> {
	
	final String QUERY_STRING = "SELECT 	SEQ_NO,"
									+ "SUR_SURVEY1,"
									+ "SUR_SURVEY2,"
									+ "TO_CHAR(SUR_ANS_DATE, 'YYYY/MM/DD HH24:MI:SS') SUR_ANS_DATE "
									+ "FROM 	TB_IVR_SURVEY_PCUBE "
									+ "WHERE 	SUR_ANS_DATE > TRUNC(SYSDATE) "
									+ "AND 		SUR_INPUTCODE = 'Y' "
									+ "AND 		SUR_SURVEY1 IS NOT NULL";
	
    @Query(value = QUERY_STRING, nativeQuery = true)
    List<Entity_IVR_SURVEY_PCUBE> findSurveyNative();
	
	
}
