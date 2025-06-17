package com.exemplo.validacaocpf.controller;

import com.exemplo.validacaocpf.model.CpfResponse;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CpfController {
    @GetMapping("/admin/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> validateAdmin() {
        return ResponseEntity.ok("Rota admin protegida");
    }

    @GetMapping("/user/validate")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> validateUser() {
        return ResponseEntity.ok("Rota user protegida");
    }

    private final String API_URL = "https://api.cpfhub.io/api/cpf";
    private final String API_KEY = "65be66fdf9c1b2266a96102853af96b1a4595cb5dda5a74cc8bb89c7e2e2b9a5";

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/validate")
    public String validateCpf(@RequestParam String cpf,
            @RequestParam String birthdate,
            Model model) {

        Map<String, Object> payload = new HashMap<>();
        String birthFormatted = LocalDate.parse(birthdate).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        payload.put("cpf", cpf);
        payload.put("birthDate", birthFormatted);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<CpfResponse> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    request,
                    CpfResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                    && response.getBody().getSuccess() == 1) {
                CpfResponse.Data data = response.getBody().getData();

                int anosRegistro = calcularAnosDesdeRegistro(data.getRegistrationDate());
                String estado = identificarEstadoPorCPF(data.getCpfNumber());

                model.addAttribute("data", data);
                model.addAttribute("anosRegistro", anosRegistro);
                model.addAttribute("estado", estado);
                model.addAttribute("status", verificarStatus(data, anosRegistro, birthFormatted));
            } else {
                model.addAttribute("error", "Falha na validação do CPF.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erro na requisição: " + e.getMessage());
        }

        return "index";
    }

    private int calcularAnosDesdeRegistro(String registrationDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate registro = LocalDate.parse(registrationDate, formatter);
            return LocalDate.now().getYear() - registro.getYear();
        } catch (Exception e) {
            return 0;
        }
    }

    private String identificarEstadoPorCPF(String cpf) {
        char regiao = cpf.charAt(8);
        return switch (regiao) {
            case '1' -> "DF, GO, MT, MS, TO";
            case '2' -> "AC, AM, AP, PA, RO, RR";
            case '3' -> "CE, MA, PI";
            case '4' -> "AL, PB, PE, RN";
            case '5' -> "BA, SE";
            case '6' -> "MG";
            case '7' -> "ES, RJ";
            case '8' -> "SP";
            case '9' -> "PR, SC";
            case '0' -> "RS";
            default -> "Desconhecido";
        };
    }

    private String verificarStatus(CpfResponse.Data data, int anosRegistro, String inputBirthDate) {
        if (!"Aprovado".equals(data.getStatus()) || !"REGULAR".equals(data.getSituation())) {
            return "invalid";
        }
        if (!data.getBirthDate().equals(inputBirthDate)) {
            return "invalid";
        }
        if (anosRegistro < 1) {
            return "suspect";
        }
        return "valid";
    }
}
