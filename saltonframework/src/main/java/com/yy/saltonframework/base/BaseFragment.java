package com.yy.saltonframework.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2015/12/14 16:16
 * Time: 16:16
 * Description:
 */
public abstract class BaseFragment extends Fragment {
    public View mContentView ;
    protected ProgressDialog mLoadingDialog;

    /**
     * //绑定Activity的时候就会调用
     * @param activity 要绑定的Activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLoadingDialog = new ProgressDialog(activity);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setMessage("数据加载中...");

    }


    /**
     * 设置是否可见
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if(isVisibleToUser){        //可见
            onUserVisible();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mContentView ==null){
            initView(savedInstanceState);
            setListener();
            processLogic(savedInstanceState);
        }
        return mContentView;
    }

    public void setContentView(@LayoutRes int LayoutResId){
        mContentView = LayoutInflater.from(getActivity()).inflate(LayoutResId,null);
    }
    /**
     * 初始化View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    /**
     * 当fragment对用户可见时，会调用该方法，可在该方法中懒加载网络数据
     */
    protected abstract void onUserVisible();

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) mContentView.findViewById(id);
    }

    protected void showToast(String text) {
        Toast.makeText(getContext(),text, Toast.LENGTH_LONG).show();
//        Logger.e(text);
    }


    public Context GetContext(){
        return getActivity();
    }
    /**
     * 根据类打开Activity
     *
     * @param pClass
     */
    public void OpenActivity(Class<?> pClass) {
        OpenActivity(pClass, null);
    }

    /**
     * 带有参数的根据类打开Activity
     *
     * @param pClass
     * @param pBundle 封装的参数
     */
    protected void OpenActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(GetContext(), pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    public void OpenActivityForResult(Class<?> pClass, int pRequestCode) {
        OpenActivityForResult(pClass, pRequestCode,null);
    }
    /**
     * 带有参数的根据类打开Activity
     *
     * @param pClass
     * @param pBundle 封装的参数
     */
    public void OpenActivityForResult(Class<?> pClass, int pRequestCode, Bundle pBundle) {
        Intent intent = new Intent(GetContext(), pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivityForResult(intent, pRequestCode);
    }

    /**
     *
     * @return 布局解析器
     */
    protected LayoutInflater GetLayouInflater() {
        LayoutInflater _LayoutInflater = LayoutInflater.from(GetContext());
        return _LayoutInflater;
    }

    public void ShowToast(String p_Msg) {
        Toast.makeText(getActivity(), p_Msg, Toast.LENGTH_SHORT).show();
        

    }



}
