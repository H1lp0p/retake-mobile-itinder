package com.stuf.itinder.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stuf.itinder.R
import com.stuf.itinder.main.screens.ChatsPlaceholderScreen
import com.stuf.itinder.main.screens.FeedPlaceholderScreen
import com.stuf.itinder.main.screens.PeoplePlaceholderScreen
import com.stuf.itinder.main.screens.ProfilePlaceholderScreen

private fun AnimatedContentTransitionScope<NavBackStackEntry>.mainEnter(): EnterTransition {
    val from = MainRoutes.orderIndex(initialState.destination.route)
    val to = MainRoutes.orderIndex(targetState.destination.route)
    return when {
        to > from -> slideInHorizontally { it }
        to < from -> slideInHorizontally { -it }
        else -> EnterTransition.None
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.mainExit(): ExitTransition {
    val from = MainRoutes.orderIndex(initialState.destination.route)
    val to = MainRoutes.orderIndex(targetState.destination.route)
    return when {
        to > from -> slideOutHorizontally { -it }
        to < from -> slideOutHorizontally { it }
        else -> ExitTransition.None
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.mainPopEnter(): EnterTransition {
    val from = MainRoutes.orderIndex(initialState.destination.route)
    val to = MainRoutes.orderIndex(targetState.destination.route)
    return when {
        to < from -> slideInHorizontally { -it }
        to > from -> slideInHorizontally { it }
        else -> EnterTransition.None
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.mainPopExit(): ExitTransition {
    val from = MainRoutes.orderIndex(initialState.destination.route)
    val to = MainRoutes.orderIndex(targetState.destination.route)
    return when {
        to < from -> slideOutHorizontally { it }
        to > from -> slideOutHorizontally { -it }
        else -> ExitTransition.None
    }
}

@Composable
fun MainScreenRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MainRoutes.FEED

    Box(modifier = Modifier.fillMaxSize()) {
        SplashGradientBackground(modifier = Modifier.fillMaxSize())
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            containerColor = Color.Transparent,
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = dimensionResource(R.dimen.main_bottom_bar_screen_horizontal_padding))
                        .padding(bottom = dimensionResource(R.dimen.main_bottom_bar_bottom_padding)),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    MainFloatingBottomBar(
                        selectedRoute = currentRoute,
                        onTabSelected = { route ->
                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                    )
                }
            },
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = MainRoutes.FEED,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                enterTransition = { mainEnter() },
                exitTransition = { mainExit() },
                popEnterTransition = { mainPopEnter() },
                popExitTransition = { mainPopExit() },
            ) {
                composable(MainRoutes.FEED) { FeedPlaceholderScreen() }
                composable(MainRoutes.PEOPLE) { PeoplePlaceholderScreen() }
                composable(MainRoutes.CHATS) { ChatsPlaceholderScreen() }
                composable(MainRoutes.PROFILE) { ProfilePlaceholderScreen() }
            }
        }
    }
}
