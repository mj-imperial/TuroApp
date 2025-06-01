package com.example.turomobileapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.MainWhite

@Composable
fun CustomDropDownMenu(
    menuText: String,
    dropdownMenuItems: List<DropdownMenuItem>
){
    val windowInfo = rememberWindowInfo()
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = !expanded }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = menuText,
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                modifier = Modifier.padding(end = 5.dp)
            )

            IconButton(onClick = { expanded = !expanded }) {
                Icon(painter = painterResource(R.drawable.dropdown), contentDescription = null)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            scrollState = rememberScrollState(),
            properties = PopupProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
            containerColor = MainWhite,
            tonalElevation = 5.dp,
            shadowElevation = 5.dp,
        ) {
            dropdownMenuItems.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.itemName,
                            fontSize = ResponsiveFont.body(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata))
                        )
                    },
                    onClick = it.onClick,
                    leadingIcon = {
                        Icon(painter = painterResource(it.leadingIcon), contentDescription = it.itemName)
                    }
                )
            }
        }
    }
}

data class DropdownMenuItem(
    val itemName: String,
    @DrawableRes val leadingIcon: Int,
    val onClick: () -> Unit
)