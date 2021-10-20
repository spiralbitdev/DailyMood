package spiral.bit.dev.dailymood.ui.feature.registration.models.mvi

import spiral.bit.dev.dailymood.ui.base.StateMarker
import spiral.bit.dev.dailymood.ui.feature.registration.models.RegScreenState

data class RegState(
    val regScreenState: RegScreenState
) : StateMarker