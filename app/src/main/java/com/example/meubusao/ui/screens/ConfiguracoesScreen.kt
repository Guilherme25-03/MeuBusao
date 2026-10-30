
package com.example.meubusao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConfiguracoesScreen() {
    TelaBase("Configurações") {
        Text(
            "Meu Busão",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text("Aplicativo acadêmico para registrar despesas e categorias.")

        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Como usar", fontWeight = FontWeight.Bold)
                Text("1. Cadastre uma categoria.")
                Text("2. Anote o ID que aparece no cartão.")
                Text("3. Cadastre despesas usando esse ID.")
                Text("4. Toque nos cartões para editar e consultar detalhes.")
                Text("5. Consulte o total em Relatórios.")
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Os dados são salvos localmente com Room e permanecem depois de fechar o aplicativo."
        )
    }
}
