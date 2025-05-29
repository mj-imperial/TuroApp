package com.example.turomobileapp.ui.screens.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.enums.ResetStep
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.viewmodels.authentication.ChangePasswordViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val requiresPasswordChange = uiState.requiresChange
    val cooldown by viewModel.cooldownRemaining.collectAsState()

    if (uiState.resetStep == ResetStep.LOADING) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MainRed)
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MainWhite)
        }
        return
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MainRed)
            .systemBarsPadding()
    ) {
        val cardHeight = when (windowInfo.screenHeightInfo) {
            WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.5f
            WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.6f
            WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.7f
        }

        LaunchedEffect(uiState.passwordChangeResult) {
            if (uiState.passwordChangeResult == Result.Success(Unit)) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
                viewModel.resetPasswordChangeResult()
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(MainWhite)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(this@BoxWithConstraints.maxWidth * 0.05f)
                        .fillMaxSize()
                ) {
                    when(uiState.resetStep){
                        ResetStep.EMAIL_INPUT -> {
                            EmailStep(
                                email = uiState.email,
                                loading = uiState.loading,
                                errorMessage = uiState.errorMessage,
                                onEmailChange = viewModel::updateEmail,
                                onSendCode = viewModel::sendRequestCode,
                                title = ResponsiveFont.title(windowInfo),
                                body = ResponsiveFont.body(windowInfo),
                                heading3Size = ResponsiveFont.heading3(windowInfo),
                                subtitle = ResponsiveFont.subtitle(windowInfo),
                                cooldownRemaining = cooldown
                            )
                        }
                        ResetStep.CODE_INPUT  -> {
                            CodeStep(
                                loading = uiState.loading,
                                errorMessage = uiState.errorMessage,
                                onCodeChange = viewModel::updateVerificationCode,
                                onVerifyCode = viewModel::verifyResetCode,
                                title = ResponsiveFont.title(windowInfo),
                                body = ResponsiveFont.body(windowInfo),
                                heading3Size = ResponsiveFont.heading3(windowInfo),
                                cooldownRemaining = cooldown,
                                subtitle = ResponsiveFont.subtitle(windowInfo)
                            )
                        }
                        ResetStep.PASSWORD_INPUT -> {
                            PasswordCard(
                                title = ResponsiveFont.title(windowInfo),
                                body = ResponsiveFont.body(windowInfo),
                                heading3Size = ResponsiveFont.heading3(windowInfo),
                                oldPassword = uiState.oldPassword,
                                onOldPasswordChange = viewModel::updateOldPassword,
                                loading = uiState.loading,
                                newPassword = uiState.newPassword,
                                onNewPasswordChange = viewModel::updateNewPassword,
                                confirmPassword = uiState.confirmPassword,
                                onChangeConfirmPassword = viewModel::updateConfirmPassword,
                                errorMessage = uiState.errorMessage,
                                requiresPasswordChange = requiresPasswordChange,
                                onChangePassword = if (requiresPasswordChange == true) viewModel::changeDefaultPassword else viewModel::resetPassword
                            )
                        }
                        else -> {Unit}
                    }
                }
            }
        }
    }
}