package com.flamabrava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MesaNotFoundException extends RuntimeException {
    public MesaNotFoundException(Integer id) {
        super("No se encontró la mesa con ID: " + id);
    }
}
