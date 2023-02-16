package com.example.httpbutton.util

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.httpbutton.R
import com.example.httpbutton.screens.Card
import com.example.httpbutton.screens.Menu

class UserViewModel : ViewModel() {

    private val _cards = mutableStateListOf<Card>()

    val cards: List<Card>
        get() = _cards

    val ddCards = mutableStateOf(listOf<Card>())


    fun onDropdownAt(index: Int) {
        _cards[index] =
            _cards[index].let { it.copy(isDisabled = it.isDisabled.not(), shown = it.shown.not()) }
        ddCards.value = _cards.toList()
    }

    fun addCard(card: Card) {
        _cards.add(card)
    }

    fun deleteCard(index: Int) {
        _cards.removeAt(index)
    }

    private val _deletedCards = mutableStateListOf<String>()

    fun addDeletedFact(deletedFact: String) {
        _deletedCards.add(deletedFact)
    }

    private val _menu = listOf(
        Menu("Facts", R.drawable.menu_book_case, "facts_screen/ "),
        Menu("Write new", R.drawable.edit_case, "edit_screen/ "),
        Menu("Gallery", R.drawable.image_case, "picture_screen")
    )
    val menus: List<Menu>
        get() = _menu
}