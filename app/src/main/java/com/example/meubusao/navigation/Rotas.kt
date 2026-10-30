package com.example.meubusao.navigation

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
