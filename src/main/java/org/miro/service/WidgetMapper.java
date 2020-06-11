package org.miro.service;

import org.miro.api.WidgetPresentation;
import org.miro.model.Widget;
import org.springframework.stereotype.Service;

@Service
public class WidgetMapper {

    public WidgetPresentation getWidgetPresentation(Widget widget) {
        return new WidgetPresentation()
                .withId(widget.getId())
                .withXindex(widget.getX())
                .withYindex(widget.getY())
                .withZindex(widget.getZ())
                .withWidth(widget.getWidth())
                .withHeight(widget.getHeight())
                .withLastModifiedDate(widget.getLastModifiedDate().toString());
    }

}
