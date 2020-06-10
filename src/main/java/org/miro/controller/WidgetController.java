package org.miro.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.miro.api.WidgetCoordinates;
import org.miro.api.WidgetPresentation;
import org.miro.exception.WidgetNotFound;
import org.miro.service.WidgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.InvalidObjectException;
import java.util.List;

@RequestMapping("/widget")
@RestController
@RequiredArgsConstructor
@Slf4j
public class WidgetController {

    private final WidgetService widgetService;

    @PostMapping
    @ApiOperation("Creates new widget")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Success", response = WidgetPresentation.class)})
    public ResponseEntity<WidgetPresentation> create(@RequestBody WidgetCoordinates coordinates) {
        try {
            var widget = widgetService.createWidget(coordinates);
            return new ResponseEntity<>(widget, HttpStatus.CREATED);
        } catch (InvalidObjectException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Updates widget by id")
    @ResponseBody
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = WidgetPresentation.class)})
    public ResponseEntity<WidgetPresentation> update(@PathVariable("id") String id, @RequestBody WidgetCoordinates coordinates) {
        WidgetPresentation updatedWidget = null;
        try {
            updatedWidget = widgetService.updateWidget(id, coordinates);
        } catch (WidgetNotFound widgetNotFound) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedWidget);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Deletes widget by id")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        try {
            widgetService.deleteWidget(id);
        } catch (WidgetNotFound e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiOperation("Gets widget by id")
    public ResponseEntity<WidgetPresentation> findById(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(widgetService.findWidgetById(id));
        } catch (WidgetNotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public List<WidgetPresentation> getAll() {
        return widgetService.findAllWidgets();
    }

}
