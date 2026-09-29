package lgbt.kami.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lgbt.kami.data.SupabaseClient

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val supabaseAuth = SupabaseClient.client.auth

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabaseAuth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign up failed", e)
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabaseAuth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign in failed", e)
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    fun signInWithGoogleToken(idToken: String, rawNonce: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabaseAuth.signInWith(io.github.jan.supabase.auth.providers.builtin.IDToken) {
                    this.idToken = idToken
                    this.provider = Google
                    this.nonce = rawNonce
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Google Sign in failed", e)
                _authState.value = AuthState.Error(e.message ?: "Google Sign in failed")
            }
        }
    }
}
