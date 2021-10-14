package com.eyeorders.navigator

import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController

interface NavComponentsProvider {
    val navController: NavController
    val drawerLayout:DrawerLayout
}