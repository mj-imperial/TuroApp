package com.example.turomobileapp.ui.reusablefunctions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopNavigationBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    onNotificationClick: () -> Unit,
    subtitle: TextUnit,
    body: TextUnit,
    modifier: Modifier = Modifier,
    barHeight: Dp,
    hasNotification: Boolean
){
    val iconSize = barHeight * 0.5f
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.bookicon_icon),
                    tint = Color.White,
                    contentDescription = "Book Icon",
                    modifier = Modifier.size(iconSize)
                )
                Spacer(Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ){
                    Text(
                        text = stringResource(R.string.TURO),
                        color = MainWhite,
                        fontSize = body,
                        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                        lineHeight = body
                    )
                    Text(
                        text = stringResource(R.string.byGSCS),
                        color = MainOrange,
                        fontSize = subtitle,
                        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                        modifier = Modifier.padding(start = 5.dp),
                        lineHeight = subtitle
                    )
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.back_icon),
                        tint = MainWhite,
                        contentDescription = "Back Button",
                        modifier = Modifier.size(iconSize)
                    )
                }
            }else{
                IconButton(onClick = {TODO()}) {
                    Icon(
                        painter = painterResource(R.drawable.hamburger_icon),
                        tint = MainWhite,
                        contentDescription = "Hamburger Button",
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (hasNotification) {
                        Badge(
                            containerColor = MainOrange
                        ) {}
                    }
                }
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(barHeight)
                        .padding(0.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.notifications_icon),
                        contentDescription = "Notifications",
                        tint = MainWhite,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(MainRed),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        windowInsets = WindowInsets(0),
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth()
            .height(barHeight)
    )
}