package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Backend.Model.Paciente;

public interface  PacienteRepository extends JpaRepository<Paciente,String> {

}
