package com.bnpp.irb.datalake.portail.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "controle_history",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_history_controle_dept_mois",
           columnNames = {"controle_id", "department_identity", "mois_controle"}))
public class ControleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== Période =====
    // Format "2026-10" : mois du contrôle
    @Column(name = "mois_controle", nullable = false, length = 7)
    private String moisControle;

    @Column(name = "date_arrete")
    private LocalDate dateArrete;

    @Column(name = "date_limite")
    private LocalDate dateLimite;

    // ===== Copie du contrôle (au moment de l'ouverture) =====
    @Column(name = "controle_id", nullable = false)
    private String controleId;

    @Column(name = "controle_title", nullable = false)
    private String controleTitle;

    @Column(name = "controle_description", length = 1000)
    private String controleDescription;

    @Column(name = "controle_frequence")
    private String controleFrequence;

    @Column(name = "controle_nb_jours_apres_arret")
    private Integer controleNbJoursApresArret;

    // ===== Copie du département =====
    @Column(name = "department_identity", nullable = false)
    private String departmentIdentity;

    @Column(name = "department_direction")
    private String departmentDirection;

    @Column(name = "department_departement")
    private String departmentDepartement;

    @Column(name = "department_entity")
    private String departmentEntity;

    @Column(name = "manager_uid")
    private String managerUid;

    @Column(name = "manager_nom")
    private String managerNom;

    @Column(name = "manager_prenom")
    private String managerPrenom;

    // ===== Copie du collaborateur qui renseigne =====
    @Column(name = "user_uid")
    private String userUid;

    @Column(name = "user_nom")
    private String userNom;

    @Column(name = "user_prenom")
    private String userPrenom;

    @Column(name = "user_role")
    private String userRole;

    @Column(name = "user_responsable_uid")
    private String userResponsableUid;

    // ===== Suivi du remplissage =====
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutControle statut = StatutControle.A_REMPLIR;

    @Column(length = 2000)
    private String commentaire;

    @Column(name = "date_remplissage")
    private LocalDateTime dateRemplissage;

    @Column(name = "date_derniere_mise_a_jour")
    private LocalDateTime dateDerniereMiseAJour;

    // Plusieurs fichiers possibles par contrôle
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "controle_history_fichier",
                     joinColumns = @JoinColumn(name = "history_id"))
    @Column(name = "chemin_s3", nullable = false, length = 1000)
    private Set<String> cheminsS3 = new LinkedHashSet<>();

    // ===== Validation =====
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "validateur_uid")
    private String validateurUid;

    @Column(name = "validateur_nom")
    private String validateurNom;

    @Column(name = "validateur_prenom")
    private String validateurPrenom;

    @Column(name = "validateur_role")
    private String validateurRole;

    @PrePersist
    @PreUpdate
    void touch() {
        this.dateDerniereMiseAJour = LocalDateTime.now();
    }

    public enum StatutControle {
        A_REMPLIR, REMPLI, VALIDE, REJETE
    }

    // ===== Getters / setters =====
    public Long getId() { return id; }

    public String getMoisControle() { return moisControle; }
    public void setMoisControle(String moisControle) { this.moisControle = moisControle; }

    public LocalDate getDateArrete() { return dateArrete; }
    public void setDateArrete(LocalDate dateArrete) { this.dateArrete = dateArrete; }

    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }

    public String getControleId() { return controleId; }
    public void setControleId(String controleId) { this.controleId = controleId; }

    public String getControleTitle() { return controleTitle; }
    public void setControleTitle(String controleTitle) { this.controleTitle = controleTitle; }

    public String getControleDescription() { return controleDescription; }
    public void setControleDescription(String controleDescription) { this.controleDescription = controleDescription; }

    public String getControleFrequence() { return controleFrequence; }
    public void setControleFrequence(String controleFrequence) { this.controleFrequence = controleFrequence; }

    public Integer getControleNbJoursApresArret() { return controleNbJoursApresArret; }
    public void setControleNbJoursApresArret(Integer v) { this.controleNbJoursApresArret = v; }

    public String getDepartmentIdentity() { return departmentIdentity; }
    public void setDepartmentIdentity(String departmentIdentity) { this.departmentIdentity = departmentIdentity; }

    public String getDepartmentDirection() { return departmentDirection; }
    public void setDepartmentDirection(String departmentDirection) { this.departmentDirection = departmentDirection; }

    public String getDepartmentDepartement() { return departmentDepartement; }
    public void setDepartmentDepartement(String departmentDepartement) { this.departmentDepartement = departmentDepartement; }

    public String getDepartmentEntity() { return departmentEntity; }
    public void setDepartmentEntity(String departmentEntity) { this.departmentEntity = departmentEntity; }

    public String getManagerUid() { return managerUid; }
    public void setManagerUid(String managerUid) { this.managerUid = managerUid; }

    public String getManagerNom() { return managerNom; }
    public void setManagerNom(String managerNom) { this.managerNom = managerNom; }

    public String getManagerPrenom() { return managerPrenom; }
    public void setManagerPrenom(String managerPrenom) { this.managerPrenom = managerPrenom; }

    public String getUserUid() { return userUid; }
    public void setUserUid(String userUid) { this.userUid = userUid; }

    public String getUserNom() { return userNom; }
    public void setUserNom(String userNom) { this.userNom = userNom; }

    public String getUserPrenom() { return userPrenom; }
    public void setUserPrenom(String userPrenom) { this.userPrenom = userPrenom; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getUserResponsableUid() { return userResponsableUid; }
    public void setUserResponsableUid(String userResponsableUid) { this.userResponsableUid = userResponsableUid; }

    public StatutControle getStatut() { return statut; }
    public void setStatut(StatutControle statut) { this.statut = statut; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public LocalDateTime getDateRemplissage() { return dateRemplissage; }
    public void setDateRemplissage(LocalDateTime dateRemplissage) { this.dateRemplissage = dateRemplissage; }

    public LocalDateTime getDateDerniereMiseAJour() { return dateDerniereMiseAJour; }

    public Set<String> getCheminsS3() { return cheminsS3; }
    public void setCheminsS3(Set<String> cheminsS3) { this.cheminsS3 = cheminsS3; }

    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }

    public String getValidateurUid() { return validateurUid; }
    public void setValidateurUid(String validateurUid) { this.validateurUid = validateurUid; }

    public String getValidateurNom() { return validateurNom; }
    public void setValidateurNom(String validateurNom) { this.validateurNom = validateurNom; }

    public String getValidateurPrenom() { return validateurPrenom; }
    public void setValidateurPrenom(String validateurPrenom) { this.validateurPrenom = validateurPrenom; }

    public String getValidateurRole() { return validateurRole; }
    public void setValidateurRole(String validateurRole) { this.validateurRole = validateurRole; }
}
