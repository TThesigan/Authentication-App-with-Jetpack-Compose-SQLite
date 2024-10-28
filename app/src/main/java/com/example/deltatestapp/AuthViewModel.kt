package com.example.deltatestapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel(context: Context) : ViewModel() {
    private val databaseHelper = DatabaseHelper(context)

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    var username: String? = null

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        // Logic to determine authentication status can be implemented here if needed
        _authState.value = AuthState.Unauthenticated
    }

    fun signin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        // Validate user with SQLite
        if (databaseHelper.isUserExist(email, password)) {
            username = databaseHelper.getUser(email, password)
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Error("Invalid email or password")
        }
    }

    fun signup(email: String, password: String, username: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }
        // Check if user already exists
        if (databaseHelper.userExists(email)) {
            _authState.value = AuthState.Error("Email already in use")
            return
        }
        _authState.value = AuthState.Loading
        databaseHelper.addUser(username, email, password)
        _authState.value = AuthState.Authenticated
        if (databaseHelper.isUserExist(email, password)) {
            this.username = databaseHelper.getUser(email, password)
        }
    }

    fun signout() {
        _authState.value = AuthState.Unauthenticated
        username = null
    }

    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
    }
}