@file:Suppress("FunctionName")

package com.example.turomobileapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.ui.theme.TextBlack

@Composable
fun CapsuleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable () -> Unit = { Text("") },
    label: @Composable (() -> Unit)? = null,
    isSingleLine: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    roundedCornerShape: Dp,
    modifier: Modifier,
    enabled: Boolean,
    textStyle: TextStyle = LocalTextStyle.current,
    maxLines: Int = 1
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        label = label,
        singleLine = isSingleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation= visualTransformation,
        shape = RoundedCornerShape(roundedCornerShape),
        colors = colors(
            focusedTextColor = TextBlack,
            unfocusedTextColor = TextBlack,
            errorTextColor = MainRed,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = SoftGray,
            disabledContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        enabled = enabled,
        textStyle = textStyle,
        maxLines = maxLines
    )
}