package cyberpulse.soc.service;

import org.springframework.stereotype.Component;

@Component
public class MitreMappingService {

    public MitreTechnique resolve(String threatOrEventType) {
        if (threatOrEventType == null) {
            return new MitreTechnique("TA0001", "Initial Access", "T1078", "Valid Accounts");
        }

        return switch (threatOrEventType.trim().toUpperCase()) {
            case "BRUTE_FORCE", "LOGIN_FAILED" ->
                    new MitreTechnique("TA0006", "Credential Access", "T1110", "Brute Force");
            case "PORT_SCAN" ->
                    new MitreTechnique("TA0043", "Reconnaissance", "T1046", "Network Service Discovery");
            case "SUSPICIOUS_LOGIN", "UNAUTHORIZED_ACCESS" ->
                    new MitreTechnique("TA0001", "Initial Access", "T1078", "Valid Accounts");
            case "PRIVILEGE_ESCALATION" ->
                    new MitreTechnique("TA0004", "Privilege Escalation", "T1068", "Exploitation for Privilege Escalation");
            case "MALWARE_DETECTED" ->
                    new MitreTechnique("TA0002", "Execution", "T1204", "User Execution");
            case "FILE_MODIFIED" ->
                    new MitreTechnique("TA0003", "Persistence", "T1543", "Create or Modify System Process");
            default ->
                    new MitreTechnique("TA0001", "Initial Access", "T1190", "Exploit Public-Facing Application");
        };
    }

    public record MitreTechnique(
            String tacticId,
            String tacticName,
            String techniqueId,
            String techniqueName
    ) {
    }
}
