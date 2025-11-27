package com.chickisa.sofskil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chickisa.sofskil.data.model.Achievement
import com.chickisa.sofskil.data.model.AchievementType
import com.chickisa.sofskil.ui.theme.*
import com.chickisa.sofskil.ui.viewmodel.StatisticsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    onNavigateBack: () -> Unit
) {
    val statistics by viewModel.statistics.collectAsState()
    val zones by viewModel.zones.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Statistics & Achievements",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = if (MaterialTheme.colorScheme.background == DarkBackground) DarkPrimaryGreen else PrimaryGreen
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Statistics") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Achievements") }
                )
            }
            
            when (selectedTab) {
                0 -> StatisticsContent(statistics, zones)
                1 -> AchievementsContent(achievements)
            }
        }
    }
}

@Composable
fun StatisticsContent(
    statistics: com.chickisa.sofskil.ui.viewmodel.StatisticsData,
    zones: List<com.chickisa.sofskil.data.model.Zone>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OverviewCard(statistics)
        }
        
        item {
            CleanDirtyChartCard(statistics)
        }
        
        item {
            RecentActivityCard(statistics)
        }
        
        if (statistics.cleaningsByDay.isNotEmpty()) {
            item {
                Text(
                    "Recent Cleanings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                RecentCleaningsCard(statistics)
            }
        }
    }
}

@Composable
fun OverviewCard(statistics: com.chickisa.sofskil.ui.viewmodel.StatisticsData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    value = statistics.totalCleanings.toString(),
                    label = "Total Cleanings",
                    icon = "🧹",
                    color = PrimaryGreen
                )
                StatCard(
                    value = statistics.cleanZones.toString(),
                    label = "Clean Zones",
                    icon = "✨",
                    color = SuccessGreen
                )
                StatCard(
                    value = statistics.dirtyZones.toString(),
                    label = "Need Cleaning",
                    icon = "⚠️",
                    color = if (statistics.dirtyZones > 0) DangerRed else SuccessGreen
                )
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, icon: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, style = MaterialTheme.typography.headlineMedium)
        }
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = TextGray.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CleanDirtyChartCard(statistics: com.chickisa.sofskil.ui.viewmodel.StatisticsData) {
    val total = statistics.cleanZones + statistics.dirtyZones
    
    // Если нет зон вообще, не показываем график
    if (total == 0) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, MaterialTheme.shapes.large),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Zone Status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "No zones yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray.copy(alpha = 0.7f)
                )
            }
        }
        return
    }
    
    val cleanPercentage = (statistics.cleanZones.toFloat() / total).coerceIn(0.01f, 0.99f)
    
    val animatedCleanPercentage by animateFloatAsState(
        targetValue = cleanPercentage,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "clean_percentage"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Zone Status",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Если все зоны грязные
                if (statistics.cleanZones == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.medium)
                            .background(DangerRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "🧹",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                "${statistics.dirtyZones}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                // Если все зоны чистые
                else if (statistics.dirtyZones == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.medium)
                            .background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "✨",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                "${statistics.cleanZones}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                // Если есть и чистые и грязные
                else {
                    // Clean zones bar
                    Box(
                        modifier = Modifier
                            .weight(animatedCleanPercentage)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.medium)
                            .background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "✨",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                "${statistics.cleanZones}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    
                    // Dirty zones bar
                    Box(
                        modifier = Modifier
                            .weight(1f - animatedCleanPercentage)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.medium)
                            .background(DangerRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "🧹",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                "${statistics.dirtyZones}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (statistics.cleanZones > 0) {
                    LegendItem("Clean", SuccessGreen)
                }
                if (statistics.dirtyZones > 0) {
                    LegendItem("Need Cleaning", DangerRed)
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color)
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RecentActivityCard(statistics: com.chickisa.sofskil.ui.viewmodel.StatisticsData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            ActivityRow(
                icon = "📅",
                label = "This Week",
                value = "${statistics.cleaningsThisWeek} cleanings"
            )
            
            ActivityRow(
                icon = "📆",
                label = "This Month",
                value = "${statistics.cleaningsThisMonth} cleanings"
            )
            
            if (statistics.averageDaysBetweenCleanings > 0) {
                ActivityRow(
                    icon = "⏱️",
                    label = "Average Frequency",
                    value = "${String.format("%.1f", statistics.averageDaysBetweenCleanings)} days"
                )
            }
        }
    }
}

@Composable
fun ActivityRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = PrimaryGreen
        )
    }
}

@Composable
fun RecentCleaningsCard(statistics: com.chickisa.sofskil.ui.viewmodel.StatisticsData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            statistics.cleaningsByDay.entries.take(7).forEach { (date, count) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        date,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(minOf(count, 5)) {
                            Text("✨", style = MaterialTheme.typography.bodySmall)
                        }
                        if (count > 5) {
                            Text("+${count - 5}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsContent(achievements: List<Achievement>) {
    val achievementTypes = AchievementType.entries
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            val unlockedCount = achievements.count { it.isUnlocked }
            val totalCount = achievementTypes.size
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, MaterialTheme.shapes.large),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🏆",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        "$unlockedCount / $totalCount Achievements",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { unlockedCount.toFloat() / totalCount },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.small),
                        color = SecondaryYellow,
                        trackColor = LightYellow
                    )
                }
            }
        }
        
        items(achievementTypes.toList()) { achievementType ->
            val achievement = achievements.find { it.id == achievementType.id }
            AchievementCard(achievementType, achievement)
        }
    }
}

@Composable
fun AchievementCard(achievementType: AchievementType, achievement: Achievement?) {
    val isUnlocked = achievement?.isUnlocked ?: false
    val progress = achievement?.progress ?: 0
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) SecondaryYellow.copy(alpha = 0.3f)
                        else TextGray.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    achievementType.emoji,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isUnlocked) Color.Unspecified else TextGray.copy(alpha = 0.3f)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    achievementType.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) TextGray else TextGray.copy(alpha = 0.6f)
                )
                Text(
                    achievementType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray.copy(alpha = 0.7f)
                )
                
                if (!isUnlocked) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "$progress / ${achievementType.requiredProgress}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryGreen
                        )
                        LinearProgressIndicator(
                            progress = {
                                (progress.toFloat() / achievementType.requiredProgress).coerceIn(
                                    0f,
                                    1f
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(MaterialTheme.shapes.small),
                            color = PrimaryGreen,
                            trackColor = LightGreen
                        )
                    }
                } else {
                    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
                    achievement?.unlockedTimestamp?.let { timestamp ->
                        Text(
                            "Unlocked: ${dateFormat.format(Date(timestamp))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryYellow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            if (isUnlocked) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = SecondaryYellow,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

