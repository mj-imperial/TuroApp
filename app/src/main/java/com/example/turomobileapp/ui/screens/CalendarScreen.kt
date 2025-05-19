//package com.example.turomobileapp.ui.screens
//
//import AppScaffold
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.text.font.Font
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.turomobileapp.R
//import com.example.turomobileapp.ui.components.WindowInfo
//import com.example.turomobileapp.ui.components.rememberWindowInfo
//import com.example.turomobileapp.ui.theme.TextBlack
//import com.example.turomobileapp.ui.theme.calendarEvent
//import com.example.turomobileapp.ui.theme.calendarGray
//import com.example.turomobileapp.ui.theme.green
//import com.example.turomobileapp.viewmodels.SessionManager
//import com.kizitonwose.calendar.compose.ContentHeightMode
//import com.kizitonwose.calendar.compose.HorizontalCalendar
//import com.kizitonwose.calendar.compose.rememberCalendarState
//import com.kizitonwose.calendar.core.CalendarDay
//import com.kizitonwose.calendar.core.DayPosition
//import com.kizitonwose.calendar.core.OutDateStyle
//import com.kizitonwose.calendar.core.daysOfWeek
//import java.time.DayOfWeek
//import java.time.LocalDate
//import java.time.YearMonth
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun CalendarScreen(
//    navController: NavController,
//    sessionManager: SessionManager
//){
//    val windowInfo = rememberWindowInfo()
//    val height = windowInfo.screenHeight
//
//    AppScaffold(
//        navController = navController,
//        canNavigateBack = true,
//        navigateUp = {
//            navController.navigateUp()
//        },
//        windowInfo = windowInfo,
//        sessionManager = sessionManager,
//        content = { innerPadding ->
//            CalendarContent(
//                innerPadding = innerPadding,
//                height = height
//            )
//        }
//    )
//
//
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun CalendarContent(
//    innerPadding: PaddingValues,
//    height: Dp
//){
//    val currentMonth = remember { YearMonth.now() }
//    val firstDayOfWeek = remember { DayOfWeek.SUNDAY }
//    val daysOfWeek = daysOfWeek(firstDayOfWeek = firstDayOfWeek)
//
//    val state = rememberCalendarState(
//        startMonth = remember { currentMonth.minusMonths(50) },
//        endMonth = remember { currentMonth.plusMonths(50) },
//        firstVisibleMonth = currentMonth,
//        firstDayOfWeek = firstDayOfWeek,
//        outDateStyle = OutDateStyle.EndOfGrid
//    )
//
//    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
//
//    Column(
//        modifier = Modifier
//            .padding(innerPadding)
//            .fillMaxSize()
//    ) {
//        Column(modifier = Modifier.fillMaxWidth().height(height * 0.55f)) {
//            HorizontalCalendar(
//                state = state,
//                calendarScrollPaged = true,
//                userScrollEnabled = true,
//                reverseLayout = false,
//                contentPadding = PaddingValues(10.dp),
//                contentHeightMode = ContentHeightMode.Wrap,
//                dayContent = {
//                    //TODO
//                },
//                monthHeader = {
//                    DaysOfWeekTitle(daysOfWeek = daysOfWeek)
//                },
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//    }
//}
//
//@Composable
//fun DaysOfWeekTitle(
//    daysOfWeek: List<DayOfWeek>,
//){
//    Row(modifier = Modifier.fillMaxWidth()) {
//        daysOfWeek.forEach { day ->
//            Text(
//                text = day.name,
//                modifier = Modifier.weight(1f),
//                textAlign = TextAlign.Center,
//                fontFamily = FontFamily(Font(R.font.alata)),
//            )
//        }
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun Day(
//    day: CalendarDay,
//    isSelected: Boolean,
//    hasEvent: Boolean,
//    isUrgent: Boolean,
//    onClick: (CalendarDay) -> Unit
//){
//    Box(
//        modifier = Modifier
//            .aspectRatio(1f)
//            .clip(RectangleShape)
//            .background(
//                if (isSelected) {
//                    green
//                }else if (hasEvent){
//                    calendarEvent
//                }else if (isUrgent){
//                    TextBlack
//                }else{
//                    calendarGray
//                }
//            )
//            .clickable(
//                enabled = day.position == DayPosition.MonthDate,
//                onClick = {onClick(day)}
//            )
//    ){
//        Text(
//            text = day.date.dayOfMonth.toString(),
//            textAlign = TextAlign.Center,
//            fontFamily = FontFamily(Font(R.font.alata))
//        )
//    }
//}
//
