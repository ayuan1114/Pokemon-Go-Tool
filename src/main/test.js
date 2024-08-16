import axios from 'axios';

const basePath = "http://localhost:8080/pokemon/api"
async function makeGetRequest(path) {
    await axios.get(path).then(
        (response) => {
            let result = response.data;
            console.log(result);
        },
        (error) => {
            console.log(error.code);
        }
    );
}

async function makePostRequest(path, queryObj) {
    await axios.post(path, queryObj).then(
        (response) => {
            let result = response.data;
            console.log(result);
        },
        (error) => {
            console.log(error);
        }
    );
}

async function makePutRequest(path, queryObj) {
    await axios.put(path, queryObj).then(
        (response) => {
            let result = response.data;
            console.log(result);
        },
        (error) => {
            console.log(error.code);
        }
    );
}

async function addPokemon() {
    await makePutRequest(basePath + "/add-instance", {name: "pika", species: "Pikachu", iv: {attack: 15, defense: 14, stamina: 15}, level: 35.5, isShadow: false});
    await makePutRequest(basePath + "/add-instance", {name: "META", species: "Metagross", iv: {attack: 15, defense: 14, stamina: 15}, level: 43.5, isShadow: true});
    await makePutRequest(basePath + "/add-instance", {name: "Raid Boss", species: "Rayquaza", raidTier: 5, level: -1});
}

await addPokemon();
await makeGetRequest(basePath + "/get-by-search?search=Raid Boss")
await makePostRequest(basePath + "/rank-pokemon-counters", {target: "Raid Boss", num: 15});