#!/bin/sh

url="https://play.pokemonshowdown.com/sprites/dex/"
path="public/sprites/"

arr=( $(jq -r '.[].pokemon' public/*.json) )
for pkmn in $(printf '%s\n' "${arr[@]}" | uniq); do
  pkmn=$(echo $pkmn | tr '[:upper:]' '[:lower:]' | tr -d ' ')
  curl "$url${pkmn}.png" --create-dirs -o "$path${pkmn}.png"
done