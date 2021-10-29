package spiral.bit.dev.dailymood.ui.feature.survey_result_mood.view

import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import spiral.bit.dev.dailymood.R
import spiral.bit.dev.dailymood.data.emotion.MoodEntity
import spiral.bit.dev.dailymood.data.emotion.MoodRepository
import spiral.bit.dev.dailymood.ui.base.BaseViewModel
import spiral.bit.dev.dailymood.ui.common.mappers.EmotionTypeMapper
import spiral.bit.dev.dailymood.ui.common.resolvers.SurveyResolver
import spiral.bit.dev.dailymood.ui.feature.survey_result_mood.models.SurveyResult
import spiral.bit.dev.dailymood.ui.feature.survey_result_mood.models.SurveyResultItem
import spiral.bit.dev.dailymood.ui.feature.survey_result_mood.models.mvi.MoodSurveyEffect
import spiral.bit.dev.dailymood.ui.feature.survey_result_mood.models.mvi.MoodSurveyState
import javax.inject.Inject

@HiltViewModel
class SurveyResultViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val surveyResolver: SurveyResolver,
    private val emotionTypeMapper: EmotionTypeMapper
) : BaseViewModel<MoodSurveyState, MoodSurveyEffect>() {

    override val container = container<MoodSurveyState, MoodSurveyEffect>(MoodSurveyState(emptyList(), null))

    fun createDataForRecyclerView(
        depressionSectionItems: Int,
        neurosisSectionItems: Int,
        socialPhobiaSectionItems: Int,
        astheniaSectionItems: Int,
        insomniaSectionItems: Int
    ) = intent {
        val surveyResultsList = listOf(
            SurveyResultItem(
                SurveyResult(
                    surveyReason = R.string.depression_reason,
                    scoresInThisSection = depressionSectionItems,
                    advicesText = R.string.depression_advices
                )
            ),
            SurveyResultItem(
                SurveyResult(
                    surveyReason = R.string.neurosis_reason,
                    scoresInThisSection = neurosisSectionItems,
                    advicesText = R.string.neurosis_advices
                )
            ),
            SurveyResultItem(
                SurveyResult(
                    surveyReason = R.string.social_phobia_reason,
                    scoresInThisSection = socialPhobiaSectionItems,
                    advicesText = R.string.social_phobia_advices
                )
            ),
            SurveyResultItem(
                SurveyResult(
                    surveyReason = R.string.asthenia_reason,
                    scoresInThisSection = astheniaSectionItems,
                    advicesText = R.string.asthenia_advices
                )
            ),
            SurveyResultItem(
                SurveyResult(
                    surveyReason = R.string.insomnia_reason,
                    scoresInThisSection = insomniaSectionItems,
                    advicesText = R.string.insomnia_advices
                )
            )
        )
        reduce { state.copy(surveyResultList = surveyResultsList) }
    }

    fun repeatTest() = intent {
        postSideEffect(MoodSurveyEffect.NavigateToSurvey)
    }

    fun getCurrentMood(scores: IntArray) = intent {
        val moodValue = surveyResolver.resolveSurvey(scores)
        val emotionType = emotionTypeMapper.mapToEmotionType(moodValue)
        reduce { state.copy(moodValue = emotionType) }
    }

    fun saveMood(scores: IntArray) = intent {
        val moodValue = surveyResolver.resolveSurvey(scores)
        val moodEntity = MoodEntity(emotionType = moodValue)
        moodRepository.insert(moodEntity)
        postSideEffect(MoodSurveyEffect.NavigateToMain)
    }
}