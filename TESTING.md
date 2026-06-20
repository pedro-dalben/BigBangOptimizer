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

## Como Executar Testes

1. Compile o mod: `./gradlew build`
2. Instale no servidor de teste
3. Execute comandos de teste
4. Verifique logs em `logs/bigbangoptimizer/`
