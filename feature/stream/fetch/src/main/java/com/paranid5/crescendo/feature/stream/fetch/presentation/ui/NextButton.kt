package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R

@Composable
internal fun NextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = UrlManagerButton(
    modifier = modifier,
    title = stringResource(R.string.next),
    icon = ImageVector.vectorResource(R.drawable.ic_next),
    onClick = onClick,
)
