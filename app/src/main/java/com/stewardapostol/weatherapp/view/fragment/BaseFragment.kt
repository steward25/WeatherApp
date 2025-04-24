package com.stewardapostol.weatherapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<F : ViewDataBinding?> : Fragment() {

    val TAG = this::class.java.simpleName

    private var _binding : F?=null
    val binding : F get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "@onAttach")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        Log.d(TAG, "@onCreateView")
        return binding?.root

    }
    override fun onStart() {
        super.onStart()
        loadData()
        Log.d(TAG, "@onStart")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "@onViewCreated")
        processViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "@onDestroy")
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "@onDetach")
        _binding = null
    }

    abstract fun getLayout() : Int
    open fun loadData(){}
    open fun processViews(){}
}