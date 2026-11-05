
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.meubusao.data.Despesa
import com.example.meubusao.viewmodel.CategoriaViewModel
import com.example.meubusao.viewmodel.DespesaViewModel

@Composable
fun DetalheDespesaScreen(
    id: Int,
    despesaViewModel: DespesaViewModel,
    categoriaViewModel: CategoriaViewModel,
    onVoltar: () -> Unit
) {
    val despesaState by despesaViewModel.uiState.collectAsStateWithLifecycle()
    val categoriaState by categoriaViewModel.uiState.collectAsStateWithLifecycle()

    val despesa = despesaState.despesas.firstOrNull { it.id == id }
    val categorias = categoriaState.categorias

    var titulo by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }

    LaunchedEffect(despesa) {
        if (despesa != null) {
            titulo = despesa.titulo
            valor = despesa.valor.toString()
            data = despesa.data
            descricao = despesa.descricao
            categoriaId = despesa.categoriaId.toString()
        }
    }

    TelaBase("Detalhes da despesa", onVoltar) {
        if (despesa == null) {
            Text("Carregando despesa ou item não encontrado.")
        } else {
            Text(
                "Editar despesa #$id",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = categoriaId,
                onValueChange = { categoriaId = it },
                label = { Text("ID da categoria") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Categorias: " + categorias.joinToString {
                    "${it.id} - ${it.nome}"
                }
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

            Text(
                "Categoria atual: ${
                    categorias.firstOrNull { it.id == despesa.categoriaId }?.nome
                        ?: "não encontrada"
                }"
            )

            if (mensagem.isNotBlank()) {
                Text(mensagem)
            }

            Button(
                onClick = {
                    val novoValor = valor.replace(",", ".").toDoubleOrNull()
                    val novoCatId = categoriaId.toIntOrNull()

                    if (
                        titulo.isBlank() ||
                        novoValor == null ||
                        novoValor <= 0 ||
                        novoCatId == null ||
                        data.isBlank()
                    ) {
                        mensagem = "Confira os campos obrigatórios."
                    } else if (categorias.none { it.id == novoCatId }) {
                        mensagem = "O ID informado não pertence a uma categoria."
                    } else {
                        despesaViewModel.atualizar(
                            Despesa(
                                id = id,
                                titulo = titulo.trim(),
                                valor = novoValor,
                                categoriaId = novoCatId,
                                data = data.trim(),
                                descricao = descricao.trim()
                            )
                        )
                        mensagem = "Despesa atualizada com sucesso."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar alterações")
            }
        }
    }
}
