package com.paranid5.crescendo.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    Surface(
        modifier = modifier,
        color = colors.background
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center)) {
                Image(
                    painter = painterResource(id = R.drawable.crescendo),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(140.dp)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = stringResource(id = R.string.app_name),
                    color = colors.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = stringResource(id = R.string.version),
                    color = colors.primary,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}