package io.github.belgif.rest.problem;

import jakarta.ejb.Stateless;

@Stateless
public class EJBService {

    public void throwPoblem() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("problem from EJB");
        throw problem;
    }

}
