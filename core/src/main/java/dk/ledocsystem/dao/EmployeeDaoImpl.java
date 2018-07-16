package dk.ledocsystem.dao;

import dk.ledocsystem.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "employeeDao")
public class EmployeeDaoImpl {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Transactional(transactionManager = "transactionManager", propagation = Propagation.REQUIRED)
    public Employee getEmployeeById(Long id) {
        return hibernateTemplate.get(Employee.class, id);
    }
}
