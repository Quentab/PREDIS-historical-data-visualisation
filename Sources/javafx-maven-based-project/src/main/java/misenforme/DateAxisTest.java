package misenforme;




import ihm.DateAxis;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//import org.testng.Assert;

import java.util.Date;
import java.util.GregorianCalendar;


public class DateAxisTest extends Application {

    private void init(Stage primaryStage) {
        VBox root = new VBox();
        final Chart chart = createChart();

        VBox.setVgrow(chart, Priority.ALWAYS);
        primaryStage.setScene(new Scene(root));
        //xAxis.setAutoRanging(true);
       // xAxis.setAnimated(true);
        xAxis.setLowerBound(new GregorianCalendar(2008, 0, 1).getTime());
        xAxis.setUpperBound(new GregorianCalendar(2018, 0, 1).getTime());
        Button btnSetMaxDate = new Button("Click me");
        btnSetMaxDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                series.getData().add(new XYChart.Data<Date, Number>(new GregorianCalendar(2012, 1, 1).getTime(), 60d));
                //series2.getData().add(new XYChart.Data<Number, Number>(15, 160d));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                chart.requestLayout();
                            }
                        });
                    }
                }).start();
            }
        });

        root.getChildren().addAll(chart, btnSetMaxDate);
    }

    final DateAxis xAxis = new DateAxis();
    XYChart.Series<Date, Number> series;

    protected LineChart<Date, Number> createChart() {

        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAnimated(true);

        final LineChart<Date, Number> lc = new LineChart<Date, Number>(xAxis, yAxis);
        //lc.setAnimated(false);
        // setup chart
        lc.setTitle("Basic LineChart");
        xAxis.setLabel("X Axis");
        //xAxis.setTickLabelRotation(-90);
        yAxis.setLabel("Y Axis");
        // add starting data
        series = new XYChart.Series<Date, Number>();
        series.setName("Data Series 1");
        series.getData().add(new XYChart.Data<Date, Number>(new GregorianCalendar(1970, 3, 1, 12, 2, 1).getTime(), 50d));
        series.getData().add(new XYChart.Data<Date, Number>(new GregorianCalendar(1972, 3, 1, 23, 3, 1).getTime(), 80d));

        return lc;
    }
    
    /*@Override
    public void start(Stage stage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        /*series.getData().add(new XYChart.Data<Date, Number>(new GregorianCalendar(2011, 3, 3).getTime(), 90d));
        series.getData().add(new XYChart.Data<Date, Number>(new GregorianCalendar(2011, 3, 4).getTime(), 30d));
        series.getData().add(new XYChart.Data<Date, Number>(new GregorianCalendar(2011, 3, 6).getTime(), 122d));
       
        lc.getData().add(series);
        return lc;
    }*/

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}