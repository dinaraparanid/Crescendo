package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun SaveButton(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.colorScheme

    Button(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.onBackground
        ),
        onClick = {
            // TODO: start trimming
        }
    ) {
        Text(
            text = stringResource(R.string.save),
            color = colors.inverseSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = textModifier
        )
    }
}