package com.mycompany.forecasthub;

import com.mycompany.forecasthub.Models.Location;
import com.mycompany.forecasthub.Models.WeatherData;
import com.mycompany.forecasthub.Services.LocationDataService;
import com.mycompany.forecasthub.Services.WeatherDataService;
import com.mycompany.forecasthub.Utilities.SharedUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GraphController implements Initializable {

    @FXML
    private VBox dataNode;

    @FXML
    private Button chartButton;

    @FXML
    private Button tableButton;

    List<WeatherData> weatherValues;

    private WeatherDataService weatherDataService;
    private LocationDataService locationDataService;
    private boolean isChartView = true;

    public GraphController() {
        weatherDataService = WeatherDataService.getInstance();
        locationDataService = LocationDataService.getInstance();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Location activeLocation = locationDataService.getObservableActiveLocation().getValue();
        weatherValues = weatherDataService.get16DaysForecast(activeLocation.getCity(), activeLocation.getLatitude(), activeLocation.getLongitude());
        makeGraph(weatherValues);
        weatherDataService.getObservableIsCelsius().addListener((obs, oldLocation, newLocation) -> {
            if (isChartView) {
                makeGraph(weatherValues);
            } else {
                updateUI(weatherValues);
            }
        });

        locationDataService.getObservableActiveLocation().addListener((obs, oldLocation, newLocation) -> {
            weatherValues = weatherDataService.get16DaysForecast(newLocation.getCity(), newLocation.getLatitude(), newLocation.getLongitude());

            // Update weather data or perform any action based on the new location
            if (isChartView) {
                makeGraph(weatherValues);
            } else {
                updateUI(weatherValues);
            }
        });
    }

    @FXML
    private void showChart() {
        makeGraph(weatherValues);
        toggleButtonStyles(true);
        isChartView = true;
    }

    @FXML
    private void showTable() {
        updateUI(weatherValues);
        toggleButtonStyles(false);
        isChartView = false;
    }

    private void toggleButtonStyles(Boolean isChart) {
        if (isChart) {
            chartButton.setStyle("-fx-background-color: #ffffff;-fx-cursor: hand; -fx-border-radius: 0px 8px 8px 0px;");
            chartButton.setTextFill(Color.BLACK);
            tableButton.setStyle("-fx-background-color: #444444;-fx-cursor: hand; -fx-border-radius: 0px 8px 8px 0px;");
            tableButton.setTextFill(Color.WHITE);
        } else {
            chartButton.setStyle("-fx-background-color: #444444;-fx-cursor: hand; -fx-border-radius: 0px 8px 8px 0px;");
            chartButton.setTextFill(Color.WHITE);
            tableButton.setStyle("-fx-background-color: #ffffff;-fx-cursor: hand; -fx-border-radius: 0px 8px 8px 0px;");
            tableButton.setTextFill(Color.BLACK);
        }
    }

    private void makeGraph(List<WeatherData> weatherValues) {
        dataNode.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        xAxis.setStyle("-fx-font-size: 20;-fx-text-fill: white;");
        yAxis.setLabel("Temperature (" + (weatherDataService.getIsCelsius() ? "°C" : "°F") + ")");
        yAxis.setStyle("-fx-font-size: 20;-fx-text-fill: white;");
        xAxis.setTickLabelFill(Color.WHITE);
        yAxis.setTickLabelFill(Color.WHITE);
        // Create the line chart
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Weather Forecast");

        // Create a data series
        XYChart.Series<String, Number> minSeries = new XYChart.Series<>();
        minSeries.setName("Min Temperature");

        // Create a data series
        XYChart.Series<String, Number> maxSeries = new XYChart.Series<>();
        maxSeries.setName("Max Temperature");

        for (int i = 8; i < 16; i++) {
            minSeries.getData().add(new XYChart.Data<>(weatherValues.get(i).getDate(), SharedUtils.getParsedTemperatureValue(weatherValues.get(i).getMinTemperature(), weatherDataService.getIsCelsius())));
            maxSeries.getData().add(new XYChart.Data<>(weatherValues.get(i).getDate(), SharedUtils.getParsedTemperatureValue(weatherValues.get(i).getTemperature(), weatherDataService.getIsCelsius())));
        }

        // Add the series to the chart
        lineChart.getData().add(maxSeries);
        lineChart.getData().add(minSeries);
        dataNode.getChildren().addAll(lineChart);

    }

    private void updateUI(List<WeatherData> weatherValues) {
        dataNode.getChildren().clear();
        HBox tableRowOne = new HBox();
        tableRowOne.setPrefHeight(244.0);
        tableRowOne.setPrefWidth(1158.0);
        tableRowOne.setSpacing(16.0);

        // Create HBox for tableRowTwo
        HBox tableRowTwo = new HBox();
        tableRowTwo.setPrefHeight(244.0);
        tableRowTwo.setPrefWidth(1158.0);
        tableRowTwo.setSpacing(16.0);

        // Add children to mainVBox
        List<VBox> values = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            values.add(createWeatherForecastVBox(weatherValues.get(i)));
        }
        tableRowOne.getChildren().addAll(values);
        values = new ArrayList<>();
        for (int i = 8; i < 16; i++) {
            values.add(createWeatherForecastVBox(weatherValues.get(i)));
        }
        tableRowTwo.getChildren().addAll(values);
        dataNode.getChildren().addAll(tableRowOne, tableRowTwo);
    }

    private VBox createWeatherForecastVBox(WeatherData weatherData) {
        // Create VBox
        VBox vbox = new VBox();

        // Create ImageView for the weather condition
        ImageView weatherImageView = new ImageView(new Image(getClass().getResourceAsStream(SharedUtils.getWeatherConditionIcon(SharedUtils.getWeatherConditionFromCode(weatherData.getWeatherCode())))));
        weatherImageView.setFitWidth(119.0);
        weatherImageView.setFitHeight(148.0);
        weatherImageView.setPreserveRatio(true);
        weatherImageView.setPickOnBounds(true);

        // Create HBox for Min temperature
        HBox minTemperatureHBox = createLabelHBox("Min:", SharedUtils.getParsedTemperature(weatherData.getMinTemperature(), weatherDataService.getIsCelsius()));

        Label dateLabel = new Label(weatherData.getDate());
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setFont(Font.font(16.0));

        // Create HBox for Max temperature
        HBox maxTemperatureHBox = createLabelHBox("Max:", SharedUtils.getParsedTemperature(weatherData.getTemperature(), weatherDataService.getIsCelsius()));

        // Add elements to VBox
        vbox.getChildren().addAll(weatherImageView, dateLabel, minTemperatureHBox, maxTemperatureHBox);

        // Set styles or other properties if needed
        vbox.setStyle("-fx-background-color: #6E6E6E; -fx-background-radius: 8px;");
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(8));
        return vbox;
    }

    private HBox createLabelHBox(String labelText, String valueText) {
        // Create HBox
        HBox hbox = new HBox();

        // Create Label for the text
        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("System", FontWeight.BOLD, 16.0));
        // Create Label for the value
        Label valueLabel = new Label(valueText);
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setFont(Font.font(16.0));

        // Set margin for the label
        HBox.setMargin(label, new Insets(0, 8.0, 0, 0));

        // Add labels to HBox
        hbox.getChildren().addAll(label, valueLabel);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

}
