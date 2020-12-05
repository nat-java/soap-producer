package com.example.producingsoapservice;

import com.example.producing_soap_service.GetWeatherRequest;
import com.example.producing_soap_service.GetWeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Endpoint
public class WeatherEndpoint {
    private Logger logger = LoggerFactory.getLogger(this.toString());
	private static final String NAMESPACE_URI = "http://example.com/producing-soap-service";

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetWeatherRequest")
	@ResponsePayload
	public GetWeatherResponse getWeather(@RequestPayload GetWeatherRequest request) {
        GetWeatherResponse response = new GetWeatherResponse();
		response.setCity(request.getCity());
		response.setTemperature(BigDecimal.valueOf(10 * Math.random()).setScale(2, RoundingMode.HALF_DOWN).floatValue());
		logger.debug("Отправляем данные погоды {} = {}", response.getCity(), response.getTemperature());
		return response;
	}
}
