package com.infognc.apim.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "CAMPMA")
public class Entity_CampMa {
	
	@Column(name = "COID")
	private int coid;

	@Id
	@Column(name = "CPID")
	private String cpid;
	
	@Column(name = "CPNA")
 	private String cpna;

	public Entity_CampMa() {
	}

	public Entity_CampMa(int coid, String cpid, String cpna) {
		this.coid = coid;
		this.cpid = cpid;
		this.cpna = cpna;
	}


	public int getCoid() { return coid; }
	public void setCoid(int coid) {this.coid = coid;}

	
	public String getCpid() {return cpid;}
	public void setCpid(String cpid) {this.cpid = cpid;}
	
	
	public String getCpna() {return cpna;}
	public void setCpna(String cpna) {this.cpna = cpna;}
	
	
}
	
