package com.myfund.advice.config;

import com.myfund.advice.client.ThinkModelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 模型接入配置：构建 WebClient 与 ThinkModelClient，调用 GPT-5 Think 或兼容接口。
 */
@Configuration
public class ModelConfig {

    @Bean
    public WebClient thinkModelWebClient(@Value("${gpt5think.api.base-url}") String baseUrl) {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Bean
    public ThinkModelClient thinkModelClient(WebClient thinkModelWebClient,
                                             @Value("${gpt5think.api.key}") String apiKey,
                                             @Value("${gpt5think.api.model:gpt-5-think}") String model) {
        return new ThinkModelClient(thinkModelWebClient, apiKey, model);
    }
}
