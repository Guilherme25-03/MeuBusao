
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.meubusao.data.AppDatabase
import com.example.meubusao.util.dinheiro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    onDespesas: () -> Unit,
    onCategorias: () -> Unit,
    onRelatorios: () -> Unit,
    db: AppDatabase
) {
    var total by remember { mutableDoubleStateOf(0.0) }
    var quantidade by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val despesas = withContext(Dispatchers.IO) {
            db.despesaDao().buscarTodos()
        }

        total = despesas.sumOf { it.valor }
        quantidade = despesas.size
    }

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
