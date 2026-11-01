@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "department_id", referencedColumnName = "identity", nullable = true)
private Department department;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "responsable_uid", referencedColumnName = "uid", nullable = true)
private User responsable;

public Department getDepartment() { return department; }
public void setDepartment(Department department) { this.department = department; }
public User getResponsable() { return responsable; }
public void setResponsable(User responsable) { this.responsable = responsable; }









public record RegisterRequest(
    String uid, String nom, String prenom, String password,
    String role, String status,
    String responsableUid,        // optionnel
    String departmentIdentity     // optionnel
) {}




public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByIdentity(String identity);
}







@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    if (userRepository.findByUid(req.uid()).isPresent()) {
        return ResponseEntity.badRequest().body(Map.of("message", "UID déjà utilisé"));
    }

    User user = new User();
    user.setUid(req.uid());
    user.setNom(req.nom());
    user.setPrenom(req.prenom());
    user.setPassword(passwordEncoder.encode(req.password()));
    user.setRole(req.role());
    user.setStatus(req.status());

    if (req.responsableUid() != null && !req.responsableUid().isBlank()) {
        var resp = userRepository.findByUid(req.responsableUid());
        if (resp.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "Responsable introuvable"));
        user.setResponsable(resp.get());
    }

    if (req.departmentIdentity() != null && !req.departmentIdentity().isBlank()) {
        var dep = departmentRepository.findByIdentity(req.departmentIdentity());
        if (dep.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "Département introuvable"));
        user.setDepartment(dep.get());
    }

    userRepository.save(user);
    return ResponseEntity.ok(Map.of("message", "Utilisateur créé"));
}
















