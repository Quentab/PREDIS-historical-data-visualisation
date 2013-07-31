/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagnostic;

import static ihm.DATAController.allVarToExport;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.hal.ExportData;
import org.gu.vesta.dao.impl.model.SampleData;
import org.gu.vesta.dao.impl.model.VariableData;

/**
 *
 * @author tabartq
 */
public class Bigseriefinding{
 
    static long maximumHoleTime =  60*60 * 1000; //= 1h 
    static List<SampleData> serieSansTrou = new ArrayList();
    static SortedMap <Integer,List>  allSeries ;
    static List<Long> listeDate = new ArrayList();
    static Collection allBigSeries;
    static long debut;
    static long fin;
    static long veryDebut;
    static long veryFin;
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.FRENCH);
 
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException, InterruptedException {

        Collection <VariableData> vars = DAO.getAllVariables();
        
        writeEnTete();
        
        for(VariableData variable : vars){
         
            recherche(variable);
            System.out.println("Récap : nombre de série isolées : "+ allSeries.size());
            System.out.println("taille de la plus grande : "+allSeries.lastKey());
            System.out.println("taille de la plus petite : "+allSeries.firstKey()); 
            
        //Export matlab de la plus grande serie avec un pas de 1
           
            //allBigSeries.add();
            
            
            Collection oneVar= new ArrayList();
            oneVar.add(variable);
            long debut = (Long)allSeries.get(allSeries.lastKey()).get(0);
            long fin = (Long)allSeries.get(allSeries.lastKey()).get(allSeries.get(allSeries.lastKey()).size()-1);
            
            ExportData.exportMatlab(oneVar,debut,fin,60*60*1000);
            
            allSeries.clear();
        }
       
        System.out.println("very fin");
    }

    public static void recherche(VariableData var) throws IOException {

         System.out.println("début recherche pour la variable : "+var.getName());

        debut = System.currentTimeMillis();
        Iterator<SampleData> iter = DAO.getSamples(var).iterator();
        fin = System.currentTimeMillis();

        while (iter.hasNext()) {
            SampleData samp = iter.next();
            Long currentDate = samp.getSampleDate();

            //l'écart entre le dernier sample et le courant doit etre inférieur a maximumHoleTime
            
            if(listeDate.isEmpty()){
            listeDate.add(currentDate);
            }
            
            Long currentDateMaxList = Collections.max(listeDate);

            if (currentDate < (currentDateMaxList + maximumHoleTime)) {
                listeDate.add(currentDate);
            } else {// trou détecté
                 
                System.out.println("Trou DETECTE || nombre de sample sans trou précédent : "+listeDate.size() );
                allSeries.put(listeDate.size(),listeDate);
                publicationResultat("\r\n" + sdf.format(new Date((Long) listeDate.get(0))) + "\t" + sdf.format(new Date((Long) listeDate.get(listeDate.size()-1))) + "\t" + ((Long) listeDate.get(listeDate.size()-1) - (Long) listeDate.get(0))/1000 + "\t" + (fin - debut));
                listeDate.clear();
            }
        }
    }

    public static void writeEnTete() throws FileNotFoundException, UnsupportedEncodingException, IOException {

        String phrase = "date premier element\tdate dernier element\tdate de ce bout\ttemps d'import";
        FileWriter writer = new FileWriter("Resultat-de-suppression-doublons.txt", true);
        writer.append(phrase);
        writer.close();

    }

    public static void publicationResultat(String phrase ) throws IOException {

                FileWriter writer = new FileWriter("Resultat-de-recherche(48H).txt", true);
                writer.append(phrase);
                writer.close();
        
    }
    
    }

           
           
    
  
   
   
   
    
