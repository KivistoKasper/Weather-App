package com.mycompany.forecasthub;

/**
 *
 * @author Ayman Khan, Phuong Nguyen
 */
import com.mycompany.forecasthub.Models.Location;
import com.mycompany.forecasthub.Models.WeatherData;
import com.mycompany.forecasthub.Services.LocationDataService;
import com.mycompany.forecasthub.Services.LocationDataService.CityWeatherInfo;
import com.mycompany.forecasthub.Services.WeatherDataService;
import com.mycompany.forecasthub.Utilities.SharedUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class SidePanelController implements Initializable {

    @FXML
    private TextField searchLocation;

    @FXML
    private VBox savedCities;

    @FXML
    private ChoiceBox<String> unitSelector;

    private WeatherDataService weatherDataService;

    private LocationDataService locationDataService;

    private String selectedCity;
    private ArrayList<Location> locations = new ArrayList<>();
    ObservableList<String> temperatureUnits = FXCollections.observableArrayList("°C", "°F");

    @FXML
    private ComboBox<CityWeatherInfo> comboBox;

    private static final String[] data = {
        "Apple", "Banana", "Cherry", "Date", "Grape",
        "Lemon", "Lime", "Orange", "Peach", "Pear",
        "Pineapple", "Strawberry"
    };

    public SidePanelController() {
        weatherDataService = WeatherDataService.getInstance();
        locationDataService = LocationDataService.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        updateCurrentLocation();
        initUIValues();
        initLocationAutoComplete();

    }

    private void initUIValues() {
        unitSelector.setItems(temperatureUnits);
        unitSelector.setValue(temperatureUnits.get(0));

        unitSelector.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Handle the change here
                weatherDataService.setIsCelsius(newValue.equals(temperatureUnits.get(0)));
            }
        });
    }

    private void setSelected(String cityName) {
        selectedCity = cityName;
        for (javafx.scene.Node node : savedCities.getChildren()) {
            if (node instanceof HBox) {
                HBox existingHBox = (HBox) node;

                // Assuming "X" button is at index 2
                Label label = (Label) existingHBox.getChildren().get(1);
                String existingStyle = existingHBox.getStyle();
                if (label.getText().equals(cityName)) {

                    existingStyle = existingStyle.replace("-fx-background-color: transparent;", "-fx-background-color: gray;");
                } else {
                    existingStyle = existingStyle.replace("-fx-background-color: gray;", "-fx-background-color: transparent;");
                }
                existingHBox.setStyle(existingStyle);

            }
        }
    }

    private void addCity(String cityName, String weatherImage) {
        Boolean cityExists = false;
        for (javafx.scene.Node node : savedCities.getChildren()) {
            if (node instanceof HBox) {
                HBox existingHBox = (HBox) node;

                // Assuming "X" button is at index 2
                Label label = (Label) existingHBox.getChildren().get(1);
                if (label.getText().equals(cityName)) {
                    cityExists = true;
                    break;
                }

            }
        }
        if (cityExists) {
            return;
        }
        // Create a new HBox
        HBox newHBox = new HBox();
        newHBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        newHBox.setStyle("-fx-padding: 0 8px;-fx-background-color: transparent;");

        newHBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setSelected(cityName);
                Location slocation = new Location();
                for (int i = 0; i < locations.size(); i++) {

                    if (locations.get(i).getCity().equals(cityName)) {
                        slocation = locations.get(i);
                        break;
                    }
                }
                locationDataService.setActiveLocation(slocation);
            }
        });

        // Create an ImageView
        ImageView newImageView = new ImageView(new Image(weatherImage));
        newImageView.setFitHeight(45.0);
        newImageView.setFitWidth(66.0);
        newImageView.setPickOnBounds(true);
        newImageView.setPreserveRatio(true);

        // Create a Label
        Label newLabel = new Label(cityName);
        newLabel.setPrefHeight(27.0);
        newLabel.setPrefWidth(213.0);
        newLabel.setTextFill(javafx.scene.paint.Color.valueOf("#fbfbfb"));
        newLabel.setFont(new javafx.scene.text.Font(18.0));

        // Create a Button
        Button newButton = new Button("X");
        newButton.setMnemonicParsing(false);
        newButton.setStyle("-fx-background-color: #444444;");
        newButton.setTextFill(javafx.scene.paint.Color.WHITE);
        newButton.setFont(new javafx.scene.text.Font(18.0));
        newButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Get the parent HBox and remove it from the VBox
                HBox parentHBox = (HBox) newButton.getParent();
                String currentCity = ((Label) ((HBox) newButton.getParent()).getChildren().get(1)).getText();
                if (selectedCity.equals(currentCity)) {
                    setSelected(((Label) ((HBox) savedCities.getChildren().get(0)).getChildren().get(1)).getText());
                }
                int index = savedCities.getChildren().indexOf(parentHBox);
                locations.remove(index);

                savedCities.getChildren().remove(parentHBox);

                locationDataService.setActiveLocation(locations.get(0));

            }
        });
        // Add the children to the new HBox
        newHBox.getChildren().addAll(newImageView, newLabel, newButton);

        // Add the new HBox to the existing VBox
        savedCities.getChildren().add(newHBox);

    }

    private void initLocationAutoComplete() {
        searchLocation.setText("");

        searchLocation.textProperty().addListener((observable, oldValue, newValue) -> {
            List<CityWeatherInfo> cityWeatherInfoList = locationDataService.searchCityOptions(newValue);

            Service<Void> apiService = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() {
                            for (CityWeatherInfo city : cityWeatherInfoList) {
                                WeatherData wd = weatherDataService.getCurrentWeather(city.getCity(), city.getLatitude(), city.getLongitude());
                                city.weatherCode = wd.getWeatherCode();
                            }
                            return null;
                        }
                    };
                }
            };

            apiService.setOnSucceeded(event -> {
                // Update UI after API calls are completed
                comboBox.getItems().setAll(cityWeatherInfoList);
                comboBox.hide();
                comboBox.show();
            });
            apiService.start();

        });

        // Auto-complete when an item is selected from the dropdown
        comboBox.setOnAction(event -> {
//            searchLocation.setText(comboBox.getSelectionModel().getSelectedItem());
            CityWeatherInfo selectedCityWeatherInfo = comboBox.getValue();

            Location loc = new Location(selectedCityWeatherInfo.getCity(), selectedCityWeatherInfo.getLatitude(), selectedCityWeatherInfo.getLongitude(), selectedCityWeatherInfo.getTimeZone(), SharedUtils.getWeatherConditionIcon(SharedUtils.getWeatherConditionFromCode(selectedCityWeatherInfo.weatherCode)));
            locationDataService.setActiveLocation(loc);
            addCity(loc.getCity(), SharedUtils.getWeatherConditionIcon(SharedUtils.getWeatherConditionFromCode(selectedCityWeatherInfo.weatherCode)));
            setSelected(loc.getCity());
            locations.add(loc);

        });

        comboBox.setCellFactory(param -> new ListCell<CityWeatherInfo>() {
            @Override
            protected void updateItem(CityWeatherInfo item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCity());
                }
            }
        });

        comboBox.setConverter(new StringConverter<CityWeatherInfo>() {
            @Override
            public String toString(CityWeatherInfo object) {
                return object == null ? null : object.getCity();
            }

            @Override
            public CityWeatherInfo fromString(String string) {
                // You might implement this method if needed
                return null;
            }
        });
    }

    @FXML
    private void updateCurrentLocation() {
        // Fetch current time and date
        Location loc = locationDataService.getCurrentLocation();
        updateWeatherData(loc);
    }

    @FXML
    private void navigateHome(MouseEvent event) throws IOException {

        if (!App.getCenter().equals("mainView")) {
            App.setCenter("mainView");
        }
    }

    private void updateWeatherData(Location loc) {
        // Call the getWeatherData method to fetch weather data
        WeatherData currentWeatherData = weatherDataService.getCurrentWeather(loc.getCity(), loc.getLatitude(), loc.getLongitude());

        String weatherCondition = SharedUtils.getWeatherConditionFromCode(currentWeatherData.getWeatherCode());

        String currentWeatherIconLocation = SharedUtils.getWeatherConditionIcon(weatherCondition);
        addCity(loc.getCity(), currentWeatherIconLocation);
        setSelected(loc.getCity());
        loc.weatherConditionImage = currentWeatherIconLocation;
        locations.add(loc);
        ((Button) ((HBox) savedCities.getChildren().get(0)).getChildren().get(2)).setDisable(true);
    }
}
