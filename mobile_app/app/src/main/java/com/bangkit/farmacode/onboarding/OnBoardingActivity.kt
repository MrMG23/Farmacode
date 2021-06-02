package com.bangkit.farmacode.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.farmacode.databinding.ActivityOnBoardingBinding
import com.bangkit.farmacode.onboarding.fragments.OnBoardingFragment1
import com.bangkit.farmacode.onboarding.fragments.OnBoardingFragment2
import com.bangkit.farmacode.onboarding.fragments.OnBoardingFragment3

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            hide()
        }

        // List fragment that will show in onBoarding
        val fragmentList = arrayListOf(
            OnBoardingFragment1(),
            OnBoardingFragment2(),
            OnBoardingFragment3()
        )

        val adapter = OnBoardingAdapter(fragmentList, supportFragmentManager, lifecycle)

        binding.boardingViewPager.adapter = adapter
    }
}