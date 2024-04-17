package com.infognc.apim.entities.oracle;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "TB_IVR_SURVEY_UCUBE_SDW")
public class Entity_IVR_SURVEY_UCUBE_SDW {

	@Id
	@NotNull
	@Column(name = "ORDERID")
	private Integer orderid;
	
	@NotNull
	@Column(name = "CMD")
	private String cmd;
	
	@Column(name = "OLD_SEQ_NO")
	private String old_seq_no;
	
	@Column(name = "OLD_SUR_ANI")
	private String old_sur_ani;
	
	@Column(name = "OLD_SUR_GUBUN")
	private String old_sur_gubun;
	
	@Column(name = "OLD_SUR_INPUTCODE")
	private String old_sur_inputcode;
	
	@Column(name = "OLD_SUR_SURVEY1")
	private String old_sur_survey1;
	
	@Column(name = "OLD_SUR_SURVEY2")
	private String old_sur_survey2;
	
	@Column(name = "OLD_SUR_SURVEY3")
	private String old_sur_survey3;
	
	@Column(name = "OLD_SUR_ANS_DATE")
	private String old_sur_ans_date;
	
	@Column(name = "NEW_SEQ_NO")
	private String new_seq_no;
	
	@Column(name = "NEW_SUR_ANI")
	private String new_sur_ani;
	
	@Column(name = "NEW_SUR_GUBUN")
	private String new_sur_gubun;
	
	@Column(name = "NEW_SUR_INPUTCODE")
	private String new_sur_inputcode;
	
	@Column(name = "NEW_SUR_SURVEY1")
	private String new_sur_survey1;
	
	@Column(name = "NEW_SUR_SURVEY2")
	private String new_sur_survey2;
	
	@Column(name = "NEW_SUR_SURVEY3")
	private String new_sur_survey3;
	
	@Column(name = "NEW_SUR_ANS_DATE")
	private String new_sur_ans_date;
	
	public Entity_IVR_SURVEY_UCUBE_SDW() {
		
	}
	
}
