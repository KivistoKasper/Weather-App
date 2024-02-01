package com.mycompany.forecasthub;

/**
 *
 * @author Ayman Khan, Phuong Nguyen
 */
import com.mycompany.forecasthub.Controllers.TimeDataController;
import com.mycompany.forecasthub.Models.Location;
import com.mycompany.forecasthub.Models.WeatherData;
import com.mycompany.forecasthub.Services.LocationDataService;
import com.mycompany.forecasthub.Services.WeatherDataService;
import com.mycompany.forecasthub.Utilities.SharedUtils;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.controlsfx.control.textfield.AutoCompletionBinding;

public class MainViewController implements Initializable {

    @FXML
    private Label currentCityLabel;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private Label currentDateLabel;

    @FXML
    private Label currentTemperatureLabel;

    @FXML
    private Label currentHumidityLabel;

    @FXML
    private Label currentWindSpeedLabel;

    @FXML
    private Label currentWeatherConditionLabel;

    @FXML
    private Label currentSurfacePressureLabel;

    @FXML
    private Label currentApparentTemperatureLabel;

    @FXML
    private Label todaySunriseLabel;

    @FXML
    private Label todaySunsetLabel;

    @FXML
    private Label todayUVLabel;

    @FXML
    private ImageView currentWeatherImage;

    @FXML
    private GridPane dayForecastGridPane;

    @FXML
    private HBox hourlyWeatherHBox;

    private WeatherDataService weatherDataService;

    private LocationDataService locationDataService;

    private String selectedCity;

    private int numberOfDays = 5;

    //       comment for Debug
    private AutoCompletionBinding<String> autoCompletionBinding;
    private String[] values = {"Helsinki", "Turku", "Tampere", "Espoo"};
    private Set<String> suggestions = new HashSet<>(Arrays.asList(values));

    public MainViewController() {
        weatherDataService = WeatherDataService.getInstance();
        locationDataService = LocationDataService.getInstance();
    }

    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1),
                    e -> {
                        LocalTime time = LocalTime.parse(currentTimeLabel.getText());
                        time = time.plusSeconds(1);
                        currentTimeLabel.setText(time.toString() + (time.getSecond() == 0 ? ":00" : ""));
                    }));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startClock();
        if (locationDataService.getObservableActiveLocation().getValue() != null) {
            updateWeatherData(locationDataService.getObservableActiveLocation().getValue());
        }
        initializeListeners();
    }

    private void initializeListeners() {
        locationDataService.getObservableActiveLocation().addListener((obs, oldLocation, newLocation) -> {
            // Update weather data or perform any action based on the new location
            updateWeatherData(newLocation);
        });

        weatherDataService.getObservableIsCelsius().addListener((obs, oldLocation, newLocation) -> {
            // Update weather data or perform any action based on the new location
            updateWeatherData(locationDataService.getObservableActiveLocation().getValue());
        });
    }

    private void startClock() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void onButtonClick() throws IOException {
        // Add your custom logic or navigation code here
        App.setCenter("graphView");
    }

    private void updateWeatherData(Location loc) {
        // Call the getWeatherData method to fetch weather data

        WeatherData currentWeatherData = weatherDataService.getCurrentWeather(loc.getCity(), loc.getLatitude(), loc.getLongitude());

        currentTemperatureLabel.setText(SharedUtils.getParsedTemperature(currentWeatherData.getTemperature(), weatherDataService.getIsCelsius()));

        currentHumidityLabel.setText(currentWeatherData.getHumidity() + "%");
        currentWindSpeedLabel.setText(currentWeatherData.getWindSpeed() + "km/h");
        TimeDataController timeData = new TimeDataController(loc.getTimezone());

        currentTimeLabel.setText(timeData.getTime());
        currentCityLabel.setText(loc.getCity());
        currentApparentTemperatureLabel.setText(SharedUtils.getParsedTemperature(currentWeatherData.getApparentTemperature(), weatherDataService.getIsCelsius()));
        currentSurfacePressureLabel.setText(currentWeatherData.getSurfacePressure() + "hPa");

        // Weather condition
        String weatherCondition = SharedUtils.getWeatherConditionFromCode(currentWeatherData.getWeatherCode());
        currentWeatherConditionLabel.setText(weatherCondition);

        String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        var currentDate = days[timeData.getWeekday()] + ", " + timeData.getDate();
        currentDateLabel.setText(currentDate);

        String currentWeatherIconLocation = SharedUtils.getWeatherConditionIcon(weatherCondition);

        Image image = new Image(getClass().getResourceAsStream(currentWeatherIconLocation));
        currentWeatherImage.setFitWidth(220); //500
        currentWeatherImage.setImage(image);

        List<WeatherData> weeklyWeatherDataList = weatherDataService.getWeeklyForecast(loc.getCity(), loc.getLatitude(), loc.getLongitude());
        List<WeatherData> hourlyWeatherDataList = weatherDataService.getHourlyForecast(loc.getCity(), loc.getLatitude(), loc.getLongitude());

        // Fetch sunrise/sunset and UV index
        updateTodayWeatherData(weeklyWeatherDataList);

        // Fetch 5-day weather data
        updateWeeklyWeatherData(weeklyWeatherDataList);

        // Fetch hourly weather data
        updateHourlyWeatherData(hourlyWeatherDataList);
    }

    private void updateTodayWeatherData(List<WeatherData> weeklyWeatherDataList) {
        if (!weeklyWeatherDataList.isEmpty()) {
            WeatherData todayWeatherData = weeklyWeatherDataList.get(0);

            // Format sunrise and sunset time
            String sunriseString = todayWeatherData.getSunrise();
            String sunsetString = todayWeatherData.getSunset();

            LocalTime sunriseTime = LocalTime.parse(sunriseString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String formattedSunrise = sunriseTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            LocalTime sunsetTime = LocalTime.parse(sunsetString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String formattedSunset = sunsetTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            todaySunriseLabel.setText(formattedSunrise);
            todaySunsetLabel.setText(formattedSunset);
            todayUVLabel.setText(Math.round(todayWeatherData.getUVIndex()) + "");

        } else {
            System.out.println("The weeklyWeatherDataList is empty.");
        }
    }

    private void updateWeeklyWeatherData(List<WeatherData> weeklyWeatherDataList) {
        if (!weeklyWeatherDataList.isEmpty()) {

            // Set the layout of the 5-day GridPane
            dayForecastGridPane.setPadding(new Insets(20));
            dayForecastGridPane.setHgap(20);
            dayForecastGridPane.setVgap(10);
            dayForecastGridPane.getChildren().clear();
            // Set GridPane column width
            ColumnConstraints column1 = new ColumnConstraints();
            ColumnConstraints column2 = new ColumnConstraints();
            ColumnConstraints column3 = new ColumnConstraints();

            column1.setPrefWidth(70);
            column2.setPrefWidth(100);
            column3.setPrefWidth(100);

            for (int i = 0; i < numberOfDays; i++) {
                WeatherData weatherData = weeklyWeatherDataList.get(i);

                // Create an ImageView for the weather condition
                String weatherCondition = SharedUtils.getWeatherConditionFromCode(weatherData.getWeatherCode());
                String currentWeatherIconLocation = SharedUtils.getWeatherConditionIcon(weatherCondition);;
                Image image = new Image(getClass().getResourceAsStream(currentWeatherIconLocation));
                ImageView weatherImageView = new ImageView(image);
                weatherImageView.setFitWidth(60);
                weatherImageView.setFitHeight(60);

                // Create temperature and date labels
                Label temperatureLabel = new Label(SharedUtils.getParsedTemperature(weatherData.getTemperature(), weatherDataService.getIsCelsius()));
                temperatureLabel.setTextFill(Color.WHITE);
                temperatureLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

                String date = LocalDate.parse(weatherData.getDate()).format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH));
                Label dateLabel = new Label(date);
                dateLabel.setTextFill(Color.WHITE);
                dateLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

                // Add image and labels to the GridPane
                dayForecastGridPane.add(weatherImageView, 0, i);
                dayForecastGridPane.add(temperatureLabel, 1, i);
                dayForecastGridPane.add(dateLabel, 2, i);
            }
        } else {
            System.out.println("The weeklyWeatherDataList is empty.");
        }
    }

    private void updateHourlyWeatherData(List<WeatherData> hourlyWeatherDataList) {
        // Get the current time
        LocalTime currentTime = LocalTime.now();
        hourlyWeatherHBox.getChildren().clear();

        for (int i = currentTime.getHour(); i <= currentTime.getHour() + 6; i++) {
            WeatherData weatherData = hourlyWeatherDataList.get(i);
            Pane horizontalPane = createHourlyPane(weatherData);

            // Set padding for each pane individually
            horizontalPane.setPadding(new Insets(10, 20, 10, 20));

            hourlyWeatherHBox.getChildren().add(horizontalPane);
        }

        for (Node pane : hourlyWeatherHBox.getChildren()) {
            // Set margin for each pane individually
            HBox.setMargin(pane, new Insets(10));
        }
    }

    private Pane createHourlyPane(WeatherData weatherData) {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #373636;-fx-background-radius: 40;");
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        LocalTime time = LocalTime.parse(weatherData.getTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"));
        Label timeLabel = new Label(formattedTime);
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        String weatherCondition = SharedUtils.getWeatherConditionFromCode(weatherData.getWeatherCode());
        String currentWeatherIconLocation = SharedUtils.getWeatherConditionIcon(weatherCondition);;
        Image image = new Image(getClass().getResourceAsStream(currentWeatherIconLocation));
        ImageView weatherImageView = new ImageView(image);
        weatherImageView.setFitWidth(50);
        weatherImageView.setFitHeight(50);

        Label temperatureLabel = new Label(SharedUtils.getParsedTemperature(weatherData.getTemperature(), weatherDataService.getIsCelsius()));
        temperatureLabel.setTextFill(Color.WHITE);
        temperatureLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        Image windDirectionImage = new Image(getClass().getResourceAsStream("/assets/windDirection.png"));
        ImageView windDirectionImageView = new ImageView(windDirectionImage);
        windDirectionImageView.setFitWidth(50);
        windDirectionImageView.setFitHeight(50);
        windDirectionImageView.setRotate(weatherData.getWindDirection());

        Label windSpeedLabel = new Label(weatherData.getWindSpeed() + "km/h");
        windSpeedLabel.setTextFill(Color.WHITE);
        windSpeedLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        vbox.getChildren().add(timeLabel);
        vbox.getChildren().add(weatherImageView);
        vbox.getChildren().add(temperatureLabel);
        vbox.getChildren().add(windDirectionImageView);
        vbox.getChildren().add(windSpeedLabel);

        for (Node node : vbox.getChildren()) {
            VBox.setMargin(node, new Insets(0, 0, 10, 0));
        }

        pane.getChildren().add(vbox);
        return pane;
    }
}
