#!/bin/sh

url="https://play.pokemonshowdown.com/sprites/dex/"
path="public/sprites/"

PKMNS=( $(jq -r '.[].pokemon' public/data/*.json | uniq | tr -d ' ') )
for pkmn in "${PKMNS[@]}"; do
  pkmn=$(echo $pkmn | tr '[:upper:]' '[:lower:]')
  curl "$url${pkmn}.png" --create-dirs -o "$path${pkmn}.png"
done