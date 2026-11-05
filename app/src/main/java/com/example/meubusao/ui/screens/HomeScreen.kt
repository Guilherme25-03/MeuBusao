
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.meubusao.util.dinheiro
import com.example.meubusao.viewmodel.DespesaViewModel

@Composable
fun HomeScreen(
    onDespesas: () -> Unit,
    onCategorias: () -> Unit,
    onRelatorios: () -> Unit,
    despesaViewModel: DespesaViewModel
) {
    val despesaState by despesaViewModel.uiState.collectAsStateWithLifecycle()

    val despesas = despesaState.despesas
    val total = despesas.sumOf { it.valor }
    val quantidade = despesas.size

    TelaBase("Meu Busão") {
        Text("Controle simples dos gastos com transporte e do dia a dia.")

        Spacer(Modifier.height(16.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Total de despesas cadastradas")

                Text(
                    "R$ ${dinheiro(total)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text("$quantidade despesa(s)")
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onDespesas,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver despesas")
        }

        OutlinedButton(
            onClick = onCategorias,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver categorias")
        }

        OutlinedButton(
            onClick = onRelatorios,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abrir relatórios")
        }

        Spacer(Modifier.height(8.dp))

        Text("Dica: cadastre uma categoria antes de adicionar uma despesa.")
    }
}
