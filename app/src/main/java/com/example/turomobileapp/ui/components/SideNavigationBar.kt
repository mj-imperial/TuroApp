package com.example.turomobileapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.navigation.sideNavigationItems
import com.example.turomobileapp.ui.theme.LighterOrange
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.SideRed1

@Composable
fun SideDrawer(
    navController: NavController,
    onCloseClick: () -> Unit,
    onLogOut: () -> Unit,
    windowInfo: WindowInfo,
    heading2: TextUnit,
    body: TextUnit,
    profilePic: ByteArray?,
    firstName: String,
    lastName: String,
    email: String,
) {
    val drawerWidth = windowInfo.screenWidth * 0.75f
    val drawerHeight = windowInfo.screenHeight
    val headerHeight = drawerHeight * 0.45f
    val iconSize = drawerWidth * 0.2f
    val drawerPadding = drawerWidth * 0.15f
    var isCloseClicked by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .width(drawerWidth)
            .fillMaxHeight()
            .background(SideRed1)
            .navigationBarsPadding()
            .padding(top = drawerPadding),
    ) {
        IconButton(onClick = {
            onCloseClick()
            isCloseClicked = true
        }) {
            Icon(
                painter = painterResource(R.drawable.close_icon),
                tint = if (isCloseClicked) MainOrange else MainWhite,
                contentDescription = "Close Button",
                modifier = Modifier.align(Alignment.Start)
            )
        }
        // —— HEADER ——
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .clickable(onClick = {
                    navController.navigate(Screen.Profile.route){
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (profilePic != null){
                    BlobImage(
                        byteArray = profilePic,
                        modifier = Modifier
                            .size(headerHeight * 0.45f)
                            .clip(CircleShape)
                    )
                }else{
                    AsyncImage(
                        model = R.drawable.default_account,
                        placeholder = painterResource(R.drawable.default_account),
                        error = painterResource(R.drawable.error_pic),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(headerHeight * 0.45f)
                            .clip(CircleShape)
                    )
                }

                Spacer(Modifier.height(15.dp))

                Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                    Text(
                        text = "$firstName $lastName",
                        fontSize = heading2,
                        color = MainWhite,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = email,
                        fontSize = body,
                        color = MainWhite,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
        HorizontalDivider(color = MainWhite, thickness = 1.dp)

        // —— NAV ITEMS ——
        val navEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navEntry?.destination?.route

        sideNavigationItems.forEach { item ->
            val selected = item.route == currentRoute
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (item.route == Screen.Login.route) {
                            onLogOut()
                        } else {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                        onCloseClick()
                    }
                    .padding(vertical = 16.dp,horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = stringResource(item.title),
                    tint = if (selected) LighterOrange else MainWhite,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(item.title),
                    fontSize = body,
                    color = if (selected) LighterOrange else MainWhite,
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            }
            HorizontalDivider(color = MainWhite.copy(alpha = 0.3f), thickness = 0.5.dp)
        }
    }
}
