package cyberpulse.risk.dto;

public record RiskFactorResponse(

        String name,

        Integer score,

        String reason
) {
}
