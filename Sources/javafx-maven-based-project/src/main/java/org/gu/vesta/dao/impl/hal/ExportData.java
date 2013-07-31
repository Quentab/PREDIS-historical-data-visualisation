
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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.lang.ArrayUtils;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.model.*;

/**
 *
 * @author Jack
 */
public class ExportData {
    
   static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
    
 /**
 * Permet d'exporter plusieurs variables dans un fichier matlab (.mat) 
 * @param stepTime step time between each samples ( seconds )
 * @param debut l'export partira de ce point 
 * @param fin l'export finira par ce point 
 */ 
    public static void exportMatlab(Collection <VariableData> varToExport,long debut,long fin,long stepTime) throws IOException{
       
        //Decodage de l'echelle de temps 
        
        long rapport=1;
        String unity = "S";
        if(stepTime>60){unity="M";rapport=60;}
        else if(stepTime>3600){unity="H";rapport=3600;}
        else if(stepTime>3600*24){unity="J";rapport=3600*24;}
        
        ArrayList  list = new ArrayList();
        ArrayList <Double> date = new ArrayList();
        for (VariableData variable : varToExport) {
      
            SortedMap map = DAO.getServiceVariableValues(variable);
            
            // recherche des clefs pour la submap
            
            Long keyDebut = keySeeker(map.keySet(), debut);
            Long keyFin   = keySeeker(map.keySet(), fin);
            
            SortedMap submap = map.subMap(keyDebut, keyFin);
            
            Iterator iterator = submap.keySet().iterator();
            ArrayList <Double> value = new ArrayList();
            
           
          if(stepTime==0){ //Pas temporel null = données brutes
            while(iterator.hasNext()){
                Object key = iterator.next();
                Long tempDate = (Long) key;
                if(list.size()>=2){//On ne rempli la date qu'une seule fois
                    date.add(tempDate.doubleValue()-keyDebut.doubleValue()); //On part de 0 pour la date   
                }
                value.add((Double)submap.get(key));
             }
          }else if(stepTime>0){
             
            
             long previousDate=0; 
             
             while(iterator.hasNext()){
                Object key = iterator.next();
                
                if(((Long)key-previousDate)>=stepTime){
                    Long tempDate = (Long) key;
                    if(list.size()>=2){//On ne rempli la date qu'une seule fois
                        date.add(tempDate.doubleValue()-keyDebut.doubleValue());
                    }
                    value.add((Double)submap.get(key));
                }
              previousDate=(Long)key;   
            } 
          }
             
             
             Double[] mab = date.toArray(new Double[0]);
             double[] src = ArrayUtils.toPrimitive(mab);
             System.out.println(src.toString());
             MLDouble mlDate = new MLDouble( "Temps",src , 1 );//
             MLDouble mlValue = new MLDouble( variable.getName(),value.toArray(new Double[0]) , 1 );   
             if(list.size()>=2){//On ne met la liste date qu'une seule fois
             list.add(mlDate);
             }
             list.add(mlValue);
        }
        
        new MatFileWriter( "Export du "+sdf.format(new Date(debut))+" au "+sdf.format(new Date(fin))+" ("+stepTime/rapport+unity+")" , list );

       }

    public static void exportCsv(long debut, long fin){

       
        
        
        
        
        
        
    }
    
    public static Long keySeeker( Set keySet,long date){
        
        
        Iterator <Long> iter = keySet.iterator();
        long key= iter.next();
        
        while(key<date){      
            key = iter.next();
        }
        
        return key;
    }
    
    
    

    
}
