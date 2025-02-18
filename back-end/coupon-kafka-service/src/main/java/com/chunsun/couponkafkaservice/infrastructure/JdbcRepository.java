package com.chunsun.couponkafkaservice.infrastructure;

import static com.chunsun.couponkafkaservice.domain.CouponStatus.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.couponkafkaservice.application.dto.BulkInsertCouponDto;

import lombok.RequiredArgsConstructor;

@Transactional
@Repository
@RequiredArgsConstructor
public class JdbcRepository {

	private final JdbcTemplate jdbcTemplate;

	public void bulkInsert(final List<BulkInsertCouponDto> coupons) {
		final String sql = "INSERT INTO member_coupon (coupon_id, member_id, status, expiry_date) VALUES (?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql,
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					BulkInsertCouponDto coupon = coupons.get(i);
					ps.setLong(1, coupon.couponId());
					ps.setLong(2, coupon.memberId());
					ps.setString(3, UNUSED.toString());
					ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().plusDays(coupon.validDays())));
				}

				@Override
				public int getBatchSize() {
					return coupons.size();
				}
			});
	}

	public void updateRemainingQuantities(final List<BulkInsertCouponDto> bulkCoupons) {
		final Map<Long, Long> issuedCountByCouponId = bulkCoupons.stream()
			.collect(Collectors.groupingBy(BulkInsertCouponDto::couponId, Collectors.counting()));

		if (issuedCountByCouponId.isEmpty()) {
			return;
		}

		final String sql = "UPDATE coupons " +
			"   SET remaining_quantity = remaining_quantity - ? " +
			" WHERE id = ?";

		final List<Object[]> batchArgs = new ArrayList<>();
		for (Map.Entry<Long, Long> entry : issuedCountByCouponId.entrySet()) {
			batchArgs.add(new Object[] {entry.getValue().intValue(), entry.getKey()});
		}

		jdbcTemplate.batchUpdate(sql, batchArgs);
	}
}
