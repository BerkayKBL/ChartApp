package com.berkaykbl.chartapp

import android.graphics.Path
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.patrykandpatrick.vico.core.common.MeasuringContext
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlin.random.Random

@Composable
fun ColumnPartsChart(
    chartVariables: List<VariableEntity>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val top = listOf(150, 20, 80)
    val bottom = listOf(10, 150, 20)
    val topValues = ArrayList<Double>()
    val bottomValues = ArrayList<Double>()
    chartVariables.forEach {
        val variable = it.variable
        val topAverage = Random.nextDouble(0.0, 1.0)
        val bottomAverage = 1 - topAverage
        topValues.add(variable * topAverage)
        bottomValues.add(bottomAverage * variable)

    }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries {
                series(topValues)
                series(bottomValues)
            }
        }
    }

    val mergeMode: (ExtraStore) -> ColumnCartesianLayer.MergeMode = {
        ColumnCartesianLayer.MergeMode.Stacked
    }

    val spec = LineComponent(
        fill = Fill(android.graphics.Color.RED)
    )

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
                                    topLeft = CorneredShape.Corner.Rounded,
                                    topRight = CorneredShape.Corner.Rounded,
                                    bottomRight = CorneredShape.Corner.Sharp,
                                    bottomLeft = CorneredShape.Corner.Sharp,
                                ),
                                fill = Fill(android.graphics.Color.BLUE)
                            )
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
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}