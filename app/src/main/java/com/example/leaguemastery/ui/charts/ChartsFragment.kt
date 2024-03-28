package com.example.leaguemastery.ui.charts

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.leaguemastery.API.Update
import com.example.leaguemastery.Cache
import com.example.leaguemastery.databinding.FragmentChartsBinding
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson


class ChartsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var screensize: Display

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val context = root.context
        val wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        screensize = wm.defaultDisplay
        val webView = binding.webview
        if(!Cache.updating){
            webView.settings.javaScriptEnabled = true

            val jsonData = convertListToJson(Cache.actualSummonerChampion)
            val script = "createTreeMap($jsonData);"

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    webView.evaluateJavascript(script, null)
                }
            }
            webView.loadUrl("file:///android_asset/treemap.html")
        } else {
            webView.loadUrl("about:blank")
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val webView = binding.webview
        if(webView != null){
            webView.loadUrl("about:blank")
        }
        _binding = null
    }

    fun convertListToJson(champions: List<ChampionSummonerLanguage>): String {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        (activity?.windowManager?.defaultDisplay)?.getMetrics(displayMetrics)
        val screenWidthInPixels = displayMetrics.widthPixels / 3 + 50
        val screenHeightInPixels = displayMetrics.heightPixels / 3 - 30

        val gson = Gson()
        var res = gson.toJson(champions)
        res = "{screen:{\"height\":$screenHeightInPixels,\"width\": $screenWidthInPixels},\"data\":$res}"
        return res
    }
}