package com.chunsun.couponservice.application.service;

import com.chunsun.couponservice.application.dto.IssueCouponRecord;

public interface KafkaProducerService {

	void sendCouponIssuedEvent(final IssueCouponRecord issueCouponRecord);
}
