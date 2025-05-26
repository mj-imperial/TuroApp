package com.example.turomobileapp.ui.screens.student

import AppScaffold
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.AssessmentResultViewModel

@Composable
fun QuizResultScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: AssessmentResultViewModel
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.Start
                ) {

                }
            }
        }
    )
}

@Composable
fun QuizResultContent(){

}

@Composable
fun QuizResultAttemptHistory(){

}