package com.example.davidhsu.sdweather.ResideMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davidhsu.sdweather.R;
import com.example.davidhsu.sdweather.match;
import com.example.davidhsu.sdweather.sqlDatabase.InsertActivity;
import com.example.davidhsu.sdweather.sqlDatabase.MySQLiteOpenHelper;
import com.example.davidhsu.sdweather.sqlDatabase.Spot;
import com.example.davidhsu.sdweather.sqlDatabase.UpdateActivity;

import java.util.List;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下午1:31
 * Mail: specialcyci@gmail.com
 */
public class CameraFragment extends Fragment {

    private View cameraParentView;

    private ImageView ivSpot;
    private TextView tvRowCount;
    private TextView tvId;
    private TextView tvClothes;
    private TextView tvType;
    private TextView tvSleeve;
    private TextView tvMaterial;
    private List<Spot> spotList;
    private int index;
    private MySQLiteOpenHelper helper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraParentView = inflater.inflate(R.layout.camera_activity, container, false);

        findViews();
        if (helper == null) {
            helper = new MySQLiteOpenHelper(getActivity());
        }
        spotList = helper.getAllSpots();
        showSpots(0);

        return cameraParentView;
    }

    @Override
        public void onStart() {
        super.onStart();
        spotList = helper.getAllSpots();
        showSpots(0);
    }

    private void findViews() {
        ivSpot = (ImageView) cameraParentView.findViewById(R.id.ivSpot);
        tvRowCount = (TextView) cameraParentView.findViewById(R.id.tvRowCount);
        tvId = (TextView) cameraParentView.findViewById(R.id.tvId);
        tvClothes = (TextView) cameraParentView.findViewById(R.id.tvClothes);
        tvType = (TextView) cameraParentView.findViewById(R.id.tvType);
        tvSleeve = (TextView) cameraParentView.findViewById(R.id.tvSleeve);
        tvMaterial = (TextView) cameraParentView.findViewById(R.id.tvMaterial);

        ImageButton onNextClick = (ImageButton)cameraParentView.findViewById(R.id.btNext);
        onNextClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if (index >= spotList.size()) {
                    index = 0;
                }
                showSpots(index);
            }
        });

        ImageButton onBackClick = (ImageButton)cameraParentView.findViewById(R.id.btBack);
        onBackClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                if (index < 0) {
                    index = spotList.size() - 1;
                }
                showSpots(index);
            }
        });

        ImageButton onInsertClick = (ImageButton)cameraParentView.findViewById(R.id.btInsert);
        onInsertClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InsertActivity.class);
                startActivity(intent);
            }
        });

        ImageButton onUpdateClick = (ImageButton)cameraParentView.findViewById(R.id.btUpdate);
        onUpdateClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spotList.size() <= 0) {
                    Toast.makeText(getActivity(), R.string.msg_NoDataFound,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = Integer.parseInt(tvId.getText().toString());
                Intent intent = new Intent(getActivity(), UpdateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        ImageButton onDeleteClick = (ImageButton)cameraParentView.findViewById(R.id.btDelete);
        onDeleteClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spotList.size() <= 0) {
                    Toast.makeText(getActivity(), R.string.msg_NoDataFound,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = Integer.parseInt(tvId.getText().toString());
                int count = helper.deleteById(id);
                Toast.makeText(getActivity(), count + " " + getString(R.string.msg_RowDeleted),
                        Toast.LENGTH_SHORT).show();
                spotList = helper.getAllSpots();
                showSpots(0);
            }
        });



    }

    private void showSpots(int index) {
        if (spotList.size() > 0) {
            Spot spot = spotList.get(index);
            Bitmap image = BitmapFactory.decodeByteArray(spot.getImage(), 0,
                    spot.getImage().length);
            ivSpot.setImageBitmap(image);
            tvId.setText(Integer.toString(spot.getId()));
            tvClothes.setText(spot.getClothes());
            tvType.setText(spot.getType());
            tvSleeve.setText(spot.getSleeve());
            tvMaterial.setText(spot.getMaterial());
            tvRowCount.setText((index + 1) + "/" + spotList.size());
        } else {
            ivSpot.setImageResource(R.drawable.ic_launcher);
            tvId.setText(null);
            tvClothes.setText(null);
            tvType.setText(null);
            tvSleeve.setText(null);
            tvMaterial.setText(null);
            tvRowCount.setText(" 0/0 " + getString(R.string.msg_NoDataFound));
        }
    }

   /* public void onNextClick(View view) {
        index++;
        if (index >= spotList.size()) {
            index = 0;
        }
        showSpots(index);
    }

    public void onBackClick(View view) {
        index--;
        if (index < 0) {
            index = spotList.size() - 1;
        }
        showSpots(index);
    }

    public void onInsertClick(View view) {
        Intent intent = new Intent(getActivity(), InsertActivity.class);
        startActivity(intent);
    }

    public void onUpdateClick(View view) {
        if (spotList.size() <= 0) {
            Toast.makeText(getActivity(), R.string.msg_NoDataFound,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int id = Integer.parseInt(tvId.getText().toString());
        Intent intent = new Intent(getActivity(), UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onDeleteClick(View view) {
        if (spotList.size() <= 0) {
            Toast.makeText(getActivity(), R.string.msg_NoDataFound,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int id = Integer.parseInt(tvId.getText().toString());
        int count = helper.deleteById(id);
        Toast.makeText(getActivity(), count + " " + getString(R.string.msg_RowDeleted),
                Toast.LENGTH_SHORT).show();
        spotList = helper.getAllSpots();
        showSpots(0);
    }

*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.close();
        }
    }


}
