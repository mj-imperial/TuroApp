package com.example.turomobileapp.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.viewmodels.authentication.AgreementTermsViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TermsAgreementScreen(
    navController: NavController,
    viewModel: AgreementTermsViewModel = hiltViewModel()
){
    val windowInfo = rememberWindowInfo()
    val cardHeight = windowInfo.screenHeight * 0.95f
    var cardWidth = windowInfo.screenWidth * 0.9f
    val paragraphList = listOf(
        stringResource(R.string.ConfidentialityP1),
        stringResource(R.string.ConfidentialityP2),
        stringResource(R.string.ConfidentialityP3),
        stringResource(R.string.ConfidentialityP4)
    )
    val radioList = listOf(
        stringResource(R.string.Understand),
        stringResource(R.string.NotUnderstand)
    )
    val titlePaddings = when (windowInfo.screenHeightInfo) {
        WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.02f
        WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.03f
        WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.04f
    }
    val paragraphPaddings = when (windowInfo.screenHeightInfo) {
        WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.01f
        WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.02f
        WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.03f
    }

    val uiState by viewModel.uiState.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MainRed)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        TermsCard(
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            heading1 = ResponsiveFont.heading1(windowInfo),
            heading2 = ResponsiveFont.heading2(windowInfo),
            titlePaddings = titlePaddings,
            paragraphList = paragraphList,
            radioList = radioList,
            paragraphPaddings = paragraphPaddings,
            hasAgreed = uiState.hasAgreed,
            setAgreed = viewModel::setAgreed,
            saveAgreement = viewModel::saveAgreement,
            loading = uiState.loading
        )
    }

    val ctx = LocalContext.current
    LaunchedEffect(uiState.isAgreementSaved) {
        if (uiState.isAgreementSaved is Result.Success){
            navController.navigate(Screen.Dashboard.route){
                popUpTo(0) { inclusive = true }
            }
        }else{
            Toast.makeText(ctx, "Failed to save agreement, please try again", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun TermsCard(
    cardWidth: Dp,
    cardHeight: Dp,
    heading1: TextUnit,
    heading2: TextUnit,
    titlePaddings: Dp,
    paragraphList: List<String>,
    radioList: List<String>,
    paragraphPaddings: Dp,
    hasAgreed: Boolean,
    setAgreed: (Boolean) -> Unit,
    saveAgreement: () -> Unit,
    loading: Boolean
){
    Card(
        modifier = Modifier.size(width = cardWidth, height = cardHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Confidentiality Agreement",
                    fontSize = heading1,
                    fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = titlePaddings)
                )

                paragraphList.forEach { paragraph ->
                    Text(
                        text = paragraph,
                        fontSize = heading2,
                        fontFamily = FontFamily(Font(R.font.alexandria)),
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(bottom = paragraphPaddings)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.padding(top = titlePaddings).fillMaxWidth()
            ){
                radioList.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (hasAgreed && option == radioList[0]) || (!hasAgreed && option == radioList[1]),
                                onClick = {
                                    setAgreed(option == radioList[0])
                                }
                            )
                            .padding(bottom = titlePaddings)
                    ) {
                        RadioButton(
                            selected = (hasAgreed && option == radioList[0]) || (!hasAgreed && option == radioList[1]),
                            onClick = { setAgreed(option == radioList[0]) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MainOrange
                            )
                        )
                        Text(
                            text = option,
                            fontSize = heading2,
                            fontFamily = FontFamily(Font(R.font.alexandria)),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                CapsuleButton(
                    text = {
                        Text(
                            text = stringResource(R.string.Submit)
                        )
                    },
                    onClick = {
                        if (hasAgreed) saveAgreement()
                    },
                    modifier = Modifier.height(40.dp).width(100.dp).align(Alignment.CenterHorizontally),
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
        }
    }
}
