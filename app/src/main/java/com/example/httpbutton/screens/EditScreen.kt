package com.example.httpbutton.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.httpbutton.ui.theme.*

@Composable
fun EditScreen(fact: String?, navController: NavController) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    var fact2: String? = fact
    fact2 = if (fact2 == " ") "" else fact
    val message = remember { mutableStateOf(fact2) }
    Scaffold(scaffoldState = scaffoldState, modifier = Modifier.background(EditScreenBackground),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(EditScreenBackground),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    shape = RoundedCornerShape(7.dp),
                  //  border = BorderStroke(5.dp, BorderFacts),
                    elevation = 5.dp,
                    backgroundColor = Color(
                        red = 0x255, green = 0x235, blue = 0x205, alpha = 0xFF22
                    ),
                ) {
                    Column {
                        TextField(value = message.value.toString(),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp, 12.dp, 16.dp, 0.dp),
                            textStyle = TextStyle(fontSize = 25.sp),//, color = TextEditScreen),
                            placeholder = { Text("Enter a fact", fontSize = 25.sp, color = Color.DarkGray) },
                            onValueChange = { newText ->
                                message.value = newText
                            }
                        )
                    }
                }
            }
        }, topBar = {
            TopAppBar(backgroundColor = TopBarEdit,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = {
                        if (!fact.isNullOrBlank()) {
                            navController.navigate("facts_screen/${fact}")
                        } else navController.navigate("facts_screen/ ")
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "return back"
                        )
                    }
                },
                title = { if (fact == " ") Text(text = "Write new fact") else Text(text = "Edit fact") },
                actions = {
                    IconButton(onClick = {
                        if (!fact.isNullOrBlank()) {
                            navController.navigate("facts_screen/${message.value}")
                        } else navController.navigate("facts_screen/ ")
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Done, contentDescription = "Info"
                        )
                    }
                }
            )
        }
    )
}