package com.example.Backend.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.Backend.Config.RabbitMQConfig;
import com.example.Backend.Model.Alerta;
import com.example.Backend.Repository.AlertaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AlertaConsumer {

    private final AlertaRepository alertaRepository;
    private final ObjectMapper objectMapper;
    
    @Value("${alertas.archivos.ruta:/tmp/alertas}")
    private String rutaArchivos;

    public AlertaConsumer(AlertaRepository alertaRepository, ObjectMapper objectMapper) {
        this.alertaRepository = alertaRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ALERTAS)
    public void recibirAlerta(String mensaje) {
        try {
            JsonNode alertaJson = objectMapper.readTree(mensaje);
            
            // Crear y guardar la alerta en la base de datos
            Alerta alerta = new Alerta();
            alerta.setPacienteId(alertaJson.get("pacienteId").asLong());
            alerta.setFecha(LocalDateTime.parse(alertaJson.get("fecha").asText()));
            alerta.setDescripcion(String.join(", ", 
                StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                        alertaJson.get("alertas").elements(),
                        Spliterator.ORDERED
                    ),
                    false
                )
                .map(JsonNode::asText)
                .toList()
            ));
            alerta.setTipo("ALERTA");
            
            alertaRepository.save(alerta);
            
            // Generar archivo JSON
            generarArchivoJson(alerta);
            
            System.out.println("Alerta procesada y guardada: " + mensaje);
        } catch (Exception e) {
            System.err.println("Error al procesar alerta: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RESUMEN)
    public void recibirResumen(String mensaje) {
        try {
            JsonNode resumenJson = objectMapper.readTree(mensaje);
            
            // Crear y guardar el resumen en la base de datos
            Alerta resumen = new Alerta();
            resumen.setFecha(LocalDateTime.parse(resumenJson.get("fecha").asText()));
            resumen.setDescripcion(String.format(
                "Resumen periódico - Total registros: %d, Total alertas: %d",
                resumenJson.get("totalRegistros").asInt(),
                resumenJson.get("totalAlertas").asLong()
            ));
            resumen.setTipo("RESUMEN");
            
            alertaRepository.save(resumen);
            
            // Generar archivo JSON
            generarArchivoJson(resumen);
            
            System.out.println("Resumen procesado y guardado: " + mensaje);
        } catch (Exception e) {
            System.err.println("Error al procesar resumen: " + e.getMessage());
        }
    }

    private void generarArchivoJson(Alerta alerta) {
        try {
            // Crear directorio si no existe
            File directorio = new File(rutaArchivos);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Generar nombre de archivo con timestamp
            String timestamp = alerta.getFecha().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = String.format("%s_%s_%d.json",
                alerta.getTipo().toLowerCase(),
                timestamp,
                alerta.getId()
            );

            // Escribir archivo JSON
            String rutaCompleta = Paths.get(rutaArchivos, nombreArchivo).toString();
            String jsonContent = objectMapper.writeValueAsString(alerta);
            Files.write(Paths.get(rutaCompleta), jsonContent.getBytes());

            System.out.println("Archivo JSON generado: " + rutaCompleta);
        } catch (Exception e) {
            System.err.println("Error al generar archivo JSON: " + e.getMessage());
        }
    }
}
