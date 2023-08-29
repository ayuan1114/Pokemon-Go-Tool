# Pokemon Go Tool
App that uses the PoGo API (https://pogoapi.net/) \
Currently, features include a command to get top attackers by type and top counters against a certain Pokemon. \
The tool also allows for choosing the weather conditions and filtering out legacy moves, shadow Pokemon and mega Pokemon.

For all rankings, this tool uses an effectiveness rating which is the DPS * (TOF)^(1/4) (DPS is damage per second and TOF is time on field). \
For DPS calculations, the tool uses comprehensive DPS which takes into account the expected energy wasted when a Pokemon dies. \
More info on the effectiveness rating calculations can be found at https://www.reddit.com/r/TheSilphRoad/comments/z3xuzc/analysis_legendarymythical_signature_moves/ \
More info on comprehensive DPS can be found at https://gamepress.gg/pokemongo/how-calculate-comprehensive-dps \
The alogrithm used to sort the best counters against a certain Pokemon uses the rankings for best Pokemon of each type. \
It works in a way similar to heap sort. \
First it determines the best type counters to the defender's typing. \
It will then get the ER (Effectiveness Rating) of each type's best Pokemon against the defender. \
The Pokemon with the best ER is taken and the next best attacker of the same type takes its spot. \
This process is repeated until the algorithm finds the number of counters the user queried.

