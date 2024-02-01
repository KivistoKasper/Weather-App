module com.mycompany.forecasthub {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.mycompany.forecasthub to javafx.fxml;
    exports com.mycompany.forecasthub;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.json;

    requires org.controlsfx.controls;


}
