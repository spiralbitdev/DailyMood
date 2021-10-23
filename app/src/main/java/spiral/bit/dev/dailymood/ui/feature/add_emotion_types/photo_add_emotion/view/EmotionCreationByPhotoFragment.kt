package spiral.bit.dev.dailymood.ui.feature.add_emotion_types.photo_add_emotion.view

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.listeners.addClickListener
import dagger.hilt.android.AndroidEntryPoint
import spiral.bit.dev.dailymood.R
import spiral.bit.dev.dailymood.databinding.FragmentEmotionCreationByPhotoBinding
import spiral.bit.dev.dailymood.databinding.ItemAddByPhotoTypeBinding
import spiral.bit.dev.dailymood.ui.base.*
import spiral.bit.dev.dailymood.ui.base.Logger
import spiral.bit.dev.dailymood.ui.common.resolvers.FaceMoodResolver
import spiral.bit.dev.dailymood.ui.feature.add_emotion_types.photo_add_emotion.models.PhotoTypeItem
import spiral.bit.dev.dailymood.ui.feature.add_emotion_types.photo_add_emotion.realtime.models.mvi.RealtimeEffect
import spiral.bit.dev.dailymood.ui.feature.add_emotion_types.photo_add_emotion.realtime.models.mvi.RealtimeState
import spiral.bit.dev.dailymood.ui.feature.add_emotion_types.photo_add_emotion.realtime.view.RealtimeViewModel

@AndroidEntryPoint
class EmotionCreationByPhotoFragment :
    BaseFragment<RealtimeState, RealtimeEffect, FragmentEmotionCreationByPhotoBinding>(
        FragmentEmotionCreationByPhotoBinding::inflate
    ) {

    private val faceMoodResolver = FaceMoodResolver()
    override val viewModel: RealtimeViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private val cameraPermission = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) setUpCamera()
    }
    private val galleryPermission = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) openGallery()
    }

    private fun openGallery() = binding {

    }

    private val itemAdapter = ItemAdapter<PhotoTypeItem>()
    private val photoTypesAdapter = FastAdapter.with(itemAdapter).apply {
        addClickListener<ItemAddByPhotoTypeBinding, PhotoTypeItem>({
            it.iconPhotoFrameLayout
        }) { _, _, _, item ->
            viewModel.onPhotoTypeClicked(item.model.id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (handlePermissions()) setUpCamera()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = binding {
        addByPhotoTypesRecyclerView.apply {
            setHasFixedSize(true)
            adapter = photoTypesAdapter
        }
    }

    private fun handlePermissions() =
        if (requireContext().hasPermissions(Manifest.permission.CAMERA)) {
            true
        } else {
            permission.launch(Manifest.permission.CAMERA)
            false
        }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setUpCamera() = binding {
        val cameraExecutor = ContextCompat.getMainExecutor(requireContext())
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageFaceEmotionAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, { imageProxy ->
                        imageProxy.image?.let { mediaImage ->
                            InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            ).run {
                                viewModel.detectFace(this) { imageProxy.close() }
                            }
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            runCatching {
                cameraProvider.apply {
                    unbindAll()
                    bindToLifecycle(
                        viewLifecycleOwner,
                        cameraSelector,
                        preview,
                        imageFaceEmotionAnalyzer
                    )
                }
            }.onFailure { throwable ->
                Logger.logError(throwable)
            }
        }, cameraExecutor)
    }

    override fun renderState(state: RealtimeState) = binding {

    }

    override fun handleSideEffect(sideEffect: RealtimeEffect) = binding {
        when (sideEffect) {
            is RealtimeEffect.AddEmotionByRealtime -> {
                EmotionCreationByPhotoFragmentDirections.toEmotionCreationByRealtime().apply {
                    findNavController().navigate(this)
                }
            }
            is RealtimeEffect.AddEmotionByCamera -> {
                EmotionCreationByPhotoFragmentDirections.toEmotionCreationByCamera().apply {
                    findNavController().navigate(this)
                }
            }
            is RealtimeEffect.NavigateToMain -> {
                EmotionCreationByPhotoFragmentDirections.toMain().apply {
                    findNavController().navigate(this)
                }
            }
            is RealtimeEffect.Toast -> {
                root.toast(sideEffect.msg)
            }
            is RealtimeEffect.ExceptionHappened -> {
                Logger.logError(sideEffect.error)
            }
            is RealtimeEffect.AddEmotionByGallery -> {
                //gallery
            }
        }
    }
}