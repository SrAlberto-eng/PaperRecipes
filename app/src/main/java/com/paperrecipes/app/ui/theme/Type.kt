package com.paperrecipes.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val AppTypography = Typography(
    /*
       Display Small:
       Used for short, important text or numerals.
       Best suited for large screens or as a primary header on a landing page
       where visual impact is required.
    */
    displaySmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp,
    ),
    /*
       Headline Medium:
       High-emphasis text used to mark primary sections of a page.
       Ideal for top-level headers in content-heavy layouts.
    */
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.25).sp,
    ),
    /*
       Headline Small:
       Used for sub-sections or smaller headers.
       It provides clear hierarchy within a specific block of information.
    */
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    /*
       Title Large:
       The largest of the "title" roles.
       Typically used for Top App Bar titles, dialog titles,
       or prominent headers in lists and cards.
    */
    titleLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    /*
       Title Medium:
       Used for medium-emphasis titles, such as headers for
       specific card components or settings categories.
    */
    titleMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    /*
       Body Large:
       The default style for long-form text (paragraphs).
       Used for main content descriptions or article text.
    */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    /*
       Body Medium:
       Used for secondary body text or smaller descriptions
       where space is more constrained than Body Large.
    */
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
    ),
    /*
       Label Large:
       A call-to-action style. Primarily used for Button text,
       tabs, or functional components that require clear, concise text.
    */
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp,
    ),
    /*
       Label Medium:
       Used for small, functional text such as tag labels,
       input field captions, or subtle annotations.
    */
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
)