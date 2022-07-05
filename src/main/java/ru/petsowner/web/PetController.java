package ru.petsowner.web;


import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.petsowner.exception.IllegalRequestDataException;
import ru.petsowner.model.Pet;
import ru.petsowner.repository.PetRepository;

@RestController
@RequestMapping(value = PetController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class PetController {

  static final String REST_URL = "/api/pets";

  private final PetRepository repository;

  @GetMapping("/{id}")
  public Pet get(@PathVariable("id") long id) {
    return repository.findPetByIdAAndOwnerId(id, AuthUser.getAuth().id())
        .orElseThrow(() -> new IllegalRequestDataException("Pet with id= " + id + " not found"));
  }

  @GetMapping
  public List<Pet> getAll() {
    return repository.findAllByOwnerId(AuthUser.getAuth().id());
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void update(@Valid @RequestBody Pet pet, @PathVariable("id") long id) {
    if (pet.getId() != id) {
      throw new IllegalRequestDataException("Pet" + " must has id=" + id);
    }
    Optional<Pet> checkName = repository.getByPetName(pet.getPetName());
    if (checkName.isPresent() && !checkName.get().getId().equals(pet.getId())) {
      throw new IllegalRequestDataException("Pet with " + pet.getPetName() + " already exists");
    }

    pet.setOwner(AuthUser.getAuth().getUser());
    repository.save(pet);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Pet> create(@Valid @RequestBody Pet pet) {
    if (pet.getId() != null) {
      throw new IllegalRequestDataException("Pet" + " must be new (id=null)");
    }
    if (repository.getByPetName(pet.getPetName()).isPresent()) {
      throw new IllegalRequestDataException(
          "Pet with petname " + pet.getPetName() + " already exists");
    }

    pet.setOwner(AuthUser.getAuth().getUser());
    Pet created = repository.save(pet);
    URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(REST_URL + "/{id}")
        .buildAndExpand(created.getId()).toUri();
    return ResponseEntity.created(uriOfNewResource).body(created);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") int id) {
    if (repository.delete(id, AuthUser.getAuth().getUser().getId()) == 0) {
      throw new IllegalRequestDataException("Pet with id=" + id + " not found");
    }
  }
}
