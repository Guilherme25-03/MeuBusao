
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.meubusao.util.dinheiro
import com.example.meubusao.viewmodel.CategoriaViewModel
import com.example.meubusao.viewmodel.DespesaViewModel

@Composable
fun RelatoriosScreen(
    despesaViewModel: DespesaViewModel,
    categoriaViewModel: CategoriaViewModel
) {
    val despesaState by despesaViewModel.uiState.collectAsStateWithLifecycle()
    val categoriaState by categoriaViewModel.uiState.collectAsStateWithLifecycle()

    val despesas = despesaState.despesas
    val categorias = categoriaState.categorias

    TelaBase("Relatórios") {
        Text(
            "Resumo por categoria",
            style = MaterialTheme.typography.titleLarge
        )

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
