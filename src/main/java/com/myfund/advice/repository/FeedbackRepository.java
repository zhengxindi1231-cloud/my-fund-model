package com.myfund.advice.repository;

import com.myfund.advice.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户反馈仓储接口。
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
