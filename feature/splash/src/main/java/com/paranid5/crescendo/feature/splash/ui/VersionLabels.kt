package com.paranid5.crescendo.feature.splash.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppText

@Composable
internal fun VersionLabels(modifier: Modifier = Modifier) =
    Column(modifier) {
        AppText(
            text = stringResource(R.string.app_name),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = typography.h.h2.copy(
                color = colors.selection.selected,
                fontWeight = FontWeight.W700,
                fontStyle = FontStyle.Italic,
            ),
        )

        Spacer(Modifier.height(dimensions.padding.extraSmall))

        AppText(
            text = stringResource(R.string.version),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = typography.regular.copy(
                color = colors.selection.selected,
                fontStyle = FontStyle.Italic,
            ),
        )
    }