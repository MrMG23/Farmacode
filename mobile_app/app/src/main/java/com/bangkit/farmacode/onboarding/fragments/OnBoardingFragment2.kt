package com.bangkit.farmacode.onboarding.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.farmacode.R
import com.bangkit.farmacode.databinding.FragmentOnBoarding2Binding
import com.bangkit.farmacode.scanner.ScannerActivity

class OnBoardingFragment2 : Fragment() {

    private var _binding: FragmentOnBoarding2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOnBoarding2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.boarding_view_pager)

        // Skip
        binding.ob2Skip.setOnClickListener {
            // Set sharedPreferences
            onBoardingFinished()

            // Navigate to ScannerActivity
            val intent = Intent(activity, ScannerActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // Next
        binding.ob2Next.setOnClickListener {
            viewPager?.currentItem = 2
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onBoardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

}