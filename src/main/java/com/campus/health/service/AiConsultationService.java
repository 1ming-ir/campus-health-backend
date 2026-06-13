package com.campus.health.service;

import com.campus.health.dto.ConsultationRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiConsultationService {
    public String generateAdvice(ConsultationRequest req) {
        List<String> lines = new ArrayList<>();
        lines.add("初步判断：根据您描述的症状、持续时间和严重程度，目前只能给出健康咨询层面的初步建议，不能替代医生诊断。");
        lines.add("日常护理建议：保持休息，适量饮水，避免熬夜和剧烈运动，饮食尽量清淡，观察症状变化。");
        if ("已用药".equals(req.getMedicineUsed())) {
            lines.add("用药提示：您已使用 " + safe(req.getMedicineName()) + "，请按说明书或医生建议使用，不要自行叠加同类药物。");
        } else {
            lines.add("用药提示：暂未用药时，不建议自行随意服药，尤其不要自行使用抗生素或多种复方感冒药。");
        }
        lines.add("需要观察的风险信号：若出现高热不退、呼吸困难、胸痛、意识异常、剧烈疼痛、皮疹扩散、频繁呕吐或症状持续加重，请及时线下就医。");
        lines.add("是否建议预约校医院：如症状持续超过 24-48 小时未缓解，或您对症状原因不确定，建议预约校医院进一步评估。");
        lines.add("免责声明：以上内容仅供健康咨询参考，不构成正式医学诊断或处方。");
        return cleanMarkdown(String.join("\n", lines));
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "相关药物" : value.trim();
    }

    private String cleanMarkdown(String text) {
        return text.replace("###", "").replace("##", "").replace("**", "").replace("---", "").replace("```", "").trim();
    }
}