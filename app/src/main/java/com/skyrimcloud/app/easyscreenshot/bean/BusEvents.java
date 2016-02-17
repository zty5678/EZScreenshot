package com.skyrimcloud.app.easyscreenshot.bean;

public class BusEvents {
    public static class SettingChangedEvent{
        private SettingChangedEvent(){

        }
        public SettingEventType eventType;
        public SettingChangedEvent(SettingEventType eventType){
            this.eventType=eventType;
        }

    }
    public enum SettingEventType{

        HideStatusbarIconSetting /*隐藏状态栏图标*/,
        ShakeFunctionSettingChanged/*开启了摇一摇*/,
        ShakeSpeedThresholdChanged/*设置了摇一摇的敏感度*/,


    }

}
