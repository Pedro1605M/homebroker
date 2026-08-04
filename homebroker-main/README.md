# 📈 Simulador de Home Broker

Aplicação desktop desenvolvida em Java que simula operações de compra e venda de ações utilizando dados reais de mercado em tempo real.

## 🚀 Tecnologias Utilizadas

- Java
- JavaFX (Scene Builder)
- JavaFX Charts
- API Alpha Vantage
- Manipulação de JSON
- Programação Orientada a Objetos

## 🔐 Sistema de Login

✔️ Sistema de autenticação implementado  
✔️ Verificação de credenciais  
✔️ Controle de acesso ao sistema  
✔️ Fluxo de navegação após login validado  

## 📊 Funcionalidades

✔️ Simulação de compra e venda de ações  
✔️ Atualização dinâmica de preços via API  
✔️ Conversão e manipulação de dados JSON  
✔️ Armazenamento interno dos dados em listas  
✔️ Cálculo automático de lucro ou prejuízo  
✔️ Geração de gráfico financeiro em tempo real  

## 🧠 Funcionamento do Sistema

Os dados são obtidos através da API Alpha Vantage em formato JSON.  
Esses dados são processados e armazenados em listas internas, que são atualizadas conforme novas informações são recebidas.  

O gráfico é construído utilizando JavaFX Charts, sendo atualizado dinamicamente a partir da estrutura de dados mantida na aplicação.


## 📌 Status do Projeto

Projeto em desenvolvimento ativo.

As funcionalidades principais estão implementadas e operacionais (login, integração com API, simulação de compra e venda e geração de gráficos).

Alguns ajustes ainda estão sendo realizados, principalmente relacionados à estabilidade do gráfico e melhorias na experiência do usuário.

## 🔮 Melhorias Futuras

- Melhorar a estabilidade do gráfico (correção de pequenos bugs visuais)
- Otimizar atualização em tempo real
- Implementar persistência de dados (armazenamento de operações)
- Melhorar tratamento de exceções
- Implementar testes unitários
