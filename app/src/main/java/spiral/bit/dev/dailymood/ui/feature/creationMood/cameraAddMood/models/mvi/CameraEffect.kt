package spiral.bit.dev.dailymood.ui.feature.creationMood.cameraAddMood.models.mvi

import android.net.Uri
import androidx.annotation.StringRes

sealed class CameraEffect {
    data class NavigateToShowMoodRating(val smilingProbabilities: Float, val capturedSelfieUri: Uri) : CameraEffect()
    class Toast(@StringRes val msg: Int) : CameraEffect()
    class ExceptionHappened(val error: Throwable) : CameraEffect()
}