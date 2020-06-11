package org.miro.repository.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miro.model.Widget;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WidgetJpaRepositoryTest {

    private WidgetCrudRepository crudRepo;
    private WidgetJpaRepository repository;

    @BeforeEach
    public void setUp() {
        crudRepo = mock(WidgetCrudRepository.class);
        repository = new WidgetJpaRepository(crudRepo);
    }
    
    
    @Test
    void findAll_shouldReturnWidgetsFromCrudRepo() {
        //arrange
        List<Widget> expectedList = Collections.emptyList();
        when(crudRepo.findAllByOrderByZ()).thenReturn(expectedList);

        //act
        var widgets = repository.findAll();

        //assert
        assertSame(expectedList, widgets);
    }

    @Test
    void findById_shouldReturnWidgetFromCrudRepo() {
        //arrange
        var id = UUID.randomUUID().toString();
        var  expectedWidget = Optional.of(new Widget());
        when(crudRepo.findById(id)).thenReturn(expectedWidget);

        //act
        var  widget = repository.findById(id);

        //assert
        assertSame(expectedWidget, widget);
    }


    @Test
    void deleteById_shouldCallCrudRepo() {
        //arrange
        var id = UUID.randomUUID().toString();

        //act
        repository.deleteById(id);

        //assert
        verify(crudRepo).deleteById(id);
    }

    @Test
    void createOrUpdate_withNoZEmptySet_shouldSetZero() {
        //arrange
        var widget = Widget.builder().build();
        when(crudRepo.findTopByOrderByZ()).thenReturn(Optional.empty());
        when(crudRepo.save(widget)).thenReturn(widget);

        //act
        var actual = repository.createOrUpdate(widget);

        //assert
        assertEquals(0, actual.getZ());
    }

    @Test
    void createOrUpdate_withNoZ_shouldSetTopZ() {
        //arrange
        var widget = Widget.builder().build();
        var topZ = 123;
        var  topWidget = Optional.of(Widget.builder().z(topZ).build());
        when(crudRepo.findTopByOrderByZ()).thenReturn(topWidget);
        when(crudRepo.save(widget)).thenReturn(widget);

        //act
        var actual = repository.createOrUpdate(widget);

        //assert
        assertEquals(topZ + 1, actual.getZ());
    }

    @Test
    void createOrUpdate_withWithZ_shouldShiftTop() {
        //arrange
        int targetZ = 123;
        var widget = Widget.builder().z(targetZ).build();
        var topZ = 321;
        var topWidget = Widget.builder().z(topZ).build();
        var list = List.of(topWidget);
        when(crudRepo.findAllByZGreaterThanEqualOrderByZDesc(targetZ)).thenReturn(list);
        when(crudRepo.save(widget)).thenReturn(widget);

        //act
        var actual = repository.createOrUpdate(widget);

        //assert
        assertEquals(targetZ, actual.getZ());
        assertEquals(topZ + 1, topWidget.getZ());
        verify(crudRepo).save(topWidget);
        verify(crudRepo).save(widget);
        assertSame(widget, actual);
    }
}