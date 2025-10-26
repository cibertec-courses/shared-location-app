package edu.pe.cibertec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


sealed class AuthState{
    object Idle: AuthState()
    object Loading: AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel(){
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState> (AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthStatus()
    }
    private fun checkAuthStatus(){
        if (auth.currentUser!= null){
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email:String, password: String){
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated
            }catch (e: Exception){
                _authState.value = AuthState.Error(
                    e.message ?: "Error al iniciar sesion"
                )

            }
        }
    }

    fun register (email: String, password: String){
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated
            }catch (e: Exception){
                _authState.value = AuthState.Error(
                    e.message ?: "Error al registrar usuario"
                )

            }
        }
    }
    fun logout(){
        auth.signOut()
        _authState.value = AuthState.Idle
    }

}