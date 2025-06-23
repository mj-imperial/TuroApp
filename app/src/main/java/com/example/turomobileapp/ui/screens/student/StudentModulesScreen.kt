package com.example.turomobileapp.ui.screens.student

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ViewAllModulesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentModulesScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ViewAllModulesViewModel,
    courseId: String
){
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getModules()
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            if (uiState.loading){
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }else{
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.getModules()
                    },
                ) {
                    val columns = when {
                        windowInfo.screenWidth >= 1000.dp -> 4
                        windowInfo.screenWidth >= 700.dp -> 3
                        else -> 2
                    }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .padding(it)
                            .padding(15.dp)
                            .fillMaxSize(),
                        columns = GridCells.Fixed(columns),
                        contentPadding = PaddingValues(15.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(uiState.modules) { index, item ->
                            val isUnlocked = if (index == 0){
                                true
                            }else{
                                val previousModule = uiState.modules[index - 1]
                                previousModule.moduleProgress == 100.0
                            }

                            ModuleItem(
                                windowInfo = windowInfo,
                                moduleName = item.moduleName,
                                modulePicture = item.modulePicture,
                                isUnlocked = isUnlocked,
                                progress = item.moduleProgress,
                                onClickModule = {
                                    viewModel.updateModuleName(item.moduleName)
                                    navController.navigate(Screen.StudentModuleActivities.createRoute(courseId, item.moduleId))
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun ModuleItem(
    windowInfo: WindowInfo,
    moduleName: String,
    modulePicture: ByteArray,
    isUnlocked: Boolean,
    progress: Double,
    onClickModule: () -> Unit
){
    val formattedProgress = String.format("%.1f", progress)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                onClick = onClickModule,
                enabled = isUnlocked,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            BlobImage(
                byteArray = modulePicture,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f),
                alpha = if (isUnlocked) 1f else 0.6f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .background(TextBlack),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$formattedProgress%",
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    color = MainWhite,
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!isUnlocked) {
                        Icon(
                            painter = painterResource(R.drawable.lock),
                            contentDescription = null,
                            tint = MainWhite,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }

                    Text(
                        text = moduleName,
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = MainWhite,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

