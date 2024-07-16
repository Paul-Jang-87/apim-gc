package com.infognc.apim.repositories.oracle;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.infognc.apim.entities.oracle.Entity_IVR_SURVEY_UCUBE_W;

@Repository
public interface Repository_IvrSurUCUBE_W extends CrudRepository<Entity_IVR_SURVEY_UCUBE_W, String> {

	List<Entity_IVR_SURVEY_UCUBE_W> findAll();
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM TB_IVR_SURVEY_UCUBE_W e WHERE e.seq_no = :seq_no", nativeQuery = true)
	void deleteById(@Param("seq_no") String seq_no);
	
}
