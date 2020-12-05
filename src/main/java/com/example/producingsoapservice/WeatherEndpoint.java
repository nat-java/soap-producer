package com.example.producingsoapservice;

import com.example.producing_soap_service.GetWeatherRequest;
import com.example.producing_soap_service.GetWeatherResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Endpoint
public class WeatherEndpoint {
    private Logger logger = LoggerFactory.getLogger(this.toString());
	private static final String NAMESPACE_URI = "http://example.com/producing-soap-service";
	private final String REST_URL = "https://api.openweathermap.org/data/2.5/weather?q=%city%&appid=f63245171f3ebf5f0137c7eb28cdd6da&units=metric";

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetWeatherRequest")
	@ResponsePayload
	public GetWeatherResponse getWeather(@RequestPayload GetWeatherRequest request) {
        OpenWeatherResponse responseRest = (OpenWeatherResponse) sendGetRequest(
                REST_URL.replaceAll("%city%", request.getCity()), OpenWeatherResponse.class);

        GetWeatherResponse response = new GetWeatherResponse();
		response.setCity(request.getCity());
		response.setTemperature(responseRest.getTemp().setScale(2, RoundingMode.HALF_DOWN).floatValue());
		logger.debug("Отправляем данные погоды {} = {}", response.getCity(), response.getTemperature());
		return response;
	}

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public Object sendGetRequest(String url, Class responseClass) {
        ResponseEntity responseEntity = sendRequest(url, null, responseClass, HttpMethod.GET);
        logger.info("Получили ответ {}", responseEntity.getStatusCode());
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }
        return null;
    }

    public ResponseEntity sendRequest(String url, String body, Class responseClass, HttpMethod httpMethod) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        HttpEntity request = new HttpEntity(body);

        try {
            ResponseEntity response = restTemplate.exchange(url, httpMethod, request, responseClass);
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Ошибка при выполнении запроса {} {}: {}", httpMethod, url, e.getMessage());
            return new ResponseEntity(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class OpenWeatherResponse implements Serializable {
        private OpenWeatherMain main;

        public OpenWeatherMain getMain() {
            return main;
        }

        public void setMain(OpenWeatherMain main) {
            this.main = main;
        }

        public BigDecimal getTemp() {
            return this.main.getTemp();
        }

        static class OpenWeatherMain {
            private BigDecimal temp;

            public BigDecimal getTemp() {
                return temp;
            }

            public void setTemp(BigDecimal temp) {
                this.temp = temp;
            }
        }
    }
}
