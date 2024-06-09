package ru.iu3.backend.controllers;

import ru.iu3.backend.models.Artist;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.PaintingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;


import java.util.*;

@RestController
@CrossOrigin("")
@RequestMapping("/api/v1")

public class PaintingController {

    @Autowired
    PaintingRepository paintingRepository;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/paintings")
    public Page<Painting> getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return paintingRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }
    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@RequestBody Painting painting) throws Exception {
        try {
            if (painting.artist != null) {
                Optional<Artist>
                        art = artistRepository.findById(painting.artist.id);
                if (art.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such artist in db");
                }
                else{
                    art.ifPresent(artist -> painting.artist = artist);
                }
            } else {
                Optional<Artist> art=null;
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such artist in db");
            }
            if (painting.museum != null) {


                Optional<Museum>
                        mus = museumRepository.findById(painting.museum.id);
                if (mus.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such museum in db");
                }
                else{
                    mus.ifPresent(museum -> painting.museum = museum);
                }
            }

            Painting nc = paintingRepository.save(painting);
            return ResponseEntity.ok(nc);
        }

        catch(Exception ex) {

            String error = "undefinederror";
            Map<String, String> map =  new HashMap<>();
            map.put("error", error);
            System.out.println(error);
            return ResponseEntity.ok(map);
        }
    }

    @PutMapping("/paintings/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable(value = "id") Long paintingId,
                                                   @RequestBody Painting paintingDetails) {
        Painting painting = null;
        Optional<Painting>
                cc = paintingRepository.findById(paintingId);
        if (cc.isPresent()) {

            Optional<Artist>
                    art = artistRepository.findById(cc.get().artist.id);
            if(art.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such artist in db");
            }
            Optional<Museum>
                    mus = museumRepository.findById(cc.get().museum.id);
            if(mus.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such museum in db");
            }

            art.ifPresent(artist -> paintingDetails.artist = artist);
            mus.ifPresent(museum -> paintingDetails.museum = museum);
            painting= cc.get();
            painting.name = paintingDetails.name;
            painting.artist = paintingDetails.artist;
            painting.museum = paintingDetails.museum;
            painting.year = paintingDetails.year;
            paintingRepository.save(painting);
            return ResponseEntity.ok(painting);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Painting not found");
        }
    }

    @PostMapping("/deletepaintings")
    public ResponseEntity<HttpStatus> deletePaintings(@RequestBody List<Painting> paintings) {
        List<Long> listOfIds = new ArrayList<>();
        for (Painting painting : paintings) {
            listOfIds.add(painting.id);
        }
        paintingRepository.deleteAllById(listOfIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}