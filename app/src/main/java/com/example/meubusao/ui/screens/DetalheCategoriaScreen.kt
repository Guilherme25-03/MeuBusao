
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.meubusao.data.Categoria
import com.example.meubusao.util.dinheiro
import com.example.meubusao.viewmodel.CategoriaViewModel
import com.example.meubusao.viewmodel.DespesaViewModel

@Composable
fun DetalheCategoriaScreen(
    id: Int,
    categoriaViewModel: CategoriaViewModel,
    despesaViewModel: DespesaViewModel,
    onVoltar: () -> Unit
) {
    val categoriaState by categoriaViewModel.uiState.collectAsStateWithLifecycle()
    val despesaState by despesaViewModel.uiState.collectAsStateWithLifecycle()

    val categoria = categoriaState.categorias.find { it.id == id }
    val despesas = despesaState.despesas.filter { it.categoriaId == id }

    var nome by remember(categoria?.id) {
        mutableStateOf(categoria?.nome ?: "")
    }
    var descricao by remember(categoria?.id) {
        mutableStateOf(categoria?.descricao ?: "")
    }
    var mensagem by remember { mutableStateOf("") }

    TelaBase("Detalhes da categoria", onVoltar) {
        if (categoria == null) {
            Text("Categoria não encontrada.")
        } else {
            Text(
                "Categoria #$id",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Gasto total nesta categoria")
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
                        categoriaViewModel.atualizar(
                            Categoria(
                                id = id,
                                nome = nome.trim(),
                                descricao = descricao.trim()
                            )
                        )
                        mensagem = "Categoria atualizada."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar categoria")
            }
        }
    }
}
