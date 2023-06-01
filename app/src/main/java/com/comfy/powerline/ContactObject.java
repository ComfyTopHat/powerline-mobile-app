package com.comfy.powerline;

public enum ContactObject {

    RED(R.color.pl_primary, R.layout.activity_contacts);

    private int mTitleResId;
    private int mLayoutResId;

    ContactObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
