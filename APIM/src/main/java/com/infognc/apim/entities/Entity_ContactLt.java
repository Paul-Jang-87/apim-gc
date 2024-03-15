package com.infognc.apim.entities;

import com.infognc.apim.embeddable.ContactLt;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "CONTACTLT")
public class Entity_ContactLt {
	
	@EmbeddedId
    private ContactLt id;
	
	@Column(name = "CSKE")
 	private String cske;

	@Column(name = "TNO1")
	private String tno1;

	@Column(name = "TNO2")
	private String tno2;
	
	@Column(name = "TNO3")
	private String tno3;
	
	@Column(name = "CSNA")
	private String csna;
	
	@Column(name = "TKDA")
	private String tkda;
	
	@Column(name = "FLAG")
	private String flag;

	/*
	public Entity_ContactLt(String cske, String tno1, String tno2, String tno3, String csna, String tkda,
			String flag) {
		this.id = new ContactLt();
		this.cske = cske;
		this.tno1 = tno1;
		this.tno2 = tno2;
		this.tno3 = tno3;
		this.csna = csna;
		this.tkda = tkda;
		this.flag = flag;
	}
	*/
	
}
	
