package spiral.bit.dev.dailymood.ui.feature.creationMood.surveyAddMood.providers

import android.content.Context
import spiral.bit.dev.dailymood.R

class AnswerProvider {

    fun getAnswers(context: Context): List<String> {
        return context.resources.getStringArray(R.array.survey_answers).toList()
    }
}