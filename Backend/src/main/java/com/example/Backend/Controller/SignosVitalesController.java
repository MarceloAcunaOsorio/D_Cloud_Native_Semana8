package com.example.Backend.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Model.SignosVitales;
import com.example.Backend.Service.Signos_VitalesRep;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class SignosVitalesController {
    
    private final Signos_VitalesRep signosvitales_resp;

    //constructor
    public SignosVitalesController(Signos_VitalesRep signosvitales_resp) {
        this.signosvitales_resp = signosvitales_resp;
    }


    //listar signos
      //Listar todos los pacientes
    @GetMapping("/signos-vitales")
    public List<SignosVitales> getAllSignosVitales() {
        return signosvitales_resp.getAllSignosVitales();
    }
}
