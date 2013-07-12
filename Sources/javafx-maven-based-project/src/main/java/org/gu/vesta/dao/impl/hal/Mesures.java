package org.gu.vesta.dao.impl.hal;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Collection;
import java.util.SortedMap;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.model.SampleData;
import org.gu.vesta.dao.impl.model.ServiceData;
import org.gu.vesta.dao.impl.model.VariableData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.DAO;
//import org.gu.vesta.dao.impl.model.Datasources;
/**
 *
 * @author Jack
 */
public class Mesures   {

    /**
     * @param args the command line arguments
     */
     // static  DAO x = new DAO();
      static int j=1;
      static int stop;
      static int count;
      static int max = 10; 
      static ActionEvent e;
      static Timer timer = new Timer();
      static long debut;
      final static Object lock1 = new Object();


  
    public static void main(String[] args) {

     
 
    }
    
   public static void writeResults() throws FileNotFoundException, UnsupportedEncodingException{
       long fin = pointDeMersure(); 
       String phrase = "Temps d'execution : "+ (fin - debut) + " pour "+ Import.getnbLignes() +" �chantillons";
        
        PrintWriter writer = new PrintWriter("Resultat-de-mesure.txt", "UTF-8");

        writer.println(phrase);
 
        debut = pointDeMersure(); 
   }
    
    
/*______________________Mesures de temps_________________________*/   
public static long pointDeMersure(){          
              long p = System.currentTimeMillis();  
             return p;        
    }


/*___________________Structure a base de timer _______________________________*/
public static void tacheMesure(int period){

    timer.scheduleAtFixedRate(new TimerTask(){ public void run()
        {
        try {
            writeResults();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mesures.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Mesures.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
      }, 1000*period,1000*period);
    }
   
public static void stopRefresh (){
            timer.cancel();
    }
 
   /* __________________Structure classique______________________________*/ 
   public static void creaServices( int j){

     for (int i=0;i<j;i++){   
         ServiceData service1 = new ServiceData("service"+i+"");
         DAO.saveService(service1);
         System.out.println("service "+i);
     }
   }
   
   public static void creaVar( String service, int j){
            
     for (int i=0;i<j;i++){
         
        VariableData variable1 = new VariableData("variable"+i+"");
        ServiceData service1 = DAO.getService("service0");
        service1.addVariable(variable1);
        DAO.saveService(service1);
        System.out.println("var "+  i);
     }  
   }       
   
        public static  void creaSamples (String variable){
            
			
        ServiceData service1 = DAO.getService("service0");
        VariableData variable1 = service1.getVariable(variable);
        long variableId = variable1.getId();        
        long sampleDate = System.currentTimeMillis();
       
      for( int i=1;i<=max;i++){
        SampleData sample = new SampleData(variableId, sampleDate,(double)j);
        DAO.saveSample(sample);
     
        }
     }

}
