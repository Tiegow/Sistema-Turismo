#!/usr/bin/env bash
# Observa alterações em src/main e recompila automaticamente.
# Use junto com './mvnw spring-boot:run' (em outro terminal) + spring-boot-devtools
# para que a aplicação reinicie sozinha a cada alteração salva.
set -euo pipefail
cd "$(dirname "$0")"

echo "Observando alterações em src/main ... (Ctrl+C para parar)"

while true; do
  inotifywait -r -e modify,create,delete,move --format '%w%f' src/main >/dev/null 2>&1
  echo "Alteração detectada, recompilando..."
  ./mvnw -q compile || echo "Falha ao compilar."
done
