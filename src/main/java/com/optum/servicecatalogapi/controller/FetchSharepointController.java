package com.optum.servicecatalogapi.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class FetchSharepointController {

	/*
	 * @Autowired RestTemplate restTemplate;
	 */

	@ModelAttribute
	public void setResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

	@RequestMapping("/")
	public String sayHello() {
		RestTemplate restTemplate = new RestTemplate();
		final String uri = "https://accounts.accesscontrol.windows.net/db05faca-c82a-4b9d-b9c5-0f64b6755421/tokens/OAuth/2";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "client_credentials");
		body.add("client_id", "e9729a08-bdd1-4541-910b-3d2abe4cf516@db05faca-c82a-4b9d-b9c5-0f64b6755421");
		body.add("client_secret", "hXtiSsZfTuMrtlbyUUdWeLxBNa0Xb0KmPNxOSreSw+s=");
		body.add("resource",
				"00000003-0000-0ff1-ce00-000000000000/uhgazure.sharepoint.com@db05faca-c82a-4b9d-b9c5-0f64b6755421");

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(body,
				headers);

		String response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class).getBody();
		response = response.replace("{", "").replace("}", "").replace("\"", "");

		String[] respArray = response.split(",");
		for (String acb : respArray) {
			if (acb.startsWith("access_token")) {
				response = acb.substring(13);
				break;
			}

		}
		return response;
	}

	@RequestMapping(value = "/fetchSPData", method = RequestMethod.POST)
	public static String fetchSharepointData() {
		String token = getToken();
		System.out.println(token);
		String response = "RESPONSE NOT FOUND";
		if (token != null) {
			response = getSharePointData(token);
		}
		return response;
	}

	private static String getSharePointData(String token) {
		RestTemplate restTemplate = new RestTemplate();
		final String uri = "https://uhgazure.sharepoint.com/sites/OptumCommercial/JMH/ServiceCatalog/_api/web/lists/getbytitle('ServiceCatalog_Dev')/items?$top=300";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Bearer " + token);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

		return response.getBody();

	}

	private static String getToken() {
		RestTemplate restTemplate = new RestTemplate();
		final String uri = "https://accounts.accesscontrol.windows.net/db05faca-c82a-4b9d-b9c5-0f64b6755421/tokens/OAuth/2";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "client_credentials");
		body.add("client_id", "e9729a08-bdd1-4541-910b-3d2abe4cf516@db05faca-c82a-4b9d-b9c5-0f64b6755421");
		body.add("client_secret", "hXtiSsZfTuMrtlbyUUdWeLxBNa0Xb0KmPNxOSreSw+s=");
		body.add("resource",
				"00000003-0000-0ff1-ce00-000000000000/uhgazure.sharepoint.com@db05faca-c82a-4b9d-b9c5-0f64b6755421");

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(body,
				headers);

		String response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class).getBody();
		response = response.replace("{", "").replace("}", "").replace("\"", "");

		String[] respArray = response.split(",");
		for (String acb : respArray) {
			if (acb.startsWith("access_token")) {
				response = acb.substring(13);
				break;
			}

		}
		return response;
	}

	public static void main(String[] args) {
		FetchSharepointController controller = new FetchSharepointController();
		controller.fetchSharepointData();
	}

}
