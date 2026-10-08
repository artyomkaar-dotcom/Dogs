package com.kodex.guide.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.compose.runtime.remember
import com.kodex.guide.data.mapper.ParallaxImageCache
import com.kodex.guide.data.mapper.toParallaxNavObject
import com.kodex.guide.presentation.admin_panel.AdminPanelScreen
import com.kodex.guide.presentation.admin_panel.ModerationScreen
import com.kodex.guide.presentation.comments.CommentsScreen
import com.kodex.guide.presentation.details.parallaxScreen.ParallaxScreen
import com.kodex.guide.presentation.home.HomeScreen
import com.kodex.guide.presentation.home.HomeViewModel
import com.kodex.guide.presentation.login.LoginScreen
import com.kodex.guide.presentation.login.sign_up.SignUpScreen
import com.kodex.guide.presentation.myPosts.MyPostEditorScreen
import com.kodex.guide.presentation.myPosts.MyPostsScreen
import com.kodex.guide.presentation.settingsScreen.SettingsScreen

 @RequiresApi(Build.VERSION_CODES.O)
 @Composable
fun NavigationNavGraph (navController: NavHostController){
    NavHost(
    navController = navController,
   // startDestination = NavRoutes.LoginNavObject,
    startDestination = NavRoutes.HomeDataObject(
        uid = "",
        email = ""
    ),

    ) {

        composable<NavRoutes.LoginNavObject> {
            LoginScreen(
                onNavigationToMainScreen = { navData ->
                    navController.navigate(navData)
                },
                onNavigationToSignUpScreen = { navData ->
                    navController.navigate(navData)
                }
            )
        }
        composable<NavRoutes.SingUpNavObject> {
            SignUpScreen() { navData ->
                navController.navigate(navData)
            }
        }

        composable<NavRoutes.HomeDataObject> { navEntry ->
            val navData = navEntry.toRoute<NavRoutes.HomeDataObject>()
            val homeViewModel: HomeViewModel = hiltViewModel()

            HomeScreen(
                navData = navData,
                viewModel = homeViewModel, // Передаем viewModel явно
                onBookClick = { place ->
                    navController.navigate(
                        place.toParallaxNavObject()
                    )
                },

                onBookEditClick = { book ->
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = book.key))
                },
                onAdminClick = {
                    navController.navigate(NavRoutes.ModerationNavObject)
                },
                onLoginClick = {
                    navController.navigate(NavRoutes.LoginNavObject)
                },
                onAnonymousClick = {
                    navController.navigate(NavRoutes.HomeDataObject(
                        uid = "", email = ""))
                },
                onSettingsClick = {
                    navController.navigate(NavRoutes.SettingsNavObject)
                },
               onCategoryClick = {
                   navController.navigate(NavRoutes.HomeDataObject(
                       uid = "", email = ""))
               },
                onAddClick = {
                    navController.navigate(NavRoutes.MyPostEditorNavObject())
                },
                onRegistrationNeeded = {
                    // ✅ Запуск регистрации
                    navController.navigate(NavRoutes.SingUpNavObject)
                    // или
                    // signUpViewModel.signUp(...)
                },
                onEnter = {
                    // ✅ Запуск регистрации
                    navController.navigate(NavRoutes.LoginNavObject)
                    // или
                    // signUpViewModel.signUp(...)
                },
                onMyPostsClick = {
                    navController.navigate(NavRoutes.MyPostsNavObject(
                        uid = ""
                    ))
                }, onSavedClick = {

                },
            )
        }
        composable<NavRoutes.MyPostsNavObject> {
            MyPostsScreen(
                onEditClick = { book ->
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = book.key))
                },
                onAddClick = {
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = ""))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.MyPostEditorNavObject> { navEntry ->
            val navData = navEntry.toRoute<NavRoutes.MyPostEditorNavObject>()
            val homeEntry = navController.getBackStackEntry<NavRoutes.HomeDataObject>()
            val homeViewModel: HomeViewModel = hiltViewModel(homeEntry)
            MyPostEditorScreen(
                bookKey = navData.bookKey,
                onSaved = {
                    homeViewModel.refreshBooks()
                    navController.popBackStack<NavRoutes.HomeDataObject>(inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.AdminPanelNavObject> {
            AdminPanelScreen(
                onAddBookClick = {
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = ""))
                },
                onModerationClick = {
                    navController.navigate(NavRoutes.ModerationNavObject)
                }
            )
        }
        composable<NavRoutes.ModerationNavObject> {
            ModerationScreen()
        }
        composable<NavRoutes.SettingsNavObject> { navEntry ->
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseAccountClick = {
                    navController.popBackStack(
                        NavRoutes.LoginNavObject,
                        inclusive = false
                    )
                }
            )
        }
        composable<NavRoutes.ParallaxNavObject> { navEntry ->
            val navData = navEntry.toRoute<NavRoutes.ParallaxNavObject>()
            val navObject = remember(navData.bookId) {
                navData.copy(
                    imageUrl = ParallaxImageCache.take(navData.bookId) ?: navData.imageUrl
                )
            }

            ParallaxScreen(
                navObject = navObject,
                onBackPressed = { navController.popBackStack() },
                onNavigateToReviews = {},
                onCommentClick = {
                    navController.navigate(
                        NavRoutes.CommentsNavData(
                            bookId = navData.bookId,
                            title = navData.title,
                            ratingsList = navData.ratingsList
                        )
                    )
                },
                // ✅ категория ТЕКУЩЕГО поста — из navData, а не из book
                bookCategory = navData.categoryIndex,
                // ✅ клик по похожему посту — переход на НОВЫЙ ParallaxScreen
                onRelatedBookClick = { relatedBook ->
                    navController.navigate(relatedBook.toParallaxNavObject())
                },
            )
        }

        composable<NavRoutes.CommentsNavData> { navEntry ->
            val navData = navEntry.toRoute<NavRoutes.CommentsNavData>()
            CommentsScreen(
                navObject = navData
            )
        }
    }
}