package org.miro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.miro.api.WidgetDescription;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.InvalidObjectException;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Widget implements Cloneable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    private Integer x;
    private Integer y;
    private Integer z;
    private Integer width;
    private Integer height;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public static Widget from(WidgetDescription widgetDescription) throws InvalidObjectException {
        if (widgetDescription.getXindex() == null || widgetDescription.getYindex() == null) {
            throw new InvalidObjectException("X index and Y index should be not empty");
        }

        if (widgetDescription.getWidth() == null || widgetDescription.getHeight() == null ||
            widgetDescription.getWidth() <= 0 || widgetDescription.getHeight() <= 0 ) {
            throw new InvalidObjectException("Width and Height should be positive");
        }
        return Widget.builder()
                .x(widgetDescription.getXindex())
                .y(widgetDescription.getYindex())
                .z(widgetDescription.getZindex())
                .width(widgetDescription.getWidth())
                .height(widgetDescription.getHeight())
                .build();
    }

    @Override
    public Widget clone() {
        return Widget.builder()
                .id(id)
                .x(x)
                .y(y)
                .z(z)
                .width(width)
                .height(height)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }
}
