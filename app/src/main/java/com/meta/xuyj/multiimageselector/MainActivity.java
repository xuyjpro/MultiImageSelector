package com.meta.xuyj.multiimageselector;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meta.xuyj.permission.AskPermission;
import com.meta.xuyj.permission.PermissionHelper;
import com.meta.xuyj.permission.PermissionInterface;
import com.meta.xuyj.pictureselect.MutiImageSelector;

public class MainActivity extends AppCompatActivity {

    private PermissionHelper permissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionHelper=AskPermission.init(this)
                .request(new PermissionInterface() {
                    @Override
                    public int getPermissionsRequestCode() {
                        return 10000;
                    }

                    @Override
                    public String[] getPermissions() {
                        return new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        };
                    }

                    @Override
                    public void requestPermissionsSuccess() {
                        initView();

                    }

                    @Override
                    public void requestPermissionsFail() {
                        Toast.makeText(MainActivity.this,"Requests Permission Failed!",Toast
                                .LENGTH_SHORT).show();
                    }
                });

    }
    public void initView(){
        Button btn_gallery=findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MutiImageSelector.create(MainActivity.this)
                        .setSpancount(4)
                        .init();
            }
        });
    }
}
