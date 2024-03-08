package com.infognc.apim.entities;

import java.util.Date;

import com.infognc.apim.embeddable.CampRt;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access=AccessLevel.PROTECTED) // 기본생성자 막고, 프록시를 위해 protected까지 허용
@Data	// getter, setter 생성
@Table(name = "CAMPRT")
public class Entity_CampRt {
	
	@EmbeddedId		// 복합키
    private CampRt id;
	
	@Column(name = "CONTACTID_LIST_ID")
	private String contactLtId;
	
	@Column(name = "CONTACT_ID")
 	private String contactid;
	
	@Column(name = "IBM_SLTN_CNTA_HUB_ID")
	private int hubid;
	
	@Column(name = "CPSQ")
	private int cpsq;
	
	@Column(name = "TKDA")	// 2024.03.06 TKDA 컬럼 추가 varchar(4000)
	private int tkda;
	
	@Column(name = "DIRT")
	private int dirt;
	
	@Column(name = "DICT")
	private int dict;
	
	@Column(name = "CAMP_ID")
	private String cpid;
	
	@Column(name = "DIDT")
 	private Date didt;
	

	public Entity_CampRt(String contactLtId, String contactid, int hubid, int cpsq, int tkda, int dirt,
			int dict, String cpid, Date didt) {
		this.id = new CampRt();;
		this.contactLtId = contactLtId;
		this.contactid = contactid;
		this.hubid = hubid;
		this.cpsq = cpsq;
		this.tkda = tkda;
		this.dirt = dirt;
		this.dict = dict;
		this.cpid = cpid;
		this.didt = didt;
	}
}
	
