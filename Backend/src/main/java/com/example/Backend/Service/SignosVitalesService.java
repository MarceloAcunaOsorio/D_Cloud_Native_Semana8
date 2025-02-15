package com.example.Backend.Service;

import com.example.Backend.Model.SignosVitales;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignosVitalesService {

    public List<SignosVitales> obtenerTodos() {
        // Implementar lógica para obtener los signos vitales
        return List.of(
            new SignosVitales(80, "120/80", 36.5),
            new SignosVitales(75, "110/70", 37.0)
        );
    }
}
