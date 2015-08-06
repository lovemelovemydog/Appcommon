package com.common.app.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.common.app.R;
import com.common.app.activity.BaseActivity;
import com.common.app.common.util.DateUtil;
import com.common.app.entity.UserInfo;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * you can study ormlite
 * http://ormlite.com/
 * in android os demo Hello World, HelloNoBase!
 * Created by fangzhu on 2015/8/6.
 *
 */
public class OrmDemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_orm);

        final TextView textView = (TextView)findViewById(R.id.textView);
        setContent(textView);


        final EditText editText = (EditText)findViewById(R.id.editText);

        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().length() == 0)
                    return;
                UserInfo userInfo = new UserInfo();
                userInfo.setNickName(editText.getText().toString().trim());
                userInfo.setBirthDay(DateUtil.getTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
                try {
                    getDatabaseHelper().getUserInfoDao().create(userInfo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                setContent(textView);
            }
        });
    }

    void setContent (TextView textView) {
        try {
            StringBuilder stringBuilder = new StringBuilder();

            List<UserInfo> list = getDatabaseHelper().getUserInfoDao().queryForAll();
            if (list == null || list.size() == 0)
                return;
            for (UserInfo userInfo:list) {
                stringBuilder.append("getLocalId="+userInfo.getLocalId() + " getNickName=" +userInfo.getNickName()+" getBirthDay="+userInfo.getBirthDay()+"\n");
            }
            textView.setText(stringBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
