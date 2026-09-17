package cyberpulse.risk.engine;


import cyberpulse.risk.entity.RiskSeverity;

import java.util.Map;

public record RiskCalculationResult(

        int riskScore,

        RiskSeverity severity,

        int baseScore,

        int confidenceScore,

        int frequencyScore,

        int severityScore,

        Map<String, Object> factors
) {
}