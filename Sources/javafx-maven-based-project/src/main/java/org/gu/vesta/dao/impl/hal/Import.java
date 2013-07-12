/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gu.vesta.dao.impl.hal;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.model.SampleData;
import org.gu.vesta.dao.impl.model.ServiceData;
import org.gu.vesta.dao.impl.model.VariableData;

/**
 *
 * @author Jack
 */
public class Import{

      static List<VariableData> variables = new ArrayList<VariableData>();
      static Date milis = new Date();
      static int nbLignes =0;
      static Timer timer = new Timer();
      static long debut = Mesures.pointDeMersure();
      static String normalDate;
      
        
    public static void  main(String[] args){
      
         tacheMesure(200);
         DAO.clear();
         
          try {
              csvParser(new File("E:\\Cour\\Stage PREDIS\\Travaux_BDD\\HAL\\9_2012(MIN) - Copie.CSV"), ';', 0);
              System.out.println("second fichier");
              csvParser(new File("E:\\Cour\\Stage PREDIS\\Travaux_BDD\\HAL\\9_2012(MIN) - Copie2.CSV"), ';', 0);
              System.out.println("fin");
              
              // test(new File("E:\\Cour\\Stage PREDIS\\Travaux Base de Donn�es\\HAL\\2013_03_04_2013_03_14_Espace_Bureau.csv"), ',', 3);
             // test(new File("E:\\Cour\\Stage PREDIS\\Travaux Base de Donn�es\\HAL\\2013_03_04_2013_03_14_Espace_Bureau.csv"), ',', 3);
          } catch (ParseException ex) {
              Logger.getLogger(Import.class.getName()).log(Level.SEVERE, null, ex);
          }
   
    }

    
    public static void csvParser(File file, char separator, int startLineIndex) throws ParseException {
        Parser csvParser = new Parser(file, separator, startLineIndex);
        String[] columNames = csvParser.getNextLineTokens();
        String[] lineData;
        String finalDate;
        ServiceData serv;
   
   //create service if non exist    
        if(DAO.getAllServices().isEmpty()){
             serv = new ServiceData("GTB");
        }else{         
            serv = DAO.getAllServices().iterator().next();           
        }
        
        Iterator iterVar ;
       
   //create var if non exist     
        Collection allvariables = DAO.getAllVariables();
        
        for (int i = 0 ; i < columNames.length ; i++ ){
        
            VariableData var = new VariableData(columNames[i]);
      
            if(!allvariables.contains(var)){
                     serv.addVariable(var);      
                }
          DAO.saveService(serv);  
        } 
  //fill a local list if empty      
        allvariables = DAO.getAllVariables();
        iterVar=allvariables.iterator();
        
   if(variables.isEmpty()){
      
       while(iterVar.hasNext()){
           VariableData var = (VariableData)iterVar.next();
           variables.add(var);
       }
   }     
 
   // parsing loop
        while ((lineData = csvParser.getNextLineTokens()) != null) {  
         nbLignes++;
        
            for (int i = 0; i < columNames.length; i++) {
               
                String columName = columNames[i];
                String value;
                if (i < lineData.length) {
                    value = lineData[i];
                } else {
                    value = "";
                }
    //Format date          
                if(i<2){
                        String date = lineData[0];
                        //  System.out.println(date);
                        normalDate=date.substring(0,6)+"20"+date.substring(6,date.length()); 
                         //   System.out.println(normalDate);
               
                         String normalTime = lineData[1];
                   
                         SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.FRENCH);
                         finalDate = normalDate+" "+normalTime;
                         //System.out.println(finalDate);
                       
                          milis = sdf.parse(finalDate);
       //Format Data     
                }else{
       
                 //    System.out.println(columName + " = " + value);
                       
                    if(!value.isEmpty()){
                       String ret=value.replaceAll("[,]",".");
                       
                       long id = variables.get(i).getId();
                       SampleData sample = new SampleData(id,milis.getTime(),Double.parseDouble(ret));
                       DAO.saveSample(sample); 
                    }
                }
            }
            System.out.println(nbLignes);
        }

    }
   
    public static void writeResults() throws FileNotFoundException, UnsupportedEncodingException{
        
        System.out.println("Write");
        FileWriter writer = null;
        long fin = pointDeMersure();
        
        String phrase = "" + (fin - debut) + "\t "+ Import.getnbLignes() +"\r\n";
        
     try {  
        writer =  new FileWriter("Resultat-de-mesure.txt", true);
    
        writer.append(phrase);
        
   } catch (IOException ex){
     // report
   } finally {
      try {writer.close();} catch (Exception ex) {}
   }
     
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
    
    public static int getnbLignes(){
        int lignes = nbLignes;
        nbLignes=0;
        return lignes;
    }
}
