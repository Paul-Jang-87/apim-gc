package com.infognc.apim.repositories.postgre;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.infognc.apim.entities.postgre.Entity_CampMa;



@Repository
public interface Repository_CampMa extends CrudRepository<Entity_CampMa , String> {
	 Optional<Entity_CampMa> findByCpid(String cpid);
}