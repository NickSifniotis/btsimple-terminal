package au.net.nicksifniotis.btsimpleterminal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by nsifniotis on 12/06/16.
 *
 * Reverse engineering this project ...
 */
public class DeviceListActivity extends Activity
{
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter mBtAdapter;
    private AdapterView.OnItemClickListener mDeviceClickListener;
    private ArrayAdapter<String> mPairedDevicesArrayAdaptor;
    TextView textView1;

    public DeviceListActivity()
    {
        super();

        mDeviceClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
                textView1.setText("Connecting..");
                String info = ((TextView)v).getText().toString();
                String address = info.substring(info.length() - 11);

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra(EXTRA_DEVICE_ADDRESS, address);
                startActivity(i);
            }
        };
    }


    /***
     * Gotta make sure the Bluetooth adaptor is switched on hey
     */
    private void checkBTState()
    {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null)
        {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (!mBtAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            startActivityForResult(enableBtIntent, 1);
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        // the app rater function call is located here, but it won't be implemented because fuck that
        // AppRater.app_launched (this);
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        menu.add (0, 2, 2, "About");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId())
        {
            case 2:
                AboutDialog about = new AboutDialog(this);
                about.setTitle ("About");
                about.show();
                break;
        }
        return true;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        checkBTState();

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setTextSize(40.0f);
        textView1.setText("");

        mPairedDevicesArrayAdaptor = new ArrayAdapter<String>(this, R.layout.device_name);

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdaptor);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0)
        {
            // something gets its visibility set to zero here.
            for (BluetoothDevice device: pairedDevices)
                mPairedDevicesArrayAdaptor.add(device.getName() + "\n" + device.getAddress());
        }
        else
        {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdaptor.add(noDevices);
        }
    }
}
