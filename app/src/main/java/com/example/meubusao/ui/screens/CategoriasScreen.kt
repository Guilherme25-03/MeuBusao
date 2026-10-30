
package com.example.meubusao.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.meubusao.data.AppDatabase
import com.example.meubusao.data.Categoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



@Composable
fun CategoriasScreen(
    db: AppDatabase,
    onDetalhe: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }

    suspend fun atualizar() {
        categorias = withContext(Dispatchers.IO) {
            db.categoriaDao().buscarTodos()
        }
    }

    LaunchedEffect(Unit) {
        atualizar()
    }

    TelaBase("Categorias") {
        Text("Nova categoria", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            nome,
            { nome = it },
            label = { Text("Nome (ex.: Transporte)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            descricao,
            { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (erro.isNotBlank()) {
            Text(erro, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                if (nome.isBlank()) {
                    erro = "Informe o nome da categoria."
                } else {
                    erro = ""

                    scope.launch {
                        withContext(Dispatchers.IO) {
                            db.categoriaDao().inserir(
                                Categoria(
                                    nome = nome.trim(),
                                    descricao = descricao.trim()
                                )
                            )
                        }

                        atualizar()
                        nome = ""
                        descricao = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar categoria")
        }

        HorizontalDivider(Modifier.padding(vertical = 10.dp))

        Text("Toque no cartão para ver os detalhes e o total gasto.")

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(categorias, key = { it.id }) { categoria ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onDetalhe(categoria.id) }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(categoria.nome, fontWeight = FontWeight.Bold)
                            Text(categoria.descricao.ifBlank { "Sem descrição" })
                            Text("ID: ${categoria.id}")
                        }

                        TextButton(
                            onClick = {
                                scope.launch {
                                    val relacionadas = withContext(Dispatchers.IO) {
                                        db.despesaDao().buscarPorCategoria(categoria.id)
                                    }

                                    if (relacionadas.isEmpty()) {
                                        withContext(Dispatchers.IO) {
                                            db.categoriaDao().excluir(categoria)
                                        }
                                        atualizar()
                                    } else {
                                        erro = "Essa categoria tem despesas. Remova-as antes."
                                    }
                                }
                            }
                        ) {
                            Text("Excluir")
                        }
                    }
                }
            }
        }
    }
}
