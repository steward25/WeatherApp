package com.stewardapostol.weatherapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding



abstract class BaseActivity<V : ViewDataBinding> : AppCompatActivity() {

    private var _binding: ViewDataBinding? = null
    val binding: ViewDataBinding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, activityLayout())
        setContent()
        setFragment()
    }

    abstract fun activityLayout(): Int
    open fun setContent() {}
    open fun setFragment() {}


}