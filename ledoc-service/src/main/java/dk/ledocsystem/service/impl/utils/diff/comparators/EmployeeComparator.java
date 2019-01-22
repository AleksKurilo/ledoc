package dk.ledocsystem.service.impl.utils.diff.comparators;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.impl.utils.diff.Diff;
import dk.ledocsystem.service.impl.utils.diff.EntityComparator;

public class EmployeeComparator implements EntityComparator<Employee> {

    public static final EmployeeComparator INSTANCE = new EmployeeComparator();

    @Override
    public Diff compare(Employee left, Employee right) {
        Diff diff = new Diff();

        compareSimple(left.getUsername(), right.getUsername(), "Username", diff);
        compareSimple(left.getIdNumber(), right.getIdNumber(), "ID number", diff);
        compareSimple(left.getFirstName(), right.getFirstName(), "First name", diff);
        compareSimple(left.getLastName(), right.getLastName(), "Last name", diff);
        compareSimple(left.getInitials(), right.getInitials(), "Initials", diff);
        compareSimple(left.getCellPhone(), right.getCellPhone(), "Cell phone", diff);
        compareSimple(left.getPhoneNumber(), right.getPhoneNumber(), "Phone number", diff);
        compareSimple(left.getExpireOfIdCard(), right.getExpireOfIdCard(), "Expire of ID card", diff);
        compareSimple(left.getResponsible(), right.getResponsible(), "Responsible", diff);
        compareSimple(left.getPlaceOfEmployment(), right.getPlaceOfEmployment(), "Place of employment", diff);
        compareSimple(left.getRole(), right.getRole(), "Role", diff);

        compareSimple(left.getDetails().getComment(), right.getDetails().getComment(), "Comment", diff);
        compareSimple(left.getDetails().getSkillAssessed(), right.getDetails().getSkillAssessed(), "Skill assessed", diff);
        compareSimple(left.getDetails().getReviewFrequency(), right.getDetails().getReviewFrequency(), "Review frequency", diff);
        compareSimple(left.getDetails().getResponsibleOfSkills(), right.getDetails().getResponsibleOfSkills(), "Skill responsible", diff);
        compareSimple(left.getDetails().getReviewTemplate(), right.getDetails().getReviewTemplate(), "Review template", diff);

        compareSimple(left.getPersonalInfo().getAddress(), right.getPersonalInfo().getAddress(), "Address", diff);
        compareSimple(left.getPersonalInfo().getBuildingNo(), right.getPersonalInfo().getBuildingNo(), "Building number", diff);
        compareSimple(left.getPersonalInfo().getPostalCode(), right.getPersonalInfo().getPostalCode(), "Postal code", diff);
        compareSimple(left.getPersonalInfo().getCity(), right.getPersonalInfo().getCity(), "City", diff);
        compareSimple(left.getPersonalInfo().getPersonalPhone(), right.getPersonalInfo().getPersonalPhone(), "Personal phone", diff);
        compareSimple(left.getPersonalInfo().getPersonalMobile(), right.getPersonalInfo().getPersonalMobile(), "Personal mobile", diff);
        compareSimple(left.getPersonalInfo().getDateOfBirth(), right.getPersonalInfo().getDateOfBirth(), "Birth date", diff);
        compareSimple(left.getPersonalInfo().getPrivateEmail(), right.getPersonalInfo().getPrivateEmail(), "Private email", diff);
        compareSimple(left.getPersonalInfo().getDayOfEmployment(), right.getPersonalInfo().getDayOfEmployment(), "Employment date", diff);
        compareSimple(left.getPersonalInfo().getComment(), right.getPersonalInfo().getComment(), "Personal comment", diff);

        compareSimple(left.getNearestRelative().getFirstName(), right.getNearestRelative().getFirstName(), "Nearest relative first name", diff);
        compareSimple(left.getNearestRelative().getLastName(), right.getNearestRelative().getLastName(), "Nearest relative last name", diff);
        compareSimple(left.getNearestRelative().getComment(), right.getNearestRelative().getComment(), "Nearest relative comment", diff);
        compareSimple(left.getNearestRelative().getEmail(), right.getNearestRelative().getEmail(), "Nearest relative email", diff);
        compareSimple(left.getNearestRelative().getPhoneNumber(), right.getNearestRelative().getPhoneNumber(), "Nearest relative phone number", diff);

        compareSets(left.getLocations(), right.getLocations(), "Locations", diff);

        return diff;
    }
}
