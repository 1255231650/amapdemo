package com.android.amapdemo;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RxManager {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public RxManager() {
    }

    public void add(Disposable m) {
        this.mCompositeDisposable.add(m);
    }

    public void clear() {
        this.mCompositeDisposable.clear();
    }
}
