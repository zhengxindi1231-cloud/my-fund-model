# A股基金智能投顾方案（基于 Spring AI Alibaba 与 GPT-5 Think）

## 1. 背景与目标
- 为国内用户提供 A 股公募基金的买入/卖出建议，结合最新市场行情、基金净值及用户画像。
- 利用 GPT-5 Think 作为核心问答/推理模型，通过 Spring AI 与 Spring AI Alibaba 生态快速集成。
- 满足金融合规需求，确保数据源可靠、回复可解释、支持人工审核闭环。

## 2. 功能范围
1. **信息采集**：
   - 行情数据：基金净值、涨跌幅、行业指数、北向资金等。
   - 基金档案：基金经理、业绩基准、费率、风险等级。
   - 新闻/研报：重大事件、政策解读、基金公司公告。
2. **用户画像**：根据用户填写的风险等级、投资期限、收益期望、持仓结构生成画像。
3. **问答与建议**：
   - 买入建议、卖出建议、调仓建议。
   - 解释性回答：引用数据来源，输出理由和风险提示。
4. **合规审核**：
   - 提供审计日志：用户问题、模型输入、模型输出、数据引用。
   - 敏感词与合规策略过滤。
5. **运营分析**：
   - 使用观测/埋点收集服务性能、提示命中率、用户满意度。

## 3. 技术架构
```
用户端（Web/小程序/APP）
    ↓ HTTPS
API 网关 / Spring Cloud Gateway
    ↓
投顾服务（Spring Boot, Spring AI Alibaba）
    ├─ Prompt Orchestrator（提示模板、Few-Shot、参数调优）
    ├─ Tool Calling（行情数据、基金档案、风险测算）
    ├─ Memory & Profile Store（Redis/PolarDB/PostgreSQL）
    ├─ Observability（Micrometer + Spring AI Observation Extension）
    └─ Compliance Guard（策略过滤、人工审核接口）
        ↓
GPT-5 Think（通过 Spring AI OpenAI 接口/自建代理）
```

## 4. 关键组件设计
### 4.1 接口层
- 使用 Spring MVC/WebFlux 提供 RESTful API：`/advice`, `/profile`, `/feedback`。
- 集成 Spring Security + OAuth2.1，支持企业用户登录、权限划分。
- 通过 API Gateway 进行限流、灰度发布。

### 4.2 服务层
- **AdviceService**：封装提示工程、工具调用、风险控制。
- **MarketDataTool**：封装行情 API（如同花顺、东方财富开放接口或自建数据服务）。
- **FundProfileTool**：查询基金档案库、历史业绩。
- **RiskAssessmentService**：根据用户画像与仓位计算风险暴露。
- **ComplianceService**：敏感词检测、模型输出审计。

### 4.3 数据与存储
- 用户画像与交互历史：PostgreSQL + Redis 缓存。
- 市场/基金数据：
  - 实时行情：Kafka 订阅 + InfluxDB/TimescaleDB。
  - 基础档案：MySQL/PolarDB。
- 模型提示日志：存储于 MongoDB/ElasticSearch 便于分析。

### 4.4 模型调用流程
1. 聚合用户问题、画像、行情数据生成上下文。
2. Prompt 模板包含：
   - 系统提示：合规要求、引用格式。
   - 工具定义：允许模型调用行情/档案/Risk Assessment 工具。
   - Few-shot 示例：买入、卖出、观望场景。
3. 通过 Spring AI Alibaba Tool Calling 接口实现函数调用。
4. 收集模型输出，执行合规策略，如需人工审核则挂起。
5. 返回结构化响应（建议、理由、风险、引用）。

### 4.5 观测与治理
- 使用 Spring AI Observation Extension + Micrometer/Prometheus 记录请求耗时、提示成本、模型质量。
- 接入阿里云 ARMS/SLS 进行日志监控。
- 通过 Feature Flags（如 Alibaba AHAS）管理模型版本与策略。

## 5. 安全与合规
- 符合《证券投资顾问业务暂行规定》：明确告知风险、非强制性建议。
- 存储用户数据需脱敏，加密传输（HTTPS/TLS）。
- 审核流程保留人工 Override 能力，防止模型错误输出。
- 敏感词过滤、政策风险提示、输出引用真实数据来源。

## 6. 开发迭代计划
1. **迭代一：基础设施搭建**
   - 初始化 Spring Boot 项目，引入 Spring AI、Spring AI Alibaba 依赖。
   - 接入 GPT-5 Think 代理（API Key 配置、健康检查）。
   - 实现基础问答 API 与日志记录。
2. **迭代二：工具调用与数据集成**
   - 构建行情/基金档案工具，完成 Tool Calling 流程。
   - 引入用户画像管理、风险评估。
3. **迭代三：合规与运营**
   - 敏感词过滤、人工审核、审计日志。
   - 观测指标与告警。
4. **迭代四：优化与扩展**
   - Prompt 优化、提示版本控制。
   - 增加多语言支持、个性化投顾策略。

## 7. 未来扩展
- 引入量化策略模拟、回测模块。
- 结合强化学习（RAG + 规则）提升建议准确性。
- 接入更多模型（如 Qwen-Max/DeepSeek）构建混合推理策略。
