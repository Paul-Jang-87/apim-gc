package com.infognc.apim.repositories.oracle;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.infognc.apim.entities.oracle.Entity_IVR_SURVEY_UCUBE_W;

@Repository
public interface Repository_IvrSurUCUBE_W extends CrudRepository<Entity_IVR_SURVEY_UCUBE_W, String> {

	List<Entity_IVR_SURVEY_UCUBE_W> findAll();
}
