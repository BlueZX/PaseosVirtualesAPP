package com.ubb.paseosVirtuales.helper;

import android.app.Activity;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class SnackbarHelper {
    private static final int BACKGROUND_COLOR = 0xbf323232;
    private Snackbar messageSnackbar;
    private enum DismissBehavior { HIDE, SHOW, FINISH };
    private int maxLines = 2;
    private String lastMessage = "";
    private View snackbarView;

    public boolean isShowing() {
        return messageSnackbar != null;
    }

    // Se muestra un snackbar  con un mensaje enviado por parametro
    public void showMessage(Activity activity, String message) {
        if (!message.isEmpty() && (!isShowing() || !lastMessage.equals(message))) {
            lastMessage = message;
            show(activity, message, BACKGROUND_COLOR, DismissBehavior.HIDE);
        }
    }

    // Se muestra un mensaje con un boton de descartar el mensaje
    public void showMessageWithDismiss(Activity activity, String message, int Color) {
        show(activity, message, Color, DismissBehavior.SHOW);
    }

    // muestra el snackbar con un mensaje de error
    public void showError(Activity activity, String errorMessage) {
        show(activity, errorMessage, BACKGROUND_COLOR, DismissBehavior.FINISH);
    }

    // oculta el snackbar
    public void hide(Activity activity) {
        if (!isShowing()) {
            return;
        }
        lastMessage = "";
        final Snackbar messageSnackbarToHide = messageSnackbar;
        messageSnackbar = null;
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        messageSnackbarToHide.dismiss();
                    }
                });
    }

    public void setMaxLines(int lines) {
        maxLines = lines;
    }

    public void setParentView(View snackbarView) {
        this.snackbarView = snackbarView;
    }

    private void show(final Activity activity, final String message, int BackgroundColor, final DismissBehavior dismissBehavior) {
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        messageSnackbar = Snackbar.make(snackbarView == null ? activity.findViewById(android.R.id.content) : snackbarView, message, Snackbar.LENGTH_INDEFINITE);
                        messageSnackbar.getView().setBackgroundColor((BackgroundColor == 0) ? BACKGROUND_COLOR: BackgroundColor);

                        if (dismissBehavior != DismissBehavior.HIDE) {
                            messageSnackbar.setAction("Cerrar", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    messageSnackbar.dismiss();
                                }
                            });

                            if (dismissBehavior == DismissBehavior.FINISH) {
                                messageSnackbar.addCallback(
                                        new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                            @Override
                                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                                super.onDismissed(transientBottomBar, event);
                                                activity.finish();
                                            }
                                        }
                                );
                            }
                        }
                        ((TextView) messageSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setMaxLines(maxLines);
                        messageSnackbar.setDuration(6000);
                        messageSnackbar.show();
                    }
                }
        );
    }
}
