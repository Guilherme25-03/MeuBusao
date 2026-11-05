
package com.example.meubusao.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.meubusao.data.Despesa
import com.example.meubusao.util.dinheiro
import com.example.meubusao.viewmodel.CategoriaViewModel
import com.example.meubusao.viewmodel.DespesaViewModel

@Composable
fun DespesasScreen(
    despesaViewModel: DespesaViewModel,
    categoriaViewModel: CategoriaViewModel,
    onDetalhe: (Int) -> Unit
) {
    val despesaState by despesaViewModel.uiState.collectAsStateWithLifecycle()
    val categoriaState by categoriaViewModel.uiState.collectAsStateWithLifecycle()

    var titulo by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }

    TelaBase("Despesas") {
        Text("Nova despesa", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (ex.: 15,50)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = categoriaId,
            onValueChange = { categoriaId = it },
            label = { Text("ID da categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "Categorias cadastradas: " +
                    categoriaState.categorias.joinToString { "${it.id} - ${it.nome}" }
        )

        OutlinedTextField(
            value = data,
            onValueChange = { data = it },
            label = { Text("Data") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        if (mensagem.isNotBlank()) {
            Text(mensagem, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val valorConvertido = valor.replace(",", ".").toDoubleOrNull()
                val categoriaConvertida = categoriaId.toIntOrNull()

                if (
                    titulo.isBlank() ||
                    valorConvertido == null ||
                    valorConvertido <= 0 ||
                    categoriaConvertida == null ||
                    data.isBlank()
                ) {
                    mensagem = "Preencha os campos corretamente."
                } else if (
                    categoriaState.categorias.none {
                        it.id == categoriaConvertida
                    }
                ) {
                    mensagem = "Escolha um ID de categoria existente."
                } else {
                    despesaViewModel.inserir(
                        titulo.trim(),
                        valorConvertido,
                        categoriaConvertida,
                        data.trim(),
                        descricao.trim()
                    )

                    titulo = ""
                    valor = ""
                    categoriaId = ""
                    data = ""
                    descricao = ""
                    mensagem = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar despesa")
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Text("Despesas cadastradas", style = MaterialTheme.typography.titleLarge)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(despesaState.despesas, key = { it.id }) { despesa ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDetalhe(despesa.id) }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(despesa.titulo, style = MaterialTheme.typography.titleMedium)
                        Text("Valor: R$ ${dinheiro(despesa.valor)}")
                        Text("Data: ${despesa.data}")
                        Text(
                            "Categoria: ${
                                categoriaState.categorias.find {
                                    it.id == despesa.categoriaId
                                }?.nome ?: "Não encontrada"
                            }"
                        )
                        Text("Toque para ver detalhes e editar.")
                    }
                }
            }
        }
    }
}
