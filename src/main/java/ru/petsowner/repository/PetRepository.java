package ru.petsowner.repository;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.petsowner.model.Pet;

@Repository
@Transactional(readOnly = true)
public interface PetRepository extends JpaRepository<Pet, Long> {

  @Transactional
  @Modifying
  @Query("DELETE FROM Pet p WHERE p.id=:id AND p.owner.id=:userId")
  int delete(long id, long userId);

  List<Pet> findAllByOwnerId(long id);

  @Query("SELECT p FROM Pet p WHERE p.id=:petId AND p.owner.id=:userId")
  Optional<Pet> findPetByIdAAndOwnerId(long petId, long userId);

  Optional<Pet> getByPetName(String petName);
}
