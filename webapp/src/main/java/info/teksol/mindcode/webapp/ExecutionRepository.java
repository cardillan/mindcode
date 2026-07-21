package info.teksol.mindcode.webapp;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ExecutionRepository extends CrudRepository<Execution, UUID> {
}
