package com.example.turomobileapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun PopupAlertWithActions(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    icon: Painter,
    title: @Composable () -> Unit = { Text("") },
    dialogText: @Composable () -> Unit = { Text("") },
    confirmText: @Composable () -> Unit = { Text("") },
    dismissText: @Composable () -> Unit = { Text("") }
) {
    AlertDialog(
        icon = {
            Icon(
                painter = icon,
                contentDescription = "Icon"
            )
        },
        title = {
            title()
        },
        text = {
            dialogText()
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                confirmText()
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                dismissText()
            }
        }
    )
}