package au.net.nicksifniotis.btsimpleterminal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by nsifniotis on 11/06/16.
 *
 * Reverse engineering the shit out of BT Simple Terminal.
 */
public class MainActivity extends Activity
{
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int RESULT_DEFAULT = 2;
    static boolean displayHex;
    private static String macAddress;

    String Dfn0 = "0";
    String Dfn1 = "1";
    String Dfn2 = "2";
    String Dfn3 = "3";
    String Dfn4 = "4";
    String Dfn5 = "5";

    final int READ_MESSAGE = 1;
    boolean autoScroll;

    private BluetoothAdapter btAdaptor = null;
    private BluetoothDevice btDevice = null;
    private BluetoothSocket btSocket = null;

    Button btn0;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btnCR;
    Button btnLF;
    Button btnSend;
    Button btnSendHex;
    Button dataClear;

    EditText dataInput;
    EditText dataInputHex;
    TextView dataReceived;
    boolean displayDateStamp;

    String fn0;
    String fn1;
    String fn2;
    String fn3;
    String fn4;
    String fn5;

    String fnLabel0;
    String fnLabel1;
    String fnLabel2;
    String fnLabel3;
    String fnLabel4;
    String fnLabel5;

    String fnLabelDefault0 = "fn0";
    String fnLabelDefault1 = "fn1";
    String fnLabelDefault2 = "fn2";
    String fnLabelDefault3 = "fn3";
    String fnLabelDefault4 = "fn4";
    String fnLabelDefault5 = "fn5";

    Handler handler;
    private ConnectedThread mConnectedThread;


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1)
        {
            fn0 = data.getStringExtra ("FN0VALUE");
            fn1 = data.getStringExtra ("FN1VALUE");
            fn2 = data.getStringExtra ("FN2VALUE");
            fn3 = data.getStringExtra ("FN3VALUE");
            fn4 = data.getStringExtra ("FN4VALUE");
            fn5 = data.getStringExtra ("FN5VALUE");

            fnLabel0 = data.getStringExtra ("FUNCTEXT0");
            fnLabel1 = data.getStringExtra ("FUNCTEXT1");
            fnLabel2 = data.getStringExtra ("FUNCTEXT2");
            fnLabel3 = data.getStringExtra ("FUNCTEXT3");
            fnLabel4 = data.getStringExtra ("FUNCTEXT4");
            fnLabel5 = data.getStringExtra ("FUNCTEXT5");

            displayDateStamp = data.getBooleanExtra("CHECKVALUE", true);
            displayHex = data.getBooleanExtra("OUTPUTHEX", false);
            autoScroll = data.getBooleanExtra("AUTOSCROLL", true);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("FUNCVALUE0", fn0);
            editor.putString("FUNCVALUE1", fn1);
            editor.putString("FUNCVALUE2", fn2);
            editor.putString("FUNCVALUE3", fn3);
            editor.putString("FUNCVALUE4", fn4);
            editor.putString("FUNCVALUE5", fn5);

            editor.putString("FUNCLABEL0", fnLabel0);
            editor.putString("FUNCLABEL1", fnLabel1);
            editor.putString("FUNCLABEL2", fnLabel2);
            editor.putString("FUNCLABEL3", fnLabel3);
            editor.putString("FUNCLABEL4", fnLabel4);
            editor.putString("FUNCLABEL5", fnLabel5);

            editor.putBoolean("CHECKBOX", displayDateStamp);
            editor.putBoolean("OUTPUTHEX", displayHex);
            editor.putBoolean("AUTOSCROLL", autoScroll);

            editor.commit();
        }
        else if (resultCode == 2)
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("FUNCVALUE0", Dfn0);
            editor.putString("FUNCVALUE1", Dfn1);
            editor.putString("FUNCVALUE2", Dfn2);
            editor.putString("FUNCVALUE3", Dfn3);
            editor.putString("FUNCVALUE4", Dfn4);
            editor.putString("FUNCVALUE5", Dfn5);

            editor.putString("FUNCLABEL0", fnLabelDefault0);
            editor.putString("FUNCLABEL1", fnLabelDefault1);
            editor.putString("FUNCLABEL2", fnLabelDefault2);
            editor.putString("FUNCLABEL3", fnLabelDefault3);
            editor.putString("FUNCLABEL4", fnLabelDefault4);
            editor.putString("FUNCLABEL5", fnLabelDefault5);

            editor.putBoolean("OUTPUTHEX", false);
            editor.putBoolean("AUTOSCROLL", true);

            editor.commit();
        }
    }

    public boolean onCreateOptionsMenu (Menu menu)
    {
        menu.add(0, 1, 1, "Settings");
        menu.add(0, 2, 2, "Email Log");
        menu.add(0, 3, 3, "About");

        return true;
    }


    @SuppressLint("HandlerLeak")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = (Button)findViewById(R.id.buttonSend);
        btnSendHex = (Button)findViewById(R.id.buttonSendHex);
        btn0 = (Button)findViewById(R.id.button0);
        btn1 = (Button)findViewById(R.id.button1);
        btn2 = (Button)findViewById(R.id.button2);
        btn3 = (Button)findViewById(R.id.button3);
        btn4 = (Button)findViewById(R.id.button4);
        btn5 = (Button)findViewById(R.id.button5);
        btnCR = (Button)findViewById(R.id.buttonCR);
        btnLF = (Button)findViewById(R.id.buttonLF);
        dataClear = (Button)findViewById(R.id.buttonClear);
        dataReceived = (TextView)findViewById(R.id.textView);
        dataInput = (EditText)findViewById(R.id.editText1);
        dataInputHex = (EditText)findViewById(R.id.editTextHex);

        dataReceived.setMovementMethod(new ScrollingMovementMethod());

        getWindow().setSoftInputMode(2);        // soft_input_mode_hidden

        dataReceived.setText("");

        handler = new Handler() {
            /**
             * I am not certain of the reverse engineering of this code. When decompiling
             * messages passed into this handler, pay particular attention to the format
             * of these messages.
             *
             * @param msg
             */
            public void handleMessage (Message msg) {
                String readMessage = (String) msg.obj;

                if (msg.what != 1)
                    return;

                if (msg.arg1 > 0)
                {
                    if (displayHex)
                        readMessage = convertStringToHex(readMessage);

                    addMessage(readMessage);
                }
            }
        };

        btAdaptor = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (dataInput.getText().length() == 0)
                    return;

                mConnectedThread.write(dataInput.getText().toString());
                Toast.makeText(getBaseContext(), "ASCII Data Sent", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendHex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataInputHex.getText().length() == 0)
                    return;

                String myString = "0x" + dataInputHex.getText().toString();
                int myNum = Integer.decode(myString);
                mConnectedThread.writeHex(myNum);
                Toast.makeText(getBaseContext(), "Hex Data Sent", Toast.LENGTH_SHORT).show();
            }
        });

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write(fn0);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write(fn1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write(fn2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write(fn3);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write(fn4);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write(fn5);
            }
        });

        btnCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.writeHex(13);
            }
        });

        btnLF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.writeHex(10);
            }
        });

        dataClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataReceived.setText("");
                Toast.makeText(getBaseContext(), "Terminal Window Cleared", Toast.LENGTH_SHORT);
            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();

        SharedPreferences loadSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        fn0 = loadSharedPrefs.getString("FUNCVALUE0", "0");
        fn1 = loadSharedPrefs.getString("FUNCVALUE1", "1");
        fn2 = loadSharedPrefs.getString("FUNCVALUE2", "2");
        fn3 = loadSharedPrefs.getString("FUNCVALUE3", "3");
        fn4 = loadSharedPrefs.getString("FUNCVALUE4", "4");
        fn5 = loadSharedPrefs.getString("FUNCVALUE5", "5");

        fnLabel0 = loadSharedPrefs.getString("FUNCTEXT0", fnLabelDefault0);
        fnLabel1 = loadSharedPrefs.getString("FUNCTEXT1", fnLabelDefault1);
        fnLabel2 = loadSharedPrefs.getString("FUNCTEXT2", fnLabelDefault2);
        fnLabel3 = loadSharedPrefs.getString("FUNCTEXT3", fnLabelDefault3);
        fnLabel4 = loadSharedPrefs.getString("FUNCTEXT4", fnLabelDefault4);
        fnLabel5 = loadSharedPrefs.getString("FUNCTEXT5", fnLabelDefault5);

        displayDateStamp = loadSharedPrefs.getBoolean("CHECKBOX", true);
        displayHex = loadSharedPrefs.getBoolean("OUTPUTHEX", false);
        autoScroll = loadSharedPrefs.getBoolean("AUTOSCROLL", true);

        btn0.setText(fnLabel0);
        btn1.setText(fnLabel1);
        btn2.setText(fnLabel2);
        btn3.setText(fnLabel3);
        btn4.setText(fnLabel4);
        btn5.setText(fnLabel5);

        Intent intent = getIntent();
        macAddress = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        BluetoothDevice device = btAdaptor.getRemoteDevice(macAddress);
        try
        {
            btSocket = createBluetoothSocket(device);
        }
        catch (Exception e)
        {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            return;
        }

        btAdaptor.cancelDiscovery();

        try
        {
            btSocket.connect();
        }
        catch (Exception e)
        {
            try
            {
                btSocket.close();
            }
            catch (Exception e2)
            {
                errorExit ("Fatal Error", "In onResume() and unable to close socket during connection failure: " + e2.getMessage() + ".");
            }
        }

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());
        mConnectedThread.write ("");

        if (displayDateStamp)
        {
            mConnectedThread.writeHexNoWarning(0xa);
            mConnectedThread.writeHexNoWarning(0xd);
            mConnectedThread.writeNoWarning("Bluetooth Terminal Connected - " + currentDateAndTime);
            dataReceived.append("\n Bluetooth Terminal Connected - " + currentDateAndTime);
            mConnectedThread.writeHexNoWarning(0xa);
            mConnectedThread.writeHexNoWarning(0xd);
        }
    }


    private void addMessage(String msg)
    {
        dataReceived.append(msg);

        if (autoScroll) {
            int count = dataReceived.getLineCount();

            int v1 = dataReceived.getLayout().getLineTop(count);
            int v2 = dataReceived.getHeight();

            int scrollAmount = v1 - v2;

            dataReceived.scrollTo (0, (scrollAmount > 0) ? scrollAmount : 0);
        }
    }

    private void checkBTState()
    {
        if (btAdaptor == null)
        {
            errorExit("Fatal Error", "Bluetooth not supported");
            return;
        }

        if (!btAdaptor.isEnabled())
        {
            Intent enableBtIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            startActivityForResult(enableBtIntent, 1);
        }
    }


    private BluetoothSocket createBluetoothSocket (BluetoothDevice device) throws IOException
    {
        BluetoothSocket res = null;
        BluetoothDevice p1 = device;

        if (Build.VERSION.SDK_INT < 10)
        {
            res = device.createRfcommSocketToServiceRecord(MY_UUID);
        }
        else
        {
            try
            {
                Class<?> c = device.getClass();
                Method m = c.getMethod("createInsecureRfcommSocketToServiceRecord", c);

                res = (BluetoothSocket) m.invoke(p1, MY_UUID);
            }
            catch (Exception e)
            {

            }
        }
        return res;
    }


    private void errorExit (String title, String message)
    {
        Toast.makeText(getBaseContext(), new StringBuilder(title)
                .append(" - ").append(message).toString(), Toast.LENGTH_LONG).show();

        finish();
    }

    private String convertStringToHex (String string)
    {
        String newString = "";
        for (int i = 0; i < string.length(); i ++)
        {
            newString += (String.format("%x ", (byte)string.charAt(i)));
        }

        return newString;
    }


    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1:
                Intent intent = new Intent();
                intent.setClass(this, FunctionSettingActivity.class);
                startActivityForResult(intent, 1);
                break;
            case 2:
                SimpleDateFormat smf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                String currentDateAndTime = smf.format(new Date());
                String temp = dataReceived.getText().toString();

                Intent i = new Intent ("android.intent.action.SEND");
                i.setType("message/rfc822");
                i.putExtra("android.intent.extra.EMAIL", "recipient@example.com");
                i.putExtra("android.intent.extra.SUBJECT", "Bluetooth Terminal Log " + currentDateAndTime);
                i.putExtra("android.intent.extra.TEXT", temp);

                try {
                    startActivity(Intent.createChooser(i, "Send mail.."));
                }
                catch (Exception e)
                {
                    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                AboutDialog about = new AboutDialog(this);
                about.setTitle("About");
                about.show();
                break;
        }

        return true;
    }


    @Override
    public void onPause()
    {
        super.onPause();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:s");
        String currentDateAndTime = sdf.format(new Date());

        if (displayDateStamp)
        {
            mConnectedThread.writeHexNoWarning(10);
            mConnectedThread.writeHexNoWarning(13);

            mConnectedThread.writeNoWarning("Bluetooth Terminal Disconnected - " + currentDateAndTime);
        }

        try
        {
            btSocket.close();
        }
        catch (Exception e)
        {
            errorExit("Fatal Error", "In onPause() and failed to close socket. " + e.getMessage() + ".");
        }
    }


    class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedThread (BluetoothSocket socket)
        {
            super();

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (Exception e)
            {}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        @Override
        public void run()
        {
            byte[] buffer = new byte[0x100];

            while (true) {
                try {
                    int bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    handler.obtainMessage(1, bytes, -1, readMessage).sendToTarget();
                } catch (Exception e) {

                }
            }
        }


        public void write(String message)
        {
            byte[] msgBuffer = message.getBytes();

            try
            {
                mmOutStream.write(msgBuffer);
            }
            catch (Exception e)
            {
                finish();
                Toast.makeText(getBaseContext(), "DEVICE UNAVAILABLE", Toast.LENGTH_SHORT).show();
            }
        }


        public void writeNoWarning (String message)
        {
            byte[] msgBuffer = message.getBytes();

            try
            {
                mmOutStream.write(msgBuffer);
            }
            catch (Exception e)
            {
                finish();
            }
        }


        public void writeHex(int i)
        {
            try
            {
                mmOutStream.write(i);
            }
            catch (Exception e)
            {
                finish();
                Toast.makeText(getBaseContext(), "DEVICE UNAVAILABLE", Toast.LENGTH_SHORT).show();
            }
        }


        public void writeHexNoWarning(int i)
        {
            try
            {
                mmOutStream.write(i);
            }
            catch (Exception e)
            {
                finish();
            }
        }
    }
}
