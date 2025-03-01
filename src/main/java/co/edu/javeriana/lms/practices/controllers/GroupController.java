package co.edu.javeriana.lms.practices.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.lms.practices.services.GroupPerSimulationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/group")
public class GroupController {

    @Autowired
    private GroupPerSimulationService groupService;

    @PostMapping("/add")
    public void createGroup() {
        log.info("Creating a new group");
        groupService.save();
    }
    
}
