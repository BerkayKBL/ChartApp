package com.berkaykbl.chartapp

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.berkaykbl.chartapp.ui.theme.ChartAppTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberCandlestickCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.candlestickSeries
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.text.SimpleDateFormat
import java.util.UUID
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChartAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        HomeScreen()
                    }
                }
            }
        }
    }
}

val defaultChartVariableCount = 10
val endDate = 1751210803000
val everyDay = 86400000
val maxDouble = 108.8

val dateFormat = SimpleDateFormat("dd MMM yyyy")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    val activity = (context as Activity)
    var chartVariables by remember {
        mutableStateOf(
            emptyList<VariableEntity>()
        )
    }

    chartVariables = emptyList()
    var i = 0
    val newList = ArrayList<VariableEntity>()
    while (i < defaultChartVariableCount) {
        newList.add(
            VariableEntity(
                i.toString(),
                endDate - (everyDay * i),
                Random.nextDouble(0.0, maxDouble)
            )
        )
        i++
    }
    chartVariables = newList.toList()

    var pagerState = rememberPagerState(pageCount = { 4 })

    HorizontalPager(
        pagerState,
        modifier = Modifier.fillMaxSize()
    ) {
        var newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (it == 3) {
            VariablesPage(chartVariables) { action, entity ->
                if (action == "add") {
                    val newArray = ArrayList<VariableEntity>()
                    newArray.add(entity)
                    newArray.addAll(chartVariables)
                    chartVariables = newArray.toList()
                } else if (action == "delete") {
                    val newArray = ArrayList<VariableEntity>()
                    newArray.addAll(chartVariables)
                    chartVariables = newArray.filter { it.id != entity.id }.toList()

                }
            }
        } else if (it == 2) {

            newOrientation= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            LinearChart(chartVariables)
        } else if (it == 1) {

            newOrientation= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            ColumnChart(chartVariables)
        } else if (it == 0) {

            newOrientation= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            LinearChartMountain(chartVariables)
        }
        if (pagerState.currentPageOffsetFraction == 0f) {
            activity.requestedOrientation = newOrientation
        }
    }
}

@Composable
fun VariablesPage(
    chartVariables: List<VariableEntity>,
    action: (String, VariableEntity) -> Unit
) {
    var inputDate by remember { mutableStateOf("12102000") }
    var inputValue by remember { mutableStateOf("12.0") }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Icon(Icons.Default.Add, null, modifier = Modifier.clickable {
                action(
                    "add", VariableEntity(
                        UUID.randomUUID().toString(),
                        convertToTimestamp(inputDate),
                        inputValue.toDouble()
                    )
                )
            })
            TextFieldComponent(Modifier.weight(1f), "Tarih", inputDate, inputType = "date") {
                inputDate = it
            }

            TextFieldComponent(Modifier.weight(1f), "Değer", inputValue, inputType = "double") {
                inputValue = it
            }
        }
        LazyColumn {
            items(chartVariables.size, key = {
                val variable = chartVariables[it]
                variable.id
            }) { i ->
                val variable = chartVariables[i]
                Row {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.clickable {
                        action("delete", variable)
                    })
                    TextFieldComponent(
                        Modifier.weight(1f),
                        "Tarih",
                        dateFormat.format(variable.date),
                        readOnly = true
                    ) {}

                    TextFieldComponent(
                        Modifier.weight(1f),
                        "Değer",
                        variable.variable.toString(),
                        readOnly = true
                    ) {
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnChart(
    chartVariables: List<VariableEntity>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(chartVariables.map { it.variable }.toList()) }
        }
    }

    Scaffold { inner ->
        Column(modifier = Modifier.padding(inner)) {
            CartesianChartHost(
                rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = CartesianValueFormatter { context, value, verticalAxisPosition ->
                            dateFormat.format(chartVariables[value.toInt()].date)
                        }
                    ),
                ),
                modelProducer,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}

@Composable
fun LinearChart(
    chartVariables: List<VariableEntity>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { series(chartVariables.map {
                it.variable }.toList()) }
        }
    }

    Scaffold { inner ->
        Column(modifier = Modifier.padding(inner)) {
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = CartesianValueFormatter { context, value, verticalAxisPosition ->
                            dateFormat.format(chartVariables[value.toInt()].date)
                        }
                    ),
                ),
                modelProducer,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}
@Composable
fun LinearChartMountain(
    chartVariables: List<VariableEntity>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { series(chartVariables.map {
                it.variable }.toList()) }
        }

    }

    Scaffold { inner ->
        Column(modifier = Modifier.padding(inner)) {
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = CartesianValueFormatter { context, value, verticalAxisPosition ->
                            dateFormat.format(chartVariables[value.toInt()].date)
                        }
                    ),
                ),
                modelProducer,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}