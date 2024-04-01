package com.example.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment2.api.WeatherApiViewModel
import com.example.assignment2.ui.theme.Assignment2Theme
import UserLocation
import android.annotation.SuppressLint
//import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.assignment2.database.Weather
import com.example.assignment2.database.WeatherDBrepository
import com.example.assignment2.database.WeatherDatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : ComponentActivity() {
    val user_location:UserLocation=UserLocation(this)

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherDBviewModel=ViewModelProvider(this).get(WeatherDatabaseViewModel::class.java)
        appContext = applicationContext
        setContent {
            Assignment2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                   MainPage(user_location,weatherDBviewModel)
//                    DateTimePickerComponent()

                }
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Calendar.getInstance().time)
}

@Composable
fun NetworkListener(context: Context, updateNetConnected: (Boolean) -> Unit) {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCallback = remember {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                updateNetConnected(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                updateNetConnected(false)
            }
        }
    }

    DisposableEffect(key1 = context) {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}

@Composable
fun MainPage(user_location:UserLocation,weatherDBviewmodel:WeatherDatabaseViewModel){

    var netConnected by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    latitude=user_location.getUserLocation(context = MainActivity.appContext).latitude
    longitude=user_location.getUserLocation(context = MainActivity.appContext).longitude

    val viewModel:WeatherApiViewModel= viewModel()
    LaunchedEffect(latitude,longitude){

        viewModel.updateLocation(latitude,longitude)
    }

    val allData by weatherDBviewmodel.allData.observeAsState(initial = emptyList())


    NetworkListener(context = MainActivity.appContext) { isConnected ->
        netConnected = isConnected
    }
    println("latitude:"+latitude)
    println("longitude:"+longitude)

        if (netConnected) {

            Log.d("NETWORK", "Network Available")

            val weatherData=viewModel.weatherState.value
            val forecastData=viewModel.forecastState.value
            Toast.makeText(MainActivity.appContext, "Network Available", Toast.LENGTH_SHORT).show()

            weatherDBviewmodel.clearAllData()

            for (index in 0..weatherData.daily.time.size - 1) {
                if (weatherData.daily.time.size == 0) {
                    continue
                }
                val date = weatherData.daily.time[index]
                val minTemp = weatherData.daily.temperature2mMin[index]
                val maxTemp = weatherData.daily.temperature2mMax[index]
                val weatherDBviewModel = weatherDBviewmodel

                weatherDBviewModel.upsert(Weather(index, date, minTemp, maxTemp))
            }
            for (index in 0..forecastData.daily.time.size - 1) {
                if (forecastData.daily.time.size == 0) {
                    continue
                }
                val date = forecastData.daily.time[index]
                val minTemp = forecastData.daily.temperature2mMin[index]
                val maxTemp = forecastData.daily.temperature2mMax[index]
                val weatherDBviewModel = weatherDBviewmodel


                weatherDBviewModel.upsert(
                    Weather(
                        index + weatherData.daily.time.size,
                        date,
                        minTemp,
                        maxTemp
                    )
                )

            }


        } else {

            Log.d("NETWORK", "Network Disconnected")
            Toast.makeText(MainActivity.appContext,"Network Disconnected",Toast.LENGTH_SHORT).show()
        }



    HomeScreen(user_location=user_location,latitude,longitude,netConnected =netConnected , weatherDBviewmodel = weatherDBviewmodel , apiViewModel =viewModel )







}

fun getMinPrediction(allData: List<Weather>, currentDate: String):Double {
    var minAvg:Double?=0.0


    val currentDateParts = currentDate.split("-")
    val currentMonth = currentDateParts[1].toInt()
    val currentDay = currentDateParts[2].toInt()

    for (weather in allData) {
        val weatherDateParts = weather.date.split("-")
        val weatherMonth = weatherDateParts[1].toInt()
        val weatherDay = weatherDateParts[2].toInt()

        if (minAvg != null) {
            if (currentMonth == weatherMonth && currentDay == weatherDay) {
                minAvg += weather.minTemp!!

            }
        }

    }
    if (minAvg != null) {
        return "%.1f".format(minAvg/10.0).toDouble()
    }
    return 0.0
}


fun getMaxPrediction(allData: List<Weather>, currentDate: String):Double {
    var maxAvg:Double?=0.0


    val currentDateParts = currentDate.split("-")
    val currentMonth = currentDateParts[1].toInt()
    val currentDay = currentDateParts[2].toInt()

    for (weather in allData) {
        val weatherDateParts = weather.date.split("-")
        val weatherMonth = weatherDateParts[1].toInt()
        val weatherDay = weatherDateParts[2].toInt()

        if (maxAvg != null) {
            if (currentMonth == weatherMonth && currentDay == weatherDay) {
                maxAvg += weather.maxTemp!!

            }
        }

    }
    if (maxAvg != null) {
        return "%.1f".format(maxAvg/10.0).toDouble()
    }
    return 0.0
}

@Composable
fun HomeScreen(user_location:UserLocation,latitude:Double,longitude:Double,netConnected:Boolean,weatherDBviewmodel:WeatherDatabaseViewModel,apiViewModel: WeatherApiViewModel?){
    var selectedDate by remember{mutableStateOf(getCurrentDate())}
    val allData by weatherDBviewmodel.allData.observeAsState(initial = emptyList())
    var curMin by remember {
        mutableStateOf(0.0)
    }
    var curMax by remember {
        mutableStateOf(0.0)
    }

    val currentDateParts = getCurrentDate().split("-")
    val currentYear=currentDateParts[0].toInt()
    val currentMonth = currentDateParts[1].toInt()
    val currentDay = currentDateParts[2].toInt()

    val weatherDateParts = selectedDate.split("-")
    val weatherYear=weatherDateParts[0].toInt()
    val weatherMonth = weatherDateParts[1].toInt()
    val weatherDay = weatherDateParts[2].toInt()
    for(index in 0..allData.size-1){
        val date=allData[index].date

        if(selectedDate==date){
            Log.d("CHECK_DATE","current date: $selectedDate  --  date:$date ")
            if(allData[index].minTemp==null){
                curMin=0.0
            }
            else{
                curMin= allData[index].minTemp!!

            }
            if(allData[index].maxTemp==null){
                curMax=0.0
            }
            else{
                curMax=allData[index].maxTemp!!

            }
        }

    }
    // Parsing the date string to a LocalDate object
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateToCheck = LocalDate.parse(selectedDate, formatter)
    val sevenDaysFromNow = LocalDate.now().plusDays(7)
    if(dateToCheck.isAfter(sevenDaysFromNow)){
        curMin= getMinPrediction(allData,selectedDate)
        curMax= getMaxPrediction(allData,selectedDate)
        Log.d("CHECK_DATE","curMin:$curMin --- curMax:$curMax")

    }

    Column(
        modifier= Modifier
            .fillMaxSize()
            .background(color = Color(0xFF21BCFF))
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (apiViewModel != null) {

            Row(
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    text="Latitude: ${apiViewModel.weatherState.value.latitude}",
//                    modifier = Modifier.padding(10.dp),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text="Longitude: ${apiViewModel.weatherState.value.longitude}",
//                    modifier = Modifier.padding(10.dp),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )

            }
            Text(
                text="Timezone: ${apiViewModel.weatherState.value.timezone}",
//                modifier = Modifier.padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Thin
            )
        }
            Row(
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(bottom = 25.dp),
                horizontalArrangement = Arrangement.SpaceAround,

            ){
                Temp(curMax,"Max Temperature")
                if (weatherDBviewmodel != null) {
                    Temp(curMin,"Min Temperature")
                }
            }
            Image(
                painter = painterResource(id = R.drawable.weather2),
                modifier = Modifier
                    .size(250.dp)
                    .padding(10.dp),
                contentDescription = "weather_image"
            )

        Text(
            text = "Date: $selectedDate",
            modifier = Modifier.padding(10.dp),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text="Location: ${user_location.getReadableLocation(latitude,longitude,MainActivity.appContext)}",
            modifier = Modifier.padding(10.dp),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Thin
        )
            DateTimePickerComponent({ newDate ->
                selectedDate=newDate
            })




    }

}

@Composable
fun Temp(temperature:Double?,type:String){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(type,
            modifier = Modifier,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text="$temperature \u2103",
            modifier = Modifier,
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.SemiBold
        )

    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerComponent(selectingDate:(String)->Unit) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

           Button(
            onClick = {
                showDatePicker = true
            },
               colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color.Blue) ,
               shape= RoundedCornerShape(8.dp),

            modifier = Modifier.width(200.dp),
        ) {
            Text(text = "Change Date" ,
                modifier = Modifier,
//                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium)
        }

    }

    //TODO show date picker when state is true
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                Button(
                onClick = {
                    val selectedDate = Calendar.getInstance().apply {
                        datePickerState.selectedDateMillis?.let { timeInMillis = it }
                    }
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectingDate(dateFormatter.format(selectedDate.time))

                        Toast.makeText(
                            MainActivity.appContext,
                            "Selected date ${dateFormatter.format(selectedDate.time)}",
                            Toast.LENGTH_SHORT
                        ).show()
                        showDatePicker = false
                }
            ) { Text("OK") } },
            dismissButton = {
                Button(
                onClick = {
                    showDatePicker = false
                }
            ) { Text("Cancel") } }

        )
        {
            DatePicker(state = datePickerState)
        }
    }


    //TODO show time picker when state is true

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
//    DateTimePickerComponent()
//    HomeScreen(netConnected =true , weatherDBviewmodel =null , apiViewModel =null )
}