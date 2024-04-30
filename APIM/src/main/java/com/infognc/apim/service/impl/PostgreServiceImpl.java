package com.infognc.apim.service.impl;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

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
		
		@Override
		public Entity_ContactLt findByCpidCpsq(String cpid, String cpsq) {
			Optional<Entity_ContactLt> existingEntity = repositoryContactLt.findByCpidCpsq(cpid, cpsq);
			
			if(existingEntity.isEmpty()) {
				throw new NoSuchElementException("Entity with cpid: " + cpid + " and cpsq: " + cpsq + " not found");
			}
			
			return existingEntity.orElse(null);
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
		public void updateContactLt(Entity_ContactLt entityContactLt, String cpsq) {
	        // 주어진 엔티티의 ID를 기준으로 기존 엔티티를 조회합니다.
	        Entity_ContactLt existingEntity = repositoryContactLt.findById(entityContactLt.getId()).orElseThrow(() ->
	                new EntityNotFoundException("Entity not found: " + entityContactLt.getId()));

	        // 기존 엔티티의 필드 값을 새로운 값으로 업데이트합니다.
	        existingEntity.getId().setCpsq(Integer.parseInt(cpsq));

	        // 변경된 엔티티를 저장합니다.
	        repositoryContactLt.save(existingEntity);
			
		}
		
}
