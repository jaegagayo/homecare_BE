package jaega.homecare.domain.voucherUsage.service.command;

import jaega.homecare.domain.voucherUsage.entity.ServiceFee;

import java.util.Map;

public class ServiceFeeTable {
    private static final Map<Integer, ServiceFee> feeTable = Map.of(
            30, new ServiceFee(16940L, 2541L),
            60, new ServiceFee(24580L, 3687L),
            90, new ServiceFee(33120L, 4968L),
            120, new ServiceFee(42160L, 6324L),
            150, new ServiceFee(49160L, 7374L),
            180, new ServiceFee(55350L, 8303L),
            210, new ServiceFee(61670L, 9251L),
            240, new ServiceFee(68030L, 10205L)
    );

    public static ServiceFee getFee(int minutes) {
        return feeTable.getOrDefault(minutes, feeTable.get(180)); // 기본값: 180분
    }
}