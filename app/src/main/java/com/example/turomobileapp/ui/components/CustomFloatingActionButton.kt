package com.example.turomobileapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.MainOrange

@Composable
fun CustomFloatingActionButton(
    items: List<ButtonItems>
){
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }) + expandVertically(),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }) + shrinkVertically()
        ) {
            LazyColumn(modifier = Modifier.padding(bottom = 8.dp)) {
                items(items){
                    ActionButtonItemUI(
                        icon = it.icon,
                        title = it.title,
                        onClick = it.onClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        val transition = updateTransition(targetState = expanded, label = "transition")
        val rotation by transition.animateFloat(label = "rotation") {
            if (it) 315f else 0f
        }

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MainOrange,
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = "Add",
                modifier = Modifier.rotate(rotation),
            )
        }
    }
}

@Composable
fun ActionButtonItemUI(
    icon: Painter,
    title: String,
    onClick: () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.border(2.dp, MainOrange, RoundedCornerShape(15.dp)).padding(6.dp)
        ){
            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.alata))
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(50.dp),
            containerColor = MainOrange
        ) {
            Icon(
                painter = icon,
                contentDescription = title,
                modifier = Modifier.clip(CircleShape)
            )
        }
    }
}

data class ButtonItems(
    val icon: Painter,
    val title: String,
    val onClick: () -> Unit
)