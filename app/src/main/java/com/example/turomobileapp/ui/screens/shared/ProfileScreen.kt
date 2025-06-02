package com.example.turomobileapp.ui.screens.shared

import AppScaffold
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.authentication.LoginViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    sessionManager: SessionManager,
    loginViewModel: LoginViewModel = hiltViewModel()
){

    val windowInfo = rememberWindowInfo()
    val screenHeight = windowInfo.screenHeight
    val headerHeight = screenHeight * 0.25f
    val imageSize = headerHeight * 0.70f
    val imageOverlap = imageSize / 2f

    AppScaffold(
        navController = navController,
        modifier = Modifier,
        canNavigateBack = true,
        navigateUp = {navController.navigateUp()},
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { innerPadding ->
            ProfileContent(
                innerPadding = innerPadding,
                headerHeight = headerHeight,
                imageOverlap = imageOverlap,
                imageSize = imageSize,
                sessionManager = sessionManager,
                onLogOut = {
                    loginViewModel.logout()
                    navController.navigate(Screen.Login.route)
                },
                windowInfo = windowInfo,
            )
        },
    )
}

@Composable
fun ProfileContent(
    innerPadding: PaddingValues,
    headerHeight: Dp,
    imageSize: Dp,
    imageOverlap: Dp,
    sessionManager: SessionManager,
    onLogOut: () -> Unit,
    windowInfo: WindowInfo,
) {
    val firstName by sessionManager.firstName.collectAsState(initial = "")
    val lastName by sessionManager.lastName.collectAsState(initial = "")
    val email by sessionManager.email.collectAsState(initial = "")
    val roleStr by sessionManager.role.collectAsState(initial = "")

    val items = listOf(
        ProfileCardItems(
            name = R.string.Name,
            icon = R.drawable.fullname_icon,
            value = "$firstName $lastName"
        ),
        ProfileCardItems(
            name = R.string.Email,
            icon = R.drawable.mail_icon,
            value = email.toString()
        ),
        ProfileCardItems(
            name = R.string.Role,
            icon = R.drawable.role_icon,
            value = roleStr.toString()
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(MainOrange)
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = headerHeight - imageOverlap)
                .clip(RoundedCornerShape(topStart = 24.dp,topEnd = 24.dp))
                .drawBehind {
                    val stroke = 2.dp.toPx()
                    drawLine(
                        color = MainOrange,
                        start = Offset(0f,stroke / 2),
                        end = Offset(size.width,stroke / 2),
                        strokeWidth = stroke
                    )
                },
            colors = CardDefaults.cardColors(containerColor = MainWhite),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = imageOverlap + 16.dp,
                        bottom = 24.dp,
                    ),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    items.forEach { item ->
                        ProfileField(
                            nameRes = item.name,
                            iconRes = item.icon,
                            value = item.value
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                CapsuleButton(
                    text = {
                        Text(
                            text = stringResource(R.string.Logout),
                            color = MainRed,
                            fontSize = ResponsiveFont.heading3(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata))
                        )
                    },
                    onClick = {
                        onLogOut()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp,MainRed,RoundedCornerShape(28.dp)),
                    roundedCornerShape = 28.dp,
                    buttonElevation = ButtonDefaults.buttonElevation(8.dp),
                    buttonColors = ButtonDefaults.buttonColors(
                        containerColor = MainWhite,
                        contentColor = MainRed,
                        disabledContainerColor = Color.DarkGray,
                        disabledContentColor = Color.DarkGray,
                    ),
                    enabled = true
                )
            }
        }

        Box(
            modifier = Modifier
                .size(imageSize)
                .align(Alignment.TopCenter)
                .offset(y = imageOverlap)
        ) {
            AsyncImage(
                model = sessionManager.profilePicUrl,
                placeholder = painterResource(R.drawable.default_account),
                error = painterResource(R.drawable.default_account),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .border(4.dp,MainOrange,CircleShape)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun ProfileField(
    @StringRes nameRes: Int,
    @DrawableRes iconRes: Int,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(nameRes),
            tint = Color.Black,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(25.dp))
        Column {
            Text(
                text = stringResource(nameRes),
                fontSize = ResponsiveFont.heading3(rememberWindowInfo()),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.Black
            )
            Text(
                text = value,
                fontSize = ResponsiveFont.body(rememberWindowInfo()),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.Black
            )
        }
    }
}


data class ProfileCardItems(
    @StringRes val name: Int,
    @DrawableRes val icon: Int,
    val value: String
)

