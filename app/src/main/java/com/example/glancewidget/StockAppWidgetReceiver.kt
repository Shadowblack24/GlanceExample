package com.example.glancewidget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.glanceexample.glance.PriceDataRepo
import com.example.glanceexample.glance.StockAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = StockAppWidget()
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        CoroutineScope(Dispatchers.IO).launch {
            PriceDataRepo.update()
        }
    }
}