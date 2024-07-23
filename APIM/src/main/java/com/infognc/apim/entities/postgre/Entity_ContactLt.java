package com.infognc.apim.entities.postgre;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.infognc.apim.embeddable.ContactLt;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
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

	@Column(name = "DATE", nullable = false, updatable = false)
	private LocalDateTime date;
	
	
	/**
	 * Entity가 저장될때 (Insert) 직전에 호출
	 * date를 현재 시간으로 저장
	 */
	@PrePersist
	protected void onCreate() {
		ZonedDateTime utcTime = ZonedDateTime.now(ZoneId.of("UTC"));
		ZonedDateTime seoulTime = utcTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
		this.date = seoulTime.toLocalDateTime();
		
	}
	
	
}
	
