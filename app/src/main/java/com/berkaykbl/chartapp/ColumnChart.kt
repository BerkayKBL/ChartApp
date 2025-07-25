package com.berkaykbl.chartapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.shape.CorneredShape


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
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                fill = Fill(android.graphics.Color.RED),
                                shape = CorneredShape(
                                    bottomRight = CorneredShape.Corner.Rounded,
                                    bottomLeft = CorneredShape.Corner.Rounded,
                                    topLeft = CorneredShape.Corner.Rounded,
                                    topRight = CorneredShape.Corner.Rounded
                                )
                            )
                        )
                    ),
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