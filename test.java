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

    // getters
    public Long getId() { return id; }
    public String getIdentity() { return identity; }
    public String getDirection() { return direction; }
    public String getEntity() { return entity; }
    public String getDepartement() { return departement; }
    public String getManagerNom() { return managerNom; }
    public String getManagerPrenom() { return managerPrenom; }
}


////////


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

    // getters
    public Long getId() { return id; }
    public String getUid() { return uid; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartementNom() { return departementNom; }
}



///////

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("""
        SELECT new com.bnpp.irb.datalake.portail.dto.DepartmentResponse(
            d.id, d.identity, d.direction, d.entity, d.departement,
            m.nom, m.prenom)
        FROM Department d
        LEFT JOIN d.manager m
    """)
    List<DepartmentResponse> findAllWithManager();
}


///////


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT new com.bnpp.irb.datalake.portail.dto.UserResponse(
            u.id, u.uid, u.nom, u.prenom, u.role, u.status,
            d.id, d.departement)
        FROM User u
        LEFT JOIN u.department d
    """)
    List<UserResponse> findAllWithDepartment();
}


/////










