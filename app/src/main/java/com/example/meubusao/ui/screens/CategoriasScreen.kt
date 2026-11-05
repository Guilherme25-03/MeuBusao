
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.meubusao.viewmodel.CategoriaViewModel
import com.example.meubusao.viewmodel.DespesaViewModel

@Composable
fun CategoriasScreen(
    categoriaViewModel: CategoriaViewModel,
    despesaViewModel: DespesaViewModel,
    onDetalhe: (Int) -> Unit
) {
    val categoriaState by categoriaViewModel.uiState.collectAsStateWithLifecycle()
    val despesaState by despesaViewModel.uiState.collectAsStateWithLifecycle()

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    TelaBase("Categorias") {
        Text("Nova categoria", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome (ex.: Transporte)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (categoriaState.mensagem.isNotBlank()) {
            Text(
                categoriaState.mensagem,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                categoriaViewModel.inserir(nome, descricao)
                if (nome.isNotBlank()) {
                    nome = ""
                    descricao = ""
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
            items(categoriaState.categorias, key = { it.id }) { categoria ->
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
                                val relacionadas = despesaState.despesas.filter {
                                    it.categoriaId == categoria.id
                                }

                                if (relacionadas.isEmpty()) {
                                    categoriaViewModel.excluir(categoria)
                                } else {
                                    categoriaViewModel.informarMensagem(
                                        "Essa categoria tem despesas. Remova-as antes."
                                    )
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
