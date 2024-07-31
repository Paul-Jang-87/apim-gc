package com.infognc.apim.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infognc.apim.entities.postgre.Entity_ContactLt;
import com.infognc.apim.repositories.postgre.Repository_ContactLt;
import com.infognc.apim.service.PostgreService;


@Service
public class PostgreServiceImpl implements PostgreService{
	private static final Logger logger = LoggerFactory.getLogger(PostgreServiceImpl.class);
	private static final Logger errorLogger = LoggerFactory.getLogger("ErrorLogger");
	private final Repository_ContactLt repositoryContactLt;
	
	public PostgreServiceImpl(Repository_ContactLt repositoryContactLt) {
		this.repositoryContactLt = repositoryContactLt;
	}
		
		@Override
		@Transactional
		public Integer insertContactLt(Entity_ContactLt entityContactLt) {
			
			Optional<Entity_ContactLt> existingEntity = repositoryContactLt.findById(entityContactLt.getId());

			if (existingEntity.isPresent()) {
			    throw new DataIntegrityViolationException("해당 복합 키를 가진 레코드가 이미 존재합니다");
			}
			repositoryContactLt.save(entityContactLt);
			
			return 1;
		}
		
		// SELECT CONTACTLT TABLE - MAX CPSQ 
		@Override
		@Transactional
		public Integer selectMaxCpsq(String id) {
			int res = 0;
			try {
				Optional<String> optionalEntity = repositoryContactLt.findMaxCpsqByCpid(id);
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
		
}
