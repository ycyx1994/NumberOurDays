package com.numberourdays.app.ui.holiday

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.R
import com.numberourdays.app.data.LegalHolidays
import com.numberourdays.app.domain.DateCalculator
import com.numberourdays.app.domain.HolidayCalculator
import com.numberourdays.app.domain.HolidayListRow
import com.numberourdays.app.ui.components.NodTopBar
import com.numberourdays.app.ui.components.rememberCurrentDate
import com.numberourdays.app.ui.theme.Accent
import com.numberourdays.app.ui.theme.HolidayEdge
import com.numberourdays.app.ui.theme.HolidayTint
import com.numberourdays.app.ui.theme.N0
import com.numberourdays.app.ui.theme.N100
import com.numberourdays.app.ui.theme.N300
import com.numberourdays.app.ui.theme.N50
import com.numberourdays.app.ui.theme.N500
import com.numberourdays.app.ui.theme.N700
import com.numberourdays.app.ui.theme.N900
import com.numberourdays.app.ui.theme.NodCardShape
import com.numberourdays.app.ui.theme.NodDisplayCount

@Composable
fun HolidayListScreen(onBack: () -> Unit) {
    val today = rememberCurrentDate()
    val rows = remember(today) {
        HolidayCalculator.listRows(LegalHolidays.periods(), today)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            NodTopBar(
                title = stringResource(R.string.holiday_list_title),
                onBack = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                text = stringResource(R.string.holiday_year_title, today.year),
                color = N500,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(rows, key = { it.period.name + it.period.start }) { row ->
                    HolidayRowCard(row = row)
                }
            }
        }
    }
}

@Composable
private fun HolidayRowCard(row: HolidayListRow) {
    val past = row.past
    val ongoing = row.ongoing
    val titleColor = when {
        past -> N500
        ongoing -> N900
        else -> N900
    }
    val surface = when {
        ongoing -> HolidayTint
        past -> N50
        else -> N0
    }
    val edge = when {
        ongoing -> HolidayEdge
        past -> N100
        else -> N100
    }
    val statusText = when {
        past -> stringResource(R.string.holiday_elapsed, row.daysPastEnd)
        ongoing -> stringResource(R.string.holiday_ongoing)
        else -> stringResource(R.string.holiday_countdown, row.daysUntilStart)
    }
    val statusColor = when {
        past -> N300
        ongoing -> Accent
        else -> Accent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(NodCardShape)
            .background(surface)
            .border(1.dp, edge, NodCardShape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.period.name,
                color = titleColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = DateCalculator.format(row.period.start) +
                    " ~ " +
                    DateCalculator.format(row.period.end) +
                    " · " +
                    stringResource(R.string.holiday_vacation_days, row.vacationDays),
                color = if (past) N300 else N500,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = statusText,
            style = NodDisplayCount,
            color = statusColor,
        )
    }
}
