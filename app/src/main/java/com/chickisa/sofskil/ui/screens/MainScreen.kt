package com.chickisa.sofskil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chickisa.sofskil.data.model.Zone
import com.chickisa.sofskil.data.model.ZoneCategory
import com.chickisa.sofskil.ui.theme.*
import com.chickisa.sofskil.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToAddZone: () -> Unit,
    onNavigateToZoneDetail: (Long) -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val zones by viewModel.filteredZones.collectAsState()
    val cleanPercentage by viewModel.cleanZonesPercentage.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val showOnlyDirty by viewModel.showOnlyDirty.collectAsState()
    
    var showFilterMenu by remember { mutableStateOf(false) }
    var showBubbleAnimation by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(
                            onClick = { showBubbleAnimation = true }
                        )
                    ) {
                        Text(
                            "Chick Sanity",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (showBubbleAnimation) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "🫧 Clean as Egg! 🥚",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(2000)
                                showBubbleAnimation = false
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(Icons.Default.BarChart, "Statistics")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                    
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (showOnlyDirty) "Show All" else "Show Only Dirty") },
                            onClick = {
                                viewModel.toggleShowOnlyDirty()
                                showFilterMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    if (showOnlyDirty) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null
                                )
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("All Categories") },
                            onClick = {
                                viewModel.setFilterCategory(null)
                                showFilterMenu = false
                            },
                            leadingIcon = { Text("📋") }
                        )
                        ZoneCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text("${category.emoji} ${category.displayName}") },
                                onClick = {
                                    viewModel.setFilterCategory(category.name)
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddZone,
                icon = { Icon(Icons.Default.Add, "Add Zone") },
                text = { Text("Add Zone") },
                containerColor = SecondaryYellow,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CleanlinessProgressCard(cleanPercentage)
            }
            
            if (filterCategory != null || showOnlyDirty) {
                item {
                    FilterChip(
                        text = buildString {
                            if (showOnlyDirty) append("Dirty zones")
                            if (showOnlyDirty && filterCategory != null) append(" • ")
                            if (filterCategory != null) {
                                val category = ZoneCategory.entries.find { it.name == filterCategory }
                                append("${category?.emoji} ${category?.displayName}")
                            }
                        },
                        onDismiss = {
                            viewModel.setFilterCategory(null)
                            if (showOnlyDirty) viewModel.toggleShowOnlyDirty()
                        }
                    )
                }
            }
            
            if (zones.isEmpty()) {
                item {
                    EmptyStateCard(onAddZone = onNavigateToAddZone)
                }
            } else {
                items(zones, key = { it.id }) { zone ->
                    ZoneCard(
                        zone = zone,
                        onClick = { onNavigateToZoneDetail(zone.id) },
                        onMarkCleaned = { viewModel.markZoneAsCleaned(zone) }
                    )
                }
            }
        }
    }
}

@Composable
fun CleanlinessProgressCard(percentage: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "progress"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Farm Cleanliness",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$percentage%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        percentage >= 80 -> SuccessGreen
                        percentage >= 50 -> SecondaryYellow
                        else -> DangerRed
                    }
                )
            }
            
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(MaterialTheme.shapes.medium),
                color = when {
                    percentage >= 80 -> SuccessGreen
                    percentage >= 50 -> SecondaryYellow
                    else -> DangerRed
                },
                trackColor = LightGreen,
            )
            
            Text(
                when {
                    percentage == 100 -> "🌟 Perfect! All zones are clean!"
                    percentage >= 80 -> "✨ Great job! Keep it up!"
                    percentage >= 50 -> "💪 Good progress! A bit more cleaning needed."
                    else -> "🧹 Time to get cleaning!"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
        }
    }
}

@Composable
fun FilterChip(text: String, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = PrimaryGreen.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filter: $text",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryGreen
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    "Clear filter",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneCard(
    zone: Zone,
    onClick: () -> Unit,
    onMarkCleaned: () -> Unit
) {
    // Calculate status from zone properties
    val isClean = zone.isClean()
    val daysUntil = zone.getDaysUntilNextCleaning()
    val daysSince = zone.getDaysSinceLastCleaning()
    
    // Dynamic colors based on theme
    val isDarkTheme = MaterialTheme.colorScheme.background == DarkBackground
    
    val backgroundColor = when {
        isClean -> if (isDarkTheme) DarkCardClean else CardBackground
        daysUntil >= -3 -> if (isDarkTheme) DarkCardWarning else LightYellow
        else -> if (isDarkTheme) DarkCardDirty else LightRed
    }
    
    val statusColor = when {
        isClean -> if (isDarkTheme) DarkPrimaryGreen else SuccessGreen
        daysUntil >= -3 -> if (isDarkTheme) DarkYellow else SecondaryYellow
        else -> if (isDarkTheme) DarkRed else DangerRed
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        zone.category.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        zone.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        zone.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray.copy(alpha = 0.7f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isClean) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            if (isClean) {
                                "Cleaned $daysSince ${if (daysSince == 1) "day" else "days"} ago"
                            } else {
                                "Needs cleaning (${-daysUntil} ${if (daysUntil == -1) "day" else "days"} overdue)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor
                        )
                    }
                }
            }
            
            FilledIconButton(
                onClick = onMarkCleaned,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                Icon(
                    Icons.Default.CleaningServices,
                    "Mark as cleaned",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(onAddZone: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "🏡",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                "No Zones Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Start by adding your first zone to keep track of cleanliness!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray.copy(alpha = 0.7f)
            )
            Button(
                onClick = onAddZone,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryYellow
                )
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add Your First Zone")
            }
        }
    }
}

