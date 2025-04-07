package com.example.snapcash.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.snapcash.R
import com.example.snapcash.data.OnboardingPage

@Composable
fun OnboardingScreen(onFinish: () -> Unit, navController: NavHostController) {
    val onboardingPages = listOf(
        OnboardingPage(R.drawable.onboarding_image1, "Abadikan struk, atur keuangan, tanpa ribet!"),
        OnboardingPage(R.drawable.onboarding_image2, "Analisa pemasukan dan pengeluaranmu dengan mudah, semua rapi tanpa usaha lebih.!"),
        OnboardingPage(R.drawable.onboarding_image1, "Dapatkan ringkasan keuanganmu tiap bulan!")
    )

    var currentPage by remember { mutableStateOf(0) }

    OnboardingContent(
        page = onboardingPages[currentPage],
        currentPage = currentPage,
        totalPages = onboardingPages.size,
        onNextClicked = {
            if (currentPage < onboardingPages.lastIndex) {
                currentPage++
            } else {
                onFinish()
                navController.navigate("signIn")
            }
        }
    )
}


@Composable
fun OnboardingContent(
    page: OnboardingPage,
    currentPage: Int,
    totalPages: Int,
    onNextClicked: () -> Unit
) {
    val backgroundColor = Color(0xFF0D0F13)
    val activeIndicatorColor = Color(0xFF3B82F6)
    val inactiveIndicatorColor = Color.Gray

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                repeat(totalPages) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(4.dp)
                            .width(if (index == currentPage) 40.dp else 20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (index == currentPage) activeIndicatorColor else inactiveIndicatorColor)
                    )
                }
            }

            // Image
            Image(
                painter = painterResource(id = page.imageRes),
                contentDescription = "Illustration",
                modifier = Modifier
                    .size(400.dp)
                    .padding(top = 48.dp)
            )

            // Text
            Text(
                text = page.text,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Button
            Button(
                onClick = onNextClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)

            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Centered Text
                    Text(
                        text = if (currentPage == totalPages - 1) "Finish" else "Next",
                        color = Color.Black,
                        style = MaterialTheme.typography.labelLarge,
                    )

                    // Icon aligned to end
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(50.dp))
                            .shadow(
                                elevation = 20.dp,
                                ambientColor = Color.Black,
                                spotColor = Color.Black
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            tint = Color.Black
                        )
                    }
                }

            }
        }
    }
}
