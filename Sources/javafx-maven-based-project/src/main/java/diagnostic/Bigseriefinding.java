/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagnostic;

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
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.model.SampleData;
import org.gu.vesta.dao.impl.model.VariableData;

/**
 *
 * @author tabartq
 */
public class Bigseriefinding{
 
    static long maximumHoleTime =  48*60*60 * 1000; //= 1h 
    static List<SampleData> serieSansTrou = new ArrayList();
    static List<List> allSeries = new ArrayList();
    static VariableData[] vars;
    static List<Long> listeDate = new ArrayList();
    static long debut;
    static long fin;
    static long veryDebut;
    static long veryFin;
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.FRENCH);

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException, InterruptedException {

        writeEnTete();
        System.out.println("début recherche");
        recherche();
        System.out.println("Récap : nombre de série isolées : "+ allSeries.size());
        System.out.println("very fin");
    }

    public static void recherche() throws IOException {

        vars = DAO.getAllVariables().toArray(new VariableData[0]);

        debut = System.currentTimeMillis();
        Iterator<SampleData> iter = DAO.getSamples(vars[3]).iterator();
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
                allSeries.add(listeDate);
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

           
           
    
  
   
   
   
    
