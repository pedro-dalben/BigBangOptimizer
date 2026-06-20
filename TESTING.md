# Testes do BigBangOptimizer

## Cenários de Teste

### 1. Pokémon selvagem comum
- **Setup**: Pokémon wild, antigo, distante de jogadores
- **Esperado**: Pode ser removido
- **Comando**: `/bbo clear preview pokemon`

### 2. Pokémon shiny
- **Setup**: Pokémon shiny wild
- **Esperado**: Nunca removido
- **Proteção**: `shiny`

### 3. Pokémon em batalha
- **Setup**: Pokémon em batalha ativa
- **Esperado**: Nunca removido
- **Proteção**: `battling`

### 4. Pokémon enviado por jogador
- **Setup**: Pokémon com owner
- **Esperado**: Nunca removido
- **Proteção**: `player_owned`

### 5. Pokémon tethered a Pasture Block
- **Setup**: Pokémon vinculado a Pasture
- **Esperado**: Nunca removido
- **Proteção**: `pasture_tethered`

### 6. Pokémon em evolução
- **Setup**: Pokémon durante evolução
- **Esperado**: Nunca removido
- **Proteção**: `evolving`

### 7. Pokémon recém-spawnado
- **Setup**: Pokémon com poucos ticks
- **Esperado**: Nunca removido
- **Proteção**: `too_young`

### 8. Pokémon montado ou com passageiro
- **Setup**: Pokémon com entidade montada
- **Esperado**: Nunca removido
- **Proteção**: `mounted`

### 9. Item solto antigo
- **Setup**: ItemEntity no chão por tempo suficiente
- **Esperado**: Pode ser removido
- **Comando**: `/bbo clear preview items`

### 10. Item próximo de jogador
- **Setup**: ItemEntity perto de jogador
- **Esperado**: Protegido por proximidade
- **Proteção**: `near_player`

### 11. Preview
- **Setup**: Qualquer cenário
- **Esperado**: Nunca remove entidades
- **Comando**: `/bbo clear preview all`

### 12. Limpeza manual
- **Setup**: Comando admin
- **Esperado**: Obedece todas proteções
- **Comando**: `/bbo clear now all`

### 13. Limpeza automática
- **Setup**: Agendamento automático
- **Esperado**: Emite avisos e respeita cooldown

### 14. Servidor sem Cobblemon
- **Setup**: Servidor sem Cobblemon
- **Esperado**: Inicia normalmente, desativa módulo Cobblemon

### 15. Milhares de entidades
- **Setup**: Grande quantidade de entidades
- **Esperado**: Usa lotes, não trava tick principal

### 16. Reload de config inválida
- **Setup**: Configuração inválida
- **Esperado**: Preserva última config válida

### 17. Nenhum chunk carregado
- **Setup**: Durante contagem/limpeza
- **Esperado**: Nenhum chunk é carregado

### 18. Cancelamento de Limpeza Ativa
- **Setup**: Executar `/bbo clear schedule items` ou aguardar início de avisos (`State.WARNING`), e rodar `/bbo clear cancel`
- **Esperado**: O scheduler cancela a limpeza imediatamente, retornando ao estado `IDLE` e silenciando os avisos subsequentes.

### 19. Execução via Console do Servidor
- **Setup**: Executar `/bbo clear now all`, `/bbo status` ou `/bbo reload` diretamente pelo console do servidor Linux
- **Esperado**: O comando executa perfeitamente sem NullPointerException ou problemas de permissão.

### 20. Triggers de Performance (TPS/MSPT)
- **Setup**: Configurar `mode = "threshold"` e forçar degradação de MSPT (por exemplo, acima de 60ms) com número de itens ou Pokémon acima do limite global.
- **Esperado**: O mod aciona a limpeza automática após atingir o número configurado de amostras consecutivas degradadas (`consecutive_bad_samples`).

### 21. Rotação de Logs e Formato JSONL
- **Setup**: Executar limpezas automáticas ou manuais e verificar o arquivo de saída
- **Esperado**: Logs estruturados em formato JSON Lines em um único arquivo `logs/bigbangoptimizer/cleanup.log` que rotaciona automaticamente ao atingir 10MB (gerando backups `.1` a `.5`).

## Como Executar Testes

1. Compile o mod: `./gradlew build`
2. Instale o JAR gerado em `build/libs/` na pasta `mods/` de um servidor NeoForge 1.21.1 de teste.
3. Teste o boot sem o Cobblemon para validar o isolamento de classes.
4. Adicione o Cobblemon, inicialize e execute os comandos administrativos descritos para validar as proteções e a máquina de estados.
5. Verifique a escrita do log de auditoria em `logs/bigbangoptimizer/cleanup.log`.
