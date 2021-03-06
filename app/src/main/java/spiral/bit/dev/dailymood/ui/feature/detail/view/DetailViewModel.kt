package spiral.bit.dev.dailymood.ui.feature.detail.view

import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import spiral.bit.dev.dailymood.data.mood.MoodRepository
import spiral.bit.dev.dailymood.ui.base.BaseViewModel
import spiral.bit.dev.dailymood.ui.feature.detail.models.mvi.DetailEffect
import spiral.bit.dev.dailymood.ui.feature.detail.models.mvi.DetailState
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val moodRepository: MoodRepository
) : BaseViewModel<DetailState, DetailEffect>() {

    override val container = container<DetailState, DetailEffect>(DetailState(moodEntity = null))

    fun getEmotionById(moodId: Long) = intent {
        val emotion = moodRepository.getEmotionById(moodId)
        reduce {
            state.copy(moodEntity = emotion)
        }
    }
}