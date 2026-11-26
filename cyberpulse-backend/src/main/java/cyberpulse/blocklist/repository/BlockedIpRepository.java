package cyberpulse.blocklist.repository;

import cyberpulse.blocklist.entity.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlockedIpRepository extends JpaRepository<BlockedIp, UUID> {

    Optional<BlockedIp> findByIpAddress(String ipAddress);

    List<BlockedIp> findByActiveTrueOrderByCreatedAtDesc();

    List<BlockedIp> findTop5ByIpAddressContainingIgnoreCaseOrReasonContainingIgnoreCaseOrderByCreatedAtDesc(
            String ipAddress,
            String reason
    );
}
