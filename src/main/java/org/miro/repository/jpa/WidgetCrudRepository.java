package org.miro.repository.jpa;

import org.miro.model.Widget;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WidgetCrudRepository extends CrudRepository<Widget, String> {
    List<Widget> findAll();
    List<Widget> findAllByZGreaterThanEqualOrderByZDesc(Integer zIndex);
    List<Widget> findAllByOrderByZ(Pageable pageable);
    Optional<Widget> findTopByOrderByZ();
}
