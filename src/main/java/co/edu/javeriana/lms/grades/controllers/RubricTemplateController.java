package co.edu.javeriana.lms.grades.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/rubric/template")
public class RubricTemplateController {

    //list the rubrics

    //get the rubric template by id

    //create the rubric template
    

    //update the rubric template
    //this depeends on several factors, like if the rubric template is being used by a rubric, if it is being used by a course, etc.
    //if the rubric template is being used by a rubric, then the rubric template cannot be updated, we will need to create a new rubric template
    //if it is no been used by a rubric, then we can update the rubric template 


    //delete the rubric template
    /*this depends
        If it is been used by a rubric, then we cannot delete the rubric template
        If it is not been used by a rubric, then we can delete the rubric template
    */
}
