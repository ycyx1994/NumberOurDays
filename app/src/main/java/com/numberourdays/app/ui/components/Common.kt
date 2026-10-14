package com.numberourdays.app.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.numberourdays.app.R
import com.numberourdays.app.domain.DateCalculator
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.delay

@Composable
fun rememberCurrentDate(): LocalDate {
    var currentDate by remember { mutableStateOf(DateCalculator.today()) }
    LaunchedEffect(Unit) {
        while (true) {
            val millisToNextMinute = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(millisToNextMinute.coerceIn(1L, 60_000L))
            val nextDate = DateCalculator.today()
            if (nextDate != currentDate) currentDate = nextDate
        }
    }
    return currentDate
}

/**
 * Material3 官方 [TopAppBar] 的薄封装：统一纸色容器与标题字重，控件本体仍是官方组件。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}
