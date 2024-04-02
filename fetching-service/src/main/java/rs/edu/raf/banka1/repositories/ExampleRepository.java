package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ExampleModel;

import java.util.Optional;

@Repository
public interface ExampleRepository extends JpaRepository<ExampleModel, Long> {
    Optional<ExampleModel> findByExampleId(Long exampleId);
}
