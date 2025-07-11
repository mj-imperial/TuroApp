package com.example.turomobileapp.viewmodels.utils

import com.example.turomobileapp.models.ModuleActivityResponse

fun unlockModuleQuizzesInOrder(
    activities: List<ModuleActivityResponse>,
    quizTypeOrder: List<String> = listOf("PRACTICE", "SHORT")
): List<ModuleActivityResponse> {
    val unlocked = mutableListOf<ModuleActivityResponse>()

    val grouped = activities
        .filter { it.activityType == "QUIZ" }
        .groupBy { it.moduleId }

    grouped.forEach { (_, moduleQuizzes) ->
        var canUnlock = true

        val sorted = moduleQuizzes.sortedBy {
            val type = it.quizTypeName?.uppercase()
            quizTypeOrder.indexOf(type).takeIf { i -> i >= 0 } ?: Int.MAX_VALUE
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