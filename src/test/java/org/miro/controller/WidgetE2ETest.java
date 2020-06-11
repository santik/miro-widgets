package org.miro.controller;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miro.api.WidgetDescription;
import org.miro.api.WidgetPresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@RunWith(SpringRunner.class)
public class WidgetE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    private Faker faker = new Faker();

    @Test
    public void create_withCorrectCoordinates_shouldReturnCreated() {
        //arrange
        var coordinates = new WidgetDescription(
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100)
        );

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);
        WidgetPresentation actualPresentation = response.getBody();

        //assert
        assertEquals(CREATED, response.getStatusCode());
        assertEquals(coordinates.getXindex(), actualPresentation.getXindex());
        assertEquals(coordinates.getYindex(), actualPresentation.getYindex());
        assertEquals(coordinates.getZindex(), actualPresentation.getZindex());
        assertEquals(coordinates.getWidth(), actualPresentation.getWidth());
        assertEquals(coordinates.getHeight(), actualPresentation.getHeight());
    }

    @Test
    public void create_withInCorrectCoordinates_shouldReturnBadRequest() {
        //arrange
        var coordinates = new WidgetDescription(null,
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100)
        );

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);

        //assert
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void update_withExistingWidget_shouldReturnOk() {
        //arrange
        var coordinates = new WidgetDescription(
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100)
        );
        ResponseEntity<WidgetPresentation> response = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);

        var newCoordinates = new WidgetDescription(
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100)
        );
        var coordinatesEntity = new HttpEntity<>(newCoordinates);
        var id = response.getBody().getId();

        //act
        response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.PUT, coordinatesEntity, WidgetPresentation.class);

        //assert
        assertEquals(OK, response.getStatusCode());
        assertEquals(newCoordinates.getXindex(), response.getBody().getXindex());
        assertEquals(newCoordinates.getYindex(), response.getBody().getYindex());
        assertEquals(newCoordinates.getZindex(), response.getBody().getZindex());
        assertEquals(newCoordinates.getWidth(), response.getBody().getWidth());
        assertEquals(newCoordinates.getHeight(), response.getBody().getHeight());
    }

    @Test
    public void update_withNotExistingWidget_shouldReturnNotFound() {
        //arrange
        var coordinates = new WidgetDescription(1, 1, 1, 1, 1);
        var coordinatesEntity = new HttpEntity<>(coordinates);
        var id = UUID.randomUUID().toString();

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.PUT, coordinatesEntity, WidgetPresentation.class);

        //assert
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void delete_withExistingWidget_shouldReturnOk() {
        //arrange
        var coordinates = new WidgetDescription(
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100)
        );
        ResponseEntity<WidgetPresentation> response = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);
        var id = response.getBody().getId();

        //act
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.DELETE, null, Void.class);
        ResponseEntity<WidgetPresentation> getResponse = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.GET, null, WidgetPresentation.class);

        //assert
        assertEquals(OK, deleteResponse.getStatusCode());
        assertEquals(NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    public void delete_withNotExistingWidget_shouldReturnNotFound() {
        //arrange
        var id = UUID.randomUUID().toString();

        //act
        ResponseEntity<Void> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.DELETE, null, Void.class);

        //assert
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void findById_withExistingWidget_shouldReturnOk() {
        //arrange
        var coordinates = new WidgetDescription(
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100),
                faker.number().numberBetween(1, 100)
        );
        ResponseEntity<WidgetPresentation> response = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);
        var id = response.getBody().getId();

        //act
        ResponseEntity<WidgetPresentation> getResponse = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.GET, null, WidgetPresentation.class);

        //assert
        assertEquals(OK, getResponse.getStatusCode());
        assertEquals(coordinates.getXindex(), response.getBody().getXindex());
        assertEquals(coordinates.getYindex(), response.getBody().getYindex());
        assertEquals(coordinates.getZindex(), response.getBody().getZindex());
        assertEquals(coordinates.getWidth(), response.getBody().getWidth());
        assertEquals(coordinates.getHeight(), response.getBody().getHeight());
    }

    @Test
    public void findById_withNotExistingWidget_shouldReturnNotFound() {
        //arrange
        var id = UUID.randomUUID().toString();

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.GET, null, WidgetPresentation.class);

        //assert
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAll_shouldReturnOk() {
        //arrange
        ResponseEntity<WidgetPresentation[]> response = restTemplate.exchange(getEndpointPath() + "/all?page=1&perPage=200", HttpMethod.GET, null, WidgetPresentation[].class);
        Stream.of(response.getBody()).forEach(widget -> {
            restTemplate.delete(getEndpointPath() + "/" + widget.getId());
        });

        Map<String, WidgetDescription> coordinatesMap = new HashMap<>();
        int endExclusive = 100;
        IntStream.range(0, endExclusive).forEach(i -> {
            var coordinates = new WidgetDescription(
                    faker.number().numberBetween(1, 100),
                    faker.number().numberBetween(1, 100),
                    faker.number().numberBetween(1, 100),
                    faker.number().numberBetween(1, 100),
                    faker.number().numberBetween(1, 100)
            );
            ResponseEntity<WidgetPresentation> responseEntity = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);
            coordinatesMap.put(responseEntity.getBody().getId(), coordinates);
        });

        //act
        var perPage = endExclusive/2;
        ResponseEntity<WidgetPresentation[]> getAllFirstPage = restTemplate.exchange(getEndpointPath() + "/all?page=1&perPage="+perPage, HttpMethod.GET, null, WidgetPresentation[].class);
        ResponseEntity<WidgetPresentation[]> getAllSecondPage = restTemplate.exchange(getEndpointPath() + "/all?page=2&perPage="+perPage, HttpMethod.GET, null, WidgetPresentation[].class);

        //assert
        assertEquals(OK, response.getStatusCode());
        assertEquals(coordinatesMap.size(), getAllFirstPage.getBody().length + getAllSecondPage.getBody().length);

        Stream.of(getAllFirstPage.getBody()).forEach(item -> {
            verifyRetrievedItems(coordinatesMap, item);
        });

        Stream.of(getAllSecondPage.getBody()).forEach(item -> {
            verifyRetrievedItems(coordinatesMap, item);
        });

        assertTrue(coordinatesMap.isEmpty());
    }

    private void verifyRetrievedItems(Map<String, WidgetDescription> coordinatesMap, WidgetPresentation item) {
        WidgetDescription widgetDescription = coordinatesMap.get(item.getId());
        assertEquals(item.getXindex(), widgetDescription.getXindex());
        assertEquals(item.getYindex(), widgetDescription.getYindex());
        assertEquals(item.getWidth(), widgetDescription.getWidth());
        assertEquals(item.getHeight(), widgetDescription.getHeight());
        assertTrue(item.getZindex() >= widgetDescription.getZindex());
        coordinatesMap.remove(item.getId());
    }

    private String getEndpointPath() {
        RequestMapping requestMapping = WidgetController.class.getAnnotation(RequestMapping.class);
        return requestMapping.value()[0];
    }
}