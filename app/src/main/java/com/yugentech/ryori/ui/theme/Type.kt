package com.yugentech.ryori.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yugentech.ryori.R

val ProductSansFontFamily = FontFamily(
    Font(R.font.product_sans_regular, FontWeight.Normal),
    Font(R.font.product_sans_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = ProductSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = ProductSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ProductSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)