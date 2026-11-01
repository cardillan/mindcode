package io.github.cardillan.mindcode.webapp;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SourceRepository extends CrudRepository<Source, UUID> {
}
