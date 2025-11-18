package com.example.glanceexample.glance

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.Locale

class StockAppWidget : GlanceAppWidget() {

    private var job: Job? = null

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Инициализация корутины
        if (job == null) {
            job = startUpdateJob(
                Duration.ofSeconds(20).toMillis(),
                context
            )
        }

        provideContent {
            GlanceTheme {
                GlanceContent() // вызываем GlanceContent
            }
        }
    }

    private fun startUpdateJob(timeInterval: Long, context: Context): Job {
        // Создание корутины с использованием CoroutineScope
        return CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                PriceDataRepo.update()
                StockAppWidget().updateAll(context) // обновляем виджет
                delay(timeInterval) // приостанавливаем корутину
            }
        }
    }

    // Вынесенные функции
    @Composable
    fun StockDisplay(stateCount: Float) {
        val color = if (PriceDataRepo.change > 0) {
            GlanceTheme.colors.primary
        } else {
            GlanceTheme.colors.error
        }

        val textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(PriceDataRepo.ticker, style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        ))

        Text(
            text = String.format(Locale.getDefault(), "%.2f", stateCount),
            style = textStyle
        )

        Text(
            text = "${PriceDataRepo.change} %",
            style = textStyle
        )
    }

    @Composable
    fun GlanceContent() {
        val stateCount by PriceDataRepo.currentPrice.collectAsState()
        Small(stateCount) // вызываем Small здесь
    }

    @Composable
    fun Small(stateCount: Float) {
        Column(modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(8.dp)) {
            StockDisplay(stateCount)
        }
    }
}
