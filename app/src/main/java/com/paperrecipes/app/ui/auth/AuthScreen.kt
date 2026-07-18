package com.paperrecipes.app.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paperrecipes.app.R
import com.paperrecipes.app.ui.components.AppTextField
import com.paperrecipes.app.ui.components.AppTextLogo
import com.paperrecipes.app.ui.components.PasswordTextField
import com.paperrecipes.app.ui.components.PrimaryButton
import com.paperrecipes.app.ui.theme.PaperRecipesTheme

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    PaperRecipesTheme {
        AuthScreen(onSignedIn = {})
    }
}


class AuthModifiers {
    val textModifier: Modifier =
        Modifier.fillMaxWidth().padding(horizontal = 32.dp)
    val spacerModifier: Modifier =
        Modifier.padding(16.dp)
    val textFieldModifier: Modifier =
        Modifier.fillMaxWidth().padding(
            horizontal = 32.dp,
            vertical = 8.dp)
    val buttonModifier: Modifier =
        Modifier.fillMaxWidth().padding(horizontal = 32.dp)
}

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onSignedIn: () -> Unit ) {

    val mod = AuthModifiers()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isSignedIn) {
        if(uiState.isSignedIn)
            onSignedIn()
    }

    Scaffold(
        contentColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            AppTextLogo()

            Text(
                text = stringResource(R.string.welcome_back),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                modifier = mod.textModifier
            )

            Spacer(modifier = mod.spacerModifier)

            AppTextField(
                value = viewModel.email,
                onValueChange = { viewModel.inputListener(it, isEmail = true) },
                label = stringResource(R.string.email),
                modifier = mod.textFieldModifier,
                keyboardType = KeyboardType.Email,
                placeholder = stringResource(R.string.email_placeholder)
            )

            if(uiState.isRegisterMode){
                AppTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.inputListener(it, isUsername = true) },
                    label = stringResource(R.string.username),
                    modifier = mod.textFieldModifier,
                    keyboardType = KeyboardType.Text,
                    placeholder = stringResource(R.string.username_placeholder)
                )
            }

            PasswordTextField(
                value = viewModel.password,
                onValueChange = { viewModel.inputListener(it, isPassword = true) },
                modifier = mod.textFieldModifier,
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.password_placeholder)
            )

            if(uiState.isRegisterMode){
                PasswordTextField(
                    value = viewModel.confirm,
                    onValueChange = { viewModel.inputListener(it, isConfirmPassword = true) },
                    modifier = mod.textFieldModifier,
                    label = stringResource(R.string.confirm_password)
                )
            }

            if (uiState.error != null){
                Text(
                    text = stringResource(uiState.error!!),
                    modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = mod.spacerModifier)

            PrimaryButton(
                onClick = {
                    viewModel.submit()
                },
                text = if ( !uiState.isRegisterMode) stringResource(R.string.sign_in)
                else stringResource(R.string.create_account),
                modifier = mod.buttonModifier,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text (
                text = if ( !uiState.isRegisterMode ) stringResource(R.string.new_here)
                else stringResource(R.string.already_have_account),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable(
                    onClick = {
                        viewModel.viewToggle()
                    }
                ).padding(start = 32.dp, end = 32.dp)
            )
        }
    }
}






