package pokemon.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import pokemon.Pair;
import pokemon.database.MoveElite;
import pokemon.database.Pokemon;
import pokemon.database.PokemonData;
import pokemon.database.PokemonInfoLoader;
import pokemon.database.PokemonRanker;
import pokemon.database.Stats;
import pokemon.database.Type;

class CounterReq {
	public String target;
	public int num;
}

class NewPokemonReq {
	public String name;
	public String species;
	public int raidTier;
	public double level;
	public Stats iv;
	public boolean isShadow;
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotInRepoException extends ResponseStatusException { 
    public NotInRepoException(String errorMessage) {
        super(HttpStatus.NOT_FOUND, errorMessage);
    }
}

@RestController
@RequestMapping(path = "/pokemon/api")
public class PokemonController {
	PokemonRanker ranker;
	PokemonRepo repo;
	List<PokemonRecord> repoRecord;
	public PokemonController() {
		PokemonInfoLoader.loadIntoDatabase();
		ranker = new PokemonRanker();
		repo = new PokemonRepo();
		repoRecord = new ArrayList<>();
	}

	@GetMapping("/get-move")
	public ResponseEntity<MoveRecord> getMove(@RequestParam(value = "name", required = true) String name) {
		return new ResponseEntity<>(new MoveRecord(PokemonData.getMove(name)), HttpStatus.OK);
	}

	@GetMapping("/get-pokemon")
	public ResponseEntity<PokemonRecord> getPokemon(@RequestParam(value = "name", required = true) String name) {
		return new ResponseEntity<>(new PokemonRecord(PokemonData.getPokemon(name)), HttpStatus.OK);
	}

	@GetMapping("/get-all-instances")
	public ResponseEntity<List<PokemonRecord>> getAll() {
		if (!repo.updated) {
			List<Pair<String, Pokemon>> instanceList = repo.getAll();
			for (Pair<String, Pokemon> instance : instanceList) {
				repoRecord.add(new PokemonRecord(instance.val, instance.key));
			}
		}
		return new ResponseEntity<>(repoRecord, HttpStatus.OK);
	}

	@GetMapping("/get-by-search")
	public ResponseEntity<List<PokemonRecord>> getBySearch(@RequestParam(value = "search", required = true) String searchString) {
		List<Pair<String, Pokemon>> instanceList = repo.getBySearch(searchString);
		for (Pair<String, Pokemon> instance : instanceList) {
			repoRecord.add(new PokemonRecord(instance.val, instance.key));
		}
		return new ResponseEntity<>(repoRecord, HttpStatus.OK);
	}

	@PutMapping("/add-instance")
	public ResponseEntity<PokemonRecord> addPokemon(@RequestBody NewPokemonReq request) {
		Pokemon newPoke;
		if (request.level == -1) {
			newPoke = PokemonData.getPokemon(request.species).asRaidBoss(request.raidTier);
		}
		else {
			newPoke = PokemonData.getPokemon(request.species).createInstance(request.iv, request.level, request.isShadow);
		}
		repo.addPokemon(request.name, newPoke);
		
		return new ResponseEntity<> (new PokemonRecord(newPoke, request.name), HttpStatus.OK);
	}

	@PostMapping("/remove-instance")
	public ResponseEntity<PokemonRecord> removePokemon(@RequestParam(value = "search", required = true) String searchString) {
		Pokemon deleted = repo.removePokemon(searchString);
		return new ResponseEntity<> (new PokemonRecord(deleted, searchString), HttpStatus.OK);
	}

	@PostMapping("/rank-type-counters")
	public ResponseEntity<List<PokemonRecord>> rankTypeCounters(@RequestBody CounterReq request) {
		List<Pair<Pokemon, Pair<MoveElite, MoveElite>>> response = ranker.bestOfType(Type.parseType(request.target), request.num, false, false);
		List<PokemonRecord> toReturn = new ArrayList<>();
		for (int i = 0; i < response.size(); i++) {
			toReturn.add(new PokemonRecord(response.get(i).key, i + 1,  response.get(i).val.key, response.get(i).val.val));
		}
		return new ResponseEntity<>(toReturn, HttpStatus.OK);
	}

	@PostMapping("/rank-pokemon-counters")
	public ResponseEntity<List<PokemonRecord>> rankPokeCounters(@RequestBody CounterReq request) {
		Pokemon defender = repo.getBySearch(request.target).get(0).val;
		List<PokemonRecord> toReturn = new ArrayList<>();
		List<Pair<Pokemon, Pair<MoveElite, MoveElite>>> response = ranker.bestCounters(defender, false, request.num);
		for (int i = 0; i < response.size(); i++) {
			toReturn.add(new PokemonRecord(response.get(i).key, i + 1,  response.get(i).val.key, response.get(i).val.val));
		}
		return new ResponseEntity<>(toReturn, HttpStatus.OK);
	}
}