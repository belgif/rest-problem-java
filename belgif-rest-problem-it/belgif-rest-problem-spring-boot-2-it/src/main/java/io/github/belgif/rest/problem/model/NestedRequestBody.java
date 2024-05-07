package io.github.belgif.rest.problem.model;

import javax.validation.Valid;

public class NestedRequestBody {
    @Valid
    private ParentRequestBody myRequestBody;

    public NestedRequestBody(ParentRequestBody myRequestBody) {
        this.myRequestBody = myRequestBody;
    }

    public NestedRequestBody() {
    }

    public ParentRequestBody getMyRequestBody() {
        return myRequestBody;
    }

    public void setMyRequestBody(ParentRequestBody myRequestBody) {
        this.myRequestBody = myRequestBody;
    }
}
