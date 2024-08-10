package com.paranid5.crescendo.feature.play.main.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTextSelectionColors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.play.main.presentation.effect.SearchBarFocusEffect
import com.paranid5.crescendo.feature.play.main.view_model.PlayState
import com.paranid5.crescendo.feature.play.main.view_model.PlayUiIntent
import com.paranid5.crescendo.ui.utils.clickableWithRipple

private val IconSize = 24.dp

@Composable
internal fun PlaySearchBar(
    state: PlayState,
    onUiIntent: (PlayUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBarFocusEffect(state)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(dimensions.corners.small))
            .background(colors.background.searchBar),
    ) {
        SearchTextField(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.padding.small),
        )
    }
}

@Composable
private fun SearchTextField(
    state: PlayState,
    onUiIntent: (PlayUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = TextField(
    modifier = modifier,
    value = state.searchQuery,
    onValueChange = { onUiIntent(PlayUiIntent.UpdateSearchQuery(query = it)) },
    singleLine = true,
    textStyle = typography.body,
    colors = TextFieldDefaults.colors(
        focusedTextColor = colors.text.onSearchBar,
        unfocusedTextColor = colors.text.onSearchBar,
        disabledTextColor = colors.text.tertiriary,
        errorTextColor = colors.error,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        cursorColor = colors.selection.selected,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        selectionColors = AppTextSelectionColors,
    ),
    leadingIcon = { SearchIcon() },
    placeholder = { Placeholder() },
    trailingIcon = {
        AnimatedVisibility(visible = state.isSearchActive) {
            CancelIcon { onUiIntent(PlayUiIntent.SearchCancelClick) }
        }
    },
)

@Composable
private fun SearchIcon(modifier: Modifier = Modifier) =
    Icon(
        imageVector = Icons.Default.Search,
        contentDescription = null,
        tint = colors.icon.onSearchBar,
        modifier = modifier.size(IconSize),
    )

@Composable
private fun Placeholder(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.play_search_bar_placeholder),
        modifier = modifier,
        color = colors.text.tertiriary,
        style = typography.h.h3,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )

@Composable
private fun CancelIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Icon(
    imageVector = Icons.Default.Close,
    contentDescription = null,
    tint = colors.icon.onSearchBar,
    modifier = modifier
        .size(IconSize)
        .clickableWithRipple(onClick = onClick),
)
