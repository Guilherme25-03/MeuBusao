
package com.example.meubusao.navigation

import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.meubusao.data.AppDatabase
import com.example.meubusao.data.repository.CategoriaRepository
import com.example.meubusao.data.repository.DespesaRepository
import com.example.meubusao.ui.screens.*
import com.example.meubusao.viewmodel.*

private data class Aba(
    val rota: String,
    val titulo: String,
    val icone: ImageVector
)

@Composable
fun MeuBusaoApp() {
    val context = LocalContext.current

    val db = remember {
        AppDatabase.getDatabase(context)
    }

    val categoriaRepository = remember {
        CategoriaRepository(db.categoriaDao())
    }

    val despesaRepository = remember {
        DespesaRepository(db.despesaDao())
    }

    val categoriaViewModel: CategoriaViewModel = viewModel(
        factory = CategoriaViewModelFactory(categoriaRepository)
    )

    val despesaViewModel: DespesaViewModel = viewModel(
        factory = DespesaViewModelFactory(despesaRepository)
    )

    val navController = rememberNavController()

    val abas = listOf(
        Aba(Rotas.INICIO, "Início", Icons.Default.Home),
        Aba(Rotas.DESPESAS, "Despesas", Icons.Default.ReceiptLong),
        Aba(Rotas.CATEGORIAS, "Categorias", Icons.Default.Category),
        Aba(Rotas.RELATORIOS, "Relatórios", Icons.Default.Assessment),
        Aba(Rotas.CONFIGURACOES, "Config.", Icons.Default.Settings)
    )

    val entrada by navController.currentBackStackEntryAsState()
    val rotaAtual = entrada?.destination?.route
    val mostrarBarra = abas.any { it.rota == rotaAtual }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar {
                    abas.forEach { aba ->
                        NavigationBarItem(
                            selected = rotaAtual == aba.rota,
                            onClick = {
                                navController.navigate(aba.rota) {
                                    popUpTo(Rotas.INICIO) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = aba.icone,
                                    contentDescription = aba.titulo
                                )
                            },
                            label = {
                                Text(aba.titulo)
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Rotas.INICIO) {
                HomeScreen(
                    onDespesas = {
                        navController.navigate(Rotas.DESPESAS)
                    },
                    onCategorias = {
                        navController.navigate(Rotas.CATEGORIAS)
                    },
                    onRelatorios = {
                        navController.navigate(Rotas.RELATORIOS)
                    },
                    despesaViewModel = despesaViewModel
                )
            }

            composable(Rotas.DESPESAS) {
                DespesasScreen(
                    despesaViewModel = despesaViewModel,
                    categoriaViewModel = categoriaViewModel,
                    onDetalhe = { id ->
                        navController.navigate(
                            Rotas.detalheDespesa(id)
                        )
                    }
                )
            }

            composable(Rotas.CATEGORIAS) {
                CategoriasScreen(
                    categoriaViewModel = categoriaViewModel,
                    despesaViewModel = despesaViewModel,
                    onDetalhe = { id ->
                        navController.navigate(
                            Rotas.detalheCategoria(id)
                        )
                    }
                )
            }

            composable(
                route = Rotas.DETALHE_DESPESA,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    }
                )
            ) { entry ->
                DetalheDespesaScreen(
                    id = entry.arguments?.getInt("id") ?: -1,
                    despesaViewModel = despesaViewModel,
                    categoriaViewModel = categoriaViewModel,
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Rotas.DETALHE_CATEGORIA,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    }
                )
            ) { entry ->
                DetalheCategoriaScreen(
                    id = entry.arguments?.getInt("id") ?: -1,
                    categoriaViewModel = categoriaViewModel,
                    despesaViewModel = despesaViewModel,
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Rotas.RELATORIOS) {
                RelatoriosScreen(
                    despesaViewModel = despesaViewModel,
                    categoriaViewModel = categoriaViewModel
                )
            }

            composable(Rotas.CONFIGURACOES) {
                ConfiguracoesScreen()
            }
        }
    }
}
