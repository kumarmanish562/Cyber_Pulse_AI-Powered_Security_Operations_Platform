package cyberpulse.event.specification;

import cyberpulse.event.dto.SecurityEventFilter;
import cyberpulse.event.entity.SecurityEvent;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public final class SecurityEventSpecification {

    private SecurityEventSpecification() {
    }

    public static Specification<SecurityEvent> withFilter(
            SecurityEventFilter filter
    ) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            if (filter.eventType() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("eventType"),
                                filter.eventType()
                        )
                );
            }

            if (filter.severity() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("severity"),
                                filter.severity()
                        )
                );
            }

            if (filter.sourceIp() != null &&
                    !filter.sourceIp().isBlank()) {

                try {

                    InetAddress ip =
                            InetAddress.getByName(
                                    filter.sourceIp().trim()
                            );

                    predicates.add(
                            criteriaBuilder.equal(
                                    root.get("sourceIp"),
                                    ip
                            )
                    );

                } catch (Exception exception) {

                    throw new IllegalArgumentException(
                            "Invalid source IP address"
                    );
                }
            }

            if (filter.username() != null &&
                    !filter.username().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("username")
                                ),
                                "%" +
                                        filter.username()
                                                .trim()
                                                .toLowerCase() +
                                        "%"
                        )
                );
            }

            if (filter.service() != null &&
                    !filter.service().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("service")
                                ),
                                "%" +
                                        filter.service()
                                                .trim()
                                                .toLowerCase() +
                                        "%"
                        )
                );
            }

            if (filter.from() != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("eventTime"),
                                filter.from()
                        )
                );
            }

            if (filter.to() != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("eventTime"),
                                filter.to()
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }
}