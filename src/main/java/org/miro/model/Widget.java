package org.miro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.miro.api.WidgetCoordinates;
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
import java.util.InvalidPropertiesFormatException;

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

    @Column
    private Integer z;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public static Widget from(WidgetCoordinates widgetCoordinates) throws InvalidObjectException {
        if (widgetCoordinates.getXindex() == null || widgetCoordinates.getYindex() == null) {
            throw new InvalidObjectException("X index and Y index should be not empty");
        }
        return Widget.builder()
                .x(widgetCoordinates.getXindex())
                .y(widgetCoordinates.getYindex())
                .z(widgetCoordinates.getZindex())
                .build();
    }

    @Override
    public Widget clone() {
        return Widget.builder()
                .id(id)
                .x(x)
                .y(y)
                .z(z)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }
}
