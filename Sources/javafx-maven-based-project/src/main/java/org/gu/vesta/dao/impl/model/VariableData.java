package org.gu.vesta.dao.impl.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * VariableData.java
 * Copyright (c) Grenoble-Universite - Vesta-system.
 * Use, duplication or distribution is subject to authorization.
 * Created on 4 mars 2013
 * @author Sylvain Galmiche <sylvain.galmiche@vesta-system.com>
 */
@Entity
@Table(name = "variables")
public class VariableData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    public VariableData() {
    }

    public VariableData(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id = " + id + " | name = " + name ;
    }
}
