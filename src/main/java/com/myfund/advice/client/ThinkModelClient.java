package com.myfund.advice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * ThinkModelClient 封装了对 GPT-5 Think（或兼容 OpenAI 协议模型）的调用逻辑。
 * 实际对接时需要根据接口格式调整 payload 与鉴权。
 */
public class ThinkModelClient {

    private static final Logger log = LoggerFactory.getLogger(ThinkModelClient.class);

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public ThinkModelClient(WebClient webClient, String apiKey, String model) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.model = model;
    }

    /**
     * 发送提示到模型服务。
     * TODO: 补充同花顺/阿里大模型代理的真实请求与响应结构。
     */
    public String generateAdvice(String prompt) {
        if (!StringUtils.hasText(apiKey)) {
            log.warn("未配置 GPT-5 Think API Key，将返回占位响应");
            return fallbackResponse();
        }
        try {
            Mono<Map> responseMono = webClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(Map.of(
                    "model", model,
                    "messages", new Object[]{Map.of("role", "user", "content", prompt)},
                    "temperature", 0.2
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(15));

            Map<String, Object> response = responseMono.blockOptional().orElse(Map.of());
            return extractContent(response);
        } catch (Exception ex) {
            log.warn("调用 GPT-5 Think 接口失败，使用兜底文案", ex);
            return fallbackResponse();
        }
    }

    private String extractContent(Map<String, Object> response) {
        Object choices = response.get("choices");
        if (choices instanceof Iterable<?> iterable) {
            for (Object choice : iterable) {
                if (choice instanceof Map<?, ?> choiceMap) {
                    Object message = choiceMap.get("message");
                    if (message instanceof Map<?, ?> messageMap) {
                        Object content = messageMap.get("content");
                        if (content instanceof String text && StringUtils.hasText(text)) {
                            return text;
                        }
                    }
                }
            }
        }
        return fallbackResponse();
    }

    private String fallbackResponse() {
        return "模型服务暂不可用，请联系人工顾问或稍后重试。";
    }
}
