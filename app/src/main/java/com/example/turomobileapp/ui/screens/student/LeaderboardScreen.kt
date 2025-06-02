package com.example.turomobileapp.ui.screens.student

import AppScaffold
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.StudentProgressResponse
import com.example.turomobileapp.ui.components.CustomDropDownMenu
import com.example.turomobileapp.ui.components.DropdownMenuItem
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.LeaderboardViewModel

@Composable
fun LeaderboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    leaderboardViewModel: LeaderboardViewModel = hiltViewModel()
){
    val windowInfo = rememberWindowInfo()
    val leaderboardUiState by leaderboardViewModel.uiState.collectAsState()

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
            leadingIcon = R.drawable.trophy,
            onClick = {
                currentMenu = "Achievements"
            }
        )
    )

    val sortedStudents = remember(leaderboardUiState.progresses) {
        leaderboardUiState.progresses.sortedByDescending { it.totalPoints }
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomDropDownMenu(
                        menuText = currentMenu,dropdownMenuItems = menuList
                    )

                    when(currentMenu){
                        "Leaderboard" -> LeaderboardCard(
                            students = sortedStudents,
                            windowInfo = windowInfo,
                            loading = leaderboardUiState.loading
                        )
                        "Badges" -> BadgesCard()
                        "Achievements" -> AchievementCard()
                    }
                }
            }
        }
    )
}

@Composable
fun LeaderboardCard(
    students: List<StudentProgressResponse>,
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
                    0 -> FirstPlaceRow(student, windowInfo, windowInfo.screenHeight * 0.13f)
                    1 -> SecondPlaceRow(student, windowInfo, windowInfo.screenHeight * 0.10f)
                    2 -> ThirdPlaceRow(student, windowInfo, windowInfo.screenHeight * 0.10f)
                    else -> RegularPlaceRow(student, index + 1, windowInfo, windowInfo.screenHeight * 0.10f)
                }
            }
        }
    }
}

@Composable
fun BadgesCard(){
    Text("this is badges")
}

@Composable
fun AchievementCard(){
    Text("this is achievements")
}