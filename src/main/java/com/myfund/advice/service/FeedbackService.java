package com.myfund.advice.service;

import com.myfund.advice.model.Feedback;
import com.myfund.advice.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户反馈服务：收集用户满意度及改进建议。
 */
@Service
public class FeedbackService {

    private final FeedbackRepository repository;

    public FeedbackService(FeedbackRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Feedback save(Feedback feedback) {
        return repository.save(feedback);
    }
}
