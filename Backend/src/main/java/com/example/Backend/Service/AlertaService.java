package com.example.Backend.Service;

import java.util.List;

import com.example.Backend.Model.Alerta;
import com.example.Backend.Model.SignosVitales;

public interface AlertaService {
    void procesarAlertas(List<Alerta> alertas);
    List<Alerta> obtenerAlertas(String pacienteId, String tipo);
    void procesarSignosVitales(SignosVitales signosVitales);
}
