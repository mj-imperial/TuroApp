package com.example.turomobileapp.ui.screens.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.ModuleListActivityActionsViewModel

//TODO add picture instead of icon
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleFoldersScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ModuleListActivityActionsViewModel,
    courseId: String,
    sectionId: String
){
    val context = LocalContext.current
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()

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

    LaunchedEffect(uiState.moduleDeleteStatus) {
        if (uiState.moduleDeleteStatus == Result.Success(Unit)) {
            Toast.makeText( context, "Module successfully deleted.", Toast.LENGTH_SHORT).show()
            viewModel.getModulesInCourse()
        }
        viewModel.resetModuleDeleteStatus()
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { padding ->
            if (uiState.loading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }else{
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 15.dp)
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "MODULES",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.title(windowInfo),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    PullToRefreshBox(
                        isRefreshing = uiState.loading,
                        state = pullRefreshState,
                        onRefresh = {
                            viewModel.getModulesInCourse()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val columns = when {
                            windowInfo.screenWidth >= 1000.dp -> 4
                            windowInfo.screenWidth >= 700.dp -> 3
                            else -> 2
                        }

                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = GridCells.Fixed(columns),
                            contentPadding = PaddingValues(15.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.modules) { module ->
                                ModuleFolders(
                                    windowInfo = windowInfo,
                                    modulePicture = module.modulePicture,
                                    moduleName = module.moduleName,
                                    onFolderClick = {
                                        navController.navigate(
                                            Screen.TeacherCreateEditActivitiesInModule.createRoute(module.moduleId, sectionId)
                                        )
                                    },
                                    onClickEdit = {
                                        navController.navigate(Screen.TeacherEditModule.createRoute(courseId,module.moduleId, sectionId))
                                    },
                                    onDeleteClick = {
                                        selectedToDeleteId = module.moduleId
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ModuleFolders(
    windowInfo: WindowInfo,
    modulePicture: ByteArray?,
    moduleName: String,
    onFolderClick: () -> Unit,
    onClickEdit: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onFolderClick),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            if (modulePicture == null){
                AsyncImage(
                    model = "https://wallpapers.com/images/featured/math-background-jbcyizvw0ckuvcro.jpg",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }else{
                BlobImage(
                    byteArray = modulePicture,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(15.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                IconButton(
                    onClick = onClickEdit,
                    modifier = Modifier
                        .size(28.dp)
                        .background(green, shape = RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit_icon),
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(28.dp)
                        .background(MainRed, shape = RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_delete_24),
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                Text(
                    text = moduleName,
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = MainWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}



