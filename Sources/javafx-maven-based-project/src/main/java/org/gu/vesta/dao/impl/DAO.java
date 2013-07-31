package org.gu.vesta.dao.impl;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gu.vesta.dao.impl.model.SampleData;
import org.gu.vesta.dao.impl.model.ServiceData;
import org.gu.vesta.dao.impl.model.VariableData;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * DAO.java
 * Copyright (c) Grenoble-Universite - Vesta-system.
 * Use, duplication or distribution is subject to authorization.
 * Created on 4 mars 2013
 * @author Sylvain Galmiche <sylvain.galmiche@vesta-system.com>
 */


public class DAO {

    /** The logger */
    private static final Logger LOGGER = Logger.getLogger(DAO.class.getName());

    /** The session factory */
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Get the session factory
     * @return the session factory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Save an object into database
     * @param object the object to save
     */
    private static void saveObject(Object object) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(object);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    /**
     * Save a service into database
     * @param object the service to save
     */
    public static void saveService(ServiceData serviceData) {
        saveObject(serviceData);
    }

    /**
     * Save a sample into database
     * @param object the sample to save
     */
    public static void saveSample(SampleData sampleData) {
        saveObject(sampleData);
    }
    
    public static void saveVariable(VariableData variableData) {
        saveObject(variableData);
    }
    
    /**
     * Get all the services in the database
     * @return all the services in the database
     */
    public static Collection<ServiceData> getAllServices() {
        Collection<ServiceData> retValue = getObjects(ServiceData.class);
        return retValue;
    }

    /**
     * Get all the variables in the database
     * @return all the variables in the database
     */
    public static Collection<VariableData> getAllVariables() {
        Collection<VariableData> retValue = getObjects(VariableData.class);
        return retValue;
    }
    

    /**
     * Get all the samples in the database
     * @return all the samples in the database
     */
    public static Collection<SampleData> getSamples(String serviceName, String variableName) {
        VariableData variableData = getVariable(serviceName, variableName);
        Collection<SampleData> retValue = getSamples(variableData);
        return retValue;
    }

    /**
     * Get all the sample for a given variable
     * @param variable the given variable
     * @return all the sample for the given variable
     */
    public static Collection<SampleData> getSamples(VariableData variable) {
        long variableId = variable.getId();
        Collection<SampleData> retValue = getSamples(variableId);
        return retValue;
    }

    /**
     * Get all the sample for a given variable
     * @param variableId the id of the variable
     * @return all the sample for the given variable
     */
    public static Collection<SampleData> getSamples(long variableId) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(SampleData.class);
        criteria.add(Restrictions.like("variableId", variableId));
        Collection<SampleData> retValue = (Collection<SampleData>) criteria.list();
        session.close();
        return retValue;
    }

    
      public static Collection<SampleData> getSamplesTimeInterval(long variableId,Long startDate,Long endDate) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(SampleData.class);
        criteria.add(Restrictions.like("variableId", variableId));
        criteria.add(Restrictions.between("sampleDate",startDate, endDate));
        System.out.println("collecte des samples");
        Collection<SampleData> retValue = (Collection<SampleData>) criteria.list();
        System.out.println("envoi des samples");
        session.close();
        return retValue;
    }
    /**
     * Get all the samples
     * @return all the samples
     */
    public static Collection<SampleData> getAllSamples() {
        Collection<SampleData> retValue = getObjects(SampleData.class);
        return retValue;
    }

    /**
     * Get all the objects with the given class
     * @param <T>
     * @param cl the given class
     * @return all the objects with the given class
     */
    private static <T> Collection<T> getObjects(Class<T> cl) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(cl);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        Collection<T> retValue = criteria.list();
        session.close();
        return retValue;
    }

    /**
     * Get the service with the given name
     * @param name the given name
     * @return the service with the given name
     */
    public static ServiceData getService(String name) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(ServiceData.class);
        criteria.add(Restrictions.like("name", name));
        ServiceData retValue = (ServiceData) criteria.uniqueResult();
        session.close();
        return retValue;
    }

    /**
     * Delete a service
     * @param service the service to delete
     */
    public static void deleteService(ServiceData service) {
        deleteObject(service);
    }

    /**
     * Delete a sample
     * @param sample the sample to delete
     */
    public static void deleteSample(SampleData sample) {
        deleteObject(sample);
    }

    /**
     * Delete a variable
     * @param variable the variable to delete
     */
    public static void deleteVariable(VariableData variable) {
        deleteObject(variable);
    }

    /**
     * Delete an object
     * @param variable the object to delete
     */
    private static void deleteObject(Object object) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(object);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    /**
     * Get a variable of a service
     * @param serviceName the service name
     * @param variableName the variable name
     * @return the variable
     */
    public static VariableData getVariable(String serviceName, String variableName) {
        VariableData retValue = null;
        try {
            ServiceData serviceData = getService(serviceName);
            retValue = serviceData.getVariable(variableName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return retValue;
    }

    /**
     * Get the history for a given service variable
     * @param serviceName the service name
     * @param variableName the variable name
     * @return the history for a given service variable
     */
    public static SortedMap<Long, Double> getServiceVariableValues(String serviceName, String variableName) {
        VariableData variable = getVariable(serviceName, variableName);
        SortedMap<Long, Double> retValue = getServiceVariableValues(variable);
        return retValue;
    }

    /**
     * Get the history for a given variable
     * @param variable the variable
     * @return the history for a given service variable
     */
    public static SortedMap<Long, Double> getServiceVariableValues(VariableData variable) {
        SortedMap<Long, Double> retValue = new TreeMap<Long, Double>();
        Collection<SampleData> sampleDatas = getSamples(variable);
        for (SampleData sampleData : sampleDatas){
            retValue.put(sampleData.getSampleDate(), sampleData.getSampleValue());
        }
        return retValue;
    }
    
    

    /**
     * Delete all the services
     */
    public static void deleteAllServices() {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("delete from ServiceData");
        query.executeUpdate();
        session.close();
    }

    /**
     * Delete all the samples
     */
    public static void deleteAllSamples() {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("delete from SampleData");
        query.executeUpdate();
        session.close();
    }

    /**
     * Delete all the variables
     */
    private static void deleteAllVariables() {
        Collection<ServiceData> serviceDatas = getAllServices();
        for (ServiceData serviceData : serviceDatas) {
            serviceData.removeAllVariables();
            saveService(serviceData);
        }
    }

    /**
     * Delete all
     */
    public static void clear() {
        deleteAllSamples();
        deleteAllVariables();
        deleteAllServices();
        System.out.println("Data base cleared");
    }
}
