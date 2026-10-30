
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
import com.example.meubusao.data.Despesa
import com.example.meubusao.util.dinheiro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DespesasScreen(
    db: AppDatabase,
    onDetalhe: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    var despesas by remember { mutableStateOf(listOf<Despesa>()) }
    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    var titulo by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }

    suspend fun atualizar() {
        val resultado = withContext(Dispatchers.IO) {
            Pair(
                db.despesaDao().buscarTodos(),
                db.categoriaDao().buscarTodos()
            )
        }

        despesas = resultado.first
        categorias = resultado.second
    }

    LaunchedEffect(Unit) {
        atualizar()
    }

    TelaBase("Despesas") {
        Text("Nova despesa", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            titulo,
            { titulo = it },
            label = { Text("Nome (ex.: passagem)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            valor,
            { valor = it },
            label = { Text("Valor (ex.: 6,00)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            categoriaId,
            { categoriaId = it },
            label = { Text("ID da categoria") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            data,
            { data = it },
            label = { Text("Data (ex.: 28/10/2026)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            descricao,
            { descricao = it },
            label = { Text("Descrição (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (categorias.isEmpty()) {
            Text(
                "Cadastre uma categoria primeiro para saber seu ID.",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (erro.isNotBlank()) {
            Text(erro, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val numero = valor.replace(",", ".").toDoubleOrNull()
                val catId = categoriaId.toIntOrNull()

                if (
                    titulo.isBlank() ||
                    numero == null ||
                    numero < 0 ||
                    catId == null ||
                    data.isBlank()
                ) {
                    erro = "Preencha nome, valor válido, ID da categoria e data."
                } else {
                    erro = ""

                    scope.launch {
                        val existe = withContext(Dispatchers.IO) {
                            db.categoriaDao().buscarPorId(catId)
                        }

                        if (existe != null) {
                            withContext(Dispatchers.IO) {
                                db.despesaDao().inserir(
                                    Despesa(
                                        titulo = titulo.trim(),
                                        valor = numero,
                                        categoriaId = catId,
                                        data = data.trim(),
                                        descricao = descricao.trim()
                                    )
                                )
                            }

                            titulo = ""
                            valor = ""
                            categoriaId = ""
                            data = ""
                            descricao = ""

                            atualizar()
                        } else {
                            erro = "Não existe categoria com esse ID."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar despesa")
        }

        HorizontalDivider(Modifier.padding(vertical = 10.dp))

        Text(
            "Despesas cadastradas (${despesas.size})",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(despesas, key = { it.id }) { despesa ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onDetalhe(despesa.id) }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(despesa.titulo, fontWeight = FontWeight.Bold)
                            Text("${despesa.data} • Categoria ID ${despesa.categoriaId}")
                            Text("R$ ${dinheiro(despesa.valor)}")
                        }

                        TextButton(
                            onClick = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.despesaDao().excluir(despesa)
                                    }
                                    atualizar()
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
