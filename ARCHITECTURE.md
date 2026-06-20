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

## Integrações

- **Cobblemon**: Verificação de proteção de Pokémon
- **FTB Chunks**: Detecção de presença (futura extensão)
- **Spark**: Detecção de presença (modo passivo)

## Tags de Proteção

- `bigbangoptimizer:protected`: Tag persistente para proteger entidades
