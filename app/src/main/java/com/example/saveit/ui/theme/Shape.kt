package com.example.saveit.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),   // Small items
    medium = RoundedCornerShape(16.dp), // Transaction Cards, Budget Cards
    large = RoundedCornerShape(24.dp),  // Bottom Sheet top corners
    extraLarge = RoundedCornerShape(50.dp) // "Create New Budget" Button (Capsule)
)