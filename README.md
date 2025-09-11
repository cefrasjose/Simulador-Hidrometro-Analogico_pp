# Simulador de Hidrômetro em Java

![Java](https://img.shields.io/badge/Java-11%2B-blue?logo=java&logoColor=white) ![IDE](https://img.shields.io/badge/IntelliJ%20IDEA-ready-brightgreen?logo=intellij-idea&logoColor=white) ![Build](https://img.shields.io/badge/Build-Maven-orange?logo=apache-maven&logoColor=white) ![License](https://img.shields.io/badge/License-MIT-yellow.svg)

## 📖 Visão Geral

Este projeto é um **Simulador de Hidrômetro Digital** desenvolvido em Java, utilizando Programação Orientada a Objetos. O objetivo é fornecer uma representação visual interativa e funcional de um hidrômetro, que pode ser integrada a outros softwares ou usada para testes e demonstrações, eliminando a necessidade de um dispositivo físico.

O simulador carrega configurações de um arquivo de propriedades, opera de forma contínua e exibe os dados em uma interface gráfica (GUI) no estilo de um medidor real. Além disso, ele é capaz de capturar e salvar as imagens da GUI em intervalos regulares, criando um histórico visual das leituras.

## ✨ Funcionalidades Principais

-   **Simulação de Consumo, Vazão e Pressão:** Contabiliza o volume de água consumido e simula a vazão e pressão na rede.
-   **Interface Gráfica Interativa:** Exibe a leitura, vazão e pressão em um painel visual que imita um hidrômetro real, com ponteiros animados.
-   **Captura de Tela:** A GUI é capturada e salva automaticamente como imagens (PNG/JPEG) em um diretório configurável, registrando o estado do hidrômetro ao longo do tempo.
-   **Configurável:** Todos os parâmetros da simulação (vazão, pressão, intervalos, probabilidades de eventos) são facilmente ajustáveis via arquivo `parametros.properties`.
-   **Simulação de Eventos:**
    -   **Falta de Água:** Eventos aleatórios de interrupção no fornecimento.
    -   **Presença de Ar:** Simula a passagem de ar, que afeta a medição do consumo.
-   **Estrutura Orientada a Objetos:** Código modular e de fácil manutenção.
-   **Operação Contínua:** Projetado para rodar indefinidamente.

---

## 📋 Requisitos do Projeto

### Requisitos Funcionais (RF)

| ID   | Requisito                                     | Descrição                                                                                                                                                                       |
| :--- | :-------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| RF01 | Simular Consumo de Água                       | O sistema deve simular o consumo de água com base em uma vazão de entrada configurável (0 a 100 m³/h). O volume total consumido deve ser continuamente contabilizado e acumulado. |
| RF02 | Gerar Imagem do Display na GUI                | O sistema deve exibir os dados do hidrômetro em uma interface gráfica que represente visualmente o display, mostrando volume, vazão e pressão.                                    |
| RF03 | Capturar Imagens da GUI                       | O sistema deve ser capaz de capturar a tela da interface gráfica em intervalos configuráveis e salvar essas capturas como arquivos de imagem (PNG ou JPEG).                      |
| RF04 | Configurar Simulação via Arquivo              | O simulador deve ler todos os seus parâmetros de funcionamento de um arquivo de texto `.properties` no momento da inicialização.                                                 |
| RF05 | Atualizar Display em Intervalos Definidos     | A atualização dos dados na GUI e a geração das imagens de captura devem ocorrer em intervalos configuráveis, seja por tempo (a cada X segundos).                              |
| RF06 | Simular Presença de Ar na Tubulação           | O sistema deve ser capaz de simular a passagem de ar, o que causa uma medição incorreta (maior) do volume. A fórmula ou fator de acréscimo deve ser configurável.               |
| RF07 | Simular Evento de Falta de Água               | O sistema deve ser capaz de simular eventos aleatórios de falta de água, nos quais a vazão e pressão de água devem ser temporariamente zeradas.                               |
| RF08 | Modelar Componentes Físicos                   | A simulação deve considerar, de forma abstrata, a rede de encanamento público, o hidrômetro e o encanamento da residência.                                                      |
| RF09 | Simular Pressão da Água                       | A simulação deve incluir o conceito de pressão da água, que influencia a vazão e é representada visualmente na GUI.                                                           |
| RF10 | Exibir Status da Rede                         | A GUI deve exibir um status claro indicando o estado atual da rede (NORMAL, SEM FLUXO, AR NA TUBULAÇÃO).                                                                  |

### Requisitos Não Funcionais (RNF)

| ID    | Requisito                                | Descrição                                                                                                                                                               |
| :---- | :--------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| RNF01 | Plataforma e Linguagem                   | O software deve ser desenvolvido integralmente em **Java**, utilizando o paradigma de **Programação Orientada a Objetos**.                                                |
| RNF02 | Ambiente de Desenvolvimento              | O projeto será desenvolvido e compilado utilizando a IDE **IntelliJ IDEA** e gerenciador de dependências **Maven**.                                                         |
| RNF03 | Operação Contínua                        | O simulador deve ser projetado para funcionar de forma ininterrupta (24/7), sem a necessidade de reinicializações manuais e gerenciando adequadamente os recursos do sistema. |
| RNF04 | Desempenho                               | A atualização da GUI e a captura de imagens não devem consumir recursos computacionais excessivos.                                                                        |
| RNF05 | Configurabilidade                        | Parâmetros chave da simulação devem ser facilmente configuráveis pelo usuário final através de um arquivo `.properties`.                                                |
| RNF06 | Versionamento de Código                  | O código-fonte do projeto será hospedado e versionado em um repositório **Git**, preferencialmente na plataforma GitHub.                                                 |
| RNF07 | Portabilidade                            | Sendo uma aplicação Java Swing, o sistema deve ser capaz de rodar em qualquer sistema operacional que possua uma Java Virtual Machine (JVM) compatível.                 |

---

## 📈 Arquitetura e Diagrama de Classes (UML)

A arquitetura do software segue o princípio da separação de responsabilidades, dividindo o código em `model` (lógica de negócio), `view` (interface gráfica e captura) e `util` (utilitários).

A imagem abaixo ilustra o relacionamento entre as principais classes do sistema.

![Diagrama de Classes](UMLHidrômetro.png)

---

## 📂 Estrutura do Projeto

O projeto está organizado na seguinte estrutura de diretórios, ideal para desenvolvimento no IntelliJ com Maven:

```
simulador-hidrometro/
├── .idea/
├── out/
│   └── imagens_geradas/  # Diretório de saída para as imagens capturadas
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/hidrometro/
│   │   │       ├── model/        # Classes de domínio (Hidrometro, RedeHidraulica)
│   │   │       │   ├── Hidrometro.java
│   │   │       │   └── RedeHidraulica.java
│   │   │       ├── util/         # Classes utilitárias (Configuracao)
│   │   │       │   └── Configuracao.java
│   │   │       └── view/         # Classes de visualização (HidrometroGUI, Display)
│   │   │           ├── Display.java      # Classe para capturar e salvar imagens da GUI
│   │   │           └── HidrometroGUI.java # Interface Gráfica Principal
│   │   │       ├── Main.java     # Ponto de entrada da aplicação
│   │   │       └── Simulador.java  # Classe principal que controla a simulação
│   │   └── resources/              # Recursos do projeto (imagens, configurações)
│   │       ├── config/
│   │       │   └── parametros.properties # Arquivo de configuração da simulação
│   │       └── hidrometro.png      # Imagem de fundo da GUI
│   └── test/                     # Testes (se houver)
├── .gitignore
├── pom.xml                     # Arquivo de configuração Maven
└── README.md                   # Este arquivo!
```

## 🛠️ Pré-requisitos

-   **Java Development Kit (JDK)** - Versão 11 ou superior.
-   **IntelliJ IDEA** (recomendado).
-   **Apache Maven** (gerenciado automaticamente pelo IntelliJ ao abrir o projeto).

## 🚀 Como Executar o Projeto

1.  **Clone o Repositório:**
    ```bash
    git clone [URL_DO_SEU_REPOSITORIO] simulador-hidrometro
    cd simulador-hidrometro
    ```

2.  **Abra no IntelliJ IDEA:**
    -   Abra o IntelliJ e selecione `File > Open...`
    -   Navegue até a pasta `simulador-hidrometro` e a selecione.
    -   O IntelliJ detectará o arquivo `pom.xml` e configurará o projeto Maven automaticamente, baixando as dependências necessárias.

3.  **Verifique os Recursos:**
    -   Certifique-se de que o arquivo `hidrometro.png` (a imagem base do hidrômetro) esteja em `src/main/resources/`.
    -   Verifique se o arquivo `parametros.properties` está em `src/main/resources/config/`.

4.  **Configure a Simulação:**
    -   Edite o arquivo `src/main/resources/config/parametros.properties` para ajustar os parâmetros da simulação (vazão média, pressão média, intervalos, probabilidades de eventos, etc.).

5.  **Execute:**
    -   No IntelliJ, encontre o arquivo `Main.java` no diretório `src/main/java/br/com/hidrometro/`.
    -   Clique com o botão direito sobre ele e selecione `Run 'Main.main()'`.

6.  **Observe a Saída:**
    -   Uma janela da interface gráfica (`Hidrometro Digital`) será exibida, mostrando a simulação do hidrômetro em tempo real.
    -   As capturas de tela da GUI serão salvas automaticamente no diretório `out/imagens_geradas/` (especificado no `parametros.properties`) em intervalos regulares.
    -   O console da IDE também exibirá logs de status da simulação.

---

## ⚙️ Configuração ( `src/main/resources/config/parametros.properties` )

O comportamento do simulador é controlado pelo arquivo `parametros.properties`. Edite-o para personalizar a simulação:

```properties
# Parâmetros de Simulação do Hidrômetro

# Vazão média da água em m³/hora. A vazão real irá variar em torno deste valor.
vazao.media=20.0

# Pressão média da água em bar. A pressão real irá variar em torno deste valor.
pressao.media=2.0

# Caminho para salvar as imagens geradas (relativo à raiz do projeto).
path.saida.imagens=out/imagens_geradas/

# Prefixo do nome do arquivo de imagem (ex: leitura_00001.png).
prefixo.nome.imagem=leitura_

# Formato da imagem (png ou jpg).
formato.imagem=png

# Intervalo em segundos para a atualização da GUI e geração de uma nova imagem.
intervalo.geracao.imagem.segundos=2

# Probabilidade de ocorrer falta de água a cada ciclo (0.0 a 1.0).
# Ex: 0.01 significa 1% de chance.
probabilidade.falta.de.agua=0.00

# Probabilidade de ter ar na tubulação a cada ciclo (0.0 a 1.0).
probabilidade.presenca.de.ar=0.05

# Fator multiplicador para o consumo quando há ar.
# Ex: 1.3 significa que o hidrômetro registrará 30% a mais de "volume".
fator.consumo.com.ar=1.3
```
