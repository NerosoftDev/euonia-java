package com.euonia.sample.application.contract;

import com.euonia.application.ApplicationService;
import com.euonia.sample.application.dto.UserCreateDto;

import java.util.concurrent.CompletableFuture;

public interface UserApplicationService extends ApplicationService {
    CompletableFuture<Void> createAsync(UserCreateDto data);
}
