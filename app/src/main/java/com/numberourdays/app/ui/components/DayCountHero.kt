package com.numberourdays.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.ui.theme.Accent
import com.numberourdays.app.ui.theme.N700
import com.numberourdays.app.ui.theme.NodDisplayNumber

/**
 * Big day-count with optical centering of the digits: a transparent
 * mirror of the suffix balances the row so the number's center is the
 * visual center; the unit/rest sit to the right.
 */
@Composable
fun DayCountHero(
    value: String,
    suffix: String,
    modifier: Modifier = Modifier,
    numberStyle: TextStyle = NodDisplayNumber,
    suffixStyle: TextStyle = TextStyle(fontSize = 14.sp),
    suffixBottomPadding: Int = 8,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (suffix.isNotEmpty()) {
            Text(
                text = suffix,
                style = suffixStyle,
                color = Color.Transparent,
                modifier = Modifier.padding(bottom = suffixBottomPadding.dp),
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = value,
            style = numberStyle,
            color = Accent,
            fontWeight = FontWeight.Bold,
        )
        if (suffix.isNotEmpty()) {
            Spacer(Modifier.width(4.dp))
            Text(
                text = suffix,
                style = suffixStyle,
                color = N700,
                modifier = Modifier.padding(bottom = suffixBottomPadding.dp),
            )
        }
    }
}
