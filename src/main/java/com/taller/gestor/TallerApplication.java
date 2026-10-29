package com.taller.gestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada principal de la aplicación.
 * Arquitectura modular: Controller -> Service -> DAO (JDBC Nativo con PreparedStatement).
 */
@SpringBootApplication
public class TallerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TallerApplication.class, args);
    }
}
