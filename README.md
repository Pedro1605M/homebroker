# 📈 Simulador de Home Broker

Aplicação desktop desenvolvida em Java que simula operações de compra e venda de ações utilizando dados reais de mercado via API, com persistência em banco de dados local.

## 🚀 Tecnologias Utilizadas

- Java 17+
- JavaFX 17 (interface gráfica com Scene Builder)
- JavaFX Charts (gráficos financeiros)
- SQLite (banco de dados local via `sqlite-jdbc`)
- API Alpha Vantage (dados reais de mercado)
- JSON (`org.json`) para manipulação das respostas da API
- Programação Orientada a Objetos

## 🔐 Sistema de Autenticação

✔️ Tela de cadastro de novos usuários (nome, e-mail, senha)  
✔️ Tela de login com verificação de credenciais no banco  
✔️ Sessão do usuário mantida globalmente durante o uso (`Sessao.java`)  
✔️ Logout disponível na tela "Minha Conta"  
✔️ Conta bancária criada automaticamente ao cadastrar  

## 📊 Funcionalidades

✔️ Busca de preços de ações via API Alpha Vantage  
✔️ Fallback automático com dados simulados (caso a API falhe ou o limite seja atingido)  
✔️ Cache de 1 hora para evitar requisições repetidas  
✔️ Compra de ações com débito automático do saldo  
✔️ Venda de ações com crédito automático do saldo  
✔️ Verificação de saldo disponível antes de comprar  
✔️ Verificação de quantidade de ações disponíveis antes de vender  
✔️ Gráfico financeiro de histórico de preços em tempo real (JavaFX Charts)  
✔️ Tela de histórico de operações (COMPRA/VENDA) com tabela formatada  
✔️ Tela "Minha Conta" com carteira de ações atual (posição líquida)  
✔️ Depósito de saldo na conta  
✔️ Persistência completa de dados no SQLite (usuários, contas, operações)  

## 🗄️ Banco de Dados

Os dados são armazenados localmente no arquivo `homebroker.db` com três tabelas:

| Tabela | Descrição |
|--------|-----------|
| `users` | Cadastro de usuários (nome, e-mail, senha) |
| `accounts` | Conta bancária vinculada ao usuário (saldo) |
| `operations` | Registro de todas as operações de compra e venda |

## 🧠 Funcionamento do Sistema

1. O usuário se cadastra ou faz login — a sessão é armazenada em `Sessao.java`
2. Na tela principal, digita o símbolo de uma ação (ex: `PETR4`) e clica em "Escolher"
3. A aplicação busca dados na API Alpha Vantage; se falhar, usa dados simulados
4. O gráfico de preços históricos é exibido com JavaFX Charts
5. O usuário seleciona a quantidade e compra/vende — operação salva no SQLite
6. O histórico completo de operações pode ser consultado na tela de Histórico
7. A carteira com posição atual de cada ação é exibida em "Minha Conta"

## ▶️ Como Rodar o Projeto

### Pré-requisito
- Ter o **JDK 17 ou superior** instalado → [Download aqui](https://adoptium.net)

---

### Opção 1 — VS Code (recomendado)

1. Abra a pasta do projeto no VS Code
2. Instale a extensão **"Extension Pack for Java"** (se não tiver)
3. Pressione **`F5`** ou vá em **Run → Start Debugging**
4. Selecione **"Run HomeBroker"**

O VS Code compila automaticamente e já abre o app.

---

### Opção 2 — Terminal (`.bat`)

Abra o terminal na pasta do projeto e execute:

**1ª vez ou após modificar o código:**
```bash
.\build.bat
```
Compila, copia os recursos e já abre o app.

**Nas próximas vezes (sem mudanças no código):**
```bash
.\run.bat
```
Abre o app direto, sem recompilar.

> Você também pode dar **duplo clique** nos arquivos `.bat` no Explorador de Arquivos.

---

## 📌 Status do Projeto

Projeto em desenvolvimento ativo.

As funcionalidades principais estão implementadas e operacionais: cadastro, login, integração com API, compra/venda de ações, histórico de operações, carteira e geração de gráficos.

Alguns ajustes ainda estão sendo realizados, principalmente relacionados à estabilidade do gráfico e melhorias na experiência do usuário.

## 🔮 Melhorias Futuras

- Melhorar a estabilidade do gráfico (correção de pequenos bugs visuais)
- Otimizar atualização em tempo real
- Criptografar senhas armazenadas no banco (atualmente em texto puro)
- Melhorar tratamento de exceções
- Implementar testes unitários
- Suporte multiplataforma (Mac/Linux) com JARs do JavaFX adequados
