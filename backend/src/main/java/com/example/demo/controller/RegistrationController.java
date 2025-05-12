// controller/RegistrationController.java
package com.example.demo.controller;

import com.example.demo.model.Registration;
import com.example.demo.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService service;

    @PostMapping
    public Registration registerUser(@RequestBody Registration registration) {
        return service.registerUser(registration);
    }

    @GetMapping
    public List<Registration> getAllRegistrations() {
        return service.getAllRegistrations();
    }
}
