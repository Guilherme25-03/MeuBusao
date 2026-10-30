
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DetalheDespesaScreen(
    id: Int,
    db: AppDatabase,
    onVoltar: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var despesa by remember { mutableStateOf<Despesa?>(null) }
    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    var titulo by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }

    suspend fun carregar() {
        val resultado = withContext(Dispatchers.IO) {
            Pair(
                db.despesaDao().buscarPorId(id),
                db.categoriaDao().buscarTodos()
            )
        }

        despesa = resultado.first
        categorias = resultado.second

        resultado.first?.let { item ->
            titulo = item.titulo
            valor = item.valor.toString()
            data = item.data
            descricao = item.descricao
            categoriaId = item.categoriaId.toString()
        }
    }

    LaunchedEffect(id) {
        carregar()
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
                titulo,
                { titulo = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                valor,
                { valor = it },
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                categoriaId,
                { categoriaId = it },
                label = { Text("ID da categoria") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                data,
                { data = it },
                label = { Text("Data") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                descricao,
                { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Categoria: ${
                    categorias.firstOrNull { it.id == despesa!!.categoriaId }?.nome
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
                        novoCatId == null ||
                        data.isBlank()
                    ) {
                        mensagem = "Confira os campos obrigatórios."
                    } else {
                        scope.launch {
                            val cat = withContext(Dispatchers.IO) {
                                db.categoriaDao().buscarPorId(novoCatId)
                            }

                            if (cat != null) {
                                withContext(Dispatchers.IO) {
                                    db.despesaDao().atualizar(
                                        Despesa(
                                            id = id,
                                            titulo = titulo.trim(),
                                            valor = novoValor,
                                            categoriaId = novoCatId,
                                            data = data.trim(),
                                            descricao = descricao.trim()
                                        )
                                    )
                                }

                                carregar()
                                mensagem = "Despesa atualizada com sucesso."
                            } else {
                                mensagem = "O ID informado não pertence a uma categoria."
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar alterações")
            }
        }
    }
}
