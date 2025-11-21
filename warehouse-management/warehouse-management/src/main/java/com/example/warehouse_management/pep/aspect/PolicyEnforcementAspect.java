package com.example.warehouse_management.pep.aspect;

import com.example.warehouse_management.pep.PolicyEnforcementPoint;
import com.example.warehouse_management.pep.annotation.EnforcePolicy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PolicyEnforcementAspect {

    @Autowired
    private PolicyEnforcementPoint pep;

    @Around("@annotation(enforcePolicy)")
    public Object checkPolicy(ProceedingJoinPoint joinPoint, EnforcePolicy enforcePolicy) throws Throwable {
        String resource = enforcePolicy.resource();
        String action = enforcePolicy.action();
        
        // Get the first argument as the resource object if it exists
        Object resourceObject = null;
        if (joinPoint.getArgs().length > 0) {
            resourceObject = joinPoint.getArgs()[0]; // Assuming first argument is the resource
        }

        boolean isAllowed = pep.checkPermission(resource, action, resourceObject);
        
        if (!isAllowed) {
            return ResponseEntity.status(403).body("Access denied by policy enforcement");
        }

        return joinPoint.proceed();
    }
}