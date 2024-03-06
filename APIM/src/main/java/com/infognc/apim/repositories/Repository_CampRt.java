package com.infognc.apim.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.infognc.apim.embeddable.CampRt;
import com.infognc.apim.entities.Entity_CampRt;

@Repository
public interface Repository_CampRt extends CrudRepository<Entity_CampRt, CampRt> {
	
	Optional<Entity_CampRt> findByCpid(String cpid);
	Optional<Entity_CampRt> findById(CampRt id);

	@Query("SELECT MAX(camprt.id.rlsq) FROM Entity_CampRt camprt")
    Optional<Integer> findMaxRlsq();

}