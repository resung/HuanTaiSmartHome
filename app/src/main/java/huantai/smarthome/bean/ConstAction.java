package huantai.smarthome.bean;

/**
 * Created by lj_xwl on 2017/9/10.
 */

public class ConstAction {
    //震动广播
    public static final String vibratoraction = "com.huantai.smarthome.mainactivity.vibrator.acion";
    //完成(删除)广播
    public static final String deletefinishaction = "com.huantai.smarthome.homefragment.deletefinish.action";
    //完成(更新)广播
    public static final String notifyfinishaction = "com.huantai.smarthome.addremoveadpter.notifyfinish.action";
    //inputPopup向DeviceShowAdapter发送界面更新广播
    public static final String devicenotifyfinishaction = "com.huantai.smarthome.deviceshowadapter.devicenotifyfinish.action";
    //inputPopup接收DeviceAddActivity传递的数据
    public static final String deviceaddaction = "com.huantai.smarthome.deviceaddactivity.deviceaddaction.action";
    //开关控制广播
    public static final String switchcontrolaction = "com.huantai.smarthome.popupswitch.switchcontrolaction.action";

}
