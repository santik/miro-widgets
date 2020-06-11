package org.miro.repository.jpa;

import lombok.RequiredArgsConstructor;
import org.miro.model.Widget;
import org.miro.repository.WidgetRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "jpa")
public class WidgetJpaRepository implements WidgetRepository<Widget, String> {

    private final WidgetCrudRepository crudRepository;

    @Override
    public List<Widget> findAll() {
        return crudRepository.findAllByOrderByZ();
    }

    @Override
    public Optional<Widget> findById(String id) {
        return crudRepository.findById(id);
    }

    @Override
    public Widget createOrUpdate(Widget widget) {
        if (widget.getZ() == null) {
            widget.setZ(getTopZ());
        } else {
            shiftUpFromZIndex(widget.getZ());
        }
        return crudRepository.save(widget);
    }

    @Override
    public void deleteById(String id) {
        crudRepository.deleteById(id);
    }

    private Integer getTopZ() {
        Optional<Widget> optionalWidget = findTop();
        if (optionalWidget.isEmpty()) {
            return 0;
        }
        return optionalWidget.get().getZ() + 1;
    }

    private void shiftUpFromZIndex(Integer zIndex) {
        crudRepository.findAllByZGreaterThanEqualOrderByZDesc(zIndex)
                .forEach(widget1 -> {
                    widget1.setZ(widget1.getZ() + 1);
                    crudRepository.save(widget1);
                });
    }

    private Optional<Widget> findTop() {
        return crudRepository.findTopByOrderByZ();
    }
}
