package com.example.turomobileapp.viewmodels.utils

import com.example.turomobileapp.models.QuizResponse

fun unlockQuizzesInOrder(
    quizzes: List<QuizResponse>,
    quizTypeOrder: List<String> = listOf("PRACTICE", "SHORT", "LONG")
): List<QuizResponse> {
    val unlocked = mutableListOf<QuizResponse>()
    val grouped = quizzes.groupBy { it.moduleName }

    grouped.forEach { (_, moduleQuizzes) ->
        var canUnlock = true

        val sorted = moduleQuizzes.sortedBy {
            quizTypeOrder.indexOf(it.quizTypeName.uppercase())
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
