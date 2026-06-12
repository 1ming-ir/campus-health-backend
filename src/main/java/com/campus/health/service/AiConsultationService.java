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
                    return cleanMarkdown(ensureMedicalDisclaimer(aiAdvice));
                }
            } catch (RuntimeException ignored) {
                // API failure falls back to local safety advice so the demo flow remains available.
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
                    "content", "浣犳槸鏍″洯鍋ュ悍鍜ㄨ鍔╂墜锛屽彧鑳芥彁渚涘仴搴峰挩璇€侀闄╂彁绀哄拰灏卞尰寤鸿锛屼笉鑳藉仛姝ｅ紡鍖诲璇婃柇銆傝涓嶈浣跨敤 Markdown 绗﹀彿锛屼笉瑕佽緭鍑?###銆?*銆?--銆傝鎸夊浐瀹氬皬鏍囬杈撳嚭锛氬垵姝ュ垽鏂€佹棩甯告姢鐞嗗缓璁€侀渶瑕佽瀵熺殑椋庨櫓淇″彿銆佹槸鍚﹀缓璁绾︽牎鍖婚櫌銆佸厤璐ｅ０鏄庛€?
                ),
                Map.of("role", "user", "content", buildPrompt(req))
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
        return "璇锋牴鎹互涓嬪鐢熺棁鐘剁敓鎴愬垵姝ュ仴搴峰缓璁€?
            + "鐥囩姸鎻忚堪锛? + valueOrDefault(req.getSymptom(), "鏈～鍐?)
            + "锛涙寔缁椂闂达細" + valueOrDefault(req.getDuration(), "鏈～鍐?)
            + "锛涗弗閲嶇▼搴︼細" + valueOrDefault(req.getSeverity(), "鏈～鍐?)
            + "锛涙槸鍚︾敤鑽細" + valueOrDefault(req.getMedicineUsed(), "鏈鏄?)
            + "锛涜嵂鍝佸悕绉帮細" + valueOrDefault(req.getMedicineName(), "鏃?)
            + "銆傝鏄庣‘璇存槑鏈缓璁粎渚涘仴搴峰挩璇㈠弬鑰冿紝涓嶆瀯鎴愭寮忓尰瀛﹁瘖鏂€?;
    }

    private String generateFallbackAdvice(ConsultationRequest req) {
        String symptom = valueOrDefault(req.getSymptom(), "鏈～鍐欏叿浣撶棁鐘?);
        String duration = valueOrDefault(req.getDuration(), "鏈～鍐欐寔缁椂闂?);
        String severity = valueOrDefault(req.getSeverity(), "鏈～鍐欎弗閲嶇▼搴?);
        String medicine = valueOrDefault(req.getMedicineUsed(), "鏈鏄庢槸鍚︾敤鑽?);
        String medicineName = valueOrDefault(req.getMedicineName(), "鏃?);
        String riskHint = "閲嶅害".equals(severity)
            ? "褰撳墠涓ラ噸绋嬪害杈冮珮锛屽缓璁敖蹇埌鏍″尰闄㈡垨姝ｈ鍖荤枟鏈烘瀯杩涗竴姝ヨ瘎浼般€?
            : "褰撳墠淇℃伅鍙厛瑙傚療鍙樺寲锛屼絾浠嶉渶鍏虫敞鐥囩姸鏄惁鍔犻噸銆?;

        return "鍒濇鍒ゆ柇锛氭牴鎹綘鎻忚堪鐨勭棁鐘垛€? + symptom + "鈥濓紝鎸佺画鏃堕棿涓? + duration + "锛屼弗閲嶇▼搴︿负" + severity + "锛岀敤鑽儏鍐典负" + medicine + "锛岃嵂鍝佸悕绉颁负" + medicineName + "銆? + riskHint + "\n"
            + "鏃ュ父鎶ょ悊寤鸿锛氫繚璇佷紤鎭紝琛ュ厖姘村垎锛岄伩鍏嶇啲澶溿€侀ギ閰掋€佸墽鐑堣繍鍔ㄥ拰杈涜荆娌硅吇椋熺墿锛岃褰曚綋娓╁拰鐥囩姸鍙樺寲銆俓n"
            + "闇€瑕佽瀵熺殑椋庨櫓淇″彿锛氬鍑虹幇鎸佺画楂樼儹銆佸懠鍚稿洶闅俱€佽兏鐥涖€佹剰璇嗗紓甯搞€佸墽鐑堢柤鐥涖€佺毊鐤瑰揩閫熸墿鏁ｃ€佹槑鏄捐劚姘存垨鐥囩姸鎸佺画鍔犻噸锛岃绔嬪嵆绾夸笅灏卞尰銆俓n"
            + "鏄惁寤鸿棰勭害鏍″尰闄細濡傜棁鐘惰秴杩?24-48 灏忔椂鏈紦瑙ｏ紝鎴栧奖鍝嶅涔犵敓娲伙紝寤鸿棰勭害鏍″尰闄㈣繘涓€姝ヨ瘎浼般€俓n"
            + "鍏嶈矗澹版槑锛氭湰寤鸿浠呬緵鍋ュ悍鍜ㄨ鍙傝€冿紝涓嶆瀯鎴愭寮忓尰瀛﹁瘖鏂€?;
    }

    private String ensureMedicalDisclaimer(String advice) {
        if (advice.contains("涓嶆瀯鎴愭寮忓尰瀛﹁瘖鏂?) || advice.contains("涓嶆瀯鎴愬尰瀛﹁瘖鏂?)) {
            return advice;
        }
        return advice + "\n鍏嶈矗澹版槑锛氭湰寤鸿浠呬緵鍋ュ悍鍜ㄨ鍙傝€冿紝涓嶆瀯鎴愭寮忓尰瀛﹁瘖鏂€?;
    }

    private String cleanMarkdown(String text) {
        return text.replace("###", "")
            .replace("##", "")
            .replace("**", "")
            .replace("---", "")
            .replace("```", "")
            .trim();
    }

    private boolean hasRealAiConfig() {
        return StringUtils.hasText(endpoint) && StringUtils.hasText(apiKey) && !"demo-key".equals(apiKey);
    }

    private String valueOrDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}