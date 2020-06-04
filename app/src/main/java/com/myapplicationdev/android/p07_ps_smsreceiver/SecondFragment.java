package com.myapplicationdev.android.p07_ps_smsreceiver;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {

    TextView tvFrag2;
    Button btnSMS2;
    EditText etWord,etWord2;

    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_second, container, false);

        tvFrag2 = v.findViewById(R.id.tvOutput2);
        etWord = v.findViewById(R.id.etWord);
        etWord2 = v.findViewById(R.id.etWord2);
        btnSMS2 = v.findViewById(R.id.btnSMS2);

        btnSMS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionChk = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if(permissionChk != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_SMS},0);
                    return;
                }

                Uri uri = Uri.parse("content://sms/sent");

                String[] reqCols = new String[]{"date","address","body","type"};
                String word = etWord.getText().toString();
                String word2 = etWord2.getText().toString();
                String[] args1 = new String[]{"%" + word + "%","%"+ word2 + "%"};
                ContentResolver cr = getActivity().getContentResolver();

                Cursor cursor = cr.query(uri,reqCols,"body LIKE ? AND body LIKE ?",args1,null,null);
                String smsBody = "";
                if(cursor.moveToFirst()){
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());

                }
                tvFrag2.setText(smsBody);
            }
        });

        return v;
    }
}
