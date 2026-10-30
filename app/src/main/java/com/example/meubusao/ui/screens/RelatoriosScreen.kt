
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.withContext

@Composable
fun RelatoriosScreen(db: AppDatabase) {
    var despesas by remember { mutableStateOf(listOf<Despesa>()) }
    var categorias by remember { mutableStateOf(listOf<Categoria>()) }

    LaunchedEffect(Unit) {
        val resultado = withContext(Dispatchers.IO) {
            Pair(
                db.despesaDao().buscarTodos(),
                db.categoriaDao().buscarTodos()
            )
        }

        despesas = resultado.first
        categorias = resultado.second
    }

    TelaBase("Relatórios") {
        Text("Resumo por categoria", style = MaterialTheme.typography.titleLarge)

        Text(
            "Total geral: R$ ${dinheiro(despesas.sumOf { it.valor })}",
            style = MaterialTheme.typography.headlineSmall
        )

        if (categorias.isEmpty()) {
            Text("Cadastre categorias e despesas para ver o relatório.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categorias, key = { it.id }) { categoria ->
                val total = despesas
                    .filter { it.categoriaId == categoria.id }
                    .sumOf { it.valor }

                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(categoria.nome)

                        Text(
                            "R$ ${dinheiro(total)}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
