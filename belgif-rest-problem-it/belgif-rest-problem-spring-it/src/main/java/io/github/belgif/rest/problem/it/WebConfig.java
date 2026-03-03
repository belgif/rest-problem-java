package io.github.belgif.rest.problem.it;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.belgif.rest.problem.spring.EnableProblemModule;
import io.github.belgif.rest.problem.spring.SpringProblemModuleJackson3;
import io.github.belgif.rest.problem.spring.client.ProblemResponseErrorHandler;
import io.github.belgif.rest.problem.spring.client.WebClientFilter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableProblemModule(beanValidation = true)
public class WebConfig {

    @Bean
    public JsonMapper objectMapper(SpringProblemModuleJackson3 springProblemModule) {
        return JsonMapper.builder()
                .disable(DeserializationFeature.WRAP_EXCEPTIONS)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .findAndAddModules()
                .changeDefaultPropertyInclusion((incl) -> incl.withContentInclusion(JsonInclude.Include.NON_NULL)
                        .withValueInclusion(JsonInclude.Include.NON_NULL))
                .addModule(springProblemModule) // add springProblem module to the mapper.
                .build();
    }

    @Bean
    public RestClient.Builder restClientBuilder(ProblemResponseErrorHandler problemResponseErrorHandler,
            JsonMapper mapper) {
        return RestClient.builder()
                .defaultStatusHandler(problemResponseErrorHandler)
                .configureMessageConverters(converter -> converter.registerDefaults()
                        .withJsonConverter(new JacksonJsonHttpMessageConverter(mapper))); // change converter with
                                                                                          // custom json mapper
    }

    @Bean
    public WebClient.Builder webClientBuilder(ProblemResponseErrorHandler problemResponseErrorHandler,
            JsonMapper mapper) {
        ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> {
            configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(mapper));
            configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(mapper));
        }).build();
        return WebClient.builder().exchangeStrategies(strategies).filter(WebClientFilter.PROBLEM_FILTER);
    }
}
