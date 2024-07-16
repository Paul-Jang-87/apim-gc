package com.infognc.apim.service.impl;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infognc.apim.entities.postgre.Entity_CampMa;
import com.infognc.apim.entities.postgre.Entity_ContactLt;
import com.infognc.apim.repositories.postgre.Repository_CampMa;
import com.infognc.apim.repositories.postgre.Repository_ContactLt;
import com.infognc.apim.service.PostgreService;


@Service
public class PostgreServiceImpl implements PostgreService{
	private static final Logger logger = LoggerFactory.getLogger(PostgreServiceImpl.class);
	private static final Logger errorLogger = LoggerFactory.getLogger("ErrorLogger");
	private final Repository_CampMa repositoryCampMa;
	private final Repository_ContactLt repositoryContactLt;
	
	public PostgreServiceImpl(Repository_CampMa repositoryCampMa, Repository_ContactLt repositoryContactLt) {

		this.repositoryCampMa = repositoryCampMa;
		this.repositoryContactLt = repositoryContactLt;
	}

		@Transactional
		@Override
		public Entity_CampMa InsertCampMa(Entity_CampMa entityCampMa) {
			Optional<Entity_CampMa> existingEntity = repositoryCampMa.findById(entityCampMa.getCpid());
			
			if (existingEntity.isPresent()) {
				throw new DataIntegrityViolationException("해당 복합 키를 가진 레코드가 이미 존재합니다");
			}
			
			return repositoryCampMa.save(entityCampMa);
		}
		
		@Transactional
		@Override
		public Integer InsertContactLt(Entity_ContactLt entityContactLt) {
			
			Optional<Entity_ContactLt> existingEntity = repositoryContactLt.findById(entityContactLt.getId());

			if (existingEntity.isPresent()) {
			    throw new DataIntegrityViolationException("해당 복합 키를 가진 레코드가 이미 존재합니다");
			}
			repositoryContactLt.save(entityContactLt);
			
			return 1;
		}
		
		@Transactional
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
		@Transactional
		public Integer selectMaxCpsq(String id) {
			int res = 0;
			try {
				Optional<String> optionalEntity = repositoryContactLt.findMaxCpsqByCpid(id);
				logger.info("maxCpsqByCpid :: " + optionalEntity);
				if(optionalEntity.orElse(null) == null) {
					res = 0;
				} else {
					res = Integer.parseInt(optionalEntity.orElse("0"));
				}

				return res ;
				
			} catch (IncorrectResultSizeDataAccessException ex) {
				logger.error("max 'cpsq' 값을 정상적으로 불러오지 못했습니다. : {}", ex.getMessage());
				errorLogger.error(ex.getMessage(), ex);
				return 0;
			} catch (Exception e) {
				logger.error("Exception 발생 : {}", e.getMessage());
				errorLogger.error(e.getMessage(), e);
				return 0;
			}
		}
		
		/*
		// UPDATE CONTACTLT TABLE - CPSQ
		@Override
		@Transactional
		public void updateContactLt(Entity_ContactLt entityContactLt, String cpsq) throws Exception {
	        // 주어진 엔티티의 ID를 기준으로 기존 엔티티를 조회합니다.
	        Entity_ContactLt existingEntity = repositoryContactLt.findById(entityContactLt.getId()).orElseThrow(() ->
	                new EntityNotFoundException("Entity not found: " + entityContactLt.getId()));

	        // 기존 엔티티의 필드 값을 새로운 값으로 업데이트합니다.
	        existingEntity.getId().setCpsq(Integer.parseInt(cpsq));

	        // 변경된 엔티티를 저장합니다.
	        repositoryContactLt.save(existingEntity);
			
		}
		*/
}
