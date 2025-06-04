package com.example.turomobileapp.ui.components

import android.annotation.SuppressLint
import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.viewinterop.AndroidView
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.green
import java.time.Duration

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReusableTimeLimitPicker(
    windowInfo: WindowInfo,
    selectedDuration: MutableState<Duration?>,
    label: String
) {
    val durationText: String = selectedDuration.value
        ?.let { dur ->
            val totalSeconds = dur.seconds
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } ?: ""

    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = durationText,
        onValueChange = { /* read‐only */ },
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Pick time limit"
                )
            }
        }
    )

    if (showDialog) {
        val initialDur = selectedDuration.value ?: Duration.ZERO
        val initialSecondsTotal = initialDur.seconds
        val startH = (initialSecondsTotal / 3600).toInt()
        val startM = ((initialSecondsTotal % 3600) / 60).toInt()
        val startS = (initialSecondsTotal % 60).toInt()

        var hour by remember { mutableStateOf(startH) }
        var minute by remember { mutableStateOf(startM) }
        var second by remember { mutableStateOf(startS) }

        PopupAlertWithActions(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                selectedDuration.value = Duration
                    .ofHours(hour.toLong())
                    .plusMinutes(minute.toLong())
                    .plusSeconds(second.toLong())
                showDialog = false
            },
            icon = painterResource(R.drawable.time_icon),
            title = {
                Text(
                    text = "SET QUIZ TIME LIMIT",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo),
                    color = LoginText
                )
            },
            dialogText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AndroidView(factory = { ctx ->
                        NumberPicker(ctx).apply {
                            minValue = 0
                            maxValue = 23
                            value = hour
                            setFormatter { i -> String.format("%02d", i) }
                            setOnValueChangedListener { _, _, newVal ->
                                hour = newVal
                            }
                        }
                    })

                    Text(
                        text = ":",
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )

                    AndroidView(factory = { ctx ->
                        NumberPicker(ctx).apply {
                            minValue = 0
                            maxValue = 59
                            value = minute
                            setFormatter { i -> String.format("%02d", i) }
                            setOnValueChangedListener { _, _, newVal ->
                                minute = newVal
                            }
                        }
                    })

                    Text(
                        text = ":",
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )

                    AndroidView(factory = { ctx ->
                        NumberPicker(ctx).apply {
                            minValue = 0
                            maxValue = 59
                            value = second
                            setFormatter { i -> String.format("%02d", i) }
                            setOnValueChangedListener { _, _, newVal ->
                                second = newVal
                            }
                        }
                    })
                }
            },
            confirmText = {
                Text(
                    text = "CONFIRM",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = green
                )
            },
            dismissText = {
                Text(
                    text = "CANCEL",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = LoginText
                )
            }
        )
    }
}

