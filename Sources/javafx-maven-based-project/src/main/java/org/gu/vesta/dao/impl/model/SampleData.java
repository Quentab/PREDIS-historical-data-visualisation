package org.gu.vesta.dao.impl.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Sample.java
 * Copyright (c) Grenoble-Universite - Vesta-system.
 * Use, duplication or distribution is subject to authorization.
 * Created on 4 mars 2013
 * @author Sylvain Galmiche <sylvain.galmiche@vesta-system.com>
 */
@Entity
@Table(name = "samples")
public class SampleData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "variableId", nullable = false)
    private long variableId;

    @Column(name = "sampleDate", nullable = false)
    private long sampleDate;

    @Column
    private Double sampleValue;

    public SampleData() {
    }

    public SampleData(long variableId, long sampleDate, Double sampleValue) {
        this.variableId = variableId;
        this.sampleDate = sampleDate;
        this.sampleValue = sampleValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVariableId() {
        return variableId;
    }

    public void setVariableId(long variableId) {
        this.variableId = variableId;
    }

    public long getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(long sampleDate) {
        this.sampleDate = sampleDate;
    }

    public Double getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(Double sampleValue) {
        this.sampleValue = sampleValue;
    }

    @Override
    public String toString() {
        return "SampleData{" + "id=" + id + ", variableId=" + variableId + ", sampleDate=" + sampleDate + ", sampleValue=" + sampleValue + '}';
    }
}
