package dk.ledocsystem.controller;

import dk.ledocsystem.dao.EmployeeDaoImpl;
import dk.ledocsystem.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    @Autowired
    private EmployeeDaoImpl employeeDao;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Employee test(@RequestParam(value = "id") String id) {
        return employeeDao.getEmployeeById(Long.valueOf(id));
    }
}
