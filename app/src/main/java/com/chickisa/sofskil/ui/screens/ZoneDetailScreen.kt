package com.chickisa.sofskil.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chickisa.sofskil.data.model.CleaningHistory
import com.chickisa.sofskil.data.model.Zone
import com.chickisa.sofskil.ui.theme.*
import com.chickisa.sofskil.ui.viewmodel.ZoneDetailViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneDetailScreen(
    viewModel: ZoneDetailViewModel,
    zoneId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val zone by viewModel.zone.collectAsState()
    val history by viewModel.history.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCleanAnimation by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(zoneId) {
        viewModel.setZoneId(zoneId)
    }
    
    zone?.let { currentZone ->
        val isClean = currentZone.isClean()
        val daysUntil = currentZone.getDaysUntilNextCleaning()
        val daysSince = currentZone.getDaysSinceLastCleaning()
        
        val isDarkTheme = MaterialTheme.colorScheme.background == DarkBackground
        
        val backgroundColor = when {
            isClean -> if (isDarkTheme) DarkCardClean else MaterialTheme.colorScheme.background
            daysUntil >= -3 -> if (isDarkTheme) DarkCardWarning else LightYellow
            else -> if (isDarkTheme) DarkCardDirty else LightRed
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            currentZone.name,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigateToEdit(zoneId) }) {
                            Icon(Icons.Default.Edit, "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor,
                        titleContentColor = TextGray
                    )
                )
            },
             floatingActionButton = {
                 if (!showCleanAnimation) {
                     ExtendedFloatingActionButton(
                         onClick = {
                             viewModel.markAsCleaned()
                             showCleanAnimation = true
                             coroutineScope.launch {
                                 kotlinx.coroutines.delay(2000)
                                 showCleanAnimation = false
                             }
                         },
                        icon = { Icon(Icons.Default.CleaningServices, null) },
                        text = { Text("Mark as Cleaned") },
                        containerColor = PrimaryGreen,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ExtendedFloatingActionButton(
                        onClick = {},
                        icon = { Text("✨") },
                        text = { Text("Cleaned! 🫧") },
                        containerColor = SuccessGreen,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ZoneStatusCard(currentZone, isClean, daysUntil, daysSince)
                }
                
                item {
                    ZoneInfoCard(currentZone)
                }
                
                item {
                    Text(
                        "Cleaning History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                if (history.isEmpty()) {
                    item {
                        EmptyHistoryCard()
                    }
                } else {
                    items(history, key = { it.id }) { record ->
                        CleaningHistoryItem(record)
                    }
                }
            }
        }
        
        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Zone?") },
                text = { Text("Are you sure you want to delete \"${currentZone.name}\"? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteZone()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = DangerRed
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ZoneStatusCard(zone: Zone, isClean: Boolean, daysUntil: Int, daysSince: Int) {
    val statusColor = when {
        isClean -> SuccessGreen
        daysUntil >= -3 -> SecondaryYellow
        else -> DangerRed
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = if (isClean) CardBackground else statusColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    zone.category.emoji,
                    style = MaterialTheme.typography.displayMedium
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isClean) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        if (isClean) "Clean" else "Needs Cleaning",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                
                if (isClean) {
                    Text(
                        "Last cleaned $daysSince ${if (daysSince == 1) "day" else "days"} ago",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray
                    )
                    Text(
                        "Next cleaning in $daysUntil ${if (daysUntil == 1) "day" else "days"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        "Overdue by ${-daysUntil} ${if (daysUntil == -1) "day" else "days"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
fun ZoneInfoCard(zone: Zone) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow(
                icon = Icons.Default.Category,
                label = "Category",
                value = "${zone.category.emoji} ${zone.category.displayName}"
            )
            
            Divider()
            
            InfoRow(
                icon = Icons.Default.Schedule,
                label = "Cleaning Frequency",
                value = "Every ${zone.cleaningFrequencyDays} ${if (zone.cleaningFrequencyDays == 1) "day" else "days"}"
            )
            
            if (zone.notes.isNotBlank()) {
                Divider()
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Notes,
                            null,
                            tint = TextGray.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Notes",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextGray.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        zone.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                tint = TextGray.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = TextGray.copy(alpha = 0.6f)
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CleaningHistoryItem(record: CleaningHistory) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "Cleaned",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    dateFormat.format(Date(record.cleaningTimestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun EmptyHistoryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🧹", style = MaterialTheme.typography.displayMedium)
            Text(
                "No History Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Mark as cleaned to start tracking!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray.copy(alpha = 0.7f)
            )
        }
    }
}

