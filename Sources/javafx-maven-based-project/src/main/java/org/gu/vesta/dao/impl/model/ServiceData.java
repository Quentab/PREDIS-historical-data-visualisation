package org.gu.vesta.dao.impl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ServiceData.java
 * Copyright (c) Grenoble-Universite - Vesta-system.
 * Use, duplication or distribution is subject to authorization.
 * Created on 4 mars 2013
 * @author Sylvain Galmiche <sylvain.galmiche@vesta-system.com>
 */
@Entity
@Table(name = "services")
public class ServiceData implements Serializable {

    @Id
    @Column(nullable = false)
    private String name;

    @Column
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Collection<VariableData> variables;

    public ServiceData() {
    }

    public ServiceData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<VariableData> getVariables() {
        return variables;
    }

    public void setVariables(Collection<VariableData> variables) {
        this.variables = variables;
    }

    public VariableData getVariable(String name) {
        VariableData retValue = null;
        if (variables != null) {
            for (VariableData variable : variables) {
                if (variable.getName().equals(name)) {
                    retValue = variable;
                    break;
                }
            }
        }
        return retValue;
    }

    public void addVariable(VariableData variable) {
        if (variables == null) {
            variables = new ArrayList<VariableData>();
        }
        variables.add(variable);
    }

    public void removeAllVariables() {
        if (variables != null) {
            variables.clear();
        }
    }

    public void removeVariable(VariableData variable) {
        if (variables != null) {
            variables.remove(variable);
        }
    }

    @Override
    public String toString() {
        return "ServiceData{" + ", name=" + name + ", variables=" + variables + '}';
    }
}
