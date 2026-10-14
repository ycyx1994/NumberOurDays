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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.numberourdays.app.notify.NotificationHelper
import com.numberourdays.app.ui.detail.DetailScreen
import com.numberourdays.app.ui.edit.EditScreen
import com.numberourdays.app.ui.holiday.HolidayListScreen
import com.numberourdays.app.ui.home.HomeScreen
import com.numberourdays.app.ui.settings.NavAnimPrefs
import com.numberourdays.app.ui.settings.SettingsScreen
import com.numberourdays.app.ui.theme.NumberOurDaysTheme
import com.numberourdays.app.widget.HolidayWidgetReceiver

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
                    openHolidays = intent?.getBooleanExtra(
                        HolidayWidgetReceiver.EXTRA_OPEN_HOLIDAYS,
                        false,
                    ) == true,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val eventId = intent.getStringExtra(NotificationHelper.EXTRA_EVENT_ID)
        val openHolidays = intent.getBooleanExtra(
            HolidayWidgetReceiver.EXTRA_OPEN_HOLIDAYS,
            false,
        )
        if (eventId != null || openHolidays) {
            setContent {
                NumberOurDaysTheme {
                    RequestNotificationPermission()
                    NodApp(initialEventId = eventId, openHolidays = openHolidays)
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
private fun NodApp(
    initialEventId: String? = null,
    openHolidays: Boolean = false,
) {
    val nav = rememberNavController()
    var deepLinkId by rememberSaveable { mutableStateOf(initialEventId.orEmpty()) }
    var pendingHolidays by rememberSaveable { mutableStateOf(openHolidays) }

    LaunchedEffect(Unit) {
        val id = deepLinkId
        if (id.isNotEmpty()) {
            nav.navigate("detail/$id")
            deepLinkId = ""
        }
        if (pendingHolidays) {
            nav.navigate("holidays")
            pendingHolidays = false
        }
    }

    val animStyle = NavAnimPrefs.style(LocalContext.current)
    NavHost(
        navController = nav,
        startDestination = "home",
        // Two selectable styles: pure fade (smoother on HyperOS) or fade+light scale.
        enterTransition = {
            fadeIn(tween(130)) +
                if (animStyle == NavAnimPrefs.FADE_SCALE) {
                    scaleIn(initialScale = 0.92f, animationSpec = tween(130))
                } else {
                    androidx.compose.animation.EnterTransition.None
                }
        },
        exitTransition = { fadeOut(tween(90)) },
        popEnterTransition = {
            fadeIn(tween(130)) +
                if (animStyle == NavAnimPrefs.FADE_SCALE) {
                    scaleIn(initialScale = 0.94f, animationSpec = tween(130))
                } else {
                    androidx.compose.animation.EnterTransition.None
                }
        },
        popExitTransition = {
            fadeOut(tween(90)) +
                if (animStyle == NavAnimPrefs.FADE_SCALE) {
                    scaleOut(targetScale = 0.94f, animationSpec = tween(90))
                } else {
                    androidx.compose.animation.ExitTransition.None
                }
        },
    ) {
        composable("home") {
            HomeScreen(
                onOpenDetail = { id -> nav.navigate("detail/$id") },
                onOpenSettings = { nav.navigate("settings") },
                onOpenAdd = { nav.navigate("edit") },
                onOpenHolidays = { nav.navigate("holidays") },
            )
        }
        composable("holidays") {
            HolidayListScreen(onBack = { nav.popBackStack() })
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
            // Destination is layout-heavy; keep transition to opacity only.
            enterTransition = { fadeIn(tween(120)) },
            exitTransition = { fadeOut(tween(80)) },
            popEnterTransition = { fadeIn(tween(120)) },
            popExitTransition = { fadeOut(tween(80)) },
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
