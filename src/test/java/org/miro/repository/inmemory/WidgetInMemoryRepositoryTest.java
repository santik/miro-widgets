package org.miro.repository.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.miro.model.Widget;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@Slf4j
class WidgetInMemoryRepositoryTest {

    @Test
    void findAll_withWidget_shouldReturnWidgetList() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        Widget widget = Widget.builder().id("id").x(1).y(1).z(1).build();
        repository.createOrUpdate(widget);

        //act
        List<Widget> widgets = repository.findAll(1, 100);

        //assert
        assertSame(widget, widgets.get(0));
    }

    @Test
    void findAll_withMultipleWidgets_shouldReturnWidgetListOrderedByZ() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        int endExclusive = 100;
        IntStream.range(0, endExclusive).forEach(i -> {
            var widget = Widget.builder().id("id" + i).x(1).y(1).z(1).build();
            repository.createOrUpdate(widget);
        });

        //act
        List<Widget> widgets = repository.findAll(1, 100);

        //assert
        assertEquals(endExclusive, widgets.size());
        IntStream.range(1, widgets.size()).forEach(i -> assertTrue(widgets.get(i).getZ() > widgets.get(i - 1).getZ()));
    }

    @Test
    void findAllPaging_shouldReturnPages() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        int endExclusive = 100;
        IntStream.range(0, endExclusive).forEach(i -> {
            var widget = Widget.builder().id("id" + i).x(1).y(1).z(1).build();
            repository.createOrUpdate(widget);
        });

        //act
        List<Widget> widgetsPage1 = repository.findAll(1, 50);
        List<Widget> widgetsPage2 = repository.findAll(2, 50);

        //assert
        assertEquals(50, widgetsPage1.size());
        assertEquals(50, widgetsPage2.size());
    }

    @Test
    void findAllPaging_withTooBigPageNumber_shouldReturnEmpty() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        int endExclusive = 100;
        IntStream.range(0, endExclusive).forEach(i -> {
            var widget = Widget.builder().id("id" + i).x(1).y(1).z(1).build();
            repository.createOrUpdate(widget);
        });

        //act
        List<Widget> widgets = repository.findAll(1000, 50);

        //assert
        assertTrue(widgets.isEmpty());
    }

    @Test
    void findAllPaging_withTooBigPerPage_shouldReturnCorrect() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        int endExclusive = 100;
        IntStream.range(0, endExclusive).forEach(i -> {
            var widget = Widget.builder().id("id" + i).x(1).y(1).z(1).build();
            repository.createOrUpdate(widget);
        });

        //act
        List<Widget> widgets = repository.findAll(1, 50000);

        //assert
        assertEquals(endExclusive, widgets.size());
    }

    @Test
    void findById_withExistingWidget_shouldReturnWidget() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        String id = "id";
        Widget widget = Widget.builder().id(id).x(1).y(1).z(1).build();
        repository.createOrUpdate(widget);

        //act
        var actual = repository.findById(id);

        //assert
        assertSame(widget, actual.get());
    }

    @Test
    void findById_withNotExistingWidget_shouldReturnEmpty() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        String id = "id";

        //act
        var actual = repository.findById(id);

        //assert
        assertTrue(actual.isEmpty());
    }


    @Test
    void deleteById_withExistingWidget_shouldDelete() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        String id = "id";
        Widget widget = Widget.builder().id(id).x(1).y(1).z(1).build();
        repository.createOrUpdate(widget);

        //act
        repository.deleteById(id);

        //assert
        assertTrue(repository.findById(id).isEmpty());
    }

    @Test
    void createOrUpdate_withSingleWidget_shouldSaveIt() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        Widget widget = Widget.builder().id("id").x(1).y(1).z(1).build();

        //act
        Widget actual = repository.createOrUpdate(widget);

        //assert
        List<Widget> widgets = repository.findAll(1, 100);
        assertSame(widget, widgets.get(0));
        assertSame(widget, actual);
    }

    @Test
    void createOrUpdate_withNoZWidgetNoWidgets_shouldSaveItTop() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        int topZ = 1;
        Widget widget = Widget.builder().id("anotherid").x(1).y(1).build();

        //act
        Widget actual = repository.createOrUpdate(widget);

        //assert
        assertEquals(0, (int) actual.getZ());
    }

    @Test
    void createOrUpdate_withNoZWidget_shouldSaveItTop() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        int topZ = 1;
        Widget widget = Widget.builder().id("id").x(1).y(1).z(topZ).build();
        repository.createOrUpdate(widget);
        widget = Widget.builder().id("anotherid").x(1).y(1).build();

        //act
        Widget actual = repository.createOrUpdate(widget);

        //assert
        List<Widget> widgets = repository.findAll(1, 100);
        assertEquals(2, widgets.size());
        assertTrue(actual.getZ() > topZ);
    }

    @Test
    void createOrUpdate_withExistingZWidget_shouldShift() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        int existing = 1;
        String firstId = "id";
        Widget widget = Widget.builder().id(firstId).x(1).y(1).z(existing).build();
        repository.createOrUpdate(widget);
        widget = Widget.builder().id("anotherid").x(1).y(1).z(existing).build();

        //act
        Widget actual = repository.createOrUpdate(widget);

        //assert
        List<Widget> widgets = repository.findAll(1, 100);
        assertEquals(2, widgets.size());
        assertEquals(actual.getZ().intValue(), existing);
        assertTrue(repository.findById(firstId).get().getZ() > existing);
    }

    @Test
    void createOrUpdate_withNoIdWidget_shouldGenerateId() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        Widget widget = Widget.builder().x(1).y(1).z(1).build();

        //act
        Widget actual = repository.createOrUpdate(widget);

        //assert
        assertNotNull(widget.getId());
    }

    @Test
    void createOrUpdate_withWidget_shouldUpdateDate() {
        //arrange
        var repository = new WidgetInMemoryRepository();
        LocalDateTime now = LocalDateTime.now();
        Widget widget = Widget.builder().id("id").x(1).y(1).z(1).lastModifiedDate(now).build();

        //act
        Widget actual = repository.createOrUpdate(widget);

        //assert
        assertNotEquals(widget.getLastModifiedDate(), now);
    }

    @Test
    void createOrUpdateConcurrentCalls() throws Exception {
        var repository = new WidgetInMemoryRepository();

        var numberOfFutures = 10;
        var perFuture = 10;

        var futures = new CompletableFuture[numberOfFutures];
        IntStream.range(0, numberOfFutures).forEach(i -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                    IntStream.range(0, perFuture).forEach(j -> {
                    var widget = Widget.builder().id(UUID.randomUUID().toString()).x(1).y(1).z(1).build();
                    var actual = repository.createOrUpdate(widget);
                    assertEquals(actual.getZ(), widget.getZ());
            }));
            futures[i] = future;
        });

        var combinedFuture = CompletableFuture.allOf(futures);
        combinedFuture.get();

        assertEquals(numberOfFutures * perFuture, repository.findAll(1, numberOfFutures * perFuture).size());
    }
}