package com.bnpp.irb.datalake.portail.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RemplirControleRequest(

        @NotBlank(message = "Le commentaire est obligatoire")
        @Size(max = 2000, message = "Le commentaire ne doit pas dépasser 2000 caractères")
        String commentaire,

        // Facultatif : chemins des fichiers déjà déposés sur S3
        List<String> cheminsS3) {
}



/////////////



private final ControleRepository controleRepository;
private final ControleHistoryRepository historyRepository;
private final UserRepository userRepository;

public ControleHistoryService(ControleRepository controleRepository,
                              ControleHistoryRepository historyRepository,
                              UserRepository userRepository) {
    this.controleRepository = controleRepository;
    this.historyRepository = historyRepository;
    this.userRepository = userRepository;
}

//////////


@Transactional
public ControleHistoryResponse remplir(Long historyId, RemplirControleRequest request,
                                       String userUid) {
    ControleHistory h = historyRepository.findById(historyId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Contrôle introuvable : " + historyId));

    if (h.getStatut() == ControleHistory.StatutControle.VALIDE) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "Ce contrôle est déjà validé, il ne peut plus être modifié");
    }

    User user = userRepository.findByUid(userUid)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Utilisateur inconnu : " + userUid));

    // Copie du collaborateur au moment du remplissage
    h.setUserUid(user.getUid());
    h.setUserNom(user.getNom());
    h.setUserPrenom(user.getPrenom());
    h.setUserRole(user.getRole());
    h.setUserResponsableUid(
        user.getResponsable() != null ? user.getResponsable().getUid() : null);

    // Contenu du remplissage
    h.setCommentaire(request.commentaire().trim());
    if (request.cheminsS3() != null) {
        h.getCheminsS3().clear();
        h.getCheminsS3().addAll(request.cheminsS3());
    }

    h.setStatut(ControleHistory.StatutControle.REMPLI);
    h.setDateRemplissage(LocalDateTime.now());

    // En cas de reprise après rejet, on efface l'ancienne validation
    h.setDateValidation(null);
    h.setValidateurUid(null);
    h.setValidateurNom(null);
    h.setValidateurPrenom(null);
    h.setValidateurRole(null);

    return ControleHistoryResponse.from(h);
}



//////////


import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.bnpp.irb.datalake.portail.dto.ControleHistoryResponse;
import com.bnpp.irb.datalake.portail.dto.RemplirControleRequest;
import com.bnpp.irb.datalake.portail.repository.UserRepository;


/////////



@PostMapping("/{id}/remplir")
public ControleHistoryResponse remplir(@PathVariable Long id,
                                       @Valid @RequestBody RemplirControleRequest request,
                                       Authentication authentication) {
    return service.remplir(id, request, authentication.getName());
}


/////////

import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import com.bnpp.irb.datalake.portail.dto.ControleHistoryResponse;
import com.bnpp.irb.datalake.portail.dto.RemplirControleRequest;





