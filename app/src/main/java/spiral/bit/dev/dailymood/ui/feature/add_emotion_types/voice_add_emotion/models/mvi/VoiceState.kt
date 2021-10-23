package spiral.bit.dev.dailymood.ui.feature.add_emotion_types.voice_add_emotion.models.mvi

import spiral.bit.dev.dailymood.data.emotion.MoodEntity
import spiral.bit.dev.dailymood.ui.base.StateMarker

data class VoiceState(
    val question: String,
    val questionCount: Int,
    val isRecorded: Boolean,
    val resultMoodEntities: ArrayList<MoodEntity>
) : StateMarker