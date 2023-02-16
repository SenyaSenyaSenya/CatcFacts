package com.example.httpbutton.screens

const val factToImport = "fact"
const val factExport = "message"
const val factToDelete = "deleted"

sealed class Screen(val route: String) {
    object FactsScreen : Screen("facts_screen/{$factExport}")
    object EditScreen : Screen("edit_screen/{$factToImport}")
    object PictureScreen : Screen ("picture_screen")
}