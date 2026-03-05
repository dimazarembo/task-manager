package com.example.demo.repositories.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("""
                select t
                from TaskEntity t
                left join t.assignee a
                where (:status is null or t.status = :status)
                  and (:assigneeId is null or a.id = :assigneeId)
                  and (:authorId is null or t.author.id = :authorId)
            """)
    List<TaskEntity> searchAllByFilter(
            @Param("status") TaskStatus status,
            @Param("assigneeId") Long assigneeId,
            @Param("authorId") Long authorId
    );
}
