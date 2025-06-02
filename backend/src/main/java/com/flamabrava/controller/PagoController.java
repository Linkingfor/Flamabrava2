package com.flamabrava.controller;

import com.flamabrava.service.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/pago")
public class PagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @PostMapping("/crear-preferencia")
    public String crearPreferencia(@RequestBody List<Map<String, Object>> productos) {

        return mercadoPagoService.createPreference(productos);
    }
}
