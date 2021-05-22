package com.example.coupangeats.src.main.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.coupangeats.R
import com.example.coupangeats.databinding.FragmentAdvencedSearchBinding
import com.example.coupangeats.src.main.MainActivity
import com.softsquared.template.kotlin.config.BaseFragment

class AdvencedSearchFragment() : BaseFragment<FragmentAdvencedSearchBinding>(FragmentAdvencedSearchBinding::bind, R.layout.fragment_advenced_search) {
    private var mSearchAble = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}