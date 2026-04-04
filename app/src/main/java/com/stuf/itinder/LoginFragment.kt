package com.stuf.itinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stuf.itinder.databinding.LoginFragmentBinding
import com.stuf.itinder.utils.NavKeys
import com.stuf.itinder.utils.hapticClick

class LoginFragment : Fragment() {

    private var _binding: LoginFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationIn()

        binding.BackBtn.setOnClickListener {
            it.hapticClick()
            animationOut {
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        NavKeys.introSourceKey,
                        NavKeys.IntroSource.Login.source)
                findNavController().navigateUp()
            }
        }

        binding.LoginButton.setOnClickListener {
            it.hapticClick()
            startActivity(MainScreenActivity.createIntent(requireContext()))
        }
    }

    private fun animationIn() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val title = binding.Title
        val form = binding.Form

        val buttons = binding.ButtonGroup

        buttons.alpha = 0.0f

        title.translationX = screenWidth
        form.translationX = screenWidth

        title.animate()
            .translationX(0f)
            .setDuration(SLIDE_DURATION)
            .start()

        form.animate()
            .setStartDelay(SLIDE_SHIFT)
            .translationX(0f)
            .setDuration(SLIDE_DURATION)
            .start()

        buttons.animate()
            .alpha(1.0f)
            .setDuration(FADE_DURATION)
            .start()
    }

    private fun animationOut(onEnd: () -> Unit) {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val title = binding.Title
        val form = binding.Form

        val buttons = binding.ButtonGroup

        form.animate()
            .translationX(screenWidth)
            .setDuration(SLIDE_DURATION)
            .start()

        title.animate()
            .setStartDelay(SLIDE_SHIFT)
            .translationX(screenWidth)
            .setDuration(SLIDE_DURATION)
            .start()

        buttons.animate()
            .alpha(0.0f)
            .setDuration(FADE_DURATION)
            .withEndAction(onEnd)
            .start()
    }

    companion object {
        const val SLIDE_DURATION = 500L
        const val SLIDE_SHIFT = 100L

        const val FADE_DURATION = 500L
    }
}
