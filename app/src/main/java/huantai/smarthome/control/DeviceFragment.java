package huantai.smarthome.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import huantai.smarthome.adapter.DeviceShowAdapter;
import huantai.smarthome.bean.ConstAction;
import huantai.smarthome.bean.ControlDataible;
import huantai.smarthome.bean.SwitchInfo;
import huantai.smarthome.initial.R;
import huantai.smarthome.popWindow.PopupSwitch;
import huantai.smarthome.utils.ControlUtils;
import huantai.smarthome.utils.ToastUtil;
import huantai.smarthome.view.SlideListView;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class DeviceFragment extends Fragment implements ControlDataible {

    private View view;
    private SlideListView lv_device;
    private List<SwitchInfo> switchInfoList = new ArrayList<SwitchInfo>();
    private DeviceShowAdapter deviceShowAdapter;
    protected static final int DEVICEDELETE = 99;//删除设备
    protected static final int DEVICERENAME = 100;//设备改名
    protected static final int SENDSUCCESS = 101;//设备改名
    private TextView tv_rename;
    private ImageView bt_device_add;
    private GizWifiDevice device;//机智云设备
    private String address;//点击条目的当前地址

    private MyReceiver receiver = null;//自定义广播接收者

//    private ImageView bt_device_refresh;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DEVICERENAME:
                    tv_rename = (TextView) msg.obj;
                    setDeviceInfo();
                    break;
                case SENDSUCCESS:
                    ToastUtil.ToastShow(getActivity(),"数据发送成功");
                    break;
            }
        }
    };


    public DeviceFragment() {
    }

    public void setSwitchInfoList(List<SwitchInfo> switchInfoList) {
        this.switchInfoList = switchInfoList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_device, container, false);

        initView();
        initData();
        initStatusListener();
        initBroadreceive();
        initDevice();
        return view;
    }

    private void initData() {

//        switchInfoList = InputPopup.getSwitchLists();
        //初始化显示未删除的设备
        List<SwitchInfo> initItemLists = Select.from(SwitchInfo.class)
                .where(Condition.prop("isdelete").eq(0))
                .list();
        if (initItemLists.size() != 0) {
            deviceShowAdapter = new DeviceShowAdapter(initItemLists, getContext());
            //更新数据
            deviceShowAdapter.setData(initItemLists);
            //通知适配器更新视图
            deviceShowAdapter.notifyDataSetChanged();
        } else {
            deviceShowAdapter = new DeviceShowAdapter(switchInfoList, getContext());
            deviceShowAdapter.setData(switchInfoList);
        }
        deviceShowAdapter.setHandler(handler);
        lv_device.setAdapter(deviceShowAdapter);

    }

    @Override
    public void initStatusListener() {

        bt_device_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceAddActivity.class);
                startActivity(intent);
            }
        });


        lv_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                address = deviceShowAdapter.getSwitchInfoLists().get(position).getAddress(); //获取锁点击设备的当前地址
//                Log.i("address",address);

                PopupSwitch popupSwitch = new PopupSwitch(getActivity(), deviceShowAdapter.getSwitchInfoLists().get(position).getType(), deviceShowAdapter.getSwitchInfoLists().get(position).getAddress());
                //popup初始化事件
                popupSwitch.init();
                popupSwitch.showPopupWindow();

            }
        });

//        bt_device_refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                deviceShowAdapter.notifyDataSetChanged();
//                //发送DeviceShowAdapter界面更新广播
//                Intent intent = new Intent(ConstAction.devicenotifyfinishaction);
//                getContext().sendBroadcast(intent);
//            }
//        });

    }

    @Override
    public void initView() {
//        bt_device_refresh = (ImageView) view.findViewById(R.id.bt_device_refresh);
        bt_device_add = (ImageView) view.findViewById(R.id.bt_device_add);
        lv_device = (SlideListView) view.findViewById(R.id.lv_device);
        //设定策划模式
        lv_device.initSlideMode(SlideListView.MOD_RIGHT);

    }

    @Override
    public void initBroadreceive() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstAction.switchcontrolaction);
        getContext().registerReceiver(receiver,filter);
    }

    @Override
    public void initDevice() {
        Intent intent = getActivity().getIntent();
        device = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
        System.out.print(1);
    }

    @Override
    public void sendJson(String key, Object value) throws JSONException {

        ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
        hashMap.put(key, value);
        device.write(hashMap, 0);
        Log.i("==", hashMap.toString());


    }

    private void setDeviceInfo() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(new EditText(getActivity())).create();
        final TextView re = tv_rename;
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.alert_devicefragment_set_device_info);

        final EditText et_rename = (EditText) window.findViewById(R.id.et_rename);
        LinearLayout ll_device_no = (LinearLayout) window.findViewById(R.id.ll_device_no);
        LinearLayout ll_device_sure = (LinearLayout) window.findViewById(R.id.ll_device_sure);

        ll_device_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SwitchInfo> findSwitchList =  Select.from(SwitchInfo.class).where(Condition.prop("name").eq(re.getText())).list();;
                SwitchInfo switchInfo = findSwitchList.get(0);
                switchInfo.setName(String.valueOf(et_rename.getText()));
                Log.i("rename",switchInfo.toString());
                SugarRecord.save(switchInfo);
                re.setText(et_rename.getText());
                ToastUtil.ToastShow(getActivity(),"改名成功");
                dialog.cancel();
            }
        });

        ll_device_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    }

    //自定义广播接收者
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            device = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");

//            Intent intent1 = getActivity().getIntent();
            if (action.equals(ConstAction.switchcontrolaction)) {
                byte type = intent.getByteExtra("type", (byte) 0xff);
                byte status = intent.getByteExtra("status",(byte) 0xff);
//                byte[] statu = intent.getByteArrayExtra("status");
                try {
                    Log.i("address",address);
                    byte[] b = ControlUtils.getSwitchInstruction(type,status,address.toUpperCase());
                    sendJson("kuozhan", ControlUtils.getSwitchInstruction(type,status,address.toUpperCase()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = SENDSUCCESS;
                handler.sendMessage(msg);

            }

        }
    }


}
