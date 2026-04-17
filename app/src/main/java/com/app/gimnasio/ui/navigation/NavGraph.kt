package com.app.gimnasio.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.app.gimnasio.data.model.MuscleGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.app.gimnasio.ui.screens.WeeklyStats
import com.app.gimnasio.ui.screens.ActiveWorkoutScreen
import com.app.gimnasio.ui.screens.ActivityScreen
import com.app.gimnasio.ui.screens.CreateRoutineScreen
import com.app.gimnasio.ui.screens.ExerciseDetailScreen
import com.app.gimnasio.ui.screens.ExerciseGalleryScreen
import com.app.gimnasio.ui.screens.HomeScreen
import com.app.gimnasio.ui.screens.MuscleExercisesScreen
import com.app.gimnasio.ui.screens.PRCalculatorScreen
import com.app.gimnasio.ui.screens.ProfileScreen
import com.app.gimnasio.ui.screens.RestTimerScreen
import com.app.gimnasio.ui.screens.RoutineDetailScreen
import com.app.gimnasio.ui.screens.RoutinesScreen
import com.app.gimnasio.ui.screens.WelcomeScreen
import com.app.gimnasio.ui.screens.WorkoutPlanScreen
import com.app.gimnasio.ui.screens.WorkoutSummaryScreen
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.ActiveWorkoutViewModel
import com.app.gimnasio.ui.viewmodel.ActivityViewModel
import com.app.gimnasio.ui.viewmodel.CreateRoutineViewModel
import com.app.gimnasio.ui.viewmodel.ExerciseGalleryViewModel
import com.app.gimnasio.ui.viewmodel.ProfileViewModel
import com.app.gimnasio.ui.viewmodel.RestTimerViewModel
import com.app.gimnasio.ui.viewmodel.PlanViewModel
import com.app.gimnasio.ui.viewmodel.RoutinesViewModel

object Routes {
    const val HOME = "home"
    const val ACTIVITY = "activity"
    const val LIBRARY = "library"
    const val PROFILE = "profile"
    const val ROUTINES = "routines"
    const val ROUTINE_DETAIL = "routine/{routineId}"
    const val CREATE_ROUTINE = "create_routine"
    const val REST_TIMER = "rest_timer"
    const val EXERCISES = "exercises"
    const val MUSCLE_EXERCISES = "muscle_exercises/{muscleGroup}"
    const val EXERCISE_DETAIL = "exercise_detail/{exerciseId}"
    const val ACTIVE_WORKOUT = "active_workout/{routineId}"
    const val WORKOUT_SUMMARY = "workout_summary"
    const val WORKOUT_PLAN = "workout_plan"
    const val EDIT_ROUTINE = "edit_routine/{routineId}"
    const val WELCOME = "welcome"
    const val PR_CALCULATOR = "pr_calculator"

    fun routineDetail(routineId: Long) = "routine/$routineId"
    fun editRoutine(routineId: Long) = "edit_routine/$routineId"
    fun activeWorkout(routineId: Long) = "active_workout/$routineId"
    fun muscleExercises(muscleGroup: MuscleGroup) = "muscle_exercises/${muscleGroup.name}"
    fun exerciseDetail(exerciseId: Long) = "exercise_detail/$exerciseId"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Inicio", Icons.Default.FitnessCenter),
    BottomNavItem(Routes.ACTIVITY, "Actividad", Icons.Default.CalendarMonth),
    BottomNavItem(Routes.EXERCISES, "Ejercicios", Icons.Default.AccessibilityNew),
    BottomNavItem(Routes.PROFILE, "Perfil", Icons.Default.Person),
)

@Composable
fun GimnasioNavGraph(navController: NavHostController) {
    val routinesViewModel: RoutinesViewModel = viewModel()
    val restTimerViewModel: RestTimerViewModel = viewModel()
    val createRoutineViewModel: CreateRoutineViewModel = viewModel()
    val exerciseGalleryViewModel: ExerciseGalleryViewModel = viewModel()
    val activeWorkoutViewModel: ActiveWorkoutViewModel = viewModel()
    val activityViewModel: ActivityViewModel = viewModel()
    val planViewModel: PlanViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    val isFirstTime by profileViewModel.isFirstTime.collectAsState()
    val profile by profileViewModel.profile.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Navigate to welcome on first launch
    LaunchedEffect(isFirstTime) {
        if (isFirstTime == true && currentRoute != Routes.WELCOME) {
            navController.navigate(Routes.WELCOME) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }
    }

    val showBottomBar = currentRoute in listOf(
        Routes.HOME, Routes.ACTIVITY, Routes.EXERCISES, Routes.PROFILE
    )

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = Color.White
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val scale by animateFloatAsState(
                            targetValue = if (selected) 1.15f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "navScale"
                        )
                        val iconAlpha by animateFloatAsState(
                            targetValue = if (selected) 1f else 0.6f,
                            animationSpec = tween(200),
                            label = "navAlpha"
                        )
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .scale(scale)
                                        .graphicsLayer { alpha = iconAlpha }
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.graphicsLayer { alpha = iconAlpha }
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LimeGreen,
                                selectedTextColor = LimeGreen,
                                unselectedIconColor = TextGray,
                                unselectedTextColor = TextGray,
                                indicatorColor = LimeGreen.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = if (showBottomBar) Modifier.padding(padding) else Modifier,
            enterTransition = {
                fadeIn(animationSpec = tween(250)) +
                scaleIn(initialScale = 0.94f, animationSpec = tween(250))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200)) +
                scaleOut(targetScale = 1.04f, animationSpec = tween(200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(250)) +
                scaleIn(initialScale = 1.04f, animationSpec = tween(250))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(200)) +
                scaleOut(targetScale = 0.94f, animationSpec = tween(200))
            }
        ) {
            composable(Routes.HOME) {
                val weeklyCount by activityViewModel.weeklyCount.collectAsState()
                val weeklyTotalSeconds by activityViewModel.weeklyTotalSeconds.collectAsState()
                val weeklyTotalSets by activityViewModel.weeklyTotalSets.collectAsState()
                val nextWorkout by planViewModel.nextWorkout.collectAsState()
                val hasPlan by planViewModel.hasPlan.collectAsState()
                val planDays by planViewModel.planDays.collectAsState()
                val showInfoCard by profileViewModel.showInfoCard.collectAsState()
                val weeklyGoal = if (planDays.isNotEmpty()) planDays.size else 5
                val periodDays by activityViewModel.periodDays.collectAsState()
                val periodWorkouts by activityViewModel.periodWorkouts.collectAsState()
                val periodTotalSeconds by activityViewModel.periodTotalSeconds.collectAsState()
                val periodTotalSets by activityViewModel.periodTotalSets.collectAsState()
                val dailyCalories by activityViewModel.dailyCalories.collectAsState()
                val hasActiveWorkout by activeWorkoutViewModel.hasActiveWorkout.collectAsState()
                LaunchedEffect(Unit) {
                    planViewModel.loadPlan()
                    activityViewModel.loadWeeklyData()
                    activityViewModel.loadPeriodData()
                    activityViewModel.loadMonthData()
                }
                HomeScreen(
                    onNavigateToRoutines = { navController.navigate(Routes.ROUTINES) },
                    onNavigateToTimer = { navController.navigate(Routes.REST_TIMER) },
                    onNavigateToPlan = { navController.navigate(Routes.WORKOUT_PLAN) },
                    onNavigateToProfile = {
                        navController.navigate(Routes.PROFILE) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToActivity = {
                        navController.navigate(Routes.ACTIVITY) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToExercises = {
                        navController.navigate(Routes.EXERCISES) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onStartWorkout = { routineId ->
                        navController.navigate(Routes.activeWorkout(routineId))
                    },
                    onNavigateToPRCalculator = { navController.navigate(Routes.PR_CALCULATOR) },
                    weeklyCount = weeklyCount,
                    weeklyGoal = weeklyGoal,
                    userName = profile.name,
                    userPhotoPath = profile.photoPath,
                    weeklyStats = WeeklyStats(
                        workouts = weeklyCount,
                        totalSeconds = weeklyTotalSeconds,
                        totalSets = weeklyTotalSets
                    ),
                    nextWorkout = nextWorkout,
                    hasPlan = hasPlan,
                    showInfoCard = showInfoCard,
                    onDismissInfoCard = { profileViewModel.dismissInfoCard() },
                    periodDays = periodDays,
                    periodWorkouts = periodWorkouts,
                    periodTotalSeconds = periodTotalSeconds,
                    periodTotalSets = periodTotalSets,
                    dailyCalories = dailyCalories,
                    onPeriodChange = { activityViewModel.setPeriod(it) },
                    hasActiveWorkout = hasActiveWorkout,
                    onResumeWorkout = {
                        val savedId = activeWorkoutViewModel.getSavedRoutineId()
                        if (savedId != -1L) {
                            navController.navigate(Routes.activeWorkout(savedId))
                        }
                    },
                    onDiscardWorkout = {
                        activeWorkoutViewModel.reset()
                    }
                )
            }

            composable(Routes.ACTIVITY) {
                ActivityScreen(viewModel = activityViewModel)
            }

            composable(Routes.EXERCISES) {
                LaunchedEffect(Unit) { exerciseGalleryViewModel.loadCounts() }
                ExerciseGalleryScreen(
                    onMuscleGroupClick = { muscle ->
                        navController.navigate(Routes.muscleExercises(muscle))
                    },
                    onImportRoutine = { name, description, exercises ->
                        routinesViewModel.createRoutine(name, description, exercises)
                    },
                    onImportPlan = { plan ->
                        plan.days
                            .map { it.routine }
                            .distinctBy { it.name }
                            .forEach { routine ->
                                routinesViewModel.createRoutine(
                                    routine.name,
                                    routine.description,
                                    routine.exercises
                                )
                            }
                    },
                    viewModel = exerciseGalleryViewModel
                )
            }

            composable(
                route = Routes.MUSCLE_EXERCISES,
                arguments = listOf(navArgument("muscleGroup") { type = NavType.StringType })
            ) { backStackEntry ->
                val muscleGroupName = backStackEntry.arguments?.getString("muscleGroup") ?: return@composable
                val muscleGroup = MuscleGroup.valueOf(muscleGroupName)
                MuscleExercisesScreen(
                    muscleGroup = muscleGroup,
                    viewModel = exerciseGalleryViewModel,
                    onExerciseClick = { exerciseId ->
                        navController.navigate(Routes.exerciseDetail(exerciseId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EXERCISE_DETAIL,
                arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: return@composable
                ExerciseDetailScreen(
                    exerciseId = exerciseId,
                    viewModel = exerciseGalleryViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PROFILE) {
                profileViewModel.refreshWorkoutCount()
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToPRCalculator = { navController.navigate(Routes.PR_CALCULATOR) }
                )
            }

            composable(Routes.ROUTINES) {
                RoutinesScreen(
                    viewModel = routinesViewModel,
                    onRoutineClick = { routineId ->
                        navController.navigate(Routes.routineDetail(routineId))
                    },
                    onCreateRoutine = { navController.navigate(Routes.CREATE_ROUTINE) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.CREATE_ROUTINE) {
                val allGallery by exerciseGalleryViewModel.allExercises.collectAsState()
                LaunchedEffect(Unit) { exerciseGalleryViewModel.loadAllExercises() }
                CreateRoutineScreen(
                    viewModel = createRoutineViewModel,
                    galleryExercises = allGallery,
                    onSave = { name, description, exercises, imagePath ->
                        routinesViewModel.createRoutine(name, description, exercises, imagePath)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EDIT_ROUTINE,
                arguments = listOf(navArgument("routineId") { type = NavType.LongType })
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getLong("routineId") ?: return@composable
                val allGallery by exerciseGalleryViewModel.allExercises.collectAsState()
                LaunchedEffect(routineId) {
                    exerciseGalleryViewModel.loadAllExercises()
                    val routine = routinesViewModel.routines.value.find { it.id == routineId }
                        ?: return@LaunchedEffect
                    createRoutineViewModel.loadForEdit(routine)
                }
                CreateRoutineScreen(
                    viewModel = createRoutineViewModel,
                    galleryExercises = allGallery,
                    onSave = { name, description, exercises, imagePath ->
                        routinesViewModel.updateRoutine(routineId, name, description, exercises, imagePath)
                        createRoutineViewModel.reset()
                        navController.popBackStack()
                    },
                    onBack = {
                        createRoutineViewModel.reset()
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.ROUTINE_DETAIL,
                arguments = listOf(navArgument("routineId") { type = NavType.LongType })
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getLong("routineId") ?: return@composable
                routinesViewModel.selectRoutine(routineId)
                RoutineDetailScreen(
                    viewModel = routinesViewModel,
                    onBack = { navController.popBackStack() },
                    onStartWorkout = { id ->
                        navController.navigate(Routes.activeWorkout(id))
                    },
                    onEdit = { id ->
                        navController.navigate(Routes.editRoutine(id))
                    }
                )
            }

            composable(Routes.REST_TIMER) {
                RestTimerScreen(
                    viewModel = restTimerViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ACTIVE_WORKOUT,
                arguments = listOf(navArgument("routineId") { type = NavType.LongType })
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getLong("routineId") ?: return@composable
                LaunchedEffect(routineId) {
                    activeWorkoutViewModel.startWorkout(routineId)
                }
                ActiveWorkoutScreen(
                    viewModel = activeWorkoutViewModel,
                    onFinished = {
                        navController.navigate(Routes.WORKOUT_SUMMARY) {
                            popUpTo(Routes.HOME)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.WORKOUT_SUMMARY) {
                WorkoutSummaryScreen(
                    viewModel = activeWorkoutViewModel,
                    onDone = {
                        activityViewModel.loadWeeklyData()
                        activityViewModel.loadMonthData()
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.WORKOUT_PLAN) {
                LaunchedEffect(Unit) { planViewModel.loadRoutines() }
                WorkoutPlanScreen(
                    viewModel = planViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PR_CALCULATOR) {
                PRCalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onContinue = { name ->
                        profileViewModel.saveName(name)
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
