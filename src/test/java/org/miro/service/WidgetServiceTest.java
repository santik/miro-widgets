package org.miro.service;

import org.junit.jupiter.api.Test;
import org.miro.api.Area;
import org.miro.api.LowerLeft;
import org.miro.api.UpperRight;
import org.miro.api.WidgetDescription;
import org.miro.api.WidgetPresentation;
import org.miro.exception.WidgetNotFound;
import org.miro.model.Widget;
import org.miro.repository.WidgetRepository;
import org.mockito.ArgumentCaptor;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WidgetServiceTest {

    @Test
    void createWidget_withFullCoordinates_shouldSaveWidget() throws InvalidObjectException {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var coordinates = new WidgetDescription(1,1,1,1,1);
        var widget = new Widget();
        when(repository.createOrUpdate(any())).thenReturn(widget);
        var widgetPresentation = new WidgetPresentation();
        when(mapper.getWidgetPresentation(widget)).thenReturn(widgetPresentation);

        //act
        var actualWidgetPresentation = service.createWidget(coordinates);

        //assert
        assertEquals(widgetPresentation, actualWidgetPresentation);

    }

    @Test
    void createWidget_withNullX_shouldThrowException() {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var coordinates = new WidgetDescription(null,1,1,1,1);

        //act && assert
        assertThrows(InvalidObjectException.class, () -> service.createWidget(coordinates));
    }

    @Test
    void createWidget_withNullY_shouldThrowException() {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var coordinates = new WidgetDescription(1,null,1,1,1);

        //act && assert
        assertThrows(InvalidObjectException.class, () -> service.createWidget(coordinates));
    }


    @Test
    void updateWidget_withExistingWidget_shouldSaveIt() throws WidgetNotFound {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var id = "someid";
        var coordinates = new WidgetDescription(1,1,1,1,1);
        Widget widget = Widget.builder().id(id).x(2).y(2).z(2).build();
        when(repository.findById(id)).thenReturn(Optional.of(widget));
        ArgumentCaptor<Widget> widgetArgumentCaptor = ArgumentCaptor.forClass(Widget.class);
        when(repository.createOrUpdate(any())).thenReturn(widget);
        WidgetPresentation widgetPresentation = new WidgetPresentation();
        when(mapper.getWidgetPresentation(widget)).thenReturn(widgetPresentation);

        //act
        WidgetPresentation actualWidgetPresentation = service.updateWidget(id, coordinates);

        //assert
        assertEquals(widgetPresentation, actualWidgetPresentation);
        verify(repository).createOrUpdate(widgetArgumentCaptor.capture());
        Widget actual = widgetArgumentCaptor.getValue();
        assertEquals(coordinates.getXindex(), actual.getX());
        assertEquals(coordinates.getYindex(), actual.getY());
        assertEquals(coordinates.getZindex(), actual.getZ());
    }

    @Test
    void updateWidget_withNotExistingWidget_shouldThrowException() {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var id = "someid";
        var coordinates = new WidgetDescription(1,1,1,1,1);
        when(repository.findById(id)).thenReturn(Optional.empty());

        //act && assert
        assertThrows(WidgetNotFound.class, () -> service.updateWidget(id, coordinates));
    }

    @Test
    void deleteWidget_withExistingWidget_shouldDelete() throws WidgetNotFound {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var id = "someid";
        when(repository.findById(id)).thenReturn(Optional.of(new Widget()));

        //act
        service.deleteWidget(id);

        //assert
        verify(repository).deleteById(id);
    }

    @Test
    void deleteWidget_withInexistingWidget_shouldThrowException() {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var id = "someid";
        when(repository.findById(id)).thenReturn(Optional.empty());

        //act && assert
        assertThrows(WidgetNotFound.class, () -> service.deleteWidget(id));
    }

    @Test
    void findWidgetById_withExistingWidget_shouldReturnIt() throws WidgetNotFound {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var id = "someid";
        Widget expected = new Widget();
        when(repository.findById(id)).thenReturn(Optional.of(expected));
        ArgumentCaptor<Widget> widgetArgumentCaptor = ArgumentCaptor.forClass(Widget.class);

        //act
        service.findWidgetById(id);

        //assert
        verify(mapper).getWidgetPresentation(widgetArgumentCaptor.capture());
        assertEquals(expected, widgetArgumentCaptor.getValue());
    }

    @Test
    void findWidgetById_withInexistingWidget_shouldThrowException() {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        var id = "someid";
        when(repository.findById(id)).thenReturn(Optional.empty());

        //act && assert
        assertThrows(WidgetNotFound.class, () -> service.findWidgetById(id));
    }

    @Test
    void findAllWidgets_shouldReturnWidgetsFromRepo() {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        Widget widget = new Widget();
        List<Object> t = List.of(widget);
        when(repository.findAll(1, 10)).thenReturn(t);
        ArgumentCaptor<Widget> widgetArgumentCaptor = ArgumentCaptor.forClass(Widget.class);

        //act
        service.findAllWidgets(1, 10);

        //assert
        verify(mapper).getWidgetPresentation(widgetArgumentCaptor.capture());
        assertEquals(widget, widgetArgumentCaptor.getValue());
    }

    @Test
    void findAllWidgetsInArea_shouldFilterCorrectly() {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        Area area = new Area()
                .withLowerLeft(new LowerLeft().withX(-50).withY(-50))
                .withUpperRight(new UpperRight().withX(100).withY(150));
        List<Widget> list = new ArrayList<>();
        Widget widget1 = Widget.builder().x(50).y(50).height(100).width(100).build();
        list.add(widget1);
        Widget widget2 = Widget.builder().x(50).y(100).height(100).width(100).build();
        list.add(widget2);
        Widget widget3 = Widget.builder().x(100).y(100).height(100).width(100).build();
        list.add(widget3);
        Widget widget4 = Widget.builder().x(0).y(0).height(100).width(100).build();
        list.add(widget4);
        when(repository.findAll()).thenReturn(list);
        ArgumentCaptor<Widget> widgetArgumentCaptor = ArgumentCaptor.forClass(Widget.class);

        //act
        List<WidgetPresentation> allWidgetsInArea = service.findAllWidgetsInArea(area, 1, 500);

        //assert
        verify(mapper, times(3)).getWidgetPresentation(widgetArgumentCaptor.capture());
        assertEquals(3, allWidgetsInArea.size());
        List<Widget> allValues = widgetArgumentCaptor.getAllValues();
        assertSame(widget1, allValues.get(0));
        assertSame(widget2, allValues.get(1));
        assertSame(widget4, allValues.get(2));
    }

    @Test
    void findAllWidgetsInArea_shouldPaginateCorrectly() {
        //arrange
        var mapper = mock(WidgetMapper.class);
        var repository = mock(WidgetRepository.class);
        var service = new WidgetService(repository, mapper);
        Area area = new Area()
                .withLowerLeft(new LowerLeft().withX(-50).withY(-50))
                .withUpperRight(new UpperRight().withX(100).withY(150));
        List<Widget> list = new ArrayList<>();
        Widget widget1 = Widget.builder().x(50).y(50).height(100).width(100).build();
        list.add(widget1);
        Widget widget2 = Widget.builder().x(50).y(100).height(100).width(100).build();
        list.add(widget2);
        Widget widget3 = Widget.builder().x(100).y(100).height(100).width(100).build();
        list.add(widget3);
        Widget widget4 = Widget.builder().x(0).y(0).height(100).width(100).build();
        list.add(widget4);
        when(repository.findAll()).thenReturn(list);

        //act
        List<WidgetPresentation> allWidgetsInAreaPage1 = service.findAllWidgetsInArea(area, 1, 1);
        List<WidgetPresentation> allWidgetsInAreaPage2 = service.findAllWidgetsInArea(area, 2, 1);
        List<WidgetPresentation> allWidgetsInAreaPage3 = service.findAllWidgetsInArea(area, 3, 1);
        List<WidgetPresentation> allWidgetsInAreaPage4 = service.findAllWidgetsInArea(area, 4, 1);

        //assert
        assertFalse(allWidgetsInAreaPage1.isEmpty());
        assertFalse(allWidgetsInAreaPage2.isEmpty());
        assertFalse(allWidgetsInAreaPage3.isEmpty());
        assertTrue(allWidgetsInAreaPage4.isEmpty());
    }
}