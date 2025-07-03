package com.berkaykbl.chartapp

import android.annotation.SuppressLint
import android.graphics.Color
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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.copyColor
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider

@SuppressLint("RestrictedApi")
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
    val col = Color.RED.copyColor(alpha = 50)
    val col2 = Color.TRANSPARENT
    val areaFill = LineCartesianLayer.AreaFill.single(Fill(ShaderProvider.verticalGradient(col, col2)))
    val lineSpec = LineCartesianLayer.Line(
        fill = LineCartesianLayer.LineFill.single(Fill(Color.argb(100, 255, 255, 255))),
        areaFill = areaFill,

    )

    Scaffold { inner ->
        Column(modifier = Modifier.padding(inner)) {
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(lineSpec)
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