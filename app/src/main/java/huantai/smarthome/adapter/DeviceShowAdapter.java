package huantai.smarthome.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.orm.SugarRecord;

import java.util.List;

import huantai.smarthome.bean.ConstAction;
import huantai.smarthome.bean.SwitchInfo;
import huantai.smarthome.initial.R;
import huantai.smarthome.view.DeviceHolder;

/**
 * Created by lj_xwl on 2017/9/19.
 */

public class DeviceShowAdapter extends BaseAdapter{
    private List<SwitchInfo> switchInfoList;
    private Context context;

    Handler handler = new Handler();
    protected static final int DEVICEDELETE = 99;//删除设备
    protected static final int DEVICERENAME = 100;//设备改名

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public DeviceShowAdapter(List<SwitchInfo> switchInfoList, Context context) {
        this.switchInfoList = switchInfoList;
        this.context = context;
    }

    //更新数据
    public void setData(List<SwitchInfo> switchInfoList) {
        this.switchInfoList=switchInfoList;
    }

    public List<SwitchInfo> getSwitchInfoLists() {
        return switchInfoList;
    }

    @Override
    public int getCount() {
        return switchInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        final DeviceHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.device_item_list, null);
            holder = new DeviceHolder(view);
            view.setTag(holder);

        } else {
            holder = (DeviceHolder) view.getTag();
        }

        if (switchInfoList != null) {
            holder.getTv_deviceName().setText(switchInfoList.get(position).getName());
            holder.getTv_icon().setImageResource(R.drawable.device_images);
            holder.getTv_icon().setImageLevel(switchInfoList.get(position).getPicture());

            //注册界面更新广播接收者
            IntentFilter filter = new IntentFilter(ConstAction.devicenotifyfinishaction);
            context.registerReceiver(notifyfinishbroadcast, filter);


            holder.getTv_rename().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = new Message();
                    msg.what = DEVICERENAME;
                    msg.obj = holder.getTv_deviceName();
                    handler.sendMessage(msg);
                }
            });
            holder.getTv_delete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //列出数据库所有设备并标记item上的设备为已删除
                    List<SwitchInfo> deleteSwitchInfoList = SugarRecord.listAll(SwitchInfo.class);
                    SwitchInfo deleteSwich = deleteSwitchInfoList.get(position);
                    deleteSwich.setIsdelete(true);
                    SugarRecord.save(deleteSwich);

                    //删除item
                    switchInfoList.remove(position);

                    //更新界面
                    notifyDataSetChanged();
                }
            });
        }
        return view;
    }

    //实现界面更新广播内容
    private BroadcastReceiver notifyfinishbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<SwitchInfo> switchInfoList2 = SugarRecord.listAll(SwitchInfo.class);
            if (switchInfoList2 != null) {
                switchInfoList = switchInfoList2;
            }

            //刷新数据
            notifyDataSetChanged();
        }
    };

}
