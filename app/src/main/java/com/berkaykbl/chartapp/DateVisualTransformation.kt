package com.berkaykbl.chartapp

import androidx.compose.ui.text.input.VisualTransformation
import java.time.LocalDate
import java.time.ZoneId

fun formatDoubleToString(double: String): String {
    var newDouble = double
    if (double.endsWith(".")) {
        newDouble = double
    } else {
        if (newDouble != "0" && newDouble.startsWith("0")) newDouble = newDouble.drop(1)
        newDouble = newDouble.formatDouble()
    }

    if (newDouble.isEmpty()) newDouble = "0"
    return newDouble
}


fun String.formatDouble(): String {
    val double = this.toDouble()
    return if (double % 1.0 == 0.0) {
        double.toInt().toString()
    } else {
        double.toString()
    }
}

fun convertToTimestamp(date: String): Long {
    val day = date.substring(0, 2).toInt()
    val month = date.substring(2, 4).toInt()
    val year = date.substring(4, 8).toInt()
    var local = LocalDate.of(year, month,day)
    val zoneId = ZoneId.systemDefault()
    val zoneDate = local.atStartOfDay(zoneId)
    return zoneDate.toInstant().toEpochMilli()
}

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val originalText = text.text
        val formattedText = buildString {
            for (i in originalText.indices) {
                append(originalText[i])
                if ((i == 1 || i == 3) && i < originalText.length -1) { // Add '/' after MM and DD
                    append('/')
                }
            }
            // Add a trailing '/' if the user has typed MM or MMDD
            if (originalText.length == 2 || originalText.length == 4) {
                append('/')
            }
        }

        val offsetMapping = object : androidx.compose.ui.text.input.OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // When the user types '10', the transformed text is '10/'
                // If the cursor is at original offset 2 (after '0'),
                // it should be at transformed offset 3 (after '/')
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 5) return offset + 2
                return formattedText.length // Default to end of string
            }

            override fun transformedToOriginal(offset: Int): Int {
                // When the transformed text is '10/25/4' and cursor is at transformed offset 3 (after '/'),
                // the original offset should be 2 (after '0' in '10')
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 8) return offset - 2
                return originalText.length // Default to end of string
            }
        }

        return androidx.compose.ui.text.input.TransformedText(
            androidx.compose.ui.text.AnnotatedString(formattedText),
            offsetMapping
        )
    }
}



