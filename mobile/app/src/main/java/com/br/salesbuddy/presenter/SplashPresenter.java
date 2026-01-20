package com.br.salesbuddy.presenter;

import com.br.salesbuddy.contract.SplashContract;
import com.br.salesbuddy.model.SplashModel;

import java.util.Timer;
import java.util.TimerTask;

public class SplashPresenter implements SplashContract.Presenter {

    private SplashContract.View view;
    private SplashModel model;
    private Timer timer;

    public SplashPresenter(SplashContract.View view) {
        this.view = view;
        this.model = new SplashModel();
    }

    @Override
    public void startSplashLogic() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (view != null) {
                    view.navigateToLogin();
                }
            }
        }, model.getDelayMillis());
    }

    @Override
    public void destroy() {
        // Cancela o timer se a tela for fechada antes do tempo para evitar crashes
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        view = null;
    }
}