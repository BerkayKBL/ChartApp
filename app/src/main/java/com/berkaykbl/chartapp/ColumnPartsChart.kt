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
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore

@Composable
fun ColumnPartsChart(
    chartVariables: List<VariableEntity>
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val stackedData = listOf(
        listOf(30f, 70f),
        listOf(40f, 20f),
        listOf(50f, 50f)
    )
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries {
                series(1, 2, 3, 4)

            }
        }
    }

    val mergeMode: (ExtraStore) -> ColumnCartesianLayer.MergeMode = {
        val r = ColumnCartesianLayer.MergeMode.Stacked
        r.getMaxY(
            ColumnCartesianLayerModel(
                listOf(
                    listOf(
                        ColumnCartesianLayerModel.Entry(1, 5),
                        ColumnCartesianLayerModel.Entry(1, 10),
                    ),
                )
            )
        )
        r.getMinY(ColumnCartesianLayerModel(
            listOf(
                listOf(
                    ColumnCartesianLayerModel.Entry(1, 5),
                    ColumnCartesianLayerModel.Entry(1, 10),
                ),
            )
        ))
        r
    }

    Scaffold { inner ->
        Column(modifier = Modifier.padding(inner)) {
            CartesianChartHost(
                rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        mergeMode = mergeMode
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