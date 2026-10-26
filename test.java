package com.bnpp.irb.datalake.portail.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identity;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private String entity;

    @Column(nullable = false)
    private String departement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToMany(mappedBy = "department")
    private List<User> users = new ArrayList<>();

    public Long getId() { return id; }

    public String getIdentity() { return identity; }
    public void setIdentity(String identity) { this.identity = identity; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}





//////////////





package com.bnpp.irb.datalake.portail.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uid;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ADMIN, USER, etc.

    @Column(nullable = false)
    private String status; // ACTIF, INACTIF

    // Nouveau Column ajouté suite à l'ajout de beacon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    public Long getId() { return id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}


///////////


package com.bnpp.irb.datalake.portail.models;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bnpp.irb.datalake.portail.dto.UserResponse;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUid(String uid);

    @Query("""
        SELECT new com.bnpp.irb.datalake.portail.dto.UserResponse(
            u.id, u.uid, u.nom, u.prenom, u.role, u.status,
            d.id, d.departement)
        FROM User u
        LEFT JOIN u.department d
    """)
    List<UserResponse> findAllWithDepartment();
}





///////




package com.bnpp.irb.datalake.portail.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bnpp.irb.datalake.portail.dto.DepartmentResponse;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByIdentity(String identity);

    @Query("""
        SELECT new com.bnpp.irb.datalake.portail.dto.DepartmentResponse(
            d.id, d.identity, d.direction, d.entity, d.departement,
            m.nom, m.prenom)
        FROM Department d
        LEFT JOIN d.manager m
    """)
    List<DepartmentResponse> findAllWithManager();
}






//////

package com.bnpp.irb.datalake.portail.dto;

public class DepartmentResponse {

    private Long id;
    private String identity;
    private String direction;
    private String entity;
    private String departement;
    private String managerNom;
    private String managerPrenom;

    public DepartmentResponse(Long id, String identity, String direction,
                              String entity, String departement,
                              String managerNom, String managerPrenom) {
        this.id = id;
        this.identity = identity;
        this.direction = direction;
        this.entity = entity;
        this.departement = departement;
        this.managerNom = managerNom;
        this.managerPrenom = managerPrenom;
    }

    public Long getId() { return id; }
    public String getIdentity() { return identity; }
    public String getDirection() { return direction; }
    public String getEntity() { return entity; }
    public String getDepartement() { return departement; }
    public String getManagerNom() { return managerNom; }
    public String getManagerPrenom() { return managerPrenom; }
}



///////


package com.bnpp.irb.datalake.portail.dto;

public class UserResponse {

    private Long id;
    private String uid;
    private String nom;
    private String prenom;
    private String role;
    private String status;
    private Long departmentId;
    private String departementNom;

    public UserResponse(Long id, String uid, String nom, String prenom,
                        String role, String status,
                        Long departmentId, String departementNom) {
        this.id = id;
        this.uid = uid;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.status = status;
        this.departmentId = departmentId;
        this.departementNom = departementNom;
    }

    public Long getId() { return id; }
    public String getUid() { return uid; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartementNom() { return departementNom; }
}



///::/:

package com.bnpp.irb.datalake.portail.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateDepartmentRequest {

    @NotBlank(message = "L'identity est obligatoire")
    private String identity;

    @NotBlank(message = "La direction est obligatoire")
    private String direction;

    @NotBlank(message = "L'entity est obligatoire")
    private String entity;

    @NotBlank(message = "Le département est obligatoire")
    private String departement;

    // optionnel : null = pas de responsable
    private String managerUid;

    public String getIdentity() { return identity; }
    public void setIdentity(String identity) { this.identity = identity; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public String getManagerUid() { return managerUid; }
    public void setManagerUid(String managerUid) { this.managerUid = managerUid; }
}


///////

package com.bnpp.irb.datalake.portail.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bnpp.irb.datalake.portail.dto.CreateDepartmentRequest;
import com.bnpp.irb.datalake.portail.dto.DepartmentResponse;
import com.bnpp.irb.datalake.portail.dto.UserResponse;
import com.bnpp.irb.datalake.portail.models.Department;
import com.bnpp.irb.datalake.portail.models.DepartmentRepository;
import com.bnpp.irb.datalake.portail.models.User;
import com.bnpp.irb.datalake.portail.models.UserRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                             UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<DepartmentResponse> getDepartments() {
        return departmentRepository.findAllWithManager();
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAllWithDepartment();
    }

    @Transactional
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        if (departmentRepository.existsByIdentity(request.getIdentity())) {
            throw new IllegalArgumentException("Cet identity existe déjà");
        }

        Department department = new Department();
        department.setIdentity(request.getIdentity());
        department.setDirection(request.getDirection());
        department.setEntity(request.getEntity());
        department.setDepartement(request.getDepartement());

        // Manager facultatif
        if (request.getManagerUid() != null && !request.getManagerUid().isBlank()) {
            User manager = userRepository.findByUid(request.getManagerUid())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Manager introuvable : " + request.getManagerUid()));
            department.setManager(manager);
        }

        Department saved = departmentRepository.save(department);

        User m = saved.getManager();
        return new DepartmentResponse(
            saved.getId(), saved.getIdentity(), saved.getDirection(),
            saved.getEntity(), saved.getDepartement(),
            m != null ? m.getNom() : null,
            m != null ? m.getPrenom() : null);
    }

    @Transactional
    public void assignUser(Long userId, Long departmentId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setDepartment(departmentRepository.getReferenceById(departmentId));
    }

    @Transactional
    public void setManager(Long departmentId, Long userId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow();
        department.setManager(userRepository.getReferenceById(userId));
    }
}



//////////



package com.bnpp.irb.datalake.portail.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bnpp.irb.datalake.portail.dto.CreateDepartmentRequest;
import com.bnpp.irb.datalake.portail.dto.DepartmentResponse;
import com.bnpp.irb.datalake.portail.dto.UserResponse;
import com.bnpp.irb.datalake.portail.service.DepartmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping("/departments")
    public List<DepartmentResponse> departments() {
        return service.getDepartments();
    }

    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> create(
            @Valid @RequestBody CreateDepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createDepartment(request));
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return service.getUsers();
    }

    @PutMapping("/users/{userId}/department/{departmentId}")
    public void assign(@PathVariable Long userId, @PathVariable Long departmentId) {
        service.assignUser(userId, departmentId);
    }

    @PutMapping("/departments/{departmentId}/manager/{userId}")
    public void manager(@PathVariable Long departmentId, @PathVariable Long userId) {
        service.setManager(departmentId, userId);
    }
}






////




















