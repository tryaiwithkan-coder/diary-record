package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BabyViewModel
import com.example.ui.viewmodel.DiaryViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val babyViewModel: BabyViewModel = viewModel()
                val diaryViewModel: DiaryViewModel = viewModel()

                val babiesList by babyViewModel.allBabies.collectAsStateWithLifecycle()
                val activeBaby by babyViewModel.activeBaby.collectAsStateWithLifecycle()
                val growthRecords by babyViewModel.growthRecords.collectAsStateWithLifecycle()
                
                val diaryEntries by diaryViewModel.entries.collectAsStateWithLifecycle()
                val searchQuery by diaryViewModel.searchQuery.collectAsStateWithLifecycle()
                val searchResults by diaryViewModel.searchResults.collectAsStateWithLifecycle()
                
                val ocrLoading by diaryViewModel.ocrLoading.collectAsStateWithLifecycle()
                val ocrResult by diaryViewModel.ocrResult.collectAsStateWithLifecycle()
                
                val recapLoading by diaryViewModel.recapLoading.collectAsStateWithLifecycle()
                val recapText by diaryViewModel.monthlyRecapText.collectAsStateWithLifecycle()
                val recapEntries by diaryViewModel.recapEntries.collectAsStateWithLifecycle()

                val navController = rememberNavController()
                var dbChecked by remember { mutableStateOf(false) }

                // Sync active baby to diary ViewModel reactively
                LaunchedEffect(activeBaby) {
                    activeBaby?.let { 
                        diaryViewModel.setBabyId(it.id)
                    }
                }

                // Prevent onboarding splash flicker during DB check
                LaunchedEffect(babiesList) {
                    delay(300)
                    dbChecked = true
                }

                if (!dbChecked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                } else {
                    val startDestination = if (activeBaby == null) "onboarding" else "home"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(
                                onProfileCreated = { name, bdate, gender ->
                                    babyViewModel.addBaby(name, bdate, gender)
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            activeBaby?.let { baby ->
                                HomeScreen(
                                    baby = baby,
                                    growthRecords = growthRecords,
                                    recentEntries = diaryEntries,
                                    onAddEntryClick = { navController.navigate("add_entry") },
                                    onTimelineClick = { navController.navigate("timeline") },
                                    onSearchClick = { navController.navigate("search") },
                                    onRecapClick = { navController.navigate("recap") },
                                    onProfileClick = { navController.navigate("profile") }
                                )
                            }
                        }

                        composable("timeline") {
                            TimelineScreen(
                                entries = diaryEntries,
                                onDeleteEntry = { entry -> diaryViewModel.deleteEntry(entry) },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable("add_entry") {
                            AddEntryScreen(
                                viewModel = diaryViewModel,
                                onBackClick = {
                                    diaryViewModel.clearOcrResult()
                                    diaryViewModel.clearBulkItems()
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("search") {
                            SearchScreen(
                                query = searchQuery,
                                searchResults = searchResults,
                                onQueryChange = { q -> diaryViewModel.updateSearchQuery(q) },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable("recap") {
                            activeBaby?.let { baby ->
                                RecapScreen(
                                    baby = baby,
                                    recapText = recapText,
                                    recapLoading = recapLoading,
                                    recapEntries = recapEntries,
                                    onGenerateRecap = { monthYear ->
                                        diaryViewModel.generateRecap(baby.name, monthYear)
                                    },
                                    onClearRecap = { diaryViewModel.clearRecap() },
                                    onBackClick = {
                                        diaryViewModel.clearRecap()
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        composable("profile") {
                            activeBaby?.let { baby ->
                                ProfileScreen(
                                    baby = baby,
                                    growthRecords = growthRecords,
                                    onAddGrowthRecord = { w, h, hc, date ->
                                        babyViewModel.addGrowthRecord(w, h, hc, date)
                                    },
                                    onDeleteGrowthRecord = { record ->
                                        babyViewModel.deleteGrowthRecord(record)
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
