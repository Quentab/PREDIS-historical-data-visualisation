
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gu.vesta.dao.impl.hal;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;

import java.io.IOException;
import java.util.ArrayList;
import static ihm.DATAController.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import org.apache.commons.lang.ArrayUtils;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.model.*;

/**
 *
 * @author Jack
 */
public class ExportData {
    
    //long debut,long fin,long stepTime
    
    public static void exportMatlab() throws IOException{
        
        ArrayList  list = new ArrayList();
        ArrayList <Double> date = new ArrayList();
        for (VariableData variable : allVarToExport) {

            SortedMap map = DAO.getServiceVariableValues(variable);
            Iterator iterator = map.keySet().iterator();
            ArrayList <Double> value = new ArrayList();
            
            while(iterator.hasNext()){
               
                Object key = iterator.next();
                Long tempDate = (Long) key;
                date.add(tempDate.doubleValue());
                value.add((Double)map.get(key));
            }
           //  MLDouble mlValue = new MLDouble(variable.getName() , value.toArray(new Double[0]), 3 );
             
             Double[] mab = date.toArray(new Double[0]);
             double[] src = ArrayUtils.toPrimitive(mab);
             System.out.println(src.toString());
             MLDouble mlDate = new MLDouble( "Temps",src , 1 );//
             MLDouble mlValue = new MLDouble( variable.getName(),value.toArray(new Double[0]) , 1 );   
             list.add(mlDate);
             list.add(mlValue);
        }
 
        new MatFileWriter( "mat_file.mat", list );

       }

    public static void exportCsv(long debut, long fin){

       
    }

    
}
