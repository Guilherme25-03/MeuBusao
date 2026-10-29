package com.example.meubusao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.meubusao.data.AppDatabase
import com.example.meubusao.data.Categoria
import com.example.meubusao.data.Despesa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

object Rotas {
    const val INICIO = "inicio"
    const val DESPESAS = "despesas"
    const val CATEGORIAS = "categorias"
    const val DETALHE_DESPESA = "detalheDespesa/{id}"
    const val DETALHE_CATEGORIA = "detalheCategoria/{id}"
    const val RELATORIOS = "relatorios"
    const val CONFIGURACOES = "configuracoes"
    fun detalheDespesa(id: Int) = "detalheDespesa/$id"
    fun detalheCategoria(id: Int) = "detalheCategoria/$id"
}

private data class Aba(val rota: String, val titulo: String, val icone: androidx.compose.ui.graphics.vector.ImageVector)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MeuBusaoApp()
            }
        }
    }
}

@Composable
fun MeuBusaoApp() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
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
    val mostrarBarra = rotaAtual in abas.map { it.rota }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar {
                    abas.forEach { aba ->
                        NavigationBarItem(
                            selected = rotaAtual == aba.rota,
                            onClick = { navController.navigate(aba.rota) {
                                popUpTo(Rotas.INICIO) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            } },
                            icon = { Icon(aba.icone, contentDescription = aba.titulo) },
                            label = { Text(aba.titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rotas.INICIO) {
                InicioScreen(
                    onDespesas = { navController.navigate(Rotas.DESPESAS) },
                    onCategorias = { navController.navigate(Rotas.CATEGORIAS) },
                    onRelatorios = { navController.navigate(Rotas.RELATORIOS) },
                    db = db
                )
            }
            composable(Rotas.DESPESAS) {
                DespesasScreen(
                    db = db,
                    onDetalhe = { id -> navController.navigate(Rotas.detalheDespesa(id)) }
                )
            }
            composable(Rotas.CATEGORIAS) {
                CategoriasScreen(
                    db = db,
                    onDetalhe = { id -> navController.navigate(Rotas.detalheCategoria(id)) }
                )
            }
            composable(
                route = Rotas.DETALHE_DESPESA,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                DetalheDespesaScreen(
                    id = entry.arguments?.getInt("id") ?: -1,
                    db = db,
                    onVoltar = { navController.popBackStack() }
                )
            }
            composable(
                route = Rotas.DETALHE_CATEGORIA,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                DetalheCategoriaScreen(
                    id = entry.arguments?.getInt("id") ?: -1,
                    db = db,
                    onVoltar = { navController.popBackStack() }
                )
            }
            composable(Rotas.RELATORIOS) { RelatoriosScreen(db) }
            composable(Rotas.CONFIGURACOES) { ConfiguracoesScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TelaBase(titulo: String, onVoltar: (() -> Unit)? = null, conteudo: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize()) {
        if (onVoltar != null) {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        } else {
            Text(titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
        }
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp), content = conteudo)
    }
}

@Composable
private fun InicioScreen(
    onDespesas: () -> Unit,
    onCategorias: () -> Unit,
    onRelatorios: () -> Unit,
    db: AppDatabase
) {
    var total by remember { mutableDoubleStateOf(0.0) }
    var quantidade by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val despesas = db.despesaDao().buscarTodos()
            withContext(Dispatchers.Main) {
                total = despesas.sumOf { it.valor }
                quantidade = despesas.size
            }
        }
    }
    TelaBase("Meu Busão") {
        Text("Controle simples dos gastos com transporte e do dia a dia.")
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Total de despesas cadastradas")
                Text("R$ ${dinheiro(total)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("$quantidade despesa(s)")
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onDespesas, modifier = Modifier.fillMaxWidth()) { Text("Ver despesas") }
        OutlinedButton(onClick = onCategorias, modifier = Modifier.fillMaxWidth()) { Text("Ver categorias") }
        OutlinedButton(onClick = onRelatorios, modifier = Modifier.fillMaxWidth()) { Text("Abrir relatórios") }
        Spacer(Modifier.height(8.dp))
        Text("Dica: cadastre uma categoria antes de adicionar uma despesa.")
    }
}

@Composable
private fun DespesasScreen(db: AppDatabase, onDetalhe: (Int) -> Unit) {
    var despesas by remember { mutableStateOf(listOf<Despesa>()) }
    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    var titulo by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }

    fun atualizar() {
        CoroutineScope(Dispatchers.IO).launch {
            val lista = db.despesaDao().buscarTodos()
            val cats = db.categoriaDao().buscarTodos()
            withContext(Dispatchers.Main) {
                despesas = lista
                categorias = cats
            }
        }
    }
    LaunchedEffect(Unit) { atualizar() }

    TelaBase("Despesas") {
        Text("Nova despesa", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(titulo, { titulo = it }, label = { Text("Nome (ex.: passagem)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(valor, { valor = it }, label = { Text("Valor (ex.: 6,00)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(categoriaId, { categoriaId = it }, label = { Text("ID da categoria") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(data, { data = it }, label = { Text("Data (ex.: 28/10/2026)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(descricao, { descricao = it }, label = { Text("Descrição (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        if (categorias.isEmpty()) Text("Cadastre uma categoria primeiro para saber seu ID.", color = MaterialTheme.colorScheme.error)
        if (erro.isNotBlank()) Text(erro, color = MaterialTheme.colorScheme.error)
        Button(
            onClick = {
                val numero = valor.replace(",", ".").toDoubleOrNull()
                val catId = categoriaId.toIntOrNull()
                if (titulo.isBlank() || numero == null || numero < 0 || catId == null || data.isBlank()) {
                    erro = "Preencha nome, valor válido, ID da categoria e data."
                } else {
                    erro = ""
                    CoroutineScope(Dispatchers.IO).launch {
                        val existe = db.categoriaDao().buscarPorId(catId)
                        if (existe != null) {
                            db.despesaDao().inserir(Despesa(titulo = titulo.trim(), valor = numero, categoriaId = catId, data = data.trim(), descricao = descricao.trim()))
                            val lista = db.despesaDao().buscarTodos()
                            withContext(Dispatchers.Main) {
                                despesas = lista
                                titulo = ""; valor = ""; categoriaId = ""; data = ""; descricao = ""
                            }
                        } else {
                            withContext(Dispatchers.Main) { erro = "Não existe categoria com esse ID." }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Adicionar despesa") }
        HorizontalDivider(Modifier.padding(vertical = 10.dp))
        Text("Despesas cadastradas (${despesas.size})", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(despesas, key = { it.id }) { despesa ->
                Card(Modifier.fillMaxWidth().clickable { onDetalhe(despesa.id) }) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(despesa.titulo, fontWeight = FontWeight.Bold)
                            Text("${despesa.data} • Categoria ID ${despesa.categoriaId}")
                            Text("R$ ${dinheiro(despesa.valor)}")
                        }
                        TextButton(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                db.despesaDao().excluir(despesa)
                                val lista = db.despesaDao().buscarTodos()
                                withContext(Dispatchers.Main) { despesas = lista }
                            }
                        }) { Text("Excluir") }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriasScreen(db: AppDatabase, onDetalhe: (Int) -> Unit) {
    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }

    fun atualizar() {
        CoroutineScope(Dispatchers.IO).launch {
            val lista = db.categoriaDao().buscarTodos()
            withContext(Dispatchers.Main) { categorias = lista }
        }
    }
    LaunchedEffect(Unit) { atualizar() }

    TelaBase("Categorias") {
        Text("Nova categoria", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(nome, { nome = it }, label = { Text("Nome (ex.: Transporte)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(descricao, { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        if (erro.isNotBlank()) Text(erro, color = MaterialTheme.colorScheme.error)
        Button(onClick = {
            if (nome.isBlank()) erro = "Informe o nome da categoria."
            else {
                erro = ""
                CoroutineScope(Dispatchers.IO).launch {
                    db.categoriaDao().inserir(Categoria(nome = nome.trim(), descricao = descricao.trim()))
                    val lista = db.categoriaDao().buscarTodos()
                    withContext(Dispatchers.Main) { categorias = lista; nome = ""; descricao = "" }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Adicionar categoria") }
        HorizontalDivider(Modifier.padding(vertical = 10.dp))
        Text("Toque no cartão para ver os detalhes e o total gasto.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(categorias, key = { it.id }) { categoria ->
                Card(Modifier.fillMaxWidth().clickable { onDetalhe(categoria.id) }) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(categoria.nome, fontWeight = FontWeight.Bold)
                            Text(categoria.descricao.ifBlank { "Sem descrição" })
                            Text("ID: ${categoria.id}")
                        }
                        TextButton(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val relacionadas = db.despesaDao().buscarPorCategoria(categoria.id)
                                // Para manter a relação coerente, não exclui categoria que ainda tem despesas.
                                if (relacionadas.isEmpty()) {
                                    db.categoriaDao().excluir(categoria)
                                    val lista = db.categoriaDao().buscarTodos()
                                    withContext(Dispatchers.Main) { categorias = lista }
                                } else {
                                    withContext(Dispatchers.Main) { erro = "Essa categoria tem despesas. Remova-as antes." }
                                }
                            }
                        }) { Text("Excluir") }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetalheDespesaScreen(id: Int, db: AppDatabase, onVoltar: () -> Unit) {
    var despesa by remember { mutableStateOf<Despesa?>(null) }
    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    var titulo by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }

    fun carregar() {
        CoroutineScope(Dispatchers.IO).launch {
            val item = db.despesaDao().buscarPorId(id)
            val cats = db.categoriaDao().buscarTodos()
            withContext(Dispatchers.Main) {
                despesa = item
                categorias = cats
                if (item != null) {
                    titulo = item.titulo; valor = item.valor.toString(); data = item.data
                    descricao = item.descricao; categoriaId = item.categoriaId.toString()
                }
            }
        }
    }
    LaunchedEffect(id) { carregar() }

    TelaBase("Detalhes da despesa", onVoltar) {
        if (despesa == null) {
            Text("Carregando despesa ou item não encontrado.")
        } else {
            Text("Editar despesa #$id", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(titulo, { titulo = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(valor, { valor = it }, label = { Text("Valor") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(categoriaId, { categoriaId = it }, label = { Text("ID da categoria") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(data, { data = it }, label = { Text("Data") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(descricao, { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
            Text("Categoria: ${categorias.firstOrNull { it.id == despesa!!.categoriaId }?.nome ?: "não encontrada"}")
            if (mensagem.isNotBlank()) Text(mensagem)
            Button(onClick = {
                val novoValor = valor.replace(",", ".").toDoubleOrNull()
                val novoCatId = categoriaId.toIntOrNull()
                if (titulo.isBlank() || novoValor == null || novoCatId == null || data.isBlank()) {
                    mensagem = "Confira os campos obrigatórios."
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        val cat = db.categoriaDao().buscarPorId(novoCatId)
                        if (cat != null) {
                            db.despesaDao().atualizar(Despesa(id = id, titulo = titulo.trim(), valor = novoValor, categoriaId = novoCatId, data = data.trim(), descricao = descricao.trim()))
                            val itemAtualizado = db.despesaDao().buscarPorId(id)
                            withContext(Dispatchers.Main) { despesa = itemAtualizado; mensagem = "Despesa atualizada com sucesso." }
                        } else withContext(Dispatchers.Main) { mensagem = "O ID informado não pertence a uma categoria." }
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Salvar alterações") }
        }
    }
}

@Composable
private fun DetalheCategoriaScreen(id: Int, db: AppDatabase, onVoltar: () -> Unit) {
    var categoria by remember { mutableStateOf<Categoria?>(null) }
    var despesas by remember { mutableStateOf(listOf<Despesa>()) }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }

    fun carregar() {
        CoroutineScope(Dispatchers.IO).launch {
            val cat = db.categoriaDao().buscarPorId(id)
            val lista = db.despesaDao().buscarPorCategoria(id)
            withContext(Dispatchers.Main) {
                categoria = cat
                despesas = lista
                if (cat != null) { nome = cat.nome; descricao = cat.descricao }
            }
        }
    }
    LaunchedEffect(id) { carregar() }

    TelaBase("Detalhes da categoria", onVoltar) {
        if (categoria == null) Text("Carregando categoria ou item não encontrado.")
        else {
            Text("Categoria #$id", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(nome, { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(descricao, { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
            Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Gasto total nesta categoria", style = MaterialTheme.typography.titleMedium)
                    Text("R$ ${dinheiro(despesas.sumOf { it.valor })}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("${despesas.size} despesa(s) relacionada(s)")
                }
            }
            Text("Despesas desta categoria", modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold)
            despesas.forEach { Text("• ${it.titulo} — R$ ${dinheiro(it.valor)} (${it.data})") }
            if (mensagem.isNotBlank()) Text(mensagem)
            Button(onClick = {
                if (nome.isBlank()) mensagem = "Informe o nome."
                else CoroutineScope(Dispatchers.IO).launch {
                    db.categoriaDao().atualizar(Categoria(id = id, nome = nome.trim(), descricao = descricao.trim()))
                    val atualizado = db.categoriaDao().buscarPorId(id)
                    withContext(Dispatchers.Main) { categoria = atualizado; mensagem = "Categoria atualizada." }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Salvar categoria") }
        }
    }
}

@Composable
private fun RelatoriosScreen(db: AppDatabase) {
    var despesas by remember { mutableStateOf(listOf<Despesa>()) }
    var categorias by remember { mutableStateOf(listOf<Categoria>()) }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val ds = db.despesaDao().buscarTodos()
            val cs = db.categoriaDao().buscarTodos()
            withContext(Dispatchers.Main) { despesas = ds; categorias = cs }
        }
    }
    TelaBase("Relatórios") {
        Text("Resumo por categoria", style = MaterialTheme.typography.titleLarge)
        Text("Total geral: R$ ${dinheiro(despesas.sumOf { it.valor })}", style = MaterialTheme.typography.headlineSmall)
        if (categorias.isEmpty()) Text("Cadastre categorias e despesas para ver o relatório.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categorias, key = { it.id }) { categoria ->
                val total = despesas.filter { it.categoriaId == categoria.id }.sumOf { it.valor }
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(categoria.nome)
                        Text("R$ ${dinheiro(total)}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfiguracoesScreen() {
    TelaBase("Configurações") {
        Text("Meu Busão", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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
        Text("Os dados são salvos localmente com Room e permanecem depois de fechar o aplicativo.")
    }
}

private fun dinheiro(valor: Double): String =
    String.format(Locale("pt", "BR"), "%.2f", valor)
