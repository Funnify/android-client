package com.funnify.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val tag = "FunnifyApp"
    private val rcSignInWithGoogle = 101
    private lateinit var signInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // region set up google sign-in
        val googleSignInOptions = GoogleSignInOptions.Builder()
            .requestProfile()
            .requestEmail()
            .requestIdToken("750835935495-2mc7apul2pjfsreheknem56qe7mv4fc9.apps.googleusercontent.com")
            .build()
        signInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        googleSignInButton.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account == null) {
                val intent = signInClient.signInIntent
                intent.flags = 0
                startActivityForResult(intent, rcSignInWithGoogle)
            } else {
                googleSignInButton.isEnabled = false
                signInClient.signOut().addOnSuccessListener {
                    googleSignInButton.isEnabled = true
                    val intent = signInClient.signInIntent
                    intent.flags = 0
                    startActivityForResult(intent, rcSignInWithGoogle)
                }
            }
        }
        // endregion

        // region set up facebook sign-in
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.e(tag, "FacebookCallback.onSuccess()")
                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken != null && !accessToken.isExpired)
                    handleAuthToken(accessToken.token, Auth.AuthProvider.FACEBOOK)
            }

            override fun onCancel() {
                Log.e(tag, "FacebookCallback.onCancel()")
            }

            override fun onError(e: FacebookException) {
                Log.e(tag, "FacebookCallback.onCancel()")
                e.printStackTrace()
            }
        })
        // If a custom facebook login button is clicked, call the following
        //    LoginManager.getInstance().logInWithReadPermissions(this@MainActivity, listOf("public_profile"))
        // endregion
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Google SignIn case
        if (requestCode == rcSignInWithGoogle) {
            if (resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.result
                    if (account != null) {
                        val idToken = account.idToken
                        if (idToken != null)
                            handleAuthToken(idToken, Auth.AuthProvider.GOOGLE)
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }

        // Facebook SignIn case
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleAuthToken(idToken: String, authProvider: Auth.AuthProvider) {
        val credential = when (authProvider) {
            Auth.AuthProvider.GOOGLE -> GoogleAuthProvider.getCredential(idToken, null)
            Auth.AuthProvider.FACEBOOK -> FacebookAuthProvider.getCredential(idToken)
            Auth.AuthProvider.UNRECOGNIZED -> null
        }
        credential!!
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener {
            Log.e(tag, "handleFacebookToken(); onSuccessListener()")
            val user = firebaseAuth.currentUser
            if (user != null)
                FunnifyService.authenticate(
                    context = applicationContext,
                    idToken = idToken,
                    authProvider = authProvider,
                    onSuccessListener = {
                        handleFirebaseUser(user)
                    },
                    onFailureListener = {
                        Log.e(tag, "handleAuthToken(); signInWithCredential(); onFailureListener()")
                    }
                )
        }.addOnFailureListener {
            Log.e(tag, "handleFacebookToken(); onFailureListener()")
            it.printStackTrace()
        }
    }

    private fun handleFirebaseUser(user: FirebaseUser) {
        val a = 4
    }
}
