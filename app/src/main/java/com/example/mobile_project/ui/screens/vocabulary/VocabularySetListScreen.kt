package com.example.mobile_project.ui.screens.vocabulary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.data.sample.VocabularyDemoStore
import com.example.mobile_project.ui.components.EmptyStateView
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.VocabularySetCard

@Composable
fun VocabularySetListScreen(
    onSetClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Tất cả") }
    val allSets = VocabularyDemoStore.vocabularySets
    val tags = listOf("Tất cả") + allSets.flatMap { it.tags }.distinct().sorted()
    val filteredSets = allSets.filter { set ->
        val matchesSearch = searchQuery.isBlank() ||
            set.title.contains(searchQuery, ignoreCase = true) ||
            set.description.contains(searchQuery, ignoreCase = true) ||
            set.tags.any { it.contains(searchQuery, ignoreCase = true) }
        val matchesTag = selectedTag == "Tất cả" || selectedTag in set.tags
        matchesSearch && matchesTag
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Từ vựng", style = MaterialTheme.typography.headlineLarge)
                Text("Quản lý bộ từ và tiến độ học", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Tìm bộ từ") },
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { tag ->
                        AssistChip(
                            onClick = { selectedTag = tag },
                            label = { Text(tag) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                PrimaryButton("Tạo bộ từ", onClick = onAddClick)
                Spacer(Modifier.height(12.dp))
                Text("Bộ từ được lưu theo collection vocabulary_sets; mỗi từ bên trong trỏ về setId của bộ này.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(filteredSets, key = { it.setId }) { set ->
                VocabularySetCard(vocabularySet = set, onClick = { onSetClick(set.setId) })
            }
            if (filteredSets.isEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    EmptyStateView("Chưa có bộ từ phù hợp", "Tạo bộ từ theo chủ đề IELTS, TOEIC, Business hoặc Travel.")
                    Spacer(Modifier.height(132.dp))
                }
            } else {
                item {
                    Spacer(Modifier.height(132.dp))
                }
            }
        }
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 118.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Image(painter = painterResource(R.drawable.ic_add), contentDescription = "Thêm bộ từ", modifier = Modifier.size(28.dp), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary))
        }
    }
}
