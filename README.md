# Weather-App
This Weather App utilizes the Open Meto API to provide historical weather data as well as weather forecasts. It follows the MVVM presentation pattern, leveraging Jetpack Compose for UI and efficient data management.

# Features:
<ul>
  <li>
    <b>Historical Data Retrieval:</b> Utilizes the Archive endpoint of the Open Meto API to fetch historical weather data. Note: Current day's data is not included in this endpoint.
  </li>
  <li>
    <b>Forecast Data:</b> Utilizes the Forecast endpoint of the Open Meto API to provide weather forecasts for the next seven days.
  </li>
  <li>
    <b>User Input:</b> Users can input a specific date to retrieve historical weather data. For future dates, the app calculates the average of the last 10 available years' temperatures.
  </li>
  <li>
    <b>Offline Mode:</b> Stores retrieved data in a local database, enabling offline usage.
  </li>
</ul>

<h1>Functionality:</h1>
Upon launching the application, users are presented with a user-friendly interface showing weather information for the current date, including:
<ul>
  <li>Max and Min Temperature for the selected date.</li>
  <li>Time Zone, Latitude, and Longitude of the location.</li>
  <li>Location of the user.</li>
</ul>
Users can change the date using the provided input field to view weather data for different dates. Upon submission, the app fetches and displays relevant weather information for the selected date.

<h1>Tech Stack</h1>
<ul>
  <li>
    <b>Jetpack Compose:</b> For building a modern and reactive UI.
  </li>
  <li>
    <b>MVVM Architecture: </b>Separation of concerns for efficient development and testing.
  </li>
  <li>
    <b>Room Database:</b> For local data storage and offline functionality.
  </li>
  <li>
    <b>Open Meto API:</b> Used for accessing historical weather data and forecasts
  </li>
</ul>

<h1>References</h1>
<ul>
  <li>
    <a href="https://proandroiddev.com/getting-user-location-in-android-the-jetpack-compose-way-ebd35dabab46" target="_blank">https://proandroiddev.com/getting-user-location-in-android-the-jetpack-compose-way-ebd35dabab46</a>
  </li>
  <li>
    <a href="https://medium.com/@droidvikas/exploring-date-and-time-pickers-compose-bytes-120e75349797">https://medium.com/@droidvikas/exploring-date-and-time-pickers-compose-bytes-120e75349797</a>
  </li>
  <li>
    <a href="https://medium.com/scalereal/observing-live-connectivity-status-in-jetpack-compose-way-f849ce8431c7">https://medium.com/scalereal/observing-live-connectivity-status-in-jetpack-compose-way-f849ce8431c7</a>
  </li>
  <li>
    <a href="https://youtu.be/bOd3wO0uFr8?si=506dVxPcplAOt-e6">https://youtu.be/bOd3wO0uFr8?si=506dVxPcplAOt-e6</a>
  </li>
</ul>
