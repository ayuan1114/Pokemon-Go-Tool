package pokemon.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import pokemon.Pair;
import pokemon.database.Pokemon;

public class PokemonRepo {
    Hashtable<String, Pokemon> repo;
    public boolean updated;
    
    public PokemonRepo() {
        repo = new Hashtable<>();
        updated = false;
    }

    public void addPokemon(String name, Pokemon pokemon) {
        repo.put(name, pokemon);
        updated = false;
    }
    
    public Pokemon removePokemon(String name) throws NotInRepoException {
        Pokemon found = repo.remove(name);
        if (found == null) {
            throw new NotInRepoException("Pokemon with name " + name + " not found");
        }
        updated = false;
        return found;
    }

    public List<Pair<String, Pokemon>> getBySearch(String search) throws NotInRepoException {
        List<Pair<String, Pokemon>> matches = new ArrayList<>();
        ArrayList<String> keys = Collections.list(repo.keys());
        for (String name : keys) {
            if (name.contains(search)) {
                matches.add(new Pair<>(name, repo.get(name)));
            }
        }
        if (matches.isEmpty()) {
            throw new NotInRepoException("No Pokemon matching " + search + " found.");
        }
        return matches;
    }

    public List<Pair<String, Pokemon>> getAll() throws NotInRepoException {
        List<Pair<String, Pokemon>> toReturn = new ArrayList<>();
        ArrayList<String> keys = Collections.list(repo.keys());
        for (String name : keys) {
            toReturn.add(new Pair<>(name, repo.get(name)));
        }
        if (toReturn.isEmpty()) {
            throw new NotInRepoException("No Pokemons in the repository");
        }
        updated = true;
        return toReturn;
    }
}
