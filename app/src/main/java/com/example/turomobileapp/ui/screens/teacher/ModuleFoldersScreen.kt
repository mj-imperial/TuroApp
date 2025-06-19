package com.example.turomobileapp.ui.screens.teacher

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.ui.theme.practice2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.ModuleListActivityActionsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleFoldersScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ModuleListActivityActionsViewModel,
    courseId: String
){
    val context = LocalContext.current
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()
    val modules = uiState.modules

    var selectedToDeleteId by remember { mutableStateOf<String?>(null) }

    if (selectedToDeleteId != null){
        PopupAlertWithActions(
            onDismissRequest = { selectedToDeleteId = null },
            onConfirmation = {
                viewModel.deleteModuleInCourse(selectedToDeleteId!!)
                viewModel.resetModuleDeleteStatus()
                selectedToDeleteId = null
            },
            icon = painterResource(R.drawable.delete_vector_icon),
            title = {
                Text(
                    text = "Delete Module",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo),
                    fontWeight = FontWeight.Medium
                )
            },
            dialogText = {
                Text(
                    text = "Every activity created under this module will also be deleted.\nAre you sure you want to delete? This action can't be reversed.",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo)
                )
            },
            confirmText = {
                Text(
                    text = "Yes",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.body(windowInfo),
                    color = MainRed
                )
            },
            dismissText = {
                Text(
                    text = "No",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.body(windowInfo),
                    color = LoginText
                )
            }
        )
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
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
                            viewModel.getModulesInCourse()
                        },
                    ) {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            columns = GridCells.FixedSize(windowInfo.screenWidth * 0.90f),
                            contentPadding = PaddingValues(15.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            item {
                                Text(
                                    text = "MODULES",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = ResponsiveFont.title(windowInfo),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            items(
                                items = modules,
                                key = { it.moduleId }
                            ) {  module ->
                                ModuleFolders(
                                    windowInfo = windowInfo,
                                    cardHeight = windowInfo.screenHeight * 0.09f,
                                    moduleName = module.moduleName,
                                    onFolderClick = {
                                        navController.navigate(
                                            Screen.TeacherCreateEditActivitiesInModule.createRoute(
                                                module.moduleId
                                            )
                                        )
                                    },
                                    onClickEdit = {
                                        navController.navigate(Screen.TeacherEditModule.createRoute(courseId,module.moduleId))
                                    },
                                    onDeleteClick = {
                                        selectedToDeleteId = module.moduleId
                                    },
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(uiState.moduleDeleteStatus) {
                if (uiState.moduleDeleteStatus == Result.Success(Unit)) {
                    Toast.makeText( context, "Module successfully deleted.", Toast.LENGTH_SHORT).show()
                    viewModel.getModulesInCourse()
                }
                viewModel.resetModuleDeleteStatus()
            }
        }
    )
}

@Composable
fun ModuleFolders(
    windowInfo: WindowInfo,
    cardHeight: Dp,
    moduleName: String,
    onFolderClick: () -> Unit,
    onClickEdit: () -> Unit,
    onDeleteClick: () -> Unit,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable(onClick = onFolderClick),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(MainOrange, practice2)))
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.folder_icon),
                    contentDescription = "$moduleName Folder",
                    modifier = Modifier
                        .size(cardHeight * 0.6f)
                        .padding(end = 12.dp)
                )

                Text(
                    text = moduleName,
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClickEdit) {
                    Icon(
                        painter = painterResource(R.drawable.edit_icon),
                        contentDescription = null,
                        tint = green
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_delete_24),
                        contentDescription = null,
                        tint = MainRed
                    )
                }
            }
        }
    }
}