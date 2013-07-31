package ihm;

import static ihm.DATAController.*;
import java.util.Collection;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import java.lang.Math;
import java.util.Date;
import java.util.SortedMap;
import javafx.scene.chart.XYChart;
import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.model.VariableData;

/**
 *
 * @author Jack
 */
public class ControllerExtension {

    /**
     * Refresh the field variablesListExport of the DATAcontroller
     *
     *
     */
    static void refreshVariableListExport() {
        ObservableList<String> listExport = FXCollections.observableArrayList();
        Collection<VariableData> allVar;
        VariableData varl;

        selectedServExport = DAO.getService((String) servicesListExport.getSelectionModel().getSelectedItem());

        if (selectedServExport != null) {
            allVar = selectedServExport.getVariables();
            Iterator iter = allVar.iterator();
            while (iter.hasNext()) {
                varl = (VariableData) iter.next();
                listExport.add(varl.getName());
            }
            variablesListExport.getItems().clear();
            variablesListExport.getItems().addAll(listExport);
        }
        //  refreshVariableListToExport();
    }

    /**
     * Refresh the field listVariableToExport of the DATAcontroller
     *
     *
     */
    static void refreshVariableListToExport() {

        if (allVarToExport != null) {
            listVariableToExport.getItems().clear();
            Iterator<VariableData> iterVarExport = allVarToExport.iterator();
            while (iterVarExport.hasNext()) {
                String next = iterVarExport.next().getName();
                listVariableToExport.getItems().add(next);
            }
        }
    }

    /**
     * Delete the variable in the export list of the DATAcontroller
     *
     *
     */
    static void deleteVariableFromExport() {

        int index = listVariableToExport.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            VariableData var = (VariableData) allVarToExport.toArray()[index];
            if (var != null) {
                allVarToExport.remove(var);
            }
        }
        refreshVariableListToExport();
    }

    /**
     * Add a variable in the export list of the DATAcontroller
     *
     *
     */
    static void dragVariableToExport() {

        String selectedVarName = (String) variablesListExport.getSelectionModel().getSelectedItem();
        if (selectedVarName != null && selectedServExport != null) {
            VariableData currentVariable = DAO.getVariable(selectedServExport.getName(), selectedVarName);
            allVarToExport.add(currentVariable);
        }
        refreshVariableListToExport();
    }

   /**
    *
    * This method is called to change the size of the window
    *
    */
    static void axisteprefresh() {

        System.out.println("conteur de zoom : " + xSizecounter);

        switch (xSizecounter) {

            case 0: {//2ans
                textTailleZoom.setText("2 Ans");
                deltaX = 315360000000L;
                break;
            }
            case 1: { // 1 ans  
                textTailleZoom.setText("1 Ans");
                deltaX = 31536000000L;

                break;
            }
            case 2: { //6 months
                textTailleZoom.setText("6 Mois");
                deltaX = 15768000000L;
                break;
            }
            case 3: { // 1 month
                textTailleZoom.setText("1 Mois");
                deltaX = 2628000000L;
                break;
            }
            case 4: { // 15 days
                textTailleZoom.setText("15 Jours");
                deltaX = 1314000000L;
                break;
            }
            case 5: { // 1 week
                textTailleZoom.setText("1 Semaine");
                deltaX = 604800000L;
                break;
            }
            case 6: { //1 Day
                textTailleZoom.setText("1 Jour");
                deltaX = 86400000L;
                break;
            }
            case 7: { //12 hours

                deltaX = 43200000L;
                textTailleZoom.setText("12 Heures");
                break;
            }
            case 8: { //1 hour
                textTailleZoom.setText("1 Heure");
                deltaX = 3600000L;
                break;
            }
        }

        int sizeWindow = numberOfKeyInWindowResearch();
        xSlide.setMax(size - sizeWindow);
        xSlide.setValue(xSlide.getValue() - 1);
        cursorChanged();
    }

    /**
     * Import the data of the considered variable into the field map sortedData
     * (can take a while for big series)
     */
    static public void importData() {
        System.out.println("methode IMPORT DATA");

        VariableData var = null;
        if (!allVariablesList1.getSelectionModel().isEmpty()) {
            var = (VariableData) allVariablesList1.getSelectionModel().getSelectedItem();
        } else if (!variablesList.getSelectionModel().isEmpty()) {
            var = (VariableData) variablesList.getSelectionModel().getSelectedItem();
        }
        if (var != null) {
            sortedData = DAO.getServiceVariableValues(var);
            if(!sortedData.isEmpty()){
            firstElement = sortedData.firstKey();
            lastElement = sortedData.lastKey();
            size = sortedData.keySet().size();
            listKey = sortedData.keySet().toArray(new Long[0]);

            System.out.println("firstElement = " + firstElement);
            System.out.println("lastElement = " + lastElement);
            System.out.println("deltaX = " + deltaX);
            
            System.out.println("fin methode IMPORT DATA ");
        }else{
            System.out.println("Variable Vide");
            }
        cursorChanged();
        }

    }

    /**
     * Refresh the window when something changed.
     *
     * -it draws all the samples if there is less than 250 sample in the window
     * -if not it makes sure the number of sample is under 250 by taking one of
     * n sample. So sometimes that involves distortion in the graphs for big
     * windows but its not annoying because you draw big series in order to look
     * at the tendancy, not to have precise values.
     */
    public static void cursorChanged() {
        ObservableList<XYChart.Data<String, Double>> listeSerie = series1.getData();
        if (sortedData != null) {
            Object key = 0L;
            int numberOfKeyInWindow;
            int step = 1;

            series1.getData().clear();
            curentCursor = (int) xSlide.getValue();
            numberOfKeyInWindow = numberOfKeyInWindowResearch();

            if (numberOfKeyInWindow > 200) {
                step = numberOfKeyInWindow / 200;
            }

            //Permet d'eviter un bug d'affichage due a la JDK
            if (curentCursor < formerCursor && curentCursor >= numberOfKeyInWindow) {
                curentCursor = curentCursor - numberOfKeyInWindow;
                xSlide.setValue(curentCursor);
            } else if (curentCursor < formerCursor && curentCursor < numberOfKeyInWindow) {
                curentCursor = numberOfKeyInWindow + 1;
                xSlide.setValue(curentCursor);
            }


            if (curentCursor < 0) {
                curentCursor = 0;
            }
            System.out.println("currentCursor = " + curentCursor);

            if (numberOfKeyInWindow + curentCursor < size && numberOfKeyInWindow > 0) {
                SortedMap subMap = sortedData.subMap(listKey[curentCursor], listKey[curentCursor + numberOfKeyInWindow]);
                Iterator iterator = subMap.keySet().iterator();
                if (step > 1) {
                    xSlide.setBlockIncrement(step * 2);
                    int j = 0;
                    System.out.println("nombre de sample supérieur a 200 et step = " + step);
                    while (iterator.hasNext()) {
                        j++;
                        key = iterator.next();
                        if (j == 1) {
                            series1.getData().add(new XYChart.Data(toRealValue((Long) key), subMap.get(key)));
                        } else if (j >= step) {
                            j = 0;
                        }
                        xSlide.setBlockIncrement(1);
                    }
                } else {
                    System.out.println("nombre de sample inférieur a 1000");
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        series1.getData().add(new XYChart.Data(toRealValue((Long) key), subMap.get(key)));
                    }
                }
                formerCursor = curentCursor;
            }
        }
    }

    /**
     *
     * @return number of keys the chart need to plot to have precise
     * boundaries(ex 1 month, 1 year)
     */
    public static int numberOfKeyInWindowResearch() {
        int i = 0;

        if (curentCursor >= 0 && size != 0) {
            Long lastKey = new Long(listKey[curentCursor] + deltaX);

            //System.out.println("actualFirstKey = "+listKey[actualFirstKey]);
            //System.out.println("lastKey        = "+lastKey);

            while ((listKey[curentCursor + i].longValue() < lastKey) && curentCursor + i < size - 1) {
                i++;
            }
        }
        return i;
    }

    /**
     *
     * @param date from the class Date
     * @return poch time in ms
     */
    public static double toNumericValue(Date date) {
        return date.getTime();
    }

    /**
     *
     * @param v Time in Epoch time
     * @return a string with the date displayed with the good time zonne
     */
    public static String toRealValue(long v) {
        Date date = new Date(v);

        return sdf.format(date);
    }
}
