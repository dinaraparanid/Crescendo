package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppExpandableCard
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun HowToUseCard(modifier: Modifier = Modifier) {
    val steps = rememberSteps()

    AppExpandableCard(
        modifier = modifier,
        title = stringResource(R.string.stream_fetch_url_how_to_use_title),
    ) {
        steps.forEachIndexed { index, text ->
            Step(number = index + 1, text = text, modifier = Modifier.fillMaxWidth())
            if (index != steps.lastIndex) Spacer(Modifier.height(dimensions.padding.medium))
        }
    }
}

@Composable
private fun Step(
    number: Int,
    text: String,
    modifier: Modifier = Modifier,
) = MarkdownText(
    color = colors.text.onHighContrast,
    style = typography.regular,
    modifier = modifier,
    markdown = "$number. $text",
)

@Composable
private fun rememberSteps(): ImmutableList<String> {
    val step1 = stringResource(R.string.stream_fetch_url_how_to_use_step_1)
    val step2 = stringResource(R.string.stream_fetch_url_how_to_use_step_2)
    val step3 = stringResource(R.string.stream_fetch_url_how_to_use_step_3)
    val step4 = stringResource(R.string.stream_fetch_url_how_to_use_step_4)
    val step5 = stringResource(R.string.stream_fetch_url_how_to_use_step_5)
    val step6 = stringResource(R.string.stream_fetch_url_how_to_use_step_6)
    val step7 = stringResource(R.string.stream_fetch_url_how_to_use_step_7)

    return remember(step1, step2, step3, step4, step5, step6, step7) {
        persistentListOf(step1, step2, step3, step4, step5, step6, step7)
    }
}
