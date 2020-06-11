package org.miro.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miro.api.WidgetDescription;
import org.miro.api.WidgetPresentation;
import org.miro.exception.WidgetNotFound;
import org.miro.service.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InvalidObjectException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@RunWith(SpringRunner.class)
public class WidgetControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private WidgetService widgetService;

    @Test
    public void create_withCorrectCoordinates_shouldReturnCreated() throws InvalidObjectException {
        //arrange
        var coordinates = new WidgetDescription(1, 1, 1, 1, 1);
        var expectedPresentation = new WidgetPresentation();
        when(widgetService.createWidget(coordinates)).thenReturn(expectedPresentation);

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);
        WidgetPresentation actualPresentation = response.getBody();

        //assert
        assertEquals(CREATED, response.getStatusCode());
        assertEquals(expectedPresentation, actualPresentation);
    }

    @Test
    public void create_withInCorrectDimensions_shouldReturnBadRequest() throws InvalidObjectException {
        //arrange
        var coordinates = new WidgetDescription(1, 1, 1, 0, 1);
        when(widgetService.createWidget(coordinates)).thenThrow(InvalidObjectException.class);

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);

        //assert
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void create_withInCorrectCoordinates_shouldReturnBadRequest() throws InvalidObjectException {
        //arrange
        var coordinates = new WidgetDescription(1, 1, 1, 1, 1);
        when(widgetService.createWidget(coordinates)).thenThrow(InvalidObjectException.class);

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.postForEntity(getEndpointPath(), coordinates, WidgetPresentation.class);

        //assert
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void update_withExistingWidget_shouldReturnOk() throws WidgetNotFound {
        //arrange
        var coordinates = new WidgetDescription(1, 1, 1, 1, 1);
        var expectedPresentation = new WidgetPresentation();
        var coordinatesEntity = new HttpEntity<>(coordinates);
        var id = "someid";
        when(widgetService.updateWidget(id, coordinates)).thenReturn(expectedPresentation);

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.PUT, coordinatesEntity, WidgetPresentation.class);

        //assert
        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedPresentation, response.getBody());
    }

    @Test
    public void update_withNotExistingWidget_shouldReturnNotFound() throws WidgetNotFound {
        //arrange
        var coordinates = new WidgetDescription(1, 1, 1, 1, 1);
        var coordinatesEntity = new HttpEntity<>(coordinates);
        var id = "someid";
        when(widgetService.updateWidget(id, coordinates)).thenThrow(WidgetNotFound.class);

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.PUT, coordinatesEntity, WidgetPresentation.class);

        //assert
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void delete_withExistingWidget_shouldReturnOk() {
        //arrange
        var id = "someid";

        //act
        ResponseEntity<Void> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.DELETE, null, Void.class);

        //assert
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void delete_withNotExistingWidget_shouldReturnNotFound() throws WidgetNotFound {
        //arrange
        var id = "someid";
        doThrow(WidgetNotFound.class).when(widgetService).deleteWidget(id);

        //act
        ResponseEntity<Void> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.DELETE, null, Void.class);

        //assert
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void findById_withExistingWidget_shouldReturnOk() throws WidgetNotFound {
        //arrange
        var id = "someid";
        WidgetPresentation expectedPresentation = new WidgetPresentation();
        when(widgetService.findWidgetById(id)).thenReturn(expectedPresentation);

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.GET, null, WidgetPresentation.class);

        //assert
        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedPresentation, response.getBody());

    }

    @Test
    public void findById_withNotExistingWidget_shouldReturnNotFound() throws WidgetNotFound {
        //arrange
        var id = "someid";
        when(widgetService.findWidgetById(id)).thenThrow(WidgetNotFound.class);

        //act
        ResponseEntity<WidgetPresentation> response = restTemplate.exchange(getEndpointPath() + "/" + id, HttpMethod.GET, null, WidgetPresentation.class);

        //assert
        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAll_shouldReturnOk() {
        //arrange
        List<WidgetPresentation> list = Collections.singletonList(new WidgetPresentation());
        when(widgetService.findAllWidgets(anyInt(), anyInt())).thenReturn(list);

        //act
        ResponseEntity<List> response = restTemplate.exchange(getEndpointPath() + "/all", HttpMethod.GET, null, List.class);

        //assert
        assertEquals(OK, response.getStatusCode());
        assertEquals(list.size(), response.getBody().size());
    }

    @Test
    public void getAll_withoutParams_shouldSetDefaults() {
        //act
        restTemplate.exchange(getEndpointPath() + "/all", HttpMethod.GET, null, List.class);

        //assert
        verify(widgetService).findAllWidgets(1, 10);
    }

    private String getEndpointPath() {
        RequestMapping requestMapping = WidgetController.class.getAnnotation(RequestMapping.class);
        return requestMapping.value()[0];
    }
}