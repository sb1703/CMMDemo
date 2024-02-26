package tab.home

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import screen.home.HomeScreen

// Since this tab is not going to hold any argument and will not be reused anywhere, we can use object
object HomeTab : Tab {

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
//        Navigator(HomeScreen()) { navigator ->
//            SlideTransition(navigator)
//        }
        BottomSheetNavigator {
            Navigator(HomeScreen())
        }
    }

    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(
                index = 0u,
                title = "Home",
                icon = null
            )
        }

}