package au.net.nicksifniotis.btsimpleterminal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
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

    /*
        Reverse engineering notes for MainActivity

        synthetic access$0 - access to method String convertStringToHex(String)
        synthetic access$1 - access to method void addMessage (String)
        synthetic access$2 - access to field ConnectedThread mConnectedThread

     */

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
    }

//    .line 96
//        new-instance v0, Lwingood/bluetooth/btsimpleterminal/MainActivity$1;
//
//    invoke-direct {v0, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$1;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    iput-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->handler:Landroid/os/Handler;
//
//    .line 116
//    invoke-static {}, Landroid/bluetooth/BluetoothAdapter;->getDefaultAdapter()Landroid/bluetooth/BluetoothAdapter;
//
//    move-result-object v0
//
//    iput-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btAdapter:Landroid/bluetooth/BluetoothAdapter;
//
//    .line 117
//    invoke-direct {p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity;->checkBTState()V
//



//
//    .line 142
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btn0:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$4;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$4;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 147
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btn1:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$5;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$5;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 153
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btn2:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$6;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$6;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 159
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btn3:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$7;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$7;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 165
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btn4:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$8;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$8;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 171
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btn5:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$9;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$9;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 177
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btnCR:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$10;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$10;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 183
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->btnLF:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$11;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$11;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 189
//    iget-object v0, p0, Lwingood/bluetooth/btsimpleterminal/MainActivity;->dataClear:Landroid/widget/Button;
//
//    new-instance v1, Lwingood/bluetooth/btsimpleterminal/MainActivity$12;
//
//    invoke-direct {v1, p0}, Lwingood/bluetooth/btsimpleterminal/MainActivity$12;-><init>(Lwingood/bluetooth/btsimpleterminal/MainActivity;)V
//
//    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V
//
//    .line 196
//        return-void
//    .end method



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



    class ConnectedThread extends Thread
    {
        public void write (String s)
        {

        }


        public void writeHex (int i)
        {

        }
    }
}
