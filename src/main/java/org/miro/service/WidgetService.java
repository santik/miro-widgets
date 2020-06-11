package org.miro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.miro.api.WidgetDescription;
import org.miro.api.WidgetPresentation;
import org.miro.exception.WidgetNotFound;
import org.miro.model.Widget;
import org.miro.repository.WidgetRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InvalidObjectException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WidgetService {

    private final WidgetRepository<Widget, String> repository;
    private final WidgetMapper mapper;

    @Transactional
    public WidgetPresentation createWidget(WidgetDescription widgetDescription) throws InvalidObjectException {
        var widget = Widget.from(widgetDescription);
        return mapper.getWidgetPresentation(
                repository.createOrUpdate(widget)
        );
    }

    @Transactional
    public WidgetPresentation updateWidget(String id, WidgetDescription coordinates) throws WidgetNotFound {
        var widget = getWidgetToUpdate(id, coordinates);
        return mapper.getWidgetPresentation(
                repository.createOrUpdate(widget)
        );
    }

    @Transactional
    public void deleteWidget(String id) throws WidgetNotFound {
        Optional<Widget> optionalWidget = repository.findById(id);
        if (optionalWidget.isEmpty()) {
            throw new WidgetNotFound();
        }

        repository.deleteById(id);
    }

    public WidgetPresentation findWidgetById(String id) throws WidgetNotFound {
        Optional<Widget> optionalWidget = repository.findById(id);
        if (optionalWidget.isEmpty()) {
            throw new WidgetNotFound();
        }

        return mapper.getWidgetPresentation(
                optionalWidget.get()
        );
    }

    public List<WidgetPresentation> findAllWidgets() {
        return repository.findAll().stream()
                .map(mapper::getWidgetPresentation)
                .collect(Collectors.toList());
    }

    private Widget getWidgetToUpdate(String id, WidgetDescription coordinates) throws WidgetNotFound {

        var widgetOptional = repository.findById(id);
        if (widgetOptional.isEmpty()) {
            throw new WidgetNotFound();
        }

        var widget = widgetOptional.get().clone();

        Optional.ofNullable(coordinates.getXindex()).ifPresent(widget::setX);
        Optional.ofNullable(coordinates.getYindex()).ifPresent(widget::setY);
        Optional.ofNullable(coordinates.getZindex()).ifPresent(widget::setZ);

        return widget;
    }
}
