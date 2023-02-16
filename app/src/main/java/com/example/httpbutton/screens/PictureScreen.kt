package com.example.httpbutton.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.httpbutton.ui.theme.*
import com.example.httpbutton.util.UserViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Composable
fun PictureScreen(navController: NavController) {
    val theme = remember {
        mutableStateOf(false)
    }
    SwitchTheme(darkTheme = theme.value) {
        val roundCornerShape = RoundedCornerShape(
            topEnd = 30.dp, bottomStart = 30.dp, topStart = 5.dp, bottomEnd = 5.dp
        )
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val vm = UserViewModel()
        val coroutineScope = rememberCoroutineScope()
        val gradientColors =
            if (theme.value) listOf(Color(0xFF906D9B), Color(0xFF582461)) else listOf(
                Color(0xFF431B4E), Color(0xFF785383)
            )
        val composableScope = rememberCoroutineScope()
        val client = remember {
            HttpClient(CIO)
        }
        DisposableEffect(key1 = LocalLifecycleOwner.current) {
            onDispose { client.close() }
        }
        val loadingImage = remember {
            mutableStateOf("")
        }
        Scaffold(scaffoldState = scaffoldState, content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colors.onBackground),
            ) {
                Box(modifier = Modifier
                    .padding(10.dp)
                    .background(
                        brush = Brush.horizontalGradient(colors = gradientColors),
                        shape = roundCornerShape
                    )
                    .clip(roundCornerShape)
                    .fillMaxWidth()
                    .clickable {
                        composableScope.launch {
                            val request:String = client
                                .get("https://aws.random.cat/meow")
                                .body()
                            val obj = Json.decodeFromString<CatDTO>(request)
                            loadingImage.value = obj.file
                        }
                    }
                    .padding(PaddingValues(horizontal = 60.dp, vertical = 16.dp)),
                    contentAlignment = Alignment.Center) {
                    Text(
                        "Get a cat!",
                        fontSize = 25.sp,
                        color = if (!theme.value) Color(0xFFE7DAEB) else Color(0xFFE4EFF5)
                    )
                }
                val configuration = LocalConfiguration.current
                Box(modifier = Modifier.padding(5.dp, 0.dp, 5.dp, 5.dp)) {
                    if (loadingImage.value.isNotEmpty()) Image(
                        painter = rememberAsyncImagePainter(loadingImage.value),
                        contentDescription = "Picture of a cat",
                        modifier = Modifier.width(configuration.screenWidthDp.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }, drawerContent = {
            Drawer(vm, navController, theme)
        }, topBar = {
            TopBarGallery(
                scaffoldState = scaffoldState,
                coroutineScope = coroutineScope,
                navController = navController
            )
        })
    }
}

@Composable
fun TopBarGallery(
    scaffoldState: ScaffoldState, coroutineScope: CoroutineScope, navController: NavController
) {
    TopAppBar(backgroundColor = MaterialTheme.colors.secondary,
        contentColor = Color.White,
        navigationIcon = {
            IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = null)
            }
        },
        title = { Text(text = "Gallery") },
        actions = {
            IconButton(onClick = {
                navController.navigate("facts_screen/ ")
            }) {
                Icon(
                    imageVector = Icons.Outlined.Done, contentDescription = "Done"
                )
            }
        }
    )
}

@Serializable
data class CatDTO(
    val file: String
)