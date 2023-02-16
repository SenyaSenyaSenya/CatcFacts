package com.example.httpbutton.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.httpbutton.R
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
import java.text.DateFormat
import java.text.DateFormat.getTimeInstance
import java.util.*

var ind = 0
val dFormat: DateFormat = getTimeInstance()

@Composable
fun FactsScreen(factExport: String?, navController: NavController) {
    val theme = remember {
        mutableStateOf(false)
    }
    SwitchTheme(darkTheme = theme.value) {
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val vm: UserViewModel = viewModel()
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(coroutineScope) {
            if (!factExport.isNullOrBlank()) {
                vm.addCard(
                    Card(
                        factExport, dFormat.format(Date()), ind,
                        isDisabled = false,
                        shown = false
                    )
                )
            }
        }
        Scaffold(scaffoldState = scaffoldState,
            topBar = { TopBarFacts(scaffoldState, coroutineScope) },
            drawerContent = { Drawer(vm, navController, theme) },
            floatingActionButton = {
                FAB(navController)
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colors.onBackground),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AddButton(vm = vm)
                    }
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        vm.cards.forEachIndexed { index, it ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                shape = RoundedCornerShape(7.dp),
                                border = BorderStroke(5.dp, MaterialTheme.colors.primary),
                                elevation = 5.dp,
                                backgroundColor = MaterialTheme.colors.background,
                                contentColor = MaterialTheme.colors.error
                            ) {
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = it.fact,
                                            modifier = Modifier
                                                .fillMaxWidth(0.9f)
                                                .padding(16.dp, 12.dp, 1.dp, 0.dp),
                                            fontSize = 20.sp,
                                        )
                                        IconButton(onClick = {
                                            vm.onDropdownAt(index)
                                        }) {
                                            Icon(
                                                Icons.Filled.MoreVert,
                                                contentDescription = "More vertically",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            if (it.shown) {
                                                DropDMenu(
                                                    index = index,
                                                    vm = vm,
                                                    factToSomething = it.fact,
                                                    navController = navController
                                                )
                                            }
                                        }
                                    }
                                    Row {
                                        Text(
                                            modifier = Modifier,
                                            text = it.index.toString(),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp, 0.dp),
                                            text = it.time,
                                            textAlign = TextAlign.End
                                        )
                                        Spacer(modifier = Modifier.height(30.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun Alert(openBuff: MutableState<Boolean>, vm: UserViewModel, index: Int, deleted: String) {
    if (openBuff.value) {
        AlertDialog(onDismissRequest = {
            openBuff.value = false
        }, title = {
            Text(text = "Are you sure to delete this fact?", fontSize = 18.sp, color = Color.Black)
        }, text = {
            Text(
                text = "If you're agree, this fact would be deleted.",
                fontSize = 17.sp, color = Color.Black
            )
        }, confirmButton = {
            Button(onClick = {
                openBuff.value = false
            }) {
                Text("Cancel", fontSize = 16.sp, color = BackgroundFacts)
            }
        }, dismissButton = {
            Button(onClick = {
                vm.deleteCard(index)
                vm.addDeletedFact(deleted)
                openBuff.value = false
            }) {
                Text("Delete", fontSize = 16.sp, color = BackgroundFacts)
            }
        })
    }
}

@Composable
fun DropDMenu(
    index: Int,
    vm: UserViewModel,
    factToSomething: String,
    navController: NavController
) {
    var deletedFact = ""
    var number = 0
    val openDialog = remember {
        mutableStateOf(false)
    }
    DropdownMenu(
        expanded = vm.ddCards.value[index].isDisabled,
        onDismissRequest = {
            vm.onDropdownAt(index)
        },
        modifier = Modifier
            .border(3.dp, BorderDDMFacts)
    ) {
        DropdownMenuItem(onClick = {
            number = index
            openDialog.value = true
            deletedFact = factToSomething
        }) {
            IconButton(onClick = {
                openDialog.value = true
            }) {
                if (openDialog.value) {
                    Alert(openDialog, vm, number, deletedFact)
                }
                Row {
                    Icon(
                        painterResource(id = R.drawable.delete_case),
                        contentDescription = "Delete fact",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.secondary
                    )
                    Text(text = "Delete Fact", color = MaterialTheme.colors.secondary, modifier = Modifier.padding(5.dp,0.dp))
                }
            }
        }
        Divider(modifier = Modifier.background(BorderDDMFacts), thickness = 2.dp)
        DropdownMenuItem(onClick = {
        }) {
            IconButton(onClick = {
                vm.onDropdownAt(index)
                navController.navigate("edit_screen/$factToSomething")

            }) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(id = R.drawable.edit_case),
                        contentDescription = "Edit fact",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = "Edit fact",
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(5.dp, 0.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun AddButton(vm: UserViewModel) {
    val composableScope = rememberCoroutineScope()
    val client = remember {
        HttpClient(CIO)
    }
    DisposableEffect(key1 = LocalLifecycleOwner.current) {
        onDispose { client.close() }
    }
    val index = remember {
        mutableStateListOf(0)
    }
    Button(modifier = Modifier
        .padding(16.dp)
        .width(140.dp), colors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.secondaryVariant,
    ), border = BorderStroke(
        3.dp, MaterialTheme.colors.primary
    ), shape = MaterialTheme.shapes.medium, onClick = {
        composableScope.launch {
            val request: String = client.get("https://catfact.ninja/fact").body()
            val obj = Json.decodeFromString<FactDTO>(request)
            vm.addCard(
                Card(
                    obj.fact, dFormat.format(Date()), ind,
                    isDisabled = false,
                    shown = false
                )
            )
        }
        index.add(++ind)
    }) {
        Text("Add", fontSize = 25.sp)
    }
}

@Composable
fun TopBarFacts(scaffoldState: ScaffoldState, coroutineScope: CoroutineScope) {
    TopAppBar(backgroundColor = MaterialTheme.colors.secondary, contentColor = Color.White,
        navigationIcon = {
            IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = null)
            }
        }, title = { Text(text = "Facts") }, actions = {
        })
}

@Composable
fun FAB(navController: NavController) {
    FloatingActionButton(content = {
        Icon(
            painter = painterResource(id = R.drawable.edit_case), contentDescription = "Write new"
        )
    }, backgroundColor = BorderFacts, onClick = {
        navController.navigate("edit_screen/ ")
    })
}

@Composable
fun Drawer(vm: UserViewModel, navController: NavController, theme: MutableState<Boolean>) {
    var isRotated by rememberSaveable { mutableStateOf(false) }
    val angle by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f, animationSpec = tween(durationMillis = 500)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.onPrimary)
    ) {
        Row(
            modifier = Modifier
                .padding(0.dp, 10.dp)
                .align(CenterHorizontally)
        ) {
            val rainbowColorsBrush = remember {
                Brush.sweepGradient(
                    listOf(
                        Color(0xFFFF9800),
                        Color(0xFFF44336),
                        Color(0xFF9C27B0),
                        Color(0xFF3F51B5),
                        Color(0xFF03A9F4),
                        Color(0xFF009688),
                        Color(0xFF8BC34A),
                        Color(0xFFFFEB3B),
                        Color(0xFFFF9800)
                    )
                )
            }
            Image(
                painter = painterResource(id = R.drawable.cat_menu),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(4.dp, rainbowColorsBrush), CircleShape
                    )
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(vm.menus) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp, 0.dp)
                ) {
                    Button(modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.onSecondary,
                            contentColor = Color.White
                        ),
                        shape = CutCornerShape(10.dp),
                        border = BorderStroke(2.dp, MaterialTheme.colors.onSurface),
                        onClick = {
                            navController.navigate(it.route)
                        }) {
                        IconButton(onClick = {
                            navController.navigate(it.route)
                        }) {
                            Image(painterResource(id = it.iconRes), contentDescription = "")
                        }
                        Text(it.name, fontSize = 20.sp)
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier.rotate(angle),
            ) {
                IconButton(onClick = {
                    isRotated = !isRotated
                    theme.value = !theme.value
                }) {
                    Row(modifier = Modifier.padding(0.dp, 16.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.brightness),
                            contentDescription = "Rotation",
                            modifier = Modifier.size(40.dp),
                            tint = if (isRotated) MenuBrightnessLight else MenuBrightness
                        )
                    }
                }
            }
        }
    }
}

@Serializable
data class FactDTO(
    val fact: String, val length: Int
)

@Serializable
data class Card(
    val fact: String,
    val time: String,
    val index: Int,
    val isDisabled: Boolean,
    val shown: Boolean
)

@Serializable
data class Menu(
    val name: String, val iconRes: Int, val route: String
)