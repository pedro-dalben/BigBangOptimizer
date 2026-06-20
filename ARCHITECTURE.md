# Arquitetura do BigBangOptimizer

## Estrutura do Projeto

```
bigbangoptimizer/
├── BigBangOptimizer.java          # Classe principal do mod
├── config/
│   ├── OptimizerConfig.java       # Configuração TOML server-side
│   └── ConfigValidator.java       # Validação de configuração
├── core/
│   ├── OptimizerModule.java       # Interface para módulos
│   ├── ModuleRegistry.java        # Registro de módulos
│   ├── CleanupScheduler.java      # Agendamento de limpezas
│   ├── CleanupPlan.java           # Plano de limpeza (preview)
│   ├── CleanupExecution.java      # Execução de limpeza
│   ├── CleanupResult.java         # Resultado da limpeza
│   └── OptimizerPermissions.java  # Permissões
├── monitoring/
│   ├── PerformanceSnapshot.java   # Snapshot de performance
│   ├── TpsMonitor.java            # Monitor de TPS
│   ├── MemoryMonitor.java         # Monitor de memória
│   ├── EntityStatisticsCollector.java # Coletor de estatísticas
│   └── DimensionStatistics.java   # Estatísticas por dimensão
├── cleanup/
│   ├── EntityCleanupModule.java   # Módulo de limpeza de entidades
│   ├── ItemCleanupModule.java     # Módulo de limpeza de itens
│   ├── PokemonCleanupModule.java  # Módulo de limpeza de Pokémon
│   ├── TemporaryEntityCleanupModule.java # Módulo de entidades temporárias
│   ├── CleanupRunner.java         # Executor de limpeza
│   └── EntityCleanupAuditLogger.java # Logger de auditoria
├── integrations/
│   ├── OptimizerIntegration.java  # Interface de integração
│   ├── cobblemon/
│   │   ├── CobblemonIntegration.java      # Integração Cobblemon
│   │   ├── CobblemonEntityInspector.java  # Inspector de entidades
│   │   └── CobblemonCompatibilityReport.java # Relatório de compatibilidade
│   ├── ftbchunks/
│   │   └── FtbChunksIntegration.java # Integração FTB Chunks
│   └── spark/
│       └── SparkIntegration.java   # Integração Spark
├── commands/
│   ├── BigBangOptimizerCommand.java # Comando raiz /bbo
│   ├── ClearCommand.java          # Comandos de limpeza
│   ├── StatusCommand.java         # Comando de status
│   ├── EntityStatsCommand.java    # Comando de estatísticas
│   └── ConfigCommand.java         # Comandos de config
└── util/
    ├── TimeFormatters.java        # Formatadores de tempo
    ├── ComponentFactory.java      # Fábrica de componentes
    └── SafeEntityRemoval.java     # Remoção segura de entidades
```

## Princípios de Design

1. **Modularidade**: Cada funcionalidade é um módulo independente
2. **Segurança**: Nunca remove entidades sem verificação de proteção
3. **Observabilidade**: Toda ação é auditável e logável
4. **Performance**: Processamento em lotes, sem varreduras pesadas
5. **Compatibilidade**: Funciona sem dependências opcionais

## Fluxo de Limpeza

```
IDLE → SCHEDULED → WARNING → EXECUTING → COOLDOWN → IDLE
```

1. **IDLE**: Aguardando agendamento
2. **SCHEDULED**: Limpeza agendada
3. **WARNING**: Enviando avisos aos jogadores
4. **EXECUTING**: Executando limpeza
5. **COOLDOWN**: Aguardando cooldown

- **Cobblemon**: Verificação de proteção de Pokémon (desacoplada de forma segura)
- **FTB Chunks**: Detecção de presença (modo passivo)
- **Spark**: Detecção de presença (modo passivo)

## Tags de Proteção

- `bigbangoptimizer:protected`: Tag persistente para proteger entidades

## Isolamento e Otimizações de Concorrência

1. **Isolamento de Integrações Opcionais**: Todo import de classes de mods externos (como `PokemonEntity` do Cobblemon) está isolado em classes que só são carregadas em tempo de execução se o mod estiver presente. Outros componentes lidam apenas com a API padrão do Minecraft (`Entity`), eliminando erros de `NoClassDefFoundError` na inicialização do servidor.
2. **Log de Auditoria Assíncrono**: Escritas de auditoria em disco ocorrem fora da thread principal através de `CompletableFuture.runAsync()`, eliminando travamentos de tick por I/O síncrono bloqueante.
3. **Formato JSON Lines (JSONL)**: Os registros de auditoria são anexados incrementalmente a um único arquivo `cleanup.log` no formato JSON Lines.
4. **Rotação de Logs automática**: O arquivo de auditoria rotaciona ao atingir 10MB, preservando até 5 backups históricos (`cleanup.log.1` a `cleanup.log.5`).
5. **Máquina de Estados Absoluta**: O agendador `CleanupScheduler` calcula o tick de destino (`cleanupTargetTick`) de forma absoluta, tratando warnings e execuções de forma sequencial exata mesmo sob variações de tick rate (lag).
