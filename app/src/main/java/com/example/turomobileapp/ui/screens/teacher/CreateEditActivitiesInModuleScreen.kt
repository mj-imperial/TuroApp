package com.example.turomobileapp.ui.screens.teacher

import AppScaffold
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
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
import com.example.turomobileapp.models.ModuleActivityResponse
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.ButtonItems
import com.example.turomobileapp.ui.components.CustomDropDownMenu
import com.example.turomobileapp.ui.components.DropdownMenuItem
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LighterOrange
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.ui.theme.practice2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.ActivityActionsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateEditActivitiesInModuleScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ActivityActionsViewModel,
    moduleId: String
){
    val context = LocalContext.current
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()

    val activitiesSortedByDate: List<ModuleActivityResponse> =
        uiState.activities.map {
            val dt = LocalDateTime.parse(it.unlockDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val parsedDate = dt.toLocalDate()
            it to parsedDate
        }
        .sortedBy { it.second }
        .map { it.first }
    val tutorials = activitiesSortedByDate.filter { it.activityType == "TUTORIAL" }
    val lectures = activitiesSortedByDate.filter { it.activityType == "LECTURE" }
    val quizzes = activitiesSortedByDate.filter { it.activityType == "QUIZ" }
    var selectedToDeleteId by remember { mutableStateOf<String?>(null) }

    var currentMenu by remember { mutableStateOf("Lecture") }
    val menuList = listOf(
        DropdownMenuItem(
            itemName = "Lecture",
            leadingIcon = R.drawable.lecture_icon,
            onClick = {
                currentMenu = "Lecture"
            }
        ),
        DropdownMenuItem(
            itemName = "Tutorial",
            leadingIcon = R.drawable.tutorial_floating_icon,
            onClick = {
                currentMenu = "Tutorial"
            }
        ),
        DropdownMenuItem(
            itemName = "Quiz",
            leadingIcon = R.drawable.quiz_icon,
            onClick = {
                currentMenu = "Quiz"
            }
        )
    )
    val floatingButtonItemList = listOf(
        ButtonItems(
            icon = painterResource(R.drawable.tutorial_floating_icon),
            title = "Create Tutorial",
            onClick = {
                navController.navigate(Screen.TeacherCreateTutorial.createRoute(moduleId))
            }
        ),
        ButtonItems(
            icon = painterResource(R.drawable.lecture_icon),
            title = "Create Lecture",
            onClick = {
                navController.navigate(Screen.TeacherCreateLecture.createRoute(moduleId))
            }
        ),
        ButtonItems(
            icon = painterResource(R.drawable.quiz_icon),
            title = "Create Quiz",
            onClick = {
                navController.navigate(Screen.TeacherCreateQuiz.createRoute(moduleId))
            }
        )
    )

    LaunchedEffect(uiState.deleteStatusResult) {
        if (uiState.deleteStatusResult == Result.Success(Unit)){
            Toast.makeText(context, "Activity successfully deleted.",Toast.LENGTH_SHORT).show()
        }
        viewModel.resetDeleteStatusResult()
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        hasFloatingActionButton = true,
        items = floatingButtonItemList,
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
                        .fillMaxSize()
                        .padding(padding)
                        .padding(25.dp)
                ) {
                    CustomDropDownMenu(
                        menuText = currentMenu,
                        dropdownMenuItems = menuList
                    )

                    when(currentMenu){
                        "Lecture" -> ActivityGrid(
                            windowInfo = windowInfo,
                            activities = lectures,
                            activityType = "LECTURE",
                            onEdit = {
                                navController.navigate(Screen.TeacherEditLecture.createRoute(moduleId, it.activityId))
                            },
                            onDeleteClick = { activityId ->
                                selectedToDeleteId = activityId
                            }
                        )
                        "Tutorial" -> ActivityGrid(
                            windowInfo = windowInfo,
                            activities = tutorials,
                            activityType = "TUTORIAL",
                            onEdit = {
                                navController.navigate(Screen.TeacherEditTutorial.createRoute(moduleId, it.activityId))
                            },
                            onDeleteClick = { activityId ->
                                selectedToDeleteId = activityId
                            }
                        )
                        "Quiz" -> ActivityGrid(
                            windowInfo = windowInfo,
                            activities = quizzes,
                            activityType = "QUIZ",
                            onEdit = {
                                navController.navigate(Screen.TeacherEditQuiz.createRoute(it.activityId, moduleId))
                            },
                            onDeleteClick = { activityId ->
                                selectedToDeleteId = activityId
                            }
                        )
                    }
                }

                if (selectedToDeleteId != null){
                    PopupAlertWithActions(
                        onDismissRequest = { selectedToDeleteId = null },
                        onConfirmation = {
                            viewModel.deleteActivity(selectedToDeleteId!!)
                            selectedToDeleteId = null
                        },
                        icon = painterResource(R.drawable.delete_vector_icon),
                        title = {
                            Text(
                                text = "DELETE ACTIVITY",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.title(windowInfo),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        dialogText = {
                            Text(
                                text = "Are you sure you want to delete this item? \nThis action cannot be undone",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.heading3(windowInfo),
                            )
                        },
                        confirmText = {
                            Text(
                                text = "Yes",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.heading3(windowInfo),
                                color = MainRed
                            )
                        },
                        dismissText = {
                            Text(
                                text = "No",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.heading3(windowInfo),
                                color = LoginText
                            )
                        }
                    )
                }

                LaunchedEffect(uiState.deleteStatusResult) {
                    if (uiState.deleteStatusResult == Result.Success(Unit)) {
                        Toast.makeText(
                            context,
                            "Activity successfully deleted.",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.getActivitiesInModule()
                    }
                    viewModel.resetDeleteStatusResult()
                }
            }
        }
    )
}

@Composable
fun ActivityGrid(
    windowInfo: WindowInfo,
    activities: List<ModuleActivityResponse>,
    activityType: String,
    onEdit: (ModuleActivityResponse) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (activities.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.empty_list_icon),
                contentDescription = "No activities",
                modifier = Modifier.size(windowInfo.screenWidth * 0.5f),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            userScrollEnabled = true,
        ) {
            items(activities) { item ->
                ActivityItem(
                    windowInfo = windowInfo,
                    activityType = activityType,
                    activityName = item.activityName,
                    height = windowInfo.screenHeight * 0.1f,
                    onClickEdit = { onEdit(item) },
                    onClickDelete= { onDeleteClick(item.activityId) }
                )
            }
        }
    }
}

@Composable
fun ActivityItem(
    windowInfo: WindowInfo,
    activityType: String,
    activityName: String,
    height: Dp,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(LighterOrange,practice2)))
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = if (activityType == "TUTORIAL") painterResource(R.drawable.tutorial_icon)
                    else if (activityType == "LECTURE") painterResource(R.drawable.viewallmodules)
                    else painterResource(R.drawable.shortquiz_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(height * 0.8f)
                        .padding(end = 10.dp)
                )

                Text(
                    text = activityName,
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
              horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClickEdit) {
                    Icon(
                        painter = painterResource(R.drawable.edit_icon),
                        contentDescription = null,
                        tint = green
                    )
                }

                IconButton(onClick = onClickDelete) {
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