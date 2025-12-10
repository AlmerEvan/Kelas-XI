package com.example.newsapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.newsapp.viewmodel.NewsViewModel

@Composable
fun CategoriesBar(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "General",
        "Business",
        "Entertainment",
        "Health",
        "Science",
        "Sports",
        "Technology"
    )
    val selectedCategory = viewModel.selectedCategory.value

    LazyRow(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory.lowercase() == category.lowercase()

            AssistChip(
                onClick = { viewModel.onCategorySelected(category) },
                label = { Text(category) },
                modifier = Modifier.padding(horizontal = 4.dp),
                colors = if (isSelected) {
                    AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF1976D2),
                        labelColor = Color.White
                    )
                } else {
                    AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFF0F0F0),
                        labelColor = Color.Black
                    )
                }
            )
        }
    }
}
