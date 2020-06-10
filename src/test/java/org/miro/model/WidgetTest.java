package org.miro.model;

import org.junit.jupiter.api.Test;
import org.miro.api.WidgetCoordinates;

import java.io.InvalidObjectException;

import static org.junit.jupiter.api.Assertions.*;

class WidgetTest {

    @Test
    void from_withFullCoordinates_shouldReturnWidget() throws InvalidObjectException {
        //arrange
        var coordinates = new WidgetCoordinates(1,1,1);

        //act
        var widget = Widget.from(coordinates);

        //assert
        assertEquals(coordinates.getXindex(), widget.getX());
        assertEquals(coordinates.getYindex(), widget.getY());
        assertEquals(coordinates.getZindex(), widget.getZ());
    }

    @Test
    void from_withIncompleteCoordinates_shouldThrowException() {
        //arrange
        var coordinates = new WidgetCoordinates(null,1,1);
        var coordinates1 = new WidgetCoordinates(1,null,1);

        //act && assert
        assertThrows(InvalidObjectException.class, () -> Widget.from(coordinates));
        assertThrows(InvalidObjectException.class, () -> Widget.from(coordinates1));
    }

    @Test
    void testClone_withWidget_shouldReturnClonedWidget() {
        //arrange
        var widget = Widget.builder().id("id").x(1).y(1).z(1).build();

        //act
        var clonedWidget = widget.clone();

        //assert
        assertNotSame(widget, clonedWidget);
        assertEquals(widget, clonedWidget);
    }
}