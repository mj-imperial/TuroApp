package com.example.turomobileapp.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.models.StudentLeaderboardResponse
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack

@Composable
fun FirstPlaceRow(
    student: StudentLeaderboardResponse,
    windowInfo: WindowInfo,
    height: Dp
) {
    val avatarSize = height * 0.8f
    val badgeSize  = avatarSize / 3
    val borderColor = MainOrange

    val background = Color(255,182,29,195)
    val background2 = Color(197,154,66,195)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.verticalGradient(colors = listOf(background, background2)))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier.size(avatarSize)
        ) {
            BlobImage(
                byteArray = student.profilePic,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = borderColor, shape = CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(borderColor)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = "1",
                    color = MainWhite,
                    fontSize = ResponsiveFont.body(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "${student.firstName} ${student.lastName}",
                fontSize = ResponsiveFont.title(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${student.totalPoints} pts",
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = TextBlack
            )
        }
    }
}

@Composable
fun SecondPlaceRow(
    student: StudentLeaderboardResponse,
    windowInfo: WindowInfo,
    height: Dp
) {
    val avatarSize = height * 0.8f
    val badgeSize = avatarSize / 3
    val borderColor = Color(0, 155, 214)

    val background = Color(0,155,214,191)
    val background2 = Color(6,98,133,191)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.verticalGradient(colors = listOf(background, background2)))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier.size(avatarSize)
        ) {
            BlobImage(
                byteArray = student.profilePic,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = borderColor, shape = CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(borderColor)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = "2",
                    color = Color.White,
                    fontSize = ResponsiveFont.body(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = "${student.firstName} ${student.lastName}",
                fontSize = ResponsiveFont.heading1(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "${student.totalPoints} pts",
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun ThirdPlaceRow(
    student: StudentLeaderboardResponse,
    windowInfo: WindowInfo,
    height: Dp
) {
    val avatarSize = height * 0.8f
    val badgeSize  = avatarSize / 3
    val borderColor = Color(0, 217, 95)

    val background = Color(0,217,95,169)
    val background2 = Color(6,141,64,169)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.verticalGradient(colors = listOf(background, background2)))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier.size(avatarSize)
        ) {
            BlobImage(
                byteArray = student.profilePic,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = borderColor, shape = CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(borderColor)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = "3",
                    color = Color.White,
                    fontSize = ResponsiveFont.body(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = "${student.firstName} ${student.lastName}",
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${student.totalPoints} pts",
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun RegularPlaceRow(
    student: StudentLeaderboardResponse,
    position: Int,
    windowInfo: WindowInfo,
    height: Dp
) {
    val avatarSize = height * 0.8f
    val badgeSize  = avatarSize / 3
    val borderColor = Color(0xFF888888)

    val background = Color(0xB9888888)
    val background2 = Color(0xB9575757)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.verticalGradient(colors = listOf(background, background2)))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier.size(avatarSize)
        ) {
            AsyncImage(
                model = student.profilePic,
                contentDescription = "Avatar of ${student.firstName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = borderColor, shape = CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(borderColor)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = position.toString(),
                    color = Color.White,
                    fontSize = ResponsiveFont.body(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = "${student.firstName} ${student.lastName}",
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${student.totalPoints} pts",
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.DarkGray
            )
        }
    }
}






