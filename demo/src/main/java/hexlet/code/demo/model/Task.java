package hexlet.code.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Integer index;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "assignee_id", foreignKey = @ForeignKey(name = "fk_task_user", foreignKeyDefinition = "FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User assigneeId;

    private String title;
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatus status;
}
