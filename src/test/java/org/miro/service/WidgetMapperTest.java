package org.miro.service;

import org.junit.jupiter.api.Test;
import org.miro.api.WidgetPresentation;
import org.miro.model.Widget;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WidgetMapperTest {

    @Test
    void getWidgetPresentation_withWidget_shouldReturnCorrectPresentation() {
        //arrange
        var mapper = new WidgetMapper();
        var widget = Widget.builder().z(1).x(1).y(1).id("id").createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();

        //act
        WidgetPresentation widgetPresentation = mapper.getWidgetPresentation(widget);

        //assert
        assertEquals(widgetPresentation.getId(), widget.getId());
        assertEquals(widgetPresentation.getXindex(), widget.getX());
        assertEquals(widgetPresentation.getYindex(), widget.getY());
        assertEquals(widgetPresentation.getZindex(), widget.getZ());
        assertEquals(widgetPresentation.getLastModifiedDate(), widget.getLastModifiedDate().toString());
    }
}