package lgbt.kami

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import lgbt.kami.ui.auth.AuthState
import lgbt.kami.ui.auth.AuthViewModel
import lgbt.kami.ui.auth.LoginScreen
import lgbt.kami.ui.auth.SignupScreen
import lgbt.kami.ui.theme.KamiTheme
import java.security.MessageDigest
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KamiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val authState by authViewModel.authState.collectAsState()
                    val context = LocalContext.current
                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(authState) {
                        if (authState is AuthState.Success) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                authState = authState,
                                onLoginClick = { email, password ->
                                    authViewModel.signIn(email, password)
                                },
                                onGoogleSignInClick = {
                                    coroutineScope.launch {
                                        try {
                                            val credentialManager = CredentialManager.create(context)
                                            val rawNonce = UUID.randomUUID().toString()
                                            val bytes = rawNonce.toByteArray()
                                            val md = MessageDigest.getInstance("SHA-256")
                                            val digest = md.digest(bytes)
                                            val hashedNonce = digest.joinToString("") { "%02x".format(it) }

                                            val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

                                            val googleIdOption = GetGoogleIdOption.Builder()
                                                .setFilterByAuthorizedAccounts(false)
                                                .setServerClientId(webClientId)
                                                .setNonce(hashedNonce)
                                                .build()

                                            val request = GetCredentialRequest.Builder()
                                                .addCredentialOption(googleIdOption)
                                                .build()

                                            val result = credentialManager.getCredential(
                                                context = context,
                                                request = request
                                            )

                                            val credential = result.credential
                                            if (credential is CustomCredential &&
                                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                            ) {
                                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                                val idToken = googleIdTokenCredential.idToken

                                                authViewModel.signInWithGoogleToken(idToken, rawNonce)
                                            } else {
                                                Log.e("MainActivity", "Unexpected credential type")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("MainActivity", "Google Sign in failed", e)
                                        }
                                    }
                                },
                                onNavigateToSignup = {
                                    navController.navigate("signup")
                                }
                            )
                        }
                        composable("signup") {
                            SignupScreen(
                                authState = authState,
                                onSignupClick = { email, password ->
                                    authViewModel.signUp(email, password)
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                }
                            )
                        }
                        composable("home") {
                            Greeting(name = "Kami User")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KamiTheme {
        Greeting("Android")
    }
}