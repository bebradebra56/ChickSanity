package com.chickisa.sofskil.ui.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object AddEditZone : Screen("add_edit_zone?zoneId={zoneId}") {
        fun createRoute(zoneId: Long? = null) = if (zoneId != null) {
            "add_edit_zone?zoneId=$zoneId"
        } else {
            "add_edit_zone"
        }
    }
    data object ZoneDetail : Screen("zone_detail/{zoneId}") {
        fun createRoute(zoneId: Long) = "zone_detail/$zoneId"
    }
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
}

