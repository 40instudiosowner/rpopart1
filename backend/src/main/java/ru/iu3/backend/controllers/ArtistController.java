package ru.iu3.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.CountryRepository;
import ru.iu3.backend.tools.DataValidationException;


import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
public class ArtistController {

    public long findCountryIdByName(String cName){
        long id = 252;
        for (long index = 0; index < 251; index++){
            Optional <Country> cc = countryRepository.findById(index);
            if (cc.isPresent()) {
                Country countr = cc.get();
                if (cName.equalsIgnoreCase(countr.name)){
                    id = index;
                }
            }
        }
        return id;
    }

    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/artists")
    public Page<Artist> getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return artistRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<Artist> getArtist(@PathVariable(value = "id") Long artistId)
            throws DataValidationException
    {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()-> new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artist);
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) throws Exception {
        try {

            long ind = findCountryIdByName(artist.country.name);
            if (ind > 251){
                throw new Exception("Неизвестная страна");
            }
            Optional<Country>
                    cc = countryRepository.findById(ind);
            cc.ifPresent(country -> artist.country = country);
            Artist nc = artistRepository.save(artist);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error = "undefinederror";
            Map<String, String> map =  new HashMap<>();
            map.put("error", error);
            System.out.println(error);
            return new ResponseEntity<Object> (map, HttpStatus.OK);
        }
    }
    @PostMapping("/deleteartists")
    public ResponseEntity<HttpStatus> deleteArtists(@RequestBody List<Artist> artists) {
        System.out.println(artists.get(0).toString());
        List<Long> listOfIds = new ArrayList<>();
        for (Artist artist : artists) {
            listOfIds.add(artist.id);
        }
        artistRepository.deleteAllById(listOfIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long artistId,
                                               @RequestBody Artist artist) {
        Artist artistt = null;
        Optional<Artist> cc = artistRepository.findById(artistId);
        long ind = findCountryIdByName(artist.country.name);
        if (ind > 251) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "country not found");
        }
        Optional<Country>
                countr = countryRepository.findById(ind);
        countr.ifPresent(country -> artist.country = country);
        if (cc.isPresent()) {
            artistt = cc.get();
            artistt.name = artist.name;
            artistt.country = artist.country;
            artistt.century= artist.century;
            System.out.println(artistt.country.name);
            artistRepository.save(artistt);
            return ResponseEntity.ok(artistt);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "artist not found");
        }
    }

}