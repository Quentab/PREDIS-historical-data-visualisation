package ihm;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import org.gu.vesta.dao.impl.model.SampleData;
import org.gu.vesta.dao.impl.model.ServiceData;
import org.gu.vesta.dao.impl.model.VariableData;
import java.text.ParseException;
import javafx.event.EventHandler;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import javafx.animation.Animation;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import static main.Main.lancerImport;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.gu.vesta.dao.impl.DAO;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.ListIterator;
import java.util.Locale;
import ihm.ControllerExtension;
import java.io.IOException;
import java.util.SortedMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.Group;
import javafx.util.Duration;
import org.gu.vesta.dao.impl.hal.ExportData;

/**
 *
 * @author BAB
 */
public class DATAController implements Initializable {

    TableColumn temps = new TableColumn("Date");
    TableColumn valeurs = new TableColumn("Valeur");
    @FXML
    TableView table;
    @FXML
    static Slider xSlide;
    @FXML
    static CategoryAxis xAxis;// = new NumberAxis(xMin,xMax,deltaX);
    @FXML
    NumberAxis yAxis;// = new NumberAxis(yMin,yMax,deltaY);
    @FXML
    static LineChart<String, Number> chart;
    @FXML
    static MenuBar menus;
    @FXML
    private WebView web = new WebView();
    private WebEngine webEngine;
    // Listes
    @FXML
    static ListView variablesList;
    @FXML
    static ListView allVariablesList1;
    @FXML
    static ListView allVariablesList2;
    @FXML
    static ListView samplesList;
    @FXML
    static ListView servicesList;
    @FXML
    static ListView servicesListExport;
    @FXML
    static TextField textTailleZoom;
    @FXML
    static TextField newVar;
    @FXML
    static TextField newServ;
    @FXML
    static Group groupeMessage;
    @FXML
    static ListView variablesListExport;
    @FXML
    static ListView listVariableToExport;
    @FXML
    static ChoiceBox typeExportChoice;
    @FXML
    static ChoiceBox unitTime;
    @FXML
    static ChoiceBox stepTime;
    @FXML
    static Slider sliderDebut;
    @FXML
    static Slider sliderFin;
    @FXML
    static TextField dateDebut;
    @FXML
    static TextField dateFin;
    @FXML
    static TextField heureFin;
    @FXML
    static TextField heureDebut;
    
    static ServiceData selectedServExport;
    public static Collection<VariableData> allVarToExport = new <VariableData> ArrayList();

    static Long deltaX;
    static double deltaY = 0;
    static int center;
    static int oldCenter = 0;
    static int xSizecounter = 8;
    
    static XYChart.Series series1 = new XYChart.Series();
    static long sampleDate = System.currentTimeMillis();
    static EventHandler<ActionEvent> e;
    static Timer timer = new Timer();
    static Timeline animation;
    static int deltaIndex;
    
    VariableData selectedVar;
    ServiceData selectedServ;
    static SortedMap<Long, Double> sortedData;
    static Long[] listKey;
    static Long firstElement;
    static Long lastElement;
    // static Collection <ObservableList<SampleData>> allSample ;
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
    static int size = 0;
    static int curentCursor;
    static int formerCursor;
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {



//_________________Animation_Chart______________________________________________

        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        xAxis.setTickLabelRotation(-80);

        temps.setCellValueFactory(new PropertyValueFactory<SampleData, Long>("sampleDate"));
        valeurs.setCellValueFactory(new PropertyValueFactory<SampleData, Double>("sampleValue"));

        chart.getData().add(series1);

        axisteprefresh();
        groupeMessage.setVisible(false);
//     table.getColumns().addAll(temps, valeurs);

        xSlide.setBlockIncrement(1);
        xSlide.setMin(0);


//__________________ php my admin ______________________________________________

        webEngine = web.getEngine();
        webEngine.load("http://localhost/phpmyadmin/");


//____________________Lists_____________________________________________________       

        refreshServiceList();
        refreshAllVariableList();
//____________________Lists_____________________________________________________        

        ObservableList<String> choixExport = FXCollections.observableArrayList();
        choixExport.add("CSV");
        choixExport.add("Matlab");
        typeExportChoice.setItems(choixExport);

        ObservableList <Integer> stepTimeItems = FXCollections.observableArrayList();
        
        stepTimeItems.add(1);
        stepTimeItems.add(2);
        stepTimeItems.add(3);
        stepTimeItems.add(5);
        stepTimeItems.add(6);
        stepTimeItems.add(10);
        stepTimeItems.add(12);
        stepTimeItems.add(30);
        
         ObservableList<String> unitTimeItems = FXCollections.observableArrayList();
         unitTimeItems.add("S");
         unitTimeItems.add("M");
         unitTimeItems.add("H");
         unitTimeItems.add("J");
        unitTime.setItems(stepTimeItems);

    }

    public static void lancementData(int vitesse) {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
            }
        }, vitesse * 250, vitesse * 500); // periode et delay en ms
    }

    //____________________TAB 1 ___________________________________________________
    @FXML
    public void refreshAllVariableList() {
        ObservableList<VariableData> listVar = FXCollections.observableArrayList();
        Collection<VariableData> allVar;
        VariableData varl;
        allVar = DAO.getAllVariables();
        Iterator iter = allVar.iterator();

        while (iter.hasNext()) {
            varl = (VariableData) iter.next();
            listVar.add(varl);
        }
        allVariablesList1.getItems().clear();
        allVariablesList1.getItems().addAll(listVar);
        //allVariablesList2.getItems().clear();
        //allVariablesList2.getItems().addAll(listVar);
    }

    @FXML
    public void cursorChanged() {

        ControllerExtension.cursorChanged();

    }

    @FXML
    public void importData() {

        showMessage();
        
        ControllerExtension.importData();
        
        hideMessage();

    }
    private static Timeline animation1 = new Timeline();
    private static Timeline animation2 = new Timeline();

    public void showMessage() {
        animation1.stop();
        animation2.getKeyFrames().addAll(new KeyFrame(new Duration(500),
                new KeyValue(groupeMessage.opacityProperty(), 1)));
        animation2.play();
    }

    public void hideMessage() {
        animation2.stop();
        animation1.getKeyFrames().addAll(new KeyFrame(new Duration(500),
                new KeyValue(groupeMessage.opacityProperty(), 0)));
        animation1.play();
    }

    @FXML
    public void zoomInXaxis() {
        if (xSizecounter < 8) {
            xSizecounter++;
        }
        axisteprefresh();


    }

    @FXML
    public void zoomOutXaxis() {
        if (xSizecounter > 0) {
            xSizecounter--;
        }
        axisteprefresh();

    }

    public void axisteprefresh() {
        ControllerExtension.axisteprefresh();
    }

    @FXML
    public void selectService() {
        if (!servicesList.getSelectionModel().isEmpty()) {
            selectedServ = DAO.getService((String) servicesList.getSelectionModel().getSelectedItem());
            refreshVariableList();
        }
    }

    @FXML
    public void refreshVariableList() {
        Collection<VariableData> allVar;
        if (selectedServ != null) {
            allVar = selectedServ.getVariables();
            variablesList.getItems().clear();
            variablesList.getItems().addAll(allVar);
        }
    }

    @FXML
    public void refreshServiceList() {

        ObservableList<String> list = FXCollections.observableArrayList();
        list.clear();
        Collection<ServiceData> allServ;
        ServiceData serv;
        allServ = DAO.getAllServices();
        Iterator iter = allServ.iterator();
        while (iter.hasNext()) {
            serv = (ServiceData) iter.next();
            list.add(serv.getName());
        }
        servicesList.getItems().setAll(list);
        servicesListExport.getItems().setAll(list);
    }

    @FXML
    public void addService() {

        if (newServ.getText() != null) {
            ServiceData serv;
            serv = new ServiceData(newServ.getText());
            DAO.saveService(serv);
            refreshServiceList();
        }
    }

    @FXML
    public void deleteVariableFromService() {

        if (!variablesList.getSelectionModel().isEmpty() && selectedServ != null) {
            VariableData var = (VariableData) variablesList.getSelectionModel().getSelectedItem();
            selectedServ.removeVariable(var);
            DAO.saveService(selectedServ);
        }
        refreshVariableList();
    }

    @FXML
    public void addVariableToService() {

        if (selectedServ != null) {
            if (!allVariablesList1.getSelectionModel().isEmpty() && selectedServ != null) {
                VariableData var = (VariableData) allVariablesList1.getSelectionModel().getSelectedItem();
                for (VariableData varIter : selectedServ.getVariables()) {
                    if (varIter.getName().equals(var.getName())) {
                        selectedServ.removeVariable(var);
                    }
                }
                // DAO.saveService(selectedServ);
                selectedServ.addVariable(var);
                DAO.saveService(selectedServ);

                System.out.println("variable ajoutée");
            }

            refreshVariableList();
        }
    }

    @FXML
    public void exportData() throws IOException, ParseException {
        
        String stepUnit = (String)unitTime.getSelectionModel().getSelectedItem();
        Long steptime = (Long)stepTime.getSelectionModel().getSelectedItem();
        Long step = 0L;
        
        if (!stepUnit.isEmpty() && stepTime != null){
        
        String hDebut = heureDebut.getText();
        String hFin = heureFin.getText();
       
        
        if (hDebut == null){hDebut="00:00:00";}
        if (hFin == null){hFin="00:00:00";}
        //On converti le pas en secondes
        Date datDebut = sdf.parse(dateDebut.getText()+hDebut);
        Long debut = datDebut.getTime();
        Date datFin = sdf.parse(dateFin.getText()+hFin);
        Long fin = datFin.getTime();
        
        if(stepUnit.contains("S")){
            step = steptime;
        }else if (stepUnit.contains("M")){
            step = steptime*60;
        }else if (stepUnit.contains("H")){
            step = steptime*3600;
        }else if (stepUnit.contains("J")){
            step = steptime*3600*24;
        }
        
        if (typeExportChoice.getValue() != null) {
            String type = (String) typeExportChoice.getSelectionModel().getSelectedItem();
            if (type.equals("CSV")) {
                System.out.println("CSV");
            } else if (type.equals("Matlab")) {
                System.out.println("Matlab debut:"+debut+" fin:"+fin+" step:"+step );
                ExportData.exportMatlab(allVarToExport,debut,fin,step);
            }
        }
      }
    }

    @FXML
    public void refreshVariableListExport() {
        ControllerExtension.refreshVariableListExport();
    }

    @FXML
    public void refreshVariableListToExport() {
        ControllerExtension.refreshVariableListToExport();
    }

    @FXML
    public void deleteVariableFromExport() {
        ControllerExtension.deleteVariableFromExport();
    }

    @FXML
    public void dragVariableToExport() {
        ControllerExtension.dragVariableToExport();
    }

    public void cleanDB() {
        DAO.clear();
    }
}
