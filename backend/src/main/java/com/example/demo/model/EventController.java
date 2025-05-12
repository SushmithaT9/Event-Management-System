package com.example.demo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000") // Adjust port as needed
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<String> createEvent(@RequestBody Event event) {
        System.out.println("Event received: " + event);
        boolean success = eventService.saveEvent(event);
        if (success) {
            return ResponseEntity.ok("Event saved successfully");
        } else {
            System.out.println("Failed to save event: " + event);  // Log detailed error info
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving event");
        }

    }


    // New GET method to fetch all events
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();  // You need to create this method in your service
        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(events);  // Return 204 if no events
        }
        return ResponseEntity.ok(events);  // Return 200 with events list
    }
 // Approve event
    @PostMapping("/approve")
    public ResponseEntity<String> approveEvent(@RequestParam String title) {
        boolean success = eventService.approveEvent(title);
        if (success) {
            return ResponseEntity.ok("Event approved");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }
    }

    @DeleteMapping("/reject")
    public ResponseEntity<String> rejectEvent(@RequestParam String title) {
        boolean success = eventService.rejectEvent(title);
        if (success) {
            return ResponseEntity.ok("Event rejected");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }
    }

}
