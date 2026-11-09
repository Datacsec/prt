package com.bnpp.irb.datalake.portail.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bnpp.irb.datalake.portail.models.Controle;
import com.bnpp.irb.datalake.portail.models.ControleHistory;
import com.bnpp.irb.datalake.portail.models.Department;
import com.bnpp.irb.datalake.portail.models.User;
import com.bnpp.irb.datalake.portail.repository.ControleHistoryRepository;
import com.bnpp.irb.datalake.portail.repository.ControleRepository;

@Service
public class ControleHistoryService {

    private static final Set<Integer> MOIS_TRIMESTRIELS = Set.of(1, 4, 7, 10);

    private final ControleRepository controleRepository;
    private final ControleHistoryRepository historyRepository;

    public ControleHistoryService(ControleRepository controleRepository,
                                  ControleHistoryRepository historyRepository) {
        this.controleRepository = controleRepository;
        this.historyRepository = historyRepository;
    }

    // Le 1er de chaque mois à 00:05
    @Scheduled(cron = "0 5 0 1 * *", zone = "Africa/Casablanca")
    public void ouvertureAutomatique() {
        ouvrirPeriode(YearMonth.now().minusMonths(1));
    }

    /**
     * Ouvre les contrôles pour le mois d'arrêté donné.
     * Ex. moisArrete = 2026-10 (ouverture le 1er novembre).
     * Retourne le nombre de lignes créées.
     */
    @Transactional
    public int ouvrirPeriode(YearMonth moisArrete) {
        int moisOuverture = moisArrete.plusMonths(1).getMonthValue();
        LocalDate dateArrete = moisArrete.atEndOfMonth();
        String moisControle = moisArrete.toString(); // "2026-10"
        int crees = 0;

        for (Controle c : controleRepository.findAllWithDepartments()) {
            if (!estOuvert(c.getFrequence(), moisOuverture)) {
                continue;
            }
            for (Department d : c.getDepartments()) {
                if (historyRepository.existsByControleIdAndDepartmentIdentityAndMoisControle(
                        c.getControleId(), d.getIdentity(), moisControle)) {
                    continue; // déjà ouvert : pas de doublon
                }
                historyRepository.save(construireLigne(c, d, moisControle, dateArrete));
                crees++;
            }
        }
        return crees;
    }

    private boolean estOuvert(String frequence, int moisOuverture) {
        if (frequence == null) return false;
        return switch (frequence.trim().toLowerCase()) {
            case "monthly" -> true;
            case "quarterly" -> MOIS_TRIMESTRIELS.contains(moisOuverture);
            default -> false;
        };
    }

    private ControleHistory construireLigne(Controle c, Department d,
                                            String moisControle, LocalDate dateArrete) {
        ControleHistory h = new ControleHistory();
        h.setMoisControle(moisControle);
        h.setDateArrete(dateArrete);
        if (c.getNbJoursApresArret() != null) {
            h.setDateLimite(dateArrete.plusDays(c.getNbJoursApresArret()));
        }

        // Copie du contrôle
        h.setControleId(c.getControleId());
        h.setControleTitle(c.getTitle());
        h.setControleDescription(c.getDescription());
        h.setControleFrequence(c.getFrequence());
        h.setControleNbJoursApresArret(c.getNbJoursApresArret());

        // Copie du département
        h.setDepartmentIdentity(d.getIdentity());
        h.setDepartmentDirection(d.getDirection());
        h.setDepartmentDepartement(d.getDepartement());
        h.setDepartmentEntity(d.getEntity());

        User m = d.getManager();
        if (m != null) {
            h.setManagerUid(m.getUid());
            h.setManagerNom(m.getNom());
            h.setManagerPrenom(m.getPrenom());
        }

        h.setStatut(ControleHistory.StatutControle.A_REMPLIR);
        return h;
    }
}




//////////




@EnableScheduling   // import org.springframework.scheduling.annotation.EnableScheduling
@SpringBootApplication
public class PortailApplication { ... }






/////////




package com.bnpp.irb.datalake.portail.controller;

import java.time.YearMonth;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.bnpp.irb.datalake.portail.service.ControleHistoryService;

@RestController
@RequestMapping("/HistoriqueApi")
public class ControleHistoryController {

    private final ControleHistoryService service;

    public ControleHistoryController(ControleHistoryService service) {
        this.service = service;
    }

    // POST /HistoriqueApi/ouvrir?mois=2026-10  (mois = mois d'arrêté)
    @PostMapping("/ouvrir")
    public Map<String, Object> ouvrir(@RequestParam String mois) {
        int crees = service.ouvrirPeriode(YearMonth.parse(mois));
        return Map.of("mois", mois, "lignesCreees", crees);
    }
}




