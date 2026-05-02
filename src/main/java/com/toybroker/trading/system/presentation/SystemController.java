package com.toybroker.trading.system.presentation;

import com.toybroker.trading.common.api.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    @GetMapping("/api/system/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.ok(Map.of(
                "status", "UP",
                "project", "stock-trading-system"
        ));
    }
}
