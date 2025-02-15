package com.example.Backend.Controller;

import com.example.Backend.Model.SignosVitales;
import com.example.Backend.Service.SignosVitalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/signos-vitales")
public class SignosVitalesController {

    @Autowired
    private SignosVitalesService signosVitalesService;

    @GetMapping
    public List<SignosVitales> obtenerSignosVitales() {
        return signosVitalesService.obtenerTodos();
    }
}
