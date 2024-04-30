package com.infognc.apim.embeddable;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ContactLt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4671268505557288462L;

	@NotNull
	@Column(name = "CPID")
    private String cpid;

	@NotNull
    @Column(name = "CPSQ")
    private int cpsq=0;
	
	public ContactLt(){
		
	}


}
