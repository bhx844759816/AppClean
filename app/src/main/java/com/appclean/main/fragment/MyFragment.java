package com.appclean.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fragment.YwBaseFragment;
import com.app.presenter.Presenter;
import com.appclean.main.R;
import com.appclean.main.presenter.MyPresenter;
import com.appclean.main.view.IMyView;

/**
 * 我的Fragment
 */
public class MyFragment extends YwBaseFragment implements IMyView {
    @Override
    protected Presenter getPresenter() {
        return new MyPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        return view;
    }
}
