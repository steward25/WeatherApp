package com.stewardapostol.weatherapp.view.fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stewardapostol.weatherapp.R
import com.stewardapostol.weatherapp.data.model.CurrentWeatherEntity
import com.stewardapostol.weatherapp.databinding.CurrentWeatherLayoutBinding
import com.stewardapostol.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class CurrentWeatherFragment : BaseFragment<CurrentWeatherLayoutBinding>() {

    private lateinit var viewModel: WeatherViewModel

    override fun getLayout(): Int = R.layout.current_weather_layout

    override fun loadData() {
        super.loadData()
        viewModel = ViewModelProvider(requireParentFragment())[WeatherViewModel::class.java]
    }

    override fun processViews() {
        super.processViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weatherData.collect {
                        binding.weather = it as? CurrentWeatherEntity
                    }
                }
            }
        }
    }

}