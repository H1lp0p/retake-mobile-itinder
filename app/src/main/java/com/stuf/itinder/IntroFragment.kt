package com.stuf.itinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stuf.itinder.databinding.IntroFragmentBinding
import com.stuf.itinder.utils.NavKeys
import com.stuf.itinder.utils.hapticClick

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class IntroFragment : Fragment() {

    private var _binding: IntroFragmentBinding? = null
    private val binding get() = _binding!!

    private var hasPlayedInitialAnimation = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = IntroFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fromSplash = arguments?.getBoolean(NavKeys.isFromSplashKey) == true

        if (fromSplash && !hasPlayedInitialAnimation) {
            hasPlayedInitialAnimation = true
            initialAnimation()
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>(getString(R.string.navigation_intro_source_key))
            ?.observe(viewLifecycleOwner) {
                animationFromFragment()
            }

        binding.RegisterButton.setOnClickListener {
            it.hapticClick()
            animationOut {
                findNavController().navigate(R.id.RegisterFragment)
            }
        }

        binding.LoginButton.setOnClickListener {
            it.hapticClick()
            animationOut {
                findNavController().navigate(R.id.LoginFragment)
            }
        }
    }

    private fun animationOut(onEnd: () -> Unit) {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val logo = binding.Logo
        val text = binding.textView

        val bg = binding.ImageBg

        val buttons = binding.ButtonGroup

        logo.animate()
            .translationX(-screenWidth)
            .setDuration(SLIDE_DURATION)
            .start()

        text.animate()
            .translationX(-screenWidth)
            .setDuration(SLIDE_DURATION)
            .start()

        bg.animate()
            .alpha(0.0f)
            .setDuration(SLIDE_DURATION)
            .withEndAction(onEnd)
            .start()

        buttons.animate()
            .alpha(0.0f)
            .setDuration(SLIDE_DURATION)
            .start()
    }

    private fun animationFromFragment() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        val logo = binding.Logo
        val text = binding.textView

        val bg = binding.ImageBg

        val buttons = binding.ButtonGroup

        logo.translationX = -screenWidth
        text.translationX = -screenWidth
        buttons.alpha = 0.0f
        bg.alpha = 0.0f

        logo.animate()
            .translationX(0.0f)
            .setDuration(SLIDE_DURATION)
            .start()

        text.animate()
            .translationX(0.0f)
            .setDuration(SLIDE_DURATION)
            .start()

        bg.animate()
            .alpha(1.0f)
            .setDuration(SLIDE_DURATION)
            .start()

        buttons.animate()
            .alpha(1.0f)
            .setDuration(SLIDE_DURATION)
            .start()
    }
    private fun initialAnimation() {
        val root = binding.root
        val logo = binding.Logo
        val imageBg = binding.ImageBg
        val text = binding.textView
        val registerButton = binding.RegisterButton
        val loginButton = binding.LoginButton

        imageBg.alpha = 0f
        text.alpha = 0f

        registerButton.alpha = 0f
        loginButton.alpha = 0f
        registerButton.translationY = REGISTER_BUTTON_TRANSLATION
        loginButton.translationY = LOGIN_BUTTON_TRANSLATION

        root.doOnLayout {
            val rootCenterY = root.height / 2f
            val logoCenterY = logo.y + logo.height / 2f

            val offsetToCenter = rootCenterY - logoCenterY

            logo.translationY = offsetToCenter

            imageBg.animate()
                .alpha(1f)
                .setDuration(IMAGE_ALPHA_DURATION)
                .withEndAction {
                    logo.animate()
                        .translationY(0f)
                        .setDuration(LOGO_MOVE_DURATION)
                        .withEndAction {
                            text.animate()
                                .alpha(1f)
                                .setDuration(TEXT_FADE_DURATION)
                                .start()
                        }
                        .start()

                    registerButton.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(BUTTON_SLIDE_DURATION)
                        .start()

                    loginButton.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setStartDelay(LOGIN_BUTTON_START_DELAY)
                        .setDuration(BUTTON_SLIDE_DURATION)
                        .start()
                }
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val IMAGE_ALPHA_DURATION = 500L
        const val LOGO_MOVE_DURATION = 500L
        const val TEXT_FADE_DURATION = 300L
        const val BUTTON_SLIDE_DURATION = 400L

        const val LOGIN_BUTTON_START_DELAY = 100L
        const val REGISTER_BUTTON_TRANSLATION = 150f
        const val LOGIN_BUTTON_TRANSLATION = 150f

        const val SLIDE_DURATION = 500L
    }
}
