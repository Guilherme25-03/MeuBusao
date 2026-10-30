
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.meubusao.data.AppDatabase
import com.example.meubusao.data.Categoria
import com.example.meubusao.data.Despesa
import com.example.meubusao.util.dinheiro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DetalheCategoriaScreen(
    id: Int,
    db: AppDatabase,
    onVoltar: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var categoria by remember { mutableStateOf<Categoria?>(null) }
    var despesas by remember { mutableStateOf(listOf<Despesa>()) }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }

    suspend fun carregar() {
        val resultado = withContext(Dispatchers.IO) {
            Pair(
                db.categoriaDao().buscarPorId(id),
                db.despesaDao().buscarPorCategoria(id)
            )
        }

        categoria = resultado.first
        despesas = resultado.second

        resultado.first?.let {
            nome = it.nome
            descricao = it.descricao
        }
    }

    LaunchedEffect(id) {
        carregar()
    }

    TelaBase("Detalhes da categoria", onVoltar) {
        if (categoria == null) {
            Text("Carregando categoria ou item não encontrado.")
        } else {
            Text("Categoria #$id", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                nome,
                { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                descricao,
                { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Gasto total nesta categoria",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "R$ ${dinheiro(despesas.sumOf { it.valor })}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text("${despesas.size} despesa(s) relacionada(s)")
                }
            }

            Text(
                "Despesas desta categoria",
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.Bold
            )

            despesas.forEach {
                Text("• ${it.titulo} — R$ ${dinheiro(it.valor)} (${it.data})")
            }

            if (mensagem.isNotBlank()) {
                Text(mensagem)
            }

            Button(
                onClick = {
                    if (nome.isBlank()) {
                        mensagem = "Informe o nome."
                    } else {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                db.categoriaDao().atualizar(
                                    Categoria(
                                        id = id,
                                        nome = nome.trim(),
                                        descricao = descricao.trim()
                                    )
                                )
                            }

                            carregar()
                            mensagem = "Categoria atualizada."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar categoria")
            }
        }
    }
}
