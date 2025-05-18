package com.example.turomobileapp.enums

enum class UserRole {
    STUDENT,
    TEACHER;

    companion object {
        fun fromId(id: Int): UserRole = when (id) {
            1 -> STUDENT
            2 -> TEACHER
            else -> throw IllegalArgumentException("Unknown role id: $id")
        }

        fun fromName(name: String): UserRole =
            UserRole.entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown role name: $name")
    }
}