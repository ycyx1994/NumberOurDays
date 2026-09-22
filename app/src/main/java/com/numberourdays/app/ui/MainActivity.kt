package com.numberourdays.app.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.numberourdays.app.notify.NotificationHelper
import com.numberourdays.app.ui.detail.DetailScreen
import com.numberourdays.app.ui.edit.EditScreen
import com.numberourdays.app.ui.home.HomeScreen
import com.numberourdays.app.ui.settings.SettingsScreen
import com.numberourdays.app.ui.theme.NumberOurDaysTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createChannel(this)
        setContent {
            NumberOurDaysTheme {
                RequestNotificationPermission()
                NodApp(
                    initialEventId = intent?.getStringExtra(NotificationHelper.EXTRA_EVENT_ID),
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Recompose path: recreate content with the new event id when possible
        val eventId = intent.getStringExtra(NotificationHelper.EXTRA_EVENT_ID)
        if (eventId != null) {
            setContent {
                NumberOurDaysTheme {
                    RequestNotificationPermission()
                    NodApp(initialEventId = eventId)
                }
            }
        }
    }
}

@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT < 33) return
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { /* denied: Worker still schedules; notify becomes no-op */ }
    LaunchedEffect(Unit) {
        if (!NotificationHelper.canPost(context)) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun NodApp(initialEventId: String? = null) {
    val nav = rememberNavController()
    var deepLinkId by rememberSaveable { mutableStateOf(initialEventId.orEmpty()) }

    LaunchedEffect(Unit) {
        val id = deepLinkId
        if (id.isNotEmpty()) {
            nav.navigate("detail/$id")
            deepLinkId = ""
        }
    }

    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onOpenDetail = { id -> nav.navigate("detail/$id") },
                onOpenSettings = { nav.navigate("settings") },
                onOpenAdd = { nav.navigate("edit") },
            )
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            DetailScreen(
                eventId = id,
                onBack = { nav.popBackStack() },
                onEdit = { editId -> nav.navigate("edit?id=$editId") },
            )
        }
        composable(
            route = "edit?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }),
        ) { entry ->
            EditScreen(
                eventId = entry.arguments?.getString("id"),
                onBack = { nav.popBackStack() },
                onSaved = { nav.popBackStack() },
            )
        }
        composable("settings") {
            SettingsScreen(onBack = { nav.popBackStack() })
        }
    }
}
