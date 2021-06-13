package com.haibin.httpnet;

import com.haibin.httpnet.builder.Request;
import com.haibin.httpnet.core.Response;
import com.haibin.httpnet.core.call.Callback;
import com.haibin.httpnet.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());

    }
}
