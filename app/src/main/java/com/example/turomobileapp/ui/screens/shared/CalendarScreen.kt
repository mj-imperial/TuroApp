package com.example.turomobileapp.ui.screens.shared

import AppScaffold
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.CalendarResponse
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.calendarEvent
import com.example.turomobileapp.ui.theme.calendarGray
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.shared.CalendarViewModel
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: CalendarViewModel = hiltViewModel()
){
    val windowInfo = rememberWindowInfo()
    val height = windowInfo.screenHeight

    val uiState by viewModel.uiState.collectAsState()
    val eventsByDate = remember(uiState.rawEvents) {
        uiState.rawEvents.groupBy { it.date.toLocalDate() }
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                CalendarContent(
                    height = height,
                    windowInfo = windowInfo,
                    eventsByDate = eventsByDate
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarContent(
    height: Dp,
    windowInfo: WindowInfo,
    eventsByDate: Map<LocalDate, List<CalendarResponse>>
){
    val currentMonth = remember { YearMonth.now() }
    val firstDayOfWeek = remember { DayOfWeek.SUNDAY }
    val daysOfWeek = daysOfWeek(firstDayOfWeek = firstDayOfWeek)

    val state = rememberCalendarState(
        startMonth = remember { currentMonth.minusMonths(25) },
        endMonth = remember { currentMonth.plusMonths(25) },
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfGrid
    )

    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val eventsList = remember(selectedDate, eventsByDate) {
        selectedDate?.let { eventsByDate[it].orEmpty() } ?: emptyList()
    }
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .height(height * 0.5f)
        ) {
            HorizontalCalendar(
                state = state,
                calendarScrollPaged = true,
                userScrollEnabled = true,
                reverseLayout = false,
                contentHeightMode = ContentHeightMode.Wrap,
                dayContent = { day ->
                    val hasEvent = eventsByDate[day.date].orEmpty().isNotEmpty()
                    Day(
                        day = day,
                        isSelected = day.date == selectedDate,
                        hasEvent = hasEvent,
                        isUrgent = eventsByDate[day.date]?.any { it.isUrgent } == true,
                        onClick = {
                            selectedDate = it.date
                        },
                    )
                },
                monthHeader = {
                    Text(
                        text = "${it.yearMonth.month.name} ${it.yearMonth.year}",
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                    )
                    AbbreviatedDaysOfWeek(
                        daysOfWeek = daysOfWeek.map { DayOfWeek.of(it.value) }
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), 0.8.dp,LoginText)
        }

        DateEvents(
            windowInfo = windowInfo,
            list = eventsList,
            height = height * 0.5f
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    hasEvent: Boolean,
    isUrgent: Boolean,
    onClick: (CalendarDay) -> Unit
){
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RectangleShape)
            .padding(3.dp)
            .background(
                when {
                    isUrgent -> TextBlack
                    hasEvent -> calendarEvent
                    else -> calendarGray
                }
            )
            .clickable(
                enabled = day.position==DayPosition.MonthDate,onClick = { onClick(day) })
    ){
        Text(
            text = day.date.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.alata)),
            color = if (isUrgent) MainWhite else TextBlack
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AbbreviatedDaysOfWeek(
    daysOfWeek: List<DayOfWeek>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                ),
                fontFamily = FontFamily(Font(R.font.alata)),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateEvents(
    windowInfo: WindowInfo,
    list: List<CalendarResponse>,
    height: Dp
){
    val dateFormatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
    if (list.isEmpty()){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height*0.5f),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(R.drawable.empty_icon),
                contentDescription = "No Events",
                modifier = Modifier.fillMaxSize()
            )
        }
    }else{
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(height*0.5f),
            contentPadding = PaddingValues(5.dp)
        ) {
            items(list) {
                Text(
                    text = it.title,
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                )

                Text(
                    text = it.date.format(dateFormatterOut),
                    fontSize = ResponsiveFont.body(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                )

                Text(
                    text = "Course: ${it.courseCode}",
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    modifier = Modifier.padding(start = 20.dp)
                )

                Text(
                    text = "Description: ${it.description.toString()}\nLocation: ${it.location}",
                    fontSize = ResponsiveFont.body(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    modifier = Modifier.padding(start = 20.dp, bottom = 15.dp)
                )
            }
        }
    }
}

