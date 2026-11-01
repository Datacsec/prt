package com.bnpp.irb.datalake.portail.models;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "controle")
public class Controle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Saisi par l'utilisateur
    @Column(name = "controle_id", unique = true, nullable = false)
    private String controleId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "nb_jours_apres_arret")
    private Integer nbJoursApresArret;

    @Column(nullable = false)
    private String frequence;

    // Un contrôle est lié à plusieurs lignes de department (via identity)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "controle_department",
        joinColumns = @JoinColumn(name = "controle_pk"),
        inverseJoinColumns = @JoinColumn(name = "department_identity",
                                         referencedColumnName = "identity"))
    private Set<Department> departments = new LinkedHashSet<>();

    public Long getId() { return id; }

    public String getControleId() { return controleId; }
    public void setControleId(String controleId) { this.controleId = controleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getNbJoursApresArret() { return nbJoursApresArret; }
    public void setNbJoursApresArret(Integer nbJoursApresArret) { this.nbJoursApresArret = nbJoursApresArret; }

    public String getFrequence() { return frequence; }
    public void setFrequence(String frequence) { this.frequence = frequence; }

    public Set<Department> getDepartments() { return departments; }
    public void setDepartments(Set<Department> departments) { this.departments = departments; }
}





//////////





package com.bnpp.irb.datalake.portail.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class CreateControleRequest {

    @NotBlank(message = "Le controleId est obligatoire")
    private String controleId;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;

    @Min(value = 0, message = "Le nombre de jours doit être positif")
    private Integer nbJoursApresArret;

    @NotBlank(message = "La fréquence est obligatoire")
    private String frequence;

    @NotEmpty(message = "Au moins un département est obligatoire")
    private List<String> departmentIdentities;

    public String getControleId() { return controleId; }
    public void setControleId(String controleId) { this.controleId = controleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getNbJoursApresArret() { return nbJoursApresArret; }
    public void setNbJoursApresArret(Integer nbJoursApresArret) { this.nbJoursApresArret = nbJoursApresArret; }

    public String getFrequence() { return frequence; }
    public void setFrequence(String frequence) { this.frequence = frequence; }

    public List<String> getDepartmentIdentities() { return departmentIdentities; }
    public void setDepartmentIdentities(List<String> departmentIdentities) { this.departmentIdentities = departmentIdentities; }
}


/////////




package com.bnpp.irb.datalake.portail.dto;

import java.util.List;

public class ControleResponse {

    private Long id;
    private String controleId;
    private String title;
    private String description;
    private Integer nbJoursApresArret;
    private String frequence;
    private List<DepartmentInfo> departments;

    public ControleResponse(Long id, String controleId, String title, String description,
                            Integer nbJoursApresArret, String frequence,
                            List<DepartmentInfo> departments) {
        this.id = id;
        this.controleId = controleId;
        this.title = title;
        this.description = description;
        this.nbJoursApresArret = nbJoursApresArret;
        this.frequence = frequence;
        this.departments = departments;
    }

    public Long getId() { return id; }
    public String getControleId() { return controleId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getNbJoursApresArret() { return nbJoursApresArret; }
    public String getFrequence() { return frequence; }
    public List<DepartmentInfo> getDepartments() { return departments; }

    public static class DepartmentInfo {
        private String identity;
        private String direction;
        private String departement;
        private String entity;

        public DepartmentInfo(String identity, String direction,
                              String departement, String entity) {
            this.identity = identity;
            this.direction = direction;
            this.departement = departement;
            this.entity = entity;
        }

        public String getIdentity() { return identity; }
        public String getDirection() { return direction; }
        public String getDepartement() { return departement; }
        public String getEntity() { return entity; }
    }
}





//////////





package com.bnpp.irb.datalake.portail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bnpp.irb.datalake.portail.models.Controle;

public interface ControleRepository extends JpaRepository<Controle, Long> {

    boolean existsByControleId(String controleId);

    @Query("""
        SELECT DISTINCT c FROM Controle c
        LEFT JOIN FETCH c.departments
        ORDER BY c.controleId
    """)
    List<Controle> findAllWithDepartments();

    @Query("""
        SELECT DISTINCT c FROM Controle c
        LEFT JOIN FETCH c.departments
        WHERE c.id IN (
            SELECT c2.id FROM Controle c2
            JOIN c2.departments d2
            WHERE d2.identity = :identity)
        ORDER BY c.controleId
    """)
    List<Controle> findByDepartmentIdentity(@Param("identity") String identity);
}




////////----////



List<Department> findByIdentityIn(Collection<String> identities);




///////////////


package com.bnpp.irb.datalake.portail.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bnpp.irb.datalake.portail.dto.ControleResponse;
import com.bnpp.irb.datalake.portail.dto.ControleResponse.DepartmentInfo;
import com.bnpp.irb.datalake.portail.dto.CreateControleRequest;
import com.bnpp.irb.datalake.portail.models.Controle;
import com.bnpp.irb.datalake.portail.models.Department;
import com.bnpp.irb.datalake.portail.repository.ControleRepository;
import com.bnpp.irb.datalake.portail.repository.DepartmentRepository;

@Service
public class ControleService {

    private final ControleRepository controleRepository;
    private final DepartmentRepository departmentRepository;

    public ControleService(ControleRepository controleRepository,
                           DepartmentRepository departmentRepository) {
        this.controleRepository = controleRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<ControleResponse> getAll() {
        return controleRepository.findAllWithDepartments().stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ControleResponse> getByDepartmentIdentity(String identity) {
        return controleRepository.findByDepartmentIdentity(identity).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public ControleResponse create(CreateControleRequest request) {
        if (controleRepository.existsByControleId(request.getControleId())) {
            throw new IllegalArgumentException("Ce controleId existe déjà : " + request.getControleId());
        }

        Set<String> wanted = new LinkedHashSet<>(request.getDepartmentIdentities());
        List<Department> found = departmentRepository.findByIdentityIn(wanted);

        if (found.size() != wanted.size()) {
            Set<String> foundIds = found.stream().map(Department::getIdentity)
                    .collect(Collectors.toSet());
            wanted.removeAll(foundIds);
            throw new IllegalArgumentException("Départements introuvables : " + wanted);
        }

        Controle c = new Controle();
        c.setControleId(request.getControleId());
        c.setTitle(request.getTitle());
        c.setDescription(request.getDescription());
        c.setNbJoursApresArret(request.getNbJoursApresArret());
        c.setFrequence(request.getFrequence());
        c.setDepartments(new LinkedHashSet<>(found));

        return toResponse(controleRepository.save(c));
    }

    private ControleResponse toResponse(Controle c) {
        List<DepartmentInfo> deps = c.getDepartments().stream()
                .map(d -> new DepartmentInfo(d.getIdentity(), d.getDirection(),
                                             d.getDepartement(), d.getEntity()))
                .toList();
        return new ControleResponse(
            c.getId(), c.getControleId(), c.getTitle(), c.getDescription(),
            c.getNbJoursApresArret(), c.getFrequence(), deps);
    }
}



/////////////


package com.bnpp.irb.datalake.portail.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bnpp.irb.datalake.portail.dto.ControleResponse;
import com.bnpp.irb.datalake.portail.dto.CreateControleRequest;
import com.bnpp.irb.datalake.portail.service.ControleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/controles")
public class ControleController {

    private final ControleService service;

    public ControleController(ControleService service) {
        this.service = service;
    }

    @GetMapping
    public List<ControleResponse> all(@RequestParam(required = false) String departmentIdentity) {
        if (departmentIdentity != null && !departmentIdentity.isBlank()) {
            return service.getByDepartmentIdentity(departmentIdentity);
        }
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<ControleResponse> create(@Valid @RequestBody CreateControleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}





////////










