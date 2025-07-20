package com.example.turomobileapp.ui.screens.student

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.ScreeningExamLearningResources
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobFileViewer
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.scorequizcolor
import com.example.turomobileapp.ui.theme.screeningBody2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ScreeningExamResultViewModel
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.SimpleYouTubePlayerOptionsBuilder
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubePlayer
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubePlayerHostState
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubePlayerState
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubeVideoId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreeningExamLearningResourcesScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ScreeningExamResultViewModel,
    conceptId: String,
    topicId: String?
){
    LaunchedEffect(conceptId, topicId) {
        viewModel.loadLearningResources(conceptId, topicId)
    }

    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { paddingValues ->
            if (uiState.loading) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }else{
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.loadLearningResources(conceptId, topicId)
                    },
                    modifier = Modifier.padding(paddingValues)
                ) {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        item {
                            ScreeningResourceTitle(
                                windowInfo = windowInfo,
                                headingName = "CONCEPT RESOURCES"
                            )

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        items(uiState.resources) { resource ->
                            ScreeningResourceBody(
                                resource = resource,
                                windowInfo = windowInfo
                            )

                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ScreeningResourceTitle(
    windowInfo: WindowInfo,
    headingName: String
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(screeningBody2, RoundedCornerShape(5.dp))
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(R.drawable.additionalscreening),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
        )

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = headingName,
            fontSize = ResponsiveFont.heading1(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = MainWhite
        )
    }
}

@Composable
fun ScreeningResourceBody(
    resource: ScreeningExamLearningResources,
    windowInfo: WindowInfo
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(scorequizcolor, RoundedCornerShape(5.dp))
            .padding(16.dp)
    ) {
        Text(
            text = resource.title,
            fontSize = ResponsiveFont.heading2(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = MainWhite
        )

        resource.description?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = MainWhite
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (!resource.videoUrl.isNullOrBlank()) {
            val context = LocalContext.current
            val intent = remember(resource.videoUrl) {
                Intent(Intent.ACTION_VIEW, resource.videoUrl.toUri())
            }

            val videoId = remember(resource.videoUrl) { extractYoutubeVideoId(resource.videoUrl) }
            val hostState = remember { YouTubePlayerHostState() }

            Text(
                text = resource.videoUrl,
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

        if (resource.pdfBlob != null) {
            Spacer(modifier = Modifier.height(10.dp))
            BlobFileViewer(
                windowInfo = windowInfo,
                fileBytes = resource.pdfBlob
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
