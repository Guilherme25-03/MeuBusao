package com.example.meubusao.util

import java.util.Locale

fun dinheiro(valor: Double): String {
    return String.format(Locale("pt", "BR"), "%.2f", valor)
}
