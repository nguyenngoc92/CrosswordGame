package org.com.myapp.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties
public class SubjectData implements Serializable {

	private Integer idSubject;
	private String name;
	private Date createDate;
	private BigInteger totalMatch;

	public SubjectData() {

	}

	public SubjectData(int idSubject, String name, Date createDate,
			BigInteger totalMatch) {
		this.idSubject = idSubject;
		this.name = name;
		this.createDate = createDate;
		this.totalMatch = totalMatch;
	}

	public Integer getIdSubject() {
		return idSubject;
	}

	public void setIdSubject(Integer idSubject) {
		this.idSubject = idSubject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public BigInteger getTotalMatch() {
		return totalMatch;
	}

	public void setTotalMatch(BigInteger totalMatch) {
		this.totalMatch = totalMatch;
	}


	
}
