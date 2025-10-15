# A股基金智能投顾服务骨架

本项目依据 `fund.md` 方案实现了一个基于 Spring Boot 与 Spring AI 的基金智能投顾后端骨架，覆盖行情数据拉取、基金档案、风险评估、合规控制以及观测埋点等核心模块。当前版本默认使用内存数据库与占位数据，便于后续快速对接真实接口。

## 主要功能
- `/advice`：生成买入/卖出/调仓建议，整合用户画像、实时行情、基金档案并调用 GPT-5 Think 推理。
- `/profile`：管理用户画像信息，用于构建风险偏好与收益目标。
- `/feedback`：收集用户反馈，支持运营分析与模型调优。
- Spring Security 基础防护，可扩展 OAuth2.1 企业级认证。
- Micrometer 观测配置，可接入 Prometheus / 阿里云 ARMS。

## 同花顺 API 对接说明
- `MarketDataTool` 与 `FundProfileTool` 中的 `TODO` 标记了同花顺或其他权威数据源的接入位置。
- 请根据实际提供的 API Endpoint、鉴权参数补全调用逻辑，处理限流、重试、缓存等细节。

## GPT-5 Think 接入
- `src/main/resources/application.yml` 中预留了 `gpt5think.api` 配置，可通过环境变量或配置中心覆盖。
- `ModelConfig` 构造了 `ThinkModelClient`，默认通过 WebClient 调用 GPT-5 Think 或兼容代理服务。

## 本地运行
```bash
mvn spring-boot:run
```

默认账号密码：`admin/admin123`。可在 `application.yml` 中调整。

## 后续扩展建议
- 引入 MongoDB/ElasticSearch 存储提示日志，配合人工审核工作台。
- 接入 Kafka + 时序数据库处理实时行情。
- 构建 Feature Flag 管理模型版本、提示策略与合规规则。
