package com.chickisa.sofskil.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chickisa.sofskil.ui.theme.*
import com.chickisa.sofskil.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onDataCleared: () -> Unit = {}
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val dateFormat by viewModel.dateFormat.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var isClearing by remember { mutableStateOf(false) }
    var showDateFormatDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryGreen,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            
            item {
                SettingCard(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = when (themeMode) {
                        "dark" -> "Dark"
                        else -> "Light"
                    },
                    onClick = { showThemeDialog = true }
                )
            }

            
            item {
                Text(
                    "Display",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryGreen,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }
            
            item {
                SettingCard(
                    icon = Icons.Default.CalendarToday,
                    title = "Date Format",
                    subtitle = dateFormat,
                    onClick = { showDateFormatDialog = true }
                )
            }
            
            item {
                Text(
                    "About",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryGreen,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }

            item {
                SettingCard(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "Tap to read",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chicksanity.com/privacy-policy.html"))
                        context.startActivity(intent)
                    }
                )
            }
            
            item {
                AboutCard()
            }
            
            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
    
    // Clear All Data Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!isClearing) showClearDataDialog = false 
            },
            title = { Text("Clear All Data?", fontWeight = FontWeight.Bold) },
            text = { 
                if (isClearing) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = PrimaryGreen,
                            strokeWidth = 4.dp
                        )
                        Text(
                            "Clearing all data...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "Please wait",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    Text("This will permanently delete:\n• All zones\n• Cleaning history\n• All achievements\n\nThis action cannot be undone!")
                }
            },
            confirmButton = {
                if (!isClearing) {
                    Button(
                        onClick = {
                            isClearing = true
                            viewModel.clearAllData {
                                coroutineScope.launch {
                                    delay(100)
                                    isClearing = false
                                    showClearDataDialog = false
                                    onDataCleared()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRed
                        )
                    ) {
                        Text("Delete Everything", color = Color.White)
                    }
                }
            },
            dismissButton = {
                if (!isClearing) {
                    TextButton(onClick = { showClearDataDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
    
    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeOption("Light", "light", themeMode) {
                        viewModel.setThemeMode("light")
                        showThemeDialog = false
                    }
                    ThemeOption("Dark", "dark", themeMode) {
                        viewModel.setThemeMode("dark")
                        showThemeDialog = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
    
    // Date Format Dialog
    if (showDateFormatDialog) {
        AlertDialog(
            onDismissRequest = { showDateFormatDialog = false },
            title = { Text("Date Format", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DateFormatOption("MMM dd, yyyy", dateFormat) {
                        viewModel.setDateFormat("MMM dd, yyyy")
                        showDateFormatDialog = false
                    }
                    DateFormatOption("dd/MM/yyyy", dateFormat) {
                        viewModel.setDateFormat("dd/MM/yyyy")
                        showDateFormatDialog = false
                    }
                    DateFormatOption("MM/dd/yyyy", dateFormat) {
                        viewModel.setDateFormat("MM/dd/yyyy")
                        showDateFormatDialog = false
                    }
                    DateFormatOption("yyyy-MM-dd", dateFormat) {
                        viewModel.setDateFormat("yyyy-MM-dd")
                        showDateFormatDialog = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDateFormatDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.large)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray.copy(alpha = 0.7f)
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextGray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SettingSwitchCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray.copy(alpha = 0.7f)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = PrimaryGreen,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                    uncheckedTrackColor = TextGray.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun ThemeOption(label: String, value: String, currentValue: String, onClick: () -> Unit) {
    val isDarkTheme = MaterialTheme.colorScheme.background == DarkBackground
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = if (value == currentValue) {
            if (isDarkTheme) DarkPrimaryGreen.copy(alpha = 0.2f) else PrimaryGreen.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (value == currentValue) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = if (isDarkTheme) DarkPrimaryGreen else PrimaryGreen
                )
            }
        }
    }
}

@Composable
fun DateFormatOption(format: String, currentFormat: String, onClick: () -> Unit) {
    val isDarkTheme = MaterialTheme.colorScheme.background == DarkBackground
    val exampleDate = java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
        .format(java.util.Date())
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = if (format == currentFormat) {
            if (isDarkTheme) DarkPrimaryGreen.copy(alpha = 0.3f) else PrimaryGreen.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    format,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Example: $exampleDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray.copy(alpha = 0.7f)
                )
            }
            if (format == currentFormat) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = PrimaryGreen
                )
            }
        }
    }
}

@Composable
fun AboutCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🧹",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                "Chick Sanity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Version 1.0",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray.copy(alpha = 0.7f)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                "Keep your farm clean and organized with simple tracking and reminders.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
            Text(
                "Made with 💚 for farmers",
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryGreen,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

