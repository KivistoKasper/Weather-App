# Weather ForecastHub

The Weather ForecastHub is a Java-based desktop application that provides users with weather forecasts based on location and time intervals. This application offers a user-friendly interface, detailed weather data display, daily summaries, and extended forecasts for weekly and 15 days time periods.

## Features

- User-Friendly Interface: Enjoy an intuitive and aesthetically pleasing user interface designed for ease of use.

- Weather Data Display: View detailed weather data, including temperature, humidity, precipitation, wind speed and direction, UV index, and more in the form of line charts and tables.

- Daily Summaries: Access concise daily summaries of weather conditions, including highs and lows, precipitation probabilities, and notable weather events.

- Weekly and Monthly Forecasts: Plan activities and events in advance with the help of weekly and 15 days weather overviews.

## Project Structure
The project follows a standard Maven directory structure compromising MVC architecture:

- `src/main/java`: Contains the Java source code.
  - `com.forecasthub.controllers`: Controllers responsible for handling user interactions.
  - `com.forecasthub.models`: Models representing weather, location, and time data.
  - `com.forecasthub.services`: Services responsible for fetching and processing data.
  - `com.forecasthub.utilities`: Utility classes for common functionalities.

- `src/main/resources/com/forecasthub/fxml`: Contains FXML files defining the application's views.
- `src/main/resources/com/forecasthub/other_resources`: Additional project resources.

- `target`: Default output directory for compiled code and generated artifacts.
- `nbproject`: NetBeans project configuration files.
- `pom.xml`: Maven build configuration file.
- `README.md`: Project documentation file (you're reading it now).

## Dependencies

The project uses Maven for build and dependency management. Ensure that you have JavaFX and other required dependencies correctly configured in your development environment. To build and run this project, ensure you have the following installed:

- Java Development Kit (JDK) 8 or higher
- Apache Maven

## Getting Started

1. **Clone the Repository:**

   ```bash
   git clone https://course-gitlab.tuni.fi/comp.se.110-software-design_2023-2024/syntaxsamurais.git
   cd ForecastHub
   
2. **Build the Project:**

   ```bash
   mvn clean install
   
3. **Run the Application:**

   ```bash
   java -jar target/forecasthub.jar
   
Alternatively, you can run the application from your IDE by opening the project in NetBeans.


## User Interface

The app shall have 3 user interfaces

1. The Dashboard
1. The Chart Screen
1. The Tabular Chart Screen

![](https://github-production-user-asset-6210df.s3.amazonaws.com/24876640/271853056-f40bb622-3e0e-4ca6-a2c3-95e0905c20eb.jpeg)

Fig 5.1: Dashboard

![](https://github.com/rafinutshaw/quiz/assets/24876640/2291864c-cee9-4eff-8acb-3827988a668b)

Fig 5.2: The Tabular Chart Screen

![](https://github.com/rafinutshaw/quiz/assets/24876640/85cb7962-ea6b-4a4d-b5ec-fb41950a5e30)

Fig 5.3: The Chart Screen


## Authors

- [Ayman Asad Khan]
- [Rafin Akther Utshaw]
- [Abdullah Mohammad Ashraf]
- [Kasper Kivistö]
- [Phuong Nguyen]

