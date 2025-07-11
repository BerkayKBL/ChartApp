package com.berkaykbl.chartapp

import android.graphics.Path
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.MeasuringContext
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlin.random.Random

@Composable
fun ColumnPartsChart(
    chartVariables: List<VariableEntity>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val topValues = ArrayList<Double>()
    val middleValues = ArrayList<Double>()
    val bottomValues = ArrayList<Double>()
    chartVariables.forEach {
        val variable = it.variable
        val topAverage = Random.nextDouble(0.0, 1.0)
        val exceptTopAverage = 1 - topAverage
        val middleAverage = Random.nextDouble(0.0, exceptTopAverage)
        val bottomAverage = exceptTopAverage - middleAverage
        topValues.add(variable * topAverage)
        bottomValues.add(bottomAverage * variable)
        middleValues.add(middleAverage * variable)

    }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries {
                series(topValues)
                series(middleValues)
                series(bottomValues)
            }
        }
    }

    val mergeMode: (ExtraStore) -> ColumnCartesianLayer.MergeMode = {
        ColumnCartesianLayer.MergeMode.Stacked
    }

    val com = rememberLineComponent(
        fill = Fill(android.graphics.Color.RED),
        shape = CorneredShape(
            bottomRight = CorneredShape.Corner.Rounded,
            bottomLeft = CorneredShape.Corner.Rounded,
            topLeft = CorneredShape.Corner.Sharp,
            topRight = CorneredShape.Corner.Sharp
        )
    )

    Scaffold { inner ->
        Column(modifier = Modifier.padding(inner)) {
            CartesianChartHost(
                rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            com,
                            com.copy(
                                shape = CorneredShape(
                                    topLeft = CorneredShape.Corner.Sharp,
                                    topRight = CorneredShape.Corner.Sharp,
                                    bottomRight = CorneredShape.Corner.Sharp,
                                    bottomLeft = CorneredShape.Corner.Sharp,
                                ),
                                fill = Fill(android.graphics.Color.GREEN),
                            ),
                            com.copy(
                                shape = CorneredShape(
                                    topLeft = CorneredShape.Corner.Rounded,
                                    topRight = CorneredShape.Corner.Rounded,
                                    bottomRight = CorneredShape.Corner.Sharp,
                                    bottomLeft = CorneredShape.Corner.Sharp,
                                ),
                                fill = Fill(android.graphics.Color.BLUE),
                            ),
                        ),
                        columnCollectionSpacing = 10.dp,
                        mergeMode = mergeMode,
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = CartesianValueFormatter { context, value, verticalAxisPosition ->
                            dateFormat.format(chartVariables[value.toInt()].date)
                        }
                    ),
                ),
                modelProducer,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter =  painterResource(R.drawable.vector_circle), null, tint = Color.Red, modifier = Modifier.size(15.dp))
                    Text("Alt Veri")
                }
                Row(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter =  painterResource(R.drawable.vector_circle), null, tint = Color.Green, modifier = Modifier.size(15.dp))
                    Text("Orta Veri")
                }
                Row(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter =  painterResource(R.drawable.vector_circle), null, tint = Color.Blue, modifier = Modifier.size(15.dp))
                    Text("Üst Veri")
                }
            }
        }
    }
}