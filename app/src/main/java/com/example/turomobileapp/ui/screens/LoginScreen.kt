package com.example.turomobileapp.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.reusablefunctions.CapsuleButton
import com.example.turomobileapp.ui.reusablefunctions.CapsuleTextField
import com.example.turomobileapp.ui.reusablefunctions.ResponsiveFont
import com.example.turomobileapp.ui.reusablefunctions.WindowInfo
import com.example.turomobileapp.ui.reusablefunctions.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.viewmodels.authentication.LoginEvent
import com.example.turomobileapp.viewmodels.authentication.LoginViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MainRed)
            .systemBarsPadding()
    ) {
        val heading1Size = ResponsiveFont.heading1(windowInfo)
        val heading2Size = ResponsiveFont.heading2(windowInfo)
        val heading3Size = ResponsiveFont.heading3(windowInfo)
        val body = ResponsiveFont.body(windowInfo)
        val subtitle = ResponsiveFont.subtitle(windowInfo)
        val cardHeight = when (windowInfo.screenHeightInfo) {
            WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.4f
            WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.5f
            WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.6f
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.book_open),
                    contentDescription = "App logo",
                    modifier = Modifier.size(
                        when (windowInfo.screenWidthInfo) {
                            WindowInfo.WindowType.Compact  -> windowInfo.screenWidth * 0.4f
                            WindowInfo.WindowType.Medium   -> windowInfo.screenWidth * 0.5f
                            WindowInfo.WindowType.Expanded -> windowInfo.screenWidth * 0.6f
                        }
                    )
                )

                Spacer(Modifier.width(8.dp))

                Column{
                    Text(
                        text = stringResource(R.string.TURO),
                        fontSize = heading2Size,
                        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                        color = MainWhite,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = stringResource(R.string.byGSCS),
                        fontSize = heading3Size,
                        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                        color = MainOrange,
                        modifier = Modifier.padding(start = 9.dp)
                    )
                }


            }

            Row(
                modifier = Modifier.weight(3f)
            ) {
                LoginCard(
                    parentMaxWidth = this@BoxWithConstraints.maxWidth,
                    email = uiState.email,
                    password = uiState.password,
                    isLoginEnabled = uiState.isLoginEnabled,
                    loading = uiState.loading,
                    onEmailChange = viewModel::updateEmail,
                    onPasswordChange = viewModel::updatePassword,
                    onSignIn = viewModel::login,
                    errorMessage = uiState.errorMessage,
                    cardHeight = cardHeight,
                    heading1Size = heading1Size,
                    body = body,
                    heading3Size = heading3Size,
                    subtitle = subtitle,
                    navController = navController
                )
            }
        }
    }

    val ctx = LocalContext.current
    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when(event) {
                is LoginEvent.ShowToast ->
                    Toast.makeText(ctx, event.message, Toast.LENGTH_SHORT).show()
                is LoginEvent.NavigateToChangeDefaultPassword -> {
                    val route = buildString {
                        append("changePassword?requiresChange=${event.requiresChange}")
                        append("&email=${Uri.encode(event.email)}")
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
                is LoginEvent.NavigateToDashboard -> {
                    val route = "dashboard_screen/${event.userId}"
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginCard(
    parentMaxWidth: Dp,
    email: String,
    password: String,
    isLoginEnabled: Boolean,
    loading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit,
    cardHeight: Dp,
    heading1Size: TextUnit,
    heading3Size: TextUnit,
    body: TextUnit,
    subtitle: TextUnit,
    errorMessage: String?,
    navController: NavController
){
    val passwordFocusRequester = remember { FocusRequester() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight),
        shape = RoundedCornerShape(bottomStart = 10.dp ,bottomEnd = 10.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(MainWhite)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(parentMaxWidth * 0.05f)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.Login),
                color = MainOrange,
                fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                fontSize = heading1Size
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
                    imeAction = ImeAction.Next
                ),
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

            CapsuleTextField(
                value = password,
                onValueChange = { new ->
                    onPasswordChange(new)
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.Password),
                        color = LoginText,
                        fontSize = body,
                        fontFamily = FontFamily(Font(R.font.alata))
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.Password),
                        color = LoginText,
                        fontFamily = FontFamily(Font(R.font.alata))
                    )
                },
                isSingleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSignIn() }
                ),
                visualTransformation = PasswordVisualTransformation(),
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

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MainRed,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = body
                )
            }

            Column(modifier = Modifier.fillMaxWidth()){
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
                                text = stringResource(R.string.SignIn).uppercase(),
                                fontSize = heading3Size,
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                        }
                    },
                    onClick = { onSignIn() },
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
                    enabled = isLoginEnabled && !loading
                )

                Text(
                    text = stringResource(R.string.ForgotPassword),
                    color = LoginText,
                    fontSize = subtitle,
                    fontFamily = FontFamily(Font(R.font.alata)),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.End)
                        .clickable(
                            enabled = true,
                            onClick = {
                                navController.navigate(
                                    Screen.ChangePassword.createRoute(
                                        requiresChange = false,
                                        email = ""
                                    )
                                )
                            }
                        )
                )
            }
        }
    }
}


//@Preview
//@Composable
//fun LoginScreenPreview(){
//    LoginScreen()
//}