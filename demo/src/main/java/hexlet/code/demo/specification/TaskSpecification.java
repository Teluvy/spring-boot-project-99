package hexlet.code.demo.specification;

import hexlet.code.demo.dto.TaskFilter;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.model.Task;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {
    public static Specification<Task> build(TaskFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitleCont() != null && !filter.getTitleCont().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("title").as(String.class)),
                        "%" + filter.getTitleCont().toLowerCase() + "%"
                ));
            }

            if (filter.getAssigneeId() != null) {
                predicates.add(cb.equal(root.get("assigneeId").get("id"), filter.getAssigneeId()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status").get("slug"), filter.getStatus()));
            }

            if (filter.getLabelId() != null) {
                Join<Task, Label> labels = root.join("labels", JoinType.INNER);
                predicates.add(cb.equal(labels.get("id"), filter.getLabelId()));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
