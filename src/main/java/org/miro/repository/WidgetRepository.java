package org.miro.repository;

import org.miro.model.Widget;

import java.util.List;
import java.util.Optional;

public interface WidgetRepository<T extends Widget, I extends String> {
    List<Widget> findAll(int page, int perPage);
    Optional<T> findById(I id);
    T createOrUpdate(T widget);
    void deleteById(I id);
}