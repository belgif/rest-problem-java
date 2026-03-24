package io.github.belgif.rest.problem.spring.server.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.ApiPath;
import com.atlassian.oai.validator.model.ApiPathImpl;
import com.atlassian.oai.validator.report.ValidationReport;

import io.github.belgif.rest.problem.api.InEnum;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;

class InvalidRequestExceptionUtilTest {

    @Test
    void getInPath() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("path").name("myParam")).build());
        ValidationReport.Message message = builder.build();
        assertThat(InvalidRequestExceptionUtil.getIn(message)).isEqualTo(InEnum.PATH);
    }

    @Test
    void getInHeader() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("header").name("myParam")).build());
        ValidationReport.Message message = builder.build();
        assertThat(InvalidRequestExceptionUtil.getIn(message)).isEqualTo(InEnum.HEADER);
    }

    @Test
    void getInQuery() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("query").name("myParam")).build());
        ValidationReport.Message message = builder.build();
        assertThat(InvalidRequestExceptionUtil.getIn(message)).isEqualTo(InEnum.QUERY);
    }

    @Test
    void getInBody() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        ValidationReport.Message message = builder.build();
        assertThat(InvalidRequestExceptionUtil.getIn(message)).isEqualTo(InEnum.BODY);
    }

    @Test
    void getNameParameter() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withParameter(new Parameter().in("query").name("myParam")).build());
        ValidationReport.Message message = builder.build();
        assertThat(InvalidRequestExceptionUtil.getName(message)).isEqualTo("myParam");
    }

    @Test
    void getNameBody() {
        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withPointers("/myProperty", "yes")
                .build());
        ValidationReport.Message message = builder.build();
        assertThat(InvalidRequestExceptionUtil.getName(message)).isEqualTo("/myProperty");
    }

    @Test
    void getPathValue() {
        ApiPath path = new ApiPathImpl("/{myFirstParam}/{mySecondParam}/pathSegment/{myThirdParam}/collection",
                "https://myPrefix.io");

        ValidationReport.Message.Builder builder =
                ValidationReport.Message.create("something.crazy.going.on", "My Test Message");
        builder.withContext(ValidationReport.MessageContext.create()
                .withApiOperation(new ApiOperation(path, path, PathItem.HttpMethod.GET, new Operation()))
                .withRequestPath("/firstValue/secondValue/pathSegment/thirdValue/collection").build());
        ValidationReport.Message message = builder.build();
        assertThat(InvalidRequestExceptionUtil.getPathValue(message, "myFirstParam")).isEqualTo("firstValue");
        assertThat(InvalidRequestExceptionUtil.getPathValue(message, "mySecondParam")).isEqualTo("secondValue");
        assertThat(InvalidRequestExceptionUtil.getPathValue(message, "myThirdParam")).isEqualTo("thirdValue");
    }

    @Test
    void getDetailForNormalMessages() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.firstError", "first issue").build());
        messages.add(ValidationReport.Message.create("validation.schema.secondError", "second issue").build());

        ValidationReport.Message message = ValidationReport.Message.create("my.validation.issue", "test message")
                .build().withNestedMessages(messages);

        assertThat(InvalidRequestExceptionUtil.getDetail(message)).isEqualTo("test message");
    }

    @Test
    void getDetailForOneOfMessages() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.firstError", "first issue").build());
        messages.add(ValidationReport.Message.create("validation.schema.secondError", "second issue").build());

        ValidationReport.Message message =
                ValidationReport.Message.create("validation.request.body.schema.oneOf", "test message")
                        .build().withNestedMessages(messages);

        String detail = InvalidRequestExceptionUtil.getDetail(message);

        assertThat(detail.contains("test message")).isTrue();
        assertThat(detail.contains("first issue")).isTrue();
        assertThat(detail.contains("second issue")).isTrue();
    }

    @Test
    void getDetailForAnyOfMessages() {
        List<ValidationReport.Message> messages = new ArrayList<>();
        messages.add(ValidationReport.Message.create("validation.schema.firstError", "first issue").build());
        messages.add(ValidationReport.Message.create("validation.schema.secondError", "second issue").build());

        ValidationReport.Message message =
                ValidationReport.Message.create("validation.request.body.schema.anyOf", "test message")
                        .build().withNestedMessages(messages);

        String detail = InvalidRequestExceptionUtil.getDetail(message);

        assertThat(detail.contains("test message")).isTrue();
        assertThat(detail.contains("first issue")).isTrue();
        assertThat(detail.contains("second issue")).isTrue();
    }

}
