package screen.home

import HomeViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import screen.details.DetailsScreen

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        KoinContext {
            AppContent(
                navigator = navigator,
                bottomSheetNavigator = bottomSheetNavigator
            )
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    homeViewModel: HomeViewModel = koinInject(),
    navigator: Navigator?,
    bottomSheetNavigator: BottomSheetNavigator?
) {
    val products = homeViewModel.products.collectAsState()

    BoxWithConstraints {
        val scope = this
        val maxWidth = scope.maxWidth

        var cols = 2
        var modifier = Modifier.fillMaxWidth()
        if(maxWidth > 840.dp) {
            cols = 3
            modifier = Modifier.widthIn(max = 1080.dp)
        }

        val scrollState = rememberLazyGridState()
        val coroutineScope = rememberCoroutineScope()

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                state = scrollState,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.draggable(orientation = Orientation.Vertical, state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta)
                    }
                })
            ) {

                item(span = { GridItemSpan(cols) }) {
                    Column {
                        SearchBar(
                            modifier = Modifier.fillMaxWidth(),
                            query = "",
                            active = false,
                            onActiveChange = {},
                            onQueryChange = {},
                            onSearch = {},
                            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                            placeholder = { Text("Search Products") }
                        ) {}
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                items(
                    items = products.value,
                    key = { product -> product.id.toString() }
                ) { product ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
//                                navigator?.push(DetailsScreen(product))
                                bottomSheetNavigator?.show(DetailsScreen(product = product))
                            },
                        shape = RoundedCornerShape(15.dp),
                        elevation = 2.dp
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            KamelImage(
                                resource = asyncPainterResource(product.image.toString()),
                                contentDescription = "product image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.0f)
                            )
                            Text(
                                product.title.toString(),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 16.dp).heightIn(min = 40.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    "${product.price.toString()} INR",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 16.dp).heightIn(min = 40.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}