package au.net.nicksifniotis.btsimpleterminal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by nsifniotis on 11/06/16.
 *
 * Reverse engineered from the original BT Simple Terminal application.
 * This shit aint easy.
 */
public class AboutDialog extends Dialog
{
    private static Context mContext;

    public AboutDialog (Context context)
    {
        super(context);
        mContext = context;
    }

    public void onCreate (Bundle savedInstanceState)
    {
        setContentView (R.layout.about);
        TextView tv = (TextView) findViewById(R.id.legal_text);
        tv.setText(readRawTextFile(R.raw.legal));
        tv = (TextView) findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
        tv.setLinkTextColor(0xfffffff);
        Linkify.addLinks(tv, Linkify.ALL);
    }

    public static String readRawTextFile (int id)
    {
        InputStream inputStream = mContext.getResources().openRawResource(id);;
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        StringBuilder text = new StringBuilder();

        try {
            String line = buf.readLine();
            while (line != null)
            {
                text.append(line);
                line = buf.readLine();
            }
        }
        catch (Exception e)
        {}

        return text.toString();
    }
}
