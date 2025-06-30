package com.berkaykbl.chartapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextFieldComponent(
    modifier: Modifier,
    label: String,
    text: String,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    inputType: String = "text",
    suffix: String = "",
    onClick: () -> Unit = {},
    onChange: (String) -> Unit
) {

    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme
    val color = if (isError) {
        Color.Red
    } else {
        colorScheme.onBackground
    }
    val keyboardType = if (inputType == "double" || inputType == "date") {
        KeyboardType.Number
    } else {
        KeyboardType.Text
    }

    val visualTransformation = if(inputType == "date") {
        DateVisualTransformation()
    } else {
        VisualTransformation.None
    }

    val fieldModifier = if (readOnly) {
        Modifier
            .clickable {
                onClick()
            }
    } else {
        Modifier
    }
    Row(
        modifier = modifier.padding(horizontal = 15.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        TextField(
            text,
            { change ->
                if (inputType == "date") {
                    var digits = change.filter { it.isDigit() }
                    if (digits.length <= 8) {
                        var anError = false
                        if (digits.length > 1) {
                            println(digits.substring(0, 2))
                            val day = digits.substring(0, 2).toInt()
                            if (day > 31) anError = true
                        }
                        if (digits.length > 3) {
                            val month = digits.substring(2, 4).toInt()
                            if (month > 12) anError = true
                        }

                        if (!anError) {
                            onChange(digits)
                        }
                    }
                } else if (inputType == "double") {
                    onChange(formatDoubleToString(change))
                }

            },
            label = {
                Text(label)
            },
            enabled = !readOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedLabelColor = color,
                unfocusedLabelColor = color,
                focusedTextColor = color,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledTextColor = color,
                disabledLabelColor = color,
                disabledPlaceholderColor = color,
                cursorColor = color,
            ),
            readOnly = readOnly,
            singleLine = singleLine,
            modifier = fieldModifier
                .weight(1f)
                .padding(bottom = 5.dp)
                .drawBehind {
                    val lineY = size.height - 1.dp.toPx()
                    drawLine(
                        color = color,
                        start = Offset(0f, lineY),
                        end = Offset(size.width, lineY),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .horizontalScroll(scrollState),
            visualTransformation = visualTransformation
        )
        Text(suffix, fontSize = 13.sp)
    }
    Spacer(Modifier.height(10.dp))
}