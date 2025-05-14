package com.example.turomobileapp.ui.reusablefunctions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    body: TextUnit,
    subtitle: TextUnit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
){
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                    Icon(
                        painter = painterResource(R.drawable.bookicon_icon),
                        tint = Color.White,
                        contentDescription = "Book Icon",
                    )
                }
                Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                    Text(
                        text = stringResource(R.string.TURO),
                        color = MainWhite,
                        fontSize = body,
                        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                    )
                    Text(
                        text = stringResource(R.string.byGSCS),
                        color = MainOrange,
                        fontSize = subtitle,
                        fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.back_icon),
                        tint = MainWhite,
                        contentDescription = "Back Button",
                    )
                }
            }
        },
        actions = { actions },
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(MainRed),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}