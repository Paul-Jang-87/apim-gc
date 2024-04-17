package com.infognc.apim.entities.oracle;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "TB_IVR_SURVEY_PCUBE")
public class Entity_IVR_SURVEY_PCUBE {

	@Id
	@NotNull
	@Column(name = "SEQ_NO")
	private Integer seq_no;
	
	@NotNull
	@Column(name = "SUR_ANI")
	private String sur_ani;
	
	@NotNull
	@Column(name = "SUR_GUBUN")
	private String sur_gubun;
	
	@Column(name = "SUR_INPUTCODE")
	private String sur_inputcode;
	
	@Column(name = "SUR_SURVEY1")
	private String sur_survey1;
	
	@Column(name = "SUR_SURVEY2")
	private String sur_survey2;
	
	@Column(name = "SUR_SURVEY3")
	private String sur_survey3;
	
	@NotNull
	@Column(name = "SUR_ANS_DATE")
	private String sur_ans_date;
	
	public Entity_IVR_SURVEY_PCUBE() {
		
	}
	
}
