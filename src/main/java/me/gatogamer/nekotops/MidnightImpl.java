package me.gatogamer.nekotops;

import com.google.common.util.concurrent.ListeningExecutorService;
import lombok.Getter;
import me.gatogamer.midnight.libs.gson.Gson;

import javax.inject.Inject;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@Getter
public class MidnightImpl {
    @Inject
    private ListeningExecutorService listeningExecutorService;
    @Inject
    private Gson gson;
}