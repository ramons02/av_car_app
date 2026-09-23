# av_car_app: Frontend (Java Swing)

Frontend desktop do **AV-CAR**, construído em **Java Swing** com tema escuro **FlatLaf**. Esta pasta também contém o bootstrap do Spring Boot (`AvCarApplication`), responsável por subir o contexto Spring e abrir a interface.

## O que existe aqui

* **Tela principal** (`TelaPrincipalGUI`) com sidebar, dashboard (KPIs e gráficos Java2D) e abas de gestão.
* **Telas de cadastro** (14): Cliente PF/PJ, Colaborador, Veículo, Peça, Serviço, Fornecedor, Parceiro Externo, OS, Itens de OS, Garantia, Desconto e Fila de Espera.
* **Padrão MVP**: cada tela possui um `Presenter` responsável pelas regras de apresentação.
* **Utilitários visuais** (`swing/views/utils`): tabelas, máscaras, cards KPI, gráficos e geração de OS em PDF.
* **Resources**: `application.yml`, ícones e logos.

## Dependências principais

* Spring Boot Web + JDBC (o app conversa com a API via beans do Spring)
* `flatlaf` / `flatlaf-extras`
* `openpdf`
* `springboot3-dotenv` (carrega o `av_car_infra/.env`)
* `av-car` (artefato da pasta `av_car_api`)

## Como compilar e rodar

A app depende do jar de `av-car` instalado no repositório local do Maven. Execute antes, na pasta `av_car_api`:

```bash
mvnw install
```

Depois, nesta pasta:

```bash
mvnw spring-boot:run
```

Ou gere o executável e rode:

```bash
mvnw clean package
java -jar target/av-car-app-1.0.0.jar
```

## Ambiente

* **JDK:** 21 (obrigatório; o JDK 25 quebra a compilação do Lombok)
* **Banco:** variáveis definidas no `av_car_infra/.env`, lidas automaticamente pelo Spring com valores padrão no `application.yml`