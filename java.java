package com.bnpp.irb.datalake.portail.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.bnpp.irb.datalake.portail.models.ControleHistory;

public record ControleHistoryResponse(
        Long id,
        String moisControle,
        LocalDate dateArrete,
        LocalDate dateLimite,
        String controleId,
        String controleTitle,
        String controleDescription,
        String controleFrequence,
        Integer controleNbJoursApresArret,
        String departmentIdentity,
        String departmentDirection,
        String departmentDepartement,
        String departmentEntity,
        String managerUid,
        String managerNom,
        String managerPrenom,
        String userUid,
        String userNom,
        String userPrenom,
        String statut,
        String commentaire,
        LocalDateTime dateRemplissage,
        LocalDateTime dateDerniereMiseAJour,
        List<String> cheminsS3,
        LocalDateTime dateValidation,
        String validateurUid,
        String validateurNom,
        String validateurPrenom) {

    // Conversion entité -> DTO, réutilisable par toutes les APIs
    public static ControleHistoryResponse from(ControleHistory h) {
        return new ControleHistoryResponse(
                h.getId(), h.getMoisControle(), h.getDateArrete(), h.getDateLimite(),
                h.getControleId(), h.getControleTitle(), h.getControleDescription(),
                h.getControleFrequence(), h.getControleNbJoursApresArret(),
                h.getDepartmentIdentity(), h.getDepartmentDirection(),
                h.getDepartmentDepartement(), h.getDepartmentEntity(),
                h.getManagerUid(), h.getManagerNom(), h.getManagerPrenom(),
                h.getUserUid(), h.getUserNom(), h.getUserPrenom(),
                h.getStatut().name(), h.getCommentaire(),
                h.getDateRemplissage(), h.getDateDerniereMiseAJour(),
                List.copyOf(h.getCheminsS3()),
                h.getDateValidation(), h.getValidateurUid(),
                h.getValidateurNom(), h.getValidateurPrenom());
    }
}




/////////////



@Query("""
    SELECT DISTINCT h FROM ControleHistory h
    LEFT JOIN FETCH h.cheminsS3
    WHERE h.departmentDepartement = :departement
      AND (:direction IS NULL OR h.departmentDirection = :direction)
      AND h.moisControle = (
          SELECT MAX(h2.moisControle) FROM ControleHistory h2
          WHERE h2.departmentDepartement = :departement
            AND (:direction IS NULL OR h2.departmentDirection = :direction))
    ORDER BY h.controleId, h.departmentEntity
""")
List<ControleHistory> findDernierMoisByDepartement(
        @Param("departement") String departement,
        @Param("direction") String direction);



////////

@Transactional(readOnly = true)
public List<ControleHistoryResponse> getDernierMoisByDepartement(String departement,
                                                                 String direction) {
    return historyRepository.findDernierMoisByDepartement(departement, direction)
            .stream()
            .map(ControleHistoryResponse::from)
            .toList();
}


///////


@GetMapping("/dernier-mois")
public List<ControleHistoryResponse> dernierMois(
        @RequestParam String departement,
        @RequestParam(required = false) String direction) {
    return service.getDernierMoisByDepartement(departement, direction);
}



