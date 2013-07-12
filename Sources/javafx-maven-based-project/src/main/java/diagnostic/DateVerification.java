/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagnostic;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.model.SampleData;
import org.gu.vesta.dao.impl.model.VariableData;

/**
 *
 * @author Jack
 */
public class DateVerification {

    /**
     * @param args the command line arguments
     */
   static SampleData samp ; 
    
    public static void main(String[] args) {
       
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.FRENCH);
       
        
       Collection <VariableData> Vars = DAO.getAllVariables();
       Iterator <VariableData> itVars = Vars.iterator();
       
       int i=0;
       int j= 20;
       int index =0;
       
       while(i<j){
           i++;
           itVars.next();          
       }
      
       System.out.println(itVars.next());
       Collection <SampleData> samps = DAO.getSamples(itVars.next());
    
       System.out.println("taille de l'échantillon "+samps.size());
       
       Iterator <SampleData> itSample = samps.iterator();
       
       while(itSample.hasNext()){
           
            index++;
            samp = itSample.next(); 
            Date date = new Date(samp.getSampleDate());
            
            System.out.println("Methode 1 Itération  itération="+index+" Date="+date.toString());    
            System.out.println("Methode 2 Itération  itération="+index+" Date="+sdf.format(date));
       } 
               
    }
}
