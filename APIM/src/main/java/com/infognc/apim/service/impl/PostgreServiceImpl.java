package com.infognc.apim.service.impl;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.infognc.apim.entities.postgre.Entity_CampMa;
import com.infognc.apim.entities.postgre.Entity_ContactLt;
import com.infognc.apim.repositories.postgre.Repository_CampMa;
import com.infognc.apim.repositories.postgre.Repository_ContactLt;
import com.infognc.apim.service.PostgreService;


@Service
public class PostgreServiceImpl implements PostgreService{
//	private static final Logger logger = LoggerFactory.getLogger(PostgreServiceImpl.class);
	private final Repository_CampMa repositoryCampMa;
	private final Repository_ContactLt repositoryContactLt;
	
	public PostgreServiceImpl(Repository_CampMa repositoryCampMa, Repository_ContactLt repositoryContactLt) {

		this.repositoryCampMa = repositoryCampMa;
		this.repositoryContactLt = repositoryContactLt;
	}

		@Override
		public Entity_CampMa InsertCampMa(Entity_CampMa entityCampMa) {
			Optional<Entity_CampMa> existingEntity = repositoryCampMa.findById(entityCampMa.getCpid());
			
			if (existingEntity.isPresent()) {
				throw new DataIntegrityViolationException("Record with the given composite key already exists.");
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
	
		
		// =================== [ SELECT DB ] ==================================
		
}
