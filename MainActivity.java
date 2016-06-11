package au.net.nicksifniotis.btsimpleterminal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        synthetic access$1 - access to method void AddMessage (String)
        synthetic access$2 - access to field ConnectedThread mConnectedThread

     */

    private void AddMessage (String msg)
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

    private void errorExit (String s1, String s2)
    {

    }


    private String convertStringToHex (String string)
    {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < string.length(); i ++)
        {
            newString.append(String.format("%x ", (byte)string.charAt(i)));
        }

        return newString.toString();
    }



    class ConnectedThread extends Thread
    {
    }
}
