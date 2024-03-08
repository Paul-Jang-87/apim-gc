package com.infognc.apim.entities;

import com.infognc.apim.embeddable.ContactLt;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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
	public Entity_ContactLt() {
		
	}

	public Entity_ContactLt(
	 String cpid,  
	 int cpsq,   
	 String cske,   
	 String tno1,   
	 String tno2,   
	 String tno3,   
	 String csna,   
	 String tkda,   
	 String flag,
	 ContactLt id) {
	this.id = new ContactLt();
    this.cske =  cske ;
	this.tno1 =  tno1 ;
	this.tno2 =  tno2 ;
	this.tno3 =  tno3 ;
	this.csna =  csna ;
	this.tkda =  tkda ;
	this.flag =  flag ;
	}

	public ContactLt getId() {return id;}
	public String getCpid() {return id.getCpid();}
	public int getCpsq() {return id.getCpsq();}
	public String getCske() {return cske;}
	public String getTn01() {return tno1;}
	public String getTn02() {return tno2;}
	public String getTn03() { return tno3; }
	public String getCsna() { return csna; }
	public String getTkda() { return tkda; }
	public String getFlag() { return flag; }
	
	public void setId(ContactLt id) {this.id = id;}
	public void setCske(String cske) {this.cske = cske;}
	public void setTn01(String tno1) {this.tno1 = tno1;}
	public void setTn02(String tno2) {this.tno2 = tno2;}
	public void setTn03(String tno3) {this.tno3 = tno3;}
	public void setCsna(String csna) {this.csna = csna;}
	public void setTkda(String tkda) {this.tkda = tkda;}
	public void setFlag(String flag) {this.flag = flag;}
*/	
}
	
