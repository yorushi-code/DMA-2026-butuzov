package dev.yorushi.dma.task3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import dev.yorushi.dma.task3.sync.AccountAuthenticator;

/** P39: provides the account type the sync adapter runs under. */
public final class AuthenticatorService extends Service {

    private AccountAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new AccountAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
