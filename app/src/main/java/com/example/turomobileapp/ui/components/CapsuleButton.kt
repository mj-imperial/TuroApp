package com.example.turomobileapp.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CapsuleButton(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    roundedCornerShape: Dp,
    buttonElevation: ButtonElevation,
    contentPadding: PaddingValues = PaddingValues(vertical = 0.dp),
    buttonColors: ButtonColors,
    enabled: Boolean
){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(roundedCornerShape),
        modifier = modifier,
        enabled = enabled,
        colors = buttonColors,
        elevation = buttonElevation,
        contentPadding = contentPadding,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        text()
    }
}