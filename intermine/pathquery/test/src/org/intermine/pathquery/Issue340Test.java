package org.intermine.pathquery;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.intermine.metadata.Model;

/**
 * Regression test to help fix and maintain the issue reported
 * in https://github.com/intermine/intermine/issues/340
 */
public class Issue340Test {

    private PathQuery query;

    @Before
    public void setup() {
        Model model = Model.getInstanceByName("testmodel");
        query = new PathQuery(model);
        query.addViews(
            "Employee.name",
            "Employee.department.name",
            "Employee.address.address",
            "Employee.departmentThatRejectedMe.name",
            "Employee.employmentPeriod"
        );
        query.setOuterJoinStatus("Employee.department", OuterJoinStatus.OUTER);
        query.setOuterJoinStatus("Employee.address", OuterJoinStatus.OUTER);
        query.setOuterJoinStatus("Employee.departmentThatRejectedMe", OuterJoinStatus.OUTER);
        query.setOuterJoinStatus("Employee.employmentPeriod", OuterJoinStatus.OUTER);

    }

    @Test
    public void isValid() {
        assertTrue(query.isValid());
    }
}

