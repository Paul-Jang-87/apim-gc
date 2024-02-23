package com.infognc.apim.embeddable;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CampRt implements Serializable {

	@Column(name = "RLSQ")
	private int rlsq;
	
	@Column(name = "COID")
    private int coid;

    public CampRt() {
    }

    public int getRlsq() {return rlsq;}
    public int getCoid() {return coid;}

	public void setRlsq(int rlsq) {this.rlsq = rlsq;}
	public void setCoid(int coid) {this.coid = coid;}
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampRt that = (CampRt) o;
        return getRlsq() == that.getRlsq() &&
                Objects.equals(getCoid(), that.getCoid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCoid(), getRlsq());
    }


}
