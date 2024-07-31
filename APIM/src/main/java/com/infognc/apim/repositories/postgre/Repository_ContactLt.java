package com.infognc.apim.repositories.postgre;

import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.infognc.apim.embeddable.ContactLt;
import com.infognc.apim.entities.postgre.Entity_ContactLt;

import jakarta.persistence.LockModeType;


@Repository
public interface Repository_ContactLt extends CrudRepository<Entity_ContactLt,  ContactLt> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Entity_ContactLt> findById(ContactLt id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT MAX(c.cpsq) FROM CONTACTLT c WHERE c.cpid = :cpid", nativeQuery = true)
    Optional<String> findMaxCpsqByCpid(@Param("cpid") String cpid);
    
}
