
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaBase(
    titulo: String,
    onVoltar: (() -> Unit)? = null,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        if (onVoltar != null) {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        } else {
            Text(
                titulo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                )
            )
        }

        Column(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            content = conteudo
        )
    }
}
