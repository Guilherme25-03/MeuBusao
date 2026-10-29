# Meu Busão — Controle de despesas

Projeto acadêmico Android feito com Kotlin, Jetpack Compose, Navigation Compose e Room.

## Telas
1. Início — resumo e atalhos.
2. Despesas — cadastrar, listar, excluir e abrir detalhes.
3. Categorias — cadastrar, listar, excluir e abrir detalhes.
4. Detalhes da despesa — consultar por ID e editar.
5. Detalhes da categoria — consultar por ID, editar e calcular a soma das despesas vinculadas.
6. Relatórios — total geral e total por categoria.
7. Configurações — instruções de uso.

## Tecnologias
- Kotlin e Jetpack Compose
- `NavHost`, `NavController`, objeto `Rotas` e `NavigationBar`
- Room: `Categoria`, `Despesa`, `CategoriaDao`, `DespesaDao` e `AppDatabase`
- KSP para gerar as implementações Room
- Chamadas suspend executadas em `Dispatchers.IO`

## Como abrir no Android Studio
1. Extraia o ZIP para uma pasta fácil de localizar.
2. Abra o Android Studio.
3. Clique em **Open** e selecione a pasta `MeuBusao` extraída (a pasta que contém `settings.gradle.kts`).
4. Aguarde a sincronização do Gradle. Se o Android Studio solicitar a instalação do SDK Android 35, aceite.
5. Crie/inicie um emulador pelo Device Manager ou conecte um Android com depuração USB ativada.
6. Clique em **Run ▶**.

Requisitos sugeridos: Android Studio recente, JDK 17 e Android SDK 35. O Android Studio pode precisar baixar dependências na primeira sincronização.

## Como testar
1. Abra **Categorias** e cadastre “Transporte”, com descrição “Ônibus e deslocamentos”.
2. Anote o ID exibido no cartão.
3. Abra **Despesas** e cadastre “Passagem”, valor `6,00`, o ID da categoria e uma data.
4. Toque no cartão da despesa, altere um campo e salve.
5. Abra o detalhe da categoria e confira o total calculado.
6. Exclua uma despesa e confira os relatórios.
7. Feche o aplicativo completamente e abra novamente: os registros devem continuar salvos.

## Organização do código
- `data/Models.kt`: entidades Room.
- `data/Daos.kt`: operações de inserção, consulta, atualização e exclusão.
- `data/AppDatabase.kt`: banco Room singleton.
- `MainActivity.kt`: rotas, telas e componentes Compose.

## Decisão de complexidade extra
O detalhe da categoria consulta as despesas associadas ao seu ID e calcula o valor total gasto nessa categoria. O detalhe da despesa também permite editar os dados do item selecionado.

## Documentação do processo — completar antes de entregar
**Importante:** o grupo precisa substituir esta seção por suas decisões reais e adicionar capturas de tela próprias do aplicativo funcionando. Não invente etapas que não aconteceram.

### Evolução do Trabalho 1 para o Trabalho 2 e 3
- Trabalho 1: [descrever as telas e o planejamento que o grupo realmente fez].
- Trabalho 2: [descrever a navegação e as listas em memória].
- Trabalho 3: [descrever a migração da lista em memória para Room].

### Decisões de organização
[Explicar como as rotas foram organizadas e como os dados são consultados.]

### Dificuldades e soluções
[Cada integrante deve registrar uma dificuldade real e como resolveu.]

### Evidências visuais obrigatórias
Adicionar imagens próprias, por exemplo:
- `docs/01-inicio.png`
- `docs/02-categorias.png`
- `docs/03-despesas.png`
- `docs/04-detalhe-despesa.png`
- `docs/05-detalhe-categoria.png`
- `docs/06-relatorios.png`
- `docs/07-persistencia.png` — mostrar que os dados continuam depois de fechar e reabrir o app.

Crie a pasta `docs/` e coloque as capturas reais nela. A documentação sem imagens do app funcionando está incompleta para os requisitos do professor.

## GitHub
Crie um **novo repositório** para esta entrega. Na pasta do projeto, use Git para adicionar os arquivos e enviar o projeto. Confirme o prazo com o professor; não faça commits depois do prazo estabelecido em sala.
