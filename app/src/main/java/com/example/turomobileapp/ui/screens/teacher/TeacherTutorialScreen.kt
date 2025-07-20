package com.example.turomobileapp.ui.screens.teacher

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.TutorialDetailViewModel
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.SimpleYouTubePlayerOptionsBuilder
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubePlayer
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubePlayerHostState
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubePlayerState
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubeVideoId

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeacherTutorialDetailScreen(
    viewModel: TutorialDetailViewModel,
    navController: NavController,
    sessionManager: SessionManager,
){
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            if (uiState.loading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }else{
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.getTutorial()
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.Start
                    ) {
                        item {
                            TutorialTitle(
                                height = windowInfo.screenHeight * 0.17f,
                                windowInfo = windowInfo,
                                tutorialName = uiState.tutorialName
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 15.dp),
                                thickness =  1.dp,
                                color = LoginText
                            )
                        }

                        item {
                            TutorialBody(
                                windowInfo = windowInfo,
                                tutorialDescription = uiState.tutorialDescription,
                                videoUrl = uiState.videoUrl
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 15.dp),
                                thickness =  1.dp,
                                color = LoginText
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TutorialTitle(
    height: Dp,
    windowInfo: WindowInfo,
    tutorialName: String,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.watch_video),
            contentDescription = null,
            modifier = Modifier.size(height)
        )

        Text(
            text = tutorialName,
            fontSize = ResponsiveFont.title(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata))
        )
    }
}

@Composable
fun TutorialBody(
    windowInfo: WindowInfo,
    tutorialDescription: String,
    videoUrl: String,
) {
    val context = LocalContext.current
    val intent = remember(videoUrl) {
        Intent(Intent.ACTION_VIEW, videoUrl.toUri())
    }

    val videoId = remember(videoUrl) { extractYoutubeVideoId(videoUrl) }
    val hostState = remember { YouTubePlayerHostState() }

    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tutorialDescription,
            fontSize = ResponsiveFont.heading3(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = videoUrl,
            fontSize = ResponsiveFont.heading3(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clickable {
                    context.startActivity(intent)
                }
        )

        val currentState = hostState.currentState
        if (currentState is YouTubePlayerState.Error) {
            Text(
                text = "Error: ${currentState.message}",
                color = Color.Red
            )
        }

        LaunchedEffect(currentState) {
            if (currentState is YouTubePlayerState.Ready && videoId != null) {
                hostState.loadVideo(YouTubeVideoId(videoId))
            }
        }

        YouTubePlayer(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            hostState = hostState,
            options = SimpleYouTubePlayerOptionsBuilder.builder {
                autoplay(false)
                controls(true)
                rel(false)
                ivLoadPolicy(false)
                ccLoadPolicy(false)
                fullscreen = true
            }
        )
    }
}

fun extractYoutubeVideoId(url: String): String? {
    return try {
        val uri = url.toUri()
        uri.getQueryParameter("v") ?: uri.lastPathSegment
    } catch (e: Exception) {
        null
    }
}