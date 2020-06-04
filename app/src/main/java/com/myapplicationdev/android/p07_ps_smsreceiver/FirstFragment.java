package com.myapplicationdev.android.p07_ps_smsreceiver;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {

    Button btnSMS1;
    EditText etNum;
    TextView tvFrag1;

    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_first, container, false);

        tvFrag1 = v.findViewById(R.id.tvOutput1);
        btnSMS1 = v.findViewById(R.id.btnSMS1);
        etNum = v.findViewById(R.id.etNum);

        btnSMS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionChk = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if(permissionChk != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_SMS},0);
                    return;
                }

                Uri uri = Uri.parse("content://sms/inbox");

                String[] reqCols = new String[]{"date","address","body","type"};
                String num = etNum.getText().toString();
                String[] args = new String[]{"%"+num+"%"};

                ContentResolver cr = getActivity().getContentResolver();

                Cursor cursor = cr.query(uri,reqCols,"address LIKE ?",args,null,null);
                String smsBody = "";
                if(cursor.moveToFirst()){
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        Log.d("TAG",address);
                        Log.d("TAG",date);
                        Log.d("TAG",body);
                        Log.d("TAG",type);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());

                }
                tvFrag1.setText(smsBody);
            }
        });



        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0 : {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnSMS1.performClick();
                }else {
                    Toast.makeText(getActivity(),"Permission not granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
