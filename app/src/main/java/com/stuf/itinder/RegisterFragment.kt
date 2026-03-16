package com.stuf.itinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stuf.itinder.databinding.RegisterFragmentBinding
import com.stuf.itinder.utils.NavKeys
import com.stuf.itinder.utils.hapticClick

class RegisterFragment : Fragment() {

    private var _binding: RegisterFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
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
                        NavKeys.IntroSource.Register.source)
                findNavController().navigateUp()
            }
        }

        binding.RegisterButton.setOnClickListener {
            it.hapticClick()
            animationNext {
                findNavController().navigate(R.id.action_RegisterFragment_to_AboutSelfFragment)
            }
        }
    }

    private fun animationIn() {
        AnimationsTrackerHolder.instance.onAnimationStart()

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
            .withEndAction {
                AnimationsTrackerHolder.instance.onAnimationEnd()
            }
            .start()
    }

    private fun animationNext(onEnd: () -> Unit) {
        AnimationsTrackerHolder.instance.onAnimationStart()

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val title = binding.Title
        val form = binding.Form

        val buttons = binding.ButtonGroup

        title.animate()
            .translationX(-screenWidth)
            .setDuration(SLIDE_DURATION)
            .start()

        form.animate()
            .setStartDelay(SLIDE_SHIFT)
            .translationX(-screenWidth)
            .setDuration(SLIDE_DURATION)
            .start()

        buttons.animate()
            .alpha(0.0f)
            .setDuration(FADE_DURATION)
            .withEndAction {
                try {
                    onEnd()
                } finally {
                    AnimationsTrackerHolder.instance.onAnimationEnd()
                }
            }
            .start()
    }

    private fun animationOut(onEnd: () -> Unit) {
        AnimationsTrackerHolder.instance.onAnimationStart()

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
            .withEndAction {
                try {
                    onEnd()
                } finally {
                    AnimationsTrackerHolder.instance.onAnimationEnd()
                }
            }
            .start()
    }

    companion object {
        const val SLIDE_DURATION = 500L
        const val SLIDE_SHIFT = 100L

        const val FADE_DURATION = 500L
    }
}
