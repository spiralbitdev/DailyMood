package spiral.bit.dev.dailymood.ui.feature.creationMood.voiceAddMood.view

import android.Manifest
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import spiral.bit.dev.dailymood.R
import spiral.bit.dev.dailymood.databinding.FragmentMoodCreationByVoiceBinding
import spiral.bit.dev.dailymood.ui.base.BaseFragment
import spiral.bit.dev.dailymood.ui.base.binding
import spiral.bit.dev.dailymood.ui.base.extensions.hasPermissions
import spiral.bit.dev.dailymood.ui.base.extensions.toast
import spiral.bit.dev.dailymood.ui.feature.creationMood.voiceAddMood.models.mvi.VoiceEffect
import spiral.bit.dev.dailymood.ui.feature.creationMood.voiceAddMood.models.mvi.VoiceState

class VoiceAddEmotionFragment :
    BaseFragment<VoiceState, VoiceEffect, FragmentMoodCreationByVoiceBinding>(
        FragmentMoodCreationByVoiceBinding::inflate
    ) {

    override val viewModel: VoiceViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private val permissions =
        registerForActivityResult(RequestMultiplePermissions()) {
            if (checkPermissions()) {
                viewModel.onRecordClick()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClicks()
        setUpViews()
        initApi()
    }

    private fun setUpViews() = binding {
        voiceToolbar.titleTextView.text = getString(R.string.create_emotion_label)
    }

    private fun initApi() = viewModel.initVokaturiApi()

    private fun setUpClicks() = binding {
        recordBtn.setOnClickListener {
            if (checkPermissions()) {
                viewModel.onRecordClick()
            }
        }

        voiceToolbar.iconBackImageView.setOnClickListener {
            viewModel.navigateBack()
        }
    }

    private fun checkPermissions(): Boolean {
        return if (requireContext().hasPermissions(Manifest.permission.RECORD_AUDIO) &&
            requireContext().hasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            true
        } else {
            permissions.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("questionCount", viewModel.container.stateFlow.value.questionCount)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getInt("questionCount")?.let { questionCount ->
            viewModel.onRecordClick(questionCount)
        }
    }

    override fun handleSideEffect(sideEffect: VoiceEffect) = binding {
        when (sideEffect) {
            VoiceEffect.NavigateToMain -> {
                VoiceAddEmotionFragmentDirections.toMain().apply {
                    findNavController().navigate(this)
                }
            }
            VoiceEffect.TooQuiet -> {
                root.toast(R.string.too_quiet_toast)
            }
            VoiceEffect.NavigateBack -> {
                findNavController().popBackStack()
            }
        }
    }

    override fun renderState(state: VoiceState) = binding {
        questionTextView.text = state.question
        recordChronometer.apply {
            base = SystemClock.elapsedRealtime()
            isVisible = state.isRecorded
            if (state.isRecorded) start()
            else stop()
        }
        recordTextView.text = if (state.isRecorded) {
            getString(R.string.record_begin)
        } else {
            getString(R.string.tap_to_record_label)
        }
    }
}