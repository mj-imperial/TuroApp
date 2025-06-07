package com.example.turomobileapp.ui.components

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.turomobileapp.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReusableDateTimePicker(
    selectedDateTime: LocalDateTime?,
    label: String,
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"),
    onUpdateDateTime: (LocalDateTime) -> Unit,
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    val displayText = selectedDateTime?.format(dateFormatter) ?: ""

    val initialDateMillis = selectedDateTime
        ?.atZone(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    var tempPickedDate by remember { mutableStateOf<LocalDate?>(selectedDateTime?.toLocalDate()) }
    var tempPickedTime by remember { mutableStateOf<LocalTime?>(selectedDateTime?.toLocalTime()) }

    OutlinedTextField(
        value = displayText,
        onValueChange = { },
        label = { Text(label) },
        placeholder = {
            Text(
                text = "MM/DD/YYYY HH:MM AM/PM",
                fontFamily = FontFamily(Font(R.font.alata)),
            )
        },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showDateDialog = true
            },
        trailingIcon = {
            Row {
                IconButton(onClick = { showDateDialog = true }) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Choose date")
                }
                Spacer(Modifier.height(0.dp).width(4.dp))
                IconButton(onClick = { showTimeDialog = true }) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = "Choose time")
                }
            }
        }
    )

    if (showDateDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDateDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        tempPickedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDateDialog = false
                    showTimeDialog = true
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDateDialog = false
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimeDialog) {
        val now = LocalTime.now()
        val hour = tempPickedTime?.hour ?: now.hour
        val minute = tempPickedTime?.minute ?: now.minute

        TimePickerDialog(
            LocalContext.current,
            { _, pickedHour: Int, pickedMinute: Int ->
                tempPickedTime = LocalTime.of(pickedHour, pickedMinute)

                val finalDate = tempPickedDate ?: LocalDate.now()
                val finalTime = tempPickedTime ?: LocalTime.now()
                val newDateTime = LocalDateTime.of(finalDate, finalTime)
                onUpdateDateTime(newDateTime)

                showTimeDialog = false
            },
            hour,
            minute,
            false
        ).show()
    }
}
