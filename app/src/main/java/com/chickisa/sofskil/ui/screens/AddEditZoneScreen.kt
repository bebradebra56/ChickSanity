package com.chickisa.sofskil.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.chickisa.sofskil.data.model.ZoneCategory
import com.chickisa.sofskil.ui.theme.*
import com.chickisa.sofskil.ui.viewmodel.AddEditZoneViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditZoneScreen(
    viewModel: AddEditZoneViewModel,
    zoneId: Long?,
    onNavigateBack: () -> Unit
) {
    val name by viewModel.name.collectAsState()
    val category by viewModel.category.collectAsState()
    val cleaningFrequencyDays by viewModel.cleaningFrequencyDays.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showBubbleAnimation by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(zoneId) {
        if (zoneId != null) {
            viewModel.loadZone(zoneId)
        } else {
            viewModel.resetForm()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Zone" else "Add Zone",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Zone Name",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = viewModel::setName,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., Chicken Coop") },
                        leadingIcon = { Icon(Icons.Default.Home, null) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            focusedLabelColor = PrimaryGreen
                        )
                    )
                }
            }
            
            // Category Picker
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, MaterialTheme.shapes.medium)
                    .clickable { showCategoryPicker = true },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                category.emoji,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                category.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
            }
            
            // Cleaning Frequency
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Cleaning Frequency",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Every $cleaningFrequencyDays ${if (cleaningFrequencyDays == 1) "day" else "days"}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledIconButton(
                                onClick = { viewModel.setCleaningFrequencyDays(cleaningFrequencyDays - 1) },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = PrimaryGreen
                                )
                            ) {
                                Icon(Icons.Default.Remove, "Decrease", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                            FilledIconButton(
                                onClick = { viewModel.setCleaningFrequencyDays(cleaningFrequencyDays + 1) },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = PrimaryGreen
                                )
                            ) {
                                Icon(Icons.Default.Add, "Increase", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                    Slider(
                        value = cleaningFrequencyDays.toFloat(),
                        onValueChange = { viewModel.setCleaningFrequencyDays(it.toInt()) },
                        valueRange = 1f..30f,
                        steps = 28,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryGreen,
                            activeTrackColor = PrimaryGreen,
                            inactiveTrackColor = LightGreen
                        )
                    )
                    Text(
                        "Slide to adjust cleaning frequency (1-30 days)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Notes
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Notes (Optional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = viewModel::setNotes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Add any additional notes...") },
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            focusedLabelColor = PrimaryGreen
                        )
                    )
                }
            }
            
             // Save Button
             Button(
                 onClick = {
                     viewModel.saveZone {
                         onNavigateBack()
                     }
                 },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)
                ),
                shape = MaterialTheme.shapes.large
            ) {
                if (showBubbleAnimation) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🫧", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Saved!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Save, null)
                        Text(
                            if (isEditMode) "Update Zone" else "Create Zone",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
        }
    }
    
    // Category Picker Dialog
    if (showCategoryPicker) {
        AlertDialog(
            onDismissRequest = { showCategoryPicker = false },
            title = {
                Text(
                    "Select Category",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ZoneCategory.entries.forEach { cat ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setCategory(cat)
                                    showCategoryPicker = false
                                },
                            shape = MaterialTheme.shapes.medium,
                            color = if (cat == category) {
                                PrimaryGreen.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    cat.emoji,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    cat.displayName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryPicker = false }) {
                    Text("Close")
                }
            }
        )
    }
}

