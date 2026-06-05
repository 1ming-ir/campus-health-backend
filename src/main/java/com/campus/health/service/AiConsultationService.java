package com.campus.health.service;

import com.campus.health.dto.ConsultationRequest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class AiConsultationService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.api-key:demo-key}")
    private String apiKey;

    @Value("${ai.endpoint:}")
    private String endpoint;

    @Value("${ai.model:gpt-4o-mini}")
    private String model;

    public String generateAdvice(ConsultationRequest req) {
        if (hasRealAiConfig()) {
            try {
                String aiAdvice = callLargeLanguageModel(req);
                if (StringUtils.hasText(aiAdvice)) {
                    return ensureMedicalDisclaimer(aiAdvice);
                }
            } catch (RuntimeException ignored) {
                // Network or API failures must not block the student demo workflow.
            }
        }
        return generateFallbackAdvice(req);
    }

    private String callLargeLanguageModel(ConsultationRequest req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
            "model", model,
            "messages", List.of(
                Map.of(
                    "role", "system",
                    "content", "你是校园健康咨询助手，只能提供健康建议、风险提示和就医建议，不能做正式医学诊断。遇到严重症状必须建议尽快线下就医或急救。"
                ),
                Map.of(
                    "role", "user",
                    "content", buildPrompt(req)
                )
            ),
            "temperature", 0.2
        );

        Map response = restTemplate.postForObject(endpoint, new HttpEntity<>(body, headers), Map.class);
        if (response == null) {
            return null;
        }
        Object choicesObj = response.get("choices");
        if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
            return null;
        }
        Object first = choices.get(0);
        if (!(first instanceof Map<?, ?> firstChoice)) {
            return null;
        }
        Object messageObj = firstChoice.get("message");
        if (!(messageObj instanceof Map<?, ?> message)) {
            return null;
        }
        Object content = message.get("content");
        return content == null ? null : content.toString();
    }

    private String buildPrompt(ConsultationRequest req) {
        return "请根据以下学生症状生成初步健康建议，包含：可能的日常护理建议、需要观察的风险信号、是否建议预约校医院。"
            + "症状描述：" + valueOrDefault(req.getSymptom(), "未填写")
            + "；持续时间：" + valueOrDefault(req.getDuration(), "未填写")
            + "；严重程度：" + valueOrDefault(req.getSeverity(), "未填写")
            + "；用药情况：" + valueOrDefault(req.getMedicineUsed(), "未说明")
            + "。请明确说明本建议仅供健康咨询参考，不构成正式医学诊断。";
    }

    private String generateFallbackAdvice(ConsultationRequest req) {
        String symptom = valueOrDefault(req.getSymptom(), "未填写具体症状");
        String duration = valueOrDefault(req.getDuration(), "未填写持续时间");
        String severity = valueOrDefault(req.getSeverity(), "未填写严重程度");
        String medicine = valueOrDefault(req.getMedicineUsed(), "未说明是否用药");
        String riskHint = "轻度".equals(severity) ? "当前描述偏轻症，可先观察变化。" : "症状程度较高，建议尽快到校医院或正规医疗机构进一步评估。";

        return "根据你描述的症状：" + symptom + "；持续时间：" + duration + "；严重程度：" + severity + "；用药情况：" + medicine + "。"
            + riskHint
            + "建议先保持休息、补充水分，记录体温和症状变化；如出现持续高热、呼吸困难、剧烈疼痛、意识异常、皮疹迅速扩散或症状明显加重，请及时前往校医院或拨打急救电话。"
            + "当前未配置可用的大语言模型接口，系统已使用本地安全兜底建议。"
            + "本建议仅供健康咨询参考，不构成正式医学诊断。";
    }

    private String ensureMedicalDisclaimer(String advice) {
        if (advice.contains("不构成正式医学诊断") || advice.contains("不构成医学诊断")) {
            return advice;
        }
        return advice + " 本建议仅供健康咨询参考，不构成正式医学诊断。";
    }

    private boolean hasRealAiConfig() {
        return StringUtils.hasText(endpoint) && StringUtils.hasText(apiKey) && !"demo-key".equals(apiKey);
    }

    private String valueOrDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}
