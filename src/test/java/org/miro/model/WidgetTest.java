package org.miro.model;

import org.junit.jupiter.api.Test;
import org.miro.api.WidgetDescription;

import java.io.InvalidObjectException;

import static org.junit.jupiter.api.Assertions.*;

class WidgetTest {

    @Test
    void from_withFullCoordinates_shouldReturnWidget() throws InvalidObjectException {
        //arrange
        var coordinates = new WidgetDescription(1,1,1,1,1);

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
        var coordinates = new WidgetDescription(null,1,1,1,1);
        var coordinates1 = new WidgetDescription(1,null,1,1,1);
        var coordinates2 = new WidgetDescription(1,1,1,0,1);
        var coordinates3 = new WidgetDescription(1,1,1,1,0);

        //act && assert
        assertThrows(InvalidObjectException.class, () -> Widget.from(coordinates));
        assertThrows(InvalidObjectException.class, () -> Widget.from(coordinates1));
        assertThrows(InvalidObjectException.class, () -> Widget.from(coordinates2));
        assertThrows(InvalidObjectException.class, () -> Widget.from(coordinates3));
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