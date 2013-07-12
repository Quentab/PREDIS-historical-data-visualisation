/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagnostic;

import static diagnostic.DateVerification.samp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.model.SampleData;
import org.gu.vesta.dao.impl.model.VariableData;

/**
 *
 * @author Jack
 */
public class Diagnostic {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        VariableData [] vars = DAO.getAllVariables().toArray(new VariableData[0]);
        
        rechercheDoublon(vars[5]);
        
        suppressionDoublonCourant();
    }
    
        static List <SampleData> doublons = new ArrayList();  
  
 public static void rechercheDoublon( VariableData variable){
     
       SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.FRENCH);
       
       Collection <SampleData> samps = DAO.getSamples(variable);
       Iterator <SampleData> itSample = samps.iterator();
       
       List <Date> listeDate = new ArrayList();
       
       int index=0;
       
       while(itSample.hasNext()){
           
            index++;
            samp = itSample.next(); 
            Date date = new Date(samp.getSampleDate());
            
            if(listeDate.contains(date)){
                 doublons.add(samp);
            }else{
                 listeDate.add(date);
            }     
        }   
       System.out.println("les doublons sont : "+doublons);
}
 
  public static void suppressionDoublonCourant(){
      
     Iterator <SampleData> iterDouble = doublons.iterator();
      
        while(iterDouble.hasNext()){   
           DAO.deleteSample(iterDouble.next());
        }
      
  }
  
   static List <SampleData> depassement = new ArrayList(); 
   
 public static void detectionHorsLimites(VariableData variable,double limiteHaute,double limiteBasse){ 
 
       Collection <SampleData> samps = DAO.getSamples(variable);
       Iterator <SampleData> itSample = samps.iterator();
     
        while(itSample.hasNext()){
 
            samp = itSample.next(); 
    
            if(samp.getSampleValue()<=limiteBasse || samp.getSampleValue()>=limiteHaute){
                 depassement.add(samp);
            }   
        }   

        
        
        
} 
}
