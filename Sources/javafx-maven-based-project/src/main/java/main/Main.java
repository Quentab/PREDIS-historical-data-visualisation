package main;
import java.io.File;
import java.text.ParseException;

import org.gu.vesta.dao.impl.DAO;
import org.gu.vesta.dao.impl.hal.Import;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

    public class Main extends Application {

    
    
             private static final Logger log = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws Exception {
        launch(args);
     }

    public void start(Stage stage) throws Exception {

        Stage stage1 = new Stage();
        openDATAView(stage1);
    
    }

     
    public static void lancerImport() throws ParseException {
       
       // DAO.clear();
        
   //     Import.lancerImport();
     
    }
    
    private Parent openDATAView(Stage stage) throws Exception {

        log.debug("Loading FXML for main view from: {}", "/fxml/IHM_DATA.fxml");
        FXMLLoader loader = new FXMLLoader();
        Parent page = (Parent) loader.load(getClass().getResourceAsStream("/fxml/IHM_DATA.fxml"));

        Scene scene = new Scene(page);
        scene.getStylesheets().add("/styles/style.css");
        stage.setScene(scene);

        stage.sizeToScene();
        stage.show();
        return page;
    }
}
