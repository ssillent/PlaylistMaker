package com.example.playlistmaker.ui.make_playlist.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.CustomToastBinding
import com.example.playlistmaker.databinding.MakePlaylistFragmentBinding
import com.example.playlistmaker.ui.make_playlist.view_model.MakePlaylistState
import com.example.playlistmaker.ui.make_playlist.view_model.MakePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class MakePlaylistFragment : Fragment() {

    protected var _binding: MakePlaylistFragmentBinding? = null
    protected val binding get() = _binding!!

    protected open val viewModel: MakePlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            binding.choosePictureImage.setPadding(0, 0, 0, 0)
            binding.choosePictureImage.setImageURI(it)
            val savedPath = saveImageToPrivateStorage(it)
            viewModel.onImageSaved(savedPath, it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MakePlaylistFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.MakePlaylistBackButton.setOnClickListener{
            viewModel.onBackPressed()
        }

        binding.choosePictureImage.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistNameEditText.addTextChangedListener { text ->
            viewModel.onNameChanged(text.toString())
        }

        binding.playlistDescriptionEditText.addTextChangedListener { text ->
            viewModel.onDescriptionChanged(text.toString())
        }

        binding.createPlaylistButton.setOnClickListener {
            viewModel.onCreatePlaylist()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            viewModel.onBackPressed()
        }
    }

    protected open fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUIState(state)
        }
    }

    protected open fun updateUIState(state: MakePlaylistState) {
        binding.createPlaylistButton.isEnabled = state.isNameValid && !state.isSaving

        if (state.showExitDialog) {
            showExitDialog()
        }

        state.path?.let { path ->
            val file = File(path)
            if (file.exists()) {
                val pxSize = dpToPx(8f, requireContext())

                Glide.with(this)
                    .load(file)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(pxSize))
                    .into(binding.choosePictureImage)
            } else {
                binding.choosePictureImage.setImageResource(R.drawable.placeholder)
            }
        }

        state.toastMessageResId?.let { resId ->
            val message = if (state.toastArg != null) {
                getString(resId, state.toastArg)
            } else {
                getString(resId)
            }
            showCustomToast(message)
            viewModel.clearToast()
        }

        if (state.shouldNavigateBack) {
            parentFragmentManager.popBackStack()
            viewModel.onNavigationComplete()
        }
    }

    protected fun saveImageToPrivateStorage(uri: Uri): String? {
        return try {
            val filePath = File(requireActivity().filesDir, "playlist_image")
            if (!filePath.exists()) {
                filePath.mkdirs()
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "playlist_image_$timeStamp.jpg"
            val file = File(filePath, fileName)

            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

            outputStream.close()
            inputStream?.close()

            file.absolutePath
        } catch (e: Exception){
            null
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.end_create)
            .setMessage(R.string.lost_data)
            .setPositiveButton(R.string.end) { dialog, _ ->
                viewModel.onExitConfirmed()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                viewModel.onExitCancelled()
                dialog.dismiss()
            }
            .show()
    }

    private fun showCustomToast(message: String) {
        val toastBinding = CustomToastBinding.inflate(layoutInflater, binding.root, false)
        toastBinding.toastText.text = message

        Toast(requireContext()).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 16)
            view = toastBinding.root
            show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}