package com.paperrecipes.app.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.paperrecipes.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirm by mutableStateOf("")
    var username by mutableStateOf("")

    /*
    * Agregar por parámetro cuando el repositorio de autenticación esté listo,
    * debería retornar tipo Boolean para cambiar el estado de la pantalla.
    * */
    private var _uiState = MutableStateFlow(AuthState())
    var uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun inputListener(
        it: String,
        isEmail: Boolean = false,
        isPassword: Boolean = false,
        isConfirmPassword: Boolean = false,
        isUsername: Boolean = false,
        ) {
         when {
             isEmail -> email = it
             isPassword -> password = it
             isConfirmPassword -> confirm = it
             isUsername -> username = it
         }
        clearError()
    }


    fun submit() {
        val validationError: Int? =
            validate(email, username, password,confirm, _uiState.value.isRegisterMode)

        if ( validationError != null ){
            _uiState.update { it.copy(error = validationError) }
        } else {
            _uiState.update { it.copy(isSignedIn = true) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearAll() {
        email = ""
        password = ""
        confirm = ""
        username = ""
    }
    fun viewToggle() {
        _uiState.update {
            it.copy(isRegisterMode = !it.isRegisterMode, error = null)
        }
        clearAll()
    }
}


private fun validate(
        email: String,
        user: String,
        password: String,
        confirm: String,
        register: Boolean,
    ): Int? = when {
        email.isBlank() -> R.string.email_blank
        !email.contains("@") -> R.string.email_invalid
        password.length < 6 -> R.string.password_length
        register && user.isBlank() -> R.string.username_blank
        register && password != confirm -> R.string.password_match
        else -> null
    }

data class AuthState(
    var isRegisterMode: Boolean = false,
    var isSignedIn: Boolean = false,
    var error: Int? = null
)