package com.example.leaguemastery.ui.charts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.leaguemastery.R
import com.example.leaguemastery.databinding.FragmentChartsBinding
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson

class ChartsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val list:ArrayList<BarEntry> = ArrayList()

        list.add(BarEntry(100f,100f))
        list.add(BarEntry(101f,101f))
        list.add(BarEntry(102f,102f))
        list.add(BarEntry(103f,103f))
        list.add(BarEntry(104f,104f))

        val barDataSet = BarDataSet(list, "List")

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)

        barDataSet.valueTextColor = Color.WHITE
        val webView = binding.webview
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/treemap.html")




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun convertListToJson(champions: List<ChampionSummonerLanguage>): String {
        val gson = Gson()

        return gson.toJson(champions)
    }
}