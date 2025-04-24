package com.stewardapostol.weatherapp.view.fragment

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.stewardapostol.weatherapp.BuildConfig
import com.stewardapostol.weatherapp.R
import com.stewardapostol.weatherapp.databinding.WeatherPagerLayoutBinding
import com.stewardapostol.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class WeatherFragment : BaseFragment<WeatherPagerLayoutBinding>() {

    private lateinit var viewModel: WeatherViewModel
    private val fragments by lazy { listOf(CurrentWeatherFragment(), WeatherForecastFragment()) }
    private val titles by lazy { listOf("Current Weather", "Weather List") }


    override fun getLayout(): Int = R.layout.weather_pager_layout

    override fun loadData() {
        super.loadData()
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        viewLifecycleOwner.lifecycleScope.launch {
            //Fetch Current and Forecast
            viewModel.fetchAllWeatherData(BuildConfig.API_KEY)
            //Use as this as Fallback
            viewModel.fetchForecastFromAssets()

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weatherData.collect { Log.i(TAG, "currentWeather: $it") }
                }
                launch {
                    viewModel.weatherForecastData.collect { Log.i(TAG, "weatherForeCast: $it") }
                }
            }
        }
        viewModel.locMessage.observe(viewLifecycleOwner){
            Toast.makeText(activity,it.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    override fun processViews() {
        super.processViews()

        val pagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

}