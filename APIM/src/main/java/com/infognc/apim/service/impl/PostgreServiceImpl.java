package com.infognc.apim.service.impl;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.infognc.apim.entities.Entity_CampMa;
import com.infognc.apim.entities.Entity_CampRt;
import com.infognc.apim.entities.Entity_ContactLt;
import com.infognc.apim.repositories.Repository_CampMa;
import com.infognc.apim.repositories.Repository_CampRt;
import com.infognc.apim.repositories.Repository_ContactLt;
import com.infognc.apim.service.PostgreService;


@Service
public class PostgreServiceImpl implements PostgreService{
	
	private final Repository_CampRt repositoryCampRt;
	private final Repository_CampMa repositoryCampMa;
	private final Repository_ContactLt repositoryContactLt;
	
	public PostgreServiceImpl(Repository_CampRt repositoryCampRt, Repository_CampMa repositoryCampMa,
			Repository_ContactLt repositoryContactLt) {

		this.repositoryCampRt = repositoryCampRt;
		this.repositoryCampMa = repositoryCampMa;
		this.repositoryContactLt = repositoryContactLt;
	}

	// **Insert
		@Override
		public Entity_CampRt InsertCampRt(Entity_CampRt entity_CampRt) {

			Optional<Entity_CampRt> existingEntity = repositoryCampRt.findById(entity_CampRt.getId());

			if (existingEntity.isPresent()) {
			    throw new DataIntegrityViolationException("Record with the given composite key already exists.");
			}

			return repositoryCampRt.save(entity_CampRt);
			
		}

		@Override
		public Entity_CampMa InsertCampMa(Entity_CampMa entityCampMa) {
			
			Optional<Entity_CampMa> existingEntity = repositoryCampMa.findByCpid(entityCampMa.getCpid());

	        if (existingEntity.isPresent()) {
	            throw new DataIntegrityViolationException("Record with 'cpid' already exists.");
	        }

	        return repositoryCampMa.save(entityCampMa);
		}

		@Override
		public Integer InsertContactLt(Entity_ContactLt entityContactLt) {
			
			Optional<Entity_ContactLt> existingEntity = repositoryContactLt.findById(entityContactLt.getId());

			if (existingEntity.isPresent()) {
			    throw new DataIntegrityViolationException("Record with the given composite key already exists.");
			}
			repositoryContactLt.save(entityContactLt);
			
			return 1;
		}
	
	
	
	
//	@Override
//	public List<Map<String, String>> selCampMa() throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public List<Map<String, String>> selCampLt() throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public List<Map<String, String>> selCampRt() throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
}
