package com.crazydude.lostfilmclient.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;
import android.widget.Toast;

import com.crazydude.common.api.LoginResponse;
import com.crazydude.common.api.LostFilmApi;
import com.crazydude.common.events.LoginEvent;
import com.crazydude.lostfilmclient.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Observer;

/**
 * Created by Crazy on 07.01.2017.
 */

public class LoginFragment extends GuidedStepFragment implements Observer<LoginResponse> {

    private static final long LOGIN_ACTION = 0;
    private static final long EMAIL = 1;
    private static final long PASSWORD = 2;

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(getActivity(), getString(R.string.wrong_login_or_password), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNext(LoginResponse loginResponse) {
        EventBus.getDefault().post(new LoginEvent(loginResponse.getName()));
    }

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        String title = getString(R.string.login_fragment_title);
        String description = getString(R.string.login_fragment_description);
        return new GuidanceStylist.Guidance(title, description, "", null);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction enterUsername = new GuidedAction.Builder(getActivity())
                .id(EMAIL)
                .title(R.string.email)
                .descriptionEditable(true)
                .build();
        GuidedAction enterPassword = new GuidedAction.Builder(getActivity())
                .id(PASSWORD)
                .title(R.string.password)
                .descriptionEditable(true)
                .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT)
                .build();
        GuidedAction login = new GuidedAction.Builder(getActivity())
                .id(LOGIN_ACTION)
                .title(R.string.login)
                .build();
        actions.add(enterUsername);
        actions.add(enterPassword);
        actions.add(login);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == LOGIN_ACTION) {
            try {
                String email = findActionById(EMAIL).getDescription().toString();
                String password = findActionById(PASSWORD).getDescription().toString();
                LostFilmApi.getInstance().login(email, password).subscribe(this);
            } catch (NullPointerException e) {
                Toast.makeText(getActivity(), R.string.enter_login, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
