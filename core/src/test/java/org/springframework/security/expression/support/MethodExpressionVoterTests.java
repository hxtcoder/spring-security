package org.springframework.security.expression.support;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.annotation.ExpressionProtectedBusinessServiceImpl;
import org.springframework.security.providers.TestingAuthenticationToken;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.security.vote.AccessDecisionVoter;

public class MethodExpressionVoterTests {
    private TestingAuthenticationToken joe = new TestingAuthenticationToken("joe", "joespass", "blah");
    private MethodInvocation miStringArgs;
    private MethodInvocation miListArg;
    private MethodInvocation miArrayArg;
    private List listArg;
    private Object[] arrayArg;
    private MethodExpressionVoter am = new MethodExpressionVoter();

    @Before
    public void setUp() throws Exception {
        Method m = ExpressionProtectedBusinessServiceImpl.class.getMethod("methodReturningAList",
                String.class, String.class);
        miStringArgs = new SimpleMethodInvocation(new Object(), m, new String[] {"joe", "arg2Value"});
        m = ExpressionProtectedBusinessServiceImpl.class.getMethod("methodReturningAList", List.class);
        listArg = new ArrayList(Arrays.asList("joe", "bob", "sam"));
        miListArg = new SimpleMethodInvocation(new Object(), m, new Object[] {listArg});
        m = ExpressionProtectedBusinessServiceImpl.class.getMethod("methodReturningAnArray", Object[].class);
        arrayArg = new Object[] {"joe", "bob", "sam"};
        miArrayArg = new SimpleMethodInvocation(new Object(), m, new Object[] {arrayArg});
    }

    @Test
    public void hasRoleExpressionAllowsUserWithRole() throws Exception {
        ConfigAttributeDefinition cad = new ConfigAttributeDefinition(new PreInvocationExpressionConfigAttribute(null, null, "hasRole('blah')"));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, am.vote(joe, miStringArgs, cad));
    }

    @Test
    public void hasRoleExpressionDeniesUserWithoutRole() throws Exception {
        ConfigAttributeDefinition cad = new ConfigAttributeDefinition(new PreInvocationExpressionConfigAttribute(null, null, "hasRole('joedoesnt')"));
        assertEquals(AccessDecisionVoter.ACCESS_DENIED, am.vote(joe, miStringArgs, cad));
    }

    @Test
    public void matchingArgAgainstAuthenticationNameIsSuccessful() throws Exception {
        // userName is an argument name of this method
        ConfigAttributeDefinition cad = new ConfigAttributeDefinition(new PreInvocationExpressionConfigAttribute(null, null, "(#userName == principal) and (principal == 'joe')"));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, am.vote(joe, miStringArgs, cad));
    }

    @Test
    public void accessIsGrantedIfNoPreAuthorizeAttributeIsUsed() throws Exception {
        ConfigAttributeDefinition cad = new ConfigAttributeDefinition(new PreInvocationExpressionConfigAttribute("(filterObject == 'jim')", "someList", null));
        assertEquals(AccessDecisionVoter.ACCESS_GRANTED, am.vote(joe, miListArg, cad));
        // All objects should have been removed, because the expression is always false
        assertEquals(0, listArg.size());
    }

    @Test(expected=IllegalArgumentException.class)
    public void arraysCannotBePrefiltered() throws Exception {
        ConfigAttributeDefinition cad = new ConfigAttributeDefinition(new PreInvocationExpressionConfigAttribute("(filterObject == 'jim')", "someArray", null));
        am.vote(joe, miArrayArg, cad);
    }

    @Test
    public void listPreFilteringIsSuccessful() throws Exception {
        ConfigAttributeDefinition cad = new ConfigAttributeDefinition(new PreInvocationExpressionConfigAttribute("(filterObject == 'joe' or filterObject == 'sam')", "someList", null));
        am.vote(joe, miListArg, cad);
        assertEquals("joe and sam should still be in the list", 2, listArg.size());
        assertEquals("joe", listArg.get(0));
        assertEquals("sam", listArg.get(1));
    }
}
