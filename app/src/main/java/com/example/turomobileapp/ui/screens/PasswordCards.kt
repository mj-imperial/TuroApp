package com.example.turomobileapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.reusablefunctions.CapsuleButton
import com.example.turomobileapp.ui.reusablefunctions.CapsuleTextField
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.SoftGray
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext.Key

@Composable
fun PasswordCard(
    title: TextUnit,
    body: TextUnit,
    heading3Size: TextUnit,
    oldPassword: String,
    onOldPasswordChange: (String) -> Unit,
    loading: Boolean,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onChangeConfirmPassword: (String) -> Unit,
    onChangePassword: () -> Unit,
    errorMessage: String?,
    requiresPasswordChange: Boolean
){
    val passwordFocusRequester = remember { FocusRequester() }

    Text(
        text = stringResource(R.string.ChangeDefaultPassword),
        color = MainOrange,
        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
        fontSize = title,
        textAlign = TextAlign.Center
    )

    if (requiresPasswordChange){
        CapsuleTextField(
            value = oldPassword,
            onValueChange = { newOldPassword ->
                onOldPasswordChange(newOldPassword)
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.OldPassword),
                    color = LoginText,
                    fontSize = body,
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.OldPassword),
                    color = LoginText,
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            },
            isSingleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
            roundedCornerShape = 28.dp,
            colors = colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = SoftGray,
                disabledContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(28.dp),
                    clip = false
                )
                .background(
                    color = SoftGray,
                    shape = RoundedCornerShape(28.dp)
                )
                .then(if (!loading) Modifier else Modifier.alpha(0.5f)),
            enabled = !loading
        )
    }

    CapsuleTextField(
        value = newPassword,
        onValueChange = { newPassword ->
            onNewPasswordChange(newPassword)
        },
        placeholder = {
            Text(
                text = stringResource(R.string.NewPassword),
                color = LoginText,
                fontSize = body,
                fontFamily = FontFamily(Font(R.font.alata))
            )
        },
        label = {
            Text(
                text = stringResource(R.string.NewPassword),
                color = LoginText,
                fontFamily = FontFamily(Font(R.font.alata))
            )
        },
        isSingleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        visualTransformation = PasswordVisualTransformation(),
        keyboardActions = KeyboardActions(
            onNext = { passwordFocusRequester.requestFocus() }
        ),
        roundedCornerShape = 28.dp,
        colors = colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = SoftGray,
            disabledContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(passwordFocusRequester)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false
            )
            .background(
                color = SoftGray,
                shape = RoundedCornerShape(28.dp)
            )
            .then(if (!loading) Modifier else Modifier.alpha(0.5f)),
        enabled = !loading
    )

    CapsuleTextField(
        value = confirmPassword,
        onValueChange = { newPassword ->
            onChangeConfirmPassword(newPassword)
        },
        placeholder = {
            Text(
                text = stringResource(R.string.ConfirmPassword),
                color = LoginText,
                fontSize = body,
                fontFamily = FontFamily(Font(R.font.alata))
            )
        },
        label = {
            Text(
                text = stringResource(R.string.ConfirmPassword),
                color = LoginText,
                fontFamily = FontFamily(Font(R.font.alata))
            )
        },
        isSingleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        visualTransformation = PasswordVisualTransformation(),
        keyboardActions = KeyboardActions(
            onDone = { onChangePassword() }
        ),
        roundedCornerShape = 28.dp,
        colors = colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = SoftGray,
            disabledContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(passwordFocusRequester)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false
            )
            .background(
                color = SoftGray,
                shape = RoundedCornerShape(28.dp)
            )
            .then(if (!loading) Modifier else Modifier.alpha(0.5f)),
        enabled = !loading
    )

    errorMessage?.let { msg ->
        Text(
            text = msg,
            color = MainRed,
            fontSize = body,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }

    CapsuleButton(
        text = {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.ChangePassword),
                    fontSize = heading3Size,
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            }
        },
        onClick = { onChangePassword() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        roundedCornerShape = 24.dp,
        buttonElevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        ),
        buttonColors = ButtonDefaults.buttonColors(
            containerColor = MainOrange,
            contentColor = MainWhite
        ),
        enabled = !loading
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun EmailStep(
    title: TextUnit,
    body: TextUnit,
    heading3Size: TextUnit,
    subtitle: TextUnit,
    email: String,
    loading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    cooldownRemaining: Int
) {
    var showTimer by remember { mutableStateOf(false) }
    val minutes = cooldownRemaining / 60
    val seconds = cooldownRemaining % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

    Text(
        text = stringResource(R.string.GetEmailCode),
        color = MainOrange,
        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
        fontSize = title,
        textAlign = TextAlign.Center
    )

    CapsuleTextField(
        value = email,
        onValueChange = { new ->
            onEmailChange(new)
        },
        placeholder = {
            Text(
                text = stringResource(R.string.Email),
                color = LoginText,
                fontSize = body,
                fontFamily = FontFamily(Font(R.font.alata))
            )
        },
        label = {
            Text(
                text = stringResource(R.string.Email),
                color = LoginText,
                fontFamily = FontFamily(Font(R.font.alata))
            )
        },
        isSingleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onSendCode()
                showTimer = true
            }
        ),
        roundedCornerShape = 28.dp,
        colors = colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = SoftGray,
            disabledContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false
            )
            .background(
                color = SoftGray,
                shape = RoundedCornerShape(28.dp)
            )
            .then(if (!loading) Modifier else Modifier.alpha(0.5f)),
        enabled = !loading
    )

    errorMessage?.let { msg ->
        Text(
            text = msg,
            color = MainRed,
            fontSize = body,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }

    Column {
        CapsuleButton(
            text = {
                Text(
                    text = if (loading) "WAIT" else "SEND CODE",
                    fontSize = heading3Size,
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            },
            onClick = {
                onSendCode()
                showTimer = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            roundedCornerShape = 24.dp,
            buttonElevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 2.dp,
                disabledElevation = 0.dp
            ),
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = MainOrange,
                contentColor = MainWhite
            ),
            enabled = !loading && cooldownRemaining == 0
        )

        if (showTimer) {
            Text(
                text = timerText,
                color = LoginText,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = subtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Text(
                text = stringResource(R.string.CheckInbox),
                color = LoginText,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = subtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            if (cooldownRemaining > 0) {
                Text(
                    text = "Please wait $timerText to resend code",
                    fontSize = subtitle,
                    color = LoginText,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CodeStep(
    title: TextUnit,
    body: TextUnit,
    heading3Size: TextUnit,
    loading: Boolean,
    errorMessage: String?,
    onCodeChange: (String) -> Unit,
    onVerifyCode: () -> Unit,
    cooldownRemaining: Int,
    subtitle: TextUnit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val chars = remember { List(6) { mutableStateOf("") } }

    val minutes = cooldownRemaining / 60
    val seconds = cooldownRemaining % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

    Text(
        text = stringResource(R.string.EnterEmailCode),
        color = MainOrange,
        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
        fontSize = title,
        textAlign = TextAlign.Center
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        chars.forEachIndexed { i, charState ->
            CapsuleTextField(
                value = chars[i].value,
                onValueChange = { new ->
                    if (new.length <= 1 && new.all { it.isDigit() }) {
                        charState.value = new
                        onCodeChange(chars.joinToString("") { it.value })
                        if (new.isNotEmpty() && i < 5) {
                            focusRequesters[i + 1].requestFocus()
                        }
                        if (chars.all { it.value.isNotEmpty() }) {
                            onVerifyCode()
                        }
                    }
                },
                isSingleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (i == 5) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (!loading) onVerifyCode() }
                ),
                roundedCornerShape = 29.dp,
                colors = colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = SoftGray,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .size(64.dp)
                    .focusRequester(focusRequesters[i])
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyUp && keyEvent.type == KeyEventType.KeyDown) {
                            if (charState.value.isEmpty() && i > 0) {
                                focusRequesters[i - 1].requestFocus()
                            }
                            true
                        } else {
                            false
                        }
                    },
                enabled = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
        }
    }

    errorMessage?.let { msg ->
        Text(
            text = msg,
            color = MainRed,
            fontSize = body,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }

    Column {
        CapsuleButton(
            text = {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.EnterEmailCode),
                        fontSize = heading3Size,
                        fontFamily = FontFamily(Font(R.font.alata))
                    )
                }
            },
            onClick = {
                onVerifyCode()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            roundedCornerShape = 24.dp,
            buttonElevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 2.dp,
                disabledElevation = 0.dp
            ),
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = MainOrange,
                contentColor = MainWhite
            ),
            enabled = !loading
        )

        Text(
            text = timerText,
            color = LoginText,
            fontFamily = FontFamily(Font(R.font.alata)),
            fontSize = subtitle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Text(
            text = stringResource(R.string.CheckInbox),
            color = LoginText,
            fontFamily = FontFamily(Font(R.font.alata)),
            fontSize = subtitle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

