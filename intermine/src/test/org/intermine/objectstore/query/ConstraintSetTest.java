package org.flymine.objectstore.query;

import junit.framework.TestCase;


public class ConstraintSetTest extends TestCase {

    private ConstraintSet set;
    private SimpleConstraint sc1;
    private SimpleConstraint sc2;


    public ConstraintSetTest(String arg1) {
        super(arg1);
    }

    public void setUp() throws Exception {
        sc1 = new SimpleConstraint(new QueryValue("test"), SimpleConstraint.EQUALS, new QueryValue("test"));
        Integer testInt1 = new Integer(5);
        Integer testInt2 = new Integer(7);
        sc2 = new SimpleConstraint(new QueryValue(testInt1), SimpleConstraint.LESS_THAN, new QueryValue(testInt2));
    }


    public void testRemoveNotExists() throws Exception {
        try {
            set = new ConstraintSet(false);
            set.addConstraint(sc1);
            set.removeConstraint(sc2);
            fail("Expected IllegalArgumentExcepion");
        } catch (IllegalArgumentException e) {
        }
    }

}
