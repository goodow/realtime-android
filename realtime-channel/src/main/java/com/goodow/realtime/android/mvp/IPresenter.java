package com.goodow.realtime.android.mvp;

/**
 * Created by larry on 2017/11/7.
 */

public interface IPresenter<T> {

  void update(IView view, T data);

}
