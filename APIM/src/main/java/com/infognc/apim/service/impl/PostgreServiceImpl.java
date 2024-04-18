package com.infognc.apim.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import com.infognc.apim.embeddable.ContactLt;
import com.infognc.apim.entities.postgre.Entity_CampMa;
import com.infognc.apim.entities.postgre.Entity_ContactLt;
import com.infognc.apim.repositories.postgre.Repository_CampMa;
import com.infognc.apim.repositories.postgre.Repository_ContactLt;
import com.infognc.apim.service.PostgreService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;


@Service
public class PostgreServiceImpl implements PostgreService{
	private static final Logger logger = LoggerFactory.getLogger(PostgreServiceImpl.class);
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
		
		// SELECT CONTACTLT TABLE - MAX CPSQ 
		@Override
		public Integer selectMaxCpsq(String id) {
			
			try {
				Optional<Integer> optionalEntity = repositoryContactLt.findMaxCpsqByCpid(id);
				return optionalEntity.orElse(null);
			} catch (IncorrectResultSizeDataAccessException ex) {
				logger.error("Error retrieving Entity_ContactLt which has hightest value of 'cpsq' column: {}", ex);

				return 0;
			}
		}
		
		// UPDATE CONTACTLT TABLE - CPSQ
		@Override
		@Transactional
		public void updateContactLt(ContactLt id, String cpsq) {
			Optional<Entity_ContactLt> optionalEntity = repositoryContactLt.findById(id);

			if (optionalEntity.isPresent()) {
				Entity_ContactLt entity = optionalEntity.get();
				entity.getId().setCpsq(Integer.parseInt(cpsq));
				repositoryContactLt.save(entity);
			} else {
				throw new EntityNotFoundException("Entity not found : " + id);
			}
			
			
		}
		
}
