package com.example.turomobileapp.ui.screens.student

import AppScaffold
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.models.StudentBadgeResponse
import com.example.turomobileapp.models.StudentLeaderboardResponse
import com.example.turomobileapp.objects.CelebrationPrefs
import com.example.turomobileapp.ui.components.CustomDropDownMenu
import com.example.turomobileapp.ui.components.DropdownMenuItem
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.GamificationViewModel
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: GamificationViewModel = hiltViewModel()
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    var currentMenu by remember { mutableStateOf<String>("Leaderboard") }

    val menuList = listOf(
        DropdownMenuItem(
            itemName = "Leaderboard",
            leadingIcon = R.drawable.leaderboard,
            onClick = {
                currentMenu = "Leaderboard"
            }
        ),
        DropdownMenuItem(
            itemName = "Badges",
            leadingIcon = R.drawable.badge,
            onClick = {
                currentMenu = "Badges"
            }
        ),
        DropdownMenuItem(
            itemName = "Achievements",
            leadingIcon = R.drawable.trophy_outlined,
            onClick = {
                currentMenu = "Achievements"
            }
        )
    )

    val sortedStudents = remember(uiState.progresses) {
        uiState.progresses.sortedByDescending { it.averageScore }
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            if (uiState.loading){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }else{
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.getLeaderboard()
                        viewModel.getBadgesForStudent()
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomDropDownMenu(
                            menuText = currentMenu,dropdownMenuItems = menuList
                        )

                        when(currentMenu){
                            "Leaderboard" -> LeaderboardCard(
                                students = sortedStudents,
                                windowInfo = windowInfo,
                                loading = uiState.loading
                            )
                            "Badges" -> BadgesCard(
                                studentBadges = uiState.studentBadges,
                                windowInfo = windowInfo
                            )
                            "Achievements" -> AchievementCard()
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LeaderboardCard(
    students: List<StudentLeaderboardResponse>,
    windowInfo: WindowInfo,
    loading: Boolean
) {
    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }else{
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(students) { index, student ->
                when (index) {
                    0 -> FirstPlaceRow(student, windowInfo, windowInfo.screenHeight * 0.16f)
                    1 -> SecondPlaceRow(student, windowInfo, windowInfo.screenHeight * 0.14f)
                    2 -> ThirdPlaceRow(student, windowInfo, windowInfo.screenHeight * 0.12f)
                    else -> RegularPlaceRow(student, index + 1, windowInfo, windowInfo.screenHeight * 0.10f)
                }
            }
        }
    }
}

@Composable
fun BadgesCard(
    studentBadges: List<StudentBadgeResponse>,
    windowInfo: WindowInfo
){
    val context = LocalContext.current
    val iconSize = windowInfo.screenHeight * 0.09f
    val unlockedBadges = studentBadges.filter { it.isUnlocked }
    val lockedBadges = studentBadges.filter { !it.isUnlocked }

    var showConfetti by remember { mutableStateOf(false) }

    val confettiEmitter = remember { Party(
        speed = 1f,
        maxSpeed = 10f,
        damping = 0.9f,
        spread = 360,
        angle = Angle.BOTTOM,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.5, 0.0)
    ) }

    LaunchedEffect(unlockedBadges) {
        val celebratedIds = CelebrationPrefs.getCelebratedBadges(context)

        val newlyUnlocked = unlockedBadges
            .filter { it.studentTotalPoints == it.pointsRequired && it.badgeId !in celebratedIds }
            .map { it.badgeId }
            .toSet()

        if (newlyUnlocked.isNotEmpty()) {
            showConfetti = true
            CelebrationPrefs.appendCelebratedBadges(context, newlyUnlocked)
            delay(2000)
            showConfetti = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(confettiEmitter)
            )
        }

        LazyColumn {
            item {
                Text(
                    text = "UNLOCKED BADGES",
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )

                if (unlockedBadges.isEmpty()){
                    Spacer(modifier = Modifier.height(40.dp))
                }else{
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            items(unlockedBadges) {
                IndividualBadgeCard(
                    badge = it,
                    iconSize = iconSize,
                    windowInfo = windowInfo
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Text(
                    text = "LOCKED BADGES",
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(15.dp))
            }

            items(lockedBadges) {
                IndividualBadgeCard(
                    badge = it,
                    iconSize = iconSize,
                    windowInfo = windowInfo
                )
            }
        }
    }
}

@Composable
fun IndividualBadgeCard(
    badge: StudentBadgeResponse,
    iconSize: Dp,
    windowInfo: WindowInfo
){
    val alpha = if (badge.isUnlocked) 1f else 0.7f
    val unlocked = badge.isUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MainWhite),
        border = BorderStroke(1.dp, LoginText)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = badge.badgeImage,
                contentDescription = "Badge ${badge.badgeName}",
                modifier = Modifier
                    .size(iconSize)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = MainOrange, shape = CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = badge.badgeName,
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontWeight = FontWeight.Bold,
                    color = if (unlocked) TextBlack else LoginText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = badge.badgeDescription,
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    maxLines = 2,
                    color = if (unlocked) TextBlack else LoginText
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text =
                        if (unlocked) "POINTS NEEDED: ${badge.pointsRequired}"
                        else "POINTS NEEDED: ${badge.studentTotalPoints}/${badge.pointsRequired}",
                    fontSize = ResponsiveFont.body(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = if (unlocked) TextBlack else LoginText
                )
            }
        }
    }
}

@Composable
fun AchievementCard(){

}