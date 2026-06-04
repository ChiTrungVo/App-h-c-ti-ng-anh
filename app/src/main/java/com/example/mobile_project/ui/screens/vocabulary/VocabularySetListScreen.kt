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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.R
import com.example.mobile_project.feature.vocabulary.viewmodel.VocabularySetListViewModel
import com.example.mobile_project.feature.vocabulary.viewmodel.VocabularyTab
import com.example.mobile_project.ui.components.EmptyStateView
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.components.VocabularySetCard
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun VocabularySetListScreen(
    onSetClick: (String) -> Unit,
    onAddClick: () -> Unit,
    viewModel: VocabularySetListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Tải lại khi quay lại màn hình
    LaunchedEffect(Unit) {
        viewModel.loadSets()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (uiState.errorMessage != null && uiState.filteredSets.isEmpty()) {
            // Hiển thị lỗi + nút retry
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.errorMessage ?: "Đã xảy ra lỗi.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(16.dp))
                PrimaryButton("Thử lại", onClick = { viewModel.loadSets() })
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("Từ vựng", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "Quản lý bộ từ và tiến độ học",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))

                    // ── Tab Row ──────────────────────────────────
                    PrimaryTabRow(
                        selectedTabIndex = if (uiState.selectedTab == VocabularyTab.Mine) 0 else 1
                    ) {
                        Tab(
                            selected = uiState.selectedTab == VocabularyTab.Mine,
                            onClick = { viewModel.onTabSelected(VocabularyTab.Mine) },
                            text = { Text("Của tôi") }
                        )
                        Tab(
                            selected = uiState.selectedTab == VocabularyTab.Discover,
                            onClick = { viewModel.onTabSelected(VocabularyTab.Discover) },
                            text = { Text("Khám phá") }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // ── Tab: CỦA TÔI ────────────────────────────────
                if (uiState.selectedTab == VocabularyTab.Mine) {
                    item {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
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
                            uiState.allTags.forEach { tag ->
                                AssistChip(
                                    onClick = { viewModel.onTagSelected(tag) },
                                    label = { Text(tag) }
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        PrimaryButton("Tạo bộ từ", onClick = onAddClick)
                        Spacer(Modifier.height(12.dp))
                    }

                    items(uiState.filteredSets, key = { it.setId }) { set ->
                        VocabularySetCard(
                            vocabularySet = set,
                            onClick = { onSetClick(set.setId) }
                        )
                    }

                    if (uiState.filteredSets.isEmpty()) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            EmptyStateView(
                                title = "Chưa có bộ từ phù hợp",
                                message = "Tạo bộ từ theo chủ đề IELTS, TOEIC, Business hoặc Travel."
                            )
                        }
                    }
                }

                // ── Tab: KHÁM PHÁ ───────────────────────────────
                if (uiState.selectedTab == VocabularyTab.Discover) {
                    if (uiState.isLoadingPublic) {
                        item {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else if (uiState.publicSets.isEmpty()) {
                        item {
                            Spacer(Modifier.height(32.dp))
                            EmptyStateView(
                                title = "Chưa có bộ từ công khai",
                                message = "Hãy tạo bộ từ và bật chế độ công khai để chia sẻ với cộng đồng."
                            )
                        }
                    } else {
                        items(uiState.publicSets, key = { "public_${it.setId}" }) { set ->
                            Column {
                                VocabularySetCard(
                                    vocabularySet = set,
                                    onClick = { } // Không navigate, chỉ để xem
                                )
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    SecondaryButton(
                                        text = if (uiState.forkLoadingSetId == set.setId)
                                            "Đang sao chép..."
                                        else "Sao chép",
                                        onClick = { viewModel.forkSet(set.setId) },
                                        enabled = uiState.forkLoadingSetId == null
                                    )
                                }
                            }
                        }
                    }
                }

                // Spacer bottom cho FAB
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
            Image(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "Thêm bộ từ",
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VocabularySetListScreenPreview() {
    Mobile_projectTheme {
        VocabularySetListScreen(
            onSetClick = {},
            onAddClick = {}
        )
    }
}
