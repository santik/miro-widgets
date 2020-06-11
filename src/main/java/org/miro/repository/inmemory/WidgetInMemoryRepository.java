package org.miro.repository.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.miro.model.Widget;
import org.miro.repository.WidgetRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
@Slf4j
@ConditionalOnProperty(name = "app.storage.type", havingValue = "inmemory")
public class WidgetInMemoryRepository implements WidgetRepository<Widget, String> {

    private static final int Z_SHIFT_VALUE = 1;
    private final Map<String, Widget> mainStorage = new HashMap<>();
    //created to avoid iteration through the main storage
    private final NavigableMap<Integer, String> zKeyIndex = new TreeMap<>();

    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;

    public WidgetInMemoryRepository() {
        var lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    @Override
    public List<Widget> findAll(int page, int perPage) {
        var startIndex = (page - 1) * perPage;
        if (startIndex >= zKeyIndex.size()) {
            return Collections.EMPTY_LIST;
        }

        readLock.lock();
        try {
            return zKeyIndex.values().stream()
                    .skip(startIndex)
                    .limit(perPage)
                    .map(mainStorage::get)
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Optional<Widget> findById(String id) {
        readLock.lock();
        try {
            return Optional.ofNullable(mainStorage.get(id));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Widget createOrUpdate(Widget widget) {
        writeLock.lock();
        try {
            if (widget.getZ() == null) {
                widget.setZ(getTopZ());
            } else {
                shiftUpFromZIndex(widget.getZ());
            }
            return save(widget);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void deleteById(String id) {
        writeLock.lock();
        try {
            Optional<Widget> byId = findById(id);
            byId.ifPresent(widget -> {
                zKeyIndex.remove(widget.getZ());
                mainStorage.remove(widget.getId());
            });
        } finally {
            writeLock.unlock();
        }
    }

    private Integer getTopZ() {
        Optional<Widget> optionalWidget = findTop();
        if (optionalWidget.isEmpty()) {
            return 0;
        }
        return optionalWidget.get().getZ() + Z_SHIFT_VALUE;
    }

    private Widget save(Widget widget) {
        if (widget.getId() == null) {
            widget.setId(UUID.randomUUID().toString());
            widget.setCreatedDate(LocalDateTime.now());
        } else {
            deleteById(widget.getId());
        }

        widget.setLastModifiedDate(LocalDateTime.now());

        mainStorage.put(widget.getId(), widget);
        zKeyIndex.put(widget.getZ(), widget.getId());

        return widget;
    }

    private void shiftUpFromZIndex(Integer zIndex) {
        findAllAboveZIndex(zIndex).forEach(widget -> {
                    Integer z = widget.getZ();
                    zKeyIndex.remove(z);
                    widget.setZ(z + 1);
                    save(widget);
                });
    }

    private List<Widget> findAllAboveZIndex(Integer zIndex) {
        int size = zKeyIndex.size();
        if (size == 0 || zIndex > zKeyIndex.lastKey()) {
            return Collections.emptyList();
        }

        return zKeyIndex.navigableKeySet().subSet(
                zIndex, true,
                zKeyIndex.lastKey(), true
        ).descendingSet().stream()
                .map(zKeyIndex::get)
                .map(mainStorage::get)
                .collect(Collectors.toList());

    }

    private Optional<Widget> findTop() {
        if (zKeyIndex.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(mainStorage.get(zKeyIndex.lastEntry().getValue()));
    }
}
