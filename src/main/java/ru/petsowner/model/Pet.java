package ru.petsowner.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pets")
public class Pet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "petname", nullable = false, unique = true)
  @NotNull
  private String petName;
  @Column(name = "gender", nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Gender gender;
  @Column(name = "pettype", nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private PetType petType;
  @Column(name = "birthday", nullable = false)
  @NotNull
  private LocalDate birthday;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  @JsonIgnore
  private User owner;

}
