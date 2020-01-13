## Unofficial fork of [SkStuff](https://github.com/TheBukor/SkStuff)
 
 


This fork adds support for 1.13.2, 1.14.2 and 1.15.1 Minecraft versions. removes support for older than 1.13 versions, removes worldguard/worldedit, vanishnospacket and nbt stuff. This fork is focused on making pathfinder goals to work as they are unique to SkStuff.

**Download it [here](https://github.com/Govindass/SkStuff/releases)**

##List of pathfinder goals
```
(avoid|run away from) %entitytype%[, radius %number%[, speed %number%[, speed (if|when) (close|near) %number%]]]
break door[s]
breed[, [move[ment]] speed %number%] #Note 1
eat grass
(flee from the sun|seek shad(e|ow))[, [move[ment]] speed %number%]
(float (in[side]|on) water|swim)
follow (owner|tamer)[, speed %number%[, min[imum] distance %number%[, max[imum] distance %number%]]] #Note 2
follow (adult|parent)[s][, [move[ment]] speed %number%] #Note 3
(fight back|react to|target) (damager|attacker) [[of] type] %entitytype%[, call ([for] help|reinforcement) %boolean%]
o(c|z)elot jump on blocks[, [move[ment]] speed %number%]
leap at target[, [leap] height %number%]
look at %entitytype%[, (radius|max[imum] distance) %number%]
melee attack %entitytype%[, [move[ment]] speed %number%[, (memorize|do('nt| not) forget) target [for [a] long[er] time] %boolean%]] #Note 4
move to[wards] target[, [move[ment]] speed %number%[, (radius|max[imum] distance) %number%]]
target nearest [entity [of] type] %entitytype%[, check sight %boolean%] #Note 5
o(c|z)elot attack #Note 6
open door[s]
(panic|flee)[, [move[ment]] speed %number%]
look around randomly
(walk around randomly|wander)[, [move[ment]] speed %number%[, min[imum] [of] %timespan% between mov(e[ment][s]|ing)]]
sit #Note 2
[creeper] (explode|inflate|swell)
squid (swim around|wander)
shoot fireball[s] #Note 7
[silverfish] hide (in[side]|on) block[s]
(call|summon|wake) [other] [hidden] silverfish[es]
[enderman] pick[[ ]up] block[s]
[enderman] place block[s]
[enderman] attack player (staring|looking) at [their] eye[s]]
ghast move to[wards] target
ghast (idle move[ment]|wander|random fl(ight|y[ing]))
(tempt to|follow players (holding|with)) %itemstack%[, [move[ment]] speed %number%[, scared of player movement %boolean%]]
target [random] %entitytype% (if|when) (not |un)tamed #Note 2
guardian attack [entities] #Note 6
[z[ombie[ ]]pig[man]] attack [player[s]] (if|when) angry
[z[ombie[ ]]pig[man]] (react to|fight back|target) (attacker|damager) (if|when) angry
[rabbit] eat carrot crops
[killer] rabbit [melee] attack
slime [random] jump
slime change (direction|facing) randomly
slime (idle move[ment]|wander)
 
Notes:
1 - Can only be applied to breedable animals.
2 - Can only be applied to tameable animals (Wolves and Ocelots).
3 - Only takes effect when the entity is a baby (age is less than 0).
4 - The boolean argument specifies whether or not the entity should lose interest on its target if there's no suitable path. The speed parameter will ALWAYS be 1 for SPIDERS. The boolean will ALWAYS be TRUE for SPIDERS.
5 - The boolean argument specifier whether or not the entity should have a direct line of sight of its target. Examples are spiders, by default they can see players through blocks. The boolean argument will ALWAYS be TRUE for SPIDERS.
6 - If this goal is missing on the entity, it will not be able to attack.
7 - Can only be applied on Ghasts or Blazes.
```
