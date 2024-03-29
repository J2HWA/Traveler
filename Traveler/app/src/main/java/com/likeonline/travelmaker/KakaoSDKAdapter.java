package com.likeonline.travelmaker;

import android.app.Activity;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.IPushConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;

public class KakaoSDKAdapter extends KakaoAdapter {
 /**
 * Session Config에 대해서는 default값들이 존재한다.
 * 필요한 상황에서만 override해서 사용하면 됨.
 * @return Session의 설정값.
 */

 @Override
 public ISessionConfig getSessionConfig() {
     return new ISessionConfig() {
         /** AuthType을 클릭하면 로그인하고 싶은 타입을 볼수있다.**/
         @Override
         public AuthType[] getAuthTypes() {
             return new AuthType[]{AuthType.KAKAO_TALK_ONLY};
         }

         @Override
         public boolean isUsingWebviewTimer() {
             return false;
         }

         @Override
         public boolean isSecureMode() {
             return true;
         }

         @Override
         public ApprovalType getApprovalType() {
             return ApprovalType.INDIVIDUAL;
         }

         @Override
         public boolean isSaveFormData() {
             return true;
         }
     };
 }

    @Override
    public IPushConfig getPushConfig() {
        return super.getPushConfig();
    }

    @Override
    public IApplicationConfig getApplicationConfig() {
        return new IApplicationConfig() {
            @Override
            public Context getApplicationContext() {
                return GlobalApplication.getGlobalApplicationContext();
            }
        };
    }
}
