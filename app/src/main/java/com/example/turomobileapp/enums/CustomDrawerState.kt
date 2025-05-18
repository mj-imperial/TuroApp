package com.example.turomobileapp.enums

enum class CustomDrawerState {
    OPENED,
    CLOSED
}

fun CustomDrawerState.isOpened(): Boolean{
    return this.name == "OPENED"
}

fun CustomDrawerState.opposite(): CustomDrawerState{
    return if (this == CustomDrawerState.OPENED) CustomDrawerState.CLOSED
    else CustomDrawerState.OPENED
}