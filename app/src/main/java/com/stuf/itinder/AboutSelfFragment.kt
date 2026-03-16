package com.stuf.itinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stuf.itinder.components.chipgroup.ChipGroup
import com.stuf.itinder.databinding.AboutSelfFragmentBinding
import com.stuf.itinder.utils.hapticClick

class AboutSelfFragment : Fragment() {

    private var _binding: AboutSelfFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AboutSelfFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val testChips = mapOf<String, String>(
        "0" to "Very Very Very Very loooooooooooooooooooooooooooooooong chip",
        "1" to "python",
        "2" to "Django",
        "3" to "REST",
        "4" to "Swift",
        "5" to "Obj-C",
        "6" to "React JS",
        "7" to "Kotlin",
        "8" to "Git",
        "9" to "Unity",
        "10" to ".NET",
        "11" to "SQL",
        "12" to "Clean Architecture",
        "13" to "UML",
        "14" to ""
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.PlaceholderBtn.setOnClickListener {
            it.hapticClick()
            val context = requireContext()
            val intent = Intent(context, MainScreenActivity::class.java)
            startActivity(intent)
        }

        val chips : ChipGroup = binding.ChipGroup

        chips.setChips(testChips)

        chips.onTagClick = { tagId, isSelected ->
            if (isSelected) {
                chips.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
            Log.i("CHIP_GROUP", "Tag $tagId got ${!isSelected} -> $isSelected")
        }

        chips.onSelectionChange = {chips ->
            Log.i("CHIP_GROUP", "SELECTED: ${chips.joinToString()}")
        }

        chips.onSelectionLimitReached = { limit ->
            Log.i("CHIP_GROUP", "Reached selection limit (${limit})")
        }
    }
}
