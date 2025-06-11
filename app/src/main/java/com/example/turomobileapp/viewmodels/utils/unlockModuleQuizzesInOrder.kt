package com.example.turomobileapp.viewmodels.utils

import com.example.turomobileapp.models.ModuleActivityResponse

fun unlockModuleQuizzesInOrder(
    activities: List<ModuleActivityResponse>,
    quizTypeOrder: List<String> = listOf("PRACTICE", "SHORT", "LONG")
): List<ModuleActivityResponse> {
    val unlocked = mutableListOf<ModuleActivityResponse>()

    val grouped = activities
        .filter { it.activityType == "QUIZ" && it.quizTypeName != "SCREENING_EXAM" }
        .groupBy { it.moduleId }

    grouped.forEach { (_, moduleQuizzes) ->
        var canUnlock = true

        val sorted = moduleQuizzes.sortedBy {
            quizTypeOrder.indexOf(it.quizTypeName!!.uppercase())
        }

        sorted.forEach { quiz ->
            val hasAnswered = quiz.hasAnswered == true

            unlocked.add(
                quiz.copy(
                    isUnlocked = canUnlock,
                    hasAnswered = hasAnswered
                )
            )
            canUnlock = hasAnswered
        }
    }

    return unlocked
}