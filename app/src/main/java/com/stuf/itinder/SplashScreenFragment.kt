package com.stuf.itinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stuf.itinder.databinding.SplashscreenBinding
import com.stuf.itinder.utils.NavKeys
import com.stuf.itinder.utils.hapticClick

class SplashScreenFragment : Fragment() {

    private var _binding: SplashscreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashscreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logo = binding.Logo
        logoAnimation(logo)
    }

    private fun logoAnimation(logo: ImageView) {
        AnimationsTrackerHolder.instance.onAnimationStart()

        logo.alpha = 0f

        logo.animate()
            .setStartDelay(LOGO_ANIMATION_START_DELAY)
            .alpha(1f)
            .setDuration(LOGO_FADEIN_DURATION)
            .withEndAction {
                try {
                    logo.hapticClick()
                    goToIntro()
                } finally {
                    AnimationsTrackerHolder.instance.onAnimationEnd()
                }
            }
            .start()
    }

    private fun goToIntro() {
        findNavController().navigate(
            R.id.action_SplashScreenFragment_to_SecondFragment,
            bundleOf(NavKeys.isFromSplashKey to true)
        )
    }

    companion object {
        const val LOGO_ANIMATION_START_DELAY = 300L
        const val LOGO_FADEIN_DURATION = 500L
    }
}
