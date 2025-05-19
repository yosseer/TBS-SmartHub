package com.example.tbssmarthub.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbssmarthub.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth


data class AuthField(
    val value: String,
    val onValueChange: (String) -> Unit,
    val label: String,
    val icon: ImageVector,
    val isPassword: Boolean,
    val validator: ((String) -> Boolean)? = null,
    val errorMessage: String = "",
    val errorState: Boolean = false,
    val onErrorChange: ((Boolean) -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthForm(
    title: String,
    fields: List<AuthField>,
    onSubmit: () -> Unit,
    submitText: String,
    navController: NavController,
    alternateActionText: String,
    alternateDestination: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(320.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                fields.forEach { field ->
                    OutlinedTextField(
                        value = field.value,
                        onValueChange = {
                            field.onValueChange(it)
                            field.validator?.let { validator ->
                                field.onErrorChange?.invoke(!validator(it) && it.isNotEmpty())
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = field.icon,
                                contentDescription = field.label,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        label = { Text(field.label) },
                        isError = field.errorState,
                        supportingText = {
                            if (field.errorState) {
                                Text(
                                    text = field.errorMessage,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        visualTransformation = if (field.isPassword)
                            PasswordVisualTransformation()
                        else
                            VisualTransformation.None,
                        keyboardOptions = if (field.isPassword)
                            KeyboardOptions(keyboardType = KeyboardType.Password)
                        else
                            KeyboardOptions.Default,
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSubmit,
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(submitText, fontSize = 16.sp)
                }
            }
        }

        val context = LocalContext.current
        val auth = Firebase.auth

        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Handle successful login (e.g., navigate to home)
                            navController.navigate("home")
                        } else {
                            // Show error
                            Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(context, "Sign-In Error", Toast.LENGTH_SHORT).show()
            }
        }

        Column(
            modifier = Modifier
                .width(320.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = alternateActionText.substringBefore("?").trim() + "?",
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = alternateActionText.substringAfter("?").trim(),
                    color = Color(0xFFFFEB3B),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(alternateDestination)
                        }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    thickness = 1.dp
                )
                Text(
                    "OR",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val client = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(client.signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(30.dp),
                border = BorderStroke(1.dp, Color.Gray),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_), // Ensure this asset exists
                        contentDescription = "Google icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Google", fontSize = 16.sp)
                }
            }
        }
    }
}