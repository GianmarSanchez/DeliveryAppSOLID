package srp.controllers;

import org.jetbrains.annotations.NotNull;

import io.javalin.http.Context;

public interface OrderController {
    void create(@NotNull Context context);

    void find(@NotNull Context context);

    void findAll(@NotNull Context context);
}