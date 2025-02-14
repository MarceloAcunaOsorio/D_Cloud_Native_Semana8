package com.example.Backend.Service.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Backend.DTO.PacienteDTO;
import com.example.Backend.Exceptions.NotFoundException;
import com.example.Backend.Model.Paciente;
import com.example.Backend.Repository.PacienteRepository;
import com.example.Backend.Service.PacienteService;

@Service
public class PacienteServiceImpl implements PacienteService{
    
     private final PacienteRepository pacienteRepository;
    
    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }


    // Implementar los métodos de la interfaz PacienteService

    // Método para obtener todos los pacientes
    @Override
    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAll();
    }


    // Método para obtener un paciente por su id
    @Override
    public Optional<Paciente> getPacienteById(String rut) {
        return pacienteRepository.findById(rut);
    }


    // Método para crear un paciente
    @Override
    public Paciente createPaciente(Paciente paciente)throws IOException {     
        return pacienteRepository.save(paciente);
    }


    // Método para actualizar un paciente
    @Override
    public Paciente updatePaciente(String rut,Paciente paciente){

        if(!pacienteRepository.existsById(rut)){
            throw new NotFoundException("Paciente no encontrado en ID:"+ paciente.getrut());
        }
        paciente.setrut(rut);
        return pacienteRepository.save(paciente);
    }

    // metodo para elimar un paciente
    @Override
    public void deletePaciente(Paciente paciente) throws IOException {
        
        if(!pacienteRepository.existsById(paciente.getrut())){
            throw new NotFoundException("Paciente no encontrado en ID:"+paciente.getrut());
        }
        pacienteRepository.deleteById(paciente.getrut());
    }

    @Override
    public Map<String, Object> generarReporteSaludCompleto(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con ID: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        PacienteDTO pacienteDTO = new PacienteDTO(
            paciente.getrut(),
            paciente.getNombre(),
            paciente.getApellido(),
            paciente.getEdad(),
            paciente.getHabitacion(),
            paciente.getFrecuenciaCardiaca(),
            paciente.getOxigeno(),
            paciente.getPresionSistolica(),
            paciente.getPresionDiastolica(),
            paciente.getPresionArterial(),
            paciente.getTemperatura(),
            paciente.getCondicion(),
            paciente.getUltimoControl(),
            paciente.getObservaciones(),
            paciente.getHistorialMedico(),
            new ArrayList<>(),
            paciente.getMedicamentos(),
            0, // IMC será calculado
            paciente.getUltimaActualizacion(),
            0 // Glucosa será calculada
        );
        
        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("informacionBasica", Map.of(
            "nombreCompleto", pacienteDTO.getNombre() + " " + pacienteDTO.getApellido(),
            "edad", pacienteDTO.getEdad(),
            "habitacion", pacienteDTO.getHabitacion(),
            "condicion", pacienteDTO.getCondicion()
        ));
        
        reporte.put("estadoActual", Map.of(
            "presionArterial", String.format("%.0f/%.0f mmHg (%s)", 
                pacienteDTO.getPresionSistolica(),
                pacienteDTO.getPresionDiastolica(),
                pacienteDTO.getEstadoPresion()),
            "frecuenciaCardiaca", String.format("%.0f lpm (%s)", 
                pacienteDTO.getFrecuenciaCardiaca(),
                pacienteDTO.evaluarAlertaFrecuencia(pacienteDTO.getFrecuenciaCardiaca())),
            "oxigeno", String.format("%.1f%% (%s)", 
                pacienteDTO.getOxigeno(),
                pacienteDTO.getEstadoOxigeno()),
            "temperatura", String.format("%.1f°C (%s)", 
                pacienteDTO.getTemperatura(),
                pacienteDTO.evaluarAlertaTemperatura(pacienteDTO.getTemperatura()))
        ));
        
        reporte.put("analisisSalud", Map.of(
            "riesgoCardiovascular", pacienteDTO.getRiesgoCardiovascular(),
            "edadBiologica", String.format("%.1f años", pacienteDTO.calcularEdadBiologica()),
            "alertasActivas", pacienteDTO.getAlertasMedicas(),
            "recomendaciones", pacienteDTO.generarRecomendaciones()
        ));
        
        reporte.put("historial", Map.of(
            "ultimoControl", pacienteDTO.getUltimoControl(),
            "ultimaActualizacion", pacienteDTO.getUltimaRevision(),
            "medicamentosActuales", pacienteDTO.getMedicamentos(),
            "evolucionCondicion", paciente.getEvolucionCondicion()
        ));
        
        return reporte;
    }

    @Override
    public Map<String, Object> obtenerEstadisticasSalud(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con ID: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("promedioPresion", paciente.getHistorialPresion().stream().mapToDouble(Double::doubleValue).average().orElse(0));
        estadisticas.put("promedioFrecuencia", paciente.getHistorialFrecuencia().stream().mapToDouble(Double::doubleValue).average().orElse(0));
        estadisticas.put("tendencias", Map.of(
            "presion", calcularTendencia(paciente.getHistorialPresion()),
            "frecuencia", calcularTendencia(paciente.getHistorialFrecuencia())
        ));
        return estadisticas;
    }

    @Override
    public Map<String, Object> obtenerAlertasSaludDetalladas(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con ID: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        Map<String, Object> alertas = new HashMap<>();
        alertas.put("presionArterial", evaluarAlertaPresion(paciente.getPresionArterial()));
        alertas.put("frecuenciaCardiaca", evaluarAlertaFrecuencia(paciente.getFrecuenciaCardiaca()));
        alertas.put("temperatura", evaluarAlertaTemperatura(paciente.getTemperatura()));
        return alertas;
    }

    @Override
    public Map<String, Object> obtenerSeguimientoCondicionCronica(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con ID: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        Map<String, Object> seguimiento = new HashMap<>();
        seguimiento.put("condicion", paciente.getCondicion());
        seguimiento.put("evolucion", paciente.getEvolucionCondicion());
        seguimiento.put("medicamentos", paciente.getMedicamentos());
        seguimiento.put("ultimosControles", paciente.getUltimosControles());
        return seguimiento;
    }

    @Override
    public List<String> obtenerHistorialMedico(String rut) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(rut);
        if (pacienteOpt.isEmpty()) {
            throw new NotFoundException("Paciente no encontrado con ID: " + rut);
        }
        
        Paciente paciente = pacienteOpt.get();
        return paciente.getHistorialMedico();
    }

    @Override
    public byte[] exportarReporte(String rut, String formato) {
        Map<String, Object> reporte = generarReporteSaludCompleto(rut);
        
        if (formato.equalsIgnoreCase("pdf")) {
            // Implementar generación de PDF
            return reporte.toString().getBytes();
        } else if (formato.equalsIgnoreCase("csv")) {
            // Implementar generación de CSV
            return reporte.toString().getBytes();
        }
        
        throw new IllegalArgumentException("Formato no soportado: " + formato);
    }

    private String calcularTendencia(List<Double> valores) {
        if (valores.size() < 2) return "Estable";
        double ultimo = valores.get(valores.size() - 1);
        double penultimo = valores.get(valores.size() - 2);
        return ultimo > penultimo ? "Ascendente" : ultimo < penultimo ? "Descendente" : "Estable";
    }

    private Map<String, String> evaluarAlertaPresion(double presion) {
        Map<String, String> alerta = new HashMap<>();
        if (presion > 140) {
            alerta.put("nivel", "ALTO");
            alerta.put("recomendacion", "Consultar inmediatamente con médico");
        } else if (presion < 90) {
            alerta.put("nivel", "BAJO");
            alerta.put("recomendacion", "Monitorear y consultar si persiste");
        } else {
            alerta.put("nivel", "NORMAL");
            alerta.put("recomendacion", "Continuar monitoreo regular");
        }
        return alerta;
    }

    private Map<String, String> evaluarAlertaFrecuencia(double frecuencia) {
        Map<String, String> alerta = new HashMap<>();
        if (frecuencia > 100) {
            alerta.put("nivel", "ALTO");
            alerta.put("recomendacion", "Consultar con médico si persiste");
        } else if (frecuencia < 60) {
            alerta.put("nivel", "BAJO");
            alerta.put("recomendacion", "Monitorear y consultar si hay síntomas");
        } else {
            alerta.put("nivel", "NORMAL");
            alerta.put("recomendacion", "Continuar monitoreo regular");
        }
        return alerta;
    }

    private Map<String, String> evaluarAlertaTemperatura(double temperatura) {
        Map<String, String> alerta = new HashMap<>();
        if (temperatura > 38) {
            alerta.put("nivel", "FIEBRE");
            alerta.put("recomendacion", "Tomar antipirético y consultar si persiste");
        } else if (temperatura < 36) {
            alerta.put("nivel", "BAJA");
            alerta.put("recomendacion", "Abrigarse y monitorear");
        } else {
            alerta.put("nivel", "NORMAL");
            alerta.put("recomendacion", "Continuar monitoreo regular");
        }
        return alerta;
    }
}
