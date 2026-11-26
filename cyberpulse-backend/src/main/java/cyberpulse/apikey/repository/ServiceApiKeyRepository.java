package cyberpulse.apikey.repository;

import cyberpulse.apikey.entity.ServiceApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceApiKeyRepository extends JpaRepository<ServiceApiKey, UUID> {

    Optional<ServiceApiKey> findByKeyHashAndActiveTrue(String keyHash);

    boolean existsByNameIgnoreCase(String name);

    List<ServiceApiKey> findAllByOrderByCreatedAtDesc();
}
