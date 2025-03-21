package org.romanzhula.microservices_common.utis;


import org.romanzhula.microservices_common.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceFeignClient {

    @GetMapping("/api/v1/users/by-username")
    UserResponse getUserByUsername(@RequestParam String username);

}
