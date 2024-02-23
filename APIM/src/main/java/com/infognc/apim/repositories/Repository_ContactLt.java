package com.infognc.apim.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.infognc.apim.embeddable.ContactLt;
import com.infognc.apim.entities.Entity_ContactLt;

import java.util.List;
import java.util.Optional;


@Repository
public interface Repository_ContactLt extends CrudRepository<Entity_ContactLt,  ContactLt> {
    
    @Query("SELECT c FROM Entity_ContactLt c WHERE c.id.cpid = :cpidValue")
    List<Entity_ContactLt> findByCpid(@Param("cpidValue") String id);

    Optional<Entity_ContactLt> findByCske(String id);

    Optional<Entity_ContactLt> findById(ContactLt id);

}
