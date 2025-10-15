package com.myfund.advice.tool;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 基金档案查询工具。
 */
@Component
public class FundProfileTool {

    /**
     * TODO: 替换为访问基金档案数据库或 API 的逻辑。
     */
    public Map<String, Object> findFundProfile(String fundCode) {
        return Map.of(
            "fundCode", fundCode,
            "fundName", "示例成长混合A",
            "fundManager", "TODO: 填写基金经理信息",
            "benchmark", "中证800指数",
            "fee", "1.5%",
            "riskLevel", "中高风险"
        );
    }
}
